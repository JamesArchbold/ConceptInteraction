package MethodTesting;
//test runner - outdatesd by antagonisticTests
import influence.concepts.Concept;
import influence.concepts.DiverseICConcept;
import influence.concepts.DiverseLTConcept;
import influence.concepts.DiverseSIR;
import influence.concepts.DiverseSIS;
import influence.concepts.ExtendedConcept;
import influence.concepts.cascades.BurnInInteractionCascades;
import influence.concepts.cascades.BurnInTargetCascades;
import influence.concepts.cascades.DiverseInteractionCascades;
import influence.edges.TypedWeightedEdge;
import influence.graph.generation.GraphReaderWriter;
import influence.nodes.MultiProbabilityNode;
import influence.seed.selection.SeedSelector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import edu.uci.ics.jung.graph.Graph;

public class testSuite {
	
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
		//args[8] is the graph type
		//args[9] is the graph secondary characteristic (expo for SW, edges added for SF, prob for R)
		//args[10] is target concept type
		//args[11] is boosting concept type
		//args[12] is run type (single run or multi run)
		//args[13] is concept we're concerned with
		//args[14] is burn in length
		//args[15] is the recovery chance for first concept
		//args[16] is the recovery chance for the second concept
		//args[17] is number of total concepts
		
//		args = new String[17];
//		args[0] = "500";
//		args[1] = "100000";
//		args[2] = "out22.txt";
//		args[3] = "16";
//		args[4] = "-0.2";
//		args[5] = "0.8";
//		args[6] = "0.1";
//		args[7] = "1";
//		args[8] = "SW";
//		args[9] = "0.75";
//		args[10] = "IC";
//		args[11] = "IC";
//		args[12] = "True";
//		args[13] = "False";
//		args[14] = "0";
//		args[15] = "0.1";
//		args[16] = "0.1";
//		
//		if (args[13].equals("True")){
//			burnIn(args);
//		}
//		else{
//			noBurnIn(args);
//		}
		
//		args = new String[17];
//		args[0] = "10";
//		args[1] = "317080";
//		args[2] = "100_75879_16_-2_80_10_0_1_EP_8";
//		args[3] = "17";
//		args[4] = "-0.2";
//		args[5] = "0.8";
//		args[6] = "0.1";
//		args[7] = "5";
//		args[8] = "EP";
//		args[9] = "0";
//		args[10] = "LT";
//		args[11] = "LT";
//		args[12] = "True";
//		args[13] = "True";
//		args[14] = "2";
//		args[15] = "0.1";
//		args[16] = "0.1";
//		
		burnInTargetSelection(args);
		
	}

	private static void burnInTargetSelection(String[] args){
		int seedSize = Integer.parseInt(args[0]);
		int graphSize = Integer.parseInt(args[1]);
		String fileName = args[2];
		int heuristicSelection = Integer.parseInt(args[3]);
		double conceptRelationshipStrength = Double.parseDouble(args[4]);
		double LTThresh = Double.parseDouble(args[5]);
		double ICProb = Double.parseDouble(args[6]);
		int runs = Integer.parseInt(args[7]);
		String graphType = args[8];
		String graphCharacteristic = args[9];
		String targetConceptType = args[10];
		String secondaryConceptType = args[11];
		Boolean singleRun = Boolean.parseBoolean(args[12]);
		int burnInLength = Integer.parseInt(args[14]);
		double tarRecovery = 0.0;
		double secRecovery = 0.0;
		
		if (targetConceptType.equals("SIS") || targetConceptType.equals("SIR")){
			tarRecovery = Double.parseDouble(args[15]);
		}
		
		if (secondaryConceptType.equals("SIS") || secondaryConceptType.equals("SIR")) {
			secRecovery = Double.parseDouble(args[16]);
		}
		
		File file = new File(fileName + "_" + secondaryConceptType + "Boost" + targetConceptType + ".txt");
		FileWriter fw;
		BufferedWriter bw = null;
		
		int conceptToPick = 1;
		int targetCon = 0; 
		
		
		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			bw.write("Size of graph: " + graphSize + "\n");
			bw.write("Graph Type: " + graphType + " - " + graphCharacteristic + "\n");
			bw.write("Uniform Threshold: false \n");
			bw.write("Threshold Average: " + LTThresh + "\n");
			bw.write("Independent Cascade Infection rate: " + ICProb + "\n");
			bw.write("Relationship Strength: " + conceptRelationshipStrength + "\n");
			bw.write("Number of runs: 100\n");
			bw.write("Size of seed set: " + seedSize + "\n");
			bw.write("Seed Selection: " + heuristicSelection + "\n");
			bw.write("Burn in: " + burnInLength + "\n");
			bw.write("----------------------------" + "\n");
			//bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int j = 0;
		if (singleRun) {
			j = runs - 1;
		}
		
		for (int i = j; i < runs; i++) {
			ArrayList<ExtendedConcept> concepts = conceptMaker(targetConceptType, secondaryConceptType, i, conceptRelationshipStrength, tarRecovery, secRecovery);
			Graph<MultiProbabilityNode, TypedWeightedEdge> smg = graphMaker(graphSize, graphType, graphCharacteristic, LTThresh, ICProb, i, concepts, LTThresh, ICProb);
			
			System.out.println("Run " + i + " for " + file.getName() + " with a seed size of " + seedSize);
			try {
				bw.write("Run " + i + "\n");
				bw.write(System.currentTimeMillis() + "\n");
				//bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			for (MultiProbabilityNode m : smg.getVertices()) {
				if (m.getId() == 746)
				System.out.println(m.getId() + " has " + smg.getOutEdges(m).size() + " out edges");
			}
		
			HashSet<MultiProbabilityNode> seedsTar = new HashSet<MultiProbabilityNode>();
			HashSet<MultiProbabilityNode> seedsSec = new HashSet<MultiProbabilityNode>();
			
			Random rand = new Random(i);
			HashMap<Integer, MultiProbabilityNode> nodes = new HashMap<Integer, MultiProbabilityNode>();
			ArrayList<MultiProbabilityNode> listOfNodes = new ArrayList<MultiProbabilityNode>();
			for (MultiProbabilityNode n : smg.getVertices()) {
				nodes.put(n.getId(), n);
				listOfNodes.add(n);
			}
			
			ArrayList<HashSet<MultiProbabilityNode>> seedGroups = new ArrayList<HashSet<MultiProbabilityNode>>();
			seedGroups.add(seedsTar);
			seedGroups.add(seedsSec);
			
			for (int ii = 0; ii < seedGroups.size(); ii++) {
				if (ii != conceptToPick) {
					while (seedGroups.get(ii).size() < seedSize) {
						//System.out.println(ii);
						int id = rand.nextInt(listOfNodes.size());
						listOfNodes.get(id).activate(concepts.get(ii));
						seedGroups.get(ii).add(listOfNodes.get(id));
					}
				}
			}


			if (targetConceptType.equals("IC") && secondaryConceptType.equals("IC")) {
				BurnInTargetCascades.ICBoostIC(concepts, seedGroups, heuristicSelection, smg, bw, burnInLength, nodes, seedSize, rand, conceptRelationshipStrength, graphSize, conceptToPick, targetCon, graphType, graphCharacteristic, i);
			}
			else if (targetConceptType.equals("LT") && secondaryConceptType.equals("LT")) {
				BurnInTargetCascades.LTBoostLT(concepts, seedGroups, heuristicSelection, smg, bw, burnInLength, nodes, seedSize, rand, conceptRelationshipStrength, graphSize, conceptToPick, targetCon, graphType, graphCharacteristic, i);
			}
			else if (targetConceptType.equals("SIS") && secondaryConceptType.equals("SIS")){
				BurnInTargetCascades.SISBoostSIS(concepts, seedGroups, heuristicSelection, smg, bw, burnInLength, nodes, seedSize, rand, conceptRelationshipStrength, graphSize, conceptToPick, targetCon, graphType, graphCharacteristic, i);
			}
			else if (targetConceptType.equals("SIR") && secondaryConceptType.equals("SIR")){
				BurnInTargetCascades.SIRBoostSIR(concepts, seedGroups, heuristicSelection, smg, bw, burnInLength, nodes, seedSize, rand, conceptRelationshipStrength, graphSize, conceptToPick, targetCon, graphType, graphCharacteristic, i);

			}
			
		}
		
		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	private static void burnIn(String[] args){
		int seedSize = Integer.parseInt(args[0]);
		int graphSize = Integer.parseInt(args[1]);
		String fileName = args[2];
		int heuristicSelection = Integer.parseInt(args[3]);
		double conceptRelationshipStrength = Double.parseDouble(args[4]);
		double LTThresh = Double.parseDouble(args[5]);
		double ICProb = Double.parseDouble(args[6]);
		int runs = Integer.parseInt(args[7]);
		String graphType = args[8];
		String graphCharacteristic = args[9];
		String targetConceptType = args[10];
		String secondaryConceptType = args[11];
		Boolean singleRun = Boolean.parseBoolean(args[12]);
		int burnInLength = Integer.parseInt(args[14]);
		double tarRecovery = 0.0;
		double secRecovery = 0.0;
		
		if (targetConceptType.equals("SIS") || targetConceptType.equals("SIR")){
			tarRecovery = Double.parseDouble(args[15]);
		}
		
		if (secondaryConceptType.equals("SIS") || secondaryConceptType.equals("SIR")) {
			secRecovery = Double.parseDouble(args[16]);
		}
		
		File file = new File(fileName + "_" + secondaryConceptType + "Boost" + targetConceptType + ".txt");
		FileWriter fw;
		BufferedWriter bw = null;
		
		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			bw.write("Size of graph: " + graphSize + "\n");
			bw.write("Graph Type: " + graphType + " - " + graphCharacteristic + "\n");
			bw.write("Uniform Threshold: false \n");
			bw.write("Threshold Average: " + LTThresh + "\n");
			bw.write("Independent Cascade Infection rate: " + ICProb + "\n");
			bw.write("Relationship Strength: " + conceptRelationshipStrength + "\n");
			bw.write("Number of runs: 100\n");
			bw.write("Size of seed set: " + seedSize + "\n");
			bw.write("Seed Selection: " + heuristicSelection + "\n");
			bw.write("Burn in: " + burnInLength + "\n");
			bw.write("----------------------------" + "\n");
			//bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int j = 0;
		if (singleRun) {
			j = runs - 1;
		}
		
		for (int i = j; i < runs; i++) {
			ArrayList<ExtendedConcept> concepts = conceptMaker(targetConceptType, secondaryConceptType, i, conceptRelationshipStrength, tarRecovery, secRecovery);
			Graph<MultiProbabilityNode, TypedWeightedEdge> smg = graphMaker(graphSize, graphType, graphCharacteristic, LTThresh, ICProb, i, concepts, LTThresh, ICProb);
			
			System.out.println("Run " + i + " for " + file.getName() + " with a seed size of " + seedSize);
			try {
				bw.write("Run " + i + "\n");
				bw.write(System.currentTimeMillis() + "\n");
				//bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
			HashSet<MultiProbabilityNode> seedsTar = new HashSet<MultiProbabilityNode>();
			HashSet<MultiProbabilityNode> seedsSec = new HashSet<MultiProbabilityNode>();
			
			Random rand = new Random(i);
			HashMap<Integer, MultiProbabilityNode> nodes = new HashMap<Integer, MultiProbabilityNode>();
			
			for (MultiProbabilityNode n : smg.getVertices()) {
				nodes.put(n.getId(), n);
			}
			
			while (seedsTar.size() < seedSize) {
				int id = rand.nextInt(graphSize);
				nodes.get(id).activate(concepts.get(0));
				seedsTar.add(nodes.get(id));
			}
			
			if (targetConceptType.equals("IC") && secondaryConceptType.equals("LT")){
				BurnInInteractionCascades.LTBoostIC(concepts, seedsTar, seedsSec, heuristicSelection, smg, bw, burnInLength, nodes, seedSize, rand, conceptRelationshipStrength, graphSize, graphType, graphCharacteristic, i);
			}
			else if (targetConceptType.equals("IC") && secondaryConceptType.equals("IC")) {
				BurnInInteractionCascades.ICBoostIC(concepts, seedsTar, seedsSec, heuristicSelection, smg, bw, burnInLength, nodes, seedSize, rand, conceptRelationshipStrength, graphSize, graphType, graphCharacteristic, i);
			}
			else if (targetConceptType.equals("LT") && secondaryConceptType.equals("IC")) {
				BurnInInteractionCascades.ICBoostLT(concepts, seedsTar, seedsSec, heuristicSelection, smg, bw, burnInLength, nodes, seedSize, rand, conceptRelationshipStrength, graphSize, graphType, graphCharacteristic, i);
			}
			else if (targetConceptType.equals("LT") && secondaryConceptType.equals("LT")) {
				BurnInInteractionCascades.LTBoostLT(concepts, seedsTar, seedsSec, heuristicSelection, smg, bw, burnInLength, nodes, seedSize, rand, conceptRelationshipStrength, graphSize, graphType, graphCharacteristic, i);
			}
			else if (targetConceptType.equals("SIS") && secondaryConceptType.equals("SIS")){
				BurnInInteractionCascades.SISBoostSIS(concepts, seedsTar, seedsSec, heuristicSelection, smg, bw, burnInLength, nodes, seedSize, rand, conceptRelationshipStrength, graphSize, graphType, graphCharacteristic, i);
			}
			else if (targetConceptType.equals("SIR") && secondaryConceptType.equals("SIR")){
				BurnInInteractionCascades.SIRBoostSIR(concepts, seedsTar, seedsSec, heuristicSelection, smg, bw, burnInLength, nodes, seedSize, rand, conceptRelationshipStrength, graphSize, graphType, graphCharacteristic, i);

			}
			
		}
		
		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	private static void noBurnIn(String[] args){
		int seedSize = Integer.parseInt(args[0]);
		int graphSize = Integer.parseInt(args[1]);
		String fileName = args[2];
		int heuristicSelection = Integer.parseInt(args[3]);
		double conceptRelationshipStrength = Double.parseDouble(args[4]);
		double LTThresh = Double.parseDouble(args[5]);
		double ICProb = Double.parseDouble(args[6]);
		int runs = Integer.parseInt(args[7]);
		String graphType = args[8];
		String graphCharacteristic = args[9];
		String targetConceptType = args[10];
		String secondaryConceptType = args[11];
		Boolean singleRun = Boolean.parseBoolean(args[12]);
		
		double tarRecovery = 0.0;
		double secRecovery = 0.0;
		
		if (targetConceptType.equals("SIS") || targetConceptType.equals("SIR")){
			tarRecovery = Double.parseDouble(args[15]);
		}
		
		if (secondaryConceptType.equals("SIS") || secondaryConceptType.equals("SIR")) {
			secRecovery = Double.parseDouble(args[16]);
		}
		
		File file = new File(fileName + "_" + secondaryConceptType + "Boost" + targetConceptType + ".txt");
		FileWriter fw;
		BufferedWriter bw = null;
		long run = 0;

		
		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			bw.write("Size of graph: " + graphSize + "\n");
			bw.write("Graph Type: " + graphType + " - " + graphCharacteristic + "\n");
			bw.write("Uniform Threshold: false \n");
			bw.write("Threshold Average: " + LTThresh + "\n");
			bw.write("Independent Cascade Infection rate: " + ICProb + "\n");
			bw.write("Relationship Strength: " + conceptRelationshipStrength + "\n");
			bw.write("Number of runs: 100\n");
			bw.write("Size of seed set: " + seedSize + "\n");
			bw.write("Seed Selection: " + heuristicSelection + "\n");
			bw.write("----------------------------" + "\n");
			//bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int j = 0;
		if (singleRun) {
			j = runs - 1;
		}
		for (int i = j; i < runs; i++) {
			ArrayList<ExtendedConcept> concepts = conceptMaker(targetConceptType, secondaryConceptType, i, conceptRelationshipStrength, tarRecovery, secRecovery);
			Graph<MultiProbabilityNode, TypedWeightedEdge> smg = graphMaker(graphSize, graphType, graphCharacteristic, LTThresh, ICProb, i, concepts, LTThresh, ICProb);
			
			System.out.println("Run " + i + " for " + file.getName() + " with a seed size of " + seedSize);
			try {
				bw.write("Run " + i + "\n");
				bw.write(System.currentTimeMillis() + "\n");
				//bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
			HashSet<MultiProbabilityNode> seedsTar = new HashSet<MultiProbabilityNode>();
			HashSet<MultiProbabilityNode> seedsSec = new HashSet<MultiProbabilityNode>();
			
			Random rand = new Random(i);
			HashMap<Integer, MultiProbabilityNode> nodes = new HashMap<Integer, MultiProbabilityNode>();
			
			for (MultiProbabilityNode n : smg.getVertices()) {
				nodes.put(n.getId(), n);
			}
			System.out.println(seedSize);
			while (seedsTar.size() < seedSize) {
				int id = rand.nextInt(graphSize);
				nodes.get(id).activate(concepts.get(0));
				seedsTar.add(nodes.get(id));
				
				if (heuristicSelection == 1){
					nodes.get(id).activate(concepts.get(1));
					seedsSec.add(nodes.get(id));
				}
			}
			
			if (heuristicSelection != 1) {
				long start = System.currentTimeMillis();
				SeedSelector.seedSelection(concepts.get(0), concepts.get(1), smg, seedSize, seedsTar, seedsSec, heuristicSelection, rand, conceptRelationshipStrength, graphSize, nodes, graphType, graphCharacteristic, i);
				run = System.currentTimeMillis() - start;
			}
			
			if (targetConceptType.equals("IC") && secondaryConceptType.equals("LT")){
				DiverseInteractionCascades.LTBoostIC(concepts, seedsTar, seedsSec, heuristicSelection, smg, bw);
			}
			else if (targetConceptType.equals("IC") && secondaryConceptType.equals("IC")) {
				DiverseInteractionCascades.ICBoostIC(concepts, seedsTar, seedsSec, heuristicSelection, smg, bw);
			}
			else if (targetConceptType.equals("LT") && secondaryConceptType.equals("IC")) {
				DiverseInteractionCascades.ICBoostLT(concepts, seedsTar, seedsSec, heuristicSelection, smg, bw);
			}
			else if (targetConceptType.equals("LT") && secondaryConceptType.equals("LT")) {
				DiverseInteractionCascades.LTBoostLT(concepts, seedsTar, seedsSec, heuristicSelection, smg, bw);
			}
			else if (targetConceptType.equals("SIS") && secondaryConceptType.equals("SIS")) {
				DiverseInteractionCascades.SISBoostSIS(concepts, seedsTar, seedsSec, heuristicSelection, smg, bw);
			}
			else if (targetConceptType.equals("SIR") && secondaryConceptType.equals("SIR")) {
				DiverseInteractionCascades.SIRBoostSIR(concepts, seedsTar, seedsSec, heuristicSelection, smg, bw);
			}
			
			try {
				bw.write("Time for seed set: " + run + "\n");
				bw.write("----------------------------\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
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
			return grw.graphReadInStandford("stanford", "hepph.txt", concepts, ICProb, false, LTThresh, runs);
		}
		
		return null;
	}

	private static ArrayList<ExtendedConcept> conceptMaker(String targetConceptType, String secondaryConceptType, int runs, double conceptRelationshipStrength, double recoverTar, double recoverSec) {
		
		ArrayList<ExtendedConcept> concepts = new ArrayList<ExtendedConcept>();
		
		if (targetConceptType.equals("LT")) {
			concepts.add(new DiverseLTConcept(0));
		}
		else if (targetConceptType.equals("IC")){
			concepts.add(new DiverseICConcept(0, runs));
		}
		else if (targetConceptType.equals("SIS")) {
			concepts.add(new DiverseSIS(0, recoverTar));
		}
		else if (targetConceptType.equals("SIR")) {
			concepts.add(new DiverseSIR(0, recoverTar));
		}
		
		if (secondaryConceptType.equals("LT")) {
			concepts.add(new DiverseLTConcept(1));
		}
		else if (secondaryConceptType.equals("IC")){
			concepts.add(new DiverseICConcept(1, runs+1));
		}
		else if (secondaryConceptType.equals("SIS")) {
			concepts.add(new DiverseSIS(1, recoverSec));
		}
		else if (secondaryConceptType.equals("SIR")) {
			concepts.add(new DiverseSIR(1, recoverSec));
		}
		
		concepts.get(0).addConceptInteractions(concepts.get(1), conceptRelationshipStrength, conceptRelationshipStrength);
		return concepts;
	}
	
	
}
