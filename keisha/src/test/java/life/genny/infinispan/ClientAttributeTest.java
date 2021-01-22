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
import life.genny.models.attribute.Attribute;
import life.genny.utils.AttributeTestUtil;
import life.genny.utils.KeyConvertion;

@QuarkusTest
@QuarkusTestResource(InfinispanServer.class)
@TestInstance(Lifecycle.PER_CLASS)
public class ClientAttributeTest {

  Attribute fetchedAttribute;

  @Inject @Remote(CacheNames.ATTRIBUTE)
  RemoteCache<String, Attribute> cache;

  @Inject
  RemoteCacheManager remoteCacheManager;

  @Test 
  public void testAttributeIntegrity(){
    Assertions.assertTrue(AttributeTestUtil.attributeObject.equals(fetchedAttribute));
  }

  @BeforeAll
  public void initCacheConfig(){
    cache.put(KeyConvertion.convertObjectToKey(AttributeTestUtil.attributeObject),AttributeTestUtil.attributeObject);
    fetchedAttribute = cache.get(KeyConvertion.convertObjectToKey(AttributeTestUtil.attributeObject));
  }

}
