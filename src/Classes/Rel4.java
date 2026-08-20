package Classes;

import processing.core.PApplet;
import java.util.ArrayList;

public class Rel4 extends PApplet {

    private final float BEAT_DURATION = 0.6f;
    private final float BEATS_PER_STAGE = 4f;
    private final float TOTAL_STAGES = 1f;
    private final float TOTAL_BEATS = BEATS_PER_STAGE * TOTAL_STAGES;

    private float globalTime = 0;
    private float startTimeSec = -1;

    private ArrayList<BackgroundPoint> bgPoints;
    private ArrayList<Particle> particles;
    private ArrayList<RandomEvent> randomEvents;
    private ArrayList<HorizonParticle> horizonParticles;

    public static Alogo alogo;
    float logoTransparency;
    float transY;

    private float theta1 = 0;
    private float theta2 = 0;

    @Override
    public void settings() {
        fullScreen();
        smooth(8);
    }

    @Override
    public void setup() {
        particles = new ArrayList<>();
        randomEvents = new ArrayList<>();
        bgPoints = new ArrayList<>();
        horizonParticles = new ArrayList<>();
        noCursor();
        startTimeSec = millis() * 0.001f;

        float step = 22f;
        for (float x = -width; x < width * 2; x += step) {
            for (float y = 0; y < height; y += step) {
                float jx = x + random(-4f, 4f);
                float jy = y + random(-4f, 4f);
                bgPoints.add(new BackgroundPoint(jx, jy));
            }
        }

        for (int i = 0; i < 120; i++) {
            horizonParticles.add(new HorizonParticle(100f));
        }

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

        drawGeneralRelativityStage(centerX, centerY, localProgress, timeSec);

        if (alogo != null) {
            alogo.display(this, b, logoTransparency);
        }

        updateAndDisplayParticles();
        updateAndDisplayRandomEvents();
    }

    private void drawGeneralRelativityStage(float cx, float cy, float progress, float timeSec) {
        pushStyle();

        float bhSpeed = 110f;
        float bhX_world = cx + timeSec * bhSpeed;
        float bhY_world = cy;

        float camX = bhX_world - cx;

        pushMatrix();
        translate(-camX, 0);

        float r_event = 100f;
        float r_einstein = 125f;

        strokeWeight(1f);
        for (BackgroundPoint pt : bgPoints) {
            float worldPtX = pt.x0;
            while (worldPtX < camX - 100) worldPtX += width * 2;
            while (worldPtX > camX + width + 100) worldPtX -= width * 2;

            float dx = worldPtX - bhX_world;
            float dy = pt.y0 - bhY_world;
            float distSq = dx * dx + dy * dy;
            float distSrc = sqrt(distSq);

            if (distSrc < 1f) continue;

            float r_img = 0.5f * (distSrc + sqrt(distSq + 4f * r_einstein * r_einstein));

            if (r_img < r_event) continue;

            float factor = r_img / distSrc;
            float x_lens = bhX_world + dx * factor;
            float y_lens = bhY_world + dy * factor;

            float distToEinstein = abs(r_img - r_einstein);
            float mag = 1.0f + (r_einstein * 14f) / (distToEinstein + 18f);

            stroke(255, min(255, pt.baseBrightness * mag * 0.9f));
            if (mag > 2.2f) {
                strokeWeight(2.0f);
            } else {
                strokeWeight(1.0f);
            }
            point(x_lens, y_lens);
        }

        float a = 460f;
        float e = 0.78f;

        float p1 = a * (1f - e * e);
        float r1 = p1 / (1f + e * cos(theta1));
        theta1 += (2800f / (r1 * r1));

        float p2 = a * (1f - e * e);
        float r2 = p2 / (1f + e * cos(theta2));
        theta2 += (2800f / (r2 * r2));

        drawEllipticalOrbitAndStar(bhX_world, bhY_world, a, e, theta1, 0f, r_einstein, r_event, true);
        drawEllipticalOrbitAndStar(bhX_world, bhY_world, a, e, theta2, PI, r_einstein, r_event, false);

        fill(0);
        noStroke();
        ellipse(bhX_world, bhY_world, r_event * 2f, r_event * 2f);

        noFill();
        float pulse = sin(frameCount * 0.15f) * 4f;
        stroke(255, 240);
        strokeWeight(2.5f);
        ellipse(bhX_world, bhY_world, (r_einstein + pulse) * 2f, (r_einstein + pulse) * 2f);

        stroke(255, 80);
        strokeWeight(1f);
        ellipse(bhX_world, bhY_world, (r_einstein + 18f) * 2f, (r_einstein + 18f) * 2f);

        for (HorizonParticle hp : horizonParticles) {
            hp.update();
            hp.display(this, bhX_world, bhY_world);
        }

        if (frameCount % 1 == 0) {
            float ang = random(TWO_PI);
            float rad = r_event + random(-2f, 8f);
            particles.add(new Particle(bhX_world + rad * cos(ang), bhY_world + rad * sin(ang), -sin(ang) * random(3f, 8f), cos(ang) * random(3f, 8f), random(2f, 5f)));
        }

        popMatrix();
        popStyle();
    }

