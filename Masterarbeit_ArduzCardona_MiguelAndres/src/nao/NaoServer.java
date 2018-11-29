package nao;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.regex.Pattern;

import utils.GlobalVariables;

public class NaoServer extends Thread{
	
	private Controller controller;
	
	public NaoServer(Controller controller) {
		this.controller = controller;
	}
	
	@Override
	public void run() {
		try {
			DatagramSocket serverSocket = new DatagramSocket(9876);

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
}
