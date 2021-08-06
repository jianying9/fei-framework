<template>
  <v-app>
    <v-navigation-drawer permanent app>
      <v-list>
        <v-list-item>
          <v-list-item-title class="text-h6">
            {{ user.name }}
          </v-list-item-title>
          <v-menu left offset-y>
            <template v-slot:activator="{ on, attrs }">
              <v-btn icon v-bind="attrs" v-on="on">
                <v-icon>mdi-dots-vertical</v-icon>
              </v-btn>
            </template>
            <v-list>
              <v-list-item link v-for="item in moreItemArray" :key="item.title" @click="selectMoreItem(item)">
                <v-list-item-title>{{ item.title }}</v-list-item-title>
              </v-list-item>
            </v-list>
          </v-menu>
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
    <v-dialog v-model="errorDialog.show" max-width="300">
      <v-alert
        style="margin: 0"
        border="left"
        colored-border
        type="error"
        elevation="2"
      >
        {{ errorDialog.msg }}
      </v-alert>
    </v-dialog>
    <v-dialog
      v-model="processDialog.show"
      persistent
      hide-overlay
      fullscreen
      transition="false"
    >
      <v-progress-linear
        indeterminate
        color="green"
        class="mb-0"
      ></v-progress-linear>
    </v-dialog>
  </v-app>
</template>

<script>
import global from "./assets/js/global.js";
import http from "./assets/js/http.js";
export default {
  name: "App",
  data: () => ({
    errorDialog: {
      show: false,
      msg: "",
    },
    processDialog: {
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
    moreItemArray: [
      {
        route: "",
        href: "https://a.zlw333.com/gitlab/-/profile/password/edit",
        title: "修改密码",
      },
    ],
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
    //通信遮罩处理
    http.interceptors.before(() => {
      this.processDialog.show = true;
    });
    http.interceptors.after(() => {
      this.processDialog.show = false;
    });
    //通信异常结果处理
    http.interceptors.fail((bizData) => {
      if (bizData.code === "unlogin") {
        //未登陆,跳转到登陆页面
        window.location.href = "/login.html";
      } else {
        //其它请求失败处理
        this.errorDialog.msg = bizData.msg;
        this.errorDialog.show = true;
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
    selectMoreItem: function (item) {
      if(item.route) {
        this.$router.push(item.route);
      } else if(item.href) {
        window.location.href = item.href;
      }
    },
  },
};
</script>
