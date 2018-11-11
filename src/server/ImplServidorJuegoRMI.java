package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import common.IntCallbackCliente;
//import common.IntCallbackCliente;
import common.IntServidorJuegoRMI;
import common.IntServidorPartidasRMI;

@SuppressWarnings("serial")
public class ImplServidorJuegoRMI extends UnicastRemoteObject implements IntServidorJuegoRMI {

	private static final long serialVersionUID = -2787665753856301145L;

	protected ImplServidorJuegoRMI() throws RemoteException {
		super();
	}

	@Override
	public IntServidorPartidasRMI nuevoServidorPartidas() throws RemoteException {
		return new ImplServidorPartidaRMI();
	}

	@Override
	public boolean proponPartida(String nombreJugador, IntCallbackCliente callbackClientObject) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean borraPartida(String nombreJugador) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String[] listaPartidas() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean aceptaPartida(String nombreJugador, String nombreRival) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}


}
