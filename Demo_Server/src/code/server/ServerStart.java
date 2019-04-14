package code.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.text.SimpleDateFormat;
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
		
		//每2s监听数据库信息 进行数据发送
		MysqlAnalysisThread msT = new MysqlAnalysisThread();
		msT.start();
//		MyServerSocket.SelectAndUpdateMysqlDate();
//		System.out.println("end");

	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ServerStart();
	}
	

}
