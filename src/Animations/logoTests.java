package Animations;

import Classes.Alogo;
import Classes.Blogo;
import Classes.Clogo;
import Classes.Dlogo;
import Classes.Elogo;
import Classes.Ilogo;
import Classes.Xlogo;
import processing.core.PApplet;

public class logoTests extends PApplet {

    Alogo a;
    Blogo b;
    Clogo c;
    Dlogo e1;
    Elogo e2;
    Ilogo ie;
    Xlogo ee;

    float w = 53 / 60f;
    int fps = 30;

    public void draw(){
        background(0);
        float t = w * frameCount / (float) fps;

        float transX = t % 1.25f;
        float transY = t % 1f;
        float logoTransparency = (float) (255 * (-Math.pow(transX, 2) + 2*(transX)));

        a.display(this, t, logoTransparency);
        b.display(this, t, logoTransparency);
        c.display(this, t, logoTransparency);
        e1.display(this, t, logoTransparency);
        e2.display(this, t, logoTransparency);
        ie.display(this, t, logoTransparency);
        ee.display(this, t, logoTransparency);

    }

    public void settings(){
        fullScreen();
    }

    public void setup(){
        float spacing = width / 8f;

        a = new Alogo(spacing * 1, height / 2, 150, 150);
        b = new Blogo(spacing * 2, height / 2, 150, 150);
        c = new Clogo(spacing * 3, height / 2, 150, 150);
        e1 = new Dlogo(spacing * 4, height / 2, 150, 150);
        e2 = new Elogo(spacing * 5, height / 2, 150, 150);
        ie = new Ilogo(spacing * 6, height / 2, 150, 150);
        ee = new Xlogo(spacing * 7, height / 2, 150, 150);
    }

    public static void main(String[] args){
        PApplet.main("Animations.logoTests");
    }
}
