package influence.nodes.comparators;
import influence.nodes.Node;

import java.util.Comparator;

public class MultiProbabilityComparator<E> implements Comparator<E> {

	@Override
	public int compare(E o1, E o2) {
		if (((Node) o1).getValue() < ((Node) o2).getValue()) {
			return -1;
		}
		else if (((Node) o1).getValue() > ((Node) o2).getValue()) {
			return 1;
		}
		else if (((Node) o1).getValue() == ((Node) o2).getValue() && ((Node) o1).getId() > ((Node) o2).getId()){
			return 1;
		}
		else if (((Node) o1).getValue() == ((Node) o2).getValue() && ((Node) o1).getId() < ((Node) o2).getId()){
			return -1;
		}
		else {
			return 0;
		}
	}

}
