package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import common.IntCallbackCliente;
import common.IntServidorJuegoRMI;
import common.IntServidorPartidasRMI;


public class ClienteFlotaRMI {
	public static final int NUMFILAS=8, NUMCOLUMNAS=8, NUMBARCOS=6; //	Parametros por defecto de una partida
	static IntCallbackCliente callback;								// Instancia del callback
	private static IntServidorJuegoRMI juego;						// Instancia del stub juego
	private GuiTablero guiTablero = null;				// El juego se encarga de crear y modificar la interfaz grafica
	private static IntServidorPartidasRMI partida;     				// Stub de la partida
	private String nombre;
	private int quedan = NUMBARCOS, disparos = 0; //Atributos de la partida guardados en el juego para simplificar su implementacion
	Scanner sc;
	
	public static void main(String args[]) {
        try {
            String registryURL = "rmi://localhost:1099/flota";
            // busca el objeto remoto y lo castea a un objeto de la interfaz
            juego = (IntServidorJuegoRMI) Naming.lookup(registryURL);

            System.out.println("Lookup completed ");
            //invoca el metodo remoto
            partida = juego.nuevoServidorPartidas();
            ClienteFlotaRMI cliente = new ClienteFlotaRMI();
            cliente.partida=partida;
            cliente.ejecuta();
            callback = new ImplCallbackCliente();
        } // end try
        catch (Exception e) {
            System.out.println("Exception in Client: " + e);
        }
    } //end main


