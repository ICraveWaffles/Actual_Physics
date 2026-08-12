package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class Cir1 extends PApplet {

    float t = 0;
    float w = 100f / 120f;
    PFont ntr;
    float logoTransparency;
    float transY;

    public static Blogo clogo;

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

        pushMatrix();
        drawScene(b, cx, cy);
        popMatrix();

        if (clogo != null) {
            clogo.display(this, b, logoTransparency);
        }

        popStyle();
    }

    private void drawScene(float b, float cx, float cy) {


        float acCurrent = sin(PI * b);
        float dcCurrent = abs(acCurrent);

        float xTransf = width * 0.82f;
        float xBridge = width * 0.50f;
        float xLoad = width * 0.18f;

        float r = 90f;

        float topY = cy - r;
        float botY = cy + r;
        float leftX = xBridge - r;
        float rightX = xBridge + r;

        drawTransformer(xTransf, cy, 7, 4, acCurrent, b);

        drawWireWithCurrent(xTransf - 40f, topY, xBridge, topY, acCurrent, b);
        drawWireWithCurrent(xTransf - 40f, botY, xBridge, botY, -acCurrent, b);

        boolean posCycle = acCurrent >= 0;

        drawDiodeSegment(xBridge, topY, rightX, cy, posCycle, dcCurrent, b);
        drawDiodeSegment(leftX, cy, xBridge, botY, posCycle, dcCurrent, b);
        drawDiodeSegment(xBridge, botY, rightX, cy, !posCycle, dcCurrent, b);
        drawDiodeSegment(leftX, cy, xBridge, topY, !posCycle, dcCurrent, b);


        drawWireWithCurrent(rightX, cy, rightX, cy - 220f, dcCurrent, b);
        drawWireWithCurrent(rightX, cy - 220f, xLoad, cy - 220f, dcCurrent, b);
        drawWireWithCurrent(xLoad, cy - 220f, xLoad, cy - 60f, dcCurrent, b);

        drawWireWithCurrent(xLoad, cy + 60f, xLoad, cy + 220f, dcCurrent, b);
        drawWireWithCurrent(xLoad, cy + 220f, leftX, cy + 220f, dcCurrent, b);
        drawWireWithCurrent(leftX, cy + 220f, leftX, cy, dcCurrent, b);

        drawLoadResistor(xLoad, cy, dcCurrent);

        drawNode(xBridge, topY);
        drawNode(xBridge, botY);
        drawNode(leftX, cy);
        drawNode(rightX, cy);
        drawNode(xTransf - 40f, topY);
        drawNode(xTransf - 40f, botY);
    }

    private void drawWireWithCurrent(float x1, float y1, float x2, float y2, float currentVal, float b) {
        strokeCap(SQUARE);

        stroke(40); strokeWeight(14); line(x1, y1, x2, y2);
        stroke(100); strokeWeight(7); line(x1, y1, x2, y2);
        stroke(180); strokeWeight(1.5f); line(x1, y1, x2, y2);

        float absCurr = abs(currentVal);
        if (absCurr > 0.02f) {
            float d = dist(x1, y1, x2, y2);
            if (d < 1f) return;

            float chargeSpacing = 28f;
            float speed = 220f * currentVal;
            float globalOffset = (b * speed) % chargeSpacing;
            if (globalOffset < 0) globalOffset += chargeSpacing;

            noStroke();
            for (float pos = globalOffset; pos <= d; pos += chargeSpacing) {
                float tFactor = pos / d;
                float px = lerp(x1, x2, tFactor);
                float py = lerp(y1, y2, tFactor);

                fill(200, 200, 0, 130 * absCurr);
                circle(px, py, 14 * absCurr + 2);

                fill(255, 240 * absCurr);
                circle(px, py, 5 * absCurr + 1);
            }
        }
    }

    private void drawTransformer(float x, float cy, int primLoops, int secLoops, float current, float b) {
        float coreWidth = 24f;
        float coreHeight = 280f;

        stroke(255, 140);
        strokeWeight(4f);
        line(x - coreWidth / 2f, cy - coreHeight / 2f, x - coreWidth / 2f, cy + coreHeight / 2f);
        line(x + coreWidth / 2f, cy - coreHeight / 2f, x + coreWidth / 2f, cy + coreHeight / 2f);

        float primX = x + 40f;
        float primH = 240f;
        float primStep = primH / primLoops;
        drawCoil(primX, cy - primH / 2f, primLoops, primStep, 35f);

        float secX = x - 40f;
        float secH = 180f;
        float secStep = secH / secLoops;
        drawCoil(secX, cy - secH / 2f, secLoops, secStep, 35f);
    }

    private void drawCoil(float x, float startY, int loops, float step, float rx) {
        noFill();
        strokeCap(ROUND);
        for (int i = 0; i < loops; i++) {
            float y = startY + i * step + step / 2f;
            stroke(50); strokeWeight(16); ellipse(x, y, rx * 2, step * 1.2f);
            stroke(130); strokeWeight(10); ellipse(x, y, rx * 2, step * 1.2f);
            stroke(220); strokeWeight(3); ellipse(x, y, rx * 2, step * 1.2f);
        }
    }

    private void drawDiodeSegment(float x1, float y1, float x2, float y2, boolean active, float dcCurrent, float b) {
        float current = active ? dcCurrent : 0f;
        drawWireWithCurrent(x1, y1, x2, y2, current, b);

        float mx = (x1 + x2) / 2f;
        float my = (y1 + y2) / 2f;
        float angle = atan2(y2 - y1, x2 - x1);

        pushMatrix();
        translate(mx, my);
        rotate(angle);

        if (active) {
            fill(0, 220, 255);
            stroke(0, 255, 255);
        } else {
            fill(30);
            stroke(100);
        }

        strokeWeight(2.5f);
        beginShape();
        vertex(14, 0);
        vertex(-14, -12);
        vertex(-14, 12);
        endShape(CLOSE);

        strokeWeight(3.5f);
        line(14, -14, 14, 14);

        popMatrix();
    }

    private void drawLoadResistor(float x, float cy, float current) {
        pushMatrix();
        translate(x, cy);

        if (current > 0.05f) {
            stroke(0, 220, 255, 140 * current);
            strokeWeight(16);
            noFill();
            drawZigZag();
        }

        stroke(255);
        strokeWeight(3.5f);
        noFill();
        strokeJoin(ROUND);
        drawZigZag();

        popMatrix();
    }

    private void drawZigZag() {
        beginShape();
        vertex(0, -60);
        vertex(-18, -45);
        vertex(18, -15);
        vertex(-18, 15);
        vertex(18, 45);
        vertex(0, 60);
        endShape();
    }

    private void drawNode(float x, float y) {
        fill(255);
        noStroke();
        circle(x, y, 12);
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
        clogo = new Blogo(finalX, finalY, finalW, finalH);
        noCursor();
    }

    public static void main(String[] args) {
        PApplet.main("Classes.Cir1");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}