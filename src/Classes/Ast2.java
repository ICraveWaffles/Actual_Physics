package Classes;

import processing.core.PApplet;

public class Ast2 extends PApplet {

    private final float BEAT_DURATION = 0.6f;
    private final float BEATS_PER_STAGE = 4f;
    private final float TOTAL_STAGES = 1f;
    private final float TOTAL_BEATS = BEATS_PER_STAGE * TOTAL_STAGES;
    private final float CYCLE_TIME = TOTAL_BEATS * BEAT_DURATION;

    private float globalTime = 0;
    private float startTimeSec = -1;

    public static Elogo elogo;
    float logoTransparency;
    float transY;

    private final float[][] hrStarCatalog = {
            {-0.42f, -0.40f}, {-0.38f, -0.35f}, {-0.32f, -0.28f}, {-0.25f, -0.20f},
            {-0.18f, -0.10f}, {-0.10f,  0.02f}, { 0.00f,  0.12f}, { 0.12f,  0.22f},
            { 0.22f,  0.30f}, { 0.32f,  0.38f}, { 0.40f,  0.42f},
            { 0.15f, -0.18f}, { 0.25f, -0.22f}, { 0.35f, -0.25f}, { 0.30f, -0.15f},
            {-0.20f, -0.44f}, { 0.00f, -0.42f}, { 0.20f, -0.40f},
            {-0.38f,  0.38f}, {-0.32f,  0.42f}, {-0.25f,  0.35f}
    };

    @Override
    public void settings() {
        fullScreen();
        smooth(8);
    }

    @Override
    public void setup() {
        noCursor();
        startTimeSec = millis() * 0.001f;

        // Manteniendo exactamente las dimensiones y posicionamiento original del Elogo
        float finalX = (width / 2f) + 865.3f;
        float finalY = (height / 2f) - 458.1f;
        float finalW = (height / 2f) - 381.75f;
        float finalH = (height / 2f) - 381.75f;

        elogo = new Elogo(finalX, finalY, finalW, finalH);
    }

    @Override
    public void draw() {
        background(0);

        if (startTimeSec < 0) {
            startTimeSec = millis() * 0.001f;
        }

        float timeSec = (millis() * 0.001f) - startTimeSec;
        globalTime = timeSec % CYCLE_TIME;
        float currentBeat = globalTime / BEAT_DURATION;

        float b = timeSec * (100f / 60f);
        transY = b % 1f;
        logoTransparency = (float) (255 * (-Math.pow(transY, 2) + 2 * (transY)));

        drawAstrophysics2Sequence(globalTime, currentBeat);

        if (elogo != null) {
            elogo.display(this, b, logoTransparency);
        }
    }

    private void drawAstrophysics2Sequence(float t, float beat) {
        pushStyle();
        strokeCap(ROUND);
        strokeJoin(ROUND);

        float rawProg = constrain(map(beat, 0.2f, 3.6f, 0f, 1f), 0f, 1f);
        float prog = rawProg * rawProg * (3f - 2f * rawProg);

        // Ubicación exacta de la estrella e interfaz
        float starX = width * 0.27f;
        float starY = height * 0.5f;
        drawEvolvingStar(starX, starY, prog, t);

        float paneX = width * 0.68f;
        float paneWidth = width * 0.32f;
        float paneHeight = height * 0.275f;

        float hrY = height * 0.28f;
        float wienY = height * 0.70f;

        drawHRDiagram(paneX, hrY, paneWidth, paneHeight, prog, t);
        drawWienDiagram(paneX, wienY, paneWidth, paneHeight, prog, t);

        popStyle();
    }

