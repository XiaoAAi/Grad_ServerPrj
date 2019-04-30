package code.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

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
			System.out.println("连接到服务器" + host);
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
	public static void SendMsg(Socket mySocket, byte[] sendBytes) throws IOException {
		try {
			@SuppressWarnings("resource")
			Scanner sc = new Scanner(System.in);
			
			String read = sc.next();
			System.out.println(read);
			if(read != null) {
				byte[] buf = ByteUtils.hexStringToBytes(read);
				OutputStream outputStream = mySocket.getOutputStream();	
				outputStream.write(buf);			
				outputStream.flush();
				
				Arrays.fill(buf,(byte)0);
				System.out.println("发送成功");				
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			mySocket.close();
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
