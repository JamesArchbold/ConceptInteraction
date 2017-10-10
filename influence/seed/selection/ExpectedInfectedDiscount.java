package influence.seed.selection;
//the expected infected heuristic for maximising the spread of a target concept by utilising benifical concept interaction and avoiding undesirable ones
import influence.concepts.Concept;
import influence.edges.Edge;
import influence.edges.TypedWeightedEdge;
import influence.nodes.MultiProbabilityNode;
import influence.nodes.Node;
import influence.nodes.comparators.MultiProbabilityComparator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.uci.ics.jung.graph.Graph;

public class ExpectedInfectedDiscount {
	
	public <E extends Node, T extends Edge> HashSet<E> getSeeds
	(Graph<E, T> g, int seedCount, double prob, Concept c) {
		
		HashSet<E> seeds = new HashSet<E>();
		HashSet<E> nodesToSort = new HashSet<E>();
		HashMap<Integer, Set<E>> neighbours = new HashMap<Integer, Set<E>>();
		HashMap<Integer, Set<E>> influencedNeighbours = new HashMap<Integer, Set<E>>();
		
		//TIntDoubleHashMap tVal = new TIntDoubleHashMap();
		
		for (E m : g.getVertices()) {
			double total = 1;
			double internal = c.getInternalEnvironment((MultiProbabilityNode) m);
			Set<E> neighs = new HashSet<E>();
			neighbours.put(m.getId(), neighs);
			influencedNeighbours.put(m.getId(), new HashSet<E>());
		//	System.out.println("external " + external);
			for (E n : g.getNeighbors(m)){
				neighs.add(n);
				total += prob * (1 + internal + c.getExternalEnvironment((MultiProbabilityNode) n));
			//	System.out.println("internal for " + n.getId() + " is " + external);
			}
			
			m.setValue(total);
			nodesToSort.add(m);
		}
		
		Comparator<E> comp = new MultiProbabilityComparator<E>();
		SortedSet<E> sortedN = new TreeSet<E>(comp);
		
		for (E m : nodesToSort) {
			sortedN.add(m);
		//	System.out.println("Adding " + m.getId());
		}
		
		while (seeds.size() < seedCount) {
			E choice = sortedN.last();
			seeds.add(choice);

			nodesToSort.remove(choice);

			for (E n : g.getNeighbors(choice)){
				Set<E> neighs = neighbours.get(n.getId());
				Set<E> infNeighs = influencedNeighbours.get(n.getId());

				infNeighs.add(choice);
				neighs.remove(choice);
				
				double probTotal = 0;
				double expectedTotal = 1;
				double internal = c.getInternalEnvironment((MultiProbabilityNode) n);
				double external = c.getExternalEnvironment((MultiProbabilityNode) n);

				for (E nn : infNeighs) {
					probTotal += prob * (1 + external + c.getInternalEnvironment((MultiProbabilityNode) nn)); 

				}

				for (E nn : neighs) {
					expectedTotal += prob * (1 + internal + c.getExternalEnvironment((MultiProbabilityNode) nn));

				}
				n.setValue((1 - probTotal) * expectedTotal);

			}
			
			sortedN.clear();
			
			for (E m : nodesToSort) {
				sortedN.add(m);
			}
		}
		
		return seeds;
	}

public static HashSet<MultiProbabilityNode> getSeedsBurnIn
(Graph<MultiProbabilityNode, TypedWeightedEdge> g, int seedCount, Concept c) {
	
	HashSet<MultiProbabilityNode> seeds = new HashSet<MultiProbabilityNode>();
	HashSet<MultiProbabilityNode> nodesToSort = new HashSet<MultiProbabilityNode>();
	HashMap<Integer, Set<MultiProbabilityNode>> neighbours = new HashMap<Integer, Set<MultiProbabilityNode>>();
	HashMap<Integer, Set<MultiProbabilityNode>> influencedNeighbours = new HashMap<Integer, Set<MultiProbabilityNode>>();
	
	//TIntDoubleHashMap tVal = new TIntDoubleHashMap();
	
	for (MultiProbabilityNode m : g.getVertices()) {
		double total = 1;
		double internal = c.getInternalEnvironment(m);
		Set<MultiProbabilityNode> neighs = new HashSet<MultiProbabilityNode>();
		neighbours.put(m.getId(), neighs);
		influencedNeighbours.put(m.getId(), new HashSet<MultiProbabilityNode>());
	//	System.out.println("external " + external);
		for (TypedWeightedEdge edge : g.getOutEdges(m)){
			MultiProbabilityNode n = g.getOpposite(m, edge);
			neighs.add(n);
			total += edge.getWeight() * (1 + internal + c.getExternalEnvironment(n));
		//	System.out.println("internal for " + n.getId() + " is " + external);
		}
		
		m.setValue(total);
		nodesToSort.add(m);
	//	tVal.put(m.getId(), 0);
	//	System.out.println(m.getId() + " : " + m.getValue());
	}
	
	for (MultiProbabilityNode m : g.getVertices()) {
		if (m.isActivated(c)) {
			nodesToSort.remove(m);
			for (MultiProbabilityNode n : g.getNeighbors(m)){
				Set<MultiProbabilityNode> neighs = neighbours.get(n.getId());
				Set<MultiProbabilityNode> infNeighs = influencedNeighbours.get(n.getId());
		//		System.out.println("Neighbour : " + n.getId());
				infNeighs.add(m);
				neighs.remove(m);
				
				double probTotal = 0;
				double expectedTotal = 1;
				double internal = c.getInternalEnvironment(n);
				double external = c.getExternalEnvironment(n);
		//		System.out.println("intenral " + internal);
		//		System.out.println("extenral " + external);
				
				for (TypedWeightedEdge edge : g.getIncidentEdges(n)){
					if (edge.getConcept() == c.getId()) {
						MultiProbabilityNode nn = g.getOpposite(n, edge);
						
						if (infNeighs.contains(nn)) {
							probTotal += edge.getWeight() * (1 + external + c.getInternalEnvironment(nn));
						}
						else {
							expectedTotal += edge.getWeight() * (1 + internal + c.getExternalEnvironment(nn));
						}
						
					}
				}
				n.setValue((1 - probTotal) * expectedTotal);

			//	System.out.println("final value is : " + n.getValue());
			}
		}
	}
	
	Comparator<MultiProbabilityNode> comp = new MultiProbabilityComparator<MultiProbabilityNode>();
	SortedSet<MultiProbabilityNode> sortedN = new TreeSet<MultiProbabilityNode>(comp);
	
	for (MultiProbabilityNode m : nodesToSort) {
		sortedN.add(m);
	//	System.out.println("Adding " + m.getId());
	}
	
	while (seeds.size() < seedCount) {
		System.out.println("Seeds: " + seeds.size());
		MultiProbabilityNode choice = sortedN.last();
		if (!choice.isActivated(c)) {
			seeds.add(choice);	
		}
	//	System.out.println("choice node:");
	//	System.out.println(c.getInternalEnvironment((MultiProbabilityNode) choice));
	//	System.out.println(c.getExternalEnviornment((MultiProbabilityNode) choice));
		nodesToSort.remove(choice);
	//	System.out.println("Adding " + choice.getId());
		for (MultiProbabilityNode n : g.getNeighbors(choice)){
			Set<MultiProbabilityNode> neighs = neighbours.get(n.getId());
			Set<MultiProbabilityNode> infNeighs = influencedNeighbours.get(n.getId());
	//		System.out.println("Neighbour : " + n.getId());
			infNeighs.add(choice);
			neighs.remove(choice);
			
			double probTotal = 0;
			double expectedTotal = 1;
			double internal = c.getInternalEnvironment(n);
			double external = c.getExternalEnvironment(n);
	//		System.out.println("intenral " + internal);
	//		System.out.println("extenral " + external);

			for (TypedWeightedEdge edge : g.getIncidentEdges(n)){
				if (edge.getConcept() == c.getId()) {
					MultiProbabilityNode nn = g.getOpposite(n, edge);
					
					if (infNeighs.contains(nn)) {
						probTotal += edge.getWeight() * (1 + external + c.getInternalEnvironment(nn));
					}
					else {
						expectedTotal += edge.getWeight() * (1 + internal + c.getExternalEnvironment(nn));
					}
					
				}
			}
			n.setValue((1 - probTotal) * expectedTotal);

		//	System.out.println("final value is : " + n.getValue());
		}
		
		sortedN.clear();
		
		for (MultiProbabilityNode m : nodesToSort) {
			sortedN.add(m);
		//	System.out.println(m.getId() + " : " + m.getValue());
		}
	}
	
	return seeds;
}
}