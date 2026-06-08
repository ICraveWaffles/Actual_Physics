package Classes;

import processing.core.PApplet;

public class Phone {

    public float x, y, w, h;
    int phase;

    public Phone(){
        this.x = 640;
        this.y = 540;
        this.w = 480;
        this.h = 960;
        this.phase = 0;
    }

    public void rotate(){
            phase++;
    }

    public void display(PApplet p5, int t){

        p5.pushStyle();
        p5.rectMode(p5.CENTER);
        p5.stroke(255);
        p5.fill(0);

        switch (t % 8){

            case 0:
                p5.rect(x, y, w, 1.01f*h, 50);
                p5.rect(x, y, w, h, 50);

                p5.fill(0, 255, 0);
                p5.rect(x, y, 0.99f*w, 0.8f*h, 2);
                break;

            case 1:
                p5.rect(x - w/18f, y, w*0.7f, h, 50);
                p5.rect(x + w/18f, y, w*0.7f, h, 50);

                p5.fill(0, 255, 0);
                p5.rect(x + w/18f, y, 0.69f*w, 0.8f*h, 2);
                break;

            case 2:
                p5.rect(x - w/64f, y, w*0.1f, h, 5);
                p5.rect(x + w/64f, y, w*0.1f, h, 5);

                break;

            case 3:
                p5.rect(x + w/18f, y, w*0.7f, h, 50);
                p5.rect(x - w/18f, y, w*0.7f, h, 50);
                break;

            case 4:
                p5.rect(x, y, w, h, 50);
                p5.rect(x, y, 1.01f*w, h, 50);
                break;

            case 5:
                p5.rect(x - w/18f, y, w*0.7f, h, 50);
                p5.rect(x + w/18f, y, w*0.7f, h, 50);
                break;

            case 6:
                p5.rect(x + w/64f, y, w*0.1f, h, 5);
                p5.rect(x - w/64f, y, w*0.1f, h, 5);
                break;

            case 7:
                p5.rect(x + w/18f, y, w*0.7f, h, 50);
                p5.rect(x - w/18f, y, w*0.7f, h, 50);

                p5.fill(0, 255, 0);
                p5.rect(x - w/18f, y, 0.69f*w, 0.8f*h, 20);
                break;
        }

        p5.popStyle();

    }
}
