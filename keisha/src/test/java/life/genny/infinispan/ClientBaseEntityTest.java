package life.genny.infinispan;

import java.util.Optional;

import javax.inject.Inject;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import io.quarkus.infinispan.client.Remote;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import life.genny.constants.CacheNames;
import life.genny.models.attribute.EntityAttribute;
import life.genny.models.entity.BaseEntity;
import life.genny.models.exception.BadDataException;
import life.genny.utils.BaseEntityTestUtil;
import life.genny.utils.EntityAttributeTestUtil;
import life.genny.utils.KeyConvertion;

@QuarkusTest
@QuarkusTestResource(InfinispanServer.class)
@TestInstance(Lifecycle.PER_CLASS)
public class ClientBaseEntityTest {


  @Inject @Remote(CacheNames.BASEENTITY)
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
    cache.put(KeyConvertion.convertObjectToKey(BaseEntityTestUtil.baseEntityObject),BaseEntityTestUtil.baseEntityObject);
    fetchedBaseEntity = cache.get(KeyConvertion.convertObjectToKey(BaseEntityTestUtil.baseEntityObject));
    fetchedBaseEntity.baseEntityAttributes.stream().forEach(d -> d.baseentity = fetchedBaseEntity);
  }

}
