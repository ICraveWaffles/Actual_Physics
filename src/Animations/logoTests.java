package Animations;

import Classes.Alogo;
import Classes.Clogo;
import Classes.Elogo;
import processing.core.PApplet;


public class logoTests extends PApplet {

    Elogo test;

    float w = 53 / 60f;
    int fps = 30;

    public void draw(){
        background(0);
        float t = w*(frameCount) / (float) fps;
        test.display(this, t);
    }

    public void settings(){
        fullScreen();
    }

    public void setup(){
        test = new Elogo(width/2, height/2, 250, 250);
    }

    public static void main(String[]args){
        PApplet.main("Animations.logoTests");
    }
}
