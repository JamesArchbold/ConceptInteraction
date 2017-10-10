package MethodTesting;
//early experiments
import gnu.trove.map.hash.TIntDoubleHashMap;
import influence.concepts.Concept;
import influence.edges.TypedEdge;
import influence.graph.generation.ScalefreeGenerator;
import influence.graph.generation.SmallworldGenerator;
import influence.models.MultiIndependentCascade;
import influence.nodes.MultiProbabilityNode;
import influence.seed.selection.RandomSeeds;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;

public class Tests {

	private ArrayList<Concept> cList;
	private Concept keyCon;
	private int listChosen;
	
	public Tests(ArrayList<Concept> cList, Concept keyCon, int listChosen) {
		this.cList = cList;
		this.keyCon = keyCon;
		this.listChosen = listChosen;
	}
	
	public Tests(ArrayList<Concept> cList, Concept keyCon) {
		this.cList = cList;
		this.keyCon = keyCon;
		this.listChosen = 0;
	}
	
	public void burnInScaleFree(int nodeNum, int conNum, double boostProb, double inhibitProb, double prob, int seedCount, int delay, int seedOption, int seededNum, int interventionCount, String filepath, int initialNodes, int edgesToAdd, double range) {
		ScalefreeGenerator swg = new ScalefreeGenerator();
		
		File file = new File(filepath);
		FileWriter fw;
		BufferedWriter bw = null;
		
		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			bw.write("Scale free graph\n");
			bw.write("Concepts:" + conNum + "\n");
			bw.write("Key Concept:" + keyCon.getId() + "\n");
			bw.write("Boost Proportion:" + boostProb + "\n");
			bw.write("Inhibit Proportion:" + inhibitProb + "\n");
			bw.write("probability:" + prob + "\n");
			bw.write("seedCount:" + seedCount + "\n");
			bw.write("runs:100\n");
			bw.write("turn delay:" + delay + "\n");
			bw.write("seed selector:" + seedOption + "\n");
			bw.write("Nodes in graph:" + nodeNum + "\n");
			bw.write("Range of Gaussian:" + range + "\n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		RandomSeeds selector = new RandomSeeds(seededNum);
		
		TIntDoubleHashMap probs = new TIntDoubleHashMap();
		for (Concept c : cList) {
			probs.put(c.getId(), prob);	
		}
		
		for (int i = 0; i < 100; i++) {
			try {
			//	System.out.println("------------ Run " + i + " ------------\n");
				bw.write("------------ Run " + i + " ------------\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Graph<MultiProbabilityNode, TypedEdge> g = swg.undirectedMultiGraphMakerSplit(nodeNum, initialNodes, edgesToAdd, cList, seededNum+i);
			HashMap<Integer, Set<MultiProbabilityNode>> seeds = new HashMap<Integer, Set<MultiProbabilityNode>>();
			for (Concept c : cList) {
				seeds.put(c.getId(), selector.getSeeds(g, seedCount));
			}
			
			MultiIndependentCascade mIc = new MultiIndependentCascade(probs, cList);
			ArrayList<Double> avgSeedEnv = new ArrayList<Double>();
			if (delay > 0) {
				
				List<HashMap<Integer, Set<MultiProbabilityNode>>> activated = mIc.runBurnIn(g, seeds, keyCon, delay, seedOption, interventionCount, filepath, avgSeedEnv);
				
				for (Concept c : cList) {
					try {
						bw.write("Concept " + c.getId() + " Total: " + activated.get(0).get(c.getId()).size()+"\n");
						bw.write("Concept " + c.getId() + " Post Intervention: " + activated.get(1).get(c.getId()).size()+"\n");
						bw.write("Concept " + c.getId() + " Pre Intervention: " + (activated.get(0).get(c.getId()).size() - activated.get(1).get(c.getId()).size())+"\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			else {
				HashMap<Integer, Set<MultiProbabilityNode>> activated = mIc.run(g, seeds, avgSeedEnv);
				for (Concept c : cList) {
					try {
						bw.write("Concept " + c.getId() + " Total: " + activated.get(c.getId()).size()+"\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			for (MultiProbabilityNode m : g.getVertices()) {
				for (Concept c : cList) {
					m.deactivate(c);
				}
			}
			//System.out.println("Run took: " + (System.currentTimeMillis() - start) + "milliseconds");
		}
		
		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void burnInSmallWorld(int nodeNum, int conNum, double boostProb, double inhibitProb, double prob, int seedCount, int delay, int seedOption, int seededNum, int interventionCount, String filepath){
		SmallworldGenerator swg = new SmallworldGenerator();
		
		File file = new File(filepath);
		FileWriter fw;
		BufferedWriter bw = null;
		
		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			bw.write("Small World graph\n");
			bw.write("Concepts:" + conNum + "\n");
			bw.write("Key Concept:" + keyCon.getId() + "\n");
			bw.write("Boost Proportion:" + boostProb + "\n");
			bw.write("Inhibit Proportion:" + inhibitProb + "\n");
			bw.write("probability:" + prob + "\n");
			bw.write("seedCount:" + seedCount + "\n");
			bw.write("runs:100\n");
			bw.write("turn delay:" + delay + "\n");
			bw.write("seed selector:" + seedOption + "\n");
			bw.write("Nodes in graph:" + nodeNum + "\n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		RandomSeeds selector = new RandomSeeds(seededNum);
		
		TIntDoubleHashMap probs = new TIntDoubleHashMap();
		for (Concept c : cList) {
			probs.put(c.getId(), prob);	
		}
		
		for (int i = 0; i < 100; i++) {
			try {
				bw.write("------------ Run " + i + " ------------\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int row = nodeNum / 100;
			Graph<MultiProbabilityNode, TypedEdge> g = swg.undirectedMultiGraphMakerSplit(row, 100, 0.5, cList, seededNum);
			
			HashMap<Integer, Set<MultiProbabilityNode>> seeds = new HashMap<Integer, Set<MultiProbabilityNode>>();
			for (Concept c : cList) {
				seeds.put(c.getId(), selector.getSeeds(g, seedCount));
			}
			
			MultiIndependentCascade mIc = new MultiIndependentCascade(probs, cList);
			ArrayList<Double> avgSeedEnv = new ArrayList<Double>();
			if (delay > 0) {
				
				List<HashMap<Integer, Set<MultiProbabilityNode>>> activated = mIc.runBurnIn(g, seeds, keyCon, delay, seedOption, interventionCount, filepath, avgSeedEnv);
				for (Concept c : cList) {
					try {
						bw.write("Concept " + c.getId() + " Total: " + activated.get(0).get(c.getId()).size()+"\n");
						bw.write("Concept " + c.getId() + " Post Intervention: " + activated.get(1).get(c.getId()).size()+"\n");
						bw.write("Concept " + c.getId() + " Pre Intervention: " + (activated.get(0).get(c.getId()).size() - activated.get(1).get(c.getId()).size())+"\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			else {
				HashMap<Integer, Set<MultiProbabilityNode>> activated = mIc.run(g, seeds, avgSeedEnv);
				for (Concept c : cList) {
					try {
						bw.write("Concept " + c.getId() + " Total: " + activated.get(c.getId()).size()+"\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
			for (MultiProbabilityNode m : g.getVertices()) {
				for (Concept c : cList) {
					m.deactivate(c);
				}
			}
			//System.out.println("Run took: " + (System.currentTimeMillis() - start) + "milliseconds");
		}
		
		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void scaleFree(int nodeNum, int init, int edgeNum, int conNum, double prob, int seedCount, int runs, int delay, int seedOption, String filepath) {
		ScalefreeGenerator swg = new ScalefreeGenerator();
		
		File file = new File(filepath);
		FileWriter fw;
		BufferedWriter bw = null;
		
		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			bw.write("Scale free graph\n");
			bw.write("initial nodes:" + init + "\n");
			bw.write("edges each turn:" + edgeNum + "\n");
			bw.write("Concepts:" + conNum + "\n");
			bw.write("Concept List:" + listChosen + "\n");
			bw.write("probability:" + prob + "\n");
			bw.write("seedCount:" + seedCount + "\n");
			bw.write("runs:" + runs + "\n");
			bw.write("turn delay:" + delay + "\n");
			bw.write("seed selector:" + seedOption + "\n");
			bw.write("Nodes in graph:" + nodeNum + "\n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//int row = nodeNum / 100;
		RandomSeeds selector = new RandomSeeds();
			
		TIntDoubleHashMap probs = new TIntDoubleHashMap();
		for (Concept c : cList) {
			probs.put(c.getId(), prob);	
		}
		
		for (int i = 0; i < runs; i++) {
			//long start = System.currentTimeMillis();
			try {
				bw.write("------------ Run " + i + " ------------\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Graph<MultiProbabilityNode, TypedEdge> g = swg.undirectedMultiGraphMakerSplit(nodeNum, init, edgeNum, cList);
			HashMap<Integer, Set<MultiProbabilityNode>> seeds = new HashMap<Integer, Set<MultiProbabilityNode>>();
			for (Concept c : cList) {
				if (!c.equals(keyCon)) {
					seeds.put(c.getId(), selector.getSeeds(g, seedCount));
				}
			}
			
			MultiIndependentCascade mIc = new MultiIndependentCascade(probs, cList);
			
			Double avgSeedEnv = 0.0;
			HashMap<Integer, Set<MultiProbabilityNode>> activated = mIc.run(g, seeds, keyCon, delay, seedOption, seedCount, filepath, avgSeedEnv);
			
			for (Concept c : cList) {
				try {
					bw.write("Concept " + c.getId() + " : " + activated.get(c.getId()).size()+"\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			try {
				bw.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			for (MultiProbabilityNode m : g.getVertices()) {
				for (Concept c : cList) {
					m.deactivate(c);
				}
			}
			//System.out.println("Run took: " + (System.currentTimeMillis() - start) + "milliseconds");
		}
		
		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void smallWorld(int nodeNum, double expo, int conNum, double prob, int seedCount, int runs, int delay, int seedOption, String filepath) {
		SmallworldGenerator swg = new SmallworldGenerator();
		
		File file = new File(filepath);
		FileWriter fw;
		BufferedWriter bw = null;
		
		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			bw.write("Small world graph\n");
			bw.write("Concepts:" + conNum + "\n");
			bw.write("clustering exponent:" + expo + "\n");
			bw.write("Concept List:" + listChosen + "\n");
			bw.write("probability:" + prob + "\n");
			bw.write("seedCount:" + seedCount + "\n");
			bw.write("runs:" + runs + "\n");
			bw.write("turn delay:" + delay + "\n");
			bw.write("seed selector:" + seedOption + "\n");
			bw.write("Nodes in graph:" + nodeNum + "\n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		int row = nodeNum / 100;
		RandomSeeds selector = new RandomSeeds();
			
		TIntDoubleHashMap probs = new TIntDoubleHashMap();
		for (Concept c : cList) {
			probs.put(c.getId(), prob);	
		}
		
		for (int i = 0; i < runs; i++) {
			//long start = System.currentTimeMillis();
			try {
				bw.write("------------ Run " + i + " ------------\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Graph<MultiProbabilityNode, TypedEdge> g = swg.undirectedMultiGraphMakerSplit(row, 100, expo, cList);
			HashMap<Integer, Set<MultiProbabilityNode>> seeds = new HashMap<Integer, Set<MultiProbabilityNode>>();
			for (Concept c : cList) {
				if (!c.equals(keyCon)) {
					seeds.put(c.getId(), selector.getSeeds(g, seedCount));
				}
			}
			
			MultiIndependentCascade mIc = new MultiIndependentCascade(probs, cList);
			
			Double avgSeedEnv = 0.0;
			HashMap<Integer, Set<MultiProbabilityNode>> activated = mIc.run(g, seeds, keyCon, delay, seedOption, seedCount, filepath, avgSeedEnv);
			
			for (Concept c : cList) {
				try {
					bw.write("Concept " + c.getId() + " : " + activated.get(c.getId()).size() + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			try {
				bw.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			for (MultiProbabilityNode m : g.getVertices()) {
				for (Concept c : cList) {
					m.deactivate(c);
				}
			}
		//	System.out.println("Run took: " + (System.currentTimeMillis() - start) + "milliseconds");
		}
		
		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
