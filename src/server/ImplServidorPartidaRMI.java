package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import common.IntServidorPartidasRMI;

public class ImplServidorPartidaRMI extends UnicastRemoteObject implements IntServidorPartidasRMI {
	private static final long serialVersionUID = 2712848023629954136L;

	protected ImplServidorPartidaRMI() throws RemoteException {
		super();
	}
	private Partida partida;
	
	@Override
	public void nuevaPartida(int fila, int col, int barcos) {
		 this.partida = new Partida(fila, col, barcos);
	}

	@Override
	public int pruebaCasilla(int fila, int col) {
		return this.partida.pruebaCasilla(fila, col);
	}

	@Override
	public String getBarco(int id) {
		return this.partida.getBarco(id);
	}

	@Override
	public String[] getSolucion() {
		return this.partida.getSolucion();
	}

}