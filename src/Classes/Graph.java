package Classes;

import processing.core.PApplet;

public class Graph {

    public PApplet p;

    public float[] datos;
    public int maxDatos;
    public int x, y, w, h;

    public float minY;
    public float maxY;

    public Graph(PApplet p, int x, int y, int w, int h, int maxDatos, float minY, float maxY) {
        this.p = p;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.maxDatos = maxDatos;
        this.minY = minY;
        this.maxY = maxY;

        datos = new float[maxDatos];
    }

    public void actualizar(float valor) {
        for (int i = 0; i < maxDatos - 1; i++) {
            datos[i] = datos[i + 1];
        }
        datos[maxDatos - 1] = valor;
    }

    public void display() {
        p.pushStyle();

        p.noFill();
        p.stroke(255);
        p.rect(x, y, w, h);


        if (minY < 0 && maxY > 0) {
            float yZero = p.map(0, minY, maxY, y + h, y);

            p.stroke(255, 0, 0);
            float dash = 10;
            float gap = 6;

            for (float xx = x; xx < x + w; xx += dash + gap) {
                p.line(xx, yZero, p.min(xx + dash, x + w), yZero);
            }
        }

        p.stroke(0, 150, 255);
        p.noFill();
        p.beginShape();
        for (int i = 0; i < datos.length; i++) {
            float vx = p.map(i, 0, datos.length - 1, x, x + w);
            float vy = p.map(datos[i], minY, maxY, y + h, y);
            p.vertex(vx, vy);
        }
        p.endShape();

        p.popStyle();
    }
}
