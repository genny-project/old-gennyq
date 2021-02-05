package life.genny.services;

import java.lang.invoke.MethodHandles;
import javax.inject.Inject;

import life.genny.qwandautils.KeycloakUtils;
import life.genny.utils.SecurityUtils;
import life.genny.security.SecureResources;
import org.apache.logging.log4j.Logger;
import io.vertx.core.json.JsonObject;
import life.genny.qwandautils.GennySettings;

import javax.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.TimeZone;

import java.util.HashMap;

import life.genny.bootxport.bootx.RealmUnit;


@ApplicationScoped

public class ServiceTokenService {

    protected static final Logger log = org.apache.logging.log4j.LogManager
            .getLogger(MethodHandles.lookup().lookupClass().getCanonicalName());

    private HashMap<String, String> serviceTokens = new HashMap<>();
    private HashMap<String, String> refreshServiceTokens = new HashMap<>();

    @Inject
    SecureResources secureResources;

    public void init(RealmUnit multitenancy) {
        log.info("Initialising Service Tokens ");

        log.info("Project: " + multitenancy.getName());

        if (!multitenancy.getDisable()) {
            String realm = multitenancy.getCode();
            String keycloakUrl = multitenancy.getKeycloakUrl();
            String secret = multitenancy.getClientSecret();
            String key = multitenancy.getSecurityKey();
            String encryptedPassword = multitenancy.getServicePassword();
            String realmToken = generateServiceToken(realm, keycloakUrl, secret, key, encryptedPassword);
            serviceTokens.put(multitenancy.getCode(), realmToken);
        }
    }


    public String getServiceToken(String realm) {
        /* we get the service token currently stored in the cache */

        if (GennySettings.devMode) {
            realm = "genny";
        } else {
            if ("genny".equals(realm)) {
                realm = GennySettings.mainrealm;
            }
        }

        String serviceToken = serviceTokens.get(realm);

        /* if we have got a service token cached */
        if (serviceToken != null) {

            /* we decode it */
            JsonObject decodedServiceToken = KeycloakUtils.getDecodedToken(serviceToken);

            /* we get the expiry timestamp */
            long expiryTime = decodedServiceToken.getLong("exp");

            /* we get the current time */
            long nowTime = LocalDateTime.now().atZone(TimeZone.getDefault().toZoneId()).toEpochSecond();

            /* we calculate the differencr */
            long duration = expiryTime - nowTime;

            /*
             * if the difference is negative it means the expiry time is less than the
             * nowTime if the difference < ACCESS_TOKEN_EXPIRY_LIMIT_SECONDS, it means the
             * token will expire in 3 hours
             */
            if (duration >= GennySettings.ACCESS_TOKEN_EXPIRY_LIMIT_SECONDS) {

                // log.info("======= USING CACHED ACCESS TOKEN ========");

                /* if the token is NOTn about to expire (> 3 hours), we reuse it */
                return serviceToken;
            }
        }

        return generateServiceToken(realm);
    }

    public String generateServiceToken(String realm) {

        log.info("Generating Service Token for " + realm);

        String jsonFile = realm + ".json";

        if (SecureResources.getKeycloakJsonMap().isEmpty()) {
            secureResources.init(null);
        }
        String keycloakJson = SecureResources.getKeycloakJsonMap().get(jsonFile);
        if (keycloakJson == null) {
            log.info("No keycloakMap for " + realm + " ... fixing");
            String gennyKeycloakJson = SecureResources.getKeycloakJsonMap().get("genny");
            if (GennySettings.devMode) {
                SecureResources.getKeycloakJsonMap().put(jsonFile, gennyKeycloakJson);
                keycloakJson = gennyKeycloakJson;
            } else {
                log.info("Error - No keycloak Json file available for realm - " + realm);
                return null;
            }
        }
        JsonObject realmJson = new JsonObject(keycloakJson);
        JsonObject secretJson = realmJson.getJsonObject("credentials");
        String secret = secretJson.getString("secret");
        String jsonRealm = realmJson.getString("realm");

        // Now ask the bridge for the keycloak to use
        String keycloakUrl = realmJson.getString("auth-server-url").substring(0,
                realmJson.getString("auth-server-url").length() - "/auth".length());

        String key = GennySettings.dynamicKey(jsonRealm);
        String initVector = GennySettings.dynamicInitVector(jsonRealm);
        String encryptedPassword = GennySettings.dynamicEncryptedPassword(jsonRealm);
        String password = null;

        return generateServiceToken(realm, keycloakUrl, secret, key, encryptedPassword);

    }

    public String generateServiceToken(final String realm, final String keycloakUrl, final String secret,
                                       final String key, final String encryptedPassword) {

        log.info("Generating Service Token for " + realm);

        String jsonFile = realm + ".json";

        String initVector = GennySettings.dynamicInitVector(realm);
        String password = null;

        log.info("key:" + key + ":" + initVector + ":" + encryptedPassword);
        password = SecurityUtils.decrypt(key, initVector, encryptedPassword);

        log.info("password=" + password);

        try {
            log.info("realm()! : " + realm + "\n" + "realm! : " + realm + "\n" + "secret : " + secret + "\n"
                    + "keycloakurl: " + keycloakUrl + "\n" + "key : " + key + "\n" + "initVector : " + initVector + "\n"
                    + "enc pw : " + encryptedPassword + "\n" + "password : " + password + "\n");

            /* we get the refresh token from the cache */
            String cached_refresh_token = null;
            if (refreshServiceTokens.containsKey(realm)) {
                cached_refresh_token = refreshServiceTokens.get(realm);
            }

            /*
             * we get a secure token payload containing a refresh token and an access token
             */
            JsonObject secureTokenPayload = KeycloakUtils.getSecureTokenPayload(keycloakUrl, realm, realm, secret,
                    "service", password, cached_refresh_token);

            /* we get the access token and the refresh token */
            String access_token = secureTokenPayload.getString("access_token");
            String refresh_token = secureTokenPayload.getString("refresh_token");

            /* if we have an access token */
            if (access_token != null) {

                serviceTokens.put(realm, access_token);
                refreshServiceTokens.put(realm, refresh_token);
                return access_token;
            }

        } catch (Exception e) {
            log.info(e);
        }

        return null;
    }
}
