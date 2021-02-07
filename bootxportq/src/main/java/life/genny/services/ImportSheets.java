package life.genny.services;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;
import life.genny.bootxport.bootx.*;
import life.genny.bootxport.xlsimport.GoogleSheetBuilder;
import life.genny.bootxport.xlsimport.Summary;
import life.genny.models.attribute.Attribute;
import life.genny.models.attribute.AttributeLink;
import life.genny.models.attribute.EntityAttribute;
import life.genny.models.datatype.DataType;
import life.genny.models.entity.BaseEntity;
import life.genny.models.entity.EntityEntity;
import life.genny.models.exception.BadDataException;
import life.genny.models.validation.Validation;
import life.genny.models.validation.ValidationList;
import life.genny.qwandautils.KeycloakUtils;
import life.genny.qwanda.Ask;
import life.genny.qwanda.Question;
import life.genny.qwanda.QuestionQuestion;
import life.genny.qwanda.message.QBaseMSGMessageTemplate;
import life.genny.qwanda.message.QEventLinkChangeMessage;
import life.genny.qwandautils.GennySettings;
import life.genny.qwandautils.JsonUtils;
import life.genny.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.*;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/import")
@RequestScoped
public class ImportSheets {
    @Inject
    EntityManager em;
    @Inject
    UserTransaction userTransaction;
    private static final int BATCHSIZE = 500;
    String currentRealm = GennySettings.mainrealm; // permit temprorary override

    private static final Logger log = Logger.getLogger(ImportSheets.class);


    public ImportSheets() {
    }

    public Realm getRealm() {
        XlsxImport xlsImport;
        XSSFService service = new XSSFService();
        GoogleImportService gs = GoogleImportService.getInstance();
        Boolean onlineMode = Optional.ofNullable(System.getenv("ONLINE_MODE"))
                .map(val -> val.toLowerCase())
                .map(Boolean::getBoolean)
                .orElse(true);

        if (onlineMode) {
            xlsImport = new XlsxImportOnline(gs.getService());
        } else {
            xlsImport = new XlsxImportOffline(service);
        }
        return new Realm(xlsImport,
                System.getenv("GOOGLE_HOSTING_SHEET_ID"));
    }

    public void persistEnabledProject(RealmUnit realmUnit) {
        String realmCode = realmUnit.getCode();
        this.currentRealm = realmCode;
        Boolean skipGoogleDoc = realmUnit.getSkipGoogleDoc();
        if (realmUnit.getDisable() || skipGoogleDoc) {
            log.warn("PROJECT:" + realmCode + "disabled or skip google doc.");
            return;
        }

        log.info("PROJECT " + realmCode);
        persistProject(realmUnit);
        String keycloakJson = constructKeycloakJson(realmUnit);
        upsertKeycloakJson(keycloakJson);
        upsertProjectUrls(realmUnit.getUrlList());
    }


    public Long updateWithAttributes(BaseEntity entity) {
        EntityTransaction transaction = em.getTransaction();
        if (!transaction.isActive()) transaction.begin();
        entity.realm = currentRealm;
        try {
            entity = em.merge(entity);
        } catch (final Exception e) {
            em.persist(entity);
        }
        String json = JsonUtils.toJson(entity);
//        writeToDDT(entity.getCode(), json);
        transaction.commit();
        return entity.id;
    }

    public Integer updateEntityEntity(EntityEntity ee) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        int result = 0;
        try {
            String sql =
                    "update EntityEntity ee set ee.weight=:weight, ee.valueString=:valueString, ee.link.weight=:weight, ee.link.linkValue=:valueString where ee.pk.targetCode=:targetCode and ee.link.attributeCode=:linkAttributeCode and ee.link.sourceCode=:sourceCode";
            result = em.createQuery(sql)
                    .setParameter("sourceCode",
                            ee.getPk().getSource().getCode())
                    .setParameter("linkAttributeCode",
                            ee.getLink().getAttributeCode())
                    .setParameter("targetCode", ee.getPk().getTargetCode())
                    .setParameter("weight", ee.getWeight())
                    .setParameter("valueString", ee.valueString)
                    .executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
        transaction.commit();
        return result;
    }

    public Attribute upsert(Attribute attribute) {
        Attribute existing = findAttributeByCode(attribute.code);
        if (existing == null) {
            attribute.persist();
            return attribute;
        }
        existing.getEntityManager().merge(attribute);
        return existing;
    }

    public Question upsert(Question q) {
        Question existing = findQuestionByCode(q.getCode());
        if (existing == null) {
            q.persist();
            return q;
        }
        existing.getEntityManager().merge(q);
        return existing;
    }

    public void insert(Ask ask) {
        ask.persist();
    }

    //TODO
    public void sendQEventLinkChangeMessage(final QEventLinkChangeMessage event) {
        log.info("Send Link Change:" + event);
    }

    // TODO
    protected String getCurrentToken() {
        return "DUMMY_TOKEN";
    }

