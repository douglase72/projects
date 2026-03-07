import Keycloak from 'keycloak-js';

const keycloak = new Keycloak({
  url: 'http://localhost:8080',
  realm: 'projects',
  clientId: 'emdb-ui' 
});

export default keycloak;