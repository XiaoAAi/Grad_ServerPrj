package code.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;


public class MySqlServer {

	static java.sql.Connection conn;
    private final String dbDrive="com.mysql.jdbc.Driver";
//    private final String url = "jdbc:mysql://127.0.0.1:3306/GRA_DATA";			//应该也是可以的
//    private final String url = "jdbc:mysql://localhost:3306/GRA_DATA";
    private final String url = "jdbc:mysql://120.78.79.152:3306/GRA_DATA";			//同样是可以的
    private final String userName = "root";
    private final String password = "123";
    private Connection con = null;
    
    //构造方法
    public MySqlServer() {
        try {
            Class.forName(dbDrive).newInstance(); 
        } catch (Exception ex) {
            System.out.println("数据库加载失败");
            ex.printStackTrace();
        }
    }
    
    //创建数据库连接
    public boolean creatConnection() {
        try {
            con = DriverManager.getConnection(url, userName, password);
            con.setAutoCommit(true);        
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("creatConnectionError!");
        }
        return true;
    }

    //对数据库的增加、修改和删除的操作
    public boolean executeUpdate(String sql) throws NullPointerException{
         if (con == null) {
            creatConnection();
        }
        try {
            Statement stmt = (Statement) con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
            int iCount = stmt.executeUpdate(sql);
            System.out.println("操作成功，所影响的记录数为" + String.valueOf(iCount));
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }   
    }
    //对数据库的查询操作
    public ResultSet executeQuery(String sql) {
        ResultSet rs;
        try {
            if (con == null) {
                creatConnection();
            }
            Statement stmt = (Statement) con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
            try {
                rs = stmt.executeQuery(sql);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return null;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("executeQueryError!");
            return null;
        }
        return rs;
    }
    
    //关闭数据库
    public void  closeConnection(){
        if(con == null){
            try {
                con.close();
            } catch (SQLException e) {
            // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    
    
    
//	public static boolean SqlConn(String url, String name, String password) throws SQLException{
//		 boolean ret = false;
//		 //新建驱动，注册驱动
//		 com.mysql.jdbc.Driver driverOne=new com.mysql.jdbc.Driver();
//		 DriverManager.registerDriver(driverOne);
//		 conn = DriverManager.getConnection(url,name,password);
//		 if(!conn.isClosed()) {
//			 ret = true;
//			 System.out.println("Succeeded connecting to the Database!");
//		 }	 
//		 return ret;
//	}
	
//	public static boolean SqlSelect() {
//		boolean ret = false;
//		//2.创建statement类对象，用来执行SQL语句！！
//		Statement statement = conn.createStatement();		
//		
//		return ret;
//	}
	
	
	
	
}
