package Classes;

import processing.core.PApplet;
import processing.core.PFont;

public class EI extends PApplet {

    private final float BEAT_DURATION = 0.6f;
    private final float TOTAL_BEATS = 4.0f;
    private float globalTime = 0;

    private PFont fontTimesBold;
    public static Elogo elogo;

    private int[] pCol = {
            0, 0, 0, 0,
            1, 1, 1, 1,
            2, 2, 2, 2,
            3, 3, 3, 3,
            4
    };

    private int[] pRow = {
            0, 1, 2, 3,
            0, 1, 2, 3,
            0, 1, 2, 3,
            0, 1, 2, 3,
            0
    };

    private String[] pSymbol = {
            "u", "d", "e⁻", "νₑ",
            "c", "s", "μ⁻", "νᵤ",
            "t", "b", "τ⁻", "νₜ",
            "g", "γ", "Z", "W",
            "H"
    };

    private float[] pScale = {
            0.70f, 0.70f, 0.60f, 0.42f,
            0.90f, 0.80f, 0.75f, 0.42f,
            1.35f, 1.05f, 0.90f, 0.42f,
            0.85f, 0.80f, 1.15f, 1.10f,
            1.20f
    };

    private boolean[] fStrong = {
            true, true, false, false,
            true, true, false, false,
            true, true, false, false,
            true, false, false, false,
            false
    };

    private boolean[] fEM = {
            true, true, true, false,
            true, true, true, false,
            true, true, true, false,
            false, true, false, true,
            false
    };

    private boolean[] fWeak = {
            true, true, true, true,
            true, true, true, true,
            true, true, true, true,
            false, false, true, true,
            true
    };

    private boolean[] fGravity = {
            true, true, true, true,
            true, true, true, true,
            true, true, true, true,
            true, true, true, true,
            true
    };

    private boolean[] isMediatorStrong = {
            false, false, false, false,
            false, false, false, false,
            false, false, false, false,
            true, false, false, false,
            false
    };

    private boolean[] isMediatorEM = {
            false, false, false, false,
            false, false, false, false,
            false, false, false, false,
            false, true, false, false,
            false
    };

    private boolean[] isMediatorWeak = {
            false, false, false, false,
            false, false, false, false,
            false, false, false, false,
            false, false, true, true,
            false
    };

    private boolean[] isMediatorGravity = {
            false, false, false, false,
            false, false, false, false,
            false, false, false, false,
            false, false, false, false,
            true
    };

    @Override
    public void settings() {
        fullScreen();
        smooth(8);
    }

    @Override
    public void setup() {
        fontTimesBold = createFont("Times New Roman Bold", 22, true);

        float cx = width * 0.5f;
        float cy = height * 0.5f;
        elogo = new Elogo(cx + 865.3f, cy - 458.1f, cy - 381.75f, cy - 381.75f);
        noCursor();
    }

    @Override
    public void draw() {
        background(0);

        globalTime = (millis() * 0.001f) % (BEAT_DURATION * TOTAL_BEATS);
        float currentBeat = globalTime / BEAT_DURATION;

        float centerX = width * 0.5f;
        float centerY = height * 0.5f;

        int activeForce = -1;
        if (currentBeat >= 2.0f) {
            activeForce = constrain((int) ((currentBeat - 2.0f) / 0.5f), 0, 3);
        }

        drawGridParticles(centerX, centerY, activeForce, currentBeat);
        drawForceCircles(centerX, centerY, activeForce, currentBeat);

        if (elogo != null) {
            elogo.display(this, currentBeat, 255);
        }
    }

