package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class Mag4 extends PApplet {

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

        pushStyle();
        drawScene(b, cx, cy);
        popStyle();

        if (clogo != null) {
            clogo.display(this, b, logoTransparency);
        }

        popStyle();
    }

    private void drawScene(float b, float cx, float cy) {


        float coilCx = width * 0.70f;
        float magX = coilCx + 220f * cos(PI * b);

        float flux = cos(PI * b);
        float emf = sin(PI * b);
        float current = emf;

        drawFluxGraphAndEMFKinStyle(b, flux, emf);

        int numLoops = 7;
        float loopSpacing = 55f;
        float coilWidth = (numLoops - 1) * loopSpacing;
        float startX = coilCx - coilWidth / 2f;
        float rx = 55f;
        float ry = 200f;

        for (int i = 0; i < numLoops; i++) {
            float x = startX + i * loopSpacing;
            drawCoilHalf(x, cy, rx, ry, true, current, b);
        }

        drawMagnet(magX, cy);

        for (int i = 0; i < numLoops; i++) {
            float x = startX + i * loopSpacing;
            drawCoilHalf(x, cy, rx, ry, false, current, b);
        }

    }

    private void drawFluxGraphAndEMFKinStyle(float b, float flux, float emf) {
        float gx = width * 0.08f;
        float gy = height * 0.30f;
        float gw = width * 0.30f;
        float gh = height * 0.36f;
        float gcy = gy + gh * 0.5f;

        float amp = gh * 0.38f;

        stroke(255, 40);
        strokeWeight(1.0f);
        drawDashedLine(gx, gcy - amp, gx + gw, gcy - amp, 6f);
        drawDashedLine(gx, gcy + amp, gx + gw, gcy + amp, 6f);

        stroke(255, 180);
        strokeWeight(2.0f);
        line(gx - 15f, gcy, gx + gw + 25f, gcy);
        drawAxisArrow(gx + gw + 25f, gcy, 0);

        line(gx, gy + gh + 15f, gx, gy - 25f);
        drawAxisArrow(gx, gy - 25f, -HALF_PI);


        stroke(255, 180);
        strokeWeight(2.5f);
        noFill();
        beginShape();
        for (float tb = 0; tb <= 4.0f; tb += 0.04f) {
            float px = gx + (tb / 4.0f) * gw;
            float py = gcy - cos(PI * tb) * amp;
            vertex(px, py);
        }
        endShape();

        float curPx = gx + (b / 4.0f) * gw;
        float curPy = gcy - flux * amp;

        stroke(255, 100);
        strokeWeight(1.2f);
        drawDashedLine(curPx, gcy, curPx, curPy, 4f);
        drawDashedLine(gx, curPy, curPx, curPy, 4f);

        float dXdb = gw / 4.0f;
        float dYdb = amp * PI * sin(PI * b);
        float len = sqrt(dXdb * dXdb + dYdb * dYdb);
        float tanLen = 45f;
        float tx = (dXdb / len) * tanLen;
        float ty = (dYdb / len) * tanLen;

        stroke(255);
        strokeWeight(3.0f);
        line(curPx - tx, curPy - ty, curPx + tx, curPy + ty);

        fill(255);
        noStroke();
        circle(curPx, curPy, 8f);

        float barX = gx + gw + 60f;
        float barH = amp * 2.0f;

        stroke(255, 120);
        strokeWeight(1.5f);
        line(barX - 10f, gcy, barX + 10f, gcy);
        line(barX, gcy - barH * 0.5f - 10f, barX, gcy + barH * 0.5f + 10f);
        drawAxisArrow(barX, gcy - barH * 0.5f - 10f, -HALF_PI);

        float emfY = gcy - emf * amp;
        stroke(255);
        strokeWeight(5.0f);
        line(barX, gcy, barX, emfY);

        fill(255);
        noStroke();
        circle(barX, emfY, 7f);

    }

    private void drawCoilHalf(float x, float y, float rx, float ry, boolean isBack, float current, float b) {
        float startAngle = isBack ? HALF_PI : -HALF_PI;
        float endAngle = isBack ? PI + HALF_PI : HALF_PI;

        noFill();
        strokeCap(SQUARE);

        stroke(50);
        strokeWeight(18);
        arc(x, y, rx * 2, ry * 2, startAngle, endAngle);

        stroke(130);
        strokeWeight(12);
        arc(x, y, rx * 2, ry * 2, startAngle, endAngle);

        stroke(220);
        strokeWeight(4);
        arc(x, y, rx * 2, ry * 2, startAngle, endAngle);

        float absCurr = abs(current);
        if (!isBack && absCurr > 0.05f) {
            float speed = 280f;
            float pulseSpacing = 50f;
            float offset = (b * speed * (current > 0 ? 1 : -1)) % pulseSpacing;

            strokeCap(ROUND);
            float step = 0.08f;
            for (float a = startAngle; a <= endAngle; a += step) {
                float arcDist = (a - startAngle) * ry;
                float posInPattern = (arcDist + offset) % pulseSpacing;
                if (posInPattern < 0) posInPattern += pulseSpacing;

                float intensity = map(cos(TWO_PI * posInPattern / pulseSpacing), -1, 1, 0, 1);
                intensity = pow(intensity, 2.5f);

                if (intensity > 0.08f) {
                    float px = x + rx * cos(a);
                    float py = y + ry * sin(a);

                    stroke(255, 240 * intensity * absCurr);
                    strokeWeight(7 * intensity + 2);
                    point(px, py);
                }
            }
        }
    }

    private void drawMagnet(float mx, float cy) {
        float w = 220f;
        float h = 80f;

        rectMode(CENTER);

        stroke(255);
        strokeWeight(3f);
        fill(30);
        rect(mx, cy, w, h, 12);

        fill(255);
        noStroke();
        textAlign(CENTER, CENTER);
        textSize(38);
        text("S", mx - w / 4, cy);
        text("N", mx + w / 4, cy);

        stroke(255, 120);
        strokeWeight(2f);
        noFill();
        line(mx, cy - h / 2, mx, cy + h / 2);
    }

    private void drawDashedLine(float x1, float y1, float x2, float y2, float dashLen) {
        float d = dist(x1, y1, x2, y2);
        if (d < 0.001f) return;
        for (float i = 0; i < d; i += dashLen * 2) {
            float t1 = i / d;
            float t2 = min((i + dashLen) / d, 1.0f);
            line(lerp(x1, x2, t1), lerp(y1, y2, t1), lerp(x1, x2, t2), lerp(y1, y2, t2));
        }
    }

    private void drawAxisArrow(float x, float y, float angle) {
        pushMatrix();
        translate(x, y);
        rotate(angle);
        fill(255);
        noStroke();
        triangle(0, 0, -10, -4, -10, 4);
        popMatrix();
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
        PApplet.main("Classes.Mag4");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}