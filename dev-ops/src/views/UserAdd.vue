<template>
  <div>
    <v-toolbar color="#bbe6d6" flat class="mb-2">
      <v-toolbar-title>新增用户</v-toolbar-title>
      <v-spacer></v-spacer>
    </v-toolbar>
    <v-card max-width="640" class="mx-auto">
      <v-form ref="dataForm" v-model="form.valid" :readonly="form.readonly" class="pa-4">
        <v-text-field
          v-model="form.data.name"
          :rules="form.rules.nameRules"
          label="名称"
        ></v-text-field>
        <v-text-field
          v-model="form.data.username"
          :rules="form.rules.usernameRules"
          label="账号"
        ></v-text-field>
        <v-text-field
          v-model="form.data.email"
          :rules="form.rules.emailRules"
          label="邮箱"
        ></v-text-field>
        <v-alert
          v-model="alert.show"
          border="bottom"
          colored-border
          type="warning"
          elevation="2"
        >
          {{ alert.msg }}
        </v-alert>
        <v-btn
          v-show="alert.show == false"
          :disabled="!form.valid"
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
import global from "../assets/js/global.js";
export default {
  name: "userAdd",
  data: () => ({
    form: {
      valid: false,
      readonly: false,
      data: {
        name: "",
        username: "",
        email: "",
      },
      rules: {
        nameRules: [(v) => !!v || "name is required"],
        usernameRules: [(v) => !!v || "username is required"],
        emailRules: [(v) => !!v || "email is required"],
      },
    },
    alert: {
      show: false,
      msg: "",
    },
  }),
  mounted: function () {},
  methods: {
    validate: function () {
      var pass = this.$refs.dataForm.validate();
      if (pass) {
        this.$http.post(global.api.user_add, this.form.data).then((bizData) => {
          if (bizData.code == "success") {
            this.form.readonly = true;
            this.alert.msg = "(只显示一次)密码:" + bizData.data.password;
            this.alert.show = true;
          }
        });
      }
    },
    goBack: function () {
      this.$router.go(-1);
    },
  },
};
</script>
