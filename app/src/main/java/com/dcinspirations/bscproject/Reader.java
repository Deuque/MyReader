package com.dcinspirations.bscproject;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import android.widget.Toast;

import com.aspose.words.License;
import com.aspose.words.SaveFormat;
import com.dcinspirations.bscproject.models.myfile;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

//import org.docx4j.Docx4J;
//import org.docx4j.model.fields.FieldUpdater;
//import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

import pl.droidsonroids.gif.GifImageView;

//import kotlin.jvm.Synchronized;

public class Reader extends AppCompatActivity implements View.OnClickListener {

    Runnable r = new Runnable() {
        @Override
        public void run() {
            rt.setVisibility(View.GONE);
            controls.setVisibility(View.GONE);


        }
    };
    TextView t,pages;
    Toolbar rt;
    LinearLayout controls;
    RelativeLayout parent;
    PDFView viewer;
    GifImageView loader;
    Handler h = new Handler();
    myfile currentfile;
    int start, status;
    String words[];
    ImageView mc,pr,nx;
    TextToSpeech tts;
    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        License lic = new License();
        start = status = 0;
        tts= BaseApp.tts;
        Gson gson = new Gson();
        String json = getIntent().getExtras().getString("currentfile");
        if (!json.isEmpty()) {
            Type type = new TypeToken<myfile>() {
            }.getType();
            currentfile = gson.fromJson(json, type);

        }

        t = findViewById(R.id.title);
        rt = findViewById(R.id.rtoolbar);
        controls = findViewById(R.id.controls);
        parent = findViewById(R.id.parent);
        viewer = findViewById(R.id.viewer);
        loader = findViewById(R.id.loader);
        mc = findViewById(R.id.maincontrol);
        pr = findViewById(R.id.prev);
        nx = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(sol);

        t.setText(currentfile.getName() + "." + currentfile.getType());
        parent.setOnClickListener(this);
        viewer.setOnClickListener(this);
        rt.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mc.setOnClickListener(this);
        pr.setOnClickListener(this);
        nx.setOnClickListener(this);
        controls.setOnClickListener(this);


