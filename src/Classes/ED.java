package Classes;

import processing.core.PApplet;

public class ED extends PApplet {

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
        if (b < 2.0f) {
            drawPart1(b, cx, cy);
        } else {
            drawPart2(b - 2.0f, cx, cy);
        }
        popMatrix();

        if (elogo != null) {
            elogo.display(this, b, logoTransparency);
        }

        popStyle();
    }

    private void drawPart1(float b, float cx, float cy) {
        float topY = height * 0.25f;
        float bottomY = height * 0.75f;

        float uX = width * 0.15f;
        float leftX = width * 0.15f;
        float rightX = width * 0.85f;

        float rUranium = 65f;
        float rFluorine = 35f;
        float rCarbon = 30f;
        float rAlpha = 16f;
        float rParticle = 10f;
        float rNeutrino = 6f;

        stroke(255);
        strokeWeight(2.5f);
        noFill();
        circle(uX, topY, rUranium * 2f);

        if (b >= 0f) {
            float alphaStartX = uX + rUranium;
            float currentAlphaX = alphaStartX + b * (width * 0.4f);

            stroke(255, 120);
            strokeWeight(1.2f);
            line(alphaStartX, topY, currentAlphaX, topY);

            stroke(255);
            strokeWeight(2.5f);
            fill(255);
            circle(currentAlphaX, topY, rAlpha * 2f);
        }

        stroke(255);
        strokeWeight(2.5f);
        noFill();
        circle(leftX, bottomY, rFluorine * 2f);

        if (b >= 0.5f) {
            float pPos = constrain(b - 0.5f, 0f, 1.0f);
            float startX = leftX + rFluorine;
            float eX = lerp(startX, cx, pPos);
            float eY = lerp(bottomY, cy, pPos);

            if (pPos < 1.0f) {
                stroke(255, 120);
                strokeWeight(1.2f);
                line(startX, bottomY, eX, eY);

                fill(255);
                noStroke();
                circle(eX, eY, rParticle * 2f);
            }

            float nuDist = (b - 0.5f) * (width * 0.25f);
            float nuX = leftX - rFluorine - nuDist * 0.8f;
            float nuY = bottomY + nuDist * 0.6f;

            stroke(255, 120);
            strokeWeight(1.2f);
            line(leftX - rFluorine, bottomY, nuX, nuY);
            fill(255);
            noStroke();
            circle(nuX, nuY, rNeutrino * 2f);
        }

        stroke(255);
        strokeWeight(2.5f);
        noFill();
        circle(rightX, bottomY, rCarbon * 2f);

        if (b >= 1.0f) {
            float pElec = constrain((b - 1.0f) / 0.5f, 0f, 1.0f);
            float startX = rightX - rCarbon;
            float eX = lerp(startX, cx, pElec);
            float eY = lerp(bottomY, cy, pElec);

            if (pElec < 1.0f) {
                stroke(255, 120);
                strokeWeight(1.2f);
                line(startX, bottomY, eX, eY);

                fill(255);
                noStroke();
                circle(eX, eY, rParticle * 2f);
            }

            float nuDist = (b - 1.0f) * (width * 0.25f);
            float nuX = rightX + rCarbon + nuDist * 0.8f;
            float nuY = bottomY + nuDist * 0.6f;

            stroke(255, 120);
            strokeWeight(1.2f);
            line(rightX + rCarbon, bottomY, nuX, nuY);
            fill(255);
            noStroke();
            circle(nuX, nuY, rNeutrino * 2f);
        }

        if (b >= 1.5f) {
            float gDist = (b - 1.5f) * (height * 0.8f);

            stroke(255);
            strokeWeight(3f);
            noFill();

            beginShape();
            for (float wy = 0; wy >= -gDist; wy -= 4f) {
                float wx = sin(wy * 0.15f - t * 30f) * 16f;
                vertex(cx + wx, cy + wy);
            }
            endShape();

            beginShape();
            for (float wy = 0; wy <= gDist; wy += 4f) {
                float wx = sin(wy * 0.15f - t * 30f) * 16f;
                vertex(cx + wx, cy + wy);
            }
            endShape();
        }
    }

    private void drawPart2(float localB, float cx, float cy) {
        float halfLife = 0.5f;
        float expectedActive = pow(0.5f, localB / halfLife);

        float gLeft = width * 0.10f;
        float gRight = width * 0.45f;
        float gTop = height * 0.20f;
        float gBottom = height * 0.80f;

        stroke(100);
        strokeWeight(2.5f);
        line(gLeft, gTop, gLeft, gBottom);
        line(gLeft, gBottom, gRight, gBottom);

        noFill();
        stroke(255);
        strokeWeight(4f);
        beginShape();
        for (float x = 0; x <= 2.0f; x += 0.02f) {
            float px = map(x, 0, 2.0f, gLeft, gRight);
            float py = map(pow(0.5f, x / halfLife), 0, 1.0f, gBottom, gTop);
            vertex(px, py);
        }
        endShape();

        float dotX = map(localB, 0, 2.0f, gLeft, gRight);
        float dotY = map(expectedActive, 0, 1.0f, gBottom, gTop);

        fill(15);
        stroke(255);
        strokeWeight(3f);
        circle(dotX, dotY, 22f);

        fill(255);
        noStroke();
        circle(dotX, dotY, 10f);

        float sLeft = width * 0.55f;
        float sRight = width * 0.90f;
        float sTop = height * 0.20f;
        float sBottom = height * 0.80f;

        int cols = 30;
        int rows = 30;

        noStroke();
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                float px = lerp(sLeft, sRight, i / (float) (cols - 1));
                float py = lerp(sTop, sBottom, j / (float) (rows - 1));

                float u = getHash(i, j);

                if (u < expectedActive) {
                    fill(255);
                } else {
                    fill(35);
                }

                circle(px, py, 9f);
            }
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
        PApplet.main("Classes.ED");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}