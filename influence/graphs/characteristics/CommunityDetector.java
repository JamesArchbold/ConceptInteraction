package influence.graphs.characteristics;

import gnu.trove.map.hash.TIntIntHashMap;
import influence.edges.TypedWeightedEdge;
import influence.edges.WeightedEdge;
import influence.nodes.MultiProbabilityNode;
import influence.nodes.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;

public class CommunityDetector {

	public static HashMap<Integer, ArrayList<MultiProbabilityNode>> getCommunities(Graph<MultiProbabilityNode, TypedWeightedEdge> graph) {
		HashMap<Integer, ArrayList<MultiProbabilityNode>> communities = new HashMap<Integer, ArrayList<MultiProbabilityNode>>();
		TIntIntHashMap commMembership = new TIntIntHashMap();
		HashMap<Integer, MultiProbabilityNode> nodes = new HashMap<Integer, MultiProbabilityNode>();
		
		for (MultiProbabilityNode m : graph.getVertices()) {
			nodes.put(m.getId(), m);
			communities.put(m.getId(), new ArrayList<MultiProbabilityNode>());
			communities.get(m.getId()).add(m);
			commMembership.put(m.getId(), m.getId());
		}
		
		double totalWeight = 0;
		for (TypedWeightedEdge t : graph.getEdges()) {
			totalWeight = totalWeight + t.getWeight();
		}
		
		boolean change = true;
		int cycles = -1;
		System.out.println("Cycles begin");
		int count = 0;
		while (change) {
			change = false;
			cycles++;
			count = 0;
			for (MultiProbabilityNode m : graph.getVertices()) {
				System.out.println(count++);
				Collection<MultiProbabilityNode> neighbours = graph.getNeighbors(m);
				ArrayList<MultiProbabilityNode> currComm = communities.get(commMembership.get(m.getId()));
				System.out.println("Nieghbours: " + neighbours.size());
				for (MultiProbabilityNode neigh : neighbours) {
					Integer neighCommID = commMembership.get(neigh.getId());
					ArrayList<MultiProbabilityNode> neighComm = communities.get(neighCommID);
					if (commMembership.get(m.getId()) != neighCommID) {
						neighComm.remove(neigh);
						
						double moveGain = calculateGain(currComm, neigh, graph, totalWeight);
						double stayGain = calculateGain(neighComm, neigh, graph, totalWeight);
						
						if (moveGain > stayGain) {
							change = true;
							currComm.add(neigh);
							commMembership.put(neigh.getId(), commMembership.get(m.getId()));
						}
						else {
							neighComm.add(neigh);
						}
						
						if (neighComm.isEmpty()) {
							communities.remove(neighCommID);
						}
					}

				}
				
			}
			System.out.println("Cycle " + cycles + " complete.");
		}
		
		System.out.println("Cycles done.");
		
		if (cycles > 0) {
			HashMap<Integer, HashMap<Integer, Double>> weightMatrix = new HashMap<Integer, HashMap<Integer, Double>>();
			
			System.out.println("Communitiy weight matrix construction");
			for (Integer commID : communities.keySet()) {
				HashMap<Integer, Double> weightMap = new HashMap<Integer, Double>();
				ArrayList<MultiProbabilityNode> community = communities.get(commID);
				HashSet<TypedWeightedEdge> edgeSet = new HashSet<TypedWeightedEdge>();
				
				for (MultiProbabilityNode m : community) {
					edgeSet.addAll(graph.getIncidentEdges(m));
				}
				
				for (TypedWeightedEdge t : edgeSet) {
					Node first = graph.getEndpoints(t).getFirst();
					Node second = graph.getEndpoints(t).getSecond();
					
					if (commMembership.get(first.getId()) == commID) {
						int otherComm = commMembership.get(second.getId());
						if (!weightMap.containsKey(otherComm)){
							weightMap.put(otherComm, 0.0);
						}
						weightMap.put(otherComm, weightMap.get(otherComm) + t.getWeight());
					}
					else {
						int otherComm = commMembership.get(first.getId());
						if (!weightMap.containsKey(otherComm)){
							weightMap.put(otherComm, 0.0);
						}
						weightMap.put(otherComm, weightMap.get(otherComm) + t.getWeight());
					}
				}
				
				weightMatrix.put(commID, weightMap);
				
			}
			
			System.out.println("Meta graph construction");
			
			Graph<MultiProbabilityNode, TypedWeightedEdge> metaGraph = new SparseMultigraph<MultiProbabilityNode, TypedWeightedEdge>();
			HashMap<Integer, MultiProbabilityNode> nodeList = new HashMap<Integer, MultiProbabilityNode>();
			for (Integer commID : communities.keySet()) {
				MultiProbabilityNode m = new MultiProbabilityNode(commID);
				metaGraph.addVertex(m);
				nodeList.put(commID, m);
			}
			
			int edgeID = 0;

			for (Integer commID : weightMatrix.keySet()){
				HashMap<Integer, Double> weights = weightMatrix.get(commID);
				for (Integer neighID : weights.keySet()){
					System.out.println(weights.get(neighID));
					if (metaGraph.findEdge(nodeList.get(commID), nodeList.get(neighID)) == null &&  metaGraph.findEdge(nodeList.get(neighID),nodeList.get(commID)) == null){
						System.out.println("Edge added");
						metaGraph.addEdge(new TypedWeightedEdge(edgeID, 0, weights.get(neighID)), nodeList.get(commID), nodeList.get(neighID));
						edgeID++;
					}
				}
			}
			
			System.out.println("Next level of communities: ");
			HashMap<Integer, ArrayList<MultiProbabilityNode>> metaCommunities = getCommunities(metaGraph);
			
			HashMap<Integer, ArrayList<MultiProbabilityNode>> newCommunities = new HashMap<Integer, ArrayList<MultiProbabilityNode>>();
			int currCommCount = 0;
			System.out.println("Combining nodes for current level");
			for (Integer commID : metaCommunities.keySet()){
				ArrayList<MultiProbabilityNode> commToCombine = metaCommunities.get(commID);
				ArrayList<MultiProbabilityNode> fullNodeList = new ArrayList<MultiProbabilityNode>();
				
				for (Node m : commToCombine) {
					fullNodeList.addAll(communities.get(m.getId()));
				}
				
				newCommunities.put(currCommCount, fullNodeList);
				currCommCount++;
			}
			
			communities = newCommunities;
		}
		
		System.out.println("Returning:");
		return communities;
	}

