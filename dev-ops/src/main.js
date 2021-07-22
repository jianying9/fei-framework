import Vue from 'vue';
import App from './App.vue';
import router from './router';
import vuetify from './plugins/vuetify';
import global from './assets/js/global.js';
import http from './assets/js/http.js';

http.interceptors.use(function (bizData) {
  if (bizData.code === 'unlogin') {
    //未登陆,跳转到登陆页面
    window.location.href = '/login.html';
  } else {
    //其它请求失败处理
    console.error(bizData.msg);
  }
});

Vue.prototype.$http = http;
Vue.prototype.$api = global.api;
Vue.config.productionTip = false;

new Vue({
  router,
  vuetify,
  render: h => h(App)
}).$mount('#app');
