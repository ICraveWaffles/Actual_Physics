package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class Har4 extends PApplet {

    float t = 0;
    float w = 100f / 120f;
    PFont ntr;
    float logoTransparency;
    float transY;

    public static Clogo clogo;

    float cycleTime = 4.8f;

    public void draw() {

        pushStyle();

        frameRate = 30;
        t = frameCount / (float) frameRate;
        if (t > cycleTime) {
            frameCount = 0;
            t = 0;
        }

        background(0);

        float b = t * w;
        textFont(ntr);

        transY = b % 1f;
        logoTransparency = (float) (255 * (-Math.pow(transY, 2) + 2 * (transY)));

        if (clogo != null) {
            clogo.display(this, b, logoTransparency);
        }

        float startX = width * 0.1f;
        float endX = width * 0.75f;
        float shmFreq = TWO_PI / 0.26f;

        float stringY0 = height * 0.32f;
        float ampString = height * 0.12f;

        float nString = 1f;
        if (b >= 1.6f) {
            nString = floor((b - 1.51f) * 2.0f) + 1f;
        }

        float numQuartersString = nString * 2f;

        stroke(200,150);
        strokeWeight(6);
        noFill();
        drawStandingWave(startX, endX, stringY0, ampString, numQuartersString, shmFreq * b, true);

        stroke(255);
        strokeWeight(2);
        drawStandingWave(startX, endX, stringY0, ampString, numQuartersString, shmFreq * b, false);

        fill(100);
        noStroke();
        rectMode(CENTER);
        rect(startX - 10, stringY0, 20, 60, 5);
        rect(endX + 10, stringY0, 20, 60, 5);

        drawMarkers(startX, endX, stringY0, ampString, numQuartersString, true);

        float tubeY0 = height * 0.68f;
        float ampTube = height * 0.12f;

        float nTube = 1f;
        if (b >= 1.6f) {
            nTube = floor((b - 1.51f)) * 2f + 1f;
        }

        float numQuartersTube = nTube;

        stroke(255, 60);
        strokeWeight(1);
        line(startX, tubeY0, endX, tubeY0);

        stroke(255);
        strokeWeight(3);
        noFill();
        drawStandingWave(startX, endX, tubeY0, ampTube, numQuartersTube, shmFreq * b, false);

        stroke(255, 150);
        strokeWeight(4);
        line(startX, tubeY0 - ampTube * 1.1f, endX, tubeY0 - ampTube * 1.1f);
        line(startX, tubeY0 + ampTube * 1.1f, endX, tubeY0 + ampTube * 1.1f);
        line(startX, tubeY0 - ampTube * 1.1f, startX, tubeY0 + ampTube * 1.1f);

        drawMarkers(startX, endX, tubeY0, ampTube, numQuartersTube, false);

        popStyle();
    }

    void drawStandingWave(float x0, float x1, float y0, float A, float numQuarters, float phase, boolean isShadow) {
        float len = x1 - x0;
        beginShape();
        for (float x = x0; x <= x1; x += 3) {
            float dx = x - x0;
            float spatial = sin(numQuarters * HALF_PI * dx / len);
            float temporal = cos(phase);
            float y = y0 - A * spatial * temporal;
            if (isShadow) {
                spatial = sin(numQuarters * HALF_PI * dx / len + 0.1f);
                temporal = cos(phase + 0.05f);
                y = y0 - A * spatial * temporal + 2;
            }
            vertex(x, y);
        }
        endShape();
    }

    void drawMarkers(float x0, float x1, float y0, float A, float numQuarters, boolean isTop) {
        float len = x1 - x0;
        int maxI = round(numQuarters);

        for (int i = 0; i <= maxI; i++) {
            float dx = i * len / numQuarters;
            float px = x0 + dx;

            String type = markerType(i);
            int mColor = markerColor(type);

            fill(mColor);
            noStroke();
            circle(px, y0, 8);

            fill(220);
            textSize(18);
            textAlign(CENTER, CENTER);

            float textY = y0 + A * 1.35f;
            if (isTop) {
                textY = y0 - A * 1.35f;
            }

            text(type, px, textY);
        }
    }

    String markerType(int i) {
        return (i % 2 == 0) ? "N" : "A";
    }

    int markerColor(String type) {
        return color(255);
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

        clogo = new Clogo(finalX, finalY, finalW, finalH);

        noCursor();
    }

    public static void main(String[] args) {
        PApplet.main("Classes.Har4");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}