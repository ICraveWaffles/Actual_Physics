package Classes;

import processing.core.PApplet;
import processing.core.PFont;
import java.util.ArrayList;

public class XC extends PApplet {

    private final float BEAT_DURATION = 0.6f;
    private final float TOTAL_BEATS = 5f;
    private float globalTime = 0;
    private float startTimeSec = -1;

    private PFont fontTimesHuge;
    private PFont fontMainBold;

    private ArrayList<Particle> particles;
    private ArrayList<DecayParticle> decayParticles;
    private int lastTriggeredStep = -1;

    public static Xlogo xlogo;
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

        xlogo = new Xlogo(finalX, finalY, finalW, finalH);
    }

    @Override
    public void draw() {
        background(255);

        if (startTimeSec < 0) {
            startTimeSec = millis() * 0.001f;
        }

        float timeSec = (millis() * 0.001f) - startTimeSec;
        globalTime = timeSec % (BEAT_DURATION * TOTAL_BEATS);
        float currentBeat = globalTime / BEAT_DURATION;

        float b = timeSec * (5/6f);
        transY = b % 1f;
        logoTransparency = (float) (255 * (-Math.pow(transY, 2) + 2 * (transY)));

        float centerX = width * 0.5f;
        float centerY = height * 0.5f;

        int activeQ = constrain((int) currentBeat, 0, 4);
        float localProgress = currentBeat % 1.0f;

        drawQuestionStage(centerX, centerY, activeQ, localProgress, timeSec);

        xlogo.display(this, b, logoTransparency);

        updateAndDisplayParticles();
    }

    private void drawQuestionStage(float cx, float cy, int questionIndex, float progress, float timeSec) {
        float targetX = cx;
        boolean entering = false, exiting = false;

        if (progress < 0.15f) {
            entering = true;
            float entryT = progress / 0.15f;
            float eased = 1.0f - pow(1.0f - entryT, 3);
            targetX = lerp(width * 1.3f, cx, eased);
        } else if (progress > 0.85f) {
            exiting = true;
            float exitT = (progress - 0.85f) / 0.15f;
            float eased = exitT * exitT * exitT;
            targetX = lerp(cx, -width * 0.3f, eased);
        }

        float currentX = targetX;
        float stageAlpha = 255;
        if (entering) stageAlpha = constrain(map(progress, 0f, 0.15f, 0f, 255f), 0, 255);
        if (exiting) stageAlpha = constrain(map(progress, 0.85f, 1f, 255f, 0f), 0, 255);

        pushMatrix();
        translate(currentX, cy);
        fill(0, stageAlpha * 0.08f);
        textFont(fontTimesHuge);
        textAlign(CENTER, CENTER);
        text(String.valueOf(questionIndex + 1), 0, 0);
        popMatrix();

        if (stageAlpha < 4) return;

        float animW = width * 0.75f;
        float animH = height * 0.75f;
        float animX = currentX - animW * 0.5f;
        float animY = cy - animH * 0.5f;

        switch (questionIndex) {
            case 0: drawEarthMoonSystem(animX, animY, animW, animH, stageAlpha, timeSec); break;
            case 1: drawElectricCharges(animX, animY, animW, animH, stageAlpha, timeSec); break;
            case 2: drawSquareToCircleLoop(animX, animY, animW, animH, stageAlpha, timeSec); break;
            case 3: drawLightRefractionLayers(animX, animY, animW, animH, stageAlpha, timeSec); break;
            case 4: drawDecayChainGraph(animX, animY, animW, animH, stageAlpha, progress); break;
            default: break;
        }
    }

    private void drawEarthMoonSystem(float x, float y, float w, float h, float alpha, float t) {
        pushStyle();
        pushMatrix();
        clip(x, y, w, h);

        float earthX = x + w * 0.20f;
        float moonX = x + w * 0.80f;
        float sysY = y + h * 0.50f;

        float earthR = 55f;
        float moonR = 20f;

        stroke(160, alpha);
        strokeWeight(2f);
        line(earthX, sysY, moonX, sysY);

        float dimY = sysY + 90f;
        stroke(100, alpha);
        strokeWeight(1.5f);
        line(earthX, dimY, moonX, dimY);
        line(earthX, sysY + earthR + 5, earthX, dimY + 8);
        line(moonX, sysY + moonR + 5, moonX, dimY + 8);

        fill(30, alpha);
        stroke(0, alpha);
        strokeWeight(2.5f);
        ellipse(earthX, sysY, earthR * 2, earthR * 2);

        fill(210, alpha);
        stroke(0, alpha);
        strokeWeight(2.5f);
        ellipse(moonX, sysY, moonR * 2, moonR * 2);

        float pointPX = lerp(moonX, earthX, 0.15f);

        stroke(0, alpha);
        strokeWeight(3f);
        line(pointPX, sysY - 16, pointPX, sysY + 16);

        stroke(80, alpha);
        strokeWeight(1.5f);
        line(pointPX, sysY + 35, moonX, sysY + 35);

        noClip();
        popMatrix();
        popStyle();
    }

    private void drawElectricCharges(float x, float y, float w, float h, float alpha, float t) {
        pushStyle();
        pushMatrix();
        clip(x, y, w, h);

        float rectW = w * 0.58f;
        float rectH = h * 0.48f;
        float rectX = x + w * 0.21f;
        float rectY = y + h * 0.26f;

        float qPos1X = rectX;
        float qPos1Y = rectY + rectH;
        float qPos2X = rectX + rectW;
        float qPos2Y = rectY + rectH;
        float pointBX = rectX + rectW;
        float pointBY = rectY;
        float pointAX = rectX;
        float pointAY = rectY;

        stroke(160, alpha);
        strokeWeight(2f);
        fill(248, alpha * 0.5f);
        rect(rectX, rectY, rectW, rectH);

        drawChargeNode(qPos1X, qPos1Y, 24, true, alpha);
        drawChargeNode(qPos2X, qPos2Y, 20, false, alpha);

        fill(0, alpha);
        ellipse(pointAX, pointAY, 10, 10);
        ellipse(pointBX, pointBY, 10, 10);

        stroke(100, alpha);
        strokeWeight(1.5f);
        line(qPos1X, qPos1Y + 35, qPos2X, qPos2Y + 35);

        drawVector(pointBX, pointBY, -45, -35, color(80, alpha));
        drawVector(pointBX, pointBY, 0, 55, color(120, alpha));
        drawVector(pointBX, pointBY, -45, 20, color(0, alpha));

        noClip();
        popMatrix();
        popStyle();
    }

    private void drawSquareToCircleLoop(float x, float y, float w, float h, float alpha, float t) {
        pushStyle();
        pushMatrix();
        clip(x, y, w, h);

        float cx = x + w * 0.50f;
        float cy = y + h * 0.50f;
        float loopSize = h * 0.30f;

        float leftCx = cx - w * 0.25f;
        float rightCx = cx + w * 0.25f;

        float[] layerWeights = {14f, 9f, 3f};
        int[] layerColors = {
                color(210, alpha),
                color(120, alpha),
                color(20, alpha)
        };

        float fieldSpacing = 35f;
        float fieldSize = 8f;
        stroke(0, alpha * 0.65f);
        strokeWeight(2f);
        strokeCap(ROUND);
        for (float fx = leftCx - loopSize * 0.7f; fx <= leftCx + loopSize * 0.7f; fx += fieldSpacing) {
            for (float fy = cy - loopSize * 0.7f; fy <= cy + loopSize * 0.7f; fy += fieldSpacing) {
                if (dist(leftCx, cy, fx, fy) < loopSize * 0.75f) {
                    line(fx - fieldSize, fy - fieldSize, fx + fieldSize, fy + fieldSize);
                    line(fx - fieldSize, fy + fieldSize, fx + fieldSize, fy - fieldSize);
                }
            }
        }

        int numPoints = 100;
        for (int l = 0; l < 3; l++) {
            stroke(layerColors[l]);
            strokeWeight(layerWeights[l]);
            strokeCap(SQUARE);
            noFill();

            beginShape();
            for (int i = 0; i <= numPoints; i++) {
                float theta = map(i, 0, numPoints, 0, TWO_PI);
                float currX = leftCx + loopSize * cos(theta);
                float currY = cy + loopSize * sin(theta);
                vertex(currX, currY);
            }
            endShape();
        }

        int numArrows = 3;
        for (int i = 0; i < numArrows; i++) {
            float angleOffset = (TWO_PI / numArrows) * i;
            float currentAngle = (t * TWO_PI * 0.7f + angleOffset) % TWO_PI;
            if (currentAngle < 0) currentAngle += TWO_PI;

            float theta = currentAngle;
            float px = leftCx + loopSize * cos(theta);
            float py = cy + loopSize * sin(theta);
            float tang = theta + HALF_PI;

            pushMatrix();
            translate(px, py);
            rotate(tang);
            fill(20, alpha);
            noStroke();
            triangle(12, 0, -10, 8, -10, -8);
            popMatrix();
        }

        noStroke();
        fill(0, alpha * 0.75f);
        for (float fx = rightCx - loopSize * 0.7f; fx <= rightCx + loopSize * 0.7f; fx += fieldSpacing) {
            for (float fy = cy - loopSize * 0.7f; fy <= cy + loopSize * 0.7f; fy += fieldSpacing) {
                if (abs(fx - rightCx) < loopSize * 0.75f && abs(fy - cy) < loopSize * 0.75f) {
                    ellipse(fx, fy, 8, 8);
                }
            }
        }

        for (int l = 0; l < 3; l++) {
            stroke(layerColors[l]);
            strokeWeight(layerWeights[l]);
            strokeCap(SQUARE);
            noFill();

            beginShape();
            for (int i = 0; i <= numPoints; i++) {
                float theta = map(i, 0, numPoints, 0, TWO_PI);
                float absCos = abs(cos(theta));
                float absSin = abs(sin(theta));
                float maxC = max(absCos, absSin);
                float squareX = (loopSize * cos(theta)) / maxC;
                float squareY = (loopSize * sin(theta)) / maxC;

                float currX = rightCx + squareX;
                float currY = cy + squareY;
                vertex(currX, currY);
            }
            endShape();
        }

        for (int i = 0; i < numArrows; i++) {
            float angleOffset = (TWO_PI / numArrows) * i;
            float currentAngle = (t * TWO_PI * 0.7f + angleOffset) % TWO_PI;
            if (currentAngle < 0) currentAngle += TWO_PI;

            float theta = currentAngle;
            float delta = 0.01f;
            float theta2 = theta + delta;

            float absCos = abs(cos(theta));
            float absSin = abs(sin(theta));
            float maxC = max(absCos, absSin);
            float px = rightCx + (loopSize * cos(theta)) / maxC;
            float py = cy + (loopSize * sin(theta)) / maxC;

            float absCos2 = abs(cos(theta2));
            float absSin2 = abs(sin(theta2));
            float maxC2 = max(absCos2, absSin2);
            float x2 = rightCx + (loopSize * cos(theta2)) / maxC2;
            float y2 = cy + (loopSize * sin(theta2)) / maxC2;

            float tang = atan2(y2 - py, x2 - px);

            pushMatrix();
            translate(px, py);
            rotate(tang);
            fill(20, alpha);
            noStroke();
            triangle(12, 0, -10, 8, -10, -8);
            popMatrix();
        }

        noClip();
        popMatrix();
        popStyle();
    }

    private void drawLightRefractionLayers(float x, float y, float w, float h, float alpha, float t) {
        pushStyle();
        pushMatrix();
        clip(x, y, w, h);

        float cx = x + w * 0.50f;
        float cy = y + h * 0.50f;
        float boxW = w * 0.65f;
        float boxH = h * 0.60f;
        float halfW = boxW * 0.5f;
        float halfH = boxH * 0.5f;

        float yTop = cy - halfH * 0.33f;
        float yBot = cy + halfH * 0.33f;
        float slabH = yBot - yTop;

        noStroke();
        fill(255, alpha);
        rect(cx - halfW, cy - halfH, boxW, yTop - (cy - halfH));
        fill(235, alpha * 0.8f);
        rect(cx - halfW, yTop, boxW, slabH);
        fill(255, alpha);
        rect(cx - halfW, yBot, boxW, (cy + halfH) - yBot);

        stroke(0, alpha);
        strokeWeight(2f);
        noFill();
        rect(cx - halfW, cy - halfH, boxW, boxH);

        stroke(0, alpha * 0.6f);
        strokeWeight(1.5f);
        line(cx - halfW, yTop, cx + halfW, yTop);
        line(cx - halfW, yBot, cx + halfW, yBot);

        drawDashedNormal(cx, yTop - 35, cx, yTop + 35, alpha);
        drawDashedNormal(cx, yBot - 35, cx, yBot + 35, alpha);

        float n_air = 1.0f;
        float n_slab = 1.5f;

        float progress = (t * 0.5f) % 1.0f;
        float theta1 = map(progress, 0, 1, radians(15), radians(50));

        float sinTheta2 = (n_air / n_slab) * sin(theta1);
        float theta2 = asin(sinTheta2);

        float maxIncL = (yTop - (cy - halfH) - 10) / cos(theta1);
        float maxOutL = maxIncL;

        float hitTopX = cx;
        float hitTopY = yTop;

        float incStartX = hitTopX - maxIncL * sin(theta1);
        float incStartY = hitTopY - maxIncL * cos(theta1);

        float hitBotX = hitTopX + slabH * tan(theta2);
        float hitBotY = yBot;

        float outEndX = hitBotX + maxOutL * sin(theta1);
        float outEndY = hitBotY + maxOutL * cos(theta1);

        stroke(0, alpha * 0.2f);
        strokeWeight(1.5f);
        line(incStartX, incStartY, hitTopX, hitTopY);
        line(hitTopX, hitTopY, hitBotX, hitBotY);
        line(hitBotX, hitBotY, outEndX, outEndY);

        float reflEndX = hitTopX + maxIncL * sin(theta1);
        float reflEndY = hitTopY - maxIncL * cos(theta1);
        line(hitTopX, hitTopY, reflEndX, reflEndY);

        drawAngleArc(cx, yTop, 45, -HALF_PI, -HALF_PI + theta1, alpha);
        drawAngleArc(cx, yTop, 40, HALF_PI - theta2, HALF_PI, alpha);
        drawAngleArc(cx, yBot, 45, -HALF_PI, -HALF_PI + theta2, alpha);
        drawAngleArc(cx, yBot, 40, HALF_PI, HALF_PI + theta1, alpha);

        float speed = 120f;
        float spacing = 28f;
        float offset = (t * speed) % spacing;

        for (float d = offset; d <= maxIncL; d += spacing) {
            float px = hitTopX - d * sin(theta1);
            float py = hitTopY - d * cos(theta1);
            drawPhoton(px, py, 1.0f, alpha);
        }

        for (float d = offset; d <= maxIncL; d += spacing) {
            float px = hitTopX + d * sin(theta1);
            float py = hitTopY - d * cos(theta1);
            drawPhoton(px, py, 0.4f, alpha);
        }

        float speed2 = speed / n_slab;
        float spacing2 = spacing / n_slab;
        float offset2 = (t * speed2) % spacing2;
        float slabLen = dist(hitTopX, hitTopY, hitBotX, hitBotY);

        for (float d = offset2; d <= slabLen; d += spacing2) {
            float tParam = d / slabLen;
            float px = lerp(hitTopX, hitBotX, tParam);
            float py = lerp(hitTopY, hitBotY, tParam);
            drawPhoton(px, py, 1.0f, alpha);
        }

        float offsetOut = (t * speed) % spacing;
        for (float d = offsetOut; d <= maxOutL; d += spacing) {
            float px = hitBotX + d * sin(theta1);
            float py = hitBotY + d * cos(theta1);
            drawPhoton(px, py, 1.0f, alpha);
        }

        noClip();
        popMatrix();
        popStyle();
    }

    private void drawDecayChainGraph(float x, float y, float w, float h, float alpha, float progress) {
        pushStyle();
        pushMatrix();
        clip(x, y, w, h);

        float originX = x + w * 0.16f;
        float originY = y + h * 0.84f;
        float graphW = w * 0.76f;
        float graphH = h * 0.72f;

        stroke(0, alpha);
        strokeWeight(2.5f);
        line(originX, originY, originX + graphW, originY);
        line(originX, originY, originX, originY - graphH);

        float minZ = 81, maxZ = 93;
        float minA = 202, maxA = 240;

        int[][] decayNodes = {
                {92, 238}, {90, 234}, {91, 234}, {92, 234},
                {90, 230}, {88, 226}, {86, 222}, {84, 218},
                {82, 214}, {83, 214}, {84, 214}, {82, 210},
                {83, 210}, {84, 210}, {82, 206}
        };

        stroke(120, alpha);
        strokeWeight(2f);
        for (int i = 0; i < decayNodes.length - 1; i++) {
            float px1 = map(decayNodes[i][0], minZ, maxZ, originX, originX + graphW);
            float py1 = map(decayNodes[i][1], minA, maxA, originY, originY - graphH);
            float px2 = map(decayNodes[i+1][0], minZ, maxZ, originX, originX + graphW);
            float py2 = map(decayNodes[i+1][1], minA, maxA, originY, originY - graphH);

            line(px1, py1, px2, py2);
        }

        for (int i = 0; i < decayNodes.length; i++) {
            float px = map(decayNodes[i][0], minZ, maxZ, originX, originX + graphW);
            float py = map(decayNodes[i][1], minA, maxA, originY, originY - graphH);

            fill(i == 0 || i == decayNodes.length - 1 ? 0 : 200, alpha);
            stroke(0, alpha);
            strokeWeight(2f);
            ellipse(px, py, 12, 12);
        }

        if (progress < 0.05f) {
            lastTriggeredStep = -1;
        }

        float stepProgress = progress * (decayNodes.length - 1);
        int currentStep = constrain((int) stepProgress, 0, decayNodes.length - 2);
        float subProgress = stepProgress - currentStep;

        if (currentStep > lastTriggeredStep) {
            lastTriggeredStep = currentStep;
            int z2 = decayNodes[currentStep + 1][0];
            int a2 = decayNodes[currentStep + 1][1];
            int z1 = decayNodes[currentStep][0];
            int a1 = decayNodes[currentStep][1];

            boolean isAlpha = (a2 - a1 == -4);
            int particleCount = isAlpha ? 4 : 2;

            float nodeX = map(z2, minZ, maxZ, originX, originX + graphW);
            float nodeY = map(a2, minA, maxA, originY, originY - graphH);

            for (int k = 0; k < particleCount; k++) {
                float angle = random(TWO_PI);
                float spd = random(2f, 4.5f);
                decayParticles.add(new DecayParticle(nodeX, nodeY, cos(angle) * spd, sin(angle) * spd));
            }
        }

        float animPx1 = map(decayNodes[currentStep][0], minZ, maxZ, originX, originX + graphW);
        float animPy1 = map(decayNodes[currentStep][1], minA, maxA, originY, originY - graphH);
        float animPx2 = map(decayNodes[currentStep+1][0], minZ, maxZ, originX, originX + graphW);
        float animPy2 = map(decayNodes[currentStep+1][1], minA, maxA, originY, originY - graphH);

        float currPartX = lerp(animPx1, animPx2, subProgress);
        float currPartY = lerp(animPy1, animPy2, subProgress);

        fill(0, alpha);
        stroke(255, alpha);
        strokeWeight(2f);
        ellipse(currPartX, currPartY, 14, 14);

        for (int i = decayParticles.size() - 1; i >= 0; i--) {
            DecayParticle dp = decayParticles.get(i);
            dp.update();
            dp.display(this, alpha);
            if (dp.isDead()) {
                decayParticles.remove(i);
            }
        }

        noClip();
        popMatrix();
        popStyle();
    }

    private void drawPhoton(float x, float y, float alphaMult, float baseAlpha) {
        noStroke();
        fill(0, 40 * alphaMult * (baseAlpha / 255f));
        circle(x, y, 14);
        fill(0, 230 * alphaMult * (baseAlpha / 255f));
        circle(x, y, 5);
    }

    private void drawDashedNormal(float x1, float y1, float x2, float y2, float alpha) {
        stroke(0, alpha * 0.4f);
        strokeWeight(1f);
        float d = dist(x1, y1, x2, y2);
        float dash = 5f, gap = 5f;
        for (float i = 0; i < d; i += dash + gap) {
            float sy = lerp(y1, y2, i / d);
            float ey = lerp(y1, y2, min((i + dash) / d, 1f));
            line(x1, sy, x2, ey);
        }
    }

    private void drawAngleArc(float cx, float cy, float r, float startA, float endA, float alpha) {
        stroke(0, alpha * 0.6f);
        strokeWeight(1f);
        noFill();
        arc(cx, cy, r, r, min(startA, endA), max(startA, endA));
    }

    private void drawChargeNode(float cx, float cy, float r, boolean positive, float alpha) {
        stroke(0, alpha);
        strokeWeight(2);
        fill(positive ? 240 : 100, alpha);
        ellipse(cx, cy, r * 2, r * 2);

        fill(0, alpha);
        textFont(fontMainBold);
        textSize(16);
        textAlign(CENTER, CENTER);
        text(positive ? "+" : "—", cx, cy - 1);
    }

    private void drawVector(float x, float y, float vx, float vy, int col) {
        pushStyle();
        stroke(col);
        strokeWeight(2.5f);
        fill(col);
        line(x, y, x + vx, y + vy);

        float angle = atan2(vy, vx);
        drawArrowHead(x + vx, y + vy, angle, col);
        popStyle();
    }

    private void drawArrowHead(float x, float y, float angle, int col) {
        pushMatrix();
        translate(x, y);
        rotate(angle);
        fill(col);
        noStroke();
        triangle(0, 0, -10, -5, -10, 5);
        popMatrix();
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
            alpha -= 5;
        }

        void display(PApplet p) {
            p.pushStyle();
            p.noStroke();
            p.fill(0, max(0, alpha));
            p.ellipse(x, y, 5, 5);
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
            alphaLife -= 8f;
        }

        void display(PApplet p, float baseAlpha) {
            p.pushStyle();
            p.noStroke();
            float curAlpha = max(0, alphaLife) * (baseAlpha / 255f);
            p.fill(0, curAlpha);
            p.ellipse(x, y, 4, 4);
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
        PApplet.main("Classes.XC");
    }
}