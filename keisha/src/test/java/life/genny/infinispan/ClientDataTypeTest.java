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
import life.genny.models.datatype.DataType;
import life.genny.utils.DataTypeTestUtil;
import life.genny.utils.KeyConvertion;

@QuarkusTest
@QuarkusTestResource(InfinispanServer.class)
@TestInstance(Lifecycle.PER_CLASS)
public class ClientDataTypeTest {


  @Inject @Remote(CacheNames.DATA_TYPE)
  RemoteCache<String, DataType> cache;

  DataType fetchedDataType;

  @Inject
  RemoteCacheManager remoteCacheManager;


  @Test 
  public void testDataTypeIntegrity(){
    Assertions.assertTrue(DataTypeTestUtil.dataTypeObject.equals(fetchedDataType));
  }

  @BeforeAll
  public void initCacheConfig(){
    cache.put(KeyConvertion.convertObjectToKey(DataTypeTestUtil.dataTypeObject),DataTypeTestUtil.dataTypeObject);
    fetchedDataType = cache.get(KeyConvertion.convertObjectToKey(DataTypeTestUtil.dataTypeObject));
  }

}
