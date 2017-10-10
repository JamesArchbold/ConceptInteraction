package influence.seed.selection;

import java.util.Comparator;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;

import influence.concepts.Concept;
import influence.edges.Edge;
import influence.edges.TypedEdge;
import influence.edges.TypedWeightedEdge;
import influence.nodes.MultiProbabilityNode;
import influence.nodes.Node;
import influence.nodes.comparators.MultiProbabilityComparator;
import edu.uci.ics.jung.graph.Graph;

public class SingleDiscount {
	
	public static <E extends Node, T extends Edge> HashSet<E> getSeeds
		(Graph<E, T> g, int seedCount) {
		
		HashSet<E> seeds = new HashSet<E>();
		HashSet<E> nodesToSort = new HashSet<E>();
		for (E m : g.getVertices()) {
			m.setValue(g.getNeighborCount(m));
			nodesToSort.add(m);
	//		System.out.println(m.getId() + " : " + m.getValue());
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
		//	System.out.println("Adding " + choice.getId());
			for (E n : g.getNeighbors(choice)){
				n.setValue(n.getValue() - 1);
			//	System.out.println("Neighbour : " + n.getId());
			}
			
			sortedN.clear();
			
			for (E m : nodesToSort) {
				sortedN.add(m);
			}
		}
		
		return seeds;
	}
	public static HashSet<MultiProbabilityNode> getSeedsBurnIn
	(Graph<MultiProbabilityNode, TypedEdge> g, int seedCount, Concept con) {
	
		HashSet<MultiProbabilityNode> seeds = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> nodesToSort = new HashSet<MultiProbabilityNode>();
		for (MultiProbabilityNode m : g.getVertices()) {
			m.setValue(g.getNeighborCount(m));
			nodesToSort.add(m);
	//		System.out.println(m.getId() + " : " + m.getValue());
		}
		
		for (MultiProbabilityNode m : g.getVertices()) {
			if (m.isActivated(con)) {
				nodesToSort.remove(m);
				for (MultiProbabilityNode n : g.getNeighbors(m)){
					n.setValue(n.getValue() - 1);
				//	System.out.println("Neighbour : " + n.getId());
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
			MultiProbabilityNode choice = sortedN.last();
			if (!choice.isActivated(con)){
				seeds.add(choice);
			}
			nodesToSort.remove(choice);
		//	System.out.println("Adding " + choice.getId());
			for (MultiProbabilityNode n : g.getNeighbors(choice)){
				n.setValue(n.getValue() - 1);
			//	System.out.println("Neighbour : " + n.getId());
			}
			
			sortedN.clear();
			
			for (MultiProbabilityNode m : nodesToSort) {
				sortedN.add(m);
			}
		}
		
		return seeds;
	}
	
	public static HashSet<MultiProbabilityNode> getSeedsTarget
	(Graph<MultiProbabilityNode, TypedWeightedEdge> g, int seedCount, Concept con) {
	
		HashSet<MultiProbabilityNode> seeds = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> nodesToSort = new HashSet<MultiProbabilityNode>();
		for (MultiProbabilityNode m : g.getVertices()) {
			m.setValue(g.getNeighborCount(m));
			nodesToSort.add(m);
	//		System.out.println(m.getId() + " : " + m.getValue());
		}
		
		for (MultiProbabilityNode m : g.getVertices()) {
			if (m.isActivated(con)) {
				nodesToSort.remove(m);
				for (MultiProbabilityNode n : g.getNeighbors(m)){
					n.setValue(n.getValue() - 1);
				//	System.out.println("Neighbour : " + n.getId());
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
			MultiProbabilityNode choice = sortedN.last();
			if (!choice.isActivated(con)){
				seeds.add(choice);
			}
			nodesToSort.remove(choice);
		//	System.out.println("Adding " + choice.getId());
			for (MultiProbabilityNode n : g.getNeighbors(choice)){
				n.setValue(n.getValue() - 1);
			//	System.out.println("Neighbour : " + n.getId());
			}
			
			sortedN.clear();
			
			for (MultiProbabilityNode m : nodesToSort) {
				sortedN.add(m);
			}
		}
		
		return seeds;
	}
}
