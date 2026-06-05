package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class Main extends PApplet {

    int beat = 0;
    float t = 0;

    float w = 106 / 120f;
    PFont ntr;
    float logoTransparency;
    float transX;
    float transY;

    LogoAbstract alogo;

    char[] logoTypes = {'A', 'B', 'C', 'D', 'E', 'I', 'X'};

    public void draw(){
        frameRate = 30;
        background(0);
        float b = (frameCount/frameRate) * w;
        textFont(ntr);

        transX = b % 1.25f;
        transY = b % 1f;
        logoTransparency = (float) (255 * (-Math.pow(transX, 2) + 2*(transX)));



        fill(logoTransparency);
        text(alogo.b +" P H Y S I C S "+ b, 1000, 500);
        int index = ((int)(b / 1.25f)) % logoTypes.length;
        alogo.b = logoTypes[index];
        alogo.display(this, b, logoTransparency);
        fill(255);
        text("FPS: " + frameRate, 50, 50);
    }

    public void settings() {
        fullScreen();
        frameRate = 30;
    }

    public void setup(){
        ntr = createFont("times.ttf", 50);
        alogo = new LogoAbstract(width/2f, height/2f, height/2f, height/2f);
    }

    public static void main(String[] args) {
        PApplet.main("Classes.Main");
    }

}
