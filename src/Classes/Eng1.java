package Classes;

import processing.core.PApplet;
import java.util.ArrayList;

public class Eng1 extends PApplet {

    private final float BEAT_DURATION = 0.6f;
    private final float BEATS_PER_STAGE = 8f;
    private final float TOTAL_STAGES = 1f;
    private final float TOTAL_BEATS = BEATS_PER_STAGE * TOTAL_STAGES;

    private float globalTime = 0;
    private float startTimeSec = -1;

    private ArrayList<Particle> particles;

    public static Blogo alogo;
    float logoTransparency;
    float transY;

    private float[] entropyParticleLevels = new float[12];
    private float[] entropyParticleX = new float[12];
    private float[] entropyParticleTargetLevels = new float[12];
    private float[] entropyParticleJitter = new float[12];
    private int lastQuarterStep = -1;

    @Override
    public void settings() {
        fullScreen();
        smooth(8);
    }

    @Override
    public void setup() {
        particles = new ArrayList<>();
        noCursor();
        startTimeSec = millis() * 0.001f;

        for (int i = 0; i < 12; i++) {
            entropyParticleLevels[i] = 0;
            entropyParticleTargetLevels[i] = 0;
            entropyParticleX[i] = random(-160f, 160f);
            entropyParticleJitter[i] = 0;
        }

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
        globalTime = timeSec % (BEAT_DURATION * TOTAL_BEATS);
        float currentBeat = globalTime / BEAT_DURATION;

        float b = timeSec * (100f / 60f);
        transY = b % 1f;
        logoTransparency = (float) (255 * (-Math.pow(transY, 2) + 2 * (transY)));

        // Etapa 1 (Beats 0-4): Ciclos P-V y Pistones
        // Etapa 2 (Beats 4-6): Máquina Térmica en el CENTRO
        // Etapa 3 (Beats 6-8): Máquina Térmica a la DERECHA + Entropía a la IZQUIERDA
        if (currentBeat < 4f) {
            drawThermodynamicCycles(currentBeat / 4f, timeSec);
            lastQuarterStep = -1;
        } else if (currentBeat < 6f) {
            float heatEngineProgress = (currentBeat - 4f) / 2f;
            drawHeatEngine(width * 0.50f, height * 0.5f, heatEngineProgress, timeSec);
            lastQuarterStep = -1;
        } else {
            // Desplazamiento fluido del centro a la derecha
            float moveProgress = smoothStep(6.0f, 6.8f, currentBeat);
            float engineX = lerp(width * 0.50f, width * 0.70f, moveProgress);

            drawHeatEngine(engineX, height * 0.5f, 1.0f, timeSec);
            drawEntropyStage(width * 0.30f, height * 0.5f, currentBeat, timeSec);
        }

        if (alogo != null) {
            alogo.display(this, b, logoTransparency);
        }

        updateAndDisplayParticles();
    }

    private float smoothStep(float edge0, float edge1, float x) {
        float t = constrain((x - edge0) / (edge1 - edge0), 0.0f, 1.0f);
        return t * t * (3.0f - 2.0f * t);
    }

    private void drawThermodynamicCycles(float progress, float timeSec) {
        pushStyle();

        float leftColumnX = width * 0.28f;
        float rightColumnX = width * 0.65f;
        float diagramY = height * 0.32f;
        float pistonY = height * 0.72f;

        float cycleProgress = progress % 1.0f;

        drawCarnotPV(leftColumnX, diagramY, cycleProgress);
        drawIsobaricIsochoricPV(rightColumnX, diagramY, cycleProgress);

        float v1 = getCarnotVolume(cycleProgress);
        float v2 = getSquareVolume(cycleProgress);

        drawSinglePiston(leftColumnX, pistonY, v1, true);
        drawSinglePiston(rightColumnX, pistonY, v2, false);

        popStyle();
    }

    private float getCarnotVolume(float u) {
        if (u < 0.25f) return lerp(0.25f, 0.65f, smoothStep(0.00f, 0.25f, u));
        else if (u < 0.50f) return lerp(0.65f, 0.85f, smoothStep(0.25f, 0.50f, u));
        else if (u < 0.75f) return lerp(0.85f, 0.45f, smoothStep(0.50f, 0.75f, u));
        else return lerp(0.45f, 0.25f, smoothStep(0.75f, 1.00f, u));
    }

