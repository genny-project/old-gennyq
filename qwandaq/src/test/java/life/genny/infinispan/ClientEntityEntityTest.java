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
    //Assertions.assertTrue(EntityEntityTestUtil.valueObject.equals(fetchedEntityEntity));
  }

  @BeforeAll
  public void initCacheConfig(){
    cache = remoteCacheManager.administration().getOrCreateCache("valueObject","example.PROTOBUF_DIST");
    EntityEntity val = EntityEntityTestUtil.valueObject;
    System.out.println("hereeeee ::: "+val.getEntityEntity());
    cache.put("valueKey",val);
    fetchedEntityEntity = cache.get("valueKey");
  }

}
