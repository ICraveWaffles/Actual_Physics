package Classes;

import processing.core.PApplet;
import processing.core.PFont;
import java.util.ArrayList;

public class XB extends PApplet {

    private final float BEAT_DURATION = 1.2f;
    private final float TOTAL_BEATS = 32.0f;
    private float globalTime = 0;
    private float startTimeSec = -1;

    private PFont fontTimesHuge;
    private PFont fontMain;
    private PFont fontMainBold;

    public static Xlogo xlogo;
    private ArrayList<Particle> particles;
    private float userInteractivityOffset = 0;

    @Override
    public void settings() {
        fullScreen();
        smooth(8);
    }

    @Override
    public void setup() {
        fontTimesHuge = createFont("Times New Roman Bold", 400, true);
        fontMain = createFont("Times New Roman", 20, true);
        fontMainBold = createFont("Times New Roman Bold", 22, true);

        float cx = width * 0.5f;
        float cy = height * 0.5f;

        xlogo = new Xlogo(cx + 865.3f, cy - 458.1f, cy - 381.75f, cy - 381.75f);
        particles = new ArrayList<>();

        noCursor();
        startTimeSec = millis() * 0.001f;
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

        float centerX = width * 0.5f;
        float centerY = height * 0.5f;

        int activeQ = 0;
        float localProgress = 0.0f;

        if (currentBeat < 7.0f) {
            activeQ = constrain((int) currentBeat, 0, 8);
            localProgress = currentBeat - activeQ;
        } else if (currentBeat < 9.0f) {
            activeQ = 7;
            localProgress = (currentBeat - 7.0f) / 2.0f;
        } else {
            activeQ = 8;
            localProgress = constrain((currentBeat - 9.0f) / 2.0f, 0.0f, 1.0f);
        }

        drawIBSpine();
        drawXLogo(centerX, centerY, currentBeat);
        drawAdvancedQuestionStage(centerX, centerY, activeQ, localProgress, timeSec);
        updateAndDisplayParticles();
    }

    private void drawIBSpine() {
        float spineW = width * 0.04f;

        noStroke();
        fill(230);
        rect(0, 0, spineW, height);

        stroke(0);
        strokeWeight(2);
        line(spineW, 0, spineW, height);

        stroke(100);
        strokeWeight(2.5f);

        float dashLen = 8f;
        float rowH = 12f;
        float colSpacing = 16f;

        float moveY = globalTime * 30.0f;
        float yOffset = moveY % rowH;

        for (float y = -rowH + yOffset; y < height + rowH; y += rowH) {
            float absY = y - moveY;
            int row = floor(absY / rowH);
            float xOffset = (abs(row) % 2 == 0) ? 6f : 6f + colSpacing * 0.5f;

            for (float x = xOffset; x < spineW - dashLen; x += colSpacing) {
                line(x, y, x + dashLen, y);
            }
        }
    }

    private void drawXLogo(float centerX, float centerY, float currentBeat) {
        if (xlogo != null) {
            xlogo.display(this, currentBeat, 0);
        }
    }

    private void drawAdvancedQuestionStage(float cx, float cy, int questionIndex, float progress, float timeSec) {
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
        translate(currentX, cy - 140);
        fill(200, stageAlpha * 0.08f);
        textFont(fontTimesHuge);
        textAlign(CENTER, CENTER);
        text(String.valueOf(questionIndex + 1), 0, 0);
        popMatrix();

        if (stageAlpha < 4) return;

        float animW = width * 0.62f;
        float animH = height * 0.62f;
        float animX = currentX - animW * 0.5f;
        float animY = cy - animH * 0.5f;

        if (mousePressed) {
            userInteractivityOffset = map(mouseX, 0, width, -0.2f, 0.2f);
        }

        switch (questionIndex) {
            case 0: drawAdvInclinedPlane(animX, animY, animW, animH, stageAlpha, timeSec); break;
            case 1: drawAdvIceWater(animX, animY, animW, animH, stageAlpha, timeSec); break;
            case 2: drawAdvSoundChannel(animX, animY, animW, animH, stageAlpha, timeSec); break;
            case 3: drawAdvElectricField(animX, animY, animW, animH, stageAlpha, timeSec); break;
            case 4: drawAdvGrating(animX, animY, animW, animH, stageAlpha, timeSec); break;
            case 5: drawAdvSelectorGrating(animX, animY, animW, animH, stageAlpha, timeSec); break;
            case 6: drawAdvRutherford(animX, animY, animW, animH, stageAlpha, timeSec); break;
            case 7: drawAdvSkydive(animX, animY, animW, animH, stageAlpha, timeSec); break;
            case 8: drawAdvInduction(animX, animY, animW, animH, stageAlpha, timeSec); break;
            default: break;
        }
    }

