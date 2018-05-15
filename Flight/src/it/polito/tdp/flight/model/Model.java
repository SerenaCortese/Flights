package it.polito.tdp.flight.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.flight.db.FlightDAO;

public class Model {
	
	private FlightDAO fdao = null;
	
	private List<Airport> airports;
	private List<Airline> airlines;
	private List<Route> routes;
	
	private AirlineIdMap airlineIdMap;
	private AirportIdMap airportIdmap;
	private RouteIdMap routeIdMap;
	
	SimpleDirectedWeightedGraph<Airport, DefaultWeightedEdge> grafo;
	
	public Model() {
		fdao = new FlightDAO();
		
		this.airlineIdMap = new AirlineIdMap();
		this.airportIdmap = new AirportIdMap();
		this.routeIdMap = new RouteIdMap();
		
		airlines = fdao.getAllAirlines(airlineIdMap);
		System.out.println(airlines.size());

		airports = fdao.getAllAirports(airportIdmap);
		System.out.println(airports.size());

		routes = fdao.getAllRoutes(routeIdMap,airportIdmap,airlineIdMap);
		System.out.println(routes.size());
		
	}
	
	public List<Airport> getAirports(){
		if(this.airports == null)
			return new ArrayList<Airport>(); 
		return this.airports;
	}
	
	public void createGraph() {
		//creo grafo
		grafo = new SimpleDirectedWeightedGraph<Airport,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		//aggiungo vertici
		Graphs.addAllVertices(this.grafo, this.airports);
		
		//creo archi(=>itero sulle rotte)
		for(Route r: this.routes) {
			Airport sourceAirport = r.getSourceAirport();
			Airport destinationAirport = r.getDestinationAirport();
			
			if(!sourceAirport.equals(destinationAirport)) {//non devo fare questo controllo se creo uno pseudografo perché lì loop ammessi
				
				double weight = LatLngTool.distance(new LatLng(sourceAirport.getLatitude(), sourceAirport.getLongitude()),
						new LatLng(destinationAirport.getLatitude(), destinationAirport.getLongitude()), LengthUnit.KILOMETER);
				Graphs.addEdge(this.grafo, sourceAirport, destinationAirport, weight);
			}
		}
		
		System.out.println(grafo.vertexSet().size());
		System.out.println(grafo.edgeSet().size());
		
	}
	
	public void printStats() {
		if(this.grafo == null) {
			this.createGraph();
		}
		
		ConnectivityInspector<Airport,DefaultWeightedEdge> ci = new ConnectivityInspector<Airport,DefaultWeightedEdge>(grafo);
		System.out.println(ci.connectedSets().size());
		
	}
	
	public Set<Airport> getBiggestSCC(){//SCC perché componente fortemente connesa
		Set<Airport> bestSet = null;
		int bestSize = 0;
		
		ConnectivityInspector<Airport,DefaultWeightedEdge> ci = new ConnectivityInspector<Airport,DefaultWeightedEdge>(grafo);
		for(Set<Airport> s : ci.connectedSets()) {
			if(s.size() > bestSize) {
				bestSet = new HashSet(s); //così son sicura che faccia un'altra copia
				bestSize = s.size();
			}
		}
		return bestSet;
	}

	public List<Airport> getShortestPath(int id1, int id2) {
		Airport a1 = airportIdmap.get(id1);
		Airport a2 = airportIdmap.get(id2);
		
		if(a1 == null || a2 == null) {
			throw new RuntimeException("Gli aeroporti selezionati non sono presenti in memoria");
		}
		System.out.println(a1.toString());
		System.out.println(a2.toString());
		
		//uso algoritmo di Dijkstra
		ShortestPathAlgorithm<Airport, DefaultWeightedEdge> spa = new DijkstraShortestPath<Airport, DefaultWeightedEdge>(this.grafo);
		
		double weight = spa.getPathWeight(a1, a2);
		System.out.println("peso: "+ weight);
		GraphPath<Airport, DefaultWeightedEdge> gp = spa.getPath(a1, a2);
	
		return gp.getVertexList();
		
	}
}