	//find gain of taking a node and placing into currentCommunity
	private static double calculateGain(ArrayList<MultiProbabilityNode> currentCommunity, MultiProbabilityNode neighbour, 
			Graph<MultiProbabilityNode, TypedWeightedEdge> graph, double totalWeight) {
		
		
		double currentCommInWeight = 0;
		double currentCommTotalWeight = 0;
		double neighbourTotalWeight = 0;
		double neighbourCurrCommWeight = 0;
		
		for (MultiProbabilityNode m : currentCommunity) {
			for (TypedWeightedEdge t : graph.getIncidentEdges(m)) {
				if (currentCommunity.contains(graph.getEndpoints(t).getFirst()) && currentCommunity.contains(graph.getEndpoints(t).getSecond())) {
					currentCommInWeight = currentCommInWeight + t.getWeight();
				}
				
				currentCommTotalWeight = currentCommTotalWeight + t.getWeight();
			}
		}
		
		for (TypedWeightedEdge t : graph.getIncidentEdges(neighbour)) {
			neighbourTotalWeight = neighbourTotalWeight + t.getWeight();
			if (currentCommunity.contains(graph.getEndpoints(t).getFirst()) || currentCommunity.contains(graph.getEndpoints(t).getSecond())) {
				neighbourCurrCommWeight = neighbourCurrCommWeight + t.getWeight();
			}
		}
		
		double gain = 0;
		
		double x = currentCommInWeight + neighbourCurrCommWeight;
		x = x / (2*totalWeight);
		
		double y = currentCommTotalWeight + neighbourTotalWeight;
		y = y / (2 * totalWeight);
		y = y * y;
		
		x = x - y;
		
		double w = currentCommInWeight / (2 * totalWeight);
		double v = currentCommTotalWeight / (2 * totalWeight);
		v = v * v;
		double u = neighbourTotalWeight / (2 * totalWeight);
		u = u * u;
		w = w - v - u;
		
		gain = x - w;
		return gain;
	}
}