    private void drawGridParticles(float ox, float oy, int activeForce, float beat) {
        float spacing = min(width, height) * 0.165f;
        float startX = ox - (spacing * 2.0f);
        float startY = oy - (spacing * 1.5f);

        float timeSec = millis() * 0.001f;

        int total = isMediatorGravity.length;
        for (int i = 0; i < total; i++) {
            float phase = i * 1.73f;
            float offsetX = sin(timeSec * 2.2f + phase) * (spacing * 0.025f);
            float offsetY = cos(timeSec * 1.8f + phase * 1.3f) * (spacing * 0.025f);
            float scaleFluctuation = 1.0f + 0.06f * sin(timeSec * 3.1f + phase * 2.1f);

            float px = startX + pCol[i] * spacing + offsetX;
            float py = startY + pRow[i] * spacing + offsetY;

            boolean affected = false;
            boolean isMediator = false;

            if (activeForce == 0) {
                affected = fStrong[i];
                isMediator = isMediatorStrong[i];
            } else if (activeForce == 1) {
                affected = fEM[i];
                isMediator = isMediatorEM[i];
            } else if (activeForce == 2) {
                affected = fWeak[i];
                isMediator = isMediatorWeak[i];
            } else if (activeForce == 3) {
                affected = fGravity[i];
                isMediator = isMediatorGravity[i];
            }

            float glow = 0;
            if (activeForce != -1 && affected && !isMediator) {
                float localPhase = (beat - 2.0f) % 0.5f;
                glow = sin(map(localPhase, 0, 0.5f, 0, PI)) * 255f;
            }

            pushMatrix();
            translate(px, py);

            float radius = spacing * 0.35f * pScale[i] * scaleFluctuation;

            if (glow > 0) {
                noFill();
                stroke(255, glow);
                strokeWeight(map(glow, 0, 255, 1f, 5f));
                float expand = map(glow, 0, 255, 35f, 0f);
                ellipse(0, 0, (radius * 2) + expand, (radius * 2) + expand);

                fill(255, glow * 0.3f);
                noStroke();
                ellipse(0, 0, radius * 2, radius * 2);
            }

            if (isMediator) {
                fill(255);
                stroke(255);
                strokeWeight(3.0f);
                ellipse(0, 0, radius * 2, radius * 2);

                fill(0);
                textFont(fontTimesBold);
                textSize(radius * 1.0f);
                textAlign(CENTER, CENTER);
                text(pSymbol[i], 0, -radius * 0.1f);
            } else {
                fill(15);
                stroke(255);
                strokeWeight(2.2f);
                ellipse(0, 0, radius * 2, radius * 2);

                fill(255);
                textFont(fontTimesBold);
                textSize(radius * 1.0f);
                textAlign(CENTER, CENTER);
                text(pSymbol[i], 0, -radius * 0.1f);
            }

            popMatrix();
        }
    }

    private void drawForceCircles(float ox, float oy, int activeForce, float beat) {
        float spacing = min(width, height) * 0.165f;
        float forceRadius = spacing * 0.65f;

        float[] fx = {
                ox - (spacing * 4.1f),
                ox - (spacing * 4.1f),
                ox + (spacing * 4.1f),
                ox + (spacing * 4.1f)
        };

        float[] fy = {
                oy - (spacing * 1.1f),
                oy + (spacing * 1.1f),
                oy - (spacing * 1.1f),
                oy + (spacing * 1.1f)
        };

        for (int i = 0; i < 4; i++) {
            float startBeat = 2.0f + (i * 0.5f);
            if (beat < startBeat) {
                continue;
            }

            boolean isActive = (activeForce == i);

            float glow = 0;
            if (isActive) {
                float localPhase = (beat - 2.0f) % 0.5f;
                glow = sin(map(localPhase, 0, 0.5f, 0, PI)) * 255f;
            }

            pushMatrix();
            translate(fx[i], fy[i]);

            if (glow > 0) {
                noFill();
                stroke(255, glow);
                strokeWeight(map(glow, 0, 255, 1.5f, 6f));
                float expand = map(glow, 0, 255, 45f, 0f);
                ellipse(0, 0, (forceRadius * 2) + expand, (forceRadius * 2) + expand);
            }

            int bgCol = isActive ? 255 : 15;
            int fgCol = isActive ? 0 : 255;

            fill(bgCol);
            stroke(255);
            strokeWeight(2.8f);
            ellipse(0, 0, forceRadius * 2, forceRadius * 2);

            drawForceGraphics(i, forceRadius, fgCol, bgCol);

            popMatrix();
        }
    }

