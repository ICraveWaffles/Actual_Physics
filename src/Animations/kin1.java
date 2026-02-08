package Animations;

import Classes.Block;
import processing.core.PApplet;

public class kin1 extends PApplet {

    float w = 53 / 60f;
    float t;
    float beats;
    float startTime;

    boolean beatOne = false;
    boolean beatTwo = false;

    Block b1;

    public void setup(){
        b1 = new Block (200, 900, 13, 0, 0, 0, 50, 50, 1);
    }

    public void settings(){
        fullScreen();
    }

    public void draw(){
        t = (float) ((millis() - startTime) / 1000.0);
        beats = t * w;

        background(0);
        b1.display(this);
        b1.update();

        if (beats > 2 && !beatOne){
            b1.vx = 0;
            b1.ay = -0.3f;
            b1.vy = -20;
            beatOne = true;
        }
        if (beats > 4 && !beatTwo){
            b1.vx = -11;
            b1.vy = -20;
            b1.ay = -0.29f;
            beatTwo = true;
        }

        if (beats > 6){
            b1.ay = 0;
            b1.vx = 0;
            b1.vy = 0;
        }
    }

    public static void main(String[]args){
        PApplet.main("Animations.kin1");
    }
}
