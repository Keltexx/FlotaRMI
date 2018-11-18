package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import common.IntServidorJuegoRMI;
import common.IntServidorPartidasRMI;


public class ClienteFlotaRMI {
	
	/** Parametros por defecto de una partida */
	public static final int NUMFILAS=8, NUMCOLUMNAS=8, NUMBARCOS=6;
	
	private GuiTablero guiTablero = null;			// El juego se encarga de crear y modificar la interfaz gráfica
	private IntServidorPartidasRMI partida = null;                 // Objeto con los datos de la partida en juego
	
	/** Atributos de la partida guardados en el juego para simplificar su implementación */
	private int quedan = NUMBARCOS, disparos = 0;
	
	public static void main(String args[]) {
        try {
            int RMIPort;
            String hostName;
            InputStreamReader is = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(is);
            System.out.println("Enter the RMIRegistry host name:");
            hostName = br.readLine();
            System.out.println("Enter the RMIregistry port number:");
            String portNum = br.readLine();
            RMIPort = Integer.parseInt(portNum);

            // start a security manager - this is needed if stub
            // downloading is in use for this application.
            // The following sentence avoids the need to use
            // the option -DJava.security.policy=..." when launching the client
//            System.setProperty("java.security.policy", "src/client/java.policy");
//            System.setSecurityManager(new SecurityManager());

            String registryURL = "rmi://localhost:" + portNum + "/flota";
            // find the remote object and cast it to an interface object
         
            System.out.println("antes de h:  "+registryURL);
            IntServidorJuegoRMI h = (IntServidorJuegoRMI) Naming.lookup(registryURL);
            System.out.println("despues de h");

            System.out.println("Lookup completed ");
            // invoke the remote method
            IntServidorPartidasRMI partida = h.nuevoServidorPartidas();
            ClienteFlotaRMI cliente = new ClienteFlotaRMI();
            cliente.partida=partida;
            cliente.ejecuta();
            
        } // end try
        catch (Exception e) {
            System.out.println("Exception in Client: " + e);
        }
    } //end main


	/**
	 * Lanza una nueva hebra que crea la primera partida y dibuja la interfaz grafica: tablero
	 */
	private void ejecuta() {
		// Instancia la primera partida
		try {
			partida.nuevaPartida(NUMFILAS, NUMCOLUMNAS, NUMBARCOS);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
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
         * Constructor de una tablero dadas sus dimensiones
         */
		GuiTablero(int numFilas, int numColumnas) {
			this.numFilas = numFilas;
			this.numColumnas = numColumnas;
			frame = new JFrame();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
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
		private void anyadeMenu() {	//TODO
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
            
            //Anade las opciones a la ventana
            opciones.add(solucion);
            opciones.add(nueva);
            opciones.add(salir);
            menu.add(opciones);
            frame.add(menu, BorderLayout.NORTH);
		} // end anyadeMenu

		/**
		 * Anyade el panel con las casillas del mar y sus etiquetas.
		 * Cada casilla sera un boton con su correspondiente escuchador
		 * @param nf	numero de filas
		 * @param nc	numero de columnas
		 */
		private void anyadeGrid(int nf, int nc) {	//TODO
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
			// El panel de estado queda en la posición SOUTH del frame
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
		public void muestraSolucion() {	//TODO
			//Pinta cada casilla del color correspondiente
			quedan=0;
			for (int i = 0; i < numFilas; i++) {
				for (int j = 0; j < numColumnas; j++){
					int toque = -5;
					try {
						toque = partida.pruebaCasilla(i, j);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
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
		public void pintaBarcoHundido(String cadenaBarco) {	//TODO
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
		 * Pinta un botón de un color dado
		 * @param b			boton a pintar
		 * @param color		color a usar
		 */
		public void pintaBoton(JButton b, Color color) {
			b.setBackground(color);
			// El siguiente código solo es necesario en Mac OS X
			b.setOpaque(true);
			b.setBorderPainted(false);
		} // end pintaBoton

		/**
		 * Limpia las casillas del tablero pintándolas del gris por defecto
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
	 * Clase interna que escucha el menu de Opciones del tablero
	 * 
	 */
	private class MenuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {	//TODO
			switch(e.getActionCommand()){
			case "Salir": 
				guiTablero.liberaRecursos();
				break;
			case "Mostrar solucion":
				guiTablero.muestraSolucion();
				break;
			case "Nueva partida":
				guiTablero.limpiaTablero();
				try {
					partida.nuevaPartida(NUMFILAS, NUMCOLUMNAS, NUMBARCOS);
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				quedan = NUMBARCOS; disparos = 0;
				guiTablero.cambiaEstado("Intentos: " + disparos + "    Barcos restantes: " + quedan);
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
		public void actionPerformed(ActionEvent e) {	//TODO
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
						// TODO Auto-generated catch block
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
							// TODO Auto-generated catch block
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

