package Diverse.Tests;
//SIR and SIS tests for diverse spreading dynamics - not in use
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
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;
import influence.concepts.BlockingICConcept;
import influence.concepts.BlockingLTConcept;
import influence.concepts.BlockingSIR;
import influence.concepts.BlockingSIS;
import influence.concepts.Concept;
import influence.concepts.DiverseICConcept;
import influence.concepts.DiverseLTConcept;
import influence.concepts.DiverseSIR;
import influence.concepts.DiverseSIS;
import influence.edges.TypedWeightedEdge;
import influence.graph.generation.DiverseGraphGenerator;
import influence.nodes.MultiProbabilityNode;

public class DiverseTests {
	public static void main(String args[]) {
		TIntArrayList numNodes = new TIntArrayList();
		numNodes.add(5000);
		
		TIntIterator iter = numNodes.iterator();
		
		while (iter.hasNext()) {
			int nodes = iter.next();
			bulkTests(nodes);
		}

		
	}
	public static void SIRSISTests(int numNodes){
		File file = new File("SISBoostingSIR" + numNodes + "seeds100.txt");
		FileWriter fw;
		BufferedWriter bw = null;
		TIntArrayList values;
		TIntArrayList sirF;
		TIntArrayList sirT;
		TIntArrayList sisF;
		TIntArrayList sisT;
		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
		sirF = new TIntArrayList();
		sirT = new TIntArrayList();
		sisF = new TIntArrayList();
		sisT = new TIntArrayList();
		double j = 0;
		double total = 14000;
		for (int i = 0; i < 1000; i++) {
			System.out.println((int)((j/total)*100) + " % completed" );
			j++;
			values = SISBoostingSIR(numNodes);
			sirF.add(values.get(0));
			sirT.add(values.get(2));
			sisF.add(values.get(1));
			sisT.add(values.get(3));
			try {
				bw.write("Run " + i + "\n");
				bw.write("SIR Final: " + values.get(0) + "\n");
				bw.write("SIR Highest: " + values.get(2) + "\n");
				bw.write("SIS Final: " + values.get(1) + "\n");
				bw.write("SIS Highest: " + values.get(3) + "\n");
				bw.write("----------\n");
				bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try{
			bw.write("Average for SIR Final: " + sirF.sum()/1000 + "\n");
			bw.write("Average for SIR Total: " + sirT.sum()/1000 + "\n");
			bw.write("Average for SIS Final: " + sisF.sum()/1000 + "\n");
			bw.write("Average for SIS Total: " + sisT.sum()/1000 + "\n");
			bw.flush();
		} catch(IOException e){
			e.printStackTrace();
		}

		
		file = new File("SISBoostingSIS" + numNodes + ".txt");
		bw = null;
		
		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
		sirF = new TIntArrayList();
		sirT = new TIntArrayList();
		sisF = new TIntArrayList();
		sisT = new TIntArrayList();
		for (int i = 0; i < 1000; i++) {
			System.out.println((int)((j/total)*100) + " % completed" );
			j++;
			values = SISBoostingSIS(numNodes);
			sirF.add(values.get(0));
			sirT.add(values.get(2));
			sisF.add(values.get(1));
			sisT.add(values.get(3));
			try {
				bw.write("Run " + i + "\n");
				bw.write("SIS Boosted Final: " + values.get(0) + "\n");
				bw.write("SIS Boosted Highest: " + values.get(2) + "\n");
				bw.write("SIS Final: " + values.get(1) + "\n");
				bw.write("SIS Highest: " + values.get(3) + "\n");
				bw.write("----------\n");
				bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try{
			bw.write("Average for SIS Boosted Final: " + sirF.sum()/1000 + "\n");
			bw.write("Average for SIS Boosted Total: " + sirT.sum()/1000 + "\n");
			bw.write("Average for SIS Final: " + sisF.sum()/1000 + "\n");
			bw.write("Average for SIS Total: " + sisT.sum()/1000 + "\n");
			bw.flush();
		} catch(IOException e){
			e.printStackTrace();
		}

		file = new File("SIRBoostingSIS" + numNodes + ".txt");
		bw = null;
		sirF = new TIntArrayList();
		sirT = new TIntArrayList();
		sisF = new TIntArrayList();
		sisT = new TIntArrayList();
		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < 1000; i++) {
			System.out.println((int)((j/total)*100) + " % completed" );
			j++;
			values = SIRBoostingSIS(numNodes);
			sisF.add(values.get(0));
			sisT.add(values.get(2));
			sirF.add(values.get(1));
			sirT.add(values.get(3));
			try {
				bw.write("Run " + i + "\n");
				bw.write("SIS Final: " + values.get(0) + "\n");
				bw.write("SIS Highest: " + values.get(2) + "\n");
				bw.write("SIR Final: " + values.get(1) + "\n");
				bw.write("SIR Highest: " + values.get(3) + "\n");
				bw.write("----------\n");
				bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try{
			bw.write("Average for SIS Final: " + sisF.sum()/1000 + "\n");
			bw.write("Average for SIS Total: " + sisT.sum()/1000 + "\n");
			bw.write("Average for SIR Final: " + sirF.sum()/1000 + "\n");
			bw.write("Average for SIR Total: " + sirT.sum()/1000 + "\n");
			bw.flush();
		} catch(IOException e){
			e.printStackTrace();
		}
		
		
		file = new File("SIRBoostingSIR" + numNodes + ".txt");
		bw = null;
		sirF = new TIntArrayList();
		sirT = new TIntArrayList();
		sisF = new TIntArrayList();
		sisT = new TIntArrayList();
		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < 1000; i++) {
			System.out.println((int)((j/total)*100) + " % completed" );
			j++;
			values = SIRBoostingSIR(numNodes);
			sirF.add(values.get(0));
			sirT.add(values.get(2));
			sisF.add(values.get(1));
			sisT.add(values.get(3));
			try {
				bw.write("Run " + i + "\n");
				bw.write("SIR Boosted Final: " + values.get(0) + "\n");
				bw.write("SIR Boosted Highest: " + values.get(2) + "\n");
				bw.write("SIR Final: " + values.get(1) + "\n");
				bw.write("SIR Highest: " + values.get(3) + "\n");
				bw.write("----------\n");
				bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try{
			bw.write("Average for SIR Boosted Final: " + sirF.sum()/1000 + "\n");
			bw.write("Average for SIR Boosted Total: " + sirT.sum()/1000 + "\n");
			bw.write("Average for SIR Final: " + sisF.sum()/1000 + "\n");
			bw.write("Average for SIR Total: " + sisT.sum()/1000 + "\n");
			bw.flush();
		} catch(IOException e){
			e.printStackTrace();
		}
		
		
		file = new File("BlockingSIRSIS" + numNodes + ".txt");
		bw = null;
		sirF = new TIntArrayList();
		sirT = new TIntArrayList();
		sisF = new TIntArrayList();
		sisT = new TIntArrayList();
		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < 1000; i++) {
			System.out.println((int)((j/total)*100) + " % completed" );
			j++;
			values = blockingSISSIR(numNodes);
			sisF.add(values.get(0));
			sisT.add(values.get(2));
			sirF.add(values.get(1));
			sirT.add(values.get(3));
			try {
				bw.write("Run " + i + "\n");
				bw.write("SIS  Final: " + values.get(0) + "\n");
				bw.write("SIS Highest: " + values.get(2) + "\n");
				bw.write("SIR Final: " + values.get(1) + "\n");
				bw.write("SIR Highest: " + values.get(3) + "\n");
				bw.write("----------\n");
				bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try{
			bw.write("Average for SIS Final: " + sisF.sum()/1000 + "\n");
			bw.write("Average for SIS Total: " + sisT.sum()/1000 + "\n");
			bw.write("Average for SIR Final: " + sirF.sum()/1000 + "\n");
			bw.write("Average for SIR Total: " + sirT.sum()/1000 + "\n");
			bw.flush();
		} catch(IOException e){
			e.printStackTrace();
		}
		
		
		file = new File("DiverseSIS" + numNodes + ".txt");
		bw = null;
		sirF = new TIntArrayList();
		sirT = new TIntArrayList();
		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < 1000; i++) {
			System.out.println((int)((j/total)*100) + " % completed" );
			j++;
			values = DiverseSISTest(numNodes);
			sirF.add(values.get(0));
			sirT.add(values.get(1));
			try {
				bw.write("Run " + i + "\n");
				bw.write("SIS  Final: " + values.get(0) + "\n");
				bw.write("SIS Highest: " + values.get(1) + "\n");
				bw.write("----------\n");
				bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try{
			bw.write("Average for SIS Final: " + sirF.sum()/1000 + "\n");
			bw.write("Average for SIS Total: " + sirT.sum()/1000 + "\n");
			bw.flush();
		} catch(IOException e){
			e.printStackTrace();
		}
		
		file = new File("DiverseSIR" + numNodes + ".txt");
		bw = null;
		sirF = new TIntArrayList();
		sirT = new TIntArrayList();
		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < 1000; i++) {
			System.out.println((int)((j/total)*100) + " % completed" );
			j++;
			values = DiverseSIRTest(numNodes);
			sirF.add(values.get(0));
			sirT.add(values.get(1));
			try {
				bw.write("Run " + i + "\n");
				bw.write("SIR  Final: " + values.get(0) + "\n");
				bw.write("SIR Highest: " + values.get(1) + "\n");
				bw.write("----------\n");
				bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try{
			bw.write("Average for SIR Final: " + sirF.sum()/1000 + "\n");
			bw.write("Average for SIR Total: " + sirT.sum()/1000 + "\n");
			bw.flush();
		} catch(IOException e){
			e.printStackTrace();
		}
		
	}
	public static TIntArrayList blockingSISSIR(int numNodes){
		Concept sis = new BlockingSIS(0, 0.05);
		Concept sir = new BlockingSIR(1, 0.05);
		Random rand = new Random();
		ArrayList<Concept> cList = new ArrayList<Concept>();
		//sis.addConceptInteractions(sis2, 1, 1);
		cList.add(sis);
		cList.add(sir);
		
		int numofNodes = 1000;
		ArrayList<Double> spreads = new ArrayList<Double>();
		spreads.add(0.1);
		spreads.add(0.1);
		
		DiverseGraphGenerator divGraph = new DiverseGraphGenerator();
		Graph<MultiProbabilityNode,TypedWeightedEdge> graph = divGraph.ICICgraphMaker((numNodes/100), 100, 0.5, cList, spreads);
		HashMap<Integer, MultiProbabilityNode> nodes = new HashMap<Integer, MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> seedsSIS = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> seedsSIR = new HashSet<MultiProbabilityNode>();
		
		for (MultiProbabilityNode m : graph.getVertices()){
			nodes.put(m.getId(), m);
		}
		int seedSize = 100;
		while (seedsSIR.size() < seedSize) {
			int id = rand.nextInt(numofNodes);
			nodes.get(id).activate(sir);
			seedsSIR.add(nodes.get(id));
		//	System.out.println(id);
		}
	//	System.out.println("--------------");
		while (seedsSIS.size() < seedSize) {
			int id = rand.nextInt(numofNodes);
			if (!seedsSIR.contains(nodes.get(id))) {
				nodes.get(id).activate(sis);
				seedsSIS.add(nodes.get(id));
			//	System.out.println(id);
			}
		}
		
		BlockingSIS conSIS = (BlockingSIS) cList.get(0);
		BlockingSIR conSIR= (BlockingSIR) cList.get(1);
		
		HashMap<Integer, Set<MultiProbabilityNode>> activated = new HashMap<Integer, Set<MultiProbabilityNode>>();
		activated.put(1, seedsSIR);
		activated.put(0, seedsSIS);
		boolean spread = true;
		boolean spreadSIR = true;
		boolean spreadSIS = true;
		
		int sizeSIR = 0;
		int sizeSIS = 0;
		int highestSizeSIR = 0;
		int highestSizeSIS = 0;
		int turns = 0;
		while (spread) {
			sizeSIS = activated.get(0).size();
			sizeSIR = activated.get(1).size();
			
			if (spreadSIS) {conSIS.spread(graph, activated, conSIR.getRecovered());}
			if (spreadSIR) {conSIR.spread(graph, activated);}
			if (spreadSIS) {conSIS.activateNodes(activated);}
			if (spreadSIR) {conSIR.activateNodes(activated);}
			
			HashSet<MultiProbabilityNode> remove = new HashSet<MultiProbabilityNode>();
			for (MultiProbabilityNode m : activated.get(0)) {
				if (activated.get(1).contains(m)) {
					if (rand.nextDouble() > 0.5) {
						activated.get(1).remove(m);
						m.deactivate(conSIR);
					} 
					else {
						remove.add(m);
						m.deactivate(conSIS);
					}
				}
			}
			
			if (activated.get(0).size() > highestSizeSIS) {
				highestSizeSIS = activated.get(0).size();
			}
			
			if (activated.get(1).size() > highestSizeSIR) {
				highestSizeSIR = activated.get(1).size();
			}
			
			if (activated.get(0).size() == sizeSIS) {
				spreadSIS = false;
			}
			
			if (activated.get(1).size() == sizeSIR) {
				spreadSIR = false;
			}
			
			if (!spreadSIR && !spreadSIS || turns == 100) {
				spread = false;
			}
			
			turns++;
		}
		
	//	System.out.println("----------");
		for (MultiProbabilityNode m : conSIR.getRecovered()){
			if(m.activatedConcepts().size() > 0) {
		//		System.out.println(m.getId());
		//		System.out.println(m.activatedConcepts().toString());
			}
		}
		TIntArrayList values = new TIntArrayList();
		values.add(activated.get(0).size());
		values.add(activated.get(1).size());
		values.add(highestSizeSIS);
		values.add(highestSizeSIR);
		return values;
	}
	public static TIntArrayList SIRBoostingSIR(int numNodes){
		Concept sir = new DiverseSIR(1, 0.05);
		Concept sirB = new DiverseSIR(0, 0.05);
		Random rand = new Random();
		ArrayList<Concept> cList = new ArrayList<Concept>();
		sirB.addConceptInteractions(sir, 1, 1);
		cList.add(sirB);
		cList.add(sir);
		
		int numofNodes = 1000;
		ArrayList<Double> spreads = new ArrayList<Double>();
		spreads.add(0.1);
		spreads.add(0.1);
		
		DiverseGraphGenerator divGraph = new DiverseGraphGenerator();
		Graph<MultiProbabilityNode,TypedWeightedEdge> graph = divGraph.ICICgraphMaker(numNodes/100, 100, 0.5, cList, spreads);
		HashMap<Integer, MultiProbabilityNode> nodes = new HashMap<Integer, MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> seedsSIRB = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> seedsSIR = new HashSet<MultiProbabilityNode>();
		
		for (MultiProbabilityNode m : graph.getVertices()){
			nodes.put(m.getId(), m);
		}
		int seedSize = 100;
		while (seedsSIR.size() < seedSize) {
			int id = rand.nextInt(numofNodes);
			nodes.get(id).activate(sir);
			seedsSIR.add(nodes.get(id));
			//System.out.println(id);
		}
		
		while (seedsSIRB.size() < seedSize) {
			int id = rand.nextInt(numofNodes);
			nodes.get(id).activate(sirB);
			seedsSIRB.add(nodes.get(id));
			//System.out.println(id);
		}
		
		
		DiverseSIR con1 = (DiverseSIR) cList.get(0);
		DiverseSIR con2 = (DiverseSIR) cList.get(1);
		
		HashMap<Integer, Set<MultiProbabilityNode>> activated = new HashMap<Integer, Set<MultiProbabilityNode>>();
		activated.put(1, seedsSIR);
		activated.put(0, seedsSIRB);
		boolean spread = true;
		boolean spreadSIR = true;
		boolean spreadSIRB = true;
		
		int sizeSIR = 0;
		int sizeSIRB = 0;
		int highestSizeSIR = 0;
		int highestSizeSIRB = 0;
		int turns = 0;
		while (spread) {
			sizeSIRB = activated.get(0).size();
			sizeSIR = activated.get(1).size();
			
			if (spreadSIRB) {con1.spread(graph, activated);}
			if (spreadSIR) {con2.spread(graph, activated);}
			if (spreadSIRB) {con1.activateNodes(activated);}
			if (spreadSIR) {con2.activateNodes(activated);}
			
			if (activated.get(0).size() > highestSizeSIRB) {
				highestSizeSIRB = activated.get(0).size();
			}
			
			if (activated.get(1).size() > highestSizeSIR) {
				highestSizeSIR = activated.get(1).size();
			}
			
			if (activated.get(1).size() == sizeSIR) {
				spreadSIR = false;
			}
			
			if (activated.get(0).size() == sizeSIRB) {
				spreadSIR = false;
			}
			
			if (!spreadSIR && !spreadSIRB || turns == 100) {
				spread = false;
			}
			
		//	System.out.println("SIR:" + activated.get(0).size());
		//	System.out.println("Highest:" + highestSizeSIR);
		//	System.out.println("SIR 2:" + activated.get(1).size());
		//	System.out.println("Highest:" + highestSizeSIS);
			turns++;
		}
		
		TIntArrayList values = new TIntArrayList();
		values.add(activated.get(0).size());
		values.add(activated.get(1).size());
		values.add(highestSizeSIRB);
		values.add(highestSizeSIR);
		return values;
	}
	public static TIntArrayList SISBoostingSIS(int numNodes) {
		Concept sis = new DiverseSIS(1, 0.05);
		Concept sisB = new DiverseSIS(0, 0.05);
		Random rand = new Random();
		ArrayList<Concept> cList = new ArrayList<Concept>();
		sisB.addConceptInteractions(sis, 1, 1);
		cList.add(sisB);
		cList.add(sis);
		
		int numofNodes = 1000;
		ArrayList<Double> spreads = new ArrayList<Double>();
		spreads.add(0.1);
		spreads.add(0.1);
		
		DiverseGraphGenerator divGraph = new DiverseGraphGenerator();
		Graph<MultiProbabilityNode,TypedWeightedEdge> graph = divGraph.ICICgraphMaker(numNodes/100, 100, 0.5, cList, spreads);
		HashMap<Integer, MultiProbabilityNode> nodes = new HashMap<Integer, MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> seedsSISB = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> seedsSIS = new HashSet<MultiProbabilityNode>();
		
		for (MultiProbabilityNode m : graph.getVertices()){
			nodes.put(m.getId(), m);
		}
		int seedSize = 100;
		while (seedsSISB.size() < seedSize) {
			int id = rand.nextInt(numofNodes);
			nodes.get(id).activate(sisB);
			seedsSISB.add(nodes.get(id));
			//System.out.println(id);
		}
		
		while (seedsSIS.size() < seedSize) {
			int id = rand.nextInt(numofNodes);
			nodes.get(id).activate(sis);
			seedsSIS.add(nodes.get(id));
			//System.out.println(id);
		}
		
		for (MultiProbabilityNode m : graph.getVertices()){
			if (seedsSIS.contains(m)) {
				//System.out.println(m.getId());
			}
		}
		
		DiverseSIS con1 = (DiverseSIS) cList.get(0);
		DiverseSIS con2 = (DiverseSIS) cList.get(1);
		
		HashMap<Integer, Set<MultiProbabilityNode>> activated = new HashMap<Integer, Set<MultiProbabilityNode>>();
		activated.put(1, seedsSIS);
		activated.put(0, seedsSISB);
		boolean spread = true;
		boolean spreadSISB = true;
		boolean spreadSIS = true;
		
		int sizeSISB = 0;
		int sizeSIS = 0;
		int highestSizeSISB = 0;
		int highestSizeSIS = 0;
		int turns = 0;
		while (spread) {
			sizeSISB = activated.get(0).size();
			sizeSIS = activated.get(1).size();
			
			if (spreadSISB) {con1.spread(graph, activated);}
			if (spreadSIS) {con2.spread(graph, activated);}
			if (spreadSISB) {con1.activateNodes(activated);}
			if (spreadSIS) {con2.activateNodes(activated);}
			
			if (activated.get(0).size() > highestSizeSISB) {
				highestSizeSISB = activated.get(0).size();
			}
			
			if (activated.get(1).size() > highestSizeSIS) {
				highestSizeSIS = activated.get(1).size();
			}
			
			if (activated.get(0).size() == sizeSISB) {
				spreadSISB = false;
			}
			
			if (activated.get(1).size() == sizeSIS) {
				spreadSIS = false;
			}
			
			if (!spreadSISB && !spreadSIS || turns == 100) {
				spread = false;
			}
			
		//	System.out.println("SIS:" + activated.get(0).size());
		//	System.out.println("Highest:" + highestSizeSIR);
		//	System.out.println("SIS 2:" + activated.get(1).size());
		//	System.out.println("Highest:" + highestSizeSIS);
			turns++;
		}
		TIntArrayList values = new TIntArrayList();
		values.add(activated.get(0).size());
		values.add(activated.get(1).size());
		values.add(highestSizeSISB);
		values.add(highestSizeSIS);
		return values;
	}
	public static TIntArrayList SIRBoostingSIS(int numNodes){
		Concept sir = new DiverseSIR(1, 0.05);
		Concept sis = new DiverseSIS(0, 0.05);
		Random rand = new Random();
		ArrayList<Concept> cList = new ArrayList<Concept>();
		sis.addConceptInteractions(sir, 1, 1);
		cList.add(sis);
		cList.add(sir);
		int numofNodes = 1000;
		ArrayList<Double> spreads = new ArrayList<Double>();
		spreads.add(0.1);
		spreads.add(0.1);
		
		DiverseGraphGenerator divGraph = new DiverseGraphGenerator();
		Graph<MultiProbabilityNode,TypedWeightedEdge> graph = divGraph.ICICgraphMaker(numNodes/100, 100, 0.5, cList, spreads);
		HashMap<Integer, MultiProbabilityNode> nodes = new HashMap<Integer, MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> seedsSIS = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> seedsSIR = new HashSet<MultiProbabilityNode>();
		
		for (MultiProbabilityNode m : graph.getVertices()){
			nodes.put(m.getId(), m);
		}
		int seedSize = 100;
		while (seedsSIR.size() < seedSize) {
			int id = rand.nextInt(numofNodes);
			nodes.get(id).activate(sir);
			seedsSIR.add(nodes.get(id));
			//System.out.println(id);
		}
		
		while (seedsSIS.size() < seedSize) {
			int id = rand.nextInt(numofNodes);
			nodes.get(id).activate(sis);
			seedsSIS.add(nodes.get(id));
			//System.out.println(id);
		}
		
		for (MultiProbabilityNode m : graph.getVertices()){
			if (seedsSIS.contains(m)) {
				//System.out.println(m.getId());
			}
		}
		
		DiverseSIS con1 = (DiverseSIS) cList.get(0);
		DiverseSIR con2 = (DiverseSIR) cList.get(1);
		
		HashMap<Integer, Set<MultiProbabilityNode>> activated = new HashMap<Integer, Set<MultiProbabilityNode>>();
		activated.put(1, seedsSIR);
		activated.put(0, seedsSIS);
		boolean spread = true;
		boolean spreadSIR = true;
		boolean spreadSIS = true;
		
		int sizeSIR = 0;
		int sizeSIS = 0;
		int highestSizeSIR = 0;
		int highestSizeSIS = 0;
		int turns = 0;
		while (spread) {
			sizeSIS = activated.get(0).size();
			sizeSIR = activated.get(1).size();
			
			if (spreadSIS) {con1.spread(graph, activated);}
			if (spreadSIR) {con2.spread(graph, activated);}
			if (spreadSIS) {con1.activateNodes(activated);}
			if (spreadSIR) {con2.activateNodes(activated);}
			
			if (activated.get(0).size() > highestSizeSIS) {
				highestSizeSIS = activated.get(0).size();
			}
			
			if (activated.get(1).size() > highestSizeSIR) {
				highestSizeSIR = activated.get(1).size();
			}
			
			if (activated.get(0).size() == sizeSIS) {
				spreadSIS = false;
			}
			
			if (activated.get(1).size() == sizeSIR) {
				spreadSIR = false;
			}
			
			if (!spreadSIR && !spreadSIS || turns == 100) {
				spread = false;
			}
			
		//	System.out.println("SIR:" + activated.get(0).size());
		//	System.out.println("Highest:" + highestSizeSIR);
		//	System.out.println("SIS:" + activated.get(1).size());
		//	System.out.println("Highest:" + highestSizeSIS);
			turns++;
		}
		
		TIntArrayList values = new TIntArrayList();
		values.add(activated.get(0).size());
		values.add(activated.get(1).size());
		values.add(highestSizeSIS);
		values.add(highestSizeSIR);
		return values;
	}
	public static TIntArrayList SISBoostingSIR(int numNodes){
		Concept sir = new DiverseSIR(0, 0.05);
		Concept sis = new DiverseSIS(1, 0.05);
		Random rand = new Random();
		ArrayList<Concept> cList = new ArrayList<Concept>();
		sir.addConceptInteractions(sis, 1, 1);
		cList.add(sir);
		cList.add(sis);
		int numofNodes = 1000;
		ArrayList<Double> spreads = new ArrayList<Double>();
		spreads.add(0.1);
		spreads.add(0.1);
		
		DiverseGraphGenerator divGraph = new DiverseGraphGenerator();
		Graph<MultiProbabilityNode,TypedWeightedEdge> graph = divGraph.ICICgraphMaker(numNodes/100, 100, 0.5, cList, spreads);
		HashMap<Integer, MultiProbabilityNode> nodes = new HashMap<Integer, MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> seedsSIS = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> seedsSIR = new HashSet<MultiProbabilityNode>();
		
		for (MultiProbabilityNode m : graph.getVertices()){
			nodes.put(m.getId(), m);
		}
		int seedSize = 100;
		while (seedsSIR.size() < seedSize) {
			int id = rand.nextInt(numofNodes);
			nodes.get(id).activate(sir);
			seedsSIR.add(nodes.get(id));
			//System.out.println(id);
		}
		
		while (seedsSIS.size() < seedSize) {
			int id = rand.nextInt(numofNodes);
			nodes.get(id).activate(sis);
			seedsSIS.add(nodes.get(id));
			//System.out.println(id);
		}
		
		for (MultiProbabilityNode m : graph.getVertices()){
			if (seedsSIS.contains(m)) {
				//System.out.println(m.getId());
			}
		}
		
		DiverseSIR con1 = (DiverseSIR) cList.get(0);
		DiverseSIS con2 = (DiverseSIS) cList.get(1);
		
		HashMap<Integer, Set<MultiProbabilityNode>> activated = new HashMap<Integer, Set<MultiProbabilityNode>>();
		activated.put(0, seedsSIR);
		activated.put(1, seedsSIS);
		boolean spread = true;
		boolean spreadSIR = true;
		boolean spreadSIS = true;
		
		int sizeSIR = 0;
		int sizeSIS = 0;
		int highestSizeSIR = 0;
		int highestSizeSIS = 0;
		int turns = 0;
		while (spread) {
			sizeSIR = activated.get(0).size();
			sizeSIS = activated.get(1).size();
			
			if (spreadSIR) {con1.spread(graph, activated);}
			if (spreadSIS) {con2.spread(graph, activated);}
			if (spreadSIR) {con1.activateNodes(activated);}
			if (spreadSIS) {con2.activateNodes(activated);}
			
			if (activated.get(0).size() > highestSizeSIR) {
				highestSizeSIR = activated.get(0).size();
			}
			
			if (activated.get(1).size() > highestSizeSIS) {
				highestSizeSIS = activated.get(1).size();
			}
			
			if (activated.get(0).size() == sizeSIR) {
				spreadSIR = false;
			}
			
			if (activated.get(1).size() == sizeSIS) {
				spreadSIS = false;
			}
			
			if (!spreadSIR && !spreadSIS || turns == 100) {
				spread = false;
			}
			
		//	System.out.println("SIR:" + activated.get(0).size());
		//	System.out.println("Highest:" + highestSizeSIR);
		//	System.out.println("SIS:" + activated.get(1).size());
		//	System.out.println("Highest:" + highestSizeSIS);
			turns++;
		}
		TIntArrayList values = new TIntArrayList();
		values.add(activated.get(0).size());
		values.add(activated.get(1).size());
		values.add(highestSizeSIR);
		values.add(highestSizeSIS);
		return values;
	}
	public static TIntArrayList DiverseSIRTest(int numNodes) {
		Concept sis = new DiverseSIR(0, 0.05);
		
		Random rand = new Random();
		ArrayList<Concept> cList = new ArrayList<Concept>();
		cList.add(sis);
		
		int numofNodes = 1000;
		ArrayList<Double> spreads = new ArrayList<Double>();
		spreads.add(0.1);
		
		DiverseGraphGenerator divGraph = new DiverseGraphGenerator();
		Graph<MultiProbabilityNode,TypedWeightedEdge> graph = divGraph.ICICgraphMaker(numNodes/100, 100, 0.5, cList, spreads);
		HashMap<Integer, MultiProbabilityNode> nodes = new HashMap<Integer, MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> seedsSIS = new HashSet<MultiProbabilityNode>();
		
		for (MultiProbabilityNode m : graph.getVertices()){
			nodes.put(m.getId(), m);
		}
		int seedSize = 100;
		while (seedsSIS.size() < seedSize) {
			int id = rand.nextInt(numofNodes);
			nodes.get(id).activate(sis);
			seedsSIS.add(nodes.get(id));
			//System.out.println(id);
		}
		
		for (MultiProbabilityNode m : graph.getVertices()){
			if (seedsSIS.contains(m)) {
		//		System.out.println(m.getId());
			}
		}
		
		DiverseSIR con1 = (DiverseSIR) cList.get(0);
		
		HashMap<Integer, Set<MultiProbabilityNode>> activated = new HashMap<Integer, Set<MultiProbabilityNode>>();
		activated.put(0, seedsSIS);
		
		boolean spread = true;
		int size = 0;
		int highestSize = 0;
		int turns = 0;
		while (spread && turns < 100) {
			size = activated.get(0).size();
			con1.spread(graph, activated);
			con1.activateNodes(activated);
			
			if (activated.get(0).size() > highestSize) {
				highestSize = activated.get(0).size();
			}
			
			if (activated.get(0).size()== size) {
				spread = false;
			}
			
		//	System.out.println("LT:" + activated.get(0).size());
	//		System.out.println("Highest:" + highestSize);
			turns++;
		}
		
		TIntArrayList values = new TIntArrayList();
		values.add(activated.get(0).size());
		values.add(highestSize);
		return values;
	}
	public static TIntArrayList DiverseSISTest(int numNodes) {
		Concept sis = new DiverseSIS(0, 0.05);
		
		Random rand = new Random();
		ArrayList<Concept> cList = new ArrayList<Concept>();
		cList.add(sis);
		
		int numofNodes = 1000;
		ArrayList<Double> spreads = new ArrayList<Double>();
		spreads.add(0.1);
		
		DiverseGraphGenerator divGraph = new DiverseGraphGenerator();
		Graph<MultiProbabilityNode,TypedWeightedEdge> graph = divGraph.ICICgraphMaker(numNodes/100, 100, 0.5, cList, spreads);
		HashMap<Integer, MultiProbabilityNode> nodes = new HashMap<Integer, MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> seedsSIS = new HashSet<MultiProbabilityNode>();
		
		for (MultiProbabilityNode m : graph.getVertices()){
			nodes.put(m.getId(), m);
		}
		int seedSize = 100;
		while (seedsSIS.size() < seedSize) {
			int id = rand.nextInt(numofNodes);
			nodes.get(id).activate(sis);
			seedsSIS.add(nodes.get(id));
			//System.out.println(id);
		}
		
		for (MultiProbabilityNode m : graph.getVertices()){
			if (seedsSIS.contains(m)) {
				//System.out.println(m.getId());
			}
		}
		
		DiverseSIS con1 = (DiverseSIS) cList.get(0);
		
		HashMap<Integer, Set<MultiProbabilityNode>> activated = new HashMap<Integer, Set<MultiProbabilityNode>>();
		activated.put(0, seedsSIS);
		
		boolean spread = true;
		int size = 0;
		int highestSize = 0;
		int turns = 0;
		while (spread && turns < 100) {
			size = activated.get(0).size();
			con1.spread(graph, activated);
			con1.activateNodes(activated);
			
			if (activated.get(0).size() > highestSize) {
				highestSize = activated.get(0).size();
			}
			
			if (activated.get(0).size()== size) {
				spread = false;
			}
			
			//System.out.println("LT:" + activated.get(0).size());
			turns++;
		}
	//	System.out.println("LT:" + activated.get(0).size());
	//	System.out.println(highestSize);
		TIntArrayList values = new TIntArrayList();
		values.add(activated.get(0).size());
		values.add(highestSize);
		return values;
	}
	public static void bulkTests(int numNodes){
		TIntArrayList ICF;
		TIntArrayList LTF;
		File file = new File("ICLTspread" + numNodes + ".txt");
		FileWriter fw;
		BufferedWriter bw = null;
		HashMap<Integer, Set<MultiProbabilityNode>> activated;
		ICF = new TIntArrayList();
		LTF = new TIntArrayList();
		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
		double j = 7000;
		double total = 14000;
		for (int i = 0; i < 1000; i++) {
			System.out.println((int)((j/total)*100) + " % completed" );
			j++;
			activated = BoostingICLTspread(numNodes);
			ICF.add(activated.get(1).size());
			LTF.add(activated.get(0).size());
			try {
				bw.write("Run " + i + "\n");
				bw.write("IC Boosted: " + activated.get(1).size() + "\n");
				bw.write("LT: " + activated.get(0).size() + "\n");
				bw.write("----------\n");
				bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try{
			bw.write("Average for IC Boosted: " + ICF.sum()/1000 + "\n");
			bw.write("Average for LT: " + LTF.sum()/1000 + "\n");
			bw.flush();
		} catch(IOException e){
			e.printStackTrace();
		}
		
		
		
		
		
		
		file = new File("LTLTspread" + numNodes + ".txt");
		bw = null;
		ICF = new TIntArrayList();
		LTF = new TIntArrayList();
		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < 1000; i++) {
			System.out.println((int)((j/total)*100) + " % completed" );
			j++;
			activated = BoostingLTLTspread(numNodes);
			ICF.add(activated.get(0).size());
			LTF.add(activated.get(1).size());
			try {
				bw.write("Run " + i + "\n");
				bw.write("LT Boosted: " + activated.get(0).size() + "\n");
				bw.write("LT: " + activated.get(1).size() + "\n");
				bw.write("----------\n");
				bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try{
			bw.write("Average for LT Boosted: " + ICF.sum()/1000 + "\n");
			bw.write("Average for LT: " + LTF.sum()/1000 + "\n");
			bw.flush();
		} catch(IOException e){
			e.printStackTrace();
		}
		
		
		
		file = new File("LTICspread" + numNodes + ".txt");
		bw = null;
		ICF = new TIntArrayList();
		LTF = new TIntArrayList();
		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < 1000; i++) {
			System.out.println((int)((j/total)*100) + " % completed" );
			j++;
			activated = BoostingLTICspread(numNodes);
			LTF.add(activated.get(0).size());
			ICF.add(activated.get(1).size());
			try {
				bw.write("Run " + i + "\n");
				bw.write("LT Boosted: " + activated.get(0).size() + "\n");
				bw.write("IC: " + activated.get(1).size() + "\n");
				bw.write("----------\n");
				bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try{
			bw.write("Average for LT Boosted: " + LTF.sum()/1000 + "\n");
			bw.write("Average for IC: " + ICF.sum()/1000 + "\n");
			bw.flush();
		} catch(IOException e){
			e.printStackTrace();
		}
		
		
		
		file = new File("ICICspread" + numNodes + ".txt");
		bw = null;
		ICF = new TIntArrayList();
		LTF = new TIntArrayList();
		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < 1000; i++) {
			System.out.println((int)((j/total)*100) + " % completed" );
			j++;
			activated = BoostingICICspread(numNodes);
			ICF.add(activated.get(0).size());
			LTF.add(activated.get(1).size());
			try {
				bw.write("Run " + i + "\n");
				bw.write("IC Boosted: " + activated.get(0).size() + "\n");
				bw.write("IC: " + activated.get(1).size() + "\n");
				bw.write("----------\n");
				bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try{
			bw.write("Average for IC Boosted: " + ICF.sum()/1000 + "\n");
			bw.write("Average for IC: " + LTF.sum()/1000 + "\n");
			bw.flush();
		} catch(IOException e){
			e.printStackTrace();
		}
		
		
		file = new File("Blockingtest" + numNodes + ".txt");
		bw = null;
		ICF = new TIntArrayList();
		LTF = new TIntArrayList();
		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < 1000; i++) {
			System.out.println((int)((j/total)*100) + " % completed" );
			j++;
			activated = blockingTest(numNodes);
			LTF.add(activated.get(0).size());
			ICF.add(activated.get(1).size());
			try {
				bw.write("Run " + i + "\n");
				bw.write("LT: " + activated.get(0).size() + "\n");
				bw.write("IC: " + activated.get(1).size() + "\n");
				bw.write("----------\n");
				bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try{
			bw.write("Average for LT: " + LTF.sum()/1000 + "\n");
			bw.write("Average for IC: " + ICF.sum()/1000 + "\n");
			bw.flush();
		} catch(IOException e){
			e.printStackTrace();
		}
		
		file = new File("DiverseLT" + numNodes + ".txt");
		bw = null;
		ICF = new TIntArrayList();
		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < 1000; i++) {
			System.out.println((int)((j/total)*100) + " % completed" );
			j++;
			activated = diverseLTSpreadingTest(numNodes);
			ICF.add(activated.get(0).size());
			try {
				bw.write("Run " + i + "\n");
				bw.write("LT: " + activated.get(0).size() + "\n");
			//	bw.write("Concept 2: " + activated.get(1).size() + "\n");
				bw.write("----------\n");
				bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try{
			bw.write("Average for LT: " + ICF.sum()/1000 + "\n");
			bw.flush();
		} catch(IOException e){
			e.printStackTrace();
		}
		
		file = new File("DiverseIC" + numNodes + ".txt");
		bw = null;
		ICF = new TIntArrayList();
		try {
			file.createNewFile();
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < 1000; i++) {
			System.out.println((int)((j/total)*100) + " % completed" );
			j++;
			activated = diverseICSpreadingTest(numNodes);
			ICF.add(activated.get(1).size());
			try {
				bw.write("Run " + i + "\n");
				bw.write("IC: " + activated.get(1).size() + "\n");
				//bw.write("IC: " + activated.get(1).size() + "\n");
				bw.write("----------\n");
				bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try{
			bw.write("Average for IC: " + ICF.sum()/1000 + "\n");
			bw.flush();
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	public static HashMap<Integer, Set<MultiProbabilityNode>> BoostingICLTspread(int numNodes){
		Concept c1 = new DiverseLTConcept(0);
		Concept c2 = new DiverseICConcept(1);
		Random rand = new Random();
		ArrayList<Concept> cList = new ArrayList<Concept>();
		cList.add(c1);
		cList.add(c2);
		int numofNodes = 1000;
		c1.addConceptInteractions(c2, 0, 0);
		c2.addConceptInteractions(c1, 1, 1);
		DiverseGraphGenerator divGraph = new DiverseGraphGenerator();
		Graph<MultiProbabilityNode,TypedWeightedEdge> graph = divGraph.multiGraphMaker(numNodes/100, 100, 0.1, cList, 0,0.1);
		HashMap<Integer, MultiProbabilityNode> nodes = new HashMap<Integer, MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> seedsLT = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> seedsIC = new HashSet<MultiProbabilityNode>();
		for (MultiProbabilityNode m : graph.getVertices()){
			nodes.put(m.getId(), m);
		}
		int seedSize = 100;
		while (seedsLT.size() < seedSize) {
			int id = rand.nextInt(numofNodes);
			nodes.get(id).activate(c1);
			seedsLT.add(nodes.get(id));
			//System.out.println(id);
		}
		
		while (seedsIC.size() < seedSize) {
			int id = rand.nextInt(numofNodes);
			nodes.get(id).activate(c2);
			seedsIC.add(nodes.get(id));
		}
		
		for (MultiProbabilityNode m : graph.getVertices()){
			if (seedsLT.contains(m)) {
			//	System.out.println(m.getId());
			}
		}
		//System.out.println("--------------------------------");
		for (MultiProbabilityNode m : graph.getVertices()){
			if (seedsIC.contains(m)) {
		//		System.out.println(m.getId());
			}
		}
		
		DiverseLTConcept con1 = (DiverseLTConcept) cList.get(0);
		DiverseICConcept con2 = (DiverseICConcept) cList.get(1);
		
		HashMap<Integer, Set<MultiProbabilityNode>> activated = new HashMap<Integer, Set<MultiProbabilityNode>>();
		activated.put(0, seedsLT);
		activated.put(1, seedsIC);
		//System.out.println("Size of IC seed set:" + seedsIC.size());
		con2.seedInitialise(seedsIC);
		boolean spread = true;
		boolean spreadLT = true;
		boolean spreadIC = true;
		int sizeLT = activated.get(0).size();
		int sizeIC = activated.get(1).size();
		
		while (spread) {
			sizeLT = activated.get(0).size();
			sizeIC = activated.get(1).size();
			if (spreadLT) {
				con1.spread(graph, activated);
			}
		//	System.out.println("--------------------------------");
			
			if (spreadIC) {
				con2.spread(graph, activated);
			}
			
			if (spreadLT){
				con1.activateNodes(activated);
			}
			if (spreadIC) {
				con2.activateNodes(activated);
			}

		//	System.out.println("LT:" + activated.get(0).size());
		//	System.out.println("IC:" + activated.get(1).size());
		//	System.out.println("-------------------------");
			if (activated.get(0).size() <= sizeLT) {spreadLT = false;}
			if (activated.get(1).size() <= sizeIC) {spreadIC = false;}
			if (!spreadLT && !spreadIC) {
				spread = false;
			}
		}
		return activated;
	}
	public static HashMap<Integer, Set<MultiProbabilityNode>> BoostingICICspread(int numNodes){
		Concept c1 = new DiverseICConcept(0);
		Concept c2 = new DiverseICConcept(1);
		Random rand = new Random();
		ArrayList<Concept> cList = new ArrayList<Concept>();
		cList.add(c1);
		cList.add(c2);
		int numofNodes = 1000;
		c1.addConceptInteractions(c2, 1, 1);
		c2.addConceptInteractions(c1, 0, 0);
		DiverseGraphGenerator divGraph = new DiverseGraphGenerator();
		
		ArrayList<Double> spreads = new ArrayList<Double>();
		spreads.add(0.1);
		spreads.add(0.1);
		Graph<MultiProbabilityNode,TypedWeightedEdge> graph = divGraph.ICICgraphMaker(numNodes/100, 100, 0.5, cList, spreads);
		HashMap<Integer, MultiProbabilityNode> nodes = new HashMap<Integer, MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> seedsLT = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> seedsIC = new HashSet<MultiProbabilityNode>();
		for (MultiProbabilityNode m : graph.getVertices()){
			nodes.put(m.getId(), m);
		}
		int seedSize = 100;
		while (seedsLT.size() < seedSize) {
			int id = rand.nextInt(numofNodes);
			nodes.get(id).activate(c1);
			seedsLT.add(nodes.get(id));
			//System.out.println(id);
		}
		
		while (seedsIC.size() < seedSize) {
			int id = rand.nextInt(numofNodes);
			nodes.get(id).activate(c2);
			seedsIC.add(nodes.get(id));
		}
		
		for (MultiProbabilityNode m : graph.getVertices()){
			if (seedsLT.contains(m)) {
				//System.out.println(m.getId());
			}
		}
	//	System.out.println("--------------------------------");
		for (MultiProbabilityNode m : graph.getVertices()){
			if (seedsIC.contains(m)) {
				//System.out.println(m.getId());
			}
		}
	//	System.out.println("--------------------------------");
		DiverseICConcept con1 = (DiverseICConcept) cList.get(0);
		DiverseICConcept con2 = (DiverseICConcept) cList.get(1);
		
		HashMap<Integer, Set<MultiProbabilityNode>> activated = new HashMap<Integer, Set<MultiProbabilityNode>>();
		activated.put(0, seedsLT);
		activated.put(1, seedsIC);
		
		//con2.seedInitialise(seedsIC);
		boolean spread = true;
		boolean spreadLT = true;
		boolean spreadIC = true;
		int sizeLT = activated.get(0).size();
		int sizeIC = activated.get(1).size();
		con1.seedInitialise(activated.get(0));
		con2.seedInitialise(activated.get(1));
		while (spread) {
			sizeLT = activated.get(0).size();
			sizeIC = activated.get(1).size();
			if (spreadLT) {
				con1.spread(graph, activated);
			}
			//System.out.println("=====================");
			
			if (spreadIC) {
				con2.spread(graph, activated);
			}
			
			if (spreadLT){
				con1.activateNodes(activated);
			}
			if (spreadIC) {
				con2.activateNodes(activated);
			}

		//	System.out.println("LT:" + activated.get(0).size());
		//	System.out.println("IC:" + activated.get(1).size());
		//	System.out.println("-------------------------");
			if (activated.get(0).size() <= sizeLT) {spreadLT = false;}
			if (activated.get(1).size() <= sizeIC) {spreadIC = false;}
			if (!spreadLT && !spreadIC) {
				spread = false;
			}
		}
		
		return activated;
	}
	public static HashMap<Integer, Set<MultiProbabilityNode>> BoostingLTLTspread(int numNodes){
		Concept c1 = new DiverseLTConcept(0);
		Concept c2 = new DiverseLTConcept(1);
		Random rand = new Random();
		ArrayList<Concept> cList = new ArrayList<Concept>();
		cList.add(c1);
		cList.add(c2);
		int numofNodes = 1000;
		c1.addConceptInteractions(c2, 1, 1);
		c2.addConceptInteractions(c1, 0, 0);
		DiverseGraphGenerator divGraph = new DiverseGraphGenerator();
		Graph<MultiProbabilityNode,TypedWeightedEdge> graph = divGraph.LTLTgraphMaker(numNodes/100, 100, 0.5, cList, 0, 1);
		HashMap<Integer, MultiProbabilityNode> nodes = new HashMap<Integer, MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> seedsLT = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> seedsIC = new HashSet<MultiProbabilityNode>();
		for (MultiProbabilityNode m : graph.getVertices()){
			nodes.put(m.getId(), m);
		}
		int seedSize = 100;
		while (seedsLT.size() < seedSize) {
			int id = rand.nextInt(numofNodes);
			nodes.get(id).activate(c1);
			seedsLT.add(nodes.get(id));
			//System.out.println(id);
		}
		
		while (seedsIC.size() < seedSize) {
			int id = rand.nextInt(numofNodes);
			nodes.get(id).activate(c2);
			seedsIC.add(nodes.get(id));
		}
		
//		System.out.println("--------------------------------");

		
		DiverseLTConcept con1 = (DiverseLTConcept) cList.get(0);
		DiverseLTConcept con2 = (DiverseLTConcept) cList.get(1);
		
		HashMap<Integer, Set<MultiProbabilityNode>> activated = new HashMap<Integer, Set<MultiProbabilityNode>>();
		activated.put(0, seedsLT);
		activated.put(1, seedsIC);
		
		//con2.seedInitialise(seedsIC);
		boolean spread = true;
		boolean spreadLT = true;
		boolean spreadIC = true;
		int sizeLT = activated.get(0).size();
		int sizeIC = activated.get(1).size();
		
		while (spread) {
			sizeLT = activated.get(0).size();
			sizeIC = activated.get(1).size();
			if (spreadLT) {
				con1.spread(graph, activated);
			}
			//System.out.println("--------------------------------");
			
			if (spreadIC) {
				con2.spread(graph, activated);
			}
			
			if (spreadLT){
				con1.activateNodes(activated);
			}
			if (spreadIC) {
				con2.activateNodes(activated);
			}

		//	System.out.println("LT:" + activated.get(0).size());
		//	System.out.println("IC:" + activated.get(1).size());
		//	System.out.println("-------------------------");
			if (activated.get(0).size() <= sizeLT) {spreadLT = false;}
			if (activated.get(1).size() <= sizeIC) {spreadIC = false;}
			if (!spreadLT && !spreadIC) {
				spread = false;
			}
		}
		
		return activated;
	}
	public static HashMap<Integer, Set<MultiProbabilityNode>> BoostingLTICspread(int numNodes){
		Concept c1 = new DiverseLTConcept(0);
		Concept c2 = new DiverseICConcept(1);
		Random rand = new Random();
		ArrayList<Concept> cList = new ArrayList<Concept>();
		cList.add(c1);
		cList.add(c2);
		int numofNodes = 1000;
		c1.addConceptInteractions(c2, 1, 1);
		c2.addConceptInteractions(c1, 0, 0);
		DiverseGraphGenerator divGraph = new DiverseGraphGenerator();
		Graph<MultiProbabilityNode,TypedWeightedEdge> graph = divGraph.multiGraphMaker(numNodes/100, 100, 0.1, cList, 0,0.1);
		HashMap<Integer, MultiProbabilityNode> nodes = new HashMap<Integer, MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> seedsLT = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> seedsIC = new HashSet<MultiProbabilityNode>();
		for (MultiProbabilityNode m : graph.getVertices()){
			nodes.put(m.getId(), m);
		}
		int seedSize = 100;
		while (seedsLT.size() < seedSize) {
			int id = rand.nextInt(numofNodes);
			nodes.get(id).activate(c1);
			seedsLT.add(nodes.get(id));
			//System.out.println(id);
		}
		
		while (seedsIC.size() < seedSize) {
			int id = rand.nextInt(numofNodes);
			nodes.get(id).activate(c2);
			seedsIC.add(nodes.get(id));
		}
		
		
		DiverseLTConcept con1 = (DiverseLTConcept) cList.get(0);
		DiverseICConcept con2 = (DiverseICConcept) cList.get(1);
		
		HashMap<Integer, Set<MultiProbabilityNode>> activated = new HashMap<Integer, Set<MultiProbabilityNode>>();
		activated.put(0, seedsLT);
		activated.put(1, seedsIC);
		
		con2.seedInitialise(seedsIC);
		boolean spread = true;
		boolean spreadLT = true;
		boolean spreadIC = true;
		int sizeLT = activated.get(0).size();
		int sizeIC = activated.get(1).size();
		
		while (spread) {
			sizeLT = activated.get(0).size();
			sizeIC = activated.get(1).size();
			if (spreadLT) {
				con1.spread(graph, activated);
			}
		//	System.out.println("--------------------------------");
			
			if (spreadIC) {
				con2.spread(graph, activated);
			}
			
			if (spreadLT){
				con1.activateNodes(activated);
			}
			if (spreadIC) {
				con2.activateNodes(activated);
			}

		//	System.out.println("LT:" + activated.get(0).size());
		//	System.out.println("IC:" + activated.get(1).size());
		//	System.out.println("-------------------------");
			if (activated.get(0).size() <= sizeLT) {spreadLT = false;}
			if (activated.get(1).size() <= sizeIC) {spreadIC = false;}
			if (!spreadLT && !spreadIC) {
				spread = false;
			}
		}
		
		return activated;
	}
	public static HashMap<Integer, Set<MultiProbabilityNode>> blockingTest(int numNodes){
		Concept c1 = new BlockingLTConcept(0);
		Concept c2 = new BlockingICConcept(1);
		Random rand = new Random();
		ArrayList<Concept> cList = new ArrayList<Concept>();
		cList.add(c1);
		cList.add(c2);
		int numofNodes = 1000;
		c1.addConceptInteractions(c2, 0, 0);
		c2.addConceptInteractions(c1, 0, 0);
		DiverseGraphGenerator divGraph = new DiverseGraphGenerator();
		Graph<MultiProbabilityNode,TypedWeightedEdge> graph = divGraph.multiGraphMaker(numNodes/100, 100, 0.1, cList, 0,0.1);
		HashMap<Integer, MultiProbabilityNode> nodes = new HashMap<Integer, MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> seedsLT = new HashSet<MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> seedsIC = new HashSet<MultiProbabilityNode>();
		for (MultiProbabilityNode m : graph.getVertices()){
			nodes.put(m.getId(), m);
		}
		int seedSize = 100;
		while (seedsLT.size() < seedSize) {
			int id = rand.nextInt(numofNodes);
			nodes.get(id).activate(c1);
			seedsLT.add(nodes.get(id));
			//System.out.println(id);
		}
		
		while (seedsIC.size() < seedSize) {
			int id = rand.nextInt(numofNodes);
			if (!nodes.get(id).isActivated(c1)) {
				nodes.get(id).activate(c2);
				seedsIC.add(nodes.get(id));
			}
		}
		
		for (MultiProbabilityNode m : graph.getVertices()){
			if (seedsLT.contains(m)) {
		//		System.out.println(m.getId());
			}
		}
	//	System.out.println("--------------------------------");
		for (MultiProbabilityNode m : graph.getVertices()){
			if (seedsIC.contains(m)) {
	//			System.out.println(m.getId());
			}
		}
		
		BlockingLTConcept con1 = (BlockingLTConcept) cList.get(0);
		BlockingICConcept con2 = (BlockingICConcept) cList.get(1);
		
		HashMap<Integer, Set<MultiProbabilityNode>> activated = new HashMap<Integer, Set<MultiProbabilityNode>>();
		activated.put(0, seedsLT);
		activated.put(1, seedsIC);
		
		con2.seedInitialise(seedsIC);
		boolean spread = true;
		boolean spreadLT = true;
		boolean spreadIC = true;
		int sizeLT = activated.get(0).size();
		int sizeIC = activated.get(1).size();
		
		while (spread) {
			sizeLT = activated.get(0).size();
			sizeIC = activated.get(1).size();
			if (spreadLT) {
				con1.spread(graph, activated, 0);
			}
	//		System.out.println("--------------------------------");
			
			if (spreadIC) {
				con2.spread(graph);
			}
			
			if (spreadLT){
				con1.activateNodes(activated);
			}
			if (spreadIC) {
				con2.activateNodes(activated);
			}
			
			HashSet<MultiProbabilityNode> remove = new HashSet<MultiProbabilityNode>();
			for (MultiProbabilityNode m : activated.get(0)) {
				if (activated.get(1).contains(m)) {
					if (rand.nextDouble() > 0.5) {
						activated.get(1).remove(m);
						m.deactivate(con2);
					} 
					else {
						remove.add(m);
						m.deactivate(con1);
					}
				}
			}
			
			activated.get(0).removeAll(remove);
	//		System.out.println("LT:" + activated.get(0).size());
	//		System.out.println("IC:" + activated.get(1).size());
	//		System.out.println("-------------------------");
			if (activated.get(0).size() <= sizeLT) {spreadLT = false;}
			if (activated.get(1).size() <= sizeIC) {spreadIC = false;}
			if (!spreadLT && !spreadIC) {
				spread = false;
			}
		}
		
		return activated;
	}
	public static HashMap<Integer, Set<MultiProbabilityNode>> diverseLTSpreadingTest(int numNodes){
		Concept c1 = new DiverseLTConcept(0);
		Random rand = new Random();
		ArrayList<Concept> cList = new ArrayList<Concept>();
		cList.add(c1);
		
		DiverseGraphGenerator divGraph = new DiverseGraphGenerator();
		Graph<MultiProbabilityNode,TypedWeightedEdge> graph = divGraph.multiGraphMaker(numNodes/100, 100, 0.1, cList, 0, 0.2);
		HashMap<Integer, MultiProbabilityNode> nodes = new HashMap<Integer, MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> seeds = new HashSet<MultiProbabilityNode>();
		
		for (MultiProbabilityNode m : graph.getVertices()){
			nodes.put(m.getId(), m);
		}
		int seedSize = 100;
		while (seeds.size() < seedSize) {
			int id = rand.nextInt(1000);
			nodes.get(id).activate(c1);
			seeds.add(nodes.get(id));
			//System.out.println(id);
		}
		
		for (MultiProbabilityNode m : graph.getVertices()){
			if (seeds.contains(m)) {
		//		System.out.println(m.getId());
			}
		}
		
		DiverseLTConcept con1 = (DiverseLTConcept) cList.get(0);
		
		HashMap<Integer, Set<MultiProbabilityNode>> activated = new HashMap<Integer, Set<MultiProbabilityNode>>();
		activated.put(0, seeds);
		
		boolean spread = true;
		while (spread) {
			spread = false;
			int originalSize = activated.get(0).size();
			con1.spread(graph, activated);
			con1.activateNodes(activated);
			
			if (activated.get(0).size() > originalSize) {
				spread = true;
			}
		///	System.out.println("Original size" + originalSize);
		//	System.out.println("New size:" + activated.get(0).size());
		}
		
		return activated;
	}
	public static HashMap<Integer, Set<MultiProbabilityNode>> diverseICSpreadingTest(int numNodes) {
		Concept c1 = new DiverseLTConcept(0);
		Concept c2 = new DiverseICConcept(1);
		Random rand = new Random();
		ArrayList<Concept> cList = new ArrayList<Concept>();
		cList.add(c1);
		cList.add(c2);
		
		c1.addConceptInteractions(c2, 0, 0);
		c2.addConceptInteractions(c1, 0, 0);
		DiverseGraphGenerator divGraph = new DiverseGraphGenerator();
		Graph<MultiProbabilityNode,TypedWeightedEdge> graph = divGraph.multiGraphMaker(numNodes/100, 100, 0.1, cList, 0, 0.1);
		HashMap<Integer, MultiProbabilityNode> nodes = new HashMap<Integer, MultiProbabilityNode>();
		HashSet<MultiProbabilityNode> seeds = new HashSet<MultiProbabilityNode>();
		
		for (MultiProbabilityNode m : graph.getVertices()){
			nodes.put(m.getId(), m);
		}
		int seedSize = 100;
		while (seeds.size() < seedSize) {
			int id = rand.nextInt(1000);
			nodes.get(id).activate(c2);
			seeds.add(nodes.get(id));
			//System.out.println(id);
		}
		
		for (MultiProbabilityNode m : graph.getVertices()){
			if (seeds.contains(m)) {
		//		System.out.println(m.getId());
			}
		}
		
		DiverseICConcept con1 = (DiverseICConcept) cList.get(1);
		
		HashMap<Integer, Set<MultiProbabilityNode>> activated = new HashMap<Integer, Set<MultiProbabilityNode>>();
		activated.put(1, seeds);
		activated.put(0, new HashSet<MultiProbabilityNode>());
		con1.seedInitialise(seeds);
		boolean spread = true;
		while (spread) {
			spread = false;
			int originalSize = activated.get(1).size();
			con1.spread(graph, activated);
			con1.activateNodes(activated);
			
			if (activated.get(1).size() > originalSize) {
				spread = true;
			}
		//	System.out.println("Original size" + originalSize);
		//	System.out.println("New size:" + activated.get(1).size());
		}
		
		return activated;
//		for (TypedWeightedEdge m : graph.getEdges(EdgeType.UNDIRECTED)) {
//			System.out.println(m.getId());
//			System.out.println(m.getConcept());
//			System.out.println(m.getWeight());
//		}
	}
}
