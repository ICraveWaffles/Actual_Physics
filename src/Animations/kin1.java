package Animations;

import Classes.Block;
import Classes.Graph;
import processing.core.PApplet;

import static processing.core.PApplet.dist;

public class kin1 {

    float w = 53 / 60f;

    boolean beatOne = false;
    boolean beatTwo = false;

    Block b1;

    Graph gPos;
    Graph gVel;
    Graph gAcc;

    public kin1(PApplet p) {

        b1 = new Block(200, 900, 11, 0, 0, 0, 50, 50, 1);

        gPos = new Graph(p, 50, 50, 400, 150, 300, 0, 1500);
        gVel = new Graph(p, 530, 50, 400, 150, 300, -32, 32);
        gAcc = new Graph(p, 1010, 50, 400, 150, 300, -0.2f, 0.2f);
    }

    public void update(float beats) {

        b1.update();

        if (beatOne && !beatTwo) {
            gVel.actualizar(2 * b1.vy);
        } else {
            gVel.actualizar(2 * (float)Math.sqrt(b1.vx * b1.vx + b1.vy * b1.vy));
        }

        if (!beatOne) {
            gPos.actualizar(b1.x - 200);
        } else if (!beatTwo) {
            gPos.actualizar(-b1.y + 900);
        } else {
            gPos.actualizar((float)dist(200, 900, b1.x, b1.y));
        }

        gAcc.actualizar(b1.ay);

        if (beats > 2 && !beatOne) {
            b1.vx = 0;
            b1.ay = -0.16f;
            b1.vy = -11;
            beatOne = true;
        }

        if (beats > 4 && !beatTwo) {
            b1.vx = -11;
            b1.vy = -11;
            b1.ay = -0.16f;
            beatTwo = true;
        }

        if (beats > 6) {
            b1.ay = 0;
            b1.vx = 0;
            b1.vy = 0;
        }
    }
    public void display(PApplet p) {
        b1.display(p);
        gPos.display();
        gVel.display();
        gAcc.display();
    }
}
