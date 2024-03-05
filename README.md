# react-native-screenshot-url

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
