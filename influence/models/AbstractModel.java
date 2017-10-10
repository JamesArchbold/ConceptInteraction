package influence.models;

import influence.edges.Edge;
import influence.nodes.Node;

import java.util.Set;

import edu.uci.ics.jung.graph.Graph;

public abstract class AbstractModel {

	abstract<E extends Node> void run(Graph<E,Edge> g, Set<E> seeds); 
}
