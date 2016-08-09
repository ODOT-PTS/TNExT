package com.model.database.queries.objects;

import java.util.Comparator;
import java.util.TreeSet;

public class ClusterPriorityQueue extends TreeSet<StopCluster> {	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public TreeSet<StopCluster> ClusterPriorityQueue;
	public ClusterPriorityQueue(){
		ClusterPriorityQueue = new TreeSet<StopCluster>(new Comparator<StopCluster>() {
			public int compare(StopCluster clusterA, StopCluster clusterB){				
				if (clusterA.agencies.size()==clusterB.agencies.size()){
					if (clusterA.routes.size()==clusterB.agencies.size()){
						if (clusterA.visits==clusterB.visits){
							if (clusterA.stops.size()==clusterB.stops.size()){
								return -1;
							} else return (clusterA.stops.size()>clusterB.stops.size() ? -1:1);
						} else return (clusterA.visits>clusterB.visits ? -1:1);
					} else return (clusterA.routes.size()>clusterB.routes.size() ? -1:1);
				} else return (clusterA.agencies.size()>clusterB.agencies.size() ? -1:1);				
			}
		});		
	}	
}
