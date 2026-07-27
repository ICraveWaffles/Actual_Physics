package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class Dyn4 extends PApplet {

    float t = 0;
    float w = 5/6f;
    PFont ntr;
    float logoTransparency;
    float transY;

    public static Alogo alogo;

    float maxBeats = 8f;

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

        float cx = width / 2f;
        float cy = height / 2f;

        float L = 380f;
        float stringAngle = PI / 4f;

        float R = L * sin(stringAngle);
        float h = L * cos(stringAngle);


        float phi;
        if (b < 4.0f) {
            phi = b * HALF_PI;
        } else if (b < 6.0f) {
            phi = 2.0f * PI + (b - 4.0f) * PI;
        } else {
            float u = (b - 6.0f) / 2.0f;
            if (u > 1.0f) u = 1.0f;
            phi = (-PI) * (u * u * u) + (0.5f * PI) * (u * u) + (2.0f * PI) * u + (4.0f * PI);
        }

        float p = 0f;
        if (b >= 3.75f && b < 4.0f) {
            p = (b - 3.75f) / 0.25f;
            p = constrain(p, 0f, 1f);
            p = p * p * (3f - 2f * p); // Smoothstep
        } else if (b >= 4.0f) {
            p = 1.0f;
        }

        float pivotX_3d = cx;
        float pivotY_3d = cy - h * 0.5f;

        float x_rel = R * cos(phi);
        float z_rel = R * sin(phi);
        float y_rel = h;

        float tilt3d = 0.35f;
        float ballX_3d = cx + x_rel;
        float ballY_3d = pivotY_3d + y_rel + z_rel * tilt3d;

        float pivotX_top = cx;
        float pivotY_top = cy;

        float ballX_top = cx + R * cos(phi);
        float ballY_top = cy + R * sin(phi);

        float pivotX = lerp(pivotX_3d, pivotX_top, p);
        float pivotY = lerp(pivotY_3d, pivotY_top, p);

        float bx = lerp(ballX_3d, ballX_top, p);
        float by = lerp(ballY_3d, ballY_top, p);

        float ellipseX = cx;
        float ellipseY = lerp(pivotY_3d + h, cy, p);
        float ellipseW = R * 2f;
        float ellipseH = lerp(R * 2f * tilt3d, R * 2f, p);

        float ballSize = 75f;

        stroke(255, lerp(40, 180, p));
        strokeWeight(3);
        point(cx, cy);

        noFill();
        stroke(255, 90);
        strokeWeight(2);
        ellipse(ellipseX, ellipseY, ellipseW, ellipseH);

        if (b >= 4.0f) {
            noStroke();
            fill(0, 140, 255, 30);
            arc(cx, cy, R * 2, R * 2, 0, phi % TWO_PI);

            stroke(255, 40);
            strokeWeight(1);
            line(cx - R - 20, cy, cx + R + 20, cy);
            line(cx, cy - R - 20, cx, cy + R + 20);
        }

        if (b < 3.75f) {
            stroke(255, 120);
            strokeWeight(2);
            float dashLength = 10f;
            float spaceLength = 8f;
            float currentY = pivotY;
            float targetVerticalY = pivotY + h;
            while (currentY < targetVerticalY) {
                float nextY = min(currentY + dashLength, targetVerticalY);
                line(pivotX, currentY, pivotX, nextY);
                currentY = nextY + spaceLength;
            }

            noFill();
            stroke(255, 200);
            strokeWeight(2);
            float arcRadius = 70f;
            float stringVectorAngle = atan2(by - pivotY, bx - pivotX);
            float startAngle = min(HALF_PI, stringVectorAngle);
            float stopAngle = max(HALF_PI, stringVectorAngle);
            arc(pivotX, pivotY, arcRadius * 2, arcRadius * 2, startAngle, stopAngle);

        }

        float flash = 0f;
        if (b >= 5.0f && b <= 6.0f) {
            flash = exp(-12.0f * (b - 5.0f) * (b - 5.0f));
        }
        float lineAlpha = lerp(100f, 255f, flash);
        float lineWeight = lerp(2f, 6f, flash);

        stroke(255, lineAlpha);
        strokeWeight(lineWeight);
        line(pivotX, pivotY, bx, by);

        pushMatrix();
        translate(bx, by);

        strokeWeight(3);
        stroke(255);
        fill(0);
        ellipse(0, 0, ballSize, ballSize);

        noFill();
        stroke(255, 150);

        if (b < 3.75f) {
            drawVector(0, 0, 0, 75, 255);

            float dxPivot = pivotX - bx;
            float dyPivot = pivotY - by;
            float distPivot = dist(0, 0, dxPivot, dyPivot);
            if (distPivot > 1f) {
                float tLen = 95f;
                float tx = (dxPivot / distPivot) * tLen;
                float ty = (dyPivot / distPivot) * tLen;
                drawVector(0, 0, tx, ty, 255);

                stroke(255, 120);
                strokeWeight(2);
                line(0, 0, tx, 0);
                line(tx, 0, tx, ty);
            }
        } else if (b >= 4.0f) {
            float toCenterX = cx - bx;
            float toCenterY = cy - by;
            float dCenter = dist(0, 0, toCenterX, toCenterY);
            if (dCenter > 1f) {
                float acLen = 70f;
                float acX = (toCenterX / dCenter) * acLen;
                float acY = (toCenterY / dCenter) * acLen;


                strokeWeight(3);
                drawVector(0, 0, acX, acY, 255);
            }


            if (b >= 6.0f) {

                float tanX = sin(phi);
                float tanY = -cos(phi);
                float atLen = 50f;
                float atX = tanX * atLen;
                float atY = tanY * atLen;

                strokeWeight(3);
                drawVector(0, 0, atX, atY, 255);
            }
        }

        popMatrix();


        popStyle();
    }

    void drawVector(float x0, float y0, float dx, float dy, int c) {
        float x1 = x0 + 2 * dx;
        float y1 = y0 + 2 * dy;
        float len = dist(x0, y0, x1, y1);
        if (len < 2) return;

        stroke(c);
        strokeWeight(3);
        line(x0, y0, x1, y1);

        float angle = atan2(dy, dx);
        float arrowSize = min(12, len * 0.3f);

        pushMatrix();
        translate(x1, y1);
        rotate(angle);
        fill(c);
        noStroke();
        triangle(0, 0, -arrowSize, -arrowSize * 0.4f, -arrowSize, arrowSize * 0.4f);

        rotate(-angle);
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

        alogo = new Alogo(finalX, finalY, finalW, finalH);

        noCursor();
    }

    public static void main(String[] args) {
        PApplet.main("Classes.Dyn4");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}