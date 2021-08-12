module.exports = {
    productionSourceMap: false,
    transpileDependencies: [
        'vuetify'
    ],
    pages: {
        index: {
            entry: 'src/main.js',
            template: 'public/index.html'
        },
        login: {
            entry: 'src/login.main.js',
            template: 'public/login.html'
        }
    }
};