    private void drawAdvInclinedPlane(float x, float y, float w, float h, float alpha, float t) {
        pushStyle();
        pushMatrix();
        clip(x, y, w, h);

        float startX = x + w * 0.12f;
        float startY = y + h * 0.35f;

        float rampAngle = radians(25.0f + userInteractivityOffset * 10);
        float rampLen = w * 0.72f;

        float endX = startX + rampLen * cos(rampAngle);
        float endY = startY + rampLen * sin(rampAngle);

        stroke(0, alpha);
        strokeWeight(4);
        line(startX, startY, endX, endY);
        line(endX, endY, endX + w * 0.1f, endY);

        stroke(120, alpha * 0.6f);
        strokeWeight(1.5f);
        line(startX, startY, startX, endY);

        float dirX = (endX - startX) / rampLen;
        float dirY = (endY - startY) / rampLen;
        float nx = dirY;
        float ny = -dirX;

        float cycle = 1.8f;
        float phase = (t % cycle) / cycle;
        float prog = min(1f, phase * 1.3f);

        float maxDistTravel = rampLen - 45f;

        float progBlock = prog;
        float progSphere = prog * 0.845f;
        float progRing = prog * 0.707f;

        float rObj = h * 0.050f;

        float dBlock = progBlock * maxDistTravel;
        float bx = startX + dirX * dBlock + nx * rObj;
        float by = startY + dirY * dBlock + ny * rObj;

        pushMatrix();
        translate(bx, by);
        rotate(atan2(dirY, dirX));
        fill(200, alpha);
        stroke(0, alpha);
        strokeWeight(2);
        rectMode(CENTER);
        rect(0, 0, rObj * 2, rObj * 2, 3);
        popMatrix();

        float dSphere = progSphere * maxDistTravel;
        float sx = startX + dirX * dSphere + nx * (rObj + 2);
        float sy = startY + dirY * dSphere + ny * (rObj + 2);

        pushMatrix();
        translate(sx, sy);
        fill(140, alpha);
        stroke(0, alpha);
        strokeWeight(2);
        ellipse(0, 0, rObj * 2f, rObj * 2f);
        rotate(progSphere * maxDistTravel / rObj);
        stroke(0, alpha);
        line(0, 0, rObj, 0);
        popMatrix();

        float dRing = progRing * maxDistTravel;
        float rx = startX + dirX * dRing + nx * (rObj + 2);
        float ry = startY + dirY * dRing + ny * (rObj + 2);

        pushMatrix();
        translate(rx, ry);
        noFill();
        stroke(0, alpha);
        strokeWeight(4);
        ellipse(0, 0, rObj * 2f, rObj * 2f);
        rotate(-progRing * maxDistTravel / rObj);
        strokeWeight(2);
        line(-rObj * 0.7f, 0, rObj * 0.7f, 0);
        popMatrix();

        fill(0, alpha);
        textFont(fontMainBold);
        textSize(18);
        textAlign(LEFT, BASELINE);
        text("1", x + 20, y + 25);

        noClip();
        popMatrix();
        popStyle();
    }

    private void drawAdvIceWater(float x, float y, float w, float h, float alpha, float t) {
        pushStyle();
        pushMatrix();
        clip(x, y, w, h);

        float pivotX = x + w * 0.5f;
        float pivotY = y + h * 0.2f;
        float stringLen = h * 0.52f;
        float rBall = 16f;

        float period = 3.0f;
        float phase = (t % period) / period;

        float theta1, theta2;
        boolean stuck = false;

        float initialAngle = -PI / 3.0f;
        float postCollisionAngle = PI / 6.0f;

        if (phase < 0.25f) {
            float u = phase / 0.25f;
            theta1 = lerp(initialAngle, 0, sin(u * HALF_PI));
            theta2 = 0;
        } else if (phase < 0.75f) {
            float u = (phase - 0.25f) / 0.5f;
            theta1 = postCollisionAngle * sin(u * PI);
            theta2 = theta1;
            stuck = true;
        } else {
            float u = (phase - 0.75f) / 0.25f;
            theta1 = lerp(0, initialAngle, 1 - cos(u * HALF_PI));
            theta2 = 0;
        }

        stroke(0, alpha);
        strokeWeight(3);
        fill(120, alpha);
        rect(pivotX - 35, pivotY - 6, 70, 6, 2);

        if (!stuck) {
            float b1X = pivotX + stringLen * sin(theta1);
            float b1Y = pivotY + stringLen * cos(theta1);
            stroke(0, alpha);
            strokeWeight(1.8f);
            line(pivotX, pivotY, b1X, b1Y);
            fill(60, alpha);
            ellipse(b1X, b1Y, rBall * 2, rBall * 2);

            float b2X = pivotX + stringLen * sin(theta2);
            float b2Y = pivotY + stringLen * cos(theta2);
            stroke(0, alpha);
            strokeWeight(1.8f);
            line(pivotX, pivotY, b2X, b2Y);
            fill(180, alpha);
            ellipse(b2X, b2Y, rBall * 2, rBall * 2);
        } else {
            float centerTheta = theta1;
            float b1X = pivotX + stringLen * sin(centerTheta) - (rBall * 0.95f) * cos(centerTheta);
            float b1Y = pivotY + stringLen * cos(centerTheta) - (rBall * 0.95f) * sin(centerTheta);
            float b2X = pivotX + stringLen * sin(centerTheta) + (rBall * 0.95f) * cos(centerTheta);
            float b2Y = pivotY + stringLen * cos(centerTheta) + (rBall * 0.95f) * sin(centerTheta);

            stroke(0, alpha);
            strokeWeight(2);
            line(pivotX, pivotY, (b1X + b2X) * 0.5f, (b1Y + b2Y) * 0.5f);

            fill(60, alpha);
            ellipse(b1X, b1Y, rBall * 2, rBall * 2);

            fill(180, alpha);
            ellipse(b2X, b2Y, rBall * 2, rBall * 2);

            stroke(255, alpha);
            strokeWeight(2.5f);
            line(b1X, b1Y, b2X, b2Y);
        }

        fill(0, alpha);
        textFont(fontMainBold);
        textSize(18);
        textAlign(LEFT, BASELINE);
        text("2", x + 20, y + 25);

        noClip();
        popMatrix();
        popStyle();
    }

