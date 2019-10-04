package com.dcinspirations.bscproject;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dcinspirations.bscproject.models.myfile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Splitter extends AppCompatActivity {

    EditText content;
    TextView save;
    ImageView empty;
    Toolbar stoolbar;
    Dialog cdialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splitter);
        cdialog = new Dialog(this);
        cdialog.setContentView(R.layout.save_details);

        stoolbar = findViewById(R.id.stoolbar);
        stoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        content = findViewById(R.id.content);
        save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveText1();
            }
        });
        empty = findViewById(R.id.empty);
        content.requestFocus();
        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().isEmpty()) {
                    save.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                }else{
                    save.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }
    private void saveText1(){
        cdialog.show();
        final EditText et = cdialog.findViewById(R.id.fname);
        Button save = cdialog.findViewById(R.id.sbut);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et.getText().toString().trim();
                if(name.isEmpty()){
                    et.setError("Enter Filename");
                    et.requestFocus();
                    return;
                }
                saveText2(name);
            }
        });
    }

    private void saveText2(String name){

            myfile recentfile = new myfile(name,"txt",BaseApp.getDir()+"/"+name+"."+"txt",BaseApp.getTime());
            new Sp().addToRecent(recentfile);
            File f=new File(BaseApp.getDir(),name+"."+"txt");
            BufferedWriter writer = null;
            try
            {
                writer = new BufferedWriter( new FileWriter( f));
                writer.write( content.getText().toString());

            }
            catch ( IOException e)
            {
            }
            finally
            {
                try
                {
                    if ( writer != null)
                        cdialog.cancel();
                    Toast.makeText(this, "File Saved", Toast.LENGTH_LONG).show();
                        writer.close( );
                }
                catch ( IOException e)
                {
                }
            }



    }

}
