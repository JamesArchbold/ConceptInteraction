package influence.concepts;

import influence.edges.TypedWeightedEdge;
import influence.nodes.MultiProbabilityNode;

import java.util.HashMap;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;

public abstract class ExtendedConcept extends Concept{

	public ExtendedConcept(int id) {
		super(id);
	}
	
	public ExtendedConcept(int id, String type) {
		super(id, type);
	}
	
	public abstract void spread(Graph<MultiProbabilityNode, TypedWeightedEdge> graph, HashMap<Integer, Set<MultiProbabilityNode>> activatedNodes);
	
	public abstract void activateNodes(HashMap<Integer, Set<MultiProbabilityNode>> activatedNodes);
	

}
