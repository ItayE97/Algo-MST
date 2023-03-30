package MSTpack;

import java.util.ArrayList;
import java.util.PriorityQueue;
import MSTpack.Graph.Edge;
import MSTpack.Graph.Vertex;

public class MST_Prim { //has 1 nested class within: Data
  Graph originalGraph; 
  Graph MST; //The MST of 'originalGraph'
  Data[] dataArray; //holds all the info needed to find MST

  public class Data implements Comparable<Data>{
    Vertex vertex; // The original vertex
    Vertex parent; // The parent of a vertex in the minimum spanning tree
    float d; // The distance of the shortest path to a vertex
    boolean isUsed; // The set of vertices that are part of the minimum spanning tree
  
    public Data(Vertex v) //each vertex starts with d = infinity & parent = null
    {
      this.vertex = v;
      parent = null;
      d = (float)Double.POSITIVE_INFINITY;
      isUsed = false;
    }

    @Override
    public int compareTo(Data other) {
      return (int)(this.d - other.d);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Data other = (Data) obj;
      if (!getEnclosingInstance().equals(other.getEnclosingInstance()))
        return false;
      if (vertex == null) {
        if (other.vertex != null)
          return false;
      } else if (!vertex.equals(other.vertex))
        return false;
      if (parent == null) {
        if (other.parent != null)
          return false;
      } else if (!parent.equals(other.parent))
        return false;
      if (Float.floatToIntBits(d) != Float.floatToIntBits(other.d))
        return false;
      if (isUsed != other.isUsed)
        return false;
      return true;
    }

    private MST_Prim getEnclosingInstance() {
      return MST_Prim.this;
    }  
  }//end of class

  public MST_Prim(Graph G, Vertex root) //Ctor to find and create the MST
  { 
    this.originalGraph = G;
    MST = new Graph();
    //creates the Data array and puts all the vertices inside
    dataArray = new Data[G.vertices.size()];
    for (int i = 0; i < dataArray.length; i++)
      dataArray[i] = new Data(G.vertices.get(i));
           
    //MST starts with no vertices and no edges
    setRoot(root);    
    FindMST(root);
    updateVertices(); //Updates the info of each vertex after building the MST
  }

  private void setRoot(Vertex root) 
  {
    MST.addVertex(root);
    dataArray[root.getIndex()].d = 0;
    dataArray[root.getIndex()].isUsed = true;
  }

  private void FindMST(Vertex r) 
  { 
    //This algorithm first updates the distances and builds a new edge from a vertex outside the MST to one inside it
    PriorityQueue<Data> PQ = new PriorityQueue<>();
    for (Data data : dataArray) //Adds all the vertices to the 'heap'(PQ)
      PQ.add(data);

    while (!PQ.isEmpty()) //runs until PQ is empty
    {
      Data vData = PQ.poll();
      Vertex v = vData.vertex; //the current vertex out of the 'heap'
      Edge closestEdge = null;
      Vertex closestNeighbor = null;
      Edge e = null;
      float shortestDistance = (float)Double.POSITIVE_INFINITY;

      //This loop updates the distances and 'heap' according the v's neighbors
      for (Vertex neighbor : v.neighbors) //for all neighbors of v
      {
        e = calculateWeight(v, neighbor); //finds the edge between the 2
        float weight = e.weight;
        if(dataArray[neighbor.getIndex()].d > weight && 
        !dataArray[neighbor.getIndex()].isUsed) 
        { //if the distance saved in dataArray is bigger than the weight found and the neighor is not yet in the MST update distance and 'heap'
          dataArray[neighbor.getIndex()].d = weight; 
          PQ.remove(dataArray[neighbor.getIndex()]);
          PQ.add(dataArray[neighbor.getIndex()]);
        }  
      }
      //This loop finds the correct edge to be later built
      for (Vertex neighbor : v.neighbors) //find the closest neighbor of v that is inside the MST
      { 
        if(dataArray[neighbor.getIndex()].isUsed)
        {
          e = calculateWeight(v, neighbor);
          float weight = e.weight;
          if(shortestDistance > weight)
          {
            shortestDistance = weight;
            closestNeighbor = neighbor;
            closestEdge = e;
          }
        }
      }
      //If the closest neighbor is in the MST and legal
      if (closestNeighbor != null && dataArray[closestNeighbor.getIndex()].isUsed)
      {
        MST.addVertex(v); //Adds v to the MST
        vData.isUsed = true;
        dataArray[v.getIndex()].parent = closestNeighbor; //update parent
        MST.edges.add(closestEdge); //Adds the edge to the MST    
      }
    }
  }

