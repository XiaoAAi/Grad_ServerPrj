package code.server;

import java.awt.geom.FlatteningPathIterator;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import code.client.ByteUtils;


@SuppressWarnings("unused")
public class MyServerSocket {

	private ServerSocket myServerSocket = null;
	private int port;
	private Socket mySocket = null;
	
	public MyServerSocket(ServerSocket myServerSocket, Socket mySocket, int port)
	{
		this.myServerSocket = myServerSocket;
		this.mySocket = mySocket;
		this.port = port;
	}
	//创建服务端Socket
	public void createServerSocket() {
		try {
			myServerSocket = new ServerSocket(port);			
		}
		catch(Exception ex) {
			System.out.println("套接字创建失败：" + ex);
		}		
	}
	
	//连接客户端
	public void MySocketAccept() {
		try {
			mySocket = new Socket();
			System.out.println("服务器端一直进行监听 Client");
			mySocket = myServerSocket.accept();
			System.out.println("远程主机地址：" + mySocket.getRemoteSocketAddress());
			
			//创建消息接收线程;
			RevMsgThread myRevMsgThread = new RevMsgThread(mySocket);
			myRevMsgThread.start();
			
		}
		catch(Exception ex){
			System.out.println("监听出现异常：" + ex);
		}
	}
	
	//发送和接收客户端消息
	public static void SocketRevMsg(Socket mySocket) throws IOException
	{
        String ret = null;
        InputStream is = mySocket.getInputStream();
        byte[] bufer = new byte[20];

        try {        	
	        //从标准输入读入一字符串
	        while(is.read(bufer) != -1) {
	        	
	        	ret = ByteUtils.ByteArraytoHex(bufer);
	        	
	            System.out.println("Client:" + ret);
	            Arrays.fill(bufer, (byte)0);		//清空数组
	        }
	        
        }
        catch(IOException ex) {    	
        	System.out.println("Client IP:" + mySocket.getRemoteSocketAddress() + "已断开");
        	mySocket.close();
        }
	}
	
	public void SocketCLose(ServerSocket myServerSocket, Socket mySocket) {
		try {
			myServerSocket.close();
			mySocket.close();			
		}
		catch(Exception ex) {
			System.out.println("关闭套接字异常：" + ex);
		}

		
	}
	
}
