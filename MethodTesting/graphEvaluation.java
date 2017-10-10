package MethodTesting;
//early test runner - out dated
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.map.hash.TIntIntHashMap;
import influence.concepts.Concept;
import influence.concepts.DiverseICConcept;
import influence.edges.TypedWeightedEdge;
import influence.graph.generation.GraphReaderWriter;
import influence.nodes.MultiProbabilityNode;
import edu.uci.ics.jung.algorithms.scoring.EigenvectorCentrality;
import edu.uci.ics.jung.graph.Graph;

public class graphEvaluation {

	public static void main(String[] args) {
		oldEval();
	}
	
	private static void newEval(){
		
		Graph<MultiProbabilityNode, TypedWeightedEdge> smg;
		GraphReaderWriter grw = new GraphReaderWriter();
		
		TDoubleArrayList density = new TDoubleArrayList();
		TDoubleArrayList avgDegree = new TDoubleArrayList();
		TDoubleArrayList avgGlobalClust = new TDoubleArrayList();
		TDoubleArrayList avgLocalClust = new TDoubleArrayList();
		TDoubleArrayList avgEigenCent = new TDoubleArrayList();
		
		ArrayList<TIntIntHashMap> distribution = new ArrayList<TIntIntHashMap>();
		
		int[] sizes = new int[7];
		double[] expo = new double[7];
		
		expo[0] = 0.25;
		expo[1] = 0.75;
		sizes[0] = 1000;
		sizes[1] = 5000;
		sizes[2] = 10000;
		sizes[3] = 25000;
		sizes[4] = 50000;
		
		for (int k = 0; k < 2; k++) {
			for (int j = 0; j < 5; j++){
				density.clear();
				avgDegree.clear();
				avgGlobalClust.clear();
				avgLocalClust.clear();
				avgEigenCent.clear();
				distribution.clear();
				for (int i = 0; i < 100; i++) {
					System.out.println(i + "-" + sizes[j]);
					Concept ic = new DiverseICConcept(0, i);
					ArrayList<Concept> cList = new ArrayList<Concept>();
					cList.add(ic);
					
					smg = grw.graphReadInSW(sizes[j], expo[k], i, cList, false, 0.1, 0.1);
					
					ArrayList<MultiProbabilityNode> none = new ArrayList<MultiProbabilityNode>();
					for (MultiProbabilityNode m : smg.getVertices()) {
						if (smg.getIncidentEdges(m).size() == 0) {
							none.add(m);
						}
					}
					for (MultiProbabilityNode m : none) {
						smg.removeVertex(m);
					}
					
					EigenvectorCentrality<MultiProbabilityNode, TypedWeightedEdge> eigen = new EigenvectorCentrality<MultiProbabilityNode, TypedWeightedEdge>(smg);
					eigen.evaluate();
					
					for (MultiProbabilityNode m : none) {
						smg.addVertex(m);
					}
					
					double dense = (smg.getEdgeCount()) / (smg.getVertexCount() * (((double)smg.getVertexCount() - 1)/2.0));
					density.add(dense);
					
					TDoubleArrayList degreeCount = new TDoubleArrayList();
					TIntIntHashMap degreeDistribution = new TIntIntHashMap();
					TDoubleArrayList localClustering = new TDoubleArrayList();
					TDoubleArrayList eigenCounts = new TDoubleArrayList();
					int totalTrips = 0;
					int totalOpen = 0;
					ArrayList<MultiProbabilityNode> duds = new ArrayList<MultiProbabilityNode>();
					for (MultiProbabilityNode m : smg.getVertices()){
						int count = smg.getIncidentEdges(m).size();
						degreeCount.add(count);
						
						if (none.contains(m)){
							eigenCounts.add(0);
						}
						else {
							eigenCounts.add(eigen.getVertexScore(m));
						}
						
						if (degreeDistribution.containsKey(count)) {
							degreeDistribution.put(count, degreeDistribution.get(count) + 1);
						}
						else {
							degreeDistribution.put(count, 1);
						}
						
						int closedTrips = 0;
						int openTrips = 0;
						Collection<MultiProbabilityNode> neighs = smg.getNeighbors(m);
						
						if (neighs.size() == 0 || neighs.size() == 1) {
							duds.add(m);
						}
						else {
							for (MultiProbabilityNode n : neighs){
								for (MultiProbabilityNode nn : neighs) {
									if (smg.isNeighbor(n, nn) & !n.equals(nn)) {
										closedTrips = closedTrips + 1;
										openTrips = openTrips+1;
									}
									else if (!n.equals(nn)){
										openTrips = openTrips+1;
									}
								}
							}
						
						
							closedTrips = closedTrips/2;
							totalTrips = totalTrips + closedTrips;
							totalOpen = totalOpen + openTrips/2;
							localClustering.add(closedTrips / (0.5 * openTrips));
						}
						
					}
				
					for (MultiProbabilityNode m : duds) {
						smg.removeVertex(m);
					}
					
					//eigen.evaluate();
					avgDegree.add(degreeCount.sum()/degreeCount.size());
					avgLocalClust.add(localClustering.sum()/localClustering.size());
					avgEigenCent.add(eigenCounts.sum()/eigenCounts.size());
					avgGlobalClust.add(totalTrips/(double)totalOpen);
					distribution.add(degreeDistribution);
				}
				
				String filename = sizes[j] + "SmallWorldExpo" + expo[k] + ".txt"; 
				File file = new File(filename);
				FileWriter fw;
				BufferedWriter bw = null;
				
				TIntDoubleHashMap avgDist = new TIntDoubleHashMap();
				TIntDoubleHashMap stdDist = new TIntDoubleHashMap();
				for (TIntIntHashMap curr : distribution){
					for (int key : curr.keys()){
						if (avgDist.containsKey(key)) {
							avgDist.put(key, avgDist.get(key) + curr.get(key));
						}
						else{
							avgDist.put(key, curr.get(key));
						}
					}
				}
				
				for (int key : avgDist.keys()) {
					avgDist.put(key, avgDist.get(key) / distribution.size());
				}
				
				for (TIntIntHashMap curr : distribution){
					for (int key : curr.keys()){
						if (avgDist.containsKey(key)) {
							stdDist.put(key, stdDist.get(key) + ((curr.get(key) - avgDist.get(key)) * (curr.get(key) - avgDist.get(key))));
						}
						else{
							avgDist.put(key, (curr.get(key) - avgDist.get(key)) * (curr.get(key) - avgDist.get(key)));
						}
					}
				}
				
				for (int key : stdDist.keys()) {
					stdDist.put(key, Math.sqrt(stdDist.get(key) /distribution.size()));
				}
				
				
				try {
					file.createNewFile();
					fw = new FileWriter(file.getAbsoluteFile());
					bw = new BufferedWriter(fw);
					bw.write("Size of graph: " + sizes[j] + "\n");
					bw.write("Type: Small World \n");
					bw.write("Expo: " + expo[k] + " \n");
					
					bw.write("Average Degree: " + (avgDegree.sum()/avgDegree.size()) + "\n");
					double standDev = 0;
					double mean = avgDegree.sum()/avgDegree.size();
					for (int qq = 0; qq < avgDegree.size(); qq++){
						standDev = standDev +  ((avgDegree.get(qq) - mean) * (avgDegree.get(qq) - mean));
					} 
					standDev = standDev / avgDegree.size();
					standDev = Math.sqrt(standDev);
					bw.write("Standard Deviation Degree: " + (standDev) + "\n");
					
					bw.write("Average Density: " + (density.sum()/density.size()) + "\n");
					standDev = 0;
					mean = density.sum()/density.size();
					for (int qq = 0; qq < density.size(); qq++){
						standDev = standDev +  ((density.get(qq) - mean) * (density.get(qq) - mean));
					} 
					standDev = standDev / density.size();
					standDev = Math.sqrt(standDev);
					bw.write("Standard Deviation Density: " + (standDev) + "\n");
					
					bw.write("Average Local Clustering Coefficient: "+ (avgLocalClust.sum()/avgLocalClust.size())+"\n");
					standDev = 0;
					mean = avgLocalClust.sum()/avgLocalClust.size();
					for (int qq = 0; qq < avgLocalClust.size(); qq++){
						standDev = standDev +  ((avgLocalClust.get(qq) - mean) * (avgLocalClust.get(qq) - mean));
					} 
					standDev = standDev / avgLocalClust.size();
					standDev = Math.sqrt(standDev);
					bw.write("Standard Deviation Local Clustering Coefficient: " + (standDev) + "\n");
					
					bw.write("Average Global Clustering Coefficient: "+ (avgGlobalClust.sum()/avgGlobalClust.size())+"\n");
					standDev = 0;
					mean = avgGlobalClust.sum()/avgGlobalClust.size();
					for (int qq = 0; qq < avgGlobalClust.size(); qq++){
						standDev = standDev +  ((avgGlobalClust.get(qq) - mean) * (avgGlobalClust.get(qq) - mean));
					} 
					standDev = standDev / avgGlobalClust.size();
					standDev = Math.sqrt(standDev);
					bw.write("Standard Deviation Global Clustering Coefficient: " + (standDev) + "\n");
					
					bw.write("Average Eigenvector Centraility: "+ (avgEigenCent.sum()/avgEigenCent.size())+"\n");
					standDev = 0;
					mean = avgEigenCent.sum()/avgEigenCent.size();
					for (int qq = 0; qq < avgEigenCent.size(); qq++){
						standDev = standDev +  ((avgEigenCent.get(qq) - mean) * (avgEigenCent.get(qq) - mean));
					} 
					standDev = standDev / avgEigenCent.size();
					standDev = Math.sqrt(standDev);
					bw.write("Standard Deviation Eigenvector Centraility: " + (standDev) + "\n");
					
					bw.write("Average Degree Distribution: \n");
					
					for (int key: avgDist.keys()) {
						bw.write("Degree " + key + " : " + avgDist.get(key) + "\n");
					}
					
					bw.write("----------------------------" + "\n");
					bw.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
				
	}
	
	private static void oldEval() {
		Graph<MultiProbabilityNode, TypedWeightedEdge> smg;
		GraphReaderWriter grw = new GraphReaderWriter();
		
		TDoubleArrayList density = new TDoubleArrayList();
		TDoubleArrayList avgDegree = new TDoubleArrayList();
		TDoubleArrayList avgGlobalClust = new TDoubleArrayList();
		TDoubleArrayList avgLocalClust = new TDoubleArrayList();
		TDoubleArrayList avgEigenCent = new TDoubleArrayList();
		TDoubleArrayList avgEdgeCount = new TDoubleArrayList();
		
		ArrayList<TIntIntHashMap> distribution = new ArrayList<TIntIntHashMap>();
		
		int[] sizes = new int[7];
		sizes[0] = 1000;
		sizes[1] = 5000;
		sizes[2] = 10000;
		sizes[3] = 25000;
		sizes[4] = 50000;
		sizes[5] = 100000;
		
		int[] edges = new int[3];
		edges[0] = 4;
		edges[1] = 8;
		
		for (int k = 0; k < 2; k++) {
			for (int j = 0; j < 6; j++){
				density.clear();
				avgDegree.clear();
				avgGlobalClust.clear();
				avgLocalClust.clear();
				avgEigenCent.clear();
				distribution.clear();
				avgEdgeCount.clear();
				for (int i = 0; i < 100; i++) {
					System.out.println(i + "-" + sizes[j] + "-" + edges[k]);
					Concept ic = new DiverseICConcept(0, i);
					ArrayList<Concept> cList = new ArrayList<Concept>();
					cList.add(ic);

					smg = grw.graphReadInSF(10, edges[k], sizes[j], i, cList, false, 0.8, 0.1);
					
					ArrayList<MultiProbabilityNode> none = new ArrayList<MultiProbabilityNode>();
					for (MultiProbabilityNode m : smg.getVertices()) {
						if (smg.getIncidentEdges(m).size() == 0) {
							none.add(m);
						}
					}
					for (MultiProbabilityNode m : none) {
						smg.removeVertex(m);
					}
					
					EigenvectorCentrality<MultiProbabilityNode, TypedWeightedEdge> eigen = new EigenvectorCentrality<MultiProbabilityNode, TypedWeightedEdge>(smg);
					eigen.evaluate();
					
					for (MultiProbabilityNode m : none) {
						smg.addVertex(m);
					}
					
					double dense = (smg.getEdgeCount()) / (smg.getVertexCount() * (((double)smg.getVertexCount() - 1)/2.0));
					density.add(dense);
					
					TDoubleArrayList degreeCount = new TDoubleArrayList();
					TIntIntHashMap degreeDistribution = new TIntIntHashMap();
					TDoubleArrayList localClustering = new TDoubleArrayList();
					TDoubleArrayList eigenCounts = new TDoubleArrayList();
					int totalTrips = 0;
					int totalOpen = 0;
					ArrayList<MultiProbabilityNode> duds = new ArrayList<MultiProbabilityNode>();
					for (MultiProbabilityNode m : smg.getVertices()){
						int count = smg.getIncidentEdges(m).size();
						degreeCount.add(count);
						
						if (none.contains(m)){
							eigenCounts.add(0);
						}
						else {
							eigenCounts.add(eigen.getVertexScore(m));
						}
						
						if (degreeDistribution.containsKey(count)) {
							degreeDistribution.put(count, degreeDistribution.get(count) + 1);
						}
						else {
							degreeDistribution.put(count, 1);
						}
						
						int closedTrips = 0;
						int openTrips = 0;
						Collection<MultiProbabilityNode> neighs = smg.getNeighbors(m);
						
						if (neighs.size() == 0 || neighs.size() == 1) {
							duds.add(m);
						}
						else {
							for (MultiProbabilityNode n : neighs){
								for (MultiProbabilityNode nn : neighs) {
									if (smg.isNeighbor(n, nn) & !n.equals(nn)) {
										closedTrips = closedTrips + 1;
										openTrips = openTrips+1;
									}
									else if (!n.equals(nn)){
										openTrips = openTrips+1;
									}
								}
							}
						
						
							closedTrips = closedTrips/2;
							totalTrips = totalTrips + closedTrips;
							totalOpen = totalOpen + openTrips/2;
							localClustering.add(closedTrips / (0.5 * openTrips));
						}
						
					}
				
					for (MultiProbabilityNode m : duds) {
						smg.removeVertex(m);
					}
					
					//eigen.evaluate();
					avgEdgeCount.add(smg.getEdgeCount());
					avgDegree.add(degreeCount.sum()/degreeCount.size());
					avgLocalClust.add(localClustering.sum()/localClustering.size());
					avgEigenCent.add(eigenCounts.sum()/eigenCounts.size());
					avgGlobalClust.add(totalTrips/(double)totalOpen);
					distribution.add(degreeDistribution);
				}
				
				String filename = sizes[j] + "ScaleFreeEdges"+edges[k]+".txt"; 
				File file = new File(filename);
				FileWriter fw;
				BufferedWriter bw = null;
				
				TIntDoubleHashMap avgDist = new TIntDoubleHashMap();
				TIntDoubleHashMap stdDist = new TIntDoubleHashMap();
				for (TIntIntHashMap curr : distribution){
					for (int key : curr.keys()){
						if (avgDist.containsKey(key)) {
							avgDist.put(key, avgDist.get(key) + curr.get(key));
						}
						else{
							avgDist.put(key, curr.get(key));
						}
					}
				}
				
				for (int key : avgDist.keys()) {
					avgDist.put(key, avgDist.get(key) / distribution.size());
				}
				
				for (TIntIntHashMap curr : distribution){
					for (int key : curr.keys()){
						if (avgDist.containsKey(key)) {
							stdDist.put(key, stdDist.get(key) + ((curr.get(key) - avgDist.get(key)) * (curr.get(key) - avgDist.get(key))));
						}
						else{
							avgDist.put(key, (curr.get(key) - avgDist.get(key)) * (curr.get(key) - avgDist.get(key)));
						}
					}
				}
				
				for (int key : stdDist.keys()) {
					stdDist.put(key, Math.sqrt(stdDist.get(key) /distribution.size()));
				}
				
				
				try {
					file.createNewFile();
					fw = new FileWriter(file.getAbsoluteFile());
					bw = new BufferedWriter(fw);
					bw.write("Size of graph: " + sizes[j] + "\n");
					bw.write("Type: Scale Free \n");
					bw.write("Edges Added: "+edges[k]+" \n");
					
					bw.write("Average Degree: " + (avgDegree.sum()/avgDegree.size()) + "\n");
					
					double standDev = 0;
					double mean = avgDegree.sum()/avgDegree.size();
					for (int qq = 0; qq < avgDegree.size(); qq++){
						standDev = standDev +  ((avgDegree.get(qq) - mean) * (avgDegree.get(qq) - mean));
					} 
					standDev = standDev / avgDegree.size();
					standDev = Math.sqrt(standDev);
					bw.write("Standard Deviation Degree: " + (standDev) + "\n");
					
					bw.write("Average Edge Counts: " + (avgEdgeCount.sum() / avgEdgeCount.size()));
					standDev = 0;
					mean = avgEdgeCount.sum()/avgEdgeCount.size();
					for (int qq = 0; qq < avgEdgeCount.size(); qq++){
						standDev = standDev +  ((avgEdgeCount.get(qq) - mean) * (avgEdgeCount.get(qq) - mean));
					} 
					standDev = standDev / avgEdgeCount.size();
					standDev = Math.sqrt(standDev);
					bw.write("Standard Deviation Edge Count: " + (standDev) + "\n");
					
					bw.write("Average Density: " + (density.sum()/density.size()) + "\n");
					standDev = 0;
					mean = density.sum()/density.size();
					for (int qq = 0; qq < density.size(); qq++){
						standDev = standDev +  ((density.get(qq) - mean) * (density.get(qq) - mean));
					} 
					standDev = standDev / density.size();
					standDev = Math.sqrt(standDev);
					bw.write("Standard Deviation Density: " + (standDev) + "\n");
					
					bw.write("Average Local Clustering Coefficient: "+ (avgLocalClust.sum()/avgLocalClust.size())+"\n");
					standDev = 0;
					mean = avgLocalClust.sum()/avgLocalClust.size();
					for (int qq = 0; qq < avgLocalClust.size(); qq++){
						standDev = standDev +  ((avgLocalClust.get(qq) - mean) * (avgLocalClust.get(qq) - mean));
					} 
					standDev = standDev / avgLocalClust.size();
					standDev = Math.sqrt(standDev);
					bw.write("Standard Deviation Local Clustering Coefficient: " + (standDev) + "\n");
					
					bw.write("Average Global Clustering Coefficient: "+ (avgGlobalClust.sum()/avgGlobalClust.size())+"\n");
					standDev = 0;
					mean = avgGlobalClust.sum()/avgGlobalClust.size();
					for (int qq = 0; qq < avgGlobalClust.size(); qq++){
						standDev = standDev +  ((avgGlobalClust.get(qq) - mean) * (avgGlobalClust.get(qq) - mean));
					} 
					standDev = standDev / avgGlobalClust.size();
					standDev = Math.sqrt(standDev);
					bw.write("Standard Deviation Global Clustering Coefficient: " + (standDev) + "\n");
					
					bw.write("Average Eigenvector Centraility: "+ (avgEigenCent.sum()/avgEigenCent.size())+"\n");
					standDev = 0;
					mean = avgEigenCent.sum()/avgEigenCent.size();
					for (int qq = 0; qq < avgEigenCent.size(); qq++){
						standDev = standDev +  ((avgEigenCent.get(qq) - mean) * (avgEigenCent.get(qq) - mean));
					} 
					standDev = standDev / avgEigenCent.size();
					standDev = Math.sqrt(standDev);
					bw.write("Standard Deviation Eigenvector Centraility: " + (standDev) + "\n");
					
					bw.write("Average Degree Distribution: \n");
					
					for (int key: avgDist.keys()) {
						bw.write("Degree " + key + " : " + avgDist.get(key) + "\n");
					}
					
					bw.write("----------------------------" + "\n");
					
					for (int ii = 0; ii < distribution.size(); ii++) {
						TIntIntHashMap currDis = distribution.get(ii);
						
						bw.write("Network " + ii + ": \n");
						
						for (int key : currDis.keys()) {
							bw.write("Degree " + key + " : " + currDis.get(key) + "\n");
						}
						
						bw.write("====================================\n");
						
					}
					
					bw.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				
			}
		}
		
		double[] expo = new double[3];
		expo[0] = 0.25;
		expo[1] = 0.75;
		
		for (int k = 0; k < 2; k++) {
			for (int j = 0; j < 6; j++){
				density.clear();
				avgDegree.clear();
				avgGlobalClust.clear();
				avgLocalClust.clear();
				avgEigenCent.clear();
				distribution.clear();
				avgEdgeCount.clear();
				for (int i = 0; i < 100; i++) {
					System.out.println(i + "-" + sizes[j]);
					Concept ic = new DiverseICConcept(0, i);
					ArrayList<Concept> cList = new ArrayList<Concept>();
					cList.add(ic);
					
					
					smg = grw.graphReadInSW(sizes[j], expo[k], i, cList, false, 0.1, 0.1);
					
					ArrayList<MultiProbabilityNode> none = new ArrayList<MultiProbabilityNode>();
					for (MultiProbabilityNode m : smg.getVertices()) {
						if (smg.getIncidentEdges(m).size() == 0) {
							none.add(m);
						}
					}
					for (MultiProbabilityNode m : none) {
						smg.removeVertex(m);
					}
					
					EigenvectorCentrality<MultiProbabilityNode, TypedWeightedEdge> eigen = new EigenvectorCentrality<MultiProbabilityNode, TypedWeightedEdge>(smg);
					eigen.evaluate();
					
					for (MultiProbabilityNode m : none) {
						smg.addVertex(m);
					}
					
					double dense = (smg.getEdgeCount()) / (smg.getVertexCount() * (((double)smg.getVertexCount() - 1)/2.0));
					density.add(dense);
					
					TDoubleArrayList degreeCount = new TDoubleArrayList();
					TIntIntHashMap degreeDistribution = new TIntIntHashMap();
					TDoubleArrayList localClustering = new TDoubleArrayList();
					TDoubleArrayList eigenCounts = new TDoubleArrayList();
					int totalTrips = 0;
					int totalOpen = 0;
					ArrayList<MultiProbabilityNode> duds = new ArrayList<MultiProbabilityNode>();
					for (MultiProbabilityNode m : smg.getVertices()){
						int count = smg.getIncidentEdges(m).size();
						degreeCount.add(count);
						
						if (none.contains(m)){
							eigenCounts.add(0);
						}
						else {
							eigenCounts.add(eigen.getVertexScore(m));
						}
						
						if (degreeDistribution.containsKey(count)) {
							degreeDistribution.put(count, degreeDistribution.get(count) + 1);
						}
						else {
							degreeDistribution.put(count, 1);
						}
						
						int closedTrips = 0;
						int openTrips = 0;
						Collection<MultiProbabilityNode> neighs = smg.getNeighbors(m);
						
						if (neighs.size() == 0 || neighs.size() == 1) {
							duds.add(m);
						}
						else {
							for (MultiProbabilityNode n : neighs){
								for (MultiProbabilityNode nn : neighs) {
									if (smg.isNeighbor(n, nn) & !n.equals(nn)) {
										closedTrips = closedTrips + 1;
										openTrips = openTrips+1;
									}
									else if (!n.equals(nn)){
										openTrips = openTrips+1;
									}
								}
							}
						
						
							closedTrips = closedTrips/2;
							totalTrips = totalTrips + closedTrips;
							totalOpen = totalOpen + openTrips/2;
							localClustering.add(closedTrips / (0.5 * openTrips));
						}
						
					}
				
					for (MultiProbabilityNode m : duds) {
						smg.removeVertex(m);
					}
					
					//eigen.evaluate();
					avgEdgeCount.add(smg.getEdgeCount());
					avgDegree.add(degreeCount.sum()/degreeCount.size());
					avgLocalClust.add(localClustering.sum()/localClustering.size());
					avgEigenCent.add(eigenCounts.sum()/eigenCounts.size());
					avgGlobalClust.add(totalTrips/(double)totalOpen);
					distribution.add(degreeDistribution);
				}
				
				String filename = sizes[j] + "SmallWorldExpo" + expo[k] + ".txt"; 
				File file = new File(filename);
				FileWriter fw;
				BufferedWriter bw = null;
				
				TIntDoubleHashMap avgDist = new TIntDoubleHashMap();
				TIntDoubleHashMap stdDist = new TIntDoubleHashMap();
				for (TIntIntHashMap curr : distribution){
					for (int key : curr.keys()){
						if (avgDist.containsKey(key)) {
							avgDist.put(key, avgDist.get(key) + curr.get(key));
						}
						else{
							avgDist.put(key, curr.get(key));
						}
					}
				}
				
				for (int key : avgDist.keys()) {
					avgDist.put(key, avgDist.get(key) / distribution.size());
				}
				
				for (TIntIntHashMap curr : distribution){
					for (int key : curr.keys()){
						if (avgDist.containsKey(key)) {
							stdDist.put(key, stdDist.get(key) + ((curr.get(key) - avgDist.get(key)) * (curr.get(key) - avgDist.get(key))));
						}
						else{
							avgDist.put(key, (curr.get(key) - avgDist.get(key)) * (curr.get(key) - avgDist.get(key)));
						}
					}
				}
				
				for (int key : stdDist.keys()) {
					stdDist.put(key, Math.sqrt(stdDist.get(key) /distribution.size()));
				}
				
				
				try {
					file.createNewFile();
					fw = new FileWriter(file.getAbsoluteFile());
					bw = new BufferedWriter(fw);
					bw.write("Size of graph: " + sizes[j] + "\n");
					bw.write("Type: Small World \n");
					bw.write("Expo: " + expo[k] + " \n");
					
					bw.write("Average Degree: " + (avgDegree.sum()/avgDegree.size()) + "\n");
					double standDev = 0;
					double mean = avgDegree.sum()/avgDegree.size();
					for (int qq = 0; qq < avgDegree.size(); qq++){
						standDev = standDev +  ((avgDegree.get(qq) - mean) * (avgDegree.get(qq) - mean));
					} 
					standDev = standDev / avgDegree.size();
					standDev = Math.sqrt(standDev);
					bw.write("Standard Deviation Degree: " + (standDev) + "\n");
					
					bw.write("Average Edge Counts: " + (avgEdgeCount.sum() / avgEdgeCount.size()));
					standDev = 0;
					mean = avgEdgeCount.sum()/avgEdgeCount.size();
					for (int qq = 0; qq < avgEdgeCount.size(); qq++){
						standDev = standDev +  ((avgEdgeCount.get(qq) - mean) * (avgEdgeCount.get(qq) - mean));
					} 
					standDev = standDev / avgEdgeCount.size();
					standDev = Math.sqrt(standDev);
					bw.write("Standard Deviation Edge Count: " + (standDev) + "\n");
					
					bw.write("Average Density: " + (density.sum()/density.size()) + "\n");
					standDev = 0;
					mean = density.sum()/density.size();
					for (int qq = 0; qq < density.size(); qq++){
						standDev = standDev +  ((density.get(qq) - mean) * (density.get(qq) - mean));
					} 
					standDev = standDev / density.size();
					standDev = Math.sqrt(standDev);
					bw.write("Standard Deviation Density: " + (standDev) + "\n");
					
					bw.write("Average Local Clustering Coefficient: "+ (avgLocalClust.sum()/avgLocalClust.size())+"\n");
					standDev = 0;
					mean = avgLocalClust.sum()/avgLocalClust.size();
					for (int qq = 0; qq < avgLocalClust.size(); qq++){
						standDev = standDev +  ((avgLocalClust.get(qq) - mean) * (avgLocalClust.get(qq) - mean));
					} 
					standDev = standDev / avgLocalClust.size();
					standDev = Math.sqrt(standDev);
					bw.write("Standard Deviation Local Clustering Coefficient: " + (standDev) + "\n");
					
					bw.write("Average Global Clustering Coefficient: "+ (avgGlobalClust.sum()/avgGlobalClust.size())+"\n");
					standDev = 0;
					mean = avgGlobalClust.sum()/avgGlobalClust.size();
					for (int qq = 0; qq < avgGlobalClust.size(); qq++){
						standDev = standDev +  ((avgGlobalClust.get(qq) - mean) * (avgGlobalClust.get(qq) - mean));
					} 
					standDev = standDev / avgGlobalClust.size();
					standDev = Math.sqrt(standDev);
					bw.write("Standard Deviation Global Clustering Coefficient: " + (standDev) + "\n");
					
					bw.write("Average Eigenvector Centraility: "+ (avgEigenCent.sum()/avgEigenCent.size())+"\n");
					standDev = 0;
					mean = avgEigenCent.sum()/avgEigenCent.size();
					for (int qq = 0; qq < avgEigenCent.size(); qq++){
						standDev = standDev +  ((avgEigenCent.get(qq) - mean) * (avgEigenCent.get(qq) - mean));
					} 
					standDev = standDev / avgEigenCent.size();
					standDev = Math.sqrt(standDev);
					bw.write("Standard Deviation Eigenvector Centraility: " + (standDev) + "\n");
					
					bw.write("Average Degree Distribution: \n");
					
					for (int key: avgDist.keys()) {
						bw.write("Degree " + key + " : " + avgDist.get(key) + "\n");
					}
					
					bw.write("----------------------------" + "\n");
					
					for (int ii = 0; ii < distribution.size(); ii++) {
						TIntIntHashMap currDis = distribution.get(ii);
						
						bw.write("Network " + ii + ": \n");
						
						for (int key : currDis.keys()) {
							bw.write("Degree " + key + " : " + currDis.get(key) + "\n");
						}
						
						bw.write("====================================\n");
						
					}
					
					bw.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				
			}
		}

	}
}
