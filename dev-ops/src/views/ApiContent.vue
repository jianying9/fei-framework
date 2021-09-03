<template>
  <div>
    <v-toolbar color="#bbe6d6" flat class="mb-2">
      <v-toolbar-title>接口详情</v-toolbar-title>
      <v-spacer></v-spacer>
      <v-btn class="ma-2" color="success" @click="goBack()">返回</v-btn>
    </v-toolbar>
    <v-card max-width="640" class="mx-auto">
      <v-card-title>基本信息</v-card-title>
      <v-card-text>
        <span>{{ router.route }}</span>
        <v-chip class="ma-2">{{ router.group }}</v-chip>
        <v-chip v-if="router.auth" class="ma-2" color="primary">鉴权</v-chip>
      </v-card-text>
      <v-card-text>{{ router.description }}</v-card-text>
      <v-divider class="mx-2"></v-divider>
      <v-card-title>请求参数</v-card-title>
      <v-simple-table class="ma-2">
        <template v-slot:default>
          <thead>
            <tr>
              <th class="text-left">参数名</th>
              <th class="text-left">必填</th>
              <th class="text-left">类型</th>
              <th class="text-left">描述</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in router.requestArray" :key="item.name">
              <td>{{ item.name }}</td>
              <td>{{ item.required }}</td>
              <td>{{ item.type }}</td>
              <td>{{ item.description }}</td>
            </tr>
          </tbody>
        </template>
      </v-simple-table>
      <v-divider class="mx-2"></v-divider>
      <v-card-title>响应参数</v-card-title>
      <v-simple-table class="ma-2">
        <template v-slot:default>
          <thead>
            <tr>
              <th class="text-left">参数名</th>
              <th class="text-left">类型</th>
              <th class="text-left">描述</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in router.responseArray" :key="item.name">
              <td>{{ item.name }}</td>
              <td>{{ item.type }}</td>
              <td>{{ item.description }}</td>
            </tr>
          </tbody>
        </template>
      </v-simple-table>
      <v-divider class="mx-2"></v-divider>
      <v-card-title>测试</v-card-title>
      <v-form ref="dataForm" class="pa-4">
        <v-text-field v-model="form.data.url" label="url"></v-text-field>
        <v-select
          :items="form.contentTypeArray"
          v-model="form.data.contentType"
          label="Content-Type"
        ></v-select>
        <v-textarea class="text-body-2" v-model="form.data.request" label="Request" rows="8" dense outlined></v-textarea>
        <v-textarea class="text-body-2" v-model="form.data.response" label="Response" rows="8" dense outlined></v-textarea>
        <v-btn color="success" class="mr-4" @click="test()"> 提交 </v-btn>
      </v-form>
    </v-card>
  </div>
</template>

<script>
import axios from "axios";
const apiInstance = axios.create({
  baseURL: "http://localhost:8091/demo-app/api",
});
export default {
  name: "apiContent",
  data: () => ({
    router: {
      route: "",
      group: "",
      auth: false,
      description: "",
      requestArray: [],
      responseArray: [],
    },
    form: {
      data: {
        url: "",
        contentType: "application/json",
        request: "",
        response: "",
      },
      contentTypeArray: ["application/json"],
    },
  }),
  mounted: function () {
    this.router.route = this.$route.params.route.replaceAll("_", "/");
    this.form.data.url = apiInstance.defaults.baseURL + this.router.route;
    apiInstance({
      url: this.router.route,
      method: "post",
      data: {
        _api: true,
      },
    }).then((response) => {
      this.router = response.data;
      this.createRequestJson();
    });
  },
  methods: {
    goBack: function () {
      this.$router.go(-1);
    },
    test: function () {
      let request = JSON.parse(this.form.data.request);
      apiInstance({
        url: this.router.route,
        method: "post",
        data: request,
      }).then((response) => {
        this.form.data.response = JSON.stringify(response.data, null, 4);
      });
    },
    createRequestJson: function () {
      let requestJson = {};
      this.router.requestArray.forEach((param) => {
        if (param.type === "object") {
          requestJson[param.name] = {};
        } else if (param.type.indexOf("array") >= 0) {
          requestJson[param.name] = [];
        } else {
          let paramArray = param.name.split(".");
          if (paramArray.length > 1) {
            this.createSubParam(requestJson, paramArray, param.type);
          } else {
            if (param.type === "boolean") {
              requestJson[param.name] = false;
            } else if (param.type.indexOf("string") >= 0) {
              requestJson[param.name] = "";
            } else if (param.type.indexOf("regex") >= 0) {
              requestJson[param.name] = "";
            } else {
              requestJson[param.name] = 0;
            }
          }
        }
      });
      this.form.data.request = JSON.stringify(requestJson, null, 4);
    },
    createSubParam: function (parent, paramArray, type) {
      let child;
      for (let param of paramArray) {
        child = parent[param];
        if (child instanceof Array) {
          //array
          if (child.length > 0) {
            parent = child[0];
          } else {
            parent = {};
            child.push(parent);
          }
        } else if (child instanceof Object) {
          //object
          parent = child;
        } else {
          if (type === "boolean") {
            parent[param] = false;
          } else if (type.indexOf("string") >= 0) {
            parent[param] = "";
          } else if (type.indexOf("regex") >= 0) {
            parent[param] = "";
          } else {
            parent[param] = 0;
          }
        }
      }
    },
  },
};
</script>
