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
import ToastService from 'primevue/toastservice';
import keycloak from './auth/keycloak';

async function bootstrap() {
  try {
    const authenticated = await keycloak.init({
      onLoad: 'check-sso',
      pkceMethod: 'S256',
      checkLoginIframe: false,
      silentCheckSsoRedirectUri: window.location.origin + '/emdb/silent-check-sso.html',
    });
    console.log('Authenticated:', authenticated);
  } catch (error) {
    console.warn('Init failed:', error);
    keycloak.clearToken();
  }

  const app = createApp(App);

  app.use(router);
  app.use(PrimeVue, {
    theme: {
      preset: Noir,
      options: {
        cssLayer: {
          name: 'primevue',
          order: 'theme, base, primevue',
        },
      },
    },
  });
  app.use(ConfirmationService);
  app.use(ToastService);
  app.component('Toast', Toast);

  await router.isReady();
  app.mount('#app');
}

bootstrap();