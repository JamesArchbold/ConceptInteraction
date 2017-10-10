package influence.concepts;
//Base concept class
import influence.nodes.MultiProbabilityNode;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.set.hash.TIntHashSet;

public class Concept {
	
	private int id;
	private TIntDoubleHashMap interactionMapInternal;//holds relationship strengths for when another concept is active on the same node and this concept is trying to spread
	private TIntDoubleHashMap interactionMapExternal;//holds relationship strengths for when another concept is active on the node this concept is spreading to
	private String type;
	//internal = spread
	//external = adopt
	public Concept(int id) {
		this.id = id;
		interactionMapInternal = new TIntDoubleHashMap();
		interactionMapExternal = new TIntDoubleHashMap();
		type = "";
	}
	
	public Concept(int id, String type) {
		this.id = id;
		interactionMapInternal = new TIntDoubleHashMap();
		interactionMapExternal = new TIntDoubleHashMap();
		this.type = type;
	}
	//type can be IC,LT,SIS or SIR
	public String getType() {
		return type;
	}
	
	public int getId(){
		return id;
	}
	
	public TIntDoubleHashMap getMapInternal() {
		return interactionMapInternal;
	}
	
	public TIntDoubleHashMap getMapExternal() {
		return interactionMapExternal;
	}
	
	//adds relationships between concepts
	public void addConceptInteractions(Concept c, double internal, double external) {
		interactionMapInternal.put(c.getId(), internal);
		interactionMapExternal.put(c.getId(), external);
	}
	//how does c affect chance of current concept being adopted by node
	public double getConceptExternal(Concept c){
		if (c == null) {return 0;}
		return interactionMapExternal.get(c.getId());
	}
	
	//how does c affect the chance of concept spreading to another node
	public double getConceptInternal(Concept c){
		return interactionMapInternal.get(c.getId());
	}
	
	//finds the internal environment of a node as it relates to this concept, used for spreading to another node
	public double getInternalEnvironment(MultiProbabilityNode n) {
		TIntHashSet concs = n.activatedConcepts();
		//System.out.println(concs);
		TIntIterator iterator = concs.iterator();
		double total = 0;
		
		while (iterator.hasNext()) {
			total += interactionMapInternal.get(iterator.next());
		}
		
		if (total > 1) {
			total = 1;
		}
		
		if (total < -1){
			total = -1;
		}
		//System.out.println(interactionMapInternal);
		return total;
	}
	
	public boolean equals(Concept c) {
		return (this.id == c.id);
	}
	
	//finds the external environment of a node as it relates to this concept, used for adopting this concept
	public double getExternalEnvironment(MultiProbabilityNode n) {
		TIntHashSet concs = n.activatedConcepts();
		//System.out.println(concs.size());
		TIntIterator iterator = concs.iterator();
		double total = 0;
		
		while (iterator.hasNext()) {
			total += interactionMapExternal.get(iterator.next());
		}
		
		if (total > 1) {
			total = 1;
		}
		
		if (total < -1){
			total = -1;
		}
		
		return total;
	}
}
