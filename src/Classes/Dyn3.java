package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class Dyn3 extends PApplet {

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
        // GEOMETRÍA Y PARÁMETROS FÍSICOS (Idénticos a Dyn2 para Raccord Exacto)
        // ==========================================
        float theta = PI / 6f; // 30 grados
        float S = 70; // Tamaño del bloque

        float groundY = height * 0.82f;
        float startX = width * 0.08f;
        float vScale = 2.0f;

        float g_orig = 280.0f;
        float accelRampMag = -g_orig * sin(theta);
        float rampLength = 450.0f;
        float v0_ramp = (rampLength - 2.0f * accelRampMag) / 2.0f;

        float rampStartX = startX + v0_ramp;
        float rampEndX = rampStartX + rampLength * cos(theta);
        float rampEndY = groundY - (rampLength * sin(theta)) * vScale;

        float v_top = v0_ramp + accelRampMag * 2.0f;
        float v0x_air = v_top * cos(theta);
        float v0y_air_orig = -v_top * sin(theta);
        float v0y_air = v0y_air_orig * vScale;

        // Contenedor de agua (alineado píxel a píxel con Dyn2)
        float tankRight = width * 0.84f;
        float tankLeft = rampEndX + 60;
        float tankTop = rampEndY - 50;
        float waterH = 180 * vScale;
        float waterSurfaceY = groundY - waterH;

        float tankLeftX = tankLeft - 100;
        float tankRightX = tankRight - 100;

        // ==========================================
        // ESTADO FINAL EXACTO DE DYN2 (b = 4.0 de Dyn2)
        // ==========================================
        float dt_end_dyn2 = 1.0f;
        float px_init = rampEndX + v0x_air * dt_end_dyn2;
        float py_init = rampEndY + (v0y_air_orig * dt_end_dyn2 + 0.5f * g_orig * dt_end_dyn2 * dt_end_dyn2) * vScale;
        float vy_init = v0y_air + (g_orig * vScale) * dt_end_dyn2;

        // ==========================================
        // FÍSICA DE DESACELERACIÓN EXTREMA (Fricción masiva de fluidos)
        // ==========================================
        float py_target = waterSurfaceY + 60;
        float px = px_init + v0x_air * b * 0.2f;

        float py, vy;
        float b_stop = 1.2f; // El bloque frena de golpe casi al instante de entrar al agua

        if (b <= b_stop) {
            float p = b / b_stop;
            // Frenado brusco y pesado por fricción extrema (ease-out cúbico agresivo)
            float ease = 1.0f - (float)Math.pow(1.0f - p, 3.0f);
            float maxPenetration = py_init + vy_init * 0.35f;
            py = lerp(py_init, maxPenetration, ease);
            vy = vy_init * (1.0f - p * p); // La velocidad cae verticalmente a cero de forma drástica
        } else {
            // Ascenso extremadamente lento y viscoso debido a la alta fricción remanente
            float p = (b - b_stop) / (4.0f - b_stop);
            float ease = p * p;
            float maxPenetration = py_init + vy_init * 0.35f;
            py = lerp(maxPenetration, py_target, ease);
            vy = lerp(0.0f, -25.0f, p); // Subida muy pesada
        }
        float v_mag = abs(vy);

        // ==========================================
        // DIBUJO DEL ENTORNO
        // ==========================================
        stroke(255);
        strokeWeight(5);
        line(0, groundY, width, groundY);

        stroke(70);
        strokeWeight(2);
        for (float x = 0; x < width; x += 35) {
            line(x, groundY, x + 180, groundY);
        }

        // Cuerpo de Agua Translúcido
        noStroke();
        fill(0, 140, 230, 130);
        rectMode(CORNER);
        rect(tankLeftX + 3, waterSurfaceY, (tankRightX - tankLeftX) - 6, waterH);

        // Línea de Superficie del Agua
        stroke(0, 200, 255, 200);
        strokeWeight(2);
        line(tankLeftX + 3, waterSurfaceY, tankRightX - 3, waterSurfaceY);

        // Paredes del Contenedor
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
        // DIBUJO DEL BLOQUE Y VECTORES (FLECHITAS)
        // ==========================================
        rectMode(CENTER);
        pushMatrix();
        translate(px, py);

        // Cubo Transparente (Sin Relleno)
        strokeWeight(3);
        stroke(255);
        noFill();
        rect(0, 0, S, S);


        // 2. Vector Flotación (Fb) - Azul, siempre hacia arriba
        drawVector(0, 0, 0, -65, color(100, 150, 255), "Fb");

        // 3. Vector Fricción de Fluido (Fr) - Fricción extrema y masiva
        if (v_mag > 0.2f) {
            float frictionDir = (vy > 0) ? -1.0f : 1.0f; // Opuesto al movimiento
            float frictionLen = min(180, v_mag * 1.2f); // Fricción gigante para reflejar la desaceleración drástica
            drawVector(0, 0, 0, frictionDir * frictionLen*1.5f, color(255, 200, 50), "Fr");
        }

        popMatrix();

        popStyle();
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
        PApplet.main("Classes.Dyn3");
        PApplet.main("Classes.Dyn2");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}