package shioura;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eppstein.Aresta;
import util.Graph;

public class Shioura {
	private LinkedList<Edge> leaves;
	private Map<Edge,LinkedList<Edge>> candi;
	private _Aux aux;
	private List<Graph> alltrees;
	private Set<Aresta> extras;
	public int total;
        
	
	public Shioura(Graph grafo, String root) {
		aux = new _Aux();
		total = 0;
		alltrees = new LinkedList<Graph>();
		leaves = aux.mountTree(grafo,root);
		mountCandi(aux.getNonTreeEdges(grafo,root));
		alltrees.add(aux.getActualTreeCopy());
		//this.extras = extras;
		//total += getfator();
		findChildren();
	}
	
	public List<Graph> getAllTrees() {
		return alltrees;
	}
	
	private void ordenaCandi(LinkedList<Edge> list) {
		for(int i = 1; i < list.size(); i++) {
			Edge eleito = list.get(i);
			int j = i-1;
			while(j >= 0 && eleito.u < list.get(j).u) {
				list.set(j+1,list.get(j));
				j = j-1;
			}
			list.set(j+1,eleito);
		}
	}

	private void mountCandi(LinkedList<Edge> nonTreeEdges) {
		LinkedList<Edge> treeEdges = aux.getTreeEdges();
		candi = new HashMap<Edge,LinkedList<Edge>>();
		
		for(Edge e : treeEdges) {
			LinkedList<Edge> list = new LinkedList<Edge>();
			candi.put(e, list);
			for(Edge f : nonTreeEdges) {
				if(e.v == f.v && e.u >= f.u) {
					list.addFirst(f);
				}
			}
			if(list.isEmpty()) 
				leaves.remove(e);
			else
				ordenaCandi(list);
		}
	}
	
	private int getfator() {
		int fator = 1;
		for(Aresta a : extras) {
			if(aux.hasEdge(a.u, a.v)) {
				fator *= 2;
			}
		}
		return fator;
	}

	
	private void findChildren() {
		if(leaves.size() == 0) 	return;
		LinkedList<Edge> queue = new LinkedList<Edge>();
		Edge ek = leaves.removeLast();
		LinkedList<Edge> candi_ek = candi.get(ek);
		while(!candi_ek.isEmpty()) {
			Edge g = candi_ek.removeLast();
			queue.addFirst(g);
			aux.changeEdge(ek, g);
			alltrees.add(aux.getActualTreeCopy());
			//total += getfator();
			subchild(ek, g);
			aux.changeEdge(g, ek);
		}
		candi_ek.addAll(queue);
		subchild(ek, ek);
		leaves.addLast(ek);
	}
	
	private void insertIntoLeave(Edge f) {
		for(int i = 0; i < leaves.size(); i++) {
			Edge e = leaves.get(i);
			if(e.u > f.u) {
				leaves.add(i, f);
				return;
			}
		}
		leaves.addLast(f);
	}
	
	private void mergeSIntoCandiF(LinkedList<Edge> candi_f, LinkedList<Edge> s) {
		for(Edge f : s) {
			boolean hasPut = false;
			for(int i = 0; i < candi_f.size(); i++) {
				Edge e = candi_f.get(i);
				if(e.u > f.u) {
					candi_f.add(i, f);
					hasPut = true;
					break;
				}
			}
			if(!hasPut)
				candi_f.addLast(f);
		}
	}
	
	private void subchild(Edge ek, Edge g) {
		LinkedList<Edge> candi_ek = candi.get(ek);
		if(candi_ek.isEmpty() || g.u <= candi_ek.getFirst().u) {
			findChildren();
			return;
		}
		
		Edge f = aux.getF(g.u);
		LinkedList<Edge> candi_f = candi.get(f);
		if(!candi_f.isEmpty()) {
			LinkedList<Edge> s = new LinkedList<Edge>();
			for(Edge e : candi_ek) {
				if(e.u < g.u) {
					s.addLast(e);
				} else {
					break;
				}
			}
			mergeSIntoCandiF(candi_f, s);
			findChildren();
			candi_f.removeAll(s);
			s.clear();
		} else {
			for(Edge e : candi_ek) {
				if(e.u < g.u) {
					candi_f.addLast(e);
				} else {
					break;
				}
			}
			insertIntoLeave(f);
			findChildren();
			leaves.remove(f);
			candi_f.clear();
		}
	}
}
