<template>
  <v-app>
    <v-app-bar app color="primary" dark>
      <v-toolbar-title>Devops</v-toolbar-title>
    </v-app-bar>
    <v-main>
      <v-card class="mx-auto my-12" max-width="768">
        <v-card-title>用户登录</v-card-title>
        <v-card-text>
          <v-form ref="loginForm" v-model="valid" lazy-validation>
            <v-text-field
              v-model="account"
              :rules="accountRules"
              label="账号"
            ></v-text-field>
            <v-text-field
              v-model="password"
              :rules="passwordRules"
              label="密码"
            ></v-text-field>
            <v-btn
              :disabled="!valid"
              color="success"
              class="mr-4"
              @click="validate"
            >
              登录
            </v-btn>
            <v-btn color="error" class="mr-4" @click="goBack">返回</v-btn>
          </v-form>
        </v-card-text>
      </v-card>
    </v-main>
  </v-app>
</template>

<script>
import global from "./assets/js/global.js";
import MD5 from "md5.js";

export default {
  name: "LoginApp",
  data: () => ({
    valid: false,
    account: "",
    accountRules: [(v) => !!v || "Account is required"],
    password: "",
    passwordRules: [(v) => !!v || "Password is required"],
  }),
  methods: {
    validate: function () {
      var pass = this.$refs.loginForm.validate();
      if (pass) {
        var pwd = new MD5().update(this.password).digest("hex");
        this.$http
          .post(global.api.account_login, {
            account: this.account,
            password: pwd,
          })
          .then(() => {
            window.location.href = "/";
          });
      }
    },
    goBack: function () {
      window.location.href = "/";
    },
  },
};
</script>
