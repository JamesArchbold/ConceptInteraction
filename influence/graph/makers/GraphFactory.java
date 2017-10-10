package influence.graph.makers;

import influence.edges.Edge;
import influence.nodes.ProbabilityNode;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.UndirectedGraph;

public class GraphFactory implements Factory<Graph<ProbabilityNode, Edge>> {

	@Override
	public Graph<ProbabilityNode, Edge> create() {
		return new SparseGraph<ProbabilityNode, Edge>();
	}
	
}
