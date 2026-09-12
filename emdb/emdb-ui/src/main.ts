import './assets/main.css';
import '@fontsource-variable/montserrat';
import 'primeicons/primeicons.css';

import { createApp } from 'vue'
import router from './router'
import App from './App.vue'
import ConfirmationService from 'primevue/confirmationservice';
import Noir from './presets/Noir';
import PrimeVue from 'primevue/config'
import ToastService from 'primevue/toastservice'

document.documentElement.classList.toggle('dark', true);

const app = createApp(App)

app.use(router)
app.use(PrimeVue, {
  theme: {
    preset: Noir,
    options: {
      darkModeSelector: '.dark',
      cssLayer: {
        name: 'primevue',
        order: 'theme, base, primevue',
      },
    },
  },
});
app.use(ToastService);
app.use(ConfirmationService);

app.mount('#app')
