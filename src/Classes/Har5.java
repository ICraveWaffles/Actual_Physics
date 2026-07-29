package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class Har5 extends PApplet {

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

        float n1 = 1.6f;
        float n2 = 1.0f;

        float progress = b / 4.0f;
        float theta1 = map(progress, 0, 1, radians(5), radians(58));

        float cx = width * 0.5f;
        float cy = height * 0.5f;

        float boxW = width * 0.65f;
        float boxH = height * 0.50f;
        float halfW = boxW * 0.5f;
        float halfH = boxH * 0.5f;

        rectMode(CORNER);
        fill(255, 0);
        stroke(255);
        strokeWeight(4);
        rect(cx - halfW, cy - halfH, boxW, halfH);

        fill(255, 22);
        stroke(255);
        rect(cx - halfW, cy, boxW, halfH);

        stroke(255, 140);
        strokeWeight(1.5f);
        line(cx - halfW, cy, cx + halfW, cy);

        drawDashedNormal(cx, cy - halfH + 15, cx, cy + halfH - 15);

        float sinTheta2 = (n1 / n2) * sin(theta1);
        boolean isTIR = sinTheta2 > 1.0f;
        float theta2 = isTIR ? HALF_PI : asin(sinTheta2);

        float maxL1 = min(halfW / sin(theta1), halfH / cos(theta1));
        float maxL_refl = maxL1;
        float maxL2 = min(halfW / sin(max(theta2, 0.0001f)), halfH / max(cos(theta2), 0.0001f));

        stroke(255, 25);
        strokeWeight(1f);
        line(cx - maxL1 * sin(theta1), cy + maxL1 * cos(theta1), cx, cy);
        line(cx, cy, cx + maxL_refl * sin(theta1), cy + maxL_refl * cos(theta1));
        if (!isTIR) {
            line(cx, cy, cx + maxL2 * sin(theta2), cy - maxL2 * cos(theta2));
        }

        float speed1 = 140f;
        float spacing1 = 24f;
        float speed2 = speed1 * (n1 / n2);
        float spacing2 = spacing1 * (n1 / n2);

        float offset1_inc = (spacing1 - ((b * speed1) % spacing1)) % spacing1;
        float offset1_refl = (b * speed1) % spacing1;
        float offset2 = (b * speed2) % spacing2;

        for (float d = offset1_inc; d <= maxL1; d += spacing1) {
            float px = cx - d * sin(theta1);
            float py = cy + d * cos(theta1);
            drawPhoton(px, py, 1.0f);
        }

        if (!isTIR) {
            for (float d = offset2; d <= maxL2; d += spacing2) {
                float px = cx + d * sin(theta2);
                float py = cy - d * cos(theta2);
                drawPhoton(px, py, 1.0f);
            }
        }

        float reflAlpha = isTIR ? 1.0f : 0.35f;
        for (float d = offset1_refl; d <= maxL_refl; d += spacing1) {
            float px = cx + d * sin(theta1);
            float py = cy + d * cos(theta1);
            drawPhoton(px, py, reflAlpha);
        }

        drawAngleArc(cx, cy, 50, HALF_PI + theta1, HALF_PI);
        if (!isTIR) {
            drawAngleArc(cx, cy, 50, -HALF_PI, -HALF_PI + theta2);
        }

        popStyle();
    }

    void drawPhoton(float x, float y, float alphaMult) {
        noStroke();
        fill(255, 40 * alphaMult);
        circle(x, y, 14);
        fill(255, 230 * alphaMult);
        circle(x, y, 5);
    }

    void drawDashedNormal(float x1, float y1, float x2, float y2) {
        stroke(255, 80);
        strokeWeight(1f);
        float d = dist(x1, y1, x2, y2);
        float dash = 5f, gap = 5f;
        for (float i = 0; i < d; i += dash + gap) {
            float sy = lerp(y1, y2, i / d);
            float ey = lerp(y1, y2, min((i + dash) / d, 1f));
            line(x1, sy, x2, ey);
        }
    }

    void drawAngleArc(float cx, float cy, float r, float startA, float endA) {
        stroke(255, 120);
        strokeWeight(1f);
        noFill();
        arc(cx, cy, r, r, min(startA, endA), max(startA, endA));
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
        PApplet.main("Classes.Har5");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}