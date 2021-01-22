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
import life.genny.models.Value;
import life.genny.utils.KeyConvertion;
import life.genny.utils.ValueTestUtil;

@QuarkusTest
@QuarkusTestResource(InfinispanServer.class)
@TestInstance(Lifecycle.PER_CLASS)
public class ClientValueTest {

  @Inject @Remote(CacheNames.VALUE)
  RemoteCache<String, Value> cache;

  Value fetchedValue;

  @Inject
  RemoteCacheManager remoteCacheManager;


  @Test 
  public void testValueIntegrity(){
    Assertions.assertTrue(ValueTestUtil.valueObject.equals(fetchedValue));
  }

  @BeforeAll
  public void initCacheConfig(){
    cache.put(KeyConvertion.convertObjectToKey(ValueTestUtil.valueObject),ValueTestUtil.valueObject);
    fetchedValue = cache.get(KeyConvertion.convertObjectToKey(ValueTestUtil.valueObject));
  }

}
