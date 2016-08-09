package com.model.database.queries.congraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "ConGraphAgencyGraph")
public class ConGraphAgencyGraph {
	@XmlElement ( name = "ID")
	public String ID;
	
	@XmlElement ( name = "centralized")
	public boolean centralized;
	
	@XmlElement ( name = "vertices")
	public List<Coordinate> vertices = new ArrayList<Coordinate>();
	
	@XmlElement ( name = "edges")
	public Set<Coordinate[]> edges = new HashSet<Coordinate[]>();
	
	
	public ConGraphAgencyGraph(String agencyID, List<ConGraphCluster> clusters){
		for (ConGraphCluster c : clusters){
			this.vertices.add(c.centroid);
		}
		this.edges = getMST(vertices);
		this.ID = agencyID;
	}
	
	public ConGraphAgencyGraph(){
		
	}
	
	/**
	 * Inner Class Edge
	 * @author PB
	 *
	 */
	static class Edge implements Comparable<Edge>{
		Coordinate u;
		Coordinate v;
		double weight;
		
		public Edge(Coordinate u, Coordinate v){
			this.u = u;
			this.v = v;
			this.weight = getDistance(u,v);
		}
		
		@Override
		public int compareTo(Edge other) {
	 
			if (this.weight > other.weight)
				return 1;
			else if (this.weight < other.weight)
				return -1;
			else
				return 0;
		}
		
		@Override
		public String toString(){
			return this.u + "," + this.v;
		}
	}
	
	/**
	 * Gets the distance between two coordinates in kilometers.
	 * @param p1 (Coordinate)
	 * @param p2 (Coordinate)
	 * @return distance (double)
	 */
	public static double getDistance(Coordinate p1, Coordinate p2){
		double dLon = Math.toRadians(p2.lng - p1.lng); 
		double dLat = Math.toRadians(p2.lat - p1.lat);
		double lat1 = Math.toRadians(p1.lat);
		double lat2 = Math.toRadians(p2.lat);
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2); 
		double c = 2 * Math.atan2( Math.sqrt(a), Math.sqrt(1-a) ) ;
		double output =  6371 * c ;
		return output;
	}
	
	
	/**
	 * Returns the minimum spanning tree on the full graph build
	 * on the input coordinates.
	 * 
	 * @param vertices Set<Coordinate>
	 * @return edges Set<Coordinate[]>
	 */
	private static Set<Coordinate[]> getMST(List<Coordinate> vertices){
		Set<Coordinate[]> response = new HashSet<Coordinate[]>();
		// Building the set of all possible edges between vertices.
		PriorityQueue<Edge> edges = new PriorityQueue<ConGraphAgencyGraph.Edge>();

		for (int i = 0 ; i < vertices.size() ; i++){
			for (int j = i+1 ; j < vertices.size() ; j++){
				Coordinate u = vertices.get(i);
				Coordinate v = vertices.get(j);
				Edge e = new Edge(u, v);
				edges.add(e);
			}
		}
		
		// Kruskal Algorithm.
		Set<Coordinate> visitedVertices = new HashSet<Coordinate>();
		
		// pick a random node and add it to the visitedVertices.
		Coordinate currentNode = vertices.get(0);
		visitedVertices.add(currentNode);
		
		while (!edges.isEmpty()){
			PriorityQueue<Edge> currentEdges = new PriorityQueue<Edge>();
			for (Edge e : edges)
				if (visitedVertices.contains(e.u) || visitedVertices.contains(e.v) )
					currentEdges.add(e);
			
			Edge currentEdge = currentEdges.poll();
			if ( !visitedVertices.contains(currentEdge.u) || !visitedVertices.contains(currentEdge.v) ){
				visitedVertices.add(currentEdge.u);
				visitedVertices.add(currentEdge.v);
				Coordinate[] instance = new Coordinate[2];
				instance[0] = currentEdge.u;
				instance[1] = currentEdge.v;			
				response.add (instance);
			}else{
				edges.remove(currentEdge);
			}
		}
		
		return response;
	}
}
