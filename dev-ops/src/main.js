import Vue from 'vue';
import App from './App.vue';
import router from './router';
import vuetify from './plugins/vuetify';
import global from './assets/js/global.js';
import http from './assets/js/http.js';

Vue.prototype.$http = http;
Vue.prototype.$api = global.api;
Vue.config.productionTip = false;

Vue.filter('firstLetterFilter', function (value) {
  return value.charAt(0).toUpperCase();
});

Vue.filter('visibilityFilter', function (value) {
  let text;
  switch (value) {
    case 'private':
      text = '私密';
      break;
    case 'internal':
      text = '登录可见';
      break;
    default:
      text = '公开';
  }
  return text;
});

Vue.filter('accessLevelFilter', function (value) {
  let text;
  switch (value) {
    case 20:
      text = 'Reporter';
      break;
    case 30:
      text = 'Developer';
      break;
    case 40:
      text = 'Maintainer';
      break;
    case 50:
      text = 'Owner';
      break;
    default:
      text = 'Guest';
  }
  return text;
});

new Vue({
  router,
  vuetify,
  render: h => h(App)
}).$mount('#app');
