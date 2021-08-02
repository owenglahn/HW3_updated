import java.util.*;

public class BellmanFord{

    private int[] distances = null;
    private int[] predecessors = null;
    private int source;

    class BellmanFordException extends Exception{
        public BellmanFordException(String str){
            super(str);
        }
    }

    class NegativeWeightException extends BellmanFordException{
        public NegativeWeightException(String str){
            super(str);
        }
    }

    class PathDoesNotExistException extends BellmanFordException{
        public PathDoesNotExistException(String str){
            super(str);
        }
    }

    BellmanFord(WGraph g, int source) throws NegativeWeightException{
        /* Constructor, input a graph and a source
         * Computes the Bellman Ford algorithm to populate the
         * attributes 
         *  distances - at position "n" the distance of node "n" to the source is kept
         *  predecessors - at position "n" the predecessor of node "n" on the path
         *                 to the source is kept
         *  source - the source node
         *
         *  If the node is not reachable from the source, the
         *  distance value must be Integer.MAX_VALUE
         */
    	this.source = source;
    	distances = new int[g.getNbNodes()];
    	for ( int i = this.source + 1 ; i < g.getNbNodes() ; i++ ) distances[i] = 10000; // some large int in place of infinity
    	predecessors = new int[g.getNbNodes()];
    	for ( int i = 0 ; i < g.getNbNodes() ; i++ ) predecessors[i] = -1; // stand in for NIL
    	for ( int i = 0 ; i < g.getNbNodes() - 1; i++ )
    	{
    		for ( Edge edge : g.getEdges() ) 
    		{
    			relax(edge.nodes[0], edge.nodes[1], g);
    		}
    	}
    	for ( Edge edge : g.getEdges() )
    	{
    		if ( distances[edge.nodes[1]] > distances[edge.nodes[0]] + edge.weight )
    			throw new NegativeWeightException("There is a negative weight cycle.");
    	}
    }

    /*
     * Helper that relaxes an edge (u, v) in graph
     * 
     * @pre graph has an edge u->v
     */
    private void relax(int u, int v, WGraph graph)
    {
    	assert graph.getEdge(u, v) != null;
    	int edgeWeight = graph.getEdge(u, v).weight;
    	if ( distances[v] > distances[u] + edgeWeight ) 
    	{
    		distances[v] = distances[u] + edgeWeight;
    		predecessors[v] = u;
    	}
    }
    
    public int[] shortestPath(int destination) throws PathDoesNotExistException{
        /*Returns the list of nodes along the shortest path from 
         * the object source to the input destination
         * If not path exists an Error is thrown
         */
    	Stack<Integer> path = new Stack<>();
    	while ( destination != -1) 
    	{
    		path.push(destination);
    		destination = predecessors[destination];
    	}
        int[] toReturn = new int[path.size()];
        for ( int i = 0 ; i < toReturn.length ; i++ )
        {
        	toReturn[i] = path.pop();
        }
        if ( toReturn[0] == this.source ) return toReturn;
        throw new PathDoesNotExistException("There is no shortest path to node " + destination + " from node " + this.source);
    }

    public void printPath(int destination){
        /*Print the path in the format s->n1->n2->destination
         *if the path exists, else catch the Error and 
         *prints it
         */
        try {
            int[] path = this.shortestPath(destination);
            for (int i = 0; i < path.length; i++){
                int next = path[i];
                if (next == destination){
                    System.out.println(destination);
                }
                else {
                    System.out.print(next + "-->");
                }
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    public static void main(String[] args){

        String file = args[0];
        WGraph g = new WGraph(file);
        try{
            BellmanFord bf = new BellmanFord(g, g.getSource());
            bf.printPath(g.getDestination());
        }
        catch (Exception e){
            System.out.println(e);
        }

   } 
}

