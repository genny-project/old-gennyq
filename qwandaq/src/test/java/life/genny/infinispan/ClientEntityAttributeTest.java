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
import life.genny.models.attribute.EntityAttribute;
import life.genny.utils.EntityAttributeTestUtil;

@QuarkusTest
@QuarkusTestResource(InfinispanServer.class)
@TestInstance(Lifecycle.PER_CLASS)
public class ClientEntityAttributeTest {


  RemoteCache<String, EntityAttribute> cache;

  EntityAttribute fetchedEntityAttribute;

  @Inject
  RemoteCacheManager remoteCacheManager;


  @Test 
  public void testEntityAttributeIntegrity(){
    System.out.println(EntityAttributeTestUtil.entityAttributeObject.toString());
    System.out.println(fetchedEntityAttribute.toString());
    Assertions.assertTrue(EntityAttributeTestUtil.entityAttributeObject.equals(fetchedEntityAttribute));
  }

  @BeforeAll
  public void initCacheConfig(){
    cache = remoteCacheManager.administration().getOrCreateCache("baseentity","example.PROTOBUF_DIST");
    cache.put("entityattribute",EntityAttributeTestUtil.entityAttributeObject);
    fetchedEntityAttribute = cache.get("entityattribute");
  }

}
