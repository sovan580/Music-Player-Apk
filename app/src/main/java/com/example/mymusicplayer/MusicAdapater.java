package com.example.mymusicplayer;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;

public class MusicAdapater extends RecyclerView.Adapter<MusicAdapater.MyViewHolder> {
    private Context mContext;
    static ArrayList<Music> mFiles;
    MusicAdapater(Context mContext,ArrayList<Music> mFiles){
        this.mFiles=mFiles;
        this.mContext=mContext;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.items,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.file_name.setText(mFiles.get(position).getTitle());
        byte[] image=getAlbumArt(mFiles.get(position).getPath());
        if(image!=null){
            Glide.with(mContext).asBitmap().load(image).into(holder.music_img);
        }
        else{
            Glide.with(mContext).load(R.drawable.song).into(holder.music_img);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(mContext,PlayerActivity.class);
                i.putExtra("position",position);
                mContext.startActivity(i);
            }
        });
        holder.menumore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PopupMenu popupMenu=new PopupMenu(mContext,v);
                popupMenu.getMenuInflater().inflate(R.menu.popup,popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch ((item.getItemId())){
                            case R.id.delete:
                                Toast.makeText(mContext,"Delete Clicked!!!",Toast.LENGTH_SHORT).show();
                                deleteFile(position,v);
                                break;
                        }
                        return true;
                    }
                });
            }
        });
    }
    private  void deleteFile(int position,View v){
        Uri contextUri= ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,Long.parseLong(mFiles.get(position).getId()));
        File file=new File(mFiles.get(position).getPath());
        boolean deleted=file.delete();
        if(deleted){
            mContext.getContentResolver().delete(contextUri,null,null);
            mFiles.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position,mFiles.size());
            Snackbar.make(v,"File Deleted ",Snackbar.LENGTH_LONG).show();
        }
        else{
            Snackbar.make(v,"File can't be Deleted ",Snackbar.LENGTH_LONG).show();
        }

    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView file_name;
        ImageView music_img,menumore;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            file_name=itemView.findViewById(R.id.file_name);
            music_img=itemView.findViewById(R.id.music_img);
            menumore=itemView.findViewById(R.id.menudelete);

        }
    }
    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art=retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
    void  UpdateList(ArrayList<Music> musicArrayList){
        mFiles=new ArrayList<>();
        mFiles.addAll(musicArrayList);
        notifyDataSetChanged();
    }
}
