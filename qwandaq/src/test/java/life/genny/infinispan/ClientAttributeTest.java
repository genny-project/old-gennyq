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
import life.genny.qwanda.attribute.Attribute;
import life.genny.utils.AttributeTestUtil;

@QuarkusTest
@QuarkusTestResource(InfinispanServer.class)
@TestInstance(Lifecycle.PER_CLASS)
public class ClientAttributeTest {


  RemoteCache<String, Attribute> cache;

  Attribute fetchedAttribute;

  @Inject
  RemoteCacheManager remoteCacheManager;


  @Test 
  public void testAttributeIntegrity(){
    Assertions.assertTrue(AttributeTestUtil.attributeObject.equals(fetchedAttribute));
  }

  @BeforeAll
  public void initCacheConfig(){
    cache = remoteCacheManager.administration().getOrCreateCache("attribute","example.PROTOBUF_DIST");
    cache.put("attribute",AttributeTestUtil.attributeObject);
    fetchedAttribute = cache.get("attribute");
  }

}
