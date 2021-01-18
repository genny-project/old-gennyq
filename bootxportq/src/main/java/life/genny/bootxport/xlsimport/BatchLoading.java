package life.genny.bootxport.xlsimport;

import life.genny.bootxport.bootx.RealmUnit;
import life.genny.models.attribute.Attribute;
import life.genny.models.datatype.DataType;
import life.genny.models.entity.BaseEntity;
import life.genny.models.exception.BadDataException;
import life.genny.models.validation.Validation;
import life.genny.models.validation.ValidationList;
import life.genny.nest.utils.KeycloakUtils;
import life.genny.qwandautils.GennySettings;
import life.genny.services.QwandaRepository;
import life.genny.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.lang.invoke.MethodHandles;
import java.util.*;

class Options {
    public String optionCode = null;
    public String optionLabel = null;
}

public class BatchLoading {
    private QwandaRepository service;

    private String mainRealm;

    protected static final Logger log = org.apache.logging.log4j.LogManager
            .getLogger(MethodHandles.lookup().lookupClass().getCanonicalName());

    public BatchLoading(QwandaRepository repo) {
        this.service = repo;
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
                        Validation validation = service.findValidationByCode(validationCode);
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
        final String PROJECT_CODE = "PRJ_" + this.mainRealm.toUpperCase();
        BaseEntity be = service.findBaseEntityByCode(PROJECT_CODE);

        ValidatorFactory factory = javax.validation.Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Attribute attr = service.findAttributeByCode("ENV_KEYCLOAK_JSON");
        if (attr == null) {
            DataType dataType = new DataType("DTT_TEXT");
            dataType.setDttCode("DTT_TEXT");
            attr = new Attribute("ENV_KEYCLOAK_JSON", "Keycloak Json", dataType);
            attr.realm = mainRealm;
            Set<ConstraintViolation<Attribute>> constraints = validator.validate(attr);
            for (ConstraintViolation<Attribute> constraint : constraints) {
                log.info(String.format("[\"%s\"], %s, %s.", this.mainRealm,
                        constraint.getPropertyPath(), constraint.getMessage()));
            }
            service.upsert(attr);
        }
        try {
            be.addAttribute(attr, 0.0, keycloakJson);
        } catch (BadDataException e) {
            log.error(String.format("BadDataException:%s", e.getMessage()));
        }

        service.updateWithAttributes(be);

    }

    public void upsertProjectUrls(String urlList) {

        final String PROJECT_CODE = "PRJ_" + this.mainRealm.toUpperCase();
        BaseEntity be = service.findBaseEntityByCode(PROJECT_CODE);

        ValidatorFactory factory = javax.validation.Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Attribute attr = service.findAttributeByCode("ENV_URL_LIST");
        attr.realm = mainRealm;
        DataType dataType = new DataType("DTT_TEXT");
        dataType.setDttCode("DTT_TEXT");
        attr = new Attribute("ENV_URL_LIST", "Url List", dataType);
        Set<ConstraintViolation<Attribute>> constraints = validator.validate(attr);
        for (ConstraintViolation<Attribute> constraint : constraints) {
            log.info(String.format("[\" %s\"] %s, %s.", this.mainRealm, constraint.getPropertyPath(), constraint.getMessage()));
        }
        service.upsert(attr);
        try {
            be.addAttribute(attr, 0.0, urlList);
        } catch (BadDataException e) {
            log.error(String.format("BadDataException:%s", e.getMessage()));
        }
        service.updateWithAttributes(be);
    }

    public String constructKeycloakJson(final RealmUnit realm) {
        this.mainRealm = realm.getCode();
        String keycloakUrl = null;
        String keycloakSecret = null;
        String keycloakJson = null;

        keycloakUrl = realm.getKeycloakUrl();
        keycloakSecret = realm.getClientSecret();

        keycloakJson = "{\n" + "  \"realm\": \"" + this.mainRealm + "\",\n" + "  \"auth-server-url\": \"" + keycloakUrl
                + "/auth\",\n" + "  \"ssl-required\": \"external\",\n" + "  \"resource\": \"" + this.mainRealm + "\",\n"
                + "  \"credentials\": {\n" + "    \"secret\": \"" + keycloakSecret + "\" \n" + "  },\n"
                + "  \"policy-enforcer\": {}\n" + "}";

        log.info(String.format("[%s] Loaded keycloak.json:%s ", this.mainRealm, keycloakJson));
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
        Optimization optimization = new Optimization(service);

        // clean up
        service.cleanAsk(rx.getCode());
        service.cleanFrameFromBaseentityAttribute(rx.getCode());

        optimization.validationsOptimization(rx.getValidations(), rx.getCode());

        Map<String, DataType> dataTypes = dataType(rx.getDataTypes());
        optimization.attributesOptimization(rx.getAttributes(), dataTypes, rx.getCode());

        optimization.baseEntitysOptimization(rx.getBaseEntitys(), rx.getCode(), userCodeUUIDMapping);

        optimization.attributeLinksOptimization(rx.getAttributeLinks(), dataTypes, rx.getCode());

        optimization.baseEntityAttributesOptimization(rx.getEntityAttributes(), rx.getCode(), userCodeUUIDMapping);

        optimization.entityEntitysOptimization(rx.getEntityEntitys(), rx.getCode(), isSynchronise, userCodeUUIDMapping);

        optimization.questionsOptimization(rx.getQuestions(), rx.getCode(), isSynchronise);

        optimization.questionQuestionsOptimization(rx.getQuestionQuestions(), rx.getCode());

        optimization.asksOptimization(rx.getAsks(), rx.getCode());

        optimization.messageTemplatesOptimization(rx.getNotifications(), rx.getCode());
        optimization.messageTemplatesOptimization(rx.getMessages(), rx.getCode());
    }
}
