<template>
  <div>
    <v-toolbar color="#bbe6d6" flat class="mb-2">
      <v-toolbar-title>首页</v-toolbar-title>
    </v-toolbar>
    <v-card max-width="640" class="mx-auto">
      <v-list three-line>
        <v-subheader>应用列表</v-subheader>
        <template v-for="(item, index) in appArray">
          <v-divider :key="index"></v-divider>
          <v-list-item :key="item.appName">
            <v-list-item-avatar>
              <v-icon dark class="blue">mdi-application</v-icon>
            </v-list-item-avatar>
            <v-list-item-content>
              <v-list-item-title v-html="item.appName"></v-list-item-title>
              <v-list-item-subtitle
                v-html="item.appDesc"
              ></v-list-item-subtitle>
            </v-list-item-content>
          </v-list-item>
        </template>
      </v-list>
    </v-card>
  </div>
</template>

<script>
import global from "../assets/js/global.js";
export default {
  name: "Home",
  data: () => ({
    appArray: [],
  }),
  mounted: function () {
    this.$http.post(global.api.app_search, {}).then((bizData) => {
      this.appArray = bizData.data.appArray;
    });
  },
  methods: {},
};
</script>
