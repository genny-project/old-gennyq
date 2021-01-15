package life.genny.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.vertx.core.json.JsonObject;
import life.genny.bootxport.bootx.*;
import life.genny.bootxport.xlsimport.BatchLoading;
import life.genny.qwanda.Answer;
import life.genny.models.attribute.Attribute;
import life.genny.models.attribute.EntityAttribute;
import life.genny.models.datatype.DataType;
import life.genny.models.entity.BaseEntity;
import life.genny.qwandautils.GennySettings;
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
                serviceBe = service.upsert(serviceBe);
            }
        }
    }

    private boolean checkWriteCache(JsonObject jsonOb, String jsonString) {
        if ((jsonOb == null) || ("error".equals(jsonOb.getString("status")))) {
            return false;
        } else {
            return jsonOb.getString("value").equals(jsonString);
        }
    }

/*
    private void pushProjectsUrlsToDTT(RealmUnit realmUnit) {
        String realm = realmUnit.getCode();

        if ((realmUnit != null) && ("FALSE".equals((String) realmUnit.getDisable().toString().toUpperCase()))) {

            // push the project to the urls as keys too
            service.setCurrentRealm(realm);

            BaseEntity be = null; // service.findBaseEntityByCode("PRJ_" + realm.toUpperCase(), true);
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<BaseEntity> query = cb.createQuery(BaseEntity.class);
            Root<BaseEntity> root = query.from(BaseEntity.class);

            query = query.select(root).where(cb.equal(root.get("code"), "PRJ_" + realm.toUpperCase()),
                    cb.equal(root.get("realm"), realm));

            try {
                be = em.createQuery(query).getSingleResult();
            } catch (NoResultException nre) {
                log.error("NoResultException occurred for baseentity code:" + "PRJ_" + realm.toUpperCase()
                        + " realm:" + realm);
            }

            String urlList = be.getValue("ENV_URL_LIST", "alyson3.genny.life");
            String token = serviceTokens.getServiceToken(realm); // be.getValue("ENV_SERVICE_TOKEN", "DUMMY");

            // log.info(be.getRealm() + ":" + be.getCode() + ":token=" + token);
            VertxUtils.writeCachedJson(GennySettings.GENNY_REALM, "TOKEN" + realm.toUpperCase(), token);
            VertxUtils.putObject(realm, "CACHE", "SERVICE_TOKEN", token);
            String[] urls = urlList.split(",");
            log.info(String.format("DEBUG, Realm: %s has %d urls, they are:%s", realm, urls.length, Arrays.toString(urls)));
            for (String url : urls) {
                try {
                    if (!((url.startsWith("http:")) || (url.startsWith("https:")))) {
                        url = "http://" + url.replaceAll("\\s", ""); // hack
                    }
                    final String cleanUrl = new URL(url).getHost();
                    log.info("Writing to Cache: " + GennySettings.GENNY_REALM + ":" + cleanUrl.toUpperCase());
                    String keyString = cleanUrl.toUpperCase();
                    String gennyRealm = GennySettings.GENNY_REALM;
                    VertxUtils.writeCachedJson(gennyRealm, keyString, JsonUtils.toJson(be));
                    JsonObject jsonOb = VertxUtils.readCachedJson(gennyRealm, keyString);
                    if (!checkWriteCache(jsonOb, JsonUtils.toJson(be))) {
                        log.error(String.format("Realm:%s, Key:%s not cached properly!",
                                GennySettings.GENNY_REALM, cleanUrl.toUpperCase()));
                    }

                    keyString = "TOKEN" + cleanUrl.toUpperCase();
                    VertxUtils.writeCachedJson(gennyRealm, keyString, token);
                    jsonOb = VertxUtils.readCachedJson(gennyRealm, keyString);
                    if (!checkWriteCache(jsonOb, token)) {
                        log.error(String.format("Realm:%s, Key:%s not cached properly!",
                                GennySettings.GENNY_REALM, cleanUrl.toUpperCase()));
                    }
                } catch (MalformedURLException e) {
                    log.error("Bad URL for realm " + be.realm + "=" + url);
                }
            }

            be.findEntityAttribute("ENV_GOOGLE_MAPS_APIKEY").ifPresent(googleMapKey -> {
                try (PrintWriter writer = new PrintWriter(GOOGLE_MAP_KEY_FILE_PATH, "UTF-8");) {
                    writer.println(googleMapKey.getValueString());
                } catch (FileNotFoundException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            });

            be.findEntityAttribute("ENV_ABN_API_KEY").ifPresent(abnKey -> {
                try (PrintWriter writer = new PrintWriter(ABN_KEY_FILE_PATH, "UTF-8");) {
                    writer.println(abnKey.getValueString());
                } catch (FileNotFoundException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            });
        }
    }
    */


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
//        rx.getDataUnits().forEach(this::saveProjectBes);
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
