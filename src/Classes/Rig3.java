package Classes;

import processing.core.PApplet;
import java.util.ArrayList;
import java.util.Collections;

public class Rig3 extends PApplet {

    float t = 0;
    float w = 5/6f;
    float logoTransparency;
    float transY;

    public static Alogo alogo;

    float maxBeats = 4f;
    float theta = 0;
    float omega = 0;

    public void draw() {
        pushStyle();

        frameRate = 30;
        t = frameCount / (float) frameRate;
        float b = t * w;

        if (frameCount == 1 || b == 0) {
            theta = 0;
            omega = 0;
        }

        if (b >= maxBeats) {
            frameCount = 0;
            t = 0;
            b = 0;
        }

        background(0);

        transY = b % 1f;
        logoTransparency = (float) (255 * (-Math.pow(transY, 2) + 2 * (transY)));
        alogo.display(this, b, logoTransparency);

        float L = 800f;
        float H = 60f;
        float D = 60f;

        float I = (L * L) + (D * D);
        float omegaTarget = (PI / 3f) * 2.0f;
        float pushDurationBeats = 2.5f;

        float torqueMag = 0f;

        if (b < pushDurationBeats) {
            torqueMag = (I * omegaTarget) / pushDurationBeats;
        }

        float alpha = torqueMag / I;
        float db = (1f / 30f) * w;

        omega += alpha * db;
        theta += omega * db;

        float cx = width / 2f;
        float cy = height / 2f;

        drawPseudo3DCylinder(cx, cy, 12, 0, 200);
        drawPseudo3DBox(cx, cy, theta, L, H, D);
        drawPseudo3DCylinder(cx, cy, 12, -200, 0);

        if (b < pushDurationBeats) {
            drawVector3D(L / 2f, 0, 0, 0, 0, -180f, theta, cx, cy);
            drawVector3D(0, -100, 0, 0, -120f, 0, 0, cx, cy);
        }

        popStyle();
    }

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
        int shade;

        Face(int[] vIdx, float avgZ, int shade) {
            this.vIdx = vIdx;
            this.avgZ = avgZ;
            this.shade = shade;
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

        P3[] normals = {
                new P3(0, 0, -1), new P3(0, 0, 1), new P3(-1, 0, 0),
                new P3(1, 0, 0),  new P3(0, -1, 0), new P3(0, 1, 0)
        };

        ArrayList<Face> faceList = new ArrayList<>();
        float cosY = cos(rotY);
        float sinY = sin(rotY);

        for (int k = 0; k < faces.length; k++) {
            int[] f = faces[k];
            float zSum = projVerts[f[0]].z + projVerts[f[1]].z + projVerts[f[2]].z + projVerts[f[3]].z;

            P3 n = normals[k];
            float rnx = n.x * cosY + n.z * sinY;
            float rny = n.y;
            float rnz = -n.x * sinY + n.z * cosY;

            float dot = max(0.05f, -rnx * 0.3f - rny * 0.6f + rnz * 0.7f);
            int shade = (int) (15 + dot * 85);

            faceList.add(new Face(f, zSum / 4f, shade));
        }

        Collections.sort(faceList);

        for (Face f : faceList) {
            fill(f.shade);
            stroke(255);
            strokeWeight(2.5f);
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

    void drawVector3D(float x0, float y0, float z0, float dx, float dy, float dz, float rotY, float cx, float cy) {
        P3 p0 = project(x0, y0, z0, rotY, cx, cy);
        P3 p1 = project(x0 + dx, y0 + dy, z0 + dz, rotY, cx, cy);

        float len = dist(p0.x, p0.y, p1.x, p1.y);
        if (len < 2) return;

        stroke(255);
        strokeWeight(3f);
        line(p0.x, p0.y, p1.x, p1.y);

        float angle = atan2(p1.y - p0.y, p1.x - p0.x);
        float arrowSize = min(14, len * 0.3f);

        pushMatrix();
        translate(p1.x, p1.y);
        rotate(angle);
        fill(255);
        noStroke();
        triangle(0, 0, -arrowSize, -arrowSize * 0.45f, -arrowSize, arrowSize * 0.45f);
        popMatrix();
    }

    public void settings() {
        fullScreen();
    }

    public void setup() {
        float finalX = (width / 2f) + 865.3f;
        float finalY = (height / 2f) - 458.1f;
        float finalW = (height / 2f) - 381.75f;
        float finalH = (height / 2f) - 381.75f;

        alogo = new Alogo(finalX, finalY, finalW, finalH);

        noCursor();
    }

    public static void main(String[] args) {
        PApplet.main("Classes.Rig3");
    }

    public void mousePressed() {
        t = 0;
        frameCount = 0;
    }
}