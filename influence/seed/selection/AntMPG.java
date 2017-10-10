package influence.seed.selection;

import gnu.trove.map.hash.TObjectDoubleHashMap;
import influence.concepts.Concept;
import influence.concepts.DiverseICConcept;
import influence.concepts.DiverseLTConcept;
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

import edu.uci.ics.jung.graph.Graph;
//Version of MPG that accounts for antagonistic concepts
public class AntMPG {
	public static HashSet<MultiProbabilityNode> getSeeds
	(Graph<MultiProbabilityNode, TypedWeightedEdge> g, int seedCount, double thresh, Concept target, Concept inhibiting, HashSet<MultiProbabilityNode> seedsTarget, Concept antagonist) {
		HashSet<MultiProbabilityNode> seeds = new HashSet<MultiProbabilityNode>();


		HashMap<MultiProbabilityNode, TObjectDoubleHashMap<MultiProbabilityNode>> mapOfPathCosts = new HashMap<MultiProbabilityNode, TObjectDoubleHashMap<MultiProbabilityNode>>();
		HashMap<MultiProbabilityNode, HashSet<MultiProbabilityNode>> seedReachable = new HashMap<MultiProbabilityNode, HashSet<MultiProbabilityNode>>();
		TObjectDoubleHashMap<MultiProbabilityNode> actProb = new TObjectDoubleHashMap<MultiProbabilityNode>();

		Collection<MultiProbabilityNode> nodesAll = null;
		HashMap<MultiProbabilityNode, TObjectDoubleHashMap<MultiProbabilityNode>> mapOfPathCosts2 = new HashMap<MultiProbabilityNode, TObjectDoubleHashMap<MultiProbabilityNode>>();
		TObjectDoubleHashMap<MultiProbabilityNode> antProb = new TObjectDoubleHashMap<MultiProbabilityNode>();
		
		//find nodes currently capable of spreading the target concept
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
		//first find the nodes reachable by each node capable of spreadin target concept
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
				mapOfPathCosts.put(curr, pathCost);//store those paths and costs
			}
		}
		
		System.out.println("act done in " + (System.currentTimeMillis() - start) + "ms.");
	//	System.out.println(actProb.size());
		int i = 0;
		//HashMap<MultiProbabilityNode, Double> expected = new HashMap<MultiProbabilityNode, Double>();
		TObjectDoubleHashMap<MultiProbabilityNode> expected = new TObjectDoubleHashMap<MultiProbabilityNode>();
		Iterator<MultiProbabilityNode> actIter = actProb.keySet().iterator();
		
		//find if antagonistic concept exists
		if (antagonist != null) {
			while (actIter.hasNext()) {
				//find expected gain of activating the controllable concept on each node
				MultiProbabilityNode current = actIter.next();
				System.out.println("ExpProb: " + (i++)  + "/" + actProb.size());
				System.out.println("pathCost Start");
				start = System.currentTimeMillis();
				TObjectDoubleHashMap<MultiProbabilityNode> pathCost = getPathCostMap(g, current, thresh,seeds, target, inhibiting);
				System.out.println("pathCostEnd in " + (System.currentTimeMillis() - start) + "ms.");
				double total = 0;
				Iterator<MultiProbabilityNode> keyIter = pathCost.keySet().iterator();
				while (keyIter.hasNext()) {
					MultiProbabilityNode m = keyIter.next();
					total = total + pathCost.get(m);
				}
				
				expected.put(current, total);
				mapOfPathCosts2.put(current, pathCost);
				//weight expected gain by likelihood of interacting with the target concept
				current.setValue(actProb.get(current) * expected.get(current));
				
				System.out.println("antPath Start");
				start = System.currentTimeMillis();
				//now find chance of node activating the antagonistic concept
				double probAnt = getIncomingAnt(g, current, thresh, seeds, antagonist);
				System.out.println("antPath end in " + (System.currentTimeMillis() - start) + "ms.");
				//weight expected gain by the expected effect of the antagonistic concept
				double modify = (1-probAnt) * target.getConceptExternal(antagonist);
				antProb.put(current, modify);
				current.setValue(current.getValue() * modify);
			}
		}
		else {
			while (actIter.hasNext()) {
				MultiProbabilityNode current = actIter.next();
				System.out.println("ExpProb: " + (i++)  + "/" + actProb.size());
				System.out.println("pathCost Start");
				start = System.currentTimeMillis();
				TObjectDoubleHashMap<MultiProbabilityNode> pathCost = getPathCostMap(g, current, thresh,seeds, target, inhibiting);
				System.out.println("pathCostEnd in " + (System.currentTimeMillis() - start) + "ms.");
				double total = 0;
				Iterator<MultiProbabilityNode> keyIter = pathCost.keySet().iterator();
				while (keyIter.hasNext()) {
					MultiProbabilityNode m = keyIter.next();
					total = total + pathCost.get(m);
				}
				
				expected.put(current, total);
				mapOfPathCosts2.put(current, pathCost);
				current.setValue(actProb.get(current) * expected.get(current));
				
				System.out.println("antPath Start");
				start = System.currentTimeMillis();
				double probAnt = 0;//antagonistic concept is null, thus probabilitgy of activation is 0. 
				System.out.println("antPath end in " + (System.currentTimeMillis() - start) + "ms.");
				double modify = (1-probAnt) * target.getConceptExternal(antagonist);
				antProb.put(current, modify);
				current.setValue(current.getValue() * modify);
			}
		}
		

		System.out.println("exp done)");
		System.out.println(actProb.size());
		System.out.println(mapOfPathCosts2.size());
		Comparator<MultiProbabilityNode> comp = new MultiProbabilityComparator<MultiProbabilityNode>();
		SortedSet<MultiProbabilityNode> sortedN = new TreeSet<MultiProbabilityNode>(comp);
	   // sortedN.addAll(actProb.keySet());
	    
		actIter = actProb.keySet().iterator();
		for (MultiProbabilityNode ac : actProb.keySet()) {
			sortedN.add(ac);
		}
	    
	    //select node with highest weighted gain
    	MultiProbabilityNode u = sortedN.last();
    	seeds.add(u);
    	actProb.remove(u);
    	expected.remove(u);
    	mapOfPathCosts2.remove(u);
    	HashSet<MultiProbabilityNode> changed = new HashSet<MultiProbabilityNode>();
    	//update nodes
	    while (seeds.size() < seedCount && sortedN.size() > 0){
	    	System.out.println("Seeds: " + seeds.size());

	    	
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
	    						
	    						//double highVal = oldCost.get(u) * t.getWeight() * (1 + target.getConceptInternal(inhibiting));
	    						double highVal = 0.0;
	    					//	highVal = highVal * (1+target.getInternalEnvironment(u)) * (1+target.getExternalEnvironment(neigh));
	    						if (neighIsSeed){
	    							highVal = oldCost.get(u) * t.getWeight() * (1+target.getInternalEnvironment(u)+ target.getConceptInternal(inhibiting)) * (1+target.getExternalEnvironment(neigh)+ target.getConceptExternal(inhibiting));
	    						}
	    						else {
	    							highVal = oldCost.get(u) * t.getWeight() * (1+target.getInternalEnvironment(u)+ target.getConceptInternal(inhibiting)) * (1+target.getExternalEnvironment(neigh));
	    						}
	    						
	    					//	if (neighIsSeed){highVal = highVal * (1 + target.getConceptExternal(inhibiting));}
	    						
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
		    		    					antProb.remove(neigh);
		    		    					changed.remove(neigh);
		    	    					}
		    	    					else{
		    	    						changed.add(neigh);
		    	    					}
	    	    					}
	    	    					
	    	    				}
	    						
	    					}
	    					else if (!neigh.equals(tSeed) && !neigh.isActivated(target) && !oldCost.containsKey(neigh)) {
	    						//double highVal = oldCost.get(u) * t.getWeight() * (1 + target.getConceptInternal(inhibiting));
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
		    	    		    					antProb.remove(neigh);
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
	    	
	    	
	    	TObjectDoubleHashMap<MultiProbabilityNode> neighCostMap = getIncomingNeighbours(g, u, thresh, seeds, target, inhibiting);
	    	Set<MultiProbabilityNode> neighIter = neighCostMap.keySet();
	    	int kk = 0;
	    	//Iterate through each incoming node that can reach the current chosen seed node
	    	
		//	System.out.println(actProb.size());
		//	System.out.println(mapOfPathCosts2.size());
	    	for (MultiProbabilityNode tNeigh : neighIter) {
	    		//System.out.println("Seed " + seeds.size() + " " + (kk++)  + "/" + neighIter.size());
	    		//the reachable node has some chance of activating the target and isn't a target seed
	    		if (actProb.containsKey(tNeigh) && !seeds.contains(tNeigh)) {
	    			
	    			//we get the path costs, and use that to update the expected value of tNeigh, first by removing u's contribution
	    			TObjectDoubleHashMap<MultiProbabilityNode> pathCost = mapOfPathCosts2.get(tNeigh);
	    		//	System.out.println(pathCost.get(u));
	    		//	System.out.println(expected.get(tNeigh));
	    			expected.put(tNeigh, expected.get(tNeigh) - pathCost.get(u));
	    			double newVal = pathCost.get(u) + (pathCost.get(u) * target.getConceptExternal(inhibiting));
		    		pathCost.put(u, newVal);
		    		expected.put(tNeigh, expected.get(tNeigh) + pathCost.get(u));
	    			
	    			
	    			//adding u's new contribution

	    			
	    			//now need to explore u's neighbours and update their contributions
	    			HashSet<TypedWeightedEdge> nextDoor = new HashSet<TypedWeightedEdge>();
	    			HashSet<MultiProbabilityNode> nextDoorNodes = new HashSet<MultiProbabilityNode>();
	    			
	    			//get the neighbours first and explore their change contributions
	    			for (TypedWeightedEdge t : g.getOutEdges(u)){
	    				if (t.getConcept() == target.getId()) {
	    					MultiProbabilityNode neigh = g.getOpposite(u, t);
	    					boolean neighIsSeed = seeds.contains(neigh);
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
	    						//now explore incoming neighbours for neigh
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
		    									}
	    									}
	    								}
	    								
	    							}
	    						}
	    						//if highvalue is different from current path cost and still more than the threshold, we need to update the contribution and 
	    						//explore that node's neighbours
	    	    				if (highVal != pathCost.get(neigh) && highVal >= thresh) {
	    	    					expected.put(tNeigh, expected.get(tNeigh) - pathCost.get(neigh));
	    	    					pathCost.put(neigh, highVal);
	    	    					expected.put(tNeigh, expected.get(tNeigh) + pathCost.get(neigh));
	    	    					changed.add(tNeigh);
		    						nextDoorNodes.add(neigh);
	    	    				}
	    	    				else if (highVal < thresh) {
	    	    					expected.put(tNeigh, expected.get(tNeigh) - pathCost.get(neigh));
	    	    					pathCost.remove(neigh);
	    	    					changed.add(tNeigh);
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
		    						pathCost.put(neigh, highVal);
		    						expected.put(tNeigh, expected.get(tNeigh) + pathCost.get(neigh));
	    	    					changed.add(tNeigh);
		    						nextDoorNodes.add(neigh);
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
	    		    									}
	    	    									}
	    	    								}
	    	    							}
	    	    						}
	    	    						
	    	    	    				if (highVal != pathCost.get(neigh) && highVal >= thresh) {
	    	    	    					expected.put(tNeigh, expected.get(tNeigh) - pathCost.get(neigh));
	    	    	    					pathCost.put(neigh, highVal);
	    	    	    					expected.put(tNeigh, expected.get(tNeigh) + pathCost.get(neigh));
	    	    	    					changed.add(tNeigh);
	    		    						nextDoorNodesAgain.add(neigh);
	    	    	    				}
	    	    	    				else if (highVal < thresh) {
	    	    	    					expected.put(tNeigh, expected.get(tNeigh) - pathCost.get(neigh));
	    	    	    					pathCost.remove(neigh);
	    	    	    					changed.add(tNeigh);
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
	    		    						expected.put(tNeigh, expected.get(tNeigh) + pathCost.get(neigh));
	    	    	    					changed.add(tNeigh);
	    		    						nextDoorNodesAgain.add(neigh);
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
	    		cu.setValue(antProb.get(cu) * actProb.get(cu) * expected.get(cu));
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

	    	//System.out.println("Chosen: " + u.getId());
	    }
		
		return seeds;
	}
	
	//get the path costs of incoming neighbours to current node
	private static TObjectDoubleHashMap<MultiProbabilityNode> getIncomingNeighbours(Graph<MultiProbabilityNode, TypedWeightedEdge> g, MultiProbabilityNode current, double thresh, 
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
					//	if (seeds.contains(neigh)) {cost = cost * (1 + target.getConceptInternal(boosting));}
					//	if (currBoost) {cost = cost * (1 + target.getConceptExternal(boosting));}
						
						if (cost > pathCost.get(neigh)) {
							pathCost.put(neigh, cost);
						}
						
					}
					else {
						double cost = contextCost;
						if (seeds.contains(neigh)) {cost = cost * (1 + target.getConceptInternal(boosting));}
						if (currBoost) {cost = cost * (1 + target.getConceptExternal(boosting));}
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
						//	double conCost = outgoingEdge.getWeight() * (1 + target.getInternalEnvironment(s)) * (currNeighEnv);
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
		
		return pathCost;
	}
	
	//calculate influence related to antagonistic concept incoming into current node
	private static double getIncomingAnt(Graph<MultiProbabilityNode, TypedWeightedEdge> g, MultiProbabilityNode current, double thresh, 
			HashSet<MultiProbabilityNode> seeds, Concept antagonist){
		
		if (current.isActivated(antagonist)) {
			return 1;
		}
		
		TObjectDoubleHashMap<MultiProbabilityNode> pathCost = new TObjectDoubleHashMap<MultiProbabilityNode>();
		//HashMap<MultiProbabilityNode, TObjectDoubleHashMap<MultiProbabilityNode>> pathCost = new HashMap<MultiProbabilityNode, TObjectDoubleHashMap<MultiProbabilityNode>>();
		Collection<TypedWeightedEdge> currSet = g.getInEdges(current);
		HashSet<MultiProbabilityNode> neighbours = new HashSet<MultiProbabilityNode>();
		
		boolean currBoost = seeds.contains(current);
		//double currEnv = 1 + target.getExternalEnvironment(current);
		double totProb = 0;
		
		for (TypedWeightedEdge twe : currSet) {
			if (twe.getConcept() == antagonist.getId()) {
				MultiProbabilityNode neigh = g.getOpposite(current, twe);
				if (twe.getWeight() >= thresh) {
					if (neigh.isActivated(antagonist)){
						totProb += twe.getWeight();
					}
					else {
						pathCost.put(neigh, twe.getWeight());
						neighbours.add(neigh);
					}
				}
			}
		}
		
		HashSet<MultiProbabilityNode> seenBefore = new HashSet<MultiProbabilityNode>();
		seenBefore.addAll(neighbours);
		HashSet<MultiProbabilityNode> nextConsider = new HashSet<MultiProbabilityNode>();
		
		while (!neighbours.isEmpty()) {

			for (MultiProbabilityNode currentNeighbour : neighbours) {
				for(TypedWeightedEdge outgoingEdge : g.getInEdges(currentNeighbour)) {
					if (outgoingEdge.getConcept() == antagonist.getId()) {
						MultiProbabilityNode s = g.getOpposite(currentNeighbour, outgoingEdge);
						double sCost = outgoingEdge.getWeight() * pathCost.get(currentNeighbour);
						
						if (sCost >= thresh) {
							if (s.isActivated(antagonist)) {
								totProb += sCost;
							}
							else {
								if (!pathCost.containsKey(s)){
									pathCost.put(s, sCost);
									nextConsider.add(s);
								}
								else {
									if (sCost >= pathCost.get(s)) {
										pathCost.put(s, sCost);
										nextConsider.add(s);
									}
								}
							}
						}
					}
				}
			}
			//System.out.println(nextConsider.size());
			neighbours.clear();
			neighbours.addAll(nextConsider);
			nextConsider.clear();
			
		}
		
		return totProb;
	}
	
	//find reachable path costs for current node
	private static TObjectDoubleHashMap<MultiProbabilityNode> getPathCostMap(Graph<MultiProbabilityNode, TypedWeightedEdge> g, MultiProbabilityNode current, double thresh, 
			HashSet<MultiProbabilityNode> seeds, Concept target, Concept boosting) {
		
		TObjectDoubleHashMap<MultiProbabilityNode> pathCost = new TObjectDoubleHashMap<MultiProbabilityNode>();
		//System.out.println(current.getId());
		Collection<TypedWeightedEdge> currSet = g.getOutEdges(current);
		HashSet<MultiProbabilityNode> neighbours = new HashSet<MultiProbabilityNode>();

		int i = 0;
		double currEnv = 1 + target.getInternalEnvironment(current);
		for (TypedWeightedEdge twe : currSet) {
			if (twe.getConcept() == target.getId()) {
				MultiProbabilityNode neigh = g.getOpposite(current,twe);
			//double contextCost = twe.getWeight() * (currEnv) * (1 + target.getExternalEnvironment(neigh));
				
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
							//double conCost = outgoingEdge.getWeight() * (currNeighEnv) * (1 + target.getExternalEnvironment(s));
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
//Following methods adapt the above methods to fit in the LTM, which requires some small adjustments to account for the use of thresholds.
	public static HashSet<MultiProbabilityNode> getSeedsLT
		(Graph<MultiProbabilityNode, TypedWeightedEdge> g, int seedCount, double thresh, Concept target, Concept inhibiting, HashSet<MultiProbabilityNode> seedsTarget, Concept antagonist) {
			HashSet<MultiProbabilityNode> seeds = new HashSet<MultiProbabilityNode>();
			
			HashMap<MultiProbabilityNode, TObjectDoubleHashMap<MultiProbabilityNode>> mapOfPathCosts = new HashMap<MultiProbabilityNode, TObjectDoubleHashMap<MultiProbabilityNode>>();
			HashMap<MultiProbabilityNode, HashSet<MultiProbabilityNode>> seedReachable = new HashMap<MultiProbabilityNode, HashSet<MultiProbabilityNode>>();
			TObjectDoubleHashMap<MultiProbabilityNode> actProb = new TObjectDoubleHashMap<MultiProbabilityNode>();
			DiverseLTConcept target2 = (DiverseLTConcept) target;
			Collection<MultiProbabilityNode> nodesAll = null;
			nodesAll = seedsTarget;

			TObjectDoubleHashMap<MultiProbabilityNode> antProb = new TObjectDoubleHashMap<MultiProbabilityNode>();
			HashMap<MultiProbabilityNode, TObjectDoubleHashMap<MultiProbabilityNode>> mapOfPathCosts2 = new HashMap<MultiProbabilityNode, TObjectDoubleHashMap<MultiProbabilityNode>>();

			double infLimit;
			
			if (target.getConceptInternal(inhibiting) <= -1) {
				infLimit = Double.MAX_VALUE;
			}
			else if (target.getConceptInternal(inhibiting) > 0) {
				infLimit = 1.0;
			}
			else {
				infLimit = 1.0 / (1.0 + target.getConceptInternal(inhibiting));
			}
			int ii = 0;
			for (MultiProbabilityNode curr : nodesAll) {
				System.out.println("Checking " + (ii++) + " out of " + nodesAll.size());
				if (curr.isActivated(target)){
					TObjectDoubleHashMap<MultiProbabilityNode> pathCost = getPathCostMapLT(g, curr, thresh, seeds, target, inhibiting, infLimit);
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
			
			int i = 0;
			ii = 0;
			TObjectDoubleHashMap<MultiProbabilityNode> expected = new TObjectDoubleHashMap<MultiProbabilityNode>();
			Iterator<MultiProbabilityNode> actIter = actProb.keySet().iterator();
			while (actIter.hasNext()) {
				MultiProbabilityNode current = actIter.next();
				System.out.println("Checking " + (ii++) + " out of " + actProb.size());
				TObjectDoubleHashMap<MultiProbabilityNode> pathCost = getPathCostMapLT(g, current, thresh,seeds, target, inhibiting, infLimit);
				double total = 0;
				Iterator<MultiProbabilityNode> keyIter = pathCost.keySet().iterator();
				while (keyIter.hasNext()) {
					MultiProbabilityNode m = keyIter.next();
					total = total + pathCost.get(m);
				}
				
				expected.put(current, total);
				
				current.setValue(actProb.get(current) * expected.get(current));
				
				double probAnt = getIncomingAntLT(g, current, thresh, seeds, antagonist, infLimit);
				double modify = (1-probAnt) * target.getConceptExternal(antagonist);
				mapOfPathCosts2.put(current, pathCost);
				antProb.put(current, modify);
				current.setValue(current.getValue() * modify);
			}
			
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
	    	mapOfPathCosts.remove(u);
	    	antProb.remove(u);
	    	expected.remove(u);
	    	HashSet<MultiProbabilityNode> changed = new HashSet<MultiProbabilityNode>();
	    	
	    	 while (seeds.size() < seedCount && sortedN.size() > 0){
	    		 System.out.println("Seed " + seeds.size());

//	 	    	
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
	    					double neighThresh = neigh.getAttribute(target.getId());
	    					
	    					if (actProb.containsKey(neigh)) {
		    					if (!neigh.equals(tSeed) && oldCost.containsKey(neigh)) {
		    						
		    						double relativeWeight = t.getWeight() / neighThresh;
		    						
		    						//double highVal = oldCost.get(u) * relativeWeight * (1 + target.getConceptInternal(inhibiting));
		    						double highVal = 0.0;
		    					//	highVal = highVal * (1+target.getInternalEnvironment(u)) * (1+target.getExternalEnvironment(neigh));
		    						if (neighIsSeed){
		    							highVal = oldCost.get(u) * Math.min(1.0, relativeWeight * (1+target.getInternalEnvironment(u)+ target.getConceptInternal(inhibiting)) * (1+target.getExternalEnvironment(neigh)+ target.getConceptExternal(inhibiting)));
		    						}
		    						else {
		    							highVal = oldCost.get(u) * Math.min(1.0, relativeWeight * (1+target.getInternalEnvironment(u)+ target.getConceptInternal(inhibiting)) * (1+target.getExternalEnvironment(neigh)));
		    						}
		    						
		    					//	highVal = Math.min(1,highVal);
		    						for (TypedWeightedEdge tt : g.getInEdges(neigh)) {
		    							if (tt.getConcept() == target.getId()) {
		    								MultiProbabilityNode neighNeigh = g.getOpposite(neigh, tt);
		    								
		    								if (!neighNeigh.equals(u) && !neighNeigh.equals(tSeed) && oldCost.containsKey(neighNeigh)) {
		    									if (oldCost.get(neighNeigh) > oldCost.get(neigh)) {
		    										double pWeight = tt.getWeight() / neighThresh;
			    									double pVal = oldCost.get(neighNeigh);
			    											    									
			    									double neighNeighEnv = 1 + target.getInternalEnvironment(neighNeigh);
			    									double neighEnv = 1 + target.getExternalEnvironment(neigh);
			    									if (seeds.contains(neighNeigh)) {neighNeighEnv = neighNeighEnv +target.getConceptInternal(inhibiting);}
			    									if (neighIsSeed){neighEnv = neighEnv + target.getConceptExternal(inhibiting);}
			    									
			    									pVal = pVal * Math.min(1.0, pWeight * neighNeighEnv * neighEnv);
			    									//pVal = Math.min(1,pVal);
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
			    		    					antProb.remove(neigh);
			    		    					changed.remove(neigh);
			    	    					}
			    	    					else{
			    	    						changed.add(neigh);
			    	    					}
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
		    	    					double neighThresh = neigh.getAttribute(target.getId());
		    	    					
		    	    					if (!seenbefore.contains(neigh) && oldCost.containsKey(neigh)) {
		    	    						double relativeWeight = t.getWeight() / neighThresh;
		    	    						
		    	    						double highVal = oldCost.get(current);
		    	    						
	    									double currEnv2 = 1 + target.getInternalEnvironment(current);
	    									double neighEnv = 1 + target.getExternalEnvironment(neigh);
	    									if (seeds.contains(current)) {currEnv2 = currEnv2 +target.getConceptInternal(inhibiting);}
	    									if (neighIsSeed){neighEnv = neighEnv + target.getConceptExternal(inhibiting);}
	    									
	    									highVal = highVal * Math.min(1.0, relativeWeight * currEnv2 * neighEnv);  	    						
		    	    								    	    						
		    	    						highVal = Math.min(1.0, highVal);
		    	    						for (TypedWeightedEdge tt : g.getInEdges(neigh)) {
		    	    							if (tt.getConcept() == target.getId()) {
		    	    								MultiProbabilityNode neighNeigh = g.getOpposite(neigh, tt);
		    	    								
		    	    								if (!neighNeigh.equals(current) && !neighNeigh.equals(tSeed) && oldCost.containsKey(neighNeigh)) {
		    	    									if (oldCost.get(neighNeigh) > oldCost.get(neigh)) {
		    	    										double pWeight = tt.getWeight() / neighThresh;
		    		    									double pVal = oldCost.get(neighNeigh);
					    									double neighNeighEnv = 1 + target.getInternalEnvironment(neighNeigh);
					    								//	double neighEnv = 1 + target.getExternalEnvironment(neigh);
					    									if (seeds.contains(neighNeigh)) {neighNeighEnv = neighNeighEnv +target.getConceptInternal(inhibiting);}
					    								//	if (neighIsSeed){neighEnv = neighEnv + target.getConceptExternal(inhibiting);}
					    									
					    									pVal = pVal * Math.min(1.0, pWeight * neighNeighEnv * neighEnv);
		    		    									//pVal = Math.min(1.0,pVal);
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
			    	    		    					antProb.remove(neigh);
			    	    		    					changed.remove(neigh);
			    	    	    					}
			    	    	    					else{
			    	    	    						changed.add(neigh);
			    	    	    					}
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
	    		
//	 	    	
	 	    	Set<MultiProbabilityNode> neighIter = getIncomingNeighboursLT(g, u, thresh, seeds, target, inhibiting, infLimit);
		    	// = neighCostMap.keySet();
		    	int kk = 0;
		    	//Iterate through each incoming node that can reach the current chosen seed node
		    	
				//System.out.println(actProb.size());
				//System.out.println(mapOfPathCosts2.size());
		    	for (MultiProbabilityNode tNeigh : neighIter) {
		    		//System.out.println("Seed " + seeds.size() + " " + (kk++)  + "/" + neighIter.size());
		    		//the reachable node has some chance of activating the target and isn't a target seed
		    		if (actProb.containsKey(tNeigh) && !seeds.contains(tNeigh)) {
		    			
		    			//we get the path costs, and use that to update the expected value of tNeigh, first by removing u's contribution
		    			TObjectDoubleHashMap<MultiProbabilityNode> pathCost = mapOfPathCosts2.get(tNeigh);
		    		//	System.out.println(pathCost.get(u));
		    		//	System.out.println(expected.get(tNeigh));
		    			expected.put(tNeigh, expected.get(tNeigh) - pathCost.get(u));
			    		double newVal = pathCost.get(u) + (pathCost.get(u) * target.getConceptExternal(inhibiting));
			    		//newVal = Math.min(1.0, newVal);
			    		pathCost.put(u, newVal);
			    		expected.put(tNeigh, expected.get(tNeigh) + pathCost.get(u));
		    			
		    			
		    			//adding u's new contribution

		    			
		    			//now need to explore u's neighbours and update their contributions
		    			HashSet<TypedWeightedEdge> nextDoor = new HashSet<TypedWeightedEdge>();
		    			HashSet<MultiProbabilityNode> nextDoorNodes = new HashSet<MultiProbabilityNode>();
		    			
		    			//get the neighbours first and explore their change contributions
		    			for (TypedWeightedEdge t : g.getOutEdges(u)){
		    				if (t.getConcept() == target.getId()) {
		    					
		    					MultiProbabilityNode neigh = g.getOpposite(u, t);
		    					boolean neighIsSeed = seeds.contains(neigh);
		    					double neighThresh = neigh.getAttribute(target.getId());
		    					if (!neigh.equals(tNeigh) && pathCost.containsKey(neigh)) {
		    						double relativeWeight = t.getWeight() / neighThresh;
		    						double highVal = 0.0;
		    					//	highVal = highVal * (1+target.getInternalEnvironment(u)) * (1+target.getExternalEnvironment(neigh));
		    						if (neighIsSeed){
		    							highVal = pathCost.get(u) * Math.min(1.0, relativeWeight * (1+target.getInternalEnvironment(u)+ target.getConceptInternal(inhibiting)) * (1+target.getExternalEnvironment(neigh)+ target.getConceptExternal(inhibiting)));
		    						}
		    						else {
		    							highVal = pathCost.get(u) * Math.min(1.0, relativeWeight * (1+target.getInternalEnvironment(u)+ target.getConceptInternal(inhibiting)) * (1+target.getExternalEnvironment(neigh)));
		    						}
		    						
		    						
		    						//now explore incoming neighbours for neigh
		    						for (TypedWeightedEdge tt : g.getInEdges(neigh)) {
		    							if (tt.getConcept() == target.getId()) {
		    								
		    								MultiProbabilityNode neighNeigh = g.getOpposite(neigh, tt);
		    								
		    								//check neighbour is reachable by tneigh and is not tneigh or the chosen seed
		    								if (!neighNeigh.equals(u) && !neighNeigh.equals(tNeigh) && pathCost.containsKey(neighNeigh)) {
		    									
		    									//if neighneighs path cost is higher, it means it has a shorter path that does not go through s
		    									if (pathCost.get(neighNeigh) > pathCost.get(neigh)) {
		    										double pWeight = tt.getWeight() / neighThresh;
			    									double pVal = pathCost.get(neighNeigh);
			    									
			    									
			    									double neighNeighEnv = 1 + target.getInternalEnvironment(neighNeigh);
			    									double neighEnv = 1 + target.getExternalEnvironment(neigh);
			    									if (seeds.contains(neighNeigh)) {neighNeighEnv = neighNeighEnv +target.getConceptInternal(inhibiting);}
			    									if (neighIsSeed){neighEnv = neighEnv + target.getConceptExternal(inhibiting);}
			    									
			    									pVal = pVal * Math.min(1.0,  pWeight * neighNeighEnv * neighEnv);
			    									
			    									if (pVal > highVal) {
			    										highVal = pVal;
			    									}
			    									
		    									}
		    								}
		    								
		    							}
		    						}
		    						//if highvalue is different from current path cost and still more than the threshold, we need to update the contribution and 
		    						//explore that node's neighbours
		    	    				if (highVal != pathCost.get(neigh) && highVal >= thresh) {
		    	    					expected.put(tNeigh, expected.get(tNeigh) - pathCost.get(neigh));
		    	    					pathCost.put(neigh, highVal);
		    	    					expected.put(tNeigh, expected.get(tNeigh) + pathCost.get(neigh));
		    	    					changed.add(tNeigh);
			    						nextDoorNodes.add(neigh);
		    	    				}
		    	    				else if (highVal < thresh) {
		    	    					expected.put(tNeigh, expected.get(tNeigh) - pathCost.get(neigh));
		    	    					pathCost.remove(neigh);
		    	    					changed.add(tNeigh);
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
		    	    					double neighThresh = neigh.getAttribute(target.getId());
		    	    					
		    	    					if (!seenbefore.contains(neigh) && pathCost.containsKey(neigh)) {
		    	    						double relativeWeight = t.getWeight() / neighThresh;
		    	    						double highVal = pathCost.get(current);
	    									double currEnv2 = 1 + target.getInternalEnvironment(current);
	    									double neighEnv = 1 + target.getExternalEnvironment(neigh);
	    									if (seeds.contains(current)) {currEnv2 = currEnv2 +target.getConceptInternal(inhibiting);}
	    									if (neighIsSeed){neighEnv = neighEnv + target.getConceptExternal(inhibiting);}
	    									
	    									highVal = highVal * Math.min(1.0, relativeWeight * currEnv2 * neighEnv);  
		    	    						
		    	    						for (TypedWeightedEdge tt : g.getInEdges(neigh)) {
		    	    							if (tt.getConcept() == target.getId()) {
		    	    								
		    	    								MultiProbabilityNode neighNeigh = g.getOpposite(neigh, tt);
		    	    								
		    	    								if (!neighNeigh.equals(current) && !neighNeigh.equals(tNeigh) && pathCost.containsKey(neighNeigh)) {
		    	    									
		    	    									if (pathCost.get(neighNeigh) > pathCost.get(neigh)) {
		    	    										double pWeight = tt.getWeight() / neighThresh;
		    		    									double pVal = pathCost.get(neighNeigh) * pWeight;
					    									double neighNeighEnv = 1 + target.getInternalEnvironment(neighNeigh);
					    								//	double neighEnv = 1 + target.getExternalEnvironment(neigh);
					    									if (seeds.contains(neighNeigh)) {neighNeighEnv = neighNeighEnv +target.getConceptInternal(inhibiting);}
					    								//	if (neighIsSeed){neighEnv = neighEnv + target.getConceptExternal(inhibiting);}
					    									
					    									pVal = pVal * Math.min(1.0, pWeight * neighNeighEnv * neighEnv);
		    		    									
		    		    									if (pVal > highVal) {
		    		    										highVal = pVal;
		    		    									}
		    		    									
		    	    									}
		    	    									
		    	    								}
		    	    								
		    	    							}
		    	    						}
		    	    						
		    	    	    				if (highVal != pathCost.get(neigh) && highVal >= thresh) {
		    	    	    					expected.put(tNeigh, expected.get(tNeigh) - pathCost.get(neigh));
		    	    	    					pathCost.put(neigh, highVal);
		    	    	    					expected.put(tNeigh, expected.get(tNeigh) + pathCost.get(neigh));
		    	    	    					changed.add(tNeigh);
		    		    						nextDoorNodesAgain.add(neigh);
		    	    	    				}
		    	    	    				else if (highVal < thresh) {
		    	    	    					expected.put(tNeigh, expected.get(tNeigh) - pathCost.get(neigh));
		    	    	    					pathCost.remove(neigh);
		    	    	    					changed.add(tNeigh);
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
	 	    		cu.setValue(antProb.get(cu) * actProb.get(cu) * expected.get(cu));
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
	 		    	mapOfPathCosts.remove(u);
	 		    	antProb.remove(u);
	 		    	actProb.remove(u);
	 		    	expected.remove(u);
	 			}

	 	    	
	 	    }
	 		
	 		return seeds;

	}
	
	private static Set<MultiProbabilityNode> getIncomingNeighboursLT(Graph<MultiProbabilityNode, TypedWeightedEdge> g, MultiProbabilityNode current, double thresh, 
			HashSet<MultiProbabilityNode> seeds, Concept target, Concept boosting, double infLimit){
		
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
				double neighThreshold = neigh.getAttribute(target.getId());
				double relativeInf = twe.getWeight() / neighThreshold;
				double contextCost = 0;
				
				if (seeds.contains(neigh)) {
					contextCost = Math.min(1.0, relativeInf * (currEnv) * (1 + target.getExternalEnvironment(neigh) + target.getConceptExternal(boosting)));
				} 
				else {
					contextCost = Math.min(1.0, relativeInf * (currEnv) * (1 + target.getExternalEnvironment(neigh)));
				}
				
				if (contextCost < infLimit && !neigh.isActivated(target)) {
					if (pathCost.containsKey(neigh)) {
						double cost = contextCost;
						//if (seeds.contains(neigh)) {cost = cost * (1 + target.getConceptInternal(boosting));}
						//if (currBoost) {cost = cost * (1 + target.getConceptExternal(boosting));}
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
		int hops = 0;
		while (!neighbours.isEmpty() && hops < 3) {

			for (MultiProbabilityNode currentNeighbour : neighbours) {

				boolean currNeighBoost = seeds.contains(currentNeighbour);
				double currNeighEnv = 0;
				if (currNeighBoost) {
					currNeighEnv = 1 + target.getInternalEnvironment(currentNeighbour) + target.getConceptInternal(boosting);
				}
				else {
					currNeighEnv = 1 + target.getInternalEnvironment(currentNeighbour);
				}
				
				double neighThreshold = currentNeighbour.getAttribute(target.getId());
				
				for(TypedWeightedEdge outgoingEdge : g.getInEdges(currentNeighbour)) {
					if (outgoingEdge.getConcept() == target.getId()) {
						MultiProbabilityNode s = g.getOpposite(currentNeighbour, outgoingEdge);
						
						
						double relativeInf = outgoingEdge.getWeight() / neighThreshold;
						double conCost = 0.0;
						
						if (seeds.contains(s)) {
							conCost = Math.min(1.0, relativeInf * (currNeighEnv) * (1 + target.getExternalEnvironment(s) + target.getConceptExternal(boosting)));
						}
						else {
							conCost = Math.min(1.0, relativeInf * (currNeighEnv) * (1 + target.getExternalEnvironment(s)));
						}
						
						if (!s.isActivated(target) & conCost < infLimit) {
							double sCost = conCost * pathCost.get(currentNeighbour.getId());
							//if (currNeighBoost) {sCost = sCost * (1 + target.getConceptInternal(boosting));}
							//if (seeds.contains(s)) {sCost = sCost * (1+target.getConceptExternal(boosting));}
							if (sCost >= thresh){
								if (!pathCost.containsKey(s)){
									pathCost.put(s, sCost);
									nextConsider.add(s);
								}
								else {
									if (sCost >= pathCost.get(s)) {
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
			hops++;
		}
		
		return pathCost.keySet();
	}
	
	private static double getIncomingAntLT(Graph<MultiProbabilityNode, TypedWeightedEdge> g, MultiProbabilityNode current, double thresh, 
			HashSet<MultiProbabilityNode> seeds, Concept antagonist, double infLimit){
		
		if (current.isActivated(antagonist)) {
			return 1;
		}
		
		TObjectDoubleHashMap<MultiProbabilityNode> pathCost = new TObjectDoubleHashMap<MultiProbabilityNode>();
		//HashMap<MultiProbabilityNode, TObjectDoubleHashMap<MultiProbabilityNode>> pathCost = new HashMap<MultiProbabilityNode, TObjectDoubleHashMap<MultiProbabilityNode>>();
		Collection<TypedWeightedEdge> currSet = g.getInEdges(current);
		HashSet<MultiProbabilityNode> neighbours = new HashSet<MultiProbabilityNode>();
		
		boolean currBoost = seeds.contains(current);
		//double currEnv = 1 + target.getExternalEnvironment(current);
		double totProb = 0;
		
		for (TypedWeightedEdge twe : currSet) {
			if (twe.getConcept() == antagonist.getId()) {
				MultiProbabilityNode neigh = g.getOpposite(current, twe);
				if (twe.getWeight() >= thresh) {
					if (neigh.isActivated(antagonist)){
						totProb += twe.getWeight();
					}
					else {
						pathCost.put(neigh, twe.getWeight());
						neighbours.add(neigh);
					}
				}
			}
		}
		
		HashSet<MultiProbabilityNode> seenBefore = new HashSet<MultiProbabilityNode>();
		seenBefore.addAll(neighbours);
		HashSet<MultiProbabilityNode> nextConsider = new HashSet<MultiProbabilityNode>();
		int hops = 0;
		while (!neighbours.isEmpty() && hops < 3) {

			for (MultiProbabilityNode currentNeighbour : neighbours) {
				for(TypedWeightedEdge outgoingEdge : g.getInEdges(currentNeighbour)) {
					if (outgoingEdge.getConcept() == antagonist.getId()) {
						MultiProbabilityNode s = g.getOpposite(currentNeighbour, outgoingEdge);
						double sCost = outgoingEdge.getWeight() * pathCost.get(currentNeighbour);
						
						if (sCost >= thresh) {
							if (s.isActivated(antagonist)) {
								totProb += sCost;
							}
							else {
								if (!pathCost.containsKey(s)){
									pathCost.put(s, sCost);
									nextConsider.add(s);
								}
								else {
									if (sCost >= pathCost.get(s)) {
										pathCost.put(s, sCost);
										nextConsider.add(s);
									}
								}
							}
						}
					}
				}
			}
			//System.out.println(nextConsider.size());
			neighbours.clear();
			neighbours.addAll(nextConsider);
			nextConsider.clear();
			hops++;
		}
		
		return totProb;
	}
	
	private static TObjectDoubleHashMap<MultiProbabilityNode> getPathCostMapLT(Graph<MultiProbabilityNode, TypedWeightedEdge> g, MultiProbabilityNode current, double thresh, 
			HashSet<MultiProbabilityNode> seeds, Concept target, Concept boosting, double infLimit) {
		
		TObjectDoubleHashMap<MultiProbabilityNode> pathCost = new TObjectDoubleHashMap<MultiProbabilityNode>();
		//System.out.println(current.getId());
		Collection<TypedWeightedEdge> currSet = g.getOutEdges(current);
		HashSet<MultiProbabilityNode> neighbours = new HashSet<MultiProbabilityNode>();
		double currEnv = 1 + target.getInternalEnvironment(current);
		int i = 0;
		
		for (TypedWeightedEdge twe : currSet) {
			if (twe.getConcept() == target.getId()) {
				MultiProbabilityNode neigh = g.getOpposite(current,twe);
				double neighThreshold = neigh.getAttribute(target.getId());
				double relativeInf = twe.getWeight() / neighThreshold;
				double contextCost = 0;
				
				if (seeds.contains(neigh)) {
					contextCost = Math.min(1.0, relativeInf * (currEnv) * (1 + target.getExternalEnvironment(neigh) + target.getConceptExternal(boosting)));
				} 
				else {
					contextCost = Math.min(1.0, relativeInf * (currEnv) * (1 + target.getExternalEnvironment(neigh)));
				}
				
				if (contextCost < infLimit && !neigh.isActivated(target)) {
					if (pathCost.containsKey(neigh)) {
						double cost = contextCost;
					//	if (seeds.contains(neigh)) {cost = cost * (1+target.getConceptExternal(boosting));}
				//		if (seeds.contains(neigh)) {cost = relativeInf * (currEnv) * (1 + target.getExternalEnvironment(neigh) + target.getConceptExternal(boosting));}
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
		int hops = 0;
		while (!neighbours.isEmpty() && hops < 3) {

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
						double neighThreshold = s.getAttribute(target.getId());
						double relativeInf = outgoingEdge.getWeight() / neighThreshold;
						double conCost = 0.0;
						
						if (seeds.contains(s)) {
							conCost = Math.min(1.0, relativeInf * (currNeighEnv) * (1 + target.getExternalEnvironment(s) + target.getConceptExternal(boosting)));
						}
						else {
							conCost = Math.min(1.0, relativeInf * (currNeighEnv) * (1 + target.getExternalEnvironment(s)));
						}
						
						//double conCost = relativeInf * (currNeighEnv) * (1 + target.getExternalEnvironment(s));
						if (!s.isActivated(target) & conCost < infLimit) {
							double sCost = conCost * pathCost.get(currentNeighbour.getId());
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
			hops++;
		}
		
		return pathCost;
	}
}
