package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class Har3 extends PApplet {

    float t = 0;
    float w = 100f / 120f;
    PFont ntr;
    float logoTransparency;
    float transY;

    public static Clogo clogo;

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

        float startX = width * 0.10f;
        float endX = width * 0.90f;
        float waveLen = (endX - startX) / 2.0f;
        float k = TWO_PI / waveLen;
        float shmFreq = TWO_PI / 2.0f;
        float phase = shmFreq * b;

        float transY0 = height * 0.32f;
        float ampTrans = height * 0.12f;

        stroke(255, 60);
        strokeWeight(1);
        line(startX, transY0, endX, transY0);


        for (float x = startX; x <= endX; x += waveLen / 64f) {
            float y = transY0 - ampTrans * sin(k * (x - startX) - phase);
            fill(100, 220, 255);
            noStroke();
            circle(x, y, 6);
        }

        float trackX_T = startX + waveLen * 0.75f;
        float trackY_T = transY0 - ampTrans * sin(k * (trackX_T - startX) - phase);
        fill(255, 100, 100);
        circle(trackX_T, trackY_T, 12);

        float crestX = startX + waveLen * 0.25f;
        float troughX = startX + waveLen * 0.75f;

        drawTag(crestX, transY0 - ampTrans - 15, "");
        drawTag(troughX, transY0 + ampTrans + 25, "");

        drawDashedLine(crestX, transY0, crestX, transY0 - ampTrans);
        drawTag(crestX + 45, transY0 - ampTrans / 2f, "");

        drawDimensionLine(startX + waveLen * 0.25f, transY0 - ampTrans - 40, startX + waveLen * 1.25f, transY0 - ampTrans - 40, "");


        float longY0 = height * 0.68f;
        float ampLong = 38f;
        int rows = 7;
        float rowSpacing = 10f;

        stroke(255, 40);
        strokeWeight(1);
        line(startX, longY0, endX, longY0);

        for (float x0 = startX; x0 <= endX; x0 += 12f) {
            float disp = ampLong * sin(k * (x0 - startX) - phase);
            float actualX = x0 + disp;

            for (int r = 0; r < rows; r++) {
                float y = longY0 + (r - (rows - 1) / 2f) * rowSpacing;
                fill(255, 200, 100);
                noStroke();
                circle(actualX, y, 5);
            }
        }

        float trackX_L0 = startX + waveLen * 0.75f;
        float trackX_L = trackX_L0 + ampLong * sin(k * (trackX_L0 - startX) - phase);
        fill(255, 100, 100);
        circle(trackX_L, longY0, 12);

        float compPhaseX = startX + waveLen * 0.75f;
        float rarefPhaseX = startX + waveLen * 0.25f;

        drawTag(compPhaseX, longY0 - (rows * rowSpacing / 2f) - 15, "");
        drawTag(rarefPhaseX, longY0 - (rows * rowSpacing / 2f) - 15, "");

        drawDimensionLine(startX + waveLen * 0.25f, longY0 + (rows * rowSpacing / 2f) + 45, startX + waveLen * 1.25f, longY0 + (rows * rowSpacing / 2f) + 45, "");


        popStyle();
    }

    void drawTag(float x, float y, String label) {
        fill(220);
        textSize(13);
        textAlign(CENTER);
        text(label, x, y);
    }

    void drawDimensionLine(float x1, float y, float x2, float y2, String label) {
        stroke(255, 180);
        strokeWeight(1.2f);
        line(x1, y, x2, y);
        line(x1, y - 5, x1, y + 5);
        line(x2, y - 5, x2, y + 5);

        fill(255, 200);
        textSize(12);
        textAlign(CENTER);
        text(label, (x1 + x2) / 2f, y - 6);
    }

    void drawPropagationArrow(float x, float y, String label) {
        stroke(100, 255, 150);
        strokeWeight(2);
        line(x, y, x + 60, y);
        fill(100, 255, 150);
        triangle(x + 60, y - 4, x + 60, y + 4, x + 68, y);

        fill(100, 255, 150);
        textSize(12);
        textAlign(RIGHT);
        text(label, x - 10, y + 4);
    }

    void drawDashedLine(float x1, float y1, float x2, float y2) {
        stroke(255, 140);
        strokeWeight(1.2f);
        float d = dist(x1, y1, x2, y2);
        float dash = 4f, gap = 4f;
        for (float i = 0; i < d; i += dash + gap) {
            float sx = lerp(x1, x2, i / d);
            float sy = lerp(y1, y2, i / d);
            float ex = lerp(x1, x2, min((i + dash) / d, 1f));
            float ey = lerp(y1, y2, min((i + dash) / d, 1f));
            line(sx, sy, ex, ey);
        }
    }

    void drawDoubleArrowVert(float x, float y1, float y2, int col) {
        stroke(col);
        strokeWeight(1.5f);
        line(x, y1, x, y2);
        fill(col);
        triangle(x - 3, y1 + 5, x + 3, y1 + 5, x, y1);
        triangle(x - 3, y2 - 5, x + 3, y2 - 5, x, y2);
    }

    void drawDoubleArrowHoriz(float x1, float x2, float y, int col) {
        stroke(col);
        strokeWeight(1.5f);
        line(x1, y, x2, y);
        fill(col);
        triangle(x1 + 5, y - 3, x1 + 5, y + 3, x1, y);
        triangle(x2 - 5, y - 3, x2 - 5, y + 3, x2, y);
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

        clogo = new Clogo(finalX, finalY, finalW, finalH);

        noCursor();
    }

    public static void main(String[] args) {
        PApplet.main("Classes.Har3");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}