    private void drawEllipticalOrbitAndStar(float bhX, float bhY, float a, float e, float theta, float precAngle, float r_einstein, float r_event, boolean isInnerStar) {
        noFill();
        stroke(255, 130);
        strokeWeight(1.5f);

        pushMatrix();
        translate(bhX, bhY);
        rotate(precAngle);
        beginShape();
        for (float ang = 0; ang < TWO_PI; ang += 0.04f) {
            float r = (a * (1f - e * e)) / (1f + e * cos(ang));
            float ox = r * cos(ang);
            float oy = r * sin(ang);
            vertex(ox, oy);
        }
        endShape(CLOSE);
        popMatrix();

        float r_star = (a * (1f - e * e)) / (1f + e * cos(theta));
        float x_orbit = r_star * cos(theta);
        float y_orbit = r_star * sin(theta);

        float x_world = bhX + x_orbit * cos(precAngle) - y_orbit * sin(precAngle);
        float y_world = bhY + x_orbit * sin(precAngle) + y_orbit * cos(precAngle);

        float dx = x_world - bhX;
        float dy = y_world - bhY;
        float distSrc = sqrt(dx * dx + dy * dy);

        float r_img1 = 0.5f * (distSrc + sqrt(distSrc * distSrc + 4f * r_einstein * r_einstein));
        float factor1 = r_img1 / max(1f, distSrc);

        float finalX = bhX + dx * factor1;
        float finalY = bhY + dy * factor1;

        float speedFactor = max(1f, 350f / distSrc);
        for (int i = 0; i < (int) speedFactor; i++) {
            particles.add(new Particle(finalX + random(-3f, 3f), finalY + random(-3f, 3f), random(-0.8f, 0.8f), random(-0.8f, 0.8f), isInnerStar ? 5f : 4f));
        }

        if (r_img1 > r_event) {
            noStroke();
            fill(255, 70);
            ellipse(finalX, finalY, isInnerStar ? 24f : 18f, isInnerStar ? 24f : 18f);
            fill(255, 255);
            ellipse(finalX, finalY, isInnerStar ? 10f : 8f, isInnerStar ? 10f : 8f);
        }

        if (distSrc < r_einstein * 1.4f) {
            float r_img2 = 0.5f * (-distSrc + sqrt(distSrc * distSrc + 4f * r_einstein * r_einstein));
            if (r_img2 > r_event) {
                float factor2 = -r_img2 / max(1f, distSrc);
                float secX = bhX + dx * factor2;
                float secY = bhY + dy * factor2;

                fill(255, 220);
                ellipse(secX, secY, 6f, 6f);
            }
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

    private void updateAndDisplayParticles() {
        for (int i = particles.size() - 1; i >= 0; i--) {
            Particle pt = particles.get(i);
            pt.update();
            pt.display(this);
            if (pt.isDead()) particles.remove(i);
        }
    }

    class HorizonParticle {
        float angle;
        float radius;
        float speed;
        float size;
        float alpha;

        HorizonParticle(float baseR) {
            this.angle = random(TWO_PI);
            this.radius = baseR + random(-4f, 4f);
            this.speed = random(0.06f, 0.14f);
            this.size = random(2f, 4.5f);
            this.alpha = random(160, 255);
        }

        void update() {
            angle += speed;
        }

        void display(PApplet p, float cx, float cy) {
            float x = cx + radius * cos(angle);
            float y = cy + radius * sin(angle);
            p.pushStyle();
            p.noStroke();
            p.fill(255, alpha);
            p.ellipse(x, y, size, size);
            p.popStyle();
        }
    }

    class BackgroundPoint {
        float x0, y0;
        float baseBrightness;

        BackgroundPoint(float x0, float y0) {
            this.x0 = x0;
            this.y0 = y0;
            this.baseBrightness = random(90f, 180f);
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
            alpha -= 9;
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
        randomEvents.clear();
    }

    public static void main(String[] args) {
        PApplet.main("Classes.Rel4");
    }
}