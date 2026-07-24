package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class Kin4 extends PApplet {

    float t = 0;
    float w = 106 / 120f;
    PFont ntr;
    float logoTransparency;
    float transY;

    public static Alogo alogo;

    float beatDuration = 2.26415f / 2f;
    float halfT = beatDuration * 4f;
    float maxT = halfT * 2f;

    public void draw() {

        pushStyle();

        frameRate = 30;
        t = frameCount / (float) frameRate;
        if (t > maxT) {
            frameCount = 0;
            t = 0;
        }

        background(0);

        float b = t * w;
        textFont(ntr);

        transY = b % 1f;
        logoTransparency = (float) (255 * (-Math.pow(transY, 2) + 2 * (transY)));

        alogo.display(this, b, logoTransparency);

        float animT = min(t, halfT);

        float groundY = height * 0.75f;
        float blockSize = 180;
        float rightWallX = width * 0.85f;
        float startX = width / 2f - blockSize / 2f;

        float hitTime = beatDuration * 2f;
        float frictionStartTime = beatDuration * 3f;
        float frictionStartX = width * 0.45f;

        float shakeDuration = 0.20f;
        float shakeOffset = 0;

        if (animT >= hitTime && animT < hitTime + shakeDuration) {
            float progress = (animT - hitTime) / shakeDuration;
            shakeOffset = sin(progress * TWO_PI * 4) * 12 * (1 - progress);
        }

        pushMatrix();
        translate(shakeOffset, 0);

        drawGroundWithVariableFriction(groundY, frictionStartX);

        stroke(120);
        strokeWeight(2);
        line(rightWallX, groundY - 250, rightWallX, groundY);

        float blockX = calculateBlockX(animT, startX, rightWallX - blockSize, hitTime, frictionStartTime, beatDuration, frictionStartX);
        float blockY = groundY - blockSize;

        fill(255);
        stroke(255);
        strokeWeight(2);
        rect(blockX, blockY, blockSize, blockSize);

        // Beat 2: FUERZA impulsora aplicada hacia la derecha
        if (animT >= beatDuration && animT < hitTime) {
            float forceProgress = constrain((animT - beatDuration) / (beatDuration * 0.3f), 0, 1);
            float arrowLen = 220 * forceProgress;
            drawVector(blockX + blockSize / 2f, blockY + blockSize / 2f, blockX + blockSize / 2f + arrowLen, blockY + blockSize / 2f, 255);
        }

        // Beat 3: Movimiento por inercia -> SIN FLECHA

        // Beat 4: FUERZA DE FRICCIÓN (Flecha sustancialmente MÁS LARGA)
        if (animT >= frictionStartTime) {
            float fProgress = 1.0f - constrain((animT - frictionStartTime) / beatDuration, 0, 1);
            if (fProgress > 0.05f) {
                float frictionArrowLen = 320 * fProgress; // Incrementada a 320 px
                drawVector(blockX + blockSize / 2f, blockY + blockSize / 2f, blockX + blockSize / 2f - frictionArrowLen, blockY + blockSize / 2f, 255);
            }
        }

        popMatrix();

        popStyle();
    }

    float calculateBlockX(float time, float x0, float xImpact, float tImpact, float tFriction, float bLen, float fStartX) {
        if (time < bLen) {
            return x0;
        }
        else if (time >= bLen && time < tImpact) {
            float normT = (time - bLen) / (tImpact - bLen);
            float eased = normT * normT;
            return lerp(x0, xImpact, eased);
        }
        else if (time >= tImpact && time < tFriction) {
            float bounceTimeElapsed = time - tImpact;
            float speed = (xImpact - fStartX) / (tFriction - tImpact);
            return xImpact - (speed * bounceTimeElapsed);
        }
        else {
            float fTimeElapsed = constrain((time - tFriction) / bLen, 0, 1);
            float speed = (xImpact - fStartX) / (tFriction - tImpact);

            float stopDist = (speed * bLen) * 0.5f;
            float easedFriction = 1.0f - (1.0f - fTimeElapsed) * (1.0f - fTimeElapsed);

            return fStartX - (stopDist * easedFriction);
        }
    }

    void drawGroundWithVariableFriction(float gY, float frictionBorderX) {
        stroke(255);
        strokeWeight(2);
        line(0, gY, width, gY);

        stroke(80);
        strokeWeight(1);

        float hatchLen = 15;

        // Zona con FRICCIÓN (Suelo rugoso)
        float denseSpacing = 8;
        for (float x = -20; x < frictionBorderX; x += denseSpacing) {
            line(x, gY, x - hatchLen, gY + hatchLen);
        }

        // Zona SIN FRICCIÓN
        float normalSpacing = 28;
        for (float x = frictionBorderX; x < width + 20; x += normalSpacing) {
            line(x, gY, x - hatchLen, gY + hatchLen);
        }
    }

    void drawVector(float x0, float y0, float x1, float y1, float alpha) {
        float angle = atan2(y1 - y0, x1 - x0);
        float len = dist(x0, y0, x1, y1);

        if (len < 1f) return;

        stroke(255, alpha);
        strokeWeight(6);
        line(x0, y0, x1, y1);

        float arrowSize = min(26, len * 0.4f);

        pushMatrix();
        translate(x1, y1);
        rotate(angle);
        fill(255, alpha);
        noStroke();
        triangle(0, 0, -arrowSize, -arrowSize / 2.2f, -arrowSize, arrowSize / 2.2f);
        popMatrix();
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

        alogo = new Alogo(finalX, finalY, finalW, finalH);

        noCursor();
    }

    public static void main(String[] args) {
        PApplet.main("Classes.Kin4");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}