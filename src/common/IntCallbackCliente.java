package common;

import java.rmi.*;

public interface IntCallbackCliente extends Remote{
	/** Este método se invoca mediante callback
	 * de servidor, de forma que realiza un callback a 
	 * un cliente que implementa esta interfaz.
	 * 
	 * @param mensaje		cadena de caracteres que contiene información procesada por el cliente
	 * @throws java.rmi.RemoteException
	 */
	public void notificame(String mensaje) throws RemoteException;

} //fin interfaz
