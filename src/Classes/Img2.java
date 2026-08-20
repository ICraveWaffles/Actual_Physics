package Classes;

import processing.core.PApplet;

public class Img2 extends PApplet {

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
        float currentBeat = globalTime / BEAT_DURATION;

        float b = timeSec * (100f / 60f);
        transY = b % 1f;
        logoTransparency = (float) (255 * (-Math.pow(transY, 2) + 2 * (transY)));

        drawInstrumentsSequence(globalTime, currentBeat);

        if (clogo != null) {
            clogo.display(this, b, logoTransparency);
        }
    }

    private void drawInstrumentsSequence(float t, float beat) {
        pushStyle();
        strokeCap(ROUND);
        strokeJoin(ROUND);

        float colWidth = width / 4f;
        float centerY = height * 0.5f;

        float alpha1 = constrain(map(beat, 0.0f, 0.35f, 0f, 255f), 0f, 255f);
        if (alpha1 > 0) {
            drawRefractorTelescope(colWidth * 0.5f, centerY, t, alpha1);
        }

        float alpha2 = constrain(map(beat, 1.0f, 1.35f, 0f, 255f), 0f, 255f);
        if (alpha2 > 0) {
            drawJanssenMicroscope(colWidth * 1.5f, centerY, t, alpha2);
        }

        float alpha3 = constrain(map(beat, 2.0f, 2.35f, 0f, 255f), 0f, 255f);
        if (alpha3 > 0) {
            drawReflectorTelescope(colWidth * 2.5f, centerY, t, alpha3);
        }

        float alpha4 = constrain(map(beat, 3.0f, 3.35f, 0f, 255f), 0f, 255f);
        if (alpha4 > 0) {
            drawParabolicAntenna(colWidth * 3.5f, centerY, t, alpha4);
        }

        popStyle();
    }

    private void drawRefractorTelescope(float cx, float cy, float t, float alpha) {
        pushMatrix();
        translate(cx, cy);

        float scaleFactor = map(alpha, 0, 255, 0.85f, 1.0f);
        scale(scaleFactor);

        float tubeLen = 270f;
        float r1 = 38f;
        float r2 = 22f;

        stroke(255, alpha * 0.9f);
        strokeWeight(2.2f);
        noFill();

        line(-tubeLen * 0.5f, -r1, tubeLen * 0.1f, -r1 * 0.8f);
        line(-tubeLen * 0.5f, r1, tubeLen * 0.1f, r1 * 0.8f);
        line(tubeLen * 0.1f, -r1 * 0.8f, tubeLen * 0.5f, -r2);
        line(tubeLen * 0.1f, r1 * 0.8f, tubeLen * 0.5f, r2);

        rectMode(CORNERS);
        rect(-tubeLen * 0.5f - 12f, -r1 - 4f, -tubeLen * 0.5f, r1 + 4f);
        line(tubeLen * 0.1f, -r1 * 0.8f, tubeLen * 0.1f, r1 * 0.8f);

        stroke(255, alpha);
        strokeWeight(2.8f);
        beginShape();
        vertex(-tubeLen * 0.5f, -r1);
        quadraticVertex(-tubeLen * 0.5f + 12f, 0, -tubeLen * 0.5f, r1);
        quadraticVertex(-tubeLen * 0.5f - 12f, 0, -tubeLen * 0.5f, -r1);
        endShape();

        rect(tubeLen * 0.5f, -r2 - 2f, tubeLen * 0.5f + 18f, r2 + 2f);

        stroke(255, alpha * 0.5f);
        strokeWeight(1.8f);
        line(-20f, r1 * 0.7f, -60f, 160f);
        line(-20f, r1 * 0.7f, 20f, 160f);
        ellipse(-20f, r1 * 0.7f, 12f, 12f);

        stroke(255, alpha * 0.7f);
        strokeWeight(1.2f);
        for (int side = -1; side <= 1; side += 2) {
            float yIn = side * (r1 - 8f);
            line(-tubeLen * 0.5f - 40f, yIn, -tubeLen * 0.5f, yIn);
            line(-tubeLen * 0.5f, yIn, tubeLen * 0.15f, 0);
            line(tubeLen * 0.15f, 0, tubeLen * 0.5f, side * (r2 - 6f));
            line(tubeLen * 0.5f, side * (r2 - 6f), tubeLen * 0.5f + 35f, side * (r2 - 2f));
        }

        popMatrix();
    }

    private void drawJanssenMicroscope(float cx, float cy, float t, float alpha) {
        pushMatrix();
        translate(cx, cy);

        float scaleFactor = map(alpha, 0, 255, 0.85f, 1.0f);
        scale(scaleFactor);

        stroke(255, alpha * 0.9f);
        strokeWeight(2f);
        noFill();

        rectMode(CENTER);
        rect(0, 0, 56f, 110f, 4f);

        stroke(255, alpha * 0.4f);
        line(-28f, -30f, 28f, -30f);
        line(-28f, 0, 28f, 0);
        line(-28f, 30f, 28f, 30f);
        stroke(255, alpha * 0.9f);

        rect(0, -85f, 40f, 65f, 3f);
        rect(0, -122f, 50f, 12f, 2f);

        rect(0, 85f, 40f, 65f, 3f);
        rect(0, 120f, 30f, 10f, 2f);

        stroke(255, alpha * 0.6f);
        strokeWeight(2.5f);
        line(-45f, 155f, 45f, 155f);
        line(0, 125f, 0, 155f);

        noStroke();
        fill(255, alpha);
        ellipse(0, 155f, 6f, 6f);

        stroke(255, alpha * 0.75f);
        strokeWeight(1.2f);

        line(0, 155f, -12f, 120f);
        line(0, 155f, 12f, 120f);

        line(-12f, 120f, 18f, 0);
        line(12f, 120f, -18f, 0);

        line(18f, 0, -15f, -122f);
        line(-18f, 0, 15f, -122f);

        popMatrix();
    }

    private void drawReflectorTelescope(float cx, float cy, float t, float alpha) {
        pushMatrix();
        translate(cx, cy);

        float scaleFactor = map(alpha, 0, 255, 0.85f, 1.0f);
        scale(scaleFactor);

        float tubeLen = 250f;
        float r = 42f;

        stroke(255, alpha * 0.9f);
        strokeWeight(2.2f);
        noFill();
        line(-tubeLen * 0.5f, -r, tubeLen * 0.5f, -r);
        line(-tubeLen * 0.5f, r, tubeLen * 0.5f, r);
        line(tubeLen * 0.5f, -r, tubeLen * 0.5f, r);

        rectMode(CORNERS);
        rect(-tubeLen * 0.5f - 15f, -r - 3f, -tubeLen * 0.5f, r + 3f, 4f);

        stroke(255, alpha);
        strokeWeight(3.5f);
        beginShape();
        vertex(-tubeLen * 0.5f + 4f, -r + 4f);
        quadraticVertex(-tubeLen * 0.5f - 8f, -12f, -tubeLen * 0.5f + 4f, -12f);
        endShape();
        beginShape();
        vertex(-tubeLen * 0.5f + 4f, 12f);
        quadraticVertex(-tubeLen * 0.5f - 8f, 12f, -tubeLen * 0.5f + 4f, r - 4f);
        endShape();

        strokeWeight(1.5f);
        line(tubeLen * 0.35f, -r, tubeLen * 0.35f, r);
        strokeWeight(3.5f);
        line(tubeLen * 0.35f - 2f, -14f, tubeLen * 0.35f - 2f, 14f);

        strokeWeight(2f);
        rect(-tubeLen * 0.5f - 32f, -10f, -tubeLen * 0.5f - 15f, 10f);

        stroke(255, alpha * 0.5f);
        line(0, r + 3f, -35f, 160f);
        line(0, r + 3f, 35f, 160f);

        stroke(255, alpha * 0.75f);
        strokeWeight(1.2f);
        for (int side = -1; side <= 1; side += 2) {
            float yIn = side * (r - 8f);
            line(tubeLen * 0.5f, yIn, -tubeLen * 0.5f + 4f, yIn);
            line(-tubeLen * 0.5f + 4f, yIn, tubeLen * 0.35f - 2f, side * 8f);
            line(tubeLen * 0.35f - 2f, side * 8f, -tubeLen * 0.5f - 32f, 0);
        }

        popMatrix();
    }

    private void drawParabolicAntenna(float cx, float cy, float t, float alpha) {
        pushMatrix();
        translate(cx, cy);

        float scaleFactor = map(alpha, 0, 255, 0.85f, 1.0f);
        scale(scaleFactor);

        float dishRadius = 120f;
        float dishDepth = 40f;
        float focusX = 42f;

        stroke(255, alpha * 0.5f);
        strokeWeight(2f);
        line(-dishDepth - 12f, 0, -dishDepth - 45f, 0);
        line(-dishDepth - 45f, -15f, -dishDepth - 45f, 160f);
        line(-dishDepth - 65f, 160f, -dishDepth - 25f, 160f);

        stroke(255, alpha);
        strokeWeight(3.5f);
        noFill();
        beginShape();
        vertex(-dishDepth, -dishRadius);
        quadraticVertex(dishDepth * 0.6f, 0, -dishDepth, dishRadius);
        endShape();

        stroke(255, alpha * 0.35f);
        strokeWeight(1.2f);
        ellipse(-dishDepth, 0, 16f, dishRadius * 2f);

        stroke(255, alpha * 0.85f);
        strokeWeight(1.8f);
        line(-dishDepth + 8f, -dishRadius * 0.75f, focusX, 0);
        line(-dishDepth + 8f, dishRadius * 0.75f, focusX, 0);

        fill(255, alpha);
        noStroke();
        rectMode(CENTER);
        rect(focusX + 4f, 0, 14f, 18f, 2f);
        triangle(focusX - 3f, -9f, focusX - 3f, 9f, focusX - 10f, 0);

        noFill();
        stroke(255, alpha * 0.75f);
        strokeWeight(1.2f);
        float waveRadius = (t * 75f) % 32f;
        arc(focusX - 3f, 0, waveRadius, waveRadius, PI * 0.6f, PI * 1.4f);

        stroke(255, alpha * 0.7f);
        strokeWeight(1.2f);
        int rays = 3;
        for (int i = -rays; i <= rays; i++) {
            if (i == 0) continue;
            float yVal = i * (dishRadius * 0.8f / rays);
            float normY = yVal / dishRadius;
            float xSurf = -dishDepth + (1f - normY * normY) * (dishDepth * 1.6f);

            line(130f, yVal, xSurf, yVal);
            line(xSurf, yVal, focusX - 3f, 0);
        }

        popMatrix();
    }

    @Override
    public void mousePressed() {
        startTimeSec = millis() * 0.001f;
    }

    public static void main(String[] args) {
        PApplet.main("Classes.Img2");
    }
}