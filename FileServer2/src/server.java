
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;


public class server {
	static ServerSocket ss=null;
	public static UploadedFiles files;
	public static Boolean keepServerRunning = true;
	public static void main(String[] args){
		System.out.println("FServe2 started...");
		files=new UploadedFiles();
		try {
			ss=new ServerSocket(3333);
			
			while(keepServerRunning){
				Socket sock=ss.accept();
				System.out.println("Client connected:"+sock.toString());
				new SocketThread(sock).start();
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
