package network;

import static utils.GlobalVariables.SERVER_ACTIVE;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Pattern;

import nao.Controller;

/**
 * NaoServer is the bridge to the local system that connects as a client. It
 * opens a socket in port 9876 and waits for the client to connect and to start
 * sending commands. The server then constantly runs handling the requests until
 * the system is fully stopped.
 * 
 * @author Miguel Arduz
 *
 */
public class NaoServer {

	private Controller controller;
	private int port;
	private ServerSocket serverSocket;

	/**
	 * Normal constructor to initialize the server with the port and reference to
	 * the controller.
	 * 
	 * @param controller Controller of the system
	 */
	public NaoServer(Controller controller) {
		this.controller = controller;
		port = 9876;
		System.out.println("Server started...");
	}

	/**
	 * The run method starts the loop that constantly waits for more input from the
	 * client. First a connection is established and after each request arrives, it
	 * gets appropriately filtered, partitioned into the relevant parts and then
	 * forwarded to the Controller. Afterward an acknowledgement message is return
	 * to the client confirming the receipt of the message. Depending on the case
	 * the ACK is sent before or after running the request. The loop is stopped only
	 * externally from the Controller when the correspondent stopping request
	 * arrives.
	 */
	public void run() {
		try {
			serverSocket = new ServerSocket(port);

			while (SERVER_ACTIVE) {
				System.out.println("Waiting for connection...");

				Socket connectionSocket = serverSocket.accept();
				String command = "";

				BufferedReader inFromClient = new BufferedReader(
						new InputStreamReader(connectionSocket.getInputStream()));
				DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

				while ((command = inFromClient.readLine()) != null) {
					command = command.trim();

					System.out.println("Received: " + command);
					String[] commandArray = command.split(Pattern.quote("|"));

					if (commandArray[0].equals("INI")) {
						controller.runCommand(commandArray);
						// Send back ACK
						outToClient.writeByte(1);
					}
					if (commandArray[0].equals("STP")) {
						// Send back ACK
						outToClient.writeByte(1);
						controller.runCommand(commandArray);
						break;
					} else {
						// Send back ACK
						outToClient.writeByte(1);
						controller.runCommand(commandArray);
					}

					command = "";
				}

				connectionSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
