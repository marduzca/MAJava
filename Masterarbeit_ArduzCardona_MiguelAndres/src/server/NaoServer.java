package server;

import static utils.GlobalVariables.SERVER_ACTIVE;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Pattern;

import nao.Controller;

public class NaoServer {

	private Controller controller;
	private int port;
	private ServerSocket serverSocket;

	public NaoServer(Controller controller) {
		this.controller = controller;
		port = 9876;
		System.out.println("Server started...");
	}

	public void run() {
		try {
			serverSocket = new ServerSocket(port);

			while (SERVER_ACTIVE) {
				System.out.println("Waiting for connection...");

				Socket connectionSocket = serverSocket.accept();
				String command = "";
				System.out.println("Got it and processing...");

				BufferedReader inFromClient = new BufferedReader(
						new InputStreamReader(connectionSocket.getInputStream()));
				DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

				System.out.println("Streams created...");

				while ((command = inFromClient.readLine()) != null) {
					command = command.trim();

					System.out.println("Received: " + command);
					String[] commandArray = command.split(Pattern.quote("|"));
					
					if(commandArray[0].equals("INI")) {
						controller.runCommand(commandArray);
						//Send back ACK
						outToClient.writeByte(1);
					}
					if(commandArray[0].equals("STP")) {
						//Send back ACK
						outToClient.writeByte(1);
						controller.runCommand(commandArray);
						break;
					}
					else {
						//Send back ACK
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