    private void drawAdvSoundChannel(float x, float y, float w, float h, float alpha, float t) {
        pushStyle();
        pushMatrix();
        clip(x, y, w, h);

        float layerH = h / 5.0f;
        float[] vLayers = {1500.0f, 1470.0f, 1450.0f, 1470.0f, 1500.0f};

        noStroke();
        for (int i = 0; i < 5; i++) {
            float layerY = y + i * layerH;
            float tone = map(vLayers[i], 1450, 1500, 245, 210);
            fill(tone, alpha);
            rect(x, layerY, w, layerH);

            if (i < 4) {
                stroke(0, alpha * 0.3f);
                strokeWeight(1.2f);
                line(x, layerY + layerH, x + w, layerY + layerH);
                noStroke();
            }
        }

        float sourceX = x + w * 0.12f;
        float sourceY = y + 2.5f * layerH;

        fill(0, alpha);
        ellipse(sourceX, sourceY, 10, 10);

        float sweep = (sin(t * 1.2f) + 1.0f) * 0.5f;
        float thetaTrapped = lerp(radians(58), radians(76), sweep);
        float thetaSteep = lerp(radians(22), radians(46), sweep);

        stroke(120, alpha * 0.8f);
        drawMultiLayerRay(sourceX, sourceY, thetaTrapped, -1, 2, vLayers, x, y, w, layerH, alpha, t, false);
        drawMultiLayerRay(sourceX, sourceY, thetaTrapped, 1, 2, vLayers, x, y, w, layerH, alpha, t, false);

        stroke(0, alpha);
        drawMultiLayerRay(sourceX, sourceY, thetaSteep, -1, 2, vLayers, x, y, w, layerH, alpha, t, true);
        drawMultiLayerRay(sourceX, sourceY, thetaSteep, 1, 2, vLayers, x, y, w, layerH, alpha, t, true);

        fill(0, alpha);
        textFont(fontMainBold);
        textSize(18);
        textAlign(LEFT, BASELINE);
        text("3", x + 20, y + 25);

        noClip();
        popMatrix();
        popStyle();
    }

    private void drawMultiLayerRay(float startX, float startY, float initTheta, int initDirY, int initLayer,
                                   float[] vLayers, float x, float y, float w, float layerH, float alpha, float t, boolean drawPulses) {
        float currX = startX;
        float currY = startY;
        float theta = initTheta;
        int dirY = initDirY;
        int currLayer = initLayer;

        float speedFactor = 120.0f;
        float pulseOffset = (t * speedFactor) % 20.0f;

        for (int step = 0; step < 12; step++) {
            if (currX >= x + w) break;

            float nextBoundaryY = (dirY == -1) ? (y + currLayer * layerH) : (y + (currLayer + 1) * layerH);
            float dy = abs(nextBoundaryY - currY);
            float dx = dy * tan(theta);

            float nextX = currX + dx;
            float nextY = nextBoundaryY;

            boolean hitWall = false;
            if (nextX > x + w) {
                nextX = x + w;
                nextY = currY + dirY * (nextX - currX) / max(tan(theta), 0.0001f);
                hitWall = true;
            }

            strokeWeight(2f);
            line(currX, currY, nextX, nextY);

            if (drawPulses) {
                float segLen = dist(currX, currY, nextX, nextY);
                float spacing = 22.0f * (vLayers[currLayer] / 1450.0f);
                for (float d = pulseOffset % spacing; d <= segLen; d += spacing) {
                    float px = lerp(currX, nextX, d / segLen);
                    float py = lerp(currY, nextY, d / segLen);
                    drawSoundPulse(px, py, 0.9f, alpha);
                }
            }

            if (hitWall) break;

            int nextLayer = currLayer + dirY;

            if (nextLayer < 0 || nextLayer >= vLayers.length) {
                dirY = -dirY;
                drawDashedLine(nextX - 15, nextY, nextX + 15, nextY, alpha);
            } else {
                float vCurr = vLayers[currLayer];
                float vNext = vLayers[nextLayer];
                float sinNext = (vNext / vCurr) * sin(theta);

                drawDashedLine(nextX, nextY - 15, nextX, nextY + 15, alpha);

                if (sinNext > 1.0f) {
                    dirY = -dirY;
                } else {
                    theta = asin(sinNext);
                    currLayer = nextLayer;
                }
            }

            currX = nextX;
            currY = nextY;
        }
    }

    private void drawSoundPulse(float x, float y, float alphaMult, float globalAlpha) {
        pushStyle();
        noStroke();
        fill(0, 30 * alphaMult * (globalAlpha / 255.0f));
        ellipse(x, y, 12, 12);
        fill(0, 200 * alphaMult * (globalAlpha / 255.0f));
        ellipse(x, y, 5, 5);
        popStyle();
    }

