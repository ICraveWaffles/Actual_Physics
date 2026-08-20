package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class Mag3 extends PApplet {

    float t = 0;
    float w = 100f / 120f;
    PFont ntr;
    float logoTransparency;
    float transY;

    public static Dlogo clogo;

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

        float cx = width * 0.5f;
        float cy = height * 0.5f;

        pushMatrix();
        pushStyle();
        drawScene(b, cx, cy);
        popStyle();
        popMatrix();

        if (clogo != null) {
            pushStyle();
            clogo.display(this, b, logoTransparency);
            popStyle();
        }

        popStyle();
    }

    private void drawScene(float b, float cx, float cy) {
        float rl_base = 210f;
        float rr_base;

        if (b < 2.0f) {
            rr_base = 300f - 210f * (b / 2.0f);
        } else {
            rr_base = 90f;
        }

        float p = constrain((b - 2.0f) / 0.6f, 0f, 1f);
        float e = 0.5f - 0.5f * cos(PI * p);

        float lx = lerp(cx - 360f, cx, e);
        float ly = lerp(cy, cy - 65f, e);
        float rx_pos = lerp(cx + 360f, cx, e);
        float ry_pos = lerp(cy, cy + 65f, e);

        float rl_x = lerp(rl_base, 250f, e);
        float rl_y = lerp(rl_base, 75f, e);
        float rr_x = lerp(rr_base, 250f, e);
        float rr_y = lerp(rr_base, 75f, e);

        drawLoopHalf(lx, ly, rl_x, rl_y, b, -1f, false);
        drawLoopHalf(rx_pos, ry_pos, rr_x, rr_y, b, 1f, false);

        if (b < 2.0f) {
            drawFieldIn(lx, ly, rl_x);
            drawFieldOut(rx_pos, ry_pos, rr_x);
            drawComparator(cx, cy, rl_base, rr_base);
        } else {
            float fluxAlpha = 255f * e;
            drawFluxArrows(cx, cy, ly, ry_pos, b, fluxAlpha);
        }

        drawLoopHalf(lx, ly, rl_x, rl_y, b, -1f, true);
        drawLoopHalf(rx_pos, ry_pos, rr_x, rr_y, b, 1f, true);
    }

    private void drawLoopHalf(float x, float y, float rx, float ry, float phase, float dir, boolean front) {
        pushMatrix();
        translate(x, y);
        noFill();
        strokeCap(SQUARE);

        float start = front ? 0 : PI;
        float end = front ? PI : TWO_PI;

        stroke(50);
        strokeWeight(18);
        arc(0, 0, rx * 2, ry * 2, start, end);

        stroke(130);
        strokeWeight(12);
        arc(0, 0, rx * 2, ry * 2, start, end);

        stroke(220);
        strokeWeight(4);
        arc(0, 0, rx * 2, ry * 2, start, end);

        for (int i = 0; i < 3; i++) {
            float angleOffset = (TWO_PI / 3f) * i;
            float currentAngle = (phase * TWO_PI * dir * 0.7f) + angleOffset;

            currentAngle = (currentAngle % TWO_PI + TWO_PI) % TWO_PI;

            boolean inFront = (currentAngle >= 0 && currentAngle <= PI);
            if (inFront == front) {
                float px = rx * cos(currentAngle);
                float py = ry * sin(currentAngle);
                float tang = atan2(ry * cos(currentAngle), -rx * sin(currentAngle));
                if (dir < 0) tang += PI;

                pushMatrix();
                translate(px, py);
                rotate(tang);
                fill(255);
                noStroke();
                triangle(14, 0, -12, 10, -12, -10);
                popMatrix();
            }
        }
        popMatrix();
    }

    private void drawFieldIn(float x, float y, float radius) {
        float bMag = 210f / radius;
        float size = 18f * bMag;
        stroke(255);
        strokeWeight(4 * bMag);
        strokeCap(ROUND);
        line(x - size, y - size, x + size, y + size);
        line(x - size, y + size, x + size, y - size);
    }

    private void drawFieldOut(float x, float y, float radius) {
        float bMag = 210f / radius;
        float size = 12f * bMag;
        stroke(255);
        strokeWeight(4 * bMag);
        noFill();
        circle(x, y, size * 3.5f);
        fill(255);
        noStroke();
        circle(x, y, size * 1.5f);
    }

    private void drawComparator(float cx, float cy, float rLeft, float rRight) {
        float ratio = rLeft / rRight;
        float hinge = constrain((1.0f - ratio) * 3.5f, -1f, 1f);

        float w = 38f;
        float tlY = map(hinge, 1, -1, -38, 0);
        float trY = map(hinge, 1, -1, 0, -38);
        float blY = map(hinge, 1, -1, 38, 0);
        float brY = map(hinge, 1, -1, 0, 38);

        stroke(255);
        strokeWeight(7);
        strokeCap(ROUND);
        line(cx - w, cy + tlY, cx + w, cy + trY);
        line(cx - w, cy + blY, cx + w, cy + brY);
    }

    private void drawFluxArrows(float cx, float cy, float topWireY, float botWireY, float b, float alpha) {
        if (alpha < 2f) return;
        stroke(255, 140 * (alpha / 255f));
        strokeWeight(3.5f);
        strokeCap(ROUND);

        float spacing = 75f;
        float offset = (b * 160f) % 70f;

        for (int i = -2; i <= 2; i++) {
            float x = cx + i * spacing;
            float topY = topWireY;
            float botY = botWireY;


            for (float headY = botY - offset; headY > topY; headY -= 70f) {
                if (headY > botY || headY < topY) continue;
                pushMatrix();
                translate(x, headY);
                fill(255, alpha);
                noStroke();
                triangle(0, -14, -9, 9, 9, 9);
                popMatrix();
            }
        }
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
        clogo = new Dlogo(finalX, finalY, finalW, finalH);
        noCursor();
    }

    public static void main(String[] args) {
        PApplet.main("Classes.Mag3");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}