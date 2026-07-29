package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class Har7 extends PApplet {

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

        float cx = width * 0.5f;
        float cy = height * 0.5f;

        float boxW = width * 0.65f;
        float boxH = height * 0.50f;
        float halfW = boxW * 0.5f;
        float halfH = boxH * 0.5f;


        float p1 = constrain(b / 2.0f, 0, 1);
        float p2 = constrain((b - 2.0f) / 2.0f, 0, 1);

        float slitSize = lerp(85f, 18f, p1);
        float lambda = lerp(16f, 50f, p2);

        float barrierX = cx - halfW + 70f;
        float sineCenterX = barrierX - 35f;

        stroke(255, 220);
        strokeWeight(2);
        noFill();
        beginShape();
        for (float y = cy - halfH; y <= cy + halfH; y += 2) {
            float x = sineCenterX + sin((y - cy - b * 90f) * TWO_PI / lambda) * 16f;
            vertex(x, y);
        }
        endShape();

        stroke(255, 60);
        strokeWeight(1.5f);
        float speed = 80f;
        float phaseDiff = (b * speed) % lambda;
        float resRatio = lambda / slitSize;
        float maxSpread = map(resRatio, 0.18f, 2.7f, PI * 0.20f, PI * 0.85f);
        maxSpread = constrain(maxSpread, PI * 0.20f, PI * 0.85f);

        for (float r = phaseDiff; r < (cx - barrierX); r += lambda) {
            arc(barrierX, cy, r * 2, r * 2, -maxSpread * 0.5f, maxSpread * 0.5f);
        }

        fill(0);
        noStroke();
        rect(0, 0, width, cy - halfH);
        rect(0, cy + halfH, width, height - (cy + halfH));

        stroke(255, 220);
        strokeWeight(4);
        line(barrierX, cy - halfH, barrierX, cy - slitSize * 0.5f);
        line(barrierX, cy + slitSize * 0.5f, barrierX, cy + halfH);

        stroke(255, 40);
        strokeWeight(1);
        line(cx, cy - halfH + 10, cx, cy + halfH - 10);

        float rightCX = cx + halfW * 0.5f;
        float bigR = halfH * 1.125f;

        pushMatrix();
        translate(rightCX, cy);

        stroke(255, 120);
        strokeWeight(1.5f);
        fill(255, 5);
        circle(0, 0, bigR * 2);

        rotate(b * 0.25f);

        float gridStep = map(resRatio, 0.18f, 2.7f, 10f, 42f);

        stroke(255, 160);
        strokeWeight(1f);

        for (float x = 0; x < bigR; x += gridStep) {
            float h = sqrt(max(0, bigR * bigR - x * x));
            line(x, -h, x, h);
            if (x > 0) {
                line(-x, -h, -x, h);
            }
        }

        for (float y = 0; y < bigR; y += gridStep) {
            float wLine = sqrt(max(0, bigR * bigR - y * y));
            line(-wLine, y, wLine, y);
            if (y > 0) {
                line(-wLine, -y, wLine, -y);
            }
        }

        popMatrix();

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

        clogo = new Clogo(finalX, finalY, finalW, finalH);

        noCursor();
    }

    public static void main(String[] args) {
        PApplet.main("Classes.Har7");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}