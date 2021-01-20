package life.genny.infinispan;


import javax.inject.Inject;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;

import io.quarkus.infinispan.client.Remote;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import life.genny.constants.CacheNames;
import life.genny.models.entity.BaseEntity;
import life.genny.models.entity.EntityEntity;
import life.genny.utils.BaseEntityTestUtil;
import life.genny.utils.EntityEntityTestUtil;
import life.genny.utils.KeyConvertion;

@QuarkusTest
@QuarkusTestResource(InfinispanServer.class)
@TestInstance(Lifecycle.PER_CLASS)
public class ClientEntityEntityTest {


  @Inject @Remote(CacheNames.ENTITY_ENTITY)
  RemoteCache<String, EntityEntity> cache;

  @Inject @Remote(CacheNames.BASEENTITY)
  RemoteCache<String, BaseEntity> bcache;

  EntityEntity fetchedEntityEntity;

  @Inject
  RemoteCacheManager remoteCacheManager;


  @Test 
  public void testEntityEntityIntegrity(){
    Assertions.assertTrue(EntityEntityTestUtil.entityEntityObject.equals(fetchedEntityEntity));
  }

  @BeforeAll
  public void initCacheConfig(){
    cache.put(KeyConvertion.convertObjectToKey(EntityEntityTestUtil.entityEntityObject),EntityEntityTestUtil.entityEntityObject);
    bcache.put(KeyConvertion.convertObjectToKey(BaseEntityTestUtil.baseEntityObject),BaseEntityTestUtil.baseEntityObject);
    fetchedEntityEntity = cache.get(KeyConvertion.convertObjectToKey(EntityEntityTestUtil.entityEntityObject));
    fetchedEntityEntity.source = bcache.get(KeyConvertion.convertObjectToKey(new BaseEntity(fetchedEntityEntity.targetCode,"")));
    fetchedEntityEntity.target = bcache.get(KeyConvertion.convertObjectToKey(new BaseEntity(fetchedEntityEntity.sourceCode,"")));
  }

}
