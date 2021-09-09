package eppstein;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import util.Graph;

class Hindi {

	private Map<String,String> p;
	private Map<String,Integer> depth;
	private int nb;
	//private int n,bcsize, wordInList;
	//private int[] mlword,BC,findRow,constant,itemsBeforeRow,All1sIn,All1sBefore,shrItem;
	private int[] BC;
	private Map<String,Boolean> vis;
	private Graph g,b;
	private String nbroot;
	private Schieber schieber;
	private Map<String,Integer> wt;
	
	public Hindi(Graph g, Graph mst) {
		//this.n = mst.V();
		this.g = g;
		Borukva boru = new Borukva();
		this.b = boru.borukva(mst);
		this.nb = b.V();	
		this.nbroot = new String(boru.nbroot);
		schieber = new Schieber(b, nbroot);
		vis = new HashMap<String,Boolean>();
		depth = schieber.getLevel();
		p = schieber.getParents();
		wt = new HashMap<String,Integer>();
		Iterable<String> vertices = b.vertices();
		for(String v : vertices) {
			vis.put(v, false);
		}
		depth.put(nbroot, 0);
		//preProcessTables();
		int bcsize = (int) Math.ceil(log2(nb));
		BC = new int[bcsize];
		dfs(nbroot);
	}
	
	@SuppressWarnings("rawtypes")
	public void mostraAllwt() {
		Iterator it = wt.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String aresta = (String) pair.getKey();
            int maior_peso = (int) pair.getValue();
            System.out.println(aresta + " maior peso " + maior_peso);
        }
	}
	
	private double log2(int x) {
		return Math.log10(x)/Math.log10(2);
	}

	/*private void preProcessTables() {
		bcsize = (int) Math.ceil(log2(nb));
		int wordsize = (int) Math.ceil((log2(n)));
		int subwordsize = (int) Math.ceil((log2(log2(nb))));
		int subwordInWord = (int) Math.floor(wordsize/subwordsize);
		wordInList = (int) Math.ceil(bcsize/subwordInWord);
		mlword = new int[bcsize];
		BC = new int[bcsize];
		int c = (int) Math.floor(log2(n));
		findRow = new int[bcsize];
		constant = new int[bcsize];
		constant[1] = 1;
		while(--c > 0) {
			constant[1] = (constant[1] << subwordsize) + 1;
		}
		
		for(int k = 0; k < bcsize; k++) {
			findRow[k] = (int) Math.floor(k/subwordInWord);
			constant[k] = constant[1]*k;
		}

		itemsBeforeRow = new int[wordInList];
		for(int k = 0; k < wordInList; k++) {
			itemsBeforeRow[k] = k*subwordInWord;
		}

		All1sIn = new int[subwordInWord];
		All1sBefore = new int[subwordInWord];
		for(int k = 0; k < subwordInWord; k++) {
			int pot2 = (subwordInWord-k-1)*subwordsize;
			All1sIn[k] = ( (1 << subwordsize) - 1)*(1 << pot2);
			int pot3 = k*subwordsize;
			int pot4 = (subwordInWord-k)*subwordsize;
			All1sBefore[k] = ((1 << pot3) - 1)*(1 << pot4);
		}

		int max = (int) (log2(nb)*log2(log2(nb)));
		shrItem = new int[max];
		for(int x = 0; x < max; x++) {
			int aux = x;
			int word = (1 << subwordsize) - 1;
			while(aux > 0) {
				if((aux & word) > 0) break;
				aux >>= subwordsize;
			}
			shrItem[x] = aux;
		}
	}

	private int bin_search(int val, int i, int j) {
		if(j == -1) return 0;

		int meio = (i+j)/2;
		if(val == BC[meio]) {
			while(BC[meio] == val) meio++;
			return meio;
		} else if(i >= j) {
			if(val > BC[meio]) {
				return meio;
			}
			return meio+1;
		} else if(val > BC[meio]) {
			return bin_search(val,i,meio-1);
		} else {
			return bin_search(val,meio+1,j);
		}
	}

	private int ML(int k) {
		int i = findRow[k];
		int j = k - itemsBeforeRow[i];
		int aux = mlword[i] & All1sIn[j];
		return shrItem[aux];
	}

	private void assign(int val, int idx) {
		int i = findRow[idx];
		int j = idx - itemsBeforeRow[i];
		int x = mlword[i] & All1sBefore[j];
		int y = constant[val];
		int z = y & (~All1sBefore[j]);
		mlword[i] = x | z;
		for(int t = i+1; t < wordInList; t++) {
			mlword[t] = y;
		}
	}*/
	
	public int getWt(String u, String v) {
		if(u.compareTo(v) < 0) {
			String aux = u+"-"+v;
			if(wt.containsKey(aux))
				return wt.get(aux);
		} else {
			String aux = v+"-"+u;
			if(wt.containsKey(aux))
				return wt.get(aux);
		}
		return 0;
	}
	
	private void fillWt(String u, String v, int cost) {
		String aux;
		if(u.compareTo(v) < 0) {
			aux = u+"-"+v;
		} else {
			aux = v+"-"+u;
		}
		wt.put(aux, cost);
	}
	
	private int getMax(int start, int end) {
		int max = -1;
		for(int i = start; i <= end; i++)
			if(BC[i] > max)
				max = BC[i];
		return max;
	}

	private void dfs(String u) {
		vis.put(u, true);
		int d = depth.get(u);

		if(!u.equals(nbroot)) {
			BC[d-1] = b.getCost(u, p.get(u));
			if(g.hasVertex(u)) {
				Iterable<String> adjInGraph = g.adjacentTo(u);
				for(String v : adjInGraph) {				
					int x = getMax(depth.get(schieber.queryLCA(u,v)),d-1);
					int wtcost = getWt(u,v);
					if(wtcost == 0)
						fillWt(u,v,x);
					else if(x > wtcost)
						fillWt(u,v,x);
				}
			}
		}
		
		Iterable<String> adj = b.adjacentTo(u);
		for(String v : adj) {
			if(vis.get(v) == false) {
				dfs(v);
			}
		}
	}
}
