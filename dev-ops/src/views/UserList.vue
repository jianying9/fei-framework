<template>
  <div>
    <v-toolbar color="#bbe6d6" flat class="mb-2">
      <v-toolbar-title>用户</v-toolbar-title>
      <v-spacer></v-spacer>
      <v-btn class="ma-2" color="success" @click="toUserAdd()">新增</v-btn>
    </v-toolbar>
    <v-card v-show="userArray.length>0" max-width="640" class="mx-auto">
      <v-list>
        <template v-for="(item, index) in userArray">
          <v-list-item :key="item.username">
            <v-badge
              v-if="item.isAdmin"
              icon="mdi-account-cog"
              color="deep-purple"
              overlap
              class="mr-4"
            >
              <v-avatar color="teal" size="36">
                <span class="white--text text-h5">{{
                  item.name | firstLetterFilter
                }}</span>
              </v-avatar>
            </v-badge>
            <v-avatar v-else color="teal" size="36" class="mr-4">
              <span class="white--text text-h5">{{
                item.name | firstLetterFilter
              }}</span>
            </v-avatar>
            <v-list-item-content>
              <v-list-item-title>{{ item.name }}</v-list-item-title>
              <v-list-item-subtitle>{{ item.username }} - {{item.email}}</v-list-item-subtitle>
            </v-list-item-content>
          </v-list-item>
          <v-divider
            v-if="index != userArray.length - 1"
            :key="index"
          ></v-divider>
        </template>
      </v-list>
    </v-card>
  </div>
</template>

<script>
import global from "../assets/js/global.js";
export default {
  name: "userList",
  data: () => ({
    userArray: [],
  }),
  mounted: function () {
    this.$http.post(global.api.user_search, {}).then((bizData) => {
      this.userArray = bizData.data.userArray;
    });
  },
  methods: {
    toUserAdd: function () {
      this.$router.push("/user/add");
    },
  },
};
</script>
