import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class ClientApp {

	static volatile boolean isManualTestConnected=true;
	static volatile Socket sock;
	static BufferedReader bReader;
	static BufferedWriter bWriter;
	static String EOL = "@end>";
	static void reConnect(){
		try {
			sock=new Socket("localhost",3333);
			//sock=new Socket("35.196.160.177",3333);
			bReader=new BufferedReader(new InputStreamReader(sock.getInputStream()));
			bWriter=new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws UnknownHostException, IOException{
		reConnect();
		
		File imageFile = new File("C:\\Users\\ashu\\Desktop\\img.jpg");
		FileInputStream finn = new FileInputStream(imageFile);
		byte[] fileBuffer = new byte[(int) imageFile.length()];
		finn.read(fileBuffer);
		finn.close();
		
		InputStream ins = sock.getInputStream();
		OutputStream outs = sock.getOutputStream();
		
		outs.write(constructInstruction(new String("PING<:>"+fileBuffer.length)));outs.flush();
		outs.write(fileBuffer);outs.write(EOL.getBytes());outs.flush();
		
		byte[] recImage = new byte[4194304];
		int bytesRead=0,offset = 0;
		
		do{
			bytesRead = ins.read(recImage, offset, recImage.length - 1 - offset);
			offset+=bytesRead;
			if(checkEOL(recImage, offset))
				break;
			
		}while(bytesRead>0);
		
		File outImage = new File("C:\\Users\\ashu\\Desktop\\img_o.jpg");
		FileOutputStream fout  = new FileOutputStream(outImage);
		fout.write(recImage, 0, offset-EOL.length());
		fout.close();
		
	}
	
	private static byte[] constructInstruction(String inst){
		
		byte[] data = (new String(inst+EOL)).getBytes();
		byte[] instBytes = new byte[150-data.length];
		
		return ByteBuffer.allocate(150).put(data).put(instBytes).array();
	}
	private static boolean checkEOL(byte[] data,int dataLen){
		boolean containsEOL = false;
		
		try{
			int len = dataLen;
			if(data[len-1]==62){
				if(data[len-2]==100){
					if(data[len-3]==110){
						if(data[len-4]==101){
							if(data[len-5]==64){
								containsEOL = true;
							}	
						}	
					}	
				}
			}
		}catch(Exception e){
			
		}
		
		return containsEOL;
	}
}