    private float getCarnotPressure(float u) {
        if (u < 0.25f) return lerp(0.85f, 0.50f, smoothStep(0.00f, 0.25f, u));
        else if (u < 0.50f) return lerp(0.50f, 0.20f, smoothStep(0.25f, 0.50f, u));
        else if (u < 0.75f) return lerp(0.20f, 0.40f, smoothStep(0.50f, 0.75f, u));
        else return lerp(0.40f, 0.85f, smoothStep(0.75f, 1.00f, u));
    }

    private float getSquareVolume(float u) {
        if (u < 0.25f) return 0.30f;
        else if (u < 0.50f) return lerp(0.30f, 0.80f, smoothStep(0.25f, 0.50f, u));
        else if (u < 0.75f) return 0.80f;
        else return lerp(0.80f, 0.30f, smoothStep(0.75f, 1.00f, u));
    }

    private float getSquarePressure(float u) {
        if (u < 0.25f) return lerp(0.25f, 0.80f, smoothStep(0.00f, 0.25f, u));
        else if (u < 0.50f) return 0.80f;
        else if (u < 0.75f) return lerp(0.80f, 0.25f, smoothStep(0.50f, 0.75f, u));
        else return 0.25f;
    }

    private void drawCarnotPV(float cx, float cy, float u) {
        float w = 380f;
        float h = 240f;

        stroke(255, 60);
        strokeWeight(2f);
        line(cx - w * 0.5f, cy + h * 0.5f, cx + w * 0.5f, cy + h * 0.5f);
        line(cx - w * 0.5f, cy + h * 0.5f, cx - w * 0.5f, cy - h * 0.5f);

        noFill();
        stroke(255, 200);
        strokeWeight(2.5f);
        beginShape();
        for (float t = 0; t <= 1.0f; t += 0.008f) {
            float vx = cx - w * 0.5f + getCarnotVolume(t) * w;
            float py = cy + h * 0.5f - getCarnotPressure(t) * h;
            vertex(vx, py);
        }
        endShape(CLOSE);

        float[] vertexU = {0.0f, 0.25f, 0.50f, 0.75f};
        for (float vu : vertexU) {
            float vx = cx - w * 0.5f + getCarnotVolume(vu) * w;
            float py = cy + h * 0.5f - getCarnotPressure(vu) * h;

            float diff = abs(u - vu);
            if (diff > 0.5f) diff = 1.0f - diff;

            if (diff < 0.06f) {
                float norm = map(diff, 0f, 0.06f, 1f, 0f);
                float pulse = (0.5f + 0.5f * cos(norm * PI));
                float flashAlpha = 255f * pulse;
                float flashSize = lerp(9f, 32f, pulse);

                fill(255, flashAlpha * 0.3f);
                ellipse(vx, py, flashSize * 1.5f, flashSize * 1.5f);
                fill(255, flashAlpha);
                ellipse(vx, py, flashSize, flashSize);
            }

            fill(255, 220);
            noStroke();
            ellipse(vx, py, 9f, 9f);
        }

        float curV = cx - w * 0.5f + getCarnotVolume(u) * w;
        float curP = cy + h * 0.5f - getCarnotPressure(u) * h;

        fill(255);
        noStroke();
        ellipse(curV, curP, 13f, 13f);

        stroke(255, 45);
        strokeWeight(1.5f);
        drawDashedLine(curV, curP, curV, cy + h * 0.5f, 5f);
        drawDashedLine(curV, curP, cx - w * 0.5f, curP, 5f);
    }

