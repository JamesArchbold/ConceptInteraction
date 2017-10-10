package influence.concepts;
//Version of LTM that can interact with any other concept. Call spread and activateNodes methods in that order every time step. 
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import influence.edges.TypedWeightedEdge;
import influence.nodes.MultiProbabilityNode;
import edu.uci.ics.jung.graph.Graph;

public class DiverseLTConcept extends ExtendedConcept{
	private HashSet<MultiProbabilityNode> toActivate;
	
	public DiverseLTConcept(int id){
		super(id, "lt");
		toActivate = new HashSet<MultiProbabilityNode>();
	}
	
	public void spread(Graph<MultiProbabilityNode, TypedWeightedEdge> graph, HashMap<Integer, Set<MultiProbabilityNode>> activatedNodes) {
		
		Collection<MultiProbabilityNode> nodes = graph.getVertices();
		for (MultiProbabilityNode currentNode : nodes){
			
			if (!activatedNodes.get(getId()).contains(currentNode)){
				double exertedInfluence = 0;
				Collection<TypedWeightedEdge> incomingEdges = graph.getInEdges(currentNode);
				
				for (TypedWeightedEdge currentEdge : incomingEdges) {
					
					if (currentEdge.getConcept() == getId()){
						MultiProbabilityNode currentNeighbour = graph.getSource(currentEdge);
						
						
						if (currentNeighbour.isActivated(this)) {
							exertedInfluence =+ currentEdge.getWeight() * (1 + this.getInternalEnvironment(currentNeighbour)) * (1 + this.getExternalEnvironment(currentNode));
						}
					
					}
				
				}
				if (exertedInfluence >= currentNode.getAttribute(this.getId())) {
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
