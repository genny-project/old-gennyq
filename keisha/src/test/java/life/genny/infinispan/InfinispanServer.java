package life.genny.infinispan;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class InfinispanServer implements QuarkusTestResourceLifecycleManager{

  private GenericContainer<?> infinispan;

  static final String SERVER_VERSION = "12.0.1.Final";
  static final String SERVER_IMAGE = "quay.io/infinispan/server-native";
  static final String ENV_USER = "USER";
  static final String ENV_PASS = "PASS";

  static StringJoiner sj(String... e){
    return Arrays.asList(e).stream()
      .map(new StringJoiner(":")::add)
      .reduce((a,b)-> a)
      .get();
  }

  @Override
  public Map<String,String> start(){

    Map<String, String> returnCollections = new HashMap<String,String>();

    infinispan = new FixedHostPortGenericContainer(sj(SERVER_IMAGE,SERVER_VERSION).toString())
      .withEnv(ENV_USER, "admin")
      .withEnv(ENV_PASS, "admin")
      .waitingFor(Wait.forLogMessage(".*infinispan-quarkus-server-runner.*",1));

    infinispan.start();

    String usernameVal = infinispan.getEnvMap().get(ENV_USER).toString();
    String passwordVal = infinispan.getEnvMap().get(ENV_PASS).toString();
    String serverAddr = sj(infinispan.getHost(),infinispan.getMappedPort(11222).toString()).toString();

    returnCollections.putAll(Collections.singletonMap("quarkus.infinispan-client.auth-username", usernameVal));
    returnCollections.putAll(Collections.singletonMap("quarkus.infinispan-client.auth-password", passwordVal));
    returnCollections.putAll(Collections.singletonMap("quarkus.infinispan-client.server-list", serverAddr));
    returnCollections.putAll(Collections.singletonMap("quarkus.infinispan-client.auth-realm", "default"));
    returnCollections.putAll(Collections.singletonMap("quarkus.infinispan-client.auth-realm", "infinispan"));

    return returnCollections;

  }

  @Override
  public void stop() {
    infinispan.stop();
  }
}
