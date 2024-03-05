# react-native-screenshot-url

[![npm version](https://badge.fury.io/js/react-native-screenshot-url.svg)](https://badge.fury.io/js/react-native-screenshot-url)

## Install

```shell
yarn add react-native-screenshot-url
```

## Usage
```typescript
import ScreenShotUtil, { CallbackInfo } from 'react-native-screenshot-url';

useEffect(() => {
    ScreenShotUtil.startListener((res: CallbackInfo) => {
        // android 权限请求后置 & 内置
        // ios 无需权限
        console.log('- startScreenShot uri=' + res.uri);
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
