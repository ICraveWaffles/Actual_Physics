package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class EA extends PApplet {

    float t = 0;
    float w = 100f / 120f;
    PFont ntr;
    float logoTransparency;
    float transY;

    public static Elogo blogo;

    float cycleTime = 4.8f;

    float[] nuxX = new float[197];
    float[] nuxY = new float[197];
    boolean[] isProton = new boolean[197];

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

        if (blogo != null) {
            blogo.display(this, b, logoTransparency);
        }

        popStyle();
    }

    private void drawScene(float b, float cx, float cy) {
        float topCutY = height * 0.18f;
        float bottomCutY = height * 0.82f;

        if (b < 2.0f) {
            float localB = b;
            int currentA = constrain((int) map(localB, 0f, 2.0f, 1, 197), 1, 197);

            pushMatrix();
            translate(cx, cy);

            float nucleonSize = 52f;
            strokeWeight(1.5f);
            stroke(255);

            for (int i = 0; i < currentA; i++) {
                float jx = sin(t * 22f + i * 1.7f) * 2.8f;
                float jy = cos(t * 19f + i * 2.3f) * 2.8f;

                float px = nuxX[i] + jx;
                float py = nuxY[i] + jy;

                if (isProton[i]) {
                    fill(0);
                } else {
                    fill(130);
                }
                circle(px, py, nucleonSize);
            }

            popMatrix();

        } else {
            float localB = b - 2.0f;

            float zoomProgress = constrain(map(localB, 0.0f, 0.5f, 0f, 1f), 0f, 1f);
            float scaleFactor = lerp(1.0f, 0.22f, zoomProgress);

            pushMatrix();
            translate(cx, cy);
            scale(scaleFactor);

            float foilX = 0;
            float spacingY = 80f;

            for (int yIdx = -20; yIdx <= 20; yIdx++) {
                if (yIdx == 0) continue;
                drawGoldNucleusMini(foilX, yIdx * spacingY);
            }

            drawGoldNucleusMini(foilX, 0);

            float alphaProgress = constrain(map(localB, 0.3f, 1.9f, 0f, 1f), 0f, 1f);

            float xStart = -width * 1.8f;
            float xEnd = width * 1.8f;

            drawUnfazedAlpha(xStart, xEnd, -spacingY * 0.5f, alphaProgress);
            drawDeflectedAlpha(xStart, alphaProgress);
            drawBackscatteredAlpha(xStart, alphaProgress);

            popMatrix();

        }

    }

    private void drawGoldNucleusMini(float x, float y) {
        pushMatrix();
        pushStyle();
        translate(x, y);

        stroke(255);
        strokeWeight(2f);
        fill(40);
        circle(0, 0, 20);

        popStyle();
        popMatrix();
    }

    private void drawAlphaParticle(float x, float y) {
        noStroke();
        fill(255);
        circle(x, y, 28f);
        fill(0);
        circle(x, y, 14f);
    }

    private void drawUnfazedAlpha(float xStart, float xEnd, float y, float p) {
        pushStyle();
        float currentX = lerp(xStart, xEnd, p);

        stroke(200);
        strokeWeight(5f);
        line(xStart, y, currentX, y);

        drawAlphaParticle(currentX, y);
        popStyle();
    }

    private float[] getDeflectedPos(float p, float xStart) {
        float angle = -radians(20);
        float cosA = cos(angle);
        float sinA = sin(angle);

        float p0x = -50f, p0y = -22f;
        float p1x = -15f, p1y = -22f;
        float p2x = p1x + 20f * cosA;
        float p2y = p1y + 20f * sinA;

        if (p <= 0.44f) {
            float subP = p / 0.44f;
            float x = lerp(xStart, p0x, subP);
            float y = p0y;
            return new float[]{x, y};
        } else if (p <= 0.50f) {
            float t = (p - 0.44f) / 0.06f;
            float omT = 1f - t;
            float x = omT * omT * p0x + 2f * omT * t * p1x + t * t * p2x;
            float y = omT * omT * p0y + 2f * omT * t * p1y + t * t * p2y;
            return new float[]{x, y};
        } else {
            float subP = (p - 0.50f) / 0.50f;
            float dist = subP * (width * 1.8f);
            float x = p2x + dist * cosA;
            float y = p2y + dist * sinA;
            return new float[]{x, y};
        }
    }

    private void drawDeflectedAlpha(float xStart, float p) {
        pushStyle();
        stroke(200);
        strokeWeight(5f);
        noFill();

        if (p > 0.001f) {
            beginShape();
            int steps = (int) map(p, 0f, 1f, 5, 80);
            for (int i = 0; i <= steps; i++) {
                float sampleP = map(i, 0, steps, 0f, p);
                float[] pos = getDeflectedPos(sampleP, xStart);
                vertex(pos[0], pos[1]);
            }
            endShape();
        }

        float[] curr = getDeflectedPos(p, xStart);
        drawAlphaParticle(curr[0], curr[1]);
        popStyle();
    }

    private float[] getBackscatteredPos(float p, float xStart) {
        float angle = radians(155f);
        float cosA = cos(angle);
        float sinA = sin(angle);

        float p0x = -50f, p0y = 0f;
        float p1x = -18f, p1y = 0f;
        float p2x = p1x + 20f * cosA;
        float p2y = p1y + 20f * sinA;

        if (p <= 0.44f) {
            float subP = p / 0.44f;
            float x = lerp(xStart, p0x, subP);
            float y = p0y;
            return new float[]{x, y};
        } else if (p <= 0.50f) {
            float t = (p - 0.44f) / 0.06f;
            float omT = 1f - t;
            float x = omT * omT * p0x + 2f * omT * t * p1x + t * t * p2x;
            float y = omT * omT * p0y + 2f * omT * t * p1y + t * t * p2y;
            return new float[]{x, y};
        } else {
            float subP = (p - 0.50f) / 0.50f;
            float dist = subP * (width * 1.8f);
            float x = p2x + dist * cosA;
            float y = p2y + dist * sinA;
            return new float[]{x, y};
        }
    }

    private void drawBackscatteredAlpha(float xStart, float p) {
        pushStyle();
        stroke(200);
        strokeWeight(5f);
        noFill();

        if (p > 0.001f) {
            beginShape();
            int steps = (int) map(p, 0f, 1f, 5, 80);
            for (int i = 0; i <= steps; i++) {
                float sampleP = map(i, 0, steps, 0f, p);
                float[] pos = getBackscatteredPos(sampleP, xStart);
                vertex(pos[0], pos[1]);
            }
            endShape();
        }

        float[] curr = getBackscatteredPos(p, xStart);
        drawAlphaParticle(curr[0], curr[1]);
        popStyle();
    }

    private void drawHUDText(String title, String subtitle, float x, float y) {
        pushStyle();
        fill(255);
        textFont(ntr);
        textSize(28);
        text(title, x, y);
        fill(160);
        textSize(18);
        text(subtitle, x, y + 28);
        popStyle();
    }



    public void settings() {
        fullScreen();
    }

    public void setup() {
        ntr = createFont("sans-serif", 30);

        float phi = (1.0f + sqrt(5.0f)) / 2.0f;
        for (int i = 0; i < 197; i++) {
            float r = 15f * sqrt(i + 1);
            float theta = i * TWO_PI * phi;
            nuxX[i] = r * cos(theta);
            nuxY[i] = r * sin(theta);

            isProton[i] = ((i * 79) / 197) != (((i + 1) * 79) / 197);
        }

        float finalX = (width / 2f) + 865.3f;
        float finalY = (height / 2f) - 458.1f;
        float finalW = (height / 2f) - 381.75f;
        float finalH = (height / 2f) - 381.75f;
        blogo = new Elogo(finalX, finalY, finalW, finalH);
        noCursor();
    }

    public static void main(String[] args) {
        PApplet.main("Classes.EA");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}