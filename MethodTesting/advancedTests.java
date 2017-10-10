package MethodTesting;

import influence.concepts.Concept;
import influence.concepts.DiverseICConcept;
import influence.concepts.DiverseLTConcept;
import influence.concepts.DiverseSIR;
import influence.concepts.DiverseSIS;
import influence.concepts.ExtendedConcept;
import influence.concepts.cascades.BurnInTargetCascades;
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

import edu.uci.ics.jung.graph.Graph;

public class advancedTests {
	public static void main(String[] args) {
		//args breakdown:
			// 0  - seed set size
			// 1  - graph node size
			// 2  - file name
			// 3  - heuristic to be used (0(random), 2(discount) ,3(degree) , 4(Exp. Inf.), 5(CASD), 9(Deg Dis), 16(MPG), 17(MoBoo))
			// 4  - number of concepts
			// 5  - concept relationships
			// 6  - concept types
			// 7  - concept secondary characteristic [LT,IC,SIS,SIR]
			// 8  - graph type
			// 9  - secondary graph characteristic
			// 10 - Boolean - true for single run, false for 100 runs
			// 11 - run number
			// 12 - Concept we are interested in
			// 13 - burn in length
		
		
//		args = new String[17];
//		args[0] = "500";
//		args[1] = "100000";
//		args[2] = "out22.txt";
//		args[3] = "17";
//		args[4] = "2";
//		args[7] = "[[-1.0,-1.0]],[[0.0,0.0]]";
//		args[5] = "IC,IC";
//		args[6] = "0.8,0.1,0.1,0.1";
//		args[8] = "SW";
//		args[9] = "0.75";
//		args[10] = "True";
//		args[11] = "10";
//		args[12] = "1";
//		args[13] = "0";
		
		burnInTargetSelection(args);
		
	}
	
