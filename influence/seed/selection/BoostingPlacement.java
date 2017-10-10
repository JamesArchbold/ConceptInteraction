package influence.seed.selection;

import gnu.trove.list.array.TDoubleArrayList;
import influence.concepts.Concept;
import influence.edges.TypedWeightedEdge;
import influence.nodes.MultiProbabilityNode;
import influence.nodes.comparators.MultiProbabilityComparator;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.uci.ics.jung.graph.Graph;

public class BoostingPlacement {

	public static HashSet<MultiProbabilityNode> getSeedsBoostingOutgoing
	(Graph<MultiProbabilityNode, TypedWeightedEdge> g, int seedCount, Concept keyC, Concept boostC, HashSet<MultiProbabilityNode> keySeeds, double interaction) {
		HashSet<MultiProbabilityNode> seeds = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> nodesToSort = new HashSet<MultiProbabilityNode>();
		
		TDoubleArrayList baseVale = new TDoubleArrayList();
		TDoubleArrayList hopCount = new TDoubleArrayList();
		TDoubleArrayList finalVale = new TDoubleArrayList();
		

		for (MultiProbabilityNode m : g.getVertices()) {
			finalVale.add(0);
			hopCount.add(0);
		}
		
		HashSet<MultiProbabilityNode> hopNeighbours = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> encountered = new HashSet<MultiProbabilityNode>();
		hopNeighbours.addAll(keySeeds);
		encountered.addAll(hopNeighbours);
		int currHop = 0;
		
		while (hopNeighbours.size() != 0) {
			HashSet<MultiProbabilityNode> neighbours = new HashSet<MultiProbabilityNode>();
			
			for (MultiProbabilityNode m : hopNeighbours) {
				hopCount.set(m.getId(), currHop);
				for (MultiProbabilityNode neigh : g.getNeighbors(m)) {
					Collection<TypedWeightedEdge> edges = g.findEdgeSet(m, neigh);
					if (edges != null && !encountered.contains(neigh)) {
						neighbours.add(neigh);
					}
				}
			}
			
			encountered.addAll(neighbours);
			hopNeighbours.clear();
			hopNeighbours.addAll(neighbours);
			currHop = currHop + 1;
		}
		
		for (MultiProbabilityNode m : g.getVertices()) {
			double influence = 0;
			for (MultiProbabilityNode neighbour : g.getNeighbors(m)) {
				Collection<TypedWeightedEdge> edges = g.findEdgeSet(m, neighbour);
				if (edges != null) {
					for (TypedWeightedEdge e : edges) {
						if (e.getConcept() == boostC.getId()) {
							if (keySeeds.contains(m) || keySeeds.contains(neighbour)) {
								influence = influence + (e.getWeight() * (1 + interaction));
							}
							else {
								influence += e.getWeight();
							}
							break;
						}
					}
						
				}
			}
			m.setValue(influence);
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

			HashSet<MultiProbabilityNode> toUpdate = new HashSet<MultiProbabilityNode>();
			nodesToSort.remove(choice);
			
			for (MultiProbabilityNode neighbour : g.getNeighbors(choice)) {
				Collection<TypedWeightedEdge> edges = g.findEdgeSet(neighbour, choice);
				double influence = 0;
				if (edges != null) {
					for (TypedWeightedEdge e : edges) {
						if (e.getConcept() == boostC.getId()) {
							if (keySeeds.contains(choice) || keySeeds.contains(neighbour)) {
								influence = e.getWeight() * (1 + interaction);
							}
							else {
								influence = e.getWeight();
							}
							
							break;
						}
					}
					neighbour.setValue(neighbour.getValue() - influence);
				}
				
			}
			
			sortedN.clear();
			for (MultiProbabilityNode m : nodesToSort) {
				sortedN.add(m);
			}
			
			
		}
		
		
		return seeds;
		
	}
	
