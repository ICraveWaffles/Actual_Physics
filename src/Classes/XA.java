package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class XA extends PApplet {

    private final float BEAT_DURATION = 0.6f;
    private final float TOTAL_BEATS = 16f;
    private float globalTime = 0;
    private float startTime = 0;

    private PFont fontMain;
    private PFont fontBold;

    private Xlogo xlogo;
    private QuestionData[] questions = new QuestionData[40];

    private boolean cardsInitialized = false;
    private float cxB1, cyB1, cwB1, chB1, caB1;
    private float cxC1, cyC1, cwC1, chC1, caC1;
    private float cxB2, cyB2, cwB2, chB2, caB2;

    @Override
    public void settings() {
        fullScreen();
        smooth(8);
    }

    @Override
    public void setup() {
        fontMain = createFont("Times New Roman", 22, true);
        fontBold = createFont("Times New Roman", 34, true);
        noCursor();

        startTime = millis() * 0.001f;

        float finalX = (width / 2f) + 865.3f;
        float finalY = (height / 2f) - 458.1f;
        float finalW = (height / 2f) - 381.75f;
        float finalH = (height / 2f) - 381.75f;

        xlogo = new Xlogo(finalX, finalY, finalW, finalH);

        for (int i = 0; i < 40; i++) {
            int markedOpt = (int) random(4);
            long seed = (long) random(100000);
            questions[i] = new QuestionData(i + 1, markedOpt, seed);
        }
    }

    @Override
    public void draw() {
        background(255);

        globalTime = ((millis() * 0.001f) - startTime) % (BEAT_DURATION * TOTAL_BEATS);
        float currentBeat = globalTime / BEAT_DURATION;

        drawIBSpine();

        if (xlogo != null) {
            xlogo.display(this, globalTime, 255);
        }

        if (currentBeat < 10.0f) {
            int maxMarkedIndex = constrain(1 + (int) (currentBeat / 0.25f), 1, 40);
            drawContinuousGrid(currentBeat, maxMarkedIndex);
        } else if (currentBeat >= 10.0f && currentBeat < 16.0f) {
            float p1bBeat = currentBeat - 10.0f;
            updateAndDrawP1BSequence(p1bBeat);
        } else {
            drawContinuousGrid(currentBeat, 40);
        }
    }

    private void drawIBSpine() {
        float spineW = width * 0.04f;

        noStroke();
        fill(200);
        rect(0, 0, spineW, height);

        stroke(30);
        strokeWeight(2);
        line(spineW, 0, spineW, height);

        stroke(80);
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

    private void updateAndDrawP1BSequence(float p1bBeat) {
        float spineW = width * 0.04f;
        float marginL = spineW + width * 0.03f;
        float marginT = height * 0.15f;
        float marginR = width * 0.03f;
        float marginB = height * 0.15f;

        float fullW = width - marginL - marginR;
        float fullH = height - marginT - marginB;
        float gap = width * 0.02f;
        float halfW = (fullW - gap) * 0.5f;

        if (!cardsInitialized) {
            cxB1 = marginL; cyB1 = marginT; cwB1 = fullW; chB1 = fullH; caB1 = 255;
            cxC1 = marginL + halfW + gap; cyC1 = marginT; cwC1 = halfW; chC1 = fullH; caC1 = 0;
            cxB2 = marginL; cyB2 = marginT; cwB2 = fullW; chB2 = fullH; caB2 = 0;
            cardsInitialized = true;
        }

        float txB1 = marginL, tyB1 = marginT, twB1 = fullW, thB1 = fullH, taB1 = 0;
        float txC1 = marginL + halfW + gap, tyC1 = marginT, twC1 = halfW, thC1 = fullH, taC1 = 0;
        float txB2 = marginL, tyB2 = marginT, twB2 = fullW, thB2 = fullH, taB2 = 0;

        if (p1bBeat < 2.0f) {
            taB1 = 255;
            txC1 = width + 100;
            twB2 = fullW;
        } else if (p1bBeat < 4.0f) {
            twB1 = halfW; taB1 = 255;
            taC1 = 255;
            twB2 = fullW;
        } else {
            twB1 = halfW; txB1 = -halfW - 100;
            txC1 = width + 100;
            taB2 = 255;
        }

        float lerpAmt = 0.12f;
        cxB1 = lerp(cxB1, txB1, lerpAmt); cyB1 = lerp(cyB1, tyB1, lerpAmt);
        cwB1 = lerp(cwB1, twB1, lerpAmt); chB1 = lerp(chB1, thB1, lerpAmt); caB1 = lerp(caB1, taB1, lerpAmt);

        cxC1 = lerp(cxC1, txC1, lerpAmt); cyC1 = lerp(cyC1, tyC1, lerpAmt);
        cwC1 = lerp(cwC1, twC1, lerpAmt); chC1 = lerp(chC1, thC1, lerpAmt); caC1 = lerp(caC1, taC1, lerpAmt);

        cxB2 = lerp(cxB2, txB2, lerpAmt); cyB2 = lerp(cyB2, tyB2, lerpAmt);
        cwB2 = lerp(cwB2, twB2, lerpAmt); chB2 = lerp(chB2, thB2, lerpAmt); caB2 = lerp(caB2, taB2, lerpAmt);

        if (caB1 > 5) drawZoneBExp1(cxB1, cyB1, cwB1, chB1, caB1, globalTime);
        if (caC1 > 5) drawZoneCExp1(cxC1, cyC1, cwC1, chC1, caC1, globalTime);
        if (caB2 > 5) drawZoneBExp2(cxB2, cyB2, cwB2, chB2, caB2, globalTime);
    }

    private void drawZoneBExp1(float x, float y, float w, float h, float alpha, float t) {
        pushStyle();

        fill(250, alpha);
        noStroke();
        rect(x, y, w, h, 15);

        pushMatrix();
        clip(x, y, w, h);

        float cylW = min(w * 0.35f, 180);
        float cylH = h * 0.75f;
        float cylX = x + w * 0.5f - cylW * 0.5f;
        float cylY = y + h * 0.12f;

        stroke(0, alpha);
        strokeWeight(3);
        fill(225, alpha);
        rect(cylX, cylY, cylW, cylH, 8);

        strokeWeight(4);
        line(cylX - 25, cylY + cylH + 10, cylX + cylW + 25, cylY + cylH + 10);

        noFill();
        strokeWeight(3);
        arc(cylX + cylW * 0.5f, cylY + cylH + 10, 35, 30, 0, PI);
        line(cylX + cylW * 0.5f, cylY + cylH + 10, cylX + cylW * 0.5f, cylY + cylH - 10);

        fill(255, alpha);
        stroke(0, alpha);
        strokeWeight(2);
        float bubbleSpeed = 120.0f;
        int numBubbles = 6;
        for (int i = 0; i < numBubbles; i++) {
            float phaseOffset = i * (cylH / numBubbles);
            float rawY = (t * bubbleSpeed + phaseOffset) % cylH;
            float bubbleY = (cylY + cylH - 20) - rawY;
            float wobble = sin(t * 8 + i) * 3;
            float size = map(bubbleY, cylY + cylH, cylY, 10, 22);
            ellipse(cylX + cylW * 0.5f + wobble, bubbleY, size, size);
        }

        stroke(100, alpha);
        strokeWeight(2);
        float camX = cylX + cylW + w * 0.08f;
        line(camX, cylY + 20, camX, cylY + cylH - 20);
        for (int i = 0; i <= 5; i++) {
            float tickY = (cylY + 20) + i * ((cylH - 40) / 5);
            line(camX - 5, tickY, camX + 15, tickY);
        }

        noClip();
        popMatrix();

        noFill();
        stroke(0, alpha);
        strokeWeight(3);
        rect(x, y, w, h, 15);
        popStyle();
    }

    private void drawZoneCExp1(float x, float y, float w, float h, float alpha, float t) {
        pushStyle();

        fill(250, alpha);
        noStroke();
        rect(x, y, w, h, 15);

        pushMatrix();
        clip(x, y, w, h);

        float centerX = x + w * 0.5f;
        float bob = sin(t * 3.5f) * (h * 0.015f);

        stroke(0, alpha);
        strokeWeight(5);
        line(centerX - w * 0.15f, y + h * 0.12f, centerX + w * 0.15f, y + h * 0.12f);

        strokeWeight(3);
        fill(220, alpha);
        float sensorY = y + h * 0.16f;
        rect(centerX - 35, sensorY, 70, 40, 6);

        line(centerX, y + h * 0.12f, centerX, sensorY);

        float woodW = min(w * 0.12f, 50);
        float woodH = h * 0.35f;
        float woodY = y + h * 0.28f + bob;

        fill(100, alpha);
        stroke(0, alpha);
        strokeWeight(2);
        rect(centerX - woodW * 0.5f, woodY, woodW, woodH, 4);
        line(centerX, sensorY + 40, centerX, woodY);

        float beakerW = min(w * 0.35f, 140);
        float beakerH = h * 0.38f;
        float beakerX = centerX - beakerW * 0.5f;
        float beakerY = y + h * 0.44f;

        fill(210, alpha);
        noStroke();
        float liquidLevel = beakerY + beakerH * 0.15f - (bob * 0.4f);
        rect(beakerX + 2, liquidLevel, beakerW - 4, (beakerY + beakerH) - liquidLevel - 2);

        stroke(0, alpha);
        strokeWeight(3);
        noFill();
        rect(beakerX, beakerY, beakerW, beakerH, 0, 0, 8, 8);

        float standY = beakerY + beakerH;
        strokeWeight(3);
        line(centerX - beakerW * 0.6f, standY, centerX + beakerW * 0.6f, standY);
        float scissorsH = h * 0.08f;
        line(centerX - beakerW * 0.4f, standY, centerX + beakerW * 0.4f, standY + scissorsH);
        line(centerX + beakerW * 0.4f, standY, centerX - beakerW * 0.4f, standY + scissorsH);
        line(centerX - beakerW * 0.6f, standY + scissorsH, centerX + beakerW * 0.6f, standY + scissorsH);

        noClip();
        popMatrix();

        noFill();
        stroke(0, alpha);
        strokeWeight(3);
        rect(x, y, w, h, 15);
        popStyle();
    }

    private void drawZoneBExp2(float x, float y, float w, float h, float alpha, float t) {
        pushStyle();

        fill(250, alpha);
        noStroke();
        rect(x, y, w, h, 15);

        pushMatrix();
        clip(x, y, w, h);

        float wallX = x + w * 0.85f;
        float wallY = y + h * 0.15f;
        float wallW = min(w * 0.08f, 50);
        float wallH = h * 0.70f;

        fill(180, alpha);
        stroke(0, alpha);
        strokeWeight(3);
        rect(wallX, wallY, wallW, wallH);
        for (float wy = wallY + 20; wy < wallY + wallH; wy += 25) {
            line(wallX, wy, wallX + wallW, wy);
        }

        float studX = x + w * 0.15f;
        float studY = y + h * 0.50f;

        stroke(0, alpha);
        strokeWeight(4);
        noFill();
        ellipse(studX, studY - 60, 35, 35);
        line(studX, studY - 42, studX, studY + 30);
        line(studX, studY + 30, studX - 20, studY + 100);
        line(studX, studY + 30, studX + 20, studY + 100);

        float armAngle = sin(t * 12) * (PI / 6);
        float armEndX = studX + 35 * cos(armAngle);
        float armEndY = (studY - 20) + 35 * sin(armAngle);
        line(studX, studY - 20, armEndX, armEndY);

        fill(50, alpha);
        noStroke();
        rect(armEndX - 5, armEndY - 15, 10, 30);
        rect(armEndX + 5, armEndY - 15, 10, 30);

        float micX = studX + w * 0.15f;
        float micY = studY + 30;

        stroke(0, alpha);
        strokeWeight(3);
        line(micX, micY + 70, micX - 20, micY + 100);
        line(micX, micY + 70, micX + 20, micY + 100);
        line(micX, micY, micX, micY + 70);

        fill(30, alpha);
        ellipse(micX, micY, 16, 16);
        rect(micX + 8, micY - 5, 20, 10, 4);

        float logX = micX + 40;
        float logY = micY + 60;
        fill(200, alpha);
        stroke(0, alpha);
        rect(logX, logY, 50, 40, 5);
        fill(250, alpha);
        rect(logX + 5, logY + 5, 40, 20);
        noFill();
        bezier(micX, micY + 20, micX + 20, micY + 90, logX - 10, logY + 90, logX, logY + 20);

        float maxDist = wallX - micX;
        float waveSpeed = 250.0f;
        float waveInterval = 0.8f;

        strokeWeight(3);
        noFill();

        for (int i = 0; i < 4; i++) {
            float waveTime = (t + i * waveInterval) % 3.2f;
            float r = waveTime * waveSpeed;

            if (r > 0 && r < maxDist) {
                stroke(80, alpha);
                arc(micX, micY, r * 2, r * 2, -PI * 0.25f, PI * 0.25f);
            } else if (r >= maxDist && r < maxDist * 2) {
                float echoR = r - maxDist;
                stroke(150, alpha);
                if (wallX - echoR > micX) {
                    arc(wallX, micY, echoR * 2, echoR * 2, PI * 0.75f, PI * 1.25f);
                }
            }
        }

        stroke(0, alpha);
        strokeWeight(3);
        line(studX - 50, studY + 100, wallX + wallW + 20, studY + 100);

        noClip();
        popMatrix();

        noFill();
        stroke(0, alpha);
        strokeWeight(3);
        rect(x, y, w, h, 15);
        popStyle();
    }

    private void drawContinuousGrid(float currentBeat, int maxMarkedIndex) {
        float spineW = width * 0.04f;
        float numX = spineW + width * 0.03f;
        float startX = numX + width * 0.06f;
        float startY = height * 0.15f;

        float totalW = width - startX - width * 0.04f;
        float gapX = width * 0.015f;
        float boxW = (totalW - 3 * gapX) / 4.0f;

        float totalH = height * 0.7f;
        float gapY = height * 0.015f;
        float boxH = (totalH - 7 * gapY) / 4f;
        float rowH = boxH + gapY;

        float currentQFloat = currentBeat / 0.25f;
        float scrollQ = constrain(currentQFloat - 2, 0, 36);

        String[] options = {"A", "B", "C", "D"};

        pushMatrix();
        pushStyle();
        clip(0, startY - 5, width, totalH + 10);

        for (int i = 0; i < 40; i++) {
            float rowY = startY + (i - scrollQ) * rowH;

            if (rowY + boxH < startY - 20 || rowY > startY + totalH + 20) {
                continue;
            }

            fill(0);
            textFont(fontBold);
            textSize(36);
            textAlign(RIGHT, CENTER);
            text(String.valueOf(i + 1), numX + width * 0.03f, rowY + boxH * 0.5f);

            QuestionData qData = questions[i];
            boolean isMarked = ((i + 1) <= maxMarkedIndex);

            for (int c = 0; c < 4; c++) {
                float boxX = startX + c * (boxW + gapX);

                stroke(0);
                strokeWeight(2.5f);
                fill(255);
                rect(boxX, rowY, boxW, boxH, 4);

                fill(0);
                textFont(fontMain);
                textSize(28);
                textAlign(CENTER, CENTER);
                text(options[c], boxX + boxW * 0.5f, rowY + boxH * 0.45f);

                if (isMarked && qData != null && qData.markedOption == c) {
                    drawThickScribble(boxX, rowY, boxW, boxH, qData.scribbleSeed);
                }
            }
        }

        noClip();
        popStyle();
        popMatrix();
    }

    private void drawThickScribble(float bx, float by, float bw, float bh, long seed) {
        randomSeed(seed);
        stroke(0);
        strokeWeight(6f);

        int lines = 30;
        for (int i = 0; i < lines; i++) {
            float x1 = bx + random(8, bw - 8);
            float y1 = by + random(6, bh - 6);
            float x2 = bx + random(8, bw - 8);
            float y2 = by + random(6, bh - 6);
            line(x1, y1, x2, y2);
        }
        randomSeed(System.currentTimeMillis());
    }

    @Override
    public void mousePressed() {
        startTime = millis() * 0.001f;
        cardsInitialized = false;
    }

    public static void main(String[] args) {
        PApplet.main("Classes.XA");
    }

    private static class QuestionData {
        int id;
        int markedOption;
        long scribbleSeed;

        QuestionData(int id, int markedOption, long scribbleSeed) {
            this.id = id;
            this.markedOption = markedOption;
            this.scribbleSeed = scribbleSeed;
        }
    }
}