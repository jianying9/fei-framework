import Vue from 'vue';
import VueRouter from 'vue-router';

//防止重复切换相同的路由地址导致控制台异常
const originalPush = VueRouter.prototype.push;
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err);
};
Vue.use(VueRouter);

const routes = [
  { path: '/', redirect: '/home' },
  {
    path: '/home',
    name: 'home',
    component: () => import(/* webpackChunkName: "chunk" */ '../views/Home.vue')
  },
  {
    path: '/user/list',
    name: 'userList',
    component: () => import(/* webpackChunkName: "chunk" */ '../views/UserList.vue')
  },
  {
    path: '/user/add',
    name: 'userAdd',
    component: () => import(/* webpackChunkName: "chunk" */ '../views/UserAdd.vue')
  },
  {
    path: '/group/list',
    name: 'groupList',
    component: () => import(/* webpackChunkName: "chunk" */ '../views/GroupList.vue')
  },
  {
    path: '/group/add',
    name: 'groupAdd',
    component: () => import(/* webpackChunkName: "chunk" */ '../views/GroupAdd.vue')
  },
  {
    path: '/group/:id',
    name: 'group',
    component: () => import(/* webpackChunkName: "chunk" */ '../views/Group.vue')
  },
  {
    path: '/group/member/add/:id',
    name: 'groupMemberAdd',
    component: () => import(/* webpackChunkName: "chunk" */ '../views/GroupMemberAdd.vue')
  },
  {
    path: '/group/projct/add/:id',
    name: 'groupProjectAdd',
    component: () => import(/* webpackChunkName: "chunk" */ '../views/GroupProjectAdd.vue')
  },
  {
    path: '/node/list',
    name: 'groupList',
    component: () => import(/* webpackChunkName: "chunk" */ '../views/NodeList.vue')
  },
  {
    path: '/node/add',
    name: 'nodeAdd',
    component: () => import(/* webpackChunkName: "chunk" */ '../views/NodeAdd.vue')
  },
];

const router = new VueRouter({
  mode: 'history',
  routes: routes
});

export default router;
