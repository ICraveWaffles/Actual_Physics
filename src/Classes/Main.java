package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class Main extends PApplet {

    int beat = 0;
    float t = 0;


    float w = 5/6f;
    PFont ntr;
    float logoTransparency;
    float transX;
    float transY;

    public static LogoAbstract alogo;

    public static Phone phone;

    char[] logoTypes = {'A', 'B', 'C', 'D', 'E', 'I', 'X'};

    boolean logoIntroMove = true;

    public void draw() {

        pushStyle();

        frameRate = 30;
        background(0);
        float b = (frameCount / frameRate) * w;
        textFont(ntr);

        transX = b % 1.25f;
        transY = b % 1f;
        logoTransparency = (float) (255 * (-Math.pow(b < 32? transX : transY, 2) + 2 * (b < 32? transX : transY)));

        fill(logoTransparency);
        int index = ((int) (b / (b < 32 ? 1.25f : 1f))) % logoTypes.length;
        alogo.b = logoTypes[index];
        alogo.display(this, b, logoTransparency);

        logoIntroMove = (b < 17.5f || b > 32.5f);

        if (!logoIntroMove) {
            float ff = w / (106f / 120f);
            alogo.x += 1.7f * ff;
            alogo.y -= 0.9f * ff;
            alogo.w -= 0.75f * ff;
            alogo.h -= 0.75f * ff;
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

            stroke(255);
            strokeWeight(5);

            float progress = constrain(b % 4f, 0, 1);

            float startY = height / 9f;
            float endY = lerp(startY, height / 1.1f, progress);

            line(width/2.15f, startY, width/2f, startY);
            line(width/2f, startY, width/2f, endY);
            line(width/2.15f, endY, width/2f, endY);
        }

        popStyle();
    }

    public void settings() {
        fullScreen();
        frameRate = 30;
    }

    public void setup(){
        ntr = createFont("times.ttf", 50);
        alogo = new LogoAbstract(width/2f, height/2f, height/2f, height/2f);
        phone = new Phone();
        noCursor();
    }

    public static void main(String[] args) {
        PApplet.main("Classes.Main");
    }

    public void mousePressed(){
        t = 0;
        frameCount = 0;
    }
}