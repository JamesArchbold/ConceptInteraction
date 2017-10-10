package influence.edges;

public class TypedEdge extends Edge {

	private int concept;
	
	public TypedEdge(int id, int concept) {
		super(id);
		this.concept = concept;
	}
	
	public int getConcept() {
		return concept;
	}

}
