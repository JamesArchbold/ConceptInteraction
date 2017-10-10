package influence.concepts;
//test class for generating concept lists
import java.util.ArrayList;
import java.util.HashMap;

public class ConceptLists {
	HashMap<Integer, ArrayList<Concept>> lists = new HashMap<Integer, ArrayList<Concept>>();
	
	public ConceptLists() {
		lists = new HashMap<Integer, ArrayList<Concept>>();
		makeLists();
	}
	//0 - 5 2 positive
	//6 - 11 2 negative
	//12 - 17 3 positive
	//18 - 23 3 negative
	//24 - 29 4 positive
	//30 - 35 4 negative
	//36 - 41 5 positive
	//42 - 47 5 negative
	public void makeLists() {
		int set = 0;
		
		for (double i = 0; i < 0.6; i = i + 0.1) {
			
			ArrayList<Concept> conceptSet = new ArrayList<Concept>();
			
			Concept c1 = new Concept(0);
			Concept c2 = new Concept(1);
			
			c1.addConceptInteractions(c2, i, i);
			c2.addConceptInteractions(c1, 0, 0);
			
			conceptSet.add(c1);
			conceptSet.add(c2);
			
			lists.put(set, conceptSet);
			set++;
		}
		
		for (double i = 0; i < 0.6; i = i + 0.1) {
			
			ArrayList<Concept> conceptSet = new ArrayList<Concept>();
			
			Concept c1 = new Concept(0);
			Concept c2 = new Concept(1);
			
			c1.addConceptInteractions(c2, -i, -i);
			c2.addConceptInteractions(c1, 0, 0);
			
			conceptSet.add(c1);
			conceptSet.add(c2);
			
			lists.put(set, conceptSet);
			set++;
		}
		
		for (double i = 0; i < 0.6; i = i + 0.1) {
			ArrayList<Concept> conceptSet = new ArrayList<Concept>();
			
			Concept c1 = new Concept(0);
			Concept c2 = new Concept(1);
			Concept c3 = new Concept (2);
			
			c1.addConceptInteractions(c2, i, i);
			c1.addConceptInteractions(c3, i, i);
			
			c2.addConceptInteractions(c1, 0, 0);
			c2.addConceptInteractions(c3, 0, 0);
			c3.addConceptInteractions(c1, 0, 0);
			c3.addConceptInteractions(c2, 0, 0);
			

			conceptSet.add(c1);
			conceptSet.add(c2);
			conceptSet.add(c3);
			
			
			lists.put(set, conceptSet);
			set++;
		}
		
		for (double i = 0; i < 0.6; i = i + 0.1) {
			ArrayList<Concept> conceptSet = new ArrayList<Concept>();
			
			Concept c1 = new Concept(0);
			Concept c2 = new Concept(1);
			Concept c3 = new Concept (2);
			
			c1.addConceptInteractions(c2, -i, -i);
			c1.addConceptInteractions(c3, -i, -i);
			
			c2.addConceptInteractions(c1, 0, 0);
			c2.addConceptInteractions(c3, 0, 0);
			c3.addConceptInteractions(c1, 0, 0);
			c3.addConceptInteractions(c2, 0, 0);
			
			conceptSet.add(c1);
			conceptSet.add(c2);
			conceptSet.add(c3);
			
			lists.put(set, conceptSet);
			set++;
		}
		
		for (double i = 0; i < 0.6; i = i + 0.1) {

			ArrayList<Concept> conceptSet = new ArrayList<Concept>();
			
			Concept c1 = new Concept(0);
			Concept c2 = new Concept(1);
			Concept c3 = new Concept(2);
			Concept c4 = new Concept(3);
			
			c1.addConceptInteractions(c2, i, i);
			c1.addConceptInteractions(c3, i, i);
			c1.addConceptInteractions(c4, i, i);
			
			c2.addConceptInteractions(c1, 0, 0);
			c2.addConceptInteractions(c3, 0, 0);
			c2.addConceptInteractions(c4, 0, 0);
			
			c3.addConceptInteractions(c1, 0, 0);
			c3.addConceptInteractions(c2, 0, 0);
			c3.addConceptInteractions(c4, 0, 0);

			c4.addConceptInteractions(c1, 0, 0);
			c4.addConceptInteractions(c2, 0, 0);
			c4.addConceptInteractions(c3, 0, 0);
			

			conceptSet.add(c1);
			conceptSet.add(c2);
			conceptSet.add(c3);
			conceptSet.add(c4);
			lists.put(set, conceptSet);
			set++;
		}
		
		for (double i = 0; i < 0.6; i = i + 0.1) {
			
			ArrayList<Concept> conceptSet = new ArrayList<Concept>();
			
			Concept c1 = new Concept(0);
			Concept c2 = new Concept(1);
			Concept c3 = new Concept(2);
			Concept c4 = new Concept(3);
			
			c1.addConceptInteractions(c2, -i, -i);
			c1.addConceptInteractions(c3, -i, -i);
			c1.addConceptInteractions(c4, -i, -i);
			
			c2.addConceptInteractions(c1, 0, 0);
			c2.addConceptInteractions(c3, 0, 0);
			c2.addConceptInteractions(c4, 0, 0);
			
			c3.addConceptInteractions(c1, 0, 0);
			c3.addConceptInteractions(c2, 0, 0);
			c3.addConceptInteractions(c4, 0, 0);

			c4.addConceptInteractions(c1, 0, 0);
			c4.addConceptInteractions(c2, 0, 0);
			c4.addConceptInteractions(c3, 0, 0);
			
			conceptSet.add(c1);
			conceptSet.add(c2);
			conceptSet.add(c3);
			conceptSet.add(c4);
			lists.put(set, conceptSet);
			set++;
		}
		
		for (double i = 0; i < 0.6; i = i + 0.1) {
			
			ArrayList<Concept> conceptSet = new ArrayList<Concept>();
			Concept c1 = new Concept(0);
			Concept c2 = new Concept(1);
			Concept c3 = new Concept(2);
			Concept c4 = new Concept(3);
			Concept c5 = new Concept(4);
			
			c1.addConceptInteractions(c2, i, i);
			c1.addConceptInteractions(c3, i, i);
			c1.addConceptInteractions(c4, i, i);
			c1.addConceptInteractions(c5, i, i);
			
			c2.addConceptInteractions(c1, 0, 0);
			c2.addConceptInteractions(c3, 0, 0);
			c2.addConceptInteractions(c4, 0, 0);
			c2.addConceptInteractions(c5, 0, 0);
			
			c3.addConceptInteractions(c1, 0, 0);
			c3.addConceptInteractions(c2, 0, 0);
			c3.addConceptInteractions(c4, 0, 0);
			c3.addConceptInteractions(c5, 0, 0);
			
			c4.addConceptInteractions(c1, 0, 0);
			c4.addConceptInteractions(c2, 0, 0);
			c4.addConceptInteractions(c3, 0, 0);
			c4.addConceptInteractions(c5, 0, 0);
			
			c5.addConceptInteractions(c1, 0, 0);
			c5.addConceptInteractions(c2, 0, 0);
			c5.addConceptInteractions(c3, 0, 0);
			c5.addConceptInteractions(c4, 0, 0);
			
			conceptSet.add(c1);
			conceptSet.add(c2);
			conceptSet.add(c3);
			conceptSet.add(c4);
			conceptSet.add(c5);
			
			
			lists.put(set, conceptSet);
			set++;
		}
		
	for (double i = 0; i < 0.6; i = i + 0.1) {
			
		ArrayList<Concept> conceptSet = new ArrayList<Concept>();
			
			Concept c1 = new Concept(0);
			Concept c2 = new Concept(1);
			Concept c3 = new Concept(2);
			Concept c4 = new Concept(3);
			Concept c5 = new Concept(4);
			
			c1.addConceptInteractions(c2, -i, -i);
			c1.addConceptInteractions(c3, -i, -i);
			c1.addConceptInteractions(c4, -i, -i);
			c1.addConceptInteractions(c5, -i, -i);
			
			c2.addConceptInteractions(c1, 0, 0);
			c2.addConceptInteractions(c3, 0, 0);
			c2.addConceptInteractions(c4, 0, 0);
			c2.addConceptInteractions(c5, 0, 0);
			
			c3.addConceptInteractions(c1, 0, 0);
			c3.addConceptInteractions(c2, 0, 0);
			c3.addConceptInteractions(c4, 0, 0);
			c3.addConceptInteractions(c5, 0, 0);
			
			c4.addConceptInteractions(c1, 0, 0);
			c4.addConceptInteractions(c2, 0, 0);
			c4.addConceptInteractions(c3, 0, 0);
			c4.addConceptInteractions(c5, 0, 0);
			
			c5.addConceptInteractions(c1, 0, 0);
			c5.addConceptInteractions(c2, 0, 0);
			c5.addConceptInteractions(c3, 0, 0);
			c5.addConceptInteractions(c4, 0, 0);
			
			conceptSet.add(c1);
			conceptSet.add(c2);
			conceptSet.add(c3);
			conceptSet.add(c4);
			conceptSet.add(c5);
			
			lists.put(set, conceptSet);
			set++;
		}
	}
	
	public ArrayList<Concept> get (int i) {
		return lists.get(i);
	}
	
	public int size() {
		return lists.size();
	}
	
}
