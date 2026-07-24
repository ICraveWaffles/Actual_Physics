package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class Dyn1 extends PApplet {

    float t = 0;
    float w = 106 / 120f;
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

        float S = 120; // Tamaño base de los bloques
        float wallL = width * 0.12f;
        float wallR = width * 0.88f;
        float cx = width / 2f;

        float xLmin = wallL + S / 2f;
        float xRmax = wallR - S / 2f;
        float distViaje = (cx - S / 2f) - xLmin;

        float topY = height * 0.35f;
        float botY = height * 0.70f;

        // ==========================================
        // CÁLCULO DE SHAKE (Temblor por colisión)
        // ==========================================
        float shakeX = 0;
        float window = 0.06f;

        float bTop = b % 2f;
        boolean topHit = (abs(bTop - 0.0f) < window || abs(bTop - 2.0f) < window ||
                abs(bTop - 0.5f) < window || abs(bTop - 1.0f) < window || abs(bTop - 1.5f) < window);

        float bBot = (b * 2.0f) % maxBeats;
        boolean botHit = (abs(bBot - 1.0f) < window || abs(bBot - 3.0f) < window || abs(bBot - 7.0f) < window);

        if (topHit || botHit) {
            shakeX = random(-6, 6);
        }

        pushMatrix();
        translate(shakeX, 0);

        // Dibujo de muros y línea central
        stroke(150);
        strokeWeight(4);
        line(wallL, topY - S - 30, wallL, botY + S + 30);
        line(wallR, topY - S - 30, wallR, botY + S + 30);

        stroke(100);
        strokeWeight(2);
        for(float y = topY - S; y < botY + S; y += 25) {
            line(cx, y, cx, y + 10);
        }

        rectMode(CENTER);

        // ==========================================
        // SISTEMA 1: ELÁSTICO (Corcheas)
        // ==========================================
        float x1, x2;

        if (bTop < 0.5f) {
            x1 = lerp(xLmin, cx - S/2f, bTop / 0.5f);
            x2 = cx + S/2f;
        } else if (bTop < 1.0f) {
            x1 = cx - S/2f;
            x2 = lerp(cx + S/2f, xRmax, (bTop - 0.5f) / 0.5f);
        } else if (bTop < 1.5f) {
            x1 = cx - S/2f;
            x2 = lerp(xRmax, cx + S/2f, (bTop - 1.0f) / 0.5f);
        } else {
            x1 = lerp(cx - S/2f, xLmin, (bTop - 1.5f) / 0.5f);
            x2 = cx + S/2f;
        }

        // Destellos y deformación (Stretch & Squash)
        float flash1 = 0, scaleW1 = 1f, scaleH1 = 1f;
        float flash2 = 0, scaleW2 = 1f, scaleH2 = 1f;

        if (abs(bTop - 0.0f) < window || abs(bTop - 2.0f) < window) {
            float p = abs(bTop - 0.0f);
            flash1 = map(p, 0, window, 255, 0);
            scaleW1 = 0.8f; scaleH1 = 1.2f; // Squash horizontal
        }
        if (abs(bTop - 0.5f) < window) {
            float p = abs(bTop - 0.5f);
            float fVal = map(p, 0, window, 255, 0);
            flash1 = max(flash1, fVal); scaleW1 = 1.2f; scaleH1 = 0.8f;
            flash2 = max(flash2, fVal); scaleW2 = 1.2f; scaleH2 = 0.8f;
        }
        if (abs(bTop - 1.0f) < window) {
            float p = abs(bTop - 1.0f);
            flash2 = map(p, 0, window, 255, 0);
            scaleW2 = 0.8f; scaleH2 = 1.2f;
        }
        if (abs(bTop - 1.5f) < window) {
            float p = abs(bTop - 1.5f);
            float fVal = map(p, 0, window, 255, 0);
            flash1 = max(flash1, fVal); scaleW1 = 1.2f; scaleH1 = 0.8f;
            flash2 = max(flash2, fVal); scaleW2 = 1.2f; scaleH2 = 0.8f;
        }

        // Dibujar Bloque 1 Elástico con transformaciones
        pushMatrix();
        translate(x1, topY);
        scale(scaleW1, scaleH1);
        stroke(255);
        strokeWeight(3);
        fill(flash1);
        rect(0, 0, S, S);
        popMatrix();

        // Dibujar Bloque 2 Elástico con transformaciones
        pushMatrix();
        translate(x2, topY);
        scale(scaleW2, scaleH2);
        stroke(255);
        strokeWeight(3);
        fill(flash2);
        rect(0, 0, S, S);
        popMatrix();


        // ==========================================
        // SISTEMA 2: INELÁSTICO (Doble de rápido)
        // ==========================================
        float x3, x4;

        if (bBot < 1.0f) {
            x3 = lerp(xLmin, cx - S/2f, bBot / 1.0f);
            x4 = cx + S/2f;
        } else if (bBot < 3.0f) {
            float p = (bBot - 1.0f) / 2.0f;
            x3 = lerp(cx - S/2f, xRmax - S, p);
            x4 = lerp(cx + S/2f, xRmax, p);
        } else if (bBot < 7.0f) {
            float p = (bBot - 3.0f) / 4.0f;
            x3 = lerp(xRmax - S, xLmin, p);
            x4 = lerp(xRmax, xLmin + S, p);
        } else {
            float p = (bBot - 7.0f) / 1.0f;
            x3 = lerp(xLmin, xLmin + distViaje / 2f, p);
            x4 = lerp(xLmin + S, xLmin + S + distViaje / 2f, p);
        }

        float flashBot = 0;
        float scaleWBot = 1f, scaleHBot = 1f;
        float db1 = abs(bBot - 1.0f);
        float db2 = abs(bBot - 3.0f);
        float db3 = abs(bBot - 7.0f);

        if (db1 < window || db2 < window || db3 < window) {
            float distToHit = min(min(db1, db2), db3);
            flashBot = map(distToHit, 0, window, 255, 0);
            scaleWBot = 1.15f; scaleHBot = 0.85f; // Deformación por impacto inelástico
        }

        stroke(255);
        strokeWeight(3);
        float cFill = lerp(80, 255, flashBot / 255f);

        pushMatrix();
        translate(x3, botY);
        scale(scaleWBot, scaleHBot);
        fill(cFill);
        rect(0, 0, S, S);
        popMatrix();

        pushMatrix();
        translate(x4, botY);
        scale(scaleWBot, scaleHBot);
        fill(cFill);
        rect(0, 0, S, S);
        popMatrix();

        if (bBot >= 1.0f) {
            stroke(40);
            strokeWeight(4);
            line(x3 + S/2f, botY - S/2f + 2, x3 + S/2f, botY + S/2f - 2);
        }

        popMatrix();

        fill(255);
        textSize(28);
        textAlign(CENTER, BOTTOM);
        popStyle();
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
        PApplet.main("Classes.Dyn1");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}