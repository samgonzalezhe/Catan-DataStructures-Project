package com.mycompany.catan;

public class HexCoord {
    int q; // columna
    int r; // fila

    public HexCoord(int q, int r) {
        this.q = q;
        this.r = r;
    }

    @Override
    public String toString() {
        return "(" + q + ", " + r + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HexCoord)) return false;
        HexCoord other = (HexCoord) o;
        return this.q == other.q && this.r == other.r;
    }

    @Override
    public int hashCode() {
        return q * 31 + r;
    }
}
