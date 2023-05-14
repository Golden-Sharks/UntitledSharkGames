package esir.progm.untitledsharkgames.jeux.sharkSlap;

import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;


public class Place {
    /*                      attributes                       */
    private boolean isHidding;
    private boolean isHidden;
    private ImageView imageView;
    private SharkSlap game;
    private final Animation down_animation = new TranslateAnimation(0, 0, 0, 300);
    private final Animation up_animation = new TranslateAnimation(0, 0, 300, 0);

    public Place(ImageView imageView, SharkSlap game){
        // Init parameters
        this.imageView = imageView;
        this.game = game;
        this.isHidding = false;
        isHidden = false;

        // Set animations
        down_animation.setDuration(1000);
        down_animation.setFillAfter(true);
        up_animation.setDuration(500);
        up_animation.setFillAfter(true);

        down_animation.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation arg0) {imageView.setOnClickListener(null);}
            @Override
            public void onAnimationRepeat(Animation arg0) {/* */}
            @Override
            public void onAnimationEnd(Animation arg0) {
                isHidden = true;
                isHidding = false;
                imageView.setOnClickListener(v -> {
                    if(!isHidding && !isHidden){
                        game.addScore(100, false);
                        down();
                    }
                });
            }
        });

        // Init the game
        init();
    }

    private void init() {
        down_animation.setDuration(1000);
        new Handler().postDelayed(() -> {
            down();
            imageView.setOnClickListener(v -> {
                if(!isHidding && !isHidden){
                    game.addScore(100, false);
                    down();
                }
            });
        }, 1500);
    }

    public void up() {
        if(isHidden) {
            isHidden = false;
            imageView.startAnimation(up_animation);
        }
    }

    public void down() {
        if(!isHidden) {imageView.startAnimation(down_animation);}
    }

}
