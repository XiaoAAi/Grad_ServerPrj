package code.client;

import java.net.Socket;

public class SendMsgThread extends Thread{
	Socket mySocket;
	public SendMsgThread(Socket mySocket) {
		this.mySocket = mySocket;
	}
	
	public void run() {
		try {
			while(true) {
				byte[] sendBytes = {0x01, 0x02, 0x03, 0x04, 0x05};
				MyClient.SendMsg(mySocket, sendBytes);	
				Thread.sleep(2000);
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			//System.out.println("消息接收异常" + ex);
		}
		
	}
}
