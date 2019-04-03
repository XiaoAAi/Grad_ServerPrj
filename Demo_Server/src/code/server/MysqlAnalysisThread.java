package code.server;


public class MysqlAnalysisThread extends Thread {

//	Socket mySocket;
//	public MysqlAnalysisThread(Socket mySocket) {
//		this.mySocket = mySocket;
//	}
	public void run() {
		try {
			//查询数据库发送指令
			MyServerSocket.HtmlStringAnalysis();
		}
		catch(Exception ex) {
			ex.printStackTrace();
			System.out.println("消息接收异常" + ex);
		}
		
	}	
	
}
