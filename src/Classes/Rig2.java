package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class Rig2 extends PApplet {

    float t = 0;
    float w = 106 / 120f;
    PFont ntr;
    float logoTransparency;
    float transY;

    public static Alogo alogo;

    float maxBeats = 4f;

    public void draw() {
        pushStyle();

        frameRate = 30;
        t = frameCount / (float) frameRate;
        float b = t * w;

        if (b >= maxBeats) {
            frameCount = 0;
            t = 0;
            b = 0;
        }

        background(0);

        textFont(ntr);
        transY = b % 1f;
        logoTransparency = (float) (255 * (-Math.pow(transY, 2) + 2 * (transY)));
        alogo.display(this, b, logoTransparency);

        float cx = width / 2f;
        float cy = height / 2f;

        float baseBarLength = 1100f;
        float targetBarLength = 800f;
        float currentBarLength = baseBarLength;

        if (b >= 3.0f) {
            float p = (b - 3.0f) / 1.0f;
            p = constrain(p, 0f, 1f);
            p = p * p * (3f - 2f * p);
            currentBarLength = lerp(baseBarLength, targetBarLength, p);
        }

        float barHeight = 60f;
        float maxHalf = max(10f, (currentBarLength / 2f) - 25f);

        float constantTorque = 12500f;

        float x1 = (0.35f + 0.25f * sin(b * TWO_PI * 0.33f)) * maxHalf;
        float f1Mag = constantTorque / x1;

        float x2 = (0.35f + 0.25f * cos(b * TWO_PI * 0.33f)) * maxHalf;
        float f2Mag = constantTorque / x2;

        stroke(255);
        strokeWeight(3);
        fill(0);
        ellipse(cx, cy, 22, 22);

        stroke(255);
        strokeWeight(3);
        fill(0);
        rectMode(CENTER);
        rect(cx, cy, currentBarLength, barHeight, 4);

        pushMatrix();
        translate(cx - x1, cy + barHeight / 2f);
        drawVector(0, 0, 0, f1Mag, "");
        popMatrix();

        pushMatrix();
        translate(cx + x2, cy + barHeight / 2f);
        drawVector(0, 0, 0, f2Mag, "");
        popMatrix();

        stroke(255, 130);
        strokeWeight(1);
        line(cx, cy - barHeight / 2f - 30, cx - x1, cy - barHeight / 2f - 30);
        line(cx - x1, cy - barHeight / 2f - 5, cx - x1, cy - barHeight / 2f - 55);
        line(cx, cy - barHeight / 2f - 30, cx + x2, cy - barHeight / 2f - 30);
        line(cx + x2, cy - barHeight / 2f - 5, cx + x2, cy - barHeight / 2f - 55);
        line(width/2f, cy - barHeight/ 2f-10, width/2f, cy - barHeight / 2f -50);

        popStyle();
    }

    void drawVector(float x0, float y0, float dx, float dy, String label) {
        float x1 = x0 + dx;
        float y1 = y0 + dy;
        float len = dist(x0, y0, x1, y1);
        if (len < 2) return;

        stroke(255);
        strokeWeight(3);
        line(x0, y0, x1, y1);

        float angle = atan2(dy, dx);
        float arrowSize = min(12, len * 0.3f);

        pushMatrix();
        translate(x1, y1);
        rotate(angle);
        fill(255);
        noStroke();
        triangle(0, 0, -arrowSize, -arrowSize * 0.4f, -arrowSize, arrowSize * 0.4f);
        popMatrix();

        fill(255);
        textSize(20);
        textAlign(LEFT, CENTER);
        text(label, x0 + dx + 12, y0 + dy * 0.5f);
    }

    public void settings() {
        fullScreen();
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
        PApplet.main("Classes.Rig2");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}