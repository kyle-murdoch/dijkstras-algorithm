/*
Kyle Murdoch
kwm150130
CS 3345 003
Project 6

Goal: Find shortest flight path using Dijktra's Algorithm with priority queues.
*/

package project6;
import java.util.*;
import java.io.*;

public class Project6 {
    public static void main(String[] args) {
        // parse data from the flight data file
        try {
            BufferedReader flightData = new BufferedReader(new FileReader("./src/project6/flight_data.dat"));
            int numData = Integer.parseInt(flightData.readLine()); // get number of data points
            
            String[] data = new String[numData * 4];
            for (int i = 0; i < numData; i++) {
                String rawData = flightData.readLine();

                /*  data[0] = departure
                    data[1] = destination
                    data[2] = cost
                    data[3] = time 
                */
                String[] temp = rawData.split("\\|");
                for (int j = 0; j < temp.length; j++) {
                    data[i * 4 + j] = temp[j];
                } 
            }
            flightData.close();
            
            // store data into separate arrays
            String[] depart = new String[data.length / 4];
            for (int i = 0; i < data.length / 4; i++) {
                depart[i] = data[i * 4];
            }
            
            String[] arrive = new String[data.length / 4];
            for (int i = 0; i < data.length / 4; i++) {
                arrive[i] = data[i * 4 + 1];
            }
            
            int[] cost = new int[data.length / 4];
            for (int i = 0; i < data.length / 4; i++) {
                cost[i] = Integer.parseInt(data[i * 4 + 2]);
            }
            
            int[] dist = new int[data.length / 4];
            for (int i = 0; i < data.length / 4; i++) {
                dist[i] = Integer.parseInt(data[i * 4 + 3]);
            }
            
            // create list of possible vertices
            String[] verts = new String[data.length / 2];
            for (int i = 0; i < data.length / 4; i++) {
                verts[i] = depart[i];
            }
            for (int i = data.length / 4; i < data.length / 2; i++) {
                verts[i] = arrive[i - data.length / 4];
            }
            
            // sort possible vertices to find repeats (simple bubble sort)
            boolean sorted = false;
            while (!sorted) {
                sorted = true;
                for (int i = 0; i < verts.length - 1; i++) {
                    if (verts[i].compareTo(verts[i + 1]) > 0) {
                        String temp = verts[i];
                        verts[i] = verts[i + 1];
                        verts[i + 1] = temp;
                        
                        sorted = false;
                    }
                }
            }
            
            // remove duplicate coppies
            int numVertices = verts.length;
            for (int i = 0; i < verts.length - 1; i++) {
                if (verts[i].compareTo(verts[i + 1]) == 0) {
                    verts[i] = null;
                    numVertices--;
                }
            }
            
            // initialize vertices
            Vertex[] vertices = new Vertex[numVertices];
            
            // get the requested departure and desination and
            // determine if we are sorting via cost or distance
            BufferedReader reportData = new BufferedReader(new FileReader("./src/project6/request.dat"));
            int numReq = Integer.parseInt(reportData.readLine()); // get number of data points

            String[] requests = new String[numReq * 4];
            for (int i = 0; i < numReq; i++) {
                String rawData = reportData.readLine();

                /*  requests[0] = departure
                    requests[1] = destination
                    requests[2] = cost or time
                */
                String[] temp = rawData.split("\\|");
                for (int j = 0; j < temp.length; j++) {
                    requests[i * 3 + j] = temp[j];
                } 
            }
            reportData.close();
            
            // create vertices
                int count = 0;
                for (int i = 0; i < verts.length; i++) {
                    if (verts[i] != null) {
                        vertices[count] = new Vertex(verts[i]);
                        count++;
                    }
                }
            
            // begin dijkstra's algorithm
            for (int k = 0; k < numReq; k++) {
                if (requests[k * 3 + 2].compareTo("T") == 0) {
                    dijkstra(vertices, depart, arrive, dist, matchVert(vertices, requests[k * 3]), matchVert(vertices, requests[k * 3 + 1]), requests[k * 3 + 2]);
                }
                else if (requests[k * 3 + 2].compareTo("C") == 0) {
                    dijkstra(vertices, depart, arrive, cost, matchVert(vertices, requests[k * 3]), matchVert(vertices, requests[k * 3 + 1]), requests[k * 3 + 2]);
                }
            }
            
            /*
            for testing the adjacency list (checked)
            
            System.out.println("Vertices:");
            for (int i = 0; i < vertices.length; i++) {
                vertices[i].setDist(i);
                System.out.println(vertices[i].getName() + ": " + vertices[i].getDist());
            }
            System.out.println();
            Vertex v;
            for (int i = 0; i < vertices.length; i++) {
                System.out.println("**" + vertices[i].getName() + "**");
                while ((v = vertices[i].getNext()) != null) {
                    System.out.println(v.getName() + " " + v.getDist());
                }
                System.out.println();
            }
            */
        }
        catch (IOException e) {
            System.out.println("Error: File not found");
            System.exit(1);
        }
        catch (NullPointerException e) {
            System.out.println("Error: Not enough data");
            System.exit(1);
        }
    }
    
