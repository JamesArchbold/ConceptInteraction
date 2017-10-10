package influence.graph.makers;

import influence.edges.WeightedEdge;
import influence.nodes.ThresholdNode;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;

public class DirectedGraphFactory implements Factory<Graph<ThresholdNode, WeightedEdge>> {

	@Override
	public Graph<ThresholdNode, WeightedEdge> create() {
		return new DirectedSparseGraph<ThresholdNode, WeightedEdge>();
	}

}
