package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class EG extends PApplet {

    float t = 0;
    float w = 100f / 120f;
    float logoTransparency;
    float transY;

    public static Elogo elogo;
    PFont tnrFont;

    float cycleTime = 4.8f;

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

        float cx = width * 0.50f;
        float cy = height * 0.50f;

        pushMatrix();
        if (b < 2.0f) {
            drawFission(b, cx, cy);
        } else {
            drawNuclearPlant(b - 2.0f, cx, cy);
        }
        popMatrix();

        if (elogo != null) {
            elogo.display(this, b, logoTransparency);
        }

        popStyle();
    }

    private void drawFission(float localB, float cx, float cy) {
        float neutronStartX = cx - width * 0.38f;
        float hitTime = 0.45f;
        float splitTime = 0.85f;
        int numNucleons = 52;

        if (localB < hitTime) {
            float normHit = localB / hitTime;
            float nx = lerp(neutronStartX, cx - 30f, normHit);

            stroke(255, 110);
            strokeWeight(1.5f);
            line(neutronStartX, cy, nx, cy);

            fill(255);
            noStroke();
            circle(nx, cy, 13f);

            drawNucleusCluster(cx, cy, 1.0f, 1.0f, 0, numNucleons, 0f);
        } else if (localB < splitTime) {
            float norm = (localB - hitTime) / (splitTime - hitTime);
            float stretchX = 1.0f + norm * 0.85f;
            float pinchY = 1.0f - norm * 0.38f;

            float wobble = sin(norm * TWO_PI * 5f) * 6f;
            drawNucleusCluster(cx, cy + wobble, stretchX, pinchY, 0, numNucleons, norm);
        } else {
            float norm = (localB - splitTime) / (2.0f - splitTime);
            float sepDist = pow(norm, 0.8f) * (height * 0.28f);

            float frag1X = cx - sepDist * 0.866f;
            float frag1Y = cy - sepDist * 0.400f;

            float frag2X = cx + sepDist * 0.866f;
            float frag2Y = cy + sepDist * 0.400f;

            drawNucleusCluster(frag1X, frag1Y, 0.82f, 0.82f, 100, 30, 1.0f);
            drawNucleusCluster(frag2X, frag2Y, 0.72f, 0.72f, 200, 22, 1.0f);

            float nDist = norm * (height * 0.36f);
            float[][] nDir = { {-0.30f, -0.92f}, {0.92f, -0.35f}, {-0.40f, 0.88f} };
            for (int i = 0; i < 3; i++) {
                float nx = cx + nDir[i][0] * nDist;
                float ny = cy + nDir[i][1] * nDist;

                stroke(255, 130);
                strokeWeight(1.2f);
                line(cx, cy, nx, ny);

                fill(255);
                noStroke();
                circle(nx, ny, 11f);
            }

            noFill();
            float alphaWave = map(norm, 0f, 1f, 255, 0);
            stroke(255, alphaWave);
            strokeWeight(2.5f);
            circle(cx, cy, norm * (height * 0.78f));
        }
    }

    private void drawNucleusCluster(float x, float y, float scaleX, float scaleY, int seedOffset, int count, float excitation) {
        pushMatrix();
        translate(x, y);
        scale(scaleX, scaleY);

        for (int i = 0; i < count; i++) {
            float h1 = getHash(i + seedOffset, 1);
            float h2 = getHash(i + seedOffset, 2);

            float r = sqrt(h1) * 45f;
            float a = h2 * TWO_PI;

            float px = r * cos(a);
            float py = r * sin(a);

            if (excitation > 0f) {
                px += (getHash(i + seedOffset, 3) - 0.5f) * excitation * 12f;
                py += (getHash(i + seedOffset, 4) - 0.5f) * excitation * 12f;
            }

            if (i % 2 == 0) {
                fill(255);
                stroke(20);
                strokeWeight(1.2f);
            } else {
                fill(110);
                stroke(255);
                strokeWeight(1.2f);
            }
            circle(px, py, 14f);
        }
        popMatrix();
    }

    private void drawNuclearPlant(float localB, float cx, float cy) {
        float normB = localB / 2.0f;
        float easeZoom = 1.0f - pow(1.0f - normB, 3f);

        float currentScale = lerp(2.20f, 0.95f, easeZoom);

        float focusX = cx - width * 0.22f;
        float focusY = cy + height * 0.05f;

        float plantCenterX = lerp(focusX, cx, easeZoom);
        float plantCenterY = lerp(focusY, cy, easeZoom);

        pushMatrix();
        translate(plantCenterX, plantCenterY);
        scale(currentScale);
        translate(-cx, -cy);

        stroke(255);
        strokeWeight(2.0f);
        noFill();

        float groundY = cy + 120f;

        float contX = cx - 320f;
        float contY = groundY;
        float contW = 220f;
        float contH = 280f;

        rect(contX - contW / 2f, contY - contH, contW, contH);
        arc(contX, contY - contH, contW, contW, PI, TWO_PI);

        float rpvX = contX - 30f;
        float rpvY = contY - 90f;
        float rpvW = 70f;
        float rpvH = 120f;

        rect(rpvX - rpvW / 2f, rpvY - rpvH / 2f, rpvW, rpvH, 15f);

        fill(255, 40);
        rect(rpvX - rpvW / 2f + 10f, rpvY - 30f, rpvW - 20f, 60f);
        noFill();

        stroke(255);
        strokeWeight(2.5f);
        for (float rx = rpvX - 18f; rx <= rpvX + 18f; rx += 12f) {
            line(rx, rpvY - rpvH / 2f - 25f, rx, rpvY + 15f);
        }

        float sgX = contX + 55f;
        float sgY = contY - 110f;
        rect(sgX - 25f, sgY - 70f, 50f, 140f, 12f);

        strokeWeight(4.0f);
        stroke(255, 180);
        line(rpvX + rpvW / 2f, rpvY - 20f, sgX - 25f, rpvY - 20f);
        line(sgX - 25f, rpvY + 30f, rpvX + rpvW / 2f, rpvY + 30f);

        float turbX = cx + 30f;
        float turbY = groundY;
        float turbW = 280f;
        float turbH = 180f;

        strokeWeight(2.0f);
        stroke(255);
        rect(turbX - 40f, turbY - turbH, turbW, turbH);

        float shaftY = turbY - 110f;
        strokeWeight(3.0f);
        line(turbX - 20f, shaftY, turbX + 180f, shaftY);

        strokeWeight(1.8f);
        for (int i = 0; i < 3; i++) {
            float tx = turbX + i * 35f;
            triangle(tx, shaftY, tx - 12f, shaftY - 25f, tx + 12f, shaftY - 25f);
            triangle(tx, shaftY, tx - 12f, shaftY + 25f, tx + 12f, shaftY + 25f);
        }

        rect(turbX + 110f, shaftY - 30f, 65f, 60f);

        float condX = turbX + 30f;
        float condY = turbY - 35f;
        rect(condX - 50f, condY - 25f, 100f, 50f);
        for (float lx = condX - 40f; lx <= condX + 40f; lx += 10f) {
            line(lx, condY - 20f, lx, condY + 20f);
        }

        strokeWeight(2.0f);
        line(sgX + 25f, sgY - 40f, turbX - 20f, shaftY - 40f);
        line(turbX + 10f, condY + 25f, sgX + 25f, sgY + 40f);

        float coolX = cx + 380f;
        float coolY = groundY;
        float coolH = 260f;

        beginShape();
        vertex(coolX - 90f, coolY);
        bezierVertex(coolX - 40f, coolY - coolH * 0.5f, coolX - 45f, coolY - coolH * 0.8f, coolX - 60f, coolY - coolH);
        vertex(coolX + 60f, coolY - coolH);
        bezierVertex(coolX + 45f, coolY - coolH * 0.8f, coolX + 40f, coolY - coolH * 0.5f, coolX + 90f, coolY);
        endShape();

        noFill();
        stroke(255, 140);
        strokeWeight(1.5f);
        for (int s = 0; s < 5; s++) {
            float steamY = coolY - coolH - 15f - s * 18f - (t * 20f) % 18f;
            float sw = 30f + s * 12f;
            arc(coolX, steamY, sw, 15f, 0, PI);
        }

        strokeWeight(2.0f);
        stroke(255, 180);
        line(condX + 50f, condY - 10f, coolX - 65f, coolY - 60f);
        line(coolX - 75f, coolY - 20f, condX + 50f, condY + 10f);

        strokeWeight(2.5f);
        stroke(255);
        line(cx - 500f, groundY, cx + 500f, groundY);

        drawAnnotations(contX, contY, rpvX, rpvY, sgX, sgY, turbX, shaftY, condX, condY, easeZoom);

        popMatrix();
    }

    private void drawAnnotations(float contX, float contY, float rpvX, float rpvY, float sgX, float sgY, float turbX, float shaftY, float condX, float condY, float easeZoom) {
        float alphaText = map(easeZoom, 0.2f, 1.0f, 0, 255);
        if (alphaText <= 0) return;

        if (tnrFont != null) {
            textFont(tnrFont);
        }
        textSize(13f);

        fill(255, alphaText);
        stroke(255, alphaText * 0.8f);
        strokeWeight(1.0f);

        drawCallout(contX - 40f, contY - 290f, contX - 170f, contY - 340f, "BLINDAJE DE CONTENCIÓN", true);
        drawCallout(rpvX - 10f, rpvY - 70f, rpvX - 170f, rpvY - 120f, "BARRAS DE CONTROL", true);
        drawCallout(rpvX + 5f, rpvY, rpvX - 170f, rpvY + 70f, "MODERADOR", true);
        drawCallout(sgX, sgY - 50f, sgX + 70f, sgY - 140f, "INTERCAMBIADOR DE CALOR", false);
        drawCallout(turbX + 15f, shaftY - 25f, turbX + 15f, shaftY - 120f, "TURBINA DE VAPOR", false);
        drawCallout(turbX + 140f, shaftY, turbX + 180f, shaftY - 80f, "GENERADOR ELÉCTRICO", false);
        drawCallout(condX, condY + 10f, condX - 20f, condY + 100f, "CONDENSADOR", true);
    }

    private void drawCallout(float targetX, float targetY, float textX, float textY, String label, boolean alignRight) {
        fill(255);
        noStroke();
        circle(targetX, targetY, 4.5f);

        stroke(255, 180);
        strokeWeight(1.0f);
        line(targetX, targetY, textX, textY);

        float lineEnd = alignRight ? textX - 25f : textX + 25f;
        line(textX, textY, lineEnd, textY);

        fill(255);
        if (alignRight) {
            textAlign(RIGHT, CENTER);
            text(label, textX - 30f, textY - 2f);
        } else {
            textAlign(LEFT, CENTER);
            text(label, textX + 30f, textY - 2f);
        }
    }

    private float getHash(int i, int j) {
        int n = i * 137 + j * 149;
        n = (n ^ (n >> 16)) * 0x45d9f3b;
        n = (n ^ (n >> 16)) * 0x45d9f3b;
        n = n ^ (n >> 16);
        return (n & 0x7fffffff) / (float) 0x7fffffff;
    }

    public void settings() {
        fullScreen();
    }

    public void setup() {
        tnrFont = createFont("Times New Roman", 13);
        float finalX = (width / 2f) + 865.3f;
        float finalY = (height / 2f) - 458.1f;
        float finalW = (height / 2f) - 381.75f;
        float finalH = (height / 2f) - 381.75f;
        elogo = new Elogo(finalX, finalY, finalW, finalH);
        noCursor();
    }

    public static void main(String[] args) {
        PApplet.main("Classes.EG");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}