    public static void dijkstra(Vertex[] vertices, String[] depart, String[] arrive,
            int[] travel, Vertex departure, Vertex arrival, String type) {
        
        // set departure vertex to a distance of 0
        // and reset paths and distances
        for(Vertex v:vertices) {
            v.setPath(null);
            v.setDist(100000000);
        }
        departure.setDist(0);
        
        // build graph using adjacency list
        for (int i = 0; i < vertices.length; i++) {
            for (int j = 0; j < depart.length; j++) {
                if (vertices[i].getName().compareTo(depart[j]) == 0) {
                    Vertex match = matchVert(vertices, arrive[j]);
                    if (match != null) {
                        vertices[i].addAdj(match, travel[j]);
                    }
                }
            }
        }
        
        // create priority queue and initiallize comparator
        PriorityQueue<Vertex> queue = new PriorityQueue(vertices.length, new Comparator<Vertex>() {
            @Override
            public int compare(Vertex v1, Vertex v2) {
                return (v1.getDist() > v2.getDist()) ? 1 : (v1.getDist() < v2.getDist()) ? -1 : 0;
            }
        });
        
        // build priority queue
        for (Vertex v:vertices) {
            queue.add(v);
        }
        
        // begin dijkstra's algorithm
        Vertex u;
        Node v;
        while (!queue.isEmpty()) {
            u = queue.poll();
            while ((v = u.getNext()) != null) {
                int distance = v.getDist();
                //System.out.println("u: " + u.getName() + "    v: " + v.getVert().getName());
                //System.out.print(u.getDist() + " + " + distance + " < " + v.getVert().getDist() + " ==> ");
                if (u.getDist() + distance < v.getVert().getDist()) {
                    //System.out.println("Success");
                    v.getVert().setDist(u.getDist() + distance);
                    v.getVert().setPath(u);
                    updateQueue(queue);
                }
                else {
                    //System.out.println("Fail");
                }
                //System.out.println();
                
            }
        }
        
        LinkedList<String> names = new LinkedList();
        
        Vertex x = arrival;
        int count = 0;
        boolean found = false;
        while (x != null) {
            if (x.getName().compareTo(departure.getName()) == 0) {
                found = true;
            }
            names.addFirst(x.getName());
            x = x.getPath();
        }
        
        // relay message if not found
        if (!found) {
            System.out.println("No path from " + departure.getName() + " to " + arrival.getName() + ".\n");
        }
        else {
            if (type.compareTo("T") == 0) {
                System.out.println(departure.getName() + ", " + arrival.getName() + " (Time)");
                while (names.size() > 0) {
                    System.out.print(names.poll());
                    if (names.isEmpty()) {
                        System.out.print(". ");
                    }
                    else {
                        System.out.print(" -> ");
                    }
                }
                System.out.println("Time: " + arrival.getDist());
            }
            else if (type.compareTo("C") == 0) {
                System.out.println(departure.getName() + ", " + arrival.getName() + " (Cost)");
                while (names.size() > 0) {
                    System.out.print(names.poll());
                    if (names.isEmpty()) {
                        System.out.print(". ");
                    }
                    else {
                        System.out.print(" -> ");
                    }
                }
                System.out.println("Cost: " + arrival.getDist());
            }
            
            System.out.println();
        }
    }
    
    public static void updateQueue(PriorityQueue<Vertex> q) {
        LinkedList<Vertex> qData = new LinkedList();
        while (!q.isEmpty()) {
            qData.add(q.poll());
        }
        
        while(!qData.isEmpty()) {
            q.add(qData.poll());
        }
    }
    
    public static Vertex matchVert(Vertex[] vertices, String name) {
        for (Vertex v:vertices) {
            if (v.getName().compareTo(name) == 0) {
                return v;
            }
        }
        return null;
    }
}