    private void drawDashedLine(float x1, float y1, float x2, float y2, float alpha) {
        stroke(120, alpha * 0.6f);
        strokeWeight(1f);
        float d = dist(x1, y1, x2, y2);
        float dash = 4f, gap = 4f;
        for (float i = 0; i < d; i += dash + gap) {
            float sx = lerp(x1, x2, i / d);
            float sy = lerp(y1, y2, i / d);
            float ex = lerp(x1, x2, min((i + dash) / d, 1f));
            float ey = lerp(y1, y2, min((i + dash) / d, 1f));
            line(sx, sy, ex, ey);
        }
    }

    private void drawAdvElectricField(float x, float y, float w, float h, float alpha, float t) {
        pushStyle();
        pushMatrix();
        clip(x, y, w, h);

        float marginX = w * 0.18f;
        float rx = x + marginX;
        float sx = x + w - marginX;
        float chargeY = y + h * 0.28f;
        float distPx = sx - rx;

        float Qr = -1.0f;
        float Qs = -2.25f;

        float zeroX = rx + distPx * 0.40f;

        stroke(180, alpha);
        strokeWeight(1.5f);
        line(rx - 20, chargeY, sx + 20, chargeY);

        stroke(80, alpha);
        strokeWeight(2);
        line(zeroX, chargeY - 12, zeroX, chargeY + 12);

        drawGlowingCharge(rx, chargeY, 22, false, alpha);
        drawGlowingCharge(sx, chargeY, 30, false, alpha);

        stroke(120, alpha * 0.6f);
        strokeWeight(1);
        line(rx, chargeY + 16, sx, chargeY + 16);

        float cycle = 4.0f;
        float progress = (t % cycle) / cycle;
        float testPosNorm = map(sin(progress * TWO_PI), -1, 1, 0.16f, 0.84f);
        float testX = rx + testPosNorm * distPx;

        float xCm = testPosNorm * 5.0f;

        fill(255);
        stroke(0, alpha);
        strokeWeight(1.5f);
        ellipse(testX, chargeY, 12, 12);

        float k = 8.0f;
        float eR = -k * abs(Qr) / (xCm * xCm);
        float eS = k * abs(Qs) / ((5.0f - xCm) * (5.0f - xCm));
        float eNet = eR + eS;

        drawVectorArrow(testX, chargeY - 22, eR * 2.8f, color(120, alpha));
        drawVectorArrow(testX, chargeY - 22, eS * 2.8f, color(160, alpha));
        drawVectorArrow(testX, chargeY + 42, eNet * 2.8f, color(0, alpha));

        float graphX = rx;
        float graphW = distPx;
        float graphY = y + h * 0.72f;
        float graphH = h * 0.32f;

        stroke(140, alpha * 0.6f);
        strokeWeight(1f);
        line(graphX - 15, graphY, graphX + graphW + 15, graphY);
        line(graphX + graphW * 0.40f, graphY - graphH * 0.45f, graphX + graphW * 0.40f, graphY + graphH * 0.45f);

        for (int cm = 1; cm <= 4; cm++) {
            float markX = graphX + (cm / 5.0f) * graphW;
            stroke(150, alpha * 0.5f);
            line(markX, graphY - 3, markX, graphY + 3);
        }

        noFill();
        stroke(0, alpha);
        strokeWeight(2);
        beginShape();
        for (float px = graphX + graphW * 0.12f; px <= graphX + graphW * 0.88f; px += 2) {
            float curXcm = map(px, graphX, graphX + graphW, 0, 5.0f);
            float curE = -k * abs(Qr) / (curXcm * curXcm) + k * abs(Qs) / ((5.0f - curXcm) * (5.0f - curXcm));
            float py = graphY - constrain(curE * 3.2f, -graphH * 0.42f, graphH * 0.42f);
            vertex(px, py);
        }
        endShape();

        float ptGraphY = graphY - constrain(eNet * 3.2f, -graphH * 0.42f, graphH * 0.42f);
        fill(255);
        stroke(0, alpha);
        strokeWeight(1.5f);
        ellipse(testX, ptGraphY, 8, 8);

        fill(0, alpha);
        textFont(fontMainBold);
        textSize(18);
        textAlign(LEFT, BASELINE);
        text("4", x + 20, y + 25);

        noClip();
        popMatrix();
        popStyle();
    }

    private void drawVectorArrow(float x, float y, float len, int col) {
        if (abs(len) < 1f) return;
        pushStyle();
        stroke(col);
        strokeWeight(2);
        fill(col);
        line(x, y, x + len, y);
        float arrowDir = len > 0 ? 1 : -1;
        float headSize = 5;
        triangle(x + len, y, x + len - arrowDir * headSize, y - headSize * 0.6f, x + len - arrowDir * headSize, y + headSize * 0.6f);
        popStyle();
    }

