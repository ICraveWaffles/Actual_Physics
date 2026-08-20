package Classes;

import processing.core.PApplet;

public class Ast3 extends PApplet {

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

    private final int NUM_PARALLAX = 140;
    private float[] px = new float[NUM_PARALLAX];
    private float[] py = new float[NUM_PARALLAX];
    private float[] pSpeed = new float[NUM_PARALLAX];
    private float[] pDepth = new float[NUM_PARALLAX];
    private float[] pSize = new float[NUM_PARALLAX];

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

        for (int i = 0; i < NUM_PARALLAX; i++) {
            px[i] = random(width);
            py[i] = random(height);
            pSpeed[i] = random(0.8f, 3.5f);
            pDepth[i] = random(0.02f, 0.20f);
            pSize[i] = map(pDepth[i], 0.02f, 0.20f, 1f, 3.2f);
        }
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

        drawAstrophysics3Sequence(globalTime, currentBeat);

        if (elogo != null) {
            elogo.display(this, b, logoTransparency);
        }
    }

    private void drawAstrophysics3Sequence(float t, float beat) {
        pushStyle();
        strokeCap(ROUND);
        strokeJoin(ROUND);

        float orbitRadius = 100f;
        float orbAngle = (t / (2f * BEAT_DURATION)) * TWO_PI;

        float planetRelX = cos(orbAngle);
        float planetRelY = sin(orbAngle);

        float planetCamX = planetRelX * 50f;
        float planetCamY = planetRelY * 75f;

        drawDeepSpaceParallax(planetCamX, planetCamY);
        drawLeftBackgroundStars(planetCamX, planetCamY, 0.12f);

        float baseMainX = width * 0.27f;
        float baseMainY = height * 0.58f;

        float mainStarX = baseMainX + planetCamX;
        float mainStarY = baseMainY + planetCamY;

        float zoomProg = constrain(map(beat, 0f, 2f, 0f, 1f), 0f, 1f);
        float zoomScale = lerp(1.0f, 0.55f, zoomProg);

        pushMatrix();
        translate(mainStarX, mainStarY);
        scale(zoomScale);
        translate(-mainStarX, -mainStarY);

        if (beat < 2.0f) {
            float expandProg = constrain(map(beat, 0.2f, 1.8f, 0f, 1f), 0f, 1f);
            drawExpandingStar(mainStarX, mainStarY, expandProg, t);
        } else {
            float novaProg = constrain(map(beat, 2.0f, 3.8f, 0f, 1f), 0f, 1f);
            drawSupernova(mainStarX, mainStarY, novaProg, t);
        }
        popMatrix();

        float bandX = width * 0.58f;
        float bandY = height * 0.58f;
        float bandWidth = width * 0.075f;
        float bandHeight = height * 0.58f;
        drawParallaxHUD(bandX, bandY, bandWidth, bandHeight, t, planetRelY, beat);

        float baseRightX = width * 0.78f;
        float baseRightY = height * 0.58f;
        drawRightPlanetSystem(baseRightX, baseRightY, orbitRadius, orbAngle, planetRelX, planetRelY, t);

        popStyle();
    }

    private void drawDeepSpaceParallax(float camX, float camY) {
        noStroke();
        fill(255, 160);
        for (int i = 0; i < NUM_PARALLAX; i++) {
            py[i] += pSpeed[i];
            if (py[i] > height) {
                py[i] = 0;
                px[i] = random(width);
            }

            float renderX = px[i] + (camX * pDepth[i]);
            float renderY = py[i] + (camY * pDepth[i]);

            ellipse(renderX, renderY, pSize[i], pSize[i]);
        }
    }

    private void drawLeftBackgroundStars(float camX, float camY, float depth) {
        float[][] baseStars = {
                {width * 0.06f, height * 0.32f, 18f},
                {width * 0.11f, height * 0.74f, 22f},
                {width * 0.05f, height * 0.85f, 14f},
                {width * 0.17f, height * 0.24f, 20f},
                {width * 0.14f, height * 0.52f, 16f}
        };

        float offsetX = camX * depth;
        float offsetY = camY * depth;

        for (float[] s : baseStars) {
            float sx = s[0] + offsetX;
            float sy = s[1] + offsetY;
            float sz = s[2];

            noFill();
            stroke(255, 60);
            strokeWeight(1f);
            ellipse(sx, sy, sz * 1.8f, sz * 1.8f);

            stroke(255, 130);
            line(sx - sz, sy, sx + sz, sy);
            line(sx, sy - sz, sx, sy + sz);

            fill(255);
            stroke(255, 200);
            strokeWeight(1.2f);
            ellipse(sx, sy, sz * 0.45f, sz * 0.45f);
        }
    }

    private void drawParallaxHUD(float cx, float cy, float w, float h, float t, float planetRelY, float beat) {
        pushMatrix();
        translate(cx, cy);

        rectMode(CENTER);
        stroke(255, 220);
        strokeWeight(1.5f);
        fill(0, 235);
        rect(0, 0, w, h, 6f);

        stroke(255, 80);
        strokeWeight(1f);
        line(0, -h * 0.46f, 0, h * 0.46f);

        for (float y = -h * 0.44f; y <= h * 0.44f; y += h * 0.08f) {
            line(-6f, y, 6f, y);
        }

        float maxHUDShift = h * 0.16f;

        float[][] bgStarsHUD = {
                {-0.38f, 0.05f, 6f},
                {-0.25f, 0.12f, 9f},
                {-0.12f, 0.22f, 12f},
                { 0.12f, 0.18f, 10f},
                { 0.25f, 0.08f, 7f},
                { 0.38f, 0.15f, 8f}
        };

        for (float[] star : bgStarsHUD) {
            float baseY = star[0] * h;
            float depth = star[1];
            float size = star[2];

            float currentY = baseY + (planetRelY * maxHUDShift * depth);

            stroke(255, map(depth, 0.05f, 0.25f, 40f, 120f));
            strokeWeight(1f);
            line(-w * 0.38f, currentY, w * 0.38f, currentY);

            fill(255);
            stroke(0);
            strokeWeight(1f);
            ellipse(0, currentY, size, size);
        }

        float supergiantBaseY = 0.0f;
        float supergiantDepth = 1.0f;
        float supergiantSize = 30f;
        float supergiantY = supergiantBaseY + (planetRelY * maxHUDShift * supergiantDepth);

        stroke(255, 180);
        strokeWeight(1f);
        line(-w * 0.38f, supergiantY, w * 0.38f, supergiantY);

        pushMatrix();
        translate(0, supergiantY);

        if (beat < 2.0f) {
            float expandProg = constrain(map(beat, 0.2f, 1.8f, 0f, 1f), 0f, 1f);
            float size = lerp(supergiantSize, supergiantSize * 1.5f, expandProg);

            fill(255);
            stroke(255, 220);
            strokeWeight(2f);
            ellipse(0, 0, size, size);

            noFill();
            stroke(255, 160);
            float ringR = size + 8f + sin(t * 6f) * 4f;
            ellipse(0, 0, ringR, ringR);
        } else {
            float novaProg = constrain(map(beat, 2.0f, 3.8f, 0f, 1f), 0f, 1f);
            drawHUDSupernova(novaProg, t);
        }
        popMatrix();

        popMatrix();
    }

    private void drawHUDSupernova(float prog, float t) {
        float flashAlpha = map(prog, 0f, 0.12f, 255f, 0f);
        float shockwaveR = lerp(4f, 110f, prog);

        if (prog < 0.15f) {
            fill(255, max(0, flashAlpha));
            noStroke();
            ellipse(0, 0, 180f, 180f);
        }

        noFill();
        int rings = 10;
        for (int r = 1; r <= rings; r++) {
            float rad = shockwaveR * (r / (float) rings);
            float alpha = map(r / (float) rings, 0f, 1f, 255f, 0f) * (1f - prog);
            stroke(255, alpha);
            strokeWeight(lerp(3f, 0.8f, prog));
            ellipse(0, 0, rad * 2f, rad * 2f);
        }

        stroke(255, (1f - prog) * 255f);
        int rays = 24;
        for (int k = 0; k < rays; k++) {
            float a = k * (TWO_PI / rays);
            float len = shockwaveR * (0.6f + 0.4f * sin(k * 3f + t * 14f));
            line(0, 0, cos(a) * len, sin(a) * len);
        }

        fill(255);
        noStroke();
        float coreR = lerp(20f, 2f, prog);
        ellipse(0, 0, coreR * 2f, coreR * 2f);
    }

    private void drawRightPlanetSystem(float baseX, float baseY, float orbitRadius, float orbAngle, float planetRelX, float planetRelY, float t) {
        pushMatrix();
        translate(baseX, baseY);

        stroke(255, 40);
        strokeWeight(1f);
        line(-160f, -height * 0.22f, -160f, height * 0.22f);
        line(160f, -height * 0.22f, 160f, height * 0.22f);

        for (float y = -height * 0.18f; y <= height * 0.18f; y += 36f) {
            float offsetY = (y + (t * 70f)) % (height * 0.36f) - (height * 0.18f);
            line(-165f, offsetY, -155f, offsetY);
            line(155f, offsetY, 165f, offsetY);
        }

        fill(255);
        stroke(255, 180);
        strokeWeight(2f);
        ellipse(0, 0, 38f, 38f);

        noFill();
        stroke(255, 60);
        strokeWeight(1.2f);
        ellipse(0, 0, orbitRadius * 2f, orbitRadius * 2f);

        float px = planetRelX * orbitRadius;
        float py = planetRelY * orbitRadius;
        float planetSize = map(planetRelY, -1f, 1f, 10f, 16f);

        stroke(255, 120);
        strokeWeight(1f);
        line(px, py, px - 35f, py);

        fill(255);
        stroke(0);
        strokeWeight(1.5f);
        ellipse(px, py, planetSize, planetSize);

        popMatrix();
    }

    private void drawExpandingStar(float cx, float cy, float prog, float t) {
        pushMatrix();
        translate(cx, cy);

        float radius = lerp(45f, 210f, prog);
        float coronaRays = lerp(16f, 64f, prog);
        float maxRayLen = lerp(25f, 130f, prog);

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
        float numRings = lerp(3f, 9f, prog);
        for (int r = 1; r <= (int) numRings; r++) {
            stroke(255, map(r, 1, numRings, 190f, 15f));
            strokeWeight(1.8f);
            float rSize = (radius + r * lerp(10f, 22f, prog)) * 2f;
            ellipse(0, 0, rSize, rSize);
        }

        fill(0);
        stroke(255);
        strokeWeight(lerp(3f, 6f, prog));
        ellipse(0, 0, radius * 2f, radius * 2f);

        popMatrix();
    }

    private void drawSupernova(float cx, float cy, float prog, float t) {
        pushMatrix();
        translate(cx, cy);

        float flashAlpha = map(prog, 0f, 0.15f, 255f, 0f);
        float shockwaveR = lerp(10f, 520f, prog);

        if (prog < 0.2f) {
            fill(255, max(0, flashAlpha));
            noStroke();
            ellipse(0, 0, 700f, 700f);
        }

        noFill();
        int rings = 16;
        for (int i = 1; i <= rings; i++) {
            float r = shockwaveR * (i / (float) rings);
            float alpha = map(i / (float) rings, 0f, 1f, 255f, 0f) * (1f - prog);
            stroke(255, alpha);
            strokeWeight(lerp(4.5f, 1f, prog));
            ellipse(0, 0, r * 2f, r * 2f);
        }

        stroke(255, (1f - prog) * 255f);
        int rays = 50;
        for (int i = 0; i < rays; i++) {
            float a = i * (TWO_PI / rays);
            float len = shockwaveR * (0.7f + 0.3f * sin(i * 5f + t * 10f));
            line(0, 0, cos(a) * len, sin(a) * len);
        }

        fill(255);
        noStroke();
        float coreR = lerp(45f, 6f, prog);
        ellipse(0, 0, coreR * 2f, coreR * 2f);

        popMatrix();
    }

    @Override
    public void mousePressed() {
        startTimeSec = millis() * 0.001f;
    }

    public static void main(String[] args) {
        PApplet.main("Classes.Ast3");
    }
}