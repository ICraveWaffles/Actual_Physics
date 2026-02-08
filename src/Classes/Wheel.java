package Classes;

import processing.core.PApplet;

public class Wheel {

    public float x, y, r;
    public float phi, omega, alpha;
    public int dots;

    public Wheel(float x, float y, float r, int n) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.dots = n;

        this.phi = 0;
        this.omega = 0;
        this.alpha = 0;
    }

    public void update() {
        this.phi += this.omega;
        this.omega += this.alpha;
    }

    public void display(PApplet p) {
        p.pushStyle();

        p.noFill();
        p.stroke(255);
        p.ellipse(x, y, 2 * r, 2 * r);

        p.fill(255, 255, 0);
        p.noStroke();

        for (int i = 0; i < dots; i++) {
            float angle = phi + p.TWO_PI * i / dots;
            float px = x + r * p.cos(angle);
            float py = y + r * p.sin(angle);
            p.circle(px, py, 15);
        }
        p.popStyle();
    }
}