    private void drawAdvGrating(float x, float y, float w, float h, float alpha, float t) {
        pushStyle();
        pushMatrix();
        clip(x, y, w, h);

        float gx = x + w * 0.32f;
        float gy = y + h * 0.5f;
        float sx = x + w * 0.82f;

        float waveSpacing = 28.0f;
        float waveOffset = (t * 35.0f) % waveSpacing;

        stroke(140, alpha * 0.5f);
        strokeWeight(1.8f);
        for (float wx = x + w * 0.08f + waveOffset; wx < gx; wx += waveSpacing) {
            line(wx, gy - h * 0.32f, wx, gy + h * 0.32f);
        }

        float gratingH = h * 0.65f;
        float slitGap = 32.0f;
        int numSlits = 3;
        float firstSlitY = gy - ((numSlits - 1) * slitGap) / 2.0f;

        stroke(0, alpha);
        strokeWeight(4);
        float topY = gy - gratingH * 0.5f;
        float botY = gy + gratingH * 0.5f;

        line(gx, topY, gx, firstSlitY - 4);
        for (int i = 0; i < numSlits - 1; i++) {
            float sY1 = firstSlitY + i * slitGap + 4;
            float sY2 = firstSlitY + (i + 1) * slitGap - 4;
            line(gx, sY1, gx, sY2);
        }
        line(gx, firstSlitY + (numSlits - 1) * slitGap + 4, gx, botY);

        stroke(0, alpha);
        strokeWeight(3);
        line(sx, gy - h * 0.38f, sx, gy + h * 0.38f);

        noFill();
        float maxR = (sx - gx) * 0.92f;

        for (int i = 0; i < numSlits; i++) {
            float sY = firstSlitY + i * slitGap;
            for (float r = waveOffset; r < maxR; r += waveSpacing) {
                float fade = map(r, 0, maxR, 180, 0);
                stroke(100, fade * (alpha / 255.0f));
                strokeWeight(1.2f);
                arc(gx, sY, r * 2, r * 2, -HALF_PI * 0.55f, HALF_PI * 0.55f);
            }
        }

        float[] angles = {0, radians(18), radians(-18)};
        float[] intensity = {1.0f, 0.65f, 0.65f};

        for (int k = 0; k < angles.length; k++) {
            float ang = angles[k];
            float endY = gy + (sx - gx) * tan(ang);

            if (endY >= gy - h * 0.38f && endY <= gy + h * 0.38f) {
                stroke(80, alpha * 0.45f * intensity[k]);
                strokeWeight(1.8f);
                line(gx, gy, sx, endY);

                fill(0, alpha * intensity[k]);
                noStroke();
                ellipse(sx + 2, endY, 7, 14 * intensity[k] + 2);
            }
        }

        fill(0, alpha);
        textFont(fontMainBold);
        textSize(18);
        textAlign(LEFT, BASELINE);
        text("5", x + 20, y + 25);

        noClip();
        popMatrix();
        popStyle();
    }

    private void drawAdvSelectorGrating(float x, float y, float w, float h, float alpha, float t) {
        pushStyle();
        pushMatrix();
        clip(x, y, w, h);

        float entryX = x + w * 0.08f;
        float fieldStartX = x + w * 0.38f;
        float fieldWidth = w * 0.54f;
        float fieldHeight = h * 0.75f;
        float fieldTopY = y + h * 0.12f;
        float beamY = y + h * 0.68f;

        fill(242, alpha * 0.6f);
        stroke(100, alpha * 0.7f);
        strokeWeight(1.5f);
        rect(fieldStartX, fieldTopY, fieldWidth, fieldHeight, 6);

        stroke(150, alpha * 0.5f);
        strokeWeight(1.2f);
        for (float mx = fieldStartX + 30; mx < fieldStartX + fieldWidth - 20; mx += 36) {
            for (float my = fieldTopY + 52; my < fieldTopY + fieldHeight - 15; my += 28) {
                line(mx - 4, my - 4, mx + 4, my + 4);
                line(mx + 4, my - 4, mx - 4, my + 4);
            }
        }

        stroke(0, alpha);
        strokeWeight(3);
        line(entryX + 25, beamY - 22, entryX + 25, beamY - 5);
        line(entryX + 25, beamY + 5, entryX + 25, beamY + 22);
        line(entryX + 65, beamY - 22, entryX + 65, beamY - 5);
        line(entryX + 65, beamY + 5, entryX + 65, beamY + 22);

        stroke(0, alpha * 0.85f);
        strokeWeight(2.5f);
        line(entryX, beamY, fieldStartX, beamY);

        float[] radii = { h * 0.32f, h * 0.42f, h * 0.52f };
        int[] isoColors = { color(20, alpha), color(90, alpha), color(160, alpha) };

        for (int i = 0; i < radii.length; i++) {
            float r = radii[i];
            float centerY = beamY - r;

            noFill();
            stroke(isoColors[i]);
            strokeWeight(2);

            float maxAngle = HALF_PI * 0.72f;
            beginShape();
            for (float ang = 0; ang <= maxAngle; ang += 0.04f) {
                float px = fieldStartX + r * sin(ang);
                float py = centerY + r * cos(ang);
                if (px > fieldStartX + fieldWidth || py < fieldTopY) break;
                vertex(px, py);
            }
            endShape();
        }

        float speed = 130.0f;
        float straightLen = fieldStartX - entryX;
        float cycleLen = straightLen + h * 0.48f;
        int numParticles = 6;

        for (int p = 0; p < numParticles; p++) {
            float pOffset = (t * speed + p * (cycleLen / numParticles)) % cycleLen;

            if (pOffset < straightLen) {
                float px = entryX + pOffset;
                float py = beamY;

                fill(0, alpha);
                stroke(255, alpha);
                strokeWeight(1.5f);
                ellipse(px, py, 9, 9);
            } else {
                float distInField = pOffset - straightLen;

                for (int i = 0; i < radii.length; i++) {
                    float r = radii[i];
                    float ang = distInField / r;

                    if (ang <= HALF_PI * 0.72f) {
                        float px = fieldStartX + r * sin(ang);
                        float py = (beamY - r) + r * cos(ang);

                        if (px <= fieldStartX + fieldWidth && py >= fieldTopY) {
                            fill(isoColors[i]);
                            stroke(255, alpha);
                            strokeWeight(1.2f);
                            ellipse(px, py, 7, 7);
                        }
                    }
                }
            }
        }

        fill(0, alpha);
        textFont(fontMainBold);
        textSize(18);
        textAlign(LEFT, BASELINE);
        text("6", x + 20, y + 25);

        noClip();
        popMatrix();
        popStyle();
    }

