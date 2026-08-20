package Classes;

import processing.core.PApplet;

public class Ast4 extends PApplet {

    private final float BEAT_DURATION = 0.6f;
    private final float BEATS_PER_STAGE = 4f;
    private final float TOTAL_STAGES = 3f; // Extendida a 3 etapas (12 beats en total)
    private final float TOTAL_BEATS = BEATS_PER_STAGE * TOTAL_STAGES; // 12 beats
    private final float CYCLE_TIME = TOTAL_BEATS * BEAT_DURATION;

    private float globalTime = 0;
    private float startTimeSec = -1;

    public static Elogo elogo;
    float logoTransparency;
    float transY;

    // Partículas y estrellas
    private final int NUM_STARS = 150;
    private float[] starX = new float[NUM_STARS];
    private float[] starY = new float[NUM_STARS];
    private float[] starSpeed = new float[NUM_STARS];
    private float[] starAngle = new float[NUM_STARS];

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

        for (int i = 0; i < NUM_STARS; i++) {
            starAngle[i] = random(TWO_PI);
            starSpeed[i] = random(0.2f, 1.0f);
            starX[i] = cos(starAngle[i]) * starSpeed[i];
            starY[i] = sin(starAngle[i]) * starSpeed[i];
        }
    }

    @Override
    public void draw() {
        background(0); // Fondo negro puro

        if (startTimeSec < 0) {
            startTimeSec = millis() * 0.001f;
        }

        float timeSec = (millis() * 0.001f) - startTimeSec;
        globalTime = timeSec % CYCLE_TIME;
        float currentBeat = globalTime / BEAT_DURATION;

        float b = timeSec * (100f / 60f);
        transY = b % 1f;

        // Transparencia base del logo
        float baseAlpha = (float) (255 * (-Math.pow(transY, 2) + 2 * (transY)));

        // Desvanecimiento del Elogo en los últimos 4 beats (Beats 8.0 a 12.0)
        if (currentBeat >= 8.0f) {
            float logoFade = map(currentBeat, 8.0f, 12.0f, 1.0f, 0.0f);
            logoFade = constrain(logoFade, 0f, 1f);
            logoTransparency = baseAlpha * logoFade;
        } else {
            logoTransparency = baseAlpha;
        }

        drawCosmologicalSequence(globalTime, currentBeat);

        if (elogo != null) {
            elogo.display(this, b, logoTransparency);
        }
    }

    private void drawCosmologicalSequence(float t, float beat) {
        pushStyle();
        strokeCap(ROUND);
        strokeJoin(ROUND);

        float centerX = width * 0.5f;
        float centerY = height * 0.5f;

        if (beat < 4.0f) {
            // BEATS 0 - 3: Inflación primordial
            float inflationProg = beat / 4.0f;
            drawInflationPhase(centerX, centerY, inflationProg, t);
        } else if (beat < 6.0f) {
            // BEATS 4 - 5: Desdoblamiento en 3 geometrías
            float splitRaw = map(beat, 4.0f, 6.0f, 0f, 1f);
            float splitProg = smoothStep(splitRaw);
            drawThreeGeometries(centerX, centerY, splitProg, t);
        } else {
            // BEATS 6 - 11 (6 Beats de duración): Evolución a los 3 finales
            float endRaw = map(beat, 6.0f, 12.0f, 0f, 1f);
            float endProg = smoothStep(endRaw);
            drawThreeEndings(centerX, centerY, endProg, beat, t);
        }

        popStyle();
    }

    // ==========================================
    // ETAPA 1: INFLACIÓN (Beats 0-3)
    // ==========================================
    private void drawInflationPhase(float cx, float cy, float prog, float t) {
        pushMatrix();
        translate(cx, cy);

        float expScale = (float) Math.pow(prog, 3.2f) * 2.2f;

        if (prog < 0.20f) {
            float flashProg = prog / 0.20f;
            fill(255, map(flashProg, 0, 1, 255, 0));
            noStroke();
            ellipse(0, 0, lerp(10f, 120f, flashProg), lerp(10f, 120f, flashProg));
        }

        int rings = 10;
        for (int i = 1; i <= rings; i++) {
            float r = (i * 35f) * expScale;
            float alpha = map(r, 0, width * 0.6f, 220, 0);
            alpha = constrain(alpha, 0, 220);

            stroke(255, alpha);
            strokeWeight(lerp(2.5f, 0.5f, prog));
            noFill();
            ellipse(0, 0, r * 2f, r * 2f);
        }

        for (int i = 0; i < NUM_STARS; i++) {
            float distFactor = starSpeed[i] * width * 0.6f * expScale;
            float sx = cos(starAngle[i]) * distFactor;
            float sy = sin(starAngle[i]) * distFactor;

            float tailX = cos(starAngle[i]) * (distFactor - 15f * expScale);
            float tailY = sin(starAngle[i]) * (distFactor - 15f * expScale);

            stroke(255, map(prog, 0f, 1f, 255f, 100f));
            strokeWeight(1.5f);
            line(sx, sy, tailX, tailY);

            noStroke();
            fill(255);
            ellipse(sx, sy, 2.5f, 2.5f);
        }

        popMatrix();
    }

    // ==========================================
    // ETAPA 2: 3 GEOMETRÍAS (Beats 4-5)
    // ==========================================
    private void drawThreeGeometries(float cx, float cy, float splitProg, float t) {
        float offX = lerp(0f, width * 0.31f, splitProg);

        float alpha = map(splitProg, 0f, 0.3f, 100f, 255f);
        alpha = constrain(alpha, 0, 255);

        // 1. Plana (k = 0)
        drawFlatUniverse3D(cx - offX, cy, t, alpha, "PLANA (k = 0)");

        // 2. Silla de Montar / Abierta (k < 0)
        drawSaddleUniverse3D(cx, cy, t, alpha, "HIPERBÓLICA (k < 0)");

        // 3. Esférica / Cerrada (k > 0)
        drawSphericalUniverse3D(cx + offX, cy, t, alpha, "ESFÉRICA (k > 0)");
    }

    // ==========================================
    // ETAPA 3: 3 FINALES EVOLUTIVOS (Beats 6-11)
    // ==========================================
    private void drawThreeEndings(float cx, float cy, float endProg, float currentBeat, float t) {
        float offX = width * 0.31f;

        drawBigFreeze(cx - offX, cy, endProg, currentBeat, t);
        drawBigRip(cx, cy, endProg, currentBeat, t);
        drawBigCrunch(cx + offX, cy, endProg, currentBeat, t);
    }

    // ----------------------------------------------------
    // RENDERS BASE DE LAS GEOMETRÍAS 3D
    // ----------------------------------------------------
    private void drawFlatUniverse3D(float x, float y, float t, float alpha, String label) {
        pushMatrix();
        translate(x, y);

        stroke(255, alpha * 0.85f);
        strokeWeight(1.2f);
        noFill();

        float size = 145f;
        float rotX = 0.9f;
        float rotY = t * 0.3f;

        int grid = 8;
        for (int i = -grid; i <= grid; i++) {
            float p1 = map(i, -grid, grid, -size, size);

            beginShape();
            for (int j = -grid; j <= grid; j++) {
                float p2 = map(j, -grid, grid, -size, size);
                float[] pt = project3D(p1, 0, p2, rotX, rotY);
                vertex(pt[0], pt[1]);
            }
            endShape();

            beginShape();
            for (int j = -grid; j <= grid; j++) {
                float p2 = map(j, -grid, grid, -size, size);
                float[] pt = project3D(p2, 0, p1, rotX, rotY);
                vertex(pt[0], pt[1]);
            }
            endShape();
        }

        drawUniverseLabel(0, 160f, label, alpha);
        popMatrix();
    }

    private void drawSaddleUniverse3D(float x, float y, float t, float alpha, String label) {
        pushMatrix();
        translate(x, y);

        stroke(255, alpha * 0.85f);
        strokeWeight(1.2f);
        noFill();

        float size = 130f;
        float rotX = 0.7f;
        float rotY = t * 0.4f;

        int steps = 12;
        for (int i = -steps; i <= steps; i++) {
            float u = map(i, -steps, steps, -1f, 1f);

            beginShape();
            for (int j = -steps; j <= steps; j++) {
                float v = map(j, -steps, steps, -1f, 1f);
                float z = (u * u - v * v) * 55f;
                float[] pt = project3D(u * size, z, v * size, rotX, rotY);
                vertex(pt[0], pt[1]);
            }
            endShape();

            beginShape();
            for (int j = -steps; j <= steps; j++) {
                float v = map(j, -steps, steps, -1f, 1f);
                float z = (v * v - u * u) * 55f;
                float[] pt = project3D(v * size, z, u * size, rotX, rotY);
                vertex(pt[0], pt[1]);
            }
            endShape();
        }

        drawUniverseLabel(0, 160f, label, alpha);
        popMatrix();
    }

    private void drawSphericalUniverse3D(float x, float y, float t, float alpha, String label) {
        pushMatrix();
        translate(x, y);

        stroke(255, alpha * 0.85f);
        strokeWeight(1.2f);
        noFill();

        float radius = 110f;
        float rotX = 0.4f;
        float rotY = t * 0.5f;

        int rings = 8;
        for (int i = 0; i <= rings; i++) {
            float lat = map(i, 0, rings, -HALF_PI + 0.1f, HALF_PI - 0.1f);
            float rLat = cos(lat) * radius;
            float zLat = sin(lat) * radius;

            beginShape();
            for (float lon = 0; lon <= TWO_PI + 0.1f; lon += 0.3f) {
                float px = cos(lon) * rLat;
                float py = sin(lon) * rLat;
                float[] pt = project3D(px, zLat, py, rotX, rotY);
                vertex(pt[0], pt[1]);
            }
            endShape();
        }

        drawUniverseLabel(0, 160f, label, alpha);
        popMatrix();
    }

    // ----------------------------------------------------
    // FINALES EXTENDIDOS CON FADE OUT EN LOS ÚLTIMOS 4 BEATS
    // ----------------------------------------------------
    private void drawBigFreeze(float x, float y, float prog, float beat, float t) {
        pushMatrix();
        translate(x, y);

        float freezeScale = lerp(1.0f, 2.8f, prog);

        // Desvanecimiento en los últimos 4 beats (beat >= 8.0)
        float alpha = 215f;
        if (beat >= 8.0f) {
            alpha = map(beat, 8.0f, 12.0f, 215f, 0f);
        }
        alpha = constrain(alpha, 0f, 255f);

        stroke(255, alpha);
        strokeWeight(1.2f);
        noFill();

        float size = 145f * freezeScale;
        float rotX = 0.9f;
        float rotY = t * 0.3f;

        int grid = 8;
        for (int i = -grid; i <= grid; i++) {
            float p1 = map(i, -grid, grid, -size, size);

            beginShape();
            for (int j = -grid; j <= grid; j++) {
                float p2 = map(j, -grid, grid, -size, size);
                float[] pt = project3D(p1, 0, p2, rotX, rotY);
                vertex(pt[0], pt[1]);
            }
            endShape();

            beginShape();
            for (int j = -grid; j <= grid; j++) {
                float p2 = map(j, -grid, grid, -size, size);
                float[] pt = project3D(p2, 0, p1, rotX, rotY);
                vertex(pt[0], pt[1]);
            }
            endShape();
        }

        if (prog > 0.05f) {
            int particleCount = (int) (prog * 30);
            for (int i = 0; i < particleCount; i++) {
                float angle = i * (TWO_PI / 30f) + t * 0.05f;
                float r = size * 0.65f;
                float px = cos(angle) * r;
                float py = sin(angle) * r;
                fill(255, alpha * 0.8f);
                noStroke();
                ellipse(px, py, 2.5f, 2.5f);
            }
        }

        drawUniverseLabel(0, 160f, "BIG FREEZE", alpha);
        popMatrix();
    }

    private void drawBigRip(float x, float y, float prog, float beat, float t) {
        pushMatrix();
        translate(x, y);

        // Desvanecimiento progresivo en los últimos 4 beats (Beats 8.0 a 12.0)
        float alpha = 255f;
        if (beat >= 8.0f) {
            alpha = map(beat, 8.0f, 12.0f, 255f, 0f);
        }
        alpha = constrain(alpha, 0f, 255f);

        stroke(255, alpha);
        strokeWeight(1.2f);
        noFill();

        float size = 130f;
        float rotX = 0.7f;
        float rotY = t * 0.4f;

        int steps = 12;
        // El rasgado sigue creciendo de manera acelerada a lo largo de todo el tiempo
        float ripIntensity = (float) Math.pow(prog, 2.5f);

        for (int i = -steps; i <= steps; i++) {
            float u = map(i, -steps, steps, -1f, 1f);

            beginShape();
            for (int j = -steps; j <= steps; j++) {
                float v = map(j, -steps, steps, -1f, 1f);
                float z = (u * u - v * v) * 55f;

                float[] pt = project3D(u * size, z, v * size, rotX, rotY);

                // Desplazamiento caótico progresivo de desgarro en continua expansión
                if (prog > 0.01f) {
                    float distFromCenter = dist(0, 0, pt[0], pt[1]);
                    float angle = atan2(pt[1], pt[0]);
                    float ripShift = distFromCenter * ripIntensity * 3.5f + sin(t * 25f + i + j) * (ripIntensity * 40f);
                    pt[0] += cos(angle) * ripShift;
                    pt[1] += sin(angle) * ripShift;
                }

                vertex(pt[0], pt[1]);
            }
            endShape();

            beginShape();
            for (int j = -steps; j <= steps; j++) {
                float v = map(j, -steps, steps, -1f, 1f);
                float z = (v * v - u * u) * 55f;

                float[] pt = project3D(v * size, z, u * size, rotX, rotY);

                if (prog > 0.01f) {
                    float distFromCenter = dist(0, 0, pt[0], pt[1]);
                    float angle = atan2(pt[1], pt[0]);
                    float ripShift = distFromCenter * ripIntensity * 3.5f + sin(t * 25f + i + j) * (ripIntensity * 40f);
                    pt[0] += cos(angle) * ripShift;
                    pt[1] += sin(angle) * ripShift;
                }

                vertex(pt[0], pt[1]);
            }
            endShape();
        }

        drawUniverseLabel(0, 160f, "BIG RIP", alpha);
        popMatrix();
    }

    private void drawBigCrunch(float x, float y, float prog, float beat, float t) {
        pushMatrix();
        translate(x, y);

        // La esfera 3D colapsa hacia el centro
        float radius = lerp(110f, 1f, (float) Math.pow(prog, 1.5f));

        float alpha = 255f;
        if (beat >= 8.0f) {
            alpha = map(beat, 8.0f, 12.0f, 255f, 0f);
        }
        alpha = constrain(alpha, 0f, 255f);

        stroke(255, alpha);
        strokeWeight(1.2f);
        noFill();

        float rotX = 0.4f;
        float rotY = t * (0.5f + prog * 5.0f); // Giro acelerado constante durante el colapso

        int rings = 8;
        for (int i = 0; i <= rings; i++) {
            float lat = map(i, 0, rings, -HALF_PI + 0.1f, HALF_PI - 0.1f);
            float rLat = cos(lat) * radius;
            float zLat = sin(lat) * radius;

            beginShape();
            for (float lon = 0; lon <= TWO_PI + 0.1f; lon += 0.3f) {
                float px = cos(lon) * rLat;
                float py = sin(lon) * rLat;
                float[] pt = project3D(px, zLat, py, rotX, rotY);
                vertex(pt[0], pt[1]);
            }
            endShape();
        }

        // Destello de singularidad durante el colapso final
        if (prog > 0.6f) {
            float flashAlpha = alpha;
            if (beat >= 8.0f) {
                flashAlpha = map(beat, 8.0f, 12.0f, 255f, 0f);
            }
            fill(255, constrain(flashAlpha, 0, 255));
            noStroke();
            float flashSize = lerp(2f, 40f, (prog - 0.6f) / 0.4f);
            ellipse(0, 0, flashSize, flashSize);
        }

        drawUniverseLabel(0, 160f, "BIG CRUNCH", alpha);
        popMatrix();
    }

    private float[] project3D(float x, float y, float z, float rotX, float rotY) {
        float y1 = y * cos(rotX) - z * sin(rotX);
        float z1 = y * sin(rotX) + z * cos(rotX);
        float x2 = x * cos(rotY) + z1 * sin(rotY);
        return new float[]{x2, y1};
    }

    private float smoothStep(float x) {
        x = constrain(x, 0f, 1f);
        return x * x * (3 - 2 * x);
    }

    private void drawUniverseLabel(float x, float y, String txt, float alpha) {
        fill(255, alpha * 0.9f);
    }

    @Override
    public void mousePressed() {
        startTimeSec = millis() * 0.001f;
    }

    public static void main(String[] args) {
        PApplet.main("Classes.Ast4");
    }
}