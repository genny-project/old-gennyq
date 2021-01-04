package life.genny.qwandautils;

public class GennySettings {
    public final static Boolean skipGoogleDocInStartup = "TRUE".equalsIgnoreCase(System.getenv("SKIP_GOOGLE_DOC_IN_STARTUP"));
}
