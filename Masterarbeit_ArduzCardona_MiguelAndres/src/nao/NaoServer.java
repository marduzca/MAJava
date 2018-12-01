package nao;

import static utils.GlobalVariables.SERVER_ACTIVE;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class NaoServer extends Thread{
	
	private Controller controller;
	private int port;
	private ServerSocket serverSocket;
	
	public NaoServer(Controller controller) {
		this.controller = controller;
		port = 9876;
		System.out.println("Server started...");
	}
	/*
	@Override
	public void run() {
		try {
			DatagramSocket serverSocket = new DatagramSocket(port);

			byte[] receiveData = new byte[1024];
			String command = "";

			while (GlobalVariables.SERVER_ACTIVE) {
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

				serverSocket.receive(receivePacket);

				command = new String(receivePacket.getData());
				command = command.trim();

				System.out.println("Received: " + command);
				controller.runCommand(command.split(Pattern.quote("|")));
				command = "";
			}

			serverSocket.close();

		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	*/
	
	@Override
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

//		        while ( (command = inFromClient.readLine()) != null ) {
//		            System.out.println(command);
//		        }

				while ((command = inFromClient.readLine()) != null) {
					command = command.trim();

					System.out.println("Received: " + command);
					//controller.runCommand(command.split(Pattern.quote("|")));
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
