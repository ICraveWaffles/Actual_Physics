package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class Cal3 extends PApplet {

    float t = 0;
    float w = 5/6f;
    PFont ntr;
    float logoTransparency;
    float transY;

    public static Blogo alogo;

    float maxBeats = 4f;

    public void draw() {
        pushStyle();

        frameRate = 30;
        t = frameCount / (float) frameRate;
        float b = t * w;

        if (b >= maxBeats) {
            frameCount = 0;
            t = 0;
            b = 0;
        }

        background(0);

        textFont(ntr);
        transY = b % 1f;
        logoTransparency = (float) (255 * (-Math.pow(transY, 2) + 2 * (transY)));
        alogo.display(this, b, logoTransparency);

        float earthX = width * 0.82f;
        float earthY = height * 0.52f;
        float earthR = 65f;

        float targetFocusX = earthX - earthR * 0.55f;
        float targetFocusY = earthY - earthR * 0.55f;

        float zoomStartBeat = 3.2f;
        float zoomProgress = constrain((b - zoomStartBeat) / (maxBeats - zoomStartBeat), 0f, 1f);
        float easeZoom = zoomProgress * zoomProgress * (3 - 2 * zoomProgress);

        float scaleVal = lerp(1.0f, 8.5f, easeZoom);
        float currentFocusX = lerp(width * 0.50f, targetFocusX, easeZoom);
        float currentFocusY = lerp(height * 0.50f, targetFocusY, easeZoom);

        pushMatrix();
        translate(width * 0.5f, height * 0.5f);
        scale(scaleVal);
        translate(-currentFocusX, -currentFocusY);

        drawWorldScene(b, scaleVal, easeZoom, earthX, earthY, earthR);

        popMatrix();

        popStyle();
    }

    void drawWorldScene(float b, float scaleVal, float easeZoom, float earthX, float earthY, float earthR) {
        float sunX = width * 0.12f;
        float sunY = height * 0.42f;

        drawSun(sunX, sunY, b, scaleVal);

        float distTotal = earthX - sunX;
        float x1 = sunX + distTotal * 0.22f;
        float x2 = sunX + distTotal * 0.44f;
        float x3 = sunX + distTotal * 0.66f;

        float h1 = height * 0.08f;
        float w1 = h1 * 0.8f;

        stroke(255, 100 * (1f - easeZoom));
        strokeWeight(1.5f / scaleVal);
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                line(sunX, sunY, x3 + w1 * 1.5f, sunY + j * (h1 * 0.85f) + (earthY - sunY) * 0.66f);
            }
        }

        if (b >= 0.5f) {
            float alpha1 = map(constrain(b, 0.5f, 0.8f), 0.5f, 0.8f, 0, 255) * (1f - easeZoom);
            drawGridScreen(x1, sunY + (earthY - sunY) * 0.22f, w1, h1, 1, alpha1, scaleVal);
        }

        if (b >= 1.0f) {
            float alpha2 = map(constrain(b, 1.0f, 1.3f), 1.0f, 1.3f, 0, 255) * (1f - easeZoom);
            drawGridScreen(x2, sunY + (earthY - sunY) * 0.44f, w1 * 2f, h1 * 2f, 2, alpha2, scaleVal);
        }

        if (b >= 1.5f) {
            float alpha3 = map(constrain(b, 1.5f, 1.8f), 1.5f, 1.8f, 0, 255) * (1f - easeZoom);
            drawGridScreen(x3, sunY + (earthY - sunY) * 0.66f, w1 * 3f, h1 * 3f, 3, alpha3, scaleVal);
        }


        drawEarthWithAtmosphere(earthX, earthY, earthR, b, scaleVal, easeZoom);
    }

    void drawSun(float sx, float sy, float b, float scaleVal) {
        fill(255);
        noStroke();
        ellipse(sx, sy, 65, 65);

        stroke(255, 180);
        strokeWeight(2f / scaleVal);
        int rays = 12;
        for (int i = 0; i < rays; i++) {
            float ang = i * (TWO_PI / rays) + b * 0.5f;
            float rInner = 36;
            float rOuter = 50 + sin(b * 15f + i) * 6f;
            line(sx + cos(ang) * rInner, sy + sin(ang) * rInner,
                    sx + cos(ang) * rOuter, sy + sin(ang) * rOuter);
        }
    }

    void drawGridScreen(float x, float y, float w, float h, int gridN, float alpha, float scaleVal) {
        if (alpha <= 1) return;

        pushMatrix();
        translate(x, y);

        float skewX = w * 0.35f;
        float skewY = -h * 0.25f;

        stroke(255, alpha);
        strokeWeight(2.0f / scaleVal);
        fill(0, alpha * 0.8f);

        beginShape();
        vertex(-w / 2f, -h / 2f);
        vertex(w / 2f + skewX, -h / 2f + skewY);
        vertex(w / 2f + skewX, h / 2f + skewY);
        vertex(-w / 2f, h / 2f);
        endShape(CLOSE);

        stroke(255, alpha * 0.7f);
        strokeWeight(1.2f / scaleVal);

        for (int i = 1; i < gridN; i++) {
            float frac = i / (float) gridN;

            float vx1 = lerp(-w / 2f, w / 2f + skewX, frac);
            float vy1 = lerp(-h / 2f, -h / 2f + skewY, frac);
            float vx2 = lerp(-w / 2f, w / 2f + skewX, frac);
            float vy2 = lerp(h / 2f, h / 2f + skewY, frac);
            line(vx1, vy1, vx2, vy2);

            float hx1 = lerp(-w / 2f, -w / 2f + skewX, frac);
            float hy1 = lerp(-h / 2f, h / 2f, frac);
            float hx2 = lerp(w / 2f + skewX, w / 2f + skewX, frac);
            float hy2 = lerp(-h / 2f + skewY, h / 2f + skewY, frac);
            line(hx1, hy1, hx2, hy2);
        }

        popMatrix();
    }

    void drawEarthWithAtmosphere(float ex, float ey, float R, float b, float scaleVal, float easeZoom) {
        float atmosThickness = 22f;

        stroke(255, 120 + easeZoom * 105);
        strokeWeight((1.5f + easeZoom * 2f) / scaleVal);
        noFill();
        ellipse(ex, ey, (R + atmosThickness) * 2, (R + atmosThickness) * 2);

        stroke(255);
        strokeWeight(2.5f / scaleVal);
        fill(0);
        ellipse(ex, ey, R * 2, R * 2);

        if (easeZoom > 0.2f) {
            stroke(255, 150 * easeZoom);
            strokeWeight(1.0f / scaleVal);
            for (float a = 3.14159f; a < 4.71238f; a += 0.08f) {
                float px1 = ex + R * cos(a);
                float py1 = ey + R * sin(a);
                float px2 = ex + (R - 6f) * cos(a + 0.04f);
                float py2 = ey + (R - 6f) * sin(a + 0.04f);
                line(px1, py1, px2, py2);
            }
        }

        drawInsolationAndAlbedo(ex, ey, R, scaleVal, easeZoom, b);
    }

    void drawInsolationAndAlbedo(float ex, float ey, float R, float scaleVal, float easeZoom, float b) {
        float globalAlpha = (1f - easeZoom / 0.8f) * 255;
        if (globalAlpha <= 0) return;

        stroke(255, globalAlpha);
        strokeWeight(4.5f / scaleVal);

        int numInsolationRays = 5;
        float raySpacing = 20f;
        float startX = ex - R - 130f;

        if (b >= 2f && b<= 3.5f) {
            for (int i = 0; i < numInsolationRays; i++) {
                float yOffset = (i - (numInsolationRays - 1) / 2f) * raySpacing;
                float iy = ey + yOffset;
                if (abs(yOffset) < R) {
                    float targetX = ex - sqrt(R * R - yOffset * yOffset);
                    drawArrow(startX, iy, targetX, iy, scaleVal, 16f);
                }
            }
        }

        if (b >= 2.5f && b<= 3.5f) {
            int numAlbedoRays = 4;
            for (int i = 0; i < numAlbedoRays; i++) {
                float angle = PI + 0.22f + (i * 0.20f);
                float bx = ex + R * cos(angle);
                float by = ey + R * sin(angle);

                float outX = bx + 70f * cos(angle);
                float outY = by + 70f * sin(angle);

                drawArrow(bx, by, outX, outY, scaleVal, 16f);
            }
        }
    }

    void drawArrow(float x1, float y1, float x2, float y2, float scaleVal, float headSize) {
        line(x1, y1, x2, y2);

        float angle = atan2(y2 - y1, x2 - x1);
        float h = headSize / scaleVal;

        pushMatrix();
        translate(x2, y2);
        rotate(angle);

        fill(255);
        stroke(255);
        strokeWeight(2.0f / scaleVal);
        triangle(0, 0, -h, -h * 0.55f, -h, h * 0.55f);

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

        alogo = new Blogo(finalX, finalY, finalW, finalH);

        noCursor();
    }

    public static void main(String[] args) {
        PApplet.main("Classes.Cal3");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}