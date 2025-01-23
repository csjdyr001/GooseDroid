package com.cfks.goosedroid;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.ashlikun.keeplive.*;
import com.ashlikun.keeplive.config.*;
import java.util.*;
import jiesheng.*;

public class MainActivity extends Activity 
{
	private Switch GooseDroid;
	private Switch EnableMods;
	private Switch SilenceSounds;
	private Switch Task_CanAttackMouse;
	private Switch AttackRandomly;
	private Switch UseCustomColors;
	private EditText GooseDefaultWhite;
	private EditText GooseDefaultOrange;
	private EditText GooseDefaultOutline;
	private EditText MinWanderingTimeSeconds;
	private EditText MaxWanderingTimeSeconds;
	private EditText FirstWanderTimeSeconds;
	
	private String 配置文件路径 = "";
	private 权限申请 权限申请1;
	private WindowManager wm1;
	private WindowManager.LayoutParams wmlay1;
	private GooseView gooseView;
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		GooseDroid = findViewById(R.id.GooseDroid);
		
		EnableMods = findViewById(R.id.EnableMods);
		SilenceSounds = findViewById(R.id.SilenceSounds);
		Task_CanAttackMouse = findViewById(R.id.TaskCanAttackMouse);
		AttackRandomly = findViewById(R.id.AttackRandomly);
		UseCustomColors = findViewById(R.id.UseCustomColors);
		GooseDefaultWhite = findViewById(R.id.GooseDefaultWhite);
		GooseDefaultOrange = findViewById(R.id.GooseDefaultOrange);
		GooseDefaultOutline = findViewById(R.id.GooseDefaultOutline);
		MinWanderingTimeSeconds = findViewById(R.id.MinWanderingTimeSeconds);
		MaxWanderingTimeSeconds = findViewById(R.id.MaxWanderingTimeSeconds);
		FirstWanderTimeSeconds = findViewById(R.id.FirstWanderTimeSeconds);
		
