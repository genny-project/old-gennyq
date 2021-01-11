package org.acme.security.keycloak.authorization;


import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import life.genny.qwandautils.PropertiesReader;

public class KeycloakServer implements QuarkusTestResourceLifecycleManager {
    
	static public String keycloakUrl;
	public static String KEYCLOAK_VERSION = new PropertiesReader("genny.properties").getProperty("keycloak.version","12.0.1");

	static public GenericContainer   keycloak  = new FixedHostPortGenericContainer("quay.io/keycloak/keycloak:" + KEYCLOAK_VERSION)//System.getProperty("keycloak.version"))
            //.withFixedExposedPort(8580, 8080)
            //.withFixedExposedPort(8543, 8443)
    		.withExposedPorts(8080)
            .withEnv("KEYCLOAK_USER", "admin")
            .withEnv("KEYCLOAK_PASSWORD", "admin")
            .withEnv("KEYCLOAK_LOGLEVEL", "debug")
            .withEnv("KEYCLOAK_IMPORT", "/config/realm.json")
            .dependsOn(MySqlServer.mysql)
          //  .withEnv("DB_VENDOR", "H2")
            .withEnv("DB_VENDOR", "mysql")
            .withEnv("DB_ADDR", "127.0.0.1")
            .withEnv("DB_PORT", MySqlServer.MYSQL_PORT)
            .withEnv("DB_DATABASE", "gennydb")
            .withEnv("DB_USER", "genny")
            .withEnv("DB_PASSWORD", "password")
            .withEnv("JAVA_OPTS_APPEND", "-Djava.awt.headless=true")
            .withEnv("PREPEND_JAVA_OPTS", "-Dkeycloak.profile=preview -Dkeycloak.profile.feature.token_exchange=enabled -Dkeycloak.profile.feature.account_api=enabled")
            .withClasspathResourceMapping("quarkus-realm.json", "/config/realm.json", BindMode.READ_ONLY)
            .waitingFor(Wait.forHttp("/auth"))
            .withStartupTimeout(Duration.ofMinutes(2));
	

	//public static GenericContainer   keycloak  = null;
    @Override
    public Map<String, String> start() {
    	System.out.println("Starting Keycloak test Server");
    	Map<String, String> returnCollections = new HashMap<String,String>();
    	
 
        keycloak.start();
        
        String fullKeycloakUrl = "http://"+keycloak.getContainerIpAddress()+":"+keycloak.getMappedPort(8080)+"/auth/realms/quarkus";
        keycloakUrl = "http://"+keycloak.getContainerIpAddress()+":"+keycloak.getMappedPort(8080);
        System.out.println("fullKeycloakURL = "+fullKeycloakUrl);

        
        returnCollections.putAll(Collections.singletonMap("%test.quarkus.oidc.auth-server-url", fullKeycloakUrl));
        returnCollections.putAll(Collections.singletonMap("%test.quarkus.oidc.client-id", "backend-service"));
        returnCollections.putAll(Collections.singletonMap("%test.quarkus.oidc.credentials.secret", "secret"));
        returnCollections.putAll(Collections.singletonMap("%test.keycloak.admin.password","admin"));
        returnCollections.putAll(Collections.singletonMap("%test.keycloak.admin.realm","quarkus"));
        returnCollections.putAll(Collections.singletonMap("quarkus.oidc.auth-server-url", fullKeycloakUrl));
        returnCollections.putAll(Collections.singletonMap("quarkus.oidc.client-id", "backend-service"));
        returnCollections.putAll(Collections.singletonMap("quarkus.oidc.credentials.secret", "secret"));
        returnCollections.putAll(Collections.singletonMap("keycloak.admin.password","admin"));
        returnCollections.putAll(Collections.singletonMap("keycloak.admin.realm","quarkus"));

        //return Collections.emptyMap();
        return returnCollections;
    }

    @Override
    public void stop() {
        keycloak.stop();
    }
    
    @Override
    public int order()
    {
    	return 1;
    }
}
