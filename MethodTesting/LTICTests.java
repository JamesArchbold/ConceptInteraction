package MethodTesting;
//early version of advancedTests
import influence.concepts.Concept;
import influence.concepts.DiverseICConcept;
import influence.concepts.DiverseLTConcept;
import influence.edges.TypedWeightedEdge;
import influence.graph.generation.GraphReaderWriter;
import influence.nodes.MultiProbabilityNode;
import influence.seed.selection.BoostingDiscount;
import influence.seed.selection.BoostingPlacement;
import influence.seed.selection.Degree;
import influence.seed.selection.DegreeDiscount;
import influence.seed.selection.PMAIBoostHeuristic;
import influence.seed.selection.PMAIHeuristc;
import influence.seed.selection.SingleDiscount;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

public class LTICTests {

	public static void main(String[] args) {
		//args[0] is seed size
		//args[1] is graph size
		//args[2] is the filename
		//args[3] is the method of seed selection for the boosting concept where: 0 is random, 1 is the same set as the key concept, 3 is degree, 2 is single discount
		//4 - boostingoutgoing 5-boostingall 6- boostingoutgoinghops 7-boostingallhops, 8-Current heuristic, 9 - degree discount
		//args[4] is boosting relationship
		//args[5] is LTThresh
		//args[6] is ICProb
		//args[7] is number of run
//		args = new String[5];
//		args[4] = "5";
//		args[0] = "100";
//		args[1] = "1000";
//		args[2] = "output";
//		args[3] = "2";

	//	LTBoostingLTStandard(Integer.parseInt(args[0]), Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]), Integer.parseInt(args[7]), Double.parseDouble(args[4]), Double.parseDouble(args[5]), Double.parseDouble(args[6]));
	//	ICBoostingLTStandard(Integer.parseInt(args[0]), Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]), Integer.parseInt(args[7]), Double.parseDouble(args[4]), Double.parseDouble(args[5]), Double.parseDouble(args[6]));
	//	LTBoostingICStandard(Integer.parseInt(args[0]), Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]), Integer.parseInt(args[7]), Double.parseDouble(args[4]), Double.parseDouble(args[5]), Double.parseDouble(args[6]));
	//	ICBoostingICStandard(Integer.parseInt(args[0]), Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]), Integer.parseInt(args[7]), Double.parseDouble(args[4]), Double.parseDouble(args[5]), Double.parseDouble(args[6]));
		singleRunICBoostingICStandard(Integer.parseInt(args[0]), Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]), Integer.parseInt(args[7]), Double.parseDouble(args[4]), Double.parseDouble(args[5]), Double.parseDouble(args[6]));
		
