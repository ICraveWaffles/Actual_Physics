package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class Elec extends PApplet {

    float t = 0;
    float w = 100f / 120f;
    PFont ntr;
    float logoTransparency;
    float transY;

    public static Dlogo clogo;

    float cycleTime = 19.2f;

    public void draw() {
        pushStyle();

        frameRate = 30;
        t = frameCount / (float) frameRate;
        if (t > cycleTime) {
            frameCount = 0;
            t = 0;
        }

        background(0);

        float b = t * w;
        textFont(ntr);

        transY = b % 1f;
        logoTransparency = (float) (255 * (-Math.pow(transY, 2) + 2 * (transY)));

        if (clogo != null) {
            clogo.display(this, b, logoTransparency);
        }

        float cx = width * 0.5f - 120f;
        float cy = height * 0.5f;

        float pan = 0;
        if (b >= 12.0f) {
            pan = (b - 12.0f) * (width * 0.25f);
        }

        pushMatrix();
        translate(-pan, 0);

        if (b < 8.0f) {
            float alpha12 = map(b, 7.5f, 8.0f, 255, 0);

            float state1 = 1.0f;
            if (b >= 2.0f) state1 = 1.0f - constrain((b - 2.0f) * 4.0f, 0f, 1f);

            float state2 = 1.0f;
            if (b >= 1.0f && b < 3.0f) {
                state2 = 1.0f - constrain((b - 1.0f) * 4.0f, 0f, 1f);
            } else if (b >= 3.0f) {
                state2 = constrain((b - 3.0f) * 4.0f, 0f, 1f);
            }

            String relation = "REPULSIVE";
            if (b >= 1.0f && b < 2.0f) relation = "ATTRACTIVE";
            else if (b >= 2.0f && b < 3.0f) relation = "REPULSIVE";
            else if (b >= 3.0f) relation = "ATTRACTIVE";

            float baseDist = width * 0.12f;
            float extraDist = 0;

            if (b < 5.0f) {
                extraDist = sin(b * PI) * 55f;
            } else if (b < 6f) {
                extraDist = -sin((b - 1.0f) * PI) * 55f;
            }

            float r = baseDist + extraDist;
            float sepProg = 0;

            if (b >= 4.0f) {
                sepProg = constrain(b - 4.0f, 0.0f, 3.5f) / 3.5f;
                sepProg = sepProg * sepProg * (3.0f - 2.0f * sepProg);
                r = lerp(baseDist, width * 0.26f, sepProg);
            }

            float x1 = cx - r;
            float x2 = cx + r;

            float chargeSize1 = 130f;
            float chargeSize2 = (b < 4.0f) ? 130f : lerp(130f, 44f, sepProg);

            if (b >= 4.0f) {
                float barX = cx + width * 0.35f;
                float barY = cy + height * 0.20f;
                float F_val = map(1f/(r*r), 1f/pow(width*0.26f,2), 1f/(baseDist*baseDist), 25f, 110f);
                float E_val = F_val * 0.9f;
                float V_val = map(1f/r, 1f/(width*0.26f), 1f/baseDist, 45f, 110f);
                float I_val = lerp(110f, 35f, sepProg);

                rectMode(CORNER); textSize(16); textAlign(LEFT, CENTER);

                if (b >= 4.0f) {
                    float aF = constrain(map(b, 4.0f, 4.5f, 0, 255), 0, alpha12);
                    fill(255, aF); text("F", barX, barY + 20);
                    noStroke(); fill(255, 220 * (aF/255f)); rect(barX - 10, barY, 32, -F_val);
                }
                if (b >= 5.0f) {
                    float aE = constrain(map(b, 5.0f, 5.5f, 0, 255), 0, alpha12);
                    fill(255, aE); text("E", barX + 55, barY + 20);
                    noStroke(); fill(255, 160 * (aE/255f)); rect(barX + 45, barY, 32, -E_val);
                }
                if (b >= 6.0f) {
                    float aV = constrain(map(b, 6.0f, 6.5f, 0, 255), 0, alpha12);
                    fill(255, aV); text("V", barX + 110, barY + 20);
                    noStroke(); fill(255, 110 * (aV/255f)); rect(barX + 100, barY, 32, -V_val);
                }
                if (b >= 7.0f) {
                    float aI = constrain(map(b, 7.0f, 7.5f, 0, 255), 0, alpha12);
                    fill(255, aI); text("I", barX + 165, barY + 20);
                    noStroke(); fill(255, 60 * (aI/255f)); rect(barX + 155, barY, 32, -I_val);
                }

                if (b >= 4.0f && b < 6.0f) {
                    float aF = constrain(map(b, 4.0f, 4.5f, 0, 255), 0, alpha12);
                    float fMag = map(F_val, 25f, 110f, 50f, 130f);
                    stroke(255, aF);
                    drawArrow(x1 + chargeSize1 * 0.5f + 10, cy, x1 + chargeSize1 * 0.5f + 10 + fMag, cy, 3.5f);
                    drawArrow(x2 - chargeSize2 * 0.5f - 10, cy, x2 - chargeSize2 * 0.5f - 10 - fMag, cy, 3.5f);
                }

                if (b >= 6.0f && b < 8.0f) {
                    float linesAlpha = constrain(map(b, 6.0f, 6.5f, 0, 255), 0, 255) * (alpha12 / 255f);
                    stroke(255, linesAlpha);
                    noFill();
                    strokeWeight(1.5f);

                    int numLines = 28;
                    float stepSize = 8f;
                    int maxSteps = 250;

                    float[] arrowX = new float[1000];
                    float[] arrowY = new float[1000];
                    float[] arrowA = new float[1000];
                    int arrowCount = 0;

                    for (int i = 0; i < numLines; i++) {
                        float startAngle = i * TWO_PI / numLines;
                        float px = x2 + cos(startAngle) * (chargeSize2 * 0.5f);
                        float py = cy + sin(startAngle) * (chargeSize2 * 0.5f);

                        beginShape();
                        for (int s = 0; s < maxSteps; s++) {
                            vertex(px, py);

                            float dx1 = px - x1; float dy1 = py - cy;
                            float d1sq = dx1*dx1 + dy1*dy1;
                            float d1 = sqrt(max(1f, d1sq));

                            float dx2 = px - x2; float dy2 = py - cy;
                            float d2sq = dx2*dx2 + dy2*dy2;
                            float d2 = sqrt(max(1f, d2sq));

                            float Ex = (dx2 / (d2sq * d2)) - (dx1 / (d1sq * d1));
                            float Ey = (dy2 / (d2sq * d2)) - (dy1 / (d1sq * d1));

                            float mag = sqrt(Ex*Ex + Ey*Ey);
                            if (mag == 0) break;

                            float nx = Ex / mag;
                            float ny = Ey / mag;

                            px += nx * stepSize;
                            py += ny * stepSize;

                            if (s == 15 || s == 40 || s == 75 || s == 120) {
                                if (arrowCount < 1000) {
                                    arrowX[arrowCount] = px;
                                    arrowY[arrowCount] = py;
                                    arrowA[arrowCount] = atan2(ny, nx);
                                    arrowCount++;
                                }
                            }

                            if (d1 < (chargeSize1 * 0.5f)) {
                                vertex(px, py);
                                break;
                            }
                            if (px < -pan - width || px > width * 2 || py < -height || py > height * 2) {
                                break;
                            }
                        }
                        endShape();
                    }

                    for (int a = 0; a < arrowCount; a++) {
                        drawArrowHead(arrowX[a], arrowY[a], arrowA[a], 7f, linesAlpha);
                    }
                }
            }

            if (b < 4.0f) {
                float beatOscilation = sin(b * PI) * 80f;
                float rectX = cx + width * 0.32f;
                float rectY = cy + beatOscilation;

                rectMode(CENTER);
                strokeWeight(2.5f);
                stroke(50, 255, 50, alpha12);
                fill(50, 255, 50, 40 * (alpha12 / 255f));
                rect(rectX, rectY, 220, 60, 12);

                fill(50, 255, 50, alpha12);
                textSize(22); textAlign(CENTER, CENTER);
                text(relation, rectX, rectY - 2);
            }

            stroke(255, alpha12); strokeWeight(2.5f);
            fill(0, alpha12);
            circle(x1, cy, chargeSize1);
            circle(x2, cy, chargeSize2);

            drawSign(x1, cy, state1, alpha12, chargeSize1);
            drawSign(x2, cy, state2, alpha12, chargeSize2);
        }

        if (b >= 7.5f && b < 12.0f) {
            float alpha3 = map(b, 7.5f, 8.0f, 0, 255);
            if (b > 11.5f) {
                alpha3 = map(b, 11.5f, 12.0f, 255, 0);
            }

            pushMatrix();
            float plateX = cx + width * 0.08f;
            translate(plateX, cy);

            noStroke();
            fill(50, alpha3); rectMode(CENTER);
            rect(-15, 0, 30, height * 0.6f, 6);
            fill(30, alpha3);
            rect(-35, 0, 10, height * 0.65f);
            fill(180, alpha3);
            rect(5, 0, 10, height * 0.55f, 4);

            for (float py = -height * 0.24f; py <= height * 0.24f; py += 35) {
                drawSign(5, py, 1.0f, alpha3, 22f);
            }
            popMatrix();

            float pivotX = cx + width * 0.26f;
            float pivotY = cy - 220f;
            float stringL = 220f;

            stroke(120, alpha3); strokeWeight(5);
            line(pivotX - 60, pivotY, pivotX + 60, pivotY);
            for (float hx = pivotX - 50; hx <= pivotX + 50; hx += 12) {
                line(hx, pivotY, hx + 12, pivotY - 12);
            }
            fill(180, alpha3); noStroke();
            circle(pivotX, pivotY, 14);

            float tDef = max(0, b - 8.0f);
            float targetTheta = 35f;
            float thetaDeg = targetTheta * (1.0f - exp(-2.0f * tDef) * cos(TWO_PI * 0.8f * tDef));
            float theta = radians(thetaDeg);

            float massX = pivotX + stringL * sin(theta);
            float massY = pivotY + stringL * cos(theta);

            stroke(255, alpha3); strokeWeight(2.5f);
            line(pivotX, pivotY, massX, massY);

            fill(0, alpha3); stroke(255, alpha3); strokeWeight(2.5f);
            circle(massX, massY, 44);
            drawSign(massX, massY, 1.0f, alpha3, 44f);

            if (tDef > 0.1f) {
                float fgLen = 80f;
                stroke(255, alpha3);
                drawArrow(massX, massY + 22, massX, massY + 22 + fgLen, 2.5f);
                drawArrow(massX + 22, massY, massX + 22 + fgLen * tan(radians(32f)), massY, 2.5f);
            }
        }

        if (b >= 12.0f) {
            float cx4 = cx + width * 0.5f + pan;

            rectMode(CENTER); noStroke();
            fill(40);
            rect(cx4, cy - 200, width * 3f, 25);
            rect(cx4, cy + 200, width * 3f, 25);

            for (float fx = cx4 - width * 1.5f; fx <= cx4 + width * 1.5f; fx += 38) {
                drawSign(fx, cy - 204, 0.0f, 255, 30f);
                drawSign(fx, cy + 196, 1.0f, 255, 30f);
            }

            stroke(255, 35); strokeWeight(1.5f);
            for (float fx = cx4 - width * 1.5f; fx <= cx4 + width * 1.5f; fx += 55) {
                drawArrow(fx, cy + 175, fx, cy - 175, 1.2f);
            }

            if (b >= 14f) {
                float t_e = constrain(map(b, 12.5f, 15.0f, 0.0f, 1.0f), 0.0f, 1.0f);

                float startX = -30f;
                float endX = cx4;
                float endY = cy;

                float xv = width * 0.5f;
                float yv = height * 0.5f;

                float a = (endY - yv) / sq(endX - xv);

                noFill();
                stroke(255, 180);
                strokeWeight(2.5f);

                beginShape();
                for (float i = 0; i <= t_e; i += 0.02f) {
                    float px = lerp(startX, endX, i);
                    float py = a * sq(px - xv) + yv;
                    vertex(px, py);
                }
                endShape();

                float eX = lerp(startX, endX, t_e);
                float eY = a * sq(eX - xv) + yv;

                stroke(255);
                fill(0);
                strokeWeight(2);
                circle(eX, eY, 26);
                drawSign(eX, eY, 0.0f, 255, 26f);

                if (t_e < 1.0f) {
                    float vx = (endX - startX) * 0.10f;
                    float vy = 2.0f * a * (eX - xv) * vx;

                    stroke(255);
                    drawArrow(eX, eY, eX + vx, eY, 2.5f);

                    if (abs(vy) > 1.5f) {
                        drawArrow(eX, eY, eX, eY + vy, 2.5f);
                    }
                }
            }
        }

        popMatrix();
        popStyle();
    }

    private void drawArrow(float x1, float y1, float x2, float y2, float weight) {
        strokeWeight(weight);
        line(x1, y1, x2, y2);
        float angle = atan2(y2 - y1, x2 - x1);
        float arrowSize = weight * 2.2f + 5f;
        pushMatrix();
        translate(x2, y2);
        rotate(angle);
        line(0, 0, -arrowSize, -arrowSize * 0.5f);
        line(0, 0, -arrowSize, arrowSize * 0.5f);
        popMatrix();
    }

    private void drawArrowHead(float x, float y, float angle, float size, float alpha) {
        pushMatrix();
        translate(x, y);
        rotate(angle);
        stroke(255, alpha);
        strokeWeight(2f);
        line(0, 0, -size, -size * 0.6f);
        line(0, 0, -size, size * 0.6f);
        popMatrix();
    }

    private void drawSign(float x, float y, float state, float alpha, float size) {
        pushMatrix();
        translate(x, y);
        stroke(255, alpha);
        strokeWeight(size * 0.06f);
        strokeCap(ROUND);
        float l = size * 0.22f;
        line(-l, 0, l, 0);
        if (state > 0.01f) {
            pushMatrix();
            rotate(state * HALF_PI);
            line(-l * state, 0, l * state, 0);
            popMatrix();
        }
        popMatrix();
    }

    public void settings() {
        fullScreen();
    }

    public void setup() {
        ntr = createFont("times.ttf", 50);
        float finalX = (width / 2f) + 865.3f;
        float finalY = (height / 2f) - 458.1f;
        float finalW = (height / 2f) - 381.75f;
        float finalH = (height / 2f) - 381.75f;
        clogo = new Dlogo(finalX, finalY, finalW, finalH);
        noCursor();
    }

    public static void main(String[] args) {
        PApplet.main("Classes.Elec");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}