        new BackgroundTask(this).execute();


    }

    private void showAndHide() {
        h.removeCallbacks(r);
        rt.setVisibility(View.VISIBLE);
        controls.setVisibility(View.VISIBLE);
        h.postDelayed(r, 5000);
    }

    public void displayContent(byte[] b) {

        if (b != null) {
            viewer.setVisibility(View.VISIBLE);
            viewer.fromBytes(b)
                    .scrollHandle(new DefaultScrollHandle(this))
                    .spacing(10) // in dp
                    .load();
            showAndHide();



        }else{
        }
    }

    private String getPages(){
        return String.valueOf(viewer.getPageCount())+" pages";
    }

    private void play(){
        status = 1;
       bJob();

    }
    private void bJob() {
        if (start < words.length) {

            tts.speak(words[start], TextToSpeech.QUEUE_ADD, null, String.valueOf(start));
            Double d1 = Double.parseDouble(String.valueOf(start));
            Double d2 = Double.parseDouble(String.valueOf(words.length));
            Double d = d1/d2;
            d = d * 100;
//            Toast.makeText(this, String.valueOf(d).substring(0,String.valueOf(d).indexOf(".")), Toast.LENGTH_SHORT).show();
            String progress = String.valueOf(d).substring(0,String.valueOf(d).indexOf("."));
            seekBar.setProgress(Integer.parseInt(progress));


            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {

                }

                @Override
                public void onDone(String utteranceId) {
                    start = Integer.parseInt(utteranceId)+1;
                    bJob();
                }

                @Override
                public void onError(String utteranceId) {

                }

                @Override
                public void onRangeStart(String utteranceId, int start, int end, int frame) {
                    super.onRangeStart(utteranceId, start, end, frame);
                }
            });
        }else{
            start=0;
            pause();
        }

    }
    private void rewind(){
        if(start>0) {
            if (status == 1) {
                pause();
                start = start - 2;
                play();
                return;
            }
            start = start - 2;
        }
    }
    private void forward(){
        if(start<words.length) {
            if (status == 1) {
                pause();
                start = start + 1;
                play();
                return;
            }
            start = start + 1;
        }
    }
    private void pause(){
        status = 0;
        BaseApp.stopSpeak();
    }
    private void getWords(byte[] b){
        try {
            String parsedText="",parsedtext2 = "";
            PdfReader reader = new PdfReader(b);
            int n = reader.getNumberOfPages();
            words = new String[n];
            for (int i = 0; i <n ; i++) {
                parsedText   = parsedText+ PdfTextExtractor.getTextFromPage(reader, i+1)+"\n"; //Extracting the content from the different pages
            }
//           parsedText = parsedText.replaceAll(getResources().getString(R.string.bl1),"")
//                   .replaceAll(getResources().getString(R.string.bl2),"")
//                   .replaceAll(getResources().getString(R.string.bl3),"").replaceAll("\n"," ");



//            for(String r:parsedText.split(" ")){
//                if(!r.isEmpty()&&Character.isUpperCase(r.charAt(0))&&Character.isUpperCase(r.charAt(0))){
//                    r = ". "+r;
//                }
//                parsedtext2 = parsedtext2+r+" ";
//            }
            words = resolveWords(parsedText).split("\n"+" ");



            reader.close();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private SeekBar.OnSeekBarChangeListener sol = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser) {
                seekBar.setProgress(progress);
                Double d1 = (double) progress;
                Double d2 = (double) words.length;
                Double d3 = d1 / 100;
                d3 = d3 * d2;
                String Sstart = String.valueOf(d3).substring(0, String.valueOf(d3).indexOf("."));
                if (status == 1) {
                    pause();
                    start = Integer.parseInt(Sstart);
                    play();
                    return;
                }
                start = Integer.parseInt(Sstart);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
    private String resolveWords(String parsedText){

        parsedText = parsedText.replaceAll(getResources().getString(R.string.bl1),"")
                .replaceAll(getResources().getString(R.string.bl2),"")
                .replaceAll(getResources().getString(R.string.bl3),"").replaceAll("\n","LB ");

        String pt3[] = parsedText.split(" ");
        String pt2 = "";
        for(int i=0;i<pt3.length;i++){
            String r = pt3[i];
            if(r.equalsIgnoreCase("LB")&&i>0&&(i+1) <pt3.length){
                String r1=pt3[i-1],r2=pt3[i+1];
                if(!r1.isEmpty()&&!r2.isEmpty()&&Character.isLowerCase(r1.charAt(0))&&Character.isLowerCase(r2.charAt(0))){
                    r=" ";
                    pt2 = pt2+r;
                    continue;
                }
            }
            pt2 = pt2+r+" ";
        }
        pt2 = pt2.replaceAll("LB","\n");
        return pt2;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.parent:
                showAndHide();
                break;
            case R.id.viewer:
                showAndHide();
                break;
            case R.id.rtoolbar:
                finish();
                break;
            case R.id.controls:
                showAndHide();
                break;
            case R.id.maincontrol:
                showAndHide();
                ImageView im = (ImageView) v;
                if(status==0){
                    mc.setBackground(getResources().getDrawable(R.drawable.pause));
                    play();
                }else{
                    mc.setBackground(getResources().getDrawable(R.drawable.play));
                    pause();
                }
                break;
            case R.id.prev:
                rewind();
                break;
            case R.id.next:
                forward();
                break;
        }
    }


    class BackgroundTask extends AsyncTask<String, Void, byte[]> {
        Context ctx;
        String jr;
        File f;
        byte[] b;

        public BackgroundTask(Context context) {
            this.ctx = context;
        }

        @Override
        protected void onPreExecute() {
            f = new File(BaseApp.getDir().getPath() + "/" + currentfile.getName() + "." + currentfile.getType());

        }

        @Override
        protected byte[] doInBackground(String... strings) {
            if (currentfile.getType().equalsIgnoreCase("pdf")) {
                b = resolvePdf(Uri.fromFile(f));
            } else {
                b = resolveDoc(Uri.fromFile(f));
            }
            return b;

        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            loader.setVisibility(View.GONE);
           displayContent(bytes);
            getWords(bytes);

        }

        public byte[] resolvePdf(Uri uri) {
            String text = "";
            try {
                InputStream is = getContentResolver().openInputStream(uri);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                return buffer;

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        public byte[] resolveDoc(Uri uri){

            try {

                 InputStream is = getContentResolver().openInputStream(uri);
                com.aspose.words.Document doc = new com.aspose.words.Document(is);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                doc.save(os, SaveFormat.PDF);

                byte[] buffer = os.toByteArray();
                return buffer;


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();

            }


            return null;
        }




    }
    @Override
    protected void onPause() {
        super.onPause();
        BaseApp.stopSpeak();
        status = 0;
        mc.setBackground(getResources().getDrawable(R.drawable.play));
    }


}
