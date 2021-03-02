package life.genny.bootxport.xlsimport;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import life.genny.qwanda.Ask;
import life.genny.qwanda.Question;
import life.genny.qwanda.QuestionQuestion;
import life.genny.models.Value;
import life.genny.models.attribute.Attribute;
import life.genny.models.attribute.AttributeLink;
import life.genny.models.attribute.EntityAttribute;
import life.genny.models.datatype.DataType;
import life.genny.models.entity.BaseEntity;
import life.genny.models.exception.BadDataException;
import life.genny.qwanda.message.QBaseMSGMessageTemplate;
import life.genny.models.validation.Validation;
import life.genny.models.validation.ValidationList;
import life.genny.qwandautils.KeycloakUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import javax.ws.rs.NotFoundException;

public class GoogleSheetBuilder {
    private static final Log log = LogFactory.getLog(GoogleSheetBuilder.class);
    private static final String WEIGHT = "weight";
    private static final String REGEX_1 = "^\"|\"$";
    private static final String REGEX_2 = "^\"|\"$|_|-";
    private static final String PRIVACY = "privacy";
    private static final String VALUEINTEGER = "valueinteger";
    public static final String MANDATORY = "mandatory";
    public static final String READONLY = "readonly";

    class Options {
        public String optionCode = null;
        public String optionLabel = null;
    }

    private GoogleSheetBuilder() {
    }

    private static boolean isDouble(String doubleStr) {
        final String Digits = "(\\p{Digit}+)";
        final String HexDigits = "(\\p{XDigit}+)";
        // an exponent is 'e' or 'E' followed by an optionally
        // signed decimal integer.
        final String Exp = "[eE][+-]?" + Digits;
        final String fpRegex =
                ("[\\x00-\\x20]*" +  // Optional leading "whitespace"
                        "[+-]?(" + // Optional sign character
                        "NaN|" +           // "NaN" string
                        "Infinity|" +      // "Infinity" string

                        // A decimal floating-point string representing a finite positive
                        // number without a leading sign has at most five basic pieces:
                        // Digits . Digits ExponentPart FloatTypeSuffix
                        //
                        // Since this method allows integer-only strings as input
                        // in addition to strings of floating-point literals, the
                        // two sub-patterns below are simplifications of the grammar
                        // productions from section 3.10.2 of
                        // The Java Language Specification.

                        // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
                        "(((" + Digits + "(\\.)?(" + Digits + "?)(" + Exp + ")?)|" +

                        // . Digits ExponentPart_opt FloatTypeSuffix_opt
                        "(\\.(" + Digits + ")(" + Exp + ")?)|" +

                        // Hexadecimal strings
                        "((" +
                        // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
                        "(0[xX]" + HexDigits + "(\\.)?)|" +

                        // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
                        "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

                        ")[pP][+-]?" + Digits + "))" +
                        "[fFdD]?))" +
                        "[\\x00-\\x20]*");// Optional trailing "whitespace"

        return Pattern.matches(fpRegex, doubleStr);
    }

    public static boolean getBooleanFromString(final String booleanString) {
        if (booleanString == null) {
            return false;
        }
        return "TRUE".equalsIgnoreCase(booleanString) || "YES".equalsIgnoreCase(booleanString)
                || "T".equalsIgnoreCase(booleanString)
                || "Y".equalsIgnoreCase(booleanString) || "1".equalsIgnoreCase(booleanString);

    }

