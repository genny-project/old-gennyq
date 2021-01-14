package life.genny.infinispan;

import javax.inject.Inject;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import life.genny.models.entity.BaseEntity;
import life.genny.utils.BaseEntityTestUtil;

@QuarkusTest
@QuarkusTestResource(InfinispanServer.class)
@TestInstance(Lifecycle.PER_CLASS)
public class ClientBaseEntityTest {


  RemoteCache<String, BaseEntity> cache;

  BaseEntity fetchedBaseEntity;

  @Inject
  RemoteCacheManager remoteCacheManager;


  @Test 
  public void testBaseEntityIntegrity(){
    Assertions.assertTrue(BaseEntityTestUtil.baseEntityObject.equals(fetchedBaseEntity));
  }

  @BeforeAll
  public void initCacheConfig(){
    cache = remoteCacheManager.administration().getOrCreateCache("baseentity","example.PROTOBUF_DIST");
    cache.put("PRJ_TEST",BaseEntityTestUtil.baseEntityObject);
    fetchedBaseEntity = cache.get("PRJ_TEST");
  }

}
