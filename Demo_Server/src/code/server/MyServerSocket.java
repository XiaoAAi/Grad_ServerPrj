package code.server;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;


import code.client.ByteUtils;



public class MyServerSocket {

	private ServerSocket myServerSocket = null;
	private int port;
	private Socket mySocket = null;
	private static ArrayList<Socket> mySocketList = new ArrayList<Socket>();
//	private static boolean flagAllDatFeedback = false;
	
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
	public void MySocketAccept(){
		try {
			mySocket = new Socket();
			System.out.println("服务器端一直进行监听 Client");
			mySocket = myServerSocket.accept();
			mySocketList.add(mySocket);
			System.out.println("远程主机地址：" + mySocket.getRemoteSocketAddress());
			
			//创建消息接收线程;
			RevMsgThread myRevMsgThread = new RevMsgThread(mySocket);
			myRevMsgThread.start();
			
//			//发送消息->用于公司测试
//			SendMsgThread mySndThread = new SendMsgThread(mySocket);
//			mySndThread.start();
			
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
        byte[] bufer = new byte[40000];

        try {        	
	        //从标准输入读入一字符串       	
	        while((bufLen = is.read(bufer)) != -1) {
	        	
	        	ret = ByteUtils.ByteArraytoHex(bufer, bufLen);
	        	ColData(bufer, bufLen);
	            System.out.println("Client:" + ret + "  NumLen:" + bufLen + "  ListSize:" + mySocketList.size());
	            Arrays.fill(bufer, (byte)0);		//清空数组
	            bufLen = 0;
	        }
	        
        }
        catch(IOException ex) {    	
        	System.out.println("Client IP:" + mySocket.getRemoteSocketAddress() + "已断开");
        	mySocketList.remove(mySocket);
        	mySocket.close();
        }
        catch(NullPointerException ex) {
        	System.out.println("Client IP:" + mySocket.getRemoteSocketAddress() + "已断开");
        	mySocketList.remove(mySocket);
        	mySocket.close();        	
        }
	}
	
	//服务器消息发送
	public static void SendMsg(Socket mySocket, byte[] sendBytes) throws IOException {
		try {
			OutputStream outputStream = mySocket.getOutputStream();	

			outputStream.write(sendBytes);
			
			outputStream.flush();		

		}
		catch(Exception ex) {
			ex.printStackTrace();
			mySocket.close();
		}

	}	
	
	//关闭服务器
	public void SocketCLose(ServerSocket myServerSocket, Socket mySocket) {
		try {
			myServerSocket.close();
			mySocket.close();			
		}
		catch(Exception ex) {
			System.out.println("关闭套接字异常：" + ex);
		}		
	}
	
	//判断校验是否正确
	private static void ColData(byte[] buf, int len) throws NullPointerException, IOException{ 
		if(buf[len - 1] == (byte)0xEE && buf[len - 2] == (byte)0xDD) {
			int ncrc = MyCRC16.ModBusCRC16(buf, len - 4);	
			//CRC校验比较
			if((byte)((ncrc >> 8) & 0xFF) == buf[len - 4] && (byte)(ncrc & 0xFF) == buf[len - 3]) {
				//开始升级指令 ->(网页发送所有数据全部转发)
				DataAnalysis(buf);
			}
		}	
	}

	//功能：数据解析，存储相应的数据库对应位置
	private static void DataAnalysis(byte[] buf) throws IOException{
		//解析数据0x01
		if(buf[0] == (byte)0x01){
			switch(buf[1]) {
				//开始升级指令
				case (byte)0xAE:{
					System.out.println("beginUpdate");
					break;
				}
				//结束升级指令
				case (byte)0xAF:{
					System.out.println("StopUpdate");
					break;
				}
				//接收心跳包指令
				case (byte)0x0B:{
					MySqlServer sqlServer = new MySqlServer();
					int tem = ((buf[2] & 0xFF) << 8 | (buf[3] & 0xFF));
					int hum = ((buf[4] & 0xFF) << 8 | (buf[5] & 0xFF));
					long lt = System.currentTimeMillis();
					Date date = new Date(lt);
					DateFormat simpleDateFormate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					
					String sql = "INSERT INTO temhum_tbl (temhum_tem,temhum_hum,submit_date) VALUES ('" 
										+ (float)tem / 10.0 + "','" + (float)hum / 10.0 + "','" + simpleDateFormate.format(date) + "');";
//					System.out.println((float)tem / 10.0 + "','" + (float)hum / 10.0);
					sqlServer.executeUpdate(sql);
					sqlServer.closeConnection();		//断开数据库连接	
					break;
				}
				//接收到灯开的状态
				case (byte)0x03:{
					MySqlServer sqlServer = new MySqlServer();
					String sql = "";
					if(buf[2] == (byte)0xAA){
						sql = "update controller_tbl set status='open' where id='light';";
					}
					else if(buf[2] == (byte)0xBB){
						sql = "update controller_tbl set status='close' where id='light';";
					}					
					sqlServer.executeUpdate(sql);
					sqlServer.closeConnection();		//断开数据库连接						
					break;
				}
				//反馈锁的状态
				case (byte)0x04:{
					MySqlServer sqlServer = new MySqlServer();
					String sql = "";
					if(buf[2] == (byte)0xAA){
						sql = "update controller_tbl set status='open' where id='lock';";
					}
					else if(buf[2] == (byte)0xBB){
						sql = "update controller_tbl set status='close' where id='lock';";
					}					
					sqlServer.executeUpdate(sql);
					sqlServer.closeConnection();		//断开数据库连接						
					break;					
				}
				//反馈风扇的状态
				case (byte)0x56:{
					MySqlServer sqlServer = new MySqlServer();
					String sql = "";
					if(buf[2] == (byte)0xAA){
						sql = "update controller_tbl set status='open' where id='fan';";
					}
					else if(buf[2] == (byte)0xBB){
						sql = "update controller_tbl set status='close' where id='fan';";
					}					
					sqlServer.executeUpdate(sql);
					sqlServer.closeConnection();		//断开数据库连接					
					break;
				}
				//接收到LED显示信息
				case (byte)0x07:{
					MySqlServer sqlServer = new MySqlServer();
					String sql = "update controller_tbl set status='success' where id='msg';";				
					sqlServer.executeUpdate(sql);
					sqlServer.closeConnection();		//断开数据库连接									
					break;
				}
				//重启底层板子成功
				case (byte)0x0E:{
					MySqlServer sqlServer = new MySqlServer();
					String sql = "update controller_tbl set status='success' where id='reboot';";				
					sqlServer.executeUpdate(sql);
					sqlServer.closeConnection();		//断开数据库连接					
					break;
				}
				//获取实时时钟
				case (byte)0x00:{
					java.util.Date t = new java.util.Date();
//					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
//					System.out.println(df.format(t));
					byte[] dateByte = df.format(t).getBytes();
					byte[] cmdlenByt = {0x01, 0x00};
					dateByte = ByteUtils.byteMerger(cmdlenByt, dateByte);
					cmdlenByt[0] = 0x00;
					cmdlenByt[1] = 0x0E;
					dateByte = ByteUtils.byteMerger(dateByte, cmdlenByt);
					byte[] endByt = {0x00, 0x00, (byte)0xDD, (byte)0xEE};
					int ncrc = MyCRC16.ModBusCRC16(dateByte, dateByte.length);
					endByt[0] = (byte)((ncrc >> 8) & 0xFF);
					endByt[1] = (byte)(ncrc & 0xFF);					
					dateByte = ByteUtils.byteMerger(dateByte, endByt);
//					for(int i= 0; i < dateByte.length; i++) {
//						System.out.printf("%X\n", dateByte[i]);
//					}
					RetSocketDat(dateByte, dateByte.length);
					break;
				}
				default:{
					System.out.println("接收命令unknow");
					break;
				}
			
			}		
		}				
	}

	//处理前端发送给数据库的字符串命令
	public static void HtmlStringAnalysis() throws InterruptedException, IOException {
		while(true){
			Thread.sleep(2500);
			SelectAndUpdateMysqlDate();
			Thread.sleep(2500);
			//System.out.println("SelectMysqlOpreation");
		}
	}

	//数据库查询及清除指令
	private static void SelectAndUpdateMysqlDate() throws IOException {
		LightCmd();
		LockCmd();	
		FanCmd();
		SendMsgOled();
		RebootBottom();
	}
	
	//灯的发送指令
	private static void LightCmd() throws IOException {
		MySqlServer sqlServer = new MySqlServer();
		String sql = "select command from controller_tbl where id='light';";
		String strCmp = sqlServer.executeStringQuery(sql);
		if(strCmp.equals("openlight")) {
			byte buf[] = {0x01, 0x01, 0x00, 0x02, (byte)0xD1, (byte)0xD9, (byte)0xDD, (byte)0xEE};
			RetSocketDat(buf, 8);
			System.out.println("发送打开灯指令");
		}
		else if(strCmp.equals("closelight")) {
			byte buf[] = {0x01, 0x02, 0x00, 0x02, (byte)0x21, (byte)0xD9, (byte)0xDD, (byte)0xEE};
			RetSocketDat(buf, 8);			
			System.out.println("发送关灯指令");
		}
		sql = "update controller_tbl set command='' where id='light';";				
		sqlServer.executeUpdate(sql);
		
		sqlServer.closeConnection();		//断开数据库连接	
	}
	//电磁锁的发送指令
	private static void LockCmd() throws IOException {	
		
		MySqlServer sqlServer = new MySqlServer();
		//电磁锁的发送指令
		String sql = "select command from controller_tbl where id='lock';";
		String strCmp = sqlServer.executeStringQuery(sql);
		if(strCmp.equals("openlock")) {
			byte buf[] = {0x01, 0x04, 0x00, 0x02, (byte)0xC1, (byte)0xD8, (byte)0xDD, (byte)0xEE};
			RetSocketDat(buf, 8);			
			System.out.println("发送开锁指令");
		}
		else if(strCmp.equals("closelock")) {
			System.out.println("发送关锁指令");
		}
		sql = "update controller_tbl set command='' where id='lock';";				
		sqlServer.executeUpdate(sql);
		
		sqlServer.closeConnection();		//断开数据库连接	
	}
	
	//电风扇的指令发送
	private static void FanCmd() throws IOException {
		MySqlServer sqlServer = new MySqlServer();
		//电风扇的发送指令
		String sql = "select command from controller_tbl where id='fan';";
		String strCmp = sqlServer.executeStringQuery(sql);
		if(strCmp.equals("openfan")) {
			byte buf[] = {0x01, 0x05, 0x00, 0x02, (byte)0x90, (byte)0x18, (byte)0xDD, (byte)0xEE};
			RetSocketDat(buf, 8);			
			System.out.println("发送开风扇指令指令");
		}
		else if(strCmp.equals("closefan")) {
			byte buf[] = {0x01, 0x06, 0x00, 0x02, (byte)0x60, (byte)0x18, (byte)0xDD, (byte)0xEE};
			RetSocketDat(buf, 8);
			System.out.println("发送关风扇指令指令");
		}
		sql = "update controller_tbl set command='' where id='fan';";				
		sqlServer.executeUpdate(sql);	
		
		sqlServer.closeConnection();		//断开数据库连接	
	}
	
	//发送消息显示在OLED显示屏指令
	private static void SendMsgOled() throws IOException {
		MySqlServer sqlServer = new MySqlServer();
		
		String sql = "select command from controller_tbl where id='msg';";
		String strCmp = sqlServer.executeStringQuery(sql);
		if(strCmp.equals("sendmsg")) {
			//获取发送的内容数据
			sql = "select content from controller_tbl where id='msg';";
			strCmp = sqlServer.executeStringQuery(sql);
			byte buf[] = {0x01, 0x07};			
			byte retBuf[] = ByteUtils.byteMerger(buf, strCmp.getBytes());
			int len = retBuf.length;
			buf[0] = 0x00;
			buf[1] = (byte)len;
			retBuf = ByteUtils.byteMerger(retBuf, buf);
			int ncrc = MyCRC16.ModBusCRC16(retBuf, (len + 2));
			byte[] bytcrc = {(byte)((ncrc >> 8) & 0xFF), (byte)(ncrc & 0xFF), (byte)0xDD, (byte)0xEE};
			retBuf = ByteUtils.byteMerger(retBuf, bytcrc);
//			for(int i = 0; i<retBuf.length; i++) {
//				System.out.printf("%02X\\n", retBuf[i]);
//			};
			RetSocketDat(retBuf, retBuf.length);			
			System.out.println("发送消息到OLED数据" + strCmp);
			sql = "update controller_tbl set content='' where id='msg';";				
			sqlServer.executeUpdate(sql);
		}
		sql = "update controller_tbl set command='' where id='msg';";				
		sqlServer.executeUpdate(sql);
		
		sqlServer.closeConnection();		//断开数据库连接
	}

	//发送重启底层指令
	private static void RebootBottom() throws IOException {
		MySqlServer sqlServer = new MySqlServer();
		//发送底层重启指令
		String sql = "select command from controller_tbl where id='reboot';";
		String strCmp = sqlServer.executeStringQuery(sql);
		if(strCmp.equals("rebottom")) {
			byte buf[] = {0x01, 0x0E, 0x00, 0x02, (byte)0xE1, (byte)0xDA, (byte)0xDD, (byte)0xEE};
			RetSocketDat(buf, 8);
			System.out.println("发送底层重启指令");
		}
		sql = "update controller_tbl set command='' where id='reboot';";				
		sqlServer.executeUpdate(sql);		

		sqlServer.closeConnection();		//断开数据库连接			
	}


	
	//数据转发
	private static void RetSocketDat( byte[] buf, int len) throws IOException {
		
    	for(Socket socket : mySocketList) {
			socket.getOutputStream().write(buf, 0, len);
			socket.getOutputStream().flush();
    	}	
	}
	
	
	
}
