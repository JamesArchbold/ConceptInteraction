package influence.graph.makers;

import influence.edges.Edge;
import influence.nodes.ProbabilityNode;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

public class UndirectedGraphFactory implements Factory<UndirectedGraph<ProbabilityNode, Edge>> {

	@Override
	public UndirectedGraph<ProbabilityNode, Edge> create() {
		return new UndirectedSparseGraph<ProbabilityNode, Edge>();
	}
	
}