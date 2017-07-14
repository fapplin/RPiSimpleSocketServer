package simplesocketserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.channels.Channels;
import jdk.dio.uart.UART;


class ProcessCommands extends Thread {

    private Socket socket;
    private int clientNumber;
    private static UART uart;
    private InputStream iStream;
    private OutputStream oStream;

	//ProcessCommands constructor
    public ProcessCommands(Socket socket, int clientNumber) {
        this.socket = socket;
        this.clientNumber = clientNumber;
    }

	//This method "run"s when the thread
	//is started.
    public void run() {
        try {
			//set up the means of input and output
			//for the socket
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Hello, you are client #" + clientNumber + ".");
            
            //Keep track of the fact that we are now
            //connected with the client.
            SimpleSocketServer.clientConnected = true;
            
            //Turn on the LED that indicates that
            //the client is connected to the server.
            SimpleSocketServer.ledClientConnected.setValue(true);

            //Use the GPIOUtils wrapper class
            //to configure the UART for use.
            uart = GPIOUtils.ConfigUART();
            
            //set up the means of input and output
			//for the UART
            oStream = Channels.newOutputStream(uart);
            iStream = Channels.newInputStream(uart);

            //Using a thread class I defined
            //whose sole purpose is to communicate
            //to the client that the button that
            //is wired to the Pi has been pressed.
            //Remember, the ProcessCommands thread
            //could be sitting and waiting for input
            //from the client. The button press could 
            //happen at any time.
            ButtonClickedThread w;

            w = new ButtonClickedThread(this.socket);
            Thread t = new Thread(w);
            t.start();
			
			//Now, start processing any messages between
			//the client and the server.
            while (true) {
                //Read the message from the socket client.
                String input = in.readLine();
                
                //This is a command that comes from the
                //client telling the server to shut down.
                if (input.equals("shutdown_server")) {
                    System.out.println("Messaged received to shutdown socket server");
                    socket.close();
                    uart.close();
                    iStream.close();
                    oStream.close();
                    SimpleSocketServer.ledClientConnected.setValue(false);
                    SimpleSocketServer.ledServerRunning.setValue(false);
                    SimpleSocketServer.ledReady.setValue(false);
                    System.exit(1);
                }
                
				//Display the data received from the client.
                System.out.println("The input received from the client: " + input);

                //Send a message back to the socket client.
                out.println("Message from socket server");

                //Turn on LED to indicate transmitting
                //to the UART.
                SimpleSocketServer.ledSerialTx.setValue(true);

                //Forward the message from the socket
                //client to the UART (RS232).
                byte[] msg = new byte[input.length()];
                for (int i = 0; i < input.length(); i++) {
                    msg[i] = (byte) input.charAt(i);
                    System.out.println("Sending to UART: " + msg[i]);
                }

                oStream.write(msg);

                //Turn off LED to indicate transmitting
                //to the UART.
                SimpleSocketServer.ledSerialTx.setValue(false);

            }
        } catch (IOException e) {
            System.out.println("Error handling client# " + clientNumber + ": " + e);
        } finally {
            try {
                socket.close();
                uart.close();
                iStream.close();
                oStream.close();
                SimpleSocketServer.ledClientConnected.setValue(false);

            } catch (IOException e) {
                System.out.println("Couldn't close a socket, what's going on?");
            }
            System.out.println("Connection with client# " + clientNumber + " closed");
        }
    }
}
