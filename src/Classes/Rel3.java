package Classes;

import processing.core.PApplet;
import java.util.ArrayList;

public class Rel3 extends PApplet {

    private final float BEAT_DURATION = 0.6f;
    private final float BEATS_PER_STAGE = 4f;
    private final float TOTAL_STAGES = 1f;
    private final float TOTAL_BEATS = BEATS_PER_STAGE * TOTAL_STAGES;

    private float globalTime = 0;
    private float startTimeSec = -1;

    private ArrayList<Particle> particlesRel;
    private ArrayList<Particle> particlesClass;
    private ArrayList<RandomEvent> randomEvents;

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
        particlesRel = new ArrayList<>();
        particlesClass = new ArrayList<>();
        randomEvents = new ArrayList<>();
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

        float localProgress = (currentBeat % BEATS_PER_STAGE) / BEATS_PER_STAGE;

        drawAccelerationStage(centerX, centerY, localProgress, timeSec);

        if (alogo != null) {
            alogo.display(this, b, logoTransparency);
        }

        updateAndDisplayRandomEvents();
    }

    private void drawAccelerationStage(float cx, float cy, float progress, float timeSec) {
        pushStyle();

        float marginTop = height * 0.15f;
        float marginBottom = height * 0.15f;
        float usableH = height - marginTop - marginBottom;

        float T = pow(progress * 3.2f, 2.6f);

        float gamma = sqrt(1.0f + T * T);
        float v_rel = T / gamma;
        float K_rel = gamma - 1.0f;

        float worldStartX = cx - width * 0.5f;
        float scaleFactor = width * 1.8f;

        float x_rel_world = worldStartX + (sqrt(1.0f + T * T) - 1.0f) * (scaleFactor * 0.25f);
        float x_clas_world = worldStartX + (0.5f * T * T) * (scaleFactor * 0.12f);

        float y_rel = marginTop + usableH * 0.28f;
        float y_clas = marginTop + usableH * 0.72f;

        float camX = x_rel_world - width * 0.5f;

        pushMatrix();
        translate(-camX, 0);

        stroke(255, 30);
        strokeWeight(1f);
        drawDashedLine(worldStartX - 3000f, y_rel, x_rel_world + 4000f, y_rel, 6f);
        drawDashedLine(worldStartX - 3000f, y_clas, x_clas_world + 4000f, y_clas, 6f);

        for (float gx = worldStartX - 2000f; gx < x_clas_world + 4000f; gx += 200f) {
            stroke(255, 15);
            line(gx, y_rel - 15, gx, y_clas + 15);
        }

        float cLimitWorldX = worldStartX + scaleFactor * 0.75f;
        float cGlow = 180 + sin(frameCount * 0.12f) * 40;
        stroke(255, cGlow);
        strokeWeight(2f);
        drawDashedLine(cLimitWorldX, y_rel - 60f, cLimitWorldX, y_clas + 60f, 4f);

        fill(255, 200);
        textSize(12);
        textAlign(CENTER, TOP);
        text("LÍMITE C", cLimitWorldX, y_clas + 70f);

        float m0 = 10f;
        float r_clas = m0 * 2.0f;

        if (frameCount % 3 == 0) {
            particlesClass.add(new Particle(x_clas_world, y_clas, random(-0.2f, 0.2f), random(-0.2f, 0.2f), m0 * 0.5f));
        }

        noStroke();
        fill(255, 40);
        ellipse(x_clas_world, y_clas, r_clas * 1.5f, r_clas * 1.5f);
        fill(255, 240);
        ellipse(x_clas_world, y_clas, r_clas, r_clas);

        float r_rel = m0 * 2.0f * (1.0f + log(gamma + 1.0f) * 1.8f);

        float A_tremble = min(35.0f, 1.5f * K_rel);
        float y_tremble = A_tremble * sin(frameCount * 0.6f * sqrt(gamma));
        float x_tremble = (A_tremble * 0.4f) * cos(frameCount * 0.8f * sqrt(gamma));

        float finalRelX = x_rel_world + x_tremble;
        float finalRelY = y_rel + y_tremble;

        if (frameCount % 2 == 0) {
            particlesRel.add(new Particle(finalRelX, finalRelY, random(-0.8f, 0.8f) * K_rel, random(-0.8f, 0.8f) * K_rel, m0 * 0.4f * log(gamma + 1f)));
        }

        noStroke();
        fill(255, max(10, 80 - gamma));
        ellipse(finalRelX, finalRelY, r_rel * 2.5f, r_rel * 2.5f);
        fill(255, 60);
        ellipse(finalRelX, finalRelY, r_rel * 1.4f, r_rel * 1.4f);
        fill(255, 250);
        ellipse(finalRelX, finalRelY, r_rel, r_rel);

        updateAndDisplayParticles(this);

        popMatrix();

        popStyle();
    }

    private void drawDashedLine(float x1, float y1, float x2, float y2, float dashLen) {
        float d = dist(x1, y1, x2, y2);
        for (float i = 0; i < d; i += dashLen * 2) {
            float start = i / d;
            float end = min((i + dashLen) / d, 1.0f);
            line(lerp(x1, x2, start), lerp(y1, y2, start), lerp(x1, x2, end), lerp(y1, y2, end));
        }
    }

    private void updateAndDisplayRandomEvents() {
        if (frameCount % 12 == 0) {
            randomEvents.add(new RandomEvent(random(width * 0.1f, width * 0.9f), random(height * 0.1f, height * 0.9f), false));
        }
        for (int i = randomEvents.size() - 1; i >= 0; i--) {
            RandomEvent re = randomEvents.get(i);
            re.update();
            re.display(this);
            if (re.isDead()) {
                randomEvents.remove(i);
            }
        }
    }

    private void updateAndDisplayParticles(PApplet p) {
        for (int i = particlesRel.size() - 1; i >= 0; i--) {
            Particle pt = particlesRel.get(i);
            pt.update();
            pt.display(p);
            if (pt.isDead()) particlesRel.remove(i);
        }
        for (int i = particlesClass.size() - 1; i >= 0; i--) {
            Particle pt = particlesClass.get(i);
            pt.update();
            pt.display(p);
            if (pt.isDead()) particlesClass.remove(i);
        }
    }

    class RandomEvent {
        float x, y, life;
        boolean isOnClockLine;

        RandomEvent(float x, float y, boolean isOnClockLine) {
            this.x = x;
            this.y = y;
            this.isOnClockLine = isOnClockLine;
            this.life = 1.0f;
        }

        void update() {
            life -= 0.038f;
        }

        void display(PApplet p) {
            p.pushStyle();
            p.noFill();
            p.stroke(255, max(0, life * 255f));
            p.strokeWeight(1.5f);
            float r = (1.0f - life) * 32f;
            p.ellipse(x, y, r, r);
            p.fill(255, max(0, life * 255f));
            p.noStroke();
            p.ellipse(x, y, 5, 5);
            p.popStyle();
        }

        boolean isDead() {
            return life <= 0;
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
            alpha -= 8;
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
        particlesRel.clear();
        particlesClass.clear();
        randomEvents.clear();
    }

    public static void main(String[] args) {
        PApplet.main("Classes.Rel3");
    }
}