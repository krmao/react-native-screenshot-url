module.exports = {
  dependency: {
    platforms: {
      ios: { project: "react-native-screenshot-url.podspec" },
      android: { sourceDir: "android" },
    },
  },
  dependencies: {
    "react-native-screenshot-url": {
      root: __dirname,
      platforms: {
        android: {
          sourceDir: __dirname + "/android",
          packageImportPath: "import com.lewin.capture.ScreenCapturePackage;",
          packageInstance: "new ScreenCapturePackage()",
        },
      },
    },
  },
};
