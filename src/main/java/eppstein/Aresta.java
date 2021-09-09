package eppstein;

public class Aresta {
	public String u,v;
	public int custo;
	
	public Aresta(String u, String v, int cost) {
		if(u.hashCode() < v.hashCode()) {
			this.u = u;
			this.v = v;
		} else {
			this.u = v;
			this.v = u;
		}
		this.custo = cost;
	}
	
	@Override
	public String toString() {
		return u+"-"+v;
	}
	
	@Override
	public boolean equals(Object obj) {
		Aresta f = (Aresta) obj;
		return this.u.equals(f.u) && this.v.equals(f.v);
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
}
