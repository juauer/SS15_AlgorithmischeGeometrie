package datastructures.test;

import geometry.Point;
import datastructures.D2Tree;

public class Test {
    public static void main(String[] args) {
        D2Tree tree = new D2Tree();
        tree.insert(new Point(1, 1));
        tree.insert(new Point(4,4));
        tree.insert(new Point(0,0));
        System.out.println(tree);
    }
}
