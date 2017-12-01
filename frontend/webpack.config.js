var packageJSON = require('./package.json');
var path = require('path');
var webpack = require('webpack');

const PATHS = {
    build: path.join(__dirname, 'target', 'classes', 'META-INF', 'resources', 'webjars', packageJSON.name, packageJSON.version),
    dev: path.join(__dirname, 'dist')
};

module.exports = {
    entry: './app/index.js',
    devtool: 'cheap-module-inline-source-map',

    devServer: {
        port: 9000,
        historyApiFallback: false
    },

    output: {
        path: PATHS.build,
        filename: 'bundle.js'
    }
};