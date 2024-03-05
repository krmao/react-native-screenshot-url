declare module 'react-native-screenshot-url' {

    export default class ScreenShotUtil {
        static checkPermissionsForScreenShot(): () => Promise<any>;

        static requestPermissionsForScreenShot: () => Promise<any>;
        static startListener: (callback: (result: any) => void, keyWords: string) => Promise<any>;
        static startListenerWithPermission: (callback: (result: any) => void, keyWords: string) => Promise<any>;
        static stopListener: () => Promise<any>;
        static clearCache: (callback: (result: any) => void) => Promise<any>;
        static screenShot: (callback: (result: any) => void, isHiddenStatus: any, config: {
            extension: string,
            quality: number,
            scale: number
        }) => Promise<any>;
    }

}
