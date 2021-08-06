<template>
  <div>
    <v-toolbar color="#bbe6d6" flat class="mb-2">
      <v-toolbar-title>新增用户</v-toolbar-title>
      <v-spacer></v-spacer>
    </v-toolbar>
    <v-card max-width="640" class="mx-auto">
      <v-form ref="dataForm" v-model="valid" lazy-validation class="pa-4">
        <v-text-field
          v-model="userName"
          :rules="userNameRules"
          label="用户"
        ></v-text-field>
        <v-text-field
          v-model="userName"
          :rules="accountRules"
          label="账号"
        ></v-text-field>
        <v-switch
          color="success"
          v-model="admin"
          label="管理员"
        ></v-switch>
        <v-btn
          :disabled="!valid"
          color="success"
          class="mr-4"
          @click="validate"
        >
          保存
        </v-btn>
        <v-btn color="error" class="mr-4" @click="goBack">返回</v-btn>
      </v-form>
    </v-card>
  </div>
</template>

<script>
// import global from "../assets/js/global.js";
export default {
  name: "User",
  data: () => ({
    admin: false,
    valid: false,
    userName: "",
    userNameRules: [(v) => !!v || "userName is required"],
    account: "",
    accountRules: [(v) => !!v || "Account is required"],
    password: "",
    passwordRules: [(v) => !!v || "Password is required"],
  }),
  mounted: function () {
  },
  methods: {
    validate: function () {
      var pass = this.$refs.dataForm.validate();
      if (pass) {
        console.log(pass);
      }
    },
    goBack: function () {
      this.$router.go(-1);
    },
  },
};
</script>
