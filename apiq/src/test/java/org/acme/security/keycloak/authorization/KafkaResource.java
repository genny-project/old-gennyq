package org.acme.security.keycloak.authorization;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.KafkaContainer;

public class KafkaResource implements QuarkusTestResourceLifecycleManager  {

    private static final KafkaContainer KAFKA = new KafkaContainer();

    @Override
    public int order(){
      return 2;
    }
    
    @Override
    public Map<String, String> start() {
        KAFKA.start();
        return Collections.singletonMap("kafka.bootstrap.servers", KAFKA.getBootstrapServers());
    }

    @Override
    public void stop() {
        KAFKA.stop();
    }
}
