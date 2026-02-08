package Animations;

import processing.core.PApplet;

public class Main extends PApplet {

    float w = 53 / 60f;
    int fps = 30;

    kin1 kinBlock;
    kin2 kinWheel;

    int mode = 1;
    boolean started = false;
    int startFrame;

    public void settings() {
        fullScreen();
    }

    public void setup() {
        frameRate(fps);
        kinBlock = new kin1(this);
        kinWheel = new kin2(this);
    }

    public void draw() {

        background(0);

        if (!started) {
            return;
        }

        float t = (frameCount - startFrame) / (float) fps;
        float beats = t * w;

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

    public void mousePressed() {
        if (!started) {
            started = true;
            startFrame = frameCount;
        }
    }
    public void keyPressed(){
        if (key == 'S' || key == 's') {
            if (!started) {
                started = true;
                startFrame = frameCount;
            }
        }
    }

    public static void main(String[] args) {
        PApplet.main("Animations.Main");
    }
}

