package Classes;

import processing.core.PApplet;

public class EB extends PApplet {

    float t = 0;
    float w = 100f / 120f;
    float logoTransparency;
    float transY;

    public static Elogo elogo;

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

        transY = b % 1f;
        logoTransparency = (float) (255 * (-Math.pow(transY, 2) + 2 * (transY)));

        float cx = width * 0.50f;
        float cy = height * 0.50f;

        pushMatrix();
        drawScene(b, cx, cy);
        popMatrix();

        if (elogo != null) {
            elogo.display(this, b, logoTransparency);
        }

        popStyle();
    }

    private void drawScene(float b, float cx, float cy) {
        float topLimit = -height * 0.32f;
        float bottomLimit = height * 0.32f;

        if (b < 2.0f) {
            float localB = b;
            float px = map(localB, 0f, 2.0f, -cx * 0.95f, cx * 0.95f);

            pushMatrix();
            translate(cx, cy);

            stroke(255);
            strokeWeight(5f);
            line(0, topLimit, 0, bottomLimit);

            if (px < 0) {
                noStroke();
                fill(255);
                circle(px, 0, 28f);
            } else {
                noFill();
                stroke(255);
                strokeWeight(4f);
                beginShape();
                for (float dx = -180; dx <= 180; dx += 2) {
                    float env = exp(-pow(dx / 55f, 2));
                    float wave = sin(dx * 0.18f - t * 25f);
                    float y = env * wave * 50f;
                    vertex(px + dx, y);
                }
                endShape();
            }

            popMatrix();

        } else {
            float localB = b - 2.0f;

            boolean show1 = localB >= 0.25f;
            boolean show2 = localB >= 1;
            boolean show3 = localB >= 1.75f;

            pushMatrix();
            translate(cx, cy);

            if (show1) {
                drawFermionLine(-480, 220, -240, 0);
                drawWavyLine(-480, -220, -240, 0);
            }

            if (show2) {
                drawFermionLine(-140, 0, 140, 0);
                noStroke();
                fill(255);
                circle(-140, 0, 14f);
                circle(140, 0, 14f);
            }

            if (show3) {
                drawWavyLine(240, 0, 480, -220);
                drawFermionLine(240, 0, 480, 220);
            }

            popMatrix();
        }
    }

    private void drawWavyLine(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float len = dist(x1, y1, x2, y2);
        float angle = atan2(dy, dx);

        pushMatrix();
        translate(x1, y1);
        rotate(angle);

        noFill();
        stroke(255);
        strokeWeight(4f);
        beginShape();
        for (float d = 0; d <= len; d += 2) {
            float wave = sin(d * 0.22f - t * 20f) * 16f;
            vertex(d, wave);
        }
        endShape();

        popMatrix();
    }

    private void drawFermionLine(float x1, float y1, float x2, float y2) {
        stroke(255);
        strokeWeight(4f);
        line(x1, y1, x2, y2);

        float angle = atan2(y2 - y1, x2 - x1);
        float mx = lerp(x1, x2, 0.5f);
        float my = lerp(y1, y2, 0.5f);

        pushMatrix();
        translate(mx, my);
        rotate(angle);
        fill(255);
        noStroke();
        triangle(10, 0, -10, -7, -10, 7);
        popMatrix();
    }

    public void settings() {
        fullScreen();
    }

    public void setup() {
        float finalX = (width / 2f) + 865.3f;
        float finalY = (height / 2f) - 458.1f;
        float finalW = (height / 2f) - 381.75f;
        float finalH = (height / 2f) - 381.75f;
        elogo = new Elogo(finalX, finalY, finalW, finalH);
        noCursor();
    }

    public static void main(String[] args) {
        PApplet.main("Classes.EB");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}