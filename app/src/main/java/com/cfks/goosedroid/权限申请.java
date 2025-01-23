/**
 * @Author 
 * @AIDE AIDE+
*/
package com.cfks.goosedroid;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.net.*;
import android.provider.*;

public class 权限申请{
	private Activity activity;
	private PermissionHelper helper;
	
	public 权限申请(Activity 窗口环境){
		this.activity = 窗口环境;
		helper = new PermissionHelper(窗口环境);
	}
	
	//获取当前应用的版本号
	public int 取应用版本号(){
	PackageManager manager = activity.getPackageManager();
	try {
		PackageInfo info = manager.getPackageInfo(activity.getPackageName(), 0);
		return info.versionCode;
	} catch (PackageManager.NameNotFoundException e) {
		e.printStackTrace();
	}
	return 0;
	
	}

	//判断当前应用是否具有悬浮窗权限
	public boolean 是否有悬浮窗权限(){
	return Settings.canDrawOverlays(activity);
	}

	//判断当前应用是否具有应用安装权限
	public boolean 是否有应用安装权限(){
	return activity.getPackageManager().canRequestPackageInstalls();
	}

	//申请所有权限
	public void 申请所有权限(PermissionHelper.PermissionResultCallback callback){
	helper.requestPermission(((Activity)activity), callback, 取应用所有权限());
	}
	
	//申请权限，参数可以无限扩展
	public void 申请权限(PermissionHelper.PermissionResultCallback callback,String... 欲申请权限){
	helper.requestPermission(((Activity)activity), callback, 欲申请权限);
	}

	//调用该public void会跳转至手机设置界面去申请悬浮窗权限，并传入请求码5003，可在窗口申请权限回调事件进行判别
	public void 申请悬浮窗权限(){
	Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
	intent.setData(Uri.parse("package:" + activity.getPackageName()));
	((Activity)activity).startActivityForResult(intent, 5003);
	}

	//调用该public void会跳转至手机设置界面去申请应用安装权限，并传入请求码5004，可在窗口申请权限回调事件进行判别
	public void 申请应用安装权限(){
	Uri packageURI = Uri.parse("package:" + activity.getPackageName());
	Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
	((Activity)activity).startActivityForResult(intent, 5004);
	}

	//判断应用是否缺少权限，参数可无限扩展
	public boolean 是否缺少权限(String... 欲判断权限){
	return helper.lacksPermissions(欲判断权限);
	}

	//获取应用所有权限
	public String[] 取应用所有权限(){
	PackageManager manager = activity.getPackageManager();
	try {
		PackageInfo info = manager.getPackageInfo(activity.getPackageName(), 0);
		String pkgName = info.packageName;
		PackageInfo packageInfo = manager.getPackageInfo(pkgName, PackageManager.GET_PERMISSIONS);
		return packageInfo.requestedPermissions;
	} catch (PackageManager.NameNotFoundException e) {
		e.printStackTrace();
		return new String[0];
	}
	}
}
