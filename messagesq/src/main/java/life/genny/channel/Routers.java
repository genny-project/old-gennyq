package life.genny.channel;

import java.lang.invoke.MethodHandles;
import org.apache.logging.log4j.Logger;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import life.genny.qwandautils.GennySettings;

public class Routers {
	  protected static final Logger log = org.apache.logging.log4j.LogManager
		      .getLogger(MethodHandles.lookup().lookupClass().getCanonicalName());


	static private Router router = null;

	public static Router routers(final Vertx vertx) {
		if (router == null) {
			router = Router.router(vertx);
		}

		RouterHandlers.avertx = vertx;
		
		router.route().handler(RouterHandlers.cors());
		router.route(HttpMethod.POST, "/write").handler(RouterHandlers::apiMapPutHandler);
		router.route(HttpMethod.POST, "/writearray").handler(RouterHandlers::apiMapPutHandlerArray);
		router.route(HttpMethod.GET, "/read/:key").handler(RouterHandlers::apiMapGetHandler);
		router.route(HttpMethod.GET, "/read/:realm/:key").handler(RouterHandlers::apiMapGetHandlerRealm);
		router.route(HttpMethod.GET, "/version").handler(VersionHandler::apiGetVersionHandler);
		router.route(HttpMethod.GET, "/clear").handler(RouterHandlers::apiClearGetHandler);
		return router;
	}
	
	public static Router getRouter(final Vertx vertx)
	{
		if (router == null) {
			routers(vertx);
		}
		return router;
	}

	public static void activate(final Vertx vertx) {
		log.info("Activating cache Routes on port "+GennySettings.cacheApiPort+" given ["+GennySettings.cacheApiPort+"]");
		HttpServerOptions serverOptions = new HttpServerOptions();
		  serverOptions.setUsePooledBuffers(true);
		  serverOptions.setCompressionSupported(true);
		  serverOptions.setCompressionLevel(3);
		  serverOptions.setUseAlpn(true);
		vertx.createHttpServer(/* serverOptions*/).requestHandler(router::accept).listen(Integer.parseInt(GennySettings.cacheApiPort));
	}

}
