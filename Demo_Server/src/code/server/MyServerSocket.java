package code.server;

import java.awt.geom.FlatteningPathIterator;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import code.client.ByteUtils;


@SuppressWarnings("unused")
public class MyServerSocket {

	private ServerSocket myServerSocket = null;
	private int port;
	private Socket mySocket = null;
	private static ArrayList<Socket> mySocketList = new ArrayList<Socket>();
	
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
			mySocketList.add(mySocket);
			System.out.println("远程主机地址：" + mySocket.getRemoteSocketAddress());
			
			//创建消息接收线程;
			RevMsgThread myRevMsgThread = new RevMsgThread(mySocket);
			myRevMsgThread.start();
			
		}
		catch(Exception ex){
			System.out.println("监听出现异常：" + ex);
		}
	}
	
	//接收客户端消息以及转发消息
	public static void SocketRevMsg(Socket mySocket) throws IOException
	{
        String ret = null;
        InputStream is = mySocket.getInputStream();
        int bufLen = 0;
        byte[] bufer = new byte[512];

        try {        	
	        //从标准输入读入一字符串       	
	        while( (bufLen = is.read(bufer)) != -1) {
	        	
	        	ret = ByteUtils.ByteArraytoHex(bufer, bufLen);
//	        	byte[] bytes = {0x01, 0x01, 0x00, 0x02};
//	        	int hh = MyCRC16.ModBusCRC16(bytes, 4);
//	        	System.out.println("CRC16:" + Integer.toHexString(hh));
	        	byte[] turnBuf = ColData(bufer, bufLen);
	            System.out.println("Client:" + ret + bufLen + " ListSize:" + mySocketList.size());
	        	//通过广播的形式转发
	            if(turnBuf[bufLen -1] == (byte)0xEE && turnBuf[bufLen - 2] == (byte)0xDD) {
//	            	System.out.println("INBread");
		        	if(mySocketList.size() > 1) {
			        	for(Socket socket : mySocketList) {
			        		if(socket.equals(mySocket) == false) {
			        			socket.getOutputStream().write(turnBuf, 0, bufLen);
			        			socket.getOutputStream().flush();
			        		}
			        	}	        		
		        	}		            	
	            }
	            else
	            {
	            	System.out.println("MyServerNotForwarding");
	            }
            	
	        	Arrays.fill(turnBuf, (byte)0);		//清空数组
	            Arrays.fill(bufer, (byte)0);		//清空数组
	            bufLen = 0;
	        }
	        
        }
        catch(IOException ex) {    	
        	System.out.println("Client IP:" + mySocket.getRemoteSocketAddress() + "已断开");
        	mySocketList.remove(mySocket);
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
	
	private static byte[] ColData(byte[] buf, int len) { 
		byte[] ret = new byte[len];
		if(buf[len - 1] == (byte)0xEE && buf[len - 2] == (byte)0xDD) {
//			System.out.println("InBread");
			int ncrc = MyCRC16.ModBusCRC16(buf, len - 4);
//			System.out.println("nCRC:" + ncrc + "YuanCRC:" + (int)(buf[len - 4] << 8) +" "+ (int)buf[len - 3]);
			//CRC校验比较
			if((byte)((ncrc >> 8) & (0xFF)) == buf[len - 4] && (byte)(ncrc & 0xFF) ==  buf[len - 3]) {
//				System.out.println("校验成功");
				if(buf[0] == 0x01) {
					buf[0] = 0x02;
				}
				else if(buf[0] == 0x02) {
					buf[0] = 0x01;
				}
				ncrc = MyCRC16.ModBusCRC16(buf, len - 4);
				buf[len - 4] = (byte)((ncrc >> 8) & 0xFF);
				buf[len - 3] = (byte)(ncrc & 0xFF);
				ret = buf;
			}
		}	
		return ret;
	}
	
	
}
