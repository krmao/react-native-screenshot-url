# react-native-screenshot-url

[![npm version](https://badge.fury.io/js/react-native-screenshot-url.svg)](https://badge.fury.io/js/react-native-screenshot-url)

## Install

```shell
yarn add react-native-screenshot-url
```

## Usage
```typescript
import ScreenShotUtil, { CallbackInfo } from 'react-native-screenshot-url';

const [screenShotUri, setScreenShotUri] = useState<string | undefined>(undefined);


useEffect(() => {
    ScreenShotUtil.startListener((res: CallbackInfo) => {
        console.log('- [Start] screenShot callback res.code=', res.code);
        if (res.code === '200') {
            setScreenShotUri(res.uri);

            //region 5s消失术
            if (timeoutIdRef.current) {
                clearTimeout(timeoutIdRef.current);
            }
            console.log('- [Start] screenShot 5s wait start');
            timeoutIdRef.current = setTimeout(() => {
                console.log('- [Start] screenShot 5s wait end');
                setScreenShotUri(undefined);
            }, 5000);
            //endregion
        } else if (res.code === '198') {
            console.log('- [Start] screenShot callback 198 show permission description');
            showDescription('AAAAA', 'BBBBBO', 'mediaPermission');
        } else if (res.code === '199') {
            console.log('- [Start] screenShot callback 199 hide permission description');
            PermissionUtil.hideDescriptionWithDialogName('mediaPermission');
        }
    }, '截屏,screen');

    return () => {
        ScreenShotUtil.stopListener();
    };
});
```

## Features

- support 22 - 32 permissions about images ***READ_EXTERNAL_STORAGE***
- support Android API level 33 permissions about images ***READ_MEDIA_IMAGES***
- startListener without permission
- permission request is built-in before callback

## Reference

- https://github.com/bigzx/react-native-screen-capture
