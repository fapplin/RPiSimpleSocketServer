package simplesocketserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.gpio.PinEvent;
import jdk.dio.gpio.PinListener;

/**
 *
 * @author pi
 */
public class SimpleSocketServer {

    static GPIOPin ledServerRunning;
    static GPIOPin ledClientConnected;
    static GPIOPin ledSerialTx;
    static GPIOPin ledReady;
    static GPIOPin readyButton;
    static boolean clientConnected = false;
    static boolean buttonClicked = false;
    static ServerSocket listener;

    public static void main(String[] args) throws Exception {
        int clientNumber = 0;
        try {
			//define listener to accept connections
			//for socket communications
            listener = new ServerSocket(9898);
            System.out.println("The socket server is running.");
			
			//use GPIOUtils wrapper class for DIO
			//to define the GPIOs we want to use
			//for the LEDs and button
            ledServerRunning = GPIOUtils.ConfigLEDPin(18);
            ledClientConnected = GPIOUtils.ConfigLEDPin(23);
            ledSerialTx = GPIOUtils.ConfigLEDPin(24);
            ledReady = GPIOUtils.ConfigLEDPin(25);
            readyButton = GPIOUtils.ConfigButtonPin(17);
            
            //turn on the "server running" LED
            ledServerRunning.setValue(true);
            
            //create a "listener" for the server program
            //to listen for when the button has been pressed.
            readyButton.setInputListener(new PinListener() {

				//This "listener" method senses when
				//the value of the pin has changed
                public void valueChanged(PinEvent event) {
                    if (event.getValue()) {
                        try {
							//We're checking the clientConnected
							//variable, which gets set when the
							//server program and the client program
							//connect. We don't want this logic to
							//execute if the client hasn't connected.
                            if (clientConnected) {
								//The button wired to the Raspberry Pi
								//has been pressed. This variable will 
								//let another thread running independently
								//from the ProcessCommands thread that
								//the button has been clicked. The 
								//ProcessCommands thread could be sitting
								//waiting for a message (input) from
								//the client.
                                buttonClicked = true; 
                                
                                System.out.println("button has been pressed.");
                                
                                //Turn on the LED that indicates
                                //the button has been pressed.
                                ledReady.setValue(true);
                            }
                        } catch (IOException ex) {
                            System.out.println("Exception: " + ex.getMessage());
                        }
                    }
                }
            });
            
            
			//The ProcessCommands class is a thread
			//that will run forever while the server
			//is running. It will be used to handle
			//the communications between the client
			//and the server via the socket.
            ProcessCommands cmds;

            while (true) {
                cmds = new ProcessCommands(listener.accept(), clientNumber++);
                
                //Sart the socket processing thread.
                cmds.start();
            }

        } finally {
            System.out.println("listener.close();");
            listener.close();
        }

    }

    @Override
    protected void finalize() {
	//Objects created in run method are finalized when
	//program terminates and thread exits
        try {
            listener.close();
            ledServerRunning.setValue(false);
            ledClientConnected.setValue(false);
            ledSerialTx.setValue(false);
            ledReady.setValue(false);
        } catch (IOException e) {
            System.out.println("Could not close socket");
            System.exit(-1);
        }
    }

}
