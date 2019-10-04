package com.dcinspirations.bscproject;

import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dcinspirations.bscproject.adapters.FileAdapter;
import com.dcinspirations.bscproject.models.TTsSettings;
import com.dcinspirations.bscproject.models.myfile;
import com.dcinspirations.bscproject.models.user;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class OtherFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String name;
    public Context ctx;
    WebView wc;
    ViewStub vs;
    Button download, read;
    RecyclerView prv;
    FloatingActionButton add;

    private OnFragmentInteractionListener mListener;

    public OtherFragment() {
    }

    public static OtherFragment newInstance(String param1) {
        OtherFragment fragment = new OtherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }

    }

    TextView t;
    AutoCompleteTextView act;
    ImageView foreimg;
    SwipeRefreshLayout refreshLayout;
    CircleImageView backimg;

    TextView username, status, reset;
    ImageView img;
    SeekBar sr, pitch;
    Button bk, rf;
    LinearLayout ll;
    RelativeLayout content;
    LinearLayout prvl;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_other, container, false);

        ctx = view.getContext();

        t = view.findViewById(R.id.name);
        vs = view.findViewById(R.id.other);
        t.setText(mParam1);

        switch (mParam1) {
            case "Document Library":
                view = inflater.inflate(R.layout.library, container, false);
                prv = view.findViewById(R.id.prv);
                prvl = view.findViewById(R.id.prvlayout);
                act = view.findViewById(R.id.search);
                ll = view.findViewById(R.id.nothing);
                refreshLayout = view.findViewById(R.id.refresh);
//                 add = view.findViewById(R.id.add);
//                 add.setOnClickListener(new View.OnClickListener() {
//                     @Override
//                     public void onClick(View v) {
//
//                     }
//                 });
                break;
            case "Settings":
                view = inflater.inflate(R.layout.activity_settings, container, false);
                username = view.findViewById(R.id.username);
                status = view.findViewById(R.id.status);
//                img = view.findViewById(R.id.edit);
                sr = view.findViewById(R.id.sr);
                pitch = view.findViewById(R.id.p);
                bk = view.findViewById(R.id.backup);
                rf = view.findViewById(R.id.rf);
                reset = view.findViewById(R.id.reset);
                content = view.findViewById(R.id.content);
                backimg = view.findViewById(R.id.backimg);


        }


        return view;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    class Library {
        List<myfile> myfileList;
        FileAdapter fa;
        ArrayList<String> filearray;

        public Library(final Context ctx) {
            refreshLayout.setColorSchemeResources(
                    R.color.aux8,
                    R.color.aux7,
                    R.color.aux9);
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    populate();
                }
            });
            myfileList = new ArrayList<>();
            fa = new FileAdapter(ctx, myfileList, "library");
            LinearLayoutManager llm = new LinearLayoutManager(ctx);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            prvl.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            prvl.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int vw = prvl.getWidth();
                            int b = 480;
                            int spans = (int) Math.floor(vw/b);
                            prv.setLayoutManager(new GridLayoutManager(ctx, spans));
                        }
                    }
            );

            prv.setAdapter(fa);
            prv.setItemAnimator(new DefaultItemAnimator());
        }



        public void populate() {
            File[] files = BaseApp.getDir().listFiles();
            filearray = new ArrayList<>();
            myfileList.clear();
            for (File f : files) {
                myfile fm = new myfile();
                String details[] = BaseApp.getFileDetails(f.getPath());
                filearray.add(details[0] + "." + details[1]);
                fm.setName(details[0]);
                fm.setType(details[1]);
                fm.setTime(formatSize(f.length()));
                fm.setUrl(BaseApp.getDir().getPath() + "/" + details[0] + "." + details[1]);
                myfileList.add(0, fm);
            }
            if (myfileList.isEmpty()) {
                ll.setVisibility(View.VISIBLE);
            } else {
                ll.setVisibility(View.GONE);
            }
            fa.notifyDataSetChanged();
            setAutoComplete();
            refreshLayout.setRefreshing(false);
        }

        String formatSize(long len) {
            double d = len / 1024;
            if (d < 1024) {
                String dd = String.format("%.2f", d) + "kb";
                return dd;
            } else {
                d = d / 1024;
                String dd = String.format("%.2f", d) + "mb";
                return dd;
            }

        }

        public void setAutoComplete() {
            Collections.sort(filearray);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_list_item_1, filearray.toArray(new String[filearray.size()]));
            act.setAdapter(adapter);
            act.setDropDownBackgroundResource(R.color.aux1);
            act.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String name = act.getText().toString();
                    File f = new File(BaseApp.getDir().getPath() + "/" + name);
                    myfile selectedfile = new myfile(name.substring(0, name.indexOf(".")), name.substring(name.indexOf(".") + 1, name.length()), f.getPath(), "");
                    if (f.exists()) {
                        Gson gson = new Gson();
                        String json = gson.toJson(selectedfile);
                        ctx.startActivity(new Intent(ctx, Reader.class)
                                .putExtra("currentfile", json));
                    } else {
                        Toast.makeText(ctx, "File not found", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(ctx, selectedfile.getName() + selectedfile.getType(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    class Settings {
        Context ctx1;
        ProgressDialog pd;
        final Dialog osdialog;

        public Settings(final Context ctx) {
            ctx1 = ctx;
            osdialog = new Dialog(ctx);
            osdialog.setContentView(R.layout.uploadlayout);

            Window window = osdialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.BOTTOM;
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            String gend = new Sp().getGender();
            backimg.setImageResource(gend.equalsIgnoreCase("male")?R.drawable.contact2:R.drawable.female);




            pd = new ProgressDialog(ctx1);
            pd.setCancelable(false);
            Boolean loggedIn = new Sp().getLoggedIn();
            if (loggedIn) {
                user cu = new Sp().getUInfo();
                username.setText(cu.getUsername());
                status.setText(cu.getStatus());

            } else {
                username.setText("Login");
                status.setText("Register");
                username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toValidate("login");
                    }
                });
                status.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toValidate("register");
                    }
                });

            }
            TTsSettings ttss = new Sp().getTTs();
            if (ttss.getPitch() != 0 && ttss.getSr() != 0) {
                sr.setProgress(ttss.getSr());
                pitch.setProgress(ttss.getPitch());
            } else {
                sr.setProgress(10);
                pitch.setProgress(11);
            }
            sr.setOnSeekBarChangeListener(sol);
            pitch.setOnSeekBarChangeListener(sol);
            reset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Sp().setTTs(0, 0);
                    sr.setProgress(10);
                    pitch.setProgress(11);
                }
            });
            bk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (new Sp().getLoggedIn()) {
                        setFile();
                    } else {
                        Toast.makeText(ctx, "You are not logged in", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            rf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (new Sp().getLoggedIn()) {
                        RetrieveFile();
                    } else {
                        Toast.makeText(ctx, "You are not logged in", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        private SeekBar.OnSeekBarChangeListener sol = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() > 0) {
                    new Sp().setTTs(sr.getProgress(), pitch.getProgress());
                }
            }
        };

        public void toValidate(String state) {
            startActivity(new Intent(ctx, Validation.class).putExtra("state", state));
        }

        public void setFile() {
            editDialog("Initiating Backup...");
            osdialog.show();
            final ArrayList<myfile> myfiles = new ArrayList<>();
            final ArrayList<File> files2 = new ArrayList<>();
            DatabaseReference reference = FirebaseDatabase.getInstance()
                    .getReference().child("Users").child(new Sp().getUInfo().getKey()).child("Files");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        myfile f1 = snapshot.getValue(myfile.class);
                        myfiles.add(f1);
                    }
                    int count = 0;
                    if(myfiles.isEmpty()) {
                            for (File f : BaseApp.getDir().listFiles()) {
                                files2.add(f);
                            }

                        if (!files2.isEmpty()) {
                            editDialog("Finishing Up...");
                            setFile2(files2);
                        } else {
                            osdialog.cancel();
                            showSnack("No File to Back up.");
                        }
                    }else {

                        for (File f : BaseApp.getDir().listFiles()) {
//                        pd.setMessage("Backing Up Files...");
//                        pd.show();
                            Boolean isthere = false;
                            String details[] = BaseApp.getFileDetails(f.getPath());
                            for (myfile f1 : myfiles) {
                                if (f1.getName().equalsIgnoreCase(details[0])) {
                                    isthere = true;
                                    break;
                                }

                            }
                            count = count + 1;
                            if (!isthere) {
//                            Toast.makeText(ctx1, "Backing Up Files...", Toast.LENGTH_LONG).show();
//
                                files2.add(f);
                            }
//                        pd.cancel();


                        }
                        if (!files2.isEmpty()) {
                            editDialog("Finishing Up...");
                            setFile2(files2);
                        } else {
                            if(BaseApp.getDir().listFiles().length==0) {
                                osdialog.cancel();
                                showSnack("No File to Back up.");
                            }else{
                                osdialog.cancel();
                                showSnack("Files already Backed Up.");
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    showSnack("Check your network connection.");
                }
            });




        }

        private void setFile2(ArrayList<File> files2){


            int  count = 0;
            for(File f: files2){
                count+=1;
                uploadFile(f);

            }
            if(count==files2.size()){

                showSnack("Files uploaded successfully.");
                osdialog.cancel();
            }
        }
        private void editDialog(String message){
            TextView t = osdialog.findViewById(R.id.info);
            t.setText(message);
        }
        private void showSnack(String message){
            Snackbar.make(content,message,Snackbar.LENGTH_LONG).show();
        }
        private void uploadFile(File file) {
            Uri uri = Uri.fromFile(file);
            final String details[] = BaseApp.getFileDetails(file.getPath());
            final String filename = System.currentTimeMillis() + "";
            final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("FileUploads")
                    .child(new Sp().getUInfo().getKey()).child(details[0] + "." + details[1]);
            storageReference.putFile(uri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {

                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String duri = uri.toString();
                                        DatabaseReference reference = FirebaseDatabase.getInstance()
                                                .getReference().child("Users").child(new Sp().getUInfo().getKey()).child("Files")
                                                .child(details[0] + details[1]);
                                        reference.child("name").setValue(details[0]);
                                        reference.child("type").setValue(details[1]);
                                        reference.child("time").setValue(BaseApp.getTime());
                                        reference.child("url").setValue(duri)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (!task.isSuccessful()) {
                                                            Toast.makeText(ctx, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                        } else {
                                                        }
                                                    }
                                                });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.cancel();
                                        Toast.makeText(ctx, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
//
//

                            } else {
                                pd.cancel();
                                Toast.makeText(ctx, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });


        }

        public void RetrieveFile() {
            showSnack("Getting a few things ready...");
            final ArrayList<myfile> myfiles = new ArrayList<>();
            final ArrayList<myfile> urls = new ArrayList<>();
            DatabaseReference reference = FirebaseDatabase.getInstance()
                    .getReference().child("Users").child(new Sp().getUInfo().getKey()).child("Files");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        myfile f1 = snapshot.getValue(myfile.class);
                        myfiles.add(f1);
                    }
                    int count = 0;
                    if(myfiles.isEmpty()) {

                            osdialog.cancel();
                            showSnack("You have no Backup.");

                    }else {
                        for (myfile f : myfiles) {
//                        pd.setMessage("Downloading Files...");
//                        pd.show();
                            Boolean isthere = false;
                            count = count + 1;
                            for (File f1 : BaseApp.getDir().listFiles()) {
                                String details[] = BaseApp.getFileDetails(f1.getPath());
                                if (details[0].equalsIgnoreCase(f.getName())) {
                                    isthere = true;
                                    break;
                                }
                            }
                            if (!isthere) {
//                            Toast.makeText(ctx1, "Downloading", Toast.LENGTH_LONG).show();
                                urls.add(f);

                            }

//                        if (count == myfiles.size()) {
//                            getActivity().finish();
//                            startActivity(new Intent(ctx1, MainActivity.class));
//                        }

//                        pd.cancel();
                        }
                        if (!urls.isEmpty()) {
                            RetrieveFiles2(urls);
                        }else{
                            showSnack("You already have these files");
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    showSnack("Check your network connection");
                }
            });

        }
        private void RetrieveFiles2(ArrayList<myfile> urls){
            showSnack("Download Started, Check Notifications");
            for(myfile mf:urls) {
                download(mf.getUrl(),mf.getName()+"."+mf.getType());
            }
            showSnack("Download Completed, Check Notifications");

//
//            final Handler h = new Handler();
//            ProgressBar pb = osdialog.findViewById(R.id.progress);
//            pb.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    osdialog.cancel();
//                }
//            });
//            TextView t = osdialog.findViewById(R.id.count);
//            TextView t2 = osdialog.findViewById(R.id.percent);
//            TextView t3 = osdialog.findViewById(R.id.title);
//            t3.setText("Retrieving Files...");
//            osdialog.show();
//
//            int  count = 0;
//
//
//                for(String s: urls){
//                    count+=1;
//                    double a = count;
//                    double b = urls.size();
//                    double c = (a/b)*100;
//                    pb.setProgress((int)c);
//                    t.setText(Integer.toString(count)+"/"+Integer.toString(urls.size()));
//                    t2.setText(Double.toString(c));
//                    if(download(s)&&count<urls.size())
//                    {
//                        continue;
//                    }else{
////                    osdialog.cancel();
//                    }
//                }
//
////            if(count==urls.size()){
////                Toast.makeText(ctx1, "Files Downloaded Successfully", Toast.LENGTH_LONG).show();
////                osdialog.cancel();
////            }
        }

        public void download(String url,String name) {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE|
                   DownloadManager.Request.NETWORK_MOBILE );
            request.setTitle("Download of "+name+" for BscProject");
            request.setDescription("Downloading "+name);
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalFilesDir(ctx1,"file_download",""+name);

            DownloadManager manager = (DownloadManager) ctx1.getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
//            final List<Boolean> status=new ArrayList<>();
//            status.add(0,false);
//            Loader.with(ctx1)
//                    .addInQueue(url)
//                    .to(BaseApp.getDir())
//                    .redownloadAttemptCount(8)
//                    .enableLogging()
//                    .downloadReceiver()
//                    .onCompleted(new OnCompleted() {
//                        @Override
//                        public void apply() {
//                            status.add(0,true);
////                            Toast.makeText(ctx, "Download completed", Toast.LENGTH_SHORT).show();
//
//                        }
//                    })
//                    .onError(new OnError() {
//                        @Override
//                        public void apply(String s, Throwable throwable) {
//                            status.add(0,false);
//                            Toast.makeText(ctx, throwable.getMessage(), Toast.LENGTH_LONG).show();
//
//                        }
//                    })
//                    .load();
//            return status.get(0);
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        switch (mParam1) {
            case "Document Library":
                new Library(ctx).populate();
                break;
            case "Settings":
                new Settings(ctx);
                break;
        }
    }
}
