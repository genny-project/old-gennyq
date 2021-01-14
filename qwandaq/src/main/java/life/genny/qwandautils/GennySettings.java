package life.genny.qwandautils;

import org.apache.commons.lang3.StringUtils;

public class GennySettings {
    public final static Boolean skipGoogleDocInStartup = "TRUE".equalsIgnoreCase(System.getenv("SKIP_GOOGLE_DOC_IN_STARTUP"));
    public final static String mainrealm = System.getenv("PROJECT_REALM") != null ? System.getenv("PROJECT_REALM") : "genny"; // UGLY
    public static final Boolean devMode = ("TRUE".equalsIgnoreCase(System.getenv("DEV_MODE")) || "TRUE".equalsIgnoreCase(System.getenv("GENNYDEV"))) ? true : false;
    public static String hostIP = System.getenv("HOSTIP") != null ? System.getenv("HOSTIP") : System.getenv("MYIP");   // remember to set up this local IP on the host
    public static final String realmDir = System.getenv("REALM_DIR") != null ? System.getenv("REALM_DIR") : "./realm";
    public static final String defaultServiceKey = System.getenv("ENV_SECURITY_KEY") == null ? "WubbaLubbaDubDub" : System.getenv("ENV_SECURITY_KEY");
    public static final String defaultServiceEncryptedPassword = System.getenv("ENV_SERVICE_PASSWORD") == null ? "vRO+tCumKcZ9XbPWDcAXpU7tcSltpNpktHcgzRkxj8o=" : System.getenv("ENV_SERVICE_PASSWORD");
    public static int ACCESS_TOKEN_EXPIRY_LIMIT_SECONDS = 60;
    public final static String GENNY_REALM = "jenny"; //deliberatly not genny

    public static String dynamicKey(final String realm) {
        String envSecurityKey = System.getenv("ENV_SECURITY_KEY" + "_" + realm.toUpperCase());
        if (envSecurityKey == null) {
            return defaultServiceKey;
        } else {
            return envSecurityKey;
        }
    }

    public static String dynamicInitVector(final String realm) {
        String initVector = "PRJ_" + realm.toUpperCase();
        initVector = StringUtils.rightPad(initVector, 16, '*');
        return initVector;
    }

    public static String dynamicEncryptedPassword(final String realm) {
        String envServiceEncryptedPassword = System.getenv("ENV_SERVICE_PASSWORD" + "_" + realm.toUpperCase());
        if (envServiceEncryptedPassword == null) {
            return defaultServiceEncryptedPassword;
        } else {
            return envServiceEncryptedPassword;
        }
    }

}
