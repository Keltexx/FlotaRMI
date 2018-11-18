package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

import javax.swing.text.html.HTMLDocument.Iterator;

import common.IntCallbackCliente;
//import common.IntCallbackCliente;
import common.IntServidorJuegoRMI;
import common.IntServidorPartidasRMI;

public class ImplServidorJuegoRMI extends UnicastRemoteObject implements IntServidorJuegoRMI {
	private static final long serialVersionUID = -2787665753856301145L;
	private HashMap<String, IntCallbackCliente> estructura;
	
	protected ImplServidorJuegoRMI() throws RemoteException {
		super();
		estructura = new HashMap<String, IntCallbackCliente>();
	}
	
	/**
	 * Crea una nueva partida
	 * 
	 * @return referencia a la interfaz remota que permite jugar la partida
	 * @throws RemoteException
	 */
	@Override
	public IntServidorPartidasRMI nuevoServidorPartidas() throws RemoteException {
		return new ImplServidorPartidaRMI();
	}

	/**
	 * Permite al cliente proponer una partida y registrarte para recibir un callback cuando otro cliente acepte,
	 * se añade al jugador en un mapa
	 * 
	 * @param nombreJugador 				jugador que propone la partida
	 * @param callbackClienteObject 		referencia al objeto del jugador que usara el servidor para acer el callback
	 * @throws RemoteException
	 * @return	devuelve el valor logico si se ha añadido el jugador.
	 */
	@Override
	public synchronized boolean proponPartida(String nombreJugador, IntCallbackCliente callbackClientObject) throws RemoteException {
		System.out.println("El jugador " + nombreJugador + " propone una aprtida.");
		if(!estructura.containsKey(nombreJugador)){
			estructura.put(nombreJugador, callbackClientObject);
			return true;
		}
		return false;
	}

	/**
	 * Permite al cliente eliminar su registro para callback
	 * 
	 * @param nombreJugador 	jugador que propuso la partida
	 * @throws RemoteException
	 * @return devuelve el valor logico si se ha borrado el jugador.
	 */
	@Override
	public boolean borraPartida(String nombreJugador) throws RemoteException {
		System.out.println("El jugador " + nombreJugador + "ha borrado su partida.");
		if(estructura.containsKey(nombreJugador)){
			estructura.remove(nombreJugador);
			return true;
		}
		return false;
	}

	/**
	 * Lista las partidas propuestas
	 * 
	 * @throws RemoteException
	 * @return vector con los nombres de los jugadores que han propuesto partidas
	 */
	@Override
	public String[] listaPartidas() throws RemoteException {
		System.out.println("Listado de partidas propuestas:");
		String[] lista = new String[0];
			
		if(estructura.isEmpty())
			return lista;
		//TODO
		
		return lista;
	}

	/**
	 * Acepta jugar una partida ya propuesta
	 * 
	 * @param nombreJugador 	nombre del jugador que acepta la partida
	 * @param nombreRival	 	nombre del jugador que propuso la partida
	 * @throws RemoteException 
	 * @return devuelve el valor logico indicado si se ha aceptado la partida
	 */
	@Override
	public boolean aceptaPartida(String nombreJugador, String nombreRival) throws RemoteException {
		System.out.println("El jugador " + nombreJugador + " solicita acpetar la partida de " + nombreRival + ".");
		if (estructura.containsKey(nombreRival)){
			try{
				IntCallbackCliente cli = estructura.remove(nombreRival);
				cli.notificame(nombreJugador + "ha aceptado tu partida");
				return true;
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return false;
	}
}
