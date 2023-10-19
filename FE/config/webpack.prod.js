const { merge } = require("webpack-merge");
const common = require("./webpack.config.js");
const CssMinimizerPlugin = require("css-minimizer-webpack-plugin");
const TerserPlugin = require("terser-webpack-plugin");
const { BundleAnalyzerPlugin } = require("webpack-bundle-analyzer");
const HtmlMinimizerPlugin = require("html-minimizer-webpack-plugin");
const isAnalyze = process.argv.includes("--analyze");

module.exports = merge(common, {
  mode: "production",
  devtool: "hidden-source-map",
  devServer: {
    //port: 8080,
    disableHostCheck: true,
    historyApiFallback: true,
    allowedHosts: "auto",
  },
  performance: {
    hints: false,
    maxEntrypointSize: 512000,
    maxAssetSize: 512000,
  },
  optimization: {
    minimizer: [
      new CssMinimizerPlugin(),
      new TerserPlugin({
        terserOptions: {
          compress: {
            drop_console: true,
          },
        },
      }),
      new HtmlMinimizerPlugin(),
    ],
  },
  plugins: [...(isAnalyze ? [new BundleAnalyzerPlugin()] : [])],
});
