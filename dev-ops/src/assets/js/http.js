import axios from 'axios';
import global from './global.js';

//通信基本配置
const instance = axios.create({
    baseURL: global.baseURL
});

//
const http = {
    interceptors: {
        _fail: function () {
        },
        fail: function (func) {
            this._fail = func;
        },
        _before: function () {
        },
        before: function (func) {
            this._before = func;
        },
        _after: function () {
        },
        after: function (func) {
            this._after = func;
        }
    },
    post: async function (url, data) {
        //开始通信
        this.interceptors._before(url, data);
        let response = await instance({
            url: url,
            method: 'post',
            headers: {
                'Authorization': global.getAuth()
            },
            data: data
        });
        let bizData = response.data;
        if (bizData.code === 'expired') {
            //刷新auth
            const refreshTokenResponse = await instance({
                url: global.api.account_refresh,
                method: 'post',
                data: {
                    refreshToken: global.getRefreshToken()
                }
            });
            bizData = refreshTokenResponse.data;
            if (bizData.code === 'success') {
                //刷新成功,更新auth
                global.setAuth(bizData.data.auth);
                //重新获取业务
                response = await instance({
                    url: url,
                    method: 'post',
                    headers: {
                        'Authorization': global.getAuth()
                    },
                    data: data
                });
                bizData = response.data;
            } else {
                bizData = {
                    route: global.api.account_refresh,
                    code: 'unlogin',
                    msg: ''
                };
            }
        }
        //通信结束
        this.interceptors._after(url, data);
        //业务结果
        if (bizData.code === 'success') {
            //成功
            if (bizData.route === global.api.account_loginByGitlab) {
                //登录成功,保存auth和refreshToken
                global.setAuth(bizData.data.auth);
                global.setRefreshToken(bizData.data.refreshToken);
            }
        } else {
            //请求失败处理
            this.interceptors._fail(bizData);
        }

        return bizData;
    }
};

export default http;

