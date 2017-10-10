package influence.nodes;

import java.util.ArrayList;

import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.set.hash.TIntHashSet;
import influence.concepts.Concept;
import influence.concepts.ExtendedConcept;
import influence.nodes.Node;

public class MultiProbabilityNode extends Node {

	private TIntIntHashMap activated;
	private TIntHashSet currAct;
	private TIntDoubleHashMap attributes;
	
	public MultiProbabilityNode(int id, ArrayList<? extends Concept> concepts) {
		super(id);
		activated = new TIntIntHashMap();
		currAct = new TIntHashSet();
		attributes = new TIntDoubleHashMap();
		for (Concept c : concepts){
			activated.put(c.getId(), 0);
		}
	}
	
	public MultiProbabilityNode(int id) {
		super(id);
		activated = new TIntIntHashMap();
		currAct = new TIntHashSet();
		attributes = new TIntDoubleHashMap();
	}
	
	public void addAttribute(int key, double attr){
		attributes.put(key, attr);
	}
	
	
	public double getAttribute(int key){
		return attributes.get(key);
	}
	public boolean isActivated(Concept c) {
		return activated.get(c.getId()) == 1;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public void activate(Concept c) {
		activated.put(c.getId(), 1);
		currAct.add(c.getId());
	}
	
	public void deactivateAll(){
		activated.clear();
	}
	
	public void deactivate(Concept c) {
		activated.put(c.getId(), 0);
		currAct.remove(c.getId());
	}
	
	public TIntHashSet activatedConcepts() {
		return currAct;
	}

}
