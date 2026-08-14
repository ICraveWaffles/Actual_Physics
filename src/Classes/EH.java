package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class EH extends PApplet {

    float t = 0;
    float w = 100f / 120f;
    float logoTransparency;
    float transY;

    public static Elogo elogo;
    PFont tnrFont;

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
        logoTransparency = 255;

        float cx = width * 0.50f;
        float cy = height * 0.50f;

        pushMatrix();
        if (b < 2.0f) {
            drawQuantumTunneling(b, cx, cy);
        } else {
            drawHadrons(cx, cy);
        }
        popMatrix();

        if (elogo != null) {
            elogo.display(this, b, 255);
        }

        popStyle();
    }

    private void drawQuantumTunneling(float localB, float cx, float cy) {
        float normB = localB / 2.0f;

        float baselineY = cy;
        stroke(255);
        strokeWeight(1.2f);
        line(cx - 500f, baselineY, cx + 500f, baselineY);

        float barrierW = 80f;
        float barrierH = height * 0.50f;
        float barrierX = cx - barrierW / 2f;
        float barrierY = cy - barrierH / 2f;

        noFill();
        stroke(255);
        strokeWeight(2.5f);
        rect(barrierX, barrierY, barrierW, barrierH);

        strokeWeight(1f);
        for (float y = barrierY + 15f; y < barrierY + barrierH; y += 20f) {
            line(barrierX, y, barrierX + barrierW, y);
        }

        float packetX = lerp(cx - 420f, cx + 380f, normB);
        float sigma = 60f;
        float k = 0.12f;

        strokeWeight(2.5f);
        noFill();
        beginShape();
        stroke(255);
        for (float x = cx - 480f; x < barrierX; x += 3f) {
            float envInc = exp(-sq((x - packetX) / sigma));
            float waveInc = envInc * sin(k * (x - packetX) - normB * TWO_PI * 4f);

            float envRef = (packetX > barrierX) ? exp(-sq((x - (2 * barrierX - packetX)) / sigma)) * 0.6f : 0f;
            float waveRef = envRef * sin(-k * (x - barrierX) - normB * TWO_PI * 4f);

            float y = baselineY - (waveInc + waveRef) * 110f;
            vertex(x, y);
        }
        endShape();

        beginShape();
        stroke(255);
        strokeWeight(1.5f);
        for (float x = barrierX; x <= barrierX + barrierW; x += 2f) {
            float distIn = x - barrierX;
            float penetration = exp(-distIn / 20f);
            float envInc = exp(-sq((barrierX - packetX) / sigma));
            float waveIn = envInc * penetration * sin(-normB * TWO_PI * 4f);

            float y = baselineY - waveIn * 110f;
            vertex(x, y);
        }
        endShape();

        beginShape();
        stroke(255);
        strokeWeight(2.0f);
        for (float x = barrierX + barrierW; x < cx + 480f; x += 3f) {
            float transFront = packetX - barrierW;
            float envTrans = (transFront > barrierX + barrierW) ? exp(-sq((x - transFront) / sigma)) * 0.4f : 0f;
            float waveTrans = envTrans * sin(k * (x - transFront) - normB * TWO_PI * 4f);

            float y = baselineY - waveTrans * 110f;
            vertex(x, y);
        }
        endShape();

        fill(255);
        noStroke();
        for (int i = 0; i < 14; i++) {
            float pX = packetX + (getHash(i, 1) - 0.5f) * 70f;
            float pY = baselineY - exp(-sq((pX - packetX) / sigma)) * 90f * getHash(i, 2);
            circle(pX, pY, 5f);
        }

        if (tnrFont != null) textFont(tnrFont);
        textSize(13f);

        drawCallout(barrierX + barrierW / 2f, barrierY + 20f, barrierX + barrierW / 2f + 140f, barrierY + 40f, "BARRERA DE POTENCIAL V(x)", false);
        drawCallout(cx - 280f, baselineY - 120f, cx - 280f, baselineY - 170f, "ONDA INCIDENTE / REFLEJADA", true);
        drawCallout(cx + 260f, baselineY - 60f, cx + 260f, baselineY - 120f, "ONDA TRANSMITIDA (EFECTO TÚNEL)", false);
    }

    private void drawHadrons(float cx, float cy) {
        float colW = width * 0.17f;
        float rowH = height * 0.32f;

        float y1 = cy - rowH * 0.5f;
        float y2 = cy + rowH * 0.5f;

        float x1 = cx - colW * 1.5f;
        float x2 = cx - colW * 0.5f;
        float x3 = cx + colW * 0.5f;
        float x4 = cx + colW * 1.5f;

        pushMatrix();

        drawHadron(x1, y1, "Protón", new String[]{"u", "u", "d"});
        drawHadron(x2, y1, "Neutrón", new String[]{"u", "d", "d"});
        drawHadron(x3, y1, "Pión (π⁺)", new String[]{"u", "d_bar"});
        drawHadron(x4, y1, "Pión (π⁰)", new String[]{"u", "u_bar"});

        drawHadron(x1, y2, "Antiprotón", new String[]{"u_bar", "u_bar", "d_bar"});
        drawHadron(x2, y2, "Lambda (Λ⁰)", new String[]{"u", "d", "s"});
        drawHadron(x3, y2, "Kaón (K⁰)", new String[]{"d", "s_bar"});
        drawHadron(x4, y2, "Mesón J/Ψ", new String[]{"c", "c_bar"});

        popMatrix();
    }

    private void drawHadron(float x, float y, String name, String[] quarks) {
        // Movimiento de temblor reducido en frecuencia (de 80-120 a 8-12) y amplitud (de 3.5 a 1.2)
        float jitterX = sin(t * 8f) * 1.2f + cos(t * 12f) * 0.8f;
        float jitterY = cos(t * 9f) * 1.2f + sin(t * 11f) * 0.8f;
        float sizeFluct = sin(t * 10f) * 2.5f;

        pushMatrix();
        translate(x + jitterX, y + jitterY);

        float hadronDiameter = 200f + sizeFluct;

        fill(0);
        stroke(255);
        strokeWeight(2.5f);
        circle(0, 0, hadronDiameter);

        noFill();
        strokeWeight(1.0f);
        circle(0, 0, hadronDiameter + 10f);

        int numQuarks = quarks.length;
        float rPos = (numQuarks == 3) ? 38f : 42f;

        for (int i = 0; i < numQuarks; i++) {
            float angle = i * TWO_PI / numQuarks - HALF_PI;

            // Movimiento interno de los quarks atenuado y más pausado
            float moveSpeed = t * 6f;
            float wobbleX = cos(moveSpeed + i * 2.3f) * 8f + sin(moveSpeed * 0.8f + i) * 3f;
            float wobbleY = sin(moveSpeed * 1.2f + i * 1.7f) * 8f + cos(moveSpeed * 0.9f) * 3f;

            float qx = cos(angle) * rPos + wobbleX;
            float qy = sin(angle) * rPos + wobbleY;

            drawQuark(qx, qy, quarks[i]);
        }

        if (tnrFont != null) textFont(tnrFont);
        textSize(15f);
        textAlign(CENTER, TOP);
        fill(255);
        text(name, 0, hadronDiameter / 2f + 12f);

        popMatrix();
    }

    private void drawQuark(float x, float y, String type) {
        pushMatrix();
        translate(x, y);

        float qRadius = 65f;

        fill(0);
        stroke(255);
        strokeWeight(2.2f);
        circle(0, 0, qRadius);

        stroke(255);
        strokeWeight(2.5f);
        noFill();

        boolean isAnti = type.endsWith("_bar");

        if (type.startsWith("u")) {
            drawArrowSymbol(0, 0, true, isAnti);
        } else if (type.startsWith("d")) {
            drawArrowSymbol(0, 0, false, isAnti);
        } else if (type.startsWith("s")) {
            drawSideSymbol(0, 0, isAnti);
        } else if (type.startsWith("c")) {
            drawStarSymbol(0, 0, isAnti);
        }

        popMatrix();
    }

    private void drawArrowSymbol(float x, float y, boolean pointUp, boolean hasBar) {
        float dir = pointUp ? -1f : 1f;
        line(x, y + dir * 10f, x, y - dir * 10f);
        line(x - 6f, y - dir * 3f, x, y - dir * 10f);
        line(x + 6f, y - dir * 3f, x, y - dir * 10f);

        if (hasBar) {
            strokeWeight(2.8f);
            line(x - 10f, y - 14f, x + 10f, y - 14f);
        }
    }

    private void drawSideSymbol(float x, float y, boolean hasBar) {
        line(x - 10f, y, x + 10f, y);
        line(x + 3f, y - 6f, x + 10f, y);
        line(x + 3f, y + 6f, x + 10f, y);

        if (hasBar) {
            strokeWeight(2.8f);
            line(x - 10f, y - 13f, x + 10f, y - 13f);
        }
    }

    private void drawStarSymbol(float x, float y, boolean hasBar) {
        float s = 9f;
        line(x - s, y, x + s, y);
        line(x, y - s, x, y + s);
        line(x - s * 0.6f, y - s * 0.6f, x + s * 0.6f, y + s * 0.6f);
        line(x - s * 0.6f, y + s * 0.6f, x + s * 0.6f, y - s * 0.6f);

        if (hasBar) {
            strokeWeight(2.8f);
            line(x - 10f, y - 14f, x + 10f, y - 14f);
        }
    }

    private void drawCallout(float targetX, float targetY, float textX, float textY, String label, boolean alignRight) {
        fill(255);
        noStroke();
        circle(targetX, targetY, 5f);

        stroke(255);
        strokeWeight(1.2f);
        line(targetX, targetY, textX, textY);

        float lineEnd = alignRight ? textX - 20f : textX + 20f;
        line(textX, textY, lineEnd, textY);

        fill(255);
        if (alignRight) {
            textAlign(RIGHT, CENTER);
            text(label, textX - 25f, textY - 2f);
        } else {
            textAlign(LEFT, CENTER);
            text(label, textX + 25f, textY - 2f);
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
        tnrFont = createFont("Times New Roman", 14);
        float finalX = (width / 2f) + 865.3f;
        float finalY = (height / 2f) - 458.1f;
        float finalW = (height / 2f) - 381.75f;
        float finalH = (height / 2f) - 381.75f;
        elogo = new Elogo(finalX, finalY, finalW, finalH);
        noCursor();
    }

    public static void main(String[] args) {
        PApplet.main("Classes.EH");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}