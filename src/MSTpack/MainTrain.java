package MSTpack;
import java.util.Random;
import MSTpack.Graph.Edge;

/*
 Given a random graph, this code will find it's MST using Prim's algorithm.  
 Then, it will add a new legal edge to the graph and check if the MST has changed,
 if so it will print the new updated MST.
 */


public class MainTrain 
{
    public static void main(String[] args) 
    {
        //part a
        Graph G = new Graph();
        G = randomGraphGenerator(); 
        MST_Prim prim = new MST_Prim(G,G.vertices.get(0));
        System.out.println("Original Graph:");
        G.printGraph();
        System.out.println();
        System.out.println("Minimal Spanning Tree:");
        prim.MST.printGraph();
        System.out.println();

        //part b1
        Graph tmp = prim.MST;
        Edge newEdge = null;
        do 
        {
            newEdge = addEdgeToRandomGraph(tmp, tmp.vertices.size(), 100); //Adds a new edge with weight = 100 (bigger than all edges in MST)
        } while (newEdge == null);

        if(!prim.MST_EdgeAddition(tmp,newEdge))
        {
            System.out.println("New Edge: V"+newEdge.v1.getIndex()+"<------"+(int)newEdge.weight+"------>"+"V"+newEdge.v2.getIndex());
            System.out.println("MST did not change");  
            tmp.printGraph();
        }
        System.out.println();

        //part b2
        do 
        {   
            newEdge = null;
            newEdge = addEdgeToRandomGraph(tmp, tmp.vertices.size(),0); //Adds a new edge with weight = 0 (smaller than all edges in MST)
        } while (newEdge == null);
        
        if(prim.MST_EdgeAddition(tmp,newEdge))
        {
            System.out.println("New Edge: V"+newEdge.v1.getIndex()+"<------"+(int)newEdge.weight+"------>"+"V"+newEdge.v2.getIndex());
            System.out.println("MST did change!");  
            tmp.printGraph();
            prim.MST = tmp;
        }
        

        System.out.println();
        //runTest1(); //Known graph from class from presentation 4
        System.out.println("Done");
    }

    public static Graph randomGraphGenerator()
    {
        Graph G = new Graph();
        int VERTEXMAXSIZE = 30;
        int EDGESMAXSIZE = 60;
        int randomNum;
        Random rn = new Random();
        randomNum = rn.nextInt(VERTEXMAXSIZE-20)+20;  //Generates a number in the range of 20-30  
        for (int i = 0; i < randomNum; i++)
            G.addVertex(i);
        randomNum = rn.nextInt(EDGESMAXSIZE-50)+50;  //Generates a number in the range of 50-60
        for (int i = 0; i < randomNum; i++)
        {
            float randomWeight = rn.nextInt(50)+1; //weights in the graph are in the range of 1-50
            addEdgeToRandomGraph(G, VERTEXMAXSIZE, randomWeight);
        }
        return G;
    }

    public static Edge addEdgeToRandomGraph(Graph G, int vertexIndexSize, float weight)
    {
        Random rn = new Random();
        int num1 = 0,num2 = 0;
        boolean flag = true;

        //Generates new edge with legal vertices, weight and checks to make sure the 2 vertices are not yet connected else returns null
        while (flag) 
        {
            num1 = rn.nextInt(vertexIndexSize-1);    
            num2 = rn.nextInt(vertexIndexSize-1);       
            if(G.addEdge(num1, num2, weight)) 
                flag = false;
        }
        Edge newEdge = G.findEdge(G.vertices.get(num1), G.vertices.get(num2));
        if(newEdge == null || newEdge.v1 == null || newEdge.v2 == null)
        {
            G.removeEdge(newEdge);
            return null; //returns null if the new edge is illegal
        }
        return newEdge;
    }

    public static MST_Prim runTest1() //Graph from presentation number 4 for easier debugging
    {
        final int N_SIZE = 12;
        Graph G = new Graph();
        for (int i = 0; i < N_SIZE; i++)
            G.addVertex(i);

        G.addEdge(0, 1, 12);
        G.addEdge(0,2,23);
        G.addEdge(0,3,5);
        G.addEdge(1,5,7);
        G.addEdge(2,3,18);
        G.addEdge(2,4,17);
        G.addEdge(3,5,10);
        G.addEdge(3,6,9);
        G.addEdge(4,8,16);
        G.addEdge(4,9,14);
        G.addEdge(5,11,20);
        G.addEdge(6,7,4);
        G.addEdge(6,9,3);
        G.addEdge(7,11,8);
        G.addEdge(8,10,7);
        G.addEdge(10,11,12);

        MST_Prim prim = new MST_Prim(G,G.vertices.get(0));     
        return prim;
    }
}
