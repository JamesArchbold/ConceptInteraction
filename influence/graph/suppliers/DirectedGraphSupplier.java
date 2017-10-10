package influence.graph.suppliers;

import influence.edges.WeightedEdge;
import influence.nodes.ThresholdNode;

import com.google.common.base.Supplier;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;

public class DirectedGraphSupplier implements Supplier<Graph<ThresholdNode, WeightedEdge>> {

	@Override
	public Graph<ThresholdNode, WeightedEdge> get() {
		return new DirectedSparseGraph<ThresholdNode, WeightedEdge>();
	}

}
