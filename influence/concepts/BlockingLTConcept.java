package influence.concepts;
//Version of LYM that blocks concepts - not in current use
import influence.edges.TypedWeightedEdge;
import influence.nodes.MultiProbabilityNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;

public class BlockingLTConcept extends Concept{
	private HashSet<MultiProbabilityNode> toActivate;
	
	public BlockingLTConcept(int id){
		super(id, "lt");
		toActivate = new HashSet<MultiProbabilityNode>();
	}
	
	public void spread(Graph<MultiProbabilityNode, TypedWeightedEdge> graph, HashMap<Integer, Set<MultiProbabilityNode>> activatedNodes, int threshold) {
		//int numOfConcepts = activatedNodes.size();
		
		Collection<MultiProbabilityNode> nodes = graph.getVertices();
		for (MultiProbabilityNode currentNode : nodes){
			
			if (currentNode.activatedConcepts().size() == 0){
				double exertedInfluence = 0;
				Collection<TypedWeightedEdge> incomingEdges = graph.getInEdges(currentNode);
				
				for (TypedWeightedEdge currentEdge : incomingEdges) {
					
					if (currentEdge.getConcept() == getId()){
						MultiProbabilityNode currentNeighbour = graph.getSource(currentEdge);
						
						
						if (currentNeighbour.isActivated(this)) {
							exertedInfluence =+ currentEdge.getWeight() * (1 + this.getInternalEnvironment(currentNeighbour) + this.getExternalEnvironment(currentNode));
						}
					
					}
				
				}
				
				if (exertedInfluence >= currentNode.getAttribute(threshold)) {
				//	System.out.println("Passed:" + currentNode.getId());
					toActivate.add(currentNode);
				}
			
			}
		}
		

	}
	
	public void activateNodes(HashMap<Integer, Set<MultiProbabilityNode>> activatedNodes) {
		for (MultiProbabilityNode m : toActivate) {
			m.activate(this);
		}
		
		activatedNodes.get(getId()).addAll(toActivate);
		toActivate.clear();
	}
}
