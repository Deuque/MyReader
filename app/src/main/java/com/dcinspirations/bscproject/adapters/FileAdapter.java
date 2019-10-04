package com.dcinspirations.bscproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.dcinspirations.bscproject.BaseApp;
import com.dcinspirations.bscproject.R;
import com.dcinspirations.bscproject.Reader;
import com.dcinspirations.bscproject.Sp;
import com.dcinspirations.bscproject.models.myfile;
import com.dcinspirations.bscproject.models.file2;
import com.google.gson.Gson;

import java.io.File;
import java.util.List;

/**
 * Created by pc on 2/18/2018.
 */

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.viewHolder>{

    private List<myfile> objectlist;
    private List<file2> objectlist2;
    private LayoutInflater inflater;
    private Context context;
    private String layout;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor spe;

    public FileAdapter(Context context, List<myfile> objectlist, String layout) {
        inflater = LayoutInflater.from(context);
        this.objectlist = objectlist;
        this.context=context;
        this.layout = layout;
        sharedPreferences= context.getSharedPreferences("default",0);
    }



    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if(layout.equalsIgnoreCase("recent")){
            view = inflater.inflate(R.layout.filelayout2,parent,false);
        }else{
            view = inflater.inflate(R.layout.filelayout4,parent,false);
        }
        viewHolder vholder = new viewHolder(view);
        return vholder;
    }

    @Override
    public void onBindViewHolder(viewHolder holder, int position) {
        myfile current = objectlist.get(position);
        holder.setData(current,position);

    }

    @Override
    public int getItemCount() {
        return objectlist.size();
    }

    public void refreshEvents() {
        notifyDataSetChanged();
    }


    public class viewHolder extends RecyclerView.ViewHolder{
        private TextView aname,atime,asize,ltype;
        private ImageView aicon1,aicon2,atype;
        private TextView del;
        private int position;
        private myfile currentObject;

        public void setPosition(int position) {
            this.position = position;
        }

        public viewHolder(final View itemView){
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   File f = new File(BaseApp.getDir().getPath() + "/" + currentObject.getName() + "." + currentObject.getType());
                   myfile f1=new myfile(currentObject.getName() ,currentObject.getType(),currentObject.getUrl(),BaseApp.getTime());
                    new Sp().addToRecent(f1);
                   if(f.exists()) {
                       Gson gson = new Gson();
                       String json = gson.toJson(currentObject);
                       context.startActivity(new Intent(context, Reader.class)
                               .putExtra("currentfile", json));
                   }else{
                       Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show();
                   }



                }
            });
            if(!layout.equalsIgnoreCase("recent")){
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        PopupMenu pm = new PopupMenu(v.getContext(),itemView, Gravity.END);
                        pm.getMenuInflater().inflate(R.menu.main3,pm.getMenu());
                        pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()){
                                    case R.id.remove:
//                                        new Sp().removeRecent(position);
                                        File f = new File(objectlist.get(position).getUrl());
                                        f.delete();
                                        objectlist.remove(position);
                                        refreshEvents();
                                        return true;
                                }
                                return false;
                            }
                        });
                        pm.show();
                        return false;
                    }
                });
            }
            aname = itemView.findViewById(R.id.name);

            try {
                ltype = itemView.findViewById(R.id.type);
                atype = itemView.findViewById(R.id.type);
                aicon1 = itemView.findViewById(R.id.icon1);
                aicon2 = itemView.findViewById(R.id.icon2);
                atime = itemView.findViewById(R.id.time);
            }catch (Exception e){

            }
//            state = itemView.findViewById(R.id.state);
        }



        public void setData(myfile current, int position) {


            if (layout.equalsIgnoreCase("recent")) {
                this.aname.setText(current.getName() + "." + current.getType());
                if (current.getType().equalsIgnoreCase("doc") || current.getType().equalsIgnoreCase("docx")) {
                    this.aicon2.setImageDrawable(context.getResources().getDrawable(R.drawable.doc));
                } else if (current.getType().equalsIgnoreCase("pdf")) {
                    this.aicon2.setImageDrawable(context.getResources().getDrawable(R.drawable.pdf));
                } else {
                    this.aicon2.setImageDrawable(context.getResources().getDrawable(R.drawable.txt2));
                }
                atime.setText(BaseApp.resolveTime(current.getTime()));


            ColorGenerator generator = ColorGenerator.MATERIAL;
            int randomColor = generator.getRandomColor();
            TextDrawable.IBuilder builder = TextDrawable.builder().beginConfig()
                    .endConfig()
                    .round();

            TextDrawable drawable = builder.build(aname.getText().toString().substring(0, 1).toUpperCase(), R.color.colorAccent);
            this.aicon1.setImageDrawable(drawable);
            }else{
                this.aname.setText(current.getName());
                this.ltype.setText(current.getType());
                this.ltype.setBackground(current.getType().equalsIgnoreCase("pdf")?context.getDrawable(R.drawable.bgshape8):
                        current.getType().equalsIgnoreCase("txt")?context.getDrawable(R.drawable.bgshape6):context.getDrawable(R.drawable.bgshape9));
//                if(current.getType().equalsIgnoreCase("pdf")){
//                    this.atype.setImageDrawable(context.getDrawable(R.drawable.pdf2));
//                }else if(current.getType().equalsIgnoreCase("txt")){
//                    this.atype.setImageDrawable(context.getDrawable(R.drawable.txt3));
//                }else{
//                    this.atype.setImageDrawable(context.getDrawable(R.drawable.doc2));
//                }
//                this.asize.setText(current.getTime());
            }

            this.position = position;
            this.currentObject=current;
        }




    }
}