  private Edge calculateWeight(Vertex v, Vertex neighbor)
  {
    Edge e = originalGraph.findEdge(v, neighbor);
    if(e == null)
     e = originalGraph.findEdge(neighbor,v);
    if(e == null)
      return null;
    return e;  
  }

  private void updateVertices()
  {
    for (Vertex v : MST.vertices) //clears all info from all the vertices
    {
      v.neighbors.clear();
      v.edges.clear();
    } 

    for (Edge e : MST.edges) //according to the MST, adds the correct data
    {
      e.v1.neighbors.add(e.v2);
      e.v2.neighbors.add(e.v1);
      e.v1.edges.add(e); 
      e.v2.edges.add(e); 
    }
    //This part sorts the vertices according to their indexes
    Vertex[] newOrder = new Vertex[originalGraph.vertices.size()];
    for (int i = 0; i < newOrder.length; i++) 
      newOrder[i] = null;
    
    for (Vertex v : MST.vertices)
      newOrder[v.getIndex()] = v;

    for (int i = 0; i < newOrder.length; i++) 
    {
      if(newOrder[i] == null)
        newOrder[i] = originalGraph.vertices.get(i);  
    }
    
    MST.vertices.clear();
    for (Vertex v : newOrder)
      MST.addVertex(v);
  }

  public boolean MST_EdgeAddition(Graph MST, Edge newEdge) //This method is related to part b
  { //This algorithm finds the circle in the MST with the use of the parents to maximize preformance
    ArrayList<Edge> edgesInCircle = new ArrayList<>();
    int caseCheck = extremeCasesInMST_EdgeAddition(newEdge.v1, newEdge.v2); 
    Edge maxEdge = null; 
    switch (caseCheck) { //based on extremeCases return value
      case 0: //both vertices in MST
        edgesInCircle = findCircleInMST(newEdge.v1, newEdge.v2); //finds all the edges in the circle
        maxEdge = newEdge;
        for (Edge e : edgesInCircle) //find the heaviest in the circle
        {
          if(e.weight > maxEdge.weight)
            maxEdge = e;
        }
        break;
      case 1: //both vertices are outside the MST (were isolated in the original graph)
        maxEdge = newEdge;
        break;
      case 2: //v1 is not inside the MST && v2 is in the MST
        dataArray[newEdge.v1.getIndex()].parent = newEdge.v2;
        return true;
      case 3: //v1 is inside the MST && v2 is not in the MST
        dataArray[newEdge.v2.getIndex()].parent = newEdge.v1;
        return true;
    }   

    MST.removeEdge(maxEdge);
    if(maxEdge.equals(newEdge) || maxEdge.weight == newEdge.weight) //if the edge added did not change the MST
      return false;
    else //the edge added did change the MST
    {
      updateParents(maxEdge,newEdge);
      return true;
    }
  }

  private ArrayList<Edge> findCircleInMST(Vertex v1, Vertex v2) //This method finds all the edges in the circle
  {
    ArrayList<Edge> edgesInCircle =  new ArrayList<>();
    boolean[] visitArray = new boolean[MST.vertices.size()]; //This array helps us know which vertex has been visited
    for (boolean b : visitArray) 
      b = false;  

    visitArray[v1.getIndex()] = true;
    visitArray[v2.getIndex()] = true;

    if(v1.equals(dataArray[0].vertex)) //v1 == root
    { //Update all the parents from v2 ---> v1 = root
      Vertex start = v2;
      while (dataArray[start.getIndex()].parent != null) 
      {
        edgesInCircle.add(MST.findEdge(start, dataArray[start.getIndex()].parent));
        start = dataArray[start.getIndex()].parent;
      }
    }
    else if(v2.equals(dataArray[0].vertex)) //v2 == root
    { //Update all the parents from v1 ---> v2 = root
      Vertex start = v1;
      while (dataArray[start.getIndex()].parent != null) 
      {
        edgesInCircle.add(MST.findEdge(start, dataArray[start.getIndex()].parent));
        start = dataArray[start.getIndex()].parent;
      }
    }
    else //v1 != root && v2 != root
    {
      Vertex son = v1;
      Vertex parent = dataArray[v1.getIndex()].parent;
      //This loop goes from v1---> root and marks them as visited
      while(parent != null)
      {
        visitArray[parent.getIndex()] = true;
        son = parent;
        parent = dataArray[parent.getIndex()].parent;
      }
      
      son = v2;
      parent = dataArray[v2.getIndex()].parent;
      //This loop goes from v2---> the first visited vertex or the root
      while(parent != null && visitArray[parent.getIndex()] == false)
      {
        visitArray[parent.getIndex()] = true;
        edgesInCircle.add(MST.findEdge(son, parent)); //each step of the loop here is considered part of the circle and we can add the edge to it
        son = parent;
        parent = dataArray[parent.getIndex()].parent;
      }
  
      //This part helps us find the first common parent of v1 and v2 as they are a part of a tree
      Vertex commonParent = null;  
      if(parent != null) //common parent != root
      {
        edgesInCircle.add(MST.findEdge(son, parent));
        commonParent = parent;
      }
      else //common parent == null
        commonParent = son;
  
      if(!commonParent.equals(v1) && !commonParent.equals(v2))  
      {
        son = v1;
        parent = dataArray[v1.getIndex()].parent;
        //Since we added all the edges from v2 ---> common parent we need to add the rest from v1 ---> common parent
        if(parent != null)
        {
          while(!parent.equals(commonParent))
          {
            edgesInCircle.add(MST.findEdge(son, parent)); //edges added here
            son = parent;
            parent = dataArray[parent.getIndex()].parent;
          }
          edgesInCircle.add(MST.findEdge(son, parent)); //last step added here
        }
      }
    }

  if(!edgesInCircle.contains(MST.findEdge(v1, v2))) //the new edge that created the circle is added here
    edgesInCircle.add(MST.findEdge(v1, v2));

  return edgesInCircle;
  }

