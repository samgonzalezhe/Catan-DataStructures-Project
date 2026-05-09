package logic.bot;

import logic.Jugador;
import model.MapaCatan;
import logic.GestorTurnos;
import model.Vertice;
import model.Arista;

import java.util.List;

public class EstadoBot {
    public Jugador jugador;
    public MapaCatan mapa;
    public GestorTurnos gestor;
    public List<Vertice> vertices;
    public List<Arista> aristas;
    public int ultimoDado;
    public boolean dadoLanzado;
    public boolean turnoTerminado;

    public EstadoBot(Jugador jugador, MapaCatan mapa, GestorTurnos gestor,
                     List<Vertice> vertices, List<Arista> aristas) {
        this.jugador = jugador;
        this.mapa = mapa;
        this.gestor = gestor;
        this.vertices = vertices;
        this.aristas = aristas;
        this.dadoLanzado = false;
        this.turnoTerminado = false;
    }
}
