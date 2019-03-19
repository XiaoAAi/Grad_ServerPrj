package code.client;

public class ClientStart {

	public ClientStart() {
		String host = "120.78.79.152";
		int port = 8888;
		MyClient client = new MyClient(host, port);
		client.CreateClient();//创建客户端	
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ClientStart();
	}

}
