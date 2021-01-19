package life.genny.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import life.genny.bootxport.bootx.*;
import life.genny.bootxport.xlsimport.BatchLoading;
import life.genny.qwanda.Answer;
import life.genny.models.attribute.Attribute;
import life.genny.models.attribute.EntityAttribute;
import life.genny.models.datatype.DataType;
import life.genny.models.entity.BaseEntity;
import life.genny.qwandautils.GennySettings;
import life.genny.qwandautils.JsonUtils;
import life.genny.security.SecureResources;
import org.apache.logging.log4j.Logger;


public class ImportSheetService {
    private final Logger log = org.apache.logging.log4j.LogManager.getLogger(ImportSheetService.class);

    private Realm rx;
    private Service service;
    private ServiceTokenService serviceTokens;


    public Realm getRealm() {
        XlsxImport xlsImport;
        XSSFService service = new XSSFService();
        GoogleImportService gs = GoogleImportService.getInstance();
        if (rx == null) {
            Boolean onlineMode = Optional.ofNullable(System.getenv("ONLINE_MODE"))
                    .map(val -> val.toLowerCase())
                    .map(Boolean::getBoolean)
                    .orElse(true);

            if (onlineMode) {
                xlsImport = new XlsxImportOnline(gs.getService());
            } else {
                xlsImport = new XlsxImportOffline(service);
            }
            rx = new Realm(xlsImport,
                    System.getenv("GOOGLE_HOSTING_SHEET_ID"));
        }
        return rx;
    }

    private BaseEntity createAnswer(BaseEntity be, final String attributeCode, final String answerValue,
                                    final Boolean privacy) {
        try {
            Answer answer = null;
            Attribute attribute = null;
            try {
                attribute = service.findAttributeByCode(attributeCode);
            } catch (Exception ee) {
                // Could not find Attribute, create it
                DataType dataType = new DataType("DTT_TEXT");
                dataType.setDttCode("DTT_TEXT");
                attribute = new Attribute(attributeCode, attributeCode, dataType);
                service.insert(attribute);
            }
            if (attribute == null) {
                DataType dataType = new DataType("DTT_TEXT");
                dataType.setDttCode("DTT_TEXT");
                attribute = new Attribute(attributeCode, attributeCode, dataType);
                service.insert(attribute);
            }
            answer = new Answer(be, be, attribute, answerValue);
            answer.setChangeEvent(false);
            be.addAnswer(answer);
            EntityAttribute ea = be.findEntityAttribute(attribute);
            ea.privacyFlag = privacy;
            ea.realm = be.realm;
        } catch (Exception e) {
            log.error("CANNOT UPDATE PROJECT " + be.getCode() + " " + e.getLocalizedMessage());
        }
        return be;
    }


    private void saveServiceBes(RealmUnit realmUnit) {
        log.info("Updating Service BaseEntitys ");

        if ("FALSE".equals((String) realmUnit.getDisable().toString().toUpperCase())) {

            String realmCode = realmUnit.getCode();
            service.setCurrentRealm(realmCode);
            log.info("Service: " + realmCode);

            if ("FALSE".equals((String) realmUnit.getDisable().toString().toUpperCase())) {
                String realm = realmCode;
                String name = realmUnit.getName() + " Service User";
                String realmToken = serviceTokens.getServiceToken(realm);

                String serviceCode = "PER_SERVICE";
                BaseEntity serviceBe = null;

                serviceBe = new BaseEntity(serviceCode, name);
                serviceBe = service.upsert(serviceBe);

                serviceBe = createAnswer(serviceBe, "PRI_NAME", name, false);
                serviceBe = createAnswer(serviceBe, "PRI_CODE", serviceCode, false);
                serviceBe = createAnswer(serviceBe, "ENV_SERVICE_TOKEN", realmToken, true);
                service.upsert(serviceBe);
            }
        }
    }


    public void persistEnabledProject(RealmUnit realmUnit) {
        if (!realmUnit.getDisable()) {
            log.info("Project: " + realmUnit.getCode());

            Boolean skipGoogleDoc = realmUnit.getSkipGoogleDoc();

            if ((skipGoogleDoc != null)
                    && !realmUnit.getDisable()) {
                String realm = realmUnit.getCode();
                service.setCurrentRealm(realm);
                log.info("PROJECT " + realm);
                BatchLoading bl = new BatchLoading(service);

                // save urls to Keycloak maps
                service.setCurrentRealm(realmUnit.getCode()); // provide overridden realm

                bl.persistProject(realmUnit);
                String keycloakJson = bl.constructKeycloakJson(realmUnit);
                bl.upsertKeycloakJson(keycloakJson);
                bl.upsertProjectUrls((String) realmUnit.getUrlList());
            }
        }
    }

