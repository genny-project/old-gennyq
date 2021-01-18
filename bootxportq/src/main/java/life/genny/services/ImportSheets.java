package life.genny.services;

import java.util.Optional;

import life.genny.bootxport.bootx.*;
import life.genny.bootxport.xlsimport.BatchLoading;
import life.genny.qwandautils.GennySettings;
import org.apache.logging.log4j.Logger;


public class ImportSheets {
    private final Logger log = org.apache.logging.log4j.LogManager.getLogger(ImportSheets.class);

    public ImportSheets() {
    }

    public QwandaRepository getQwandaRepository(String realm) {
        return new QwandaRepository(realm);
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
        Boolean skipGoogleDoc = realmUnit.getSkipGoogleDoc();
        if (realmUnit.getDisable() || skipGoogleDoc) {
            log.warn("PROJECT:" + realmCode + "disabled or skip google doc.");
            return;
        }

        log.info("PROJECT " + realmCode);
        QwandaRepository qwandaRepository = getQwandaRepository(realmCode);
        BatchLoading bl = new BatchLoading(qwandaRepository);
        bl.persistProject(realmUnit);
        String keycloakJson = bl.constructKeycloakJson(realmUnit);
        bl.upsertKeycloakJson(keycloakJson);
        bl.upsertProjectUrls(realmUnit.getUrlList());
    }

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

    public static void main(String... args) {
        ImportSheets importSheetService = new ImportSheets();
        importSheetService.doBatchLoading();
    }
}
