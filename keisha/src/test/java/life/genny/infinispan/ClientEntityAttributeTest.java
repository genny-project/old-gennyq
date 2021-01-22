package life.genny.infinispan;

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
import life.genny.utils.EntityAttributeTestUtil;
import life.genny.utils.KeyConvertion;

@QuarkusTest
@QuarkusTestResource(InfinispanServer.class)
@TestInstance(Lifecycle.PER_CLASS)
public class ClientEntityAttributeTest {


  @Inject @Remote(CacheNames.ENTITY_ATTRIBUTE)
  RemoteCache<String, EntityAttribute> cache;

  EntityAttribute fetchedEntityAttribute;

  @Inject
  RemoteCacheManager remoteCacheManager;


  @Test 
  public void testEntityAttributeIntegrity(){
    Assertions.assertTrue(EntityAttributeTestUtil.entityAttributeObject.equals(fetchedEntityAttribute));
  }

  @BeforeAll
  public void initCacheConfig(){
    String key = KeyConvertion.convertObjectToKey(EntityAttributeTestUtil.entityAttributeObject);
    cache.put(
        key,
        EntityAttributeTestUtil.entityAttributeObject);

    fetchedEntityAttribute = cache.get(KeyConvertion.convertObjectToKey(EntityAttributeTestUtil.entityAttributeObject));
  }

}