    public static Validation buildValidation(Map<String, String> validations, String realmName, String code) {
        boolean hasValidOptions = false;
        Gson gsonObject = new Gson();
        String optionString = validations.get("options");
        if (optionString != null && (!optionString.equals(" "))) {
            try {
                gsonObject.fromJson(optionString, Options[].class);
                log.trace("FOUND VALID OPTIONS STRING:" + optionString);
                hasValidOptions = true;
            } catch (JsonSyntaxException ex) {
                log.error("FOUND INVALID OPTIONS STRING:" + optionString);
                throw new JsonSyntaxException(ex.getMessage());
            }
        }

        String regex = validations.get("regex");
        if (regex != null) {
            regex = regex.replaceAll(REGEX_1, "");
        }
        if ("VLD_AU_DRIVER_LICENCE_NO".equalsIgnoreCase(code)) {
            log.trace("detected VLD_AU_DRIVER_LICENCE_NO");
        }
        String name = validations.get("name").replaceAll(REGEX_1, "");
        String recursiveStr = validations.get("recursive");
        String multiAllowedStr = validations.get("multi_allowed".toLowerCase().replaceAll(REGEX_2, ""));
        String groupCodesStr = validations.get("group_codes".toLowerCase().replaceAll(REGEX_2, ""));
        Boolean recursive = getBooleanFromString(recursiveStr);
        Boolean multiAllowed = getBooleanFromString(multiAllowedStr);
        Validation val = null;
        if (code.startsWith(Validation.getDefaultCodePrefix() + "SELECT_")) {
            if (hasValidOptions) {
                log.trace("Case 1, build Validation with OPTIONS String");
                val = new Validation(code, name, groupCodesStr, recursive, multiAllowed, optionString);
            } else {
                val = new Validation(code, name, groupCodesStr, recursive, multiAllowed);
            }
        } else {
            if (hasValidOptions) {
                log.trace("Case 2, build Validation with OPTIONS String");
                val = new Validation(code, name, regex, optionString);
            } else {
                val = new Validation(code, name, regex);
            }
        }
        val.setRealm(realmName);
        log.trace("realm:" + realmName + ",code:" + code + ",name:" + name + ",val:" + val + ", grp="
                + (groupCodesStr != null ? groupCodesStr : "X"));
        return val;
    }

    public static Attribute buildAttrribute(Map<String, String> attributes, Map<String, DataType> dataTypeMap,
                                            String realmName, String code) {
        String dataType = null;
        if (!attributes.containsKey("datatype")) {
            log.error("DataType for " + code + " cannot be null");
            throw new NotFoundException("Bad DataType given for code " + code);
        }

        dataType = attributes.get("datatype").trim().replaceAll(REGEX_1, "");
        String name = attributes.get("name").replaceAll(REGEX_1, "");
        DataType dataTypeRecord = dataTypeMap.get(dataType);
      
        ValidationList vlist = new ValidationList();
        if ((dataTypeRecord != null) && (dataTypeRecord.getValidationList()!=null)) {
        	for (Validation validation : dataTypeRecord.getValidationList()) {
        		if (validation !=null) {
        			Validation exitingValidation = Validation.findByCode(validation.code);
        			vlist.validationList.add(exitingValidation);
        		}
        	}
        	  dataTypeRecord.setValidationList(vlist.validationList);
        }
        if ((dataTypeRecord != null)) {
        	dataTypeRecord = new DataType(String.class.getCanonicalName());
        }

        String privacyStr = attributes.get(PRIVACY);
        if (privacyStr != null) {
            privacyStr = privacyStr.toUpperCase();
        }

        boolean privacy = "TRUE".equalsIgnoreCase(privacyStr);
        if (privacy) {
            log.trace("Realm:" + realmName + ", Attribute " + code + " has default privacy");
        }
        String descriptionStr = attributes.get("description");
        String helpStr = attributes.get("help");
        String placeholderStr = attributes.get("placeholder");
        String defaultValueStr = attributes.get("defaultValue".toLowerCase().replaceAll(REGEX_2, ""));
        Attribute attr = new Attribute(code, name, dataTypeRecord);
        attr.defaultPrivacyFlag = privacy;
        attr.description = descriptionStr;
        attr.help = helpStr;
        attr.placeholder = placeholderStr;
        attr.defaultValue = defaultValueStr;
        attr.realm = realmName;
        return attr;
    }

    public static AttributeLink buildAttributeLink(Map<String, String> attributeLink, Map<String, DataType> dataTypeMap, String realmName, String code) {
        String name = attributeLink.get("name").replaceAll(REGEX_1, "");
        String privacyStr = attributeLink.get(PRIVACY);
        boolean privacy = "TRUE".equalsIgnoreCase(privacyStr);

        AttributeLink linkAttribute = new AttributeLink(code, name);
        linkAttribute.defaultPrivacyFlag = privacy;
        linkAttribute.realm = realmName;

        String dataTypeStr = "dataType".toLowerCase();
        if (attributeLink.containsKey(dataTypeStr)) {
            String dataType = attributeLink.get("dataType".toLowerCase().trim().replaceAll(REGEX_2, ""))
                    .replaceAll(REGEX_1, "");
            linkAttribute.dataType = dataTypeMap.get(dataType);
        }
        return linkAttribute;
    }

