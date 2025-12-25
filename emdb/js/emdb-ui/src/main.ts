import './assets/main.css';
import 'primeicons/primeicons.css';

import { createApp } from 'vue';
import router from './router';

import App from './App.vue';
import Noir from './presets/Noir';
import PrimeVue from 'primevue/config';
import Toast from 'primevue/toast';
import ToastService from "primevue/toastservice";

const app = createApp(App);

/*
 * Register global plugins so that they can be used any where in the application.
 */
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
  }
});
app.use(ToastService);

/*
 * Register global components with Vue so that they are available to the entire 
 * application.
 */
app.component("Toast", Toast);

/**
 * Mount the application.
 */
app.mount('#app');
