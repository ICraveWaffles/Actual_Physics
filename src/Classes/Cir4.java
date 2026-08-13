package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class Cir4 extends PApplet {

    float t = 0;
    float w = 100f / 120f;
    PFont ntr;
    float logoTransparency;
    float transY;

    public static Blogo blogo;

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

        float cx = width * 0.58f;
        float cy = height * 0.50f;

        pushMatrix();
        drawScene(b, cx, cy);
        popMatrix();

        if (blogo != null) {
            blogo.display(this, b, logoTransparency);
        }

        popStyle();
    }

    private void drawScene(float b, float cx, float cy) {
        float topCutY = height * 0.14f;
        float bottomCutY = height * 0.86f;

        boolean isOpen = (b < 2.0f);

        float chargeLevel;
        if (isOpen) {
            chargeLevel = 1.0f - exp(-b * 2.5f);
        } else {
            chargeLevel = exp(-(b - 2.0f) * 2.5f);
        }

        float zoomProgress = constrain(map(b, 3.0f, 4.0f, 0f, 1f), 0f, 1f);
        zoomProgress = zoomProgress * zoomProgress * (3f - 2f * zoomProgress);
        float zoom = lerp(1.0f, 2.6f, zoomProgress);

        float topY = cy - 160f;
        float botY = cy + 160f;

        float xStart = cx - 280f;
        float xSplit = cx + 80f;
        float xJoin = cx + 280f;

        float c1X = cx - 80f;
        float c1Y = cy;

        float c2X = cx + 180f;
        float c2Y = cy - 75f;
        float c3X = cx + 180f;
        float c3Y = cy + 75f;

        float switchX = cx - 180f;

        drawChargeGraph(width * 0.05f, height * 0.30f, width * 0.18f, height * 0.40f, b, chargeLevel);

        pushMatrix();
        translate(c1X, c1Y);
        scale(zoom);
        translate(-c1X, -c1Y);

        float capHalfW = 16f;

        drawWireWithCurrent(xStart, topY, switchX - 25f, topY, 0f, b);
        drawSwitch(switchX, topY, isOpen);
        drawWireWithCurrent(switchX + 25f, topY, cx - 35f, topY, 0f, b);
        drawBatteryHorizontal(cx, topY);
        drawWireWithCurrent(cx + 35f, topY, xJoin, topY, 0f, b);

        drawWireWithCurrent(xStart, topY, xStart, botY, 0f, b);
        drawWireWithCurrent(xJoin, topY, xJoin, botY, 0f, b);

        drawWireWithCurrent(xStart, botY, xJoin, botY, 0f, b);

        float dischargeCurr = isOpen ? 0f : -chargeLevel;
        drawWireWithCurrent(xStart, cy, c1X - capHalfW, cy, dischargeCurr, b);
        drawWireWithCurrent(c1X + capHalfW, cy, xSplit, cy, dischargeCurr, b);

        drawWireWithCurrent(xSplit, cy, xSplit, c2Y, dischargeCurr * 0.5f, b);
        drawWireWithCurrent(xSplit, cy, xSplit, c3Y, dischargeCurr * 0.5f, b);

        drawWireWithCurrent(xSplit, c2Y, c2X - capHalfW, c2Y, dischargeCurr * 0.5f, b);
        drawWireWithCurrent(xSplit, c3Y, c3X - capHalfW, c3Y, dischargeCurr * 0.5f, b);

        drawWireWithCurrent(c2X + capHalfW, c2Y, xJoin, c2Y, dischargeCurr * 0.5f, b);
        drawWireWithCurrent(c3X + capHalfW, c3Y, xJoin, c3Y, dischargeCurr * 0.5f, b);

        drawNode(xSplit, cy, 255);
        drawNode(xJoin, cy, 255);

        drawFloatingCapacitor(c1X, c1Y, chargeLevel, b, 0.0f, 255);
        drawFloatingCapacitor(c2X, c2Y, chargeLevel, b, 1.5f, 255);
        drawFloatingCapacitor(c3X, c3Y, chargeLevel, b, 3.0f, 255);

        popMatrix();

        drawEnclosures(topCutY, bottomCutY);
    }

    private void drawFloatingCapacitor(float x, float y, float charge, float b, float phase, float alpha) {
        pushMatrix();
        pushStyle();

        float floatY = y + sin(b * TWO_PI * 0.8f + phase) * 7f;
        translate(x, floatY);

        float plateH = 46f;
        float plateW = 6f;
        float gap = 16f;

        stroke(180, alpha);
        strokeWeight(2.5f);
        line(-30, 0, -gap / 2f, 0);
        line(gap / 2f, 0, 30, 0);

        if (charge > 0.05f) {
            noStroke();
            fill(200, 200, 0, 80 * charge * (alpha / 255f));
            rectMode(CENTER);
            rect(0, 0, gap + 20f, plateH + 12f, 8f);
        }

        stroke(255, alpha);
        strokeWeight(1.5f);
        fill(40, alpha);
        rectMode(CENTER);
        rect(-gap / 2f, 0, plateW, plateH, 2f);
        rect(gap / 2f, 0, plateW, plateH, 2f);

        if (charge > 0.02f) {
            noStroke();
            fill(200, 200, 0, 220 * charge * (alpha / 255f));
            for (float py = -plateH / 2f + 6; py <= plateH / 2f - 6; py += 10) {
                circle(-gap / 2f - 1, py, 4);
                circle(gap / 2f + 1, py, 4);
            }
        }

        popStyle();
        popMatrix();
    }

    private void drawBatteryHorizontal(float x, float y) {
        pushMatrix();
        pushStyle();
        translate(x, y);

        stroke(255);
        strokeWeight(3f);
        line(-12, -22, -12, 22);

        strokeWeight(7f);
        line(12, -12, 12, 12);

        popStyle();
        popMatrix();
    }

    private void drawSwitch(float x, float y, boolean isOpen) {
        pushMatrix();
        pushStyle();
        translate(x, y);

        fill(255);
        noStroke();
        circle(-20, 0, 8);
        circle(20, 0, 8);

        stroke(255);
        strokeWeight(3.5f);
        if (isOpen) {
            line(-20, 0, 15, -20);
        } else {
            line(-20, 0, 20, 0);
        }

        popStyle();
        popMatrix();
    }

    private void drawChargeGraph(float gx, float gy, float gw, float gh, float b, float currentCharge) {
        pushMatrix();
        pushStyle();

        stroke(120);
        strokeWeight(1.5f);
        line(gx, gy + gh, gx + gw, gy + gh);
        line(gx, gy, gx, gy + gh);

        noFill();
        stroke(255, 180);
        strokeWeight(2f);
        beginShape();
        for (float px = 0; px <= gw; px += 2) {
            float timeVal = map(px, 0, gw, 0f, 4.0f);
            float qVal;
            if (timeVal < 2.0f) {
                qVal = 1.0f - exp(-timeVal * 2.5f);
            } else {
                qVal = exp(-(timeVal - 2.0f) * 2.5f);
            }
            float py = gy + gh - (qVal * (gh - 15f));
            vertex(gx + px, py);
        }
        endShape();

        float currentX = map(b, 0f, 4.0f, 0f, gw);
        float currentY = gy + gh - (currentCharge * (gh - 15f));

        noStroke();
        fill(200, 200, 0, 100);
        circle(gx + currentX, currentY, 14);
        fill(200, 200, 0, 255);
        circle(gx + currentX, currentY, 6);

        stroke(200, 200, 0, 90);
        strokeWeight(1f);
        line(gx + currentX, gy + gh, gx + currentX, currentY);

        popStyle();
        popMatrix();
    }

    private void drawWireWithCurrent(float x1, float y1, float x2, float y2, float currentVal, float b) {
        pushStyle();
        strokeCap(SQUARE);

        stroke(40); strokeWeight(12); line(x1, y1, x2, y2);
        stroke(100); strokeWeight(6); line(x1, y1, x2, y2);
        stroke(180); strokeWeight(1.5f); line(x1, y1, x2, y2);

        float absCurr = abs(currentVal);
        if (absCurr > 0.02f) {
            float d = dist(x1, y1, x2, y2);
            if (d >= 1f) {
                float chargeSpacing = 26f;
                float speed = 220f * currentVal;
                float globalOffset = (b * speed) % chargeSpacing;
                if (globalOffset < 0) globalOffset += chargeSpacing;

                noStroke();
                for (float pos = globalOffset; pos <= d; pos += chargeSpacing) {
                    float tFactor = pos / d;
                    float px = lerp(x1, x2, tFactor);
                    float py = lerp(y1, y2, tFactor);

                    fill(200, 200, 0, 130 * absCurr);
                    circle(px, py, 12 * absCurr + 2);

                    fill(200, 200, 0, 240 * absCurr);
                    circle(px, py, 4 * absCurr + 1);
                }
            }
        }
        popStyle();
    }

    private void drawNode(float x, float y, float alpha) {
        pushStyle();
        fill(255, alpha);
        noStroke();
        circle(x, y, 10);
        popStyle();
    }

    private void drawEnclosures(float topY, float bottomY) {
        pushStyle();
        rectMode(CORNER);
        fill(0);
        stroke(255, 180);
        strokeWeight(2.5f);

        rect(-10, -10, width + 20, topY + 10, 0, 0, 16, 16);
        rect(-10, bottomY, width + 20, height - bottomY + 10, 16, 16, 0, 0);
        popStyle();
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
        PApplet.main("Classes.Cir4");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}