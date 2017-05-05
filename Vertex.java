/*
Kyle Murdoch
kwm150130
CS 3345 003
Project 6

Vertex Class
*/

package project6;
import java.util.*;

public class Vertex {
    protected String name;
    protected LinkedList<Node> adjacent;
    protected boolean known;
    protected int dist;
    protected Vertex path;

    // constructors
    public Vertex() {
        known = false;
        path = null;
        dist = 100000000;
        adjacent = new LinkedList();
    }
    public Vertex(String n) {
        name = n;
        known = false;
        path = null;
        dist = 100000000;
        adjacent = new LinkedList();
    }
    
    // copy constructor
    public Vertex(Vertex v) {
        name = v.getName();
        adjacent = new LinkedList(v.getAdj());
        known = v.isKnown();
        dist = v.getDist();
        path = v.getPath();
    }

    // accessors
    public String getName() {
        return name;
    }
    public List getAdj() {
        return adjacent;
    }
    public boolean isKnown() {
        return known;
    }
    public int getDist() {
        return dist;
    }
    public Vertex getPath() {
        return path;
    }

    // mutators
    public void setName(String n) {
        name = n;
    }
    public void addAdj(Vertex v, int d) {
        Node n = new Node(v, d);
        adjacent.add(n);
    }
    public void setKnown(boolean b) {
        known = b;
    }
    public void setDist(int d) {
        dist = d;
    }
    public void setPath(Vertex p) {
        path = p;
    }
    
    // other functions
    public Node getNext() {
        return adjacent.poll();
    }
}