package code.server;

import java.io.IOException;
import java.net.Socket;

public class SendMsgThread extends Thread{
	Socket mySocket;
	public SendMsgThread(Socket mySocket) {
		this.mySocket = mySocket;
	}
	
	public void run() {
		try {
			while(true) {
				//如果是关闭状态
				if(mySocket.isClosed() == true) {
					break;
				}
				byte[] sendBytes = {0x01, 0x02, 0x03, 0x04, 0x05};
				MyServerSocket.SendMsg(mySocket, sendBytes);	
				Thread.sleep(2000);
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			try {
				mySocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//System.out.println("消息接收异常" + ex);
		}
		
	}
}
