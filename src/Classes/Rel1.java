package Classes;

import processing.core.PApplet;
import processing.core.PFont;
import java.util.ArrayList;

public class Rel1 extends PApplet {

    private final float BEAT_DURATION = 0.6f;
    private final float BEATS_PER_STAGE = 4f;
    private final float TOTAL_STAGES = 1f;
    private final float TOTAL_BEATS = BEATS_PER_STAGE * TOTAL_STAGES;

    private float globalTime = 0;
    private float startTimeSec = -1;

    private PFont fontTimesHuge;
    private PFont fontMainBold;

    private ArrayList<Particle> particles;
    private ArrayList<DecayParticle> decayParticles;

    public static Alogo alogo;
    float logoTransparency;
    float transY;

    @Override
    public void settings() {
        fullScreen();
        smooth(8);
    }

    @Override
    public void setup() {
        fontTimesHuge = createFont("Times New Roman Bold", 380, true);
        fontMainBold = createFont("Times New Roman Bold", 22, true);

        particles = new ArrayList<>();
        decayParticles = new ArrayList<>();
        noCursor();
        startTimeSec = millis() * 0.001f;

        float finalX = (width / 2f) + 865.3f;
        float finalY = (height / 2f) - 458.1f;
        float finalW = (height / 2f) - 381.75f;
        float finalH = (height / 2f) - 381.75f;

        alogo = new Alogo(finalX, finalY, finalW, finalH);
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

        float centerX = width * 0.5f;
        float centerY = height * 0.5f;

        int activeQ = constrain((int) (currentBeat / BEATS_PER_STAGE), 0, (int) TOTAL_STAGES - 1);
        float localProgress = (currentBeat % BEATS_PER_STAGE) / BEATS_PER_STAGE;

        drawQuestionStage(centerX, centerY, activeQ, localProgress, timeSec);

        alogo.display(this, b, logoTransparency);

        updateAndDisplayParticles();
    }

    private void drawQuestionStage(float cx, float cy, int questionIndex, float progress, float timeSec) {
        float currentX = cx;
        float stageAlpha = 255;

        float animW = width * 0.88f;
        float animH = height * 0.78f;
        float animX = currentX - animW * 0.5f;
        float animY = cy - animH * 0.5f;

        switch (questionIndex) {
            case 0: drawMuonAtmosphereRelativity(animX, animY, animW, animH, stageAlpha, progress); break;
            default: break;
        }
    }

