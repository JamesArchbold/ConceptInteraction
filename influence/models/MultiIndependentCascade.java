package influence.models;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntDoubleHashMap;
import influence.concepts.Concept;
import influence.edges.TypedEdge;
import influence.nodes.MultiProbabilityNode;
import influence.seed.selection.Degree;
import influence.seed.selection.DegreeDiscount;
import influence.seed.selection.ExpectedInfectedDiscount;
import influence.seed.selection.RandomSeeds;
import influence.seed.selection.SingleDiscount;
import influence.seed.selection.WeightedDiscount;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;

public class MultiIndependentCascade {
	
	TIntArrayList sizes;
	TIntArrayList sizes2;
	TIntDoubleHashMap probabilityOfInfection;
	ArrayList<Concept> conceptsList;
	
	public MultiIndependentCascade(TIntDoubleHashMap prob, ArrayList<Concept> cList) {
		//this.concepts = c;
		this.conceptsList = cList;
		probabilityOfInfection = prob;
		sizes = new TIntArrayList();
		sizes2 = new TIntArrayList();
	}

	public HashMap<Integer, Set<MultiProbabilityNode>> run(Graph<MultiProbabilityNode, TypedEdge> g, HashMap<Integer, Set<MultiProbabilityNode>> seeds, Concept keyCon, int delay, int option, int seedCount, String filepath, Double avgSeedRun) {
		Collection<MultiProbabilityNode> neighbours;
		
		HashMap<Integer, Set<MultiProbabilityNode>> activeThisRound = new HashMap<Integer, Set<MultiProbabilityNode>>();
		HashMap<Integer, Set<MultiProbabilityNode>> activated = new HashMap<Integer, Set<MultiProbabilityNode>>();
		HashMap<Integer, Set<MultiProbabilityNode>> toActivate = new HashMap<Integer, Set<MultiProbabilityNode>>();
		
		boolean converged = false;

		for (Concept c : conceptsList) {
			if (seeds.containsKey(c.getId())) {
				for (MultiProbabilityNode m : seeds.get(c.getId())){
					m.activate(c);
				}
				
				Set<MultiProbabilityNode> toAdd = new HashSet<MultiProbabilityNode>();
				toAdd.addAll(seeds.get(c.getId()));
				
				Set<MultiProbabilityNode> toAdd2 = new HashSet<MultiProbabilityNode>();
				toAdd2.addAll(seeds.get(c.getId()));
				
				activeThisRound.put(c.getId(), toAdd);
				activated.put(c.getId(), toAdd2);
				
				toActivate.put(c.getId(), new HashSet<MultiProbabilityNode>());		
			}
		}
		int countdown = delay;
		
		while (!converged || countdown > 0 ) {
			converged = true;
			
			for (Concept c : conceptsList) {
				if (seeds.containsKey(c.getId())) {
					for (MultiProbabilityNode s : activeThisRound.get(c.getId())) {
						neighbours = g.getNeighbors(s);
						System.out.println("----Considering active node " + s.getId() + " -----------------");
						for (MultiProbabilityNode n : neighbours) {
							if (!n.isActivated(c)) {
								Collection<TypedEdge> edges = g.findEdgeSet(s, n);
								
								boolean canTravel = false;
								for (TypedEdge ee : edges) {
									if (ee.getConcept() == c.getId()) {
										canTravel = true;
										break;
									}
								}
								if (canTravel) {
									double chance = calculateChance(s, n, c);
									System.out.println("CHANCE IS; " + chance);
									double rr = Math.random();
									
									if (rr < chance) {
										n.activate(c);
										toActivate.get(c.getId()).add(n);
										converged = false;
									}
								}
							}
						}
					}
				}
			}
			

			for (Concept c : conceptsList) {
				if (seeds.containsKey(c.getId())) {
					activated.get(c.getId()).addAll(activeThisRound.get(c.getId()));
					activeThisRound.get(c.getId()).clear();
					activeThisRound.get(c.getId()).addAll(toActivate.get(c.getId()));
					toActivate.get(c.getId()).clear();
				}
			}
			
			countdown--;
			System.out.println(countdown);
			if (countdown == 0) {
				System.out.println("selecting seeds");
				converged = false;
				Set<MultiProbabilityNode> keySeeds = getSeeds(g, keyCon, option, seedCount);
				for (MultiProbabilityNode m : keySeeds){
					m.activate(keyCon);
				}
				
				seeds.put(keyCon.getId(), keySeeds);
				Set<MultiProbabilityNode> toAdd = new HashSet<MultiProbabilityNode>();
				toAdd.addAll(keySeeds);
				Set<MultiProbabilityNode> toAdd2 = new HashSet<MultiProbabilityNode>();
				toAdd2.addAll(keySeeds);
				activeThisRound.put(keyCon.getId(), toAdd);
				activated.put(keyCon.getId(), toAdd2);
				toActivate.put(keyCon.getId(), new HashSet<MultiProbabilityNode>());
			}
		}
		return activated;
		
	}

