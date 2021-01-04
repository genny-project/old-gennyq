package org.acme.security.keycloak.authorization;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

public class KeycloakServer implements QuarkusTestResourceLifecycleManager {
    private GenericContainer keycloak;
    public   GenericContainer   mysql;

    public static String MYSQL_PORT = "3340";//new PropertiesReader("genny.properties").getProperty("mysql.test.port","3336");
    @Override
    public Map<String, String> start() {
                mysql = new FixedHostPortGenericContainer("gennyproject/mysql:8x")
                .withFixedExposedPort(Integer.parseInt(MYSQL_PORT), 3306)
                // .withExposedPorts(3306)
                .withEnv("MYSQL_USERNAME","genny")
                .withEnv("MYSQL_URL","mysql")
                .withEnv("MYSQL_DB","gennydb")
                .withEnv("MYSQL_PORT","3306")
                .withEnv("MYSQL_ALLOW_EMPTY","")
                .withEnv("MYSQL_RANDOM_ROOT_PASSWORD","no")
                .withEnv("MYSQL_DATABASE","gennydb")
                .withEnv("MYSQL_USER","genny")
                .withEnv("MYSQL_PASSWORD","password")
                .withEnv("MYSQL_ROOT_PASSWORD","password")
                .withEnv("ADMIN_USERNAME","admin")
                .withEnv("ADMIN_PASSWORD","password")
                .withEnv("MYSQL_ROOT_PASSWORD","password")
                .waitingFor(Wait.forLogMessage(".*ready for connection.*\\n", 1))
                //      .withLogConsumer(logConsumer)
                .withStartupTimeout(Duration.ofMinutes(3));
        mysql.start();

        keycloak = new FixedHostPortGenericContainer("quay.io/keycloak/keycloak:" + System.getProperty("keycloak.version"))
                .withFixedExposedPort(8180, 8080)
                .withFixedExposedPort(8543, 8443)
                .withEnv("DB_VENDOR", "H2")
                .withEnv("KEYCLOAK_USER", "admin")
                .withEnv("KEYCLOAK_PASSWORD", "admin")
                .withEnv("KEYCLOAK_IMPORT", "/config/realm.json")
                .withClasspathResourceMapping("quarkus-realm.json", "/config/realm.json", BindMode.READ_ONLY)
                .waitingFor(Wait.forHttp("/auth"))
                .withStartupTimeout(Duration.ofMinutes(2));
        keycloak.start();
        return Collections.emptyMap();
    }

    @Override
    public void stop() {
        keycloak.stop();
        mysql.stop();
    }
}
