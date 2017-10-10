package MethodTesting;

import influence.concepts.Concept;
import influence.concepts.DiverseICConcept;
import influence.concepts.DiverseLTConcept;
import influence.edges.TypedWeightedEdge;
import influence.graph.generation.GraphReaderWriter;
import influence.nodes.MultiProbabilityNode;

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

public class SingleConceptComparisons {

	public static void main(String[] args) {
		//args0 is the seed size, args1 is the graph size, args2 is the threshold/probability and args3 is the filename
		LTPureGaussian(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Double.parseDouble(args[2]), args[3], 1000);
		LTPureStandard(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Double.parseDouble(args[2]), args[3], 1000);
		ICPureStandard(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Double.parseDouble(args[2]), args[3], 1000);
	}
	
	public static void LTPureGaussian(int seedSize, int graphSize, double threshold, String filename, int runs) {
		GraphReaderWriter grw = new GraphReaderWriter();
		
		File file = new File(filename + "_LTPureGaussian.txt");
		FileWriter fw;
		BufferedWriter bw = null;
		
		//Concept ic = new DiverseICConcept(1);
		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			bw.write("Size of graph: " + graphSize + "\n");
			bw.write("Uniform Threshold: false \n");
			bw.write("Threshold Average: " + threshold + "\n");
			bw.write("Independent Cascade Infection rate: 0.1 \n");
			bw.write("Number of runs: 1000\n");
			bw.write("Size of seed set: " + seedSize + "\n");
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
			Graph<MultiProbabilityNode, TypedWeightedEdge> smg = grw.graphReadIn(graphSize, i, cList, false, threshold, 0.1);
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
			String list = "";
			list = "";
			for (MultiProbabilityNode n : activated.get(ltC.getId())) {
				list = list + n.getId() + ", ";
			}
			list = list.substring(0, list.length() - 2);
			try {
				bw.write("Initial seeds for LT: " + list + "\n");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
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
				list = "";
				for (MultiProbabilityNode n : activated.get(ltC.getId())) {
					list = list + n.getId() + ", ";
				}
				list = list.substring(0, list.length() - 2);
				bw.write("Nodes with LT Active: " + list + "\n");
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

	public static void LTPureStandard(int seedSize, int graphSize, double threshold, String filename, int runs) {
		GraphReaderWriter grw = new GraphReaderWriter();
		
		File file = new File(filename + "_LTPureUniform.txt");
		FileWriter fw;
		BufferedWriter bw = null;
		
		//Concept ic = new DiverseICConcept(1);
		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			bw.write("Size of graph: " + graphSize + "\n");
			bw.write("Uniform Threshold: true \n");
			bw.write("Threshold Average: " + threshold + "\n");
			bw.write("Independent Cascade Infection rate: 0.1 \n");
			bw.write("Number of runs: 1000\n");
			bw.write("Size of seed set: " + seedSize + "\n");
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
			Graph<MultiProbabilityNode, TypedWeightedEdge> smg = grw.graphReadIn(graphSize, i, cList, true, threshold, 0.1);
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
			String list = "";
			list = "";
			for (MultiProbabilityNode n : activated.get(ltC.getId())) {
				list = list + n.getId() + ", ";
			}
			list = list.substring(0, list.length() - 2);
			try {
				bw.write("Initial seeds for LT: " + list + "\n");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
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
				list = "";
				for (MultiProbabilityNode n : activated.get(ltC.getId())) {
					list = list + n.getId() + ", ";
				}
				list = list.substring(0, list.length() - 2);
				bw.write("Nodes with LT Active: " + list + "\n");
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

	public static void ICPureStandard(int seedSize, int graphSize, double threshold, String filename, int runs) {
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
			bw.write("Threshold Average: 0.8\n");
			bw.write("Independent Cascade Infection rate: " + threshold + " \n");
			bw.write("Number of runs: 1000\n");
			bw.write("Size of seed set: " + seedSize + "\n");
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
			Graph<MultiProbabilityNode, TypedWeightedEdge> smg = grw.graphReadIn(graphSize, i, cList, false, 0.8, threshold);
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
			list = "";
			for (MultiProbabilityNode n : activated.get(icC.getId())) {
				list = list + n.getId() + ", ";
			}
			list = list.substring(0, list.length() - 2);
			try {
				bw.write("Initial seeds for IC: " + list + "\n");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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
				list = "";
				for (MultiProbabilityNode n : activated.get(icC.getId())) {
					list = list + n.getId() + ", ";
				}
				list = list.substring(0, list.length() - 2);
				bw.write("Nodes with IC Active: " + list + "\n");
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
}
