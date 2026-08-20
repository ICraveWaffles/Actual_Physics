package Classes;

import processing.core.PApplet;
import java.util.ArrayList;

public class Rel2 extends PApplet {

    private final float BEAT_DURATION = 0.6f;
    private final float BEATS_PER_STAGE = 4f;
    private final float TOTAL_STAGES = 1f;
    private final float TOTAL_BEATS = BEATS_PER_STAGE * TOTAL_STAGES;

    private float globalTime = 0;
    private float startTimeSec = -1;

    private ArrayList<Particle> particles;
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
        particles = new ArrayList<>();
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

        float centerX = width * 0.55f;
        float centerY = height * 0.5f;

        float localProgress = (currentBeat % BEATS_PER_STAGE) / BEATS_PER_STAGE;

        drawMinkowskiStage(centerX, centerY, localProgress);

        if (alogo != null) {
            alogo.display(this, b, logoTransparency);
        }

        updateAndDisplayParticles();
        updateAndDisplayRandomEvents();
    }

    private void drawMinkowskiStage(float cx, float cy, float progress) {
        pushStyle();
        pushMatrix();

        float marginTop = height * 0.28f;
        float marginBottom = height * 0.12f;
        float usableH = height - marginTop - marginBottom;

        float usableW = min(width * 0.62f, usableH * 1.35f);

        float animX = cx - usableW * 0.5f;
        float animY = marginTop;

        clip(animX - 60, animY - 60, usableW + 120, usableH + 120);

        float originX = animX + usableW * 0.15f;
        float originY = animY + usableH * 0.70f;

        float axisLen = min(usableW * 0.65f, usableH * 0.58f);

        float v_over_c = 0.52f;
        float alpha = atan(v_over_c);
        float gamma = 1.0f / sqrt(1.0f - v_over_c * v_over_c);

        int gridSteps = 8;
        float stepSize = axisLen / gridSteps;

        float ctPrimeDx = sin(alpha);
        float ctPrimeDy = -cos(alpha);
        float xPrimeDx = cos(alpha);
        float xPrimeDy = -sin(alpha);

        stroke(255, 30 + sin(frameCount * 0.05f) * 10);
        strokeWeight(1f);

        for (int i = 0; i <= gridSteps; i++) {
            float distStep = i * stepSize;

            float startX1 = originX + xPrimeDx * distStep;
            float startY1 = originY + xPrimeDy * distStep;
            drawDashedLine(startX1, startY1, startX1 + ctPrimeDx * axisLen, startY1 + ctPrimeDy * axisLen, 4f);

            float startX2 = originX + ctPrimeDx * distStep;
            float startY2 = originY + ctPrimeDy * distStep;
            drawDashedLine(startX2, startY2, startX2 + xPrimeDx * axisLen, startY2 + xPrimeDy * axisLen, 4f);
        }

        float lightLen = (float) (axisLen * Math.sqrt(2));
        float lightX = originX + cos(-QUARTER_PI) * lightLen;
        float lightY = originY + sin(-QUARTER_PI) * lightLen;

        stroke(255, 180 + sin(frameCount * 0.1f) * 40);
        strokeWeight(2f);
        drawDashedLine(originX, originY, lightX, lightY, 4f);

        stroke(255, 240);
        strokeWeight(2.5f);
        drawArrow(originX, originY, originX, originY - axisLen * 1.05f);
        drawArrow(originX, originY, originX + axisLen * 1.05f, originY);

        stroke(255, 180);
        strokeWeight(2f);
        drawArrow(originX, originY, originX + ctPrimeDx * axisLen * 1.05f, originY + ctPrimeDy * axisLen * 1.05f);
        drawArrow(originX, originY, originX + xPrimeDx * axisLen * 1.05f, originY + xPrimeDy * axisLen * 1.05f);

        noFill();
        stroke(255, 140);
        strokeWeight(1.2f);
        arc(originX, originY, 60, 60, -HALF_PI, -HALF_PI + alpha);
        arc(originX, originY, 80, 80, -alpha, 0);



        int numEvents = 4;
        for (int i = 1; i <= numEvents; i++) {
            float eventT = i / (float) numEvents;

            float ev1X = originX;
            float ev1Y = originY - axisLen * eventT;

            float ev2X = originX + ctPrimeDx * axisLen * eventT;
            float ev2Y = originY + ctPrimeDy * axisLen * eventT;

            if (progress >= eventT) {
                fill(255);
                noStroke();
                ellipse(ev1X, ev1Y, 6, 6);
                ellipse(ev2X, ev2Y, 6, 6);

                stroke(255, 60);
                strokeWeight(1f);
                drawDashedLine(ev1X, ev1Y, ev2X, ev2Y, 3f);
            }
        }

        float curEv1Y = originY - axisLen * progress;
        float curEv2X = originX + ctPrimeDx * axisLen * progress;
        float curEv2Y = originY + ctPrimeDy * axisLen * progress;

        float trackPulse1 = 8f + sin(frameCount * 0.2f) * 2f;
        float trackPulse2 = 8f + cos(frameCount * 0.2f) * 2f;

        fill(255);
        noStroke();
        ellipse(originX, curEv1Y, trackPulse1, trackPulse1);
        ellipse(curEv2X, curEv2Y, trackPulse2, trackPulse2);

        if (frameCount % 2 == 0) {
            for (int k = 0; k < 2; k++) {
                particles.add(new Particle(originX, curEv1Y, random(-0.8f, 0.8f), random(-0.8f, 0.8f)));
                particles.add(new Particle(curEv2X, curEv2Y, random(-0.8f, 0.8f), random(-0.8f, 0.8f)));
            }
        }

        noClip();

        float clockY = originY + 52f;
        float clockRadius = 22f;

        float clockLineW = width * 0.9f;
        float clockLineX1 = cx - clockLineW * 0.5f;
        float clockLineX2 = cx + clockLineW * 0.5f;

        float clock1X = originX - 140f;
        float clock2StartX = clock1X + 180f;
        float clock2EndX = originX + axisLen * 1.35f;
        float clock2X = lerp(clock2StartX, clock2EndX, progress);

        stroke(255, 90);
        strokeWeight(1.5f);
        line(clockLineX1, clockY, clockLineX2, clockY);

        if (frameCount % 10 == 0) {
            if (random(1) < 0.5f) {
                float rx = originX + random(15, axisLen * 0.85f);
                float ry = originY - random(15, axisLen * 0.85f);
                randomEvents.add(new RandomEvent(rx, ry, false));
            } else {
                float rx = random(clockLineX1 + 20f, clockLineX2 - 20f);
                randomEvents.add(new RandomEvent(rx, clockY, true));
            }
        }


        drawClock(clock1X, clockY, clockRadius, progress * 4f * TWO_PI, progress, "");
        drawClock(clock2X, clockY, clockRadius, (progress / gamma) * 4f * TWO_PI, progress, "");

        popMatrix();
        popStyle();
    }

    private void drawClock(float x, float y, float r, float handAngle, float progress, String label) {
        pushStyle();

        float beatFraction = (progress * 4f) % 1f;
        float flashAlpha = 0;
        float beatScale = 1.0f + 0.18f * exp(-beatFraction * 9f);

        if (beatFraction < 0.14f) {
            flashAlpha = map(beatFraction, 0f, 0.14f, 255f, 0f);
        }

        pushMatrix();
        translate(x, y);
        scale(beatScale);

        if (flashAlpha > 10) {
            noFill();
            stroke(255, flashAlpha * 0.5f);
            strokeWeight(3f);
            ellipse(0, 0, r * 2.4f, r * 2.4f);
        }

        stroke(255);
        strokeWeight(2f);
        fill(flashAlpha);
        ellipse(0, 0, r * 2, r * 2);

        stroke(lerpColor(color(255), color(0), flashAlpha / 255f), 200);
        strokeWeight(1.5f);
        for (int a = 0; a < 12; a++) {
            float ang = a * (TWO_PI / 12f);
            float x1 = cos(ang) * (r - 4);
            float y1 = sin(ang) * (r - 4);
            float x2 = cos(ang) * r;
            float y2 = sin(ang) * r;
            line(x1, y1, x2, y2);
        }

        stroke(lerpColor(color(255), color(0), flashAlpha / 255f));
        strokeWeight(2f);
        float handX = cos(handAngle - HALF_PI) * (r - 5);
        float handY = sin(handAngle - HALF_PI) * (r - 5);
        line(0, 0, handX, handY);

        fill(lerpColor(color(255), color(0), flashAlpha / 255f));
        noStroke();
        ellipse(0, 0, 4, 4);

        popMatrix();

        fill(255, 180);
        textSize(11);
        textAlign(CENTER, TOP);
        text(label, x, y + r + 8);

        popStyle();
    }

    private void drawArrow(float x1, float y1, float x2, float y2) {
        line(x1, y1, x2, y2);
        float angle = atan2(y2 - y1, x2 - x1);
        float arrowSize = 8f;
        line(x2, y2, x2 - arrowSize * cos(angle - PI/6f), y2 - arrowSize * sin(angle - PI/6f));
        line(x2, y2, x2 - arrowSize * cos(angle + PI/6f), y2 - arrowSize * sin(angle + PI/6f));
    }

    private void drawDashedLine(float x1, float y1, float x2, float y2, float dashLen) {
        float d = dist(x1, y1, x2, y2);
        if (d <= 0) return;
        for (float i = 0; i < d; i += dashLen * 2) {
            float t1 = i / d;
            float t2 = min((i + dashLen) / d, 1.0f);
            line(lerp(x1, x2, t1), lerp(y1, y2, t1), lerp(x1, x2, t2), lerp(y1, y2, t2));
        }
    }

    private void updateAndDisplayRandomEvents() {
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
            Particle p = particles.get(i);
            p.update();
            p.display(this);
            if (p.isDead()) {
                particles.remove(i);
            }
        }
    }

    class RandomEvent {
        float x, y, life;
        boolean isOnClockLine;
        float maxRadius;

        RandomEvent(float x, float y, boolean isOnClockLine) {
            this.x = x;
            this.y = y;
            this.isOnClockLine = isOnClockLine;
            this.life = 1.0f;
            this.maxRadius = random(26f, 42f);
        }

        void update() {
            life -= 0.032f;
        }

        void display(PApplet p) {
            p.pushStyle();

            float progress = 1.0f - life;
            float easedProg = 1.0f - pow(1.0f - progress, 3);

            p.noFill();
            p.stroke(255, max(0, life * 255f));
            p.strokeWeight(1.5f);
            float r = easedProg * maxRadius;
            p.ellipse(x, y, r, r);

            p.stroke(255, max(0, (life - 0.2f) * 180f));
            p.ellipse(x, y, r * 0.55f, r * 0.55f);

            p.fill(255, max(0, life * 255f));
            p.noStroke();
            p.ellipse(x, y, 5f * life, 5f * life);

            if (isOnClockLine) {
                p.stroke(255, max(0, life * 150f));
                p.strokeWeight(1.2f);
                float beamH = easedProg * 110f;
                p.line(x, y, x, y - beamH);
            } else {
                p.stroke(255, max(0, life * 160f));
                p.strokeWeight(1.2f);
                float cLen = easedProg * 14f;
                p.line(x - cLen, y, x + cLen, y);
                p.line(x, y - cLen, x, y + cLen);
            }

            p.popStyle();
        }

        boolean isDead() {
            return life <= 0;
        }
    }

    class Particle {
        float x, y, vx, vy, alpha, size;

        Particle(float x, float y, float vx, float vy) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.alpha = 255;
            this.size = random(2.5f, 4.5f);
        }

        void update() {
            x += vx;
            y += vy;
            vx *= 0.95f;
            vy *= 0.95f;
            size *= 0.94f;
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
            return alpha <= 0 || size <= 0.2f;
        }
    }

    @Override
    public void mousePressed() {
        startTimeSec = millis() * 0.001f;
    }

    public static void main(String[] args) {
        PApplet.main("Classes.Rel2");
    }
}