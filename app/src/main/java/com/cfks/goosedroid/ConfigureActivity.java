/**
 * @Author 
 * @AIDE AIDE+
*/
package com.cfks.goosedroid;

import android.content.*;
import android.os.*;
import android.widget.*;
import java.io.*;
import java.util.*;

public class ConfigureActivity
 {
    private final Context context;
    private  Properties properties;
    public ConfigureActivity(Context context){
        super();
        this.context=context;
    }
    /**
     * 保存文件filename为文件名，filecontent为存入的Properties对象
     * 例:configureActivity.saveFiletoSD("text.ini",properties);
     */
    public void saveFiletoSD(String filename,Properties properties)throws Exception{
        //if条件判定SD卡是否存在并具有读写权限
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            //filename=Environment.getExternalStorageDirectory().getCanonicalPath()+"/"+filename;
            //FileOutputStream()里面直接添加filename会覆盖原来的文件数据
            FileOutputStream fileOutputStream=new FileOutputStream(filename);
			//通过properties.stringPropertyNames()获得所有key的集合Set，里面是String对象
            for(String key : properties.stringPropertyNames()){
                String s=key+" = "+properties.getProperty(key)+"\n";
                System.out.println(s);
                fileOutputStream.write(s.getBytes());
            }

            fileOutputStream.close();
        }else {
            Toast.makeText(context,"写入错误",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 读取文件
     * */
    public void readFromSD(String filename)throws Exception{
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            properties = new Properties();
            //filename=Environment.getExternalStorageDirectory().getCanonicalPath()+"/"+filename;
            FileInputStream fileInputStream=new FileInputStream(filename);
            properties.load(fileInputStream);
            fileInputStream.close();
        }else {
            Toast.makeText(context,"读取错误",Toast.LENGTH_SHORT).show();
        }
    }
    public String getIniKey(String key) {
        if (properties.containsKey(key) == false) {
            return null;
        }
        return String.valueOf(properties.get(key));
    }
}
