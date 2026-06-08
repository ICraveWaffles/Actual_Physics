package Animations;


import Classes.Phone;
import processing.core.PApplet;

public class logoTests extends PApplet {

    Phone p;

    float w = 53 / 60f;
    int fps = 30;

    public void draw(){
        background(0);
        float t = w * frameCount / (float) fps * 2;

        float transX = t % 1.25f;
        float transY = t % 1f;
        float logoTransparency = (float) (255 * (-Math.pow(transX, 2) + 2*(transX)));

        p.display(this, (int) t);
        p.rotate();
    }

    public void settings(){
        fullScreen();
    }

    public void setup(){
        p = new Phone();
    }

    public static void main(String[] args){
        PApplet.main("Animations.logoTests");
    }
}
