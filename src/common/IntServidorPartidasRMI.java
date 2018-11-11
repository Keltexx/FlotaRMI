package common;

public interface IntServidorPartidasRMI {
	
	/**
	 *  Dado el número de filas y columnas del tablero y el número de barcos,
	 *  instancia un nuevo objeto de la clase Partida
	 *  
	 * @param fila
	 * @param col
	 * @param barcos
	 */
	public void nuevaPartida (int fila, int col, int barcos);
	
	/**
	 *  Dada la fila y columna de una casilla, llama a la función del mismo
	 *  nombre del objeto Partida y devuelve el resultado obtenido
	 *  
	 * @param	fila	fila de la casilla
	 * @param   col   columna de la casilla
	 * @return   resultado de marcar la casilla: AGUA, ya TOCADO, ya HUNDIDO, identidad del barco recien hundido
	 */
	public int pruebaCasilla(int fila, int col);
	
	/**
	 * Dado el identificador de un barco, llama a la función del mismo nombre del 
	 * objeto Partida y devuelve el resultado obtenido
	 * 
	 * @param	idBarco	indice del barco en el vector de barcos
	 * @return	        cadena con los datos del barco
	 */
	public String getBarco(int id); //TODO: llamar a la funcion getBarco del objeto Partida y devuelve el resultado
	
	/**
	 * Llama a la funci´on del mismo nombre del objeto Partida y devuelve el
	 * resultado obtenido
	 * 
	 * @return	vector de cadenas, una por barco con la informacion de getBarco
	 */
	public String[] getSolucion(); //TODO: llama a la funcion getSolucion del objeto Partida y devulve el resultado
}
