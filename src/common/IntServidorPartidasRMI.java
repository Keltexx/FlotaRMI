package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IntServidorPartidasRMI extends Remote{
	
	/**
	 *  Dado el número de filas y columnas del tablero y el número de barcos,
	 *  instancia un nuevo objeto de la clase Partida
	 *  
	 * @param fila
	 * @param col
	 * @param barcos
	 */
	public void nuevaPartida (int fila, int col, int barcos) throws RemoteException;
	
	/**
	 *  Dada la fila y columna de una casilla, llama a la función del mismo
	 *  nombre del objeto Partida y devuelve el resultado obtenido
	 *  
	 * @param	fila	fila de la casilla
	 * @param   col   columna de la casilla
	 * @return   resultado de marcar la casilla: AGUA, ya TOCADO, ya HUNDIDO, identidad del barco recien hundido
	 */
	public int pruebaCasilla(int fila, int col) throws RemoteException;
	
	/**
	 * Dado el identificador de un barco, llama a la función del mismo nombre del 
	 * objeto Partida y devuelve el resultado obtenido
	 * 
	 * @param	idBarco	indice del barco en el vector de barcos
	 * @return	        cadena con los datos del barco
	 */
	public String getBarco(int id) throws RemoteException;
	
	/**
	 * Llama a la funcion del mismo nombre del objeto Partida y devuelve el
	 * resultado obtenido
	 * 
	 * @return	vector de cadenas, una por barco con la informacion de getBarco
	 */
	public String[] getSolucion() throws RemoteException;
}
