package Classes;

import processing.core.PApplet;

public class EF extends PApplet {

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

        background(0);

        float b = t * w;

        transY = b % 1f;
        logoTransparency = (float) (255 * (-Math.pow(transY, 2) + 2 * (transY)));

        float cx = width * 0.50f;
        float cy = height * 0.50f;

        pushMatrix();
        if (b < 2.0f) {
            drawSchrodinger(b, cx, cy);
        } else {
            drawHeisenberg(b - 2.0f, cx, cy);
        }
        popMatrix();

        if (elogo != null) {
            elogo.display(this, b, logoTransparency);
        }

        popStyle();
    }

    private void drawSchrodinger(float localB, float cx, float cy) {
        fill(255);
        noStroke();
        circle(cx, cy, 24f);

        noFill();
        stroke(255, 75);
        strokeWeight(2.0f);

        float[] shellRadii = { 120f, 240f, 360f };
        for (float baseR : shellRadii) {
            beginShape();
            for (float a = 0; a < TWO_PI; a += 0.04f) {
                float wave = sin(a * 6f + t * 8f) * 18f;
                float r = baseR + wave;
                vertex(cx + r * cos(a), cy + r * sin(a));
            }
            endShape(CLOSE);
        }

        int numPoints = 1800;
        noStroke();
        for (int i = 0; i < numPoints; i++) {
            float h1 = getHash(i, 1);
            float h2 = getHash(i, 2);
            float h3 = getHash(i, 3);

            float angle = h2 * TWO_PI;
            float r;
            float prob;

            if (h1 < 0.35f) {
                r = h3 * 105f;
                prob = exp(-r / 35f);
            } else if (h1 < 0.70f) {
                r = 180f + (h3 - 0.5f) * 100f;
                prob = exp(-pow(r - 220f, 2) / 1400f);
            } else {
                r = 320f + (h3 - 0.5f) * 140f;
                float pLobe = pow(cos(angle + t * 0.8f), 2);
                prob = exp(-pow(r - 350f, 2) / 2000f) * pLobe;
            }

            float px = cx + r * cos(angle);
            float py = cy + r * sin(angle);
            float alpha = prob * 255f;

            if (alpha > 15f) {
                fill(255, alpha);
                circle(px, py, 4.2f);
            }
        }
    }

    private void drawHeisenberg(float localB, float cx, float cy) {
        float startX = cx - width * 0.4f;
        float endX = cx + width * 0.4f;

        float progress;
        float currentVelocity;

        if (localB < 0.8f) {
            float normB = localB / 0.8f;
            progress = normB * 0.45f;
            currentVelocity = 1.0f;
        } else {
            float normB = (localB - 0.8f) / 1.2f;
            progress = 0.45f + (1.0f - pow(1.0f - normB, 2f)) * 0.55f;
            currentVelocity = max(0.05f, 1.0f - normB);
        }

        float posX = lerp(startX, endX, progress);
        float posY = cy;

        float deltaX = lerp(30f, 420f, localB / 2.0f);
        float deltaP = lerp(110f, 35f, localB / 2.0f);

        int numGhosts = 110;
        for (int i = 0; i < numGhosts; i++) {
            float hx = (getHash(i, 10) - 0.5f) * 2f;
            float hy = (getHash(i, 11) - 0.5f) * 2f;
            float distFactor = exp(-(hx * hx + hy * hy));

            float gx = posX + hx * deltaX;
            float gy = posY + hy * (deltaX * 0.5f);

            float ghostSize = map(distFactor, 0f, 1f, 3f, 15f);
            float alpha = distFactor * map(deltaX, 30f, 420f, 240, 35);

            fill(255, alpha);
            noStroke();
            circle(gx, gy, ghostSize);
        }

        fill(255);
        circle(posX, posY, 12f);

        float baseSpeed = 160f * currentVelocity;

        int numArrows = 12;
        for (int i = 0; i < numArrows; i++) {
            float normI = i / (float) (numArrows - 1);
            float speedVariation = lerp(-0.45f, 0.45f, normI);
            float offsetParallel = (normI - 0.5f) * map(deltaP, 35f, 110f, 16f, 48f);

            float startArrowX = posX;
            float startArrowY = posY + offsetParallel;

            float arrowLen = max(20f, baseSpeed * (1.0f + speedVariation));
            float endArrowX = startArrowX + arrowLen;
            float endArrowY = startArrowY;

            float alpha = map(abs(normI - 0.5f), 0f, 0.5f, 255, 90);
            stroke(255, alpha);
            strokeWeight(2.2f);
            line(startArrowX, startArrowY, endArrowX, endArrowY);

            float headLen = max(14f, min(24f, arrowLen * 0.35f));
            strokeWeight(2.8f);
            line(endArrowX, endArrowY, endArrowX - headLen, endArrowY - headLen * 0.5f);
            line(endArrowX, endArrowY, endArrowX - headLen, endArrowY + headLen * 0.5f);
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
        float finalX = (width / 2f) + 865.3f;
        float finalY = (height / 2f) - 458.1f;
        float finalW = (height / 2f) - 381.75f;
        float finalH = (height / 2f) - 381.75f;
        elogo = new Elogo(finalX, finalY, finalW, finalH);
        noCursor();
    }

    public static void main(String[] args) {
        PApplet.main("Classes.EF");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}