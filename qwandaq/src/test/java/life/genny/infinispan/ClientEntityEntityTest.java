package life.genny.infinispan;


import javax.inject.Inject;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import life.genny.models.entity.EntityEntity;
import life.genny.utils.EntityEntityTestUtil;

@QuarkusTest
@QuarkusTestResource(InfinispanServer.class)
@TestInstance(Lifecycle.PER_CLASS)
public class ClientEntityEntityTest {


  RemoteCache<String, EntityEntity> cache;

  EntityEntity fetchedEntityEntity;

  @Inject
  RemoteCacheManager remoteCacheManager;


  @Test 
  public void testEntityEntityIntegrity(){
    //Assertions.assertTrue(EntityEntityTestUtil.entityEntityObject.equals(fetchedEntityEntity));
  }

  @BeforeAll
  public void initCacheConfig(){
    cache = remoteCacheManager.administration().getOrCreateCache("entityEntityObject","example.PROTOBUF_DIST");
    EntityEntity ee = EntityEntityTestUtil.entityEntityObject;
    cache.put("baseentitycode+baseentitycode",ee);
    fetchedEntityEntity = cache.get("baseentitycode+baseentitycode");
  }

}
