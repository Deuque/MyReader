package com.dcinspirations.bscproject;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.widget.Toast;

import com.dcinspirations.bscproject.models.TTsSettings;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Set;

public class BaseApp extends Application {

    public static final String CHANNEL_1_ID= "channel1";
    public static final String CHANNEL_2_ID= "channel2";
    static TextToSpeech tts;
    static int result;
    static Context ctx;
    static Voice voice;
    static File appDir ;
    public static boolean done = true;


    @Override
    public void onCreate() {
        super.onCreate();

//        createNotificationChannels();

//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        ctx = getApplicationContext();
        appDir = getBaseContext().getExternalFilesDir("file_download");
        if(!appDir.exists()){
            if(appDir.mkdir()){
                Toast.makeText(ctx, "created", Toast.LENGTH_SHORT).show();
            }
        }
        initTTS();
    }

//    private void createNotificationChannels() {
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            NotificationChannel gc = new NotificationChannel(
//                    CHANNEL_1_ID,
//                    "gidi_channel",
//                    NotificationManager.IMPORTANCE_DEFAULT
//            );
//            gc.setDescription("Gidi Bet Channel");
//
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(gc);
//        }
//    }
    private static void initTTS(){
        tts = new TextToSpeech(ctx, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i== TextToSpeech.SUCCESS){
                    result = tts.setLanguage(Locale.UK);
                }else{
                    Toast.makeText(ctx, "Feature not supported in your device", Toast.LENGTH_SHORT).show();
                }
            }
        });
        TTsSettings ttss = new Sp().getTTs();
        if(ttss.getPitch()!=0&&ttss.getSr()!=0){
            tts.setSpeechRate(ttss.getSr());
            tts.setPitch(ttss.getPitch());
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


    public static void stopSpeak(){

        if(tts!=null)
        {
            tts.stop();
        }
    }

    public static File getDir(){
        return appDir;
    }

    public static String resolveTime(String s){
        String test[] = s.split(" ");
        SimpleDateFormat tf = new SimpleDateFormat("MM YY HH:mma");
        String date = tf.format( Calendar.getInstance().getTime());
        String date2[]= date.split(" ");
        String Day = String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        try{
            int a = parse(date2[1])-parse(test[2]);
            int b = parse(date2[0])-parse(test[1]);
            int c = parse(Day)-parse(test[0]);
            if(a>0 ){
                if(a>1) return a+" years ago, "+test[3];
                return a+" year ago, "+test[3];
            }else if(b>0){
                if(b>1) return b+" months ago, "+test[3];
                return b+" month ago, "+test[3];

            }else if(c>0){
                if(c>1) return c+" days ago, "+test[3];
                return c+" day ago, "+test[3];
            }else{
                return "Today, "+test[3];
            }
        }catch (Exception e){
            Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
        }
return null;
    }
    private static int parse(String s){
        return Integer.parseInt(s);
    }



    public static String[] getFileDetails(String string){

        String details[] = new String[2];
        String array[] = string.split("/");
        String name = array[array.length-1].trim();
        if(name.contains(":")){
            details[0] = name.substring(name.indexOf(":")+1,name.indexOf("."));
        }else{
            details[0] = name.substring(0,name.indexOf("."));
        }


        String ext = name.substring(name.indexOf(".")+1,name.length());
        details[1] = ext;
        return details;
    }
    public static String getTime(){
        SimpleDateFormat tf = new SimpleDateFormat("MM YY HH:mma");
        String date = tf.format( Calendar.getInstance().getTime());
        return String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH))+" "+date;
    }


}
