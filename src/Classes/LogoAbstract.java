package Classes;

import processing.core.PApplet;
import java.lang.Math;

public class LogoAbstract {

    public float x;
    public float y;
    public float w;
    public float h;
    float d;

    char b;

    public LogoAbstract (float x, float y, float w, float h){
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.d = 0;
        this.b = '8';
    }

    public void display(PApplet p5, float t) {
        p5.pushStyle();

        p5.noStroke();

        p5.rectMode(p5.CENTER);
        p5.fill(255);

        if (this.b == '8') {
            p5.rect(x, y, (float) (w * (1 + 0.5f * Math.sin(t))), (float) (h * (1 - 0.5 * Math.sin(t))));
        } else if (this.b == 'A') {
            p5.triangle(x - 0.25f * w, y - 0.5f * h, x, y + 0.5f * h, x - 0.5f * w, y + 0.5f * h);
            p5.fill(0);
            p5.triangle(x - 0.3f * w, y - 0.12f * h, x - 0.25f * w, y + 0.45f * h, x - 0.45f * w, y + 0.45f * h);
            p5.fill(255);

            p5.beginShape();

            p5.vertex(x + 0.1f * w * sin99(t), y);
            p5.vertex(x + 0.15f * w + 0.1f * w * sin99(t), y);
            p5.vertex(x + 0.5f * w - 0.1f * w * sin99(t), y + 0.5f * h);
            p5.vertex(x + 0.35f * w - 0.1f * w * sin99(t), y + 0.5f * h);

            p5.endShape(p5.CLOSE);

            p5.beginShape();

            p5.vertex(x + 0.3f * w + 0.1f * w * sin99(t), y);
            p5.vertex(x + 0.4f * w + 0.1f * w * sin99(t), y);
            p5.vertex(x + 0.2f * w - 0.1f * w * sin99(t), y + 0.5f * h);
            p5.vertex(x + 0.1f * w - 0.1f * w * sin99(t), y + 0.5f * h);

            p5.endShape(p5.CLOSE);
        } else if (b == 'B') {

            p5.rect(x, y, w, h);

        } else if (b == 'C') {

            float baseCol = (float) (127 * Math.sin(t) + 127);
            p5.strokeWeight(15);
            p5.noFill();

            for (int i = 15; i < (int) w; i += 15) {

                p5.stroke(baseCol);
                p5.circle(x, y, i);

                baseCol = (float) (127 * Math.sin((Math.PI * t + i / 15f)) + 127);
            }
        } else if (b == 'D') {
            p5.circle(x, y, w / 15f);
            p5.noFill();
            p5.strokeWeight(1);
            p5.stroke(255);
            p5.circle(x, y, w);
            p5.circle(x, y, (float) (w / Math.pow(1.5f, 2f)));
            p5.circle(x, y, (float) (w / Math.pow(2, 2f)));
            p5.circle(x, y, (float) (w / Math.pow(2.5f, 2f)));
            p5.circle(x, y, (float) (w / Math.pow(3f, 2f)));
            p5.circle(x, y, (float) (w / Math.pow(3.5f, 2f)));


            p5.line(x + w / 2f, y, x - w / 2f, y);
            p5.line(x, y + h / 2f, x, y - h / 2f);
            p5.line(x - w / 2f, y - h / 2f, x + w / 2f, y + h / 2f);
            p5.line(x + w / 2f, y - h / 2f, x - w / 2f, y + h / 2f);

            int nPoints = 8;

            float phase = t % 1f;

            float dist = (w/2f) * (1f - phase);

            for (int i = 0; i < nPoints; i++) {

                float angle = (float) (i * Math.TAU / nPoints);

                float px = x + dist * (float) Math.cos(angle);
                float py = y + dist * (float) Math.sin(angle);

                float rot = angle + (float) Math.PI;

                p5.pushMatrix();
                p5.translate(px, py);
                p5.rotate(rot);

                float size = 10;

                p5.fill(255);
                p5.noStroke();

                p5.beginShape();
                p5.vertex(size, 0);
                p5.vertex(-size * 0.6f, size * 0.4f);
                p5.vertex(-size * 0.6f, -size * 0.4f);
                p5.endShape(p5.CLOSE);

                p5.popMatrix();
            }

        } else if (b == 'E') {

            p5.stroke(255);
            p5.noFill();
            p5.strokeWeight(2);

            float L = 10f;
            float speed = 2f;

            float shift = 3.5f * (float)Math.sin(t * speed);

            p5.beginShape();

            for (float i = 0; i <= w; i += 1) {

                float xNorm = (i / w) * L - 6f + shift;

                float yVal = (float)(
                        Math.exp(-Math.pow(xNorm+0.01*i, 2)) *
                                Math.sin(10 * xNorm)
                );

                float px = x + i;
                float py = y - yVal * h/2f;

                p5.vertex(px, py);
            }

            p5.endShape();
        }
        }

    public float sin99(float t){

        return (float) Math.pow(Math.sin(Math.PI*t), 39);

    }

}