    private void drawMuonAtmosphereRelativity(float x, float y, float w, float h, float alpha, float progress) {
        pushStyle();
        pushMatrix();
        clip(x, y, w, h);

        float graphWidth = w * 0.22f;
        float sceneStartX = x + graphWidth + 40f;
        float sceneWidth = w - (graphWidth + 40f);

        float centerAnimX = sceneStartX + sceneWidth * 0.55f;
        float centerAnimY = y + h * 0.50f;

        float sunR = 250f;
        float earthR = 40f;
        float moonR = 20f;
        float atmosR = earthR + 200f;

        float D = sceneWidth * 0.35f;
        float travelProgress = constrain(map(progress, 0.1f, 0.8f, 0f, 1f), 0f, 1f);

        float offset;
        if (travelProgress < 0.4f) {
            float t1 = travelProgress / 0.4f;
            offset = lerp(D, 0f, t1);
        } else {
            float t2 = (travelProgress - 0.4f) / 0.6f;
            offset = lerp(0f, -atmosR * 0.75f, t2);
        }

        float betaVal;
        if (travelProgress < 0.4f) {
            betaVal = 0.998f;
        } else {
            float atmosPenetration = map(travelProgress, 0.4f, 1.0f, 0f, 1f);
            betaVal = lerp(0.998f, 0.15f, atmosPenetration);
        }

        float muonX = centerAnimX;
        float muonY = centerAnimY;

        float atmosImpactX = centerAnimX + offset;
        float earthX = atmosImpactX + atmosR;
        float moonX = earthX + 300f;
        float moonY = centerAnimY - 35f;
        float sunX = (centerAnimX - sunR) - travelProgress * D * 1.3f;
        float bodyY = centerAnimY;

        fill(255, alpha * 0.15f);
        noStroke();
        ellipse(sunX, bodyY, sunR * 2.15f, sunR * 2.15f);
        fill(255, alpha);
        ellipse(sunX, bodyY, sunR * 2, sunR * 2);

        stroke(255, alpha * 0.9f);
        strokeWeight(2.5f);
        fill(0);
        ellipse(earthX, bodyY, earthR * 2, earthR * 2);

        stroke(255, alpha * 0.8f);
        strokeWeight(1.5f);
        fill(40, alpha * 0.8f);
        ellipse(moonX, moonY, moonR * 2, moonR * 2);

        noStroke();
        fill(200, 220, 255, alpha * 0.12f);
        ellipse(earthX, bodyY, atmosR * 2.08f, atmosR * 2.08f);
        fill(180, 180, 180, alpha * 0.30f);
        ellipse(earthX, bodyY, atmosR * 2, atmosR * 2);

        stroke(220, alpha * 0.6f);
        strokeWeight(1.5f);
        drawDashedArc(earthX, bodyY, atmosR * 2, alpha);

        stroke(255, alpha * 0.25f);
        strokeWeight(1.5f);
        line(sunX + sunR, bodyY, atmosImpactX, bodyY);

        if (travelProgress < 1.0f) {
            float lineStart = max(sunX + sunR, muonX - 60f);
            stroke(255, alpha * 0.8f);
            strokeWeight(3f);
            line(lineStart, muonY, muonX, muonY);

            if (frameCount % 2 == 0) {
                particles.add(new Particle(muonX - random(2, 10), muonY + random(-2, 2), -random(1, 3), random(-0.5f, 0.5f)));
            }

            fill(255, alpha * 0.3f);
            noStroke();
            ellipse(muonX, muonY, 24, 24);
            fill(255, alpha);
            ellipse(muonX, muonY, 12, 12);
        } else {
            float decayT = (progress - 0.8f) / 0.2f;
            float spread = decayT * 110f;

            if (decayT < 0.15f && frameCount % 2 == 0) {
                for (int p = 0; p < 3; p++) {
                    decayParticles.add(new DecayParticle(muonX, muonY, random(-3, 3), random(-3, 3)));
                }
            }

            float eX = muonX + cos(-THIRD_PI) * spread;
            float eY = muonY + sin(-THIRD_PI) * spread;
            stroke(255, alpha * 0.85f);
            strokeWeight(2f);
            line(muonX, muonY, eX, eY);
            fill(255, alpha);
            ellipse(eX, eY, 8, 8);

            float nuMuX = muonX + spread;
            float nuMuY = muonY;
            stroke(255, alpha * 0.5f);
            strokeWeight(1.5f);
            line(muonX, muonY, nuMuX, nuMuY);
            fill(255, alpha * 0.7f);
            ellipse(nuMuX, nuMuY, 6, 6);

            float nuEX = muonX + cos(THIRD_PI) * spread;
            float nuEY = muonY + sin(THIRD_PI) * spread;
            stroke(255, alpha * 0.5f);
            strokeWeight(1.5f);
            line(muonX, muonY, nuEX, nuEY);
            fill(255, alpha * 0.7f);
            ellipse(nuEX, nuEY, 6, 6);
        }

        noStroke();
        fill(0);
        rect(x, y - 10, graphWidth + 35f, h + 20);

        drawGammaGraph(x + 10f, y + 35f, graphWidth, h * 0.78f, alpha, betaVal);

        noClip();
        popMatrix();
        popStyle();
    }

