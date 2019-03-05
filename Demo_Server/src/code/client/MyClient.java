package code.client;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

public class MyClient {

	private String host = null;
	private int port = 0;
	private Socket mySocket;
	
	public MyClient(String host, int port) {

		this.host = host;
		this.port = port;
	}
	
	//客户端的创建
	public void CreateClient() {
		try {
			mySocket = new Socket(host, port);
			System.out.println("连接成功");
			RevMsgThread revThread = new RevMsgThread(mySocket);
			revThread.start();
			SendMsgThread sendMsgThread = new SendMsgThread(mySocket);
			sendMsgThread.start();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}	
	}
	
	//客户端消息发送
	public static void SendMsg(Socket mySocket, byte[] sendBytes) {
		try {
			OutputStream outputStream = mySocket.getOutputStream();	

			outputStream.write(sendBytes);
			
			outputStream.flush();		

		}
		catch(Exception ex) {
			ex.printStackTrace();
		}

	}
	
	
	//客户端消息接收
	public static void RevMsg(Socket mySocket) {
		try {
			
			byte[] buffer = new byte[20];
			String ret = null;
			InputStream is = mySocket.getInputStream();
			while(is.read(buffer) != -1) {
				ret = ByteUtils.ByteArraytoHex(buffer);
				System.out.println("Server:" + ret);
				
				Arrays.fill(buffer, (byte)0);
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	
}