    public static QuestionQuestion buildQuestionQuestion(Map<String, String> queQues,
                                                         String realmName,
                                                         Map<String, Question> questionHashMap) {

        String parentCode = queQues.get("parentCode".toLowerCase().replaceAll("^\"|\"$|_|-", ""));
        if (parentCode == null) {
            parentCode = queQues.get("sourceCode".toLowerCase().replaceAll("^\"|\"$|_|-", ""));
        }

        String targetCode = queQues.get("targetCode".toLowerCase().replaceAll("^\"|\"$|_|-", ""));

        String weightStr = queQues.get(WEIGHT);
        String mandatoryStr = queQues.get(MANDATORY);
        String readonlyStr = queQues.get(READONLY);
        Boolean readonly = "TRUE".equalsIgnoreCase(readonlyStr);
        Boolean formTrigger = queQues.get("formtrigger") != null && "TRUE".equalsIgnoreCase(queQues.get("formtrigger"));
        Boolean createOnTrigger = queQues.get("createontrigger") != null && "TRUE".equalsIgnoreCase(queQues.get("createontrigger"));
        String dependency = queQues.get("dependency");

        double weight = 0.0;
        if (isDouble(weightStr)) {
            weight = Double.parseDouble(weightStr);
        }

        Boolean mandatory = "TRUE".equalsIgnoreCase(mandatoryStr);

        Question sbe = questionHashMap.get(parentCode.toUpperCase());
        Question tbe = questionHashMap.get(targetCode.toUpperCase());
        if (sbe == null) {
//            log.error("QuestionQuesiton parent code:" + parentCode + " doesn't exist in Question table.");
            return null;
        } else if (tbe == null) {
//            log.error("QuestionQuesiton target Code:" + targetCode + " doesn't exist in Question table.");
            return null;
        }

        String oneshotStr = queQues.get("oneshot");
        Boolean oneshot = false;
        if (oneshotStr == null) {
            // Set the oneshot to be that of the targetquestion
            oneshot = tbe.getOneshot();
        } else {
            oneshot = "TRUE".equalsIgnoreCase(oneshotStr);
        }

        try {
            QuestionQuestion qq = sbe.addChildQuestion(tbe.getCode(), weight, mandatory);
            qq.setOneshot(oneshot);
            qq.setReadonly(readonly);
            qq.setCreateOnTrigger(createOnTrigger);
            qq.setFormTrigger(formTrigger);
            qq.setRealm(realmName);
            qq.setDependency(dependency);
            return qq;
        } catch (BadDataException be) {
            log.error("Should never reach here!");
        }
        return null;
    }

    public static Ask buildAsk(Map<String, String> asks, String realmName,
                               Map<String, Question> questionHashMap) {
        String sourceCode = asks.get("sourceCode".toLowerCase().replaceAll(REGEX_2, ""));
        String targetCode = asks.get("targetCode".toLowerCase().replaceAll(REGEX_2, ""));
        String qCode = asks.get("question_code".toLowerCase().replaceAll(REGEX_2, ""));
        String name = asks.get("name");
        String weightStr = asks.get(WEIGHT);
        String mandatoryStr = asks.get(MANDATORY);
        String readonlyStr = asks.get(READONLY);
        String hiddenStr = asks.get("hidden");
        final Double weight = Double.valueOf(weightStr);
        if ("QUE_USER_SELECT_ROLE".equals(targetCode)) {
            log.trace("dummy");
        }
        Boolean mandatory = "TRUE".equalsIgnoreCase(mandatoryStr);
        Boolean readonly = "TRUE".equalsIgnoreCase(readonlyStr);
        Boolean hidden = "TRUE".equalsIgnoreCase(hiddenStr);

        Question question = questionHashMap.get(qCode.toUpperCase());
        if (question == null) return null;

        Ask ask = new Ask(question, sourceCode, targetCode, mandatory, weight);
        ask.name = name;
        ask.setHidden(hidden);
        ask.setReadonly(readonly);
        ask.realm = realmName;
        return ask;
    }