    // Parte 7: Dispersión de Rutherford (Acelerada y Sincronizada)
    private void drawAdvRutherford(float x, float y, float w, float h, float alpha, float t) {
        pushStyle();
        pushMatrix();
        clip(x, y, w, h);

        float nucX = x + w * 0.62f;
        float nucY = y + h * 0.50f;
        float nucR = 30f;

        float ecc = 1.45f;
        float a = 90f;
        float phiMax = acos(-1.0f / ecc) - 0.12f;
        float rotAngle = radians(-28f);

        int N = 150;
        float[] phiVals = new float[N];
        float[] rVals = new float[N];
        float[] tCum = new float[N];

        tCum[0] = 0;
        for (int i = 0; i < N; i++) {
            float phi = lerp(-phiMax, phiMax, (float) i / (N - 1));
            phiVals[i] = phi;
            float r = (a * (ecc * ecc - 1.0f)) / (1.0f + ecc * cos(phi));
            rVals[i] = r;
            if (i > 0) {
                float dPhi = phiVals[i] - phiVals[i - 1];
                float rAvg = (rVals[i] + rVals[i - 1]) * 0.5f;
                tCum[i] = tCum[i - 1] + rAvg * rAvg * dPhi;
            }
        }

        float maxTCum = tCum[N - 1];
        for (int i = 0; i < N; i++) {
            tCum[i] /= maxTCum;
        }

        // Acelerado a la velocidad del tiempo de escena
        float beatPos = t / BEAT_DURATION;
        float cycle = ((beatPos - 6.0f) * 1.25f) % 1.0f;
        if (cycle < 0) cycle += 1.0f;

        float currentPhi = phiVals[0];
        float currentR = rVals[0];
        for (int i = 0; i < N - 1; i++) {
            if (cycle >= tCum[i] && cycle <= tCum[i + 1]) {
                float u = (cycle - tCum[i]) / (tCum[i + 1] - tCum[i]);
                currentPhi = lerp(phiVals[i], phiVals[i + 1], u);
                currentR = lerp(rVals[i], rVals[i + 1], u);
                break;
            }
        }

        float alphaX = nucX + currentR * cos(currentPhi + rotAngle);
        float alphaY = nucY + currentR * sin(currentPhi + rotAngle);

        stroke(120, alpha * 0.5f);
        strokeWeight(2f);
        noFill();
        beginShape();
        for (int i = 0; i < N; i++) {
            float px = nucX + rVals[i] * cos(phiVals[i] + rotAngle);
            float py = nucY + rVals[i] * sin(phiVals[i] + rotAngle);
            vertex(px, py);
        }
        endShape();

        float distToNuc = dist(alphaX, alphaY, nucX, nucY);
        float minR = a * (ecc - 1.0f);
        float prox = constrain(map(distToNuc, minR * 3.5f, minR, 0, 1), 0, 1);

        for (int i = 1; i <= 5; i++) {
            float fieldR = nucR + i * 18f;
            float fieldAlpha = map(i, 1, 5, 160, 20) * (0.3f + 0.7f * prox);
            noFill();
            stroke(100, fieldAlpha * (alpha / 255f));
            strokeWeight(1.2f + prox * 1.2f);
            float arcSize = fieldR * 2f;
            arc(nucX, nucY, arcSize, arcSize, 0, TWO_PI);
        }

        pushMatrix();
        translate(nucX, nucY);
        for (int i = 0; i < 48; i++) {
            float h1 = getHash(i + 40, 1);
            float h2 = getHash(i + 40, 2);
            float pr = sqrt(h1) * (nucR - 4f);
            float pa = h2 * TWO_PI;
            float px = pr * cos(pa);
            float py = pr * sin(pa);

            if (i % 2 == 0) {
                fill(40, alpha);
                stroke(0, alpha);
            } else {
                fill(200, alpha);
                stroke(100, alpha);
            }
            strokeWeight(1.2f);
            circle(px, py, 9.0f);
        }
        popMatrix();

        pushMatrix();
        translate(alphaX, alphaY);
        stroke(0, alpha);
        strokeWeight(1.2f);

        fill(220, alpha);
        circle(-4f, -4f, 8.5f);
        circle(4f, -4f, 8.5f);
        circle(-4f, 4f, 8.5f);
        circle(4f, 4f, 8.5f);
        popMatrix();

        fill(0, alpha);
        textFont(fontMainBold);
        textSize(18);
        textAlign(LEFT, BASELINE);
        text("7", x + 20, y + 25);

        noClip();
        popMatrix();
        popStyle();
    }

