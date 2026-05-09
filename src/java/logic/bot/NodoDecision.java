package logic.bot;

public abstract class NodoDecision {
    protected NodoDecision hijoSi;
    protected NodoDecision hijoNo;

    public NodoDecision(NodoDecision hijoSi, NodoDecision hijoNo) {
        this.hijoSi = hijoSi;
        this.hijoNo = hijoNo;
    }

    public abstract NodoDecision evaluar(EstadoBot estado);
}