	public List<HashMap<Integer, Set<MultiProbabilityNode>>> runBurnIn(Graph<MultiProbabilityNode, TypedEdge> g, HashMap<Integer, Set<MultiProbabilityNode>> seeds, Concept keyCon, int delay, int option, int seedCount, String filepath, ArrayList<Double> avgSeedEnv) {
		Collection<MultiProbabilityNode> neighbours;
		
		HashMap<Integer, Set<MultiProbabilityNode>> activeThisRound = new HashMap<Integer, Set<MultiProbabilityNode>>();
		HashMap<Integer, Set<MultiProbabilityNode>> activated = new HashMap<Integer, Set<MultiProbabilityNode>>();
		HashMap<Integer, Set<MultiProbabilityNode>> toActivate = new HashMap<Integer, Set<MultiProbabilityNode>>();
		HashMap<Integer, Set<MultiProbabilityNode>> activatePostBurn = new HashMap<Integer, Set<MultiProbabilityNode>>();
		boolean converged = false;
		
		for (Concept c : conceptsList) {
			for (MultiProbabilityNode m : seeds.get(c.getId())){
				m.activate(c);
			}
			
			Set<MultiProbabilityNode> toAdd = new HashSet<MultiProbabilityNode>();
			toAdd.addAll(seeds.get(c.getId()));
			
			Set<MultiProbabilityNode> toAdd2 = new HashSet<MultiProbabilityNode>();
			toAdd2.addAll(seeds.get(c.getId()));
			
			activeThisRound.put(c.getId(), toAdd);
			activated.put(c.getId(), toAdd2);
			
			toActivate.put(c.getId(), new HashSet<MultiProbabilityNode>());		
		}
		int countdown = delay;
		
		while (!converged || countdown > 0 ) {
			converged = true;
			
			for (Concept c : conceptsList) {
				for (MultiProbabilityNode s : activeThisRound.get(c.getId())) {
					neighbours = g.getNeighbors(s);
				//	System.out.println("----Considering active node " + s.getId() + " -----------------");
					for (MultiProbabilityNode n : neighbours) {
						if (!n.isActivated(c)) {
							Collection<TypedEdge> edges = g.findEdgeSet(s, n);
							
							boolean canTravel = false;
							for (TypedEdge ee : edges) {
								if (ee.getConcept() == c.getId()) {
									canTravel = true;
									break;
								}
							}
							if (canTravel) {
								double chance = calculateChance(s, n, c);
						//		System.out.println("CHANCE IS; " + chance);
								double rr = Math.random();
								
								if (rr < chance) {
									n.activate(c);
									toActivate.get(c.getId()).add(n);
									converged = false;
								}
							}
						}
					}
				}
			}
			

			for (Concept c : conceptsList) {
				activated.get(c.getId()).addAll(activeThisRound.get(c.getId()));
				if (countdown <= 0) {
					activatePostBurn.get(c.getId()).addAll(activeThisRound.get(c.getId()));
				}
				activeThisRound.get(c.getId()).clear();
				activeThisRound.get(c.getId()).addAll(toActivate.get(c.getId()));
				toActivate.get(c.getId()).clear();
			}
			
			countdown--;
		//	System.out.println(countdown);
			if (countdown == 0) {
			//	System.out.println("selecting seeds");
				converged = false;
				Set<MultiProbabilityNode> keySeeds = getSeedsBurnIn(g, keyCon, option, seedCount);
				double totalSeeEnv = 0;
				for (MultiProbabilityNode m : keySeeds){
					m.activate(keyCon);
					double seedEnvironment = keyCon.getInternalEnvironment(m);
					for (MultiProbabilityNode near : g.getNeighbors(m)) {
						seedEnvironment += keyCon.getExternalEnvironment(near);
					}
					totalSeeEnv += seedEnvironment;
				}
				
				avgSeedEnv.add(totalSeeEnv / seedCount);
				//System.out.println("In: " + avgSeedEnv.get(0));
				
				seeds.put(keyCon.getId(), keySeeds);
				Set<MultiProbabilityNode> toAdd = new HashSet<MultiProbabilityNode>();
				toAdd.addAll(keySeeds);
				Set<MultiProbabilityNode> toAdd2 = new HashSet<MultiProbabilityNode>();
				toAdd2.addAll(keySeeds);
				activeThisRound.get(keyCon.getId()).addAll(toAdd);
				activatePostBurn.put(keyCon.getId(), toAdd2);
				activated.get(keyCon.getId()).addAll(toAdd2);
				
				for (Concept c: conceptsList) {
					if (!c.equals(keyCon)) {
						activatePostBurn.put(c.getId(), new HashSet<MultiProbabilityNode>());
					}
				}
			}
		}
		ArrayList<HashMap<Integer, Set<MultiProbabilityNode>>> activatedLists = new ArrayList<HashMap<Integer, Set<MultiProbabilityNode>>>();
		activatedLists.add(activated);
		activatedLists.add(activatePostBurn);
		return activatedLists;
		
	}
	