	public static HashSet<MultiProbabilityNode> getSeedsBoostingOutgoingHops
	(Graph<MultiProbabilityNode, TypedWeightedEdge> g, int seedCount, Concept keyC, Concept boostC, HashSet<MultiProbabilityNode> keySeeds, double interaction) {
		HashSet<MultiProbabilityNode> seeds = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> nodesToSort = new HashSet<MultiProbabilityNode>();
		
		TDoubleArrayList baseVale = new TDoubleArrayList();
		TDoubleArrayList hopCount = new TDoubleArrayList();
		TDoubleArrayList finalVale = new TDoubleArrayList();
		
		int vSize = g.getVertices().size();
		for (int ll = 0; ll < vSize; ll++) {
			finalVale.add(0);
			hopCount.add(0);
		}
		
		HashSet<MultiProbabilityNode> hopNeighbours = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> encountered = new HashSet<MultiProbabilityNode>();
		hopNeighbours.addAll(keySeeds);
		encountered.addAll(hopNeighbours);
		int currHop = 0;
		
		while (hopNeighbours.size() != 0) {
			HashSet<MultiProbabilityNode> neighbours = new HashSet<MultiProbabilityNode>();
			
			for (MultiProbabilityNode m : hopNeighbours) {
				hopCount.set(m.getId(), currHop);
				for (MultiProbabilityNode neigh : g.getNeighbors(m)) {
					Collection<TypedWeightedEdge> edges = g.findEdgeSet(m, neigh);
					if (edges != null && !encountered.contains(neigh)) {
						neighbours.add(neigh);
					}
				}
			}
			
			encountered.addAll(neighbours);
			hopNeighbours.clear();
			hopNeighbours.addAll(neighbours);
			currHop = currHop + 1;
		}
		
		for (MultiProbabilityNode m : g.getVertices()) {
			double influence = 0;
			double hCount = hopCount.get(m.getId());
			boolean mInKey = keySeeds.contains(m);
			
			for (MultiProbabilityNode neighbour : g.getNeighbors(m)) {
				Collection<TypedWeightedEdge> edges = g.findEdgeSet(m, neighbour);
				if (edges != null) {
					for (TypedWeightedEdge e : edges) {
						if (e.getConcept() == boostC.getId()) {
							if (mInKey || keySeeds.contains(neighbour)) {
								influence = influence + (e.getWeight() * (1 + interaction));
							}
							else if (hCount >= 2 && hopCount.get(neighbour.getId()) <= hCount) {
								influence = influence + ((e.getWeight() * (1 + interaction)) / hCount);
							}
							else {
								influence += e.getWeight();
							}
							break;
						}
					}
						
				}
			}
			m.setValue(influence);
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

			HashSet<MultiProbabilityNode> toUpdate = new HashSet<MultiProbabilityNode>();
			nodesToSort.remove(choice);
			double choiceHCount = hopCount.get(choice.getId());
			boolean choiceInKey = keySeeds.contains(choice);
			
			for (MultiProbabilityNode neighbour : g.getNeighbors(choice)) {
				double neighHCount = hopCount.get(neighbour.getId());
				Collection<TypedWeightedEdge> edges = g.findEdgeSet(neighbour, choice);
				double influence = 0;
				if (edges != null) {
					for (TypedWeightedEdge e : edges) {
						if (e.getConcept() == boostC.getId()) {
							if (choiceInKey || keySeeds.contains(neighbour)) {
								influence = e.getWeight() * (1 + interaction);
							}
							else if (neighHCount >= 2 && choiceHCount <= neighHCount) {
								influence = (e.getWeight() * (1 + interaction)) / neighHCount;
							}
							else {
								influence = e.getWeight();
							}
							
							break;
						}
					}
					neighbour.setValue(neighbour.getValue() - influence);
				}
				
			}
			
			sortedN.clear();
			for (MultiProbabilityNode m : nodesToSort) {
				sortedN.add(m);
			}
			
			
		}
		
		
		return seeds;
		
	}
	
