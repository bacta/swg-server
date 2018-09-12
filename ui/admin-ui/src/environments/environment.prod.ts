export const environment = {
  production: true,
  //Set these to your production endpoints for oauth and api.
  auth: {
    url: 'http://localhost:8080/oauth',
    clientId: 'rest'
  },
  api: {
    url: 'http://localhost:8080/api'
  }
};
