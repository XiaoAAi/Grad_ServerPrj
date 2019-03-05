package code.server;

public class AcceptThread implements Runnable{

	private MyServerSocket myServerSocket;
	
	public AcceptThread(MyServerSocket myServerSocket) {
		this.myServerSocket = myServerSocket;
	}
	
	public void run() {
		
		while(true) {
			try{

				myServerSocket.MySocketAccept();
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}		
	}	
}
