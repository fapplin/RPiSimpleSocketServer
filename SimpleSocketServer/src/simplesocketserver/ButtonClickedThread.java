package simplesocketserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

//It was nessesary to create this thread because
//the socket thread could be in a "pause" mode
//at any time waiting for input from the client.
//This thread runs separately and in parallel. It
//gives the socket server program a means of
//communicating to the client program that the button
//wired to the Raspberry Pi has been pressed.
public class ButtonClickedThread implements Runnable {

    private Socket server;

	//Constructor
    ButtonClickedThread(Socket server) {
        this.server = server;

    }

    public void run() {
        String line = "";
        PrintWriter out = null;

		//Set up our means of communicating
		//through the socket.
        try {
            out = new PrintWriter(server.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("in or out failed");
            System.exit(-1);
        }

		
        while (true) {
			//Check to see if the button has been pressed.
			//If the button has been pressed [the variable
			//has been set in the SimpleSocketServer part
			//of the program] - then tell the connected
			//client by sending it a message.
            if (SimpleSocketServer.buttonClicked) {
                out.println("button_clicked");
                SimpleSocketServer.buttonClicked = false;
                System.out.println("ButtonClickedThread: button_clicked sent");
            }
        }
    }
}
