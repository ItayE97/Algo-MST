package MSTpack;
import java.util.Vector;

public class Graph //has 2 nested classes within: Vertex and Edge
{
  Vector<Vertex> vertices; //A vector of all the vertices in the graph
  Vector<Edge> edges; //A vector of all the edges in the graph

  public class Edge{ // A class to represent an edge in the graph
      Vertex v1;
      Vertex v2;
      float weight;
  
      public Edge(Vertex v1, Vertex v2, float weight) {
        this.v1 = v1;
        this.v2 = v2;
        this.weight = weight;
      }
      
      @Override
      public boolean equals(Object obj) 
      {
        if (this == obj)
          return true;
        if (obj == null)
          return false;

        if (getClass() != obj.getClass())
          return false;
        Edge e = (Edge) obj;
    
        return (((e.v1.getIndex() == this.v1.getIndex() && e.v2.getIndex() == this.v2.getIndex()) ||
        (e.v1.getIndex() == this.v2.getIndex() && e.v2.getIndex() == this.v1.getIndex()))
        && e.weight == this.weight);
      }
    }

    public class Vertex{ // A class to represent a vertex in the graph
      private int index;
      Vector<Vertex> neighbors; //A vector of the neighboring vertices
      Vector<Edge> edges; //A vector of connected edges
  
      private Vertex(int index) {
        this.index = index;
        this.neighbors = new Vector<Vertex>();
        this.edges = new Vector<Edge>();
      }
      public int getIndex(){return index;}
      
      @Override
      public boolean equals(Object obj) {
        if (this == obj)
          return true;
        if (obj == null)
          return false;
        if (getClass() != obj.getClass())
          return false;
        Vertex other = (Vertex) obj;
        if (!getEnclosingInstance().equals(other.getEnclosingInstance()))
          return false;
        if (index != other.index)
          return false;
        if (neighbors == null) {
          if (other.neighbors != null)
            return false;
        } else if (!neighbors.equals(other.neighbors))
          return false;
        if (edges == null) {
          if (other.edges != null)
            return false;
        } else if (!edges.equals(other.edges))
          return false;
        return true;
      }
      private Graph getEnclosingInstance() {
        return Graph.this;
      }
    }  

  public Graph()
  {
      vertices = new Vector<Vertex>();
      edges = new Vector<Edge>();
  }

  public void addVertex(int index)
  {
    this.vertices.add(new Vertex(index));
  }

  public void addVertex(Vertex v)
  {
    this.vertices.add(v);
  }

  public boolean addEdge(int v1, int v2, float weight) // Adds an edge to the graph
  {
    if(v1 == v2 || v1 < 0 || v2 < 0 || v1 >= vertices.size() || v2 >= vertices.size())
      return false;

    Vertex search1 = null;
    Vertex search2 = null;

    for (Vertex vertex : vertices) 
    { // Finds 'v1' and 'v2' in the vertices vector
      if (vertex.getIndex() == v1)
        search1 = vertex;
      else if (vertex.getIndex() == v2)
        search2 = vertex;
    
      if (search1 != null && search2 != null) // If both vertices have been found, stop searching
        break;
    }

    if(search1 != null && search2 != null)
    {
      Edge eSearch1 = new Edge(search1, search2, weight);
      Edge eSearch2 = new Edge(search2, search1, weight);
      if(!search1.neighbors.contains(search2) && !search2.neighbors.contains(search1)) //to ensure that v1 & v2 are not neighbors
      {
        search1.neighbors.add(search2); 
        search2.neighbors.add(search1);
        search1.edges.add(eSearch1);
        search2.edges.add(eSearch2);  
        edges.add(eSearch1);
        return true;
      } 
    }
    return false;
  }

  public Edge findEdge(Vertex v1, Vertex v2) //finds an existing edge in the graph
  {
    Edge compare = null;
    for (Edge e : v1.edges) //search in v1
      if(e.v2.getIndex() == v2.getIndex())
      {
        compare = e;
        break;
      }
    for (Edge e : v2.edges) //search in v2
      if(e.equals(compare)) //if equal
        return compare;      
    return null;
  }

  public void removeEdge(Edge e) //removes an edge from the graph
  {
    if(e == null)
      return;
    else if(findEdge(e.v1, e.v2) != null)
    {
      e.v1.neighbors.remove(e.v2);
      e.v2.neighbors.remove(e.v1);
      e.v1.edges.remove(e);
      e.v2.edges.remove(e);
      edges.remove(e);
    }  
  }

  public void printGraph()
  {
    System.out.println("Number of vertices: "+this.vertices.size()+" , "+"Number of edges: "+this.edges.size());
    System.out.println("Vertex--Weight--Vertex");
    for (Edge e : edges)
      System.out.println("V"+e.v1.getIndex()+"<------"+(int)e.weight+"------>"+"V"+e.v2.getIndex());   
  }
}