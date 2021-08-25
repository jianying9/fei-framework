<template>
  <div>
    <v-toolbar color="#bbe6d6" flat class="mb-2">
      <v-toolbar-title>新增节点</v-toolbar-title>
      <v-spacer></v-spacer>
    </v-toolbar>
    <v-card max-width="640" class="mx-auto">
      <v-form ref="dataForm" v-model="form.valid" class="pa-4">
        <v-select
          v-model="form.data.branch"
          :rules="form.rules.branchRules"
          label="环境分支"
          chips
          :items="branchArray"
          item-text="name"
          item-value="value"
        >
        </v-select>
        <v-text-field
          v-model="form.data.name"
          :rules="form.rules.nameRules"
          label="名称"
        ></v-text-field>
        <v-text-field
          v-model="form.data.hostname"
          :rules="form.rules.hostnameRules"
          label="地址"
        ></v-text-field>
        <v-textarea v-model="form.data.description" label="描述"></v-textarea>
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
  name: "nodeAdd",
  data: () => ({
    branchArray: [{
      name: "正式环境",
      value: "main"
    },{
      name: "开发环境",
      value: "dev"
    }],
    form: {
      valid: false,
      canSubmit: true,
      data: {
        name: "",
        ip: "",
        description: "",
        branch: ""
      },
      rules: {
        nameRules: [
          (v) => !!v || "name is required"
        ],
        hostnameRules: [
          (v) => !!v || "hostname is required",
          (v) => !/[^\d.]/g.test(v) || "hostname must [255.255.255.255]",
        ],
        branchRules: [
          (v) => !!v || "branch is required"
        ],
        descriptionRules: [
          (v) => !!v || "description is required"
        ],
      },
    },
  }),
  mounted: function () {
  },
  methods: {
    validate: function () {
      var pass = this.$refs.dataForm.validate();
      if (pass) {
        this.$http
          .post(global.api.node_add, this.form.data)
          .then((bizData) => {
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