	public static HashSet<MultiProbabilityNode> getSeedsBoostingAll
	(Graph<MultiProbabilityNode, TypedWeightedEdge> g, int seedCount, Concept keyC, Concept boostC, HashSet<MultiProbabilityNode> keySeeds, double interaction) {
		HashSet<MultiProbabilityNode> seeds = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> nodesToSort = new HashSet<MultiProbabilityNode>();
		
		TDoubleArrayList baseVale = new TDoubleArrayList();
		TDoubleArrayList hopCount = new TDoubleArrayList();
		TDoubleArrayList finalVale = new TDoubleArrayList();
		

		for (MultiProbabilityNode m : g.getVertices()) {
			finalVale.add(0);
			hopCount.add(0);
		}
		
		HashSet<MultiProbabilityNode> hopNeighbours = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> encountered = new HashSet<MultiProbabilityNode>();
		hopNeighbours.addAll(keySeeds);
		encountered.addAll(hopNeighbours);
		int currHop = 0;
		
		while (hopNeighbours.size() != 0) {
			HashSet<MultiProbabilityNode> neighbours = new HashSet<MultiProbabilityNode>();
			
			for (MultiProbabilityNode m : hopNeighbours) {
				hopCount.set(m.getId(), currHop);
				for (MultiProbabilityNode neigh : g.getNeighbors(m)) {
					Collection<TypedWeightedEdge> edges = g.findEdgeSet(m, neigh);
					if (edges != null && !encountered.contains(neigh)) {
						neighbours.add(neigh);
					}
				}
			}
			
			encountered.addAll(neighbours);
			hopNeighbours.clear();
			hopNeighbours.addAll(neighbours);
			currHop = currHop + 1;
		}
		
		for (MultiProbabilityNode m : g.getVertices()) {
			double influence = 0;
			for (MultiProbabilityNode neighbour : g.getNeighbors(m)) {
				Collection<TypedWeightedEdge> edges = g.findEdgeSet(m, neighbour);
				if (edges != null) {
					for (TypedWeightedEdge e : edges) {
						if (e.getConcept() == boostC.getId()) {
							if (keySeeds.contains(m) || keySeeds.contains(neighbour)) {
								influence = influence + (e.getWeight() * (1 + interaction));
							}
							else {
								influence += e.getWeight();
							}
							break;
						}
					}
						
				}
			}
			m.setValue(influence);
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
			
			for (MultiProbabilityNode neighbour : g.getNeighbors(choice)) {
				HashSet<TypedWeightedEdge> edges = new HashSet<TypedWeightedEdge>();
				edges.addAll(g.findEdgeSet(choice, neighbour));
				edges.addAll(g.findEdgeSet(neighbour, choice));
				double influence = 0;
				if (edges != null) {
					for (TypedWeightedEdge e : edges) {
						if (e.getConcept() == boostC.getId()) {
							if (keySeeds.contains(choice) || keySeeds.contains(neighbour)) {
								influence = e.getWeight() * (1 + interaction);
							}
							else {
								influence = e.getWeight();
							}
							
							break;
						}
					}
					neighbour.setValue(neighbour.getValue() - influence);
				}
				
			}
			
			sortedN.clear();
			for (MultiProbabilityNode m : nodesToSort) {
				sortedN.add(m);
			}
			
			
		}
		
		
		return seeds;
		
	}