	private Set<MultiProbabilityNode> getSeedsBurnIn(Graph<MultiProbabilityNode, TypedEdge> g, Concept keyCon, int option, int seedCount) {
		switch (option){
			case 0: DegreeDiscount dd = new DegreeDiscount();
					return dd.getSeedsBurnIn(g, seedCount, probabilityOfInfection.get(keyCon.getId()), keyCon);
			case 1: SingleDiscount sd = new SingleDiscount();
					return SingleDiscount.getSeedsBurnIn(g, seedCount, keyCon);
			case 2: WeightedDiscount wd = new WeightedDiscount();
					return wd.getSeedsBurnIn(g, seedCount, keyCon);
			case 3: ExpectedInfectedDiscount ed = new ExpectedInfectedDiscount();
			//		return ExpectedInfectedDiscount.getSeedsBurnIn(g, seedCount, keyCon);
			case 4: RandomSeeds rd = new RandomSeeds();
					return rd.getSeedsBurnIn(g, seedCount, keyCon);
			case 5: Degree d = new Degree();
					return d.getSeedsBurnIn(g, seedCount, keyCon);
		}
		
		return null;
	}
	
	private Set<MultiProbabilityNode> getSeeds(Graph<MultiProbabilityNode, TypedEdge> g, Concept keyCon, int option, int seedCount) {
		switch (option){
			case 0: DegreeDiscount dd = new DegreeDiscount();
					return dd.getSeeds(g, seedCount, probabilityOfInfection.get(keyCon.getId()));
			case 1: SingleDiscount sd = new SingleDiscount();
					return SingleDiscount.getSeeds(g, seedCount);
			case 2: WeightedDiscount wd = new WeightedDiscount();
					return wd.getSeeds(g, seedCount, keyCon);
			case 3: ExpectedInfectedDiscount ed = new ExpectedInfectedDiscount();
					return ed.getSeeds(g, seedCount, probabilityOfInfection.get(keyCon.getId()), keyCon);
			case 4: RandomSeeds rd = new RandomSeeds();
					return rd.getSeeds(g, seedCount);
		}
		
		return null;
	}

