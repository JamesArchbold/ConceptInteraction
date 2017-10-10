package influence.graph.suppliers;

import influence.edges.Edge;
import influence.nodes.ProbabilityNode;

import com.google.common.base.Supplier;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;

public class GraphSupplier implements Supplier<Graph<ProbabilityNode, Edge>> {

	@Override
	public Graph<ProbabilityNode, Edge> get() {
		return new SparseGraph<ProbabilityNode, Edge>();
	}
	
}