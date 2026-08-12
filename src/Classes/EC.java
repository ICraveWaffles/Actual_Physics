package Classes;

import processing.core.PApplet;

public class EC extends PApplet {

    float t = 0;
    float w = 100f / 120f;
    float logoTransparency;
    float transY;

    public static Elogo elogo;

    float cycleTime = 4.8f;

    public void draw() {
        pushStyle();

        frameRate = 30;
        t = frameCount / (float) frameRate;
        if (t > cycleTime) {
            frameCount = 0;
            t = 0;
        }

        background(15);

        float b = t * w;

        transY = b % 1f;
        logoTransparency = (float) (255 * (-Math.pow(transY, 2) + 2 * (transY)));

        float cx = width * 0.50f;
        float cy = height * 0.50f;

        pushMatrix();
        drawScene(b, cx, cy);
        popMatrix();

        if (elogo != null) {
            elogo.display(this, b, logoTransparency);
        }

        popStyle();
    }

    private void drawScene(float b, float cx, float cy) {
        if (b < 2.0f) {
            float localB = b;

            pushMatrix();
            translate(cx - 40, cy);

            stroke(255);
            strokeWeight(5f);
            fill(25);
            rectMode(CORNERS);
            rect(-520, -250, -420, -200);
            rect(-485, -200, -455, 160);

            rect(-280, 110, 360, 160);
            rect(-280, 160, 385, 175);

            float sourceX = -420;
            float sourceY = -225;

            float sweepP = localB / 2.0f;
            float laserTargetX = lerp(-260f, 340f, sweepP);
            float targetY = 110f;

            float waveFreq = lerp(0.04f, 0.36f, sweepP);
            float waveAmp = lerp(10.0f, 3.2f, sweepP);

            stroke(255, 220);
            strokeWeight(2.5f);
            noFill();

            for (int strand = -2; strand <= 2; strand++) {
                float tx = laserTargetX + strand * 4f;
                float d = dist(sourceX, sourceY, tx, targetY);
                float angle = atan2(targetY - sourceY, tx - sourceX);

                pushMatrix();
                translate(sourceX, sourceY);
                rotate(angle);
                beginShape();
                for (float r = 0; r <= d; r += 2f) {
                    float wy = sin(r * waveFreq - t * 28f) * waveAmp;
                    vertex(r, wy);
                }
                endShape();
                popMatrix();
            }

            int numElectrons = 64;
            for (int i = 0; i < numElectrons; i++) {
                float norm = i / (float) (numElectrons - 1);
                float eX = lerp(-260f, 340f, norm);
                float eY = 110f;

                boolean wasHit = (laserTargetX >= eX);
                boolean isEjected = wasHit && (norm >= 0.5f);

                if (isEjected) {
                    float hitDistance = laserTargetX - eX;
                    float angle = -QUARTER_PI - (norm - 0.5f) * 0.25f + sin(i * 1.7f) * 0.15f;
                    float speed = 1.6f + sin(i * 2.7f) * 0.4f;

                    float currentX = eX + cos(angle) * hitDistance * speed;
                    float currentY = eY + sin(angle) * hitDistance * speed;

                    if (currentY > -height * 0.30f) {
                        stroke(255, 140);
                        strokeWeight(1.2f);
                        line(eX, eY, currentX, currentY);

                        fill(255);
                        noStroke();
                        circle(currentX, currentY, 7.5f);
                    }
                } else {
                    float jitterY = sin(t * 15f + i * 2.3f) * 1.5f;
                    fill(255);
                    noStroke();
                    circle(eX, eY - 5 + jitterY, 7.5f);
                }
            }

            popMatrix();

        } else {
            float localB = b - 2.0f;

            float p1 = 0.25f;
            float p2 = 1;
            float p3 = 1.75f;

            boolean showTrans13 = localB >= p1;
            boolean showTrans32 = localB >= p2;
            boolean showTrans21 = localB >= p3;

            pushMatrix();
            translate(cx, cy);

            pushMatrix();
            translate(-300, -30);

            fill(255);
            noStroke();
            circle(0, 0, 26f);

            noFill();
            stroke(70);
            strokeWeight(1.5f);
            float r1 = 95f;
            float r2 = 185f;
            float r3 = 275f;

            circle(0, 0, r1 * 2);
            circle(0, 0, r2 * 2);
            circle(0, 0, r3 * 2);

            fill(255);
            noStroke();

            for (int i = 0; i < 2; i++) {
                float a = i * PI + t * 2.2f;
                circle(cos(a) * r1, sin(a) * r1, 5f);
            }

            for (int i = 0; i < 8; i++) {
                float a = i * QUARTER_PI + t * 1.3f;
                circle(cos(a) * r2, sin(a) * r2, 5f);
            }

            for (int i = 0; i < 5; i++) {
                float a = i * TWO_PI / 5f - t * 0.7f + 0.4f;
                circle(cos(a) * r3, sin(a) * r3, 5f);
            }

            float rCurrent;
            if (localB < p1) {
                rCurrent = r1;
            } else if (localB < p1 + 0.2f) {
                float p = (localB - p1) / 0.2f;
                rCurrent = lerp(r1, r3, p);
            } else if (localB < p2) {
                rCurrent = r3;
            } else if (localB < p2 + 0.2f) {
                float p = (localB - p2) / 0.2f;
                rCurrent = lerp(r3, r2, p);
            } else if (localB < p3) {
                rCurrent = r2;
            } else if (localB < p3 + 0.2f) {
                float p = (localB - p3) / 0.2f;
                rCurrent = lerp(r2, r1, p);
            } else {
                rCurrent = r1;
            }

            float mainAngle = t * 3.2f;
            float ex = cos(mainAngle) * rCurrent;
            float ey = sin(mainAngle) * rCurrent;

            fill(255);
            noStroke();
            circle(ex, ey, 13f);

            if ((localB >= p1 && localB <= p1 + 0.3f) || (localB >= p2 && localB <= p2 + 0.3f) || (localB >= p3 && localB <= p3 + 0.3f)) {
                float waveP;
                float wFreq;
                if (localB <= p1 + 0.3f) {
                    waveP = (localB - p1) / 0.3f;
                    wFreq = 0.32f;
                } else if (localB <= p2 + 0.3f) {
                    waveP = (localB - p2) / 0.3f;
                    wFreq = 0.18f;
                } else {
                    waveP = (localB - p3) / 0.3f;
                    wFreq = 0.32f;
                }

                stroke(255, 200);
                strokeWeight(2.5f);
                noFill();
                beginShape();
                for (float d = 0; d <= 160 * waveP; d += 2) {
                    float wy = sin(d * wFreq - t * 22f) * 11f;
                    vertex(ex + d, ey + wy);
                }
                endShape();
            }

            popMatrix();

            pushMatrix();
            translate(260, -30);

            stroke(110);
            strokeWeight(2f);
            float L3_abs = -130f, L1 = 150f, L2 = 30f, L3_em = -60f;
            line(-100, L3_abs, 450, L3_abs);
            line(-100, L3_em, 450, L3_em);
            line(-100, L2, 450, L2);
            line(-100, L1, 450, L1);

            if (showTrans13) {
                float arrowP = constrain((localB - p1) / 0.2f, 0f, 1f);
                float y1 = L1;
                float y2 = lerp(L1, L3_abs, arrowP);
                stroke(255);
                strokeWeight(3.5f);
                line(-70, y1, -70, y2);
                fill(255);
                noStroke();
                triangle(-70, y2, -77, y2 + 9, -63, y2 + 9);
            }

            if (showTrans32) {
                float arrowP = constrain((localB - p2) / 0.2f, 0f, 1f);
                float y1 = L3_em;
                float y2 = lerp(L3_em, L2, arrowP);
                stroke(255);
                strokeWeight(3.5f);
                line(0, y1, 0, y2);
                fill(255);
                noStroke();
                triangle(0, y2, -6, y2 - 9, 6, y2 - 9);
            }

            if (showTrans21) {
                float arrowP = constrain((localB - p3) / 0.2f, 0f, 1f);
                float y1 = L2;
                float y2 = lerp(L2, L1, arrowP);
                stroke(255);
                strokeWeight(3.5f);
                line(70, y1, 70, y2);
                fill(255);
                noStroke();
                triangle(70, y2, 64, y2 - 9, 76, y2 - 9);
            }

            popMatrix();

            pushMatrix();
            translate(0, 250);

            noStroke();
            fill(255);
            rectMode(CORNERS);
            rect(-480, 100, 480, 140);

            if (localB >= p1 + 0.1f) {
                stroke(0);
                strokeWeight(4.5f);
                line(-360, 100, -360, 140);
            }
            if (localB >= p2 + 0.1f) {
                stroke(0);
                strokeWeight(4.5f);
                line(-180, 100, -180, 140);
            }
            if (localB >= p3 + 0.1f) {
                stroke(0);
                strokeWeight(4.5f);
                line(260, 100, 260, 140);
            }

            popMatrix();

            popMatrix();
        }
    }

    public void settings() {
        fullScreen();
    }

    public void setup() {
        float finalX = (width / 2f) + 865.3f;
        float finalY = (height / 2f) - 458.1f;
        float finalW = (height / 2f) - 381.75f;
        float finalH = (height / 2f) - 381.75f;
        elogo = new Elogo(finalX, finalY, finalW, finalH);
        noCursor();
    }

    public static void main(String[] args) {
        PApplet.main("Classes.EC");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}