package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class Mag1 extends PApplet {

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

        if (clogo != null) {
            clogo.display(this, b, logoTransparency);
        }

        float cx = width * 0.5f;
        float cy = height * 0.5f;

        if (b < 2.0f) {
            drawParte1(b, cx, cy);
        } else {
            drawParte2(b, cx, cy);
        }

        popStyle();
    }

    private void drawParte1(float b, float cx, float cy) {
        float povX = width * 0.24f;
        float povY = cy;
        float povSize = 280f;

        float rotAngle = b * PI - HALF_PI;

        float dirX = sin(rotAngle);
        float dirZ = cos(rotAngle);

        stroke(255, 180);
        strokeWeight(2.5f);
        noFill();
        rectMode(CENTER);
        rect(povX, povY, povSize, povSize, 20);

        boolean pointingToViewer = (dirZ < 0);

        pushMatrix();
        translate(povX, povY);
        stroke(255);
        strokeWeight(4.0f);
        noFill();

        if (pointingToViewer) {
            circle(0, 0, 110);
            fill(255);
            circle(0, 0, 24);
        } else {
            circle(0, 0, 110);
            float r = 40f;
            line(-r, -r, r, r);
            line(-r, r, r, -r);
        }
        popMatrix();

        float arrowCenterX = cx + 80f;
        float eyeX = width * 0.88f;
        float eyeY = cy;

        pushMatrix();
        translate(eyeX, eyeY);
        scale(1.6f);
        stroke(255);
        strokeWeight(2.5f);
        noFill();

        beginShape();
        vertex(30, -30);
        bezierVertex(5, -25, -25, -12, -30, 0);
        bezierVertex(-25, 12, 5, 25, 30, 30);
        endShape();

        noFill();
        stroke(255);
        arc(-26, 0, 20, 36, -HALF_PI, HALF_PI);

        fill(255);
        arc(-26, 0, 12, 24, -HALF_PI, HALF_PI);
        popMatrix();

        float arrowLen = 520f;
        float tipX3D = dirX * arrowLen * 0.5f;
        float tipZ3D = dirZ * arrowLen * 0.5f;
        float tailX3D = -tipX3D;
        float tailZ3D = -tipZ3D;

        float scaleTip = map(tipZ3D, -arrowLen * 0.5f, arrowLen * 0.5f, 0.5f, 1.5f);
        float scaleTail = map(tailZ3D, -arrowLen * 0.5f, arrowLen * 0.5f, 0.5f, 1.5f);

        float projTipX = arrowCenterX + tipX3D;
        float projTailX = arrowCenterX + tailX3D;

        float headLen = 80f * scaleTip;

        stroke(255);
        strokeWeight(10f * ((scaleTip + scaleTail) * 0.5f));
        strokeCap(ROUND);
        line(projTailX, cy, projTipX - dirX * headLen * 0.5f, cy);

        pushMatrix();
        translate(projTailX, cy);
        stroke(255);
        strokeWeight(7f * scaleTail);
        float fLen = 45f * scaleTail;
        line(0, 0, -dirX * fLen, -fLen * 0.85f);
        line(0, 0, -dirX * fLen, fLen * 0.85f);
        line(-dirX * fLen * 0.4f, 0, -dirX * fLen * 1.4f, -fLen * 0.85f);
        line(-dirX * fLen * 0.4f, 0, -dirX * fLen * 1.4f, fLen * 0.85f);
        popMatrix();

        pushMatrix();
        translate(projTipX, cy);
        fill(255);
        noStroke();
        float headWidth = 38f * scaleTip;
        beginShape();
        vertex(0, 0);
        vertex(-dirX * headLen, -headWidth);
        vertex(-dirX * headLen * 0.55f, 0);
        vertex(-dirX * headLen, headWidth);
        endShape(CLOSE);
        popMatrix();
    }

    private void drawParte2(float b, float cx, float cy) {
        float fieldCx = width * 0.70f;
        float fieldCy = cy;

        stroke(255, 150);
        strokeWeight(2.0f);

        float stepX = 85f;
        float stepY = 80f;
        float startX = fieldCx - 360f;
        float endX = fieldCx + 360f;
        float startY = cy - 360f;
        float endY = cy + 360f;

        for (float gx = startX; gx <= endX; gx += stepX) {
            for (float gy = startY; gy <= endY; gy += stepY) {
                pushMatrix();
                translate(gx, gy);
                stroke(255, 150);
                strokeWeight(2.0f);
                noFill();
                circle(0, 0, 22);

                float r = 7f;
                line(-r, -r, r, r);
                line(-r, r, r, -r);
                popMatrix();
            }
        }

        float progress = (b - 2.0f) / 2.0f;
        float phase = progress * TWO_PI;

        float rProton = 300f;
        float px = fieldCx + rProton * cos(phase);
        float py = fieldCy + rProton * sin(phase);

        stroke(255, 180);
        strokeWeight(2.2f);
        drawDashedCircle(fieldCx, fieldCy, rProton, 56);

        float pSize = 52f;
        stroke(255);
        fill(0);
        strokeWeight(3.0f);
        circle(px, py, pSize);
        drawSign(px, py, 1.0f, 255, pSize);

        drawZoomCallout(cx, cy, px, py, phase);
    }

    private void drawZoomCallout(float cx, float cy, float px, float py, float phase) {
        float zoomCx = width * 0.28f;
        float zoomCy = cy;
        float zoomR = 20f;

        fill(0);
        stroke(255);
        strokeWeight(3.5f);
        circle(zoomCx, zoomCy, zoomR * 2f);

        stroke(255, 130);
        strokeWeight(2.0f);

        float zPSize = 65f;
        stroke(255);
        fill(0);
        strokeWeight(3.5f);
        drawSign(zoomCx, zoomCy, 1.0f, 255, zPSize);

        float vAngle = phase + HALF_PI;
        drawVelocityVector(zoomCx, zoomCy, vAngle, 320f);
        float vx = zoomCx + cos(vAngle) * 365f;
        float vy = zoomCy + sin(vAngle) * 365f;
        textSize(36);
        fill(255);
        textAlign(CENTER, CENTER);
        text("v", vx, vy);

        float fAngle = phase + PI;
        drawForceVector(zoomCx, zoomCy, fAngle, 320f);
        float fx = zoomCx + cos(fAngle) * 365f;
        float fy = zoomCy + sin(fAngle) * 365f;
        textSize(36);
        fill(255);
        textAlign(CENTER, CENTER);
        text("F", fx, fy);
    }

    private void drawDashedCircle(float cx, float cy, float radius, int numSegments) {
        noFill();
        float angleStep = TWO_PI / numSegments;
        for (int i = 0; i < numSegments; i += 2) {
            float a1 = i * angleStep;
            float a2 = (i + 1) * angleStep;
            arc(cx, cy, radius * 2, radius * 2, a1, a2);
        }
    }

    private void drawVelocityVector(float x, float y, float angle, float arrowLen) {
        pushMatrix();
        translate(x, y);
        rotate(angle);

        stroke(255);
        strokeWeight(4.0f);
        line(0, 0, arrowLen, 0);

        float head = 18f;
        line(arrowLen, 0, arrowLen - head, -head * 0.45f);
        line(arrowLen, 0, arrowLen - head, head * 0.45f);

        popMatrix();
    }

    private void drawForceVector(float x, float y, float angle, float arrowLen) {
        pushMatrix();
        translate(x, y);
        rotate(angle);

        stroke(255);
        strokeWeight(4.0f);
        line(0, 0, arrowLen, 0);

        float head = 18f;
        fill(255);
        beginShape();
        vertex(arrowLen, 0);
        vertex(arrowLen - head, -head * 0.45f);
        vertex(arrowLen - head, head * 0.45f);
        endShape(CLOSE);

        popMatrix();
    }

    private void drawSign(float x, float y, float state, float alpha, float size) {
        pushMatrix();
        translate(x, y);
        stroke(255, alpha);
        strokeWeight(size * 0.08f);
        strokeCap(ROUND);
        float l = size * 0.22f;
        line(-l, 0, l, 0);
        if (state > 0.01f) {
            pushMatrix();
            rotate(state * HALF_PI);
            line(-l * state, 0, l * state, 0);
            popMatrix();
        }
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
        PApplet.main("Classes.Mag1");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}