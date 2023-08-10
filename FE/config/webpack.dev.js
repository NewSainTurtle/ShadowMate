const { merge } = require("webpack-merge");
const common = require("./webpack.config.js");

module.exports = merge(common, {
  mode: "development", // 현재 개발 모드
  devtool: "eval", // 최대성능, 개발환경에 추천
  devServer: {
    historyApiFallback: true,
    open: true,
    hot: true,
    port: 3000,
    hot: true,
  },
  watchOptions: {
    poll: true,
    ignored: "/node_modules/",
  },
});
