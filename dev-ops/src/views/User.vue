<template>
  <div>
    <v-toolbar color="#bbe6d6" flat class="mb-2">
      <v-toolbar-title>用户</v-toolbar-title>
      <v-spacer></v-spacer>
      <v-btn class="ma-2" color="success" @click="toUserAdd()">新增</v-btn>
    </v-toolbar>
    <v-card max-width="640" class="mx-auto">
      <v-simple-table>
        <template v-slot:default>
          <thead>
            <tr>
              <th class="text-left">id</th>
              <th class="text-left">昵称</th>
              <th class="text-left">账号</th>
              <th class="text-left">邮箱</th>
              <th class="text-left">是否管理员</th>
              <th class="text-left">状态</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in userArray" :key="item.userId">
              <td>{{ item.id }}</td>
              <td>{{ item.name }}</td>
              <td>{{ item.username }}</td>
              <td>{{ item.email }}</td>
              <td>{{ item.isAdmin }}</td>
              <td>{{ item.state }}</td>
            </tr>
          </tbody>
        </template>
      </v-simple-table>
    </v-card>
  </div>
</template>

<script>
import global from "../assets/js/global.js";
export default {
  name: "User",
  data: () => ({
    userArray: [],
  }),
  mounted: function () {
    this.$http.post(global.api.user_search, {}).then((bizData) => {
      this.userArray = bizData.data.userArray;
    });
  },
  methods: {
    toUserAdd: function() {
      this.$router.push('/user/add');
    }
  },
};
</script>