    private void drawIsobaricIsochoricPV(float cx, float cy, float u) {
        float w = 380f;
        float h = 240f;

        stroke(255, 60);
        strokeWeight(2f);
        line(cx - w * 0.5f, cy + h * 0.5f, cx + w * 0.5f, cy + h * 0.5f);
        line(cx - w * 0.5f, cy + h * 0.5f, cx - w * 0.5f, cy - h * 0.5f);

        float vMin = cx - w * 0.5f + 0.30f * w;
        float vMax = cx - w * 0.5f + 0.80f * w;
        float pMin = cy + h * 0.5f - 0.25f * h;
        float pMax = cy + h * 0.5f - 0.80f * h;

        noFill();
        stroke(255, 200);
        strokeWeight(2.5f);
        rectMode(CORNERS);
        rect(vMin, pMax, vMax, pMin);

        float[][] corners = {
                {vMin, pMin, 0.00f},
                {vMin, pMax, 0.25f},
                {vMax, pMax, 0.50f},
                {vMax, pMin, 0.75f}
        };

        for (float[] c : corners) {
            float vx = c[0];
            float py = c[1];
            float vu = c[2];

            float diff = abs(u - vu);
            if (diff > 0.5f) diff = 1.0f - diff;

            if (diff < 0.06f) {
                float norm = map(diff, 0f, 0.06f, 1f, 0f);
                float pulse = (0.5f + 0.5f * cos(norm * PI));
                float flashAlpha = 255f * pulse;
                float flashSize = lerp(9f, 32f, pulse);

                fill(255, flashAlpha * 0.3f);
                ellipse(vx, py, flashSize * 1.5f, flashSize * 1.5f);
                fill(255, flashAlpha);
                ellipse(vx, py, flashSize, flashSize);
            }

            fill(255, 220);
            noStroke();
            ellipse(vx, py, 9f, 9f);
        }

        float curV = cx - w * 0.5f + getSquareVolume(u) * w;
        float curP = cy + h * 0.5f - getSquarePressure(u) * h;

        fill(255);
        noStroke();
        ellipse(curV, curP, 13f, 13f);

        stroke(255, 45);
        strokeWeight(1.5f);
        drawDashedLine(curV, curP, curV, cy + h * 0.5f, 5f);
        drawDashedLine(curV, curP, cx - w * 0.5f, curP, 5f);
    }

    private void drawSinglePiston(float cx, float py, float v, boolean isLeftPiston) {
        float chamberW = 380f;
        float chamberH = 130f;

        float pLeft = cx - chamberW * 0.5f;
        float pRight = cx + chamberW * 0.5f;

        stroke(255, 180);
        strokeWeight(2.5f);
        line(pLeft, py - chamberH * 0.5f, pRight, py - chamberH * 0.5f);
        line(pLeft, py + chamberH * 0.5f, pRight, py + chamberH * 0.5f);
        line(pLeft, py - chamberH * 0.5f, pLeft, py + chamberH * 0.5f);
        line(pRight, py - chamberH * 0.5f, pRight, py + chamberH * 0.5f);

        float headX = isLeftPiston ? (pLeft + v * chamberW) : (pRight - v * chamberW);

        fill(255, 230);
        rectMode(CENTER);
        rect(headX, py, 18f, chamberH - 6f);

        stroke(255, 230);
        strokeWeight(3f);
        if (isLeftPiston) line(headX, py, headX - 80f, py);
        else line(headX, py, headX + 80f, py);

        for (int i = 0; i < 20; i++) {
            float px, pY;
            if (isLeftPiston) {
                px = random(pLeft + 8f, max(pLeft + 10f, headX - 8f));
            } else {
                px = random(min(pRight - 10f, headX + 8f), pRight - 8f);
            }
            pY = random(py - chamberH * 0.42f, py + chamberH * 0.42f);
            fill(255, 190);
            noStroke();
            ellipse(px, pY, 5f, 5f);
        }
    }

    private void drawHeatEngine(float cx, float cy, float progress, float timeSec) {
        pushStyle();

        float resW = 320f;
        float resH = 80f;
        float hotY = cy - 220f;
        float coldY = cy + 220f;
        float engineR = 80f;

        // Focos Térmicos
        stroke(255, 220);
        strokeWeight(2.5f);
        noFill();
        rectMode(CENTER);
        rect(cx, hotY, resW, resH);
        rect(cx, coldY, resW, resH);

        // Motor Central
        ellipse(cx, cy, engineR * 2f, engineR * 2f);

        // Flujos
        drawAnimatedArrow(cx, hotY + resH * 0.5f, cx, cy - engineR, timeSec, 8);
        drawAnimatedArrow(cx, cy + engineR, cx, coldY - resH * 0.5f, timeSec, 8);
        drawAnimatedArrow(cx + engineR, cy, cx + engineR + 150f, cy, timeSec, 6);

        if (frameCount % 3 == 0) {
            particles.add(new Particle(cx + random(-20f, 20f), hotY + resH * 0.5f, 0, 3.5f, 4f));
            particles.add(new Particle(cx + random(-20f, 20f), cy + engineR, 0, 3.5f, 4f));
            particles.add(new Particle(cx + engineR, cy + random(-12f, 12f), 3.5f, 0, 4f));
        }

        popStyle();
    }