	public HashMap<Integer, Set<MultiProbabilityNode>> run(Graph<MultiProbabilityNode, TypedEdge> g, HashMap<Integer, Set<MultiProbabilityNode>> seeds, ArrayList<Double> avgSeedEnv) {
		Collection<MultiProbabilityNode> neighbours;
		
		HashMap<Integer, Set<MultiProbabilityNode>> activeThisRound = new HashMap<Integer, Set<MultiProbabilityNode>>();
		HashMap<Integer, Set<MultiProbabilityNode>> activated = new HashMap<Integer, Set<MultiProbabilityNode>>();
		HashMap<Integer, Set<MultiProbabilityNode>> toActivate = new HashMap<Integer, Set<MultiProbabilityNode>>();
		
		boolean converged = false;

		for (Concept c : conceptsList) {
			if (seeds.containsKey(c.getId())) {
				for (MultiProbabilityNode m : seeds.get(c.getId())){
					m.activate(c);
				}
				Set<MultiProbabilityNode> toAdd = new HashSet<MultiProbabilityNode>();
				toAdd.addAll(seeds.get(c.getId()));
				
				Set<MultiProbabilityNode> toAdd2 = new HashSet<MultiProbabilityNode>();
				toAdd2.addAll(seeds.get(c.getId()));
				
				activeThisRound.put(c.getId(), toAdd);
				activated.put(c.getId(), toAdd2);
				
				toActivate.put(c.getId(), new HashSet<MultiProbabilityNode>());
			}
		}

		while (!converged) {
			converged = true;
			
			for (Concept c : conceptsList) {
				if (seeds.containsKey(c.getId())) {
					for (MultiProbabilityNode s : activeThisRound.get(c.getId())) {
						neighbours = g.getNeighbors(s);
					
						for (MultiProbabilityNode n : neighbours) {
							if (!n.isActivated(c)) {
								Collection<TypedEdge> edges = g.findEdgeSet(s, n);
								
								boolean canTravel = false;
								for (TypedEdge ee : edges) {
									if (ee.getConcept() == c.getId()) {
										canTravel = true;
										break;
									}
								}
								if (canTravel) {
									double chance = calculateChance(s, n, c);
									double rr = Math.random();
								//	System.out.println("With concepts " + c.getId() + " chance is: " + chance);
									if (rr < chance) {
										n.activate(c);
										toActivate.get(c.getId()).add(n);
										converged = false;
									}
								}
							}
						}
					}
				}
			}
			

			for (Concept c : conceptsList) {
				if (seeds.containsKey(c.getId())) {
					activated.get(c.getId()).addAll(activeThisRound.get(c.getId()));
					activeThisRound.get(c.getId()).clear();
					activeThisRound.get(c.getId()).addAll(toActivate.get(c.getId()));
					toActivate.get(c.getId()).clear();
				}
			}
		}
		return activated;
	}

	private double calculateChance(MultiProbabilityNode s, MultiProbabilityNode n, Concept c) {
		//System.out.println(s.getId());
		
		double iAddition = c.getInternalEnvironment(s);
		double eAddition = c.getExternalEnvironment(n);
		//System.out.println("e " + eAddition);
	//	System.out.println("i " + iAddition);
	//	System.out.println("c " + c.getId());
		if (eAddition > 0.5) { eAddition = 0.5;}
		else if (eAddition < -0.5) { eAddition = -0.5;}
		
		if (iAddition > 0.5) { iAddition = 0.5;}
		else if (iAddition < -0.5) { iAddition = -0.5;}
		
		return (probabilityOfInfection.get(c.getId()) * (1 + iAddition + eAddition));
	}

}
