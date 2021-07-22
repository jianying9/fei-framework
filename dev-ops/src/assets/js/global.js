
if (!window.localStorage) {
    window.localStorage = {};
}

const storage = window.localStorage;

const api = {
    account_login: '/account/login',
    account_get: '/account/get',
    account_refresh: '/account/refresh',
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

