package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class Kin1 extends PApplet {

    int beat = 0;
    float t = 0;

    float w = 106 / 120f;
    PFont ntr;
    float logoTransparency;
    float transX;
    float transY;

    public static Alogo alogo;

    float x, y, px, py;

    public void draw() {

        pushStyle();

        frameRate = 30;
        t = frameCount / (float) frameRate;
        if (t > 2.26415f*2) {frameCount = 0; t = 0; beat = 0;}

        background(0);

        float b = t * w;
        textFont(ntr);

        transY = b % 1f;
        logoTransparency = (float) (255 * (-Math.pow(transY, 2) + 2 * (transY)));

        alogo.display(this, b, logoTransparency);

        float x0 = 240 + 200 * 0 + 400 * sin(2 * 0);
        float y0 = 540 + 300 * cos(PI * 0);

        x = 240 + 200 * t + 400 * sin(2 * t);
        y = 540 + 300 * cos(PI * t);

        px = 100 * (480 + 200 * t + 400 * sin(2 * (t + 0.01f)));
        py = 100 * (540 + 300 * cos(PI * (t + 0.01f)));

        float d = dist(x0, y0, x, y);
        float segmentLength = 50f;
        float gapLength = 50f;
        float step = segmentLength + gapLength;

        stroke(255);
        strokeWeight(2);

        stroke(255);
        strokeWeight(4);

        float stepT = 0.005f;
        int dotSpacing = 3;
        int stepCount = 0;

        for (float time = 0; time <= t; time += stepT) {
            if (stepCount % dotSpacing == 0) {
                float rx = 240 + 200 * time + 400 * sin(2 * time);
                float ry = 540 + 300 * cos(PI * time);
                point(rx, ry);
            }
            stepCount++;
        }

        float vx = 200 + 800 * cos(2 * t);
        float vy = -300 * PI * sin(PI * t);
        float rapidez = sqrt(vx * vx + vy * vy);

        float scale = 0.3f;
        float endVx = x + vx * scale;
        float endVy = y + vy * scale;

        stroke(255);
        strokeWeight(10);
        line(x, y, endVx, endVy);

        float angle = atan2(vy, vx);
        float arrowSize = 35;
        pushMatrix();
        translate(endVx, endVy);
        rotate(angle);
        fill(255);
        noStroke();
        triangle(0, 0, -arrowSize, -arrowSize / 2f, -arrowSize, arrowSize / 2f);
        popMatrix();

        float vectorLength = rapidez * scale;

        float trackDistance = vectorLength + 35f;

        float trackX = x + trackDistance * cos(angle);
        float trackY = y + trackDistance * sin(angle);

        fill(255, 0, 0);
        noStroke();
        circle(trackX, trackY, 16);



        noFill();
        stroke(255);
        strokeWeight(3);
        circle(x, y, 200);

        popStyle();
    }

    public void settings() {
        fullScreen();
        frameRate = 30;
    }

    public void setup() {
        ntr = createFont("times.ttf", 50);

        float finalX = (width / 2f) + 865.3f;
        float finalY = (height / 2f) - 458.1f;
        float finalW = (height / 2f) - 381.75f;
        float finalH = (height / 2f) - 381.75f;

        alogo = new Alogo(finalX, finalY, finalW, finalH);

        noCursor();
    }

    public static void main(String[] args) {
        PApplet.main("Classes.Kin1");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}