package com.dcinspirations.bscproject;

import android.content.SharedPreferences;
import android.provider.Settings;

import com.dcinspirations.bscproject.models.TTsSettings;
import com.dcinspirations.bscproject.models.myfile;
import com.dcinspirations.bscproject.models.user;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Sp {
    static SharedPreferences sharedPreferences;
    static SharedPreferences.Editor spe;

    public Sp() {
        sharedPreferences = BaseApp.ctx.getSharedPreferences("default", 0);
        spe = sharedPreferences.edit();
    }

    public static void addToRecent(myfile fm) {

        ArrayList<myfile> filelist = getRecent();
        if(!filelist.contains(fm)) {
            if(filelist.size()<3){
                filelist.add(0, fm);
            }else{
                filelist.remove(filelist.size()-1);
                filelist.add(0,fm);
            }

        }
        Gson gson = new Gson();
        String json = gson.toJson(filelist);
        spe.putString("recent", json);
        spe.commit();
    }

    public static ArrayList<myfile> getRecent() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("recent", "");
        if (json.isEmpty()) {
            return new ArrayList<>();
        } else {
            Type type = new TypeToken<List<myfile>>() {
            }.getType();
            ArrayList<myfile> arrPackageData = gson.fromJson(json, type);
            return arrPackageData;
        }
    }
    public static void removeRecent(int position) {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("recent", "");

        if (!json.isEmpty()) {
            Type type = new TypeToken<List<myfile>>() {
            }.getType();
            ArrayList<myfile> arrPackageData = gson.fromJson(json, type);
            try{
                arrPackageData.remove(position);
                String json1 = gson.toJson(arrPackageData);
                spe.putString("recent", json1);
                spe.commit();
            }catch (Exception e){

            }
        }
    }

    public static void setLoggedIn(boolean state){
        spe.putBoolean("loggedIn",state);
        spe.commit();
    }
    public static boolean getLoggedIn(){
        boolean li = sharedPreferences.getBoolean("loggedIn",false);
        return li;
    }
    public void setGender(String gender){
        spe.putString("gender",gender);
        spe.commit();
    }
    public String getGender(){
        String li = sharedPreferences.getString("gender","male");
        return li;
    }
    public static void setUInfo(String key,String username,String status){
        spe.putString("key",key);
        spe.putString("username",username);
        spe.putString("status",status);
        spe.commit();
    }
    public static user getUInfo(){
        String name = sharedPreferences.getString("username","");
        String key = sharedPreferences.getString("key","");
        String status = sharedPreferences.getString("status","");
        user cu = new user(key,name,status);
        return cu;
    }
    public static void setTTs(int sr, int pitch){
        spe.putInt("sr",sr);
        spe.putInt("pitch",pitch);
        spe.commit();
        if(sr>0||pitch>0) {
            BaseApp.tts.setSpeechRate(((float) sr + 1) / 10);
            BaseApp.tts.setPitch(((float) pitch + 1) / 10);
        }else{
            try {
                float s = Settings.Secure.getFloat(BaseApp.ctx.getContentResolver(),Settings.Secure.TTS_DEFAULT_RATE);
                float p = Settings.Secure.getFloat(BaseApp.ctx.getContentResolver(),Settings.Secure.TTS_DEFAULT_PITCH);
                BaseApp.tts.setSpeechRate((float)(s/100));
                BaseApp.tts.setPitch((float)(p/100));
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
        }

    }
    public static TTsSettings getTTs(){
        int sr = sharedPreferences.getInt("sr",0);
        int pitch = sharedPreferences.getInt("pitch",0);
        TTsSettings ts = new TTsSettings(sr,pitch);
        return ts;
    }
}