//		int seedSize[] = {10,25,50,100};
//		int graphSize = 1000;
//		int seedType = 16;
//		double boost = 0.8;
//		double thresh = 0.8;
//		double icprob = 0.1;
//		
//		for (int j = 16; j < 20; j++) {
//			for (int k = 0; k < 4; k++) {
//				for (int i = 0; i < 100; i++) {
//					seedType = j;
//					String fileName = "oldTests/" + seedSize[k] + "_" + graphSize + "_" + seedType;
//					singleRunICBoostingICStandard(seedSize[k], graphSize, fileName, seedType, i, boost, thresh, icprob);
//				}
//			}
//		}
//		
//		graphSize = 10000;
//		for (int j = 16; j < 20; j++) {
//			for (int k = 0; k < 4; k++) {
//				for (int i = 0; i < 100; i++) {
//					seedType = j;
//					String fileName = "oldTests/" + seedSize[k] + "_" + graphSize + "_" + seedType;
//					singleRunICBoostingICStandard(seedSize[k], graphSize, fileName, seedType, i, boost, thresh, icprob);
//				}
//			}
//		}
		
	}
	public static void LTPureStandard(int seedSize, int graphSize, String filename, int seedSelection, int runs, double LTThresh, double ICProb) {
		GraphReaderWriter grw = new GraphReaderWriter();
		
		File file = new File(filename + "_LTPure.txt");
		FileWriter fw;
		BufferedWriter bw = null;
		
		//Concept ic = new DiverseICConcept(1);
		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			bw.write("Size of graph: " + graphSize + "\n");
			bw.write("Uniform Threshold: false \n");
			bw.write("Threshold Average: " + LTThresh + "\n");
			bw.write("Independent Cascade Infection rate: " + ICProb + "\n");
			bw.write("Number of runs: 1000\n");
			bw.write("Size of seed set: " + seedSize + "\n");
			bw.write("Seed Selection: " + seedSelection + "\n");
			bw.write("----------------------------" + "\n");
			//bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < runs; i++){
			Concept lt = new DiverseLTConcept(0);
			try {
				bw.write("Run " + i + "\n");
				//bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			ArrayList<Concept> cList = new ArrayList<Concept>();
			cList.add(lt);
			//cList.add(ic);
			Graph<MultiProbabilityNode, TypedWeightedEdge> smg = grw.graphReadIn(graphSize, i, cList, false, LTThresh, ICProb);
			HashSet<MultiProbabilityNode> seedsLT = new HashSet<MultiProbabilityNode>();
			Random rand = new Random(i);
			
			HashMap<Integer, MultiProbabilityNode> nodes = new HashMap<Integer, MultiProbabilityNode>();
			for (MultiProbabilityNode n : smg.getVertices()) {
				nodes.put(n.getId(), n);
			}
			
			while (seedsLT.size() < seedSize) {
				int id = rand.nextInt(graphSize);
				nodes.get(id).activate(lt);
				seedsLT.add(nodes.get(id));
				//System.out.println(id);
			}
			
			DiverseLTConcept ltC = (DiverseLTConcept) cList.get(0);
			
			HashMap<Integer, Set<MultiProbabilityNode>> activated = new HashMap<Integer, Set<MultiProbabilityNode>>();
			activated.put(ltC.getId(), seedsLT);

			boolean spread = true;
			boolean spreadLT = true;
		
			int sizeLT = activated.get(ltC.getId()).size();
			int t = 0;
//			String list = "";
//			list = "";
//			for (MultiProbabilityNode n : activated.get(ltC.getId())) {
//				list = list + n.getId() + ", ";
//			}
//			list = list.substring(0, list.length() - 2);
//			try {
//				bw.write("Initial seeds for LT: " + list + "\n");
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			
			while (spread) {
				sizeLT = activated.get(ltC.getId()).size();
				if (spreadLT) {
					ltC.spread(smg, activated);
				}
				
				if (spreadLT){
					ltC.activateNodes(activated);
				}

				if (activated.get(ltC.getId()).size() <= sizeLT) {spreadLT = false;}
	
				if (!spreadLT) {
					spread = false;
				}
				
				try {
					bw.write("Timestep " + t + "\n");
					bw.write("Infections LT: " + activated.get(ltC.getId()).size() + "\n");
				//	bw.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				t++;
			}
			try {
				bw.write("Final Result" + "\n");
				bw.write("Infections LT: " + activated.get(ltC.getId()).size() + "\n");
//				list = "";
//				for (MultiProbabilityNode n : activated.get(ltC.getId())) {
//					list = list + n.getId() + ", ";
//				}
//				list = list.substring(0, list.length() - 2);
//				bw.write("Nodes with LT Active: " + list + "\n");
				bw.write("----------------------------\n");
			//bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void ICPureStandard(int seedSize, int graphSize, String filename, int seedSelection, int runs, double LTThresh, double ICProb) {
		GraphReaderWriter grw = new GraphReaderWriter();
		
		File file = new File(filename + "_ICPure.txt");
		FileWriter fw;
		BufferedWriter bw = null;

		//Concept ic = new DiverseICConcept(1);
		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			bw.write("Size of graph: " + graphSize + "\n");
			bw.write("Uniform Threshold: false \n");
			bw.write("Threshold Average: " + LTThresh + "\n");
			bw.write("Independent Cascade Infection rate: " + ICProb + "\n");
			bw.write("Number of runs: 1000\n");
			bw.write("Size of seed set: " + seedSize + "\n");
			bw.write("Seed Selection: " + seedSelection + "\n");
			bw.write("----------------------------" + "\n");
			//bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < runs; i++){
			Concept ic = new DiverseICConcept(0, i);
			try {
				bw.write("Run " + i + "\n");
				//bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			ArrayList<Concept> cList = new ArrayList<Concept>();
			cList.add(ic);
			//cList.add(ic);
			Graph<MultiProbabilityNode, TypedWeightedEdge> smg = grw.graphReadIn(graphSize, i, cList, false, LTThresh, ICProb);
			HashSet<MultiProbabilityNode> seedsIC = new HashSet<MultiProbabilityNode>();
			Random rand = new Random(i);
			
			HashMap<Integer, MultiProbabilityNode> nodes = new HashMap<Integer, MultiProbabilityNode>();
			for (MultiProbabilityNode n : smg.getVertices()) {
				nodes.put(n.getId(), n);
			}
			
			while (seedsIC.size() < seedSize) {
				int id = rand.nextInt(graphSize);
				nodes.get(id).activate(ic);
				seedsIC.add(nodes.get(id));
				//System.out.println(id);
			}
			
			DiverseICConcept icC = (DiverseICConcept) cList.get(0);
			
			HashMap<Integer, Set<MultiProbabilityNode>> activated = new HashMap<Integer, Set<MultiProbabilityNode>>();
			activated.put(icC.getId(), seedsIC);

			boolean spread = true;
			boolean spreadIC = true;
		
			int sizeIC = activated.get(icC.getId()).size();
			int t = 0;
			String list = "";
			icC.seedInitialise(activated.get(icC.getId()));
//			list = "";
//			for (MultiProbabilityNode n : activated.get(icC.getId())) {
//				list = list + n.getId() + ", ";
//			}
//			list = list.substring(0, list.length() - 2);
//			try {
//				bw.write("Initial seeds for IC: " + list + "\n");
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			while (spread) {
				sizeIC = activated.get(icC.getId()).size();
				if (spreadIC) {
					icC.spread(smg, activated);
				}
				
				if (spreadIC){
					icC.activateNodes(activated);
				}

				if (activated.get(icC.getId()).size() <= sizeIC) {spreadIC = false;}
	
				if (!spreadIC) {
					spread = false;
				}
				
				try {
					bw.write("Timestep " + t + "\n");
					bw.write("Infections IC: " + activated.get(icC.getId()).size() + "\n");
				//	//bw.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				t++;
			}
			try {
				bw.write("Final Result" + "\n");
				bw.write("Infections IC: " + activated.get(icC.getId()).size() + "\n");
//				list = "";
//				for (MultiProbabilityNode n : activated.get(icC.getId())) {
//					list = list + n.getId() + ", ";
//				}
//				list = list.substring(0, list.length() - 2);
//				bw.write("Nodes with IC Active: " + list + "\n");
				bw.write("----------------------------\n");
				////bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void LTBoostingLTStandard(int seedSize, int graphSize, String filename, int seedSelection, int runs, double interaction, double LTThresh, double ICProb){
        GraphReaderWriter grw = new GraphReaderWriter();
		File file = new File(filename + "_LTBoostLT.txt");
		FileWriter fw;
		BufferedWriter bw = null;

		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			bw.write("Size of graph: " + graphSize + "\n");
			bw.write("Uniform Threshold: false \n");
			bw.write("Threshold Average: " + LTThresh + "\n");
			bw.write("Independent Cascade Infection rate: " + ICProb + "\n");
			bw.write("Number of runs: 1000\n");
			bw.write("Size of seed set: " + seedSize + "\n");
			bw.write("Seed Selection: " + seedSelection + "\n");
			bw.write("----------------------------" + "\n");
			//bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < runs; i++){
			Concept lt2 = new DiverseLTConcept(1);
			Concept lt = new DiverseLTConcept(0);
			lt.addConceptInteractions(lt2, interaction, interaction);
			System.out.println("Run " + i + " for " + file.getName() + " with a seed size of " + seedSize);
			try {
				bw.write("Run " + i + "\n");
				bw.write(System.currentTimeMillis() + "\n");
				//bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			ArrayList<Concept> cList = new ArrayList<Concept>();
			cList.add(lt);
			cList.add(lt2);
			Graph<MultiProbabilityNode, TypedWeightedEdge> smg = grw.graphReadIn(graphSize, i, cList, false, LTThresh, ICProb);
			HashSet<MultiProbabilityNode> seedsLT2 = new HashSet<MultiProbabilityNode>();
			HashSet<MultiProbabilityNode> seedsLT = new HashSet<MultiProbabilityNode>();
			Random rand = new Random(i);
			HashMap<Integer, MultiProbabilityNode> nodes = new HashMap<Integer, MultiProbabilityNode>();
			for (MultiProbabilityNode n : smg.getVertices()) {
				nodes.put(n.getId(), n);
			}
			while (seedsLT.size() < seedSize) {
				int id = rand.nextInt(graphSize);
				nodes.get(id).activate(lt);
				seedsLT.add(nodes.get(id));
				
				if (seedSelection == 1) {
					nodes.get(id).activate(lt2);
					seedsLT2.add(nodes.get(id));
				}
				//System.out.println(id);
			}
			
			if (seedSelection != 1) {
				seedSelection(lt, lt2, smg, seedSize, seedsLT, seedsLT2, seedSelection, rand, interaction, graphSize, nodes);
			}

			DiverseLTConcept ltC = (DiverseLTConcept) cList.get(0);
			DiverseLTConcept ltC2 = (DiverseLTConcept) cList.get(1);
			
			HashMap<Integer, Set<MultiProbabilityNode>> activated = new HashMap<Integer, Set<MultiProbabilityNode>>();
			activated.put(ltC.getId(), seedsLT);
			activated.put(ltC2.getId(), seedsLT2);
			//System.out.println("Size of IC seed set:" + seedsIC.size());
//			icC.seedInitialise(seedsLT2);
			boolean spread = true;
			boolean spreadLT = true;
			boolean spreadIC = true;
			int sizeLT = activated.get(ltC.getId()).size();
			int sizeIC = activated.get(ltC2.getId()).size();
			int t = 0;
			String list = "";
			
//			list = "";
//			for (MultiProbabilityNode n : activated.get(ltC.getId())) {
//				list = list + n.getId() + ", ";
//			}
//			list = list.substring(0, list.length() - 2);
//			try {
//				bw.write("Initial seeds for LT: " + list + "\n");
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			
//			list = "";
//			for (MultiProbabilityNode n : activated.get(ltC2.getId())) {
//				list = list + n.getId() + ", ";
//			}
//			list = list.substring(0, list.length() - 2);
//			try {
//				bw.write("Initial seeds for LT2: " + list + "\n");
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			if (seedSelection > 9) {spreadIC = false;}
			while (spread) {
				sizeLT = activated.get(ltC.getId()).size();
				sizeIC = activated.get(ltC2.getId()).size();
				if (spreadLT) {
					ltC.spread(smg, activated);
				}
			//	System.out.println("--------------------------------");
				
				if (spreadIC) {
					ltC2.spread(smg, activated);
				}
				
				if (spreadLT){
					ltC.activateNodes(activated);
				}
				if (spreadIC) {
					ltC2.activateNodes(activated);
				}

			//	System.out.println("LT:" + activated.get(0).size());
			//	System.out.println("IC:" + activated.get(1).size());
			//	System.out.println("-------------------------");
				if (activated.get(ltC.getId()).size() <= sizeLT) {spreadLT = false;}
				if (activated.get(ltC2.getId()).size() <= sizeIC) {spreadIC = false;}
				if (!spreadLT && !spreadIC) {
					spread = false;
				}
				
				try {
					bw.write("Timestep " + t + "\n");
					bw.write("Infections LT: " + activated.get(ltC.getId()).size() + "\n");
					bw.write("Infections LT2: " + activated.get(ltC2.getId()).size() + "\n");
					//bw.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				t++;
			}
			try {
				bw.write("Final Result" + "\n");
				bw.write("Infections LT: " + activated.get(ltC.getId()).size() + "\n");
//				list = "";
//				for (MultiProbabilityNode n : activated.get(ltC.getId())) {
//					list = list + n.getId() + ", ";
//				}
//				list = list.substring(0, list.length() - 2);
//				bw.write("Nodes with LT Active: " + list + "\n");
				bw.write("Infections LT2: " + activated.get(ltC2.getId()).size() + "\n");
//				list = "";
//				for (MultiProbabilityNode n : activated.get(ltC2.getId())) {
//					list = list + n.getId() + ", ";
//				}
//				list = list.substring(0, list.length() - 2);
//				bw.write("Nodes with LT2 Active: " + list + "\n");
				//bw.flush();
				bw.write(System.currentTimeMillis() + "\n");
				bw.write("----------------------------\n");
				//bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			System.out.println("Final Number: " +  activated.get(ltC.getId()).size());
		}
		try {
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void ICBoostingLTStandard(int seedSize, int graphSize, String filename, int seedSelection, int runs, double interaction, double LTThresh, double ICProb) {
		GraphReaderWriter grw = new GraphReaderWriter();
		File file = new File(filename + "_ICBoostLT.txt");
		FileWriter fw;
		BufferedWriter bw = null;
		

		
		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			bw.write("Size of graph: " + graphSize + "\n");
			bw.write("Uniform Threshold: false \n");
			bw.write("Threshold Average: " + LTThresh + "\n");
			bw.write("Independent Cascade Infection rate: " + ICProb + "\n");
			bw.write("Number of runs: 1000\n");
			bw.write("Size of seed set: " + seedSize + "\n");
			bw.write("Seed Selection: " + seedSelection + "\n");
			bw.write("----------------------------" + "\n");
			//bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < runs; i++){
			Concept ic = new DiverseICConcept(0, i);
			Concept lt = new DiverseLTConcept(1);
			lt.addConceptInteractions(ic, interaction, interaction);
			
			System.out.println("Run " + i + " for " + file.getName() + " with a seed size of " + seedSize);
			try {
				bw.write("Run " + i + "\n");
				bw.write(System.currentTimeMillis() + "\n");
				//bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			ArrayList<Concept> cList = new ArrayList<Concept>();
			cList.add(ic);
			cList.add(lt);
			Graph<MultiProbabilityNode, TypedWeightedEdge> smg = grw.graphReadIn(graphSize, i, cList, false, 0.8, 0.1);
			HashSet<MultiProbabilityNode> seedsIC = new HashSet<MultiProbabilityNode>();
			HashSet<MultiProbabilityNode> seedsLT = new HashSet<MultiProbabilityNode>();
			//int seedSize = 500;
			Random rand = new Random(i);
			HashMap<Integer, MultiProbabilityNode> nodes = new HashMap<Integer, MultiProbabilityNode>();
			for (MultiProbabilityNode n : smg.getVertices()) {
				nodes.put(n.getId(), n);
			}
			while (seedsLT.size() < seedSize) {
				int id = rand.nextInt(graphSize);
				nodes.get(id).activate(lt);
				seedsLT.add(nodes.get(id));
				if (seedSelection == 1) {
					nodes.get(id).activate(ic);
					seedsIC.add(nodes.get(id));
				}
			}
			
			if (seedSelection != 1) {
				seedSelection(lt, ic, smg, seedSize, seedsLT, seedsIC, seedSelection, rand, interaction, graphSize, nodes);
			}
			
			DiverseLTConcept ltC = (DiverseLTConcept) cList.get(1);
			DiverseICConcept icC = (DiverseICConcept) cList.get(0);
			
			HashMap<Integer, Set<MultiProbabilityNode>> activated = new HashMap<Integer, Set<MultiProbabilityNode>>();
			activated.put(ltC.getId(), seedsLT);
			activated.put(icC.getId(), seedsIC);
			icC.seedInitialise(seedsIC);
			boolean spread = true;
			boolean spreadLT = true;
			boolean spreadIC = true;
			int sizeLT = activated.get(ltC.getId()).size();
			int sizeIC = activated.get(icC.getId()).size();
			int t = 0;
			String list = "";
			
//			list = "";
//			for (MultiProbabilityNode n : activated.get(ltC.getId())) {
//				list = list + n.getId() + ", ";
//			}
//			list = list.substring(0, list.length() - 2);
//			try {
//				bw.write("Initial seeds for LT: " + list + "\n");
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			
//			list = "";
//			for (MultiProbabilityNode n : activated.get(icC.getId())) {
//				list = list + n.getId() + ", ";
//			}
//			list = list.substring(0, list.length() - 2);
//			try {
//				bw.write("Initial seeds for IC: " + list + "\n");
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			if (seedSelection > 9) {spreadIC = false;}
			while (spread) {
				sizeLT = activated.get(ltC.getId()).size();
				sizeIC = activated.get(icC.getId()).size();
				if (spreadLT) {
					ltC.spread(smg, activated);
				}
				
				if (spreadIC) {
					icC.spread(smg, activated);
				}
				
				if (spreadLT){
					ltC.activateNodes(activated);
				}
				if (spreadIC) {
					icC.activateNodes(activated);
				}

				if (activated.get(ltC.getId()).size() <= sizeLT) {spreadLT = false;}
				if (activated.get(icC.getId()).size() <= sizeIC) {spreadIC = false;}
				if (!spreadLT && !spreadIC) {
					spread = false;
				}
				
				try {
					bw.write("Timestep " + t + "\n");
					bw.write("Infections LT: " + activated.get(ltC.getId()).size() + "\n");
					bw.write("Infections IC: " + activated.get(icC.getId()).size() + "\n");
					//bw.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				t++;
			}
			try {
				bw.write("Final Result" + "\n");
				bw.write("Infections LT: " + activated.get(ltC.getId()).size() + "\n");
//				list = "";
//				for (MultiProbabilityNode n : activated.get(ltC.getId())) {
//					list = list + n.getId() + ", ";
//				}
//				list = list.substring(0, list.length() - 2);
//				bw.write("Nodes with LT Active: " + list + "\n");
				bw.write("Infections IC: " + activated.get(icC.getId()).size() + "\n");
				list = "";
//				for (MultiProbabilityNode n : activated.get(icC.getId())) {
//					list = list + n.getId() + ", ";
//				}
//				list = list.substring(0, list.length() - 2);
//				bw.write("Nodes with IC Active: " + list + "\n");
				//bw.flush();
				bw.write(System.currentTimeMillis() + "\n");
				bw.write("----------------------------\n");
				//bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void LTBoostingICStandard(int seedSize, int graphSize, String filename, int seedSelection, int runs, double interaction, double LTThresh, double ICProb) {
		GraphReaderWriter grw = new GraphReaderWriter();
		File file = new File(filename + "_LTBoostIC.txt");
		FileWriter fw;
		BufferedWriter bw = null;
		

		
		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			bw.write("Size of graph: " + graphSize + "\n");
			bw.write("Uniform Threshold: false \n");
			bw.write("Threshold Average: " + LTThresh + "\n");
			bw.write("Independent Cascade Infection rate: " + ICProb + "\n");
			bw.write("Number of runs: 1000\n");
			bw.write("Size of seed set: " + seedSize + "\n");
			bw.write("Seed Selection: " + seedSelection + "\n");
			bw.write("----------------------------" + "\n");
			//bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < runs; i++){
			Concept ic = new DiverseICConcept(0, i);
			Concept lt = new DiverseLTConcept(1);
			ic.addConceptInteractions(lt, interaction, interaction);
			System.out.println("Run " + i + " for " + file.getName() + " with a seed size of " + seedSize);
			try {
				bw.write("Run " + i + "\n");
				bw.write(System.currentTimeMillis() + "\n");
				//bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			ArrayList<Concept> cList = new ArrayList<Concept>();
			cList.add(ic);
			cList.add(lt);
			Graph<MultiProbabilityNode, TypedWeightedEdge> smg = grw.graphReadIn(graphSize, i, cList, false, LTThresh, ICProb);
			HashSet<MultiProbabilityNode> seedsIC = new HashSet<MultiProbabilityNode>();
			HashSet<MultiProbabilityNode> seedsLT = new HashSet<MultiProbabilityNode>();
			//int seedSize = 500;
			Random rand = new Random(i);
			HashMap<Integer, MultiProbabilityNode> nodes = new HashMap<Integer, MultiProbabilityNode>();
			for (MultiProbabilityNode n : smg.getVertices()) {
				nodes.put(n.getId(), n);
			}

			while (seedsIC.size() < seedSize) {
				int id = rand.nextInt(graphSize);
				nodes.get(id).activate(ic);
				seedsIC.add(nodes.get(id));
				
				if (seedSelection == 1){
					nodes.get(id).activate(lt);
					seedsLT.add(nodes.get(id));
				}
			}
			
			if (seedSelection != 1) {
				seedSelection(ic, lt, smg, seedSize, seedsIC, seedsLT, seedSelection, rand, interaction, graphSize, nodes);
			}
			
			DiverseLTConcept ltC = (DiverseLTConcept) cList.get(1);
			DiverseICConcept icC = (DiverseICConcept) cList.get(0);
			
			HashMap<Integer, Set<MultiProbabilityNode>> activated = new HashMap<Integer, Set<MultiProbabilityNode>>();
			activated.put(ltC.getId(), seedsLT);
			activated.put(icC.getId(), seedsIC);
			//System.out.println("Size of IC seed set:" + seedsIC.size());
			icC.seedInitialise(seedsIC);
			boolean spread = true;
			boolean spreadLT = true;
			boolean spreadIC = true;
			int sizeLT = activated.get(ltC.getId()).size();
			int sizeIC = activated.get(icC.getId()).size();
			int t = 0;
			String list = "";
//			
//			list = "";
//			for (MultiProbabilityNode n : activated.get(icC.getId())) {
//				list = list + n.getId() + ", ";
//			}
//			list = list.substring(0, list.length() - 2);
//			try {
//				bw.write("Initial seeds for IC: " + list + "\n");
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			
//			list = "";
//			for (MultiProbabilityNode n : activated.get(ltC.getId())) {
//				list = list + n.getId() + ", ";
//			}
//			list = list.substring(0, list.length() - 2);
//			try {
//				bw.write("Initial seeds for LT: " + list + "\n");
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			if (seedSelection > 9) {spreadLT = false;}
			while (spread) {
				sizeLT = activated.get(ltC.getId()).size();
				sizeIC = activated.get(icC.getId()).size();
				if (spreadLT) {
					ltC.spread(smg, activated);
				}
			//	System.out.println("--------------------------------");
				
				if (spreadIC) {
					icC.spread(smg, activated);
				}
				
				if (spreadLT){
					ltC.activateNodes(activated);
				}
				if (spreadIC) {
					icC.activateNodes(activated);
				}

				if (activated.get(ltC.getId()).size() <= sizeLT) {spreadLT = false;}
				if (activated.get(icC.getId()).size() <= sizeIC) {spreadIC = false;}
				if (!spreadLT && !spreadIC) {
					spread = false;
				}
				
				try {
					bw.write("Timestep " + t + "\n");
					bw.write("Infections IC: " + activated.get(icC.getId()).size() + "\n");
					bw.write("Infections LT: " + activated.get(ltC.getId()).size() + "\n");
					//bw.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				t++;
			}
			try {
				bw.write("Final Result" + "\n");
				bw.write("Infections IC: " + activated.get(icC.getId()).size() + "\n");
//				list = "";
//				for (MultiProbabilityNode n : activated.get(icC.getId())) {
//					list = list + n.getId() + ", ";
//				}
//				list = list.substring(0, list.length() - 2);
//				bw.write("Nodes with IC Active: " + list + "\n");
				bw.write("Infections LT: " + activated.get(ltC.getId()).size() + "\n");
//				list = "";
//				for (MultiProbabilityNode n : activated.get(ltC.getId())) {
//					list = list + n.getId() + ", ";
//				}
//				list = list.substring(0, list.length() - 2);
//				bw.write("Nodes with LT Active: " + list + "\n");
				bw.write(System.currentTimeMillis() + "\n");
				bw.write("----------------------------\n");
				//bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Final Number: " +  activated.get(icC.getId()).size());
		}
		try {
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void ICBoostingICStandard(int seedSize, int graphSize, String filename, int seedSelection, int runs, double interaction, double LTThresh, double ICProb) {
		GraphReaderWriter grw = new GraphReaderWriter();
		File file = new File(filename + "_ICBoostIC.txt");
		FileWriter fw;
		BufferedWriter bw = null;
		

		
		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			bw.write("Size of graph: " + graphSize + "\n");
			bw.write("Uniform Threshold: false \n");
			bw.write("Threshold Average: " + LTThresh + "\n");
			bw.write("Independent Cascade Infection rate: " + ICProb+ "\n");
			bw.write("Number of runs: 1000\n");
			bw.write("Size of seed set: " + seedSize + "\n");
			bw.write("Seed Selection: " + seedSelection + "\n");
			bw.write("----------------------------" + "\n");
			//bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < runs; i++){
			Concept ic = new DiverseICConcept(0, i);
			Concept ic2 = new DiverseICConcept(1, i*2);
			ic.addConceptInteractions(ic2, interaction, interaction);
			
			System.out.println("Run " + i + " for " + file.getName() + " with a seed size of " + seedSize);
			try {
				bw.write("Run " + i + "\n");
				bw.write(System.currentTimeMillis() + "\n");
				//bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			ArrayList<Concept> cList = new ArrayList<Concept>();
			cList.add(ic);
			cList.add(ic2);
			Graph<MultiProbabilityNode, TypedWeightedEdge> smg;
			
			if (graphSize > 5000) {
				smg = grw.graphReadInSW(graphSize, 0.5, i, cList, false, LTThresh, ICProb);
			}
			else {
				smg = grw.graphReadIn(graphSize, i, cList, false, LTThresh, ICProb);
			}
			
			
			HashSet<MultiProbabilityNode> seedsIC = new HashSet<MultiProbabilityNode>();
			HashSet<MultiProbabilityNode> seedsIC2 = new HashSet<MultiProbabilityNode>();
			//int seedSize = 500;
			Random rand = new Random(i);
			HashMap<Integer, MultiProbabilityNode> nodes = new HashMap<Integer, MultiProbabilityNode>();
			for (MultiProbabilityNode n : smg.getVertices()) {
				nodes.put(n.getId(), n);
			}
			
			while (seedsIC.size() < seedSize) {
				int id = rand.nextInt(graphSize);
				nodes.get(id).activate(ic);
				seedsIC.add(nodes.get(id));
				
				if (seedSelection == 1) {
					nodes.get(id).activate(ic2);
					seedsIC2.add(nodes.get(id));
				}
			}
			
			if (seedSelection != 1) {
				seedSelection(ic, ic2, smg, seedSize, seedsIC, seedsIC2, seedSelection, rand, interaction, graphSize, nodes);
			}
			
			DiverseICConcept icC2 = (DiverseICConcept) cList.get(1);
			DiverseICConcept icC = (DiverseICConcept) cList.get(0);
			
			HashMap<Integer, Set<MultiProbabilityNode>> activated = new HashMap<Integer, Set<MultiProbabilityNode>>();
			activated.put(icC2.getId(), seedsIC2);
			activated.put(icC.getId(), seedsIC);
			//System.out.println("Size of IC seed set:" + seedsIC.size());
			icC.seedInitialise(seedsIC);
			icC2.seedInitialise(seedsIC2);
			boolean spread = true;
			boolean spreadIC2 = true;
			boolean spreadIC = true;
			int sizeIC2 = activated.get(icC2.getId()).size();
			int sizeIC = activated.get(icC.getId()).size();
			int t = 0;
			String list = "";
//			
//			list = "";
//			for (MultiProbabilityNode n : activated.get(icC.getId())) {
//				list = list + n.getId() + ", ";
//			}
//			list = list.substring(0, list.length() - 2);
//			try {
//				bw.write("Initial seeds for IC: " + list + "\n");
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			
//			list = "";
//			for (MultiProbabilityNode n : activated.get(icC2.getId())) {
//				list = list + n.getId() + ", ";
//			}
//			list = list.substring(0, list.length() - 2);
//			try {
//				bw.write("Initial seeds for IC2: " + list + "\n");
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			if (seedSelection == 10 || seedSelection == 11 || seedSelection == 12) {spreadIC2 = false;}
			while (spread) {
				sizeIC2 = activated.get(icC2.getId()).size();
				sizeIC = activated.get(icC.getId()).size();
				if (spreadIC2) {
					icC2.spread(smg, activated);
				}
			//	System.out.println("--------------------------------");
				
				if (spreadIC) {
					icC.spread(smg, activated);
				}
				
				if (spreadIC2){
					icC2.activateNodes(activated);
				}
				if (spreadIC) {
					icC.activateNodes(activated);
				}

				if (activated.get(icC2.getId()).size() <= sizeIC2) {spreadIC2 = false;}
				if (activated.get(icC.getId()).size() <= sizeIC) {spreadIC = false;}
				if (!spreadIC2 && !spreadIC) {
					spread = false;
				}
				
				try {
					bw.write("Timestep " + t + "\n");
					bw.write("Infections IC: " + activated.get(icC.getId()).size() + "\n");
					bw.write("Infections IC2: " + activated.get(icC2.getId()).size() + "\n");
					//bw.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				t++;
			}
			try {
				bw.write("Final Result" + "\n");
				bw.write("Infections IC: " + activated.get(icC.getId()).size() + "\n");
//				list = "";
//				for (MultiProbabilityNode n : activated.get(icC.getId())) {
//					list = list + n.getId() + ", ";
//				}
//				list = list.substring(0, list.length() - 2);
//				bw.write("Nodes with IC Active: " + list + "\n");
				bw.write("Infections IC2: " + activated.get(icC2.getId()).size() + "\n");
//				list = "";
//				for (MultiProbabilityNode n : activated.get(icC2.getId())) {
//					list = list + n.getId() + ", ";
//				}
//				list = list.substring(0, list.length() - 2);
//				bw.write("Nodes with IC2 Active: " + list + "\n");
				bw.write(System.currentTimeMillis() + "\n");
				bw.write("----------------------------\n");
				//bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Final Number: " +  activated.get(icC.getId()).size());
			System.out.println("Final Number: " +  activated.get(icC2.getId()).size());
		}
		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void singleRunICBoostingICStandard(int seedSize, int graphSize, String filename, int seedSelection, int runs, double interaction, double LTThresh, double ICProb){
		GraphReaderWriter grw = new GraphReaderWriter();
		File file = new File(filename + "_" + runs + "_ICBoostIC.txt");
		FileWriter fw;
		BufferedWriter bw = null;
		
		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			bw.write("Size of graph: " + graphSize + "\n");
			bw.write("Uniform Threshold: false \n");
			bw.write("Threshold Average: " + LTThresh + "\n");
			bw.write("Independent Cascade Infection rate: " + ICProb+ "\n");
			bw.write("Number of runs: 1000\n");
			bw.write("Size of seed set: " + seedSize + "\n");
			bw.write("Seed Selection: " + seedSelection + "\n");
			bw.write("----------------------------" + "\n");
			//bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		Concept ic = new DiverseICConcept(0, runs);
		Concept ic2 = new DiverseICConcept(1, runs*2);
		ic.addConceptInteractions(ic2, interaction, interaction);
		
		System.out.println("Run " + runs + " for " + file.getName() + " with a seed size of " + seedSize);
		try {
			bw.write("Run " + runs + "\n");
			bw.write(System.currentTimeMillis() + "\n");
			//bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ArrayList<Concept> cList = new ArrayList<Concept>();
		cList.add(ic);
		cList.add(ic2);
		Graph<MultiProbabilityNode, TypedWeightedEdge> smg;
		
		smg = grw.graphReadInSW(graphSize, 0.5, runs, cList, false, LTThresh, ICProb);
//		if (graphSize > 5000) {
//			smg = grw.graphReadInSW(graphSize, 0.5, runs, cList, false, LTThresh, ICProb);
//		}
//		else {
//			smg = grw.graphReadIn(graphSize, runs, cList, false, LTThresh, ICProb);
//		}
		
		HashSet<MultiProbabilityNode> seedsIC = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> seedsIC2 = new HashSet<MultiProbabilityNode>();
		//int seedSize = 500;
		Random rand = new Random(runs);
		HashMap<Integer, MultiProbabilityNode> nodes = new HashMap<Integer, MultiProbabilityNode>();
		for (MultiProbabilityNode n : smg.getVertices()) {
			nodes.put(n.getId(), n);
		}
		
		while (seedsIC.size() < seedSize) {
			int id = rand.nextInt(graphSize);
			nodes.get(id).activate(ic);
			seedsIC.add(nodes.get(id));
			//System.out.println(id);
			if (seedSelection == 1) {
				nodes.get(id).activate(ic2);
				seedsIC2.add(nodes.get(id));
			}
		}
		
		if (seedSelection != 1) {
			seedSelection(ic, ic2, smg, seedSize, seedsIC, seedsIC2, seedSelection, rand, interaction, graphSize, nodes);
		}
		
		DiverseICConcept icC2 = (DiverseICConcept) cList.get(1);
		DiverseICConcept icC = (DiverseICConcept) cList.get(0);
		
		HashMap<Integer, Set<MultiProbabilityNode>> activated = new HashMap<Integer, Set<MultiProbabilityNode>>();
		activated.put(icC2.getId(), seedsIC2);
		activated.put(icC.getId(), seedsIC);
		//System.out.println("Size of IC seed set:" + seedsIC.size());
		icC.seedInitialise(seedsIC);
		icC2.seedInitialise(seedsIC2);
		boolean spread = true;
		boolean spreadIC2 = true;
		boolean spreadIC = true;
		int sizeIC2 = activated.get(icC2.getId()).size();
		int sizeIC = activated.get(icC.getId()).size();
		int t = 0;
		String list = "";
//		
//		list = "";
//		for (MultiProbabilityNode n : activated.get(icC.getId())) {
//			list = list + n.getId() + ", ";
//		}
//		list = list.substring(0, list.length() - 2);
//		try {
//			bw.write("Initial seeds for IC: " + list + "\n");
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		
//		list = "";
//		for (MultiProbabilityNode n : activated.get(icC2.getId())) {
//			list = list + n.getId() + ", ";
//		}
//		list = list.substring(0, list.length() - 2);
//		try {
//			bw.write("Initial seeds for IC2: " + list + "\n");
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		if (seedSelection == 10 || seedSelection == 11 || seedSelection == 12|| seedSelection == 16 || seedSelection == 17
				|| seedSelection == 18 || seedSelection == 19) {spreadIC2 = false;}
		while (spread) {
			sizeIC2 = activated.get(icC2.getId()).size();
			sizeIC = activated.get(icC.getId()).size();
			if (spreadIC2) {
				icC2.spread(smg, activated);
			}
		//	System.out.println("--------------------------------");
			
			if (spreadIC) {
				icC.spread(smg, activated);
			}
			
			if (spreadIC2){
				icC2.activateNodes(activated);
			}
			if (spreadIC) {
				icC.activateNodes(activated);
			}

			if (activated.get(icC2.getId()).size() <= sizeIC2) {spreadIC2 = false;}
			if (activated.get(icC.getId()).size() <= sizeIC) {spreadIC = false;}
			if (!spreadIC2 && !spreadIC) {
				spread = false;
			}
			
			try {
				bw.write("Timestep " + t + "\n");
				bw.write("Infections IC: " + activated.get(icC.getId()).size() + "\n");
				bw.write("Infections IC2: " + activated.get(icC2.getId()).size() + "\n");
				//bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			t++;
		}
		try {
			bw.write("Final Result" + "\n");
			bw.write("Infections IC: " + activated.get(icC.getId()).size() + "\n");
			bw.write("Infections IC2: " + activated.get(icC2.getId()).size() + "\n");
			bw.write(System.currentTimeMillis() + "\n");
			bw.write("----------------------------\n");
			//bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Final Number: " +  activated.get(icC.getId()).size());
		System.out.println("Final Number: " +  activated.get(icC2.getId()).size());

		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void seedSelection(Concept target, Concept boosting,
			Graph<MultiProbabilityNode, TypedWeightedEdge> smg, int seedSize, HashSet<MultiProbabilityNode> seedsTarget, HashSet<MultiProbabilityNode> seedsBoost, int seedSelection,
			Random rand, double interaction, int graphSize, HashMap<Integer, MultiProbabilityNode> nodes) {
		
		while (seedsBoost.size() < seedSize) {
			if (seedSelection == 0) {
				int id = rand.nextInt(graphSize);
				nodes.get(id).activate(boosting);
				seedsBoost.add(nodes.get(id));
			}
			else if (seedSelection == 2) {
				HashSet<MultiProbabilityNode> seeds = SingleDiscount.getSeeds(smg, seedSize);
				for (MultiProbabilityNode n : seeds) {
					n.activate(boosting);
					seedsBoost.add(n);
				}
			}
			else if (seedSelection == 3){
				HashSet<MultiProbabilityNode> seeds = Degree.getSeeds(smg, seedSize);
				for (MultiProbabilityNode n : seeds) {
					n.activate(boosting);
					seedsBoost.add(n);
				}
			}
			else if (seedSelection == 4) {
				HashSet<MultiProbabilityNode> seeds = BoostingPlacement.getSeedsBoostingAll(smg, seedSize, target, boosting, seedsTarget, interaction);
				for (MultiProbabilityNode n : seeds) {
					n.activate(boosting);
					seedsBoost.add(n);
				}
			}
			else if (seedSelection == 5) {
				HashSet<MultiProbabilityNode> seeds = BoostingPlacement.getSeedsBoostingOutgoing(smg, seedSize, target, boosting, seedsTarget, interaction);
				for (MultiProbabilityNode n : seeds) {
					n.activate(boosting);
					seedsBoost.add(n);
				}
			}
			else if (seedSelection == 6) {
				HashSet<MultiProbabilityNode> seeds = BoostingPlacement.getSeedsBoostingAllHops(smg, seedSize, target, boosting, seedsTarget, interaction);
				for (MultiProbabilityNode n : seeds) {
					n.activate(boosting);
					seedsBoost.add(n);
				}
			}
			else if (seedSelection == 7) {
				HashSet<MultiProbabilityNode> seeds = BoostingPlacement.getSeedsBoostingOutgoingHops(smg, seedSize, target, boosting, seedsTarget, interaction);
				for (MultiProbabilityNode n : seeds) {
					n.activate(boosting);
					seedsBoost.add(n);
				}
			}
			else if (seedSelection == 8) {
				HashSet<MultiProbabilityNode> seeds = BoostingDiscount.getSeeds(smg, seedSize, target, boosting, seedsTarget, interaction);
				for (MultiProbabilityNode n : seeds) {
					n.activate(boosting);
					seedsBoost.add(n);
				}
			}
			else if (seedSelection == 9) {
				HashSet<MultiProbabilityNode> seeds = DegreeDiscount.getSeedsLT(smg, seedSize, boosting);
				for (MultiProbabilityNode n : seeds) {
					n.activate(boosting);
					seedsBoost.add(n);
				}
			}
			else if (seedSelection == 10) {
				HashSet<MultiProbabilityNode> seeds = DegreeDiscount.getSeedsLTSame(smg, seedSize, target);
				for (MultiProbabilityNode n : seeds) {
					n.activate(target);
					seedsTarget.add(n);
				}
				break;
			}
			else if (seedSelection == 11) {
				HashSet<MultiProbabilityNode> seeds = SingleDiscount.getSeedsTarget(smg, seedSize, target);
				for (MultiProbabilityNode n : seeds) {
					n.activate(target);
					seedsTarget.add(n);
				}
				break;
			}
			else if (seedSelection == 12) {
				int eCount = 0;
				SparseMultigraph<MultiProbabilityNode, TypedWeightedEdge> newG = new SparseMultigraph<MultiProbabilityNode, TypedWeightedEdge>();
				for (MultiProbabilityNode m : smg.getVertices()) {
					newG.addVertex(m);
				}
				for (TypedWeightedEdge t : smg.getEdges()){
					if (t.getConcept() == target.getId()) {
						if (smg.getEdgeType(t) == EdgeType.UNDIRECTED){
							TypedWeightedEdge t1 = new TypedWeightedEdge(eCount, target.getId(), t.getWeight());
							TypedWeightedEdge t2 = new TypedWeightedEdge(eCount+1, target.getId(), t.getWeight());
							Pair<MultiProbabilityNode> ends = smg.getEndpoints(t);
							
							newG.addEdge(t1, ends.getFirst(), ends.getSecond(), EdgeType.DIRECTED);
							newG.addEdge(t2, ends.getSecond(), ends.getFirst(), EdgeType.DIRECTED);
							
						//	System.out.println(newG.getEdgeType(t1));
							eCount = eCount + 2;
						}
						else{
							TypedWeightedEdge t1 = new TypedWeightedEdge(eCount, target.getId(), t.getWeight());
							newG.addEdge(t1, smg.getSource(t), smg.getDest(t),EdgeType.DIRECTED);
							eCount = eCount + 1;
						}
					}
				}
				
				HashSet<MultiProbabilityNode> seeds = PMAIHeuristc.getSeedsBurnIn(newG, seedSize, (1.0/320.0), seedsTarget);
				for (MultiProbabilityNode n : seeds) {
					n.activate(target);
					seedsTarget.add(n);
				}
				break;
				
			}
			else if (seedSelection == 13) {
				int eCount = 0;
				SparseMultigraph<MultiProbabilityNode, TypedWeightedEdge> newG = new SparseMultigraph<MultiProbabilityNode, TypedWeightedEdge>();
				for (MultiProbabilityNode m : smg.getVertices()) {
					newG.addVertex(m);
				}
				for (TypedWeightedEdge t : smg.getEdges()){
					if (t.getConcept() == boosting.getId()) {
						if (smg.getEdgeType(t) == EdgeType.UNDIRECTED){
							TypedWeightedEdge t1 = new TypedWeightedEdge(eCount, boosting.getId(), t.getWeight());
							TypedWeightedEdge t2 = new TypedWeightedEdge(eCount+1, boosting.getId(), t.getWeight());
							Pair<MultiProbabilityNode> ends = smg.getEndpoints(t);
							newG.addEdge(t1, ends.getFirst(), ends.getSecond(), EdgeType.DIRECTED);
							newG.addEdge(t2, ends.getSecond(), ends.getFirst(), EdgeType.DIRECTED);
							eCount = eCount + 2;
						}
						else{
							TypedWeightedEdge t1 = new TypedWeightedEdge(eCount, boosting.getId(), t.getWeight());
							newG.addEdge(t1, smg.getSource(t), smg.getDest(t),EdgeType.DIRECTED);
							eCount = eCount + 1;
						}
					}
				}
				
				HashSet<MultiProbabilityNode> seeds = PMAIHeuristc.getSeeds(newG, seedSize, (1.0/320.0));
				for (MultiProbabilityNode n : seeds) {
					n.activate(boosting);
					seedsBoost.add(n);
				}
				break;
				
			}
			else if (seedSelection == 14) {
				ArrayList<MultiProbabilityNode> sT = new ArrayList<MultiProbabilityNode>();
				for (MultiProbabilityNode m : seedsTarget){
					sT.add(m);
				}
				int eCount = 0;
				SparseMultigraph<MultiProbabilityNode, TypedWeightedEdge> newG = new SparseMultigraph<MultiProbabilityNode, TypedWeightedEdge>();
				for (MultiProbabilityNode m : smg.getVertices()) {
					newG.addVertex(m);
				}
				for (TypedWeightedEdge t : smg.getEdges()){
					if (smg.getEdgeType(t) == EdgeType.UNDIRECTED){
						TypedWeightedEdge t1 = new TypedWeightedEdge(eCount, t.getConcept(), t.getWeight());
						TypedWeightedEdge t2 = new TypedWeightedEdge(eCount+1, t.getConcept(), t.getWeight());
						Pair<MultiProbabilityNode> ends = smg.getEndpoints(t);
						newG.addEdge(t1, ends.getFirst(), ends.getSecond(), EdgeType.DIRECTED);
						newG.addEdge(t2, ends.getSecond(), ends.getFirst(), EdgeType.DIRECTED);
						eCount = eCount + 2;
					}
					else{
						TypedWeightedEdge t1 = new TypedWeightedEdge(eCount, t.getConcept(), t.getWeight());
						newG.addEdge(t1, smg.getSource(t), smg.getDest(t),EdgeType.DIRECTED);
						eCount = eCount + 1;
					}
				}
				
				HashSet<MultiProbabilityNode> seeds = PMAIBoostHeuristic.getSeeds(newG, seedSize, (1.0/320.0), interaction, sT, target, boosting);
				for (MultiProbabilityNode n : seeds) {
					n.activate(boosting);
					seedsBoost.add(n);
				}
				break;
				
			}
			else if (seedSelection == 15) {
				HashSet<MultiProbabilityNode> seeds = DegreeDiscount.getSeedsBoostDegDis(smg, seedSize, target, boosting);
				for (MultiProbabilityNode n : seeds) {
					n.activate(boosting);
					seedsBoost.add(n);
				}
				break;
				
			}
			else if (seedSelection == 16) {
				for (MultiProbabilityNode n : seedsTarget) {
					n.deactivate(target);
				}
				seedsTarget.clear();
				HashSet<MultiProbabilityNode> seeds = DegreeDiscount.getSeedsLT(smg, seedSize, target);
				for (MultiProbabilityNode n : seeds) {
					n.activate(target);
					seedsTarget.add(n);
				}
				break;
			}
			else if (seedSelection == 17) {
				//Do nothing - We just want random target seeds
				break;
			}
			else if (seedSelection == 18){
				for (MultiProbabilityNode n : seedsTarget) {
					n.deactivate(target);
				}
				seedsTarget.clear();
				HashSet<MultiProbabilityNode> seeds = Degree.getSeeds(smg, seedSize);
				for (MultiProbabilityNode n : seeds) {
					n.activate(target);
					seedsTarget.add(n);
				}
				break;
			}
			else if (seedSelection == 19){
				for (MultiProbabilityNode n : seedsTarget) {
					n.deactivate(target);
				}
				seedsTarget.clear();
				HashSet<MultiProbabilityNode> seeds = SingleDiscount.getSeeds(smg, seedSize);
				for (MultiProbabilityNode n : seeds) {
					n.activate(target);
					seedsTarget.add(n);
				}
				break;
			}
		}
		
	}

}
