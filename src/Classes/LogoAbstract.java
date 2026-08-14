package Classes;

import processing.core.PApplet;

public class LogoAbstract {

    public float x, y, w, h;
    float d;
    public char b;

    public LogoAbstract(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.d = 0;
        this.b = '8';
    }

    public void display(PApplet p5, float t, float trans) {
        p5.pushStyle();

        p5.rectMode(p5.CENTER);
        p5.noStroke();
        p5.fill(trans);

        switch (b) {

            case '8':
                drawBase(p5, t, trans);
                break;

            case 'A':
                drawA(p5, t, trans);
                break;

            case 'B':
                drawB(p5, t, trans);
                break;

            case 'C':
                drawC(p5, t, trans);
                break;

            case 'D':
                drawD(p5, t, trans);
                break;

            case 'E':
                drawE(p5, t, trans);
                break;

            case 'I':
                drawI(p5, trans);
                break;

            case 'X':
                drawX(p5, t, trans);
                break;
        }

        p5.popStyle();
    }

    private void drawBase(PApplet p5, float t, float trans) {
        float scaleX = (float) (1 + 0.5 * Math.sin(t));
        float scaleY = (float) (1 - 0.5 * Math.sin(t));

        p5.stroke(trans);
        p5.fill(trans);

        p5.rect(x, y, w * scaleX, h * scaleY);
    }

    private void drawA(PApplet p5, float t, float trans) {

        p5.stroke(trans);
        p5.fill(trans);

        p5.triangle(x - 0.25f * w, y - 0.5f * h,
                x, y + 0.5f * h,
                x - 0.5f * w, y + 0.5f * h);

        p5.fill(0);
        p5.triangle(x - 0.3f * w, y - 0.12f * h,
                x - 0.25f * w, y + 0.45f * h,
                x - 0.45f * w, y + 0.45f * h);

        p5.fill(trans);

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

    private void drawB(PApplet p5, float t, float trans) {
        p5.noFill();
        p5.stroke(trans);

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

    private void drawC(PApplet p5, float t, float trans) {
        p5.noFill();
        p5.strokeWeight(15);

        float baseCol = (float)(127 * Math.sin(t) + 127);

        for (int i = 15; i < w; i += 15) {
            p5.stroke(baseCol, trans);
            p5.circle(x, y, i);

            baseCol = (float)(127 * Math.sin(Math.PI * t + i / 15f) + 127);
        }
    }

    private void drawD(PApplet p5, float t, float trans) {
        p5.fill(trans);
        p5.noStroke();
        p5.circle(x, y, w / 15f);

        p5.noFill();
        p5.stroke(trans);

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

            drawArrow(p5, 10, trans);

            p5.popMatrix();
        }
    }

    private void drawE(PApplet p5, float t, float trans) {

        p5.noFill();
        p5.stroke(trans);
        p5.strokeWeight(2);

        float L = 10f;

        float shift = 3.5f * (float)Math.sin(PApplet.PI * t);

        p5.beginShape();

        for (float i = 0; i <= w; i++) {

            float xNorm = (i / w) * L - 6f + shift;

            float yVal = (float)(
                    Math.exp(-Math.pow(xNorm + 0.01f*i, 2)) *
                            Math.sin(10 * xNorm)
            );

            float px = x - w/2f + i;

            float py = y - yVal * h/2f;

            p5.vertex(px, py);
        }

        p5.endShape();
    }

    private void drawI(PApplet p5, float trans){

        p5.pushStyle();

        float px = x + w/50f * p5.random(-1, 1);
        float py = y + h/50f * p5.random(-1, 1);
        float errX = p5.random(w/8f, w/2f);
        float errY = p5.random(h/8f, h/2f);
        float left   = Math.max(x - errX, x - w/2f);
        float right  = Math.min(x + errX, x + w/2f);
        float top    = Math.max(y - errY, y - h/2f);
        float bottom = Math.min(y + errY, y + h/2f);


        p5.stroke(trans);
        p5.fill(trans);
        p5.strokeWeight(2);
        p5.noFill();

        p5.line(left, py, right, py);

        p5.line(px, top, px, bottom);

        float cap = w/30f;

        p5.line(left,  py - cap, left,  py + cap);
        p5.line(right, py - cap, right, py + cap);

        p5.line(px - cap, top,    px + cap, top);
        p5.line(px - cap, bottom, px + cap, bottom);


        p5.noStroke();
        p5.fill(trans);

        for(int i = 0; i < 10; i++){

            float sx = p5.random(left, right);
            float sy = p5.random(top, bottom);

            p5.circle(sx, sy, w/35f);
        }

        p5.fill(trans);
        p5.circle(px, py, w/10f);

        p5.popStyle();
    }

    private void drawX(PApplet p5, float t, float trans){

        p5.pushStyle();

        float ropeLength = w * 0.9f;
        float waveAmp = h * 0.1f;

        float leftX = x - ropeLength/2f;
        float rightX = x + ropeLength/2f;

        float phase = t * 5f;

        p5.noFill();
        p5.stroke(trans);
        p5.strokeWeight(3);

        p5.beginShape();

        for(float i = 0; i <= 1; i += 0.02f){

            float px = p5.lerp(leftX, rightX, i);

            float py =
                    y
                            + waveAmp * (float)Math.sin(i * 20 + phase)
                            + waveAmp * 0.4f * (float)Math.sin(i * 55 + phase * 1.7f);

            p5.vertex(px, py);
        }

        p5.endShape();

        p5.strokeWeight(1);

        for(float i = 0; i <= 1; i += 0.05f){

            float px = p5.lerp(leftX, rightX, i);

            float offset =
                    waveAmp * 0.8f *
                            (float)Math.sin(i * 30 + phase);

            p5.line(px, y - offset, px + h*0.01f, y + offset);
        }


        p5.rectMode(p5.CENTER);

        p5.strokeWeight(w/20f);
        p5.stroke(255);

        p5.line(x - w/2.2f, y+h/2.5f, x - w/2.33f, y-h/2.5f);
        p5.line(x + w/2.33f, y+h/2.5f, x + w/2.2f, y-h/2.5f);

        p5.stroke(0);
        p5.strokeWeight(1);

        p5.popStyle();
    }

    private void drawArrow(PApplet p5, float size, float trans) {
        p5.fill(trans);
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