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

    float cycleTime = 24.0f;

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

        float cx = width * 0.5f;
        float cy = height * 0.5f;

        if (b < 12.0f) {
            drawFases123(b, cx, cy);
        } else if (b < 16.0f) {
            drawFase4(b, cx, cy);
        } else {
            drawFase5(b, cx, cy);
        }

        popStyle();
    }

    private void drawFases123(float b, float cx, float cy) {
        if (b < 8.0f) {
            float alpha12 = 255;
            if (b >= 7.8f) {
                alpha12 = map(b, 7.8f, 8.0f, 255, 0);
            }

            float state1 = 1.0f;
            if (b >= 2.0f) state1 = 1.0f - constrain((b - 2.0f) * 4.0f, 0f, 1f);

            float state2 = 1.0f;
            if (b >= 1.0f && b < 3.0f) {
                state2 = 1.0f - constrain((b - 1.0f) * 4.0f, 0f, 1f);
            } else if (b >= 3.0f) {
                state2 = constrain((b - 3.0f) * 4.0f, 0f, 1f);
            }

            float baseDist = width * 0.12f;
            float r = baseDist;
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

            if (b < 4.0f) {
                boolean isPos1 = state1 > 0.5f;
                boolean isPos2 = state2 > 0.5f;

                drawMiniField(x1, cy, chargeSize1, isPos1, alpha12);
                drawMiniField(x2, cy, chargeSize2, isPos2, alpha12);
            }

            if (b >= 4.0f) {
                float F_val = map(1f/(r*r), 1f/pow(width*0.26f,2), 1f/(baseDist*baseDist), 25f, 110f);

                if (b < 6.0f) {
                    float fMag = map(F_val, 25f, 110f, 50f, 130f);
                    stroke(255);
                    drawArrow(x1 + chargeSize1 * 0.5f + 10, cy, x1 + chargeSize1 * 0.5f + 10 + fMag, cy, 3.5f);
                    drawArrow(x2 - chargeSize2 * 0.5f - 10, cy, x2 - chargeSize2 * 0.5f - 10 - fMag, cy, 3.5f);
                }

                if (b >= 6.0f && b < 8.0f) {
                    stroke(255, 180);
                    noFill();
                    strokeWeight(1.5f);

                    int numLines = 20;
                    float stepSize = 10f;
                    int maxSteps = 150;

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

                            if (s == 20 || s == 60) {
                                drawArrowHead(px, py, atan2(ny, nx), 6f, 180);
                            }

                            if (d1 < (chargeSize1 * 0.5f) || px < -width || px > width * 2 || py < -height || py > height * 2) {
                                break;
                            }
                        }
                        endShape();
                    }
                }
            }

            stroke(255); strokeWeight(2.5f);
            fill(0);
            circle(x1, cy, chargeSize1);
            circle(x2, cy, chargeSize2);

            drawSign(x1, cy, state1, 255, chargeSize1);
            drawSign(x2, cy, state2, 255, chargeSize2);
        }

        if (b >= 8.0f && b < 12.0f) {
            float alpha3 = 255;

            float plateX = width * 0.5f;
            float plateY = cy - 250f;
            float plateW = width * 3f;
            float plateH = 30f;

            stroke(255, alpha3);
            strokeWeight(2.0f);
            noFill();
            rectMode(CENTER);
            rect(plateX, plateY, plateW, plateH);

            fill(255, alpha3);
            textSize(55);
            textAlign(CENTER, BOTTOM);
            float signStep = 80f;
            float startSignX = plateX - plateW * 0.5f + 35f;
            for (float fx = startSignX; fx < startSignX + plateW; fx += signStep) {
                text("-", fx, plateY - plateH * 0.5f - 18);
            }

            float pivotX = width * 0.5f;
            float pivotY = plateY + plateH * 0.5f;
            float stringL = 360f;
            float massRadius = 60f;

            float tDeflect = max(0, b - 8.0f);
            float targetDefAngle = radians(35f);
            float theta = targetDefAngle * (1.0f - exp(-2.0f * tDeflect) * cos(TWO_PI * 0.8f * tDeflect));

            stroke(255, 100);
            strokeWeight(1.5f);
            for (float dy = 0; dy < stringL + 30; dy += 16) {
                line(pivotX, pivotY + dy, pivotX, pivotY + dy + 8);
            }

            if (theta > 0.02f) {
                noFill();
                stroke(255, 200);
                strokeWeight(1.5f);
                float arcRadius = 110f;
                arc(pivotX, pivotY, arcRadius, arcRadius, HALF_PI, HALF_PI - theta);
            }

            fill(255, alpha3); noStroke();
            circle(pivotX, pivotY, 10);

            float massX = pivotX + stringL * sin(theta);
            float massY = pivotY + stringL * cos(theta);

            stroke(255, alpha3); strokeWeight(3.0f);
            line(pivotX, pivotY, massX, massY);

            fill(0, alpha3); stroke(255, alpha3); strokeWeight(3.0f);
            circle(massX, massY, massRadius);
            drawSign(massX, massY, 1.0f, alpha3, massRadius);

            if (tDeflect > 0.1f) {
                float fgLen = 100f;
                float feLen = fgLen * tan(radians(35f));

                stroke(255, alpha3);
                drawArrow(massX, massY + massRadius * 0.5f, massX, massY + massRadius * 0.5f + fgLen, 3.0f);
                drawArrow(massX + massRadius * 0.5f, massY, massX + massRadius * 0.5f + feLen, massY, 3.0f);
            }
        }
    }

    private void drawMiniField(float cx, float cy, float radius, boolean isPositive, float alpha) {
        int numRays = 8;
        float rayLen = 120f;
        float rStart = radius * 0.5f + 4f;
        float rEnd = rStart + rayLen;

        stroke(255, alpha * 0.35f);
        strokeWeight(1.0f);

        float flowOffset = (frameCount * 0.04f) % 1.0f;

        for (int i = 0; i < numRays; i++) {
            float angle = i * TWO_PI / numRays;
            float cosA = cos(angle);
            float sinA = sin(angle);

            float xInner = cx + cosA * rStart;
            float yInner = cy + sinA * rStart;
            float xOuter = cx + cosA * rEnd;
            float yOuter = cy + sinA * rEnd;

            line(xInner, yInner, xOuter, yOuter);

            for (int a = 0; a < 2; a++) {
                float p = (flowOffset + a * 0.5f) % 1.0f;
                float rHead = isPositive ? lerp(rStart, rEnd, p) : lerp(rEnd, rStart, p);
                float hx = cx + cosA * rHead;
                float hy = cy + sinA * rHead;

                float arrowAlpha = alpha * map(sin(p * PI), 0, 1, 0, 0.9f);
                float arrowDir = isPositive ? angle : angle + PI;

                drawArrowHead(hx, hy, arrowDir, 6.5f, arrowAlpha);
            }
        }
    }

    private void drawFase4(float b, float cx, float cy) {
        float b4 = b;

        pushMatrix();

        float fastSpeed = 450f;
        float plateShift = (b4 - 12.0f) * fastSpeed;

        float plateWidth = width * 4f;
        float plateHeight = 25f;

        float currentPlateX = (width * 0.5f) + (plateShift % 80f);

        stroke(255);
        strokeWeight(1.5f);
        noFill();
        rectMode(CENTER);
        rect(currentPlateX, cy - 200, plateWidth, plateHeight);
        rect(currentPlateX, cy + 200, plateWidth, plateHeight);

        fill(255);
        textSize(50);
        float signStep = 80f;
        float startSignX = currentPlateX - plateWidth * 0.5f;

        textAlign(CENTER, BOTTOM);
        for (float fx = startSignX; fx < startSignX + plateWidth; fx += signStep) {
            text("-", fx, cy - 200 - 18);
        }

        textAlign(CENTER, TOP);
        for (float fx = startSignX; fx < startSignX + plateWidth; fx += signStep) {
            text("+", fx, cy + 200 + 18);
        }

        stroke(255, 35);
        strokeWeight(1.5f);
        for (float fx = startSignX; fx < startSignX + plateWidth; fx += 55) {
            drawArrow(fx, cy - 180, fx, cy + 180, 1.2f);
        }

        if (b4 >= 14.0f) {
            float b_traj = constrain(b4, 14.0f, 16.0f);
            float t_norm = map(b_traj, 14.0f, 16.0f, 0.0f, 1.0f);

            float startX = 0f;
            float targetX = width * 0.5f;
            float targetY = height * 0.5f;
            float startY = cy + 150f;

            float a = (startY - targetY) / sq(startX - targetX);

            noFill();
            stroke(255, 180);
            strokeWeight(2.5f);

            beginShape();
            for (float t_s = 0; t_s <= t_norm; t_s += 0.01f) {
                float px = lerp(startX, targetX, t_s);
                float py = a * sq(px - targetX) + targetY;
                vertex(px, py);
            }
            endShape();

            float p_e_x = lerp(startX, targetX, t_norm);
            float p_e_y = a * sq(p_e_x - targetX) + targetY;

            stroke(255);
            fill(0);
            strokeWeight(2);
            circle(p_e_x, p_e_y, 26);
            drawSign(p_e_x, p_e_y, 0.0f, 255, 26f);

            if (t_norm < 1.0f) {
                float v_hor = width * 0.025f;
                float v_ver = 2f * a * (p_e_x - targetX) * v_hor;

                stroke(255);
                drawArrow(p_e_x, p_e_y, p_e_x + v_hor, p_e_y, 2.5f);

                if (abs(v_ver) > 0.5f) {
                    drawArrow(p_e_x, p_e_y, p_e_x, p_e_y + v_ver, 2.5f);
                }
            }
        }

        popMatrix();
    }

    private void drawFase5(float b, float cx, float cy) {
        pushMatrix();

        float fastSpeed = 450f;
        float plateShift = (b - 12.0f) * fastSpeed;

        float plateWidth = width * 4f;
        float plateHeight = 25f;
        float currentPlateX = (width * 0.5f) + (plateShift % 80f);

        stroke(255);
        strokeWeight(1.5f);
        noFill();
        rectMode(CENTER);
        rect(currentPlateX, cy - 200, plateWidth, plateHeight);
        rect(currentPlateX, cy + 200, plateWidth, plateHeight);

        fill(255);
        textSize(50);
        float signStep = 80f;
        float startSignX = currentPlateX - plateWidth * 0.5f;

        textAlign(CENTER, BOTTOM);
        for (float fx = startSignX; fx < startSignX + plateWidth; fx += signStep) {
            text("-", fx, cy - 200 - 18);
        }

        textAlign(CENTER, TOP);
        for (float fx = startSignX; fx < startSignX + plateWidth; fx += signStep) {
            text("+", fx, cy + 200 + 18);
        }

        stroke(255, 35);
        strokeWeight(1.5f);
        for (float fx = startSignX; fx < startSignX + plateWidth; fx += 55) {
            drawArrow(fx, cy - 180, fx, cy + 180, 1.2f);
        }

        float tRel = b - 16.0f;
        float phase = 45.0f * tRel - 5.0f * tRel * tRel;
        float flickerSignal = sin(phase) + noise(tRel * 8f) * 0.4f;
        float flicker = (flickerSignal > 0.1f) ? 1.0f : 0.05f;

        float bAlpha = 200f * flicker;
        stroke(255, bAlpha);
        strokeWeight(1.2f);
        noFill();

        float gridSpacingX = 100f;
        float gridShiftX = plateShift % gridSpacingX;

        for (float gx = -gridSpacingX + gridShiftX; gx < width + gridSpacingX; gx += gridSpacingX) {
            for (float gy = cy - 130; gy <= cy + 130; gy += 65) {
                circle(gx, gy, 14);
                line(gx - 4, gy - 4, gx + 4, gy + 4);
                line(gx - 4, gy + 4, gx + 4, gy - 4);
            }
        }

        float p_e_x = width * 0.5f;
        float p_e_y = cy;

        stroke(255);
        fill(0);
        strokeWeight(2);
        circle(p_e_x, p_e_y, 26);
        drawSign(p_e_x, p_e_y, 0.0f, 255, 26f);

        float v_hor = width * 0.025f;
        float forceMag = 70f;

        stroke(255);
        drawArrow(p_e_x, p_e_y, p_e_x + v_hor, p_e_y, 2.5f);
        drawArrow(p_e_x, p_e_y, p_e_x, p_e_y - forceMag, 2.5f);
        drawArrow(p_e_x, p_e_y, p_e_x, p_e_y + forceMag, 2.5f);

        popMatrix();
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