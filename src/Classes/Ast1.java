package Classes;

import processing.core.PApplet;

public class Ast1 extends PApplet {

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

        drawAstrophysicsSequence(globalTime, currentBeat);

        if (elogo != null) {
            elogo.display(this, b, logoTransparency);
        }
    }

    private void drawAstrophysicsSequence(float t, float beat) {
        pushStyle();
        strokeCap(ROUND);
        strokeJoin(ROUND);

        float centerY = height * 0.5f;

        float slideProg = constrain(map(beat, 1.1f, 1.7f, 0f, 1f), 0f, 1f);
        slideProg = slideProg * slideProg * (3f - 2f * slideProg);

        float coreX = lerp(width * 0.5f, width * 0.28f, slideProg);

        float alphaCollapse = constrain(map(beat, 0.0f, 0.2f, 0f, 255f), 0f, 255f);
        if (alphaCollapse > 0) {
            drawGasCloudCollapse(coreX, centerY, t, beat, alphaCollapse);
        }

        float alphaFusion = constrain(map(beat, 1.8f, 2.1f, 0f, 255f), 0f, 255f);
        if (alphaFusion > 0) {
            float zoomX = width * 0.68f;
            float zoomY = height * 0.5f;
            float zoomSize = 340f;

            drawZoomCallout(coreX, centerY, zoomX, zoomY, zoomSize, alphaFusion);
            drawPPChainFusion(zoomX, zoomY, zoomSize, t, beat, alphaFusion);
        }

        popStyle();
    }

    private void drawGasCloudCollapse(float cx, float cy, float t, float beat, float alpha) {
        pushMatrix();
        translate(cx, cy);

        float rawProg = constrain(map(beat, 0.0f, 1.3f, 0f, 1f), 0f, 1f);
        float collapseProgress = rawProg * rawProg * rawProg;

        float initialRadius = height * 0.46f;
        float currentRadius = lerp(initialRadius, 22f, collapseProgress);

        stroke(255, alpha * 0.25f);
        strokeWeight(1.5f);
        noFill();
        ellipse(0, 0, currentRadius * 2f, currentRadius * 2f);

        int numParticles = 190;
        for (int i = 0; i < numParticles; i++) {
            float baseAngle = i * (TWO_PI / numParticles);
            float spiralOffset = collapseProgress * 2.5f;
            float angle = baseAngle + spiralOffset + sin(i * 17f + t * 4f) * 0.1f;

            float particleStartDist = initialRadius + sin(i * 11.3f) * 80f;
            float r = lerp(particleStartDist, 14f + (i % 9) * 2f, collapseProgress);

            float px = r * cos(angle);
            float py = r * sin(angle);

            stroke(255, alpha * 0.9f);
            strokeWeight(2.8f);
            point(px, py);

            if (collapseProgress < 0.95f) {
                float speedTail = lerp(12f, 45f, collapseProgress);
                stroke(255, alpha * 0.35f);
                strokeWeight(1.2f);
                line(px, py, px - speedTail * cos(angle), py - speedTail * sin(angle));
            }
        }

        if (collapseProgress > 0.3f) {
            float coreAlpha = map(collapseProgress, 0.3f, 1f, 0f, 255f);
            float coreSize = map(collapseProgress, 0.3f, 1f, 10f, 52f);

            fill(255, alpha * (coreAlpha / 255f));
            noStroke();
            ellipse(0, 0, coreSize, coreSize);

            noFill();
            stroke(255, alpha * (coreAlpha / 255f) * 0.7f);
            strokeWeight(3f);
            ellipse(0, 0, coreSize + 24f, coreSize + 24f);
        }

        popMatrix();
    }

    private void drawZoomCallout(float cx, float cy, float zx, float zy, float zSize, float alpha) {
        pushMatrix();

        stroke(255, alpha * 0.85f);
        strokeWeight(2f);
        noFill();
        rectMode(CENTER);
        rect(cx, cy, 54f, 54f, 4f);

        stroke(255, alpha * 0.35f);
        strokeWeight(1.5f);
        line(cx + 27f, cy - 27f, zx - zSize * 0.5f, zy - zSize * 0.5f);
        line(cx + 27f, cy + 27f, zx - zSize * 0.5f, zy + zSize * 0.5f);

        stroke(255, alpha);
        strokeWeight(3f);
        rect(zx, zy, zSize, zSize, 12f);

        popMatrix();
    }

    private void drawPPChainFusion(float zx, float zy, float zSize, float t, float beat, float alpha) {
        pushMatrix();

        clip(zx - zSize * 0.5f + 4f, zy - zSize * 0.5f + 4f, zSize - 8f, zSize - 8f);
        translate(zx, zy);

        float fusionBeat = beat - 2.0f;

        if (fusionBeat < 0.65f) {
            float p1Prog = constrain(map(fusionBeat, 0.0f, 0.45f, 0f, 1f), 0f, 1f);
            float pDist = lerp(120f, 14f, p1Prog);

            drawProton(-pDist, 0, alpha, 26f);
            drawProton(pDist, 0, alpha, 26f);

            if (p1Prog >= 0.45f) {
                float postProg = map(fusionBeat, 0.45f, 0.65f, 0f, 1f);
                drawDeuterium(0, 0, alpha, 26f);

                float ejDist = lerp(10f, 110f, postProg);
                drawPositron(ejDist * cos(-PI * 0.3f), ejDist * sin(-PI * 0.3f), alpha);
                drawNeutrino(ejDist * cos(PI * 0.3f), ejDist * sin(PI * 0.3f), alpha);
                drawGammaWave(0, 0, -ejDist, 0, 4);
            }
        } else if (fusionBeat < 1.3f) {
            float p2Prog = constrain(map(fusionBeat, 0.65f, 1.0f, 0f, 1f), 0f, 1f);

            drawDeuterium(0, 15f * (1f - p2Prog), alpha, 26f);

            float p3Y = lerp(-130f, -14f, p2Prog);
            drawProton(0, p3Y, alpha, 26f);

            if (p2Prog >= 0.95f) {
                float postProg2 = map(fusionBeat, 1.0f, 1.3f, 0f, 1f);
                drawHelium3(0, 0, alpha, 26f);

                float gLen = lerp(10f, 120f, postProg2);
                drawGammaWave(0, 0, gLen * cos(PI * 0.25f), gLen * sin(PI * 0.25f), 5);
                drawGammaWave(0, 0, -gLen * cos(PI * 0.25f), -gLen * sin(PI * 0.25f), 5);
            }
        } else {
            float p3Prog = constrain(map(fusionBeat, 1.3f, 1.65f, 0f, 1f), 0f, 1f);

            float h3Dist = lerp(110f, 16f, p3Prog);
            drawHelium3(-h3Dist, 0, alpha, 24f);
            drawHelium3(h3Dist, 0, alpha, 24f);

            if (p3Prog >= 0.95f) {
                float postProg3 = map(fusionBeat, 1.65f, 2.0f, 0f, 1f);

                if (postProg3 < 0.25f) {
                    float flashAlpha = map(postProg3, 0f, 0.25f, 255f, 0f);
                    fill(255, alpha * (flashAlpha / 255f));
                    noStroke();
                    ellipse(0, 0, 140f, 140f);
                }

                drawHelium4(0, 0, alpha, 26f);

            }
        }

        noClip();
        popMatrix();
    }

    private void drawProton(float x, float y, float alpha, float size) {
        pushMatrix();
        translate(x, y);

        stroke(255, alpha);
        strokeWeight(2.5f);
        fill(0);
        ellipse(0, 0, size, size);

        stroke(255, alpha);
        strokeWeight(2f);
        line(-size * 0.25f, 0, size * 0.25f, 0);
        line(0, -size * 0.25f, 0, size * 0.25f);

        popMatrix();
    }

    private void drawNeutron(float x, float y, float alpha, float size) {
        pushMatrix();
        translate(x, y);

        stroke(255, alpha * 0.8f);
        strokeWeight(2.5f);
        fill(60);
        ellipse(0, 0, size, size);

        stroke(255, alpha * 0.4f);
        strokeWeight(1.5f);
        noFill();
        ellipse(0, 0, size * 0.45f, size * 0.45f);

        popMatrix();
    }

    private void drawDeuterium(float x, float y, float alpha, float nucleonSize) {
        pushMatrix();
        translate(x, y);

        drawNeutron(nucleonSize * 0.35f, 0, alpha, nucleonSize);
        drawProton(-nucleonSize * 0.35f, 0, alpha, nucleonSize);

        popMatrix();
    }

    private void drawHelium3(float x, float y, float alpha, float nucleonSize) {
        pushMatrix();
        translate(x, y);

        drawNeutron(0, nucleonSize * 0.35f, alpha, nucleonSize);
        drawProton(-nucleonSize * 0.35f, -nucleonSize * 0.2f, alpha, nucleonSize);
        drawProton(nucleonSize * 0.35f, -nucleonSize * 0.2f, alpha, nucleonSize);

        popMatrix();
    }

    private void drawHelium4(float x, float y, float alpha, float nucleonSize) {
        pushMatrix();
        translate(x, y);

        drawNeutron(-nucleonSize * 0.32f, -nucleonSize * 0.32f, alpha, nucleonSize);
        drawNeutron(nucleonSize * 0.32f, nucleonSize * 0.32f, alpha, nucleonSize);
        drawProton(nucleonSize * 0.32f, -nucleonSize * 0.32f, alpha, nucleonSize);
        drawProton(-nucleonSize * 0.32f, nucleonSize * 0.32f, alpha, nucleonSize);

        popMatrix();
    }

    private void drawPositron(float x, float y, float alpha) {
        pushMatrix();
        translate(x, y);

        stroke(255, alpha);
        strokeWeight(2f);
        fill(0);
        ellipse(0, 0, 16f, 16f);

        stroke(255, alpha);
        strokeWeight(1.8f);
        line(-3f, 0, 3f, 0);
        line(0, -3f, 0, 3f);

        popMatrix();
    }

    private void drawNeutrino(float x, float y, float alpha) {
        pushMatrix();
        translate(x, y);

        fill(255, alpha);
        noStroke();
        ellipse(0, 0, 8f, 8f);

        stroke(255, alpha * 0.6f);
        strokeWeight(1.2f);
        noFill();
        ellipse(0, 0, 16f, 16f);

        popMatrix();
    }

    private void drawGammaWave(float x1, float y1, float x2, float y2, int waves) {
        int steps = 35;
        float dx = (x2 - x1) / steps;
        float dy = (y2 - y1) / steps;
        float dist = dist(x1, y1, x2, y2);
        if (dist == 0) return;

        float nx = -(y2 - y1) / dist;
        float ny = (x2 - x1) / dist;

        noFill();
        beginShape();
        for (int i = 0; i <= steps; i++) {
            float px = x1 + dx * i;
            float py = y1 + dy * i;
            float offset = sin((float) i / steps * waves * TWO_PI) * 7f;
            vertex(px + nx * offset, py + ny * offset);
        }
        endShape();
    }

    @Override
    public void mousePressed() {
        startTimeSec = millis() * 0.001f;
    }

    public static void main(String[] args) {
        PApplet.main("Classes.Ast1");
    }
}