	public static HashSet<MultiProbabilityNode> getSeedsBoostingAllHops
	(Graph<MultiProbabilityNode, TypedWeightedEdge> g, int seedCount, Concept keyC, Concept boostC, HashSet<MultiProbabilityNode> keySeeds, double interaction) {
		HashSet<MultiProbabilityNode> seeds = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> nodesToSort = new HashSet<MultiProbabilityNode>();
		
		TDoubleArrayList baseVale = new TDoubleArrayList();
		TDoubleArrayList hopCount = new TDoubleArrayList();
		TDoubleArrayList finalVale = new TDoubleArrayList();
		

		for (MultiProbabilityNode m : g.getVertices()) {
			finalVale.add(0);
			hopCount.add(0);
		}
		
		HashSet<MultiProbabilityNode> hopNeighbours = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> encountered = new HashSet<MultiProbabilityNode>();
		hopNeighbours.addAll(keySeeds);
		encountered.addAll(hopNeighbours);
		int currHop = 0;
		
		while (hopNeighbours.size() != 0) {
			HashSet<MultiProbabilityNode> neighbours = new HashSet<MultiProbabilityNode>();
			
			for (MultiProbabilityNode m : hopNeighbours) {
				hopCount.set(m.getId(), currHop);
				for (MultiProbabilityNode neigh : g.getNeighbors(m)) {
					Collection<TypedWeightedEdge> edges = g.findEdgeSet(m, neigh);
					if (edges != null && !encountered.contains(neigh)) {
						neighbours.add(neigh);
					}
				}
			}
			
			encountered.addAll(neighbours);
			hopNeighbours.clear();
			hopNeighbours.addAll(neighbours);
			currHop = currHop + 1;
		}
		
		for (MultiProbabilityNode m : g.getVertices()) {
			double influence = 0;
			for (MultiProbabilityNode neighbour : g.getNeighbors(m)) {
				Collection<TypedWeightedEdge> edges = g.findEdgeSet(m, neighbour);
				if (edges != null) {
					for (TypedWeightedEdge e : edges) {
						if (e.getConcept() == boostC.getId()) {
							if (keySeeds.contains(m) || keySeeds.contains(neighbour)) {
								influence = influence + (e.getWeight() * (1 + interaction));
							}
							else if (hopCount.get(m.getId()) >= 2 && hopCount.get(neighbour.getId()) <= hopCount.get(m.getId())) {
								influence = influence + ((e.getWeight() * (1 + interaction)) / (hopCount.get(m.getId())));
							}
							else {
								influence += e.getWeight();
							}
							break;
						}
					}
						
				}
			}
			m.setValue(influence);
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
			
			for (MultiProbabilityNode neighbour : g.getNeighbors(choice)) {
				HashSet<TypedWeightedEdge> edges = new HashSet<TypedWeightedEdge>();
				edges.addAll(g.findEdgeSet(choice, neighbour));
				edges.addAll(g.findEdgeSet(neighbour, choice));
				double influence = 0;
				if (edges != null) {
					for (TypedWeightedEdge e : edges) {
						if (e.getConcept() == boostC.getId()) {
							if (keySeeds.contains(choice) || keySeeds.contains(neighbour)) {
								influence = e.getWeight() * (1 + interaction);
							}
							else if (hopCount.get(neighbour.getId()) >= 2 && hopCount.get(choice.getId()) <= hopCount.get(neighbour.getId())) {
								influence = (e.getWeight() * (1 + interaction)) / (hopCount.get(neighbour.getId()));
							}
							else {
								influence = e.getWeight();
							}
							
							break;
						}
					}
					neighbour.setValue(neighbour.getValue() - influence);
				}
				
			}
			
			sortedN.clear();
			for (MultiProbabilityNode m : nodesToSort) {
				sortedN.add(m);
			}
			
			
		}
		
		
		return seeds;
		
	}
	
