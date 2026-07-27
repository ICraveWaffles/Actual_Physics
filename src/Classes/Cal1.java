package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class Cal1 extends PApplet {

    float t = 0;
    float w = 5/6f;
    PFont ntr;
    float logoTransparency;
    float transY;

    public static Blogo blogo;

    float maxBeats = 4f;

    float gx, gy, gw, gh;
    float potX, potY, potW, potH;

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
        blogo.display(this, b, logoTransparency);

        gw = width * 0.35f;
        gh = height * 0.50f;
        gx = width * 0.12f;
        gy = height * 0.50f - gh / 2f;

        potW = width * 0.28f;
        potH = height * 0.50f;
        potX = width * 0.60f;
        potY = height * 0.50f - potH / 2f;

        drawGraph(b);
        drawHeater(potX + potW / 2f, potY + potH + 25f, potW, b);
        drawPotAndContents(b);

        popStyle();
    }

    void drawHeater(float cx, float topY, float pWidth, float b) {
        stroke(255);
        strokeWeight(5);
        line(cx - pWidth * 0.45f, topY, cx + pWidth * 0.45f, topY);
        line(cx - pWidth * 0.25f, topY + 12, cx + pWidth * 0.25f, topY + 12);

        int numArrows = 8;
        float spacing = (pWidth * 0.7f) / (numArrows - 1);
        float startX = cx - pWidth * 0.35f;

        for (int i = 0; i < numArrows; i++) {
            float ax = startX + i * spacing;
            float animY = (b * 60f + i * 15f) % 30f;
            float ay = topY - 10f - animY;

            float alpha = map(animY, 0, 30, 255, 0); // Desvanecimiento al subir

            stroke(255, alpha);
            strokeWeight(3f);
            line(ax, ay, ax, ay - 20);
            line(ax, ay - 20, ax - 6, ay - 12);
            line(ax, ay - 20, ax + 6, ay - 12);
        }
    }

    void drawPotAndContents(float b) {
        float marginX = 25f;
        float marginY = 25f;
        float lineY = 0;
        boolean isWavy = false;

        if (b < 0.75f) {
            lineY = potY + potH - marginY;
        } else if (b <= 1.25f) {
            lineY = lerp(potY + potH - marginY, potY + marginY, (b - 0.75f) / 0.5f);
        } else if (b < 2.75f) {
            lineY = potY + marginY;
            isWavy = true;
        } else if (b <= 3.25f) {
            lineY = lerp(potY + marginY, potY + potH - marginY, (b - 2.75f) / 0.5f);
            isWavy = true;
        } else {
            lineY = potY + potH - marginY;
            isWavy = true;
        }

        int cols = 22;
        int rows = 16;
        float gridW = potW - 2 * marginX;
        float gridH = potH - 2 * marginY;

        rectMode(CENTER);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                float origX = potX + marginX + c * (gridW / (cols - 1));
                float origY = potY + potH - marginY - r * (gridH / (rows - 1));

                int phase = 0;

                if (b <= 1.25f) {
                    phase = (origY >= lineY) ? 1 : 0;
                } else if (b < 2.75f) {
                    phase = 1; // Todo líquido
                } else if (b <= 3.25f) {
                    phase = (origY <= lineY) ? 2 : 1;
                } else {
                    phase = 2; // Todo gas
                }

                if (phase == 0) {
                    float vib = 1.5f + b * 2f;
                    float dx = sin(b * 30f + c + r) * vib;
                    float dy = cos(b * 30f + c * 2) * vib;
                    fill(255);
                    noStroke();
                    rect(origX + dx, origY + dy, 11, 11);

                } else if (phase == 1) {
                    float waveX = sin(b * 8f + r * 0.5f + c * 0.5f) * 6f;
                    float waveY = cos(b * 10f + c * 0.8f) * 4f;
                    fill(255, 200);
                    noStroke();
                    ellipse(origX + waveX, origY + waveY, 13, 13);

                } else if (phase == 2) {
                    float b_pass = 2.75f + 0.5f * ((origY - (potY + marginY)) / gridH);
                    float timeSinceGas = max(0f, b - b_pass);
                    float rise = timeSinceGas * 220f;
                    float drift = sin(b * 6f + r + c) * (15f + timeSinceGas * 20f);

                    fill(255, max(0f, 160f - timeSinceGas * 60f));
                    noStroke();
                    ellipse(origX + drift, origY - rise, 10, 10);
                }
            }
        }

        stroke(255);
        strokeWeight(4.5f);
        if (!isWavy) {
            line(potX + 6, lineY, potX + potW - 6, lineY);
        } else {
            noFill();
            beginShape();
            for (float x = potX + 6; x <= potX + potW - 6; x += 6f) {
                float yOffset = sin((x - potX) * 0.08f + b * TWO_PI * 4f) * 7f;
                vertex(x, lineY + yOffset);
            }
            endShape();
        }

        stroke(255);
        strokeWeight(5f);
        noFill();
        beginShape();
        vertex(potX, potY);
        vertex(potX, potY + potH);
        vertex(potX + potW, potY + potH);
        vertex(potX + potW, potY);
        endShape();

        line(potX - 30, potY + 45, potX, potY + 45);
        line(potX + potW, potY + 45, potX + potW + 30, potY + 45);
    }

    void drawGraph(float b) {
        stroke(255, 180);
        strokeWeight(3f);
        line(gx, gy + gh, gx + gw, gy + gh);
        line(gx, gy, gx, gy + gh);

        line(gx + gw, gy + gh, gx + gw - 12, gy + gh - 6);
        line(gx + gw, gy + gh, gx + gw - 12, gy + gh + 6);
        line(gx, gy, gx - 6, gy + 12);
        line(gx, gy, gx + 6, gy + 12);

        float t0 = 0f, t1 = 0.75f, t2 = 1.25f, t3 = 2.75f, t4 = 3.25f, t5 = 4.0f;

        float tempMinY  = gy + gh * 0.85f;
        float tempMeltY = gy + gh * 0.55f;
        float tempBoilY = gy + gh * 0.20f;
        float tempMaxY  = gy + gh * 0.05f;

        stroke(255, 60);
        strokeWeight(2f);
        drawDashedHorizontal(gx, gx + gw, tempMeltY);
        drawDashedHorizontal(gx, gx + gw, tempBoilY);

        stroke(255, 80);
        strokeWeight(2.5f);
        noFill();
        beginShape();
        vertex(gx + (t0/4f)*gw, tempMinY);
        vertex(gx + (t1/4f)*gw, tempMeltY);
        vertex(gx + (t2/4f)*gw, tempMeltY);
        vertex(gx + (t3/4f)*gw, tempBoilY);
        vertex(gx + (t4/4f)*gw, tempBoilY);
        vertex(gx + (t5/4f)*gw, tempMaxY);
        endShape();

        stroke(255);
        strokeWeight(4.5f);
        beginShape();
        addGraphVertex((t0/4f)*gw, (t1/4f)*gw, tempMinY, tempMeltY, b);
        addGraphVertex((t1/4f)*gw, (t2/4f)*gw, tempMeltY, tempMeltY, b);
        addGraphVertex((t2/4f)*gw, (t3/4f)*gw, tempMeltY, tempBoilY, b);
        addGraphVertex((t3/4f)*gw, (t4/4f)*gw, tempBoilY, tempBoilY, b);
        addGraphVertex((t4/4f)*gw, (t5/4f)*gw, tempBoilY, tempMaxY, b);
        endShape();

        float currentX = gx + (b / 4f) * gw;
        float currentY = getTempY(b, t0, t1, t2, t3, t4, t5, tempMinY, tempMeltY, tempBoilY, tempMaxY);

        fill(255);
        noStroke();
        ellipse(currentX, currentY, 14, 14);
    }

    void drawDashedHorizontal(float x1, float x2, float y) {
        float dash = 10f;
        for (float x = x1; x < x2; x += dash * 2) {
            line(x, y, min(x + dash, x2), y);
        }
    }

    void addGraphVertex(float xStartRel, float xEndRel, float yStart, float yEnd, float b) {
        float bStart = (xStartRel / gw) * 4f;
        float bEnd = (xEndRel / gw) * 4f;

        if (b >= bStart) {
            vertex(gx + xStartRel, yStart);
            if (b >= bEnd) {
                vertex(gx + xEndRel, yEnd);
            } else {
                float p = (b - bStart) / (bEnd - bStart);
                vertex(gx + lerp(xStartRel, xEndRel, p), lerp(yStart, yEnd, p));
            }
        }
    }

    float getTempY(float b, float t0, float t1, float t2, float t3, float t4, float t5,
                   float yMin, float yMelt, float yBoil, float yMax) {
        if (b < t1) {
            return lerp(yMin, yMelt, b / t1);
        } else if (b < t2) {
            return yMelt;
        } else if (b < t3) {
            return lerp(yMelt, yBoil, (b - t2) / (t3 - t2));
        } else if (b < t4) {
            return yBoil;
        } else {
            return lerp(yBoil, yMax, (b - t4) / (t5 - t4));
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

        blogo = new Blogo(finalX, finalY, finalW, finalH);

        noCursor();
    }

    public static void main(String[] args) {
        PApplet.main("Classes.Cal1");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}