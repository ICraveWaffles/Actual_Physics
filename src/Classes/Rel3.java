package Classes;

import processing.core.PApplet;
import java.util.ArrayList;

public class Rel3 extends PApplet {

    // --- Configuración de Tiempos y Beats ---
    private final float BEAT_DURATION = 0.6f; // 100 BPM
    private final float BEATS_PER_STAGE = 4f; // 4 beats por ciclo
    private final float TOTAL_STAGES = 1f;
    private final float TOTAL_BEATS = BEATS_PER_STAGE * TOTAL_STAGES;

    private float globalTime = 0;
    private float startTimeSec = -1;

    // --- Sistemas de Partículas y Eventos ---
    private ArrayList<Particle> particlesRel;  // Estela relativista
    private ArrayList<Particle> particlesClass; // Estela clásica
    private ArrayList<RandomEvent> randomEvents;

    // --- Logo y Transiciones ---
    public static Alogo alogo;
    float logoTransparency;
    float transY;

    @Override
    public void settings() {
        fullScreen();
        smooth(8);
    }

    @Override
    public void setup() {
        particlesRel = new ArrayList<>();
        particlesClass = new ArrayList<>();
        randomEvents = new ArrayList<>();
        noCursor();
        startTimeSec = millis() * 0.001f;

        float finalX = (width / 2f) + 865.3f;
        float finalY = (height / 2f) - 458.1f;
        float finalW = (height / 2f) - 381.75f;
        float finalH = (height / 2f) - 381.75f;

        alogo = new Alogo(finalX, finalY, finalW, finalH);
    }

    @Override
    public void draw() {
        background(0); // Fondo negro monocromático

        if (startTimeSec < 0) {
            startTimeSec = millis() * 0.001f;
        }

        float timeSec = (millis() * 0.001f) - startTimeSec;
        globalTime = timeSec % (BEAT_DURATION * TOTAL_BEATS);
        float currentBeat = globalTime / BEAT_DURATION;

        float b = timeSec * (100f / 60f);
        transY = b % 1f;
        logoTransparency = (float) (255 * (-Math.pow(transY, 2) + 2 * (transY)));

        float centerX = width * 0.5f;
        float centerY = height * 0.5f;

        float localProgress = (currentBeat % BEATS_PER_STAGE) / BEATS_PER_STAGE;

        // Dibujar escenario con la cámara centrada en X sobre la partícula relativista
        drawAccelerationStage(centerX, centerY, localProgress, timeSec);

        // Actualizar y dibujar logo Alogo
        if (alogo != null) {
            alogo.display(this, b, logoTransparency);
        }

        updateAndDisplayRandomEvents();
    }

    private void drawAccelerationStage(float cx, float cy, float progress, float timeSec) {
        pushStyle();

        float marginTop = height * 0.15f;
        float marginBottom = height * 0.15f;
        float usableH = height - marginTop - marginBottom;

        // --- ACELERACIÓN EXTREMA ---
        // Curva de aceleración masiva no lineal (T crece exponencialmente)
        float T = pow(progress * 3.2f, 2.6f);

        // Cálculos de Relatividad Especial
        float gamma = sqrt(1.0f + T * T);
        float v_rel = T / gamma; // Tiende asintóticamente a 1.0c
        float K_rel = gamma - 1.0f; // Energía cinética masiva

        // Posiciones absolutas en coordenadas del mundo
        float worldStartX = cx - width * 0.5f;
        float scaleFactor = width * 1.8f;

        // Posición mundo de la partícula relativista
        float x_rel_world = worldStartX + (sqrt(1.0f + T * T) - 1.0f) * (scaleFactor * 0.25f);

        // Posición mundo de la partícula clásica (desborda sin límite de C)
        float x_clas_world = worldStartX + (0.5f * T * T) * (scaleFactor * 0.12f);

        float y_rel = marginTop + usableH * 0.28f;
        float y_clas = marginTop + usableH * 0.72f;

        // --- CÁMARA DINÁMICA CENTRADA EN X ---
        // La cámara sigue a la partícula relativista manteniéndola exactamente en el centro (50% de la pantalla)
        float camX = x_rel_world - width * 0.5f;

        // --- DIBUJO DEL MUNDO DE SIMULACIÓN (CON CÁMARA) ---
        pushMatrix();
        translate(-camX, 0); // Transformación de la cámara

        // 1. Pistas de aceleración extendidas
        stroke(255, 30);
        strokeWeight(1f);
        drawDashedLine(worldStartX - 3000f, y_rel, x_rel_world + 4000f, y_rel, 6f);
        drawDashedLine(worldStartX - 3000f, y_clas, x_clas_world + 4000f, y_clas, 6f);

        // Grid o marcas de distancia en el espacio-tiempo
        for (float gx = worldStartX - 2000f; gx < x_clas_world + 4000f; gx += 200f) {
            stroke(255, 15);
            line(gx, y_rel - 15, gx, y_clas + 15);
        }

        // Línea simbólica del límite C (se desplaza respecto a la cámara)
        float cLimitWorldX = worldStartX + scaleFactor * 0.75f;
        float cGlow = 180 + sin(frameCount * 0.12f) * 40;
        stroke(255, cGlow);
        strokeWeight(2f);
        drawDashedLine(cLimitWorldX, y_rel - 60f, cLimitWorldX, y_clas + 60f, 4f);

        fill(255, 200);
        textSize(12);
        textAlign(CENTER, TOP);
        text("LÍMITE C", cLimitWorldX, y_clas + 70f);

        // 2. Partícula Clásica (No Relativista)
        float m0 = 10f;
        float r_clas = m0 * 2.0f;

        if (frameCount % 3 == 0) {
            particlesClass.add(new Particle(x_clas_world, y_clas, random(-0.2f, 0.2f), random(-0.2f, 0.2f), m0 * 0.5f));
        }

        noStroke();
        fill(255, 40);
        ellipse(x_clas_world, y_clas, r_clas * 1.5f, r_clas * 1.5f);
        fill(255, 240);
        ellipse(x_clas_world, y_clas, r_clas, r_clas);

        // 3. Partícula Relativista (Crecimiento de masa y vibración extrema)
        // El tamaño de la partícula crece con el factor gamma (Crecimiento de masa)
        float r_rel = m0 * 2.0f * (1.0f + log(gamma + 1.0f) * 1.8f);

        // Temblor extremo generado por la energía cinética K_rel
        float A_tremble = min(35.0f, 1.5f * K_rel);
        float y_tremble = A_tremble * sin(frameCount * 0.6f * sqrt(gamma));
        float x_tremble = (A_tremble * 0.4f) * cos(frameCount * 0.8f * sqrt(gamma));

        float finalRelX = x_rel_world + x_tremble;
        float finalRelY = y_rel + y_tremble;

        if (frameCount % 2 == 0) {
            particlesRel.add(new Particle(finalRelX, finalRelY, random(-0.8f, 0.8f) * K_rel, random(-0.8f, 0.8f) * K_rel, m0 * 0.4f * log(gamma + 1f)));
        }

        // Núcleo y halo relativista masivo
        noStroke();
        fill(255, max(10, 80 - gamma));
        ellipse(finalRelX, finalRelY, r_rel * 2.5f, r_rel * 2.5f);
        fill(255, 60);
        ellipse(finalRelX, finalRelY, r_rel * 1.4f, r_rel * 1.4f);
        fill(255, 250);
        ellipse(finalRelX, finalRelY, r_rel, r_rel);

        // Actualizar e interactuar con partículas dentro del sistema de la cámara
        updateAndDisplayParticles(this);

        popMatrix(); // Fin de transformaciones de la cámara

        popStyle();
    }

