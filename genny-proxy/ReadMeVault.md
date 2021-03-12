1) run the docker 
docker run --name keycloak -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=admin -p 8180:8080 -p 8543:8443 quay.io/keycloak/keycloak:11.0.3

2) Import the file
https://raw.githubusercontent.com/quarkusio/quarkus-quickstarts/master/security-keycloak-authorization-quickstart/config/quarkus-realm.json

3) in order to meet the requirements, in Brower view, localhost:8180/auth, after importing, please change the realm to “internmatch” and add new roles (dev superadmin) to the client id backend-service 

Make sure its successfully running by replace expression value to the real value
quarkus.oidc.auth-server-url=http://localhost:8180/auth/realms/internmatch
quarkus.oidc.client-id=backend-service
quarkus.oidc.credentials.secret=secret

4) exactly follow the steps: https://quarkus.io/guides/vault
But add vault kv put secret/myapps/vault-quickstart/config secret=secret client-id=backend-service auth-server-url=http://localhost:8180/auth/realms/internmatch

Replace 
quarkus.oidc.auth-server-url=${auth-server-url}
quarkus.oidc.client-id=${client-id}
quarkus.oidc.credentials.secret=${secret} 

5) alternatively, -Dquarkus.oidc.client-id=XXXX =====>>>>> do you think it could be easier, but vault is also good and its a good idea to hide confidential fields

6) idea got from https://quarkus.io/guides/vault-datasource

I believe when start up, it reads the values from vault http://localhost:8200/myapps/vault-quickstart/config