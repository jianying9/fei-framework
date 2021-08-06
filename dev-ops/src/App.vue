<template>
  <v-app>
    <v-navigation-drawer permanent app>
      <v-list>
        <v-list-item>
          <v-list-item-content>
            <v-list-item-title class="text-h6">
              {{ user.name }}
            </v-list-item-title>
            <v-list-item-subtitle>{{ user.email }}</v-list-item-subtitle>
          </v-list-item-content>
        </v-list-item>
      </v-list>

      <v-list>
        <v-list-item
          link
          v-for="item in itemArray"
          :key="item.title"
          @click="selectItem(item)"
        >
          <v-list-item-icon>
            <v-icon :color="item.color">{{ item.icon }}</v-icon>
          </v-list-item-icon>
          <v-list-item-title>{{ item.title }}</v-list-item-title>
        </v-list-item>
      </v-list>
    </v-navigation-drawer>
    <v-main style="background: #c8ebdf">
      <router-view></router-view>
    </v-main>
    <v-dialog v-model="dialog.show" max-width="300">
      <v-alert style="margin: 0" border="left" colored-border type="error" elevation="2">
        {{ dialog.msg }}
      </v-alert>
    </v-dialog>
  </v-app>
</template>

<script>
import global from "./assets/js/global.js";
import http from "./assets/js/http.js";
export default {
  name: "App",
  data: () => ({
    dialog: {
      show: false,
      msg: "",
    },
    user: {
      id: "",
      name: "",
      username: "",
      email: "",
      isAdmin: false,
    },
    itemArray: [
      {
        color: "blue-grey darken-2",
        icon: "mdi-folder",
        route: "/home",
        title: "首页",
      },
      {
        color: "blue-grey darken-2",
        icon: "mdi-account-multiple",
        route: "/user",
        title: "用户",
      },
    ],
  }),
  mounted: function () {
    http.interceptors.use((bizData) => {
      if (bizData.code === "unlogin") {
        //未登陆,跳转到登陆页面
        window.location.href = "/login.html";
      } else {
        //其它请求失败处理
        this.dialog.msg = bizData.msg;
        this.dialog.show = true;
      }
    });

    let route = window.location.pathname;
    for (const iterator of this.itemArray) {
      if (route.indexOf(iterator.route) == 0) {
        iterator.color = "green lighten-1";
        break;
      }
    }
    //加载用户
    this.$http.post(global.api.user_current, {}).then((biaData) => {
      this.user = biaData.data;
    });
  },
  methods: {
    selectItem: function (item) {
      item.color = "green lighten-1";
      for (const iterator of this.itemArray) {
        if (iterator.title !== item.title) {
          iterator.color = "blue-grey darken-2";
        }
      }
      this.$router.push(item.route);
    },
  },
};
</script>
