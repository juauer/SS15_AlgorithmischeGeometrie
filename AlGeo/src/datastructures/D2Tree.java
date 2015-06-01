package datastructures;

import geometry.Point;

public class D2Tree {
    
    private XVerticalNode root;
    
    public XVerticalNode getRoot() {
        return root;
    }
    
    public void setRoot(XVerticalNode node) {
        this.root = node;
    }
    
    public void insert (Point point) {

        if (point == null) {
            throw new IllegalArgumentException("null point not possible");
        }
 
        if(root == null) {
            root = new XVerticalNode(point);
            return;
        }
        
        // finding the right position for the new node,
        // by walking from root downwards within the tree
        // alternating x- and y-type nodes

        D2Node node = root;
        D2Node next = null;
        
        while (node != null) {
            
            if(node.isSmaller(point) == 1) {
                next = node.getSmaller();
                if (next == null) {
                    // found a leave so insert here
                    node.setSmaller(node.createChild(point));
                    break;
                }
            } else {
                if (node.isSmaller(point) == -1) {
                    next = node.getBigger();
                    if (next == null) {
                        // found a leave so insert here
                        node.setBigger(node.createChild(point));
                        break;
                    }
                } else {
                    // look on equals part
                    next = node.getEqual();
                    if (next == null) {
                        node.setEqual(node.createChild(point));
                        break;
                    }
                }
            }
            node = next;
        }
    }

    
    public static void main(String[] args) {
        
    }

}
