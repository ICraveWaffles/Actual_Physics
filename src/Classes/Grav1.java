package Classes;

import processing.core.PApplet;

public class Grav1 extends PApplet {

    float t = 0;
    float w = 100f / 120f;
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

        transY = b % 1f;
        logoTransparency = (float) (255 * (-Math.pow(transY, 2) + 2 * (transY)));

        if (clogo != null) {
            clogo.display(this, b, logoTransparency);
        }

        float cx = width * 0.5f - 120f;
        float cy = height * 0.5f;

        float boxW = width * 0.85f;
        float boxH = height * 0.82f;
        float halfW = boxW * 0.33f;
        float halfH = boxH * 0.5f;

        float a1 = halfH * 0.85f;
        float e1 = 0.60f;
        float c1 = a1 * e1;
        float b1_el = a1 * sqrt(1.0f - e1 * e1);

        float focusX = cx - c1;
        float focusY = cy;
        float focus2X = cx + c1;

        float M1 = (((b % 2.0f) / 2.0f) * TWO_PI) + PI;
        float E1 = solveKepler(M1, e1);

        float p1X = cx - a1 * cos(E1);
        float p1Y = cy + b1_el * sin(E1);
        float r1 = dist(focusX, focusY, p1X, p1Y);

        float M_impact = (((14.8f % 2.0f) / 2.0f) * TWO_PI) + PI;
        float E_impact = solveKepler(M_impact, e1);
        float impactX = cx - a1 * cos(E_impact);
        float impactY = cy + b1_el * sin(E_impact);

