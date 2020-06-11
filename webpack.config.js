const {BundleAnalyzerPlugin} = require("webpack-bundle-analyzer")

const path = require("path")
const HtmlWebPackPlugin = require("html-webpack-plugin") // eslint-disable-line import/no-extraneous-dependencies

const htmlPlugin = new HtmlWebPackPlugin({
  template: path.join(__dirname, "src/main/react/index.html"),
  filename: "./index.html",
  favicon: "src/main/react/favicon.ico",
})

// TODO: remove redundant babel-loader options and replace with .babelrc (merge webpack with @flock-eco webpack)
const proxyTarget = env => ({
  target:
    env && env.proxy === "remote"
      ? "https://workday.flock.community"
      : "http://localhost:8080",
  changeOrigin: true,
  autoRewrite: true,
})

module.exports = (env, argv) => ({
  entry: path.join(__dirname, "src/main/react"),

  output: {
    publicPath: "/",
    filename: "[name].[contenthash].js",
    path: path.resolve(__dirname, "target/classes/static"),
  },

  devtool: "eval-source-map",
  module: {
    rules: [
      {
        test: /\.js|jsx$/,
        exclude: /node_modules[\\\/](?!(@flock-community)[\\\/]).*/,
        use: {
          loader: "babel-loader",
          options: {
            plugins: [
              "@babel/plugin-proposal-class-properties",
              "@babel/plugin-transform-runtime",
            ],
            presets: ["@babel/preset-env", "@babel/preset-react"],
          },
        },
      },
    ],
  },

  plugins: [
    htmlPlugin,
    argv.mode === 'development' ? new BundleAnalyzerPlugin() : null,
  ].filter(it => it !== null),

  devServer: {
    port: 3000,
    historyApiFallback: true,
    proxy: {
      "/api/**": proxyTarget(env),
      "/oauth2/**": proxyTarget(env),
      "/login/**": proxyTarget(env),
      "/login": proxyTarget(env),
      "/logout": proxyTarget(env),
    },
  },
})