    private float getHash(int i, int j) {
        int n = i * 137 + j * 149;
        n = (n ^ (n >> 16)) * 0x45d9f3b;
        n = (n ^ (n >> 16)) * 0x45d9f3b;
        n = n ^ (n >> 16);
        return (n & 0x7fffffff) / (float) 0x7fffffff;
    }

    private void drawAdvSkydive(float x, float y, float w, float h, float alpha, float t) {
        pushStyle();
        pushMatrix();
        clip(x, y, w, h);

        int cBg     = color(10, alpha);
        int cWhite  = color(255, alpha);
        int cGrayMid= color(140, alpha);
        int cGrayLow= color(60, alpha);

        fill(cBg);
        stroke(cGrayMid);
        strokeWeight(2);
        rect(x, y, w, h);

        float cycle = (t * 0.15f) % 1.0f;
        int totalBeats = 16;
        int currentBeat = (int)(cycle * totalBeats) + 1;
        boolean chuteOpen = (currentBeat >= 8);

        fill(cWhite);
        textFont(fontMainBold);
        textSize(18);
        textAlign(LEFT, BASELINE);
        text("8", x + 20, y + 25);

        float originX = x + w * 0.28f;
        float originY = y + h * 0.22f;

        noFill();
        stroke(cWhite);
        strokeWeight(2.5f);
        ellipse(originX, originY, 40, 52);
        line(originX - 20, originY, originX + 20, originY);

        float capsuleY = originY + 42;
        stroke(cGrayMid);
        strokeWeight(1.5f);
        line(originX, originY + 26, originX, capsuleY);
        fill(cWhite);
        rectMode(CENTER);
        rect(originX, capsuleY, 14, 14);
        rectMode(CORNER);

        float targetY = y + h * 0.82f;
        stroke(cGrayLow);
        strokeWeight(2);
        for (float dy = capsuleY + 16; dy < targetY; dy += 10) {
            line(originX, dy, originX, dy + 5);
        }

        float skydiverY;
        float skydiverX = originX;

        if (currentBeat < 8) {
            float u = cycle / (7.0f / 16.0f);
            skydiverY = lerp(capsuleY + 14, capsuleY + (targetY - capsuleY) * 0.40f, u * u);
        } else if (currentBeat == 8) {
            float u = (cycle - 7.0f / 16.0f) / (1.0f / 16.0f);
            skydiverY = lerp(capsuleY + (targetY - capsuleY) * 0.40f, capsuleY + (targetY - capsuleY) * 0.52f, u);
        } else if (currentBeat < 16) {
            float u = (cycle - 8.0f / 16.0f) / (7.0f / 16.0f);
            skydiverY = lerp(capsuleY + (targetY - capsuleY) * 0.52f, targetY - 10, u);
        } else {
            skydiverY = targetY;
        }

        if (chuteOpen && currentBeat < 16) {
            stroke(cWhite);
            strokeWeight(2.5f);
            noFill();
            beginShape();
            vertex(skydiverX - 26, skydiverY - 16);
            vertex(skydiverX - 16, skydiverY - 32);
            vertex(skydiverX + 16, skydiverY - 32);
            vertex(skydiverX + 26, skydiverY - 16);
            endShape();

            stroke(cGrayMid);
            strokeWeight(1.5f);
            line(skydiverX - 26, skydiverY - 16, skydiverX, skydiverY);
            line(skydiverX + 26, skydiverY - 16, skydiverX, skydiverY);

            if (currentBeat == 8) {
                float pulse = (cycle - 7.0f / 16.0f) / (1.0f / 16.0f);
                stroke(cWhite, alpha * (1.0f - pulse));
                strokeWeight(2);
                noFill();
                ellipse(skydiverX, skydiverY - 24, 32 + pulse * 35, 18 + pulse * 20);
            }
        }

        fill(cWhite);
        noStroke();
        circle(skydiverX, skydiverY, 10);

        float gx = x + w * 0.54f;
        float gy = y + h * 0.28f;
        float gw = w * 0.40f;
        float gh = h * 0.52f;
        float gZeroY = gy + gh * 0.5f;

        stroke(cGrayLow);
        strokeWeight(1.5f);
        noFill();
        rect(gx, gy, gw, gh);
        stroke(cGrayMid);
        line(gx, gZeroY, gx + gw, gZeroY);

        stroke(cWhite);
        strokeWeight(2.5f);
        beginShape();
        for (float px = 0; px <= 1.0f; px += 0.02f) {
            float cx = lerp(gx, gx + gw, px);
            float ca = 0;
            if (px < 7.0f / 16.0f) {
                ca = 0.4f;
            } else if (px < 8.0f / 16.0f) {
                float u = (px - 7.0f / 16.0f) / (1.0f / 16.0f);
                ca = lerp(0.4f, -0.8f, u);
            } else if (px < 15.0f / 16.0f) {
                float u = (px - 8.0f / 16.0f) / (7.0f / 16.0f);
                ca = lerp(-0.8f, -0.05f, min(1.0f, u * 2.0f));
            } else {
                ca = 0.0f;
            }
            float cy = gZeroY - ca * (gh * 0.42f);
            vertex(cx, cy);
        }
        endShape();

        float dotX = lerp(gx, gx + gw, cycle);
        stroke(cWhite);
        strokeWeight(2f);
        line(dotX, gy, dotX, gy + gh);

        noClip();
        popMatrix();
        popStyle();
    }