    private void saveProjectBes(RealmUnit realmUnit) {
        log.info("Updating Project BaseEntitys ");
        if ("FALSE".equals(realmUnit.getDisable().toString().toUpperCase())) {

            String realmCode = realmUnit.getCode();
            service.setCurrentRealm(realmCode);
            log.info("Project: " + realmCode);

            if ("FALSE".equals(realmUnit.getDisable().toString().toUpperCase())) {
                String realm = realmCode;
                String keycloakUrl = realmUnit.getKeycloakUrl();
                String name = realmUnit.getName();
                String sheetID = realmUnit.getUri();
                String urlList = realmUnit.getUrlList();
                String code = realmUnit.getCode();
                String disable = realmUnit.getDisable().toString().toUpperCase();
                String secret = realmUnit.getClientSecret();
                String key = realmUnit.getSecurityKey();
                String encryptedPassword = realmUnit.getServicePassword();
                String realmToken = serviceTokens.getServiceToken(realm);
                String skipGoogleDoc = realmUnit.getSkipGoogleDoc().toString().toUpperCase();
                String projectCode = "PRJ_" + realm.toUpperCase();
                BaseEntity projectBe = null;

                try {
                    projectBe = service.findBaseEntityByCode(projectCode, realm);
                } catch (javax.persistence.NoResultException e) {
                    projectBe = new BaseEntity(projectCode, name);
                    projectBe = service.upsert(projectBe);
                }

                projectBe = createAnswer(projectBe, "PRI_NAME", name, false);
                projectBe = createAnswer(projectBe, "PRI_CODE", projectCode, false);
                projectBe = createAnswer(projectBe, "ENV_SECURITY_KEY", key, true);
                projectBe = createAnswer(projectBe, "ENV_SERVICE_PASSWORD", encryptedPassword, true);
                projectBe = createAnswer(projectBe, "ENV_SERVICE_TOKEN", realmToken, true);
                projectBe = createAnswer(projectBe, "ENV_SECRET", secret, true);
                projectBe = createAnswer(projectBe, "ENV_SHEET_ID", sheetID, true);
                projectBe = createAnswer(projectBe, "ENV_URL_LIST", urlList, true);
                projectBe = createAnswer(projectBe, "ENV_DISABLE", disable, true);
                projectBe = createAnswer(projectBe, "ENV_REALM", realm, true);
                projectBe = createAnswer(projectBe, "ENV_KEYCLOAK_URL", keycloakUrl, true);
                projectBe = createAnswer(projectBe, "ENV_KEYCLOAK_REDIRECTURI", keycloakUrl, true);
                BatchLoading bl = new BatchLoading(service);
                String keycloakJson = bl.constructKeycloakJson(realmUnit);
                projectBe = createAnswer(projectBe, "ENV_KEYCLOAK_JSON", keycloakJson, true);
                service.upsert(projectBe);

                // Set up temp keycloak.json Maps
                String[] urls = urlList.split(",");
                SecureResources.addRealm(realm, keycloakJson);
                SecureResources.addRealm(realm + ".json", keycloakJson);
                // redundant
                if (("genny".equals(realm))) {
                    SecureResources.addRealm("genny", keycloakJson);
                    SecureResources.addRealm("genny.json", keycloakJson);
                    SecureResources.addRealm("qwanda-service.genny.life.json", keycloakJson);
                }

                // Overwrite all the time, must have localhost
                SecureResources.addRealm("localhost.json", keycloakJson);
                SecureResources.addRealm("localhost", keycloakJson);
                SecureResources.addRealm("localhost:8080", keycloakJson);
                for (String url : urls) {
                    // Remove space in url
                    url = url.replaceAll("\\s", "");
                    SecureResources.addRealm(url + ".json", keycloakJson);
                    SecureResources.addRealm(url, keycloakJson);
                }

                //	projectBe = service.findBaseEntityByCode(projectBe.getCode());
                // Save project BE in a consistent place
//                VertxUtils.putObject(realm, "", "PROJECT", JsonUtils.toJson(projectBe),
//                        serviceTokens.getServiceToken(realm));

            }
        }
    }

    public void doBatchloading() {
        rx = getRealm();
        List<String> realms = rx.getDataUnits().stream()
                .filter(r -> !r.getDisable())
                .map(d -> d.getCode())
                .collect(Collectors.toList());

        StateManagement.initStateManagement(rx);

        // TODO
        rx.getDataUnits().forEach(serviceTokens::init);

        // Save projects
        // TODO
        rx.getDataUnits().forEach(this::saveProjectBes);
        rx.getDataUnits().forEach(this::saveServiceBes);

//        rx.getDataUnits().forEach(this::pushProjectsUrlsToDTT);

        if (!GennySettings.skipGoogleDocInStartup) {
            log.info("Starting Transaction for loading *********************");

            // TODO
            rx.getDataUnits().forEach(this::persistEnabledProject);
            log.info("*********************** Finished Google Doc Import ***********************************");
        } else {
            log.info("Skipping Google doc loading");
        }
    }
}
