package influence.concepts.cascades;
//Models cascades for concepts that have the same influence model.
//Each method takes a list of concepts, a list of seed sets for each concept, a map of ids to nodes, a seed set size
// a random number generator, a relationship strength, the graph size, the id of the controllable concept, the id of the target concept
// the graph type, the graph's secondary characteristic, and the run number
import influence.concepts.Concept;
import influence.concepts.DiverseICConcept;
import influence.concepts.DiverseLTConcept;
import influence.concepts.DiverseSIR;
import influence.concepts.DiverseSIS;
import influence.concepts.ExtendedConcept;
import influence.edges.TypedWeightedEdge;
import influence.nodes.MultiProbabilityNode;
import influence.seed.selection.SeedSelector;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;

public class BurnInTargetCascades {
	public static void LTBoostLT(ArrayList<ExtendedConcept> concepts, ArrayList<HashSet<MultiProbabilityNode>> seedGroups,
			int seedSelection, Graph<MultiProbabilityNode, TypedWeightedEdge> smg, BufferedWriter bw, int burnIn,
			HashMap<Integer, MultiProbabilityNode> nodes, int seedSize, Random rand, double conceptRelationshipStrength, int graphSize, int conceptToPick, int targetCon,
			String graphType, String gChar, int run){
			
			//assign concepts to correct Diverse concept class
			ArrayList<DiverseLTConcept> convertedCons = new ArrayList<DiverseLTConcept>();
			for (int i = 0; i < concepts.size(); i++) {
				convertedCons.add((DiverseLTConcept) concepts.get(i));
			}
			
			//set up the ability to track which concepts must continue spreading
			HashMap<Integer, Set<MultiProbabilityNode>> activated = new HashMap<Integer, Set<MultiProbabilityNode>>();
			ArrayList<Boolean> spreadBools = new ArrayList<Boolean>();
			
			//activate seed set for each concept
			for (int i = 0; i < seedGroups.size(); i++) {
				activated.put(convertedCons.get(i).getId(), seedGroups.get(i));
				spreadBools.add(true);
			}

			//initilise time step counter
			int t = 0;
			
			//track how many concepts have stopped spreading
			int falseSpread = 0;
			
			//begin cascades that do need a burn in time
			int size[] = new int[convertedCons.size()];
			
			while (t < burnIn) {
				
				//go through each concept that is not the controllable concept and spread, then activate the nodes seperately. This prevents concept order
				//being important and allows each concept to spread simulataneously. 
				for (int i = 0; i < spreadBools.size(); i++) {
					if (i != conceptToPick && spreadBools.get(i)) {
						int id = convertedCons.get(i).getId();
						size[i] = activated.get(id).size();
						
						convertedCons.get(i).spread(smg, activated);
						
					}
				}
				for (int i = 0; i < spreadBools.size(); i++) {
					if (i != conceptToPick && spreadBools.get(i)) {
						int id = convertedCons.get(i).getId();
						convertedCons.get(i).activateNodes(activated);
						if (activated.get(id).size() <= size[i]) {//this means the cascade has stopped
							spreadBools.set(i, false);
							falseSpread++;
						}
						
					}
				}
				//output results of time step
				try {
					bw.write("Timestep " + t + "\n");
					
					for (int ii = 0; ii < convertedCons.size(); ii++) {
						if (activated.containsKey(convertedCons.get(ii).getId())) {
							bw.write("Infections LT" + ii + ": " + activated.get(convertedCons.get(ii).getId()).size() + "\n");
						}
						else {
							bw.write("Infections LT" + ii + ": " + 0 + "\n");
						}
					}
					//bw.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				t++;
				
			}
			
			//burn in time has elapsed, now select seed set for controllable concept
			if (seedSelection != 1) {
				SeedSelector.seedSelectionNew(concepts, smg, seedSize, seedGroups, seedSelection, rand, graphSize, nodes, conceptToPick, targetCon, graphType, gChar, run);
			}
			
			//secondary.seedInitialise(seedsSec);
			activated.put(convertedCons.get(conceptToPick).getId(), seedGroups.get(conceptToPick));
			
			//spread as before
			while (falseSpread < spreadBools.size()) {
				for (int i = 0; i < spreadBools.size(); i++) {
					if (spreadBools.get(i)) {
						int id = convertedCons.get(i).getId();
						size[i] = activated.get(id).size();
						
						convertedCons.get(i).spread(smg, activated);
					}
				}
				for (int i = 0; i < spreadBools.size(); i++) {
					if (spreadBools.get(i)) {
						int id = convertedCons.get(i).getId();
						convertedCons.get(i).activateNodes(activated);
						if (activated.get(id).size() <= size[i]) {
							spreadBools.set(i, false);
							falseSpread++;
						}
						
					}
				}
				
				try {
					bw.write("Timestep " + t + "\n");
					
					for (int ii = 0; ii < convertedCons.size(); ii++) {
						if (activated.containsKey(convertedCons.get(ii).getId())) {
							bw.write("Infections LT" + ii + ": " + activated.get(convertedCons.get(ii).getId()).size() + "\n");
						}
						else {
							bw.write("Infections LT" + ii + ": " + 0 + "\n");
						}
					}
					//bw.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				t++;
				
			}
			//all cascades have ended, write out final result
			try {
				bw.write("Final Result" + "\n");
				for (int ii = 0; ii < convertedCons.size(); ii++) {
					if (activated.containsKey(convertedCons.get(ii).getId())) {
						bw.write("Infections LT" + ii + ": " + activated.get(convertedCons.get(ii).getId()).size() + "\n");
					}
					else {
						bw.write("Infections LT" + ii + ": " + 0 + "\n");
					}
				}
				bw.write(System.currentTimeMillis() + "\n");
				bw.write("----------------------------\n");
				//bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			for (int ii = 0; ii < convertedCons.size(); ii++) {
				if (activated.containsKey(convertedCons.get(ii).getId())) {
					System.out.println("Final Number for LT" + ii + ": " +  activated.get(convertedCons.get(ii).getId()).size());
				}
				else {
					System.out.println("Final Number for LT" + ii + ": " +  0);
				}
			}
	}
	
	public static void ICBoostIC(ArrayList<ExtendedConcept> concepts, ArrayList<HashSet<MultiProbabilityNode>> seedGroups,
			int seedSelection, Graph<MultiProbabilityNode, TypedWeightedEdge> smg, BufferedWriter bw, int burnIn,
			HashMap<Integer, MultiProbabilityNode> nodes, int seedSize, Random rand, double conceptRelationshipStrength, int graphSize, int conceptToPick, int targetCon,
			String graphType, String gChar, int run){
			
			ArrayList<DiverseICConcept> convertedCons = new ArrayList<DiverseICConcept>();
			for (int i = 0; i < concepts.size(); i++) {
				convertedCons.add((DiverseICConcept) concepts.get(i));
			}
			
			HashMap<Integer, Set<MultiProbabilityNode>> activated = new HashMap<Integer, Set<MultiProbabilityNode>>();
			ArrayList<Boolean> spreadBools = new ArrayList<Boolean>();
			
			for (int i = 0; i < seedGroups.size(); i++) {
				activated.put(convertedCons.get(i).getId(), seedGroups.get(i));
				spreadBools.add(true);
			}
			
			//System.out.println("Size of IC seed set:" + seedsIC.size());
		//	target.seedInitialise(seedsTar);

			int t = 0;
			
			boolean spread = true;
			int falseSpread = 0;
			
			for (int i = 0; i < convertedCons.size(); i++) {
				if (i != conceptToPick) {
					convertedCons.get(i).seedInitialise(seedGroups.get(i));
				}
			}
			
			int[] size = new int[convertedCons.size()];
			while (t < burnIn) {
				for (int i = 0; i < spreadBools.size(); i++) {
					if (spreadBools.get(i)) {
						int id = convertedCons.get(i).getId();
						size[i] = activated.get(id).size();
						
						convertedCons.get(i).spread(smg, activated);
					}
				}
				for (int i = 0; i < spreadBools.size(); i++) {
					if (spreadBools.get(i)) {
						int id = convertedCons.get(i).getId();
						convertedCons.get(i).activateNodes(activated);
						if (activated.get(id).size() <= size[i]) {
							spreadBools.set(i, false);
							falseSpread++;
						}
						
					}
				}
				
				try {
					bw.write("Timestep " + t + "\n");
					
					for (int ii = 0; ii < convertedCons.size(); ii++) {
						if (activated.containsKey(convertedCons.get(ii).getId())) {
							bw.write("Infections IC" + ii + ": " + activated.get(convertedCons.get(ii).getId()).size() + "\n");
						}
						else {
							bw.write("Infections IC" + ii + ": " + 0 + "\n");
						}
					}
					//bw.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				t++;
				
			}
			
			if (seedSelection != 1) {
				SeedSelector.seedSelectionNew(concepts, smg, seedSize, seedGroups, seedSelection, rand, graphSize, nodes, conceptToPick, targetCon, graphType, gChar, run);
			}
			
			//secondary.seedInitialise(seedsSec);
			activated.put(convertedCons.get(conceptToPick).getId(), seedGroups.get(conceptToPick));
			convertedCons.get(conceptToPick).seedInitialise(seedGroups.get(conceptToPick));
			
			while (falseSpread < spreadBools.size()) {
				for (int i = 0; i < spreadBools.size(); i++) {
					if (spreadBools.get(i)) {
						int id = convertedCons.get(i).getId();
						size[i] = activated.get(id).size();
						
						convertedCons.get(i).spread(smg, activated);
					}
				}
				for (int i = 0; i < spreadBools.size(); i++) {
					if (spreadBools.get(i)) {
						int id = convertedCons.get(i).getId();
						convertedCons.get(i).activateNodes(activated);
						if (activated.get(id).size() <= size[i]) {
							spreadBools.set(i, false);
							falseSpread++;
						}
						
					}
				}
				
				try {
					bw.write("Timestep " + t + "\n");
					
					for (int ii = 0; ii < convertedCons.size(); ii++) {
						if (activated.containsKey(convertedCons.get(ii).getId())) {
							bw.write("Infections IC" + ii + ": " + activated.get(convertedCons.get(ii).getId()).size() + "\n");
						}
						else {
							bw.write("Infections IC" + ii + ": " + 0 + "\n");
						}
					}
					//bw.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				t++;
				
			}
			
			try {
				bw.write("Final Result" + "\n");
				for (int ii = 0; ii < convertedCons.size(); ii++) {
					if (activated.containsKey(convertedCons.get(ii).getId())) {
						bw.write("Infections IC" + ii + ": " + activated.get(convertedCons.get(ii).getId()).size() + "\n");
					}
					else {
						bw.write("Infections IC" + ii + ": " + 0 + "\n");
					}
				}
				bw.write(System.currentTimeMillis() + "\n");
				bw.write("----------------------------\n");
				//bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			for (int ii = 0; ii < convertedCons.size(); ii++) {
				if (activated.containsKey(convertedCons.get(ii).getId())) {
					System.out.println("Final Number for IC" + ii + ": " +  activated.get(convertedCons.get(ii).getId()).size());
				}
				else {
					System.out.println("Final Number for IC" + ii + ": " +  0);
				}
			}
	}
	
	public static void SISBoostSIS(ArrayList<ExtendedConcept> concepts, ArrayList<HashSet<MultiProbabilityNode>> seedGroups,
			int seedSelection, Graph<MultiProbabilityNode, TypedWeightedEdge> smg, BufferedWriter bw, int burnIn,
			HashMap<Integer, MultiProbabilityNode> nodes, int seedSize, Random rand, double conceptRelationshipStrength, int graphSize, int conceptToPick, int targetCon,
			String graphType, String gChar, int run){
			
			ArrayList<DiverseSIS> convertedCons = new ArrayList<DiverseSIS>();
			for (int i = 0; i < concepts.size(); i++) {
				convertedCons.add((DiverseSIS) concepts.get(i));
			}
			
			HashMap<Integer, Set<MultiProbabilityNode>> activated = new HashMap<Integer, Set<MultiProbabilityNode>>();
			ArrayList<Boolean> spreadBools = new ArrayList<Boolean>();
			
			for (int i = 0; i < seedGroups.size(); i++) {
				activated.put(convertedCons.get(i).getId(), seedGroups.get(i));
				spreadBools.add(true);
			}
			
			//System.out.println("Size of IC seed set:" + seedsIC.size());
		//	target.seedInitialise(seedsTar);

			int t = 0;
			
			boolean spread = true;
			int falseSpread = 0;
			int[] size = new int[convertedCons.size()];
			while (t < burnIn) {
				for (int i = 0; i < spreadBools.size(); i++) {
					if (spreadBools.get(i)) {
						int id = convertedCons.get(i).getId();
						size[i] = activated.get(id).size();
						
						convertedCons.get(i).spread(smg, activated);
					}
				}
				for (int i = 0; i < spreadBools.size(); i++) {
					if (spreadBools.get(i)) {
						int id = convertedCons.get(i).getId();
						convertedCons.get(i).activateNodes(activated);
						if (activated.get(id).size() <= size[i]) {
							spreadBools.set(i, false);
							falseSpread++;
						}
						
					}
				}
				
				try {
					bw.write("Timestep " + t + "\n");
					
					for (int ii = 0; ii < convertedCons.size(); ii++) {
						if (activated.containsKey(convertedCons.get(ii).getId())) {
							bw.write("Infections SIS" + ii + ": " + activated.get(convertedCons.get(ii).getId()).size() + "\n");
						}
						else {
							bw.write("Infections SIS" + ii + ": " + 0 + "\n");
						}
					}
					//bw.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				t++;
				
			}
			
			
			if (seedSelection != 1) {
				SeedSelector.seedSelectionNew(concepts, smg, seedSize, seedGroups, seedSelection, rand, graphSize, nodes, conceptToPick, targetCon, graphType, gChar, run);
			}
			
			//secondary.seedInitialise(seedsSec);
			activated.put(convertedCons.get(conceptToPick).getId(), seedGroups.get(conceptToPick));
			
			while (falseSpread < spreadBools.size()) {
				for (int i = 0; i < spreadBools.size(); i++) {
					if (spreadBools.get(i)) {
						int id = convertedCons.get(i).getId();
						size[i] = activated.get(id).size();
						
						convertedCons.get(i).spread(smg, activated);
					}
				}
				for (int i = 0; i < spreadBools.size(); i++) {
					if (spreadBools.get(i)) {
						int id = convertedCons.get(i).getId();
						convertedCons.get(i).activateNodes(activated);
						if (activated.get(id).size() <= size[i]) {
							spreadBools.set(i, false);
							falseSpread++;
						}
						
					}
				}
				
				try {
					bw.write("Timestep " + t + "\n");
					
					for (int ii = 0; ii < convertedCons.size(); ii++) {
						if (activated.containsKey(convertedCons.get(ii).getId())) {
							bw.write("Infections SIS" + ii + ": " + activated.get(convertedCons.get(ii).getId()).size() + "\n");
						}
						else {
							bw.write("Infections SIS" + ii + ": " + 0 + "\n");
						}
					}
					//bw.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				t++;
				
			}
			
			try {
				bw.write("Final Result" + "\n");
				for (int ii = 0; ii < convertedCons.size(); ii++) {
					if (activated.containsKey(convertedCons.get(ii).getId())) {
						bw.write("Infections SIS" + ii + ": " + activated.get(convertedCons.get(ii).getId()).size() + "\n");
					}
					else {
						bw.write("Infections SIS" + ii + ": " + 0 + "\n");
					}
				}
				bw.write(System.currentTimeMillis() + "\n");
				bw.write("----------------------------\n");
				//bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			for (int ii = 0; ii < convertedCons.size(); ii++) {
				if (activated.containsKey(convertedCons.get(ii).getId())) {
					System.out.println("Final Number for SIS" + ii + ": " +  activated.get(convertedCons.get(ii).getId()).size());
				}
				else {
					System.out.println("Final Number for SIS" + ii + ": " +  0);
				}
			}
	}
	public static void SIRBoostSIR(ArrayList<ExtendedConcept> concepts, ArrayList<HashSet<MultiProbabilityNode>> seedGroups,
			int seedSelection, Graph<MultiProbabilityNode, TypedWeightedEdge> smg, BufferedWriter bw, int burnIn,
			HashMap<Integer, MultiProbabilityNode> nodes, int seedSize, Random rand, double conceptRelationshipStrength, int graphSize, int conceptToPick, int targetCon,
			String graphType, String gChar, int run){
			
			ArrayList<DiverseSIR> convertedCons = new ArrayList<DiverseSIR>();
			for (int i = 0; i < concepts.size(); i++) {
				convertedCons.add((DiverseSIR) concepts.get(i));
			}
			
			HashMap<Integer, Set<MultiProbabilityNode>> activated = new HashMap<Integer, Set<MultiProbabilityNode>>();
			ArrayList<Boolean> spreadBools = new ArrayList<Boolean>();
			
			for (int i = 0; i < seedGroups.size(); i++) {
				activated.put(convertedCons.get(i).getId(), seedGroups.get(i));
				spreadBools.add(true);
			}
			
			//System.out.println("Size of IC seed set:" + seedsIC.size());
		//	target.seedInitialise(seedsTar);

			int t = 0;
			
			boolean spread = true;
			int falseSpread = 0;
			int[] size = new int[convertedCons.size()];
			while (t < burnIn) {
				for (int i = 0; i < spreadBools.size(); i++) {
					if (spreadBools.get(i)) {
						int id = convertedCons.get(i).getId();
						size[i] = activated.get(id).size();
						
						convertedCons.get(i).spread(smg, activated);
					}
				}
				for (int i = 0; i < spreadBools.size(); i++) {
					if (spreadBools.get(i)) {
						int id = convertedCons.get(i).getId();
						convertedCons.get(i).activateNodes(activated);
						if (activated.get(id).size() <= size[i]) {
							spreadBools.set(i, false);
							falseSpread++;
						}
						
					}
				}
				
				try {
					bw.write("Timestep " + t + "\n");
					
					for (int ii = 0; ii < convertedCons.size(); ii++) {
						if (activated.containsKey(convertedCons.get(ii).getId())) {
							bw.write("Infections SIR" + ii + ": " + activated.get(convertedCons.get(ii).getId()).size() + "\n");
						}
						else {
							bw.write("Infections SIR" + ii + ": " + 0 + "\n");
						}
					}
					//bw.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				t++;
				
			}
			
			
			if (seedSelection != 1) {
				SeedSelector.seedSelectionNew(concepts, smg, seedSize, seedGroups, seedSelection, rand, graphSize, nodes, conceptToPick, targetCon, graphType, gChar, run);
			}
			
			//secondary.seedInitialise(seedsSec);
			activated.put(convertedCons.get(conceptToPick).getId(), seedGroups.get(conceptToPick));
			
			while (falseSpread < spreadBools.size()) {
				for (int i = 0; i < spreadBools.size(); i++) {
					if (spreadBools.get(i)) {
						int id = convertedCons.get(i).getId();
						size[i] = activated.get(id).size();
						
						convertedCons.get(i).spread(smg, activated);
					}
				}
				for (int i = 0; i < spreadBools.size(); i++) {
					if (spreadBools.get(i)) {
						int id = convertedCons.get(i).getId();
						convertedCons.get(i).activateNodes(activated);
						if (activated.get(id).size() <= size[i]) {
							spreadBools.set(i, false);
							falseSpread++;
						}
						
					}
				}
				
				try {
					bw.write("Timestep " + t + "\n");
					
					for (int ii = 0; ii < convertedCons.size(); ii++) {
						if (activated.containsKey(convertedCons.get(ii).getId())) {
							bw.write("Infections SIR" + ii + ": " + activated.get(convertedCons.get(ii).getId()).size() + "\n");
						}
						else {
							bw.write("Infections SIR" + ii + ": " + 0 + "\n");
						}
					}
					//bw.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				t++;
				
			}
			
			try {
				bw.write("Final Result" + "\n");
				for (int ii = 0; ii < convertedCons.size(); ii++) {
					if (activated.containsKey(convertedCons.get(ii).getId())) {
						bw.write("Infections SIR" + ii + ": " + activated.get(convertedCons.get(ii).getId()).size() + "\n");
					}
					else {
						bw.write("Infections SIR" + ii + ": " + 0 + "\n");
					}
				}
				bw.write(System.currentTimeMillis() + "\n");
				bw.write("----------------------------\n");
				//bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			for (int ii = 0; ii < convertedCons.size(); ii++) {
				if (activated.containsKey(convertedCons.get(ii).getId())) {
					System.out.println("Final Number for SIR" + ii + ": " +  activated.get(convertedCons.get(ii).getId()).size());
				}
				else {
					System.out.println("Final Number for SIR" + ii + ": " +  0);
				}
			}
	}
	//a generic version of the spread methods above
	public static void genSpread (ArrayList<ExtendedConcept> concepts, ArrayList<HashSet<MultiProbabilityNode>> seedGroups,
			int seedSelection, Graph<MultiProbabilityNode, TypedWeightedEdge> smg, BufferedWriter bw, int burnIn,
			HashMap<Integer, MultiProbabilityNode> nodes, int seedSize, Random rand, int graphSize, int conceptToPick, int targetCon,
			String[] conceptTypes, String graphType, String gChar, int run){
			
			
			HashMap<Integer, Set<MultiProbabilityNode>> activated = new HashMap<Integer, Set<MultiProbabilityNode>>();
			ArrayList<Boolean> spreadBools = new ArrayList<Boolean>();
			
			for (int i = 0; i < seedGroups.size(); i++) {
				activated.put(concepts.get(i).getId(), seedGroups.get(i));
				spreadBools.add(true);
			}
			
			//System.out.println("Size of IC seed set:" + seedsIC.size());
		//	target.seedInitialise(seedsTar);

			int t = 0;
			
			boolean spread = true;
			int falseSpread = 0;
			
			for (int i = 0; i < seedGroups.size(); i++){
				if (i != conceptToPick) {
					if (conceptTypes[i].equals("IC")) {
						((DiverseICConcept)concepts.get(i)).seedInitialise(seedGroups.get(i));
					}
				}
			}
			
			if (burnIn == 0) {
				if (seedSelection != 1) {
					SeedSelector.seedSelectionNew(concepts, smg, seedSize, seedGroups, seedSelection, rand, graphSize, nodes, conceptToPick, targetCon, graphType, gChar, run);
				}
				
				if (conceptTypes[conceptToPick].equals("IC")){
					((DiverseICConcept)concepts.get(conceptToPick)).seedInitialise(seedGroups.get(conceptToPick));
				}
				
				//secondary.seedInitialise(seedsSec);
				activated.put(concepts.get(conceptToPick).getId(), seedGroups.get(conceptToPick));
			}
			else {
				int[] size = new int[concepts.size()];
				while (t < burnIn) {
					System.out.println("Timestep " + t);
					for (int i = 0; i < spreadBools.size(); i++) {
						if (i != conceptToPick && spreadBools.get(i)) {
					//		System.out.println(i);
							int id = concepts.get(i).getId();
							size[i] = activated.get(id).size();
							
							concepts.get(i).spread(smg, activated);
							
						}
					}
					for (int i = 0; i < spreadBools.size(); i++) {
						if (i != conceptToPick && spreadBools.get(i)) {
					//		System.out.println(i);
							int id = concepts.get(i).getId();
							
							concepts.get(i).activateNodes(activated);
							if (activated.get(id).size() <= size[i]) {
								spreadBools.set(i, false);
								falseSpread++;
							}
							
						}
					}
					
					try {
						bw.write("Timestep " + t + "\n");
						
						for (int ii = 0; ii < concepts.size(); ii++) {
							if (activated.containsKey(concepts.get(ii).getId())) {
								bw.write("Infections " + conceptTypes[ii] + ii + ": " + activated.get(concepts.get(ii).getId()).size() + "\n");
							}
							else {
								bw.write("Infections " + conceptTypes[ii] +  ii + ": " + 0 + "\n");
							}
						}
						//bw.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
					t++;
				}
				
				
				if (seedSelection != 1) {
					SeedSelector.seedSelectionNew(concepts, smg, seedSize, seedGroups, seedSelection, rand, graphSize, nodes, conceptToPick, targetCon, graphType, gChar, run);
				}
				
				if (conceptTypes[conceptToPick].equals("IC")){
					((DiverseICConcept)concepts.get(conceptToPick)).seedInitialise(seedGroups.get(conceptToPick));
				}
				
				//secondary.seedInitialise(seedsSec);
				activated.put(concepts.get(conceptToPick).getId(), seedGroups.get(conceptToPick));
			}
			
			
			
			int[] size = new int[concepts.size()];
			while (falseSpread < spreadBools.size()) {
				System.out.println(t);
				for (int i = 0; i < spreadBools.size(); i++) {
					if (i != conceptToPick && spreadBools.get(i)) {
				//		System.out.println(i);
						int id = concepts.get(i).getId();
						size[i] = activated.get(id).size();
						
						concepts.get(i).spread(smg, activated);
						
					}
				}
				for (int i = 0; i < spreadBools.size(); i++) {
					if (i != conceptToPick && spreadBools.get(i)) {
				//		System.out.println(i);
						int id = concepts.get(i).getId();
						
						concepts.get(i).activateNodes(activated);
						if (activated.get(id).size() <= size[i]) {
							spreadBools.set(i, false);
							falseSpread++;
						}
						
					}
				}
				try {
					bw.write("Timestep " + t + "\n");
					
					for (int ii = 0; ii < concepts.size(); ii++) {
						if (activated.containsKey(concepts.get(ii).getId())) {
							bw.write("Infections " + conceptTypes[ii] + ii + ": " + activated.get(concepts.get(ii).getId()).size() + "\n");
						}
						else {
							bw.write("Infections " + conceptTypes[ii] + ii + ": " + 0 + "\n");
						}
					}
					//bw.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				t++;
				
			}
			
			try {
				bw.write("Final Result" + "\n");
				for (int ii = 0; ii < concepts.size(); ii++) {
					if (activated.containsKey(concepts.get(ii).getId())) {
						bw.write("Infections " + conceptTypes[ii] + ii + ": " + activated.get(concepts.get(ii).getId()).size() + "\n");
					}
					else {
						bw.write("Infections " + conceptTypes[ii] + ii + ": " + 0 + "\n");
					}
				}
				bw.write(System.currentTimeMillis() + "\n");
				bw.write("----------------------------\n");
				//bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			for (int ii = 0; ii < concepts.size(); ii++) {
				if (activated.containsKey(concepts.get(ii).getId())) {
					System.out.println("Final Number for " + conceptTypes[ii] + ii + ": " +  activated.get(concepts.get(ii).getId()).size());
				}
				else {
					System.out.println("Final Number for " + conceptTypes[ii] + ii + ": " +  0);
				}
			}
	}
	
	//a refactoring of the generic method above, used for most tests. This allows for concepts to be introduced at any time step
	// and any order.
	public static void antSpread (ArrayList<ExtendedConcept> concepts, ArrayList<HashSet<MultiProbabilityNode>> seedGroups,
			int[] seedSelection, Graph<MultiProbabilityNode, TypedWeightedEdge> smg, BufferedWriter bw, int[] burnIn,
			HashMap<Integer, MultiProbabilityNode> nodes, int seedSize, Random rand, int graphSize, int conceptToPick, int[] targetCon,
			String[] conceptTypes, String graphType, String gChar, int run){
			
			
			HashMap<Integer, Set<MultiProbabilityNode>> activated = new HashMap<Integer, Set<MultiProbabilityNode>>();
			HashMap<Integer, Boolean> spreadBools = new HashMap<Integer, Boolean>();
			
			//System.out.println("Size of IC seed set:" + seedsIC.size());
		//	target.seedInitialise(seedsTar);

			int t = 0;
			
			boolean spread = true;
			int falseSpread = 0;
			int startCascade = 0;
			for (int i = 0; i < concepts.size(); i++) {
				spreadBools.put(concepts.get(i).getId(), false);
			}
			
			ArrayList<Concept> consToCas = new ArrayList<Concept>();
			
			for (int i = 0; i < seedGroups.size(); i++){
				if (burnIn[i] == 0) {
					SeedSelector.seedSelectionAnt(concepts, smg, seedSize, seedGroups, seedSelection[i], rand, graphSize, nodes, i, targetCon[i], graphType, gChar, run);
					
					if (i == 0) {
						for (MultiProbabilityNode n : seedGroups.get(i)){
							n.activate(concepts.get(i));
							if (conceptTypes[i].equals("IC")) {
								((DiverseICConcept)concepts.get(i)).seedInitialise(seedGroups.get(i));
							}
						}
						activated.put(concepts.get(i).getId(), seedGroups.get(i));
						spreadBools.put(concepts.get(i).getId(), true);
						startCascade++;
					}
					else {
						consToCas.add(concepts.get(i));
					}
				}
			}
			
			for (Concept c : consToCas){
				for (MultiProbabilityNode n : seedGroups.get(c.getId())){
					n.activate(c);
					if (conceptTypes[c.getId()].equals("IC")) {
						((DiverseICConcept)c).seedInitialise(seedGroups.get(c.getId()));
					}
				}
				activated.put(c.getId(), seedGroups.get(c.getId()));
				spreadBools.put(c.getId(), true);
				startCascade++;
			}
			int[] size = new int[concepts.size()];
			while (falseSpread < spreadBools.size()){
			//while (t < 10) {
				System.out.println("Timestep " + t);
				System.out.println(startCascade);
				System.out.println(falseSpread);
				for (int i = 0; i < concepts.size(); i++) {
					int id = concepts.get(i).getId();
					if (spreadBools.get(id)) {
						
						System.out.println("Conc" + i);
						
						size[i] = activated.get(id).size();
						
						concepts.get(i).spread(smg, activated);
					}
				}
				for (int i = 0; i < concepts.size(); i++) {
					int id = concepts.get(i).getId();
					if (spreadBools.get(id)) {
						concepts.get(i).activateNodes(activated);
						if (activated.get(id).size() <= size[i]) {
							spreadBools.put(id, false);
							falseSpread++;
						}
					}

				}
				
				try {
					bw.write("Timestep " + t + "\n");
					
					for (int ii = 0; ii < concepts.size(); ii++) {
						if (activated.containsKey(concepts.get(ii).getId())) {
							bw.write("Infections " + conceptTypes[ii] + ii + ": " + activated.get(concepts.get(ii).getId()).size() + "\n");
						}
						else {
							bw.write("Infections " + conceptTypes[ii] +  ii + ": " + 0 + "\n");
						}
					}
					//bw.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				t++;
				
				if (startCascade < concepts.size()) {
					consToCas = new ArrayList<Concept>();
					
					for (int i = 0; i < seedGroups.size(); i++){
						if (burnIn[i] == t) {
							SeedSelector.seedSelectionAnt(concepts, smg, seedSize, seedGroups, seedSelection[i], rand, graphSize, nodes, i, targetCon[i], graphType, gChar, run);
							
							if (i == 0) {
								for (MultiProbabilityNode n : seedGroups.get(i)){
									n.activate(concepts.get(i));
									if (conceptTypes[i].equals("IC")) {
										((DiverseICConcept)concepts.get(i)).seedInitialise(seedGroups.get(i));
									}
								}
								activated.put(concepts.get(i).getId(), seedGroups.get(i));
								spreadBools.put(concepts.get(i).getId(), true);
								startCascade++;
							}
							else {
								consToCas.add(concepts.get(i));
							}
						}
					}
					
					for (Concept c : consToCas){
						for (MultiProbabilityNode n : seedGroups.get(c.getId())){
							n.activate(c);
							if (conceptTypes[c.getId()].equals("IC")) {
								((DiverseICConcept)c).seedInitialise(seedGroups.get(c.getId()));
							}
						}
						activated.put(c.getId(), seedGroups.get(c.getId()));
						spreadBools.put(c.getId(), true);
						startCascade++;
					}
				}
			}
			
			
			try {
				bw.write("Final Result" + "\n");
				for (int ii = 0; ii < concepts.size(); ii++) {
					if (activated.containsKey(concepts.get(ii).getId())) {
						bw.write("Infections " + conceptTypes[ii] + ii + ": " + activated.get(concepts.get(ii).getId()).size() + "\n");
					}
					else {
						bw.write("Infections " + conceptTypes[ii] + ii + ": " + 0 + "\n");
					}
				}
				bw.write(System.currentTimeMillis() + "\n");
				bw.write("----------------------------\n");
				//bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			for (int ii = 0; ii < concepts.size(); ii++) {
				if (activated.containsKey(concepts.get(ii).getId())) {
					System.out.println("Final Number for " + conceptTypes[ii] + ii + ": " +  activated.get(concepts.get(ii).getId()).size());
				}
				else {
					System.out.println("Final Number for " + conceptTypes[ii] + ii + ": " +  0);
				}
			}
	}
}