    private void drawEvolvingStar(float cx, float cy, float prog, float t) {
        pushMatrix();
        translate(cx, cy);

        float radius = lerp(45f, 185f, prog);
        float coronaRays = lerp(16f, 64f, prog);
        float maxRayLen = lerp(25f, 110f, prog);

        stroke(255, lerp(120f, 240f, prog));
        for (int i = 0; i < (int) coronaRays; i++) {
            float angle = i * (TWO_PI / coronaRays) + sin(t * 3f + i) * 0.05f;
            float rayLen = maxRayLen * (0.55f + 0.45f * sin(t * 8f + i * 2.3f));

            float x1 = radius * cos(angle);
            float y1 = radius * sin(angle);
            float x2 = (radius + rayLen) * cos(angle);
            float y2 = (radius + rayLen) * sin(angle);

            strokeWeight(lerp(1.5f, 3.5f, prog));
            line(x1, y1, x2, y2);
        }

        noFill();
        float numRings = lerp(3f, 8f, prog);
        for (int r = 1; r <= (int) numRings; r++) {
            stroke(255, map(r, 1, numRings, 190f, 15f));
            strokeWeight(1.8f);
            float rSize = (radius + r * lerp(10f, 22f, prog)) * 2f;
            ellipse(0, 0, rSize, rSize);
        }

        fill(0);
        stroke(255);
        strokeWeight(lerp(3f, 5f, prog));
        ellipse(0, 0, radius * 2f, radius * 2f);

        stroke(255, lerp(130f, 255f, prog));
        strokeWeight(2f);
        int surfaceLines = (int) lerp(6f, 24f, prog);
        for (int i = 0; i < surfaceLines; i++) {
            float a = i * (TWO_PI / surfaceLines) + t * lerp(0.5f, 2.5f, prog);
            float rx = (radius * 0.65f) * cos(a);
            float ry = (radius * 0.65f) * sin(a);
            point(rx, ry);
        }

        popMatrix();
    }

    private void drawHRDiagram(float zx, float zy, float w, float h, float prog, float t) {
        pushMatrix();
        translate(zx, zy);

        stroke(255);
        strokeWeight(2.5f);
        noFill();
        rectMode(CENTER);
        rect(0, 0, w, h, 10f);

        float padX = 20f;
        float padY = 18f;
        float plotW = w - padX * 2f;
        float plotH = h - padY * 2f;

        stroke(255, 40);
        strokeWeight(1f);
        for (float gx = -plotW * 0.4f; gx <= plotW * 0.4f; gx += plotW * 0.15f) {
            line(gx, -plotH * 0.45f, gx, plotH * 0.45f);
        }
        for (float gy = -plotH * 0.4f; gy <= plotH * 0.4f; gy += plotH * 0.2f) {
            line(-plotW * 0.45f, gy, plotW * 0.45f, gy);
        }

        stroke(255, 180);
        strokeWeight(1.8f);
        line(-plotW * 0.48f, plotH * 0.46f, plotW * 0.48f, plotH * 0.46f);
        line(-plotW * 0.48f, plotH * 0.46f, -plotW * 0.48f, -plotH * 0.46f);

        stroke(255, 70);
        strokeWeight(1.2f);
        ellipse(0f, -plotH * 0.38f, plotW * 0.6f, plotH * 0.12f);
        ellipse(plotW * 0.25f, -plotH * 0.2f, plotW * 0.35f, plotH * 0.2f);
        ellipse(-plotW * 0.3f, plotH * 0.32f, plotW * 0.28f, plotH * 0.18f);

        stroke(255, 30);
        strokeWeight(plotH * 0.12f);
        noFill();
        beginShape();
        for (int i = 0; i <= 20; i++) {
            float step = (float) i / 20f;
            float px = lerp(-plotW * 0.45f, plotW * 0.45f, step);
            float py = lerp(-plotH * 0.42f, plotH * 0.42f, Math.min(1f, step * 1.1f));
            vertex(px, py);
        }
        endShape();

        stroke(255, 160);
        strokeWeight(2f);
        beginShape();
        for (int i = 0; i <= 20; i++) {
            float step = (float) i / 20f;
            float px = lerp(-plotW * 0.45f, plotW * 0.45f, step);
            float py = lerp(-plotH * 0.42f, plotH * 0.42f, Math.min(1f, step * 1.1f));
            vertex(px, py);
        }
        endShape();

        noStroke();
        for (float[] star : hrStarCatalog) {
            float sx = star[0] * plotW;
            float sy = star[1] * plotH;
            fill(255, 200);
            ellipse(sx, sy, 3.5f, 3.5f);
        }

        float starHR_X = lerp(plotW * 0.45f, -plotW * 0.45f, prog);
        float starHR_Y = lerp(plotH * 0.42f, -plotH * 0.42f, prog);

        stroke(255);
        strokeWeight(2.2f);
        fill(0);
        ellipse(starHR_X, starHR_Y, 12f, 12f);

        fill(255);
        noStroke();
        ellipse(starHR_X, starHR_Y, 5f, 5f);

        noFill();
        stroke(255, 200);
        strokeWeight(1.2f);
        float ringR = 14f + sin(t * 10f) * 4f;
        ellipse(starHR_X, starHR_Y, ringR, ringR);

        popMatrix();
    }

