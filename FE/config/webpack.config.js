const path = require("path");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const { CleanWebpackPlugin } = require("clean-webpack-plugin");
const TsconfigPathsPlugin = require("tsconfig-paths-webpack-plugin");
const DotenvWebpack = require("dotenv-webpack");
const tsConfigPath = path.resolve(__dirname, "../tsconfig.json");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const devMode = process.env.NODE_ENV !== "production";

module.exports = {
  entry: `${path.resolve(__dirname, "../src")}/index.tsx`,
  resolve: {
    extensions: [".tsx", ".ts", ".js"],
    plugins: [new TsconfigPathsPlugin({ configFile: tsConfigPath })],
  },
  module: {
    rules: [
      {
        test: /\.(ts|tsx)?$/,
        use: ["babel-loader", "ts-loader"],
      },
      {
        test: /\.(png|svg|jpe?g|gif)$/,
        use: ["file-loader"],
      },
      {
        test: /\.scss$/,
        use: [
          devMode ? "style-loader" : MiniCssExtractPlugin.loader,
          {
            loader: "css-loader",
            options: {
              importLoaders: 1,
              modules: {
                mode: "local",
              },
            },
          },
          "sass-loader",
        ],
      },
    ],
  },
  output: {
    path: path.resolve(__dirname, "../build"),
    publicPath: "/",
    filename: "[contenthash].bundle.js",
  },
  plugins: [
    new CleanWebpackPlugin(),
    new HtmlWebpackPlugin({
      template: "./public/index.html",
    }),
    new DotenvWebpack(),
  ].concat(devMode ? [] : [new MiniCssExtractPlugin()]),
  stats: {
    loggingDebug: ["sass-loader"],
  },
};
