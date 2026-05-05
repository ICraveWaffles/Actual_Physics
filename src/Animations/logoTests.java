package Animations;

import Classes.Alogo;
import Classes.Blogo;
import Classes.Clogo;
import Classes.Dlogo;
import Classes.Elogo;
import processing.core.PApplet;

public class logoTests extends PApplet {

    Alogo a;
    Blogo b;
    Clogo c;
    Dlogo e1;
    Elogo e2;

    float w = 53 / 60f;
    int fps = 30;

    public void draw(){
        background(0);
        float t = w * frameCount / (float) fps;

        a.display(this, t);
        b.display(this, t);
        c.display(this, t);
        e1.display(this, t);
        e2.display(this, t);
    }

    public void settings(){
        fullScreen();
    }

    public void setup(){
        float spacing = width / 6f;

        a = new Alogo(spacing * 1, height / 2, 150, 150);
        b = new Blogo(spacing * 2, height / 2, 150, 150);
        c = new Clogo(spacing * 3, height / 2, 150, 150);
        e1 = new Dlogo(spacing * 4, height / 2, 150, 150);
        e2 = new Elogo(spacing * 5, height / 2, 150, 150);
    }

    public static void main(String[] args){
        PApplet.main("Animations.logoTests");
    }
}
