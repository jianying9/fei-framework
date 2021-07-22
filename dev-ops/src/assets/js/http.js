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
        use: function (func) {
            this._fail = func;
        }
    },
    post: async function (url, data) {
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
            }
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
        } else if (bizData.code === 'success') {
            if (bizData.route === global.api.account_login) {
                //登录成功,保存auth和refreshToken
                global.setAuth(bizData.data.auth);
                global.setRefreshToken(bizData.data.refreshToken);
            }
        } else {
            //请求失败处理
            this.interceptors._fail(bizData);
            //中断原Promise后续执行
            return new Promise(()=>{});
        }
        return bizData.data;
    }
};

export default http;

