package shioura;

class Edge {

    public int u, v;

    public Edge(int u, int v) {
        if (u < v) {
            this.u = u;
            this.v = v;
        } else {
            this.u = v;
            this.v = u;
        }
    }

    @Override
    public boolean equals(Object obj) {
        Edge f = (Edge) obj;
        return this.u == f.u && this.v == f.v;
    }

    @Override
    public String toString() {
        return u + "-" + v;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

}