    public EntityEntity insertEntityEntity(EntityEntity ee) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.persist(ee);
        QEventLinkChangeMessage msg = new QEventLinkChangeMessage(ee.getLink(), null, getCurrentToken());
        sendQEventLinkChangeMessage(msg);
        transaction.commit();
        log.info(String.format("Sent Event Link Change Msg:%s.", msg));
        return ee;
    }

    public Long updateRealm(Question que) {
        EntityTransaction transaction = em.getTransaction();
        if (!transaction.isActive()) transaction.begin();
        long result = em.createQuery(
                "update Question que set que.realm =:realm where que.code=:code")
                .setParameter("code", que.getCode())
                .setParameter("realm", que.getRealm()).executeUpdate();
        transaction.commit();
        return result;
    }

    public Validation findValidationByCode(@NotNull final String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("realm", currentRealm);
        Validation existing = Validation.find("code = :code and realm= :realm", params).firstResult();
        return existing;
    }

    public Attribute findAttributeByCode(@NotNull String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("realm", currentRealm);
        return Attribute.find("code = :code and realm= :realm", params).firstResult();
    }

    public BaseEntity findBaseEntityByCode(String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("realm", currentRealm);
        BaseEntity existing = BaseEntity.find("code = :code and realm= :realm", params).firstResult();
        return existing;
    }


    public Question findQuestionByCode(@NotNull String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("realm", currentRealm);
        Question existing = Question.find("code = :code and realm= :realm", params).firstResult();
        return existing;
    }


    public Long insert(Question question) {
        EntityTransaction transaction = em.getTransaction();
        if (!transaction.isActive()) transaction.begin();
        try {
            question.setRealm(currentRealm);
            em.persist(question);
            log.info(String.format("Saved question:%s. ", question.getCode()));
            transaction.commit();
        } catch (PersistenceException | IllegalStateException e) {
            Question existing = findQuestionByCode(question.getCode());
            existing.setRealm(currentRealm);
            existing = em.merge(existing);
            transaction.commit();
            return existing.id;
        }
        return question.id;
    }


    public <T> List<T> queryTableByRealm(String tableName, String realm) {
        List<T> result = Collections.emptyList();
        try {
            Query query = em.createQuery(String.format("SELECT temp FROM %s temp where temp.realm=:realmStr", tableName));
            query.setParameter("realmStr", realm);
            result = query.getResultList();
        } catch (Exception e) {
            log.error("Query table %s Error:%s".format(realm, e.getMessage()));
        }
        return result;
    }

    public void bulkUpdate(List<PanacheEntity> objectList, Map<String, PanacheEntity> mapping) {
        if (objectList.isEmpty()) return;
        BeanNotNullFields copyFields = new BeanNotNullFields();
        for (PanacheEntity panacheEntity : objectList) {
            if (panacheEntity instanceof QBaseMSGMessageTemplate) {
                QBaseMSGMessageTemplate obj = ((QBaseMSGMessageTemplate) panacheEntity);
                QBaseMSGMessageTemplate msg = (QBaseMSGMessageTemplate) mapping.get(obj.code);
                msg.name = obj.name;
                msg.setDescription(obj.getDescription());
                msg.setEmail_templateId(obj.getEmail_templateId());
                msg.setSms_template(obj.getSms_template());
                msg.setSubject(obj.getSubject());
                msg.setToast_template(obj.getToast_template());
                em.merge(msg);
                panacheEntity.persist();
            } else {
                if (panacheEntity instanceof BaseEntity) {
                    BaseEntity obj = (BaseEntity) panacheEntity;
                    BaseEntity val = (BaseEntity) (mapping.get(obj.getCode()));
                    if (val == null) {
                        // Should never raise this exception
                        throw new NoResultException(String.format("Can't find %s from database.", obj.getCode()));
                    }
                    try {
                        copyFields.copyProperties(val, obj);
                    } catch (IllegalAccessException | InvocationTargetException ex) {
                        log.error(String.format("Failed to copy Properties for %s", val.getCode()));
                    }

                    val.realm = currentRealm;
                    em.merge(val);
                } else if (panacheEntity instanceof Validation) {

                    Validation obj = (Validation) panacheEntity;
                    Validation val = (Validation) (mapping.get(obj.code));
                    if (val == null) {
                        // Should never raise this exception
                        throw new NoResultException(String.format("Can't find %s from database.", obj.code));
                    }
                    try {
                        copyFields.copyProperties(val, obj);
                    } catch (IllegalAccessException | InvocationTargetException ex) {
                        log.error(String.format("Failed to copy Properties for %s", val.code));
                    }
                    val.realm = currentRealm;
                    em.merge(val);
                } else if (panacheEntity instanceof Attribute) {
                    Attribute obj = (Attribute) panacheEntity;
                    Attribute val = (Attribute) (mapping.get(obj.code));
                    if (val == null) {
                        // Should never raise this exception
                        throw new NoResultException(String.format("Can't find %s from database.", obj.code));
                    }
                    try {
                        copyFields.copyProperties(val, obj);
                    } catch (IllegalAccessException | InvocationTargetException ex) {
                        log.error(String.format("Failed to copy Properties for %s", val.code));
                    }
                    val.realm = currentRealm;
                    em.merge(val);
                }
            }
        }
    }

    public void bulkInsert(List<PanacheEntity> objectList) {
        if (objectList.isEmpty()) return;

        int index = 1;

        for (PanacheEntity panacheEntity : objectList) {
            panacheEntity.persist();
            if (index % BATCHSIZE == 0) {
                //flush a batch of inserts and release memory:
                log.debug("Batch is full, flush to database.");
            }
            index += 1;
        }
    }

    public void bulkInsertQuestionQuestion(List<QuestionQuestion> objectList) {
        if (objectList.isEmpty()) return;

        int index = 1;
        for (QuestionQuestion qq : objectList) {
            if (index % BATCHSIZE == 0) {
                //flush a batch of inserts and release memory:
                log.debug("BaseEntity Batch is full, flush to database.");
            }
            index += 1;
        }
    }

    public void bulkUpdateQuestionQuestion(List<QuestionQuestion> objectList, Map<String, QuestionQuestion> mapping) {
        for (QuestionQuestion qq : objectList) {
            String uniqCode = qq.getSourceCode() + "-" + qq.getTarketCode();
            QuestionQuestion existing = mapping.get(uniqCode.toUpperCase());
            existing.setMandatory(qq.getMandatory());
            existing.setWeight(qq.getWeight());
            existing.setReadonly(qq.getReadonly());
            existing.setDependency(qq.getDependency());
//            em.merge(existing);
            existing.persist();
        }

    }

    @Transactional
    public void cleanAsk(String realm) {
        Map<String, Object> params = new HashMap<>();
        params.put("realm", realm);
        long number = Ask.delete("realm = :realm", params);
        log.info(String.format("Clean up ask, realm:%s, %d ask deleted", realm, number));
    }

    public void cleanFrameFromBaseentityAttribute(String realm) {
        Map<String, Object> params = new HashMap<>();
        params.put("realm", realm);
        long number = EntityAttribute.delete("baseEntityCode like 'RUL_FRM%_GRP' and attributeCode = 'PRI_ASKS' and realm = :realm", params);
        log.info(String.format("Clean up BaseentityAttribute, realm:%s, %d BaseentityAttribute deleted", realm, number));
    }


    public Map<String, DataType> dataType(Map<String, Map<String, String>> project) {
        final Map<String, DataType> dataTypeMap = new HashMap<>();
        project.entrySet().stream().filter(d -> !d.getKey().matches("\\s*")).forEach(data -> {
            Map<String, String> dataType = data.getValue();
            String validations = dataType.get("validations");
            String code = (dataType.get("code")).trim().replaceAll("^\"|\"$", "");
//            String className = (dataType.get("classname")).replaceAll("^\"|\"$", "");
            String name = (dataType.get("name")).replaceAll("^\"|\"$", "");
            String inputmask = dataType.get("inputmask");
            String component = dataType.get("component");
            final ValidationList validationList = new ValidationList();
            validationList.setValidationList(new ArrayList<>());
            if (validations != null) {
                final String[] validationListStr = validations.split(",");
                for (final String validationCode : validationListStr) {
                    try {
                        Validation validation = findValidationByCode(validationCode);
                        validationList.getValidationList().add(validation);
                    } catch (NoResultException e) {
                        log.error("Could not load Validation " + validationCode);
                    }
                }
            }
            if (!dataTypeMap.containsKey(code)) {
                DataType dataTypeRecord;
                if (component == null) {
                    dataTypeRecord = new DataType(name, validationList, name, inputmask);
                } else {
                    dataTypeRecord = new DataType(name, validationList, name, inputmask, component);
                }
                dataTypeRecord.setDttCode(code);
                dataTypeMap.put(code, dataTypeRecord);
            }
        });
        return dataTypeMap;
    }


    public void upsertKeycloakJson(String keycloakJson) {
        final String PROJECT_CODE = "PRJ_" + this.currentRealm.toUpperCase();
        BaseEntity be = findBaseEntityByCode(PROJECT_CODE);

        ValidatorFactory factory = javax.validation.Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Attribute attr = findAttributeByCode("ENV_KEYCLOAK_JSON");
        if (attr == null) {
            DataType dataType = new DataType("DTT_TEXT");
            dataType.setDttCode("DTT_TEXT");
            attr = new Attribute("ENV_KEYCLOAK_JSON", "Keycloak Json", dataType);
            attr.realm = this.currentRealm;
            Set<ConstraintViolation<Attribute>> constraints = validator.validate(attr);
            for (ConstraintViolation<Attribute> constraint : constraints) {
                log.info(String.format("[\"%s\"], %s, %s.", this.currentRealm,
                        constraint.getPropertyPath(), constraint.getMessage()));
            }
            upsert(attr);
        }
        try {
            be.addAttribute(attr, 0.0, keycloakJson);
        } catch (BadDataException e) {
            log.error(String.format("BadDataException:%s", e.getMessage()));
        }

        updateWithAttributes(be);

    }

    public void upsertProjectUrls(String urlList) {

        final String PROJECT_CODE = "PRJ_" + this.currentRealm.toUpperCase();
        BaseEntity be = findBaseEntityByCode(PROJECT_CODE);

        ValidatorFactory factory = javax.validation.Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Attribute attr = findAttributeByCode("ENV_URL_LIST");
        attr.realm = currentRealm;
        DataType dataType = new DataType("DTT_TEXT");
        dataType.setDttCode("DTT_TEXT");
        attr = new Attribute("ENV_URL_LIST", "Url List", dataType);
        Set<ConstraintViolation<Attribute>> constraints = validator.validate(attr);
        for (ConstraintViolation<Attribute> constraint : constraints) {
            log.info(String.format("[\" %s\"] %s, %s.", this.currentRealm, constraint.getPropertyPath(), constraint.getMessage()));
        }
        upsert(attr);
        try {
            be.addAttribute(attr, 0.0, urlList);
        } catch (BadDataException e) {
            log.error(String.format("BadDataException:%s", e.getMessage()));
        }
        updateWithAttributes(be);
    }

    public String constructKeycloakJson(final RealmUnit realm) {
        this.currentRealm = realm.getCode();
        String keycloakUrl = null;
        String keycloakSecret = null;
        String keycloakJson = null;

        keycloakUrl = realm.getKeycloakUrl();
        keycloakSecret = realm.getClientSecret();

        keycloakJson = "{\n" + "  \"realm\": \"" + this.currentRealm + "\",\n" + "  \"auth-server-url\": \"" + keycloakUrl
                + "/auth\",\n" + "  \"ssl-required\": \"external\",\n" + "  \"resource\": \"" + this.currentRealm + "\",\n"
                + "  \"credentials\": {\n" + "    \"secret\": \"" + keycloakSecret + "\" \n" + "  },\n"
                + "  \"policy-enforcer\": {}\n" + "}";

        log.info(String.format("[%s] Loaded keycloak.json:%s ", this.currentRealm, keycloakJson));
        return keycloakJson;

    }

    public void persistProject(life.genny.bootxport.bootx.RealmUnit rx) {
        persistProjectOptimization(rx);
    }

    private String decodePassword(String realm, String securityKey, String servicePass) {
        String initVector = "PRJ_" + realm.toUpperCase();
        initVector = StringUtils.rightPad(initVector, 16, '*');
        String decrypt = SecurityUtils.decrypt(securityKey, initVector, servicePass);
        return decrypt;
    }


    public void persistProjectOptimization(life.genny.bootxport.bootx.RealmUnit rx) {
        boolean isSynchronise = GennySettings.isSynchronise;
        String decrypt = decodePassword(rx.getCode(), rx.getSecurityKey(), rx.getServicePassword());
        HashMap<String, String> userCodeUUIDMapping = KeycloakUtils.getUsersByRealm(rx.getKeycloakUrl(), rx.getCode(), decrypt);
        // clean up
        cleanAsk(rx.getCode());
        cleanFrameFromBaseentityAttribute(rx.getCode());

        validationsOptimization(rx.getValidations(), rx.getCode());

        Map<String, DataType> dataTypes = dataType(rx.getDataTypes());
        attributesOptimization(rx.getAttributes(), dataTypes, rx.getCode());

        baseEntitysOptimization(rx.getBaseEntitys(), rx.getCode(), userCodeUUIDMapping);

        attributeLinksOptimization(rx.getAttributeLinks(), dataTypes, rx.getCode());

        baseEntityAttributesOptimization(rx.getEntityAttributes(), rx.getCode(), userCodeUUIDMapping);

        entityEntitysOptimization(rx.getEntityEntitys(), rx.getCode(), isSynchronise, userCodeUUIDMapping);

        questionsOptimization(rx.getQuestions(), rx.getCode(), isSynchronise);

        questionQuestionsOptimization(rx.getQuestionQuestions(), rx.getCode());

        asksOptimization(rx.getAsks(), rx.getCode());

        messageTemplatesOptimization(rx.getNotifications(), rx.getCode());
        messageTemplatesOptimization(rx.getMessages(), rx.getCode());
    }

    // optimization
    private boolean isValid(PanacheEntity t) {
        if (t == null) return false;
        if (t instanceof Validation || t instanceof Attribute || t instanceof BaseEntity) {
            ValidatorFactory factory = javax.validation.Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<PanacheEntity>> constraints = validator.validate(t);
            for (ConstraintViolation<PanacheEntity> constraint : constraints) {
                // TODO
//                log.error(String.format("Validates constraints failure, Code:%s, PropertyPath:%s,Error:%s.",
//                        t.code, constraint.getPropertyPath(), constraint.getMessage()));
            }
            return constraints.isEmpty();
        }
        return false;
    }

    // Check if sheet data changed
    //TODO
    private <T> boolean isChanged(T orgItem, T newItem) {
        return true;
    }


    private void printSummary(String tableName, Summary summary) {
        log.info(String.format("Table:%s: Total:%d, invalid:%d, skipped:%d, updated:%d, new item:%d.",
                tableName, summary.getTotal(), summary.getInvalid(), summary.getSkipped(), summary.getUpdated(),
                summary.getNewItem()));
    }

    public void asksOptimization(Map<String, Map<String, String>> project, String realmName) {
        // Get all asks
        String tableName = "Ask";
        List<Ask> askFromDB = queryTableByRealm(tableName, realmName);

        HashMap<String, Ask> codeAskMapping = new HashMap<>();
        for (Ask ask : askFromDB) {
            String targetCode = ask.getTargetCode();
            String sourceCode = ask.getSourceCode();
            String attributeCode = ask.getAttributeCode();
            String questionCode = ask.getQuestionCode();
            String uniqueCode = questionCode + "-" + sourceCode + "-" + targetCode + "-" + attributeCode;
            codeAskMapping.put(uniqueCode, ask);
        }

        tableName = "Question";
        List<Question> questionsFromDB = queryTableByRealm(tableName, realmName);
        HashMap<String, Question> questionHashMap = new HashMap<>();

        for (Question q : questionsFromDB) {
            questionHashMap.put(q.getCode(), q);
        }

        Summary summary = new Summary();

        for (Map.Entry<String, Map<String, String>> entry : project.entrySet()) {
            summary.addTotal();
            Map<String, String> asks = entry.getValue();
            Ask ask = GoogleSheetBuilder.buildAsk(asks, realmName, questionHashMap);
            if (ask == null) {
                summary.addInvalid();
                continue;
            }
            insert(ask);
            summary.addNew();
        }
        printSummary("Ask", summary);
    }


    public void attributeLinksOptimization(Map<String, Map<String, String>> project, Map<String, DataType> dataTypeMap,
                                           String realmName) {
        String tableName = "Attribute";
        List<Attribute> attributeLinksFromDB = queryTableByRealm(tableName, realmName);

        HashSet<String> codeSet = new HashSet<>();
        HashMap<String, PanacheEntity> codeAttributeMapping = new HashMap<>();

        for (Attribute attr : attributeLinksFromDB) {
            codeSet.add(attr.code);
            codeAttributeMapping.put(attr.code, attr);
        }

        ArrayList<PanacheEntity> attributeLinkInsertList = new ArrayList<>();
        ArrayList<PanacheEntity> attributeLinkUpdateList = new ArrayList<>();
        Summary summary = new Summary();

        for (Map.Entry<String, Map<String, String>> entry : project.entrySet()) {
            summary.addTotal();
            Map<String, String> attributeLink = entry.getValue();
            String code = attributeLink.get("code").replaceAll("^\"|\"$", "");
            AttributeLink attrlink = GoogleSheetBuilder.buildAttributeLink(attributeLink, dataTypeMap, realmName, code);
            // validation check
            if (isValid(attrlink)) {
                if (codeSet.contains(code.toUpperCase())) {
                    if (isChanged(attrlink, codeAttributeMapping.get(code.toUpperCase()))) {
                        attributeLinkUpdateList.add(attrlink);
                        summary.addUpdated();
                    } else {
                        summary.addSkipped();
                    }
                } else {
                    // insert new item
                    attributeLinkInsertList.add(attrlink);
                    summary.addNew();
                }
            } else {
                summary.addInvalid();
            }
        }

        bulkInsert(attributeLinkInsertList);
        bulkUpdate(attributeLinkUpdateList, codeAttributeMapping);
        printSummary("AttributeLink", summary);
    }

    public void attributesOptimization(Map<String, Map<String, String>> project,
                                       Map<String, DataType> dataTypeMap, String realmName) {
        String tableName = "Attribute";
        List<Attribute> attributesFromDB = queryTableByRealm(tableName, realmName);

        HashMap<String, PanacheEntity> codeAttributeMapping = new HashMap<>();

        for (Attribute attr : attributesFromDB) {
            codeAttributeMapping.put(attr.code, attr);
        }

        ArrayList<PanacheEntity> attributeInsertList = new ArrayList<>();
        ArrayList<PanacheEntity> attributeUpdateList = new ArrayList<>();
        Summary summary = new Summary();

        for (Map.Entry<String, Map<String, String>> data : project.entrySet()) {
            summary.addTotal();
            Map<String, String> attributes = data.getValue();
            String code = attributes.get("code").replaceAll("^\"|\"$", "");

            Attribute attr = GoogleSheetBuilder.buildAttrribute(attributes, dataTypeMap, realmName, code);

            // validation check
            if (isValid(attr)) {
                if (codeAttributeMapping.containsKey(code.toUpperCase())) {
                    if (isChanged(attr, codeAttributeMapping.get(code.toUpperCase()))) {
                        attributeUpdateList.add(attr);
                        summary.addUpdated();
                    } else {
                        summary.addSkipped();
                    }
                } else {
                    // insert new item
                    attributeInsertList.add(attr);
                    summary.addNew();
                }
            } else {
                summary.addInvalid();
            }
        }

        bulkInsert(attributeInsertList);
        bulkUpdate(attributeUpdateList, codeAttributeMapping);
        printSummary(tableName, summary);
    }

    public void baseEntityAttributesOptimization(Map<String, Map<String, String>> project, String realmName,
                                                 HashMap<String, String> userCodeUUIDMapping) {
        // Get all BaseEntity
        String tableName = "BaseEntity";
        List<BaseEntity> baseEntityFromDB = queryTableByRealm(tableName, realmName);
        HashMap<String, BaseEntity> beHashMap = new HashMap<>();
        for (BaseEntity be : baseEntityFromDB) {
            beHashMap.put(be.getCode(), be);
        }

        // Get all Attribute
        tableName = "Attribute";
        List<Attribute> attributeFromDB = queryTableByRealm(tableName, realmName);
        HashMap<String, Attribute> attrHashMap = new HashMap<>();
        for (Attribute attribute : attributeFromDB) {
            attrHashMap.put(attribute.code, attribute);
        }

        Summary summary = new Summary();

        for (Map.Entry<String, Map<String, String>> entry : project.entrySet()) {
            summary.addTotal();
            Map<String, String> baseEntityAttr = entry.getValue();

            String baseEntityCode = GoogleSheetBuilder.getBaseEntityCodeFromBaseEntityAttribute(baseEntityAttr,
                    userCodeUUIDMapping);
            if (baseEntityCode == null) {
                summary.addInvalid();
                continue;
            }
            String attributeCode = GoogleSheetBuilder.getAttributeCodeFromBaseEntityAttribute(baseEntityAttr);
            if (attributeCode == null) {
                summary.addInvalid();
                continue;
            }

            BaseEntity be = GoogleSheetBuilder.buildEntityAttribute(baseEntityAttr, realmName, attrHashMap, beHashMap,
                    userCodeUUIDMapping);
            if (be != null) {
                updateWithAttributes(be);
                summary.addNew();
            } else {
                summary.addInvalid();
            }
        }
        printSummary("BaseEntityAttributes", summary);
    }

    public void baseEntitysOptimization(Map<String, Map<String, String>> project, String realmName,
                                        HashMap<String, String> userCodeUUIDMapping) {
        String tableName = "BaseEntity";
        List<BaseEntity> baseEntityFromDB = queryTableByRealm(tableName, realmName);

        HashMap<String, PanacheEntity> codeBaseEntityMapping = new HashMap<>();

        for (BaseEntity be : baseEntityFromDB) {
            codeBaseEntityMapping.put(be.getCode(), be);
        }

        ArrayList<PanacheEntity> baseEntityInsertList = new ArrayList<>();
        ArrayList<PanacheEntity> baseEntityUpdateList = new ArrayList<>();
        Summary summary = new Summary();

        for (Map.Entry<String, Map<String, String>> entry : project.entrySet()) {
            summary.addTotal();
            Map<String, String> baseEntitys = entry.getValue();
            String code = baseEntitys.get("code").replaceAll("^\"|\"$", "");
            BaseEntity baseEntity = GoogleSheetBuilder.buildBaseEntity(baseEntitys, realmName);
            // validation check
            if (isValid(baseEntity)) {
                // get keycloak uuid from keycloak, replace code and beasentity
                if (baseEntity.getCode().startsWith("PER_")) {
                    String keycloakUUID = KeycloakUtils.getKeycloakUUIDByUserCode(baseEntity.getCode(), userCodeUUIDMapping);
                    baseEntity.setCode(keycloakUUID);
                }

                if (codeBaseEntityMapping.containsKey(baseEntity.getCode())) {
                    if (isChanged(baseEntity, codeBaseEntityMapping.get(baseEntity.getCode()))) {
                        baseEntityUpdateList.add(baseEntity);
                        summary.addUpdated();
                    } else {
                        summary.addSkipped();
                    }
                } else {
                    // insert new item
                    baseEntityInsertList.add(baseEntity);
                    summary.addNew();
                }
            } else {
                summary.addInvalid();
            }
        }
        bulkInsert(baseEntityInsertList);
        bulkUpdate(baseEntityUpdateList, codeBaseEntityMapping);
        printSummary(tableName, summary);
    }

    public void entityEntitysOptimization(Map<String, Map<String, String>> project, String realmName,
                                          boolean isSynchronise, HashMap<String, String> userCodeUUIDMapping) {
        // Get all BaseEntity
        String tableName = "BaseEntity";
        List<BaseEntity> baseEntityFromDB = queryTableByRealm(tableName, realmName);
        HashMap<String, BaseEntity> beHashMap = new HashMap<>();
        for (BaseEntity be : baseEntityFromDB) {
            beHashMap.put(be.getCode(), be);
        }

        // Get all Attribute
        tableName = "Attribute";
        List<Attribute> attributeFromDB = queryTableByRealm(tableName, realmName);
        HashMap<String, Attribute> attrHashMap = new HashMap<>();
        for (Attribute attribute : attributeFromDB) {
            attrHashMap.put(attribute.code, attribute);
        }

        tableName = "EntityEntity";
        List<EntityEntity> entityEntityFromDB = queryTableByRealm(tableName, realmName);

        HashMap<String, EntityEntity> codeBaseEntityEntityMapping = new HashMap<>();
        for (EntityEntity entityEntity : entityEntityFromDB) {
            String beCode = entityEntity.getPk().getSource().getCode();
            String attrCode = entityEntity.getPk().getAttribute().code;
            String targetCode = entityEntity.getPk().getTargetCode();
            if (targetCode.toUpperCase().startsWith("PER_")) {
                targetCode = KeycloakUtils.getKeycloakUUIDByUserCode(targetCode.toUpperCase(), userCodeUUIDMapping);
            }
            String uniqueCode = beCode + "-" + attrCode + "-" + targetCode;
            codeBaseEntityEntityMapping.put(uniqueCode, entityEntity);
        }

        Summary summary = new Summary();

        for (Map.Entry<String, Map<String, String>> entry : project.entrySet()) {
            summary.addTotal();
            Map<String, String> entEnts = entry.getValue();
            String linkCode = entEnts.get("linkCode".toLowerCase().replaceAll("^\"|\"$|_|-", ""));

            if (linkCode == null)
                linkCode = entEnts.get("code".toLowerCase().replaceAll("^\"|\"$|_|-", ""));

            String parentCode = entEnts.get("parentCode".toLowerCase().replaceAll("^\"|\"$|_|-", ""));
            if (parentCode == null)
                parentCode = entEnts.get("sourceCode".toLowerCase().replaceAll("^\"|\"$|_|-", ""));

            String targetCode = entEnts.get("targetCode".toLowerCase().replaceAll("^\"|\"$|_|-", ""));
            if (targetCode.toUpperCase().startsWith("PER_")) {
                targetCode = KeycloakUtils.getKeycloakUUIDByUserCode(targetCode.toUpperCase(), userCodeUUIDMapping);
            }

            String weightStr = entEnts.get("weight");
            String valueString = entEnts.get("valueString".toLowerCase().replaceAll("^\"|\"$|_|-", ""));
            Optional<String> weightStrOpt = Optional.ofNullable(weightStr);
            final Double weight = weightStrOpt.filter(d -> !d.equals(" ")).map(Double::valueOf).orElse(0.0);

            Attribute linkAttribute = attrHashMap.get(linkCode.toUpperCase());
            BaseEntity sbe = beHashMap.get(parentCode.toUpperCase());
            BaseEntity tbe = beHashMap.get(targetCode.toUpperCase());
            if (linkAttribute == null) {
                log.error("EntityEntity Link code:" + linkCode + " doesn't exist in Attribute table.");
                summary.addInvalid();
                continue;
            } else if (sbe == null) {
                log.error("EntityEntity parent code:" + parentCode + " doesn't exist in BaseEntity table.");
                summary.addInvalid();
                continue;
            } else if (tbe == null) {
                log.error("EntityEntity target Code:" + targetCode + " doesn't exist in BaseEntity table.");
                summary.addInvalid();
                continue;
            }

            String code = parentCode + "-" + linkCode + "-" + targetCode;
            if (isSynchronise) {
                if (codeBaseEntityEntityMapping.containsKey(code.toUpperCase())) {
                    EntityEntity ee = codeBaseEntityEntityMapping.get(code.toUpperCase());
                    ee.setWeight(weight);
                    ee.valueString = valueString;
                    updateEntityEntity(ee);
                    summary.addUpdated();
                } else {
                    EntityEntity ee = new EntityEntity(sbe, tbe, linkAttribute, weight);
                    ee.valueString = valueString;
                    insertEntityEntity(ee);
                    summary.addNew();
                }
            } else {
                try {
                    sbe.addTarget(tbe, linkAttribute, weight, valueString);
                    updateWithAttributes(sbe);
                    summary.addNew();
                } catch (BadDataException be) {
                    log.error(String.format("Should never reach here!, BaseEntity:%s, Attribute:%s ", tbe.getCode(), linkAttribute.code));
                }
            }
        }
        printSummary("EntityEntity", summary);
    }

    public void messageTemplatesOptimization(Map<String, Map<String, String>> project, String realmName) {
        String tableName = "QBaseMSGMessageTemplate";
        List<QBaseMSGMessageTemplate> qBaseMSGMessageTemplateFromDB = queryTableByRealm(tableName, realmName);

        HashMap<String, PanacheEntity> codeMsgMapping = new HashMap<>();
        for (QBaseMSGMessageTemplate message : qBaseMSGMessageTemplateFromDB) {
            codeMsgMapping.put(message.getCode(), message);
        }

        ArrayList<PanacheEntity> messageInsertList = new ArrayList<>();
        ArrayList<PanacheEntity> messageUpdateList = new ArrayList<>();
        Summary summary = new Summary();

        for (Map.Entry<String, Map<String, String>> data : project.entrySet()) {
            summary.addTotal();
            Map<String, String> template = data.getValue();
            String code = template.get("code");
            String name = template.get("name");
            if (StringUtils.isBlank(name)) {
                log.error("Templates:" + code + "has EMPTY name.");
                summary.addInvalid();
                continue;
            }

            QBaseMSGMessageTemplate msg = GoogleSheetBuilder.buildQBaseMSGMessageTemplate(template, realmName);
            if (codeMsgMapping.containsKey(code.toUpperCase())) {
                if (isChanged(msg, codeMsgMapping.get(code.toUpperCase()))) {
                    messageUpdateList.add(msg);
                    summary.addUpdated();
                } else {
                    summary.addSkipped();
                }
            } else {
                // insert new item
                messageInsertList.add(msg);
                summary.addNew();
            }
        }
        bulkInsert(messageInsertList);
        bulkUpdate(messageUpdateList, codeMsgMapping);
        printSummary(tableName, summary);
    }

    public void questionQuestionsOptimization(Map<String, Map<String, String>> project, String realmName) {
        String tableName = "Question";
        List<Question> questionFromDB = queryTableByRealm(tableName, realmName);
        HashSet<String> questionCodeSet = new HashSet<>();
        HashMap<String, Question> questionHashMap = new HashMap<>();

        for (Question question : questionFromDB) {
            questionCodeSet.add(question.getCode());
            questionHashMap.put(question.getCode(), question);
        }

        tableName = "QuestionQuestion";
        List<QuestionQuestion> questionQuestionFromDB = queryTableByRealm(tableName, realmName);

        HashMap<String, QuestionQuestion> codeQuestionMapping = new HashMap<>();

        for (QuestionQuestion qq : questionQuestionFromDB) {
            String sourceCode = qq.getSourceCode();
            String targetCode = qq.getTarketCode();
            String uniqCode = sourceCode + "-" + targetCode;
            codeQuestionMapping.put(uniqCode, qq);
        }

        ArrayList<QuestionQuestion> questionQuestionInsertList = new ArrayList<>();
        ArrayList<QuestionQuestion> questionQuestionUpdateList = new ArrayList<>();

        Summary summary = new Summary();

        for (Map.Entry<String, Map<String, String>> entry : project.entrySet()) {
            summary.addTotal();
            Map<String, String> queQues = entry.getValue();

            QuestionQuestion qq = GoogleSheetBuilder.buildQuestionQuestion(queQues, realmName, questionHashMap);
            if (qq == null) {
                summary.addInvalid();
                continue;
            }

            String parentCode = queQues.get("parentCode".toLowerCase().replaceAll("^\"|\"$|_|-", ""));
            if (parentCode == null) {
                parentCode = queQues.get("sourceCode".toLowerCase().replaceAll("^\"|\"$|_|-", ""));
            }

            String targetCode = queQues.get("targetCode".toLowerCase().replaceAll("^\"|\"$|_|-", ""));

            String uniqueCode = parentCode + "-" + targetCode;
            if (codeQuestionMapping.containsKey(uniqueCode.toUpperCase())) {
                if (isChanged(qq, codeQuestionMapping.get(uniqueCode.toUpperCase()))) {
                    questionQuestionUpdateList.add(qq);
                    summary.addUpdated();
                } else {
                    summary.addSkipped();
                }
            } else {
                // insert new item
                questionQuestionInsertList.add(qq);
                summary.addNew();
            }
        }
        bulkInsertQuestionQuestion(questionQuestionInsertList);
        bulkUpdateQuestionQuestion(questionQuestionUpdateList, codeQuestionMapping);
        printSummary("QuestionQuestion", summary);
    }

    // TODO, make it quicker
    public void questionsOptimization(Map<String, Map<String, String>> project, String realmName, boolean isSynchronise) {
        // Get all questions from database
        String tableName = "Question";
        String mainRealm = GennySettings.mainrealm;
        List<Question> questionsFromDBMainRealm = new ArrayList<>();
        HashMap<String, Question> codeQuestionMappingMainRealm = new HashMap<>();

        if (!realmName.equals(mainRealm)) {
            questionsFromDBMainRealm = queryTableByRealm(tableName, mainRealm);
            for (Question q : questionsFromDBMainRealm) {
                codeQuestionMappingMainRealm.put(q.getCode(), q);
            }
        }

        List<Question> questionsFromDB = queryTableByRealm(tableName, realmName);
        HashMap<String, Question> codeQuestionMapping = new HashMap<>();

        for (Question q : questionsFromDB) {
            codeQuestionMapping.put(q.getCode(), q);
        }

        // Get all Attributes from database
        tableName = "Attribute";
        List<Attribute> attributesFromDB = queryTableByRealm(tableName, realmName);
        HashMap<String, Attribute> attributeHashMap = new HashMap<>();

        for (Attribute attribute : attributesFromDB) {
            attributeHashMap.put(attribute.code, attribute);
        }


        Summary summary = new Summary();

        for (Map.Entry<String, Map<String, String>> rawData : project.entrySet()) {
            summary.addTotal();
            if (rawData.getKey().isEmpty()) {
                summary.addSkipped();
                continue;
            }

            Map<String, String> questions = rawData.getValue();
            String code = questions.get("code");

            Question question = GoogleSheetBuilder.buildQuestion(questions, attributeHashMap, realmName);
            if (question == null) {
                summary.addInvalid();
                continue;
            }

            Question existing = codeQuestionMapping.get(code.toUpperCase());
            if (existing == null) {
                if (isSynchronise) {
                    Question val = codeQuestionMappingMainRealm.get(code.toUpperCase());
                    if (val != null) {
                        val.setRealm(realmName);
                        updateRealm(val);
                        summary.addUpdated();
                        continue;
                    }
                }
                insert(question);
                summary.addNew();
            } else {
                String name = questions.get("name");
                String html = questions.get("html");
                String directions = questions.get("directions");
                String helper = questions.get("helper");
                existing.setName(name);
                existing.setHtml(html);
                existing.setDirections(directions);
                existing.setHelper(helper);

                String oneshotStr = questions.get("oneshot");
                String readonlyStr = questions.get(GoogleSheetBuilder.READONLY);
                String mandatoryStr = questions.get(GoogleSheetBuilder.MANDATORY);
                boolean oneshot = GoogleSheetBuilder.getBooleanFromString(oneshotStr);
                boolean readonly = GoogleSheetBuilder.getBooleanFromString(readonlyStr);
                boolean mandatory = GoogleSheetBuilder.getBooleanFromString(mandatoryStr);
                existing.setOneshot(oneshot);
                existing.setReadonly(readonly);
                existing.setMandatory(mandatory);
                upsert(existing);
                summary.addUpdated();
            }
        }
        printSummary("Question", summary);
    }

    public void validationsOptimization(Map<String, Map<String, String>> project, String realmName) {
        String tableName = "Validation";
        // Get existing validation by realm from database
        List<Validation> validationsFromDB = queryTableByRealm(tableName, realmName);

        // Unique code set
        HashSet<String> codeSet = new HashSet<>();
        // Code to validation object mapping
        HashMap<String, PanacheEntity> codeValidationMapping = new HashMap<>();

        for (Validation vld : validationsFromDB) {
            codeSet.add(vld.code);
            codeValidationMapping.put(vld.code, vld);
        }

        ArrayList<PanacheEntity> validationInsertList = new ArrayList<>();
        ArrayList<PanacheEntity> validationUpdateList = new ArrayList<>();
        Summary summary = new Summary();
        for (Map<String, String> validations : project.values()) {
            summary.addTotal();
            String code = validations.get("code").replaceAll("^\"|\"$", "");
            Validation val = GoogleSheetBuilder.buildValidation(validations, realmName, code);

            // validation check
            if (isValid(val)) {
                if (codeSet.contains(code.toUpperCase())) {
                    if (isChanged(val, codeValidationMapping.get(code.toUpperCase()))) {
                        validationUpdateList.add(val);
                        summary.addUpdated();
                    } else {
                        summary.addSkipped();
                    }
                } else {
                    validationInsertList.add(val);
                    summary.addNew();
                }
            } else {
                summary.addInvalid();
            }
        }
        bulkInsert(validationInsertList);
        bulkUpdate(validationUpdateList, codeValidationMapping);
        printSummary(tableName, summary);
    }

    @GET
    @Transactional
    public void doBatchLoading() {
        Realm rx = getRealm();
        StateManagement.initStateManagement(rx);
        if (!GennySettings.skipGoogleDocInStartup) {
            log.info("Starting Transaction for loading *********************");

            rx.getDataUnits().forEach(this::persistEnabledProject);
            log.info("*********************** Finished Google Doc Import ***********************************");
        } else {
            log.info("Skipping Google doc loading");
        }
    }
}
