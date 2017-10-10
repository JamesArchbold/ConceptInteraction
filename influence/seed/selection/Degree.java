package influence.seed.selection;
//selects nodes with the highest degrees
import edu.uci.ics.jung.graph.Graph;
import influence.concepts.Concept;
import influence.edges.Edge;
import influence.nodes.MultiProbabilityNode;
import influence.nodes.comparators.MultiProbabilityComparator;

import java.util.Comparator;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;

public class Degree {
	
	public <T extends Edge> HashSet<MultiProbabilityNode> getSeedsBurnIn
	(Graph<MultiProbabilityNode, T> g, int seedCount, Concept con) {
		
		HashSet<MultiProbabilityNode> seeds = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> nodesToSort = new HashSet<MultiProbabilityNode>();
	//	TIntDoubleHashMap tVal = new TIntDoubleHashMap();
		
		for (MultiProbabilityNode m : g.getVertices()) {
			m.setValue(g.getNeighborCount(m));
			nodesToSort.add(m);
		//	tVal.put(m.getId(), 0);
		//	System.out.println(m.getId() + " : " + m.getValue());
		}
		
		Comparator<MultiProbabilityNode> comp = new MultiProbabilityComparator<MultiProbabilityNode>();
		SortedSet<MultiProbabilityNode> sortedN = new TreeSet<MultiProbabilityNode>(comp);
		
		for (MultiProbabilityNode m : nodesToSort) {
			sortedN.add(m);
		//	System.out.println("Adding " + m.getId());
		}
		
		while (seeds.size() < seedCount) {
			MultiProbabilityNode choice = sortedN.last();
			if (!choice.isActivated(con)) { 
				seeds.add(choice);
			}
			nodesToSort.remove(choice);
			
			sortedN.clear();
			
			for (MultiProbabilityNode m : nodesToSort) {
				sortedN.add(m);
			//	System.out.println(m.getId() + " : " + m.getValue());
			}
		}
		
		return seeds;
	}
	
	public static <T extends Edge> HashSet<MultiProbabilityNode> getSeeds
	(Graph<MultiProbabilityNode, T> g, int seedCount) {
		
		HashSet<MultiProbabilityNode> seeds = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> nodesToSort = new HashSet<MultiProbabilityNode>();
		
		for (MultiProbabilityNode m : g.getVertices()) {
			m.setValue(g.getNeighborCount(m));
			nodesToSort.add(m);
		}
		
		Comparator<MultiProbabilityNode> comp = new MultiProbabilityComparator<MultiProbabilityNode>();
		SortedSet<MultiProbabilityNode> sortedN = new TreeSet<MultiProbabilityNode>(comp);
		
		for (MultiProbabilityNode m : nodesToSort) {
			sortedN.add(m);
		}
		
		while (seeds.size() < seedCount) {
			MultiProbabilityNode choice = sortedN.last();
		    seeds.add(choice);
			nodesToSort.remove(choice);
			
			sortedN.clear();
			
			for (MultiProbabilityNode m : nodesToSort) {
				sortedN.add(m);
			}
		}
		
		return seeds;
	}
	
	public static <T extends Edge> HashSet<MultiProbabilityNode> getSeedsOut
	(Graph<MultiProbabilityNode, T> g, int seedCount) {
		
		HashSet<MultiProbabilityNode> seeds = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> nodesToSort = new HashSet<MultiProbabilityNode>();
		
		for (MultiProbabilityNode m : g.getVertices()) {
			m.setValue(g.getOutEdges(m).size());
			nodesToSort.add(m);
		}
		
		Comparator<MultiProbabilityNode> comp = new MultiProbabilityComparator<MultiProbabilityNode>();
		SortedSet<MultiProbabilityNode> sortedN = new TreeSet<MultiProbabilityNode>(comp);
		
		for (MultiProbabilityNode m : nodesToSort) {
			sortedN.add(m);
		}
		
		while (seeds.size() < seedCount) {
			MultiProbabilityNode choice = sortedN.last();
		    seeds.add(choice);
			nodesToSort.remove(choice);
			
			sortedN.clear();
			
			for (MultiProbabilityNode m : nodesToSort) {
				sortedN.add(m);
			}
		}
		
		return seeds;
	}
}
