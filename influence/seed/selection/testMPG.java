package influence.seed.selection;

import gnu.trove.map.hash.TObjectDoubleHashMap;
import influence.concepts.Concept;
import influence.concepts.DiverseICConcept;
import influence.concepts.DiverseLTConcept;
import influence.edges.TypedWeightedEdge;
import influence.nodes.MultiProbabilityNode;
import influence.nodes.comparators.MultiProbabilityComparator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.uci.ics.jung.graph.Graph;

public class testMPG {
	public static HashSet<MultiProbabilityNode> getSeeds
	(Graph<MultiProbabilityNode, TypedWeightedEdge> g, int seedCount, double thresh, Concept target, Concept inhibiting, HashSet<MultiProbabilityNode> seedsTarget, Concept modifying) {
		HashSet<MultiProbabilityNode> seeds = new HashSet<MultiProbabilityNode>();

		//TIntObjectHashMap<TIntDoubleHashMap> mapOfPathCosts = new TIntObjectHashMap<TIntDoubleHashMap>();
		HashMap<MultiProbabilityNode, TObjectDoubleHashMap<MultiProbabilityNode>> mapOfPathCosts = new HashMap<MultiProbabilityNode, TObjectDoubleHashMap<MultiProbabilityNode>>();
		HashMap<MultiProbabilityNode, HashSet<MultiProbabilityNode>> seedReachable = new HashMap<MultiProbabilityNode, HashSet<MultiProbabilityNode>>();
		TObjectDoubleHashMap<MultiProbabilityNode> actProb = new TObjectDoubleHashMap<MultiProbabilityNode>();
		TObjectDoubleHashMap<MultiProbabilityNode> modProb = new TObjectDoubleHashMap<MultiProbabilityNode>();
		//int ii = 0;
		Collection<MultiProbabilityNode> nodesAll = null;
		HashMap<MultiProbabilityNode, ArrayList<TObjectDoubleHashMap<MultiProbabilityNode>>> mapOfPathCosts2 = new HashMap<MultiProbabilityNode, ArrayList<TObjectDoubleHashMap<MultiProbabilityNode>>>();

		if (target.getType().equals("ic")) {
			DiverseICConcept target2 = (DiverseICConcept) target;
			if (target2.getLastActivated().size() == 0) {
				nodesAll = seedsTarget;
			}
			else {
				nodesAll = target2.getLastActivated();
			}
		}
		else {
			nodesAll = seedsTarget;
		}

		
		System.out.println("act start");
		long start = System.currentTimeMillis();
		for (MultiProbabilityNode curr : nodesAll) {
			if (curr.isActivated(target)){
				TObjectDoubleHashMap<MultiProbabilityNode> pathCost = getPathCostMap(g, curr, thresh, seeds, target, inhibiting);
				Iterator<MultiProbabilityNode> keyIter = pathCost.keySet().iterator();
				while (keyIter.hasNext()) {
					MultiProbabilityNode m = keyIter.next();
					if (actProb.containsKey(m)) {
						actProb.put(m, actProb.get(m)+pathCost.get(m));
					}
					else {
						seedReachable.put(m, new HashSet<MultiProbabilityNode>());
						//neighbourReachable.put(m, new TIntHashSet());
						actProb.put(m,pathCost.get(m));
					}
					seedReachable.get(m).add(curr);
				}
				mapOfPathCosts.put(curr, pathCost);
			}
		}
		
		for (MultiProbabilityNode curr: g.getVertices()) {
			if (curr.isActivated(modifying)){
				TObjectDoubleHashMap<MultiProbabilityNode> pathCost = getPathCostMap2(g, curr, thresh, seeds, modifying);
				Iterator<MultiProbabilityNode> keyIter = pathCost.keySet().iterator();
				modProb.put(curr, 1);
				while (keyIter.hasNext()){
					MultiProbabilityNode m = keyIter.next();
					if (modProb.containsKey(m)){
						if (modProb.get(m) < 1) {
							modProb.put(m, modProb.get(m)+pathCost.get(m));
							if (modProb.get(m) > 1) {
								modProb.put(m, 1);
							}
						}
					}
					else {
						modProb.put(m, pathCost.get(m));
					}
				}
			}
		}
		
		System.out.println("act done in " + (System.currentTimeMillis() - start) + "ms.");
	//	System.out.println(actProb.size());
		int i = 0;
		//HashMap<MultiProbabilityNode, Double> expected = new HashMap<MultiProbabilityNode, Double>();
		TObjectDoubleHashMap<MultiProbabilityNode> expected = new TObjectDoubleHashMap<MultiProbabilityNode>();
		Iterator<MultiProbabilityNode> actIter = actProb.keySet().iterator();
		while (actIter.hasNext()) {
			MultiProbabilityNode current = actIter.next();
			System.out.println("ExpProb: " + (i++)  + "/" + actProb.size());
			System.out.println("pathCost Start");
			start = System.currentTimeMillis();
			ArrayList<TObjectDoubleHashMap<MultiProbabilityNode>> pathCostList = getPathCostDouble(g, current, thresh,seeds, target, inhibiting, modifying, modProb);
			System.out.println("pathCostEnd in " + (System.currentTimeMillis() - start) + "ms.");
			TObjectDoubleHashMap<MultiProbabilityNode> pathCost = pathCostList.get(0);
			double total = 0;
			
			TObjectDoubleHashMap<MultiProbabilityNode> boostMap = pathCostList.get(1);
			Iterator<MultiProbabilityNode> keyIter = pathCost.keySet().iterator();
			while (keyIter.hasNext()) {
				MultiProbabilityNode m = keyIter.next();
				total = total + ((pathCost.get(m) * boostMap.get(m)) - pathCost.get(m));
			}
			if (target.getConceptExternal(inhibiting) < 0) {
				total = total * -1;
			}
			expected.put(current, total);
			mapOfPathCosts2.put(current, pathCostList);
			current.setValue(actProb.get(current) * expected.get(current));
		}
		System.out.println("exp done)");
		
		Comparator<MultiProbabilityNode> comp = new MultiProbabilityComparator<MultiProbabilityNode>();
		SortedSet<MultiProbabilityNode> sortedN = new TreeSet<MultiProbabilityNode>(comp);
	   // sortedN.addAll(actProb.keySet());
	    
		actIter = actProb.keySet().iterator();
		for (MultiProbabilityNode ac : actProb.keySet()) {
			sortedN.add(ac);
		}
	    
	    
    	MultiProbabilityNode u = sortedN.last();
    	seeds.add(u);
    	actProb.remove(u);
    	mapOfPathCosts2.remove(u);
    	expected.remove(u);
    	HashSet<MultiProbabilityNode> changed = new HashSet<MultiProbabilityNode>();
	    while (seeds.size() < seedCount && sortedN.size() > 0){
	    	System.out.println("Seed " + seeds.size());
	    	
	    	int jj = 0;
	    	Iterator<MultiProbabilityNode> seedIter = seedReachable.get(u).iterator();
	    	while (seedIter.hasNext()) {
	    		MultiProbabilityNode tSeed = seedIter.next();
	    	//	System.out.println("Seed " + seeds.size() + " Working : " + (jj++) + " out of " + seedReachable.get(u).size());
	    		//TObjectDoubleHashMap<MultiProbabilityNode> pathCost = getPathCostMap(g, tSeed, thresh, seeds, target, inhibiting);
	    		TObjectDoubleHashMap<MultiProbabilityNode> oldCost = mapOfPathCosts.get(tSeed);
	    		
	    		double newVal = oldCost.get(u) + (oldCost.get(u) * target.getConceptExternal(inhibiting));
	    		oldCost.put(u, newVal);
	    		
    			HashSet<TypedWeightedEdge> nextDoor = new HashSet<TypedWeightedEdge>();
    			HashSet<MultiProbabilityNode> nextDoorNodes = new HashSet<MultiProbabilityNode>();
    			
    			for (TypedWeightedEdge t : g.getOutEdges(u)){
    				if (t.getConcept() == target.getId()) {
    					MultiProbabilityNode neigh = g.getOpposite(u, t);
    					boolean neighIsSeed = seeds.contains(neigh);
    					if (actProb.containsKey(neigh)) {
	    					if (!neigh.equals(tSeed) && oldCost.containsKey(neigh)) {
	    						
	    						double highVal = 0.0;
	    					//	highVal = highVal * (1+target.getInternalEnvironment(u)) * (1+target.getExternalEnvironment(neigh));
	    						if (neighIsSeed){
	    							highVal = oldCost.get(u) * t.getWeight() * (1+target.getInternalEnvironment(u)+ target.getConceptInternal(inhibiting)) * (1+target.getExternalEnvironment(neigh)+ target.getConceptExternal(inhibiting));
	    						}
	    						else {
	    							highVal = oldCost.get(u) * t.getWeight() * (1+target.getInternalEnvironment(u)+ target.getConceptInternal(inhibiting)) * (1+target.getExternalEnvironment(neigh));
	    						}
	    						
	    						for (TypedWeightedEdge tt : g.getInEdges(neigh)) {
	    							if (tt.getConcept() == target.getId()) {
	    								MultiProbabilityNode neighNeigh = g.getOpposite(neigh, tt);
	    								
	    								if (!neighNeigh.equals(u) && !neighNeigh.equals(tSeed) && oldCost.containsKey(neighNeigh)) {
	    									if (oldCost.get(neighNeigh) > oldCost.get(neigh)) {
		    									double pVal = oldCost.get(neighNeigh) * tt.getWeight();
		    									//double pVal = oldCost.get(neighNeigh) * pWeight;
		    									
		    									double neighNeighEnv = 1 + target.getInternalEnvironment(neighNeigh);
		    									double neighEnv = 1 + target.getExternalEnvironment(neigh);
		    									if (seeds.contains(neighNeigh)) {neighNeighEnv = neighNeighEnv +target.getConceptInternal(inhibiting);}
		    									if (neighIsSeed){neighEnv = neighEnv + target.getConceptExternal(inhibiting);}
		    									
		    									pVal = pVal * neighNeighEnv * neighEnv;
		    									if (pVal > highVal) {
		    										highVal = pVal;
		    									}
	    									}
	    								}
	    							}
	    						}
	    	    				if (highVal != oldCost.get(neigh) && highVal >= thresh) {
	    	    					if (neighIsSeed){
	    	    						oldCost.put(neigh, highVal);
	    	    						nextDoorNodes.add(neigh);
	    	    					}
	    	    					else {
	        	    					actProb.put(neigh, actProb.get(neigh) - oldCost.get(neigh));
	        	    					oldCost.put(neigh, highVal);
	        	    					actProb.put(neigh, actProb.get(neigh) + oldCost.get(neigh));
	        	    					changed.add(neigh);
	    	    						nextDoorNodes.add(neigh);
	    	    					}
	
	    	    				}
	    	    				else if (highVal < thresh) {
	    	    					
	    	    					if (neighIsSeed) {
	    	    						oldCost.remove(neigh);
	    	    					}
	    	    					else {
		    	    					actProb.put(neigh, actProb.get(neigh) - oldCost.get(neigh));
		    	    					oldCost.remove(neigh);
		    	    					
		    	    					if (actProb.get(neigh) <= 0) {
		    		    					actProb.remove(neigh);
		    		    					mapOfPathCosts2.remove(neigh);
		    		    					expected.remove(neigh);
		    		    					changed.remove(neigh);
		    	    					}
		    	    					else{
		    	    						changed.add(neigh);
		    	    					}
	    	    					}
	    	    					
	    	    				}
	    						
	    					}
	    					else if (!neigh.equals(tSeed) && !neigh.isActivated(target) && !oldCost.containsKey(neigh)) {
	    						double highVal = 0.0;
	    					//	highVal = highVal * (1+target.getInternalEnvironment(u)) * (1+target.getExternalEnvironment(neigh));
	    						if (neighIsSeed){
	    							highVal = oldCost.get(u) * t.getWeight() * (1+target.getInternalEnvironment(u)+ target.getConceptInternal(inhibiting)) * (1+target.getExternalEnvironment(neigh)+ target.getConceptExternal(inhibiting));
	    						}
	    						else {
	    							highVal = oldCost.get(u) * t.getWeight() * (1+target.getInternalEnvironment(u)+ target.getConceptInternal(inhibiting)) * (1+target.getExternalEnvironment(neigh));
	    						}
	    						if (highVal >= thresh){
	    							if (!neighIsSeed) {
	    	    						oldCost.put(neigh, highVal);
	    	    						actProb.put(neigh, actProb.get(neigh) + oldCost.get(neigh));
	        	    					changed.add(neigh);
	    	    						nextDoorNodes.add(neigh);
	    							}
	    							else {
	    								oldCost.put(neigh, highVal);
	    								nextDoorNodes.add(neigh);
	    							}
	    						}
	    					}
    					}
    				}
    			}
    			
    			//HashSet<TypedWeightedEdge> nextDoorAgain = new HashSet<TypedWeightedEdge>();
    			HashSet<MultiProbabilityNode> nextDoorNodesAgain = new HashSet<MultiProbabilityNode>();
    			HashSet<MultiProbabilityNode> seenbefore = new HashSet<MultiProbabilityNode>();
    			
    			//don't want to keep exploring nodes, so we keep track of those we've encountered before.
    			seenbefore.addAll(nextDoorNodes);
    			seenbefore.add(tSeed);
    			seenbefore.add(u);
    			
    			while (!nextDoor.isEmpty()) {
    				for (MultiProbabilityNode current : nextDoorNodes) {
    					boolean currentIsSeed = seeds.contains(current);
    					for (TypedWeightedEdge t : g.getOutEdges(current)){
    						if (t.getConcept() == target.getId()) {
    	    					MultiProbabilityNode neigh = g.getOpposite(current, t);
    	    					if (actProb.containsKey(neigh)) {
	    	    					boolean neighIsSeed = seeds.contains(neigh);
	    	    					if (!seenbefore.contains(neigh) && oldCost.containsKey(neigh)) {
	    	    						double highVal = oldCost.get(current) * t.getWeight();
	    	    						//double highVal = oldCost.get(current) * relativeWeight;
	    	    						
    									double currEnv2 = 1 + target.getInternalEnvironment(current);
    									double neighEnv = 1 + target.getExternalEnvironment(neigh);
    									if (seeds.contains(current)) {currEnv2 = currEnv2 +target.getConceptInternal(inhibiting);}
    									if (neighIsSeed){neighEnv = neighEnv + target.getConceptExternal(inhibiting);}
    									
    									highVal = highVal * currEnv2 * neighEnv;  	  
	    	    						for (TypedWeightedEdge tt : g.getInEdges(neigh)) {
	    	    							if (tt.getConcept() == target.getId()) {
	    	    								MultiProbabilityNode neighNeigh = g.getOpposite(neigh, tt);
	    	    								
	    	    								if (!neighNeigh.equals(current) && !neighNeigh.equals(tSeed) && oldCost.containsKey(neighNeigh)) {
	    	    									if (oldCost.get(neighNeigh) > oldCost.get(neigh)) {
	    		    									double pVal = oldCost.get(neighNeigh) * tt.getWeight();
				    									double neighNeighEnv = 1 + target.getInternalEnvironment(neighNeigh);
				    								//	double neighEnv = 1 + target.getExternalEnvironment(neigh);
				    									if (seeds.contains(neighNeigh)) {neighNeighEnv = neighNeighEnv +target.getConceptInternal(inhibiting);}
				    								//	if (neighIsSeed){neighEnv = neighEnv + target.getConceptExternal(inhibiting);}
				    									
				    									pVal = pVal * neighNeighEnv * neighEnv;
	    		    									if (pVal > highVal) {
	    		    										highVal = pVal;
	    		    									}
	    	    									}
	    	    								}
	    	    								
	    	    							}
	    	    						}
	    	    						if (highVal != oldCost.get(neigh) && highVal >= thresh) {
	    	    	    					if (neighIsSeed){
	    	    	    						oldCost.put(neigh, highVal);
	    	    	    						nextDoorNodes.add(neigh);
	    	    	    					}
	    	    	    					else {
		    	    	    					actProb.put(neigh, actProb.get(neigh) - oldCost.get(neigh));
		    	    	    					oldCost.put(neigh, highVal);
		    	    	    					actProb.put(neigh, actProb.get(neigh) + oldCost.get(neigh));
		    	    	    					changed.add(neigh);
		    		    						nextDoorNodes.add(neigh);
	    	    	    					}
	    	    	    				}
	    	    	    				else if (highVal < thresh) {
	    	    	    					if (neighIsSeed) {
	    	    	    						oldCost.remove(neigh);
	    	    	    					}
	    	    	    					else {
		    	    	    					actProb.put(neigh, actProb.get(neigh) - oldCost.get(neigh));
		    	    	    					oldCost.remove(neigh);
		    	    	    					
		    	    	    					if (actProb.get(neigh) <= 0) {
		    	    		    					actProb.remove(neigh);
		    	    		    					mapOfPathCosts2.remove(neigh);
		    	    		    					expected.remove(neigh);
		    	    		    					changed.remove(neigh);
		    	    	    					}
		    	    	    					else{
		    	    	    						changed.add(neigh);
		    	    	    					}
	    	    	    					}
	    	    	    				}
	    	    					}
	    	    					else if (!neigh.equals(tSeed) && !neigh.isActivated(target) && !oldCost.containsKey(neigh)) {
	    	    						double highVal = oldCost.get(current) * t.getWeight();
	    	    					//	highVal = highVal * (1+target.getInternalEnvironment(current)) * (1+target.getExternalEnvironment(neigh));
	    	    						
    									double currEnv2 = 1 + target.getInternalEnvironment(current);
    									double neighEnv = 1 + target.getExternalEnvironment(neigh);
    									
	    	    						if (currentIsSeed) {highVal = highVal * (1 + target.getConceptExternal(inhibiting));}
	    	    						if (neighIsSeed){highVal = highVal * (1 + target.getConceptExternal(inhibiting));}
	    	    						

    									
	    	    						highVal = highVal * currEnv2 * neighEnv;
	    	    						if (highVal >= thresh){
	    	    							if (!neighIsSeed) {
	    	    	    						oldCost.put(neigh, highVal);
	    	    	    						actProb.put(neigh, actProb.get(neigh) + oldCost.get(neigh));
	    	        	    					changed.add(neigh);
	    	    	    						nextDoorNodes.add(neigh);
	    	    							}
	    	    							else {
	    	    								oldCost.put(neigh, highVal);
	    	    								nextDoorNodes.add(neigh);
	    	    							}
	    	    						}
	    	    					}
    	    					}
    						}
    					}
    				}
    				
    				seenbefore.addAll(nextDoorNodesAgain);
    				nextDoorNodes.clear();
    				nextDoorNodes.addAll(nextDoorNodesAgain);
    				nextDoorNodesAgain.clear();
    			}
	    		
	    	}
	    	
	    	Set<MultiProbabilityNode> neighIter = getIncomingNeighbours(g, u, thresh, seeds, target, inhibiting);
	    	boolean flip = false;
	    	if (target.getConceptExternal(inhibiting) < 0) {
	    		flip = true;
	    	}
	    	for (MultiProbabilityNode tNeigh : neighIter) {
	    		//System.out.println("Seed " + seeds.size() + " " + (kk++)  + "/" + neighIter.size());
	    		//the reachable node has some chance of activating the target and isn't a target seed
	    		if (actProb.containsKey(tNeigh) && !seeds.contains(tNeigh)) {
	    			
	    			//we get the path costs, and use that to update the expected value of tNeigh, first by removing u's contribution
	    			TObjectDoubleHashMap<MultiProbabilityNode> pathCost = mapOfPathCosts2.get(tNeigh).get(0);
	    		//	TObjectDoubleHashMap<MultiProbabilityNode> pathCostMod = mapOfPathCosts2.get(tNeigh).get(1);
	    			TObjectDoubleHashMap<MultiProbabilityNode> boostProbMap = mapOfPathCosts2.get(tNeigh).get(1);
	    		//	System.out.println(pathCost.get(u));
	    		//	System.out.println(expected.get(tNeigh));
	    			if (flip) {expected.put(tNeigh, expected.get(tNeigh) + ((pathCost.get(u) * boostProbMap.get(u) - pathCost.get(u))));}
	    			else {expected.put(tNeigh, expected.get(tNeigh) - ((pathCost.get(u) * boostProbMap.get(u) - pathCost.get(u))));}

	    			double newVal = pathCost.get(u) + (pathCost.get(u) * target.getConceptExternal(inhibiting));
		    		pathCost.put(u, newVal);
		    	//	pathCostMod.put(u, newVal);
		    		//apBoost = (1+ target.getConceptInternal(inhibiting));
		    		boostProbMap.put(u,  1+ target.getConceptInternal(inhibiting));
	    			if (flip) {expected.put(tNeigh, expected.get(tNeigh) - ((pathCost.get(u) * boostProbMap.get(u) - pathCost.get(u))));}
	    			else {expected.put(tNeigh, expected.get(tNeigh) + ((pathCost.get(u) * boostProbMap.get(u) - pathCost.get(u))));}
		    	//	expected.put(tNeigh, expected.get(tNeigh) + pathCost.get(u));
	    			
	    			changed.add(tNeigh);
	    			//adding u's new contribution

	    			
	    			//now need to explore u's neighbours and update their contributions
	    			HashSet<TypedWeightedEdge> nextDoor = new HashSet<TypedWeightedEdge>();
	    			HashSet<MultiProbabilityNode> nextDoorNodes = new HashSet<MultiProbabilityNode>();
	    			
	    			//get the neighbours first and explore their change contributions
	    			for (TypedWeightedEdge t : g.getOutEdges(u)){
	    				if (t.getConcept() == target.getId()) {
	    					MultiProbabilityNode neigh = g.getOpposite(u, t);
	    					boolean neighIsSeed = seeds.contains(neigh);
	    					MultiProbabilityNode ancestor = u;
	    					
	    					if (!neigh.equals(tNeigh) && pathCost.containsKey(neigh)) {
		    					//	double highVal = pathCost.get(u) * t.getWeight() * (1 + target.getConceptInternal(inhibiting));
	    						double highVal = 0.0;
	    					//	highVal = highVal * (1+target.getInternalEnvironment(u)) * (1+target.getExternalEnvironment(neigh));
	    						if (neighIsSeed){
	    							highVal = pathCost.get(u) * t.getWeight() * (1+target.getInternalEnvironment(u)+ target.getConceptInternal(inhibiting)) * (1+target.getExternalEnvironment(neigh)+ target.getConceptExternal(inhibiting));
	    						}
	    						else {
	    							highVal = pathCost.get(u) * t.getWeight() * (1+target.getInternalEnvironment(u)+ target.getConceptInternal(inhibiting)) * (1+target.getExternalEnvironment(neigh));
	    						}
	    						for (TypedWeightedEdge tt : g.getInEdges(neigh)) {
	    							if (tt.getConcept() == target.getId()) {
	    								MultiProbabilityNode neighNeigh = g.getOpposite(neigh, tt);
	    								//check neighbour is reachable by tneigh and is not tneigh or the chosen seed
	    								if (!neighNeigh.equals(u) && !neighNeigh.equals(tNeigh) && pathCost.containsKey(neighNeigh)) {
	    									//if neighneighs path cost is higher, it means it has a shorter path that does not go through s
	    									if (pathCost.get(neighNeigh) > pathCost.get(neigh)) {
		    									double pVal = pathCost.get(neighNeigh) * tt.getWeight();
		    									//double pVal = pathCost.get(neighNeigh) * pWeight;
		    									
		    									
		    									double neighNeighEnv = 1 + target.getInternalEnvironment(neighNeigh);
		    									double neighEnv = 1 + target.getExternalEnvironment(neigh);
		    									if (seeds.contains(neighNeigh)) {neighNeighEnv = neighNeighEnv +target.getConceptInternal(inhibiting);}
		    									if (neighIsSeed){neighEnv = neighEnv + target.getConceptExternal(inhibiting);}
		    									
		    									pVal = pVal * neighNeighEnv * neighEnv;
		    									if (pVal > highVal) {
		    										highVal = pVal;
		    										ancestor = neighNeigh;
		    									}
	    									}
	    								}
	    								
	    							}
	    						}
	    						//if highvalue is different from current path cost and still more than the threshold, we need to update the contribution and 
	    						//explore that node's neighbours
	    	    				if (highVal != pathCost.get(neigh) && highVal >= thresh) {
	    	    					//expected.put(tNeigh, expected.get(tNeigh) - pathCost.get(neigh));
	    	    				//	pathCost.put(neigh, highVal);
	    	    					double apBoost = 0.0;
	    	    					if (neighIsSeed) {apBoost = 1;}
	    	    					else {
		    	    					for (TypedWeightedEdge bT : g.findEdgeSet(ancestor, neigh)) {
		    	    						if (t.getConcept() == inhibiting.getId()) {
		    	    							apBoost = boostProbMap.get(ancestor) * bT.getWeight();
		    	    						}
		    	    					}
		    							if (modProb.contains(neigh)){
		    								apBoost = apBoost * (1 + (inhibiting.getConceptExternal(modifying) * modProb.get(neigh)));
		    							}
		    							
		    							//apBoost = apBoost * target.getConceptInternal(inhibiting);
		    	    					
	    	    					}
//	    	    					pathCostMod.put(neigh, highVal * apBoost);
//	    	    					boostProbMap.put(neigh, apBoost);
//	    	    					//expected.put(tNeigh, expected.get(tNeigh) + pathCost.get(neigh));
	    	    					if (flip) {expected.put(tNeigh, expected.get(tNeigh) + ((pathCost.get(neigh) * boostProbMap.get(neigh) - pathCost.get(neigh))));}
	    	    					else {expected.put(tNeigh, expected.get(tNeigh) - ((pathCost.get(neigh) * boostProbMap.get(neigh) - pathCost.get(neigh))));}
	    	    					
	    	    					pathCost.put(neigh, highVal);
	    	    					apBoost = (1+ (apBoost * target.getConceptInternal(inhibiting)));
	    	    					boostProbMap.put(neigh, apBoost);
		    						nextDoorNodes.add(neigh);
	    	    					if (flip) {expected.put(tNeigh, expected.get(tNeigh) - ((pathCost.get(neigh) * boostProbMap.get(neigh) - pathCost.get(neigh))));}
	    	    					else {expected.put(tNeigh, expected.get(tNeigh) + ((pathCost.get(neigh) * boostProbMap.get(neigh) - pathCost.get(neigh))));}
	    	    				}
	    	    				else if (highVal < thresh) {
	    	    					if (flip) {expected.put(tNeigh, expected.get(tNeigh) + ((pathCost.get(neigh) * boostProbMap.get(neigh) - pathCost.get(neigh))));}
	    	    					else {expected.put(tNeigh, expected.get(tNeigh) - ((pathCost.get(neigh) * boostProbMap.get(neigh) - pathCost.get(neigh))));}
	    	    					pathCost.remove(neigh);
	    	    					boostProbMap.remove(neigh);
	    	    				//	changed.add(tNeigh);
	    	    				}
	    						
	    					}
	    					else if (!neigh.equals(tNeigh) && !neigh.isActivated(target) && !pathCost.containsKey(neigh)) {
	    						double highVal = 0.0;
	    					//	highVal = highVal * (1+target.getInternalEnvironment(u)) * (1+target.getExternalEnvironment(neigh));
	    						if (neighIsSeed){
	    							highVal = pathCost.get(u) * t.getWeight() * (1+target.getInternalEnvironment(u)+ target.getConceptInternal(inhibiting)) * (1+target.getExternalEnvironment(neigh)+ target.getConceptExternal(inhibiting));
	    						}
	    						else {
	    							highVal = pathCost.get(u) * t.getWeight() * (1+target.getInternalEnvironment(u)+ target.getConceptInternal(inhibiting)) * (1+target.getExternalEnvironment(neigh));
	    						}
	    						
	    						if (highVal >= thresh){
		    						//pathCost.put(neigh, highVal);
		    						//expected.put(tNeigh, expected.get(tNeigh) + pathCost.get(neigh));
		    						double apBoost = 0.0;
	    	    					if (neighIsSeed) {apBoost = 1;}
	    	    					else {
		    	    					for (TypedWeightedEdge bT : g.findEdgeSet(ancestor, neigh)) {
		    	    						if (t.getConcept() == inhibiting.getId()) {
		    	    							apBoost = boostProbMap.get(ancestor) * bT.getWeight();
		    	    						}
		    	    					}
		    							if (modProb.contains(neigh)){
		    								apBoost = apBoost * (1 + (inhibiting.getConceptExternal(modifying) * modProb.get(neigh)));
		    							}
		    							
		    							//apBoost = apBoost * target.getConceptInternal(inhibiting);
		    	    					
	    	    					}
	    	    					//changed.add(tNeigh);
	    	    					//expected.put(tNeigh, expected.get(tNeigh) - ((pathCost.get(neigh) * boostProbMap.get(neigh) - pathCost.get(neigh))));
	    	    					pathCost.put(neigh, highVal);
	    	    					apBoost = (1+ (apBoost * target.getConceptInternal(inhibiting)));
	    	    					boostProbMap.put(neigh, apBoost);
		    						nextDoorNodes.add(neigh);
	    	    					if (flip) {expected.put(tNeigh, expected.get(tNeigh) - ((pathCost.get(neigh) * boostProbMap.get(neigh) - pathCost.get(neigh))));}
	    	    					else {expected.put(tNeigh, expected.get(tNeigh) + ((pathCost.get(neigh) * boostProbMap.get(neigh) - pathCost.get(neigh))));}
	    						}
	    					}
	    				}
	    			}
	    			
	    			//HashSet<TypedWeightedEdge> nextDoorAgain = new HashSet<TypedWeightedEdge>();
	    			HashSet<MultiProbabilityNode> nextDoorNodesAgain = new HashSet<MultiProbabilityNode>();
	    			HashSet<MultiProbabilityNode> seenbefore = new HashSet<MultiProbabilityNode>();
	    			
	    			//don't want to keep exploring nodes, so we keep track of those we've encountered before.
	    			seenbefore.addAll(nextDoorNodes);
	    			seenbefore.add(tNeigh);
	    			seenbefore.add(u);
	    			
	    			//begin iteratively exploring u's update neighbours
	    			while (!nextDoor.isEmpty()) {
	    				for (MultiProbabilityNode current : nextDoorNodes) {
	    					boolean currentIsSeed = seeds.contains(current);
	    					for (TypedWeightedEdge t : g.getOutEdges(current)){
	    						if (t.getConcept() == target.getId()) {
	    	    					MultiProbabilityNode neigh = g.getOpposite(current, t);
	    	    					boolean neighIsSeed = seeds.contains(neigh);
	    	    					MultiProbabilityNode ancestor = current;
	    	    					
	    	    					if (!seenbefore.contains(neigh) && pathCost.containsKey(neigh)) {
	    	    						double highVal = pathCost.get(current) * t.getWeight();
	    	    				//		double highVal = pathCost.get(current) * relativeWeight;
    									double currEnv2 = 1 + target.getInternalEnvironment(current);
    									double neighEnv = 1 + target.getExternalEnvironment(neigh);
    									if (seeds.contains(current)) {currEnv2 = currEnv2 +target.getConceptInternal(inhibiting);}
    									if (neighIsSeed){neighEnv = neighEnv + target.getConceptExternal(inhibiting);}
    									
    									highVal = highVal * currEnv2 * neighEnv;  
	    	    						
	    	    						for (TypedWeightedEdge tt : g.getInEdges(neigh)) {
	    	    							if (tt.getConcept() == target.getId()) {
	    	    								MultiProbabilityNode neighNeigh = g.getOpposite(neigh, tt);
	    	    								if (!neighNeigh.equals(current) && !neighNeigh.equals(tNeigh) && pathCost.containsKey(neighNeigh)) {
	    	    									if (pathCost.get(neighNeigh) > pathCost.get(neigh)) {
	    		    									double pVal = pathCost.get(neighNeigh) * tt.getWeight();
	    		    									//double pVal = pathCost.get(neighNeigh) * pWeight;
						    							double neighNeighEnv = 1 + target.getInternalEnvironment(neighNeigh);
						    								//	double neighEnv = 1 + target.getExternalEnvironment(neigh);
						    							if (seeds.contains(neighNeigh)) {neighNeighEnv = neighNeighEnv +target.getConceptInternal(inhibiting);}
						    								//	if (neighIsSeed){neighEnv = neighEnv + target.getConceptExternal(inhibiting);}
						    									
						    							pVal = pVal * neighNeighEnv * neighEnv;
	    		    									if (pVal > highVal) {
	    		    										highVal = pVal;
	    		    										ancestor = neighNeigh;
	    		    									}
	    	    									}
	    	    								}
	    	    							}
	    	    						}
	    	    						
	    	    	    				if (highVal != pathCost.get(neigh) && highVal >= thresh) {
	    	    	    					//expected.put(tNeigh, expected.get(tNeigh) - pathCost.get(neigh));
	    	    	    				//	pathCost.put(neigh, highVal);
	    	    	    					double apBoost = 0.0;
	    	    	    					if (neighIsSeed) {apBoost = 1;}
	    	    	    					else {
	    		    	    					for (TypedWeightedEdge bT : g.findEdgeSet(ancestor, neigh)) {
	    		    	    						if (t.getConcept() == inhibiting.getId()) {
	    		    	    							apBoost = boostProbMap.get(ancestor) * bT.getWeight();
	    		    	    						}
	    		    	    					}
	    		    							if (modProb.contains(neigh)){
	    		    								apBoost = apBoost * (1 + (inhibiting.getConceptExternal(modifying) * modProb.get(neigh)));
	    		    							}
	    		    							
	    		    	    					
	    	    	    					}
	    	    	    				//	changed.add(tNeigh);
	    	    	    					if (flip) {expected.put(tNeigh, expected.get(tNeigh) + ((pathCost.get(neigh) * boostProbMap.get(neigh) - pathCost.get(neigh))));}
	    	    	    					else {expected.put(tNeigh, expected.get(tNeigh) - ((pathCost.get(neigh) * boostProbMap.get(neigh) - pathCost.get(neigh))));}
	    	    	    					pathCost.put(neigh, highVal);
	    	    	    					apBoost = (1+ (apBoost * target.getConceptInternal(inhibiting)));
	    	    	    					boostProbMap.put(neigh, apBoost);
	    		    						nextDoorNodes.add(neigh);
	    	    	    					if (flip) {expected.put(tNeigh, expected.get(tNeigh) - ((pathCost.get(neigh) * boostProbMap.get(neigh) - pathCost.get(neigh))));}
	    	    	    					else {expected.put(tNeigh, expected.get(tNeigh) + ((pathCost.get(neigh) * boostProbMap.get(neigh) - pathCost.get(neigh))));}
	    	    	    				}
	    	    	    				else if (highVal < thresh) {
	    	    	    				//	changed.add(tNeigh);
	    	    	    					if (flip) {expected.put(tNeigh, expected.get(tNeigh) + ((pathCost.get(neigh) * boostProbMap.get(neigh) - pathCost.get(neigh))));}
	    	    	    					else {expected.put(tNeigh, expected.get(tNeigh) - ((pathCost.get(neigh) * boostProbMap.get(neigh) - pathCost.get(neigh))));}
	    	    	    					pathCost.remove(neigh);
	    	    	    					boostProbMap.remove(neigh);
	    		    						//nextDoorNodes.add(neigh);
	    		    						//expected.put(tNeigh, expected.get(tNeigh) + ((pathCost.get(neigh) * boostProbMap.get(neigh) - pathCost.get(neigh))));
	    	    	    				}
	    	    					}
	    	    					else if (!seenbefore.contains(neigh) && !neigh.isActivated(target) && !pathCost.containsKey(neigh)) {
	    	    						double highVal = pathCost.get(u) * t.getWeight();
    									double currEnv2 = 1 + target.getInternalEnvironment(current);
    									double neighEnv = 1 + target.getExternalEnvironment(neigh);
    									
	    	    						if (currentIsSeed) {highVal = highVal * (1 + target.getConceptExternal(inhibiting));}
	    	    						if (neighIsSeed){highVal = highVal * (1 + target.getConceptExternal(inhibiting));}
	    	    						

    									
	    	    						highVal = highVal * currEnv2 * neighEnv;
	    	    						if (highVal >= thresh){
	    		    						pathCost.put(neigh, highVal);
	    		    						//expected.put(tNeigh, expected.get(tNeigh) + pathCost.get(neigh));
	    		    						double apBoost=0.0;
	    	    	    					if (neighIsSeed) {apBoost = 1;}
	    	    	    					else {
	    		    	    					for (TypedWeightedEdge bT : g.findEdgeSet(ancestor, neigh)) {
	    		    	    						if (t.getConcept() == inhibiting.getId()) {
	    		    	    							apBoost = boostProbMap.get(ancestor) * bT.getWeight();
	    		    	    						}
	    		    	    					}
	    		    							if (modProb.contains(neigh)){
	    		    								apBoost = apBoost * (1 + (inhibiting.getConceptExternal(modifying) * modProb.get(neigh)));
	    		    							}
	    		    	    					
	    	    	    					}
	    	    	    				//	changed.add(tNeigh);
	    	    	    					//expected.put(tNeigh, expected.get(tNeigh) - ((pathCost.get(neigh) * boostProbMap.get(neigh) - pathCost.get(neigh))));
	    	    	    					pathCost.put(neigh, highVal);
	    	    	    					apBoost = (1+ (apBoost * target.getConceptInternal(inhibiting)));
	    	    	    					boostProbMap.put(neigh, apBoost);
	    		    						nextDoorNodes.add(neigh);
	    	    	    					if (flip) {expected.put(tNeigh, expected.get(tNeigh) - ((pathCost.get(neigh) * boostProbMap.get(neigh) - pathCost.get(neigh))));}
	    	    	    					else {expected.put(tNeigh, expected.get(tNeigh) + ((pathCost.get(neigh) * boostProbMap.get(neigh) - pathCost.get(neigh))));}
	    	    						}
	    	    					}
	    						}
	    	    					
	    					}
	    				}
	    				seenbefore.addAll(nextDoorNodesAgain);
	    				nextDoorNodes.clear();
	    				nextDoorNodes.addAll(nextDoorNodesAgain);
	    				nextDoorNodesAgain.clear();
	    			}
	    			
	    		}
	    		
	    	}	    	
	    	
	    	
	    	

	    	for (MultiProbabilityNode cu : changed) {
	    		cu.setValue(actProb.get(cu) * expected.get(cu));
	    	}
	    	
	    	changed.clear();
			sortedN.clear();
			
			actIter = actProb.keySet().iterator();
			for (MultiProbabilityNode ac : actProb.keySet()) {
				sortedN.add(ac);
			}
			if (!sortedN.isEmpty()) {
		    	u = sortedN.last();
		    	seeds.add(u);
		    	actProb.remove(u);
		    	mapOfPathCosts2.remove(u);
		    	expected.remove(u);
			}

	    	
	    }
		
		return seeds;
	}
	
	
	private static Set<MultiProbabilityNode> getIncomingNeighbours(Graph<MultiProbabilityNode, TypedWeightedEdge> g, MultiProbabilityNode current, double thresh, 
			HashSet<MultiProbabilityNode> seeds, Concept target, Concept boosting){
		
		TObjectDoubleHashMap<MultiProbabilityNode> pathCost = new TObjectDoubleHashMap<MultiProbabilityNode>();
		//HashMap<MultiProbabilityNode, TObjectDoubleHashMap<MultiProbabilityNode>> pathCost = new HashMap<MultiProbabilityNode, TObjectDoubleHashMap<MultiProbabilityNode>>();
		Collection<TypedWeightedEdge> currSet = g.getInEdges(current);
		HashSet<MultiProbabilityNode> neighbours = new HashSet<MultiProbabilityNode>();
		
		boolean currBoost = seeds.contains(current);
		double currEnv = 0;
		if (currBoost) {
			currEnv = 1 + target.getInternalEnvironment(current) + target.getConceptInternal(boosting);
		}
		else {
			currEnv = 1 + target.getInternalEnvironment(current);
		}
		
		for (TypedWeightedEdge twe : currSet) {
			if (twe.getConcept() == target.getId()) {
				MultiProbabilityNode neigh = g.getOpposite(current, twe);
				double contextCost = 0.0;
				if (seeds.contains(neigh)) {
					contextCost = twe.getWeight() * (currEnv) * (1 + target.getExternalEnvironment(neigh) + target.getConceptExternal(boosting));
				} 
				else {
					contextCost = twe.getWeight() * (currEnv) * (1 + target.getExternalEnvironment(neigh));
				}
				if (contextCost >= thresh && !neigh.isActivated(target)) {
					if (pathCost.containsKey(neigh)) {
						double cost = contextCost;
						//if (seeds.contains(neigh)) {cost = cost * (1 + target.getConceptInternal(boosting));}
					//	if (currBoost) {cost = cost * (1 + target.getConceptExternal(boosting));}
						
						if (cost > pathCost.get(neigh)) {
							pathCost.put(neigh, cost);
						}
						
					}
					else {
						double cost = contextCost;
						//if (seeds.contains(neigh)) {cost = cost * (1 + target.getConceptInternal(boosting));}
						//if (currBoost) {cost = cost * (1 + target.getConceptExternal(boosting));}
						pathCost.put(neigh, cost);
					}
				}
				
				neighbours.add(neigh);
			}
		}
		
		HashSet<MultiProbabilityNode> seenBefore = new HashSet<MultiProbabilityNode>();
		seenBefore.addAll(neighbours);
		HashSet<MultiProbabilityNode> nextConsider = new HashSet<MultiProbabilityNode>();
		
		while (!neighbours.isEmpty()) {

			for (MultiProbabilityNode currentNeighbour : neighbours) {

				boolean currNeighBoost = seeds.contains(currentNeighbour);
				double currNeighEnv = 0;
				if (currNeighBoost) {
					currNeighEnv = 1 + target.getInternalEnvironment(currentNeighbour) + target.getConceptInternal(boosting);
				}
				else {
					currNeighEnv = 1 + target.getInternalEnvironment(currentNeighbour);
				}
				
				for(TypedWeightedEdge outgoingEdge : g.getInEdges(currentNeighbour)) {
					if (outgoingEdge.getConcept() == target.getId()) {
						MultiProbabilityNode s = g.getOpposite(currentNeighbour, outgoingEdge);
						
						if (!s.isActivated(target)) {
							double conCost = 0.0;
							
							if (seeds.contains(s)) {
								conCost = outgoingEdge.getWeight() * (currNeighEnv) * (1 + target.getExternalEnvironment(s) + target.getConceptExternal(boosting));
							}
							else {
								conCost = outgoingEdge.getWeight() * (currNeighEnv) * (1 + target.getExternalEnvironment(s));
							}
							double sCost = conCost * pathCost.get(currentNeighbour);
						//	if (currNeighBoost) {sCost = sCost * (1 + target.getConceptExternal(boosting));}
						//	if (seeds.contains(s)) {sCost = sCost * (1+target.getConceptInternal(boosting));}
							if (sCost >= thresh){
								if (!pathCost.containsKey(s)){
									pathCost.put(s, sCost);
									nextConsider.add(s);
								}
								else {
									if (sCost > pathCost.get(s)) {
										pathCost.put(s, sCost);
										nextConsider.add(s);
									}
								}
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
		//System.out.println(current.getId());
		Collection<TypedWeightedEdge> currSet = g.getOutEdges(current);
		HashSet<MultiProbabilityNode> neighbours = new HashSet<MultiProbabilityNode>();

		double currEnv = 1 + target.getInternalEnvironment(current);
		for (TypedWeightedEdge twe : currSet) {
			if (twe.getConcept() == target.getId()) {
				MultiProbabilityNode neigh = g.getOpposite(current,twe);
				double contextCost = 0;
				
				if (seeds.contains(neigh)) {
					contextCost = twe.getWeight() * (currEnv) * (1 + target.getExternalEnvironment(neigh) + target.getConceptExternal(boosting));
				} 
				else {
					contextCost = twe.getWeight() * (currEnv) * (1 + target.getExternalEnvironment(neigh));
				}
				if (contextCost >= thresh && !neigh.isActivated(target)) {
					if (pathCost.containsKey(neigh)) {
						double cost = contextCost;
					//	if (seeds.contains(neigh)) {cost = cost * (1+target.getConceptExternal(boosting));}
						if (cost > pathCost.get(neigh)) {
							pathCost.put(neigh, cost);
						}
						
					}
					else {
						double cost = contextCost;
					//	if (seeds.contains(neigh)) {cost = cost * (1+target.getConceptExternal(boosting));}
						pathCost.put(neigh,cost);
					}
					neighbours.add(neigh);
				}
			}
		}
		
		HashSet<MultiProbabilityNode> nextConsider = new HashSet<MultiProbabilityNode>();
		
		//System.out.println("Neighbours");
		while (!neighbours.isEmpty()) {

			for (MultiProbabilityNode currentNeighbour : neighbours) {
				
				boolean currNeighBoost = seeds.contains(currentNeighbour);
				double currNeighEnv = 0;
				if (currNeighBoost) {
					currNeighEnv = 1 + target.getInternalEnvironment(currentNeighbour) + target.getConceptInternal(boosting);
				}
				else {
					currNeighEnv = 1 + target.getInternalEnvironment(currentNeighbour);
				}
				
				for(TypedWeightedEdge outgoingEdge : g.getOutEdges(currentNeighbour)) {
					if (outgoingEdge.getConcept() == target.getId()) {
						MultiProbabilityNode s = g.getOpposite(currentNeighbour, outgoingEdge);
						if (!s.isActivated(target)) {
							double conCost = 0.0;
							
							if (seeds.contains(s)) {
								conCost = outgoingEdge.getWeight() * (currNeighEnv) * (1 + target.getExternalEnvironment(s) + target.getConceptExternal(boosting));
							}
							else {
								conCost = outgoingEdge.getWeight()  * (currNeighEnv) * (1 + target.getExternalEnvironment(s));
							}
							
							double sCost = conCost * pathCost.get(currentNeighbour);
						//	if (currNeighBoost) {sCost = sCost * (1 + target.getConceptInternal(boosting));}
						//	if (seeds.contains(s)) {sCost = sCost * (1+target.getConceptExternal(boosting));}
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
			}
			neighbours.clear();
			neighbours.addAll(nextConsider);
			nextConsider.clear();
		}
		
		return pathCost;
	}
	
	private static ArrayList<TObjectDoubleHashMap<MultiProbabilityNode>> getPathCostDouble(Graph<MultiProbabilityNode, TypedWeightedEdge> g, MultiProbabilityNode current, double thresh, 
			HashSet<MultiProbabilityNode> seeds, Concept target, Concept boosting, Concept modifying, TObjectDoubleHashMap<MultiProbabilityNode> modProb) {
		
		TObjectDoubleHashMap<MultiProbabilityNode> pathCost = new TObjectDoubleHashMap<MultiProbabilityNode>();
	//	TObjectDoubleHashMap<MultiProbabilityNode> pathCostMod = new TObjectDoubleHashMap<MultiProbabilityNode>();
		TObjectDoubleHashMap<MultiProbabilityNode> boostProbMap = new TObjectDoubleHashMap<MultiProbabilityNode>();
		//System.out.println(current.getId());
		Collection<TypedWeightedEdge> currSet = g.getOutEdges(current);
		HashSet<MultiProbabilityNode> neighbours = new HashSet<MultiProbabilityNode>();

		
	//	int i = 0;
		double currEnv = 1 + target.getInternalEnvironment(current);
		//double modEnv = 1 + boosting.getInternalEnvironment(current);
		double baseBoost = 0;
		for (TypedWeightedEdge twe : currSet) {
			if (twe.getConcept() == target.getId()) {
				MultiProbabilityNode neigh = g.getOpposite(current,twe);
				
				for (TypedWeightedEdge t : g.findEdgeSet(current, neigh)) {
					if (t.getConcept() == boosting.getId()) {
						baseBoost = t.getWeight();
					}
				}
				
				double contextCost = 0.0;
				
				if (seeds.contains(neigh)) {
					contextCost = twe.getWeight() * (currEnv) * (1 + target.getExternalEnvironment(neigh) + target.getConceptExternal(boosting));
				}
				else {
					contextCost = twe.getWeight() * (currEnv) * (1 + target.getExternalEnvironment(neigh));
				}
				double apBoost = 0;
				if (contextCost >= thresh && !neigh.isActivated(target)) {
					if (pathCost.containsKey(neigh)) {
						double cost = contextCost;
						if (seeds.contains(neigh)) {
							apBoost = 1;
						}
						else {
							if (modProb.contains(neigh)){
								apBoost = baseBoost * (1 + (boosting.getConceptExternal(modifying) * modProb.get(neigh)));
							}
							else {
								apBoost = baseBoost;
							}
						}

						if (cost > pathCost.get(neigh)) {
							pathCost.put(neigh, pathCost.get(neigh)+cost);
							apBoost = (1+ (apBoost * target.getConceptInternal(boosting)));
							boostProbMap.put(neigh, apBoost);
						//	apBoost = apBoost * target.getConceptInternal(boosting);
						//	pathCostMod.put(neigh, (apBoost * cost));
						}
						
						
					}
					else {
						double cost = contextCost;
						if (seeds.contains(neigh)) {
						//	cost = cost * (1+target.getConceptExternal(boosting));
							apBoost = 1;
						}
						else {
							if (modProb.contains(neigh)){
								apBoost = baseBoost * (1 + (boosting.getConceptExternal(modifying) * modProb.get(neigh)));
							}
							else {
								apBoost = baseBoost;
							}
						}
						pathCost.put(neigh,cost);
						apBoost = (1+ (apBoost * target.getConceptInternal(boosting)));
						boostProbMap.put(neigh, apBoost);

						//pathCostMod.put(neigh, (apBoost * cost));
					}
					neighbours.add(neigh);
				}
			}
		}
		
		HashSet<MultiProbabilityNode> nextConsider = new HashSet<MultiProbabilityNode>();
		//double currBoost = 0;
		//System.out.println("Neighbours");
		while (!neighbours.isEmpty()) {
			//currBoost = baseBoost * currBoost;
			for (MultiProbabilityNode currentNeighbour : neighbours) {
				
				boolean currNeighBoost = seeds.contains(currentNeighbour);
				double currNeighEnv = 0.0;
				
				if (currNeighBoost) {
					currNeighEnv = 1 + target.getInternalEnvironment(currentNeighbour) + target.getConceptInternal(boosting);
				}
				else {
					currNeighEnv = 1 + target.getInternalEnvironment(currentNeighbour);
				}
//				
//				if (currNeighBoost) {
//					currNeighBoostChance = boostProbMap.get(currentNeighbour);
//				}
//				else {
//					currNeighBoostChance = ;
//				}
//				
//				if (modProb.contains(currentNeighbour)){
//					currNeighBoostChance = currNeighBoostChance * (1 + (boosting.getConceptInternal(modifying) * modProb.get(currentNeighbour)));
//				}
				
				for(TypedWeightedEdge outgoingEdge : g.getOutEdges(currentNeighbour)) {
					if (outgoingEdge.getConcept() == target.getId()){
						double currNeighBoostChance = 0;
						MultiProbabilityNode s = g.getOpposite(currentNeighbour, outgoingEdge);
						
						if (!seeds.contains(s)){
							for (TypedWeightedEdge t : g.findEdgeSet(currentNeighbour,s)) {
								if (t.getConcept() == boosting.getId()) {
									currNeighBoostChance = boostProbMap.get(currentNeighbour) * t.getWeight();
									if (modProb.contains(currentNeighbour)){
										currNeighBoostChance = currNeighBoostChance * (1 + (boosting.getConceptInternal(modifying) * modProb.get(currentNeighbour)));
									}
								}
							}
						}
						
						if (!s.isActivated(target)) {
							//double conCost = outgoingEdge.getWeight() * (currNeighEnv) * (1 + target.getExternalEnvironment(s));
							double apBoost = 0;
							double conCost = 0.0;
							
							if (seeds.contains(s)) {
								conCost = outgoingEdge.getWeight() * (currNeighEnv) * (1 + target.getExternalEnvironment(s) + target.getConceptExternal(boosting));
							}
							else {
								conCost = outgoingEdge.getWeight() * (currNeighEnv) * (1 + target.getExternalEnvironment(s));
							}
							double sCost = conCost * pathCost.get(currentNeighbour);
							
							if (seeds.contains(s)) {
							//	sCost = sCost * (1+target.getConceptExternal(boosting));
								apBoost = 1;
							}
							else {
								if (modProb.contains(s)){
									apBoost = currNeighBoostChance * (1 + (boosting.getConceptExternal(modifying) * modProb.get(s)));
								}
								else {
									apBoost = currNeighBoostChance;
								}
							}
							
							if (sCost >= thresh){
								
								
								if (pathCost.containsKey(s)){
									if (pathCost.get(s) < sCost) {
										pathCost.put(s, sCost);
										apBoost = (1+ (apBoost * target.getConceptInternal(boosting)));
										boostProbMap.put(s, apBoost);
									//	apBoost = apBoost * target.getConceptInternal(boosting);
									//	pathCostMod.put(s, (apBoost * sCost));
										nextConsider.add(s);
									}
								}
								else {
									pathCost.put(s, sCost);
									apBoost = (1+ (apBoost * target.getConceptInternal(boosting)));
									boostProbMap.put(s, apBoost);
								//	apBoost = apBoost * target.getConceptInternal(boosting);
								//	pathCostMod.put(s, (apBoost * sCost));
									nextConsider.add(s);
								}
							}	
						}
					}
				}
			}
			neighbours.clear();
			neighbours.addAll(nextConsider);
			nextConsider.clear();
		}
		ArrayList<TObjectDoubleHashMap<MultiProbabilityNode>> pathList = new ArrayList<TObjectDoubleHashMap<MultiProbabilityNode>>();
		pathList.add(pathCost);
	//	pathList.add(pathCostMod);
		pathList.add(boostProbMap);
		return pathList;
	}
	
	private static TObjectDoubleHashMap<MultiProbabilityNode> getPathCostMap2(Graph<MultiProbabilityNode, TypedWeightedEdge> g, MultiProbabilityNode current, double thresh, 
			HashSet<MultiProbabilityNode> seeds, Concept target) {
		
		TObjectDoubleHashMap<MultiProbabilityNode> pathCost = new TObjectDoubleHashMap<MultiProbabilityNode>();
		//System.out.println(current.getId());
		Collection<TypedWeightedEdge> currSet = g.getOutEdges(current);
		HashSet<MultiProbabilityNode> neighbours = new HashSet<MultiProbabilityNode>();

		double currEnv = 1 + target.getInternalEnvironment(current);
		for (TypedWeightedEdge twe : currSet) {
			if (twe.getConcept() == target.getId()) {
				MultiProbabilityNode neigh = g.getOpposite(current,twe);
				double contextCost = twe.getWeight() * (currEnv) * (1 + target.getExternalEnvironment(neigh));
				if (contextCost >= thresh && !neigh.isActivated(target)) {
					if (pathCost.containsKey(neigh)) {
						double cost = contextCost;
						if (cost > pathCost.get(neigh)) {
							pathCost.put(neigh, cost);
						}
						
					}
					else {
						double cost = contextCost;
						pathCost.put(neigh,cost);
					}
					neighbours.add(neigh);
				}
			}

		}
		
		HashSet<MultiProbabilityNode> nextConsider = new HashSet<MultiProbabilityNode>();
		
		//System.out.println("Neighbours");
		while (!neighbours.isEmpty()) {

			for (MultiProbabilityNode currentNeighbour : neighbours) {
				
				double currNeighEnv = 1 + target.getInternalEnvironment(currentNeighbour);
				
				for(TypedWeightedEdge outgoingEdge : g.getOutEdges(currentNeighbour)) {
					if (outgoingEdge.getConcept() == target.getId()) {
						MultiProbabilityNode s = g.getOpposite(currentNeighbour, outgoingEdge);
						if (!s.isActivated(target)) {
							double conCost = outgoingEdge.getWeight() * (currNeighEnv) * (1 + target.getExternalEnvironment(s));
							double sCost = conCost * pathCost.get(currentNeighbour);
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
			}
			neighbours.clear();
			neighbours.addAll(nextConsider);
			nextConsider.clear();
		}
		
		return pathCost;
	}

}