  private int extremeCasesInMST_EdgeAddition(Vertex v1, Vertex v2) //Extreme cases when the original graph had isolated vertices
  {
    if(!MST.vertices.contains(v1))
    {
      if(!MST.vertices.contains(v2))
      {
        return 1; //both not in MST
      }
      return 2;  //v1 out v2 in
    }
    else
    {
      if(!MST.vertices.contains(v2))
        return 3; //v1 in v2 out
      return 0;  //both in MST
    }
  }

  private void updateParents(Edge maxEdge ,Edge newEdge) //This mathod updates the parents in the circle after removing an 'old' edge (part b2)
  { //The vertices needed to be updated are from the max edge ---> new edge (from the son of maxEdge to the parent of newEdge to be exact)
    Vertex sonOfMax = null, parentOfNew = null;
    //This condition finds the son of the max edge
    if(dataArray[maxEdge.v1.getIndex()].parent.equals(maxEdge.v2)) //the parent of v1 is v2
      sonOfMax = maxEdge.v1;
    else //v1 is the parent of v2
      sonOfMax = maxEdge.v2;

    //Simple fixes  
    if(sonOfMax.equals(newEdge.v1)) //son of max == v1
    {
      dataArray[sonOfMax.getIndex()].parent = newEdge.v2; //parent of son of max == v2
      return;
    }
    else if(sonOfMax.equals(newEdge.v2)) //son of max == v2
    {
      dataArray[sonOfMax.getIndex()].parent = newEdge.v1; //parent of son of max == v1
      return;
    }

    //Not so simple fixes
    Vertex son = newEdge.v1;
    Vertex parent = dataArray[newEdge.v1.getIndex()].parent;
    //This part helps us find the parent of the new edge
    while(parent != sonOfMax && parent != null) //runs from v1 to son of max or root
    {
      son = parent;
      parent = dataArray[parent.getIndex()].parent;
    }  
    
    if(parent == sonOfMax) //if the loop found son of max we can confirm the parent of the new edge is v2 and v1 is the son
    {
      parentOfNew = newEdge.v2;
      son = newEdge.v1;
    }
    else //parent == null means the root was found and so we can confirm the parent of the new edge is v1 and v2 is the son
    {
      parentOfNew = newEdge.v1;
      son = newEdge.v2;
    }

    parent = dataArray[son.getIndex()].parent;
    Vertex grandpa = null;
    do 
    {//This confusing bit reveses the roles of parenthood (illegal in most countries)[הבן הופך להיות האבא של האבא שלו]
      grandpa = dataArray[parent.getIndex()].parent; //the grandpa is the parent of the parent
      dataArray[parent.getIndex()].parent = son; //the new grandpa is now set to be the son
      son = parent; //step for next loop
      parent = grandpa; //step for next loop
    } while (grandpa != null && parent != dataArray[sonOfMax.getIndex()].parent); //runs until the root is reached or the end of the max edge

    if(parentOfNew.equals(newEdge.v1)) //sets the parents according to the new edge
      dataArray[newEdge.v2.getIndex()].parent = newEdge.v1;
    else
      dataArray[newEdge.v1.getIndex()].parent = newEdge.v2;

    dataArray[0].parent = null; //to prevent overwriting the parent of the root
  }   
}