    private void drawEntropyStage(float cx, float cy, float currentBeat, float timeSec) {
        pushStyle();

        float levelW = 420f;
        float levelSpacing = 75f;
        float startY = cy + 2 * levelSpacing;

        stroke(255, 90);
        strokeWeight(2f);

        // Niveles cuánticos/energéticos
        for (int i = 0; i < 5; i++) {
            float ly = startY - i * levelSpacing;
            line(cx - levelW * 0.5f, ly, cx + levelW * 0.5f, ly);
        }

        // Cálculo del paso cada cuarto de beat (0.25 beats = 8 pasos totales de beat 6 a 8)
        int currentQuarterStep = floor((currentBeat - 6.0f) / 0.25f);
        currentQuarterStep = constrain(currentQuarterStep, 0, 7);

        // Actualización de estado en cada cuarto de beat
        if (currentQuarterStep != lastQuarterStep) {
            lastQuarterStep = currentQuarterStep;
            float stepEntropyRatio = currentQuarterStep / 7.0f; // Incremento de entropía (0.0 a 1.0)
            int maxLevelAccessible = floor(stepEntropyRatio * 4.99f);

            for (int i = 0; i < 12; i++) {
                // Distribución con mayor dispersión de niveles conforme sube el cuarto de beat
                if (random(1f) < (0.3f + 0.7f * stepEntropyRatio)) {
                    entropyParticleTargetLevels[i] = floor(random(0, maxLevelAccessible + 1));
                } else {
                    entropyParticleTargetLevels[i] = 0;
                }
                entropyParticleJitter[i] = random(-180f * stepEntropyRatio, 180f * stepEntropyRatio);
            }
        }

        // Animación suave entre estados de entropía
        float entropyLevelFactor = currentQuarterStep / 7.0f;
        for (int i = 0; i < 12; i++) {
            entropyParticleLevels[i] = lerp(entropyParticleLevels[i], entropyParticleTargetLevels[i], 0.12f);

            // Agitación térmica proporcional al nivel de entropía actual
            float thermalSpeed = 1.0f + entropyLevelFactor * 4.0f;
            float wave = sin(timeSec * thermalSpeed + i * 1.2f) * (8f + entropyLevelFactor * 25f);
            float px = cx + entropyParticleJitter[i] + wave;
            float py = startY - entropyParticleLevels[i] * levelSpacing;

            fill(255, 240);
            noStroke();
            ellipse(px, py, 14f, 14f);

            fill(255, 50);
            ellipse(px, py, 28f + entropyLevelFactor * 10f, 28f + entropyLevelFactor * 10f);
        }

        popStyle();
    }

    private void drawAnimatedArrow(float x1, float y1, float x2, float y2, float timeSec, int dashes) {
        stroke(255, 200);
        strokeWeight(2.5f);
        drawDashedLine(x1, y1, x2, y2, 8f);

        float t = (timeSec * 1.8f) % 1f;
        float ax = lerp(x1, x2, t);
        float ay = lerp(y1, y2, t);

        fill(255, 250);
        noStroke();
        ellipse(ax, ay, 9f, 9f);
    }

    private void drawDashedLine(float x1, float y1, float x2, float y2, float dashLen) {
        float d = dist(x1, y1, x2, y2);
        for (float i = 0; i < d; i += dashLen * 2) {
            float start = i / d;
            float end = min((i + dashLen) / d, 1.0f);
            line(lerp(x1, x2, start), lerp(y1, y2, start), lerp(x1, x2, end), lerp(y1, y2, end));
        }
    }

    private void updateAndDisplayParticles() {
        for (int i = particles.size() - 1; i >= 0; i--) {
            Particle pt = particles.get(i);
            pt.update();
            pt.display(this);
            if (pt.isDead()) particles.remove(i);
        }
    }

    class Particle {
        float x, y, vx, vy, alpha;
        float size;

        Particle(float x, float y, float vx, float vy, float size) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.alpha = 255;
            this.size = size;
        }

        void update() {
            x += vx;
            y += vy;
            alpha -= 7;
        }

        void display(PApplet p) {
            p.pushStyle();
            p.noStroke();
            p.fill(255, max(0, alpha));
            p.ellipse(x, y, size, size);
            p.popStyle();
        }

        boolean isDead() {
            return alpha <= 0;
        }
    }

    @Override
    public void mousePressed() {
        startTimeSec = millis() * 0.001f;
        particles.clear();
        lastQuarterStep = -1;
    }

    public static void main(String[] args) {
        PApplet.main("Classes.Eng1");
    }
}