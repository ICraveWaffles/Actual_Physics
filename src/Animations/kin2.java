package Animations;

import Classes.Graph;
import Classes.Wheel;
import processing.core.PApplet;

public class kin2 {

    boolean beatOne = false;

    Wheel w1;

    Graph gPos;
    Graph gVel;
    Graph gAcc;

    public kin2(PApplet p) {

        w1 = new Wheel(960, 640, 300, 10);
        w1.omega = 0.01f;

        gPos = new Graph(p, 50, 50, 400, 150, 300, 0, PApplet.TWO_PI);
        gVel = new Graph(p, 530, 50, 400, 150, 300, 0, 0.2f);
        gAcc = new Graph(p, 1010, 50, 400, 150, 300, -0.003f, 0.003f);
    }

    public void update(float beats) {

        w1.update();
        w1.update();

        gPos.actualizar(w1.phi % PApplet.TWO_PI);
        gVel.actualizar(w1.omega);
        gAcc.actualizar(w1.alpha);

        if (beats > 7 && !beatOne) {
            w1.alpha = 0.002f;
            beatOne = true;
        }
    }

    public void display(PApplet p) {
        w1.display(p);
        gPos.display();
        gVel.display();
        gAcc.display();
    }
}


