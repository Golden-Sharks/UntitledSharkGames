package esir.progm.untitledsharkgames.jeux.sharkSlap;

import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;


public class Place {

    private ImageView imageView;
    private String name;
    private SharkSlap game;
    private boolean isHidding;
    private boolean isHidden;

    private final Animation down_animation = new TranslateAnimation(0, 0, 0, 300);
    private final Animation up_animation = new TranslateAnimation(0, 0, 300, 0);

    public Place(String name, ImageView imageView, SharkSlap game){

        this.name = name;
        this.imageView = imageView;
        this.game = game;
        this.isHidding = false;
        isHidden = false;

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
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!isHidding && !isHidden){
                            game.addScore(100, false);
                            down();
                        }
                    }
                });
            }
        });

        init();
    }

    private void init() {
        down_animation.setDuration(1000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                down();
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!isHidding && !isHidden){
                            game.addScore(100, false);
                            down();
                        }
                    }
                });
            }
        }, 1500);



    }

    public boolean getIsHidden() {return  isHidden;}

    public void up() {
        if(isHidden) {
            isHidden = false;
            imageView.startAnimation(up_animation);
        }
    }

    public void down() {
        if(!isHidden) {
            imageView.startAnimation(down_animation);
        }
    }

}
