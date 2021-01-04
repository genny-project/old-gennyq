package life.genny.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import life.genny.bootxport.bootx.*;
import life.genny.qwandautils.GennySettings;
import org.apache.logging.log4j.Logger;


public class ImportSheetService {
    private final Logger log = org.apache.logging.log4j.LogManager.getLogger(ImportSheetService.class);

    private Realm rx;

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

    public void doBatchloading() {
        rx = getRealm();
        List<String> realms = rx.getDataUnits().stream()
                .filter(r -> !r.getDisable())
                .map(d -> d.getCode())
                .collect(Collectors.toList());

        StateManagement.initStateManagement(rx);
        // TODO
//        rx.getDataUnits().forEach(serviceTokens::init);
        // Save projects
        // TODO
//        rx.getDataUnits().forEach(this::saveProjectBes);
//        rx.getDataUnits().forEach(this::saveServiceBes);
//        rx.getDataUnits().forEach(this::pushProjectsUrlsToDTT);

        if (!GennySettings.skipGoogleDocInStartup) {
            log.info("Starting Transaction for loading *********************");

            // TODO
//            rx.getDataUnits().forEach(this::persistEnabledProject);
            log.info("*********************** Finished Google Doc Import ***********************************");
        } else {
            log.info("Skipping Google doc loading");
        }
    }
}
