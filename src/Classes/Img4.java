package Classes;

import processing.core.PApplet;

public class Img4 extends PApplet {

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

        drawMedicalImagingSequence(globalTime, currentBeat);

        if (clogo != null) {
            clogo.display(this, b, logoTransparency);
        }
    }

    private void drawMedicalImagingSequence(float t, float beat) {
        pushStyle();
        strokeCap(ROUND);
        strokeJoin(ROUND);

        float colWidth = width / 4f;
        float centerY = height * 0.5f;

        float alpha1 = constrain(map(beat, 0.0f, 0.35f, 0f, 255f), 0f, 255f);
        if (alpha1 > 0) {
            drawXRayAttenuation(colWidth * 0.5f, centerY, t, alpha1);
        }

        float alpha2 = constrain(map(beat, 1.0f, 1.35f, 0f, 255f), 0f, 255f);
        if (alpha2 > 0) {
            drawUltrasoundScan(colWidth * 1.5f, centerY, t, alpha2);
        }

        float alpha3 = constrain(map(beat, 2.0f, 2.35f, 0f, 255f), 0f, 255f);
        if (alpha3 > 0) {
            drawMRISpinPrecession(colWidth * 2.5f, centerY, t, alpha3);
        }

        float alpha4 = constrain(map(beat, 3.0f, 3.35f, 0f, 255f), 0f, 255f);
        if (alpha4 > 0) {
            drawPETAnnihilation(colWidth * 3.5f, centerY, t, alpha4);
        }

        popStyle();
    }

    private void drawXRayAttenuation(float cx, float cy, float t, float alpha) {
        pushMatrix();
        translate(cx, cy);

        float scaleFactor = map(alpha, 0, 255, 0.85f, 1.0f);
        scale(scaleFactor);

        stroke(255, alpha * 0.9f);
        strokeWeight(3f);
        noFill();
        rectMode(CENTER);
        rect(0, -120f, 50f, 30f, 4f);
        line(0, -105f, 0, -95f);

        stroke(255, alpha * 0.4f);
        strokeWeight(2f);
        rect(0, 0, 110f, 90f, 6f);
        stroke(255, alpha * 0.8f);
        rect(0, 5f, 45f, 50f, 4f);

        stroke(255, alpha * 0.9f);
        strokeWeight(3f);
        line(-60f, 110f, 60f, 110f);

        int rays = 7;
        float rayXStart = -40f;
        float rayXStep = 80f / (rays - 1);

        for (int i = 0; i < rays; i++) {
            float rx = rayXStart + i * rayXStep;

            stroke(255, alpha * 0.9f);
            strokeWeight(2.5f);
            line(rx, -95f, rx, -45f);

            boolean insideBone = Math.abs(rx) <= 22.5f;

            if (insideBone) {
                stroke(255, alpha * 0.35f);
                strokeWeight(1.8f);
                line(rx, -45f, rx, 45f);

                stroke(255, alpha * 0.25f);
                strokeWeight(1.5f);
                line(rx, 45f, rx, 110f);
                fill(255, alpha * 0.3f);
                noStroke();
                ellipse(rx, 110f, 5f, 5f);
            } else {
                stroke(255, alpha * 0.7f);
                strokeWeight(2.2f);
                line(rx, -45f, rx, 45f);

                stroke(255, alpha * 0.6f);
                strokeWeight(2f);
                line(rx, 45f, rx, 110f);
                fill(255, alpha * 0.85f);
                noStroke();
                ellipse(rx, 110f, 7f, 7f);
            }
        }

        noFill();
        stroke(255, alpha * 0.85f);
        strokeWeight(2.2f);
        beginShape();
        for (float x = -60f; x <= 60f; x += 2f) {
            float val = Math.abs(x) <= 22.5f ? 140f : 123f;
            vertex(x, val);
        }
        endShape();

        popMatrix();
    }

    private void drawUltrasoundScan(float cx, float cy, float t, float alpha) {
        pushMatrix();
        translate(cx, cy);

        float scaleFactor = map(alpha, 0, 255, 0.85f, 1.0f);
        scale(scaleFactor);

        stroke(255, alpha * 0.9f);
        strokeWeight(3f);
        noFill();
        rectMode(CENTER);
        rect(-30f, -110f, 40f, 30f, 5f);
        line(-30f, -95f, -30f, -80f);

        stroke(255, alpha * 0.5f);
        strokeWeight(2f);
        line(-70f, -20f, 10f, -20f);
        line(-70f, 40f, 10f, 40f);

        stroke(255, alpha * 0.3f);
        line(-70f, 100f, 10f, 100f);

        float cycleTime = 1.2f;
        float progress = (t % cycleTime) / cycleTime;
        float waveY = map(progress, 0f, 1f, -80f, 100f);

        noFill();
        stroke(255, alpha * 0.9f);
        strokeWeight(2.5f);
        for (int i = 0; i < 3; i++) {
            float rY = waveY - i * 8f;
            if (rY >= -80f && rY <= 100f) {
                arc(-30f, rY, 30f, 12f, 0, PI);
            }
        }

        if (waveY > -20f) {
            float echo1Y = -20f - (waveY - (-20f));
            if (echo1Y >= -80f) {
                stroke(255, alpha * 0.6f);
                strokeWeight(2f);
                arc(-30f, echo1Y, 25f, 10f, PI, TWO_PI);
            }
        }

        if (waveY > 40f) {
            float echo2Y = 40f - (waveY - 40f);
            if (echo2Y >= -80f) {
                stroke(255, alpha * 0.4f);
                strokeWeight(1.8f);
                arc(-30f, echo2Y, 20f, 8f, PI, TWO_PI);
            }
        }

        stroke(255, alpha * 0.8f);
        strokeWeight(2f);
        line(35f, -90f, 35f, 110f);

        noFill();
        stroke(255, alpha * 0.95f);
        strokeWeight(2.2f);
        beginShape();
        for (float y = -90f; y <= 110f; y += 1f) {
            float xOffset = 0f;
            if (Math.abs(y - (-80f)) < 5f) {
                xOffset = map(5f - Math.abs(y - (-80f)), 0, 5, 0, 35f);
            } else if (Math.abs(y - (-20f)) < 5f) {
                xOffset = map(5f - Math.abs(y - (-20f)), 0, 5, 0, 22f);
            } else if (Math.abs(y - 40f) < 5f) {
                xOffset = map((5f - Math.abs(y - 40f)), 0, 5, 0, 15f);
            }
            vertex(35f + xOffset, y);
        }
        endShape();

        popMatrix();
    }

    private void drawMRISpinPrecession(float cx, float cy, float t, float alpha) {
        pushMatrix();
        translate(cx, cy);

        float scaleFactor = map(alpha, 0, 255, 0.85f, 1.0f);
        scale(scaleFactor);

        stroke(255, alpha * 0.5f);
        strokeWeight(2.5f);
        line(-70f, -120f, -70f, 100f);
        line(70f, -120f, 70f, 100f);

        stroke(255, alpha * 0.3f);
        strokeWeight(1.5f);
        for (float y = -110f; y <= 90f; y += 30f) {
            line(-70f, y, -70f, y + 15f);
            line(70f, y, 70f, y + 15f);
        }

        int rows = 3;
        int cols = 3;
        float spacingX = 42f;
        float spacingY = 48f;

        float rfPulse = (sin(t * 6f) + 1f) * 0.5f;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                float px = (c - 1) * spacingX;
                float py = (r - 1) * spacingY - 10f;

                stroke(255, alpha * 0.7f);
                strokeWeight(1.8f);
                noFill();
                ellipse(px, py, 14f, 14f);

                float angle = t * 7f + (r + c) * 0.4f;
                float arrowLen = 18f;
                float tilt = rfPulse * 0.8f;

                float dx = arrowLen * sin(angle) * tilt;
                float dy = -arrowLen * cos(tilt);

                stroke(255, alpha * 0.95f);
                strokeWeight(2.5f);
                line(px, py, px + dx, py + dy);
                fill(255, alpha);
                noStroke();
                ellipse(px + dx, py + dy, 4f, 4f);
            }
        }

        noFill();
        stroke(255, alpha * 0.85f);
        strokeWeight(2f);
        beginShape();
        for (float x = -60f; x <= 60f; x += 2f) {
            float decay = exp(-Math.abs(x) * 0.03f);
            float y = 110f + sin(x * 0.25f - t * 10f) * 16f * decay;
            vertex(x, y);
        }
        endShape();

        popMatrix();
    }

    private void drawPETAnnihilation(float cx, float cy, float t, float alpha) {
        pushMatrix();
        translate(cx, cy);

        float scaleFactor = map(alpha, 0, 255, 0.85f, 1.0f);
        scale(scaleFactor);

        float ringRadius = 100f;
        int detectors = 16;

        float angle = (t * 1.8f) % TWO_PI;
        int hitIndex1 = Math.round(angle / (TWO_PI / detectors)) % detectors;
        int hitIndex2 = (hitIndex1 + detectors / 2) % detectors;

        for (int i = 0; i < detectors; i++) {
            float a = i * (TWO_PI / detectors);
            float x = ringRadius * cos(a);
            float y = ringRadius * sin(a);

            pushMatrix();
            translate(x, y);
            rotate(a);

            if (i == hitIndex1 || i == hitIndex2) {
                fill(255, alpha);
                stroke(255, alpha);
                strokeWeight(2.5f);
                rectMode(CENTER);
                rect(0, 0, 16f, 26f, 3f);
            } else {
                noFill();
                stroke(255, alpha * 0.6f);
                strokeWeight(2f);
                rectMode(CENTER);
                rect(0, 0, 12f, 22f, 2f);
            }
            popMatrix();
        }

        float burstR = ((t * 80f) % 20f);
        noFill();
        stroke(255, alpha * (1f - burstR / 20f));
        strokeWeight(2f);
        ellipse(0, 0, burstR * 2f, burstR * 2f);

        fill(255, alpha);
        noStroke();
        ellipse(0, 0, 8f, 8f);

        float ray1X = ringRadius * cos(angle);
        float ray1Y = ringRadius * sin(angle);
        float ray2X = ringRadius * cos(angle + PI);
        float ray2Y = ringRadius * sin(angle + PI);

        stroke(255, alpha * 0.9f);
        strokeWeight(2.2f);
        drawGammaWave(0, 0, ray1X, ray1Y, 5);
        drawGammaWave(0, 0, ray2X, ray2Y, 5);

        popMatrix();
    }

    private void drawGammaWave(float x1, float y1, float x2, float y2, int waves) {
        int steps = 40;
        float dx = (x2 - x1) / steps;
        float dy = (y2 - y1) / steps;
        float dist = dist(x1, y1, x2, y2);
        float nx = -(y2 - y1) / dist;
        float ny = (x2 - x1) / dist;

        noFill();
        beginShape();
        for (int i = 0; i <= steps; i++) {
            float px = x1 + dx * i;
            float py = y1 + dy * i;
            float offset = sin((float) i / steps * waves * TWO_PI) * 5f;
            vertex(px + nx * offset, py + ny * offset);
        }
        endShape();
    }

    @Override
    public void mousePressed() {
        startTimeSec = millis() * 0.001f;
    }

    public static void main(String[] args) {
        PApplet.main("Classes.Img4");
    }
}