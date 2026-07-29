package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class Har1 extends PApplet {

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

        float animT = min(t, cycleTime);

        float cycleBeats = 4.0f;
        float normT = (b % cycleBeats) / cycleBeats;

        float pivotStartX = width * 0.48f;
        float pivotEndX = width * 0.88f;
        float pivotX = lerp(pivotStartX, pivotEndX, normT);
        float pivotY = height * 0.25f;

        float maxAngle = PI / 5f;
        float shmFreq = TWO_PI / 2.0f;
        float theta = maxAngle * sin(shmFreq * b);

        float stringLen = height * 0.42f;
        float bobX = pivotX + stringLen * sin(theta);
        float bobY = pivotY + stringLen * cos(theta);

        stroke(60);
        strokeWeight(2);
        line(pivotStartX - 20, pivotY, pivotEndX + 20, pivotY);

        fill(200);
        noStroke();
        circle(pivotX, pivotY, 8);

        stroke(220, 200);
        strokeWeight(2);
        line(pivotX, pivotY, bobX, bobY);

        fill(255);
        noStroke();
        circle(bobX, bobY, 22);

        float graphLeft = width * 0.08f;
        float graphWidth = width * 0.28f;
        float graphHeight = height * 0.18f;

        float targetY = height * 0.50f;
        float startY1 = height * 0.22f;
        float startY3 = height * 0.78f;

        float mergeProgress = constrain((b - 1.5f) / 0.5f, 0, 1);

        float g1CenterY = lerp(startY1, targetY, mergeProgress);
        float g2CenterY = targetY;
        float g3CenterY = lerp(startY3, targetY, mergeProgress);

        drawGraph(graphLeft, g1CenterY, graphWidth, graphHeight);
        if (mergeProgress < 1.0f) {
            drawGraph(graphLeft, g2CenterY, graphWidth, graphHeight);
            drawGraph(graphLeft, g3CenterY, graphWidth, graphHeight);
        }

        drawHarmonicCurve(graphLeft, g1CenterY, graphWidth, graphHeight, animT, 1, shmFreq);
        drawHarmonicCurve(graphLeft, g2CenterY, graphWidth, graphHeight, animT, 2, shmFreq);
        drawHarmonicCurve(graphLeft, g3CenterY, graphWidth, graphHeight, animT, 3, shmFreq);

        popStyle();
    }

    void drawGraph(float x, float centerY, float w, float h) {
        stroke(70);
        strokeWeight(1);
        line(x, centerY, x + w, centerY);
        line(x, centerY - h / 2f, x, centerY + h / 2f);
    }

    void drawHarmonicCurve(float graphX, float centerY, float gW, float gH, float currentT, int type, float freq) {
        if (type == 1) stroke(255, 200, 100);
        else if (type == 2) stroke(100, 220, 255);
        else if (type == 3) stroke(255, 100, 150);

        strokeWeight(2);
        noFill();

        float amp = 0.10f;
        float omega = freq;

        beginShape();
        for (float step = 0; step <= currentT; step += 0.02f) {
            float px = graphX + (step / cycleTime) * gW;
            float stepBeat = step * w;
            float val = 0;

            if (type == 1) {
                val = amp * sin(omega * stepBeat);
            } else if (type == 2) {
                val = amp * omega * cos(omega * stepBeat);
            } else if (type == 3) {
                val = -amp * omega * omega * sin(omega * stepBeat);
            }

            float py = centerY - (val * (gH * 0.4f));
            vertex(px, py);
        }
        endShape();

        float currentX = graphX + (currentT / cycleTime) * gW;
        float currentBeat = currentT * w;
        float currentVal = 0;
        if (type == 1) currentVal = amp * sin(omega * currentBeat);
        else if (type == 2) currentVal = amp * omega * cos(omega * currentBeat);
        else if (type == 3) currentVal = -amp * omega * omega * sin(omega * currentBeat);

        float currentY = centerY - (currentVal * (gH * 0.4f));

        if (type == 1) fill(255, 200, 100);
        else if (type == 2) fill(100, 220, 255);
        else if (type == 3) fill(255, 100, 150);
        noStroke();
        circle(currentX, currentY, 6);
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
        PApplet.main("Classes.Har1");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}