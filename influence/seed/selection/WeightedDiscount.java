package influence.seed.selection;

import influence.concepts.Concept;
import influence.edges.Edge;
import influence.edges.TypedEdge;
import influence.edges.TypedWeightedEdge;
import influence.nodes.MultiProbabilityNode;
import influence.nodes.Node;
import influence.nodes.comparators.MultiProbabilityComparator;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.uci.ics.jung.graph.Graph;

public class WeightedDiscount {
	public <E extends Node, T extends Edge> HashSet<E> getSeeds
	(Graph<E, T> g, int seedCount, Concept c) {
	
		HashSet<E> seeds = new HashSet<E>();
		HashSet<E> nodesToSort = new HashSet<E>();
		for (E m : g.getVertices()) {
			double total = 0;
			total = c.getInternalEnvironment((MultiProbabilityNode) m);
			for (E n : g.getNeighbors(m)) {
				Collection<T> edges = g.findEdgeSet(m, n);
				for (T ee: edges) {
					if (((TypedEdge)ee).getConcept() == c.getId()){
						total+= 1 + c.getExternalEnvironment((MultiProbabilityNode) n);
						break;
					}
				}
				
			}
			
			m.setValue(total);
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
		//	System.out.println("Decrement: " +( 1 + c.getInternalEnvironment((MultiProbabilityNode) choice)));
			for (E n : g.getNeighbors(choice)){
				n.setValue(n.getValue() - (1 + c.getExternalEnvironment((MultiProbabilityNode) choice)));
		//		System.out.println("Neighbour : " + n.getId());
			}
			
			sortedN.clear();
			
			for (E m : nodesToSort) {
				sortedN.add(m);
			//	System.out.println(m.getId() + " : " + m.getValue());
			}
		}
		
		return seeds;
	}
	
	public static <T extends Edge> HashSet<MultiProbabilityNode> getSeedsBurnIn
	(Graph<MultiProbabilityNode, T> g, int seedCount, Concept c) {
	
		HashSet<MultiProbabilityNode> seeds = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> nodesToSort = new HashSet<MultiProbabilityNode>();
		for (MultiProbabilityNode m : g.getVertices()) {
			double total = 0;
			total = c.getInternalEnvironment(m);
			for (MultiProbabilityNode n : g.getNeighbors(m)) {
				Collection<T> edges = g.findEdgeSet(m, n);
				for (T ee: edges) {
					if (((TypedWeightedEdge)ee).getConcept() == c.getId()){
						total+= 1 + c.getExternalEnvironment(n);
						break;
					}
				}
				
			}
			
			m.setValue(total);
			nodesToSort.add(m);
	//		System.out.println(m.getId() + " : " + m.getValue());
		}
		
		for (MultiProbabilityNode m : g.getVertices()) {
			if (m.isActivated(c)) {
				nodesToSort.remove(m);
				for (MultiProbabilityNode n : g.getNeighbors(m)){
					n.setValue(n.getValue() - (1 + c.getExternalEnvironment(m)));
			//		System.out.println("Neighbour : " + n.getId());
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
			System.out.println("Seed count for CASD: " + seeds.size());
			MultiProbabilityNode choice = sortedN.last();
			if (!choice.isActivated(c)) {
				seeds.add(choice);
			}
			nodesToSort.remove(choice);
		//	System.out.println("Adding " + choice.getId());
		//	System.out.println("Decrement: " +( 1 + c.getInternalEnvironment((MultiProbabilityNode) choice)));
			for (MultiProbabilityNode n : g.getNeighbors(choice)){
				n.setValue(n.getValue() - (1 + c.getExternalEnvironment(choice)));
		//		System.out.println("Neighbour : " + n.getId());
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
