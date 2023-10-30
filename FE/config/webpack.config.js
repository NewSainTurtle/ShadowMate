const path = require("path");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const { CleanWebpackPlugin } = require("clean-webpack-plugin");
const TsconfigPathsPlugin = require("tsconfig-paths-webpack-plugin");
const DotenvWebpack = require("dotenv-webpack");
const tsConfigPath = path.resolve(__dirname, "../tsconfig.json");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const isDevMode = process.env.NODE_ENV !== "production";

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
        exclude: "/node_modules/",
        use: ["babel-loader", "ts-loader"],
      },
      {
        test: /\.(png|jpe?g|gif)$/,
        use: [
          {
            loader: "url-loader",
            options: {
              limit: 8192,
            },
          },
        ],
      },
      {
        test: /\.svg$/,
        use: [
          {
            loader: "file-loader",
            options: {
              name: "Imgs/[name].[ext]?[hash]",
            },
          },
        ],
      },
      {
        test: /\.(sa|sc|c)ss$/,
        exclude: "/node_modules/",
        use: [
          isDevMode ? "style-loader" : MiniCssExtractPlugin.loader,
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
    filename: "[name].[contenthash].js",
  },
  plugins: [
    new CleanWebpackPlugin(),
    new HtmlWebpackPlugin({
      favicon: "./public/favicon.svg",
      template: "./public/index.html",
      filename: "index.html",
    }),
    new MiniCssExtractPlugin({
      filename: "assets/css/[name].[contenthash:8].css",
      chunkFilename: "assets/css/[name].[contenthash:8].chunk.css",
    }),
    new DotenvWebpack(),
  ],
  stats: {
    loggingDebug: ["sass-loader"],
  },
};
