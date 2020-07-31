package com.example.mymusicplayer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

import static com.example.mymusicplayer.AlbumDetailsAdapter.albumFiles;
import static com.example.mymusicplayer.MusicAdapater.mFiles;
import static com.example.mymusicplayer.Screen.musicFiles;
import static com.example.mymusicplayer.Screen.repeatButton;
import static com.example.mymusicplayer.Screen.shuffleButton;

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener{
    TextView song_name,artist_name,duration_played,total_duration;
    FloatingActionButton play_pause;
    SeekBar seek;
    ImageView cover_art,next,previous,back,shuffle,repeat;
    int position=-1;
    static ArrayList<Music> listsongs=new ArrayList<>();
    Uri uri;
    static MediaPlayer mediaPlayer;
    private Handler handler=new Handler();
    private Thread playThread,prevThread,nextThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initView();
        getIntentMethod();
        song_name.setText(listsongs.get(position).getTitle());
        artist_name.setText(listsongs.get(position).getArtist());
        mediaPlayer.setOnCompletionListener(this);
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer!=null && fromUser){
                    mediaPlayer.seekTo(progress*1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null){
                    int currentPosition=mediaPlayer.getCurrentPosition()/1000;
                    seek.setProgress(currentPosition);
                    duration_played.setText(formattedTime(currentPosition));
                }
                handler.postDelayed(this,1000);
            }
        });
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shuffleButton){
                    shuffleButton=false;
                    shuffle.setImageResource(R.drawable.ic_shuffle_on);
                }
                else{
                    shuffleButton=true;
                    shuffle.setImageResource(R.drawable.ic_shuffle_black_24dp);
                }
            }
        });
        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(repeatButton){
                    repeatButton=false;
                    repeat.setImageResource(R.drawable.ic_repeat_on);
                }
                else{
                    repeatButton=true;
                    repeat.setImageResource(R.drawable.ic_repeat_black_24dp);
                }
            }
        });
    }


    @Override
    protected  void onResume(){
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();

        super.onResume();
    }
    private void  playThreadBtn(){
        playThread=new Thread(){
            @Override
            public void run(){
                super.run();
                play_pause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playPausedBtnClicked();

                    }
                });
            }
        };
        playThread.start();
    }

    private void playPausedBtnClicked() {
        if(mediaPlayer.isPlaying()){
            play_pause.setImageResource(R.drawable.ic_play);
            mediaPlayer.pause();
            seek.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int currentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seek.setProgress(currentPosition);
                        duration_played.setText(formattedTime(currentPosition));
                    }
                    handler.postDelayed(this,1000);
                }
            });
        }
        else{
            play_pause.setImageResource(R.drawable.ic_pause_black_24dp);
            mediaPlayer.start();
            seek.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int currentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seek.setProgress(currentPosition);
                        duration_played.setText(formattedTime(currentPosition));
                    }
                    handler.postDelayed(this,1000);
                }
            });
        }

    }

    private void nextThreadBtn() {
        nextThread=new Thread(){
            @Override
            public void run(){
                super.run();
                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextBtnClicked();

                    }
                });
            }
        };
        nextThread.start();
    }

    private void nextBtnClicked() {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleButton && !repeatButton){
                position=getRandom(listsongs.size()-1);
            }
            else if(!shuffleButton && !repeatButton){
                position=((position+1)%listsongs.size());
            }
            uri=Uri.parse(listsongs.get(position).getPath());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listsongs.get(position).getTitle());
            artist_name.setText(listsongs.get(position).getArtist());
            seek.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int currentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seek.setProgress(currentPosition);
                        duration_played.setText(formattedTime(currentPosition));
                    }
                    handler.postDelayed(this,1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            play_pause.setBackgroundResource(R.drawable.ic_pause_black_24dp);
            mediaPlayer.start();
        }
        else{
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleButton && !repeatButton){
                position=getRandom(listsongs.size()-1);
            }
            else if(!shuffleButton && !repeatButton){
                position=((position+1)%listsongs.size());
            }
            uri=Uri.parse(listsongs.get(position).getPath());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listsongs.get(position).getTitle());
            artist_name.setText(listsongs.get(position).getArtist());
            seek.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int currentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seek.setProgress(currentPosition);
                        duration_played.setText(formattedTime(currentPosition));
                    }
                    handler.postDelayed(this,1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            play_pause.setBackgroundResource(R.drawable.ic_play);

        }
    }

    private int getRandom(int i) {
        Random random=new Random();
        return random.nextInt(i+1);
    }

    private void prevThreadBtn(){
        prevThread=new Thread(){
            @Override
            public void run(){
                super.run();
                previous.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prevBtnBtnClicked();

                    }
                });
            }
        };
        prevThread.start();
    }

    private void prevBtnBtnClicked() {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleButton && !repeatButton){
                position=getRandom(listsongs.size()-1);
            }
            else if(!shuffleButton && !repeatButton){
                position=((position-1)%listsongs.size());
            }
            uri=Uri.parse(listsongs.get(position).getPath());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listsongs.get(position).getTitle());
            artist_name.setText(listsongs.get(position).getArtist());
            seek.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int currentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seek.setProgress(currentPosition);
                        duration_played.setText(formattedTime(currentPosition));
                    }
                    handler.postDelayed(this,1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            play_pause.setBackgroundResource(R.drawable.ic_pause_black_24dp);
            mediaPlayer.start();
        }
        else{
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleButton && !repeatButton){
                position=getRandom(listsongs.size()-1);
            }
            else if(!shuffleButton && !repeatButton){
                position=((position-1)%listsongs.size());
            }
            position=((position-1)%listsongs.size());
            uri=Uri.parse(listsongs.get(position).getPath());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listsongs.get(position).getTitle());
            artist_name.setText(listsongs.get(position).getArtist());
            seek.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int currentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seek.setProgress(currentPosition);
                        duration_played.setText(formattedTime(currentPosition));
                    }
                    handler.postDelayed(this,1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            play_pause.setBackgroundResource(R.drawable.ic_play);

        }
    }

    private String formattedTime(int currentPosition) {
        String totalout="";
        String totalnew="";
        String seconds=String.valueOf(currentPosition%60);
        String minutes=String.valueOf(currentPosition/60);
        totalout=minutes+":"+seconds;
        totalnew=minutes+":"+"0"+seconds;
        if(seconds.length()==1){
            return totalnew;
        }
        else{
            return totalout;
        }

    }

    private void getIntentMethod() {
        position=getIntent().getIntExtra("position",-1);
        String sender=getIntent().getStringExtra("sender");
        if(sender!=null && sender.equals("albumDetails")){
            listsongs=albumFiles;
        }
        else{
            listsongs=mFiles;
        }
        if(listsongs!=null){
            play_pause.setImageResource(R.drawable.ic_pause_black_24dp);
            uri=Uri.parse(listsongs.get(position).getPath());
        }
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
        }
        else{
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
        }
        seek.setMax(mediaPlayer.getDuration()/1000);
        metaData(uri);
    }

    private void initView() {
        song_name=findViewById(R.id.song_name);
        artist_name=findViewById(R.id.artist_name);
        duration_played=findViewById(R.id.time);
        total_duration=findViewById(R.id.total_time);
        play_pause=findViewById(R.id.play);
        seek=findViewById(R.id.seekbar);
        cover_art=findViewById(R.id.song_img);
        next=findViewById(R.id.next);
        previous=findViewById(R.id.previous);
        back=findViewById(R.id.back);
        shuffle=findViewById(R.id.shuffle);
        repeat=findViewById(R.id.repeat);

    }
    private void metaData(Uri uri){
        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int duration=Integer.parseInt(listsongs.get(position).getDuration())/1000;
        total_duration.setText(formattedTime(duration));
        byte[] art=retriever.getEmbeddedPicture();
        if(art!=null){
            Glide.with(this).asBitmap().load(art).into(cover_art);
        }
        else{
            Glide.with(this).asBitmap().load(R.drawable.song).into(cover_art);
        }
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        nextBtnClicked();
        if(mediaPlayer!=null){
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
        }
    }
}
