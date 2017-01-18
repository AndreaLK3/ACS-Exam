package com.acertaininventorymanager.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;

import com.sun.javafx.collections.MappingChange.Map;

public class WaitingGraphManager {

	private ConcurrentHashMap<Integer, List<Integer>> mapOfAdjLists;
	private Strict2PLManager theLockManager; 
	
	/**The constructor*/
	public WaitingGraphManager(Strict2PLManager lockManager) {
		mapOfAdjLists = new ConcurrentHashMap<>();		
		theLockManager = lockManager;
		//// starts the monitor thread, that periodically checks the graph for cycles
		new Thread(){ 
			public void run(){
				exploreAndSolveDeadlocks();
			}
		}.start();
	}
	
	
	public void addEdges(Integer u, Set<Integer> vs){
		if (! (mapOfAdjLists.containsKey(u)) ){
			mapOfAdjLists.put(u, new ArrayList<>());
		}
		
		for (Integer v : vs){
			List<Integer> adjListOfU = mapOfAdjLists.get(u);
			adjListOfU.add(v);
		}

	}
	
	public void removeXactFromGraph(Integer xactId){
		if (mapOfAdjLists.containsKey(xactId)){
			mapOfAdjLists.remove(xactId);
		}
		Set<Integer> uIds = mapOfAdjLists.keySet();
		for (Integer u : uIds){
			removeEdge(u, xactId);
		}
	}
	
	
	public void removeEdge(Integer u, Integer v){
		List<Integer> adjListOfU = mapOfAdjLists.get(u);
		if (adjListOfU!=null){
			adjListOfU.remove(v);	//n: this is remove Object, not remove by Index.
		}
		
	}

	
	private void exploreAndSolveDeadlocks() {
		ConcurrentHashMap<Integer, List<Integer>> graph;
		Integer deadlockNode = null;
		
		while (deadlockNode==null){
			try {
				Thread.sleep(500);
				deadlockNode = isThereADeadlock(mapOfAdjLists);
				
			} catch (InterruptedException e) {
				e.printStackTrace();
				exploreAndSolveDeadlocks();
			}
		}
		theLockManager.flagTransactionAsAborted(deadlockNode);
		theLockManager.awakeTransactions();
		
		
	}


	/**The function does not modify anything, 
	 * it invokes other functions to search the graph for cycles.
	 * Returns the starting node in a cycle, or null if no cycle is found.*/
	public Integer isThereADeadlock(ConcurrentHashMap<Integer, List<Integer>> graph) {

		ConcurrentHashMap<Integer, List<Integer>> graphToSearch = new ConcurrentHashMap<>(graph);
		
		Set<Integer> startingNodes = graphToSearch.keySet();
		
		for (Integer startingNode : startingNodes){
			List<Integer> nodesReachableFromStart = explorePath(startingNode, startingNode, 
																new ArrayList<>(), startingNodes.size(), graphToSearch);
			if (nodesReachableFromStart.contains(startingNode)){
				return startingNode;
			}
		}
		return null;
	}
	
	
	/**Returns a List of the integer IDs of the nodes that can be reached from the starting one,
	 * BFS.*/
	private List<Integer> explorePath(Integer target, Integer currentStart, List<Integer> reachableNodes,
										Integer graphSize, ConcurrentHashMap<Integer, List<Integer>> graph){
		
		if (reachableNodes.contains(target) || graphSize==0){
			return reachableNodes;
		}
		
		graphSize--;
		
		List<Integer> adjNodes = graph.get(currentStart);
		if (adjNodes == null){
			return reachableNodes;
		}
		
		for (Integer node : adjNodes){
			reachableNodes.add(node);
			reachableNodes.addAll(explorePath(target, node, reachableNodes, graphSize, graph));
		}
		
		return reachableNodes;
	}


	/**Returns a copy of the map of adjacency lists**/
	public ConcurrentHashMap<Integer, List<Integer>> getMapOfAdjLists() {
		return new ConcurrentHashMap<Integer, List<Integer>> (mapOfAdjLists);
	}
}
