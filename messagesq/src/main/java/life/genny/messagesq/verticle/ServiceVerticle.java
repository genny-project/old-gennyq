package life.genny.messagesq.verticle;

import java.lang.invoke.MethodHandles;
import org.apache.logging.log4j.Logger;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import life.genny.channel.Routers;
import life.genny.channels.EBCHandlers;
import life.genny.cluster.Cluster;
import life.genny.cluster.CurrentVtxCtx;
import life.genny.eventbus.EventBusInterface;
import life.genny.eventbus.EventBusVertx;
import life.genny.eventbus.VertxCache;
import life.genny.qwandautils.GennyCacheInterface;
import life.genny.utils.VertxUtils;

public class ServiceVerticle extends AbstractVerticle {

	protected static final Logger log = org.apache.logging.log4j.LogManager
			.getLogger(MethodHandles.lookup().lookupClass().getCanonicalName());

	@Override
	public void start() {
		log.info("Setting up routes");
		final Future<Void> startFuture = Future.future();
		Cluster.joinCluster().compose(res -> {
			EventBusInterface eventBus = new EventBusVertx();
			GennyCacheInterface vertxCache = new VertxCache();
			VertxUtils.init(eventBus, vertxCache);
			Routers.routers(vertx);
			Routers.activate(vertx);
			EBCHandlers.registerHandlers(CurrentVtxCtx.getCurrentCtx().getClusterVtx().eventBus());
			startFuture.complete();
		}, startFuture);

	}
}
