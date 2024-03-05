// noinspection JSUnresolvedReference

module.exports = {
  dependency: {
    platforms: {
      ios: {},
      android: { sourceDir: "android" },
    },
  },
  dependencies: {
    "react-native-screenshot-url": {
      root: __dirname,
      platforms: {
        android: {
          sourceDir: __dirname + "/android",
          packageImportPath: "import com.krmao.screenshot.ScreenShotPackage;",
          packageInstance: "new ScreenShotPackage()",
        },
      },
    },
  },
};
