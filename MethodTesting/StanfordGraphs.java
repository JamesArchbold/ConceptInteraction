package MethodTesting;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import influence.concepts.Concept;
import influence.concepts.DiverseICConcept;
import influence.concepts.DiverseLTConcept;
import influence.concepts.cascades.BurnInInteractionCascades;
import influence.edges.TypedWeightedEdge;
import influence.graph.generation.GraphReaderWriter;
import influence.nodes.MultiProbabilityNode;
import edu.uci.ics.jung.graph.Graph;

public class StanfordGraphs {

	public static void main(String[] args) {
		
		String targetConceptType = "IC";
		String secondaryConceptType = "IC";
		ArrayList<Concept> concepts = conceptMaker(targetConceptType, secondaryConceptType, 0, -1);
		System.out.println(concepts.size());
		GraphReaderWriter grw = new GraphReaderWriter();
		Graph<MultiProbabilityNode, TypedWeightedEdge> smg =  grw.graphReadInStandford("stanford", "com-dblp.ungraph.txt", concepts, 0.1, false, 0.8, 6);
		
		args = new String[15];
		args[0] = "250";
		args[1] = "50000";
		args[2] = "out2.txt";
		args[3] = "16";
		args[4] = "-0.2";
		args[5] = "0.8";
		args[6] = "0.1";
		args[7] = "1";
		args[8] = "SF";
		args[9] = "8";
		args[10] = "IC";
		args[11] = "IC";
		args[12] = "True";
		args[13] = "True";
		args[14] = "2";
		
		HashSet<MultiProbabilityNode> seedsTar = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> seedsSec = new HashSet<MultiProbabilityNode>();
		int i = 0;
		int seedSize = 100;
		int graphSize = smg.getVertexCount();
		Random rand = new Random(i);
		HashMap<Integer, MultiProbabilityNode> nodes = new HashMap<Integer, MultiProbabilityNode>();
		System.out.println(smg.getVertexCount());
		for (MultiProbabilityNode n : smg.getVertices()) {
			nodes.put(n.getId(), n);
		}
		
		System.out.println(concepts.size());
		System.out.println(concepts.get(0).getId());
		while (seedsTar.size() < seedSize) {
			int id = rand.nextInt(graphSize);
			System.out.println(id);
			MultiProbabilityNode mn = nodes.get(id);
			mn.activate(concepts.get(0));
		//	nodes.get(id).activate(concepts.get(0));
			seedsTar.add(nodes.get(id));
		}
		
		File file = new File("outout.txt");
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			file.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			fw = new FileWriter(file.getAbsoluteFile());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bw = new BufferedWriter(fw);
		
		//BurnInInteractionCascades.ICBoostIC(concepts, seedsTar, seedsSec, 16, smg, bw, 5, nodes, seedSize, rand, -1, graphSize);
		
	}
	
	private static ArrayList<Concept> conceptMaker(String targetConceptType, String secondaryConceptType, int runs, double conceptRelationshipStrength) {
		
		ArrayList<Concept> concepts = new ArrayList<Concept>();
		
		if (targetConceptType.equals("LT")) {
			concepts.add(new DiverseLTConcept(0));
		}
		else if (targetConceptType.equals("IC")){
			concepts.add(new DiverseICConcept(0, runs));
		}
		
		if (secondaryConceptType.equals("LT")) {
			concepts.add(new DiverseLTConcept(1));
		}
		else if (secondaryConceptType.equals("IC")){
			concepts.add(new DiverseICConcept(1, runs+1));
		}
		
		concepts.get(0).addConceptInteractions(concepts.get(1), conceptRelationshipStrength, conceptRelationshipStrength);
		return concepts;
	}
}
