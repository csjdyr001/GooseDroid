package com.cfks.goosedroid;

import android.app.*;
import android.content.pm.*;
import android.support.annotation.*;
import android.support.v4.app.*;
import android.support.v4.content.*;

public class PermissionHelper
 {

    private Activity mActivity;
    private final int REQUEST_CODE = 1000;
    private PermissionResultCallback mPermissionResultCallback;

    public PermissionHelper(Activity activity) {
        mActivity = activity;
    }

    // 启动当前权限页面的公开接口
    public void requestPermission(Activity activity, PermissionResultCallback permissionResultCallback, String... permissions) {
        ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE);
        this.mPermissionResultCallback = permissionResultCallback;
    }


    // 判断权限集合
    public boolean lacksPermissions(String... permissions) {
        for (String permission : permissions) {
            if (lacksPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    // 判断是否缺少权限
    private boolean lacksPermission(String permission) {
        return ContextCompat.checkSelfPermission(mActivity, permission) ==
                PackageManager.PERMISSION_DENIED;
    }


    // 含有全部的权限
    public boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (mPermissionResultCallback != null) {
            mPermissionResultCallback.onPermissionResult(hasAllPermissionsGranted(grantResults));
        }
    }

    public interface PermissionResultCallback {
        void onPermissionResult(boolean allGranted);
    }
}

