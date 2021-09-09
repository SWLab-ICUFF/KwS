package eppstein;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import util.Graph;

public class Eppstein {
	private Aresta[] A;
	private Graph mst,eg;
	private String root;
	private Map<String,Boolean> vis;
	private Hindi hindi;
	private Map<Aresta,Aresta> subs;
	public Set<Aresta> extras = new HashSet<Aresta>();
	
	public Eppstein(Graph g, Graph mst, String root) {
		eg = new Graph(g);		
		this.mst = mst;
		this.root = root;
		hindi = new Hindi(g, mst);
		vis = new HashMap<String,Boolean>();
		subs = new HashMap<Aresta,Aresta>();
		clearVis();
		//removeUseLessEdges();
		new Removedor(g, mst, eg);
		clearVis();
		A = new Aresta[g.V()];
		sliding(root,0);
	}
	
	public Graph getEG() {
		return eg;
	}
	
	@SuppressWarnings("rawtypes")
	public void convertTreesBack(List<Graph> allmsts) {
		for(Graph t : allmsts) {
			Iterator it = subs.entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
                Aresta e = (Aresta) pair.getKey();
                Aresta f = (Aresta) pair.getValue();
                if(t.hasEdge(e.u, e.v)) {
                	t.removeEdgeBoth(e.u, e.v);
                	t.addEdge(f.u, f.v, f.custo);
                }
			}
		}
	}
	
	private void clearVis() {
		vis.clear();
		Iterable<String> vertices = eg.vertices();
		for(String u : vertices) {
			vis.put(u, false);
		}
	}
	
	private void storeSub(String p, String v, String w, int cost) {
		subs.put(new Aresta(p,w,cost), new Aresta(v,w,cost));
	}
	
	private int epps_bin_search(int value, int start, int end) {
		int middle = (start + end)/2;
	    if(start > end) {
	        return start;
	    } else if(A[middle].custo < value) {
	        return epps_bin_search(value,start,middle-1);
	    } else {
	        return epps_bin_search(value,middle+1,end);
	    }
	}

	@SuppressWarnings("rawtypes")
	private void slideOneEdge(String u, String v, int l) {
		Map<String,Integer> adj = new HashMap<String,Integer>(eg.adjacentTo(v,true));
		Iterator it = adj.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String w = (String) pair.getKey();
            int cost = (int) pair.getValue();
            if(!w.equals(u)) {
            	int heavy = epps_bin_search(cost,0,l)-1;
                String p = root;
                if(heavy > -1) {
                    p = A[heavy].v;
                }
                if(p.equals(v)) 
                	continue;
                //System.out.println("sliding " + v + "-" + w + "  com " + p + "-" + w);
                eg.removeEdgeBoth(v,w);
                remakeEdge(p,w,cost); 
                storeSub(p,v,w,cost);
	        }
        }
        
	}

	private void remakeEdge(String a, String b, int cost) {
		if(eg.getCost(a, b) == -1) 
	        eg.addEdge(a, b, cost);
	    else {
	    	int outro_custo = eg.getCost(a, b);
	    	eg.removeEdgeBoth(a, b);
	    	eg.addEdge(a, b, Math.min(outro_custo, cost));
	    	System.out.println("remake da aresta " + a + "-" + b);
	    	extras.add(new Aresta(a,b,outro_custo));
	    }
	}

	private int Afillbinsearch(int value, int start, int end) {
	    int middle = (start + end)/2;
	     if(A[middle].custo == value) {
	    	while(middle >= 0 && A[middle].custo == value) 
	    		middle--;
	    	return middle+1;
	    } else if(start > end) {
	    	return start;
	    } else if(A[middle].custo < value) {
	        return Afillbinsearch(value,start,middle-1);
	    } else {
	        return Afillbinsearch(value,middle+1,end);
	    }
	}

	@SuppressWarnings("rawtypes")
	private void sliding(String u, int l) {
	    vis.put(u, true);

	    Map<String,Integer> adj = new HashMap<String,Integer>(mst.adjacentTo(u,true));
		
		Iterator it = adj.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String v = (String) pair.getKey();
            int cost = (int) pair.getValue();
	        if(vis.get(v) == false) {
	        	if(!u.equals(root)) {
	        		//System.out.println("\nAresta " + u + "-" + v);
	        		//mostraA(l);
		            int novo_l = Afillbinsearch(cost,0,l);
		            //System.out.println("novo l = " + novo_l);
		            Aresta antigo_al = A[novo_l];    
		            Aresta nova = new Aresta(u,v,cost);
		            A[novo_l] = nova;
		            sliding(v,novo_l);
		            slideOneEdge(u,v,novo_l);
		            A[novo_l] = antigo_al;
	        	} else {
	        		Aresta nova = new Aresta(u,v,cost);
	        		//System.out.println("Aresta zero, " + nova.u + "-" + nova.v);
	        		A[0] = nova;
	        		sliding(v,0);
	        		slideOneEdge(u,v,0);
	        	}
	        }
	    }
	}

	@SuppressWarnings({ "rawtypes", "unused" })
	private void removeUseLessEdges() {
		List<Aresta> aremover = new ArrayList<Aresta>();
		Queue<String> q = new LinkedBlockingQueue<String>();
		q.add(root);

		while(!q.isEmpty()) {
			String u = q.poll();
			vis.put(u, true);
			Map<String,Integer> adj = new HashMap<String,Integer>(eg.adjacentTo(u,true));
			Iterator it = adj.entrySet().iterator();
	        while(it.hasNext()) {
	            Map.Entry pair = (Map.Entry) it.next();
	            String v = (String) pair.getKey();
	            int cost = (int) pair.getValue();
	            if(vis.get(v) == false) {
	            	if(!mst.hasEdge(u, v) && hindi.getWt(u, v) != cost) {
	            		aremover.add(new Aresta(u,v,1));
					} else {
						if(!mst.hasEdge(u, v))
							System.out.println("ficou " + u + "-" + v);
					}
	            	q.add(v);
	            }

	        }
		}
		int counter = 0;
		for(Aresta a : aremover) {
			eg.removeEdgeBoth(a.u, a.v);
			counter++;
		}
		System.out.println("removeu " + counter + " arestas");
	}
	
}
