<template>
  <div>
    <v-toolbar color="#bbe6d6" flat class="mb-2">
      <v-toolbar-title>新增群组成员:{{path}}</v-toolbar-title>
      <v-spacer></v-spacer>
    </v-toolbar>
    <v-card max-width="640" class="mx-auto">
      <v-form ref="dataForm" v-model="form.valid" class="pa-4">
        <v-autocomplete
          v-model="form.data.userIdArray"
          :rules="form.rules.userIdArrayRules"
          label="成员"
          chips
          :items="userArray"
          item-text="name"
          item-value="id"
          multiple
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
                  data.item.email
                }}</v-list-item-subtitle>
              </v-list-item-content>
            </template>
          </template>
        </v-autocomplete>
        <v-select
          v-model="form.data.accessLevel"
          :rules="form.rules.accessLevelRules"
          :items="accessLevelArray"
          item-text="value"
          item-value="key"
          label="权限"
        ></v-select>
        <v-btn
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
  name: "groupMemberAdd",
  data: () => ({
    path: "",
    userArray: [],
    accessLevelArray: [
      {
        key: 10,
        value: "Guest",
      },
      {
        key: 20,
        value: "Reporter",
      },
      {
        key: 30,
        value: "Developer",
      },
      {
        key: 40,
        value: "maintainer",
      },
      {
        key: 50,
        value: "Owner",
      },
    ],
    form: {
      valid: false,
      data: {
        userIdArray: [],
        accessLevel: "",
        id: "",
      },
      rules: {
        userIdArrayRules: [(v) => v.length > 0 || "userIdArray is required"],
        accessLevelRules: [(v) => !!v || "accessLevel is required"],
      },
    },
  }),
  mounted: function () {
    this.path = this.$route.params.path;
    this.form.data.id = this.$route.params.id;
    this.$http
      .post(global.api.group_get, {
        id: this.$route.params.id,
      })
      .then((bizData) => {
        this.path = bizData.data.path;
      });
    //获取所有用户
    this.$http.post(global.api.user_search, {}).then((bizData) => {
      this.userArray = bizData.data.userArray;
    });
  },
  methods: {
    remove(item) {
      const index = this.form.data.userIdArray.indexOf(item.id);
      if (index >= 0) this.form.data.userIdArray.splice(index, 1);
    },
    validate: function () {
      var pass = this.$refs.dataForm.validate();
      if (pass) {
        this.$http
          .post(global.api.group_member_add, this.form.data)
          .then((bizData) => {
            if (bizData.code == "success") {
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
