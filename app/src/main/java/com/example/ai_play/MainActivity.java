package com.example.ai_play;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout ParentRelLayout;

    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private String keeper = "";

    private ImageView PreviousBtn,PlayPauseBtn,NextBtn;
    private TextView SongNameTxt;

    private ImageView imageView;

    private RelativeLayout LowerRelLayout;

    private Button VoiceEnableBtn;

    private String mode = "ON";

    private MediaPlayer myMediaPlayer;
    private int position;
    private ArrayList<File> mySongsList;
    private String mSongName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkVoiceCommandPermissions();
        ParentRelLayout = findViewById(R.id.parentRelLayout);

        PreviousBtn = findViewById(R.id.previousBtn);
        PlayPauseBtn = findViewById(R.id.playPauseBtn);
        NextBtn = findViewById(R.id.nextBtn);

        SongNameTxt = findViewById(R.id.songName);

        LowerRelLayout = findViewById(R.id.lower);

        imageView = findViewById(R.id.logo);

        VoiceEnableBtn = findViewById(R.id.voiceEnableBtn);

        speechRecognizer = speechRecognizer.createSpeechRecognizer(MainActivity.this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());


        validateDataAndStartPlaying();

        imageView.setBackgroundResource(R.drawable.logo);


        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results)
            {

                ArrayList<String> matchesFound = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if(matchesFound != null)
                {

                    if (mode == "ON")
                    {

                        keeper = matchesFound.get(0);

                        if(keeper.equals("pause the song"))
                        {

                            PlayPauseSong();
                            Toast.makeText(MainActivity.this, "Command " + keeper, Toast.LENGTH_LONG).show();

                        }
                        else if(keeper.equals("play the song"))
                        {

                            PlayPauseSong();
                            Toast.makeText(MainActivity.this, "Command " + keeper, Toast.LENGTH_LONG).show();

                        }
                        else if(keeper.equals("play next song"))
                        {

                            PlayNextSong();
                            Toast.makeText(MainActivity.this, "Command " + keeper, Toast.LENGTH_LONG).show();

                        }
                        else if(keeper.equals("play previous song"))
                        {

                            PlayPreviousSong();
                            Toast.makeText(MainActivity.this, "Command " + keeper, Toast.LENGTH_LONG).show();

                        }

                    }

                    //Toast.makeText(MainActivity.this, "Result = " + keeper, Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });


        ParentRelLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {

                    case MotionEvent.ACTION_DOWN:
                        speechRecognizer.startListening(speechRecognizerIntent);
                        keeper = "";
                        break;
                    case MotionEvent.ACTION_UP:
                        speechRecognizer.stopListening();
                        break;

                }

                return false;
            }
        });

        VoiceEnableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mode.equals("ON"))
                {

                    mode = "OFF";
                    VoiceEnableBtn.setText("Voice Enabled Mode - OFF");
                    LowerRelLayout.setVisibility(View.VISIBLE);

                }
                else
                {

                    mode = "ON";
                    VoiceEnableBtn.setText("Voice Enabled Mode - ON");
                    LowerRelLayout.setVisibility(View.GONE);

                }

            }
        });

        PlayPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                PlayPauseSong();

            }
        });

        PreviousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(myMediaPlayer.getCurrentPosition()>0)
                {

                    PlayPreviousSong();

                }

            }
        });


        NextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(myMediaPlayer.getCurrentPosition()>0)
                {

                    PlayNextSong();

                }

            }
        });


    }

    private void validateDataAndStartPlaying()
    {

        if(myMediaPlayer != null)
        {

            myMediaPlayer.stop();
            myMediaPlayer.release();

        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        mySongsList = (ArrayList) bundle.getParcelableArrayList("song");
        mSongName =  mySongsList.get(position).getName();

        String SongName = intent.getStringExtra("name");

        SongNameTxt.setText(SongName);
        SongNameTxt.setSelected(true);

        position = bundle.getInt("position",0);
        Uri uri = Uri.parse(mySongsList.get(position).toString());

        myMediaPlayer = MediaPlayer.create(MainActivity.this, uri);
        myMediaPlayer.start();

    }

    private void checkVoiceCommandPermissions()
    {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {

            if(!(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED))
            {

                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();

            }

        }

    }

    private void PlayPauseSong()
    {

        imageView.setBackgroundResource(R.drawable.four);

        if(myMediaPlayer.isPlaying())
        {

            PlayPauseBtn.setImageResource(R.drawable.play);
            myMediaPlayer.pause();

        }
        else
        {

            PlayPauseBtn.setImageResource(R.drawable.pause);
            myMediaPlayer.start();
            imageView.setBackgroundResource(R.drawable.five);

        }

    }

    private void PlayNextSong()
    {

        myMediaPlayer.pause();
        myMediaPlayer.stop();
        myMediaPlayer.release();

        position = ((position+1) % mySongsList.size());
        Uri uri = Uri.parse(mySongsList.get(position).toString());

        myMediaPlayer = MediaPlayer.create(MainActivity.this,uri);

        mSongName = mySongsList.get(position).toString();
        SongNameTxt.setText(mSongName);
        myMediaPlayer.start();

        imageView.setBackgroundResource(R.drawable.three);

        if(myMediaPlayer.isPlaying())
        {

            PlayPauseBtn.setImageResource(R.drawable.pause);

        }
        else
        {

            PlayPauseBtn.setImageResource(R.drawable.play);

            imageView.setBackgroundResource(R.drawable.five);

        }

    }

    private void PlayPreviousSong()
    {

        myMediaPlayer.pause();
        myMediaPlayer.stop();
        myMediaPlayer.release();

        position = ((position - 1) <0 ? (mySongsList.size()-1) : (position-1));

        Uri uri = Uri.parse(mySongsList.get(position).toString());

        myMediaPlayer = MediaPlayer.create(MainActivity.this,uri);

        mSongName = mySongsList.get(position).toString();
        SongNameTxt.setText(mSongName);
        myMediaPlayer.start();

        imageView.setBackgroundResource(R.drawable.two);

        if(myMediaPlayer.isPlaying())
        {

            PlayPauseBtn.setImageResource(R.drawable.pause);

        }
        else
        {

            PlayPauseBtn.setImageResource(R.drawable.play);

            imageView.setBackgroundResource(R.drawable.five);

        }

    }

}