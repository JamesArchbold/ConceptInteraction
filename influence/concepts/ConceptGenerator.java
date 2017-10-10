package influence.concepts;
//Used in early experiments to generate concepts, not used
import gnu.trove.set.hash.TIntHashSet;

import java.util.ArrayList;
import java.util.Random;

public class ConceptGenerator {

	private int num;
	private double boostAvg;
	private double inhibitAvg;
	private Random rand;
	ArrayList<Concept> conList;
	
	public ConceptGenerator(int num, double boostProp, double inhibitProp, int seed) {
		this.num = num;
		this.boostAvg = boostProp * num;
		this.inhibitAvg = inhibitProp * num;
		rand = new Random(seed);
	}
	
	public void printList() {
		for (Concept c : conList) {
			System.out.println("------ Concept " + c.getId() + " ------");
			
			for (int i = 0; i < num; i++) {
				if (i != c.getId()) {
					//System.out.println("Internal value for " + conList.get(i).getId() + " is " + c.getConceptInternal(conList.get(i)));
				//	System.out.println("External value for " + conList.get(i).getId() + " is " + c.getConceptExternal(conList.get(i)));
				}
			}
		}
	}
	
	public ArrayList<Concept> getConcepts() {
		return conList;
	}
	
	public void makeConceptsSet() {
		makeConceptsSet(2.5);
	}
	public void makeConceptsSet(double range) {
        conList = new ArrayList<Concept>();
		
		for (int i = 0; i < num; i++) {
			conList.add(new Concept(i));
		}
		
		for (Concept c : conList) {
			int boostSelect = -1;

			do {
				boostSelect = (int) boostAvg;
			} while (boostSelect < 0 || boostSelect >= (num/2.0));
			
			int inhibitSelect = -1;
			
			do {
				inhibitSelect = (int) inhibitAvg;
			} while (inhibitSelect < 0 || inhibitSelect >= (num/2.0));
			
		//	System.out.println("Concept " + c.getId());
		//	System.out.println(boostSelect);
		//	System.out.println(inhibitSelect);
			
			TIntHashSet boostingConcepts = new TIntHashSet();
			while (boostingConcepts.size() < boostSelect) {
				int choice = rand.nextInt(num);
				if (choice != c.getId()) {
					boostingConcepts.add(choice);
				}
			}
			
			TIntHashSet inhibitConcepts = new TIntHashSet();
			while (inhibitConcepts.size() < inhibitSelect) {
				int choice = rand.nextInt(num);
				if (choice != c.getId() && !(boostingConcepts.contains(choice))) {
					inhibitConcepts.add(choice);
				}
			}
			
			for (int i = 0; i < num; i++){
				if (boostingConcepts.contains(i)) {
					conList.get(i).addConceptInteractions(c, (rand.nextDouble()/2.0), (rand.nextDouble()/2.0));
				}
				else if (inhibitConcepts.contains(i)) {
					conList.get(i).addConceptInteractions(c, (rand.nextDouble()/2.0)*-1, (rand.nextDouble()/2.0)*-1);
				}
				else if (i != c.getId()){
					conList.get(i).addConceptInteractions(c, 0,0);
				}
			}
			
		}
	}
	
	public void makeConceptsGauss() {
		makeConceptsGauss(num/4.0);
	}

	public void makeConceptsGauss(double gRange) {
        conList = new ArrayList<Concept>();
		
		for (int i = 0; i < num; i++) {
			conList.add(new Concept(i));
		}
		
		for (Concept c : conList) {
			int boostSelect = -1;

			do {
				boostSelect = (int) ((rand.nextGaussian()*gRange) + boostAvg + 0.5);
			} while (boostSelect < 0 || boostSelect >= (num/2.0));
			
			int inhibitSelect = -1;
			
			do {
				inhibitSelect = (int) ((rand.nextGaussian()*gRange) + inhibitAvg + 0.5);
			} while (inhibitSelect < 0 || inhibitSelect >= (num/2.0));
			
		//	System.out.println("Concept " + c.getId());
		//	System.out.println(boostSelect);
		//	System.out.println(inhibitSelect);
			
			TIntHashSet boostingConcepts = new TIntHashSet();
			while (boostingConcepts.size() < boostSelect) {
				int choice = rand.nextInt(num);
				if (choice != c.getId()) {
					boostingConcepts.add(choice);
				}
			}
			
			TIntHashSet inhibitConcepts = new TIntHashSet();
			while (inhibitConcepts.size() < inhibitSelect) {
				int choice = rand.nextInt(num);
				if (choice != c.getId() && !(boostingConcepts.contains(choice))) {
					inhibitConcepts.add(choice);
				}
			}
			
			for (int i = 0; i < num; i++){
				if (boostingConcepts.contains(i)) {
					conList.get(i).addConceptInteractions(c, (rand.nextDouble()/2.0), (rand.nextDouble()/2.0));
				}
				else if (inhibitConcepts.contains(i)) {
					conList.get(i).addConceptInteractions(c, (rand.nextDouble()/2.0)*-1, (rand.nextDouble()/2.0)*-1);
				}
				else if (i != c.getId()){
					conList.get(i).addConceptInteractions(c, 0,0);
				}
			}
			
			
		}
		
	}
}
