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
import life.genny.models.Link;
import life.genny.utils.KeyConvertion;
import life.genny.utils.LinkTestUtil;

@QuarkusTest
@QuarkusTestResource(InfinispanServer.class)
@TestInstance(Lifecycle.PER_CLASS)
public class ClientLinkTest {


  @Inject @Remote(CacheNames.LINK)
  RemoteCache<String, Link> cache;

  Link fetchedLink;

  @Inject
  RemoteCacheManager remoteCacheManager;


  @Test 
  public void testLinkIntegrity(){
    Assertions.assertTrue(LinkTestUtil.linkObject.equals(fetchedLink));
  }

  @BeforeAll
  public void initCacheConfig(){
    cache.put(KeyConvertion.convertObjectToKey(LinkTestUtil.linkObject),LinkTestUtil.linkObject);
    fetchedLink = cache.get(KeyConvertion.convertObjectToKey(LinkTestUtil.linkObject));
  }

}
