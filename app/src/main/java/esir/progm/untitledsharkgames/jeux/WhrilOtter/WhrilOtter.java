package esir.progm.untitledsharkgames.jeux.WhrilOtter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import android.widget.ImageView;
import android.widget.TextView;

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
        private final int TIME_OF_GAME = 10000;
        private final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        private static final int MAX_ROTATION_SPEED = 100;
        private static final int MIN_ROTATION_SPEED = 5;
        private static final double MAX_POWER = 200000000.0;
        private static final int UPDATE_INTERVAL = 10;
        private static final int SAMPLE_RATE = 44100;
        private static final double DECREASE_FORCE = 0.01;
        private static final float DELTA_TIME_NORM = 250.0f;
        private static final float ROTATION_NORM = 1000.0f;

        /*                       atributes                       */
        private int nb_tours = 0;
        private int rotationSpeed;
        private long lastUpdateTime;
        private boolean isRecording;
        private AudioRecord audioRecord;
        private short[] buffer;

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
                    // init parameters
                    byte[] buffer = new byte[BUFFER_SIZE];
                    double lastPower = 0.0;
                    double currentPower = 0.0;
                    int rotations = 0;

                    // start recording
                    audioRecord.startRecording();
                    isRecording = true;
                    lastUpdateTime = System.currentTimeMillis();

                    // Main Loop
                    long start = System.currentTimeMillis();
                    while (isRecording && (System.currentTimeMillis()-start <TIME_OF_GAME)) {
                        // read values
                        int bytesRead = audioRecord.read(buffer, 0, BUFFER_SIZE);

                        for (int i = 0; i < bytesRead; i += 2) {
                            short value = (short) ((buffer[i] & 0xFF) | (buffer[i + 1] << 8));
                            currentPower += value * value;
                        }

                        // Compute power
                        double powerDiff = currentPower - lastPower;

                        if (powerDiff > MAX_POWER) {powerDiff = MAX_POWER;}

                        double powerRatio = powerDiff / MAX_POWER;
                        // compute rotation speed => Corrected by ChatGPT
                        rotationSpeed = (int) (MIN_ROTATION_SPEED + (MAX_ROTATION_SPEED - MIN_ROTATION_SPEED) * powerRatio);


                        // update value if necessary
                        long currentTime = System.currentTimeMillis();

                        if (currentTime - lastUpdateTime >= UPDATE_INTERVAL) {
                            lastUpdateTime = currentTime;
                            rotations += rotationSpeed * (currentTime - lastUpdateTime) / ROTATION_NORM;
                            publishProgress((double) rotations);
                        }

                        lastPower = currentPower;
                        currentPower = 0.0;
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
            updateRotation(rotationSpeed);

            // If the image has made a complete rotation, we update counter and reset rotation to zero
            if (imageView.getRotation() <= -360f) {
                nb_tours++;
                textView.setText(String.valueOf((int) nb_tours*50));

                // Réinitialiser la rotation de l'image
                imageView.setRotation(imageView.getRotation() + 360f);
            }
        }

        private void updateRotation(float rotation_speed) {
            // Avoid back rotation and reduce lag
            rotation_speed = - Math.abs(rotation_speed);
            long currentTime = System.currentTimeMillis();

            // update values if nessecary
            if (lastUpdateTime != 0) {
                float timeDelta = (currentTime - lastUpdateTime) / DELTA_TIME_NORM;
                float rotationDelta = rotation_speed * timeDelta + 0.5f * rotation_speed * timeDelta * timeDelta;
                imageView.setRotation(imageView.getRotation() + rotationDelta);
            }

            // update rotation => inertia by ChatGPT
            this.rotationSpeed += rotation_speed * (currentTime - lastUpdateTime) / ROTATION_NORM - DECREASE_FORCE * (currentTime - lastUpdateTime) / ROTATION_NORM;
            lastUpdateTime = currentTime;

            // Limit speed
            if (this.rotationSpeed > MAX_ROTATION_SPEED) {
                this.rotationSpeed = MAX_ROTATION_SPEED;
            }
        }
    }
}