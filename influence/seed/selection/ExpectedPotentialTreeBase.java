package influence.seed.selection;

import edu.uci.ics.jung.graph.Graph;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import influence.concepts.Concept;
import influence.concepts.DiverseICConcept;
import influence.edges.TypedWeightedEdge;
import influence.nodes.MultiProbabilityNode;
import influence.nodes.comparators.MultiProbabilityComparator;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class ExpectedPotentialTreeBase {
	public static HashSet<MultiProbabilityNode> getSeeds
	(Graph<MultiProbabilityNode, TypedWeightedEdge> g, int seedCount, double thresh, Concept target, Concept inhibiting, HashSet<MultiProbabilityNode> seedsTarget) {
		HashSet<MultiProbabilityNode> seeds = new HashSet<MultiProbabilityNode>();
		HashMap<MultiProbabilityNode, TObjectDoubleHashMap<MultiProbabilityNode>> mapOfPathCosts = new HashMap<MultiProbabilityNode, TObjectDoubleHashMap<MultiProbabilityNode>>();
		//TIntObjectHashMap<TIntDoubleHashMap> mapOfPathCosts = new TIntObjectHashMap<TIntDoubleHashMap>();
		//HashSet<MultiProbabilityNode> targetSeeds = new HashSet<MultiProbabilityNode>();
		
		//Seed reachable lists seeds that can reach a given node
		HashMap<MultiProbabilityNode, HashSet<MultiProbabilityNode>> seedReachable = new HashMap<MultiProbabilityNode, HashSet<MultiProbabilityNode>>();
		//TIntObjectHashMap<TIntHashSet> seedReachable = new TIntObjectHashMap<TIntHashSet>();
		//Lists nodes that can be reached by a given node
		HashMap<MultiProbabilityNode, HashSet<MultiProbabilityNode>> neighbourReachable = new HashMap<MultiProbabilityNode, HashSet<MultiProbabilityNode>>();
	//	TIntObjectHashMap<TIntHashSet> neighbourReachable = new TIntObjectHashMap<TIntHashSet>();
		//System.out.println(g.getVertices());
	//	HashMap<Integer, Double> actProb = new HashMap<Integer, Double>();
		TObjectDoubleHashMap<MultiProbabilityNode> actProb = new TObjectDoubleHashMap<MultiProbabilityNode>();
		int ii = 0;
		DiverseICConcept target2 = (DiverseICConcept) target;
		Collection<MultiProbabilityNode> nodesAll = null;
		if (target2.getLastActivated().size() == 0) {
			nodesAll = seedsTarget;
		}
		else {
			nodesAll = target2.getLastActivated();
		}
		
		//HashMap<MultiProbabilityNode, HashMap<MultiProbabilityNode, HashSet<MultiProbabilityNode>>> ancestorMaps = new HashMap<MultiProbabilityNode, HashMap<MultiProbabilityNode, HashSet<MultiProbabilityNode>>>();
		HashMap<MultiProbabilityNode, HashMap<MultiProbabilityNode, HashSet<MultiProbabilityNode>>> descendentsMaps = new HashMap<MultiProbabilityNode, HashMap<MultiProbabilityNode, HashSet<MultiProbabilityNode>>>();
		HashMap<MultiProbabilityNode, TObjectDoubleHashMap<MultiProbabilityNode>> actProbContribution = new HashMap<MultiProbabilityNode, TObjectDoubleHashMap<MultiProbabilityNode>>();
		//HashMap<MultiProbabilityNode, HashMap<MultiProbabilityNode, HashSet<MultiProbabilityNode>>> descendentsNeighbours = new HashMap<MultiProbabilityNode, HashMap<MultiProbabilityNode, HashSet<MultiProbabilityNode>>>();
		HashMap<MultiProbabilityNode, TObjectDoubleHashMap<MultiProbabilityNode>> expContribution = new HashMap<MultiProbabilityNode, TObjectDoubleHashMap<MultiProbabilityNode>>();

		
		
		for (MultiProbabilityNode curr : nodesAll) {
			System.out.println("Checking " + (ii++) + " out of " + nodesAll.size());
			actProbContribution.put(curr, new TObjectDoubleHashMap<MultiProbabilityNode>());
			if (curr.isActivated(target)){
				TObjectDoubleHashMap<MultiProbabilityNode> pathCost = getPathCostMapTree(g, curr, thresh, seeds, target, inhibiting, descendentsMaps);
				//Iterator<MultiProbabilityNode> keyIter = pathCost.keySet().iterator();
				for (MultiProbabilityNode m : pathCost.keySet()) {
					//MultiProbabilityNode m = keyIter.next();
					if (actProb.containsKey(m)) {
						actProb.put(m, actProb.get(m)+pathCost.get(m));
						actProbContribution.get(curr).put(m, pathCost.get(m));
					}
					else {
						seedReachable.put(m, new HashSet<MultiProbabilityNode>());
						//neighbourReachable.put(m, new TIntHashSet());
						actProb.put(m,pathCost.get(m));
						actProbContribution.get(curr).put(m, pathCost.get(m));
					}
					seedReachable.get(m).add(curr);
				}
				//mapOfPathCosts.put(curr, pathCost);
			}
			//System.out.println(descendentsMaps.get(curr).size());
		}
		
		System.out.println("act done)");
////		System.out.println(actProb.size());
		int i = 0;
		//HashMap<MultiProbabilityNode, Double> expected = new HashMap<MultiProbabilityNode, Double>();
		TObjectDoubleHashMap<MultiProbabilityNode> expected = new TObjectDoubleHashMap<MultiProbabilityNode>();
		//Iterator<MultiProbabilityNode> actIter = actProb.keySet().iterator();
		for (MultiProbabilityNode current : actProb.keySet()) {
		//	expContribution.put(current, new TObjectDoubleHashMap<MultiProbabilityNode>());
		//	MultiProbabilityNode current = actIter.next();
			System.out.println(i++  + "/" + actProb.size());
			TObjectDoubleHashMap<MultiProbabilityNode> pathCost = getPathCostMap(g, current, thresh,seeds, target, inhibiting);
			double total = 0;
			//Iterator<MultiProbabilityNode> keyIter = pathCost.keySet().iterator();
			for (MultiProbabilityNode m : pathCost.keySet()) {
			//	MultiProbabilityNode m = keyIter.next();
				total = total + pathCost.get(m);
				if (!expContribution.containsKey(m)){expContribution.put(m, new TObjectDoubleHashMap<MultiProbabilityNode>());}
				expContribution.get(m).put(current, pathCost.get(m));
//				if (!neighbourReachable.containsKey(m)){
//					neighbourReachable.put(m, new HashSet<MultiProbabilityNode>());
//				}
//				
//				neighbourReachable.get(m).add(current);
			}
			
			expected.put(current, total);
			current.setValue(actProb.get(current) * expected.get(current));
		}
		System.out.println("exp done)");
		
		Comparator<MultiProbabilityNode> comp = new MultiProbabilityComparator<MultiProbabilityNode>();
		SortedSet<MultiProbabilityNode> sortedN = new TreeSet<MultiProbabilityNode>(comp);
	   // sortedN.addAll(actProb.keySet());
	    

		for (MultiProbabilityNode m : actProb.keySet()) {
			sortedN.add(m);
		}
	    
	    
    	MultiProbabilityNode u = sortedN.last();
    	seeds.add(u);
    	actProb.remove(u);
    	expected.remove(u);
    	HashSet<MultiProbabilityNode> changed = new HashSet<MultiProbabilityNode>();
	    while (seeds.size() < seedCount && sortedN.size() > 0){
	    	System.out.println("Seeds " + seeds.size());
	    	System.out.println("Evaluating seeds");
	    	int jj = 0;
	    //	TIntIterator seedIter = seedReachable.get(u.getId()).iterator();
	    	for (MultiProbabilityNode tSeed : seedReachable.get(u)) {
	    	//	int tSeed = seedIter.next();
	    	//	System.out.println("Seed " + (jj++) + " out of " + seedReachable.get(u).size());
	    		HashSet<MultiProbabilityNode> toCheck = new HashSet<MultiProbabilityNode>();
	    		HashSet<MultiProbabilityNode> checkNext = new HashSet<MultiProbabilityNode>();
	    		HashSet<MultiProbabilityNode> seenBefore = new HashSet<MultiProbabilityNode>();
	    		if (descendentsMaps.get(tSeed).containsKey(u)){
	    			toCheck.addAll(descendentsMaps.get(tSeed).get(u));
	    		}
	    		seenBefore.add(u);
	    		while (!toCheck.isEmpty()) {
		    		for (MultiProbabilityNode m : toCheck) {
		    		//	System.out.println("links!");
		    			if (seedReachable.get(m).contains(tSeed)) {
		    				actProb.put(m, actProb.get(m) - actProbContribution.get(tSeed).get(m));
		    				double newProb = actProbContribution.get(tSeed).get(m) * (1 + target.getConceptExternal(inhibiting));
		    				actProbContribution.get(tSeed).put(m, newProb);
		    				actProb.put(m, actProb.get(m) + actProbContribution.get(tSeed).get(m));
		    				if (descendentsMaps.get(tSeed).containsKey(m)){checkNext.addAll(descendentsMaps.get(tSeed).get(m));}
		    				
	    					if (actProb.get(m) <= 0) {
		    					actProb.remove(m);
		    					expected.remove(m);
		    					changed.remove(m);
		    				}
	    					else {
	    						changed.add(m);
	    					}
		    				
		    			}
		    			
		    		}
		    		
	    			seenBefore.addAll(toCheck);
	    			toCheck.clear();
	    			checkNext.removeAll(seenBefore);
	    			toCheck.addAll(checkNext);
	    			checkNext.clear();
	    		}
	    		
	    	}
	    	
	    //	HashSet<MultiProbabilityNode> seenBeforeDescen = new HashSet<MultiProbabilityNode>();
	    //	descendentNeigh.addAll(g.getSuccessors(u));
	    	HashSet<MultiProbabilityNode> tNeighbours = new HashSet<MultiProbabilityNode>();
	    	tNeighbours.addAll(expContribution.get(u).keySet());
	   // 	HashSet<MultiProbabilityNode> toRemove = new HashSet<MultiProbabilityNode>();
	    	Set<MultiProbabilityNode> neighIter = getIncomingNeighbours(g, u, thresh, seeds, target, inhibiting);
	    	for(MultiProbabilityNode tNeigh : neighIter) {
	    	//	int tNeigh = neighIter.next();
	    		if (actProb.containsKey(tNeigh) && !seeds.contains(tNeigh)) {
	    			TObjectDoubleHashMap<MultiProbabilityNode> pathCost = getPathCostMap(g, tNeigh, thresh, seeds, target, inhibiting);
	    			double total = 0;
	    			Iterator<MultiProbabilityNode> pathIter = pathCost.keySet().iterator();
	    			while(pathIter.hasNext()) {
	    				MultiProbabilityNode m = pathIter.next();
	    				total = total + pathCost.get(m);
	    			//	if (!neighbourReachable2.containsKey(m)){
	    				//	neighbourReachable2.put(m, new TIntHashSet());
	    			//	}
	    			//	neighbourReachable2.get(m).add(tNeigh);
	    			}
	    			if (total != expected.get(tNeigh)) {
	    				expected.put(tNeigh, total);
	    				changed.add(tNeigh);
	    			}
	    			
	    		}
	    	}
	 //   	System.out.println("Updating Reachable");
	    //	neighIter = neighbourReachable2.keySet().iterator();
	    //	while (neighIter.hasNext()) {
	    //		int tNeigh = neighIter.next();
	   // 		neighbourReachable.put(tNeigh, neighbourReachable2.get(tNeigh));
	   // 	}
	    //	neighbourReachable2.clear();
	//    	System.out.println("Updating Changed");
	    	
	    //	neighIter = changed.iterator();
	    	for (MultiProbabilityNode cu : changed) {
	    	//	MultiProbabilityNode cu = nodesFullSet.get(neighIter.next());
	    		cu.setValue(actProb.get(cu.getId()) * expected.get(cu.getId()));
	    	}
	    	
	    	changed.clear();
			sortedN.clear();
			
			//actIter = actProb.keySet().iterator();
			for (MultiProbabilityNode m : actProb.keySet()) {
				sortedN.add(m);
			}
			if (!sortedN.isEmpty()) {
		    	u = sortedN.last();
		    	seeds.add(u);
		    	actProb.remove(u);
		    	expected.remove(u);
			}

	    	
	    }
		
		return seeds;
	}
	
	private static Set<MultiProbabilityNode> getIncomingNeighbours(Graph<MultiProbabilityNode, TypedWeightedEdge> g, MultiProbabilityNode current, double thresh, 
			HashSet<MultiProbabilityNode> seeds, Concept target, Concept boosting){
		
		TObjectDoubleHashMap<MultiProbabilityNode> pathCost = new TObjectDoubleHashMap<MultiProbabilityNode>();
		
		Collection<TypedWeightedEdge> currSet = g.getInEdges(current);
		HashSet<MultiProbabilityNode> neighbours = new HashSet<MultiProbabilityNode>();
		
		boolean currBoost = seeds.contains(current);
		
		for (TypedWeightedEdge twe : currSet) {
			MultiProbabilityNode neigh = g.getOpposite(current, twe);
			
			if (twe.getWeight() >= thresh && !neigh.isActivated(target)) {
				if (pathCost.containsKey(neigh)) {
					double cost = twe.getWeight();
					if (seeds.contains(neigh)) {cost = cost * (1 + target.getConceptInternal(boosting));}
					if (currBoost) {cost = cost * (1 + target.getConceptExternal(boosting));}
					pathCost.put(neigh, pathCost.get(neigh) + cost);
				}
				else {
					double cost = twe.getWeight();
					if (seeds.contains(neigh)) {cost = cost * (1 + target.getConceptInternal(boosting));}
					if (currBoost) {cost = cost * (1 + target.getConceptExternal(boosting));}
					pathCost.put(neigh, cost);
				}
			}
			
			neighbours.add(neigh);
		}
		
		HashSet<MultiProbabilityNode> seenBefore = new HashSet<MultiProbabilityNode>();
		seenBefore.addAll(neighbours);
		HashSet<MultiProbabilityNode> nextConsider = new HashSet<MultiProbabilityNode>();
		
		while (!neighbours.isEmpty()) {
		//	System.out.println("CHecking neighbour numbers: " + neighbours.size());
			for (MultiProbabilityNode currentNeighbour : neighbours) {
				//int currentNeighbourID = iter.next();
				//MultiProbabilityNode currentNeighbour = nodesFullSet.get(currentNeighbourID);
				boolean currNeighBoost = seeds.contains(currentNeighbour);
				for(TypedWeightedEdge outgoingEdge : g.getInEdges(currentNeighbour)) {
					
					MultiProbabilityNode s = g.getOpposite(currentNeighbour, outgoingEdge);
					if (!s.isActivated(target)) {
						double sCost = outgoingEdge.getWeight() * pathCost.get(currentNeighbour);
						if (currNeighBoost) {sCost = sCost * (1 + target.getConceptInternal(boosting));}
						if (seeds.contains(s)) {sCost = sCost * (1+target.getConceptExternal(boosting));}
						if (sCost >= thresh){
							if (!pathCost.containsKey(s)){
								pathCost.put(s, sCost);
								nextConsider.add(s);
							}
						}	
					}
				}
			}
			neighbours.clear();
			neighbours.addAll(nextConsider);
			nextConsider.clear();
		}
		
		return pathCost.keySet();
	}
	
	private static TObjectDoubleHashMap<MultiProbabilityNode> getPathCostMap(Graph<MultiProbabilityNode, TypedWeightedEdge> g, MultiProbabilityNode current, double thresh, 
			HashSet<MultiProbabilityNode> seeds, Concept target, Concept boosting) {
		TObjectDoubleHashMap<MultiProbabilityNode> pathCost = new TObjectDoubleHashMap<MultiProbabilityNode>();
		Collection<TypedWeightedEdge> currSet = g.getOutEdges(current);
		HashSet<MultiProbabilityNode> neighbours = new HashSet<MultiProbabilityNode>();
	//	System.out.println("Starting Path Map");
		int i = 0;
		for (TypedWeightedEdge twe : currSet) {
			//System.out.println("Edge " + (i++) + " out of " + currSet.size());
			MultiProbabilityNode neigh = g.getOpposite(current,twe);
			if (twe.getWeight() >= thresh && !neigh.isActivated(target)) {
				if (pathCost.containsKey(neigh)) {
					double cost = twe.getWeight();
					if (seeds.contains(neigh)) {cost = cost * (1+target.getConceptExternal(boosting));}
					pathCost.put(neigh, pathCost.get(neigh)+cost);
				}
				else {
					double cost = twe.getWeight();
					if (seeds.contains(neigh)) {cost = cost * (1+target.getConceptExternal(boosting));}
					pathCost.put(neigh,cost);
				}
				neighbours.add(neigh);
			}
		}
		
		HashSet<MultiProbabilityNode> nextConsider = new HashSet<MultiProbabilityNode>();
		
		//System.out.println("Neighbours");
		while (!neighbours.isEmpty()) {
		//	System.out.println("CHecking neighbour numbers: " + neighbours.size());
			for (MultiProbabilityNode currentNeighbour : neighbours) {
				boolean currNeighBoost = seeds.contains(currentNeighbour);
				for(TypedWeightedEdge outgoingEdge : g.getOutEdges(currentNeighbour)) {
					MultiProbabilityNode s = g.getOpposite(currentNeighbour, outgoingEdge);
					if (!s.isActivated(target)) {
						double sCost = outgoingEdge.getWeight() * pathCost.get(currentNeighbour);
						if (currNeighBoost) {sCost = sCost * (1 + target.getConceptExternal(boosting));}
						if (seeds.contains(s)) {sCost = sCost * (1+target.getConceptInternal(boosting));}
						if (sCost >= thresh){
							if (pathCost.containsKey(s)){
								if (pathCost.get(s) < sCost) {
									pathCost.put(s, sCost);
									nextConsider.add(s);
								}
							}
							else {
								pathCost.put(s, sCost);
								nextConsider.add(s);
							}
						}	
					}
				}
			}
			neighbours.clear();
			neighbours.addAll(nextConsider);
			nextConsider.clear();
		}
		
		return pathCost;
	}
	
	@SuppressWarnings("unused")
	private static TObjectDoubleHashMap<MultiProbabilityNode> getPathCostMapTree(Graph<MultiProbabilityNode, TypedWeightedEdge> g, MultiProbabilityNode current, double thresh, 
			HashSet<MultiProbabilityNode> seeds, Concept target, Concept boosting,
			HashMap<MultiProbabilityNode, HashMap<MultiProbabilityNode, HashSet<MultiProbabilityNode>>> descendents) {
		
		
		HashMap<MultiProbabilityNode, HashSet<MultiProbabilityNode>> links = new HashMap<MultiProbabilityNode, HashSet<MultiProbabilityNode>>();
		HashMap<MultiProbabilityNode, MultiProbabilityNode> parent = new HashMap<MultiProbabilityNode, MultiProbabilityNode>();
		TObjectDoubleHashMap<MultiProbabilityNode> pathCost = new TObjectDoubleHashMap<MultiProbabilityNode>();
		Collection<TypedWeightedEdge> currSet = g.getOutEdges(current);
		HashSet<MultiProbabilityNode> neighbours = new HashSet<MultiProbabilityNode>();
	//	System.out.println("Starting Path Map");
		int i = 0;
		for (TypedWeightedEdge twe : currSet) {
			//System.out.println("Edge " + (i++) + " out of " + currSet.size());
			MultiProbabilityNode neigh = g.getOpposite(current,twe);
			if (twe.getWeight() >= thresh && !neigh.isActivated(target)) {
				if (pathCost.containsKey(neigh)) {
					double cost = twe.getWeight();
					if (seeds.contains(neigh)) {cost = cost * (1+target.getConceptExternal(boosting));}
					pathCost.put(neigh, pathCost.get(neigh)+cost);
				}
				else {
					double cost = twe.getWeight();
					if (seeds.contains(neigh)) {cost = cost * (1+target.getConceptExternal(boosting));}
					pathCost.put(neigh,cost);
				}
				neighbours.add(neigh);
			}
		}
		
		HashSet<MultiProbabilityNode> nextConsider = new HashSet<MultiProbabilityNode>();
		
		//System.out.println("Neighbours");
		while (!neighbours.isEmpty()) {
		//	System.out.println("CHecking neighbour numbers: " + neighbours.size());
			for (MultiProbabilityNode currentNeighbour : neighbours) {
				boolean currNeighBoost = seeds.contains(currentNeighbour);
				for(TypedWeightedEdge outgoingEdge : g.getOutEdges(currentNeighbour)) {
					MultiProbabilityNode s = g.getOpposite(currentNeighbour, outgoingEdge);
					if (!s.isActivated(target)) {
						double sCost = outgoingEdge.getWeight() * pathCost.get(currentNeighbour);
						if (currNeighBoost) {sCost = sCost * (1 + target.getConceptExternal(boosting));}
						if (seeds.contains(s)) {sCost = sCost * (1+target.getConceptInternal(boosting));}
						if (sCost >= thresh){
							if (pathCost.containsKey(s)){
								if (pathCost.get(s) < sCost) {
									pathCost.put(s, sCost);
									nextConsider.add(s);
									if (!links.containsKey(currentNeighbour)) {
										links.put(currentNeighbour, new HashSet<MultiProbabilityNode>());
									}
									links.get(currentNeighbour).add(s);
									links.get(parent.get(s)).remove(s);
									parent.put(s,currentNeighbour);
								}
							}
							else {
								pathCost.put(s, sCost);
								nextConsider.add(s);
								if (!links.containsKey(currentNeighbour)) {
									links.put(currentNeighbour, new HashSet<MultiProbabilityNode>());
								}
								links.get(currentNeighbour).add(s);
								parent.put(s, currentNeighbour);
							}
						}	
					}
				}
			}
			neighbours.clear();
			neighbours.addAll(nextConsider);
			nextConsider.clear();
		}
		
		descendents.put(current, links);
		return pathCost;
	}
}
