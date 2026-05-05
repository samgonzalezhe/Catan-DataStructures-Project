package logic;

import java.util.HashMap;
import java.util.Map;

public class Intercambio {

    private Jugador oferente;    
    private Jugador receptor;   
    private Map<Recurso, Integer> oferta;    
    private Map<Recurso, Integer> pedido;   
    private boolean aceptado;

    public Intercambio(Jugador oferente, Jugador receptor) {
        this.oferente = oferente;
        this.receptor = receptor;
        this.oferta = new HashMap<>();
        this.pedido = new HashMap<>();
        this.aceptado = false;
    }

    public boolean ejecutar() {
        // Verificar que ambos tienen los recursos
        if (!oferente.tieneRecursos(oferta) || !receptor.tieneRecursos(pedido)) {
            System.out.println("Intercambio fallido: recursos insuficientes");
            return false;
        }

        // Realizar intercambio
        oferente.gastarRecursos(oferta);
        receptor.gastarRecursos(pedido);

        for (Map.Entry<Recurso, Integer> e : oferta.entrySet()) {
            receptor.agregarRecurso(e.getKey(), e.getValue());
        }
        for (Map.Entry<Recurso, Integer> e : pedido.entrySet()) {
            oferente.agregarRecurso(e.getKey(), e.getValue());
        }

        this.aceptado = true;
        return true;
    }

    public void agregarOferta(Recurso r, int cantidad) { oferta.put(r, cantidad); }
    public void agregarPedido(Recurso r, int cantidad) { pedido.put(r, cantidad); }
    public Map<Recurso, Integer> getOferta() { return oferta; }
    public Map<Recurso, Integer> getPedido() { return pedido; }
    public Jugador getOferente() { return oferente; }
    public Jugador getReceptor() { return receptor; }
    public boolean isAceptado() { return aceptado; }
}
