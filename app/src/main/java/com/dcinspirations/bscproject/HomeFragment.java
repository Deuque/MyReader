package com.dcinspirations.bscproject;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.dcinspirations.bscproject.adapters.FileAdapter;
import com.dcinspirations.bscproject.models.myfile;
import com.dcinspirations.bscproject.models.user;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Uri fileuri;
    FirebaseStorage storage;
    FirebaseDatabase database;
    Context ctx;
    ProgressDialog pd;
    CardView upload,create;
    private static final int req1 = 9;
    private static final int req2 = 56;
    ImageView img1,img2;
    String details[];
    String key;
    List<myfile> myfileList, myfileList2;
    private final String TEMP_FILE_NAME = "wpta_temp_file1.txt";

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    Drawable drawable;
    Bitmap bitmap1, bitmap2;
    ByteArrayOutputStream bytearrayoutputstream;
    ImageView popup,ci,si;
    CircleImageView cimg;
    java.io.File cDir,tempFile;
    RecyclerView rv;
    FileAdapter fa;
    TextView nr,welcome;
    byte[] BYTE;
    LinearLayout icon;

//    CircularFillableLoaders cfl;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_home, container, false);
        ctx = v.getContext();

        cimg = v.findViewById(R.id.cimg);
        String gend = new Sp().getGender();
        cimg.setImageResource(gend.equalsIgnoreCase("male")?R.drawable.contact2:R.drawable.female);
        icon = v.findViewById(R.id.icon);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecticon();
            }
        });
        nr = v.findViewById(R.id.norecent);
        welcome = v.findViewById(R.id.welcome);
        myfileList = new ArrayList<>();
        myfileList2 = new ArrayList<>();
        cDir = getActivity().getBaseContext().getCacheDir();

        details = new String[2];
        key = "Files";
        pd = new ProgressDialog(ctx);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setCancelable(false);
        pd.setTitle("Uploading...");

        final Boolean loggedIn = new Sp().getLoggedIn();
        user cu = new Sp().getUInfo();
            welcome.setText(loggedIn?"Hello "+cu.getUsername()+"!":"Hello user!");

        popup = v.findViewById(R.id.popup);
        popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu pm = new PopupMenu(v.getContext(),popup,Gravity.END);
                if(loggedIn){
                    pm.getMenuInflater().inflate(R.menu.main,pm.getMenu());
                }else{
                    pm.getMenuInflater().inflate(R.menu.main2,pm.getMenu());
                }
                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.action_exit:
                                new Sp().setLoggedIn(false);
                                getActivity().recreate();
                                return true;
                            case R.id.login:
                                startActivity(new Intent(ctx,Validation.class).putExtra("state","login"));
                                return true;
                            case R.id.register:
                                startActivity(new Intent(ctx,Validation.class).putExtra("state","register"));
                                return true;
                        }
                        return false;
                    }
                });
                pm.show();
            }
        });

        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();

        upload = v.findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPerm();

            }
        });
        create = v.findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx,Splitter.class));

            }
        });


        rv =  v.findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(v.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);

        rv.setItemAnimator(new DefaultItemAnimator());

        return  v;
    }


    private void selecticon(){
        PopupMenu pm = new PopupMenu(getView().getContext(),cimg,Gravity.END);

            pm.getMenuInflater().inflate(R.menu.main4,pm.getMenu());

        pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.male:
                        cimg.setImageResource(R.drawable.contact2);
                        new Sp().setGender("male");
                        return true;
                    case R.id.female:
                        cimg.setImageResource(R.drawable.female);
                        new Sp().setGender("female");
                        return true;
                }
                return false;
            }
        });
        pm.show();
    }
    public void checkPerm(){
        if(ContextCompat.checkSelfPermission(getView().getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
        {
            selectFile();
        }else{
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},req1);
            checkPerm();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       if(requestCode==req1&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
       {
           selectFile();
       }else{
           Toast.makeText(ctx, "You need to give permission", Toast.LENGTH_SHORT).show();
       }
    }

    private void selectFile() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        String [] mimeTypes = {"application/msword", "text/plain","application/pdf","application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent,req2);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==req2 && resultCode==getActivity().RESULT_OK && data!=null)
        {
            fileuri = data.getData();
            details = BaseApp.getFileDetails(fileuri.getPath());
            saveFile(fileuri);
        }else{
            Toast.makeText(ctx, "Please Select a file", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveFile(Uri uri){
        try
        {
            myfile recentfile = new myfile(details[0],details[1],BaseApp.getDir()+"/"+details[0]+"."+details[1],BaseApp.getTime());
            new Sp().addToRecent(recentfile);
            File f=new File(BaseApp.getDir(),details[0]+"."+details[1]);
                InputStream inputStream = ctx.getContentResolver().openInputStream(uri);
                OutputStream out = new FileOutputStream(f);
                byte buf[] = new byte[1024];
                int len;
                while ((len = inputStream.read(buf)) > 0)
                    out.write(buf, 0, len);
                out.close();
                inputStream.close();

        }
        catch (IOException e){
            Toast.makeText(ctx, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public void populateRecent(){

        myfileList2 = new Sp().getRecent();
        fa = new FileAdapter(ctx, myfileList2,"recent");
        if(myfileList2.isEmpty()){
            nr.setVisibility(View.VISIBLE);
        }else{
            nr.setVisibility(View.GONE);
        }
        rv.setAdapter(fa);
        fa.notifyDataSetChanged();
    }


    @Override
    public void onResume() {
        super.onResume();
        populateRecent();
        final Boolean loggedIn = new Sp().getLoggedIn();
        user cu = new Sp().getUInfo();
        welcome.setText(loggedIn?"Hello "+cu.getUsername()+"!":"Hello user!");
    }
}