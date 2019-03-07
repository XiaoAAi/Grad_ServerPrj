package code.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

@SuppressWarnings("unused")

public class ServerStart {

	Socket mySocket;
	ServerSocket myServerSocket;

	public ServerStart() {
		//实例化MyServerSocket对象
		MyServerSocket serverSocket = new MyServerSocket(myServerSocket, mySocket, (int)8888);
		AcceptThread myAceptThread = new AcceptThread(serverSocket);
		//创建ServerSocket服务端
		serverSocket.createServerSocket();
		//监听客户端连接
		new Thread(myAceptThread).start();	

	}
	
		
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ServerStart();
	}
	

}
