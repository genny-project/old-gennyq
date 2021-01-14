package life.genny.utils;

import io.vertx.core.json.JsonObject;
import life.genny.models.GennyToken;
import life.genny.qwandautils.GennySettings;
import life.genny.qwandautils.JsonUtils;
import life.genny.qwandautils.QwandaUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VertxUtils {

    protected static final Logger log = org.apache.logging.log4j.LogManager
            .getLogger(MethodHandles.lookup().lookupClass().getCanonicalName());

    static public boolean cachedEnabled = false;

    static public EventBusInterface eb;

    static final String DEFAULT_TOKEN = "DUMMY";
    static final String[] DEFAULT_FILTER_ARRAY = {"PRI_FIRSTNAME", "PRI_LASTNAME", "PRI_MOBILE", "PRI_IMAGE_URL",
            "PRI_CODE", "PRI_NAME", "PRI_USERNAME"};


    public static GennyCacheInterface cacheInterface = null;

    public static GennyCacheInterface getCacheInterface() {
        return cacheInterface;
    }

    public static void init(EventBusInterface eventBusInterface, GennyCacheInterface gennyCacheInterface) {
        if (gennyCacheInterface == null) {
            log.error("NULL CACHEINTERFACE SUPPLUED IN INIT");
        }
        eb = eventBusInterface;
        cacheInterface = gennyCacheInterface;
        if (eb instanceof EventBusMock) {
            GennySettings.forceCacheApi = true;
            GennySettings.forceEventBusApi = true;
        }

    }

    static Map<String, String> localCache = new ConcurrentHashMap<>();


    static public <T> T getObject(final String realm, final String keyPrefix, final String key, final Class clazz,
                                  final String token) {
        T item = null;
        String prekey = (StringUtils.isBlank(keyPrefix)) ? "" : (keyPrefix + ":");
        JsonObject json = readCachedJson(realm, prekey + key, token);
        if (json.getString("status").equalsIgnoreCase("ok")) {
            String data = json.getString("value");
            try {
                item = (T) JsonUtils.fromJson(data, clazz);
            } catch (Exception e) {
                log.error("Bad JsonUtils " + realm + ":" + key + ":" + clazz.getTypeName());
            }
            return item;
        } else {
            return null;
        }

    }

    static public <T> T getObject(final String realm, final String keyPrefix, final String key, final Type clazz,
                                  final String token) {
        T item = null;
        String prekey = (StringUtils.isBlank(keyPrefix)) ? "" : (keyPrefix + ":");
        JsonObject json = readCachedJson(realm, prekey + key, token);
        if (json.getString("status").equalsIgnoreCase("ok")) {
            String data = json.getString("value");
            try {
                item = (T) JsonUtils.fromJson(data, clazz);
            } catch (Exception e) {
                log.info("Bad JsonUtils " + realm + ":" + key + ":" + clazz.getTypeName());
            }
            return item;
        } else {
            return null;
        }
    }

    static public void putObject(final String realm, final String keyPrefix, final String key, final Object obj) {
        putObject(realm, keyPrefix, key, obj, DEFAULT_TOKEN);
    }

    static public void putObject(final String realm, final String keyPrefix, final String key, final Object obj,
                                 final String token) {
        String data = JsonUtils.toJson(obj);
        String prekey = (StringUtils.isBlank(keyPrefix)) ? "" : (keyPrefix + ":");

        writeCachedJson(realm, prekey + key, data, token);
    }

    static public JsonObject readCachedJson(final String realm, final String key) {
        return readCachedJson(realm, key, DEFAULT_TOKEN);
    }

    static public JsonObject readCachedJson(String realm, final String key, final String token) {
        JsonObject result = null;

        if (!GennySettings.forceCacheApi) {
            String ret = null;
            try {
                // log.info("VERTX READING DIRECTLY FROM CACHE! USING
                // "+(GennySettings.isCacheServer?" LOCAL DDT":"CLIENT "));
                if (key == null) {
                    log.error("Cache is  null");

                    return null;
                }
                ret = (String) cacheInterface.readCache(realm, key, token);
            } catch (Exception e) {
                log.error("Cache is  null");
                e.printStackTrace();
            }
            if (ret != null) {
                result = new JsonObject().put("status", "ok").put("value", ret);
            } else {
                result = new JsonObject().put("status", "error").put("value", ret);
            }
        } else {
            String resultStr = null;
            try {
                //	log.info("VERTX READING FROM CACHE API!");
                if (cachedEnabled) {
                    if ("DUMMY".equals(token)) {
                        // leave realm as it
                    } else {
                        GennyToken temp = new GennyToken(token);
                        realm = temp.getRealm();
                    }

                    resultStr = (String) localCache.get(realm + ":" + key);
                    if ((resultStr != null) && (!"\"null\"".equals(resultStr))) {
                        String resultStr6 = null;
                        if (false) {
                            // ugly way to fix json
                            resultStr6 = VertxUtils.fixJson(resultStr);
                        } else {
                            resultStr6 = resultStr;
                        }
                        JsonObject resultJson = new JsonObject().put("status", "ok").put("value", resultStr6);
                        resultStr = resultJson.toString();
                    } else {
                        resultStr = null;
                    }

                } else {
                    log.debug(" DDT URL:" + GennySettings.ddtUrl + ", realm:" + realm + "key:" + key + "token:" + token);
                    resultStr = QwandaUtils.apiGet(GennySettings.ddtUrl + "/read/" + realm + "/" + key, token);
                    if (("<html><head><title>Error</title></head><body>Not Found</body></html>".equals(resultStr)) || ("<html><body><h1>Resource not found</h1></body></html>".equals(resultStr))) {
                        resultStr = QwandaUtils.apiGet(GennySettings.ddtUrl + "/service/cache/read/" + key, token);
                    }
                }
                if (resultStr != null) {
                    try {
                        result = new JsonObject(resultStr);
                    } catch (Exception e) {
                        log.error("JsonDecode Error " + resultStr);
                    }
                } else {
                    result = new JsonObject().put("status", "error");
                }

            } catch (IOException e) {
                log.error("Could not read " + key + " from cache");
            }

        }

        return result;
    }

    static public JsonObject writeCachedJson(final String realm, final String key, final String value) {
        return writeCachedJson(realm, key, value, DEFAULT_TOKEN);
    }

    static public JsonObject writeCachedJson(final String realm, final String key, final String value,
                                             final String token) {
        return writeCachedJson(realm, key, value, token, 0L);
    }

    static public JsonObject writeCachedJson(String realm, final String key, String value, final String token,
                                             long ttl_seconds) {
        if (!GennySettings.forceCacheApi) {
            cacheInterface.writeCache(realm, key, value, token, ttl_seconds);
        } else {
            try {
                if (cachedEnabled) {
                    // force
                    if ("DUMMY".equals(token)) {

                    } else {
                        GennyToken temp = new GennyToken(token);
                        realm = temp.getRealm();
                    }
                    if (value == null) {
                        localCache.remove(realm + ":" + key);
                    } else {
                        localCache.put(realm + ":" + key, value);
                    }
                } else {

                    log.debug("WRITING TO CACHE USING API! " + key);
                    JsonObject json = new JsonObject();
                    json.put("key", key);
                    json.put("json", value);
                    json.put("ttl", ttl_seconds + "");
                    QwandaUtils.apiPostEntity(GennySettings.ddtUrl + "/write", json.toString(), token);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        JsonObject ok = new JsonObject().put("status", "ok");
        return ok;

    }


    static public String fixJson(String resultStr) {
        String resultStr2 = resultStr.replaceAll(Pattern.quote("\\\""),
                Matcher.quoteReplacement("\""));
        String resultStr3 = resultStr2.replaceAll(Pattern.quote("\\n"),
                Matcher.quoteReplacement("\n"));
        String resultStr4 = resultStr3.replaceAll(Pattern.quote("\\\n"),
                Matcher.quoteReplacement("\n"));
//		String resultStr5 = resultStr4.replaceAll(Pattern.quote("\"{"),
//				Matcher.quoteReplacement("{"));
//		String resultStr6 = resultStr5.replaceAll(Pattern.quote("\"["),
//				Matcher.quoteReplacement("["));
//		String resultStr7 = resultStr6.replaceAll(Pattern.quote("]\""),
//				Matcher.quoteReplacement("]"));
//		String resultStr8 = resultStr5.replaceAll(Pattern.quote("}\""), Matcher.quoteReplacement("}"));
        String ret = resultStr4.replaceAll(Pattern.quote("\\\""
                        + ""),
                Matcher.quoteReplacement("\""));
        return ret;

    }


}
