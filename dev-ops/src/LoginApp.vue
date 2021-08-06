<template>
  <v-app>
    <v-app-bar app color="primary" dark>
      <v-toolbar-title>Devops</v-toolbar-title>
    </v-app-bar>
    <v-main>
      <v-card class="mx-auto my-12" max-width="768">
        <v-card-title>用户登录</v-card-title>
        <v-card-text v-if="isGitlabCheck == false">
          <v-form>
            <v-btn color="success" class="mr-4" @click="toGitlab">
              gitlab登录
            </v-btn>
            <v-btn color="error" class="mr-4" @click="goBack">返回</v-btn>
          </v-form>
        </v-card-text>
        <v-card-text v-else> 登录验证中... </v-card-text>
      </v-card>
    </v-main>
  </v-app>
</template>

<script>
import global from "./assets/js/global.js";

export default {
  name: "LoginApp",
  data: () => ({
    clientId:
      "b62eca585678d5f089e034dea04c8c2cc8d50c02f7bd627c67ea35c1252bb4ed",
    redirectUri: "",
    isGitlabCheck: false,
  }),
  mounted: function () {
    this.redirectUri =
      window.location.protocol +
      "//" +
      window.location.host +
      window.location.pathname;
    let params = new URLSearchParams(window.location.search);
    let code = params.get("code");
    if (code) {
      this.isGitlabCheck = true;
      this.$http
        .post(global.api.account_loginByGitlab, {
          code: code,
          redirectUri: this.redirectUri,
        })
        .then((bizData) => {
          if(bizData.code == 'success') {
            window.location.href = "/";
          }
        });
    }
  },
  methods: {
    toGitlab: function () {
      let gitlabUrl =
        "https://a.zlw333.com/gitlab/oauth/authorize?" +
        "client_id=" +
        this.clientId +
        "&redirect_uri=" +
        this.redirectUri +
        "&response_type=code" +
        "&state=devops_gitlab" +
        "&scope=api+write_repository" +
        "";
      window.location.href = gitlabUrl;
    },
    goBack: function () {
      window.location.href = "/";
    },
  },
};
</script>
