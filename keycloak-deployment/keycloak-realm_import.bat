docker cp digital-evidence-realm.json keycloak:/opt/keycloak/data/digital-evidence-realm.json
docker exec -it keycloak /opt/keycloak/bin/kcadm.sh config credentials ^
  --server http://localhost:8080 --realm master --user admin --password admin
docker exec -it keycloak /opt/keycloak/bin/kcadm.sh update realms/digital-evidence ^
  -f /opt/keycloak/data/digital-evidence-realm.json
