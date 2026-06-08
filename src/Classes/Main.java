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

    public static LogoAbstract alogo;

    public static Phone phone;

    char[] logoTypes = {'A', 'B', 'C', 'D', 'E', 'I', 'X'};

    boolean logoIntroMove = true;

    public void draw() {

        frameRate = 30;
        background(0);
        float b = (frameCount / frameRate) * w;
        textFont(ntr);

        transX = b % 1.25f;
        transY = b % 1f;
        logoTransparency = (float) (255 * (-Math.pow(b < 32? transX : transY, 2) + 2 * (b < 32? transX : transY)));

        fill(logoTransparency);
        text(alogo.b + " P H Y S I C S " + b, 1000, 500);
        int index = ((int) (b / (b < 32 ? 1.25f : 1f))) % logoTypes.length;
        alogo.b = logoTypes[index];
        alogo.display(this, b, logoTransparency);
        fill(255);
        text("FPS: " + frameRate, 50, 50);

        if (b > 32.5f){
            logoIntroMove = true;
        } else if (b > 17.5f && logoIntroMove) {
            logoIntroMove = false;
        }

        if (!logoIntroMove) {
            alogo.x += 1.7f;
            alogo.y -= 0.9f;
            alogo.w -= 0.75f;
            alogo.h -= 0.75f;
        }

        if (b > 17.5f){
            logoIntroMove = true;
        }

        if (b > 32 && b < 64){
            float beatInCycle = b % 4f;

            int phase = 0;

            if (beatInCycle < 1f) {
                phase = (int)(beatInCycle * 8);
            }

            phone.display(this, phase);

            rectMode(CENTER);
            fill(255, 255 - (255 * (b - 32)));
            rect(phone.x, phone.y, phone.w * 1.03f, phone.h * 1.03f, 50);
        }


    }

    public void settings() {
        fullScreen();
        frameRate = 30;
    }

    public void setup(){
        ntr = createFont("times.ttf", 50);
        alogo = new LogoAbstract(width/2f, height/2f, height/2f, height/2f);
        phone = new Phone();
    }

    public static void main(String[] args) {
        PApplet.main("Classes.Main");
    }

}