    // Parte 9: Inducción Electromagnética (Sincronización Exacta)
    private void drawAdvInduction(float x, float y, float w, float h, float alpha, float t) {
        pushStyle();
        pushMatrix();
        clip(x, y, w, h);

        fill(255, alpha);
        stroke(0, alpha);
        strokeWeight(2);
        rect(x, y, w, h, 8);

        fill(0, alpha);
        textFont(fontMainBold);
        textSize(18);
        textAlign(LEFT, BASELINE);
        text("9", x + 20, y + 25);

        // Alineado exactamente con el compás de la Pregunta 9
        float beatPos = t / BEAT_DURATION;
        float cycle = ((beatPos - 9.0f) * 0.5f) % 1.0f;
        if (cycle < 0) cycle += 1.0f;

        float simX = x + w * 0.28f;
        float coilY = y + h * 0.52f;
        float startY = coilY - h * 0.32f;
        float endY   = coilY + h * 0.36f;

        float uCenter = 0.50f;
        float sigma = 0.09f;

        float normFall = cycle * cycle;
        float magnetY = lerp(startY, endY, normFall);

        float coilW = 54;
        stroke(100, alpha);
        strokeWeight(3.5f);
        noFill();
        for (int i = -3; i <= 3; i++) {
            ellipse(simX, coilY + i * 8, coilW, 18);
        }

        float distToCoil = abs(magnetY - coilY);
        float fieldAlpha = max(0, 180 - distToCoil * 1.4f);
        stroke(120, fieldAlpha * (alpha / 255.0f));
        strokeWeight(1.2f);
        noFill();
        ellipse(simX, magnetY, 70, 30);
        ellipse(simX, magnetY, 95, 48);

        float magW = 20, magH = 44;
        rectMode(CENTER);

        fill(40, alpha);
        stroke(0, alpha);
        strokeWeight(1.5f);
        rect(simX, magnetY - magH * 0.25f, magW, magH * 0.5f);
        fill(255, alpha);
        textFont(fontMainBold);
        textSize(12);
        textAlign(CENTER, CENTER);
        text("N", simX, magnetY - magH * 0.25f);

        fill(180, alpha);
        stroke(0, alpha);
        strokeWeight(1.5f);
        rect(simX, magnetY + magH * 0.25f, magW, magH * 0.5f);
        fill(0, alpha);
        text("S", simX, magnetY + magH * 0.25f);

        rectMode(CORNER);

        float gx = x + w * 0.52f;
        float gy = y + h * 0.25f;
        float gw = w * 0.42f;
        float gh = h * 0.55f;
        float gZeroY = gy + gh * 0.5f;

        fill(248, alpha);
        stroke(200, alpha);
        strokeWeight(1.5f);
        rect(gx, gy, gw, gh, 4);

        stroke(120, alpha);
        strokeWeight(1.2f);
        line(gx, gZeroY, gx + gw, gZeroY);
        line(gx + gw * uCenter, gy, gx + gw * uCenter, gy + gh);

        stroke(0, alpha);
        strokeWeight(2.5f);
        noFill();
        beginShape();
        for (float px = 0; px <= 1.0f; px += 0.01f) {
            float cx = lerp(gx, gx + gw, px);
            float dev = (px - uCenter) / sigma;
            float speedFactor = 1.0f + px * 1.2f;
            float emfValue = -dev * exp(-dev * dev * 0.5f) * speedFactor * 0.65f;
            float cy = gZeroY - emfValue * (gh * 0.40f);
            vertex(cx, cy);
        }
        endShape();

        float currentX = lerp(gx, gx + gw, cycle);
        stroke(120, alpha * 0.6f);
        strokeWeight(1.2f);
        line(currentX, gy, currentX, gy + gh);

        float currentDev = (cycle - uCenter) / sigma;
        float currentSpeed = 1.0f + cycle * 1.2f;
        float currentEmf = -currentDev * exp(-currentDev * currentDev * 0.5f) * currentSpeed * 0.65f;
        float cursorY = gZeroY - currentEmf * (gh * 0.40f);

        fill(0, alpha);
        stroke(255, alpha);
        strokeWeight(1.5f);
        circle(currentX, cursorY, 8);

        fill(0, alpha);
        textFont(fontMain);
        textSize(13);
        textAlign(LEFT, TOP);
        text("EMF (e.m.f.)", gx + 8, gy + 8);

        noClip();
        popMatrix();
        popStyle();
    }

    private void drawGlowingCharge(float cx, float cy, float r, boolean positive, float alpha) {
        noStroke();
        for (int i = 3; i > 0; i--) {
            fill(positive ? color(220, alpha * 0.12f * i) : color(80, alpha * 0.12f * i));
            ellipse(cx, cy, r + i * 10, r + i * 10);
        }
        stroke(0, alpha);
        strokeWeight(2);
        fill(positive ? color(240, alpha) : color(80, alpha));
        ellipse(cx, cy, r * 2, r * 2);
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
        int col;

        Particle(float x, float y, float vx, float vy, int col) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.col = col;
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
            p.fill(p.red(col), max(0, alpha));
            p.ellipse(x, y, 7, 7);
            p.popStyle();
        }

        boolean isDead() {
            return alpha <= 0;
        }
    }

    @Override
    public void mousePressed() {
        startTimeSec = millis() * 0.001f;
    }

    public static void main(String[] args) {
        PApplet.main("Classes.XB");
    }
}