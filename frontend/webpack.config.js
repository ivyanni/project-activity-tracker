var packageJSON = require('./package.json');
var path = require('path');
var webpack = require('webpack');

const PATHS = {
    build: path.join(__dirname, 'target', 'classes', 'META-INF', 'resources', 'webjars', packageJSON.name, packageJSON.version)
};

module.exports = {
    entry: ['babel-polyfill', './app/index.js'],
    devtool: 'cheap-module-inline-source-map',

    devServer: {
        port: 9000,
        historyApiFallback: false
    },

    module: {
        loaders: [
            {
                loader: "babel-loader",
                include: [
                    path.resolve(__dirname, "app")
                ],
                exclude: [
                    path.resolve(__dirname, "node_modules")
                ],
                test: /\.jsx?$/,
                query: {
                    presets: ['env', 'react']
                }
            }
        ]
    },

    output: {
        path: PATHS.build,
        filename: 'bundle.js'
    }
};