package com.example.pro_music_player;

import android.appwidget.AppWidgetProvider;
import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView songlist;
    SeekBar bar;
    MediaPlayer player = new MediaPlayer();
    int x;
    String[] items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        songlist = findViewById(R.id.songlist);
        bar = findViewById(R.id.seekBar);

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                x = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                player.seekTo(x);

            }
        });
        getsongs();


        mythread mythread = new mythread();
        mythread.start();
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, songs);
        songlist.setAdapter(arrayAdapter);

        songlist.setAdapter(new myadapter());
        songlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                try {
                    player.stop();
                    player = new MediaPlayer();
                    player.setDataSource(songs.get(position).path);
                    player.prepare();
                    player.start();
                    bar.setMax(player.getDuration());

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
    }


    ArrayList<songinfo> songs = new ArrayList<songinfo>();

    public void getsongs() {
        ContentResolver contentResolver = getContentResolver();
        Uri externalContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " !=0";

        Cursor mcursor = contentResolver.query(externalContentUri, null, selection, null, null);


        if (mcursor != null && mcursor.moveToFirst()) {
            do {
                String path = mcursor.getString(mcursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DATA));
                String name = mcursor.getString(mcursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DISPLAY_NAME));
                String artist = mcursor.getString(mcursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST));
                songs.add(new songinfo(path, name, artist));


            } while (mcursor.moveToNext());
        }
    }


    public void start(View view) {
        player.start();

    }

    public void pause(View view) {
        player.pause();

    }

    public void stop(View view) {
        player.stop();

    }

    class mythread extends Thread {
        @Override
        public void run() {
            while (player != null) {
                bar.setProgress(player.getCurrentPosition());
            }
        }
    }

    class myadapter extends BaseAdapter {

        @Override
        public int getCount() {
            return songs.size();
        }

        @Override
        public Object getItem(int position) {
            return songs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View view1 = inflater.inflate(R.layout.muconytent, null);
            TextView sondn = view1.findViewById(R.id.songn);
            TextView songa = view1.findViewById(R.id.songa);
            sondn.setText(songs.get(position).songname);
            songa.setText(songs.get(position).songartist);
            return view1;
        }
    }
}
