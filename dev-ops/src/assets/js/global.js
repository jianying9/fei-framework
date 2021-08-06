
if (!window.localStorage) {
    window.localStorage = {};
}

const storage = window.localStorage;

const api = {
    //账号
    account_loginByGitlab: '/account/loginByGitlab',
    account_get: '/account/get',
    account_refresh: '/account/refresh',
    //
    user_search: '/user/search',
    user_get: '/user/get',
    user_add: '/user/add',
    user_current: '/user/current',
    //app
    app_search: '/app/search',
    app_get: '/app/get',
    app_add: '/app/add',

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

