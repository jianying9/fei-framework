<template>
  <div>
    <v-toolbar color="#bbe6d6" flat class="mb-2">
      <v-toolbar-title>新增群组</v-toolbar-title>
      <v-spacer></v-spacer>
    </v-toolbar>
    <v-card max-width="640" class="mx-auto">
      <v-form ref="dataForm" v-model="form.valid" class="pa-4">
        <v-text-field
          v-model="form.data.name"
          :rules="form.rules.nameRules"
          label="名称"
        ></v-text-field>
        <v-textarea
          v-model="form.data.description"
          label="描述"
        ></v-textarea>
        <v-btn
          v-show="form.canSubmit"
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
  name: "groupAdd",
  data: () => ({
    form: {
      valid: false,
      canSubmit: true,
      data: {
        name: "",
        description: "",
      },
      rules: {
        nameRules: [(v) => !!v || "name is required", (v) => !/[^a-z0-9_]/g.test(v) || "name only use character [a-z0-9_]"],
      },
    },
  }),
  mounted: function () {},
  methods: {
    validate: function () {
      var pass = this.$refs.dataForm.validate();
      if (pass) {
        this.$http.post(global.api.group_add, this.form.data).then((bizData) => {
          if (bizData.code == "success") {
            this.form.canSubmit = false;
            this.goBack();
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
