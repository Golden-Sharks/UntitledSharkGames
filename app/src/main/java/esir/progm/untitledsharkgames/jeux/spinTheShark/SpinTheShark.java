package esir.progm.untitledsharkgames.jeux.spinTheShark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import esir.progm.untitledsharkgames.MusicPlayer;
import esir.progm.untitledsharkgames.R;

public class SpinTheShark extends AppCompatActivity {
    /*                    hide UI parameters                    */
    private int hideSystemBars = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

    private boolean isOnBackground = false;

    /*                        attributes                        */
    private ImageView imageView;
    private TextView textView;
    private int score;
    private GestureDetector gestureDetector;
    private SpinTheSharkTask task;

    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_spin_the_shark);

        View decor = getWindow().getDecorView();
        decor.setOnSystemUiVisibilityChangeListener(visibility -> {
            if(visibility==0) {
                decor.setSystemUiVisibility(hideSystemBars);
            }
        });

        MusicPlayer.getInstance().stop();
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.spin_the_requin);
        mediaPlayer.start();

        // Init attributes
        imageView = findViewById(R.id.spin_shark);
        textView = findViewById(R.id.spin_score_text);
        score = 0;
        task = new SpinTheSharkTask();
        task.execute();
        gestureDetector = new GestureDetector(this, task);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(hideSystemBars);
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if(level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN ||
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
    private class SpinTheSharkTask extends AsyncTask<Void, Void, Integer> implements GestureDetector.OnGestureListener {
        /*                 Final attributes                 */
        private final long GAME_DURATION = 20000;
        private final double MAX_SWIPE_FORCE = 1000;
        private final long COOLDOWN = 60;

        /*                    Attributes                    */
        private float start_x = 0;
        private float start_y = 0;
        private float end_x = 0;
        private float end_y = 0;
        private boolean asSwiped = false;
        private float angle;
        private float rotation = 0;
        private long last_update = 0;
        private int nb_tours;

        public SpinTheSharkTask() {
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            long start = System.currentTimeMillis();

            while(System.currentTimeMillis() - start < GAME_DURATION) {
                if(System.currentTimeMillis() - last_update > COOLDOWN) {
                    if(asSwiped) {
                        angle = (float) computeSwipeDistance()*90;

                        RotateAnimation rotateAnimation = new RotateAnimation(rotation, angle, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        rotateAnimation.setDuration(500);
                        rotateAnimation.setInterpolator(new DecelerateInterpolator());
                        rotateAnimation.setFillAfter(true);
                        imageView.startAnimation(rotateAnimation);

                        rotation += angle;

                        last_update = System.currentTimeMillis();
                        asSwiped = false;
                    }
                }
                if(rotation - (360*(nb_tours+1)) > 0) {
                    nb_tours++;
                    score += 20;
                    publishProgress();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            textView.setText(score+" pts");
        }

        private double computeSwipeDistance() {
            float dx = end_x - start_x;
            float dy = end_y - start_y;

            return (Math.sqrt(dx*dx + dy*dy))/MAX_SWIPE_FORCE;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            // Creat new intent and push score
            Intent intent = new Intent();
            intent.putExtra("score", score);
            setResult(78, intent);
            mediaPlayer.stop();
            // stop activity
            finish();
        }

        @Override
        public boolean onDown(@NonNull MotionEvent motionEvent) {
            start_x = motionEvent.getX();
            start_y = motionEvent.getY();
            return true;
        }

        @Override
        public boolean onFling(@NonNull MotionEvent motionEvent, @NonNull MotionEvent motionEvent1, float v, float v1) {
            end_x = motionEvent1.getX();
            end_x = motionEvent1.getY();
            asSwiped = true;
            return true;
        }

        @Override
        public boolean onSingleTapUp(@NonNull MotionEvent motionEvent) {return false;}

        @Override
        public boolean onScroll(@NonNull MotionEvent motionEvent, @NonNull MotionEvent motionEvent1, float v, float v1) {return false;}

        @Override
        public void onShowPress(@NonNull MotionEvent motionEvent) {/* ... */}

        @Override
        public void onLongPress(@NonNull MotionEvent motionEvent) {/* ... */}
    }
}