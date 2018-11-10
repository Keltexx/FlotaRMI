package common;

public interface IntServidorPartidasRMI {
	
	/**
	 * 
	 * @param fila
	 * @param col
	 * @param barcos
	 * @return Partida
	 */
	public Partida nuevaPartida (int fila, int col, int barcos);
	
	/**
	 * 
	 * @param fila
	 * @param col
	 * @return 
	 */
	public pruebaCasilla(int fila, int col); //TODO: llama a la funcion pruebaCasilla del objeto Partida y devulve el resultado
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public getBarco(int id); //TODO: llamar a la funcion getBarco del objeto Partida y devuelve el resultado
	
	/**
	 * 
	 * @return 
	 */
	public getSolucion(); //TODO: llama a la funcion getSolucion del objeto Partida y devulve el resultado
}
