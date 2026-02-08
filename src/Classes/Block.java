package Classes;

import processing.core.PApplet;



public class Block {

    public int sh;
    public float x,y;
    public float vx, vy;
    public float ax, ay;
    public float w,h;

    public Block(float x, float y, float vx, float vy, float ax, float ay, float w, float h, int sh){
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.ax = ax;
        this.ay = ay;
        this.w = w;
        this.h = h;
        this.sh = sh;
    }

    public void update(){
        this.vx += this.ax;
        this.vy -= this.ay;
        this.x += this.vx;
        this.y += this.vy;

    }

    public void display(PApplet p5){
        p5.pushStyle();

        p5.noFill();
        p5.strokeWeight(10);
        p5.stroke(255);
        if (this.sh == 0) {
            p5.rectMode(p5.CENTER);
            p5.rect(this.x, this.y, this.w, this.h);
        } else {
            p5.ellipse(this.x, this.y, this.w, this.h);
        }




        p5.popStyle();
    }

}
