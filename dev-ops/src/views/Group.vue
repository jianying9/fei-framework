<template>
  <div>
    <v-toolbar color="#bbe6d6" flat class="mb-2">
      <v-toolbar-title>群组:{{ group.path }}</v-toolbar-title>
      <v-spacer></v-spacer>
    </v-toolbar>
    <v-card max-width="640" class="mx-auto">
      <v-card-title>群组信息</v-card-title>
      <v-divider></v-divider>
      <v-card-text>
        <span>名称:</span
        ><span class="font-weight-black">{{ group.name }}</span>
      </v-card-text>
      <v-divider></v-divider>
      <v-card-text>
        <span>路径:</span
        ><span class="font-weight-black">{{ group.path }}</span>
      </v-card-text>
      <v-divider></v-divider>
      <v-card-text>
        <span>描述:</span
        ><span class="font-weight-black">{{ group.description }}</span>
      </v-card-text>
      <v-divider></v-divider>
      <v-card-text>
        <span>可见性级别:</span
        ><span class="font-weight-black">{{
          group.visibility | visibilityFilter
        }}</span>
      </v-card-text>
      <v-divider></v-divider>
      <v-card-text>
        <span>ID:</span><span class="font-weight-black">{{ group.id }}</span>
      </v-card-text>
    </v-card>

    <v-card
      v-show="group.memberArray.length > 0"
      max-width="640"
      class="mx-auto mt-4"
    >
      <v-card-title>
        <span>成员信息</span>
        <v-spacer></v-spacer>
        <v-btn class="ma-2" color="success" @click="toGroupMemberAdd()">新增</v-btn>
      </v-card-title>
      <v-divider></v-divider>
      <v-list>
        <template v-for="(item, index) in group.memberArray">
          <v-list-item :key="item.id">
            <v-avatar color="teal" size="36" class="mr-4">
              <span class="white--text text-h5">{{
                item.name | firstLetterFilter
              }}</span>
            </v-avatar>
            <v-list-item-content>
              <v-list-item-title>{{ item.name }}</v-list-item-title>
            </v-list-item-content>
            <v-spacer></v-spacer>
            <v-chip>{{ item.accessLevel | accessLevelFilter }}</v-chip>
          </v-list-item>
          <v-divider
            v-if="index != group.memberArray.length - 1"
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
  name: "group",
  data: () => ({
    group: {
      path: "",
      memberArray: [],
    },
  }),
  mounted: function () {
    this.group.path = this.$route.params.path;
    this.$http
      .post(global.api.group_get, {
        id: this.$route.params.id,
      })
      .then((bizData) => {
        this.group = bizData.data;
      });
  },
  methods: {
    toGroupMemberAdd: function () {
      this.$router.push({
        name: "groupMemberAdd",
        params: {
          id: this.group.id
        }
      });
    },
  },
};
</script>
