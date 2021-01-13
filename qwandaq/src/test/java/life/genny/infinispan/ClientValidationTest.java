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
import life.genny.models.validation.Validation;
import life.genny.utils.ValidationTestUtil;

@QuarkusTest
@QuarkusTestResource(InfinispanServer.class)
@TestInstance(Lifecycle.PER_CLASS)
public class ClientValidationTest {


  RemoteCache<String, Validation> cache;

  Validation fetchedValidation;

  @Inject
  RemoteCacheManager remoteCacheManager;


  @Test 
  public void testValidationIntegrity(){
    Assertions.assertTrue(ValidationTestUtil.validationObject.equals(fetchedValidation));
  }

  @BeforeAll
  public void initCacheConfig(){
    cache = remoteCacheManager.administration().getOrCreateCache("validation","example.PROTOBUF_DIST");
    cache.put("validation",ValidationTestUtil.validationObject);
    fetchedValidation = cache.get("validation");
  }

}