	/**
	 * Lanza una nueva hebra que crea la primera partida y dibuja la interfaz grafica: tablero
	 */
	private void ejecuta() {
		/**
		 *  Instancia la partida de un jugador y pide el nombre del jugador
		 */
		try {
			partida.nuevaPartida(NUMFILAS, NUMCOLUMNAS, NUMBARCOS);
			System.out.print("Indique su nombre de jugador: ");
		    sc = new Scanner(System.in);
		    nombre = sc.nextLine();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				guiTablero = new GuiTablero(NUMFILAS, NUMCOLUMNAS);
				guiTablero.dibujaTablero();
			}
		});
	} // end ejecuta

	/******************************************************************************************/
	/*********************  CLASE INTERNA GuiTablero   ****************************************/
	/******************************************************************************************/
	private class GuiTablero {

		private int numFilas, numColumnas;

		private JFrame frame = null;        // Tablero de juego
		private JLabel estado = null;       // Texto en el panel de estado
		private JButton buttons[][] = null; // Botones asociados a las casillas de la partida

		/**
         * Constructor de una tablero dadas sus dimensiones y el nombre del jugador
         */
		GuiTablero(int numFilas, int numColumnas) {
			this.numFilas = numFilas;
			this.numColumnas = numColumnas;
			frame = new JFrame();
 			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setTitle(nombre);
		}
		/**
		 * Dibuja el tablero de juego y crea la partida inicial
		 */
		public void dibujaTablero() {
			anyadeMenu();
			anyadeGrid(numFilas, numColumnas);		
			anyadePanelEstado("Intentos: " + disparos + "    Barcos restantes: " + quedan);		
			frame.setSize(300, 300);
			frame.setVisible(true);	
		} // end dibujaTablero

		/**
		 * Anyade el menu de opciones del juego y le asocia un escuchador
		 */
		private void anyadeMenu() {
			//Creacion de opciones del menu
            JMenuBar menu = new JMenuBar();	
            JMenu opciones = new JMenu("Opciones");
            JMenuItem solucion = new JMenuItem("Mostrar solucion");
            JMenuItem nueva = new JMenuItem("Nueva partida");
            JMenuItem salir = new JMenuItem("Salir");
            
            //Llamada al escuchador con cada opcion del menu
            MenuListener escuchador = new MenuListener();
            solucion.addActionListener(escuchador);
            nueva.addActionListener(escuchador);
            salir.addActionListener(escuchador);
            
            //Anyade las opciones a la ventana opciones
            opciones.add(solucion);
            opciones.add(nueva);
            opciones.add(salir);
            menu.add(opciones);
            
            //Creacion de opciones de multijugador
            JMenu multijugador = new JMenu("Multijugador");
            JMenuItem propon = new JMenuItem("Proponer partida");
            JMenuItem borrar = new JMenuItem("Borrar propuesta");
            JMenuItem listar = new JMenuItem("Listar partidas");
            JMenuItem acepta = new JMenuItem("Aceptar partida");
            
            //Llamada al escuchador con cada opcion del multijugador
            propon.addActionListener(escuchador);
            borrar.addActionListener(escuchador);
            listar.addActionListener(escuchador);
            acepta.addActionListener(escuchador);
            
            // Anyade las opciones de la ventana multijugador
            multijugador.add(propon);
            multijugador.add(borrar);
            multijugador.add(listar);
            multijugador.add(acepta);
            menu.add(multijugador);
            
            frame.add(menu, BorderLayout.NORTH);
		} // end anyadeMenu

		/**
		 * Anyade el panel con las casillas del mar y sus etiquetas.
		 * Cada casilla sera un boton con su correspondiente escuchador
		 * @param nf	numero de filas
		 * @param nc	numero de columnas
		 */
		private void anyadeGrid(int nf, int nc) {
			JPanel cuadr = new JPanel();
			GridLayout experimentLayout = new GridLayout(nf+1,nc+2);
			ButtonListener escuchador = new ButtonListener();
			JLabel label;
			buttons = new JButton[nf][nc];
			cuadr.setLayout(experimentLayout);
			
			//Recorre la matriz
			for(int i=0; i<nf+1; i++){
				for(int j=0; j<nc+2; j++){
					if((i==0 && j==0)||(i==0 && j==nc+1))	
						//Anade espacio en blanco en la primera y ultima casilla de la primera fila
						cuadr.add(label = new JLabel("   "));
					else if(i==0 && j >=1)	
						//Numera las columnas en la primera fila
						cuadr.add(label = new JLabel("   "+j));
					else if(j==0 || j==nc+1)	
						//Numera mediante letras las filas al principio y al final
						cuadr.add(label = new JLabel("   "+Character.toString((char)('A'+i-1))));
					else{
						//Crea los botones y sus escuchadores
						JButton boton = new JButton();
						boton.putClientProperty("fila", i-1);
						boton.putClientProperty("columna", j-1);
						boton.addActionListener(escuchador);
						buttons[i-1][j-1] = boton;
						cuadr.add(boton);
					}
				}
			}
			frame.add(cuadr);
		} // end anyadeGrid


		/**
		 * Anyade el panel de estado al tablero
		 * @param cadena	cadena inicial del panel de estado
		 */
		private void anyadePanelEstado(String cadena) {	
			JPanel panelEstado = new JPanel();
			estado = new JLabel(cadena);
			panelEstado.add(estado);
			// El panel de estado queda en la posici√≥n SOUTH del frame
			frame.getContentPane().add(panelEstado, BorderLayout.SOUTH);
		} // end anyadePanel Estado

		/**
		 * Cambia la cadena mostrada en el panel de estado
		 * @param cadenaEstado	nuevo estado
		 */
		public void cambiaEstado(String cadenaEstado) {
			estado.setText(cadenaEstado);
		} // end cambiaEstado

		/**
		 * Muestra la solucion de la partida y marca la partida como finalizada
		 */
		public void muestraSolucion() {
			//Pinta cada casilla del color correspondiente
			quedan=0;
			for (int i = 0; i < numFilas; i++) {
				for (int j = 0; j < numColumnas; j++){
					int toque = -5;
					try {
						toque = partida.pruebaCasilla(i, j);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
					if(toque==-1) {
						guiTablero.pintaBoton(guiTablero.buttons[i][j],Color.cyan);
					}else if(toque!=-5){
						guiTablero.pintaBoton(guiTablero.buttons[i][j],Color.magenta);
					}
				}
			}
		} // end muestraSolucion


		/**
		 * Pinta un barco como hundido en el tablero
		 * @param cadenaBarco	cadena con los datos del barco codifificados como
		 *                      "filaInicial#columnaInicial#orientacion#tamanyo"
		 */
		public void pintaBarcoHundido(String cadenaBarco) {
			String[] parts = cadenaBarco.split("#");
			int filaInicial = Integer.parseInt(parts[0]);
			int columnaInicial = Integer.parseInt(parts[1]);     
			String orientacion = parts[2];     
			int tamanyo = Integer.parseInt(parts[3]);  

			//Pinta el barco entero segun su posicion
			if (orientacion.equals("V")){
				for (int i=filaInicial; i<tamanyo+filaInicial; i++)
					pintaBoton(buttons[i][columnaInicial], Color.red);
			}
			if (orientacion.equals("H")){
				for (int i=columnaInicial; i<tamanyo+columnaInicial; i++)
					pintaBoton(buttons[filaInicial][i], Color.red);
			}
		} // end pintaBarcoHundido

		/**
		 * Pinta un boton de un color dado
		 * @param b			boton a pintar
		 * @param color		color a usar
		 */
		public void pintaBoton(JButton b, Color color) {
			b.setBackground(color);
			// El siguiente c√≥digo solo es necesario en Mac OS X
			b.setOpaque(true);
			b.setBorderPainted(false);
		} // end pintaBoton

		/**
		 * Limpia las casillas del tablero pintandolas del gris por defecto
		 */
		public void limpiaTablero() {
			for (int i = 0; i < numFilas; i++) {
				for (int j = 0; j < numColumnas; j++) {
					buttons[i][j].setBackground(null);
					buttons[i][j].setOpaque(true);
					buttons[i][j].setBorderPainted(true);
				}
			}
		} // end limpiaTablero

		/**
		 * 	Destruye y libera la memoria de todos los componentes del frame
		 */
		public void liberaRecursos() {
			frame.dispose();
		} // end liberaRecursos
	} // end class GuiTablero

	/******************************************************************************************/
	/*********************  CLASE INTERNA MenuListener ****************************************/
	/******************************************************************************************/

	/**
	 * Clase interna que escucha el menu de Opciones y Multijugador del tablero
	 * 
	 */
	private class MenuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			switch(e.getActionCommand()){
			case "Salir": 
				try {
					juego.borraPartida(nombre);
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
				guiTablero.liberaRecursos();
				break;
				
			case "Mostrar solucion":
				guiTablero.muestraSolucion();
				break;
				
			case "Nueva partida":
				guiTablero.limpiaTablero();
				try {
					partida.nuevaPartida(NUMFILAS, NUMCOLUMNAS, NUMBARCOS);
				} catch (RemoteException ex) {
					ex.printStackTrace();
				}				
				quedan = NUMBARCOS; disparos = 0;
				guiTablero.cambiaEstado("Intentos: " + disparos + "    Barcos restantes: " + quedan);
				break;		
				
				
			case "Proponer partida":
				try {
					if(juego.proponPartida(nombre, callback)){
						System.out.println("Partida propuesta con Èxito!");
					}else{
						System.out.println("Ha ocurrido un error al proponer una partida.");
					}
				} catch (RemoteException ex) {
					ex.printStackTrace();
				}
				break;
				
			case "Borrar propuesta":
				try {
					if(juego.borraPartida(nombre)){
						System.out.println("Partida borrada con Èxito");
					}else{
						System.out.println("No tienes partidas propuestas.");
					}
				} catch (RemoteException ex) {
					ex.printStackTrace();
				}
				break;
				
			case "Listar partidas":
				try {
					String[] listado = juego.listaPartidas();
					if(listado.length > 0){
						System.out.println("Partidas propuestas: ");
						for (int i=0; i<listado.length; i++){
							System.out.println('\t' + listado[i]);
						}
					}else{
						System.out.println("No hay partidas propuestas.");
					}
				} catch (RemoteException ex) {
					ex.printStackTrace();
				}
				break;
				
			case "Aceptar partida":		// Pide el nombre del rival e intenta aceptar la partida
				String nombreRival;
				System.out.print("Indique el nombre del rival: ");
				sc = new Scanner(System.in);
				nombreRival = sc.nextLine();
				try {
					if (nombre.equals(nombreRival)){
						System.out.println("No puedes aceptar tu propia partida.");
					}
					else if(juego.aceptaPartida(nombre, nombreRival)){
						System.out.println("La partida se ha establecido con exito");
					}else{
						System.out.println("Ha ocurrido un error al aceptar la partida.");
					}
				} catch (RemoteException ex) {
					ex.printStackTrace();
				}
				break;
				
			default:
				break;
			} //end switch
			
		} //end actionPerformed
	} // end class MenuListener



	/******************************************************************************************/
	/*********************  CLASE INTERNA ButtonListener **************************************/
	/******************************************************************************************/
	/**
	 * Clase interna que escucha cada uno de los botones del tablero
	 * Para poder identificar el boton que ha generado el evento se pueden usar las propiedades
	 * de los componentes, apoyandose en los metodos putClientProperty y getClientProperty
	 */
	private class ButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			int i=(int)((JButton)e.getSource()).getClientProperty("fila"), j=(int)((JButton)e.getSource()).getClientProperty("columna");
			//Si la partida no ha terminado
			if(quedan!=0){
				//En el caso de que el boton no este pintado todavia
				if(!guiTablero.buttons[i][j].getBackground().equals(Color.yellow) &&
						!guiTablero.buttons[i][j].getBackground().equals(Color.red)){
					int toque = -5;
					try {
						toque = partida.pruebaCasilla(i,j);
					} catch (RemoteException e1) {
						e1.printStackTrace();
					}
					switch(toque){
						case -1:	//AGUA
							guiTablero.pintaBoton(guiTablero.buttons[i][j],Color.cyan);
							break;
						case -2:	//TOCADO
							guiTablero.pintaBoton(guiTablero.buttons[i][j],Color.yellow);
							break;
						case -5:
							break;
						default:	//HUNDIDO
							quedan--;
						try {
							guiTablero.pintaBarcoHundido(partida.getBarco(toque));
						} catch (RemoteException e1) {
							e1.printStackTrace();
						}
							break;
					} //end switch
				}
				disparos++;
				//La partida ha terminado, todos los barcos estan hundidos
				if(quedan==0)
					guiTablero.cambiaEstado("GAME OVER en " + disparos + " disparos");

				else
					guiTablero.cambiaEstado("Intentos: " + disparos + "    Barcos restantes: " + quedan);
			} //end if no quedan barcos
			
        } // end actionPerformed

	} // end class ButtonListener

	

}//end class


