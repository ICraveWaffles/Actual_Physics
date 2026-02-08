package Animations;
import Classes.Graph;
import Classes.Wheel;
import processing.core.PApplet;

public class kin2 extends PApplet {

    float w = 53 / 60f;
    float t;
    float beats;
    float startTime;

    boolean beatOne = false;
    boolean beatTwo = false;

    Wheel w1;

    Graph gPos;
    Graph gVel;
    Graph gAcc;

    public void settings() {
        fullScreen();
    }

    public void setup() {
        startTime = millis();

        w1 = new Wheel(960, 640, 300, 10);
        w1.omega = 0.01f;

        gPos = new Graph(this, 50, 50, 400, 150, 300, 0, TWO_PI);
        gVel = new Graph(this, 530, 50, 400, 150, 300, 0, 0.2f);
        gAcc = new Graph(this, 1010, 50, 400, 150, 300, -0.003f, 0.003f);
    }

    public void draw() {
        t = (millis() - startTime) / 1000.0f;
        beats = t * w;

        background(0);

        w1.update();
        w1.display(this);

        gPos.actualizar(w1.phi % TWO_PI);
        gVel.actualizar(w1.omega);
        gAcc.actualizar(w1.alpha);

        gPos.display();
        gVel.display();
        gAcc.display();

        if (beats > 1 && !beatOne) {
            beatOne = true;
            w1.alpha = 0.002f;
        }

    }

    public static void main(String[] args) {
        PApplet.main("Animations.kin2");
    }
}


