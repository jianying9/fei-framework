import Vue from 'vue';
import VueRouter from 'vue-router';

//防止重复切换相同的路由地址导致控制台异常
const originalPush = VueRouter.prototype.push;
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err);
};
Vue.use(VueRouter);

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import(/* webpackChunkName: "chunk" */ '../views/Home.vue')
  },
];

const router = new VueRouter({
  mode: 'history',
  routes: routes
});

export default router;
