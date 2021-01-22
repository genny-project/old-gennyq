package life.genny.service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.infinispan.client.hotrod.RemoteCacheManager;

import io.quarkus.runtime.Startup;
import life.genny.constants.CacheNames;
import life.genny.models.entity.BaseEntity;

@Startup
public class Initialise {

  @Inject
  RemoteCacheManager remoteCacheManager;

  @PostConstruct
  public void start(){
    remoteCacheManager.administration().createCache(CacheNames.ATTRIBUTE,"example.PROTOBUF_DIST");
    remoteCacheManager.administration().createCache(CacheNames.BASEENTITY,"example.PROTOBUF_DIST");
    remoteCacheManager.administration().createCache(CacheNames.ENTITY_ATTRIBUTE,"example.PROTOBUF_DIST");
    remoteCacheManager.administration().createCache(CacheNames.ENTITY_ENTITY,"example.PROTOBUF_DIST");
    remoteCacheManager.administration().createCache(CacheNames.VALUE,"example.PROTOBUF_DIST");
    remoteCacheManager.administration().createCache(CacheNames.VALIDATION,"example.PROTOBUF_DIST");
    remoteCacheManager.administration().createCache(CacheNames.DATA_TYPE,"example.PROTOBUF_DIST");
    remoteCacheManager.administration().createCache(CacheNames.LINK,"example.PROTOBUF_DIST");
  }
}
