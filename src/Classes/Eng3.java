package Classes;

import processing.core.PApplet;

public class Eng3 extends PApplet {

    private final float BEAT_DURATION = 0.6f;
    private final float BEATS_PER_STAGE = 8f;
    private final float TOTAL_STAGES = 1f;
    private final float TOTAL_BEATS = BEATS_PER_STAGE * TOTAL_STAGES;
    private final float CYCLE_TIME = TOTAL_BEATS * BEAT_DURATION;

    private float globalTime = 0;
    private float startTimeSec = -1;

    public static Blogo alogo;
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

        alogo = new Blogo(finalX, finalY, finalW, finalH);
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

        drawResonanceScene(globalTime);

        if (alogo != null) {
            alogo.display(this, b, logoTransparency);
        }
    }

    private void drawResonanceScene(float tCycle) {
        pushStyle();
        strokeCap(ROUND);
        strokeJoin(ROUND);

        float deskEndX = width * 0.12f;
        float rulerTipX = width * 0.58f;
        float traceEndX = width * 0.95f;

        float y1 = height * 0.30f;
        float y2 = height * 0.70f;
        float maxAmp = 135f;

        float omega = 32f;

        float gammaHighQ = 0.30f;
        float gammaLowQ = 3.60f;

        noStroke();
        fill(25, 28, 35);
        rectMode(CORNERS);
        rect(0, height * 0.05f, deskEndX, height * 0.95f, 0, 15f, 15f, 0);
        stroke(255, 40);
        strokeWeight(3f);
        line(deskEndX, height * 0.05f + 15f, deskEndX, height * 0.95f - 15f);

        float currentAmp1 = getAmplitude(tCycle, maxAmp, gammaHighQ, omega);
        drawTraceAndEnvelope(rulerTipX, traceEndX, y1, tCycle, maxAmp, gammaHighQ, omega);
        drawRuler(deskEndX, rulerTipX, y1, currentAmp1, tCycle, maxAmp, gammaHighQ, omega);
        drawClamp(deskEndX, y1);

        float currentAmp2 = getAmplitude(tCycle, maxAmp, gammaLowQ, omega);
        drawTraceAndEnvelope(rulerTipX, traceEndX, y2, tCycle, maxAmp, gammaLowQ, omega);
        drawRuler(deskEndX, rulerTipX, y2, currentAmp2, tCycle, maxAmp, gammaLowQ, omega);
        drawClamp(deskEndX, y2);

        popStyle();
    }

    private void drawRuler(float startX, float endX, float baseY, float amp, float tCycle, float maxAmp, float gamma, float omega) {
        float L = endX - startX;

        float numTicks = 35f;

        for (int i = 3; i >= 0; i--) {
            float tBlur = wrapTime(tCycle - (i * 0.008f));
            float blurAmp = getAmplitude(tBlur, maxAmp, gamma, omega);
            float alpha = (i == 0) ? 255f : 40f - (i * 10f);
            float weight = (i == 0) ? 6.5f : 4.5f;

            stroke(255, alpha);
            strokeWeight(weight);
            noFill();
            beginShape();
            for (float u = 0; u <= 1.0f; u += 0.02f) {
                float px = startX + u * L;
                float py = baseY + blurAmp * ((u * u * (3f - u)) / 2f);
                vertex(px, py);
            }
            endShape();

            if (i == 0) {
                stroke(255, 120);
                strokeWeight(1.8f);
                for (int t = 1; t < numTicks; t++) {
                    float u = t / numTicks;
                    float px = startX + u * L;
                    float py = baseY + blurAmp * ((u * u * (3f - u)) / 2f);
                    float tickSize = (t % 5 == 0) ? 10f : 5f;
                    line(px, py - tickSize * 0.5f, px, py + tickSize * 0.5f);
                }

                float tipY = baseY + blurAmp;
                pushStyle();
                noStroke();
                fill(255);
                ellipse(endX, tipY, 14f, 14f);
                fill(255, 70);
                ellipse(endX, tipY, 26f, 26f);
                popStyle();
            }
        }
    }

    private void drawClamp(float edgeX, float baseY) {
        rectMode(CENTER);

        fill(60);
        stroke(255, 140);
        strokeWeight(2.5f);
        rect(edgeX - 25f, baseY, 60f, 32f, 6f);

        fill(200);
        noStroke();
        ellipse(edgeX - 15f, baseY, 10f, 10f);
        ellipse(edgeX - 35f, baseY, 10f, 10f);
    }

    private void drawTraceAndEnvelope(float startX, float endX, float baseY, float tCycle, float maxAmp, float gamma, float omega) {
        float scrollSpeed = 380f;

        stroke(255, 55);
        strokeWeight(1.8f);

        for (int sign = -1; sign <= 1; sign += 2) {
            beginShape();
            for (float x = startX; x <= endX; x += 8f) {
                float delay = (x - startX) / scrollSpeed;
                float tEval = wrapTime(tCycle - delay);
                float env = getEnvelope(tEval, maxAmp, gamma);

                if (x % 16 < 8) {
                    vertex(x, baseY + sign * env);
                } else {
                    endShape();
                    beginShape();
                }
            }
            endShape();
        }

        noFill();
        stroke(255, 230);
        strokeWeight(2.5f);
        beginShape();
        for (float x = startX; x <= endX; x += 3f) {
            float delay = (x - startX) / scrollSpeed;
            float tEval = wrapTime(tCycle - delay);
            float yOffset = getAmplitude(tEval, maxAmp, gamma, omega);

            float alphaDist = map(x, startX, endX, 255f, 0f);
            stroke(255, max(0, alphaDist));
            vertex(x, baseY + yOffset);
        }
        endShape();

        stroke(255, 25);
        strokeWeight(1.5f);
        line(startX, baseY, endX, baseY);
    }

    private float getAmplitude(float t, float maxAmp, float gamma, float omega) {
        float pullDuration = 1.0f;

        if (t < pullDuration) {
            float p = smoothStep(0f, pullDuration, t);
            return maxAmp * p;
        } else {
            float tRel = t - pullDuration;
            return maxAmp * exp(-gamma * tRel) * cos(omega * tRel);
        }
    }

    private float getEnvelope(float t, float maxAmp, float gamma) {
        float pullDuration = 1.0f;

        if (t < pullDuration) {
            float p = smoothStep(0f, pullDuration, t);
            return maxAmp * p;
        } else {
            float tRel = t - pullDuration;
            return maxAmp * exp(-gamma * tRel);
        }
    }

    private float wrapTime(float t) {
        float wrapped = t % CYCLE_TIME;
        if (wrapped < 0) {
            wrapped += CYCLE_TIME;
        }
        return wrapped;
    }

    private float smoothStep(float edge0, float edge1, float x) {
        float t = constrain((x - edge0) / (edge1 - edge0), 0.0f, 1.0f);
        return t * t * (3.0f - 2.0f * t);
    }

    @Override
    public void mousePressed() {
        startTimeSec = millis() * 0.001f;
    }

    public static void main(String[] args) {
        PApplet.main("Classes.Eng3");
    }
}