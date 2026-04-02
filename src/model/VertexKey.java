package com.mycompany.catan;

public class VertexKey {

    private int x;
    private int y;

    public VertexKey(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VertexKey)) return false;
        VertexKey other = (VertexKey) o;
        return x == other.x && y == other.y;
    }

    @Override
    public int hashCode() {
        return x * 31 + y;
    }
}