package org.acme.security.keycloak;

import org.testcontainers.containers.Network;

public class SharedNetwork {

  static Network network = Network.newNetwork();
}
