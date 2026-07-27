package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class Kin3 extends PApplet {

    float t = 0;
    float w = 5/6f;
    PFont ntr;
    float logoTransparency;
    float transY;

    public static Alogo alogo;

    float halfT = 2.26415f * 2;
    float maxT = halfT * 2;

    public void draw() {

        pushStyle();

        frameRate = 30;
        t = frameCount / (float) frameRate;
        if (t > maxT) {
            frameCount = 0;
            t = 0;
        }

        background(0);

        float b = t * w;
        textFont(ntr);

        transY = b % 1f;
        logoTransparency = (float) (255 * (-Math.pow(transY, 2) + 2 * (transY)));

        alogo.display(this, b, logoTransparency);

        float marginX = width * 0.12f;
        float startX = marginX;
        float endX = width - marginX;
        float startY = height * 0.82f;
        float peakY = height * 0.22f;

        float animT = min(t, halfT);

        stroke(50);
        strokeWeight(1);
        line(startX - 40, startY, endX + 40, startY);

        drawLaunchAngle(startX, startY, 45);

        drawProgressiveParabola(startX, endX, startY, peakY, animT);
        drawProgressiveFrictionPath(startX, endX, startY, peakY, animT);

        if (animT >= halfT / 2f) {
            float peakX = getParabolaX(halfT / 2f, startX, endX);

            float peakProgress = (animT - halfT / 2f) / (halfT / 2f);
            peakProgress = constrain(peakProgress * 2.0f, 0, 1);

            float targetDashedY = lerp(peakY, startY, peakProgress);

            drawDashedLine(peakX, peakY, peakX, targetDashedY, 6, 6);

            fill(255);
            noStroke();
            circle(peakX, peakY, 6);
        }

        float px = getParabolaX(animT, startX, endX);
        float py = getParabolaY(animT, startY, peakY);

        float vx = getVx(startX, endX);
        float vy = getVy(animT, startY, peakY);

        float vectorScale = 0.44f;

        drawVector(px, py, px + vx * vectorScale, py, 180);
        drawVector(px, py, px, py + vy * vectorScale, 180);

        drawVector(px, py, px + vx * vectorScale, py + vy * vectorScale, 255);

        drawDashedLine(px + vx * vectorScale, py, px + vx * vectorScale, py + vy * vectorScale, 8, 8);
        drawDashedLine(px, py + vy * vectorScale, px + vx * vectorScale, py + vy * vectorScale, 8, 8);

        fill(255);
        noStroke();
        circle(px, py, 14);

        float fPx = getFrictionX(animT, startX, endX);
        float fPy = getFrictionY(animT, startY, peakY);

        float dt = 0.01f;
        float fPx_next = getFrictionX(animT + dt, startX, endX);
        float fPy_next = getFrictionY(animT + dt, startY, peakY);
        float fVx = (fPx_next - fPx) / dt;
        float fVy = (fPy_next - fPy) / dt;

        float fSpeed = sqrt(fVx * fVx + fVy * fVy);
        float dirX = (fSpeed > 0.001f) ? (fVx / fSpeed) : 0;
        float dirY = (fSpeed > 0.001f) ? (fVy / fSpeed) : 0;

        float fgX = 0;
        float fgY = 55;

        float dragMag = fSpeed * 0.32f;
        float fdX = -dirX * dragMag;
        float fdY = -dirY * dragMag;

        float fnetX = fgX + fdX;
        float fnetY = fgY + fdY;

        float forceScale = 0.8f;
        drawVector(fPx, fPy, fPx - fnetX * forceScale, fPy, 150);
        drawVector(fPx, fPy, fPx, fPy - fnetY * forceScale, 150);
        drawVector(fPx, fPy, fPx - fnetX * forceScale, fPy - fnetY * forceScale, 220);
        drawDashedLine(fPx - fnetX * forceScale, fPy, fPx - fnetX * forceScale, fPy - fnetY * forceScale, 6, 6);
        drawDashedLine(fPx, fPy - fnetY * forceScale, fPx - fnetX * forceScale, fPy - fnetY * forceScale, 6, 6);

        fill(150, 200, 255);
        noStroke();
        rectMode(CENTER);
        pushMatrix();
        translate(fPx, fPy);
        rect(0, 0, 14, 14);
        popMatrix();

        popStyle();
    }

    float getParabolaX(float time, float x0, float x1) {
        float normT = time / halfT;
        return lerp(x0, x1, normT);
    }

    float getParabolaY(float time, float y0, float yPeak) {
        float normT = time / halfT;
        float h = y0 - yPeak;
        return y0 - 4 * h * normT * (1f - normT);
    }

    float getFrictionX(float time, float x0, float x1) {
        float normT = time / halfT;
        float adjustedNormT = (float)((1.0 - Math.exp(-0.8 * normT)) / (1.0 - Math.exp(-0.8)));
        return lerp(x0, x1 * 0.85f, adjustedNormT);
    }

    float getFrictionY(float time, float y0, float yPeak) {
        float normT = time / halfT;
        float h = (y0 - yPeak) * 0.8f;
        return y0 - 4 * h * normT * (1f - normT);
    }

    float getVx(float x0, float x1) {
        return (x1 - x0) / halfT;
    }

    float getVy(float time, float y0, float yPeak) {
        float normT = time / halfT;
        float h = y0 - yPeak;
        return -4 * h * (1f - 2f * normT) / halfT;
    }

    void drawProgressiveParabola(float x0, float x1, float y0, float yPeak, float currentAnimT) {
        stroke(220);
        strokeWeight(2);
        noFill();

        beginShape();
        for (float step = 0; step <= currentAnimT; step += 0.02f) {
            float px = getParabolaX(step, x0, x1);
            float py = getParabolaY(step, y0, yPeak);
            vertex(px, py);
        }

        float endPx = getParabolaX(currentAnimT, x0, x1);
        float endPy = getParabolaY(currentAnimT, y0, yPeak);
        vertex(endPx, endPy);

        endShape();
    }

    void drawProgressiveFrictionPath(float x0, float x1, float y0, float yPeak, float currentAnimT) {
        stroke(100, 160, 255, 180);
        strokeWeight(2);
        noFill();

        beginShape();
        for (float step = 0; step <= currentAnimT; step += 0.02f) {
            float px = getFrictionX(step, x0, x1);
            float py = getFrictionY(step, y0, yPeak);
            vertex(px, py);
        }

        float endPx = getFrictionX(currentAnimT, x0, x1);
        float endPy = getFrictionY(currentAnimT, y0, yPeak);
        vertex(endPx, endPy);

        endShape();
    }

    void drawLaunchAngle(float x0, float y0, float radius) {
        stroke(255, 180);
        strokeWeight(1.5f);
        noFill();

        float vx = getVx(x0, x0 + (width * 0.76f));
        float vy = getVy(0, y0, height * 0.22f);
        float angleRad = atan2(vy, vx);

        arc(x0, y0, radius * 2, radius * 2, angleRad, 0);
    }

    void drawDashedLine(float x0, float y0, float x1, float y1, float dashLen, float gapLen) {
        stroke(255, 140);
        strokeWeight(1.5f);

        float distance = dist(x0, y0, x1, y1);
        if (distance <= 0) return;

        float dashes = distance / (dashLen + gapLen);

        for (int i = 0; i < dashes; i++) {
            float startFrac = (i * (dashLen + gapLen)) / distance;
            float endFrac = min(((i * (dashLen + gapLen)) + dashLen) / distance, 1.0f);

            float startX = lerp(x0, x1, startFrac);
            float startY = lerp(y0, y1, startFrac);
            float endX = lerp(x0, x1, endFrac);
            float endY = lerp(y0, y1, endFrac);

            line(startX, startY, endX, endY);
        }
    }

    void drawVector(float x0, float y0, float x1, float y1, float alpha) {
        float angle = atan2(y1 - y0, x1 - x0);
        float len = dist(x0, y0, x1, y1);

        if (len < 1f) return;

        stroke(255, alpha);
        strokeWeight(3);
        line(x0, y0, x1, y1);

        float arrowSize = min(14, len * 0.4f);

        pushMatrix();
        translate(x1, y1);
        rotate(angle);
        fill(255, alpha);
        noStroke();
        triangle(0, 0, -arrowSize, -arrowSize / 2.5f, -arrowSize, arrowSize / 2.5f);
        popMatrix();
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

        alogo = new Alogo(finalX, finalY, finalW, finalH);

        noCursor();
    }

    public static void main(String[] args) {
        PApplet.main("Classes.Kin3");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}