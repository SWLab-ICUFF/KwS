package eppstein;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import util.Graph;

public class Removedor {
	private Graph tree,g;
	
	public Removedor(Graph g, Graph tree, Graph eg) {
		this.tree = tree;
		this.g = g;
		Set<Aresta> naotree = getNonTreeEdges();
		for(Aresta a : naotree) {
			if(dfs(a.u,a.v)) {
				System.out.println("permanece " + a.u + "-" + a.v);
			} else {
				eg.removeEdgeBoth(a.u, a.v);
			}
		}
	}
	
	private Set<Aresta> getNonTreeEdges() {
		Set<Aresta> nonTree = new HashSet<Aresta>();
		Map<String,Boolean> visitados = new HashMap<String,Boolean>();
		Stack<String> pilha = new Stack<String>();
		String start = g.vertices().iterator().next();
		pilha.push(start);
		
		while(!pilha.empty()) {
			String u = pilha.pop();
			visitados.put(u, true);
			Iterable<String> adj = g.adjacentTo(u);
			for(String v : adj) {
				if(!visitados.containsKey(v) || visitados.get(v) == false) {
					pilha.push(v);
					if(!tree.hasEdge(u, v))
						nonTree.add(new Aresta(u,v,g.getCost(u, v)));
				}
			}
		}
		
		return nonTree;
	}
	
	private boolean dfs(String start, String end) {
		int originalCost = g.getCost(start, end);
		
		Map<String,String> parent = new HashMap<String,String>();
		Map<String,Boolean> visitados = new HashMap<String,Boolean>();
		Stack<String> pilha = new Stack<String>();
		pilha.push(start);
		parent.put(start, null);
		while(!pilha.empty()) {
			String u = pilha.pop();
			visitados.put(u, true);
			Iterable<String> adj = tree.adjacentTo(u);
			for(String v : adj) {
				if(!visitados.containsKey(v) || visitados.get(v) == false) {
					parent.put(v, u);
					if(v.equals(end)) {
						pilha.clear();
						break;
					}
					pilha.push(v);
				}
			}
		}
		
		//System.out.println("analisando ciclo de " + start + "-" + end);
		String u,v;
		for(v = end; v != start; v = parent.get(v)) {
			u = parent.get(v);
			int cost = tree.getCost(u, v);
			if(cost == originalCost) {
				return true;
			}
		}
		return false;
	}
}
