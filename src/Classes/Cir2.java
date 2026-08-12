package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class Cir2 extends PApplet {

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
        float topCutY = height * 0.14f;
        float bottomCutY = height * 0.86f;

        float x1 = width * 0.22f;
        float x2 = width * 0.50f;
        float x3 = width * 0.78f;

        float midX12 = (x1 + x2) / 2f;
        float midX23 = (x2 + x3) / 2f;

        float topY = cy - 180f;
        float botY = cy + 180f;

        float i1 = 1.0f;
        float i3 = 0.6f;
        float i2 = 0.4f;

        drawWireWithCurrent(x1, cy - 35f, x1, topY, i1, b);
        drawWireWithCurrent(x1, botY, x1, cy + 35f, i1, b);
        drawBattery(x1, cy);

        drawWireWithCurrent(x1, topY, midX12 - 37.5f, topY, i1, b);
        drawResistorHorizontal(midX12, topY);
        drawWireWithCurrent(midX12 + 37.5f, topY, x2, topY, i1, b);

        drawWireWithCurrent(x2, topY, x2, cy - 37.5f, i3, b);
        drawResistorVertical(x2, cy);
        drawWireWithCurrent(x2, cy + 37.5f, x2, botY, i3, b);

        drawWireWithCurrent(x2, topY, midX23 - 37.5f, topY, i2, b);
        drawResistorHorizontal(midX23, topY);
        drawWireWithCurrent(midX23 + 37.5f, topY, x3, topY, i2, b);

        drawWireWithCurrent(x3, topY, x3, cy - 37.5f, i2, b);
        drawResistorVertical(x3, cy);
        drawWireWithCurrent(x3, cy + 37.5f, x3, botY, i2, b);

        drawWireWithCurrent(x3, botY, x2, botY, i2, b);

        drawWireWithCurrent(x2, botY, x1, botY, i1, b);

        drawNode(x2, topY, b >= 2.0f);
        drawNode(x2, botY, false);
        drawNode(x1, topY, false);
        drawNode(x1, botY, false);
        drawNode(x3, topY, false);
        drawNode(x3, botY, false);

        if (b < 2.0f) {
            drawMeshLoops(midX12, midX23, cy, b);
        } else {
            drawNodeLaw(x2, topY, b);
        }

    }

    private void drawBattery(float x, float y) {
        pushMatrix();
        translate(x, y);

        stroke(255);
        strokeWeight(4f);
        line(-28, -14, 28, -14);

        strokeWeight(8f);
        line(-14, 14, 14, 14);

        popMatrix();
    }

    private void drawResistorHorizontal(float x, float y) {
        pushMatrix();
        translate(x, y);

        rectMode(CENTER);
        fill(0);
        stroke(255);
        strokeWeight(2.5f);
        rect(0, 0, 75, 28, 4);

        popMatrix();
    }

    private void drawResistorVertical(float x, float y) {
        pushMatrix();
        translate(x, y);

        rectMode(CENTER);
        fill(0);
        stroke(255);
        strokeWeight(2.5f);
        rect(0, 0, 28, 75, 4);

        popMatrix();
    }

    private void drawWireWithCurrent(float x1, float y1, float x2, float y2, float currentVal, float b) {
        strokeCap(SQUARE);

        stroke(40); strokeWeight(12); line(x1, y1, x2, y2);
        stroke(100); strokeWeight(6); line(x1, y1, x2, y2);
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
                circle(px, py, 12 * absCurr + 2);

                fill(200, 200, 0, 240 * absCurr);
                circle(px, py, 4 * absCurr + 1);
            }
        }
    }

    private void drawMeshLoops(float m1Cx, float m2Cx, float cy, float b) {
        float r = 60f;

        drawLoopSpinner(m1Cx, cy, r*2, b);
        drawLoopSpinner(m2Cx, cy, r*2, b);
    }

    private void drawLoopSpinner(float cx, float cy, float r, float b) {
        pushMatrix();
        translate(cx, cy);

        stroke(255, 40);
        strokeWeight(2.5f);
        noFill();
        circle(0, 0, r * 2);

        float startAngle = (b * TWO_PI * 0.5f) % TWO_PI;
        float arcLen = PI * 1.3f;
        stroke(255, 230);
        strokeWeight(4f);
        strokeCap(ROUND);
        arc(0, 0, r * 2, r * 2, startAngle, startAngle + arcLen);

        float headAngle = startAngle + arcLen;
        float ax = r * cos(headAngle);
        float ay = r * sin(headAngle);

        pushMatrix();
        translate(ax, ay);
        rotate(headAngle + HALF_PI);
        fill(255);
        noStroke();
        triangle(0, 0, -5, -9, 5, -9);
        popMatrix();

        popMatrix();
    }

    private void drawNodeLaw(float nx, float ny, float b) {
        float pulse = map(sin(b * PI*4), -1, 1, 12, 28);

        fill(255, 60);
        noStroke();
        circle(nx, ny, pulse * 2f);

        drawVectorArrow(nx - 75f, ny, nx - 18f, ny);
            drawVectorArrow(nx + 18f, ny, nx + 75f, ny);
        drawVectorArrow(nx, ny + 18f, nx, ny + 75f);
    }

    private void drawVectorArrow(float x1, float y1, float x2, float y2) {
        stroke(255);
        strokeWeight(3.5f);
        line(x1, y1, x2, y2);

        float angle = atan2(y2 - y1, x2 - x1);
        pushMatrix();
        translate(x2, y2);
        rotate(angle);
        fill(255);
        noStroke();
        triangle(0, 0, -8, -5, -8, 5);
        popMatrix();
    }

    private void drawNode(float x, float y, boolean highlight) {
        if (highlight) {
            fill(255);
            stroke(255);
            strokeWeight(2f);
            circle(x, y, 16);
        } else {
            fill(255);
            noStroke();
            circle(x, y, 10);
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
        clogo = new Blogo(finalX, finalY, finalW, finalH);
        noCursor();
    }

    public static void main(String[] args) {
        PApplet.main("Classes.Cir2");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}