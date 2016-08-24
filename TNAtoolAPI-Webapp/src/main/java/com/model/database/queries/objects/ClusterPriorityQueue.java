// Copyright (C) 2015 Oregon State University - School of Mechanical,Industrial and Manufacturing Engineering 
//   This file is part of Transit Network Analysis Software Tool.
//
//    Transit Network Analysis Software Tool is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Transit Network Analysis Software Tool is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU  General Public License for more details.
//
//    You should have received a copy of the GNU  General Public License
//    along with Transit Network Analysis Software Tool.  If not, see <http://www.gnu.org/licenses/>.

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
