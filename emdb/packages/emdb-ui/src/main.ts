import './assets/main.css';
import '@fontsource-variable/montserrat'; 
import 'primeicons/primeicons.css';

import { createApp } from 'vue';
import router from './router';

import App from './App.vue';
import ConfirmationService from 'primevue/confirmationservice';
import Noir from './presets/Noir';
import PrimeVue from 'primevue/config';
import Toast from 'primevue/toast';
import ToastService from "primevue/toastservice";

import keycloak from './auth/keycloak';

const app = createApp(App);

// check-sso silently checks for an existing session without forcing a login
keycloak.init({ onLoad: 'check-sso', pkceMethod: 'S256' })
  .then((authenticated) => {
    const app = createApp(App);

    // Register global plugins so that they can be used any where in the application.
    app.use(router);
    app.use(PrimeVue, {
    theme: {
      preset: Noir,
        options: {
          cssLayer: {
            name: 'primevue',
            order: 'theme, base, primevue'
          }
        }
      },
    });
    app.use(ConfirmationService);
    app.use(ToastService);    

    // Register global components with Vue so that they are available to the entire application.
    app.component("Toast", Toast);

    app.mount('#app');
  })
  .catch((error) => {
    console.error('Failed to initialize Keycloak', error);
  });