    public static QBaseMSGMessageTemplate buildQBaseMSGMessageTemplate(Map<String, String> template, String realmName) {
        String code = template.get("code");
        String name = template.get("name");
        String description = template.get("description");
        String subject = template.get("subject");
        String emailTemplateDocId = template.get("email");
        if (emailTemplateDocId == null)
            emailTemplateDocId = template.get("emailtemplateid");
        String smsTemplate = template.get("sms");
        if (smsTemplate == null)
            smsTemplate = template.get("smstemplate");
        String toastTemplate = template.get("toast");
        if (toastTemplate == null)
            toastTemplate = template.get("toasttemplate");

        QBaseMSGMessageTemplate templateObj = new QBaseMSGMessageTemplate();
        templateObj.code = code;
        templateObj.name = name;
        templateObj.created = LocalDateTime.now();
        templateObj.setDescription(description);
        templateObj.setEmail_templateId(emailTemplateDocId);
        templateObj.setSms_template(smsTemplate);
        templateObj.setSubject(subject);
        templateObj.setToast_template(toastTemplate);
        templateObj.realm = realmName;
        return templateObj;
    }

    public static Question buildQuestion(Map<String, String> questions,
                                         Map<String, Attribute> attributeHashMap,
                                         String realmName) {
        String code = questions.get("code");
        String name = questions.get("name");
        String placeholder = questions.get("placeholder");
        String directions = questions.get("directions");
        String attrCode = questions.get("attribute_code".toLowerCase().replaceAll("^\"|\"$|_|-", ""));
        String html = questions.get("html");
        String oneshotStr = questions.get("oneshot");
        String readonlyStr = questions.get(READONLY);
        String mandatoryStr = questions.get(MANDATORY);
        String helper = questions.get("helper");

        Boolean oneshot = getBooleanFromString(oneshotStr);
        Boolean readonly = getBooleanFromString(readonlyStr);
        Boolean mandatory = getBooleanFromString(mandatoryStr);

        Attribute attr = attributeHashMap.get(attrCode.toUpperCase());
        if (attr == null) {
//            log.error(String.format("Question: %s can not find Attribute:%s in database!", code, attrCode.toUpperCase()));
            return null;
        }

        Question q = null;
        if (placeholder != null) {
            q = new Question(code, name, attr, placeholder);
        } else {
            q = new Question(code, name, attr);
        }
        q.setOneshot(oneshot);
        q.setHtml(html);
        q.setReadonly(readonly);
        q.setMandatory(mandatory);
        q.setRealm(realmName);
        q.setDirections(directions);
        q.setHelper(helper);
        return q;
    }


    public static String getAttributeCodeFromBaseEntityAttribute(Map<String, String> baseEntityAttr) {
        String attributeCode = null;
        String searchKey = "attributeCode".toLowerCase();
        if (baseEntityAttr.containsKey(searchKey)) {
            attributeCode = baseEntityAttr.get(searchKey).replaceAll("^\"|\"$", "");
        } else {
//            log.error("Invalid record, AttributeCode not found [" + baseEntityAttr + "]");
        }
        return attributeCode;
    }


    public static String getBaseEntityCodeFromBaseEntityAttribute(Map<String, String> baseEntityAttr,
                                                                  HashMap<String, String> userCodeUUIDMapping) {
        String baseEntityCode = null;
        String searchKey = "baseEntityCode".toLowerCase();
        if (baseEntityAttr.containsKey(searchKey)) {
            baseEntityCode = baseEntityAttr.get(searchKey).replaceAll("^\"|\"$", "");
            if (baseEntityCode.startsWith("PER_")) {
                baseEntityCode = KeycloakUtils.getKeycloakUUIDByUserCode(baseEntityCode, userCodeUUIDMapping);
            }
        } else {
//            log.error("Invalid record, BaseEntityCode not found [" + baseEntityAttr + "]");
        }
        return baseEntityCode;
    }

