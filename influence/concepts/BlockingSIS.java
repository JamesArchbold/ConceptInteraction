package influence.concepts;
//version of SIS not in current use
import influence.edges.TypedWeightedEdge;
import influence.nodes.MultiProbabilityNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;

public class BlockingSIS extends Concept{
	private HashSet<MultiProbabilityNode> toActivate;
	//private HashSet<MultiProbabilityNode> recovered;
	private double recoverChance;
	
	public BlockingSIS(int id, double recoverChance){
		super(id, "sis");
		toActivate = new HashSet<MultiProbabilityNode>();
		this.recoverChance = recoverChance;
	}
	
	public void spread(Graph<MultiProbabilityNode, TypedWeightedEdge> graph, HashMap<Integer, 
			Set<MultiProbabilityNode>> activatedNodes, HashSet<MultiProbabilityNode> recovered) {
		//int numOfConcepts = activatedNodes.size();
		Random rand = new Random();
		double chance = 0;
		for (MultiProbabilityNode currentNode : graph.getVertices()){
			if (!currentNode.isActivated(this) && currentNode.activatedConcepts().size() == 0 && !recovered.contains(currentNode)) {
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
				//System.out.println("Removing: " + m.getId());
				removal.add(m);
			}
		}
		
		for (MultiProbabilityNode m : removal) {
			activatedNodes.get(getId()).remove(m);
			m.deactivate(this);
		}
		
		
		activatedNodes.get(getId()).addAll(toActivate);
		toActivate.clear();
	}
	
	
}
