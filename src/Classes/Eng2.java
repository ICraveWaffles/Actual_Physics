package Classes;

import processing.core.PApplet;
import java.util.ArrayList;

public class Eng2 extends PApplet {

    private final float BEAT_DURATION = 0.6f;
    private final float BEATS_PER_STAGE = 4f;
    private final float TOTAL_STAGES = 1f;
    private final float TOTAL_BEATS = BEATS_PER_STAGE * TOTAL_STAGES;

    private float globalTime = 0;
    private float startTimeSec = -1;

    private ArrayList<Particle> jetParticles;

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
        jetParticles = new ArrayList<>();
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
        globalTime = timeSec % (BEAT_DURATION * TOTAL_BEATS);
        float currentBeat = globalTime / BEAT_DURATION;

        float b = timeSec * (100f / 60f);
        transY = b % 1f;
        logoTransparency = (float) (255 * (-Math.pow(transY, 2) + 2 * (transY)));

        drawFluidMechanicsScene(currentBeat, timeSec);

        if (alogo != null) {
            alogo.display(this, b, logoTransparency);
        }

        updateAndDisplayParticles();
    }

    private float smoothStep(float edge0, float edge1, float x) {
        float t = constrain((x - edge0) / (edge1 - edge0), 0.0f, 1.0f);
        return t * t * (3.0f - 2.0f * t);
    }

    private void drawFluidMechanicsScene(float currentBeat, float timeSec) {
        pushStyle();
        strokeCap(ROUND);
        strokeJoin(ROUND);

        float pipeStartX = width * 0.12f;
        float pipeWidth1 = width * 0.28f;
        float transitionW = width * 0.12f;
        float pipeWidth2 = width * 0.18f;
        float pipeEndX = pipeStartX + pipeWidth1 + transitionW + pipeWidth2;

        float pipeY1 = height * 0.28f;
        float pipeY2 = height * 0.44f;

        float h1 = 160f;
        float h2 = 60f;

        noStroke();
        fill(255, 15);
        beginShape();
        vertex(pipeStartX, pipeY1 - h1 * 0.5f);
        vertex(pipeStartX + pipeWidth1, pipeY1 - h1 * 0.5f);
        vertex(pipeStartX + pipeWidth1 + transitionW, pipeY2 - h2 * 0.5f);
        vertex(pipeEndX, pipeY2 - h2 * 0.5f);
        vertex(pipeEndX, pipeY2 + h2 * 0.5f);
        vertex(pipeStartX + pipeWidth1 + transitionW, pipeY2 + h2 * 0.5f);
        vertex(pipeStartX + pipeWidth1, pipeY1 + h1 * 0.5f);
        vertex(pipeStartX, pipeY1 + h1 * 0.5f);
        endShape(CLOSE);

        stroke(255, 230);
        strokeWeight(3f);
        noFill();

        beginShape();
        vertex(pipeStartX, pipeY1 - h1 * 0.5f);
        vertex(pipeStartX + pipeWidth1, pipeY1 - h1 * 0.5f);
        vertex(pipeStartX + pipeWidth1 + transitionW, pipeY2 - h2 * 0.5f);
        vertex(pipeEndX, pipeY2 - h2 * 0.5f);
        endShape();

        beginShape();
        vertex(pipeStartX, pipeY1 + h1 * 0.5f);
        vertex(pipeStartX + pipeWidth1, pipeY1 + h1 * 0.5f);
        vertex(pipeStartX + pipeWidth1 + transitionW, pipeY2 + h2 * 0.5f);
        vertex(pipeEndX, pipeY2 + h2 * 0.5f);
        endShape();

        ellipse(pipeStartX, pipeY1, 30f, h1);
        ellipse(pipeEndX, pipeY2, 18f, h2);

        float support1X = pipeStartX + 12f;
        float support2X = pipeStartX + pipeWidth1 + transitionW + pipeWidth2 - 15f;
        float supportBottomY = height * 0.82f;

        strokeWeight(2.5f);
        stroke(255, 180);
        line(support1X, pipeY1 + h1 * 0.5f, support1X, supportBottomY);
        line(support2X, pipeY2 + h2 * 0.5f, support2X, supportBottomY);

        float arrowPhase = (timeSec * 1.8f) % 1.0f;
        float[] arrowYOffsets = {-h1 * 0.28f, 0f, h1 * 0.28f};

        for (float yOff : arrowYOffsets) {
            float startX = pipeStartX + 20f + arrowPhase * (pipeWidth1 * 0.5f);
            float arrowLength = 110f;
            drawVelocityArrow(startX, pipeY1 + yOff, arrowLength, 10f, 255, 180);

            if (startX + arrowLength < pipeStartX + pipeWidth1) {
                drawVelocityArrow(startX + pipeWidth1 * 0.45f, pipeY1 + yOff, arrowLength, 10f, 255, 180);
            }
        }

        float narrowArrowPhase = (timeSec * 4.5f) % 1.0f;
        float narrowX = pipeStartX + pipeWidth1 + transitionW + narrowArrowPhase * (pipeWidth2 * 0.45f);
        drawVelocityArrow(narrowX, pipeY2, 90f, 8f, 255, 200);

        float containerX = width * 0.76f;
        float containerY = height * 0.65f;
        float containerW = 210f;
        float containerH = 190f;

        float fillRatio = smoothStep(1.0f, 3.8f, currentBeat);
        float baseWaterY = (containerY + containerH * 0.5f) - (containerH * 0.60f * fillRatio);

        if (fillRatio > 0.05f) {
            noStroke();
            fill(255, 30);
            beginShape();
            float leftX = containerX - containerW * 0.48f;
            float rightX = containerX + containerW * 0.48f;

            for (float wx = leftX; wx <= rightX; wx += 4f) {
                float waveOffset = sin(wx * 0.06f + timeSec * 5f) * 4f
                        + cos(wx * 0.12f - timeSec * 3f) * 2f;
                vertex(wx, baseWaterY + waveOffset);
            }
            vertex(rightX, containerY + containerH * 0.5f - 2f);
            vertex(leftX, containerY + containerH * 0.5f - 2f);
            endShape(CLOSE);

            stroke(255, 210);
            strokeWeight(2.5f);
            noFill();
            beginShape();
            for (float wx = leftX; wx <= rightX; wx += 4f) {
                float waveOffset = sin(wx * 0.06f + timeSec * 5f) * 4f
                        + cos(wx * 0.12f - timeSec * 3f) * 2f;
                vertex(wx, baseWaterY + waveOffset);
            }
            endShape();
        }

        stroke(255, 230);
        strokeWeight(3f);
        noFill();
        rectMode(CENTER);
        rect(containerX, containerY, containerW, containerH, 2f);

        float blockW = 65f;
        float blockH = 95f;

        float oscWater = sin(containerX * 0.06f + timeSec * 5f) * 4f;
        float targetBlockY = baseWaterY - blockH * 0.22f + oscWater;
        float blockY = targetBlockY;

        if (fillRatio > 0.1f) {
            noStroke();
            fill(255, 10);
            rect(containerX, blockY, blockW + 10f, blockH + 10f, 6f);

            stroke(255, 240);
            strokeWeight(2.5f);
            fill(15, 15, 20);
            rect(containerX, blockY, blockW, blockH, 4f);

            float forceVectorLength = 75f;
            drawForceVector(containerX, blockY - blockH * 0.5f, containerX, blockY - blockH * 0.5f - forceVectorLength, 12f);
            drawForceVector(containerX, blockY + blockH * 0.5f, containerX, blockY + blockH * 0.5f + forceVectorLength, 12f);
        }

        if (currentBeat > 0.8f) {
            float jetStartX = pipeEndX;
            float targetX = containerX - blockW * 0.6f;
            float targetY = max(baseWaterY, containerY - containerH * 0.2f);

            noFill();
            for(int i = 0; i < 3; i++) {
                stroke(255, 70 - i * 20);
                strokeWeight(12f - i * 4f);
                beginShape();
                for (float t = 0; t <= 1.0f; t += 0.05f) {
                    float jx = lerp(jetStartX, targetX, t);
                    float jy = lerp(pipeY2 + (i-1)*2f, targetY, t) + sin(t * PI) * 30f;
                    vertex(jx, jy);
                }
                endShape();
            }

            if (frameCount % 2 == 0) {
                for(int i=0; i<2; i++){
                    jetParticles.add(new Particle(
                            targetX + random(-8f, 8f),
                            targetY + random(-2f, 5f),
                            random(-2.5f, 2.5f),
                            random(-3.5f, -1.0f),
                            random(2f, 5.5f)
                    ));
                }
            }
        }

        popStyle();
    }

    private void drawVelocityArrow(float x, float y, float len, float headSize, int alpha, int fillAlpha) {
        pushStyle();
        stroke(255, alpha);
        strokeWeight(2f);
        line(x, y, x + len, y);

        fill(255, fillAlpha);
        noStroke();
        triangle(x + len, y, x + len - headSize, y - headSize * 0.45f, x + len - headSize, y + headSize * 0.45f);
        popStyle();
    }

    private void drawForceVector(float x1, float y1, float x2, float y2, float headLen) {
        pushStyle();
        stroke(255, 250);
        strokeWeight(3f);
        line(x1, y1, x2, y2);

        float angle = atan2(y2 - y1, x2 - x1);

        fill(255, 250);
        noStroke();
        pushMatrix();
        translate(x2, y2);
        rotate(angle);
        triangle(0, 0, -headLen, -headLen * 0.4f, -headLen, headLen * 0.4f);
        popMatrix();

        popStyle();
    }

    private void updateAndDisplayParticles() {
        for (int i = jetParticles.size() - 1; i >= 0; i--) {
            Particle pt = jetParticles.get(i);
            pt.update();
            pt.display(this);
            if (pt.isDead()) jetParticles.remove(i);
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
            vy += 0.15f;
            alpha -= 7;
            size *= 0.98f;
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
        jetParticles.clear();
    }

    public static void main(String[] args) {
        PApplet.main("Classes.Eng2");
    }
}