    public static BaseEntity buildEntityAttribute(Map<String, String> baseEntityAttr,
                                                  String realmName,
                                                  Map<String, Attribute> attrHashMap,
                                                  Map<String, BaseEntity> beHashMap,
                                                  HashMap<String, String> userCodeUUIDMapping) {
        String attributeCode = getAttributeCodeFromBaseEntityAttribute(baseEntityAttr);
        if (attributeCode == null) return null;

        List<String> asList = Collections.singletonList("valuestring");
        Optional<String> valueString = Optional.empty();
        try {
            valueString = asList.stream().map(baseEntityAttr::get).findFirst();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("NULL ERROR: " + baseEntityAttr.get("baseentitycode") + ":" + attributeCode);
        }
        Integer valueInt = null;
        Optional<String> ofNullable = Optional.ofNullable(baseEntityAttr.get(VALUEINTEGER));
        if (ofNullable.isPresent() && !baseEntityAttr.get(VALUEINTEGER).matches("\\s*")) {
            BigDecimal big = new BigDecimal(baseEntityAttr.get(VALUEINTEGER));
            Optional<String[]> nullableVal = Optional.of(big.toPlainString().split("[.]"));
            valueInt = nullableVal.filter(d -> d.length > 0).map(d -> Integer.valueOf(d[0])).get();
        }
        String valueStr = null;
        if (valueString.isPresent()) {
            valueStr = valueString.get().replaceAll(REGEX_1, "");
        }

        String baseEntityCode = getBaseEntityCodeFromBaseEntityAttribute(baseEntityAttr, userCodeUUIDMapping);
        if (baseEntityCode == null) return null;

        String weight = baseEntityAttr.get(WEIGHT);
        String privacyStr = baseEntityAttr.get(PRIVACY);
        Boolean privacy = "TRUE".equalsIgnoreCase(privacyStr);

        // Check if attribute code exist in Attribute table, foreign key restriction
        Attribute attribute = Attribute.findByCode(attributeCode.toUpperCase());
        if (attribute == null) {
//            log.error(String.format("Invalid EntityAttribute record, AttributeCode:%s is not in the Attribute Table!!!", attributeCode));
            return null;
        }

        // Check if baseEntity code exist in BaseEntity table, foreign key restriction
        BaseEntity baseEntity = BaseEntity.findByCode(baseEntityCode);
        if (baseEntity == null) {
//            log.error(String.format("Invalid EntityAttribute record, BaseEntityCode:%s is not in the BaseEntity Table!!!", baseEntityCode));
            return null;
        }

        double weightField = 0.0;
        if (isDouble(weight)) {
            weightField = Double.parseDouble(weight);
        }

        Value value = new Value(valueStr,attribute,weightField);
       
        
        EntityAttribute ea = EntityAttribute.findByBaseEntityCodeAndAttributeCode(realmName,baseEntity,attribute);
        
        if (ea == null) {
        	ea = new EntityAttribute(baseEntity, attribute,  value);
        	
        }
        ea.realm = realmName;
        ea.value = value;
        ea.setWeight(weightField);
        ea.persist();
        
        baseEntity.baseEntityAttributes.add(ea);
        
//        if (valueInt != null) {
//            try {
//                ea = baseEntity.addAttribute(attribute, weightField, valueInt);
//            } catch (BadDataException be) {
//                log.error(String.format("Should never reach here!, Error:%s", be.getMessage()));
//            }
//        } else {
//            try {
//                ea = baseEntity.addAttribute(attribute, weightField, valueStr);
//            } catch (BadDataException be) {
//                log.error(String.format("Should never reach here!, Error:%s", be.getMessage()));
//            }
//        }
//        
//        
//        if (ea != null) {
//            if (privacy || attribute.defaultPrivacyFlag) {
//                ea.privacyFlag = true;
//            }
//            ea.realm = realmName;
//        }

        baseEntity.realm = realmName;
        baseEntity.persist();
        return baseEntity;
    }

    private static String getNameFromMap(Map<String, String> baseEntitys, String defaultString) {
        String key = "name";
        String ret = defaultString;
        if (baseEntitys.containsKey(key) && baseEntitys.get(key) != null) {
            ret = baseEntitys.get(key).replaceAll("^\"|\"$", "");
        }
        return ret;
    }

    public static BaseEntity buildBaseEntity(Map<String, String> baseEntitys, String realmName) {

        String code = baseEntitys.get("code").replaceAll("^\"|\"$", "").toUpperCase().trim().replaceAll(" ", "_");
        String name = getNameFromMap(baseEntitys, code);
        BaseEntity be = new BaseEntity(code, name);
        be.realm = realmName;
        return be;
    }
}
