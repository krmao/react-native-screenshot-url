# react-native-screenshot-url

```typescript
import ScreenShotUtil from 'react-native-screenshot-url';

useEffect(() => {
    ScreenShotUtil.startListener((res: any) => {
        console.log('- startScreenShot uri=' + res.uri);
    }, '截屏,screen');

    return () => {
        ScreenShotUtil.stopListener();
    };
}
```
