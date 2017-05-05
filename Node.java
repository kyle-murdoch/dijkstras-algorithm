/*
Kyle Murdoch
kwm150130
CS 3345 003
Project 6
*/
package project6;

public class Node {
        protected int dist;
        protected Vertex v;
        
        public Node(Vertex nv, int d) {
            v = nv;
            dist = d;
        }
        
        // copy constructor
        public Node(Node n) {
            v = n.getVert();
            dist = n.getDist();
        }
        
        public int getDist() {
            return dist;
        }
        public Vertex getVert() {
            return v;
        }
    }
