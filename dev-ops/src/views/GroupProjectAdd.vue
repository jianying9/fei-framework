<template>
  <div>
    <v-toolbar color="#bbe6d6" flat class="mb-2">
      <v-toolbar-title>新增群组项目:{{ path }}</v-toolbar-title>
      <v-spacer></v-spacer>
    </v-toolbar>
    <v-card max-width="640" class="mx-auto">
      <v-form ref="dataForm" v-model="form.valid" class="pa-4">
        <v-autocomplete
          v-model="form.data.templateId"
          :rules="form.rules.templateIdRules"
          label="模板"
          chips
          :items="templateArray"
          item-text="name"
          item-value="id"
        >
          <template v-slot:selection="data">
            <v-chip
              v-bind="data.attrs"
              :input-value="data.selected"
              close
              @click="data.select"
              @click:close="remove(data.item)"
            >
              {{ data.item.name }}
            </v-chip>
          </template>
          <template v-slot:item="data">
            <template>
              <v-list-item-content>
                <v-list-item-title>{{ data.item.name }}</v-list-item-title>
                <v-list-item-subtitle>{{
                  data.item.description
                }}</v-list-item-subtitle>
              </v-list-item-content>
            </template>
          </template>
        </v-autocomplete>
        <v-text-field
          v-model="form.data.name"
          :rules="form.rules.nameRules"
          label="名称"
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
  name: "groupProjectAdd",
  data: () => ({
    templateArray: [],
    path: "",
    form: {
      valid: false,
      canSubmit: true,
      data: {
        name: "",
        description: "",
        id: "",
        templateId: ""
      },
      rules: {
        nameRules: [
          (v) => !!v || "name is required",
          (v) => !/[^a-z0-9-]/g.test(v) || "name only use character [a-z0-9-]",
        ],
        templateIdRules: [
          (v) => !!v || "templateId is required"
        ],
      },
    },
  }),
  mounted: function () {
    this.form.data.id = this.$route.params.id;
    this.path = this.$route.params.path;
    this.$http
      .post(global.api.group_get, {
        id: this.$route.params.id,
      })
      .then((bizData) => {
        this.path = bizData.data.path;
      });
    this.$http.post(global.api.project_template_search, {}).then((bizData) => {
      this.templateArray = bizData.data.templateArray;
    });
  },
  methods: {
    remove() {
      this.form.data.templateId = "";
    },
    validate: function () {
      var pass = this.$refs.dataForm.validate();
      if (pass) {
        this.$http
          .post(global.api.group_project_add, this.form.data)
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
