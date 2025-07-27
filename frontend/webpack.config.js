const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = {
  mode: 'development',
  entry: './src/index.js',
  output: {
    filename: 'main.js',
    path: path.resolve(__dirname, 'build/www'),
  },
  plugins: [new HtmlWebpackPlugin({
    filename: 'chat.html',
    template: 'src/chat.html',
    publicPath: '/static'
  })],
};