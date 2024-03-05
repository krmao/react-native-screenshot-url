package com.krmao.screenshot;


import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.modules.core.PermissionAwareActivity;
import com.facebook.react.modules.core.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

@SuppressWarnings({"ConstantConditions", "unused", "JavadocDeclaration", "ResultOfMethodCallIgnored", "ForLoopReplaceableByForEach", "Convert2Lambda", "Anonymous2MethodRef"})
public class ScreenShot extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private ScreenShotListenManager manager;
    private final static String path = "/screenshot/";
    private Uri contentUri;

    public ScreenShot(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @NonNull
    @Override
    public String getName() {
        return "ScreenShot";
    }

    @ReactMethod
    public void startListener(String keywords, Promise promise) {
        String[] keys = null;
        if (keywords != null && keywords.length() > 0) {
            keys = keywords.split(",");
        }
        this.startListenerCapture(promise, keys);
    }

    @ReactMethod
    public void stopListener(final Promise promise) {
        try {
            getCurrentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (manager != null) {
                        manager.stopListen();
                        manager = null;
                    }

                }
            });
            promise.resolve("true");
        } catch (Exception ex) {
            ex.printStackTrace();
            promise.reject("500", ex.getMessage());
        }
    }

    @ReactMethod
    public void screenCapture(Boolean isHiddenStatus, String extension, Integer quality, Double scale, final Promise promise) {
        shotActivity(
                getCurrentActivity(),
                isHiddenStatus,
                extension,
                quality,
                scale,
                new ResultCallback() {
                    @Override
                    public void invoke(WritableMap result) {
                        promise.resolve(result);
                    }
                }
        );
    }

    @ReactMethod
    public void clearCache(Promise promise) {
        WritableMap map = Arguments.createMap();
        try {
            File file = new File(Environment.getExternalStorageDirectory() + path);
            deleteFile(file);
            map.putString("code", "200");
            promise.resolve(map);
        } catch (Exception ex) {
            ex.printStackTrace();
            promise.reject("500", ex.getMessage());
        }


    }

    private void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                deleteFile(f);
            }
            file.delete();//如要保留文件夹，只删除文件，请注释这行
        } else if (file.exists()) {
            file.delete();
        }
    }

    private boolean hasPermissions(Activity activity, String[] permissions) {
        if (permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("PERMISSIONS", "Permission is not granted: " + permission);
                    return false;
                }
                Log.d("PERMISSIONS", "Permission already granted: " + permission);
            }
            return true;
        }
        return false;
    }

    private void doWrapCallback(Uri newContentUri) {
        Log.d("ScreenShot", "doWrapCallback handleMediaContentChange newContentUri=" + newContentUri.toString());
        manager.handleMediaContentChange(newContentUri, new ScreenShotListenManager.OnScreenShotFinalListener() {
            @Override
            public void onShot(String imagePath) {
                Log.d("ScreenShot", "doWrapCallback onShot imagePath=" + imagePath);

                // 获取到系统文件
                WritableMap map = Arguments.createMap();
                map.putString("code", "200");
                map.putString("uri", imagePath.indexOf("file://") == 0 ? imagePath : "file://" + imagePath);
                map.putString("base64", bitmapToBase64(BitmapFactory.decodeFile(imagePath), "png", 100));
                reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("ScreenShot", map);
            }
        });
    }

    private void startListenerCapture(final Promise promise, final String[] keywords) {
        try {
            Activity currentActivity = getCurrentActivity();
            currentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //此时已在主线程中，可以更新UI了
                    // 开始监听
                    manager = ScreenShotListenManager.newInstance(reactContext, keywords);
                    manager.setListener(
                            new ScreenShotListenManager.OnScreenShotListen() {
                                public void onShot(Uri newContentUri) {
                                    Log.d("ScreenShot", "setListener onShot newContentUri=" + newContentUri.toString());
                                    if (SDK_INT > 22) {
                                        String[] PERMISSIONS;
                                        if (SDK_INT > 32) {
                                            Log.d("ScreenShot", "doWrapCallback SDK_INT > 32");
                                            PERMISSIONS = new String[]{Manifest.permission.READ_MEDIA_IMAGES};
                                        } else {
                                            Log.d("ScreenShot", "doWrapCallback SDK_INT <= 32");
                                            PERMISSIONS = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
                                        }
                                        Log.d("ScreenShot", "check Permissions PERMISSIONS=" + Arrays.toString(PERMISSIONS));

                                        if (!hasPermissions(currentActivity, PERMISSIONS)) {
                                            Log.d("ScreenShot", "hasPermissions false, launch PERMISSIONS ui");
                                            contentUri = newContentUri;
                                            PermissionAwareActivity activity = getPermissionAwareActivity();
                                            if (activity != null) {
                                                int originRequestCode = 999;
                                                activity.requestPermissions(PERMISSIONS, originRequestCode, new PermissionListener() {
                                                    @Override
                                                    public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
                                                        Log.d("ScreenShot", "requestPermissions onRequestPermissionsResult requestCode=" + requestCode);
                                                        Log.d("ScreenShot", "requestPermissions onRequestPermissionsResult permissions=" + Arrays.toString(permissions));
                                                        Log.d("ScreenShot", "requestPermissions onRequestPermissionsResult grantResults=" + Arrays.toString(grantResults));
                                                        boolean isAllGranted = true;
                                                        for (int index = 0; index < grantResults.length; index++) {
                                                            if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                                                                isAllGranted = false;
                                                                break;
                                                            }
                                                        }
                                                        if (requestCode == originRequestCode && isAllGranted) {
                                                            Log.d("ScreenShot", "requestPermissions onRequestPermissionsResult all granted, doWrapCallback");
                                                            doWrapCallback(contentUri);
                                                            contentUri = null;
                                                            return true;
                                                        }
                                                        contentUri = null;
                                                        Log.d("ScreenShot", "requestPermissions onRequestPermissionsResult not all granted, return false");
                                                        return false;
                                                    }
                                                });
                                            }else {
                                                Log.e("ScreenShot", "getPermissionAwareActivity is null, just return");
                                                contentUri = null;
                                            }
                                        } else {
                                            Log.d("ScreenShot", "hasPermissions true");
                                            doWrapCallback(newContentUri);
                                        }
                                    } else {
                                        Log.d("ScreenShot", "doWrapCallback SDK_INT <= 22");
                                        doWrapCallback(newContentUri);
                                    }
                                }
                            }
                    );
                    Log.d("ScreenShot", "startListen");
                    manager.startListen();
                    promise.resolve("success");
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            promise.reject("500", ex.getMessage());
        }


    }

    /**
     * 根据指定的Activity截图（带空白的状态栏）
     *
     * @param context        要截图的Activity
     * @param isHiddenStatus
     * @param extension      output extension
     * @param quality        output quality
     * @param scale          scale output size
     * @param callback
     * @return
     */
    public static void shotActivity(Activity context, final Boolean isHiddenStatus, final String extension, final int quality, final Double scale, final ResultCallback callback) {
        CaptureCallback captureCallback = new CaptureCallback() {
            @Override
            public void invoke(@Nullable Bitmap bitmap) {
                WritableMap map = Arguments.createMap();
                if (bitmap != null) {
                    Bitmap outputBitmap = (scale.floatValue() > 0) ? resizeBitmap(bitmap, scale.floatValue()) : bitmap;
                    try {
                        map.putString("code", "200");
                        map.putString("uri", "file://" + saveFile(outputBitmap, extension, quality));
                        map.putString("base64", bitmapToBase64(outputBitmap, extension, quality));
                    } catch (Exception e) {
                        e.printStackTrace();
                        map.putString("code", "500");
                    }
                } else {
                    map.putString("code", "500");
                }
                callback.invoke(map);
            }
        };
        if (isHiddenStatus) {
            ScreenShotUtils.snapShotWithoutStatusBar(context, captureCallback);
        } else {
            ScreenShotUtils.snapShotWithStatusBar(context, captureCallback);
        }
    }

    /**
     * save bitmap to file
     *
     * @param bitmap
     * @param extension
     * @param quality
     * @return
     */
    private static String saveFile(Bitmap bitmap, String extension, int quality) throws Exception {
        Calendar now = new GregorianCalendar();
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String fileName = Environment.getExternalStorageDirectory() + path + simpleDate.format(now.getTime()) + "." + extension;
        File fileDir = new File(Environment.getExternalStorageDirectory() + path);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        FileOutputStream out = new FileOutputStream(file);
        bitmap.compress(extToCompressFormat(extension), quality, out);
        out.flush();
        out.close();
        return fileName;
    }

    /**
     * Resize bitmap
     *
     * @param src
     * @return
     */
    private static Bitmap resizeBitmap(Bitmap src, float scale) {
        int width = src.getWidth();
        int height = src.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap resizedBitmap = Bitmap.createBitmap(src, 0, 0, width, height, matrix, false);
        src.recycle();
        return resizedBitmap;
    }

    /**
     * extension to CompressFormat
     *
     * @param extension
     * @return
     */
    private static Bitmap.CompressFormat extToCompressFormat(String extension) {
        switch (extension) {
            case "jpg":
            case "jpeg":
                return Bitmap.CompressFormat.JPEG;
            default:
                return Bitmap.CompressFormat.PNG;
        }
    }


    /**
     * bitmap转为base64
     *
     * @param bitmap
     * @param extension
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap, String extension, int quality) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(extToCompressFormat(extension), quality, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private PermissionAwareActivity getPermissionAwareActivity() {
        Activity activity = getCurrentActivity();
        return (PermissionAwareActivity) activity;
    }
}
