package code.client;

import java.net.Socket;

public class RevMsgThread extends Thread{
	Socket mySocket;
	public RevMsgThread(Socket mySocket) {
		this.mySocket = mySocket;
	}
	
	public void run() {
		try {
			MyClient.RevMsg(mySocket);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			//System.out.println("消息接收异常" + ex);
		}
		
	}
}
