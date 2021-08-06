<template>
  <v-app>
    <v-navigation-drawer permanent app>
      <v-list>
        <v-list-item>
          <v-list-item-content>
            <v-list-item-title class="text-h6">
              {{ user.name }}
            </v-list-item-title>
            <v-list-item-subtitle>{{user.email}}</v-list-item-subtitle>
          </v-list-item-content>
        </v-list-item>
      </v-list>

      <v-list>
        <v-list-item link v-for="(item) in itemArray" :key="item.title" @click="selectItem(item)">
          <v-list-item-icon>
            <v-icon :color="item.color">{{item.icon}}</v-icon>
          </v-list-item-icon>
          <v-list-item-title>{{item.title}}</v-list-item-title>
        </v-list-item>
      </v-list>
    </v-navigation-drawer>
    <v-main style="background: #c8ebdf;">
        <router-view></router-view>
    </v-main>
  </v-app>
</template>

<script>
import global from "./assets/js/global.js";
export default {
  name: "App",
  data: () => ({
    user: {
      id: "",
      name: "",
      username: "",
      email: "",
      isAdmin: false,
    },
    itemArray:[{
      color: 'blue-grey darken-2',
      icon: 'mdi-folder',
      route: '/home',
      title: '首页'
    },{
      color: 'blue-grey darken-2',
      icon: 'mdi-account-multiple',
      route: '/user',
      title: '用户'
    }]
  }),
  mounted: function () {
    let route = window.location.pathname;
    for (const iterator of this.itemArray) {
      if(route.indexOf(iterator.route) == 0) {
        iterator.color = 'green lighten-1';
        break;
      }
    }
    //加载用户
    this.$http.post(global.api.user_current, {}).then((biaData) => {
      this.user = biaData.data;
    });
  },
  methods: {
    selectItem: function(item) {
      item.color = 'green lighten-1';
      for (const iterator of this.itemArray) {
        if(iterator.title !== item.title) {
          iterator.color = 'blue-grey darken-2';
        }
      }
      this.$router.push(item.route);
    }
  },
};
</script>
