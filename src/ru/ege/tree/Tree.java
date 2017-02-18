package ru.ege.tree;

import ru.ege.engine.DrawableObject;
import ru.ege.engine.EGEJFrame;
import ru.ege.engine.Vector2D;

import java.awt.*;

public class Tree implements DrawableObject {
    Tree left = null;
    Tree right = null;
    Tree parent;
    int data;

    void setLeft(Tree t) {
        left = t;
        left.parent = this;
    }

    void setRight(Tree t) {
        right = t;
        right.parent = this;
    }

    public static void add(int x, Tree t) {
        if (x < t.data) {
            if (t.left == null) {
                t.setLeft(new Tree(x));
            } else {
                add(x, t.left);
            }
        } else {
            if (t.right == null) {
                t.setRight(new Tree(x));
            } else {
                add(x, t.right);
            }
        }
    }

    public static int max(Tree t) {
        if (t.right == null && t.left == null) {
            return t.data;
        }
        if (t.right != null && t.left == null) {
            return Math.max(t.data, max(t.right));
        }
        if (t.right == null && t.left != null) {
            return Math.max(t.data, max(t.left));
        }
        return Math.max(t.data, Math.max(max(t.left), max(t.right)));
    }

    public static int depth(Tree t) {
        return depth(t, 0);
    }

    public static int depth(Tree t, int x) {
        if (t.right == null && t.left == null) {
            return x + 1;
        }
        if (t.right != null && t.left == null) {
            return depth(t.right, x + 1);
        }
        if (t.right == null && t.left != null) {
            return depth(t.left, x + 1);
        }
        return Math.max(depth(t.right, x + 1), depth(t.left, x + 1));
    }

    public int down(int x) {
        if (right == null && left == null) {
            return x + 1;
        }
        if (right != null && left == null) {
            return right.down(x + 1);
        }
        if (right == null && left != null) {
            return left.down(x + 1);
        }
        return Math.max(left.down(x + 1), right.down(x + 1));
    }

    public int up() {
        if (parent == null) {
            return 0;
        }
        if (parent != null) {
            return 1 + parent.up();
        }
        return 1 + parent.up();
    }

    public int position() {
        String s = new String("");
        int p = 1;
        Tree t = this;
        while (t.parent != null) {
            if (t.parent.right == t) {
                s = "r" + s;
            }
            if (t.parent.left == t) {
                s = "l" + s;
            }
            t = t.parent;
        }
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == 'r') {
                p += Math.pow(2, s.length() - (i + 1));
            }
        }
        return p;
    }

    public Tree(int data) {
        this.data = data;
    }

    public Tree(int data, Tree left, Tree right) {
        this.data = data;
        setLeft(left);
        setRight(right);
    }

    @Override
    public String toString() {
        return data + " {" + ((left == null) ? "" : left) + ", " +//"} " + "{" +
                ((right == null) ? "" : right) +
                "} ";
    }

    private int totalLevel() {
        Tree t = this;
        while (t.parent != null) {
            t = t.parent;
        }
        return depth(t);
    }

    public Vector2D getCoordinates(EGEJFrame EGEJFrame) {
        int level = up();
        int totalLevel = totalLevel() + 1;
        int position = position();
        double totalPosition = Math.pow(2, level) + 1;
        Vector2D v = new Vector2D((EGEJFrame.getWidth() * position) / totalPosition, (EGEJFrame.getHeight() * level) / totalLevel);
        return v;
    }

    @Override
    public void drawAndUpdate(Graphics2D g, int dt, EGEJFrame EGEJFrame) {
        g.setFont(new Font("Times New Roman", Font.BOLD, 17));
        g.drawString("Binary Search Tree", 40, 60);
        Vector2D v = getCoordinates(EGEJFrame);
        int d = 100;
        g.drawString(String.valueOf(data), v.getXInt(), v.getYInt() + d);
        if (left != null) {
            g.drawLine(v.getXInt(), v.getYInt() + d, left.getCoordinates(EGEJFrame).getXInt(), left.getCoordinates(EGEJFrame).getYInt() + d);
        }
        if (right != null) {
            g.drawLine(v.getXInt(), v.getYInt() + d, right.getCoordinates(EGEJFrame).getXInt(), right.getCoordinates(EGEJFrame).getYInt() + d);
        }
        if (left != null) {
            left.drawAndUpdate(g, dt, EGEJFrame);
        }
        if (right != null) {
            right.drawAndUpdate(g, dt, EGEJFrame);
        }
    }

}
