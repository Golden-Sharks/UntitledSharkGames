package esir.progm.untitledsharkgames.jeux.sharkWind;

import android.animation.ValueAnimator;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;

public class SharkWindTask extends AsyncTask<Void, Integer, Integer> {
    private static final int SILENCE_THRESHOLD = 500;
    private static final long FRAME_TIME_MS = 16;
    private float mLastRotationSpeed = 0f;
    private boolean mIsSilenceDetected = false;
    private MediaRecorder mediaRecorder;
    private int rotationCount;
    private ImageView imageView;
    private TextView textView;


    public SharkWindTask(MediaRecorder recorder, int rotationCount, ImageView imageView, TextView textView) {
        this.mediaRecorder = recorder;

        this.rotationCount = rotationCount;
        this.imageView = imageView;
        this.textView = textView;
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        int rotationCount = 0;
        while (!isCancelled()) {
            int amplitude = mediaRecorder.getMaxAmplitude();
            if (amplitude > 0 && amplitude < SILENCE_THRESHOLD) {
                mIsSilenceDetected = true;
            } else if (amplitude >= SILENCE_THRESHOLD && mIsSilenceDetected) {
                mIsSilenceDetected = false;
                rotationCount++;
                publishProgress(rotationCount);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return rotationCount;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int numTurns = Math.round(values[0]);
        textView.setText(String.valueOf(numTurns));

        float rotationSpeed = numTurns * 360f / (2 * (float) Math.PI);
        if (rotationSpeed == 0) {
            // Animate a deceleration towards 0 rotation speed
            ValueAnimator animator = ValueAnimator.ofFloat(mLastRotationSpeed, 0f);
            animator.setDuration(500);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float rotationSpeed = (float) valueAnimator.getAnimatedValue();
                    imageView.setRotation(imageView.getRotation() + rotationSpeed * FRAME_TIME_MS / 1000);
                    mLastRotationSpeed = rotationSpeed;
                }
            });
            animator.start();
        } else {
            // Animate towards the new rotation speed
            ValueAnimator animator = ValueAnimator.ofFloat(mLastRotationSpeed, rotationSpeed);
            animator.setDuration(500);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float rotationSpeed = (float) valueAnimator.getAnimatedValue();
                    imageView.setRotation(imageView.getRotation() + rotationSpeed * FRAME_TIME_MS / 1000);
                    mLastRotationSpeed = rotationSpeed;
                }
            });
            animator.start();
        }
    }

    @Override
    protected void onPostExecute(Integer result) {
        // Do something with the final rotation count value.
    }
}