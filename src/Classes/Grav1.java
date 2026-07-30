package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class Grav1 extends PApplet {

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

        float cx = width * 0.5f;
        float cy = height * 0.5f;

        float boxW = width * 0.85f;
        float boxH = height * 0.82f;
        float halfW = boxW * 0.5f;
        float halfH = boxH * 0.5f;

        float a1 = halfH * 0.85f;
        float e1 = 0.55f;
        float c1 = a1 * e1;
        float b1_el = a1 * sqrt(1.0f - e1 * e1);

        float focusX = cx - c1;
        float focusY = cy;
        float focus2X = cx + c1;

        float M1 = ((b % 2.0f) / 2.0f) * TWO_PI;
        float E1 = solveKepler(M1, e1);

        float p1X = cx - a1 * cos(E1);
        float p1Y = cy + b1_el * sin(E1);
        float r1 = dist(focusX, focusY, p1X, p1Y);

        float escX = p1X;
        float escY = p1Y;
        if (b >= 14.8f) {
            float escProg = map(b, 14.8f, 16.0f, 0.0f, 1.0f);
            escX = p1X + escProg * (a1 * 2.5f);
            escY = p1Y - escProg * escProg * (a1 * 1.3f) - escProg * (a1 * 0.7f);
        }

        float zoom = 1.0f;
        float camX = cx;
        float camY = cy;

        if (b >= 14.8f) {
            float zProg = map(b, 14.8f, 16.0f, 0.0f, 1.0f);
            zProg = constrain(zProg, 0.0f, 1.0f);
            float easeZ = zProg * zProg * (3.0f - 2.0f * zProg);
            zoom = lerp(1.0f, 10f, easeZ);
            camX = lerp(cx, escX, easeZ);
            camY = lerp(cy, escY, easeZ);
        }

        pushMatrix();
        translate(cx, cy);
        scale(zoom);
        translate(-camX, -camY);



        noFill();
        stroke(255, 50);
        strokeWeight(3.0f / zoom);
        ellipse(cx, cy, a1 * 2, b1_el * 2);

        if (b >= 6.0f && b < 8.0f) {
            strokeWeight(1.2f / zoom);
            float step = 30f;
            float muStar = 140000f;
            float muPlanet = 35000f;

            for (float gx = cx - halfW + 30; gx <= cx + halfW - 30; gx += step) {
                for (float gy = cy - halfH + 30; gy <= cy + halfH - 30; gy += step) {
                    float dx1 = focusX - gx;
                    float dy1 = focusY - gy;
                    float d1Sq = max(900f, dx1 * dx1 + dy1 * dy1);
                    float f1 = muStar / d1Sq;

                    float dx2 = p1X - gx;
                    float dy2 = p1Y - gy;
                    float d2Sq = max(400f, dx2 * dx2 + dy2 * dy2);
                    float f2 = muPlanet / d2Sq;

                    float fx = (dx1 / sqrt(d1Sq)) * f1 + (dx2 / sqrt(d2Sq)) * f2;
                    float fy = (dy1 / sqrt(d1Sq)) * f1 + (dy2 / sqrt(d2Sq)) * f2;

                    float fTot = sqrt(fx * fx + fy * fy);
                    if (fTot > 0.001f) {
                        float len = constrain(fTot * 0.85f, 4f, 22f);
                        float angle = atan2(fy, fx);
                        float alpha = map(len, 4f, 22f, 35, 230);
                        stroke(255, alpha);
                        line(gx, gy, gx + cos(angle) * len, gy + sin(angle) * len);
                    }
                }
            }
        }

        if (b >= 10.0f && b < 12.0f) {
            noStroke();
            float normE1 = (E1 + TWO_PI) % TWO_PI;
            float illumAlpha = (b >= 11.6f) ? map(b, 11.6f, 12.0f, 60, 210) : 60;

            float periEnd = PI * 0.25f;
            float currentSweep1 = (normE1 <= periEnd) ? normE1 : periEnd;

            fill(255, illumAlpha);
            beginShape();
            vertex(focusX, focusY);
            float endA1 = map(currentSweep1, 0, periEnd, -PI * 0.25f, PI * 0.25f);
            for (float a = -PI * 0.25f; a <= endA1; a += 0.03f) {
                vertex(cx - a1 * cos(a), cy + b1_el * sin(a));
            }
            endShape(CLOSE);

            float apoStart = PI * 0.85f;
            float apoEnd = PI * 1.15f;
            if (normE1 >= apoStart) {
                float endA2 = min(normE1, apoEnd);
                fill(255, illumAlpha);
                beginShape();
                vertex(focusX, focusY);
                for (float a = apoStart; a <= endA2; a += 0.03f) {
                    vertex(cx - a1 * cos(a), cy + b1_el * sin(a));
                }
                endShape(CLOSE);
            }
        }

        if (b >= 8.0f && b < 10.0f) {
            float morphProg = map(b, 8.0f, 10.0f, 0.0f, 1.0f);
            float curE = lerp(e1, 0.0f, morphProg);
            float curC = a1 * curE;
            float curB = a1 * sqrt(1.0f - curE * curE);

            stroke(255, 140);
            strokeWeight(1.5f / zoom);
            drawDashedEllipse(cx - c1 + curC, cy, a1 * 2, curB * 2, 44);

            float ghostM = ((b % 2.0f) / 2.0f) * TWO_PI;
            float ghostE = solveKepler(ghostM, curE);
            float ghostX = (cx - c1 + curC) - a1 * cos(ghostE);
            float ghostY = cy + curB * sin(ghostE);

            fill(255, 180);
            noStroke();
            circle(ghostX, ghostY, 10 / zoom);
        }

        if (b < 2.0f) {
            stroke(255, 100);
            strokeWeight(1 / zoom);
            line(cx - a1, cy, cx + a1, cy);
            line(cx, cy - b1_el, cx, cy + b1_el);

            stroke(255, 150);
            line(focusX, focusY, p1X, p1Y);

            fill(255, 200);
            circle(cx, cy, 5 / zoom);
            circle(focus2X, focusY, 5 / zoom);

            textSize(16 / zoom);
            textAlign(CENTER, BOTTOM);
            text("a", cx - a1 * 0.5f, cy - 5);
            textAlign(RIGHT, CENTER);
            text("b", cx - 8, cy - b1_el * 0.5f);
            textAlign(CENTER, TOP);
            text("F1", focusX, focusY + 14);
            text("F2", focus2X, focusY + 14);
        }

        if (b >= 4.0f && b < 6.0f) {
            float fMag = map(1f / (r1 * r1), 1f / ((a1 * 1.55f) * (a1 * 1.55f)), 1f / ((a1 * 0.45f) * (a1 * 0.45f)), a1 * 0.15f, a1 * 0.55f);
            float sw = map(fMag, a1 * 0.15f, a1 * 0.55f, 2.0f, 6.5f) / zoom;
            float angleToFocus = atan2(focusY - p1Y, focusX - p1X);

            stroke(255);
            drawArrow(p1X, p1Y, p1X + cos(angleToFocus) * fMag, p1Y + sin(angleToFocus) * fMag, sw);
            drawArrow(focusX, focusY, focusX - cos(angleToFocus) * fMag, focusY - sin(angleToFocus) * fMag, sw);

            fill(255);
            textSize(15 / zoom);
            textAlign(LEFT, BOTTOM);
            text("F12", p1X + 15, p1Y - 15);
            text("F21", focusX - 35, focusY - 15);
        }

        fill(255);
        noStroke();
        circle(focusX, focusY, 26 / zoom);

        fill(255);
        circle(p1X, p1Y, 12 / zoom);

        if (b >= 2.0f && b < 4.0f) {
            float mu = 80000f;
            float vSq = mu * (2f / r1 - 1f / a1);
            float K = vSq * 0.005f * (a1 / 160f);
            float U = -(mu / r1) * 0.005f * (a1 / 160f);
            float E_total = K + U;

            float barX = cx + 540f;
            float barY = cy - 150f;

            fill(255);
            textSize(14);
            textAlign(LEFT, CENTER);
            text("Ek", barX, barY + 20);
            text("Ep", barX + 80, barY + 20);
            text("Et", barX + 160, barY + 20);

            noStroke();
            fill(255, 220);
            rect(barX, barY + 110, 50, -K * 40);

            fill(255, 120);
            rect(barX + 80, barY + 110, 50, -U * 40);

            fill(255, 255);
            rect(barX + 160, barY + 110, 50, -E_total * 40);
        }

        if (b >= 12.0f) {
            float a2 = a1 * 0.58f;
            float e2 = 0.25f;
            float b2_el = a2 * sqrt(1.0f - e2 * e2);
            float c2 = a2 * e2;

            stroke(255, 40);
            strokeWeight(1.0f / zoom);
            noFill();
            ellipse(focusX + c2, cy, a2 * 2, b2_el * 2);

            float T2 = 2.0f * pow(a2 / a1, 1.5f);
            float M2 = ((b % T2) / T2) * TWO_PI;
            float E2 = solveKepler(M2, e2);
            float p2X_orb = (focusX + c2) - a2 * cos(E2);
            float p2Y_orb = cy + b2_el * sin(E2);

            if (b < 14.0f) {
                fill(255, 200);
                circle(p2X_orb, p2Y_orb, 9 / zoom);
            } else if (b < 14.8f) {
                float subProg = map(b, 14.0f, 14.8f, 0.0f, 1.0f);
                float p2X = lerp(p2X_orb, p1X, subProg);
                float p2Y = lerp(p2Y_orb, p1Y, subProg);
                fill(255, 200);
                circle(p2X, p2Y, 9 / zoom);
            } else {
                if (b < 15.2f) {
                    float shockProg = map(b, 14.8f, 15.2f, 0.0f, 1.0f);
                    stroke(255, 255 * (1.0f - shockProg));
                    strokeWeight(2.5f / zoom);
                    noFill();
                    circle(p1X, p1Y, shockProg * 350f / zoom);
                }

                float escProg = map(b, 14.8f, 16.0f, 0.0f, 1.0f);
                stroke(255, 140);
                strokeWeight(1.8f / zoom);
                noFill();
                beginShape();
                for (float hp = 0; hp <= escProg; hp += 0.03f) {
                    vertex(p1X + hp * (a1 * 2.5f), p1Y - hp * hp * (a1 * 1.3f) - hp * (a1 * 0.7f));
                }
                endShape();

                fill(255);
                noStroke();
                circle(escX, escY, 11 / zoom);
            }
        }

        popMatrix();

        fill(255);
        textSize(18);
        textAlign(LEFT, TOP);

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

    private float solveKepler(float M, float e) {
        float E = M;
        for (int i = 0; i < 5; i++) {
            E = E - (E - e * sin(E) - M) / (1.0f - e * cos(E));
        }
        return E;
    }

    private void drawDashedEllipse(float x, float y, float w, float h, int segments) {
        float step = TWO_PI / segments;
        for (int i = 0; i < segments; i += 2) {
            arc(x, y, w, h, i * step, (i + 1) * step);
        }
    }

    public void settings() {
        fullScreen();
        frameRate = 30;
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
        PApplet.main("Classes.Grav1");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}