	private static void burnInTargetSelection(String[] args){
		//convert arguments into needed parameters
		int seedSize = Integer.parseInt(args[0]);
		int graphSize = Integer.parseInt(args[1]);
		String fileName = args[2];
		int heuristicSelection = Integer.parseInt(args[3]);
		int numOfConcepts = Integer.parseInt(args[4]);
		String relationshipStrengths = args[7];
		String[] conceptTypes = args[5].split(",");
		String[] conceptCharacteristic = args[6].split(",");
		String graphType = args[8];
		String graphCharacteristic = args[9];
		boolean singleRun = Boolean.parseBoolean(args[10]);
		int runs = Integer.parseInt(args[11]);
		int conceptToPick = Integer.parseInt(args[12]);
		int burnInLength = Integer.parseInt(args[13]);
		
		//create file
		File file = new File(fileName + "_" + conceptTypes[1] + "Boost" + conceptTypes[0] + ".txt");
		FileWriter fw;
		BufferedWriter bw = null;
		
		int targetCon = 0; 
		
		//file preamble
		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			bw.write("Size of graph: " + graphSize + "\n");
			bw.write("Graph Type: " + graphType + " - " + graphCharacteristic + "\n");
			bw.write("Uniform Threshold: false \n");
			bw.write("Threshold Average: " + conceptCharacteristic[0] + "\n");
			bw.write("Independent Cascade Infection rate: " + conceptCharacteristic[1]+ "\n");
			bw.write("Relationship Strengths: " + relationshipStrengths + "\n");
			bw.write("Number of runs: 100\n");
			bw.write("Size of seed set: " + seedSize + "\n");
			bw.write("Seed Selection: " + heuristicSelection + "\n");
			bw.write("Burn in: " + burnInLength + "\n");
			bw.write("Concept Focused on: " + conceptToPick + "\n");
			bw.write("----------------------------" + "\n");
			//bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//format based on if this a single run or a full 100 runs
		int j = 0;
		if (singleRun) {
			j = runs - 1;
		}
		
		//concept parameters
		double LTThresh = Double.parseDouble(conceptCharacteristic[0]);
		double ICProb = Double.parseDouble(conceptCharacteristic[1]);
		
		//begin runs
		for (int i = j; i < runs; i++) {
			//create concepts and read in pre-made graph
			ArrayList<ExtendedConcept> concepts = conceptMaker(conceptTypes, i, relationshipStrengths, conceptCharacteristic, numOfConcepts);
			Graph<MultiProbabilityNode, TypedWeightedEdge> smg = graphMaker(graphSize, graphType, graphCharacteristic, LTThresh, ICProb, i, concepts, LTThresh, ICProb);
			
			System.out.println("Run " + i + " for " + file.getName() + " with a seed size of " + seedSize);
			try {
				bw.write("Run " + i + "\n");
				bw.write(System.currentTimeMillis() + "\n");
				//bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
			//create seed sets
			HashSet<MultiProbabilityNode> seedsTar = new HashSet<MultiProbabilityNode>();
			HashSet<MultiProbabilityNode> seedsSec = new HashSet<MultiProbabilityNode>();
			
			Random rand = new Random(i);
			HashMap<Integer, MultiProbabilityNode> nodes = new HashMap<Integer, MultiProbabilityNode>();
			
			for (MultiProbabilityNode n : smg.getVertices()) {
				nodes.put(n.getId(), n);
			}
			
			ArrayList<HashSet<MultiProbabilityNode>> seedGroups = new ArrayList<HashSet<MultiProbabilityNode>>();
			seedGroups.add(seedsTar);
			seedGroups.add(seedsSec);
			
			//select seed sets for concepts with no burn in
			for (int ii = 0; ii < seedGroups.size(); ii++) {
				if (ii != conceptToPick) {
					while (seedGroups.get(ii).size() < seedSize) {
						int id = rand.nextInt(graphSize);
						nodes.get(id).activate(concepts.get(ii));
						seedGroups.get(ii).add(nodes.get(id));
					}
				}
			}


			BurnInTargetCascades.genSpread(concepts, seedGroups, heuristicSelection, smg, bw, burnInLength, nodes, seedSize, rand, 
					graphSize, conceptToPick, targetCon, conceptTypes, graphType, graphCharacteristic, i);
		}
		
		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	//creates concepts and their relationships
private static ArrayList<ExtendedConcept> conceptMaker(String[] conceptTypes, int runs, String relationshipStrengths, String[] conceptCharacteristics, int numOfConcepts) {
		
		ArrayList<ExtendedConcept> concepts = new ArrayList<ExtendedConcept>();
		System.out.println(numOfConcepts);
		for (int i = 0; i < numOfConcepts; i++) {
			System.out.println(conceptTypes[i]);
			if (conceptTypes[i].equals("LT")) {
				concepts.add(new DiverseLTConcept(i));
			}
			else if (conceptTypes[i].equals("IC")){
				concepts.add(new DiverseICConcept(i, runs+i));
			}
			else if (conceptTypes[i].equals("SIS")) {
				concepts.add(new DiverseSIS(i, Double.parseDouble(conceptCharacteristics[2])));
			}
			else if (conceptTypes[i].equals("SIR")) {
				concepts.add(new DiverseSIR(i, Double.parseDouble(conceptCharacteristics[3])));
			}
		}
		
		System.out.println(concepts.size());
		String newStr = relationshipStrengths.replace("[", "");
		newStr = newStr.replace("]", "");
		String[] vals = newStr.split(",");
		int currVal = 0;
		for (int i = 0; i < numOfConcepts; i++){
			for (int j = 0; j < numOfConcepts; j++) {
				if (j != i) {
					System.out.println(currVal + " which is " + vals[currVal]);
					double internal = Double.parseDouble(vals[currVal]);
					currVal++;
					System.out.println(currVal + " which is " + vals[currVal]);
					double external = Double.parseDouble(vals[currVal]);
					currVal++;
					System.out.println("For concept " + i + " adding relationship to " + j + " where internal is " + internal + " and external is " + external);
					concepts.get(i).addConceptInteractions(concepts.get(j), internal, external);
				}
			}
		}

		return concepts;
	}

//reads in the correct graph based on type and characteristic
	private static Graph<MultiProbabilityNode, TypedWeightedEdge> graphMaker(
			int graphSize, String graphType, String graphCharacteristic,
			double lTThresh, double iCProb, int runs, ArrayList<ExtendedConcept> concepts, double LTThresh, double ICProb) {
		
		GraphReaderWriter grw = new GraphReaderWriter();
		if (graphType.equals("SW")) {
			return grw.graphReadInSW(graphSize, Double.parseDouble(graphCharacteristic), runs, concepts, false, LTThresh, ICProb);
			
		}
		else if (graphType.equals("SF")) {
			return grw.graphReadInSF(10, Integer.parseInt(graphCharacteristic), graphSize, runs, concepts, false, LTThresh, ICProb);
		}
		else if (graphType.equals("RN")) {
			
			return grw.graphReadInRN(graphSize, (int)Math.round(Double.parseDouble(graphCharacteristic)), runs, concepts, false, LTThresh, ICProb);
		}
		else if (graphType.equals("DB")) {
			return grw.graphReadInStandford("stanford", "dblp.txt", concepts, ICProb, false, LTThresh, runs);
		}
		else if (graphType.equals("CM")) {
			return grw.graphReadInStandford("stanford", "condMatt.txt", concepts, ICProb, false, LTThresh, runs);
		}
		else if (graphType.equals("EN")) {
			return grw.graphReadInStandford("stanford", "enron.txt", concepts, ICProb, false, LTThresh, runs);
		}
		else if (graphType.equals("EP")) {
			return grw.graphReadInStandford("stanford", "epinions.txt", concepts, ICProb, false, LTThresh, runs);
		}
		else if (graphType.equals("GN")) {
			return grw.graphReadInStandford("stanford", "gnutella.txt", concepts, ICProb, false, LTThresh, runs);
		}
		else if (graphType.equals("HP")) {
			return grw.graphReadInStandford("stanford", "hepph.txt", concepts, ICProb, false, LTThresh,runs);
		}
		
		return null;
	}
}
