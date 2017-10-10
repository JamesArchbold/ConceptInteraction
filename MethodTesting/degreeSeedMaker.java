package MethodTesting;
//early test runner - out dated
import influence.concepts.DiverseICConcept;
import influence.concepts.DiverseLTConcept;
import influence.concepts.DiverseSIR;
import influence.concepts.DiverseSIS;
import influence.concepts.ExtendedConcept;
import influence.edges.TypedWeightedEdge;
import influence.graph.generation.GraphReaderWriter;
import influence.nodes.MultiProbabilityNode;
import influence.seed.selection.Degree;
import influence.seed.selection.DegreeDiscount;
import influence.seed.selection.SingleDiscount;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import edu.uci.ics.jung.graph.Graph;

public class degreeSeedMaker {
	public static void main(String[] args) {
		int[][] graphSizes = new int[2][4];
		graphSizes[0][0] = 1000;
		graphSizes[0][1] = 3000;
		graphSizes[0][2] = 5000;
		graphSizes[0][3] = 10000;
		graphSizes[1][0] = 25000;
		graphSizes[1][1] = 50000;
		graphSizes[1][2] = 100000;
		graphSizes[1][3] = 250000;
		
		String[] gTypes = new String[2];
		gTypes[0] = "SW";
		gTypes[1] = "SF";
		
		String[][] gChar = new String[2][2];
		gChar[0][0] = "0.25";
		gChar[0][1] = "0.75";
		gChar[1][0] = "4";
		gChar[1][1] = "8";
		
		int[][] seedSizes = new int[2][3];
		seedSizes[0][0] = 10;
		seedSizes[0][1] = 25;
		seedSizes[0][2] = 50;
		seedSizes[1][0] = 100;
		seedSizes[1][1] = 250;
		seedSizes[1][2] = 500;
		
		double ltThresh = 0.8;
		double icProb = 0.1;
		int runs = 100;
		int countEm = 0;
//		for (int i = 0; i < 2; i++){
//			for (int ii = 0; ii < 4; ii++){
//				for (int k = 0; k < 2; k++) {
//					for (int kk = 0; kk < 2; kk++) {
//						for (int ik = 0; ik < 3; ik++){
//							for (int tt = 0; tt < 100; tt++){
//								if (countEm > 9161) {
//									ArrayList<ExtendedConcept> cons = new ArrayList<ExtendedConcept>();
//									cons.add(new DiverseICConcept(0, tt));
//									Graph<MultiProbabilityNode, TypedWeightedEdge> g = graphMaker(graphSizes[i][ii], gTypes[k], gChar[k][kk], ltThresh, icProb, tt, cons, ltThresh, icProb);
//									int seedSize = seedSizes[i][ik];
//									
//									HashSet<MultiProbabilityNode> seedsDeg = Degree.getSeeds(g, seedSize);
//									HashSet<MultiProbabilityNode> seedsSin = SingleDiscount.getSeeds(g, seedSize);
//									HashSet<MultiProbabilityNode> seedsDis = DegreeDiscount.getSeedsLT(g, seedSize, cons.get(0));
//									
//									if (gTypes[k].equals("SW")) {
//										
//										File file = new File("hRes" + graphSizes[i][ii] + "N/"+ graphSizes[i][ii] + "NodeSmallWorldGraphExpo" + ((Double.parseDouble(gChar[k][kk]))*100) + "Num" 
//												+ tt + "seeds" + seedSize + "h" + 2 + ".txt");
//										
//										FileWriter fw = null;
//										BufferedWriter bw = null;
//										try {
//											file.createNewFile();
//											fw = new FileWriter(file.getAbsoluteFile());
//											bw = new BufferedWriter(fw);
//										} catch (IOException e) {
//											e.printStackTrace();
//										}
//	
//										for (MultiProbabilityNode n : seedsSin) {
//											try {
//												bw.write(n.getId() + "\n");
//											} catch (IOException e) {
//												e.printStackTrace();
//											}
//										}
//										
//										try {
//											bw.flush();
//											bw.close();
//											fw.close();
//										} catch (IOException e) {
//											e.printStackTrace();
//										}
//										
//										file = new File("hRes" + graphSizes[i][ii] + "N/"+ graphSizes[i][ii] + "NodeSmallWorldGraphExpo" + ((Double.parseDouble(gChar[k][kk]))*100) + "Num" 
//												+ tt + "seeds" + seedSize + "h" + 3 + ".txt");
//										
//										 fw = null;
//										 bw = null;
//										try {
//											file.createNewFile();
//											fw = new FileWriter(file.getAbsoluteFile());
//											bw = new BufferedWriter(fw);
//										} catch (IOException e) {
//											e.printStackTrace();
//										}
//	
//										for (MultiProbabilityNode n : seedsDeg) {
//											try {
//												bw.write(n.getId() + "\n");
//											} catch (IOException e) {
//												e.printStackTrace();
//											}
//										}
//										
//										try {
//											bw.flush();
//											bw.close();
//											fw.close();
//										} catch (IOException e) {
//											e.printStackTrace();
//										}
//										
//										file = new File("hRes" + graphSizes[i][ii] + "N/"+ graphSizes[i][ii] + "NodeSmallWorldGraphExpo" + ((Double.parseDouble(gChar[k][kk]))*100) + "Num" 
//												+ tt + "seeds" + seedSize + "h" + 9 + ".txt");
//										
//										 fw = null;
//										 bw = null;
//										try {
//											file.createNewFile();
//											fw = new FileWriter(file.getAbsoluteFile());
//											bw = new BufferedWriter(fw);
//										} catch (IOException e) {
//											e.printStackTrace();
//										}
//	
//										for (MultiProbabilityNode n : seedsDis) {
//											try {
//												bw.write(n.getId() + "\n");
//											} catch (IOException e) {
//												e.printStackTrace();
//											}
//										}
//										
//										try {
//											bw.flush();
//											bw.close();
//											fw.close();
//										} catch (IOException e) {
//											e.printStackTrace();
//										}
//										
//									}
//									else if (gTypes[k].equals("SF")) {
//										File file = new File("hRes" + graphSizes[i][ii] + "N/" + 10 + "Start" + gChar[k][kk] + "EdgesAdded" + graphSizes[i][ii] + 
//												"NodeScaleFreeGraph" + "Num" + tt + "seeds" + seedSize + "h" + 2 + ".txt");
//										
//										FileWriter fw = null;
//										BufferedWriter bw = null;
//										try {
//											file.createNewFile();
//											fw = new FileWriter(file.getAbsoluteFile());
//											bw = new BufferedWriter(fw);
//										} catch (IOException e) {
//											e.printStackTrace();
//										}
//	
//										for (MultiProbabilityNode n : seedsSin) {
//											try {
//												bw.write(n.getId() + "\n");
//											} catch (IOException e) {
//												e.printStackTrace();
//											}
//										}
//										
//										try {
//											bw.flush();
//											bw.close();
//											fw.close();
//										} catch (IOException e) {
//											e.printStackTrace();
//										}
//										
//										file = new File("hRes" + graphSizes[i][ii] + "N/" + 10 + "Start" + gChar[k][kk] + "EdgesAdded" + graphSizes[i][ii] + 
//												"NodeScaleFreeGraph" + "Num" + tt + "seeds" + seedSize + "h" + 3 + ".txt");
//										
//										 fw = null;
//										 bw = null;
//										try {
//											file.createNewFile();
//											fw = new FileWriter(file.getAbsoluteFile());
//											bw = new BufferedWriter(fw);
//										} catch (IOException e) {
//											e.printStackTrace();
//										}
//	
//										for (MultiProbabilityNode n : seedsDeg) {
//											try {
//												bw.write(n.getId() + "\n");
//											} catch (IOException e) {
//												e.printStackTrace();
//											}
//										}
//										
//										try {
//											bw.flush();
//											bw.close();
//											fw.close();
//										} catch (IOException e) {
//											e.printStackTrace();
//										}
//										
//										file = new File("hRes" + graphSizes[i][ii] + "N/" + 10 + "Start" + gChar[k][kk] + "EdgesAdded" + graphSizes[i][ii] + 
//												"NodeScaleFreeGraph" + "Num" + tt + "seeds" + seedSize + "h" + 9 + ".txt");
//										
//										 fw = null;
//										 bw = null;
//										try {
//											file.createNewFile();
//											fw = new FileWriter(file.getAbsoluteFile());
//											bw = new BufferedWriter(fw);
//										} catch (IOException e) {
//											e.printStackTrace();
//										}
//	
//										for (MultiProbabilityNode n : seedsDis) {
//											try {
//												bw.write(n.getId() + "\n");
//											} catch (IOException e) {
//												e.printStackTrace();
//											}
//										}
//										
//										try {
//											bw.flush();
//											bw.close();
//											fw.close();
//										} catch (IOException e) {
//											e.printStackTrace();
//										}
//									}
//									countEm++;
//									System.out.println("Completion is at: " + countEm + "/9600");
//								}
//								else {countEm++;}
//							}
//						}
//					}
//				}
//			}
//		}

		String[] stanTypes = new String[3];
		stanTypes[0] = "CM";
		stanTypes[1] = "EP";
		stanTypes[2] = "DB";

		int[] seedSizeStan = new int[3];
		seedSizeStan[0] = 100;
		seedSizeStan[1] = 250;
		seedSizeStan[2] = 500;
		countEm = 0;
		for (int qq = 0; qq < 3; qq++) {
			for (int ss = 0; ss < 3; ss++) {
				ArrayList<ExtendedConcept> cons = new ArrayList<ExtendedConcept>();
				cons.add(new DiverseICConcept(0, 0));
				Graph<MultiProbabilityNode, TypedWeightedEdge> g = graphMaker(0, stanTypes[qq], "", ltThresh, icProb, 0, cons, ltThresh, icProb);
				int seedSize = seedSizeStan[ss];
				
				
				HashSet<MultiProbabilityNode> seedsDeg = Degree.getSeeds(g, seedSize);
				HashSet<MultiProbabilityNode> seedsSin = SingleDiscount.getSeeds(g, seedSize);
				HashSet<MultiProbabilityNode> seedsDis = DegreeDiscount.getSeedsLT(g, seedSize, cons.get(0));
				
				File file = new File("hResStan/"+ stanTypes[qq] + "seeds" + seedSize + "h" + 2 + ".txt");
				
				FileWriter fw = null;
				BufferedWriter bw = null;
				try {
					file.createNewFile();
					fw = new FileWriter(file.getAbsoluteFile());
					bw = new BufferedWriter(fw);
				} catch (IOException e) {
					e.printStackTrace();
				}

				for (MultiProbabilityNode n : seedsSin) {
					try {
						bw.write(n.getId() + "\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				try {
					bw.flush();
					bw.close();
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				file = new File("hResStan/"+ stanTypes[qq] + "seeds" + seedSize + "h" + 3 + ".txt");
				
				 fw = null;
				 bw = null;
				try {
					file.createNewFile();
					fw = new FileWriter(file.getAbsoluteFile());
					bw = new BufferedWriter(fw);
				} catch (IOException e) {
					e.printStackTrace();
				}

				for (MultiProbabilityNode n : seedsDeg) {
					try {
						bw.write(n.getId() + "\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				try {
					bw.flush();
					bw.close();
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				file = new File("hResStan/"+ stanTypes[qq] + "seeds" + seedSize + "h" + 9 + ".txt");
				 fw = null;
				 bw = null;
				try {
					file.createNewFile();
					fw = new FileWriter(file.getAbsoluteFile());
					bw = new BufferedWriter(fw);
				} catch (IOException e) {
					e.printStackTrace();
				}

				for (MultiProbabilityNode n : seedsDis) {
					try {
						bw.write(n.getId() + "\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				try {
					bw.flush();
					bw.close();
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				countEm++;
				System.out.println("Completion is at: " + countEm + "/9");
			}
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
			return grw.graphReadInStandford("stanford", "hepph.txt", concepts, ICProb, false, LTThresh,runs);
		}
		
		return null;
	}
	

}
