package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class Har2 extends PApplet {

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

        float shmFreq = TWO_PI / 2.0f;
        float amplitude = height * 0.20f;

        float displacement = amplitude * cos(shmFreq * b);

        float anchorX = width * 0.5f;
        float anchorY = height * 0.25f;
        float restLen = height * 0.21f;
        float massY = anchorY + restLen + displacement;
        float massSize = 44f;

        drawDashedCeiling(anchorY, anchorX - 120, anchorX + 120);

        drawSpring(anchorX, anchorY, massY - massSize / 2f, 16, 16f);

        stroke(255, 60);
        strokeWeight(1);
        line(anchorX - 60, anchorY + restLen, anchorX + 60, anchorY + restLen);

        fill(255);
        stroke(200);
        strokeWeight(2);
        rectMode(CENTER);
        rect(anchorX, massY, massSize, massSize);

        float eK = sq(sin(shmFreq * b));
        float eP = sq(cos(shmFreq * b));
        float eTotal = eK + eP;

        float barWidth = 160f;
        float barHeight = height * 0.38f;
        float barY = height * 0.32f;

        float leftBarX = width * 0.32f;
        float rightBarX = width * 0.68f;

        drawEnergyBar(leftBarX, barY, barWidth, barHeight, eK, 255, "");
        drawEnergyBar(rightBarX, barY, barWidth, barHeight, eP, 255, "");

        drawTotalEnergyLine(leftBarX, rightBarX, barY, barHeight, eTotal);

        popStyle();
    }

    void drawDashedCeiling(float y, float startX, float endX) {
        stroke(255, 220);
        strokeWeight(2.5f);
        float dashLen = 10f;
        float gapLen = 8f;
        for (float x = startX; x <= endX; x += dashLen + gapLen) {
            line(x, y, min(x + dashLen, endX), y);
        }
    }

    void drawSpring(float x, float y1, float y2, int coils, float radius) {
        stroke(220);
        strokeWeight(2.5f);
        noFill();

        beginShape();
        vertex(x, y1);
        float len = max(y2 - y1, 4f);
        float step = len / coils;

        for (int i = 0; i < coils; i++) {
            float cy = y1 + i * step + step * 0.5f;
            float dir = (i % 2 == 0) ? 1 : -1;
            vertex(x + dir * radius, cy);
        }

        vertex(x, y2);
        endShape();
    }

    void drawEnergyBar(float x, float topY, float w, float h, float fillRatio, int col, String label) {
        stroke(100);
        strokeWeight(1.5f);
        noFill();
        rectMode(CORNER);
        rect(x - w / 2f, topY, w, h, 4);

        float currentFillH = h * constrain(fillRatio, 0, 1);
        float fillY = topY + h - currentFillH;

        fill(col);
        noStroke();
        rect(x - w / 2f + 2, fillY, w - 4, currentFillH, 2);

        fill(180);
        textSize(14);
        textAlign(CENTER);
        text(label, x, topY - 12);
    }

    void drawTotalEnergyLine(float x1, float x2, float topY, float h, float totalRatio) {
        float lineY = topY + h * (1.0f - constrain(totalRatio, 0, 1));
        stroke(255, 120);
        strokeWeight(1.5f);

        float dashLen = 6f;
        float gapLen = 6f;
        for (float x = x1; x <= x2; x += dashLen + gapLen) {
            line(x, lineY, min(x + dashLen, x2), lineY);
        }

        fill(255, 180);
        textSize(12);
        textAlign(LEFT);
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
        PApplet.main("Classes.Har2");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}