		权限申请1 = new 权限申请(this);
		if (权限申请1.是否缺少权限(权限申请1.取应用所有权限())) {
			// Do not have permissions, request them now
			权限申请1.申请所有权限(new PermissionHelper.PermissionResultCallback(){
				@Override
				public void onPermissionResult(boolean allGranted) {
					配置文件路径 = 存储卡操作.取存储卡路径() + "/GooseDroid/config.ini";
				}
			});
		}else{
			读入配置文件();
		}
		读入配置文件();
		GooseDroid.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton cb,boolean 是否开启){
				try{
				if(是否开启){
					if(!权限申请1.是否有悬浮窗权限()){
						GooseDroid.setChecked(false);
						应用操作.弹出提示(MainActivity.this,"请开启悬浮窗权限");
						权限申请1.申请悬浮窗权限();
					}else{
						//定义前台服务的默认样式。即标题、描述和图标
						ForegroundNotification foregroundNotification = new ForegroundNotification("GooseDroid","运行中…", R.mipmap.ic_launcher,1);
						//启动保活服务
						KeepLive.INSTANCE.startWork(getApplication(),foregroundNotification);
						//KeepLive.INSTANCE.startIgnoreBattery(this,foregroundNotification);
						ConfigureActivity ca = new ConfigureActivity(MainActivity.this);
						ca.readFromSD(配置文件路径);
						gooseView = new GooseView(MainActivity.this,ca);
						wm1=(WindowManager)getApplicationContext().getSystemService("window");
						wmlay1 = new WindowManager.LayoutParams();
						//当前悬浮窗口位于phone层
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
							wmlay1.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
						} else {
							wmlay1.type = WindowManager.LayoutParams.TYPE_PHONE;
						}
						wmlay1.format=PixelFormat.RGBA_8888; //悬浮窗口背景设为透明
						wmlay1.gravity=Gravity.RIGHT | Gravity.TOP;
						wmlay1.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
							| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
							| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;//属性设置
						wmlay1.x = 0;
						wmlay1.y = 0;
						wmlay1.width = 系统操作.取屏幕宽度(MainActivity.this);
						wmlay1.height = 系统操作.取屏幕高度(MainActivity.this);
						wm1.addView(gooseView, wmlay1);
						应用操作.弹出提示(MainActivity.this,"GooseDroid开启成功");
					}
				}else{
					wm1.removeView(gooseView);
					gooseView = null;
					KeepLive.INSTANCE.stopWork(getApplication());
					应用操作.弹出提示(MainActivity.this,"GooseDroid关闭成功");
				}
			}catch(Exception e){
				e.printStackTrace();
				应用操作.信息框(MainActivity.this,MainActivity.class.getName() + "-异常",e.toString());
			}
			}
		});
    }
	private void 读入配置文件(){
		try{
			配置文件路径 = 存储卡操作.取存储卡路径() + "/GooseDroid/config.ini";
			if(!文件操作.文件是否存在(配置文件路径)){
				文件操作.写出资源文件(this,"config.ini",配置文件路径);
			}
			ConfigureActivity ca = new ConfigureActivity(MainActivity.this);
			ca.readFromSD(配置文件路径);
			EnableMods.setChecked(string2boolean(ca.getIniKey("EnableMods")));
			SilenceSounds.setChecked(string2boolean(ca.getIniKey("SilenceSounds")));
			Task_CanAttackMouse.setChecked(string2boolean(ca.getIniKey("Task_CanAttackMouse")));
			AttackRandomly.setChecked(string2boolean(ca.getIniKey("AttackRandomly")));
			UseCustomColors.setChecked(string2boolean(ca.getIniKey("UseCustomColors")));
			设置文本框内容(GooseDefaultWhite,ca.getIniKey("GooseDefaultWhite"));
			设置文本框内容(GooseDefaultOrange,ca.getIniKey("GooseDefaultOrange"));
			设置文本框内容(GooseDefaultOutline,ca.getIniKey("GooseDefaultOutline"));
			设置文本框内容(MinWanderingTimeSeconds,ca.getIniKey("MinWanderingTimeSeconds"));
			设置文本框内容(MaxWanderingTimeSeconds,ca.getIniKey("MaxWanderingTimeSeconds"));
			设置文本框内容(FirstWanderTimeSeconds,ca.getIniKey("FirstWanderTimeSeconds"));
			应用操作.弹出提示(MainActivity.this,"读入配置文件");
		}catch(Exception e){
			e.printStackTrace();
			应用操作.信息框(this,MainActivity.class.getName() + "-异常",e.toString());
		}
	}
		@Override
		public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
				// Forward results to EasyPermissions
				配置文件路径 = 存储卡操作.取存储卡路径() + "/GooseDroid/config.ini";
			}
	
	public static boolean string2boolean(String str){
		return Boolean.parseBoolean(str.toLowerCase());
	}
	private void 设置文本框内容(EditText 文本框,String 内容){
		文本框.setText(内容.toCharArray(),0,内容.length());
	}
	@Override
    protected void onDestroy() {
        super.onDestroy();
		if(GooseDroid.isChecked()){
			wm1.removeView(gooseView);
			gooseView = null;
		}
		KeepLive.INSTANCE.stopWork(getApplication());
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//add的参数（菜单项的组号，ID，排序号，标题）
		menu.clear();
		menu.add(1,1,1,"Config File Path");
		menu.add(1,2,2,"Reset To Default Config");
		menu.add(1,3,3,"Save Config");
		return true;
	}
	private void reStartActivity() {
		Intent intent = getIntent();
		finish();
		startActivity(intent);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO: Implement this method
		switch(item.getTitle().toString()){
			case "Config File Path":
				应用操作.信息框(this,"Config File Path",配置文件路径);
				break;
			case "Save Config":
				try{
					ConfigureActivity ca=new ConfigureActivity(this);
					Properties prop = new Properties();
					prop.put("EnableMods",captureName(String.valueOf(EnableMods.isChecked())));
					prop.put("SilenceSounds",captureName(String.valueOf(SilenceSounds.isChecked())));
					prop.put("Task_CanAttackMouse",captureName(String.valueOf(Task_CanAttackMouse.isChecked())));
					prop.put("AttackRandomly",captureName(String.valueOf(AttackRandomly.isChecked())));
					prop.put("UseCustomColors",captureName(String.valueOf(UseCustomColors.isChecked())));
					prop.put("GooseDefaultWhite",GooseDefaultWhite.getText().toString());
					prop.put("GooseDefaultOrange",GooseDefaultOrange.getText().toString());
					prop.put("GooseDefaultOutline",GooseDefaultOutline.getText().toString());
					prop.put("MinWanderingTimeSeconds",MinWanderingTimeSeconds.getText().toString());
					prop.put("MaxWanderingTimeSeconds",MaxWanderingTimeSeconds.getText().toString());
					prop.put("FirstWanderTimeSeconds",FirstWanderTimeSeconds.getText().toString());
					ca.saveFiletoSD(配置文件路径,prop);
				}catch(Exception e){
					e.printStackTrace();
					应用操作.信息框(this,MainActivity.class.getName() + "-异常",e.toString());
				}
				应用操作.弹出提示(this,"Save successfully!");
				break;
			case "Reset To Default Config":
				if(文件操作.文件是否存在(配置文件路径)){
					文件操作.删除文件(配置文件路径);
				}
				文件操作.写出资源文件(this,"config.ini",配置文件路径);
				应用操作.弹出提示(this,"Reset successfully!");
				reStartActivity();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	private String captureName(String name) {
		name = name.substring(0, 1).toUpperCase() + name.substring(1);
		return  name;
	}
}
