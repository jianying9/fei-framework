<template>
  <v-app>
    <v-navigation-drawer v-model="drawer" app> </v-navigation-drawer>
    <v-app-bar app color="primary" dark>
      <v-app-bar-nav-icon @click="drawer = !drawer"></v-app-bar-nav-icon>
      <v-toolbar-title>Devops</v-toolbar-title>
      <v-spacer></v-spacer>
      <v-avatar v-show="isLogin" color="accent" size="56">{{
        userName
      }}</v-avatar>
      <v-btn v-show="isLogin" icon>
        <v-icon>mdi-dots-vertical</v-icon>
      </v-btn>
    </v-app-bar>
    <v-main>
      <v-fade-transition>
        <router-view></router-view>
      </v-fade-transition>
    </v-main>
  </v-app>
</template>

<script>
import global from './assets/js/global.js';
export default {
  name: "App",
  data: () => ({
    drawer: false,
    isLogin: false,
    userId: "",
    userName: "",
  }),
  mounted: function () {
    this.$http.post(global.api.account_get, {}).then((data) => {
      this.userId = data.userId;
      this.userName = data.userName;
      this.isLogin = true;
    });
  },
  methods: {
    toLogin: function () {
      window.location.href = '/login.html';
    },
  },
};
</script>
