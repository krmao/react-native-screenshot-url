// noinspection NpmUsedModulesInstalled,JSUnresolvedReference

const { NativeModules, NativeEventEmitter, DeviceEventEmitter, Platform , PermissionsAndroid} = require('react-native');

let screenShotEmitter = undefined

// noinspection JSVoidFunctionReturnValueUsed,JSCheckFunctionSignatures,JSUnusedGlobalSymbols,JSUnresolvedReference,JSDeprecatedSymbols,ES6RedundantAwait,JSIgnoredPromiseFromCall
/**
 * 获取系统截屏事件/截屏工具类
 * android 默认不包含状态时间
 * ios 截屏不包含时间
 */
export default class ScreenShotUtil  {
   static checkPermissionsForScreenShot = async () => {
    if (Platform.OS === 'ios') {
      return Promise.resolve(true);
    }
    const SDK_INT = +Platform.constants.Release;
    if (SDK_INT > 22) {
      if (SDK_INT > 32) {
        return (
            (await PermissionsAndroid.check(PermissionsAndroid.PERMISSIONS.READ_MEDIA_IMAGES)) &&
            (await PermissionsAndroid.check(PermissionsAndroid.PERMISSIONS.READ_MEDIA_VEDIO))
        );
      } else {
        return await PermissionsAndroid.check(PermissionsAndroid.PERMISSIONS.READ_EXTERNAL_STORAGE);
      }
    } else {
      return Promise.resolve(true);
    }
  };

  static requestPermissionsForScreenShot = async () => {
    if (Platform.OS === 'ios') {
      return Promise.resolve(null);
    }

    const SDK_INT = +Platform.constants.Release;
    if (SDK_INT > 22) {
      if (SDK_INT > 32) {
        return await PermissionsAndroid.requestMultiple([
          PermissionsAndroid.PERMISSIONS.READ_MEDIA_IMAGES,
          PermissionsAndroid.PERMISSIONS.READ_MEDIA_VEDIO,
        ]);
      } else {
        return await PermissionsAndroid.requestMultiple([PermissionsAndroid.PERMISSIONS.READ_EXTERNAL_STORAGE]);
      }
    } else {
      return Promise.resolve(null);
    }
  };

  /**
   * 开始监听截屏事件， keyWords android 监听数据变化，截屏文件名的关键字
   * @param {*} callBack
   * @param keyWords
   */
  static async startListener(callBack, keyWords) {
    if (!(await ScreenShotUtil.checkPermissionsForScreenShot())) {
      await ScreenShotUtil.requestPermissionsForScreenShot();
    }
    if (await ScreenShotUtil.checkPermissionsForScreenShot()) {
      return ScreenShotUtil.startListenerWithoutPermission(callBack, keyWords);
    }else{
      return Promise.resolve(null);
    }
  }

  static startListenerWithoutPermission (callBack, keyWords) {
    const ScreenShot = NativeModules.ScreenShot;
    // 创建自定义事件接口
    screenShotEmitter && screenShotEmitter.removeAllListeners('ScreenShot')
    screenShotEmitter = Platform.OS === 'ios' ? new NativeEventEmitter(ScreenShot) : DeviceEventEmitter;
    screenShotEmitter.addListener('ScreenShot', (data) => {
      if (callBack) {
        callBack(data)
      }
    })
    if (Platform.OS === 'android') {
      ScreenShot.startListener(keyWords);
    } else {
      ScreenShot.startListener();
    }
    return screenShotEmitter
  }

  /**
   * 停止监听
   */
  static stopListener () {
    screenShotEmitter && screenShotEmitter.removeAllListeners('ScreenShot')
    const ScreenShot = NativeModules.ScreenShot;
    return ScreenShot.stopListener();
  }

  /**
   * 清除截屏缓存文件
   * @param {*} callBack
   */
  static clearCache (callBack) {
    const ScreenShot = NativeModules.ScreenShot;
    ScreenShot.clearCache().then(res=>{
      callBack && callBack(res)
    }).catch(err=>{
      callBack && callBack(err)
    })
  }

  /**
   * 截取当前屏幕方法
   */
  static screenShot = (callBack, isHiddenStatus, { extension = 'png', quality = 100, scale = 0 }) => {
    const ScreenShot = NativeModules.ScreenShot;
    if (isHiddenStatus === undefined || isHiddenStatus === null) {
      isHiddenStatus = Platform.OS === 'android'
    }
    ScreenShot.screenShot(isHiddenStatus, extension || 'png', quality || 100, scale || 0).then(res=>{
      callBack && callBack(res)
    }).catch(err=>{
      callBack && callBack(err)
    })
  }
}
