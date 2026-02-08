package Animations;
import Classes.Block;
import Classes.Graph;
import processing.core.PApplet;

public class kin1 extends PApplet {

    float w = 53 / 60f;
    float t;
    float beats;
    float startTime;

    boolean beatOne = false;
    boolean beatTwo = false;

    Block b1;

    Graph gPos;
    Graph gVel;
    Graph gAcc;

    public void settings() {
        fullScreen();
    }

    public void setup() {
        startTime = millis();

        b1 = new Block(200, 900, 11, 0, 0, 0, 50, 50, 1);

        gPos = new Graph(this, 50, 50, 400, 150, 300, 0, 1500);
        gVel = new Graph(this, 530, 50, 400, 150, 300, -32, 32);
        gAcc = new Graph(this, 1010, 50, 400, 150, 300, -0.2f, 0.2f);
    }

    public void draw() {
        t = (millis() - startTime) / 1000.0f;
        beats = t * w;

        background(0);

        b1.update();
        b1.display(this);

        if (beatOne && !beatTwo){
            gVel.actualizar(2*b1.vy);
        } else {
            gVel.actualizar(2*sqrt(b1.vx * b1.vx + b1.vy * b1.vy));
        }
        if (!beatOne) {
            gPos.actualizar(b1.x - 200);
        } else if (!beatTwo){
            gPos.actualizar(-b1.y + 900);
        } else {
            gPos.actualizar(dist(200, 900, b1.x, b1.y));
        }
        gAcc.actualizar(b1.ay);

        gPos.display();
        gVel.display();
        gAcc.display();

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

    public static void main(String[] args) {
        PApplet.main("Animations.kin1");
    }
}


