package influence.concepts;
//Version of ICM spreading that blocks all other concepts
import influence.edges.TypedWeightedEdge;
import influence.nodes.MultiProbabilityNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;

public class BlockingICConcept extends Concept{
	private HashSet<MultiProbabilityNode> toActivate;
	private HashSet<MultiProbabilityNode> lastActivated;
	
	public BlockingICConcept(int id){
		super(id, "ic");
		toActivate = new HashSet<MultiProbabilityNode>();
		lastActivated = new HashSet<MultiProbabilityNode>();
	}
	
	//concepts spreads from current active set of nodes
	public void spread(Graph<MultiProbabilityNode, TypedWeightedEdge> graph) {
		Random rand = new Random();
		for (MultiProbabilityNode currentNode : lastActivated){
			for(MultiProbabilityNode neighbourNode : graph.getNeighbors(currentNode)){
				
				if (neighbourNode.activatedConcepts().size() == 0 && !toActivate.contains(neighbourNode)){
					Collection<TypedWeightedEdge> connectingEdgeSet = graph.findEdgeSet(neighbourNode, currentNode);
					if (connectingEdgeSet == null) {
						connectingEdgeSet = graph.findEdgeSet(currentNode,neighbourNode);
					}
					
					TypedWeightedEdge connectingEdge = null;
					for (TypedWeightedEdge e : connectingEdgeSet) {
						if (e.getConcept() == getId()) {
							connectingEdge = e;
						}
					}
					if (connectingEdge.getConcept() == getId()){
						double probabilityChance = connectingEdge.getWeight() * (1+this.getInternalEnvironment(currentNode)+this.getExternalEnvironment(neighbourNode));
						double check = rand.nextDouble();
						if (probabilityChance >= check){
							toActivate.add(neighbourNode);
						}
					}
				}
			}
		}
			
	}
	//nodes that have been successfully infected are activated for next time step
	public void activateNodes(HashMap<Integer, Set<MultiProbabilityNode>> activatedNodes) {
		for (MultiProbabilityNode m : toActivate) {
			m.activate(this);
		}
		
		lastActivated.clear();
		lastActivated.addAll(toActivate);
		activatedNodes.get(getId()).addAll(toActivate);
		toActivate.clear();
	}
	
	public void seedInitialise(Set<MultiProbabilityNode> seeds) {
		lastActivated.addAll(seeds);
	}
	
	
}
