package influence.seed.selection;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.hash.TIntHashSet;
import influence.concepts.Concept;
import influence.edges.TypedWeightedEdge;
import influence.nodes.MultiProbabilityNode;
import influence.nodes.comparators.MultiProbabilityComparator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.uci.ics.jung.graph.Graph;

public class BridgeEnd {
	public HashSet<MultiProbabilityNode> getSeeds(HashMap<Integer, Integer> comMembership, Graph<MultiProbabilityNode, TypedWeightedEdge> g, 
			int seedCount, Concept con, HashSet<MultiProbabilityNode> targetNodes){
		
		HashSet<MultiProbabilityNode> seeds = new HashSet<MultiProbabilityNode>();
		
		//must find bridge ends first, using BFS from rumours to get to other communities
		HashSet<MultiProbabilityNode> bridgeEnds = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> nextConsider = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> seenBefore = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> currConsider = new HashSet<MultiProbabilityNode>();
		nextConsider.addAll(targetNodes);
		
		while (currConsider.size() > 0) {
			for (MultiProbabilityNode current : currConsider) {
				for (TypedWeightedEdge neighbour : g.getOutEdges(current)) {
					MultiProbabilityNode neigh = g.getOpposite(current, neighbour);
					if (!currConsider.contains(neigh) && !seenBefore.contains(neigh)){
						if (comMembership.get(current) == comMembership.get(neigh)) {
							nextConsider.add(neigh);
						}
						else {
							bridgeEnds.add(neigh);
						}
					}
				}
			}
			
			seenBefore.addAll(currConsider);
			currConsider.clear();
			currConsider.addAll(nextConsider);
			nextConsider.clear();
		}
		
		HashMap<MultiProbabilityNode, HashSet<MultiProbabilityNode>> protectorLists = new HashMap<MultiProbabilityNode, HashSet<MultiProbabilityNode>>();
		HashMap<MultiProbabilityNode, HashSet<MultiProbabilityNode>> bridgeLists = new HashMap<MultiProbabilityNode, HashSet<MultiProbabilityNode>>();
		HashSet<MultiProbabilityNode> nodesToSort = new HashSet<MultiProbabilityNode>();
		for (MultiProbabilityNode bridge : bridgeEnds) {
			seenBefore.clear();
			currConsider.clear();
			nextConsider.clear();
			
			HashSet<MultiProbabilityNode> pList = new HashSet<MultiProbabilityNode>();
			seenBefore.add(bridge);
			for (TypedWeightedEdge t : g.getInEdges(bridge)){
				currConsider.add(g.getOpposite(bridge, t));
			}
			
			boolean loop = true;
			
			while (loop){
				for (MultiProbabilityNode current : currConsider) {
					if (targetNodes.contains(current)){
						loop = false;
					}
					else {
						pList.add(current);
						current.setValue(current.getValue() + 1);
						nodesToSort.add(current);
						
						if (!bridgeLists.containsKey(current)){
							bridgeLists.put(current, new HashSet<MultiProbabilityNode>());
						}
						
						bridgeLists.get(current).add(bridge);
						
						for (TypedWeightedEdge t : g.getInEdges(current)){
							MultiProbabilityNode m = g.getOpposite(current, t);
							if (!seenBefore.contains(m) && !currConsider.contains(m)) {
								nextConsider.add(m);
							}
						}
						
					}
					
					seenBefore.addAll(currConsider);
					currConsider.clear();
					currConsider.addAll(nextConsider);
					nextConsider.clear();
				}
			}
			protectorLists.put(bridge, pList);
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
			
			for (MultiProbabilityNode m : bridgeLists.get(choice)){
				for (MultiProbabilityNode n : protectorLists.get(m)){
					if (n.getId() != choice.getId()) {
						bridgeLists.get(n).remove(m);
						n.setValue(n.getValue() - 1);
					}
				}
			}
			
			bridgeLists.remove(choice);
			
			sortedN.clear();
			for (MultiProbabilityNode m : nodesToSort) {
				sortedN.add(m);
			}
			
		}

		
		
		//then use bridge ends to find protectors that cover highest number of bridge ends
		
		
		return seeds;
	}
}
