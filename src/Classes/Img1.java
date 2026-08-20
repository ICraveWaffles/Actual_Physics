package Classes;

import processing.core.PApplet;

public class Img1 extends PApplet {

    private final float BEAT_DURATION = 0.6f;
    private final float BEATS_PER_STAGE = 4f;
    private final float TOTAL_STAGES = 1f;
    private final float TOTAL_BEATS = BEATS_PER_STAGE * TOTAL_STAGES;

    private float globalTime = 0;
    private float startTimeSec = -1;

    public static Clogo alogo;
    float logoTransparency;
    float transY;

    @Override
    public void settings() {
        fullScreen();
        smooth(8);
    }

    @Override
    public void setup() {
        noCursor();
        startTimeSec = millis() * 0.001f;

        float finalX = (width / 2f) + 865.3f;
        float finalY = (height / 2f) - 458.1f;
        float finalW = (height / 2f) - 381.75f;
        float finalH = (height / 2f) - 381.75f;

        alogo = new Clogo(finalX, finalY, finalW, finalH);
    }

    @Override
    public void draw() {
        background(0);

        if (startTimeSec < 0) {
            startTimeSec = millis() * 0.001f;
        }

        float timeSec = (millis() * 0.001f) - startTimeSec;
        globalTime = timeSec % (BEAT_DURATION * TOTAL_BEATS);
        float currentBeat = globalTime / BEAT_DURATION;

        float b = timeSec * (100f / 60f);
        transY = b % 1f;
        logoTransparency = (float) (255 * (-Math.pow(transY, 2) + 2 * (transY)));

        drawOpticsBeatAnimation(currentBeat);

        if (alogo != null) {
            alogo.display(this, b, logoTransparency);
        }
    }

    private void drawOpticsBeatAnimation(float currentBeat) {
        pushStyle();
        strokeCap(ROUND);
        strokeJoin(ROUND);

        float xc = width * 0.5f;
        float yc = height * 0.5f;
        float F0 = 220f;

        float rawF = F0 * sin(PI * currentBeat);
        float f = (abs(rawF) < 4f) ? (rawF >= 0 ? 4f : -4f) : rawF;

        boolean isLens = currentBeat < 2.0f;

        stroke(255, 40);
        strokeWeight(1.5f);
        line(0, yc, width, yc);

        drawFocalMark(xc - f, yc);
        drawFocalMark(xc + f, yc);

        if (isLens) {
            drawLensMonochrome(xc, yc, f);
        } else {
            drawMirrorMonochrome(xc, yc, f);
        }

        float u1 = 2.0f * abs(f);
        float h1 = 120f;
        float x1 = xc - u1;
        float y1 = yc - h1;
        drawLightSource(x1, y1, yc, h1, true);

        float u2 = 0.5f * abs(f);
        float h2 = 55f;
        float x2 = xc - u2;
        float y2 = yc - h2;
        drawLightSource(x2, y2, yc, h2, false);

        if (isLens) {
            processLensRays(x1, y1, u1, h1, xc, yc, f);
            processLensRays(x2, y2, u2, h2, xc, yc, f);
        } else {
            processMirrorRays(x1, y1, u1, h1, xc, yc, f);
            processMirrorRays(x2, y2, u2, h2, xc, yc, f);
        }

        popStyle();
    }

    private void processLensRays(float xo, float yo, float u, float ho, float xc, float yc, float f) {
        if (abs(u - f) < 0.5f) return;

        float v = (f * u) / (u - f);
        float m = -v / u;
        float hi = m * ho;
        float xi = xc + v;
        float yi = yc - hi;

        drawImagePoint(xi, yi, v > 0);

        stroke(255, 190);
        strokeWeight(1.8f);
        line(xo, yo, xc, yo);
        float slope1 = (yc - yo) / f;
        line(xc, yo, width, yo + slope1 * (width - xc));
        if (v < 0) drawDashedLine(xc, yo, xi, yi, 5f);

        stroke(255, 130);
        strokeWeight(1.5f);
        float slope2 = (yc - yo) / (xc - xo);
        line(xo, yo, width, yo + slope2 * (width - xo));
        if (v < 0) drawDashedLine(xo, yo, xi, yi, 5f);
    }

