package influence.concepts.cascades;

import influence.concepts.Concept;
import influence.concepts.DiverseICConcept;
import influence.concepts.DiverseLTConcept;
import influence.concepts.DiverseSIR;
import influence.concepts.DiverseSIS;
import influence.edges.TypedWeightedEdge;
import influence.nodes.MultiProbabilityNode;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;
//cascades for only 2 concepts - outdated
public class DiverseInteractionCascades {

	public static void LTBoostLT(ArrayList<? extends Concept> concepts, HashSet<MultiProbabilityNode> seedsTar, HashSet<MultiProbabilityNode> seedsSec,
			int seedSelection, Graph<MultiProbabilityNode, TypedWeightedEdge> smg, BufferedWriter bw) {
		
		DiverseLTConcept target = (DiverseLTConcept) concepts.get(0);
		DiverseLTConcept secondary = (DiverseLTConcept) concepts.get(1);
		
		HashMap<Integer, Set<MultiProbabilityNode>> activated = new HashMap<Integer, Set<MultiProbabilityNode>>();
		activated.put(target.getId(), seedsTar);
		activated.put(secondary.getId(), seedsSec);
		boolean spread = true;
		boolean spreadTar = true;
		boolean spreadSec = true;
		int sizeTar = activated.get(target.getId()).size();
		int sizeSec = activated.get(secondary.getId()).size();
		int t = 0;
		
		if (seedSelection == 10 || seedSelection == 11 || seedSelection == 12|| seedSelection == 18
				|| seedSelection == 19 || seedSelection == 20) {spreadSec = false;}
		
		while (spread) {
			sizeTar = activated.get(target.getId()).size();
			sizeSec = activated.get(secondary.getId()).size();
			
			if (spreadTar) {
				target.spread(smg, activated);
			}
			
			if (spreadSec) {
				secondary.spread(smg, activated);
			}
			
			if (spreadTar) {
				target.activateNodes(activated);
			}

			if (spreadSec){
				secondary.activateNodes(activated);
			}
			
			if (activated.get(target.getId()).size() <= sizeTar) {spreadTar = false;}
			if (activated.get(secondary.getId()).size() <= sizeSec) {spreadSec = false;}
			
			if (!spreadTar && !spreadSec) {
				spread = false;
			}
			
			try {
				bw.write("Timestep " + t + "\n");
				bw.write("Infections LT: " + activated.get(target.getId()).size() + "\n");
				bw.write("Infections LT2: " + activated.get(secondary.getId()).size() + "\n");
				//bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			t++;
		}
		
		try {
			bw.write("Final Result" + "\n");
			bw.write("Infections LT: " + activated.get(target.getId()).size() + "\n");
			bw.write("Infections LT2: " + activated.get(secondary.getId()).size() + "\n");
			bw.write(System.currentTimeMillis() + "\n");
			bw.write("----------------------------\n");
			//bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Final Number: " +  activated.get(target.getId()).size());
		System.out.println("Final Number: " +  activated.get(secondary.getId()).size());
		
	}

	public static void ICBoostLT(ArrayList<? extends Concept> concepts, HashSet<MultiProbabilityNode> seedsTar, HashSet<MultiProbabilityNode> seedsSec,
			int seedSelection, Graph<MultiProbabilityNode, TypedWeightedEdge> smg, BufferedWriter bw) {
		
		DiverseLTConcept target = (DiverseLTConcept) concepts.get(0);
		DiverseICConcept secondary = (DiverseICConcept) concepts.get(1);
		
		HashMap<Integer, Set<MultiProbabilityNode>> activated = new HashMap<Integer, Set<MultiProbabilityNode>>();
		activated.put(target.getId(), seedsTar);
		activated.put(secondary.getId(), seedsSec);
		//System.out.println("Size of IC seed set:" + seedsIC.size());
		//target.seedInitialise(seedsTar);
		secondary.seedInitialise(seedsSec);
		boolean spread = true;
		boolean spreadTar = true;
		boolean spreadSec = true;
		int sizeTar = activated.get(target.getId()).size();
		int sizeSec = activated.get(secondary.getId()).size();
		int t = 0;
		
		if (seedSelection == 10 || seedSelection == 11 || seedSelection == 12 || seedSelection == 18
				|| seedSelection == 19 || seedSelection == 20) {spreadSec = false;}
		
		while (spread) {
			sizeTar = activated.get(target.getId()).size();
			sizeSec = activated.get(secondary.getId()).size();
			
			if (spreadTar) {
				target.spread(smg, activated);
			}
			
			if (spreadSec) {
				secondary.spread(smg, activated);
			}
			
			if (spreadTar) {
				target.activateNodes(activated);
			}

			if (spreadSec){
				secondary.activateNodes(activated);
			}
			
			if (activated.get(target.getId()).size() <= sizeTar) {spreadTar = false;}
			if (activated.get(secondary.getId()).size() <= sizeSec) {spreadSec = false;}
			
			if (!spreadTar && !spreadSec) {
				spread = false;
			}
			
			try {
				bw.write("Timestep " + t + "\n");
				bw.write("Infections LT: " + activated.get(target.getId()).size() + "\n");
				bw.write("Infections IC: " + activated.get(secondary.getId()).size() + "\n");
				//bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			t++;
		}
		
		try {
			bw.write("Final Result" + "\n");
			bw.write("Infections LT: " + activated.get(target.getId()).size() + "\n");
			bw.write("Infections IC: " + activated.get(secondary.getId()).size() + "\n");
			bw.write(System.currentTimeMillis() + "\n");
			bw.write("----------------------------\n");
			//bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Final Number: " +  activated.get(target.getId()).size());
		System.out.println("Final Number: " +  activated.get(secondary.getId()).size());
		
	}

	public static void LTBoostIC(ArrayList<? extends Concept> concepts, HashSet<MultiProbabilityNode> seedsTar, HashSet<MultiProbabilityNode> seedsSec,
			int seedSelection, Graph<MultiProbabilityNode, TypedWeightedEdge> smg, BufferedWriter bw) {
		
		DiverseICConcept target = (DiverseICConcept) concepts.get(0);
		DiverseLTConcept secondary = (DiverseLTConcept) concepts.get(1);
		
		HashMap<Integer, Set<MultiProbabilityNode>> activated = new HashMap<Integer, Set<MultiProbabilityNode>>();
		activated.put(target.getId(), seedsTar);
		activated.put(secondary.getId(), seedsSec);
		//System.out.println("Size of IC seed set:" + seedsIC.size());
		target.seedInitialise(seedsTar);
	//	secondary.seedInitialise(seedsSec);
		boolean spread = true;
		boolean spreadTar = true;
		boolean spreadSec = true;
		int sizeTar = activated.get(target.getId()).size();
		int sizeSec = activated.get(secondary.getId()).size();
		int t = 0;
		
		if (seedSelection == 10 || seedSelection == 11 || seedSelection == 12 || seedSelection == 18
				|| seedSelection == 19 || seedSelection == 20) {spreadSec = false;}
		
		while (spread) {
			sizeTar = activated.get(target.getId()).size();
			sizeSec = activated.get(secondary.getId()).size();
			
			if (spreadTar) {
				target.spread(smg, activated);
			}
			
			if (spreadSec) {
				secondary.spread(smg, activated);
			}
			
			if (spreadTar) {
				target.activateNodes(activated);
			}

			if (spreadSec){
				secondary.activateNodes(activated);
			}
			
			if (activated.get(target.getId()).size() <= sizeTar) {spreadTar = false;}
			if (activated.get(secondary.getId()).size() <= sizeSec) {spreadSec = false;}
			
			if (!spreadTar && !spreadSec) {
				spread = false;
			}
			
			try {
				bw.write("Timestep " + t + "\n");
				bw.write("Infections IC: " + activated.get(target.getId()).size() + "\n");
				bw.write("Infections LT: " + activated.get(secondary.getId()).size() + "\n");
				//bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			t++;
		}
		
		try {
			bw.write("Final Result" + "\n");
			bw.write("Infections IC: " + activated.get(target.getId()).size() + "\n");
			bw.write("Infections LT: " + activated.get(secondary.getId()).size() + "\n");
			bw.write(System.currentTimeMillis() + "\n");
			bw.write("----------------------------\n");
			//bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Final Number: " +  activated.get(target.getId()).size());
		System.out.println("Final Number: " +  activated.get(secondary.getId()).size());
		
	}

	public static void ICBoostIC(ArrayList<? extends Concept> concepts, HashSet<MultiProbabilityNode> seedsTar, HashSet<MultiProbabilityNode> seedsSec,
		int seedSelection, Graph<MultiProbabilityNode, TypedWeightedEdge> smg, BufferedWriter bw ){
		DiverseICConcept target = (DiverseICConcept) concepts.get(0);
		DiverseICConcept secondary = (DiverseICConcept) concepts.get(1);
		
		HashMap<Integer, Set<MultiProbabilityNode>> activated = new HashMap<Integer, Set<MultiProbabilityNode>>();
		activated.put(target.getId(), seedsTar);
		activated.put(secondary.getId(), seedsSec);
		System.out.println("Size of IC seed set:" + seedsTar.size());
		target.seedInitialise(seedsTar);
		secondary.seedInitialise(seedsSec);
		boolean spread = true;
		boolean spreadTar = true;
		boolean spreadSec = true;
		int sizeTar = activated.get(target.getId()).size();
		int sizeSec = activated.get(secondary.getId()).size();
		int t = 0;
		
		if (seedSelection == 10 || seedSelection == 11 || seedSelection == 12|| seedSelection == 21 || seedSelection == 18
				|| seedSelection == 19 || seedSelection == 20) {spreadSec = false;}
		
		while (spread) {
			sizeTar = activated.get(target.getId()).size();
			sizeSec = activated.get(secondary.getId()).size();
			
			if (spreadTar) {
				target.spread(smg, activated);
			}
			
			if (spreadSec) {
				secondary.spread(smg, activated);
			}
			
			if (spreadTar) {
				target.activateNodes(activated);
			}

			if (spreadSec){
				secondary.activateNodes(activated);
			}
			
			if (activated.get(target.getId()).size() <= sizeTar) {spreadTar = false;}
			if (activated.get(secondary.getId()).size() <= sizeSec) {spreadSec = false;}
			
			if (!spreadTar && !spreadSec) {
				spread = false;
			}
			System.out.println("Timestep " + t);
			try {
				bw.write("Timestep " + t + "\n");
				bw.write("Infections IC: " + activated.get(target.getId()).size() + "\n");
				bw.write("Infections IC2: " + activated.get(secondary.getId()).size() + "\n");
				//bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			t++;
		}
		
		try {
			bw.write("Final Result" + "\n");
			bw.write("Infections IC: " + activated.get(target.getId()).size() + "\n");
			bw.write("Infections IC2: " + activated.get(secondary.getId()).size() + "\n");
			//bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Final Number: " +  activated.get(target.getId()).size());
		System.out.println("Final Number: " +  activated.get(secondary.getId()).size());
	}
	
	public static void SISBoostSIS(ArrayList<? extends Concept> concepts, HashSet<MultiProbabilityNode> seedsTar, HashSet<MultiProbabilityNode> seedsSec,
			int seedSelection, Graph<MultiProbabilityNode, TypedWeightedEdge> smg, BufferedWriter bw) {
		
		DiverseSIS target = (DiverseSIS) concepts.get(0);
		DiverseSIS secondary = (DiverseSIS) concepts.get(1);
		
		HashMap<Integer, Set<MultiProbabilityNode>> activated = new HashMap<Integer, Set<MultiProbabilityNode>>();
		activated.put(target.getId(), seedsTar);
		activated.put(secondary.getId(), seedsSec);
		System.out.println("Size of SIS seed set:" + seedsTar.size());

		boolean spread = true;
		boolean spreadTar = true;
		boolean spreadSec = true;
		int sizeTar = activated.get(target.getId()).size();
		int sizeSec = activated.get(secondary.getId()).size();
		int t = 0;
		
		if (seedSelection == 10 || seedSelection == 11 || seedSelection == 12|| seedSelection == 21 || seedSelection == 18
				|| seedSelection == 19 || seedSelection == 20) {spreadSec = false;}
		
		while (spread) {
			sizeTar = activated.get(target.getId()).size();
			sizeSec = activated.get(secondary.getId()).size();
			
			if (spreadTar) {
				target.spread(smg, activated);
			}
			
			if (spreadSec) {
				secondary.spread(smg, activated);
			}
			
			if (spreadTar) {
				target.activateNodes(activated);
			}

			if (spreadSec){
				secondary.activateNodes(activated);
			}
			
			if (activated.get(target.getId()).size() <= sizeTar) {spreadTar = false;}
			if (activated.get(secondary.getId()).size() <= sizeSec) {spreadSec = false;}
			
			if (!spreadTar && !spreadSec) {
				spread = false;
			}
			System.out.println("Timestep " + t);
			try {
				bw.write("Timestep " + t + "\n");
				bw.write("Infections SIS: " + activated.get(target.getId()).size() + "\n");
				bw.write("Infections SIS2: " + activated.get(secondary.getId()).size() + "\n");
				//bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			t++;
		}
		
		try {
			bw.write("Final Result" + "\n");
			bw.write("Infections SIS: " + activated.get(target.getId()).size() + "\n");
			bw.write("Infections SIS2: " + activated.get(secondary.getId()).size() + "\n");
			//bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Final Number: " +  activated.get(target.getId()).size());
		System.out.println("Final Number: " +  activated.get(secondary.getId()).size());
	}
	
	public static void SIRBoostSIR(ArrayList<? extends Concept> concepts, HashSet<MultiProbabilityNode> seedsTar, HashSet<MultiProbabilityNode> seedsSec,
			int seedSelection, Graph<MultiProbabilityNode, TypedWeightedEdge> smg, BufferedWriter bw) {
		
		DiverseSIR target = (DiverseSIR) concepts.get(0);
		DiverseSIR secondary = (DiverseSIR) concepts.get(1);
		
		HashMap<Integer, Set<MultiProbabilityNode>> activated = new HashMap<Integer, Set<MultiProbabilityNode>>();
		activated.put(target.getId(), seedsTar);
		activated.put(secondary.getId(), seedsSec);
		System.out.println("Size of SIR seed set:" + seedsTar.size());

		boolean spread = true;
		boolean spreadTar = true;
		boolean spreadSec = true;
		int sizeTar = activated.get(target.getId()).size();
		int sizeSec = activated.get(secondary.getId()).size();
		int t = 0;
		
		if (seedSelection == 10 || seedSelection == 11 || seedSelection == 12|| seedSelection == 21 || seedSelection == 18
				|| seedSelection == 19 || seedSelection == 20) {spreadSec = false;}
		
		while (spread) {
			sizeTar = activated.get(target.getId()).size();
			sizeSec = activated.get(secondary.getId()).size();
			
			if (spreadTar) {
				target.spread(smg, activated);
			}
			
			if (spreadSec) {
				secondary.spread(smg, activated);
			}
			
			if (spreadTar) {
				target.activateNodes(activated);
			}

			if (spreadSec){
				secondary.activateNodes(activated);
			}
			
			if (activated.get(target.getId()).size() <= sizeTar) {spreadTar = false;}
			if (activated.get(secondary.getId()).size() <= sizeSec) {spreadSec = false;}
			
			if (!spreadTar && !spreadSec) {
				spread = false;
			}
			System.out.println("Timestep " + t);
			try {
				bw.write("Timestep " + t + "\n");
				bw.write("Infections SIR: " + activated.get(target.getId()).size() + "\n");
				bw.write("Infections SIR2: " + activated.get(secondary.getId()).size() + "\n");
				//bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			t++;
		}
		
		try {
			bw.write("Final Result" + "\n");
			bw.write("Infections SIR: " + activated.get(target.getId()).size() + "\n");
			bw.write("Infections SIS2: " + activated.get(secondary.getId()).size() + "\n");
			//bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Final Number: " +  activated.get(target.getId()).size());
		System.out.println("Final Number: " +  activated.get(secondary.getId()).size());
	}
	
}
