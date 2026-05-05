package Classes;

import processing.core.PApplet;

public class LogoAbstract {

    public float x, y, w, h;
    float d;
    char b;

    public LogoAbstract(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.d = 0;
        this.b = '8';
    }

    public void display(PApplet p5, float t) {
        p5.pushStyle();

        p5.rectMode(p5.CENTER);
        p5.noStroke();
        p5.fill(255);

        switch (b) {

            case '8':
                drawBase(p5, t);
                break;

            case 'A':
                drawA(p5, t);
                break;

            case 'B':
                drawB(p5, t);
                break;

            case 'C':
                drawC(p5, t);
                break;

            case 'D':
                drawD(p5, t);
                break;

            case 'E':
                drawE(p5, t);
                break;
        }

        p5.popStyle();
    }

    private void drawBase(PApplet p5, float t) {
        float scaleX = (float) (1 + 0.5 * Math.sin(t));
        float scaleY = (float) (1 - 0.5 * Math.sin(t));

        p5.rect(x, y, w * scaleX, h * scaleY);
    }

    private void drawA(PApplet p5, float t) {
        p5.triangle(x - 0.25f * w, y - 0.5f * h,
                x, y + 0.5f * h,
                x - 0.5f * w, y + 0.5f * h);

        p5.fill(0);
        p5.triangle(x - 0.3f * w, y - 0.12f * h,
                x - 0.25f * w, y + 0.45f * h,
                x - 0.45f * w, y + 0.45f * h);

        p5.fill(255);

        float s = 0.1f * w * sin99(t);

        p5.beginShape();
        p5.vertex(x + s, y);
        p5.vertex(x + 0.15f * w + s, y);
        p5.vertex(x + 0.5f * w - s, y + 0.5f * h);
        p5.vertex(x + 0.35f * w - s, y + 0.5f * h);
        p5.endShape(p5.CLOSE);

        p5.beginShape();
        p5.vertex(x + 0.3f * w + s, y);
        p5.vertex(x + 0.4f * w + s, y);
        p5.vertex(x + 0.2f * w - s, y + 0.5f * h);
        p5.vertex(x + 0.1f * w - s, y + 0.5f * h);
        p5.endShape(p5.CLOSE);
    }

    private void drawB(PApplet p5, float t) {
        p5.noFill();
        p5.stroke(255);

        p5.rect(x, y, w, h);

        for (int i = (int)(x - w/2.5f); i < x + w/2; i += (int)(w/6f)) {

            float m = w / 10f;

            for (int j = (int)(y - h/2.5f); j < y + h/2; j += (int)(h/6f)) {

                float dx = 0.03f * m * p5.random(-w/25f, w/25f) * (sin99(t*2) + 0.5f);
                float dy = 0.03f * m * p5.random(-h/25f, h/25f) * (sin99(t) + 0.5f);

                p5.circle(i + dx, j + dy, m);
                m /= 1.5f;
            }
        }
    }

    private void drawC(PApplet p5, float t) {
        p5.noFill();
        p5.strokeWeight(15);

        float baseCol = (float)(127 * Math.sin(t) + 127);

        for (int i = 15; i < w; i += 15) {
            p5.stroke(baseCol);
            p5.circle(x, y, i);

            baseCol = (float)(127 * Math.sin(Math.PI * t + i / 15f) + 127);
        }
    }

    private void drawD(PApplet p5, float t) {
        p5.fill(255);
        p5.noStroke();
        p5.circle(x, y, w / 15f);

        p5.noFill();
        p5.stroke(255);

        float[] scales = {1f, 1.5f, 2f, 2.5f, 3f, 3.5f};
        for (float s : scales) {
            p5.circle(x, y, (float)(w / Math.pow(s, 2)));
        }

        p5.line(x - w/2, y, x + w/2, y);
        p5.line(x, y - h/2, x, y + h/2);
        p5.line(x - w/2, y - h/2, x + w/2, y + h/2);
        p5.line(x + w/2, y - h/2, x - w/2, y + h/2);

        int n = 8;
        float phase = t % 1f;
        float dist = (w/2) * (1 - phase);

        for (int i = 0; i < n; i++) {
            float angle = (float)(i * Math.TAU / n);

            float px = x + dist * (float)Math.cos(angle);
            float py = y + dist * (float)Math.sin(angle);

            p5.pushMatrix();
            p5.translate(px, py);
            p5.rotate(angle + (float)Math.PI);

            drawArrow(p5, 10);

            p5.popMatrix();
        }
    }

    private void drawE(PApplet p5, float t) {
        p5.noFill();
        p5.stroke(255);
        p5.strokeWeight(2);

        float L = 10f;
        float shift = 3.5f * (float)Math.sin(t * 2);

        p5.beginShape();

        for (float i = 0; i <= w; i++) {

            float xNorm = (i / w) * L - 6f + shift;

            float yVal = (float)(
                    Math.exp(-Math.pow(xNorm + 0.01*i, 2)) *
                            Math.sin(10 * xNorm)
            );

            p5.vertex(x + i, y - yVal * h/2);
        }

        p5.endShape();
    }

    private void drawArrow(PApplet p5, float size) {
        p5.fill(255);
        p5.noStroke();

        p5.beginShape();
        p5.vertex(size, 0);
        p5.vertex(-size * 0.6f, size * 0.4f);
        p5.vertex(-size * 0.6f, -size * 0.4f);
        p5.endShape(p5.CLOSE);
    }

    public float sin99(float t) {
        return (float)Math.pow(Math.sin(Math.PI * t), 39);
    }
}