package it.polito.tdp.extflightdelays.model;

import java.util.HashMap;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {
	
	private ExtFlightDelaysDAO dao;
	private Graph<Airport, DefaultWeightedEdge> graph;
	private Map<Integer, Airport> airportIdMap;
	
	public Model() {
		this.dao = new ExtFlightDelaysDAO();
		this.airportIdMap = new HashMap<>();
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
		System.out.print(graph.vertexSet().size() + " vertici e ");
		System.out.println(graph.edgeSet().size() + " archi.");
	}

}
