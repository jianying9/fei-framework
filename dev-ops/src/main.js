import Vue from 'vue';
import App from './App.vue';
import router from './router';
import vuetify from './plugins/vuetify';
import global from './assets/js/global.js';
import http from './assets/js/http.js';

Vue.prototype.$http = http;
Vue.prototype.$api = global.api;
Vue.config.productionTip = false;

new Vue({
  router,
  vuetify,
  render: h => h(App)
}).$mount('#app');
