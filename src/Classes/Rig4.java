package Classes;

import processing.core.PApplet;
import processing.core.PFont;
import java.util.ArrayList;
import java.util.Collections;

public class Rig4 extends PApplet {

    float t = 0;
    float w = 106 / 120f;
    PFont ntr;
    float logoTransparency;
    float transY;

    public static Alogo alogo;

    float maxBeats = 4f;
    float theta = 0;
    float omega0 = (PI / 3f) * 2.0f;

    public void draw() {
        pushStyle();

        frameRate = 30;
        t = frameCount / (float) frameRate;
        float b = t * w;

        if (frameCount == 1 || b == 0) {
            theta = 0;
        }

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

        // --- FÍSICA Y CAMBIO DE DIMENSIONES (CONSERVACIÓN MOMENTO ANGULAR) ---
        float L, H, D;
        if (b < 2.0f) {
            float p = b / 2.0f;
            p = constrain(p, 0f, 1f);
            p = p * p * (3f - 2f * p);
            L = lerp(800, 180, p);
            H = lerp(60, 260, p);
            D = lerp(60, 260, p);
        } else {
            float p = (b - 2.0f) / 2.0f;
            p = constrain(p, 0f, 1f);
            p = p * p * (3f - 2f * p);
            L = lerp(180, 800, p);
            H = lerp(260, 60, p);
            D = lerp(260, 60, p);
        }

        float I_initial = (800 * 800) + (60 * 60);
        float I_current = (L * L) + (D * D);
        float currentOmega = omega0 * (I_initial / I_current);

        float db = (1f / 30f) * w;
        theta += currentOmega * db;

        float cx = width / 2f;
        float cy = height / 2f;

        // Eje central - Parte inferior
        drawPseudo3DCylinder(cx, cy, 12, 0, 200);

        // Barra rectangular tridimensional variable
        drawPseudo3DBox(cx, cy, theta, L, H, D);

        // Eje central - Parte superior
        drawPseudo3DCylinder(cx, cy, 12, -200, 0);

        popStyle();
    }

    // --- PROYECCIÓN Y RENDERIZADO 3D EN 2D ---
    static class P3 {
        float x, y, z;
        P3(float x, float y, float z) { this.x = x; this.y = y; this.z = z; }
    }

    P3 project(float x, float y, float z, float rotY, float cx, float cy) {
        float cosY = cos(rotY);
        float sinY = sin(rotY);
        float x1 = x * cosY + z * sinY;
        float y1 = y;
        float z1 = -x * sinY + z * cosY;

        float pitch = 0.38f;
        float cosX = cos(pitch);
        float sinX = sin(pitch);
        float x2 = x1;
        float y2 = y1 * cosX - z1 * sinX;
        float z2 = y1 * sinX + z1 * cosX;

        return new P3(cx + x2, cy + y2, z2);
    }

    static class Face implements Comparable<Face> {
        int[] vIdx;
        float avgZ;
        Face(int[] vIdx, float avgZ) {
            this.vIdx = vIdx;
            this.avgZ = avgZ;
        }
        public int compareTo(Face o) {
            return Float.compare(this.avgZ, o.avgZ);
        }
    }

    void drawPseudo3DBox(float cx, float cy, float rotY, float L, float H, float D) {
        P3[] rawVerts = new P3[]{
                new P3(-L/2, -H/2, -D/2), new P3( L/2, -H/2, -D/2),
                new P3( L/2,  H/2, -D/2), new P3(-L/2,  H/2, -D/2),
                new P3(-L/2, -H/2,  D/2), new P3( L/2, -H/2,  D/2),
                new P3( L/2,  H/2,  D/2), new P3(-L/2,  H/2,  D/2)
        };

        P3[] projVerts = new P3[8];
        for (int i = 0; i < 8; i++) {
            projVerts[i] = project(rawVerts[i].x, rawVerts[i].y, rawVerts[i].z, rotY, cx, cy);
        }

        int[][] faces = {
                {0, 1, 2, 3}, {4, 5, 6, 7}, {0, 3, 7, 4},
                {1, 2, 6, 5}, {0, 1, 5, 4}, {3, 2, 6, 7}
        };

        ArrayList<Face> faceList = new ArrayList<>();
        for (int[] f : faces) {
            float zSum = projVerts[f[0]].z + projVerts[f[1]].z + projVerts[f[2]].z + projVerts[f[3]].z;
            faceList.add(new Face(f, zSum / 4f));
        }

        Collections.sort(faceList);

        for (Face f : faceList) {
            fill(0);
            stroke(255);
            strokeWeight(3);
            beginShape();
            for (int idx : f.vIdx) {
                vertex(projVerts[idx].x, projVerts[idx].y);
            }
            endShape(CLOSE);
        }
    }

    void drawPseudo3DCylinder(float cx, float cy, float r, float hTop, float hBottom) {
        int sides = 16;
        float angleStep = TWO_PI / sides;
        P3[] pTop = new P3[sides];
        P3[] pBot = new P3[sides];

        for (int i = 0; i < sides; i++) {
            float a = i * angleStep;
            float rx = cos(a) * r;
            float rz = sin(a) * r;
            pTop[i] = project(rx, hTop, rz, 0, cx, cy);
            pBot[i] = project(rx, hBottom, rz, 0, cx, cy);
        }

        class QuadFace implements Comparable<QuadFace> {
            int i1, i2;
            float avgZ;
            QuadFace(int i1, int i2, float avgZ) { this.i1 = i1; this.i2 = i2; this.avgZ = avgZ; }
            public int compareTo(QuadFace o) { return Float.compare(this.avgZ, o.avgZ); }
        }

        ArrayList<QuadFace> quads = new ArrayList<>();
        for (int i = 0; i < sides; i++) {
            int next = (i + 1) % sides;
            float zAvg = (pTop[i].z + pTop[next].z + pBot[i].z + pBot[next].z) / 4f;
            quads.add(new QuadFace(i, next, zAvg));
        }

        Collections.sort(quads);

        fill(40);
        stroke(255);
        strokeWeight(1.5f);
        for (QuadFace q : quads) {
            beginShape();
            vertex(pTop[q.i1].x, pTop[q.i1].y);
            vertex(pTop[q.i2].x, pTop[q.i2].y);
            vertex(pBot[q.i2].x, pBot[q.i2].y);
            vertex(pBot[q.i1].x, pBot[q.i1].y);
            endShape(CLOSE);
        }
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
        PApplet.main("Classes.Rig2");
        PApplet.main("Classes.Rig3");
        PApplet.main("Classes.Rig4");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}