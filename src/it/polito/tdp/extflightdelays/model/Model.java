package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {
	
	private class EdgeTraversalListener implements TraversalListener<Airport, DefaultWeightedEdge> {
		@Override
		public void connectedComponentFinished(ConnectedComponentTraversalEvent arg0) {
		}

		@Override
		public void connectedComponentStarted(ConnectedComponentTraversalEvent arg0) {
		}

		@Override
		public void edgeTraversed(EdgeTraversalEvent<DefaultWeightedEdge> e) {
			Airport source = graph.getEdgeSource(e.getEdge());
			Airport target = graph.getEdgeTarget(e.getEdge());
			
			if(!visit.containsKey(target) && visit.containsKey(source))
				visit.put(target, source);
			else if(!visit.containsKey(source) && visit.containsKey(target))
				visit.put(source, target);
		}

		@Override
		public void vertexFinished(VertexTraversalEvent<Airport> arg0) {
		}

		@Override
		public void vertexTraversed(VertexTraversalEvent<Airport> arg0) {
		}
	}
	
	private ExtFlightDelaysDAO dao;
	private Graph<Airport, DefaultWeightedEdge> graph;
	private Map<Integer, Airport> airportIdMap;
	private Map<Airport, Airport> visit;
	
	public Model() {
		this.dao = new ExtFlightDelaysDAO();
		this.airportIdMap = new HashMap<>();
		this.visit = new HashMap<>();
	}
	
	public void createGraph(int minAvgDistance) {
		this.graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		dao.loadAllAirports(airportIdMap);
		Graphs.addAllVertices(graph, airportIdMap.values());
		
		for(Route route : dao.getRoutesByAvgDistance(minAvgDistance, airportIdMap)) {
			DefaultWeightedEdge edge = graph.getEdge(route.getOrigin(), route.getDestination());
			if(edge == null)
					Graphs.addEdge(graph, route.getOrigin(), route.getDestination(), route.getAvgDistance());
			else {
				double weight = graph.getEdgeWeight(edge);
				double newWeight = (weight + route.getAvgDistance())/2;
				graph.setEdgeWeight(edge, newWeight);
			}
		}
		
		System.out.println("Grafo creato!");
		System.out.println(graph.vertexSet().size() + " vertici e " + graph.edgeSet().size() + " archi.");
	}

	public Boolean testConnection(int originId, int destinationId) {
		Set<Airport> visited = new HashSet<>();
		Airport origin = airportIdMap.get(originId);
		Airport destination = airportIdMap.get(destinationId);
		System.out.println("Testo connessione tra " + origin + " e " + destination);
		
		BreadthFirstIterator<Airport, DefaultWeightedEdge> it = new BreadthFirstIterator<>(this.graph, origin);
		
		while(it.hasNext())
			visited.add(it.next());
		
		if(visited.contains(destination))
			return true;
		else
			return false;
	}
	
	public List<Airport> findRoute(int originId, int destinationId) {
		List<Airport> route = new ArrayList<>();
		Airport origin = airportIdMap.get(originId);
		Airport destination = airportIdMap.get(destinationId);
		System.out.println("Cerco percorso tra " + origin + " e " + destination);
		
		BreadthFirstIterator<Airport, DefaultWeightedEdge> it = new BreadthFirstIterator<>(this.graph, origin);
		visit.put(origin, null);
		it.addTraversalListener(new Model.EdgeTraversalListener());
		
		while(it.hasNext())
			it.next();
		
		if(!visit.containsKey(origin) || !visit.containsKey(destination))
			return null;
		
		Airport step = destination;
		while(!step.equals(origin)) {
			route.add(0, step);
			step = visit.get(step);
		}
		route.add(0, step);
		
		return route;
	}
	
}
