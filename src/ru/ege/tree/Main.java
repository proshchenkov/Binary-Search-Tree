package ru.ege.tree;

import ru.ege.engine.EGEJFrame;

import java.awt.*;
import java.util.Random;

import static ru.ege.tree.Tree.add;
import static ru.ege.tree.Tree.depth;

public class Main {

    public static void main(String[] args) {
        int n = 5;
        Tree t = new Tree(n);
        Random r = new Random();
        for (int i = 0; i < 3 * n; i++) {
            t.add(r.nextInt(3 * n), t);
        }
        System.out.println(t);
        EGEJFrame jf = new EGEJFrame() {
            @Override
            public void drawAndUpdate(Graphics2D graphics, int dt) {
            }
        };
        jf.addDrawableObject(t);
        jf.startDrawingThread();
    }
}
