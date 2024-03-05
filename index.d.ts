export type ScreenShotUtil = {
    checkPermissionsForScreenShot: () => Promise<any>;
    requestPermissionsForScreenShot: () => Promise<any>;
    startListener: (callback: (result: any) => void, keyWords: string) => Promise<any>;
    startListenerWithoutPermission: (callback: (result: any) => void, keyWords: string) => Promise<any>;
    stopListener: () => Promise<any>;
    clearCache: (callback: (result: any) => void) => Promise<any>;
    screenShot: (callback: (result: any) => void, isHiddenStatus: any, config: {
        extension: string,
        quality: number,
        scale: number
    }) => Promise<any>;
}
