<template>
  <div>
    <v-toolbar color="#bbe6d6" flat class="mb-2">
      <v-toolbar-title>节点</v-toolbar-title>
      <v-spacer></v-spacer>
      <v-btn class="ma-2" color="success" @click="toNodeAdd()">新增</v-btn>
      <v-btn class="ma-2" color="success" @click="updatePublishOverSSH()">更新POSSH</v-btn>
    </v-toolbar>
    <v-card v-show="nodeArray.length > 0" max-width="640" class="mx-auto">
      <v-list>
        <template v-for="(item, index) in nodeArray">
          <v-list-item :key="item.id">
            <v-avatar tile color="blue" size="36" class="mr-4">
              <span class="white--text text-h5">{{
                item.name | firstLetterFilter
              }}</span>
            </v-avatar>
            <v-list-item-content>
              <v-list-item-title
                >{{ item.name }}-{{ item.hostname }}</v-list-item-title
              >
              <v-list-item-subtitle
                >{{ item.branch }}-{{ item.description }}</v-list-item-subtitle
              >
            </v-list-item-content>
          </v-list-item>
          <v-divider
            v-if="index != nodeArray.length - 1"
            :key="index"
          ></v-divider>
        </template>
      </v-list>
    </v-card>
    <v-dialog v-model="dialog.show" max-width="300">
      <v-alert
        style="margin: 0"
        border="left"
        colored-border
        type="error"
        elevation="2"
      >
        {{ dialog.msg }}
      </v-alert>
    </v-dialog>
  </div>
</template>

<script>
import global from "../assets/js/global.js";
export default {
  name: "nodeList",
  data: () => ({
    nodeArray: [],
    dialog: {
      show: false,
      msg: ''
    }
  }),
  mounted: function () {
    this.$http.post(global.api.node_search, {}).then((bizData) => {
      this.nodeArray = bizData.data.nodeArray;
    });
  },
  methods: {
    toNodeAdd: function () {
      this.$router.push("/node/add");
    },
    updatePublishOverSSH: function () {
      this.$http.post(global.api.node_updatePublishOverSSH, {}).then((bizData) => {
        if(bizData.code == "success") {
          this.dialog.msg = "更新成功";
          this.dialog.show = true;
        }
      });
    },
  },
};
</script>
