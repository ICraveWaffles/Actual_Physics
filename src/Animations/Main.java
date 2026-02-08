package Animations;

import processing.core.PApplet;

public class Main extends PApplet {

    float w = 53 / 60f;
    int fps = 60;

    kin1 kinBlock;
    kin2 kinWheel;

    int mode = 1;
    public void settings() {
        fullScreen();
    }

    public void setup() {
        frameRate(fps);

        kinBlock = new kin1(this);
        kinWheel = new kin2(this);
    }

    public void draw() {

        float t = frameCount / (float) fps;
        float beats = t * w;

        background(0);

        if (mode == 1) {
            kinBlock.update(beats);
            kinBlock.display(this);

            if (beats > 6) {
                mode = 2;
            }

        } else if (mode == 2) {
            kinWheel.update(beats);
            kinWheel.display(this);
        }
    }

    public static void main(String[] args) {
        PApplet.main("Animations.Main");
    }
}
