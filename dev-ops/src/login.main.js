import Vue from 'vue';
import LoginApp from './LoginApp.vue';
import vuetify from './plugins/vuetify';
import global from './assets/js/global.js';
import http from './assets/js/http.js';

Vue.prototype.$http = http;
Vue.prototype.$api = global.api;
Vue.config.productionTip = false;

new Vue({
  vuetify,
  render: h => h(LoginApp)
}).$mount('#loginApp');