    private void drawDashedLine(float x1, float y1, float x2, float y2, float dashLen) {
        float d = dist(x1, y1, x2, y2);
        for (float i = 0; i < d; i += dashLen * 2) {
            float start = i / d;
            float end = min((i + dashLen) / d, 1.0f);
            line(lerp(x1, x2, start), lerp(y1, y2, start), lerp(x1, x2, end), lerp(y1, y2, end));
        }
    }

    private void updateAndDisplayRandomEvents() {
        if (frameCount % 12 == 0) {
            randomEvents.add(new RandomEvent(random(width * 0.1f, width * 0.9f), random(height * 0.1f, height * 0.9f), false));
        }
        for (int i = randomEvents.size() - 1; i >= 0; i--) {
            RandomEvent re = randomEvents.get(i);
            re.update();
            re.display(this);
            if (re.isDead()) {
                randomEvents.remove(i);
            }
        }
    }

    private void updateAndDisplayParticles(PApplet p) {
        for (int i = particlesRel.size() - 1; i >= 0; i--) {
            Particle pt = particlesRel.get(i);
            pt.update();
            pt.display(p);
            if (pt.isDead()) particlesRel.remove(i);
        }
        for (int i = particlesClass.size() - 1; i >= 0; i--) {
            Particle pt = particlesClass.get(i);
            pt.update();
            pt.display(p);
            if (pt.isDead()) particlesClass.remove(i);
        }
    }

    // --- CLASES INTERNAS ---

    class RandomEvent {
        float x, y, life;
        boolean isOnClockLine;

        RandomEvent(float x, float y, boolean isOnClockLine) {
            this.x = x;
            this.y = y;
            this.isOnClockLine = isOnClockLine;
            this.life = 1.0f;
        }

        void update() {
            life -= 0.038f;
        }

        void display(PApplet p) {
            p.pushStyle();
            p.noFill();
            p.stroke(255, max(0, life * 255f));
            p.strokeWeight(1.5f);
            float r = (1.0f - life) * 32f;
            p.ellipse(x, y, r, r);
            p.fill(255, max(0, life * 255f));
            p.noStroke();
            p.ellipse(x, y, 5, 5);
            p.popStyle();
        }

        boolean isDead() {
            return life <= 0;
        }
    }

    class Particle {
        float x, y, vx, vy, alpha;
        float size;

        Particle(float x, float y, float vx, float vy, float size) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.alpha = 255;
            this.size = size;
        }

        void update() {
            x += vx;
            y += vy;
            alpha -= 8;
        }

        void display(PApplet p) {
            p.pushStyle();
            p.noStroke();
            p.fill(255, max(0, alpha));
            p.ellipse(x, y, size, size);
            p.popStyle();
        }

        boolean isDead() {
            return alpha <= 0;
        }
    }

    @Override
    public void mousePressed() {
        startTimeSec = millis() * 0.001f;
        particlesRel.clear();
        particlesClass.clear();
        randomEvents.clear();
    }

    public static void main(String[] args) {
        PApplet.main("Classes.Rel3");
    }
}