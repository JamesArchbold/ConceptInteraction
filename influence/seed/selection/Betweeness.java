package influence.seed.selection;

import influence.concepts.Concept;
import influence.edges.TypedWeightedEdge;
import influence.nodes.MultiProbabilityNode;
import influence.nodes.comparators.MultiProbabilityComparator;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.Graph;

public class Betweeness {

	public static HashSet<MultiProbabilityNode> getSeeds
	(Graph<MultiProbabilityNode, TypedWeightedEdge> g, int seedCount, Concept con, HashSet<MultiProbabilityNode> targetNodes) {
		
		HashSet<MultiProbabilityNode> seeds = new HashSet<MultiProbabilityNode>();
	
		DijkstraShortestPath<MultiProbabilityNode, TypedWeightedEdge> dij = new DijkstraShortestPath<MultiProbabilityNode, TypedWeightedEdge>(g, new MAITransformer3());
		HashSet<MultiProbabilityNode> nodesToSort = new HashSet<MultiProbabilityNode>();
		
		for (MultiProbabilityNode n : targetNodes) {
			for (MultiProbabilityNode m : g.getVertices()) {
				if (!targetNodes.contains(m)) {
					List<TypedWeightedEdge> path = dij.getPath(n, m);
					MultiProbabilityNode source = null;
					MultiProbabilityNode dest = null;
					
					source = n;
					for (int i = 0; i < path.size(); i++) {
						if (targetNodes.contains(dest)) {
							break;
						}
						dest = g.getOpposite(source, path.get(i));
						dest.setValue(dest.getValue() + 1);
						source = dest;
						nodesToSort.add(dest);
					}
					
				}
			}
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

class MAITransformer3 implements Transformer<TypedWeightedEdge, Number>{

	@Override
	public Number transform(TypedWeightedEdge arg0) {
		return arg0.getWeight();
	}

}