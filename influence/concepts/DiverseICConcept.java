package influence.concepts;
//Version of ICM that can interact with anyother concept type
//To use - first seed set must be initialised, using seedInitilise method, then can call 'spread' and 'activateNodes' 
//		methods in that order repeatedly until concept stops spreading
import influence.edges.TypedWeightedEdge;
import influence.nodes.MultiProbabilityNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;

public class DiverseICConcept extends ExtendedConcept {
	private HashSet<MultiProbabilityNode> toActivate;
	private HashSet<MultiProbabilityNode> lastActivated;
	private Random rand;
	public DiverseICConcept(int id, int seed){
		super(id, "ic");
		toActivate = new HashSet<MultiProbabilityNode>();
		lastActivated = new HashSet<MultiProbabilityNode>();
        rand = new Random(seed);
      //  System.out.println("added)");
	}
	
	public DiverseICConcept(int id){
		super(id, "ic");
		toActivate = new HashSet<MultiProbabilityNode>();
		lastActivated = new HashSet<MultiProbabilityNode>();
	}
	
	public HashSet<MultiProbabilityNode> getLastActivated(){
		return lastActivated;
	}
	
	public void spread(Graph<MultiProbabilityNode, TypedWeightedEdge> graph, HashMap<Integer, Set<MultiProbabilityNode>> activatedNodes) {

		
		for (MultiProbabilityNode currentNode : lastActivated){
			for(MultiProbabilityNode neighbourNode : graph.getNeighbors(currentNode)){
				if (!neighbourNode.isActivated(this) && !toActivate.contains(neighbourNode)){
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
						double probabilityChance = connectingEdge.getWeight() * (1 + this.getInternalEnvironment(currentNode)) *(1 + this.getExternalEnvironment(neighbourNode));
						if (probabilityChance >= rand.nextDouble()){
							toActivate.add(neighbourNode);
						}
					}
				}
			}
		}

			
	}
	
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
		lastActivated.clear();
		lastActivated.addAll(seeds);
	}
	
	
}
