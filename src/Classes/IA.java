package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class IA extends PApplet {

    private final float BEAT_DURATION = 0.6f;
    private final float TOTAL_BEATS = 64.0f;
    private float globalTime = 0;

    private PFont fontTimesBold;
    public static Ilogo ilogo;

    private float[] rawR2 = {0.00000f, 0.00063f, 0.00144f, 0.00230f, 0.00336f, 0.00397f, 0.00462f, 0.00563f};
    private float[] rawInvV2 = {0.971f, 1.025f, 1.135f, 1.254f, 1.222f, 1.233f, 1.261f, 1.418f};

    private float[] dataX = new float[8];
    private float[] dataY = new float[8];
    private float errX = 0.035f;
    private float errY = 0.050f;

    @Override
    public void settings() {
        fullScreen();
        smooth(8);
    }

    @Override
    public void setup() {
        fontTimesBold = createFont("Times New Roman Bold", 26, true);

        float cx = width * 0.5f;
        float cy = height * 0.5f;
        ilogo = new Ilogo(cx + 865.3f, cy - 458.1f, cy - 381.75f, cy - 381.75f);
        noCursor();

        for (int i = 0; i < 8; i++) {
            dataX[i] = map(rawR2[i], 0f, 0.006f, 0.08f, 0.92f);
            dataY[i] = map(rawInvV2[i], 0.8f, 1.5f, 0.08f, 0.92f);
        }
    }

    @Override
    public void draw() {
        background(0);

        globalTime = (millis() * 0.001f) % (BEAT_DURATION * TOTAL_BEATS);
        float currentBeat = globalTime / BEAT_DURATION;

        float centerX = width * 0.5f;
        float centerY = height * 0.5f;

        int activeStage = constrain((int) (currentBeat / 8.0f), 0, 7);
        float stageBeat = currentBeat % 8.0f;
        float stageProgress = stageBeat / 8.0f;

        switch (activeStage) {
            case 0:
                drawStage1_Topic(centerX, centerY, stageProgress, currentBeat);
                break;
            case 1:
                drawStage2_Theory(centerX, centerY, stageProgress, currentBeat);
                break;
            case 2:
                drawStage3_Setup(centerX, centerY, stageProgress, currentBeat);
                break;
            case 3:
                drawStage4_DataCollection(centerX, centerY, stageProgress, currentBeat);
                break;
            case 4:
                drawPlotStage(centerX, centerY, currentBeat, 4);
                break;
            case 5:
                drawPlotStage(centerX, centerY, currentBeat, 5);
                break;
            case 6:
                drawPlotStage(centerX, centerY, currentBeat, 6);
                break;
            case 7:
                drawStage8_ComparisonAndImprovements(centerX, centerY, stageBeat);
                break;
        }

        if (ilogo != null) {
            ilogo.display(this, currentBeat, 255);
        }
    }

    private void drawStage1_Topic(float cx, float cy, float progress, float beat) {
        float stageBeat = beat % 8.0f;
        boolean selected = stageBeat >= 3.0f;
        float timeSec = millis() * 0.001f;

        int cols = 5;
        int rows = 5;
        float tileSize = min(width * 0.16f, height * 0.13f);
        float totalW = cols * tileSize;
        float totalH = (rows + 1) * tileSize;
        float startX = cx - totalW * 0.5f;
        float startY = cy - totalH * 0.5f - 20;

        String[] headers = {"A", "B", "C", "D", "E"};
        int[] numRows = {5, 5, 5, 4, 5};

        for (int c = 0; c < cols; c++) {
            float x = startX + c * tileSize;
            float y = startY;

            fill(35);
            stroke(255);
            strokeWeight(2.5f);
            rect(x, y, tileSize, tileSize, 5);

            fill(255);
            textFont(fontTimesBold);
            textSize(tileSize * 0.45f);
            textAlign(CENTER, CENTER);
            text(headers[c], x + tileSize * 0.5f, y + tileSize * 0.5f - 2);

            for (int r = 1; r <= numRows[c]; r++) {
                float ry = startY + r * tileSize;
                boolean isTarget = (c == 0 && r == 4);

                float waveOffset = sin(timeSec * 3f + (c + r) * 0.4f) * 2.5f;
                float currentY = ry + waveOffset;

                if (isTarget && selected) {
                    float pulse = 1.0f + 0.05f * sin(timeSec * 8f);
                    pushMatrix();
                    translate(x + tileSize * 0.5f, ry + tileSize * 0.5f);
                    scale(pulse);

                    fill(255);
                    stroke(255);
                    strokeWeight(4);
                    rect(-tileSize * 0.5f, -tileSize * 0.5f, tileSize, tileSize, 5);

                    fill(0);
                    textFont(fontTimesBold);
                    textSize(tileSize * 0.42f);
                    textAlign(CENTER, CENTER);
                    text("A.4", 0, -2);
                    popMatrix();
                } else {
                    fill(15);
                    stroke(255, 180);
                    strokeWeight(1.5f);
                    rect(x, currentY, tileSize, tileSize, 5);

                    fill(255);
                    textFont(fontTimesBold);
                    textSize(tileSize * 0.38f);
                    textAlign(CENTER, CENTER);
                    text(String.valueOf(r), x + tileSize * 0.5f, currentY + tileSize * 0.5f - 2);
                }
            }
        }

        if (selected) {
            float textAlpha = map(sin(timeSec * 4f), -1, 1, 200, 255);
            fill(255, textAlpha);
            textFont(fontTimesBold);
            textSize(30);
            textAlign(CENTER, CENTER);

        }
    }

    private void drawStage2_Theory(float cx, float cy, float progress, float beat) {
        float timeSec = millis() * 0.001f;

        float startX = width * 0.10f;
        float endX = width * 0.90f;
        float pathY = cy;

        String[] formulas = {
                "E_i = E_f",
                "Mgh = ½Mv² + ½Iω²",
                "I_t = MR² + nmr²",
                "ω = v / R",
                "1/v² = (nm/R²)r² + C"
        };

        stroke(255, 160);
        strokeWeight(4);
        line(startX, pathY, endX, pathY);

        float pulseT = (timeSec * 1.5f) % 1.0f;
        float pulseX = lerp(startX, endX, pulseT);
        fill(255);
        noStroke();
        ellipse(pulseX, pathY, 16, 16);

        int count = formulas.length;
        for (int i = 0; i < count; i++) {
            float x = lerp(startX, endX, i / (float)(count - 1));
            float nodeR = (i == 4) ? 95f : 85f;
            boolean highlight = (int)(progress * count) == i;

            float yOffset = sin(timeSec * 2f + i) * 6f;

            stroke(255, 120);
            strokeWeight(1.5f);
            line(x, pathY, x, pathY + yOffset);

            fill(highlight ? 255 : 15);
            stroke(255);
            strokeWeight(highlight ? 4.5f : 2.5f);
            ellipse(x, pathY + yOffset, nodeR * 2, nodeR * 2);

            fill(highlight ? 0 : 255);
            textFont(fontTimesBold);
            textSize((i == 4) ? 17 : 20);
            textAlign(CENTER, CENTER);
            text(formulas[i], x, pathY + yOffset - 2);
        }
    }

    private void drawStage3_Setup(float cx, float cy, float progress, float beat) {
        float centerYShift = cy + height * 0.08f;

        float secX = width * 0.22f;
        float secY = centerYShift - height * 0.08f;
        float secR = min(width, height) * 0.28f;

        fill(15);
        stroke(255);
        strokeWeight(3f);
        ellipse(secX, secY, secR * 2, secR * 2);

        noFill();
        stroke(255, 120);
        strokeWeight(1.5f);
        ellipse(secX, secY, secR * 2.15f, secR * 2.15f);

        float currentR = map(sin(millis() * 0.003f), -1, 1, secR * 0.2f, secR * 0.85f);

        stroke(255, 180);
        strokeWeight(1.5f);
        line(secX, secY, secX + currentR, secY);
        line(secX, secY, secX, secY - secR);

        fill(255);
        textFont(fontTimesBold);
        textSize(32);
        textAlign(CENTER, CENTER);
        text("R", secX + 12, secY - secR * 0.5f);
        text("r", secX + currentR * 0.5f, secY - 18);

        for (int k = 0; k < 6; k++) {
            float a = k * TWO_PI / 6f;
            float mx = secX + cos(a) * currentR;
            float my = secY + sin(a) * currentR;

            fill(255);
            stroke(255);
            strokeWeight(2);
            ellipse(mx, my, 22, 22);

            fill(0);
            textSize(11);
            text("m", mx, my - 1);
        }

        float rampL = width * 0.48f;
        float angle = radians(16);

        float startX = width * 0.48f;
        float startY = centerYShift - rampL * 0.25f;
        float endX = startX + rampL * cos(angle);
        float endY = startY + rampL * sin(angle);

        stroke(255);
        strokeWeight(4f);
        line(startX, startY, endX, endY);
        line(startX, endY, endX + width * 0.12f, endY);
        line(startX, startY, startX, endY);

        fill(15);
        stroke(255);
        strokeWeight(2f);
        ellipse(startX - 35, (startY + endY) * 0.5f, 48, 48);
        fill(255);
        textFont(fontTimesBold);
        textSize(24);
        textAlign(CENTER, CENTER);
        text("h", startX - 35, (startY + endY) * 0.5f - 1);

        float wRadius = 38f;

        pushMatrix();
        translate(startX + 50, startY + 50 * tan(angle) - wRadius);
        fill(255);
        stroke(255);
        strokeWeight(2.5f);
        ellipse(0, 0, wRadius * 2, wRadius * 2);

        for (int k = 0; k < 6; k++) {
            float a = k * TWO_PI / 6f;
            float mx = cos(a) * (wRadius * 0.55f);
            float my = sin(a) * (wRadius * 0.55f);
            fill(0);
            ellipse(mx, my, 8, 8);
        }
        popMatrix();
    }

    private void drawStage4_DataCollection(float cx, float cy, float progress, float beat) {
        float centerYShift = cy + height * 0.08f;

        float rampL = width * 0.62f;
        float angle = radians(14);

        float startX = cx - rampL * 0.48f;
        float startY = centerYShift - rampL * 0.20f;
        float endX = startX + rampL * cos(angle);
        float endY = startY + rampL * sin(angle);

        stroke(255);
        strokeWeight(4.5f);
        line(startX, startY, endX, endY);
        line(startX, endY, endX + width * 0.22f, endY);
        line(startX, startY, startX, endY);

        fill(255);
        textFont(fontTimesBold);
        textSize(20);
        textAlign(CENTER, CENTER);
        text("h", startX - 40, (startY + endY) * 0.5f - 1);

        float rollProgress = constrain(progress * 1.12f, 0f, 1f);

        float[] rNorm = {0.20f, 0.42f, 0.65f, 0.88f};
        float[] speeds = {1.012f, 0.995f, 0.981f, 0.968f};
        float wRadius = 36f;

        for (int i = 0; i < 4; i++) {
            float speedMult = 1.05f - i * 0.03f;
            float pInst = constrain(rollProgress * speedMult, 0f, 1f);

            float wx, wy, rotAngle;
            if (pInst < 0.70f) {
                float tRamp = pInst / 0.70f;
                float d = tRamp * tRamp;
                wx = lerp(startX, endX, d);
                wy = lerp(startY, endY, d);
                rotAngle = d * TWO_PI * 4f;
            } else {
                float tFlat = (pInst - 0.70f) / 0.30f;
                wx = lerp(endX, endX + width * 0.20f, tFlat);
                wy = endY;
                rotAngle = TWO_PI * 4f + tFlat * TWO_PI * 2.5f;
            }

            pushMatrix();
            translate(wx, wy - wRadius);
            rotate(rotAngle);

            fill(15, 190 - i * 30);
            stroke(255, 255 - i * 40);
            strokeWeight(2.5f);
            ellipse(0, 0, wRadius * 2, wRadius * 2);

            float rPos = wRadius * rNorm[i];
            for (int k = 0; k < 6; k++) {
                float a = k * TWO_PI / 6f;
                float mx = cos(a) * rPos;
                float my = sin(a) * rPos;
                fill(255, 255 - i * 40);
                ellipse(mx, my, 7, 7);
            }
            popMatrix();

            if (pInst >= 0.70f) {
                float tagY = endY + 28 + i * 26;
                fill(15);
                stroke(255);
                strokeWeight(1.5f);
                rect(wx - 45, tagY - 11, 90, 22, 5);

                fill(255);
                textFont(fontTimesBold);
                textSize(11);
                textAlign(CENTER, CENTER);
                text("v" + (i + 1) + "=" + String.format("%.3f", speeds[i]) + " m/s", wx, tagY);

                stroke(255, 120);
                strokeWeight(1f);
                line(wx, wy - wRadius, wx, tagY - 11);
            }
        }

        float camX = endX + width * 0.10f;
        float camY = endY - 110;

        stroke(255, 160);
        strokeWeight(1.5f);
        line(camX, camY, endX + 10, endY - wRadius);
        line(camX, camY, endX + width * 0.18f, endY - wRadius);

        fill(15);
        stroke(255);
        strokeWeight(2.5f);
        ellipse(camX, camY, 60, 60);

        fill(255);
        textFont(fontTimesBold);
        textSize(12);
        textAlign(CENTER, CENTER);
        text("30 FPS\nΔt=10f", camX, camY - 1);
    }

    private void drawPlotStage(float cx, float cy, float beat, int stageIndex) {
        float stageBeat = beat % 8.0f;

        float graphW = width * 0.68f;
        float graphH = height * 0.58f;

        float originX = cx - graphW * 0.50f;
        float originY = cy + graphH * 0.50f;

        stroke(255);
        strokeWeight(3f);
        line(originX, originY, originX + graphW, originY);
        line(originX, originY, originX, originY - graphH);

        stroke(45);
        strokeWeight(1.5f);
        for (int i = 1; i <= 6; i++) {
            float gx = originX + (graphW / 6f) * i;
            float gy = originY - (graphH / 6f) * i;
            line(gx, originY, gx, originY - graphH);
            line(originX, gy, originX + graphW, gy);
        }


        fill(255);
        textFont(fontTimesBold);
        textSize(24);
        textAlign(CENTER, CENTER);
        text("r² [m²]", originX + graphW + 35, originY - 1);
        text("1/v²", originX, originY - graphH - 33);

        float[] px = new float[8];
        float[] py = new float[8];

        for (int i = 0; i < 8; i++) {
            px[i] = originX + dataX[i] * graphW;
            py[i] = originY - dataY[i] * graphH;
        }

        float ex = errX * graphW;
        float ey = errY * graphH;

        if (stageIndex == 4) {
            for (int i = 0; i < 8; i++) {
                float pProg = constrain(stageBeat - i, 0f, 1f);
                if (pProg > 0) {
                    float popScale = sin(pProg * HALF_PI);
                    float curRadius = 14f * popScale;

                    fill(255);
                    stroke(255);
                    strokeWeight(2.5f);
                    ellipse(px[i], py[i], curRadius, curRadius);

                    fill(0);
                    ellipse(px[i], py[i], curRadius * 0.35f, curRadius * 0.35f);
                }
            }
        }

        if (stageIndex == 5) {
            for (int i = 0; i < 8; i++) {
                fill(255);
                stroke(255);
                strokeWeight(2.5f);
                ellipse(px[i], py[i], 14, 14);

                fill(0);
                ellipse(px[i], py[i], 5, 5);

                float errProg = constrain(stageBeat - i, 0f, 1f);
                if (errProg > 0) {
                    float curEx = ex * sin(errProg * HALF_PI);
                    float curEy = ey * sin(errProg * HALF_PI);

                    stroke(255, 200);
                    strokeWeight(2f);
                    line(px[i] - curEx, py[i], px[i] + curEx, py[i]);
                    line(px[i], py[i] - curEy, px[i], py[i] + curEy);
                }
            }
        }

        if (stageIndex == 6) {
            for (int i = 0; i < 8; i++) {
                stroke(255, 200);
                strokeWeight(2f);
                line(px[i] - ex, py[i], px[i] + ex, py[i]);
                line(px[i], py[i] - ey, px[i], py[i] + ey);

                fill(255);
                stroke(255);
                strokeWeight(2.5f);
                ellipse(px[i], py[i], 14, 14);

                fill(0);
                ellipse(px[i], py[i], 5, 5);
            }

            float tNormal = constrain(stageBeat / 4.0f, 0f, 1f);
            float ratioNormal = 0.5f * (1.0f - cos(PI * tNormal));

            float x1 = originX;
            float y1Fit = py[0] + 15;
            float x2 = originX + graphW;
            float y2Fit = py[7] - 20;

            float curX2 = lerp(x1, x2, ratioNormal);
            float curY2 = lerp(y1Fit, y2Fit, ratioNormal);

            stroke(255);
            strokeWeight(3.5f);
            line(x1, y1Fit, curX2, curY2);

            if (stageBeat >= 4.0f) {
                float tMinMax = constrain((stageBeat - 4.0f) / 4.0f, 0f, 1f);
                float ratioMinMax = 0.5f * (1.0f - cos(PI * tMinMax));

                float maxP1X = px[0] + ex;
                float maxP1Y = py[0] + ey;
                float maxP2X = px[7] - ex;
                float maxP2Y = py[7] - ey;

                float minP1X = px[0] - ex;
                float minP1Y = py[0] - ey;
                float minP2X = px[7] + ex;
                float minP2Y = py[7] + ey;

                float pulse = 8f + sin(millis() * 0.01f) * 3f;

                fill(255);
                noStroke();
                ellipse(maxP1X, maxP1Y, pulse, pulse);
                ellipse(maxP2X, maxP2Y, pulse, pulse);
                ellipse(minP1X, minP1Y, pulse, pulse);
                ellipse(minP2X, minP2Y, pulse, pulse);

                float curMaxX2 = lerp(maxP1X, maxP2X, ratioMinMax);
                float curMaxY2 = lerp(maxP1Y, maxP2Y, ratioMinMax);
                stroke(255, 180);
                strokeWeight(2f);
                drawDashedLine(maxP1X, maxP1Y, curMaxX2, curMaxY2, 10f, 6f);

                float curMinX2 = lerp(minP1X, minP2X, ratioMinMax);
                float curMinY2 = lerp(minP1Y, minP2Y, ratioMinMax);
                stroke(255, 180);
                strokeWeight(2f);
                drawDashedLine(minP1X, minP1Y, curMinX2, curMinY2, 10f, 6f);

            }
        }
    }

    private void drawStage8_ComparisonAndImprovements(float cx, float cy, float stageBeat) {
        float timeSec = millis() * 0.001f;

        if (stageBeat < 4.0f) {
            fill(255);
            textFont(fontTimesBold);
            textSize(28);
            textAlign(CENTER, CENTER);
            text("", cx, cy - height * 0.38f);

            float plotW = width * 0.32f;
            float plotH = height * 0.42f;

            float leftX = cx - width * 0.36f;
            float leftY = cy - plotH * 0.30f;
            drawMiniPlot(leftX, leftY, plotW, plotH, "", false, stageBeat);

            float rightX = cx + width * 0.04f;
            float rightY = cy - plotH * 0.30f;
            drawMiniPlot(rightX, rightY, plotW, plotH, "", true, stageBeat);

        } else {
            float leftCenterX = cx - width * 0.24f;
            float rightCenterX = cx + width * 0.24f;
            float wheelY = cy;

            float normR = 55f;
            boolean showCopy = stageBeat >= 6.0f;
            float origX = showCopy ? (leftCenterX - 85f) : leftCenterX;

            fill(15);
            stroke(255);
            strokeWeight(3.5f);
            ellipse(origX, wheelY, normR * 2, normR * 2);

            for (int k = 0; k < 6; k++) {
                float a = k * TWO_PI / 6f;
                float mx = origX + cos(a) * (normR * 0.6f);
                float my = wheelY + sin(a) * (normR * 0.6f);
                fill(255);
                noStroke();
                ellipse(mx, my, 10, 10);
            }

            if (showCopy) {
                float rawProg = constrain((stageBeat - 6.0f) / 1.5f, 0f, 1f);
                float smoothProg = 0.5f * (1.0f - cos(PI * rawProg));
                float maxR = 105f;
                float growR = lerp(normR, maxR, smoothProg);
                float copyX = leftCenterX + 85f;

                fill(15);
                stroke(255);
                strokeWeight(3.5f);
                ellipse(copyX, wheelY, growR * 2, growR * 2);

                for (int k = 0; k < 6; k++) {
                    float a = k * TWO_PI / 6f;
                    float mx = copyX + cos(a) * (growR * 0.6f);
                    float my = wheelY + sin(a) * (growR * 0.6f);
                    fill(255);
                    noStroke();
                    ellipse(mx, my, 12, 12);
                }

                float checkAlpha = constrain(map(rawProg, 0.4f, 1.0f, 0, 255), 0, 255);
                stroke(255, checkAlpha);
                strokeWeight(6f);
                noFill();
                beginShape();
                vertex(copyX + growR + 15, wheelY - 5);
                vertex(copyX + growR + 28, wheelY + 12);
                vertex(copyX + growR + 48, wheelY - 18);
                endShape();
            }

            float rampX1 = rightCenterX - width * 0.18f;
            float rampX2 = rightCenterX + width * 0.18f;
            float rampY1 = cy - 55f;
            float rampY2 = cy + 65f;

            stroke(255);
            strokeWeight(5f);
            line(rampX1, rampY1, rampX2, rampY2);

            float gate1X = lerp(rampX1, rampX2, 0.28f);
            float gate1Y = lerp(rampY1, rampY2, 0.28f);
            float gate2X = lerp(rampX1, rampX2, 0.78f);
            float gate2Y = lerp(rampY1, rampY2, 0.78f);

            fill(15);
            stroke(255);
            strokeWeight(3f);
            rect(gate1X - 12, gate1Y - 50, 24, 45, 4);
            rect(gate2X - 12, gate2Y - 50, 24, 45, 4);

            fill(255);
            noStroke();
            ellipse(gate1X, gate1Y - 28, 8, 8);
            ellipse(gate2X, gate2Y - 28, 8, 8);

            stroke(255, 180);
            strokeWeight(3f);
            line(gate1X, gate1Y - 5, gate1X, gate1Y + 25);
            line(gate2X, gate2Y - 5, gate2X, gate2Y + 25);

            float laserPulse = sin(timeSec * 16f);
            float laserAlpha = map(laserPulse, -1, 1, 120, 255);

            stroke(255, laserAlpha * 0.35f);
            strokeWeight(12f);
            line(gate1X, gate1Y - 28, gate2X, gate2Y - 28);

            stroke(255, laserAlpha);
            strokeWeight(3.5f);
            line(gate1X, gate1Y - 28, gate2X, gate2Y - 28);
        }
    }

    private void drawMiniPlot(float x, float y, float w, float h, String title, boolean isExperimental, float animBeat) {
        fill(255);
        textFont(fontTimesBold);
        textSize(18);
        textAlign(CENTER, CENTER);
        text(title, x + w * 0.5f, y - 22);

        stroke(255);
        strokeWeight(2.5f);
        line(x, y + h, x + w, y + h);
        line(x, y + h, x, y);

        fill(255);
        textSize(12);
        text("r²", x + w + 15, y + h);
        text("1/v²", x, y - 15);

        if (!isExperimental) {
            float lineProg = constrain(animBeat / 2.5f, 0f, 1f);
            float x1 = x;
            float y1 = y + h * 0.90f;
            float x2 = x + w * 0.90f;
            float y2 = y + h * 0.10f;

            stroke(255);
            strokeWeight(2.5f);
            line(x1, y1, lerp(x1, x2, lineProg), lerp(y1, y2, lineProg));


        } else {
            float ex = 6f;
            float ey = 8f;

            for (int i = 0; i < 8; i++) {
                float ptBeatStart = i * 0.35f;
                float pProg = constrain((animBeat - ptBeatStart) / 0.35f, 0f, 1f);

                if (pProg > 0) {
                    float px = x + dataX[i] * w * 0.88f + w * 0.06f;
                    float py = (y + h) - dataY[i] * h * 0.88f - h * 0.06f;

                    float curEx = ex * pProg;
                    float curEy = ey * pProg;

                    stroke(255, 180 * pProg);
                    strokeWeight(1.5f);
                    line(px - curEx, py, px + curEx, py);
                    line(px, py - curEy, px, py + curEy);

                    fill(255);
                    stroke(255);
                    strokeWeight(2f);
                    ellipse(px, py, 7 * pProg, 7 * pProg);
                    fill(0);
                    ellipse(px, py, 2 * pProg, 2 * pProg);
                }
            }

            if (animBeat >= 2.0f) {
                float lineProg = constrain((animBeat - 2.0f) / 1.5f, 0f, 1f);

                float startXPt = x + dataX[0] * w * 0.88f + w * 0.06f;
                float startYPt = (y + h) - map(0.971f, 0.8f, 1.5f, 0.08f, 0.92f) * h * 0.88f - h * 0.06f;
                float endXPt = x + dataX[7] * w * 0.88f + w * 0.06f;
                float endYPt = (y + h) - map(1.418f, 0.8f, 1.5f, 0.08f, 0.92f) * h * 0.88f - h * 0.06f;

                stroke(255);
                strokeWeight(2.5f);
                line(startXPt, startYPt, lerp(startXPt, endXPt, lineProg), lerp(startYPt, endYPt, lineProg));

            }
        }
    }


    private void drawDashedLine(float x1, float y1, float x2, float y2, float dashLen, float gapLen) {
        float d = dist(x1, y1, x2, y2);
        for (float i = 0; i < d; i += dashLen + gapLen) {
            float t1 = i / d;
            float t2 = min((i + dashLen) / d, 1.0f);
            line(lerp(x1, x2, t1), lerp(y1, y2, t1), lerp(x1, x2, t2), lerp(y1, y2, t2));
        }
    }

    @Override
    public void mousePressed() {
        globalTime = 0;
    }

    public static void main(String[] args) {
        PApplet.main("Classes.IA");
    }
}