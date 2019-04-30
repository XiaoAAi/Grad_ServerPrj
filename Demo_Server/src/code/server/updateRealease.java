package code.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class updateRealease {

	public static boolean ReadUpdateFile(){
		
		File updateFile = new File("/home/Server/updateBottom/realease.bin");
		
		char[] charBuf = new char[20000];
		
		try {
			FileReader rd = new FileReader(updateFile);
			
            while((rd.read(charBuf)) != -1){				//read读取一个char类型
               byte[] buf = ByteUtils.getBytes(charBuf);
//               //转发升级数据byte类型
               MyServerSocket.RetSocketDat(buf, buf.length);
//               String ret = ByteUtils.ByteArraytoHex(buf, buf.length);
//               System.out.println("UpdateFiles " + ret);
            }
            rd.close(); 			
		} catch (FileNotFoundException ex) {
			// TODO Auto-generated catch block
			System.out.println(ex.getMessage());
		}
		catch (IOException ex) {
			System.out.println(ex.getMessage());
		}
				
		return true;
	}
}
