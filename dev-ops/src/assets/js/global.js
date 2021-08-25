
if (!window.localStorage) {
    window.localStorage = {};
}

const storage = window.localStorage;

const api = {
    //账号
    account_loginByGitlab: '/account/loginByGitlab',
    account_get: '/account/get',
    account_refresh: '/account/refresh',
    //用户
    user_search: '/user/search',
    user_get: '/user/get',
    user_add: '/user/add',
    user_current: '/user/current',
    //app
    app_search: '/app/search',
    app_get: '/app/get',
    app_add: '/app/add',
    //群组
    group_search: '/group/search',
    group_get: '/group/get',
    group_detail: '/group/detail',
    group_add: '/group/add',
    group_member_add: '/group/member/add',
    group_project_add: '/group/project/add',
    //项目
    project_template_search: '/project/template/search',
    //节点
    node_search: '/node/search',
    node_add: '/node/add',
    node_updatePublishOverSSH: '/node/updatePublishOverSSH',
};

const global = {
    baseURL: 'http://localhost:8090/devops/api',
    api: api,
    setAuth: function (value) {
        storage.auth = value;
    },
    getAuth: function () {
        return storage.auth;
    },
    setRefreshToken: function (value) {
        storage.refreshToken = value;
    },
    getRefreshToken: function () {
        return storage.refreshToken;
    }
};

export default global;

