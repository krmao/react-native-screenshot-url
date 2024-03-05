declare module 'react-native-screenshot-url' {

    export type CallbackInfo = {
        code: string, // 200 为正常 其他为异常
        uri: string, // 文件路径
        base64: string, // 图片base64 png
    }

    export default class ScreenShotUtil {
        static checkPermissionsForScreenShot(): () => Promise<any>;

        static requestPermissionsForScreenShot: () => Promise<any>;
        static startListener: (callback: (result: CallbackInfo) => void, keyWords: string) => Promise<any>;
        static startListenerWithPermission: (callback: (result: CallbackInfo) => void, keyWords: string) => Promise<any>;
        static stopListener: () => Promise<any>;
        static clearCache: (callback: (result: any) => void) => Promise<any>;
        static screenShot: (callback: (result: any) => void, isHiddenStatus: any, config: {
            extension: string,
            quality: number,
            scale: number
        }) => Promise<any>;
    }

}
