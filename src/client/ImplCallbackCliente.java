package client;

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import common.IntCallbackCliente;

public class ImplCallbackCliente  extends UnicastRemoteObject implements IntCallbackCliente{
	private static final long serialVersionUID = 1L;

	/**
	 * Esta clase implementa la interfaz remota IntCallbackCliente.
	 * @author M. L. Liu 
	 */
	public ImplCallbackCliente() throws RemoteException{
		super();
	}
	
	/**
	 * Método que muestra el mensaje.
	 */
	@Override
	public void notificame(String mensaje) throws RemoteException {
		System.out.println(mensaje);
	}
}//fin clase Callback
