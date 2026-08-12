package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class Mag2 extends PApplet {

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

        drawScene(b, cx, cy);

        if (clogo != null) {
            clogo.display(this, b, logoTransparency);
        }

        popStyle();
    }

    private void drawScene(float b, float cx, float cy) {
        float wireDist = 320f;
        float xLeft = cx - wireDist;
        float xRight = cx + wireDist;

        float topCutY = height * 0.14f;
        float bottomCutY = height * 0.86f;

        float phaseL = b;
        float dirL = 1.0f;

        float phaseR;
        float dirR;
        float intensityR;

        if (b < 1.0f) {
            phaseR = (2.0f / PI) * sin(HALF_PI * b);
            dirR = cos(HALF_PI * b);
            intensityR = abs(dirR);
        } else if (b < 2.0f) {
            float p = b - 1.0f;
            phaseR = (2.0f / PI) - p;
            dirR = -1.0f;
            intensityR = 1.0f;
        } else {
            float val = cos(PI * (b - 2.0f));
            dirR = -val;
            intensityR = abs(val);
            phaseR = (2.0f / PI) - 1.0f - (1.0f / PI) * sin(PI * (b - 2.0f));
        }

        float fieldRotationAngle = b * TWO_PI;

        drawMagneticField(xLeft, cy, dirL, fieldRotationAngle, 1.0f);
        drawMagneticField(xRight, cy, dirR, fieldRotationAngle, intensityR);

        drawWireWithCurrent(xLeft, topCutY, bottomCutY, phaseL, 1.0f);
        drawWireWithCurrent(xRight, topCutY, bottomCutY, phaseR, intensityR);

        if (b >= 2.0f) {
            float forceMag = 110f * cos(PI * (b - 2.0f));
            drawSchematicForce(xLeft, cy, -forceMag);
            drawSchematicForce(xRight, cy, forceMag);
        }

        drawEnclosures(topCutY, bottomCutY);
    }

    private void drawWireWithCurrent(float x, float topY, float bottomY, float phase, float intensityFactor) {
        strokeCap(ROUND);

        stroke(50);
        strokeWeight(18);
        line(x, topY - 20, x, bottomY + 20);

        stroke(130);
        strokeWeight(12);
        line(x, topY - 20, x, bottomY + 20);

        stroke(220);
        strokeWeight(4);
        line(x, topY - 20, x, bottomY + 20);

        float speed = 320f;
        float pulseSpacing = 65f;
        float offset = (phase * speed) % pulseSpacing;

        for (float y = topY - pulseSpacing; y <= bottomY + pulseSpacing; y += 4f) {
            float distFromTop = y - topY;
            float posInPattern = (distFromTop + offset) % pulseSpacing;
            if (posInPattern < 0) posInPattern += pulseSpacing;

            float intensity = map(cos(TWO_PI * posInPattern / pulseSpacing), -1, 1, 0, 1);
            intensity = pow(intensity, 2.5f);

            if (intensity > 0.05f) {
                stroke(255, 230 * intensity * intensityFactor);
                strokeWeight(10 * intensity + 2);
                line(x, y, x, y + 3f);

                stroke(255, 255 * intensity * intensityFactor);
                strokeWeight(4 * intensity + 1);
                line(x, y, x, y + 3f);
            }
        }
    }

    private void drawMagneticField(float x, float cy, float dirFactor, float rotationAngle, float intensityFactor) {
        float alpha = 255f * abs(dirFactor) * intensityFactor;
        if (alpha < 5f) return;

        boolean ccw = dirFactor > 0;

        float[] yLevels = {cy - 160f, cy, cy + 160f};
        float rx = 160f;
        float ry = 50f;

        for (float yL : yLevels) {
            stroke(255, alpha * 0.75f);
            strokeWeight(2.0f);
            noFill();

            drawDashedEllipse(x, yL, rx, ry, 28);

            drawRotatingFieldArrow(x, yL, rx, ry, rotationAngle, ccw, alpha);
            drawRotatingFieldArrow(x, yL, rx, ry, rotationAngle + PI, ccw, alpha);
        }
    }

    private void drawDashedEllipse(float cx, float cy, float rx, float ry, int numSegments) {
        float angleStep = TWO_PI / numSegments;
        for (int i = 0; i < numSegments; i += 2) {
            float a1 = i * angleStep;
            float a2 = (i + 1) * angleStep;

            beginShape();
            for (float a = a1; a <= a2; a += 0.05f) {
                vertex(cx + rx * cos(a), cy + ry * sin(a));
            }
            endShape();
        }
    }

    private void drawRotatingFieldArrow(float cx, float cy, float rx, float ry, float angle, boolean ccw, float alpha) {
        float px = cx + rx * cos(angle);
        float py = cy + ry * sin(angle);

        float dx = -rx * sin(angle);
        float dy = ry * cos(angle);

        if (!ccw) {
            dx = -dx;
            dy = -dy;
        }

        float heading = atan2(dy, dx);

        pushMatrix();
        translate(px, py);
        rotate(heading);

        fill(255, alpha);
        noStroke();
        float head = 12f;
        beginShape();
        vertex(0, 0);
        vertex(-head, -head * 0.45f);
        vertex(-head * 0.7f, 0);
        vertex(-head, head * 0.45f);
        endShape(CLOSE);

        popMatrix();
    }

    private void drawSchematicForce(float x, float y, float forceX) {
        if (abs(forceX) < 3f) return;

        float startX = x;
        float endX = x + forceX;

        stroke(255);
        strokeWeight(3.5f);
        line(startX, y, endX, y);

        float dir = forceX > 0 ? 1f : -1f;
        float headSize = 14f;

        fill(255);
        noStroke();
        beginShape();
        vertex(endX, y);
        vertex(endX - dir * headSize, y - headSize * 0.45f);
        vertex(endX - dir * headSize, y + headSize * 0.45f);
        endShape(CLOSE);
    }

    private void drawEnclosures(float topY, float bottomY) {
        rectMode(CORNER);
        fill(0);
        stroke(255, 180);
        strokeWeight(2.5f);

        rect(-10, -10, width + 20, topY + 10, 0, 0, 16, 16);
        rect(-10, bottomY, width + 20, height - bottomY + 10, 16, 16, 0, 0);
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
        PApplet.main("Classes.Mag2");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}