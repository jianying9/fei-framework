<template>
  <div>
    <v-toolbar color="#bbe6d6" flat class="mb-2">
      <v-toolbar-title>接口</v-toolbar-title>
      <v-spacer></v-spacer>
    </v-toolbar>
    <v-card v-show="routerArray.length > 0" max-width="640" class="mx-auto">
      <v-list>
        <template v-for="(item, index) in routerArray">
          <v-list-item :key="item.route">
            <v-list-item-content>
              <v-list-item-title>
                <a @click="toApiContent(item)">{{ item.route }}</a>
                <v-chip class="ma-2">{{ item.group }}</v-chip>
                <v-chip v-if="item.auth" class="ma-2" color="primary">鉴权</v-chip>
                </v-list-item-title>
              <v-list-item-subtitle>{{ item.description }}</v-list-item-subtitle>
            </v-list-item-content>
          </v-list-item>
          <v-divider
            v-if="index != routerArray.length - 1"
            :key="index"
          ></v-divider>
        </template>
      </v-list>
    </v-card>
  </div>
</template>

<script>
import axios from "axios";
const apiInstance = axios.create({
  baseURL: "http://localhost:8091/demo-app/api",
});
export default {
  name: "apiList",
  data: () => ({
    routerArray: [],
  }),
  mounted: function () {
    apiInstance({
      url: "/",
      method: "post",
      data: {
        _api: true,
      },
    }).then((response) => {
      this.routerArray = response.data.routerArray;
    });
  },
  methods: {
    toApiContent: function (router) {
      this.$router.push({
        name: "apiContent",
        params: {
          route: router.route.replaceAll('/', '_')
        }
      });
    },
  },
};
</script>
