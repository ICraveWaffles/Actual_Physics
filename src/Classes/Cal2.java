package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class Cal2 extends PApplet {

    float t = 0;
    float w = 106 / 120f;
    PFont ntr;
    float logoTransparency;
    float transY;

    public static Blogo alogo;

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
        alogo.display(this, b, logoTransparency);

        if (b < 2.0f) {
            drawRodConduction(b);
        } else {
            drawConvectionCylinder(b - 2.0f);
        }

        popStyle();
    }

    void drawRodConduction(float bLocal) {
        float cx = width * 0.52f;
        float cy = height * 0.50f;
        float barW = width * 0.58f;
        float barH = height * 0.12f;

        float barLeft = cx - barW / 2f + 50f;

        stroke(255);
        strokeWeight(4);
        noFill();
        rectMode(CORNER);
        rect(barLeft, cy - barH / 2f, barW, barH, 8);

        int cols = 26;
        int rows = 4;
        float gridW = barW - 30f;
        float gridH = barH - 24f;
        float startX = barLeft + 15f;
        float startY = cy - gridH / 2f;

        float conductionProgress = map(bLocal, 0f, 2.0f, 0f, 1.25f);

        for (int c = 0; c < cols; c++) {
            float colNorm = c / (float) (cols - 1);
            float heatFactor = constrain((conductionProgress - colNorm * 0.85f) / 0.25f, 0f, 1f);

            for (int r = 0; r < rows; r++) {
                float x = startX + c * (gridW / (cols - 1));
                float y = startY + r * (gridH / (rows - 1));

                float vibAmp = 1.0f + heatFactor * 8.5f;
                float dx = sin(bLocal * 45f + c * 1.8f + r) * vibAmp;
                float dy = cos(bLocal * 40f + r * 2.2f + c) * vibAmp;

                if (c < cols - 1) {
                    float nextX = startX + (c + 1) * (gridW / (cols - 1));
                    stroke(255, 50 + heatFactor * 120);
                    strokeWeight(1.5f);
                    line(x + dx, y + dy, nextX, y);
                }

                fill(255);
                noStroke();
                ellipse(x + dx, y + dy, 9 + heatFactor * 3, 9 + heatFactor * 3);
            }
        }

        float tongsX = barLeft + 45f;
        drawTongs(tongsX, cy, barH, bLocal);

    }

    void drawTongs(float x, float y, float barH, float bLocal) {
        stroke(255);
        strokeWeight(4.5f);
        noFill();

        float gripYTop = y - barH / 2f - 4;
        float gripYBot = y + barH / 2f + 4;
        float pivotX = x - 90;

        beginShape();
        vertex(pivotX - 180, y - 70);
        vertex(pivotX, y - 10);
        vertex(x, gripYTop - 25);
        vertex(x, gripYTop);
        endShape();

        beginShape();
        vertex(pivotX - 180, y + 70);
        vertex(pivotX, y + 10);
        vertex(x, gripYBot + 25);
        vertex(x, gripYBot);
        endShape();

        fill(0);
        ellipse(pivotX, y, 14, 14);
        fill(255);
        ellipse(pivotX, y, 8, 8);

        stroke(255, 180);
        strokeWeight(2.5f);
        for (int i = 0; i < 3; i++) {
            float sparkOffset = (bLocal * 60f + i * 20f) % 25f;
            line(x + 10 + sparkOffset, gripYTop - 5, x + 20 + sparkOffset, gripYTop - 12);
            line(x + 10 + sparkOffset, gripYBot + 5, x + 20 + sparkOffset, gripYBot + 12);
        }
    }

    void drawConvectionCylinder(float bLocal) {
        float cx = width * 0.50f;
        float cy = height * 0.46f;
        float cylW = width * 0.44f;
        float cylH = height * 0.42f;

        float topY = cy - cylH / 2f;
        float botY = cy + cylH / 2f;
        float rx = cylW / 2f;
        float ry = cylH * 0.16f;

        stroke(255, 220);
        strokeWeight(3.5f);
        noFill();
        ellipse(cx, topY, cylW, ry * 2);

        arc(cx, botY, cylW, ry * 2, 0, PI);

        stroke(255, 90);
        strokeWeight(2f);
        drawDashedArc(cx, botY, cylW, ry * 2, PI, TWO_PI);

        stroke(255, 220);
        strokeWeight(3.5f);
        line(cx - rx, topY, cx - rx, botY);
        line(cx + rx, topY, cx + rx, botY);

        drawFlame(cx, botY + ry + 15f, bLocal);

        float cellR_X = cylW * 0.20f;
        float cellR_Y = cylH * 0.28f;

        float leftCellX = cx - cellR_X * 1.05f;
        float rightCellX = cx + cellR_X * 1.05f;
        float cellCenterY = cy + 10f;

        drawConvectionLoop(leftCellX, cellCenterY, cellR_X, cellR_Y, true, bLocal);

        drawConvectionLoop(rightCellX, cellCenterY, cellR_X, cellR_Y, false, bLocal);
    }

    void drawConvectionLoop(float lx, float ly, float rx, float ry, boolean ccw, float bLocal) {
        int numSegments = 4;
        float arcSpan = radians(62);
        float speed = bLocal * TWO_PI * 0.25f;

        for (int i = 0; i < numSegments; i++) {
            float baseAngle = i * HALF_PI;
            float currentAngle = ccw ? (baseAngle - speed) : (baseAngle + speed);

            float startA, endA;
            if (ccw) {
                startA = currentAngle + arcSpan;
                endA = currentAngle;
            } else {
                startA = currentAngle;
                endA = currentAngle + arcSpan;
            }

            stroke(255);
            strokeWeight(10);
            noFill();

            float normA1 = min(startA, endA);
            float normA2 = max(startA, endA);
            arc(lx, ly, rx * 2, ry * 2, normA1, normA2);

            drawFatArrowHead(lx, ly, rx, ry, endA, ccw);
        }
    }

    void drawFatArrowHead(float lx, float ly, float rx, float ry, float angle, boolean ccw) {
        float tipX = lx + rx * cos(angle);
        float tipY = ly + ry * sin(angle);

        float dx = ccw ? (rx * sin(angle)) : (-rx * sin(angle));
        float dy = ccw ? (-ry * cos(angle)) : (ry * cos(angle));
        float dir = atan2(dy, dx);

        float headLen = 22f;
        float headHalfW = 15f;

        float x1 = tipX;
        float y1 = tipY;

        float x2 = tipX - headLen * cos(dir) + headHalfW * sin(dir);
        float y2 = tipY - headLen * sin(dir) - headHalfW * cos(dir);

        float x3 = tipX - headLen * cos(dir) - headHalfW * sin(dir);
        float y3 = tipY - headLen * sin(dir) + headHalfW * cos(dir);

        fill(255);
        noStroke();
        triangle(x1, y1, x2, y2, x3, y3);
    }

    void drawFlame(float fx, float fy, float bLocal) {
        stroke(255);
        strokeWeight(3);
        line(fx - 25, fy + 45, fx + 25, fy + 45);
        line(fx - 12, fy + 45, fx - 12, fy + 60);
        line(fx + 12, fy + 45, fx + 12, fy + 60);

        noFill();
        strokeWeight(2.5f);

        for (int i = 0; i < 3; i++) {
            float alpha = 255 - i * 60;
            float scale = 1.0f - i * 0.22f;
            float flicker = sin(bLocal * 30f + i * 2) * 6f;

            stroke(255, alpha);
            beginShape();
            vertex(fx - 22 * scale, fy + 42);
            bezierVertex(fx - 30 * scale, fy + 15, fx - 10 * scale + flicker, fy - 15 * scale, fx, fy - 35 * scale);
            bezierVertex(fx + 10 * scale + flicker, fy - 15 * scale, fx + 30 * scale, fy + 15, fx + 22 * scale, fy + 42);
            endShape();
        }

        stroke(255, 180);
        strokeWeight(2f);
        for (int k = -1; k <= 1; k++) {
            float hx = fx + k * 18;
            float hyAnim = (bLocal * 50f + abs(k) * 10f) % 25f;
            float hy = fy - 10f - hyAnim;

            line(hx, hy, hx, hy - 12);
            line(hx, hy - 12, hx - 4, hy - 6);
            line(hx, hy - 12, hx + 4, hy - 6);
        }
    }

    void drawDashedArc(float cx, float cy, float w, float h, float startA, float endA) {
        float step = radians(8);
        for (float a = startA; a < endA; a += step * 2) {
            arc(cx, cy, w, h, a, min(a + step, endA));
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

        alogo = new Blogo(finalX, finalY, finalW, finalH);

        noCursor();
    }

    public static void main(String[] args) {
        PApplet.main("Classes.Cal2");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}