package life.genny.bootxport.bootx;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

public class GoogleImportService {
	
	@ConfigProperty(name = "google.credentials.path", defaultValue = "/root/.genny/token-secret-service-account.json")
	String googleCredentialsPath;


    protected static final Logger log = org.apache.logging.log4j.LogManager.getLogger(MethodHandles.lookup().lookupClass().getCanonicalName());
    private final JsonFactory jacksonFactory = JacksonFactory.getDefaultInstance();

    private HttpTransport httpTransport;

    private final List<String> scopes = Collections.singletonList(SheetsScopes.SPREADSHEETS);

    private Sheets service;

    public Sheets getService() {
    	if (service == null) {
    		try {
				service = getSheetsService();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
        return service;
    }

    private static GoogleImportService instance = null;

    public static GoogleImportService getInstance() {
        synchronized (GoogleImportService.class) {
            if (instance == null) {
                instance = new GoogleImportService();
            }
        }
        return instance;
    }

    private GoogleImportService() {
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            service = getSheetsService();
        } catch (final IOException e) {
            log.error(e.getMessage());
        } catch (final Exception ex) {
            log.error(ex.getMessage());
            System.exit(1);
        }
    }


    public Sheets getSheetsService() throws IOException {
        final Credential credential = authorize();
        return new Sheets.Builder(httpTransport, jacksonFactory,
                credential).build();
    }

    public Credential authorize() throws IOException {
    	String path =  ConfigProvider.getConfig().getValue("google.credentials.path", String.class);
     //   Optional<String> path = Optional.ofNullable(googleCredentialsPath);
      //  if (!path.isPresent()) {
    	if (path == null) {
            throw new FileNotFoundException("GOOGLE_SVC_ACC_PATH not set");
        }

        GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream(path),
                httpTransport, jacksonFactory).createScoped(scopes);

        if (credential != null) {
            String msg = String.format("Spreadsheets being read with user id: %s.", credential.getServiceAccountId());
            log.info(msg);
            log.info(credential.getTokenServerEncodedUrl());
        }
        return credential;
    }

}
