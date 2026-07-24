package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class Dyn2 extends PApplet {

    float t = 0;
    float w = 106 / 120f;
    PFont ntr;
    float logoTransparency;
    float transY;

    public static Alogo alogo;

    float maxBeats = 4f; // 4 beats en total

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

        // ==========================================
        // GEOMETRÍA Y PARÁMETROS FÍSICOS (Escala Vertical 100% -> 2.0x)
        // ==========================================
        float theta = PI / 6f; // 30 grados
        float S = 70; // Tamaño del bloque

        float groundY = height * 0.82f;
        float startX = width * 0.08f;
        float vScale = 2.0f; // Factor de expansión vertical al 100%

        // Parámetros de aceleración y rampa
        float g_orig = 280.0f;
        float g = g_orig * vScale;

        // Desaceleración escalar a lo largo de la rampa
        float accelRampMag = -g_orig * sin(theta);

        float rampLength = 450.0f;

        float v0_ramp = (rampLength - 2.0f * accelRampMag) / 2.0f;

        float rampStartX = startX + v0_ramp;
        float rampEndX = rampStartX + rampLength * cos(theta);
        float rampEndY = groundY - (rampLength * sin(theta)) * vScale;

        // Pared lejana y contenedor
        float tankRight = width * 0.84f;
        float wallX = tankRight - S / 2f;
        float tankLeft = rampEndX + 60;
        float tankTop = rampEndY - 50;

        // ==========================================
        // CÁLCULO DE FÍSICA Y TRAYECTORIA
        // ==========================================
        float px = 0, py = 0;
        float vx = 0, vy = 0;
        float v_mag = 0;
        float currentAngle = 0;

        boolean onRamp = false;
        boolean onFlat = false;
        boolean impacted = false;

        float v_top = v0_ramp + accelRampMag * 2.0f;

        float v0x_air = v_top * cos(theta);
        float v0y_air_orig = -v_top * sin(theta);
        float v0y_air = v0y_air_orig * vScale;

        float dt_hit = (wallX - rampEndX) / v0x_air;
        float b_impact = 3.0f + dt_hit;

        if (b < 1.0f) {
            // FASE 1: Plano horizontal
            onFlat = true;
            float p = b / 1.0f;
            px = lerp(startX, rampStartX, p);
            py = groundY;

            vx = v0_ramp;
            vy = 0;
            v_mag = v0_ramp;

            if (b > 0.8f) {
                float angleProgress = (b - 0.8f) / 0.2f;
                currentAngle = lerp(0, -atan2(sin(theta) * vScale, cos(theta)), angleProgress);
            } else {
                currentAngle = 0;
            }

        } else if (b <= 3.0f) {
            // FASE 2: Subida por la rampa con desaceleración consistente
            onRamp = true;
            float dt_ramp = b - 1.0f;

            float currentRampDist = v0_ramp * dt_ramp + 0.5f * accelRampMag * dt_ramp * dt_ramp;

            px = rampStartX + currentRampDist * cos(theta);
            py = groundY - (currentRampDist * sin(theta)) * vScale;

            v_mag = v0_ramp + accelRampMag * dt_ramp;
            vx = v_mag * cos(theta);
            vy = -v_mag * sin(theta) * vScale;
            currentAngle = -atan2(sin(theta) * vScale, cos(theta));

        } else if (b < b_impact) {
            // FASE 3: Vuelo parabólico
            float dt = b - 3.0f;

            px = rampEndX + v0x_air * dt;
            py = rampEndY + (v0y_air_orig * dt + 0.5f * g_orig * dt * dt) * vScale;

            vx = v0x_air;
            vy = v0y_air + g * dt;
            v_mag = dist(0, 0, vx, vy / vScale);

            currentAngle = atan2(vy, vx);

        } else {
            // FASE 4: Impacto
            impacted = true;
            float dt = dt_hit;

            px = wallX;
            py = rampEndY + (v0y_air_orig * dt + 0.5f * g_orig * dt * dt) * vScale;

            vx = 0;
            vy = 0;
            v_mag = 0;
            currentAngle = 0;
        }

        // ==========================================
        // SHAKE & SQUASH (Efectos de Choque)
        // ==========================================
        float shakeX = 0;
        float scaleW = 1.0f, scaleH = 1.0f;

        if (impacted) {
            float timeSinceHit = b - b_impact;
            if (timeSinceHit < 0.15f) {
                shakeX = random(-6, 6);
                scaleW = 0.75f;
                scaleH = 1.25f;
            }
        }

        pushMatrix();
        translate(shakeX, 0);

        // ==========================================
        // DIBUJO DEL ENTORNO
        // ==========================================
        stroke(255);
        strokeWeight(5);
        line(0, groundY, width, groundY);

        stroke(255);
        strokeWeight(5);
        line(rampStartX, groundY, rampEndX, rampEndY);

        // DIBUJO DE CONTENEDOR DE AGUA Y FLUIDO
        float waterH = 180 * vScale;
        float tankLeftX = tankLeft - 100;
        float tankRightX = tankRight - 100;

        noStroke();
        fill(0, 140, 230, 130);
        rectMode(CORNER);
        rect(tankLeftX + 3, groundY - waterH, (tankRightX - tankLeftX) - 6, waterH);

        stroke(0, 200, 255, 200);
        strokeWeight(2);
        line(tankLeftX + 3, groundY - waterH, tankRightX - 3, groundY - waterH);

        stroke(255);
        strokeWeight(6);
        noFill();
        beginShape();
        vertex(tankLeftX, tankTop + 100);
        vertex(tankLeftX, groundY);
        vertex(tankRightX, groundY);
        vertex(tankRightX, tankTop - (80 * vScale));
        endShape();

        // ==========================================
        // DIBUJO DEL BLOQUE Y VECTORES
        // ==========================================
        rectMode(CENTER);

        pushMatrix();
        translate(px, py);
        rotate(currentAngle);
        scale(scaleW, scaleH);

        // Cubo Transparente (Sin Relleno)
        strokeWeight(3);
        stroke(255);
        noFill();
        rect(0, -S / 2f, S, S);

        if (onRamp) {
            float massVis = 0.6f;
            float w_vec = g_orig * massVis * 0.5f;
            float wy_vec = w_vec * cos(theta);

            // 1. Vector Peso (P)
            pushMatrix();
            translate(0, -S / 2f);
            rotate(QUARTER_PI);
            drawVector(0, 0, 0, w_vec, 255, "");
            popMatrix();

            translate(0, -S / 2f);

            // 2. Vector Normal (N)
            drawVector(0, 0, 0, -wy_vec, 255, "");

            // 3. Vector Velocidad
            if (v_mag > 10) {
                drawVector(0, 0, v_mag * 0.3f, 0, 255, "");
            }
        }
        popMatrix();

        popMatrix();

        // ==========================================
        // BARRITAS DE ENERGÍA (UI - EC y EP)
        // ==========================================
        float currentKE = 0.5f * (v_mag * v_mag);
        float h = max(0, (groundY - py) / vScale);
        float currentPE = g_orig * h;

        if (impacted) {
            currentKE = 0;
        }

        float maxEnergy = 75000;
        float barMaxHeight = 350;
        float scaleE = barMaxHeight / maxEnergy;

        float uiX = 70;
        float uiY = 550;
        float barWidth = 70;
        float spacing = 110;

        drawEnergyBar(uiX, uiY, barWidth, currentKE * scaleE, barMaxHeight, 255, "");
        drawEnergyBar(uiX + spacing, uiY, barWidth, currentPE * scaleE, barMaxHeight, 255, "");

        popStyle();
    }

    void drawEnergyBar(float x, float y, float w, float val, float maxH, int c, String label) {
        stroke(200);
        strokeWeight(2);
        noFill();
        rectMode(CORNER);
        rect(x, y - maxH, w, maxH);

        noStroke();
        fill(c);
        rect(x, y - val, w, val);

        fill(255);
    }

    void drawVector(float x0, float y0, float dx, float dy, int c, String label) {
        float x1 = x0 + dx;
        float y1 = y0 + dy;
        float len = dist(x0, y0, x1, y1);
        if (len < 2) return;

        stroke(255);
        strokeWeight(3);
        line(x0, y0, x1, y1);

        float angle = atan2(dy, dx);
        float arrowSize = min(12, len * 0.3f);

        pushMatrix();
        translate(x1, y1);
        rotate(angle);
        fill(255);
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
        PApplet.main("Classes.Dyn2");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}