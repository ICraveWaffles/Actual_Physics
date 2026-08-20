package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class Cir3 extends PApplet {

    float t = 0;
    float w = 100f / 120f;
    PFont ntr;
    float logoTransparency;
    float transY;

    public static Blogo blogo;

    float cycleTime = 9.6f;

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

        float cx = width * 0.62f;
        float cy = height * 0.50f;

        pushMatrix();
        pushStyle();
        drawScene(b, cx, cy);
        popStyle();
        popMatrix();

        if (blogo != null) {
            pushMatrix();
            pushStyle();
            blogo.display(this, b, logoTransparency);
            popStyle();
            popMatrix();
        }

        popStyle();
    }

    private void drawScene(float b, float cx, float cy) {
        float topCutY = height * 0.18f;
        float bottomCutY = height * 0.82f;

        float topY = cy - 230f;
        float xStart = cx - 380f;
        float xJoin = cx + 380f;
        float xSplit = cx + 40f;

        float comp1X = cx - 170f;
        float comp2X = cx + 210f;
        float comp2Y = cy - 110f;
        float comp3X = cx + 210f;
        float comp3Y = cy + 110f;

        float switchX = cx - 180f;
        float batteryX = cx + 80f;

        if (b < 4.0f) {
            float localB = b;
            float transition = constrain(map(localB, 1.8f, 2.2f, 0f, 1f), 0f, 1f);

            float zoom = lerp(1.8f, 1.0f, transition);

            pushMatrix();
            translate(comp1X, cy);
            scale(zoom);
            translate(-comp1X, -cy);

            float resHalfW = 65f;
            float currentVal = 1.0f;

            drawWireWithCurrent(xJoin, topY, batteryX + 40f, topY, currentVal, localB);
            drawBatteryHorizontal(batteryX, topY);
            drawWireWithCurrent(batteryX - 40f, topY, switchX + 35f, topY, currentVal, localB);
            drawSwitch(switchX, topY, false);
            drawWireWithCurrent(switchX - 35f, topY, xStart, topY, currentVal, localB);

            drawWireWithCurrent(xStart, topY, xStart, cy, currentVal, localB);

            drawWireWithCurrent(xStart, cy, comp1X - resHalfW, cy, currentVal, localB);

            if (transition > 0.01f) {
                drawWireWithCurrent(comp1X + resHalfW, cy, xSplit, cy, currentVal, localB);

                drawWireWithCurrent(xSplit, cy, xSplit, comp2Y, currentVal * 0.5f, localB);
                drawWireWithCurrent(xSplit, comp2Y, comp2X - resHalfW, comp2Y, currentVal * 0.5f, localB);
                drawWireWithCurrent(comp2X + resHalfW, comp2Y, xJoin, comp2Y, currentVal * 0.5f, localB);

                drawWireWithCurrent(xSplit, cy, xSplit, comp3Y, currentVal * 0.5f, localB);
                drawWireWithCurrent(xSplit, comp3Y, comp3X - resHalfW, comp3Y, currentVal * 0.5f, localB);
                drawWireWithCurrent(comp3X + resHalfW, comp3Y, xJoin, comp3Y, currentVal * 0.5f, localB);

                drawWireWithCurrent(xJoin, comp2Y, xJoin, cy, currentVal * 0.5f, localB);
                drawWireWithCurrent(xJoin, comp3Y, xJoin, cy, currentVal * 0.5f, localB);

                drawNode(xSplit, cy, transition * 255);
                drawNode(xJoin, cy, transition * 255);
            } else {
                drawWireWithCurrent(comp1X + resHalfW, cy, xJoin, cy, currentVal, localB);
            }

            drawWireWithCurrent(xJoin, cy, xJoin, topY, currentVal, localB);

            drawFloatingResistor(comp1X, cy, localB, 0.0f, 255);
            if (transition > 0.01f) {
                drawFloatingResistor(comp2X, comp2Y, localB, 1.5f, transition * 255);
                drawFloatingResistor(comp3X, comp3Y, localB, 3.0f, transition * 255);
            }

            popMatrix();

        } else {
            float localB = b - 4.0f;
            boolean isOpen = (localB < 2.0f);

            float chargeLevel;
            float currentVal;

            if (isOpen) {
                chargeLevel = 1.0f - exp(-localB * 2.5f);
                currentVal = exp(-localB * 2.5f);
            } else {
                chargeLevel = exp(-(localB - 2.0f) * 2.5f);
                currentVal = -exp(-(localB - 2.0f) * 2.5f);
            }

            float zoomProgress = constrain(map(localB, 3.5f, 4.0f, 0f, 1f), 0f, 1f);
            zoomProgress = (float) Math.pow(zoomProgress, 4);
            float zoom = lerp(1.0f, 35f, zoomProgress);

            float graphW = width * 0.28f;
            float graphH = height * 0.45f;
            float graphX = width * 0.04f;
            float graphY = height * 0.28f;
            drawChargeGraph(graphX, graphY, graphW, graphH, localB, chargeLevel);

            pushMatrix();
            translate(comp1X, cy);
            scale(zoom);
            translate(-comp1X, -cy);

            float capHalfW = 26f;

            drawWireWithCurrent(xJoin, topY, batteryX + 40f, topY, currentVal, localB);
            drawBatteryHorizontal(batteryX, topY);
            drawWireWithCurrent(batteryX - 40f, topY, switchX + 35f, topY, currentVal, localB);
            drawSwitch(switchX, topY, isOpen);
            drawWireWithCurrent(switchX - 35f, topY, xStart, topY, currentVal, localB);

            drawWireWithCurrent(xStart, topY, xStart, cy, currentVal, localB);

            drawWireWithCurrent(xStart, cy, comp1X - capHalfW, cy, currentVal, localB);
            drawWireWithCurrent(comp1X + capHalfW, cy, xSplit, cy, currentVal, localB);

            drawWireWithCurrent(xSplit, cy, xSplit, comp2Y, currentVal * 0.5f, localB);
            drawWireWithCurrent(xSplit, comp2Y, comp2X - capHalfW, comp2Y, currentVal * 0.5f, localB);
            drawWireWithCurrent(comp2X + capHalfW, comp2Y, xJoin, comp2Y, currentVal * 0.5f, localB);

            drawWireWithCurrent(xSplit, cy, xSplit, comp3Y, currentVal * 0.5f, localB);
            drawWireWithCurrent(xSplit, comp3Y, comp3X - capHalfW, comp3Y, currentVal * 0.5f, localB);
            drawWireWithCurrent(comp3X + capHalfW, comp3Y, xJoin, comp3Y, currentVal * 0.5f, localB);

            drawWireWithCurrent(xJoin, comp2Y, xJoin, cy, currentVal * 0.5f, localB);
            drawWireWithCurrent(xJoin, comp3Y, xJoin, cy, currentVal * 0.5f, localB);

            drawNode(xSplit, cy, 255);
            drawNode(xJoin, cy, 255);

            drawWireWithCurrent(xJoin, cy, xJoin, topY, currentVal, localB);

            drawFloatingCapacitor(comp1X, cy, chargeLevel, localB, 0.0f, 255);
            drawFloatingCapacitor(comp2X, comp2Y, chargeLevel, localB, 1.5f, 255);
            drawFloatingCapacitor(comp3X, comp3Y, chargeLevel, localB, 3.0f, 255);

            popMatrix();
        }

        drawEnclosures(topCutY, bottomCutY);
    }

    private void drawFloatingResistor(float x, float y, float b, float phase, float alpha) {
        if (alpha <= 1f) return;
        pushMatrix();
        pushStyle();

        float floatY = y + sin(b * TWO_PI * 0.8f + phase) * 8f;
        translate(x, floatY);

        float bodyW = 130f;
        float bodyH = 46f;

        stroke(180, alpha);
        strokeWeight(4f);
        line(-bodyW / 2f - 25f, 0, -bodyW / 2f, 0);
        line(bodyW / 2f, 0, bodyW / 2f + 25f, 0);

        noStroke();
        fill(255, 14 * (alpha / 255f));
        rectMode(CENTER);
        rect(0, 0, bodyW + 20f, bodyH + 20f, 16f);

        stroke(230, alpha);
        strokeWeight(2.5f);
        fill(28, alpha);
        rect(0, 0, bodyW, bodyH, 12f);

        fill(80, alpha);
        rect(-bodyW / 2f + 5f, 0, 10f, bodyH + 3f, 4f);
        rect(bodyW / 2f - 5f, 0, 10f, bodyH + 3f, 4f);

        fill(255, alpha);
        noStroke();
        rectMode(CORNER);
        float bandW = 8.5f;
        rect(-bodyW / 2f + 26f, -bodyH / 2f, bandW, bodyH);
        rect(-bodyW / 2f + 44f, -bodyH / 2f, bandW, bodyH);
        rect(-bodyW / 2f + 62f, -bodyH / 2f, bandW, bodyH);
        rect(bodyW / 2f - 34f, -bodyH / 2f, bandW, bodyH);

        popStyle();
        popMatrix();
    }

    private void drawFloatingCapacitor(float x, float y, float charge, float b, float phase, float alpha) {
        pushMatrix();
        pushStyle();

        float floatY = y + sin(b * TWO_PI * 0.8f + phase) * 6f;
        translate(x, floatY);

        float plateH = 80f;
        float plateW = 10f;
        float gap = 32f;

        stroke(180, alpha);
        strokeWeight(4f);
        line(-50, 0, -gap / 2f, 0);
        line(gap / 2f, 0, 50, 0);

        if (charge > 0.05f) {
            noStroke();
            fill(255, 90 * charge * (alpha / 255f));
            rectMode(CENTER);
            rect(0, 0, gap + 30f, plateH + 20f, 12f);
        }

        stroke(255, alpha);
        strokeWeight(2.5f);
        fill(40, alpha);
        rectMode(CENTER);
        rect(-gap / 2f, 0, plateW, plateH, 4f);
        rect(gap / 2f, 0, plateW, plateH, 4f);

        if (charge > 0.02f) {
            noStroke();
            fill(255, 230 * charge * (alpha / 255f));
            for (float py = -plateH / 2f + 10; py <= plateH / 2f - 10; py += 15) {
                circle(-gap / 2f - 1.5f, py, 6.5f);
                circle(gap / 2f + 1.5f, py, 6.5f);
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
        strokeWeight(5f);
        line(-18, -32, -18, 32);

        strokeWeight(10f);
        line(18, -18, 18, 18);

        popStyle();
        popMatrix();
    }

    private void drawSwitch(float x, float y, boolean isOpen) {
        pushMatrix();
        pushStyle();
        translate(x, y);

        fill(255);
        noStroke();
        circle(-30, 0, 12);
        circle(30, 0, 12);

        stroke(255);
        strokeWeight(5f);
        strokeCap(ROUND);
        if (isOpen) {
            line(-30, 0, 20, -30);
        } else {
            line(-30, 0, 30, 0);
        }

        popStyle();
        popMatrix();
    }

    private void drawChargeGraph(float gx, float gy, float gw, float gh, float localB, float currentCharge) {
        pushMatrix();
        pushStyle();

        stroke(120);
        strokeWeight(2f);
        line(gx, gy + gh, gx + gw, gy + gh);
        line(gx, gy, gx, gy + gh);

        noFill();
        stroke(255, 180);
        strokeWeight(3f);
        beginShape();
        for (float px = 0; px <= gw; px += 2) {
            float timeVal = map(px, 0, gw, 0f, 4.0f);
            float qVal;
            if (timeVal < 2.0f) {
                qVal = 1.0f - exp(-timeVal * 2.5f);
            } else {
                qVal = exp(-(timeVal - 2.0f) * 2.5f);
            }
            float py = gy + gh - (qVal * (gh - 25f));
            vertex(gx + px, py);
        }
        endShape();

        float currentX = map(localB, 0f, 4.0f, 0f, gw);
        float currentY = gy + gh - (currentCharge * (gh - 25f));

        noStroke();
        fill(255, 100);
        circle(gx + currentX, currentY, 20);
        fill(255, 255);
        circle(gx + currentX, currentY, 10);

        stroke(255, 90);
        strokeWeight(2f);
        line(gx + currentX, gy + gh, gx + currentX, currentY);

        popStyle();
        popMatrix();
    }

    private void drawWireWithCurrent(float x1, float y1, float x2, float y2, float currentVal, float b) {
        pushStyle();
        strokeCap(SQUARE);

        stroke(40); strokeWeight(14); line(x1, y1, x2, y2);
        stroke(100); strokeWeight(8); line(x1, y1, x2, y2);
        stroke(180); strokeWeight(2f); line(x1, y1, x2, y2);

        float absCurr = abs(currentVal);
        if (absCurr > 0.02f) {
            float d = dist(x1, y1, x2, y2);
            if (d >= 1f) {
                float chargeSpacing = 34f;
                float speed = 260f * currentVal;
                float globalOffset = (b * speed) % chargeSpacing;
                if (globalOffset < 0) globalOffset += chargeSpacing;

                noStroke();
                for (float pos = globalOffset; pos <= d; pos += chargeSpacing) {
                    float tFactor = pos / d;
                    float px = lerp(x1, x2, tFactor);
                    float py = lerp(y1, y2, tFactor);

                    fill(255, 130 * absCurr);
                    circle(px, py, 16 * absCurr + 3);

                    fill(255, 240 * absCurr);
                    circle(px, py, 6 * absCurr + 1);
                }
            }
        }
        popStyle();
    }

    private void drawNode(float x, float y, float alpha) {
        pushStyle();
        fill(255, alpha);
        noStroke();
        circle(x, y, 16);
        popStyle();
    }

    private void drawEnclosures(float topY, float bottomY) {
        pushStyle();
        rectMode(CORNER);
        fill(0);
        stroke(255, 180);
        noStroke();

        rect(-10, -10, width + 20, topY + 10, 0, 0, 20, 20);
        rect(-10, bottomY, width + 20, height - bottomY + 10, 20, 20, 0, 0);
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
        PApplet.main("Classes.Cir3");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}