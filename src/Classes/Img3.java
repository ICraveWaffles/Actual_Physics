package Classes;

import processing.core.PApplet;

public class Img3 extends PApplet {

    private final float BEAT_DURATION = 0.6f;
    private final float BEATS_PER_STAGE = 4f;
    private final float TOTAL_STAGES = 1f;
    private final float TOTAL_BEATS = BEATS_PER_STAGE * TOTAL_STAGES;
    private final float CYCLE_TIME = TOTAL_BEATS * BEAT_DURATION;

    private float globalTime = 0;
    private float startTimeSec = -1;

    public static Clogo clogo;
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

        clogo = new Clogo(finalX, finalY, finalW, finalH);
    }

    @Override
    public void draw() {
        background(0);

        if (startTimeSec < 0) {
            startTimeSec = millis() * 0.001f;
        }

        float timeSec = (millis() * 0.001f) - startTimeSec;
        globalTime = timeSec % CYCLE_TIME;

        float b = timeSec * (100f / 60f);
        transY = b % 1f;
        logoTransparency = (float) (255 * (-Math.pow(transY, 2) + 2 * (transY)));

        drawFibersSequence(globalTime);

        if (clogo != null) {
            clogo.display(this, b, logoTransparency);
        }
    }

    private void drawFibersSequence(float t) {
        pushStyle();
        strokeCap(ROUND);
        strokeJoin(ROUND);

        float rowHeight = height / 4f;

        drawMultimodeStepFiber(height * 0.25f, t);
        drawMultimodeGradedFiber(height * 0.5f, t);
        drawSinglemodeStepFiber(height * 0.75f, t);

        popStyle();
    }

    private void drawMultimodeStepFiber(float cy, float t) {
        pushMatrix();
        float coreR = 30f;
        float cladR = 50f;
        float xStart = 0;
        float xEnd = width;

        float progress = (t % CYCLE_TIME) / CYCLE_TIME;
        float lightFront = lerp(xStart, xEnd, progress);

        stroke(255, 80);
        strokeWeight(3f);
        line(xStart, cy - cladR, xEnd, cy - cladR);
        line(xStart, cy + cladR, xEnd, cy + cladR);

        stroke(255, 160);
        strokeWeight(3.5f);
        line(xStart, cy - coreR, xEnd, cy - coreR);
        line(xStart, cy + coreR, xEnd, cy + coreR);

        float[] pitchList = {110f, 150f, 190f};
        float[] rRatios = {0.80f, 0.55f, 0.30f};

        noFill();
        for (int r = 0; r < pitchList.length; r++) {
            float pitch = pitchList[r];
            float currentCoreR = coreR * rRatios[r];

            stroke(255, 230);
            strokeWeight(4.5f);
            beginShape();
            for (float x = xStart; x <= lightFront; x += 4f) {
                float cycle = ((x - xStart) % (2 * pitch)) / (2 * pitch);
                float y;
                if (cycle < 0.5f) {
                    y = map(cycle, 0f, 0.5f, cy - currentCoreR, cy + currentCoreR);
                } else {
                    y = map(cycle, 0.5f, 1f, cy + currentCoreR, cy - currentCoreR);
                }
                vertex(x, y);
            }
            endShape();
        }

        popMatrix();
    }

    private void drawMultimodeGradedFiber(float cy, float t) {
        pushMatrix();
        float coreR = 30f;
        float cladR = 50f;
        float xStart = 0;
        float xEnd = width;

        float progress = (t % CYCLE_TIME) / CYCLE_TIME;
        float lightFront = lerp(xStart, xEnd, progress);

        stroke(255, 80);
        strokeWeight(3f);
        line(xStart, cy - cladR, xEnd, cy - cladR);
        line(xStart, cy + cladR, xEnd, cy + cladR);

        stroke(255, 120);
        strokeWeight(3.5f);
        line(xStart, cy - coreR, xEnd, cy - coreR);
        line(xStart, cy + coreR, xEnd, cy + coreR);

        float[] amplitudes = {coreR * 0.70f, coreR * 0.45f, coreR * 0.20f};
        float freq = 0.025f;

        noFill();
        for (int i = 0; i < amplitudes.length; i++) {
            float amp = amplitudes[i];

            stroke(255, 230);
            strokeWeight(4.5f);
            beginShape();
            for (float x = xStart; x <= lightFront; x += 4f) {
                float y = cy + amp * sin((x - xStart) * freq);
                vertex(x, y);
            }
            endShape();
        }

        popMatrix();
    }

    private void drawSinglemodeStepFiber(float cy, float t) {
        pushMatrix();
        float coreR = 30f;
        float cladR = 50f;
        float xStart = 0;
        float xEnd = width;

        float progress = (t % CYCLE_TIME) / CYCLE_TIME;
        float lightFront = lerp(xStart, xEnd, progress);

        stroke(255, 80);
        strokeWeight(3f);
        line(xStart, cy - cladR, xEnd, cy - cladR);
        line(xStart, cy + cladR, xEnd, cy + cladR);

        stroke(255, 160);
        strokeWeight(3.5f);
        line(xStart, cy - coreR, xEnd, cy - coreR);
        line(xStart, cy + coreR, xEnd, cy + coreR);

        if (lightFront > xStart) {
            stroke(255, 245);
            strokeWeight(6f);
            line(xStart, cy, lightFront, cy);
        }

        popMatrix();
    }

    @Override
    public void mousePressed() {
        startTimeSec = millis() * 0.001f;
    }

    public static void main(String[] args) {
        PApplet.main("Classes.Img3");
    }
}