package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class Har6 extends PApplet {

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

        float srcX = cx - halfW + boxW * 0.05f;
        float b1X = cx - halfW + boxW * 0.25f;
        float b2X = cx - halfW + boxW * 0.55f;
        float screenX = cx - halfW + boxW * 0.85f;

        float mix = constrain(map(b, 2.0f, 2.6f, 0, 1), 0, 1);

        float visSlitWidth = 100f;
        float visSlitSep = 250f;

        rectMode(CORNER);
        fill(255, 10);
        stroke(255, 40);
        strokeWeight(1);
        rect(cx - halfW, cy - halfH, boxW, boxH);

        noFill();
        strokeWeight(2);

        float lambda = 35f;
        float speed = 300f;
        float phase = (b * speed) % lambda;

        stroke(255, 50);
        for (float r = phase; r < (b1X - srcX); r += lambda) {
            arc(srcX, cy, r * 2, r * 2, -HALF_PI, HALF_PI);
        }

        for (float r = phase; r < (screenX - b1X); r += lambda) {
            if (r < (b2X - b1X)) {
                stroke(255, 50);
            } else {
                stroke(255, 50 * (1 - mix));
            }
            arc(b1X, cy, r * 2, r * 2, -HALF_PI, HALF_PI);
        }

        if (mix > 0) {
            stroke(255, 50 * mix);
            float limitB2 = screenX - b2X;
            for (float r = phase; r < limitB2; r += lambda) {
                arc(b2X, cy - visSlitSep * 0.5f, r * 2, r * 2, -HALF_PI, HALF_PI);
                arc(b2X, cy + visSlitSep * 0.5f, r * 2, r * 2, -HALF_PI, HALF_PI);
            }
        }

        fill(0);
        noStroke();
        rect(0, 0, width, cy - halfH);
        rect(0, cy + halfH, width, height - (cy + halfH));

        stroke(255, 220);
        strokeWeight(4);

        line(b1X, cy - halfH, b1X, cy - visSlitWidth * 0.5f);
        line(b1X, cy + visSlitWidth * 0.5f, b1X, cy + halfH);

        if (mix > 0) {
            stroke(255, 220 * mix);
            float topY = cy - visSlitSep * 0.5f;
            float botY = cy + visSlitSep * 0.5f;
            line(b2X, cy - halfH, b2X, topY - visSlitWidth * 0.5f);
            line(b2X, topY + visSlitWidth * 0.5f, b2X, botY - visSlitWidth * 0.5f);
            line(b2X, botY + visSlitWidth * 0.5f, b2X, cy + halfH);
        }

        float mathLambda = 14f;
        float calcSlitWidth = 100f;
        float calcSlitSep = 250f;
        float maxGraphW = 180f;

        noStroke();
        for (float y = cy - halfH + 5; y <= cy + halfH - 5; y += 2) {
            float dy = y - cy;

            float L1 = screenX - b1X;
            float sinTheta1 = dy / sqrt(L1 * L1 + dy * dy);
            float beta1 = (PI * calcSlitWidth / mathLambda) * sinTheta1;
            float sinc1 = (abs(beta1) < 0.001f) ? 1.0f : sin(beta1) / beta1;
            float int1 = sinc1 * sinc1;

            float L2 = screenX - b2X;
            float sinTheta2 = dy / sqrt(L2 * L2 + dy * dy);
            float beta2 = (PI * calcSlitWidth / mathLambda) * sinTheta2;
            float sinc2 = (abs(beta2) < 0.001f) ? 1.0f : sin(beta2) / beta2;
            float alpha2 = (PI * calcSlitSep / mathLambda) * sinTheta2;
            float cosF = cos(alpha2);
            float int2 = (sinc2 * sinc2) * (cosF * cosF);

            float intensity = lerp(int1, int2, mix);

            fill(255, intensity * 255);
            rect(screenX - 12, y, 12, 2);
        }

        stroke(255, 80);
        strokeWeight(2);
        line(screenX, cy - halfH + 5, screenX, cy + halfH - 5);

        fill(255, 30);
        stroke(255, 180);
        strokeWeight(2);
        beginShape();
        vertex(screenX, cy - halfH + 5);

        for (float y = cy - halfH + 5; y <= cy + halfH - 5; y += 2) {
            float dy = y - cy;

            float L1 = screenX - b1X;
            float sinTheta1 = dy / sqrt(L1 * L1 + dy * dy);
            float beta1 = (PI * calcSlitWidth / mathLambda) * sinTheta1;
            float sinc1 = (abs(beta1) < 0.001f) ? 1.0f : sin(beta1) / beta1;
            float int1 = sinc1 * sinc1;

            float L2 = screenX - b2X;
            float sinTheta2 = dy / sqrt(L2 * L2 + dy * dy);
            float beta2 = (PI * calcSlitWidth / mathLambda) * sinTheta2;
            float sinc2 = (abs(beta2) < 0.001f) ? 1.0f : sin(beta2) / beta2;
            float alpha2 = (PI * calcSlitSep / mathLambda) * sinTheta2;
            float cosF = cos(alpha2);
            float int2 = (sinc2 * sinc2) * (cosF * cosF);

            float intensity = lerp(int1, int2, mix);

            float gx = screenX + intensity * maxGraphW;
            vertex(gx, y);
        }

        vertex(screenX, cy + halfH - 5);
        endShape(CLOSE);

        noStroke();
        fill(255, 40);
        circle(srcX, cy, 25);
        fill(255, 230);
        circle(srcX, cy, 8);

        if (clogo != null) {
            clogo.display(this, b, logoTransparency);
        }

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
        PApplet.main("Classes.Har6");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}