	public static HashSet<MultiProbabilityNode> getSeeds
	(Graph<MultiProbabilityNode, TypedWeightedEdge> g, int seedCount, Concept keyC, Concept boostC) {
		HashSet<MultiProbabilityNode> seeds = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> nodesToSort = new HashSet<MultiProbabilityNode>();
		
		TDoubleArrayList baseVale = new TDoubleArrayList();
		TDoubleArrayList modVale = new TDoubleArrayList();
		TDoubleArrayList finalVale = new TDoubleArrayList();
		HashSet<MultiProbabilityNode> keySeeds = new HashSet<MultiProbabilityNode>();
		
		int seedChoiceCount = 0;
		double seedModValue = 2;
		for (MultiProbabilityNode m : g.getVertices()) {
			baseVale.add(0);
			modVale.add(0);
			finalVale.add(0);
		}
		
		for (MultiProbabilityNode m : g.getVertices()) {
			if (m.isActivated(keyC)){
				keySeeds.add(m);
				modVale.set(m.getId(), seedModValue);
			}
			else {
				int hopCount = hopCheck(m,g,keyC);
				if (hopCount >= 1) {
					modVale.set(m.getId(), 1.5);
				}
				else {
					modVale.set(m.getId(), 1);
				}
			}

			double influence = 0;
			for (MultiProbabilityNode neighbour : g.getNeighbors(m)) {
				Collection<TypedWeightedEdge> edges = g.findEdgeSet(m, neighbour);
				if (edges != null) {
					for (TypedWeightedEdge e : edges) {
						if (e.getConcept() == boostC.getId()) {
							influence += 1 + e.getWeight();
							break;
						}
					}
					
				}
			}
			baseVale.set(m.getId(), influence);

				
			
			//baseVale.set(m.getId(), g.getNeighborCount(m));
			
			finalVale.set(m.getId(), modVale.get(m.getId()) * baseVale.get(m.getId()));
			
			m.setValue(finalVale.get(m.getId()));
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

			HashSet<MultiProbabilityNode> toUpdate = new HashSet<MultiProbabilityNode>();
			nodesToSort.remove(choice);
			
			if (choice.isActivated(keyC)) {
				seedChoiceCount++;
				seedModValue = 2;// * (Math.pow(0.99, seedChoiceCount));
				
				for (MultiProbabilityNode node : keySeeds) {
					modVale.set(node.getId(), seedModValue);
					toUpdate.add(node);
				}
				
			}
			
			for (MultiProbabilityNode neighbour : g.getNeighbors(choice)) {
				Collection<TypedWeightedEdge> edges = g.findEdgeSet(neighbour, choice);
				double influence = 0;
				if (edges != null) {
					for (TypedWeightedEdge e : edges) {
						if (e.getConcept() == boostC.getId()) {
							influence = e.getWeight();
							break;
						}
					}
					
					baseVale.set(neighbour.getId(), baseVale.get(neighbour.getId()) - (1 + influence));
					toUpdate.add(neighbour);
				}
				
			}
			
			for (MultiProbabilityNode neighbour : g.getNeighbors(choice)) {
				Collection<TypedWeightedEdge> edges = g.findEdgeSet(choice, neighbour);
				double influence = 0;
				if (edges != null) {
					for (TypedWeightedEdge e : edges) {
						if (e.getConcept() == boostC.getId()) {
							influence = e.getWeight();
							break;
						}
					}
					
					baseVale.set(neighbour.getId(), baseVale.get(neighbour.getId()) - (1 + influence));
					toUpdate.add(neighbour);
				}
				
			}
			
			for (MultiProbabilityNode node : toUpdate) {
				finalVale.set(node.getId(), modVale.get(node.getId()) * baseVale.get(node.getId()));
				node.setValue(finalVale.get(node.getId()));
			}
			
			sortedN.clear();
			for (MultiProbabilityNode m : nodesToSort) {
				sortedN.add(m);
			}
			
			
		}
		
		
		return seeds;
		
	}

	private static int hopCheck(MultiProbabilityNode m, Graph<MultiProbabilityNode, TypedWeightedEdge> g, Concept keyC) {
		int total = 0;
		HashSet<MultiProbabilityNode> found = new HashSet<MultiProbabilityNode>();
		
		for (MultiProbabilityNode n : g.getNeighbors(m)){
			for (MultiProbabilityNode v : g.getNeighbors(n)) {
				if (v.isActivated(keyC) && !found.contains(v)) {
					total++;
					found.add(v);
				}
			}
		}
		
		return total;
	}
}
