import java.util.*;
import java.io.File;

public class FordFulkerson {

	public static ArrayList<Integer> pathDFS(Integer source, Integer destination, WGraph graph) {
		boolean[] isVisited = new boolean[graph.getNbNodes()];
		return dfsRecursive(source, destination, isVisited, graph);
	}
	
	private static ArrayList<Integer> dfsRecursive(Integer start, Integer destination, boolean[] isVisited, WGraph graph)
	{
		List<Integer>[] adjVertices = adjacentVertices(graph);
		ArrayList<Integer> toReturn = new ArrayList<>();
		toReturn.add(start);
		if (start == destination) return toReturn; // have reached destination
		for ( Integer adj : adjVertices[start] ) // scan adjacent vertices
		{
			if ( ! isVisited[adj] && graph.getEdge(start, adj).weight > 0 ) // do not search with zero weight edges
			{
				isVisited[adj] = true;
				List<Integer> subPath = dfsRecursive(adj, destination, isVisited, graph); // recursive step
				if ( subPath.contains(destination) )
				{
					toReturn.addAll(subPath);
					return toReturn;
				}
			}
		}
		// could not reach destination
		toReturn.clear();
		return toReturn;
	}

	/*
	 * Helper method to get the nodes adjacent to each node
	 */
	@SuppressWarnings ("unchecked")
	private static List<Integer>[] adjacentVertices(WGraph graph)
	{
		List<Integer>[] toReturn = new LinkedList[graph.getNbNodes()];
		for ( int i = 0 ; i < graph.getNbNodes(); i++ )
		{
			toReturn[i] = new LinkedList<Integer>();
		}
		for ( Edge edge : graph.getEdges() )
		{
			toReturn[edge.nodes[0]].add(edge.nodes[1]); 
		}
		return toReturn;
	}
	
	public static String fordfulkerson(WGraph graph) {
		String answer = "";
		int maxFlow = 0;
		WGraph resGraph = makeResidual(graph);
		
		while (! pathDFS(resGraph.getSource(), resGraph.getDestination(), resGraph).isEmpty()) // while there is a path in the residual graph
		{
			int margFlow = augment(pathDFS(resGraph.getSource(), resGraph.getDestination(), resGraph), resGraph); // bottleneck
			maxFlow += margFlow;
		}
		// update the original graph based on the residual graph
		for ( Edge edge : graph.getEdges() )
		{
			edge.weight = resGraph.getEdge(edge.nodes[1], edge.nodes[0]).weight;
		}
		answer += maxFlow + "\n" + graph.toString();
		return answer;
	}
	
	/*
	 * Helper to get residual graph.
	 */
	public static WGraph makeResidual(WGraph graph)
    {
    	WGraph toReturn = new WGraph(graph);
    	for ( Edge edge : graph.getEdges() )
    	{
    		toReturn.addEdge(new Edge(edge.nodes[1], edge.nodes[0], 0));
    	}
    	return toReturn;
    }
	
	/*
	 * Helper method to augment a path in a residual graph.
	 */
	private static Integer augment(List<Integer> path, WGraph graph)
	{
		Integer min = Collections.min(pathEdgeWeights(pathEdges(path, graph)));
		for ( Edge edge : pathEdges(path, graph) )
		{
			edge.weight -= min;
			Edge backEdge = graph.getEdge(edge.nodes[1], edge.nodes[0]);
			if (backEdge != null) backEdge.weight += min; // condition for safety
		}
		return min;
	}
	
	/*
	 * Helper to get the weights of the edges in the path.
	 */
	public static List<Integer> pathEdgeWeights(List<Edge> pEdges)
	{
		List<Integer> toReturn = new ArrayList<>();
		for ( Edge edge : pEdges )
		{
			toReturn.add(edge.weight);
		}
		return toReturn;
	}
	
	/*
	 * Helper to get the edges in the path.
	 */
	public static List<Edge> pathEdges(List<Integer> path, WGraph graph)
	{
		List<Edge> toReturn = new ArrayList<>();
		for ( int i = 0 ; i < path.size() - 1; i++ )
		{
			toReturn.add(graph.getEdge(path.get(i), path.get(i+1)));
		}
		return toReturn;
	}
	
	public static void main(String[] args) {
		String file = args[0];
		File f = new File(file);
		WGraph g = new WGraph(file);
		System.out.println(fordfulkerson(g).toString());
	}
}