    private void drawForceGraphics(int type, float r, int fg, int bg) {
        if (type == 0) {
            noFill();
            stroke(fg, 180);
            strokeWeight(1.5f);
            ellipse(0, 0, r * 1.45f, r * 1.45f);

            float qR = r * 0.28f;
            float[][] qPos = {
                    {0, -r * 0.38f},
                    {-r * 0.33f, r * 0.24f},
                    {r * 0.33f, r * 0.24f}
            };
            String[] qSym = {"u", "d", "u"};

            stroke(fg, 200);
            strokeWeight(1.5f);
            line(qPos[0][0], qPos[0][1], qPos[1][0], qPos[1][1]);
            line(qPos[1][0], qPos[1][1], qPos[2][0], qPos[2][1]);
            line(qPos[2][0], qPos[2][1], qPos[0][0], qPos[0][1]);

            for (int k = 0; k < 3; k++) {
                fill(bg);
                stroke(fg);
                strokeWeight(2f);
                ellipse(qPos[k][0], qPos[k][1], qR * 2, qR * 2);

                fill(fg);
                textFont(fontTimesBold);
                textSize(qR * 1.1f);
                textAlign(CENTER, CENTER);
                text(qSym[k], qPos[k][0], qPos[k][1] - qR * 0.1f);
            }

        } else if (type == 1) {
            noFill();
            stroke(fg, 220);
            strokeWeight(1.6f);

            bezier(-r * 0.45f, 0, -r * 0.2f, -r * 0.65f, r * 0.2f, -r * 0.65f, r * 0.45f, 0);
            bezier(-r * 0.45f, 0, -r * 0.2f, r * 0.65f, r * 0.2f, r * 0.65f, r * 0.45f, 0);
            bezier(-r * 0.45f, 0, -r * 0.2f, -r * 0.32f, r * 0.2f, -r * 0.32f, r * 0.45f, 0);
            bezier(-r * 0.45f, 0, -r * 0.2f, r * 0.32f, r * 0.2f, r * 0.32f, r * 0.45f, 0);
            line(-r * 0.45f, 0, r * 0.45f, 0);

            float cR = r * 0.24f;

            fill(bg);
            stroke(fg);
            strokeWeight(2f);
            ellipse(-r * 0.45f, 0, cR * 2, cR * 2);
            ellipse(r * 0.45f, 0, cR * 2, cR * 2);

            fill(fg);
            textFont(fontTimesBold);
            textSize(cR * 1.2f);
            textAlign(CENTER, CENTER);
            text("+", -r * 0.45f, -cR * 0.1f);
            text("−", r * 0.45f, -cR * 0.1f);

        } else if (type == 2) {
            float nR = r * 0.16f;
            float[][] nPos = {
                    {-nR * 0.6f, -nR * 0.6f},
                    {nR * 0.6f, -nR * 0.6f},
                    {-nR * 0.6f, nR * 0.6f},
                    {nR * 0.6f, nR * 0.6f},
                    {0, 0}
            };

            for (int k = 0; k < 5; k++) {
                fill(k % 2 == 0 ? fg : bg);
                stroke(fg);
                strokeWeight(1.5f);
                ellipse(nPos[k][0] - r * 0.18f, nPos[k][1] + r * 0.18f, nR * 2, nR * 2);
            }

            stroke(fg);
            strokeWeight(1.8f);
            noFill();
            beginShape();
            for (float t = 0; t <= 1; t += 0.08f) {
                float px = lerp(-r * 0.05f, r * 0.55f, t);
                float py = lerp(r * 0.05f, -r * 0.55f, t) + sin(t * TWO_PI * 2.5f) * (r * 0.08f);
                vertex(px, py);
            }
            endShape();

            fill(fg);
            noStroke();
            ellipse(r * 0.55f, -r * 0.55f, nR * 1.4f, nR * 1.4f);

        } else if (type == 3) {
            stroke(fg);
            strokeWeight(3.5f);
            line(-r * 0.32f, -r * 0.55f, -r * 0.32f, r * 0.55f);

            noFill();
            strokeWeight(2.2f);
            for (int k = 0; k < 4; k++) {
                float w = (r * 0.75f) + k * (r * 0.12f);
                float h = (r * 0.95f) + k * (r * 0.12f);
                stroke(fg, 255 - (k * 45));
                arc(-r * 0.32f, 0, w, h, -HALF_PI, HALF_PI);
            }
        }
    }

    @Override
    public void mousePressed() {
        globalTime = 0;
    }

    public static void main(String[] args) {
        PApplet.main("Classes.EI");
    }
}