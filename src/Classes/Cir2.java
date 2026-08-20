package Classes;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;

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
        pushStyle(); // Protege el estilo global
        drawScene(b, cx, cy);
        popStyle();
        popMatrix();

        if (clogo != null) {
            pushMatrix();
            pushStyle(); // Protege el logo del grosor de trazo de la escena
            clogo.display(this, b, logoTransparency);
            popStyle();
            popMatrix();
        }

        popStyle();
    }

    private void drawScene(float b, float cx, float cy) {
        float x1 = width * 0.22f;
        float x2 = width * 0.50f;
        float x3 = width * 0.78f;

        float midX12 = (x1 + x2) / 2f;
        float midX23 = (x2 + x3) / 2f;

        float topY = cy - 180f;
        float botY = cy + 180f;

        float i1 = 1.0f;
        float i3 = 0.6f;
        float i2 = 0.75f; // Mayor visibilidad de corriente en la segunda malla

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

        boolean isNodePhase = (b >= 2.0f);
        drawNode(x2, topY, isNodePhase);
        drawNode(x2, botY, isNodePhase); // Ambos nodos resaltados
        drawNode(x1, topY, false);
        drawNode(x1, botY, false);
        drawNode(x3, topY, false);
        drawNode(x3, botY, false);

        if (b < 2.0f) {
            drawMeshLoops(midX12, midX23, cy, b);
        } else {
            drawNodeLawTop(x2, topY, b);
            drawNodeLawBottom(x2, botY, b);
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

                fill(255, 130 * absCurr);
                circle(px, py, 12 * absCurr + 2);

                fill(255, 240 * absCurr);
                circle(px, py, 4 * absCurr + 1);
            }
        }
    }

    private void drawMeshLoops(float m1Cx, float m2Cx, float cy, float b) {
        float loopW = 140f;
        float loopH = 140f;

        // Malla izquierda y derecha con recorrido cuadrado en sentido positivo (horario)
        drawSquareLoop(m1Cx, cy, loopW, loopH, b, true);
        drawSquareLoop(m2Cx, cy, loopW, loopH, b, true);
    }

    private void drawSquareLoop(float cx, float cy, float w, float h, float b, boolean clockwise) {
        pushMatrix();
        translate(cx, cy);

        rectMode(CENTER);
        stroke(255, 40);
        strokeWeight(2.5f);
        noFill();
        rect(0, 0, w, h, 12);

        float totalL = 2 * (w + h);
        float activeL = totalL * 0.7f;
        float speed = b * totalL * 0.35f;

        float headD = speed;

        int numSamples = 40;
        stroke(255, 230);
        strokeWeight(4f);
        strokeCap(ROUND);
        strokeJoin(ROUND);
        noFill();

        beginShape();
        for (int i = 0; i <= numSamples; i++) {
            float progress = (float) i / numSamples;
            float d = headD - activeL + progress * activeL;

            PVector pt = getPointOnRect(d, w, h, clockwise);
            vertex(pt.x, pt.y);
        }
        endShape();

        PVector headPt = getPointOnRect(headD, w, h, clockwise);
        float angle = getAngleOnRect(headD, w, h, clockwise);

        pushMatrix();
        translate(headPt.x, headPt.y);
        rotate(angle);
        fill(255);
        noStroke();
        triangle(0, 0, -10, -6, -10, 6);
        popMatrix();

        popMatrix();
    }

    private PVector getPointOnRect(float d, float w, float h, boolean clockwise) {
        float hw = w / 2f;
        float hh = h / 2f;
        float totalL = 2 * (w + h);

        d = (d % totalL + totalL) % totalL;
        if (!clockwise) {
            d = (totalL - d) % totalL;
        }

        if (d < w) {
            return new PVector(-hw + d, -hh);
        } else if (d < w + h) {
            return new PVector(hw, -hh + (d - w));
        } else if (d < 2 * w + h) {
            return new PVector(hw - (d - (w + h)), hh);
        } else {
            return new PVector(-hw, hh - (d - (2 * w + h)));
        }
    }

    private float getAngleOnRect(float d, float w, float h, boolean clockwise) {
        float totalL = 2 * (w + h);

        d = (d % totalL + totalL) % totalL;
        if (!clockwise) {
            d = (totalL - d) % totalL;
        }

        float baseAngle;
        if (d < w) {
            baseAngle = 0; // Hacia la derecha
        } else if (d < w + h) {
            baseAngle = HALF_PI; // Hacia abajo
        } else if (d < 2 * w + h) {
            baseAngle = PI; // Hacia la izquierda
        } else {
            baseAngle = 3 * HALF_PI; // Hacia arriba
        }

        return clockwise ? baseAngle : baseAngle + PI;
    }

    private void drawNodeLawTop(float nx, float ny, float b) {
        float pulse = map(sin(b * PI * 4), -1, 1, 12, 28);

        fill(255, 60);
        noStroke();
        circle(nx, ny, pulse * 2f);

        drawVectorArrow(nx - 75f, ny, nx - 18f, ny);
        drawVectorArrow(nx + 18f, ny, nx + 75f, ny);
        drawVectorArrow(nx, ny + 18f, nx, ny + 75f);
    }

    private void drawNodeLawBottom(float nx, float ny, float b) {
        float pulse = map(sin(b * PI * 4), -1, 1, 12, 28);

        fill(255, 60);
        noStroke();
        circle(nx, ny, pulse * 2f);

        drawVectorArrow(nx + 75f, ny, nx + 18f, ny);
        drawVectorArrow(nx, ny + 75f, nx, ny + 18f);
        drawVectorArrow(nx - 18f, ny, nx - 75f, ny);
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
        rectMode(CENTER);
        rect(-4, 0, 9, 9);
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