        float escX = p1X;
        float escY = p1Y;
        if (b >= 14.8f) {
            float escProg = map(b, 14.8f, 16.0f, 0.0f, 1.0f);
            escX = impactX + escProg * (a1 * 2.5f);
            escY = impactY - escProg * escProg * (a1 * 1.3f) - escProg * (a1 * 0.7f);
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

        // Camera impact shudder effect
        float shakeX = 0;
        float shakeY = 0;
        if (b >= 14.8f && b < 15.4f) {
            float shakeMag = map(b, 14.8f, 15.4f, 16f, 0f) / zoom;
            shakeX = sin(b * 130f) * shakeMag;
            shakeY = cos(b * 160f) * shakeMag;
        }

        pushMatrix();
        translate(cx + shakeX, cy + shakeY);
        scale(zoom);
        translate(-camX, -camY);

        noFill();
        stroke(255, 50);
        strokeWeight(3.0f / zoom);
        ellipse(cx, cy, a1 * 2, b1_el * 2);

        // Dynamic vector field with traveling wave distortion
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
                        float wave = sin(b * 12f - sqrt(d1Sq) * 0.05f) * 2.5f;
                        float len = constrain(fTot * 0.85f + wave, 3f, 24f);
                        float angle = atan2(fy, fx);
                        float alpha = map(len, 3f, 24f, 35, 230);
                        stroke(255, alpha);
                        line(gx, gy, gx + cos(angle) * len, gy + sin(angle) * len);
                    }
                }
            }
        }

        if (b >= 10.0f && b < 12.0f) {
            noStroke();
            float illumAlpha = (b >= 11.6f) ? map(b, 11.6f, 12.0f, 60, 210) : 60;
            fill(255, illumAlpha);

            float M_seq = PI + ((b - 10.0f) / 2.0f) * TWO_PI;
            float E_seq = solveKepler(M_seq, e1);

            float deltaM = 0.30f;

            float mA_start = PI;
            float mA_end = PI + deltaM;
            float eA_start = solveKepler(mA_start, e1);
            float eA_end = solveKepler(mA_end, e1);

            if (M_seq >= mA_start) {
                float eA_cur = min(E_seq, eA_end);
                beginShape();
                vertex(focusX, focusY);
                for (float ea = eA_start; ea <= eA_cur; ea += 0.02f) {
                    vertex(cx - a1 * cos(ea), cy + b1_el * sin(ea));
                }
                vertex(cx - a1 * cos(eA_cur), cy + b1_el * sin(eA_cur));
                endShape(CLOSE);
            }

            float mP_start = TWO_PI - deltaM * 0.5f;
            float mP_end = TWO_PI + deltaM * 0.5f;
            float eP_start = solveKepler(mP_start, e1);
            float eP_end = solveKepler(mP_end, e1);

            if (M_seq >= mP_start) {
                float eP_cur = min(E_seq, eP_end);
                beginShape();
                vertex(focusX, focusY);
                for (float ep = eP_start; ep <= eP_cur; ep += 0.03f) {
                    vertex(cx - a1 * cos(ep), cy + b1_el * sin(ep));
                }
                vertex(cx - a1 * cos(eP_cur), cy + b1_el * sin(eP_cur));
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

            float ghostM = (((b % 2.0f) / 2.0f) * TWO_PI) + PI;
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
        }

        if (b >= 4.0f && b < 6.0f) {
            float rMin = a1 * (1.0f - e1);
            float rMax = a1 * (1.0f + e1);
            float normR = map(r1, rMin, rMax, 1.0f, 0.0f);
            normR = pow(constrain(normR, 0f, 1f), 0.45f);

            float fMag = lerp(a1 * 0.12f, a1 * 0.38f, normR);
            float sw = lerp(2.0f, 4.5f, normR) / zoom;
            float angleToFocus = atan2(focusY - p1Y, focusX - p1X);

            stroke(255);
            drawArrow(p1X, p1Y, p1X + cos(angleToFocus) * fMag, p1Y + sin(angleToFocus) * fMag, sw);
            drawArrow(focusX, focusY, focusX - cos(angleToFocus) * fMag, focusY - sin(angleToFocus) * fMag, sw);
        }

        // Central star glowing corona
        noStroke();
        float pulse = sin(t * 6f) * 4f;
        for (int r = 4; r >= 1; r--) {
            fill(255, 12 + r * 10);
            circle(focusX, focusY, (100f + pulse + r * 16f) / zoom);
        }
        fill(255);
        circle(focusX, focusY, 100f / zoom);

        // Orbital comet trail for planet 1
        if (b < 14.8f) {
            noStroke();
            for (int i = 10; i > 0; i--) {
                float b_hist = b - (i * 0.025f);
                if (b_hist < 0) b_hist += cycleTime * w;
                float M_hist = (((b_hist % 2.0f) / 2.0f) * TWO_PI) + PI;
                float E_hist = solveKepler(M_hist, e1);
                float hX = cx - a1 * cos(E_hist);
                float hY = cy + b1_el * sin(E_hist);
                float alpha = map(i, 0, 10, 160, 0);
                fill(255, alpha);
                circle(hX, hY, map(i, 0, 10, 12, 3) / zoom);
            }

            fill(255);
            circle(p1X, p1Y, 12 / zoom);
        }

        if (b >= 2.0f && b < 4.0f) {
            float mu = 80000f;
            float vSq = mu * (2f / r1 - 1f / a1);
            float K = vSq * 0.002f * (a1 / 160f);
            float U = -(mu / r1) * 0.005f * (a1 / 160f);
            float E_total = K + U;

            float barX = cx + 640f;

            noStroke();
            fill(255, 220);
            rect(barX, cy, 50, -K * 40);

            fill(255, 120);
            rect(barX + 80, cy, 50, -U * 40);

            fill(255, 255);
            rect(barX + 160, cy, 50, -E_total * 40);
        }

        if (b >= 12.0f) {
            float a2 = a1 * 0.58f;
            float e2 = 0.25f;
            float b2_el = a2 * sqrt(1.0f - e2 * e2);
            float c2 = a2 * e2;

            if (b < 14.8f) {
                stroke(255, 40);
                strokeWeight(1.0f / zoom);
                noFill();
                ellipse(focusX + c2, cy, a2 * 2, b2_el * 2);
            }

            float T2 = 2.0f * pow(a2 / a1, 1.5f);
            float M2 = (((b % T2) / T2) * TWO_PI) + PI;
            float E2 = solveKepler(M2, e2);
            float p2X_orb = (focusX + c2) - a2 * cos(E2);
            float p2Y_orb = cy + b2_el * sin(E2);

            if (b < 14.0f) {
                fill(255, 200);
                circle(p2X_orb, p2Y_orb, 9 / zoom);
            } else if (b < 14.8f) {
                float subProg = map(b, 14.0f, 14.8f, 0.0f, 1.0f);
                subProg = subProg * subProg * (3.0f - 2.0f * subProg);
                float p2X = lerp(p2X_orb, p1X, subProg);
                float p2Y = lerp(p2Y_orb, p1Y, subProg);
                fill(255, 200);
                circle(p2X, p2Y, 9 / zoom);
            } else {
                // Shockwaves and radiating debris sparks on collision
                float sparkAge = b - 14.8f;

                if (sparkAge < 0.6f) {
                    float shockProg = map(sparkAge, 0.0f, 0.6f, 0.0f, 1.0f);
                    stroke(255, 255 * (1.0f - shockProg));
                    strokeWeight(3.0f / zoom);
                    noFill();
                    circle(impactX, impactY, shockProg * 250f / zoom);
                    circle(impactX, impactY, shockProg * 420f / zoom);

                    // Ejecta sparks
                    noStroke();
                    int numSparks = 20;
                    for (int s = 0; s < numSparks; s++) {
                        float spAngle = (s / (float) numSparks) * TWO_PI + (s * 0.7f);
                        float spSpeed = lerp(120f, 320f, (s % 5) / 5.0f);
                        float spX = impactX + cos(spAngle) * spSpeed * sparkAge;
                        float spY = impactY + sin(spAngle) * spSpeed * sparkAge;
                        fill(255, map(sparkAge, 0f, 0.6f, 255f, 0f));
                        circle(spX, spY, map(sparkAge, 0f, 0.6f, 5f, 1f) / zoom);
                    }
                }

                float escProg = map(b, 14.8f, 16.0f, 0.0f, 1.0f);
                stroke(255, 140);
                strokeWeight(1.8f / zoom);
                noFill();
                beginShape();
                for (float hp = 0; hp <= escProg; hp += 0.03f) {
                    vertex(impactX + hp * (a1 * 2.5f), impactY - hp * hp * (a1 * 1.3f) - hp * (a1 * 0.7f));
                }
                endShape();

                fill(255);
                noStroke();
                circle(escX, escY, 11 / zoom);
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