import './assets/main.css';
import '@fontsource-variable/montserrat';
import 'primeicons/primeicons.css';

import { createApp } from 'vue'
import router from './router'
import App from './App.vue'
import Noir from './presets/Noir';
import PrimeVue from 'primevue/config'
import Toast from 'primevue/toast'
import ToastService from 'primevue/toastservice'

const app = createApp(App)

app.use(router)
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
app.use(ToastService);
app.component('Toast', Toast);

app.mount('#app')