    private void drawWienDiagram(float zx, float zy, float w, float h, float prog, float t) {
        pushMatrix();
        translate(zx, zy);

        stroke(255);
        strokeWeight(2.5f);
        noFill();
        rectMode(CENTER);
        rect(0, 0, w, h, 10f);

        float padX = 20f;
        float padY = 18f;
        float plotW = w - padX * 2f;
        float plotH = h - padY * 2f;

        stroke(255, 35);
        strokeWeight(1f);
        for (float gx = -plotW * 0.4f; gx <= plotW * 0.4f; gx += plotW * 0.2f) {
            line(gx, -plotH * 0.45f, gx, plotH * 0.45f);
        }
        for (float gy = -plotH * 0.4f; gy <= plotH * 0.4f; gy += plotH * 0.25f) {
            line(-plotW * 0.45f, gy, plotW * 0.45f, gy);
        }

        stroke(255, 180);
        strokeWeight(1.8f);
        line(-plotW * 0.48f, plotH * 0.46f, plotW * 0.48f, plotH * 0.46f);
        line(-plotW * 0.48f, plotH * 0.46f, -plotW * 0.48f, -plotH * 0.46f);

        float peakX = lerp(plotW * 0.32f, -plotW * 0.32f, prog);
        float peakH = lerp(plotH * 0.22f, plotH * 0.88f, prog);

        stroke(255, 30);
        strokeWeight(1f);
        drawPlanckCurve(plotW, plotH, plotW * 0.32f, plotH * 0.22f);
        drawPlanckCurve(plotW, plotH, 0f, plotH * 0.48f);

        stroke(255);
        strokeWeight(2.5f);
        drawPlanckCurve(plotW, plotH, peakX, peakH);

        stroke(255, 180);
        strokeWeight(1.2f);
        float dashY = (plotH * 0.46f) - peakH;
        for (float y = plotH * 0.46f; y > dashY; y -= 7f) {
            line(peakX, y, peakX, y - 3.5f);
        }

        fill(255);
        noStroke();
        ellipse(peakX, dashY, 7f, 7f);

        popMatrix();
    }

    private void drawPlanckCurve(float plotW, float plotH, float peakX, float peakH) {
        noFill();
        beginShape();
        for (int i = 0; i <= 60; i++) {
            float xVal = map(i, 0, 60, -plotW * 0.48f, plotW * 0.48f);
            float distFromPeak = (xVal - peakX) / (plotW * 0.24f);
            float yVal;
            if (distFromPeak < 0) {
                yVal = peakH * exp(distFromPeak * 2.6f);
            } else {
                yVal = peakH * exp(-distFromPeak * 1.15f);
            }
            float drawY = (plotH * 0.46f) - yVal;
            vertex(xVal, drawY);
        }
        endShape();
    }

    @Override
    public void mousePressed() {
        startTimeSec = millis() * 0.001f;
    }

    public static void main(String[] args) {
        PApplet.main("Classes.Ast2");
    }
}