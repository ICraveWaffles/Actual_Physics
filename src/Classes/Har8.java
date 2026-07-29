package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class Har8 extends PApplet {

    float t = 0;
    float w = 100f / 120f;
    PFont ntr;
    float logoTransparency;
    float transY;

    public static Clogo clogo;

    float cycleTime = 4.8f;
    float[] micBuffer = new float[150];
    float phaseAcc = 0;

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

        rectMode(CORNER);
        noFill();
        stroke(255);
        strokeWeight(1);
        rect(cx - halfW, cy - halfH, boxW, boxH);

        float R = boxH * 0.40f;
        float Cx = cx - halfW * 0.25f;
        float Cy = cy;
        float Mx = Cx + R + 40f;

        stroke(255);
        strokeWeight(1.5f);
        noFill();
        circle(Cx, Cy, R * 2);

        for (float x = Cx - R - 20; x <= Cx + R + 20; x += 15) {
            line(x, Cy, x + 5, Cy);
        }
        for (float y = Cy - R - 20; y <= Cy + R + 20; y += 15) {
            line(Cx, y, Cx, y + 5);
        }

        fill(255);
        textSize(16);
        textAlign(CENTER, BOTTOM);
        text("X", Cx, Cy - R - 15);
        textAlign(RIGHT, CENTER);
        text("Y", Cx - R - 15, Cy);
        textAlign(CENTER, TOP);
        text("Z", Cx, Cy + R + 15);
        textAlign(LEFT, CENTER);
        text("W", Cx + R + 15, Cy);

        float droneSpeed = (TWO_PI * R) / cycleTime;
        float cSound = droneSpeed * 1.8f;

        int maxWaves = 5;
        float waveInterval = 0.25f;
        noFill();
        strokeWeight(1.5f);

        for (int i = 0; i < maxWaves; i++) {
            float age = (t % waveInterval) + (i * waveInterval);
            if (age > maxWaves * waveInterval) continue;

            float emissionTime = t - age;
            if (emissionTime < 0) emissionTime += cycleTime;

            float angle = -HALF_PI - (emissionTime * TWO_PI / cycleTime);
            float px = Cx + R * cos(angle);
            float py = Cy + R * sin(angle);

            float radius = age * cSound*0.5f;
            float alpha = map(age, 0, maxWaves * waveInterval, 255, 0);
            alpha = constrain(alpha, 0, 255);

            stroke(255, alpha);
            circle(px, py, radius * 2);
        }

        float currentAngle = -HALF_PI - (t * TWO_PI / cycleTime);
        float dx = Cx + R * cos(currentAngle);
        float dy = Cy + R * sin(currentAngle);

        float vx = droneSpeed * sin(currentAngle);
        float vy = -droneSpeed * cos(currentAngle);

        float distToMicX = Mx - dx;
        float distToMicY = Cy - dy;
        float distToMic = sqrt(distToMicX * distToMicX + distToMicY * distToMicY);

        float nx = distToMicX / distToMic;
        float ny = distToMicY / distToMic;

        float vr = vx * nx + vy * ny;
        float dopplerF = cSound / (cSound - vr);

        float basePhaseStep = 0.45f;
        float currentFreq = basePhaseStep * dopplerF;

        float baseAmp = 3500f;
        float currentAmp = baseAmp / distToMic;
        currentAmp = constrain(currentAmp, 5, 50);

        phaseAcc += currentFreq;

        System.arraycopy(micBuffer, 0, micBuffer, 1, micBuffer.length - 1);
        micBuffer[0] = currentAmp * sin(phaseAcc);

        pushMatrix();
        translate(dx, dy);
        rotate(currentAngle - HALF_PI);
        stroke(255);
        strokeWeight(2);
        line(-12, -12, 12, 12);
        line(12, -12, -12, 12);
        fill(0);
        rectMode(CENTER);
        rect(0, 0, 14, 14, 3);
        noFill();
        stroke(255);
        circle(-12, -12, 10);
        circle(12, -12, 10);
        circle(-12, 12, 10);
        circle(12, 12, 10);
        popMatrix();

        fill(255);
        textAlign(RIGHT, BOTTOM);
        text("Toy drone", dx - 20, dy - 20);

        noStroke();
        fill(255);
        rectMode(CENTER);
        rect(Mx + 15, Cy, 30, 8, 4);
        fill(0);
        arc(Mx, Cy, 16, 22, HALF_PI, PI + HALF_PI);
        stroke(255);
        strokeWeight(2);
        noFill();
        bezier(Mx + 30, Cy, Mx + 50, Cy, Mx + 40, Cy + 30, Mx + 70, Cy + 30);

        float gX = Mx + 70;
        float gW = (cx + halfW - 30) - gX;
        float gY = Cy;

        rectMode(CORNER);
        noFill();
        stroke(255);
        strokeWeight(1);
        rect(gX, gY - 65, gW, 130);

        stroke(255);
        line(gX, gY, gX + gW, gY);

        noFill();
        stroke(255);
        strokeWeight(2);
        beginShape();
        for (int i = 0; i < micBuffer.length; i++) {
            float px = gX + (gW - i * (gW / (float) micBuffer.length));
            vertex(px, gY - micBuffer[i]);
        }
        endShape();

        fill(255);
        textAlign(LEFT, TOP);
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
        PApplet.main("Classes.Har8");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}