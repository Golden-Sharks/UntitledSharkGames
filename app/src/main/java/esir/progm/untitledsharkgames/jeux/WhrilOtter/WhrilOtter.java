package esir.progm.untitledsharkgames.jeux.WhrilOtter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.AsyncTaskLoader;

import android.Manifest;
import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.nio.channels.AsynchronousByteChannel;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import esir.progm.untitledsharkgames.MusicPlayer;
import esir.progm.untitledsharkgames.R;

public class WhrilOtter extends AppCompatActivity {
    /*                    hide UI parameters                    */
    private int hideSystemBars = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    private boolean isOnBackground = false;

    /*                    activity parameters                   */
    private ImageView imageView;
    private TextView textView;
    private WhrilOtterTask whrilOtterTask;
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Init activity and disable UI
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_whirl_otter);

        View decor = getWindow().getDecorView();
        decor.setOnSystemUiVisibilityChangeListener(visibility -> {
            if (visibility == 0) {
                decor.setSystemUiVisibility(hideSystemBars);
            }
        });

        // Get layout elements
        imageView = findViewById(R.id.otter);
        textView = findViewById(R.id.wind_score_text);

        // Start game
        whrilOtterTask = new WhrilOtterTask();
        whrilOtterTask.execute();


    }

    @Override
    protected void onPause() {
        super.onPause();
        // stop music player when activity is on pause
        if (isOnBackground) {
            MusicPlayer.getInstance().pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isOnBackground) {
            MusicPlayer.getInstance().stop();
            MusicPlayer.getInstance().play(getApplicationContext(), R.raw.main_menu, true);
        } else {
            MusicPlayer.getInstance().resume();
            isOnBackground = false;
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN ||
                level == ComponentCallbacks2.TRIM_MEMORY_BACKGROUND ||
                level == ComponentCallbacks2.TRIM_MEMORY_MODERATE ||
                level == ComponentCallbacks2.TRIM_MEMORY_COMPLETE) {
            isOnBackground = true;
            onPause(); // https://i.kym-cdn.com/photos/images/original/000/639/420/094.gif
        }
    }

    /**********************************************************************************************/
    /*                                         Async task                                         */
    /**********************************************************************************************/
    private class WhrilOtterTask extends AsyncTask<Void, Double, Integer> {
        /*                    Final atributes                    */
        private final int GAME_DURATION = 20000;
        private final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        private final int NOISE_THRESHOLD = 2000000;
        private static final int SAMPLE_RATE = 44100;
        private final long COOLDOWN = 150;

        /*                       atributes                       */
        private int nb_tours = 0;
        private boolean isRecording;
        private AudioRecord audioRecord;
        byte[] buffer = new byte[BUFFER_SIZE];
        private float angle;
        private float rotation = 0;
        private long last_update = 0;

        public WhrilOtterTask() {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(WhrilOtter.this, new String[] { Manifest.permission.RECORD_AUDIO }, 0);
            }
            this.audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);
        }

        @Override
        protected void onCancelled() {
            if (audioRecord != null) {
                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            // Check if permission was granted
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                // Ask for permission if not
                ActivityCompat.requestPermissions(WhrilOtter.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
            } else {
                try {
                    // start recording
                    audioRecord.startRecording();
                    isRecording = true;
                    last_update = System.currentTimeMillis();

                    // Main Loop
                    long start = System.currentTimeMillis();
                    while(System.currentTimeMillis() - start < GAME_DURATION) {
                        if(System.currentTimeMillis() - last_update > COOLDOWN) {
                            if(computePower()>10) {
                                angle = (float) (computePower()*0.5);

                                RotateAnimation rotateAnimation = new RotateAnimation(rotation, angle, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                rotateAnimation.setDuration(500);
                                rotateAnimation.setInterpolator(new DecelerateInterpolator());
                                rotateAnimation.setFillAfter(true);
                                imageView.startAnimation(rotateAnimation);

                                rotation += angle;

                                last_update = System.currentTimeMillis();
                                publishProgress();
                            }
                        }
                    }

                    // Stop and release recorder
                    audioRecord.stop();
                    audioRecord.release();
                    audioRecord = null;

                } catch (Exception e) {
                    Log.e("WhrilOtterTask", "Error in background thread", e);
                }
            }
            return 0;
        }

        private double computePower() {
            double res = 0;
            int bytesRead = audioRecord.read(buffer, 0, BUFFER_SIZE);

            for (int i = 0; i < bytesRead; i += 2) {
                short value = (short) ((buffer[i] & 0xFF) | (buffer[i + 1] << 8));
                res += value * value;
            }

            res -= NOISE_THRESHOLD;
            if (res<0) {res = 0;}

            long ratio = 1000000000;
            res = (res/ratio)/10;

            if(res < 0.01)
                res = 0;

            return res;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            // Creat new intent and push score
            Intent intent = new Intent();
            intent.putExtra("score", nb_tours*50);
            setResult(78, intent);
            // stop media player
            MusicPlayer.getInstance().stop();
            // stop activity
            finish();
        }

        @Override
        protected void onProgressUpdate(Double... values) {
            super.onProgressUpdate(values);
            if(rotation - (360*nb_tours+1) > 360) {
                score += 20;
                textView.setText(score + " pts");
            }
            textView.setText(score+" pts");
        }
    }
}