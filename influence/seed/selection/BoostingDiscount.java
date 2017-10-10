package influence.seed.selection;

import edu.uci.ics.jung.graph.Graph;
import gnu.trove.map.hash.TIntDoubleHashMap;
import influence.concepts.Concept;
import influence.edges.TypedWeightedEdge;
import influence.nodes.MultiProbabilityNode;
import influence.nodes.comparators.MultiProbabilityComparator;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

public class BoostingDiscount {
	
	public static HashSet<MultiProbabilityNode> getSeeds
	(Graph<MultiProbabilityNode, TypedWeightedEdge> g, int seedCount, Concept keyC, Concept boostC, HashSet<MultiProbabilityNode> keySeeds, double interaction) {
		HashSet<MultiProbabilityNode> seeds = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> nodesToSort = new HashSet<MultiProbabilityNode>();
		
		TIntDoubleHashMap infectedChance = new TIntDoubleHashMap();
		TIntDoubleHashMap possibleInfects = new TIntDoubleHashMap();
		TIntDoubleHashMap originalInfectedChance = new TIntDoubleHashMap();
		TIntDoubleHashMap hopCountMap = new TIntDoubleHashMap();
		HashSet<MultiProbabilityNode> hopNeighbours = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> encountered = new HashSet<MultiProbabilityNode>();
		hopNeighbours.addAll(keySeeds);
		encountered.addAll(hopNeighbours);
		int currHop = 0;
		
		while (hopNeighbours.size() != 0) {
			HashSet<MultiProbabilityNode> neighbours = new HashSet<MultiProbabilityNode>();
			
			for (MultiProbabilityNode m : hopNeighbours) {
				if (currHop == 0) {
					infectedChance.put(m.getId(), 1.0);
					hopCountMap.put(m.getId(), 0);
				}
				for (MultiProbabilityNode neighbour : g.getNeighbors(m)){
					Collection<TypedWeightedEdge> edges = g.findEdgeSet(m, neighbour);
					if (edges != null) {
						for (TypedWeightedEdge e : edges) {
							
							if (e.getConcept() == keyC.getId()) {

								if (!encountered.contains(neighbour)) {
									
									double edgeChance;
									edgeChance = e.getWeight() * (1 + keyC.getConceptExternal(boostC));
									
									if (keySeeds.contains(m)) {
										edgeChance = edgeChance * (1 + keyC.getConceptInternal(boostC));
									}
									
									if (edgeChance > 1) {edgeChance = 1;}
									
									if (!neighbours.contains(neighbour)){
										hopCountMap.put(neighbour.getId(), currHop+1);
										neighbours.add(neighbour);
										infectedChance.put(neighbour.getId(), edgeChance * infectedChance.get(m.getId()));
										originalInfectedChance.put(neighbour.getId(), edgeChance * infectedChance.get(m.getId()));
									}
									else{
										infectedChance.put(neighbour.getId(), infectedChance.get(neighbour.getId()) + (edgeChance * infectedChance.get(m.getId())));
										originalInfectedChance.put(neighbour.getId(), originalInfectedChance.get(neighbour.getId()) + (edgeChance * infectedChance.get(m.getId())));
									}
								}
								
							}
						
						}
					}
					
				}
				
			}
			
			encountered.addAll(neighbours);
			hopNeighbours.clear();
			hopNeighbours.addAll(neighbours);
			currHop = currHop + 1;
		}
		
		for (MultiProbabilityNode m : g.getVertices()){
			double pInfects = 0;
			for (MultiProbabilityNode neighbour : g.getNeighbors(m)){
				Collection<TypedWeightedEdge> edges = g.findEdgeSet(m, neighbour);
				if (edges != null) {
					for (TypedWeightedEdge e : edges) {
						if (e.getConcept() == keyC.getId()) {
							double edgeChance = e.getWeight() * (1 + keyC.getConceptInternal(boostC)) * originalInfectedChance.get(neighbour.getId());
							if (keySeeds.contains(m)) {edgeChance = edgeChance * (1+keyC.getConceptInternal(boostC));}
							if (keySeeds.contains(neighbour)) {edgeChance = edgeChance * (1+keyC.getConceptExternal(boostC));}
							if (edgeChance > 1) {edgeChance = 1;}
							pInfects = pInfects + edgeChance;
						}
					}
				}
			}
			
			possibleInfects.put(m.getId(), pInfects);
			m.setValue(pInfects * infectedChance.get(m.getId()));
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
				Collection<TypedWeightedEdge> edges = g.findEdgeSet(choice, neighbour);
				if (edges != null) {
					for (TypedWeightedEdge e : edges) {
						if (e.getConcept() == boostC.getId()) {
							double edgeChance = e.getWeight();
							infectedChance.put(neighbour.getId(), infectedChance.get(neighbour.getId()) * (1 - e.getWeight()));
							break;
						}
					}
				}
				
				edges = g.findEdgeSet(neighbour, choice);
				if (edges != null) {
					for (TypedWeightedEdge e : edges) {
						if (e.getConcept() == keyC.getId()) {
							double edgeChance = e.getWeight() * (1 + keyC.getConceptInternal(boostC)) * originalInfectedChance.get(neighbour.getId());
							if (keySeeds.contains(neighbour)) {edgeChance = edgeChance * (1+keyC.getConceptInternal(boostC));}
							if (keySeeds.contains(choice)) {edgeChance = edgeChance * (1+keyC.getConceptExternal(boostC));}
							if (edgeChance > 1) {edgeChance = 1;}
							possibleInfects.put(neighbour.getId(), possibleInfects.get(neighbour.getId())- edgeChance);
						}
					}
				}	
				
				neighbour.setValue(infectedChance.get(neighbour.getId()) * possibleInfects.get(neighbour.getId()));
			}
			
			sortedN.clear();
			for (MultiProbabilityNode m : nodesToSort) {
				sortedN.add(m);
			}
			
			
		}
		
		
		return seeds;
	}
	
	public static HashSet<MultiProbabilityNode> getSeedsInfChance
	(Graph<MultiProbabilityNode, TypedWeightedEdge> g, int seedCount, Concept keyC, Concept boostC, HashSet<MultiProbabilityNode> keySeeds, double interaction) {
		HashSet<MultiProbabilityNode> seeds = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> nodesToSort = new HashSet<MultiProbabilityNode>();
		
		TIntDoubleHashMap infectedChance = new TIntDoubleHashMap();
		TIntDoubleHashMap possibleInfects = new TIntDoubleHashMap();
		TIntDoubleHashMap originalInfectedChance = new TIntDoubleHashMap();
		TIntDoubleHashMap hopCountMap = new TIntDoubleHashMap();
		HashSet<MultiProbabilityNode> hopNeighbours = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> encountered = new HashSet<MultiProbabilityNode>();
		hopNeighbours.addAll(keySeeds);
		encountered.addAll(hopNeighbours);
		int currHop = 0;
		
		while (hopNeighbours.size() != 0) {
			HashSet<MultiProbabilityNode> neighbours = new HashSet<MultiProbabilityNode>();
			
			for (MultiProbabilityNode m : hopNeighbours) {
				if (currHop == 0) {
					infectedChance.put(m.getId(), 1.0);
					hopCountMap.put(m.getId(), 0);
				}
				for (MultiProbabilityNode neighbour : g.getNeighbors(m)){
					Collection<TypedWeightedEdge> edges = g.findEdgeSet(m, neighbour);
					if (edges != null) {
						for (TypedWeightedEdge e : edges) {
							
							if (e.getConcept() == keyC.getId()) {

								if (!encountered.contains(neighbour)) {
									
									double edgeChance;
									edgeChance = e.getWeight() * (1 + keyC.getConceptExternal(boostC));
									
									if (keySeeds.contains(m)) {
										edgeChance = edgeChance * (1 + keyC.getConceptInternal(boostC));
									}
									
									if (edgeChance > 1) {edgeChance = 1;}
									
									if (!neighbours.contains(neighbour)){
										hopCountMap.put(neighbour.getId(), currHop+1);
										neighbours.add(neighbour);
										infectedChance.put(neighbour.getId(), edgeChance * infectedChance.get(m.getId()));
										originalInfectedChance.put(neighbour.getId(), edgeChance * infectedChance.get(m.getId()));
									}
									else{
										infectedChance.put(neighbour.getId(), infectedChance.get(neighbour.getId()) + (edgeChance * infectedChance.get(m.getId())));
										originalInfectedChance.put(neighbour.getId(), originalInfectedChance.get(neighbour.getId()) + (edgeChance * infectedChance.get(m.getId())));
									}
								}
								
							}
						
						}
					}
					
				}
				
			}
			
			encountered.addAll(neighbours);
			hopNeighbours.clear();
			hopNeighbours.addAll(neighbours);
			currHop = currHop + 1;
		}
		
		for (MultiProbabilityNode m : g.getVertices()){
			double pInfects = 0;
			for (MultiProbabilityNode neighbour : g.getNeighbors(m)){
				if (!keySeeds.contains(neighbour)) {
					Collection<TypedWeightedEdge> edges = g.findEdgeSet(m, neighbour);
					if (edges != null) {
						for (TypedWeightedEdge e : edges) {
							if (e.getConcept() == keyC.getId()) {
								double edgeChance = e.getWeight() * (1 + keyC.getConceptInternal(boostC)) * originalInfectedChance.get(neighbour.getId());
								if (edgeChance > 1) {edgeChance = 1;}
								pInfects = pInfects + edgeChance;
							}
						}
					}
				}
			}
			
			possibleInfects.put(m.getId(), pInfects);
			m.setValue(pInfects * infectedChance.get(m.getId()));
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
				Collection<TypedWeightedEdge> edges = g.findEdgeSet(choice, neighbour);
				if (edges != null) {
					for (TypedWeightedEdge e : edges) {
						if (e.getConcept() == boostC.getId()) {
							double edgeChance = e.getWeight();
							if (keySeeds.contains(neighbour)) {edgeChance = edgeChance * keyC.getConceptInternal(boostC);}
							if (keySeeds.contains(choice)) {edgeChance = edgeChance * keyC.getConceptExternal(boostC);}
							infectedChance.put(neighbour.getId(), infectedChance.get(neighbour.getId()) * (1 - edgeChance));
							break;
						}
					}
				}
				
				edges = g.findEdgeSet(neighbour, choice);
				if (edges != null) {
					for (TypedWeightedEdge e : edges) {
						if (e.getConcept() == keyC.getId()) {
							double edgeChance = e.getWeight() * (1 + keyC.getConceptInternal(boostC)) *  originalInfectedChance.get(neighbour.getId());
							if (edgeChance > 1) {edgeChance = 1;}
							

							possibleInfects.put(neighbour.getId(), possibleInfects.get(neighbour.getId())- edgeChance);
						}
					}
				}	
				
				neighbour.setValue(infectedChance.get(neighbour.getId()) * possibleInfects.get(neighbour.getId()));
			}
			
			sortedN.clear();
			for (MultiProbabilityNode m : nodesToSort) {
				sortedN.add(m);
			}
			
			
		}
		
		
		return seeds;
	}
	
	public static HashSet<MultiProbabilityNode> getSeedsOldUsingKey//original but using key edge weight instead of boost weight when decreasing value
	(Graph<MultiProbabilityNode, TypedWeightedEdge> g, int seedCount, Concept keyC, Concept boostC, HashSet<MultiProbabilityNode> keySeeds, double interaction) {
		HashSet<MultiProbabilityNode> seeds = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> nodesToSort = new HashSet<MultiProbabilityNode>();
		
		TIntDoubleHashMap infectedChance = new TIntDoubleHashMap();
		TIntDoubleHashMap possibleInfects = new TIntDoubleHashMap();
		TIntDoubleHashMap originalInfectedChance = new TIntDoubleHashMap();
		TIntDoubleHashMap hopCountMap = new TIntDoubleHashMap();
		HashSet<MultiProbabilityNode> hopNeighbours = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> encountered = new HashSet<MultiProbabilityNode>();
		hopNeighbours.addAll(keySeeds);
		encountered.addAll(hopNeighbours);
		int currHop = 0;
		
		while (hopNeighbours.size() != 0) {
			HashSet<MultiProbabilityNode> neighbours = new HashSet<MultiProbabilityNode>();
			
			for (MultiProbabilityNode m : hopNeighbours) {
				if (currHop == 0) {
					infectedChance.put(m.getId(), 1.0);
					hopCountMap.put(m.getId(), 0);
				}
				for (MultiProbabilityNode neighbour : g.getNeighbors(m)){
					Collection<TypedWeightedEdge> edges = g.findEdgeSet(m, neighbour);
					if (edges != null) {
						for (TypedWeightedEdge e : edges) {
							
							if (e.getConcept() == keyC.getId()) {
								double edgeChance;
								edgeChance = e.getWeight() * (1 + keyC.getConceptExternal(boostC));
								if (edgeChance > 1) {edgeChance = 1;}
								if (!encountered.contains(neighbour)) {
									if (!neighbours.contains(neighbour)){
										hopCountMap.put(neighbour.getId(), currHop+1);
										neighbours.add(neighbour);
										infectedChance.put(neighbour.getId(), edgeChance * infectedChance.get(m.getId()));
										originalInfectedChance.put(neighbour.getId(), edgeChance * infectedChance.get(m.getId()));
									}
									else{
										infectedChance.put(neighbour.getId(), infectedChance.get(neighbour.getId()) + (edgeChance * infectedChance.get(m.getId())));
										originalInfectedChance.put(neighbour.getId(), originalInfectedChance.get(neighbour.getId()) + (edgeChance * infectedChance.get(m.getId())));
									}
								}
								
							}
						
						}
					}
					
				}
				
			}
			
			encountered.addAll(neighbours);
			hopNeighbours.clear();
			hopNeighbours.addAll(neighbours);
			currHop = currHop + 1;
		}
		
		for (MultiProbabilityNode m : g.getVertices()){
			double pInfects = 0;
			for (MultiProbabilityNode neighbour : g.getNeighbors(m)){
				if (!keySeeds.contains(neighbour)) {
					Collection<TypedWeightedEdge> edges = g.findEdgeSet(m, neighbour);
					if (edges != null) {
						for (TypedWeightedEdge e : edges) {
							if (e.getConcept() == keyC.getId()) {
								double edgeChance = e.getWeight() * (1 + keyC.getConceptInternal(boostC));
								if (edgeChance > 1) {edgeChance = 1;}
								pInfects = pInfects + edgeChance;
							}
						}
					}
				}
			}
			
			possibleInfects.put(m.getId(), pInfects);
			m.setValue(pInfects * infectedChance.get(m.getId()));
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
				Collection<TypedWeightedEdge> edges = g.findEdgeSet(choice, neighbour);
				if (edges != null) {
					for (TypedWeightedEdge e : edges) {
						if (e.getConcept() == boostC.getId()) {
							infectedChance.put(neighbour.getId(), infectedChance.get(neighbour.getId()) * (1 - e.getWeight()));
							break;
						}
					}
				}
				
				edges = g.findEdgeSet(neighbour, choice);
				if (edges != null) {
					for (TypedWeightedEdge e : edges) {
						if (e.getConcept() == keyC.getId()) {
							double edgeChance = e.getWeight() * (1 + keyC.getConceptInternal(boostC));
							if (edgeChance > 1) {edgeChance = 1;}
							
							double newEdgeChance = e.getWeight() * (1 + keyC.getConceptInternal(boostC)) * (1 + keyC.getConceptExternal(boostC));
							if (newEdgeChance > 1) {newEdgeChance = 1;}
							
							double oldPossibleInfects = possibleInfects.get(neighbour.getId());
							oldPossibleInfects = oldPossibleInfects - edgeChance;
							double newPossibleInfects = oldPossibleInfects + newEdgeChance;
							possibleInfects.put(neighbour.getId(), newPossibleInfects);
						}
					}
				}	
				
				neighbour.setValue(infectedChance.get(neighbour.getId()) * possibleInfects.get(neighbour.getId()));
			}
			
			sortedN.clear();
			for (MultiProbabilityNode m : nodesToSort) {
				sortedN.add(m);
			}
			
			
		}
		
		
		return seeds;
	}

	public static HashSet<MultiProbabilityNode> getSeedsOutgoing
	(Graph<MultiProbabilityNode, TypedWeightedEdge> g, int seedCount, Concept keyC, Concept boostC, HashSet<MultiProbabilityNode> keySeeds, double interaction) {
		HashSet<MultiProbabilityNode> seeds = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> nodesToSort = new HashSet<MultiProbabilityNode>();
		
		TIntDoubleHashMap infectedChance = new TIntDoubleHashMap();
		TIntDoubleHashMap possibleInfects = new TIntDoubleHashMap();
		TIntDoubleHashMap originalInfectedChance = new TIntDoubleHashMap();
		TIntDoubleHashMap hopCountMap = new TIntDoubleHashMap();
		HashSet<MultiProbabilityNode> hopNeighbours = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> encountered = new HashSet<MultiProbabilityNode>();
		hopNeighbours.addAll(keySeeds);
		encountered.addAll(hopNeighbours);
		int currHop = 0;
		
		while (hopNeighbours.size() != 0) {
			HashSet<MultiProbabilityNode> neighbours = new HashSet<MultiProbabilityNode>();
			
			for (MultiProbabilityNode m : hopNeighbours) {
				if (currHop == 0) {
					infectedChance.put(m.getId(), 1.0);
					hopCountMap.put(m.getId(), 0);
				}
				for (MultiProbabilityNode neighbour : g.getNeighbors(m)){
					Collection<TypedWeightedEdge> edges = g.findEdgeSet(m, neighbour);
					if (edges != null) {
						for (TypedWeightedEdge e : edges) {
							
							if (e.getConcept() == keyC.getId()) {
								double edgeChance;
								edgeChance = e.getWeight() * (1 + keyC.getConceptExternal(boostC));
								if (edgeChance > 1) {edgeChance = 1;}
								if (!encountered.contains(neighbour)) {
									if (!neighbours.contains(neighbour)){
										hopCountMap.put(neighbour.getId(), currHop+1);
										neighbours.add(neighbour);
										infectedChance.put(neighbour.getId(), edgeChance * infectedChance.get(m.getId()));
										originalInfectedChance.put(neighbour.getId(), edgeChance * infectedChance.get(m.getId()));
									}
									else{
										infectedChance.put(neighbour.getId(), infectedChance.get(neighbour.getId()) + (edgeChance * infectedChance.get(m.getId())));
										originalInfectedChance.put(neighbour.getId(), originalInfectedChance.get(neighbour.getId()) + (edgeChance * infectedChance.get(m.getId())));
									}
								}
								
							}
						
						}
					}
					
				}
				
			}
			
			encountered.addAll(neighbours);
			hopNeighbours.clear();
			hopNeighbours.addAll(neighbours);
			currHop = currHop + 1;
		}
		
		for (MultiProbabilityNode m : g.getVertices()){
			double pInfects = 0;
			for (MultiProbabilityNode neighbour : g.getNeighbors(m)){
				if (!keySeeds.contains(neighbour)) {
					Collection<TypedWeightedEdge> edges = g.findEdgeSet(m, neighbour);
					if (edges != null) {
						for (TypedWeightedEdge e : edges) {
							if (e.getConcept() == keyC.getId()) {
								double edgeChance = e.getWeight() * (1 + keyC.getConceptInternal(boostC));
								if (edgeChance > 1) {edgeChance = 1;}
								pInfects = pInfects + edgeChance;
							}
						}
					}
				}
			}
			
			possibleInfects.put(m.getId(), pInfects);
			m.setValue(pInfects * infectedChance.get(m.getId()));
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
				Collection<TypedWeightedEdge> edges = g.findEdgeSet(choice, neighbour);
				if (edges != null) {
					for (TypedWeightedEdge e : edges) {
						if (e.getConcept() == boostC.getId()) {
							infectedChance.put(neighbour.getId(), infectedChance.get(neighbour.getId()) * (1 - e.getWeight()));
							break;
						}
					}
				}
				
				edges = g.findEdgeSet(neighbour, choice);
				if (edges != null) {
					for (TypedWeightedEdge e : edges) {
						if (e.getConcept() == keyC.getId()) {
							double edgeChance = e.getWeight() * (1 + keyC.getConceptInternal(boostC));
							if (edgeChance > 1) {edgeChance = 1;}
							possibleInfects.put(neighbour.getId(), (possibleInfects.get(neighbour.getId()) - edgeChance));
							break;
						}
					}
				}	
				
				neighbour.setValue(infectedChance.get(neighbour.getId()) * possibleInfects.get(neighbour.getId()));
			}
			
			sortedN.clear();
			for (MultiProbabilityNode m : nodesToSort) {
				sortedN.add(m);
			}
			
			
		}
		
		
		return seeds;
	}
	
	public static HashSet<MultiProbabilityNode> getSeedsDegDis
	(Graph<MultiProbabilityNode, TypedWeightedEdge> g, int seedCount, Concept keyC, Concept boostC, HashSet<MultiProbabilityNode> keySeeds, double interaction) {
		HashSet<MultiProbabilityNode> seeds = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> nodesToSort = new HashSet<MultiProbabilityNode>();
		
		TIntDoubleHashMap infectedChance = new TIntDoubleHashMap();
		TIntDoubleHashMap possibleInfects = new TIntDoubleHashMap();
		TIntDoubleHashMap originalInfectedChance = new TIntDoubleHashMap();
		TIntDoubleHashMap hopCountMap = new TIntDoubleHashMap();
		HashSet<MultiProbabilityNode> hopNeighbours = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> encountered = new HashSet<MultiProbabilityNode>();
		hopNeighbours.addAll(keySeeds);
		encountered.addAll(hopNeighbours);
		int currHop = 0;
		
		while (hopNeighbours.size() != 0) {
			HashSet<MultiProbabilityNode> neighbours = new HashSet<MultiProbabilityNode>();
			
			for (MultiProbabilityNode m : hopNeighbours) {
				if (currHop == 0) {
					infectedChance.put(m.getId(), 1.0);
					hopCountMap.put(m.getId(), 0);
				}
				for (MultiProbabilityNode neighbour : g.getNeighbors(m)){
					Collection<TypedWeightedEdge> edges = g.findEdgeSet(m, neighbour);
					if (edges != null) {
						for (TypedWeightedEdge e : edges) {
							
							if (e.getConcept() == keyC.getId()) {
								double edgeChance;
								edgeChance = e.getWeight() * (1 + keyC.getConceptExternal(boostC));
								if (edgeChance > 1) {edgeChance = 1;}
								if (!encountered.contains(neighbour)) {
									if (!neighbours.contains(neighbour)){
										hopCountMap.put(neighbour.getId(), currHop+1);
										neighbours.add(neighbour);
										infectedChance.put(neighbour.getId(), edgeChance * infectedChance.get(m.getId()));
										originalInfectedChance.put(neighbour.getId(), edgeChance * infectedChance.get(m.getId()));
									}
									else{
										infectedChance.put(neighbour.getId(), infectedChance.get(neighbour.getId()) + (edgeChance * infectedChance.get(m.getId())));
										originalInfectedChance.put(neighbour.getId(), originalInfectedChance.get(neighbour.getId()) + (edgeChance * infectedChance.get(m.getId())));
									}
								}
								
							}
						
						}
					}
					
				}
				
			}
			
			encountered.addAll(neighbours);
			hopNeighbours.clear();
			hopNeighbours.addAll(neighbours);
			currHop = currHop + 1;
		}
		
		for (MultiProbabilityNode m : g.getVertices()){
			double pInfects = 0;
			for (MultiProbabilityNode neighbour : g.getNeighbors(m)){
				if (!keySeeds.contains(neighbour)) {
					Collection<TypedWeightedEdge> edges = g.findEdgeSet(m, neighbour);
					if (edges != null) {
						for (TypedWeightedEdge e : edges) {
							if (e.getConcept() == boostC.getId()) {
								if (hopCountMap.get(m.getId()) >= hopCountMap.get(neighbour.getId())) {
									pInfects = pInfects + (e.getWeight() * (1 + keyC.getConceptInternal(boostC)));
								}
							}
						}
					}
				}
			}
			
			possibleInfects.put(m.getId(), pInfects);
			m.setValue(pInfects * infectedChance.get(m.getId()));
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
				Collection<TypedWeightedEdge> edges = g.findEdgeSet(choice, neighbour);
				if (edges != null) {
					for (TypedWeightedEdge e : edges) {
						if (e.getConcept() == boostC.getId()) {
							infectedChance.put(neighbour.getId(), infectedChance.get(neighbour.getId()) * (1 - e.getWeight()));
							break;
						}
					}
				}
				
				if (hopCountMap.get(neighbour.getId()) >= hopCountMap.get(choice.getId())){
					edges = g.findEdgeSet(neighbour, choice);
					if (edges != null) {
						for (TypedWeightedEdge e : edges) {
							if (e.getConcept() == boostC.getId()) {
								double edgeChance = e.getWeight() * (1 + keyC.getConceptInternal(boostC));
								if (edgeChance > 1) {edgeChance = 1;}
								
								double newEdgeChance = edgeChance * (1 + keyC.getConceptExternal(boostC));
								if (newEdgeChance > 1) {newEdgeChance = 1;}
								
								double oldPossibleInfects = possibleInfects.get(neighbour.getId());
								oldPossibleInfects = oldPossibleInfects - edgeChance;
								double newPossibleInfects = oldPossibleInfects + newEdgeChance;
								possibleInfects.put(neighbour.getId(), newPossibleInfects);
								break;
							}
						}
					}	
				}
				
				neighbour.setValue(infectedChance.get(neighbour.getId()) * possibleInfects.get(neighbour.getId()));
			}
			
			sortedN.clear();
			for (MultiProbabilityNode m : nodesToSort) {
				sortedN.add(m);
			}
			
			
		}
		
		
		return seeds;
	}
	
	public static HashSet<MultiProbabilityNode> getSeedsNeighbourInfectionChance
	(Graph<MultiProbabilityNode, TypedWeightedEdge> g, int seedCount, Concept keyC, Concept boostC, HashSet<MultiProbabilityNode> keySeeds, double interaction) {
		HashSet<MultiProbabilityNode> seeds = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> nodesToSort = new HashSet<MultiProbabilityNode>();
		
		TIntDoubleHashMap infectedChance = new TIntDoubleHashMap();
		TIntDoubleHashMap possibleInfects = new TIntDoubleHashMap();
		TIntDoubleHashMap originalInfectedChance = new TIntDoubleHashMap();
		HashSet<MultiProbabilityNode> hopNeighbours = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> encountered = new HashSet<MultiProbabilityNode>();
		hopNeighbours.addAll(keySeeds);
		encountered.addAll(hopNeighbours);
		int currHop = 0;
		
		while (hopNeighbours.size() != 0) {
			HashSet<MultiProbabilityNode> neighbours = new HashSet<MultiProbabilityNode>();
			
			for (MultiProbabilityNode m : hopNeighbours) {
				if (currHop == 0) {
					infectedChance.put(m.getId(), 1.0);
				}
				for (MultiProbabilityNode neighbour : g.getNeighbors(m)){
					Collection<TypedWeightedEdge> edges = g.findEdgeSet(m, neighbour);
					if (edges != null) {
						for (TypedWeightedEdge e : edges) {
							
							if (e.getConcept() == keyC.getId()) {
								double edgeChance;
								edgeChance = e.getWeight() * (1 + keyC.getConceptExternal(boostC));
								if (edgeChance > 1) {edgeChance = 1;}
								if (!encountered.contains(neighbour)) {
									if (!neighbours.contains(neighbour)){
										neighbours.add(neighbour);
										infectedChance.put(neighbour.getId(), edgeChance * infectedChance.get(m.getId()));
										originalInfectedChance.put(neighbour.getId(), edgeChance * infectedChance.get(m.getId()));
									}
									else{
										infectedChance.put(neighbour.getId(), infectedChance.get(neighbour.getId()) + (edgeChance * infectedChance.get(m.getId())));
										originalInfectedChance.put(neighbour.getId(), originalInfectedChance.get(neighbour.getId()) + (edgeChance * infectedChance.get(m.getId())));
									}
								}
								
							}
						
						}
					}
					
				}
				
			}
			
			encountered.addAll(neighbours);
			hopNeighbours.clear();
			hopNeighbours.addAll(neighbours);
			currHop = currHop + 1;
		}
		
		for (MultiProbabilityNode m : g.getVertices()){
			double pInfects = 0;
			for (MultiProbabilityNode neighbour : g.getNeighbors(m)){
				if (!keySeeds.contains(neighbour)) {
					Collection<TypedWeightedEdge> edges = g.findEdgeSet(m, neighbour);
					if (edges != null) {
						for (TypedWeightedEdge e : edges) {
							if (e.getConcept() == boostC.getId()) {
									pInfects = pInfects + (e.getWeight() * infectedChance.get(neighbour.getId()) * (1 + keyC.getConceptInternal(boostC)));
							}
						}
					}
				}
			}
			
			possibleInfects.put(m.getId(), pInfects);
			m.setValue(pInfects * infectedChance.get(m.getId()));
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
				Collection<TypedWeightedEdge> edges = g.findEdgeSet(choice, neighbour);
				if (edges != null) {
					for (TypedWeightedEdge e : edges) {
						if (e.getConcept() == boostC.getId()) {
							infectedChance.put(neighbour.getId(), infectedChance.get(neighbour.getId()) * (1 - e.getWeight()));
							break;
						}
					}
				}
				
				edges = g.findEdgeSet(neighbour, choice);
				if (edges != null) {
					for (TypedWeightedEdge e : edges) {
						if (e.getConcept() == boostC.getId()) {
							double edgeChance = e.getWeight() * originalInfectedChance.get(choice.getId()) * (1 + keyC.getConceptInternal(boostC));
							if (edgeChance > 1) {edgeChance = 1;}
							
							double newEdgeChance = edgeChance * (1 + keyC.getConceptExternal(boostC));
							if (newEdgeChance > 1) {newEdgeChance = 1;}
							
							double oldPossibleInfects = possibleInfects.get(neighbour.getId());
							oldPossibleInfects = oldPossibleInfects - edgeChance;
							double newPossibleInfects = oldPossibleInfects + newEdgeChance;
							possibleInfects.put(neighbour.getId(), newPossibleInfects);
							break;
						}
					}
				}
				
				neighbour.setValue(infectedChance.get(neighbour.getId()) * possibleInfects.get(neighbour.getId()));
			}
			
			sortedN.clear();
			for (MultiProbabilityNode m : nodesToSort) {
				sortedN.add(m);
			}
			
			
		}
		
		
		return seeds;
	}
	
	public static HashSet<MultiProbabilityNode> getSeedsOld
	(Graph<MultiProbabilityNode, TypedWeightedEdge> g, int seedCount, Concept keyC, Concept boostC, HashSet<MultiProbabilityNode> keySeeds, double interaction) {
		HashSet<MultiProbabilityNode> seeds = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> nodesToSort = new HashSet<MultiProbabilityNode>();
		
		TIntDoubleHashMap infectedChance = new TIntDoubleHashMap();
		TIntDoubleHashMap possibleInfects = new TIntDoubleHashMap();
		
		HashSet<MultiProbabilityNode> hopNeighbours = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> encountered = new HashSet<MultiProbabilityNode>();
		hopNeighbours.addAll(keySeeds);
		encountered.addAll(hopNeighbours);
		int currHop = 0;
		
		while (hopNeighbours.size() != 0) {
			HashSet<MultiProbabilityNode> neighbours = new HashSet<MultiProbabilityNode>();
			
			for (MultiProbabilityNode m : hopNeighbours) {
				if (currHop == 0) {
					infectedChance.put(m.getId(), 1.0);
				}
				
				double pInfects = 0;
				
				for (MultiProbabilityNode neighbour : g.getNeighbors(m)){
					Collection<TypedWeightedEdge> edges = g.findEdgeSet(m, neighbour);
					if (edges != null) {
						for (TypedWeightedEdge e : edges) {
							
							if (e.getConcept() == keyC.getId()) {
								double edgeChance = e.getWeight() * (1 + keyC.getConceptInternal(boostC));
								if (edgeChance > 1) {edgeChance = 1;}
								pInfects = pInfects + edgeChance;
								
								if (!encountered.contains(neighbour)) {
									if (!neighbours.contains(neighbour)){
										neighbours.add(neighbour);
										edgeChance = e.getWeight() * (1 + keyC.getConceptExternal(boostC));
										if (edgeChance > 1) {edgeChance = 1;}
										infectedChance.put(neighbour.getId(), edgeChance * infectedChance.get(m.getId()));
									}
									else{
										edgeChance = e.getWeight() * (1 + keyC.getConceptExternal(boostC));
										if (edgeChance > 1) {edgeChance = 1;}
										infectedChance.put(neighbour.getId(), infectedChance.get(neighbour.getId()) + (edgeChance * infectedChance.get(m.getId())));
									}
								}
								
							}
						
						}
					}
					
				}
				
				possibleInfects.put(m.getId(), pInfects);
				m.setValue(pInfects * infectedChance.get(m.getId()));
				nodesToSort.add(m);
				
			}
			
			encountered.addAll(neighbours);
			hopNeighbours.clear();
			hopNeighbours.addAll(neighbours);
			currHop = currHop + 1;
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
				Collection<TypedWeightedEdge> edges = g.findEdgeSet(choice, neighbour);
				if (edges != null) {
					for (TypedWeightedEdge e : edges) {
						if (e.getConcept() == boostC.getId()) {
							infectedChance.put(neighbour.getId(), infectedChance.get(neighbour.getId()) * (1 - e.getWeight()));
							break;
						}
					}
				}
				
				edges = g.findEdgeSet(neighbour, choice);
				if (edges != null) {
					for (TypedWeightedEdge e : edges) {
						if (e.getConcept() == boostC.getId()) {
							double edgeChance = e.getWeight() * (1 + keyC.getConceptInternal(boostC));
							if (edgeChance > 1) {edgeChance = 1;}
							
							double newEdgeChance = e.getWeight() * (1 + keyC.getConceptInternal(boostC)) * (1 + keyC.getConceptExternal(boostC));
							if (newEdgeChance > 1) {newEdgeChance = 1;}
							
							double oldPossibleInfects = possibleInfects.get(neighbour.getId());
							oldPossibleInfects = oldPossibleInfects - edgeChance;
							double newPossibleInfects = oldPossibleInfects + newEdgeChance;
							possibleInfects.put(neighbour.getId(), newPossibleInfects);
							break;
						}
					}
				}
				
				neighbour.setValue(infectedChance.get(neighbour.getId()) * possibleInfects.get(neighbour.getId()));
			}
			
			sortedN.clear();
			for (MultiProbabilityNode m : nodesToSort) {
				sortedN.add(m);
			}
			
			
		}
		
		
		return seeds;
		
	}

	public static HashSet<MultiProbabilityNode> getSeedsVerbose
	(Graph<MultiProbabilityNode, TypedWeightedEdge> g, int seedCount, Concept keyC, Concept boostC, HashSet<MultiProbabilityNode> keySeeds, double interaction) {
		HashSet<MultiProbabilityNode> seeds = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> nodesToSort = new HashSet<MultiProbabilityNode>();
		
		TIntDoubleHashMap infectedChance = new TIntDoubleHashMap();
		TIntDoubleHashMap possibleInfects = new TIntDoubleHashMap();

		System.out.println("Key node has external interaction with boosting node of: " + keyC.getConceptExternal(boostC));
		System.out.println("Key node has internal interaction with boosting node of: " + keyC.getConceptInternal(boostC));
		System.out.println("Interaction value of: " + interaction);
		HashSet<MultiProbabilityNode> hopNeighbours = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> encountered = new HashSet<MultiProbabilityNode>();
		hopNeighbours.addAll(keySeeds);
		encountered.addAll(hopNeighbours);
		int currHop = 0;
		
		System.out.println("Size of key seeds set: " + keySeeds.size());
		System.out.println("Size of hop neighbours set: " + hopNeighbours.size());
		
		System.out.println("Beginning initial loop");
		
		while (hopNeighbours.size() != 0) {
			HashSet<MultiProbabilityNode> neighbours = new HashSet<MultiProbabilityNode>();
			
			for (MultiProbabilityNode m : hopNeighbours) {
				System.out.println("Considering node " + m.getId());
				if (currHop == 0) {
					infectedChance.put(m.getId(), 1.0);
					System.out.println("Node " + m.getId() + " is a key seed and has infectedChance of " + infectedChance.get(m.getId()));
				}
				
				double pInfects = 0;
				
				for (MultiProbabilityNode neighbour : g.getNeighbors(m)){
					System.out.println("Consider neighbour " + neighbour.getId() + " for node " + m.getId());
					Collection<TypedWeightedEdge> edges = g.findEdgeSet(m, neighbour);
					if (edges != null) {
						for (TypedWeightedEdge e : edges) {
							
							if (e.getConcept() == keyC.getId()) {
								double edgeChance = e.getWeight() * (1 + keyC.getConceptInternal(boostC));
								if (edgeChance > 1) {edgeChance = 1;}
								pInfects = pInfects + edgeChance;
								System.out.println("Node " + m.getId() + " has pInfect value of " + pInfects);
								
								if (!encountered.contains(neighbour)) {
									System.out.println("Node " + neighbour.getId() + " has not been encountered before.");
									if (!neighbours.contains(neighbour)){
										System.out.println("Node " + neighbour.getId() + " has never been seen before.");
										neighbours.add(neighbour);
										edgeChance = e.getWeight() * (1 + keyC.getConceptExternal(boostC));
										System.out.println("Edge weight is " + e.getWeight());
										System.out.println("Concept interaction consideration value: " +(1 + keyC.getConceptExternal(boostC)));
										System.out.println("Contribution is: " + edgeChance);
										if (edgeChance > 1) {edgeChance = 1;}
										System.out.println("Node " + m.getId() + " has infectedChance of " + infectedChance.get(m.getId()));
										infectedChance.put(neighbour.getId(), edgeChance * infectedChance.get(m.getId()));
										System.out.println("Node " + neighbour.getId() + " has value of " + infectedChance.get(neighbour.getId()) + " for their infectedchance"); 
									}
									else{
										System.out.println("Node " + neighbour.getId() + " has been seen before.");
										edgeChance = e.getWeight() * (1 + keyC.getConceptExternal(boostC));
										System.out.println("Edge weight is " + e.getWeight());
										System.out.println("Concept interaction consideration value: " +(1 + keyC.getConceptExternal(boostC)));
										System.out.println("Contribution is: " + edgeChance);
										if (edgeChance > 1) {edgeChance = 1;}
										System.out.println("Node " + m.getId() + " has infectedChance of " + infectedChance.get(m.getId()));
										infectedChance.put(neighbour.getId(), infectedChance.get(neighbour.getId()) + (edgeChance * infectedChance.get(m.getId())));
										System.out.println("Node " + neighbour.getId() + " has value of " + infectedChance.get(neighbour.getId()) + " for their infectedchance"); 
									}
								}
								
							}
						
						}
					}
					
				}
				
				possibleInfects.put(m.getId(), pInfects);
				System.out.println("Node " + m.getId() + " has possibleInfect vaule of " + possibleInfects.get(m.getId()));
				System.out.println("Node " + m.getId() + " has infectedChance of " + infectedChance.get(m.getId()));
				System.out.println("Node " + m.getId() + " has value of: " + (infectedChance.get(m.getId())*possibleInfects.get(m.getId())));
				m.setValue(pInfects * infectedChance.get(m.getId()));
				System.out.println("Node " + m.getId() + " has actual value of " + m.getValue());
				nodesToSort.add(m);
				
			}
			
			encountered.addAll(neighbours);
			hopNeighbours.clear();
			hopNeighbours.addAll(neighbours);
			currHop = currHop + 1;
		}

			
		
		Comparator<MultiProbabilityNode> comp = new MultiProbabilityComparator<MultiProbabilityNode>();
		SortedSet<MultiProbabilityNode> sortedN = new TreeSet<MultiProbabilityNode>(comp);
		
		for (MultiProbabilityNode m : nodesToSort) {
			sortedN.add(m);
		}
		
		System.out.println("Nodes sorted by value:");
		System.out.println("Size of nodes to sort: " + nodesToSort.size());
		System.out.println("Size of nodes to sort: " + sortedN.size());
		Iterator<MultiProbabilityNode> iter = sortedN.iterator();
		
		while (iter.hasNext()) {
			MultiProbabilityNode m = iter.next();
			System.out.println("Node " + m.getId() + " has value of " + m.getValue());
		}
		
		
		while (seeds.size() < seedCount) {
			MultiProbabilityNode choice = sortedN.last();
			seeds.add(choice);
			System.out.println("We selected " + choice.getId() + " as our seed");
			nodesToSort.remove(choice);
			
			for (MultiProbabilityNode neighbour : g.getNeighbors(choice)) {
				System.out.println("Looking at neighbour " + neighbour.getId());
				Collection<TypedWeightedEdge> edges = g.findEdgeSet(choice, neighbour);
				if (edges != null) {
					System.out.println("Node is an outgoing neighbour");
					for (TypedWeightedEdge e : edges) {
						if (e.getConcept() == boostC.getId()) {
							System.out.println("Edge weight: " + e.getWeight());
							System.out.println("Infected chance is : " + infectedChance.get(neighbour.getId()));
							infectedChance.put(neighbour.getId(), infectedChance.get(neighbour.getId()) * (1 - e.getWeight()));
							System.out.println("New Infected chance is now: " + infectedChance.get(neighbour.getId()));
							break;
						}
					}
				}
				
				edges = g.findEdgeSet(neighbour, choice);
				if (edges != null) {
					System.out.println("Node is an incoming neighbour");
					for (TypedWeightedEdge e : edges) {
						if (e.getConcept() == boostC.getId()) {
							System.out.println("Edge weight: " + e.getWeight());
							double edgeChance = e.getWeight() * (1 + keyC.getConceptInternal(boostC));
							if (edgeChance > 1) {edgeChance = 1;}
							System.out.println("Contribution is: " + edgeChance);
							
							double newEdgeChance = e.getWeight() * (1 + keyC.getConceptInternal(boostC)) * (1 + keyC.getConceptExternal(boostC));
							if (newEdgeChance > 1) {newEdgeChance = 1;}
							System.out.println("New contribution is: " + newEdgeChance);
							
							double oldPossibleInfects = possibleInfects.get(neighbour.getId());
							System.out.println("Current possibleInfects: " + possibleInfects.get(neighbour.getId()));
							oldPossibleInfects = oldPossibleInfects - edgeChance;
							double newPossibleInfects = oldPossibleInfects + newEdgeChance;
							possibleInfects.put(neighbour.getId(), newPossibleInfects);
							System.out.println("New possibleInfects: " + possibleInfects.get(neighbour.getId()));
							break;
						}
					}
				}
				
				System.out.println("Current possibleInfects: " + possibleInfects.get(neighbour.getId()));
				System.out.println("New Infected chance is now: " + infectedChance.get(neighbour.getId()));
				neighbour.setValue(infectedChance.get(neighbour.getId()) * possibleInfects.get(neighbour.getId()));
				System.out.println("new value for " + neighbour.getId() + " is " + neighbour.getValue());
			}
			
			sortedN.clear();
			for (MultiProbabilityNode m : nodesToSort) {
				sortedN.add(m);
			}
			
			System.out.println("Nodes sorted by value:");
			iter = sortedN.iterator();
			
			while (iter.hasNext()) {
				MultiProbabilityNode m = iter.next();
				System.out.println("Node " + m.getId() + " has value of " + m.getValue());
			}
			
			
		}
		
		
		return seeds;
		
	}
}
