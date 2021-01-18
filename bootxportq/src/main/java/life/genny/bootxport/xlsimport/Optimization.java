package life.genny.bootxport.xlsimport;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import life.genny.qwanda.*;
import life.genny.models.attribute.Attribute;
import life.genny.models.attribute.AttributeLink;
import life.genny.models.datatype.DataType;
import life.genny.models.entity.BaseEntity;
import life.genny.models.entity.EntityEntity;
import life.genny.models.exception.BadDataException;
import life.genny.qwanda.message.QBaseMSGMessageTemplate;
import life.genny.models.validation.Validation;
import life.genny.qwandautils.GennySettings;
import life.genny.nest.utils.KeycloakUtils;

import life.genny.services.QwandaRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;

public class Optimization {
    private static final Logger log = LoggerFactory.getLogger(Optimization.class);
    private QwandaRepository service;

    public Optimization(QwandaRepository repo) {
        this.service = repo;
    }

    private void printSummary(String tableName, Summary summary) {
        log.info(String.format("Table:%s: Total:%d, invalid:%d, skipped:%d, updated:%d, new item:%d.",
                tableName, summary.getTotal(), summary.getInvalid(), summary.getSkipped(), summary.getUpdated(),
                summary.getNewItem()));
    }


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

    public void asksOptimization(Map<String, Map<String, String>> project, String realmName) {
        // Get all asks
        String tableName = "Ask";
        List<Ask> askFromDB = service.queryTableByRealm(tableName, realmName);

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
        List<Question> questionsFromDB = service.queryTableByRealm(tableName, realmName);
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
            service.insert(ask);
            summary.addNew();
        }
        printSummary("Ask", summary);
    }

    public void attributeLinksOptimization(Map<String, Map<String, String>> project, Map<String, DataType> dataTypeMap,
                                           String realmName) {
        String tableName = "Attribute";
        List<Attribute> attributeLinksFromDB = service.queryTableByRealm(tableName, realmName);

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

        service.bulkInsert(attributeLinkInsertList);
        service.bulkUpdate(attributeLinkUpdateList, codeAttributeMapping);
        printSummary("AttributeLink", summary);
    }

    public void attributesOptimization(Map<String, Map<String, String>> project,
                                       Map<String, DataType> dataTypeMap, String realmName) {
        String tableName = "Attribute";
        List<Attribute> attributesFromDB = service.queryTableByRealm(tableName, realmName);

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

        service.bulkInsert(attributeInsertList);
        service.bulkUpdate(attributeUpdateList, codeAttributeMapping);
        printSummary(tableName, summary);
    }

    public void baseEntityAttributesOptimization(Map<String, Map<String, String>> project, String realmName,
                                                 HashMap<String, String> userCodeUUIDMapping) {
        // Get all BaseEntity
        String tableName = "BaseEntity";
        List<BaseEntity> baseEntityFromDB = service.queryTableByRealm(tableName, realmName);
        HashMap<String, BaseEntity> beHashMap = new HashMap<>();
        for (BaseEntity be : baseEntityFromDB) {
            beHashMap.put(be.getCode(), be);
        }

        // Get all Attribute
        tableName = "Attribute";
        List<Attribute> attributeFromDB = service.queryTableByRealm(tableName, realmName);
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
                service.updateWithAttributes(be);
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
        List<BaseEntity> baseEntityFromDB = service.queryTableByRealm(tableName, realmName);

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
        service.bulkInsert(baseEntityInsertList);
        service.bulkUpdate(baseEntityUpdateList, codeBaseEntityMapping);
        printSummary(tableName, summary);
    }

    public void entityEntitysOptimization(Map<String, Map<String, String>> project, String realmName,
                                          boolean isSynchronise, HashMap<String, String> userCodeUUIDMapping) {
        // Get all BaseEntity
        String tableName = "BaseEntity";
        List<BaseEntity> baseEntityFromDB = service.queryTableByRealm(tableName, realmName);
        HashMap<String, BaseEntity> beHashMap = new HashMap<>();
        for (BaseEntity be : baseEntityFromDB) {
            beHashMap.put(be.getCode(), be);
        }

        // Get all Attribute
        tableName = "Attribute";
        List<Attribute> attributeFromDB = service.queryTableByRealm(tableName, realmName);
        HashMap<String, Attribute> attrHashMap = new HashMap<>();
        for (Attribute attribute : attributeFromDB) {
            attrHashMap.put(attribute.code, attribute);
        }

        tableName = "EntityEntity";
        List<EntityEntity> entityEntityFromDB = service.queryTableByRealm(tableName, realmName);

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
                    service.updateEntityEntity(ee);
                    summary.addUpdated();
                } else {
                    EntityEntity ee = new EntityEntity(sbe, tbe, linkAttribute, weight);
                    ee.valueString = valueString;
                    service.insertEntityEntity(ee);
                    summary.addNew();
                }
            } else {
                try {
                    sbe.addTarget(tbe, linkAttribute, weight, valueString);
                    service.updateWithAttributes(sbe);
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
        List<QBaseMSGMessageTemplate> qBaseMSGMessageTemplateFromDB = service.queryTableByRealm(tableName, realmName);

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
        service.bulkInsert(messageInsertList);
        service.bulkUpdate(messageUpdateList, codeMsgMapping);
        printSummary(tableName, summary);
    }

    public void questionQuestionsOptimization(Map<String, Map<String, String>> project, String realmName) {
        String tableName = "Question";
        List<Question> questionFromDB = service.queryTableByRealm(tableName, realmName);
        HashSet<String> questionCodeSet = new HashSet<>();
        HashMap<String, Question> questionHashMap = new HashMap<>();

        for (Question question : questionFromDB) {
            questionCodeSet.add(question.getCode());
            questionHashMap.put(question.getCode(), question);
        }

        tableName = "QuestionQuestion";
        List<QuestionQuestion> questionQuestionFromDB = service.queryTableByRealm(tableName, realmName);

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
        service.bulkInsertQuestionQuestion(questionQuestionInsertList);
        service.bulkUpdateQuestionQuestion(questionQuestionUpdateList, codeQuestionMapping);
        printSummary("QuestionQuestion", summary);
    }

    public void questionsOptimization(Map<String, Map<String, String>> project, String realmName, boolean isSynchronise) {
        // Get all questions from database
        String tableName = "Question";
        String mainRealm = GennySettings.mainrealm;
        List<Question> questionsFromDBMainRealm = new ArrayList<>();
        HashMap<String, Question> codeQuestionMappingMainRealm = new HashMap<>();

        if (!realmName.equals(mainRealm)) {
            questionsFromDBMainRealm = service.queryTableByRealm(tableName, mainRealm);
            for (Question q : questionsFromDBMainRealm) {
                codeQuestionMappingMainRealm.put(q.getCode(), q);
            }
        }

        List<Question> questionsFromDB = service.queryTableByRealm(tableName, realmName);
        HashMap<String, Question> codeQuestionMapping = new HashMap<>();

        for (Question q : questionsFromDB) {
            codeQuestionMapping.put(q.getCode(), q);
        }

        // Get all Attributes from database
        tableName = "Attribute";
        List<Attribute> attributesFromDB = service.queryTableByRealm(tableName, realmName);
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
                        service.updateRealm(val);
                        summary.addUpdated();
                        continue;
                    }
                }
                service.insert(question);
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
                service.upsert(existing);
                summary.addUpdated();
            }
        }
        printSummary("Question", summary);
    }

    public void validationsOptimization(Map<String, Map<String, String>> project, String realmName) {
        String tableName = "Validation";
        // Get existing validation by realm from database
        List<Validation> validationsFromDB = service.queryTableByRealm(tableName, realmName);

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
        service.bulkInsert(validationInsertList);
        service.bulkUpdate(validationUpdateList, codeValidationMapping);
        printSummary(tableName, summary);
    }
}
