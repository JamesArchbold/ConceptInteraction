package influence.concepts;
//version of SIR that is not in current use
import influence.edges.TypedWeightedEdge;
import influence.nodes.MultiProbabilityNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;

public class BlockingSIR extends Concept{
	private HashSet<MultiProbabilityNode> toActivate;
	private HashSet<MultiProbabilityNode> recovered;
	private double recoverChance;
	
	public BlockingSIR(int id, double recoverChance){
		super(id, "sir");
		toActivate = new HashSet<MultiProbabilityNode>();
		this.recovered = new HashSet<MultiProbabilityNode>();
		this.recoverChance = recoverChance;
	}
	
	public void spread(Graph<MultiProbabilityNode, TypedWeightedEdge> graph, HashMap<Integer, Set<MultiProbabilityNode>> activatedNodes) {
		//int numOfConcepts = activatedNodes.size();
		Random rand = new Random();
		double chance = 0;
		for (MultiProbabilityNode currentNode : graph.getVertices()){
			if (!currentNode.isActivated(this) && !recovered.contains(currentNode) && currentNode.activatedConcepts().size() == 0) {
				chance = 0;
				for(MultiProbabilityNode neighbourNode : graph.getNeighbors(currentNode)){
					if (neighbourNode.isActivated(this)){
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
						if (connectingEdge.getConcept() == getId()) {
							chance =+ connectingEdge.getWeight() * (1 + this.getExternalEnvironment(neighbourNode) + this.getInternalEnvironment(currentNode));
						}
					}
				}
				
				if (chance >= rand.nextDouble()){
					toActivate.add(currentNode);
				}
			}
		}
			
	}
	
	public void activateNodes(HashMap<Integer, Set<MultiProbabilityNode>> activatedNodes) {
		Random rand = new Random();
		HashSet<MultiProbabilityNode> removal = new HashSet<MultiProbabilityNode>();
		for (MultiProbabilityNode m : toActivate) {
			m.activate(this);
		}
		
		for (MultiProbabilityNode m : activatedNodes.get(getId())) {
			if (recoverChance >= rand.nextDouble()) {
				removal.add(m);
			}
		}
		
		for (MultiProbabilityNode m : removal) {
			activatedNodes.get(getId()).remove(m);
			m.deactivate(this);
		//	System.out.println(m.getId());
		//	System.out.println(m.activatedConcepts().toString());
			recovered.add(m);
		}
		
		activatedNodes.get(getId()).addAll(toActivate);
		toActivate.clear();
	}
	
	public HashSet<MultiProbabilityNode> getRecovered() {
		return recovered;
	}
	
	
}
