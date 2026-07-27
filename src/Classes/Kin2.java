package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class Kin2 extends PApplet {

    float t = 0;
    float w = 5/6f;
    PFont ntr;
    float logoTransparency;
    float transY;

    public static Alogo alogo;

    float maxT = 2.26415f * 2;
    float halfT = 2.26415f;

    public void draw() {

        pushStyle();

        frameRate = 30;
        t = frameCount / (float) frameRate;
        if (t > maxT) {
            frameCount = 0;
            t = 0;
        }

        background(0);

        float b = t * w;
        textFont(ntr);

        transY = b % 1f;
        logoTransparency = (float) (255 * (-Math.pow(transY, 2) + 2 * (transY)));

        alogo.display(this, b, logoTransparency);

        float totalWidth = width * 0.94f;
        float spacing = 160;
        float graphW = (totalWidth - (spacing * 2)) / 3f;
        float graphH = 280;

        float startX = (width - totalWidth) / 2f;
        float x1 = startX;
        float x2 = startX + graphW + spacing;
        float x3 = startX + (graphW + spacing) * 2;

        float targetY = height / 2f;

        float y1 = targetY;
        float y2 = targetY;
        float y3 = targetY;

        drawCurveBase(x1, y1, graphW, graphH, 1);
        drawCurveBase(x2, y2, graphW, graphH, 2);
        drawCurveBase(x3, y3, graphW, graphH, 3);

        boolean isFirstHalf = (t < halfT);
        float quarterNote = halfT / 4f;

        float arrowFixedY = height / 2f;

        if (isFirstHalf) {
            drawTangent(x1, y1, graphW, graphH, 1, t);
            drawTangent(x2, y2, graphW, graphH, 2, t);

            if (t >= quarterNote) {
                float arrowProgress = constrain((t - quarterNote) / quarterNote, 0, 1);
                drawHorizontalArrow(x1 + graphW + 20, x2 - 20, arrowFixedY, arrowProgress, true);
            }

            if (t >= quarterNote * 2) {
                float arrowProgress = constrain((t - quarterNote * 2) / quarterNote, 0, 1);
                drawHorizontalArrow(x2 + graphW + 20, x3 - 20, arrowFixedY, arrowProgress, true);
            }

        } else {
            float rawT = (t - halfT) / halfT;
            float speedUpFactor = constrain(rawT * 2.0f, 0, 1);
            float easedProgress = speedUpFactor * speedUpFactor * (3 - 2 * speedUpFactor);
            float localT = halfT * (1f - easedProgress);

            drawAreaRtoL(x3, y3, graphW, graphH, 3, localT);
            drawAreaRtoL(x2, y2, graphW, graphH, 2, localT);

            float localTimeElapsed = t - halfT;

            if (localTimeElapsed >= quarterNote) {
                float arrowProgress = constrain((localTimeElapsed - quarterNote) / quarterNote, 0, 1);
                drawHorizontalArrow(x3 - 20, x2 + graphW + 20, arrowFixedY, arrowProgress, false);
            }

            if (localTimeElapsed >= quarterNote * 2) {
                float arrowProgress = constrain((localTimeElapsed - quarterNote * 2) / quarterNote, 0, 1);
                drawHorizontalArrow(x2 - 20, x1 + graphW + 20, arrowFixedY, arrowProgress, false);
            }
        }

        popStyle();
    }

    float getX(float time) {
        return 200 * time + 400 * sin(2 * time);
    }

    float getV(float time) {
        return 200 + 800 * cos(2 * time);
    }

    float getA(float time) {
        return -1600 * sin(2 * time);
    }

    void drawCurveBase(float x0, float y0, float w, float h, int type) {
        stroke(100);
        strokeWeight(1);
        line(x0, y0, x0 + w, y0);
        line(x0, y0 - h / 2, x0, y0 + h / 2);

        stroke(255);
        strokeWeight(5);
        noFill();

        beginShape();
        for (float step = 0; step <= halfT; step += 0.02f) {
            float px = x0 + (step / halfT) * w;
            float val = getMappedVal(step, h, type);
            vertex(px, y0 + val);
        }
        endShape();
    }

    float getMappedVal(float time, float h, int type) {
        if (type == 1) return map(getX(time), 0, 1000, h / 2 - 20, -h / 2 + 20);
        if (type == 2) return map(getV(time), -700, 1000, h / 2 - 20, -h / 2 + 20);
        if (type == 3) return map(getA(time), -1700, 1700, h / 2 - 20, -h / 2 + 20);
        return 0;
    }

    void drawTangent(float x0, float y0, float w, float h, int type, float time) {
        float px = x0 + (time / halfT) * w;
        float py = y0 + getMappedVal(time, h, type);

        float deltaT = 0.005f;
        float t1 = max(0, time - deltaT);
        float t2 = min(halfT, time + deltaT);

        float px1 = x0 + (t1 / halfT) * w;
        float py1 = y0 + getMappedVal(t1, h, type);
        float px2 = x0 + (t2 / halfT) * w;
        float py2 = y0 + getMappedVal(t2, h, type);

        float angle = atan2(py2 - py1, px2 - px1);
        float len = 100f;

        stroke(255);
        strokeWeight(3f);
        line(px - len * cos(angle), py - len * sin(angle), px + len * cos(angle), py + len * sin(angle));

        fill(255);
        noStroke();
        circle(px, py, 10);
    }

    void drawAreaRtoL(float x0, float y0, float w, float h, int type, float cutOffT) {
        fill(255, 60);
        stroke(255, 180);
        strokeWeight(1);

        beginShape();
        float startPx = x0 + w;
        vertex(startPx, y0);

        for (float step = halfT; step >= cutOffT; step -= 0.02f) {
            float px = x0 + (step / halfT) * w;
            float val = getMappedVal(step, h, type);
            vertex(px, y0 + val);
        }

        float endPx = x0 + (cutOffT / halfT) * w;
        vertex(endPx, y0);
        endShape(CLOSE);
    }

    void drawHorizontalArrow(float startX, float endX, float fixedY, float progress, boolean toRight) {
        float currentX = lerp(startX, endX, progress);

        stroke(255);
        strokeWeight(4);
        line(startX, fixedY, currentX, fixedY);

        float arrowHeadW = 20;
        float arrowHeadH = 24;

        fill(255);
        noStroke();

        pushMatrix();
        translate(currentX, fixedY);
        if (toRight) {
            triangle(0, 0, -arrowHeadW, -arrowHeadH / 2f, -arrowHeadW, arrowHeadH / 2f);
        } else {
            triangle(0, 0, arrowHeadW, -arrowHeadH / 2f, arrowHeadW, arrowHeadH / 2f);
        }
        popMatrix();
    }

    public void settings() {
        fullScreen();
        frameRate = 30;
    }

    public void setup() {
        ntr = createFont("times.ttf", 50);

        float finalX = (width / 2f) + 865.3f;
        float finalY = (height / 2f) - 458.1f;
        float finalW = (height / 2f) - 381.75f;
        float finalH = (height / 2f) - 381.75f;

        alogo = new Alogo(finalX, finalY, finalW, finalH);

        noCursor();
    }

    public static void main(String[] args) {
        PApplet.main("Classes.Kin2");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}