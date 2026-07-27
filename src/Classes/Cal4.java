package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class Cal4 extends PApplet {

    float t = 0;
    float w = 106 / 120f;
    PFont ntr;
    float logoTransparency;
    float transY;

    public static Blogo blogo;

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
        blogo.display(this, b, logoTransparency);

        float focusX = width * 0.5f;
        float focusY = height * 0.42f;

        float zoomStartBeat = 2f;
        float zoomProgress = constrain((b - zoomStartBeat) / (maxBeats - zoomStartBeat), 0f, 1f);
        float easeZoom = zoomProgress * zoomProgress * (3 - 2 * zoomProgress);

        float scaleVal = lerp(1.0f, 100f, easeZoom);

        float currentFocusX = lerp(width * 0.50f, focusX, easeZoom);
        float currentFocusY = lerp(height * 0.50f, focusY, easeZoom);

        pushMatrix();
        translate(width * 0.5f, height * 0.5f);
        scale(scaleVal);
        translate(-currentFocusX, -currentFocusY);

        float surfaceY = height * 0.78f;

        stroke(255, 180);
        strokeWeight(3.0f / scaleVal);
        line(width * 0.02f, surfaceY, width * 0.98f, surfaceY);

        strokeWeight(1.5f / scaleVal);
        for (float x = width * 0.02f; x < width * 0.98f; x += 15) {
            line(x, surfaceY, x - 8, surfaceY + 12);
        }

        drawEnergyBudgetDiagram(scaleVal, easeZoom, surfaceY);

        drawSecondaryCO2Molecules(b, scaleVal, easeZoom);

        // 3. Dibujar la molécula exactamente en (focusX, focusY)
        drawCO2Molecule(focusX, focusY, b, scaleVal);

        popMatrix();

        popStyle();
    }

    void drawEnergyBudgetDiagram(float scaleVal, float easeZoom, float surfaceY) {
        float alpha = (1f - easeZoom / 0.85f) * 255;
        if (alpha <= 0) return;

        fill(255, alpha * 0.85f);
        float inX = width * 0.28f;

        drawThickArrow(inX, height * 0.05f, inX, surfaceY, 35f, scaleVal, true);

        drawThickArrow(inX, height * 0.35f, inX + width * 0.12f, height * 0.45f, 16f, scaleVal, true);

        drawThickArrow(inX, height * 0.25f, inX - width * 0.10f, height * 0.15f, 6f, scaleVal, true);

        drawCloud(inX, height * 0.50f, 100f, scaleVal, alpha);
        drawThickArrow(inX, height * 0.50f, inX - width * 0.14f, height * 0.25f, 12f, scaleVal, true);

        drawThickArrow(inX, surfaceY, inX - width * 0.20f, height * 0.35f, 5f, scaleVal, true);

        fill(200, alpha * 0.85f);

        float condX = width * 0.60f;
        drawThickArrow(condX, surfaceY, condX, height * 0.62f, 6f, scaleVal, true);

        float latX = width * 0.68f;
        drawThickArrow(latX, surfaceY, latX, height * 0.52f, 14f, scaleVal, true);

        float lwX = width * 0.86f;
        drawThickArrow(lwX, surfaceY, lwX, height * 0.55f, 22f, scaleVal, false);

        drawThickArrow(lwX, height * 0.55f, lwX, height * 0.15f, 8f, scaleVal, true);

        drawThickArrow(lwX, height * 0.55f, lwX - width * 0.10f, height * 0.42f, 14f, scaleVal, true);

        drawCloud(width * 0.73f, height * 0.38f, 160f, scaleVal, alpha);

        drawThickArrow(width * 0.65f, height * 0.38f, width * 0.65f, height * 0.22f, 12f, scaleVal, true);

        drawThickArrow(width * 0.77f, height * 0.38f, width * 0.77f, height * 0.10f, 18f, scaleVal, true);
    }

    void drawThickArrow(float x1, float y1, float x2, float y2, float thickness, float scaleVal, boolean drawHead) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float len = dist(x1, y1, x2, y2);
        float angle = atan2(dy, dx);

        float t = thickness / scaleVal;
        float headLen = t * 1.8f;
        float headWidth = t * 1.5f;

        pushMatrix();
        translate(x1, y1);
        rotate(angle);
        noStroke();

        if (drawHead) {
            float rectLen = max(0, len - headLen);
            rect(0, -t / 2f, rectLen, t);
            translate(rectLen, 0);
            triangle(0, -headWidth, 0, headWidth, headLen, 0);
        } else {
            rect(0, -t / 2f, len, t);
        }

        popMatrix();
    }

    void drawCloud(float cx, float cy, float w, float scaleVal, float alpha) {
        noStroke();
        fill(255, alpha * 0.9f);
        float sW = w / scaleVal;

        ellipse(cx, cy, sW, sW * 0.6f);
        ellipse(cx - sW * 0.3f, cy + sW * 0.1f, sW * 0.7f, sW * 0.4f);
        ellipse(cx + sW * 0.3f, cy + sW * 0.1f, sW * 0.7f, sW * 0.4f);
        ellipse(cx - sW * 0.15f, cy - sW * 0.15f, sW * 0.6f, sW * 0.5f);
        ellipse(cx + sW * 0.15f, cy - sW * 0.15f, sW * 0.6f, sW * 0.5f);
    }

    void drawCO2Molecule(float cx, float cy, float b, float scaleVal) {
        float beatPhase = sin(b * TWO_PI);
        float stretch = beatPhase * 18f;

        float cX = cx - beatPhase * 4f;

        float leftBondLen = 70f + stretch;
        float rightBondLen = 70f - stretch;

        float o1X = cX - leftBondLen;
        float o2X = cX + rightBondLen;

        drawDoubleBond(cX, cy, o1X, cy, scaleVal);
        drawDoubleBond(cX, cy, o2X, cy, scaleVal);

        noFill();
        stroke(255, 130 + sin(b * TWO_PI * 4f) * 110);
        strokeWeight(1.5f / scaleVal);
        for (int i = 1; i <= 3; i++) {
            float rX = (30f + i * 16f) + stretch * 0.5f;
            float rY = (20f + i * 10f);
            ellipse(cX, cy, rX * 2, rY * 2);
        }

        fill(0);
        stroke(255);
        strokeWeight(2.5f / scaleVal);
        ellipse(o1X, cy, 28f, 28f);
        ellipse(o2X, cy, 28f, 28f);

        fill(255);
        noStroke();
        ellipse(cX, cy, 34f, 34f);

        stroke(255, 180);
        strokeWeight(1.5f / scaleVal);
        noFill();
        ellipse(cX, cy, 42f, 42f);
    }

    void drawDoubleBond(float x1, float y1, float x2, float y2, float scaleVal) {
        float gap = 5f / scaleVal;
        stroke(255, 220);
        strokeWeight(2.5f / scaleVal);
        line(x1, y1 - gap, x2, y2 - gap);
        line(x1, y1 + gap, x2, y2 + gap);
    }

    void drawSecondaryCO2Molecules(float b, float scaleVal, float easeZoom) {
        float alpha = (1f - easeZoom) * 255;
        if (alpha <= 0) return;

        float[][] pos = {
                {width * 0.40f, height * 0.35f, 0.3f},
                {width * 0.58f, height * 0.28f, -0.2f},
                {width * 0.42f, height * 0.65f, 0.5f},
                {width * 0.80f, height * 0.68f, -0.4f},
                {width * 0.15f, height * 0.70f, 0.8f}
        };

        for (int i = 0; i < pos.length; i++) {
            float mx = pos[i][0];
            float my = pos[i][1];
            float angle = pos[i][2];

            pushMatrix();
            translate(mx, my);
            rotate(angle);

            float localPhase = sin(b * TWO_PI * 2f + i) * 8f;
            float cx = -localPhase * 2f;
            float o1 = cx - (38f + localPhase);
            float o2 = cx + (38f - localPhase);

            stroke(255, alpha * 0.8f);
            strokeWeight(1.5f / scaleVal);
            line(o1, -2, o2, -2);
            line(o1, 2, o2, 2);

            fill(0);
            stroke(255, alpha);
            ellipse(o1, 0, 14, 14);
            ellipse(o2, 0, 14, 14);

            fill(255, alpha);
            ellipse(cx, 0, 18, 18);

            popMatrix();
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

        blogo = new Blogo(finalX, finalY, finalW, finalH);

        noCursor();
    }

    public static void main(String[] args) {
        PApplet.main("Classes.Cal4");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}