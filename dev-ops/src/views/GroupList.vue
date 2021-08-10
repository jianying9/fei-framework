<template>
  <div>
    <v-toolbar color="#bbe6d6" flat class="mb-2">
      <v-toolbar-title>群组</v-toolbar-title>
      <v-spacer></v-spacer>
      <v-btn class="ma-2" color="success" @click="toGroupAdd()">新增</v-btn>
    </v-toolbar>
    <v-card v-show="groupArray.length>0" max-width="640" class="mx-auto">
      <v-list>
        <template v-for="(item, index) in groupArray">
          <v-list-item :key="item.id">
            <v-avatar tile color="indigo" size="36" class="mr-4">
              <span class="white--text text-h5">{{
                item.name | firstLetterFilter
              }}</span>
            </v-avatar>
            <v-list-item-content>
              <v-list-item-title @click="toGroup(item)"><a>{{ item.name }}</a></v-list-item-title>
              <v-list-item-subtitle>{{ item.description }}</v-list-item-subtitle>
            </v-list-item-content>
          </v-list-item>
          <v-divider
            v-if="index != groupArray.length - 1"
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
  name: "groupList",
  data: () => ({
    groupArray: [],
  }),
  mounted: function () {
    this.$http.post(global.api.group_search, {}).then((bizData) => {
      this.groupArray = bizData.data.groupArray;
    });
  },
  methods: {
    toGroup: function (item) {
      this.$router.push({
        name: "group",
        params: {
          path: item.path,
          id: item.id
        }
      });
    },
    toGroupAdd: function () {
      this.$router.push("/group/add");
    },
  },
};
</script>