    private void drawGammaGraph(float gx, float gy, float gw, float gh, float alpha, float beta) {
        pushStyle();

        float currentBeta = constrain(beta, 0f, 0.995f);
        float currentGamma = 1.0f / sqrt(1.0f - currentBeta * currentBeta);

        fill(255, alpha * 0.95f);
        textFont(fontMainBold);
        textSize(14);
        textAlign(CENTER, BOTTOM);
        text("β = " + (int)(beta * 100) + "%  |  γ ≈ " + nf(currentGamma, 1, 2), gx + gw * 0.45f, gy - 8);

        stroke(255, alpha * 0.12f);
        strokeWeight(1f);
        int gridStepsY = 10;
        int gridStepsX = 6;

        for (int i = 1; i <= gridStepsY; i++) {
            float yPos = map(i, 0, 10, gy + gh, gy + 15);
            line(gx, yPos, gx + gw * 0.9f, yPos);

            fill(255, alpha * 0.4f);
            textAlign(RIGHT, CENTER);
            textSize(10);
            text(String.valueOf(i), gx - 6, yPos);
        }

        for (int i = 1; i <= gridStepsX; i++) {
            float xPos = map(i, 0, gridStepsX, gx, gx + gw * 0.9f);
            line(xPos, gy, xPos, gy + gh);
        }

        stroke(255, alpha * 0.35f);
        for (float dashY = gy; dashY < gy + gh; dashY += 8) {
            line(gx + gw * 0.9f, dashY, gx + gw * 0.9f, dashY + 4);
        }

        noFill();
        stroke(255, alpha * 0.85f);
        strokeWeight(2f);
        beginShape();
        for (float px = 0; px <= gw * 0.90f; px += 0.5f) {
            float bVal = map(px, 0, gw * 0.9f, 0f, 0.998f);
            float gVal = 1.0f / sqrt(1.0f - bVal * bVal);
            float py = map(constrain(gVal, 1f, 10f), 0, 10, gy + gh, gy + 15);
            vertex(gx + px, py);
            if (gVal >= 10.0f) {
                vertex(gx + px, gy + 15);
                break;
            }
        }
        endShape();

        float pointX = gx + map(currentBeta, 0f, 0.995f, 0f, gw * 0.9f);
        float pointY = map(constrain(currentGamma, 1f, 10f), 0, 10, gy + gh, gy + 15);

        stroke(255, alpha * 0.45f);
        strokeWeight(1f);
        drawDashedLine(gx, pointY, pointX, pointY, 4f);
        drawDashedLine(pointX, gy + gh, pointX, pointY, 4f);

        fill(255, alpha * 0.25f);
        noStroke();
        ellipse(pointX, pointY, 18, 18);
        fill(255, alpha);
        ellipse(pointX, pointY, 10, 10);

        stroke(255, alpha * 0.9f);
        strokeWeight(2f);

        line(gx, gy + gh, gx, gy);
        line(gx, gy, gx - 4, gy + 8);
        line(gx, gy, gx + 4, gy + 8);

        line(gx, gy + gh, gx + gw, gy + gh);
        line(gx + gw, gy + gh, gx + gw - 8, gy + gh - 4);
        line(gx + gw, gy + gh, gx + gw - 8, gy + gh + 4);

        fill(255, alpha * 0.9f);
        textSize(14);
        textAlign(RIGHT, CENTER);
        text("γ", gx - 10, gy);
        textAlign(LEFT, CENTER);
        text("v", gx + gw + 6, gy + gh);

        textSize(11);
        textAlign(CENTER, TOP);
        text("0", gx, gy + gh + 6);
        text("c", gx + gw * 0.9f, gy + gh + 6);

        popStyle();
    }

    private void drawDashedLine(float x1, float y1, float x2, float y2, float dashLen) {
        float d = dist(x1, y1, x2, y2);
        for (float i = 0; i < d; i += dashLen * 2) {
            float t1 = i / d;
            float t2 = min((i + dashLen) / d, 1.0f);
            line(lerp(x1, x2, t1), lerp(y1, y2, t1), lerp(x1, x2, t2), lerp(y1, y2, t2));
        }
    }

    private void drawDashedArc(float cx, float cy, float diameter, float alpha) {
        stroke(255, alpha * 0.6f);
        strokeWeight(1.5f);
        noFill();
        int steps = 40;
        for (int i = 0; i < steps; i += 2) {
            float startAng = map(i, 0, steps, 0, TWO_PI);
            float endAng = map(i + 1, 0, steps, 0, TWO_PI);
            arc(cx, cy, diameter, diameter, startAng, endAng);
        }
    }

    private void updateAndDisplayParticles() {
        for (int i = particles.size() - 1; i >= 0; i--) {
            Particle p = particles.get(i);
            p.update();
            p.display(this);
            if (p.isDead()) {
                particles.remove(i);
            }
        }
        for (int i = decayParticles.size() - 1; i >= 0; i--) {
            DecayParticle dp = decayParticles.get(i);
            dp.update();
            dp.display(this, 255f);
            if (dp.isDead()) {
                decayParticles.remove(i);
            }
        }
    }

    class Particle {
        float x, y, vx, vy, alpha;

        Particle(float x, float y, float vx, float vy) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.alpha = 255;
        }

        void update() {
            x += vx;
            y += vy;
            alpha -= 8;
        }

        void display(PApplet p) {
            p.pushStyle();
            p.noStroke();
            p.fill(255, max(0, alpha));
            p.ellipse(x, y, 4, 4);
            p.popStyle();
        }

        boolean isDead() {
            return alpha <= 0;
        }
    }

    class DecayParticle {
        float x, y, vx, vy, alphaLife;

        DecayParticle(float x, float y, float vx, float vy) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.alphaLife = 255f;
        }

        void update() {
            x += vx;
            y += vy;
            alphaLife -= 6f;
        }

        void display(PApplet p, float baseAlpha) {
            p.pushStyle();
            p.noStroke();
            float curAlpha = max(0, alphaLife) * (baseAlpha / 255f);
            p.fill(255, curAlpha);
            p.ellipse(x, y, 5, 5);
            p.popStyle();
        }

        boolean isDead() {
            return alphaLife <= 0;
        }
    }

    @Override
    public void mousePressed() {
        startTimeSec = millis() * 0.001f;
    }

    public static void main(String[] args) {
        PApplet.main("Classes.Rel1");
    }
}