package esir.progm.untitledsharkgames.jeux.sharkSlap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Random;

import esir.progm.untitledsharkgames.MusicPlayer;
import esir.progm.untitledsharkgames.R;

public class SharkSlapGame extends AsyncTask<Void, Void, Integer> {

    private final int DURATION_MILLISECONDS = 30000;
    private ArrayList<Place> places;
    private SharkSlap activity;
    private final Random random = new Random();
    private long timeBeforeNext = 0;

    public SharkSlapGame(SharkSlap activity, Context context, ArrayList<Place> places) {
        this.activity = activity;
        this.places = places;
        MusicPlayer.getInstance().play(context, R.raw.shark_slap, true);
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        long startTime = System.currentTimeMillis();
        long last_up = System.currentTimeMillis();

        while(System.currentTimeMillis()-startTime<this.DURATION_MILLISECONDS) {
            if(timeBeforeNext<(System.currentTimeMillis()-last_up)){
                Random r = new Random();
                int to_up = r.nextInt(places.size());
                places.get(to_up).up();
                timeBeforeNext = (long) random.nextInt(2000);
                last_up = System.currentTimeMillis();
            }
        }
        return 0;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        Intent intent = new Intent();
        intent.putExtra("score", activity.getScore());
        activity.setResult(78, intent);
        MusicPlayer.getInstance().stop();
        activity.finish();
    }
}
