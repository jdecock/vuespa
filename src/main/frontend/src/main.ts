import './assets/styles/main.css';

import { createApp } from 'vue';
import { createPinia } from 'pinia';

import App from './App.vue';
import router from './router';
import setupInterceptors from './services/interceptors.ts';

const app = createApp(App);
app.use(createPinia());
setupInterceptors();
app.use(router);

app.mount('#app');