    private void processMirrorRays(float xo, float yo, float u, float ho, float xc, float yc, float f) {
        if (abs(u - f) < 0.5f) return;

        float v = (f * u) / (u - f);
        float m = -v / u;
        float hi = m * ho;
        float xi = xc - v;
        float yi = yc - hi;

        drawImagePoint(xi, yi, v > 0);

        stroke(255, 190);
        strokeWeight(1.8f);
        line(xo, yo, xc, yo);
        float slope1 = (yc - yo) / (-f);
        line(xc, yo, 0, yo + slope1 * (-xc));
        if (v < 0) drawDashedLine(xc, yo, xi, yi, 5f);

        stroke(255, 130);
        strokeWeight(1.5f);
        line(xo, yo, xc, yc);
        float slope2 = -(yc - yo) / (xc - xo);
        line(xc, yc, 0, yc + slope2 * (-xc));
        if (v < 0) drawDashedLine(xc, yc, xi, yi, 5f);
    }

    private void drawLensMonochrome(float xc, float yc, float f) {
        float lensH = 320f;
        float lensW = constrain(abs(f) * 0.18f, 0f, 40f);

        stroke(255, 230);
        strokeWeight(2.5f);
        noFill();

        if (f > 0) {
            beginShape();
            vertex(xc, yc - lensH * 0.5f);
            quadraticVertex(xc + lensW, yc, xc, yc + lensH * 0.5f);
            quadraticVertex(xc - lensW, yc, xc, yc - lensH * 0.5f);
            endShape(CLOSE);
        } else {
            beginShape();
            vertex(xc - lensW, yc - lensH * 0.5f);
            quadraticVertex(xc, yc, xc - lensW, yc + lensH * 0.5f);
            vertex(xc + lensW, yc + lensH * 0.5f);
            quadraticVertex(xc, yc, xc + lensW, yc - lensH * 0.5f);
            endShape(CLOSE);
        }
    }

    private void drawMirrorMonochrome(float xc, float yc, float f) {
        float mirrorH = 320f;
        float curve = constrain(f * 0.20f, -45f, 45f);

        stroke(255, 240);
        strokeWeight(3f);
        noFill();

        beginShape();
        vertex(xc + curve, yc - mirrorH * 0.5f);
        quadraticVertex(xc - curve, yc, xc + curve, yc + mirrorH * 0.5f);
        endShape();

        stroke(255, 70);
        strokeWeight(1.2f);
        for (float y = yc - mirrorH * 0.5f; y <= yc + mirrorH * 0.5f; y += 14f) {
            float normY = (y - yc) / (mirrorH * 0.5f);
            float xBase = xc + curve * (1 - normY * normY);
            line(xBase, y, xBase + 8f, y - 5f);
        }
    }

    private void drawLightSource(float x, float y, float yc, float h, boolean isMain) {
        stroke(255, 240);
        strokeWeight(isMain ? 2.5f : 1.8f);
        line(x, yc, x, y);

        fill(255);
        noStroke();
        triangle(x, y, x - 4f, y + 8f, x + 4f, y + 8f);

        for (int r = 3; r > 0; r--) {
            fill(255, 30 * r);
            ellipse(x, y, r * 8f, r * 8f);
        }
    }

    private void drawImagePoint(float xi, float yi, boolean isReal) {
        if (abs(xi) > width * 2 || abs(yi) > height * 2) return;

        pushStyle();
        noStroke();
        if (isReal) {
            fill(255, 255);
            ellipse(xi, yi, 8f, 8f);
            fill(255, 80);
            ellipse(xi, yi, 18f, 18f);
        } else {
            fill(255, 160);
            ellipse(xi, yi, 6f, 6f);
            fill(255, 40);
            ellipse(xi, yi, 14f, 14f);
        }
        popStyle();
    }

    private void drawFocalMark(float x, float y) {
        noStroke();
        fill(255, 200);
        ellipse(x, y, 5f, 5f);
    }

    private void drawDashedLine(float x1, float y1, float x2, float y2, float dashLen) {
        pushStyle();
        stroke(255, 140);
        strokeWeight(1.5f);
        float d = dist(x1, y1, x2, y2);
        for (float i = 0; i < d; i += dashLen * 2) {
            float start = i / d;
            float end = min((i + dashLen) / d, 1.0f);
            line(lerp(x1, x2, start), lerp(y1, y2, start), lerp(x1, x2, end), lerp(y1, y2, end));
        }
        popStyle();
    }

    @Override
    public void mousePressed() {
        startTimeSec = millis() * 0.001f;
    }

    public static void main(String[] args) {
        PApplet.main("Classes.Img1");
    }
}