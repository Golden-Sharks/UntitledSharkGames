package esir.progm.untitledsharkgames;

import android.content.Context;
import android.media.MediaPlayer;
import android.provider.MediaStore;

public class MusicPlayer {
    private static volatile MusicPlayer instance;
    private int position;

    MediaPlayer mediaPlayer = null;
    private MusicPlayer() {
        mediaPlayer = new MediaPlayer();
    }

    public static MusicPlayer getInstance() {
        if(instance == null) {
            instance = new MusicPlayer();
        }
        return instance;
    }

    public void play(Context context, int id, boolean loop) {
        if(mediaPlayer.isPlaying()) {
            stop();
        }
        if(loop) {
            mediaPlayer = MediaPlayer.create(context, id);
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        } else {
            mediaPlayer = MediaPlayer.create(context, id);
            mediaPlayer.start();
        }
    }

    public void stop() {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer = new MediaPlayer();
        }
    }

    public void pause() {
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            position = mediaPlayer.getCurrentPosition();
        }
    }

    public void resume() {
        if(!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(position);
            mediaPlayer.start();
        }
    }
}
