import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;






public class SocketThread extends Thread{
	static String EOL = "@end>";
	Socket sock;
	String UserID;
	public SocketThread(){
		this.sock=null;
		this.UserID="";
	}
	public SocketThread(Socket _sock){
		this.sock=_sock;
	}
	@Override
	public void run() {
		
		try {
			InputStream ins = sock.getInputStream();
			OutputStream outs = sock.getOutputStream();
			
			//Read Instruction
			String inst = "";
			byte[] instBuffer = new byte[150];
			instBuffer = readInputBuffer(ins, 150);
			inst = new String(instBuffer);
			inst = inst.trim();
			System.out.println("instruction Read:"+inst);
			
			String OwnerID = getOwnerID(inst);
			
			switch (parseInstruction(inst)) {
			case 1:
				{
					String outData = Integer.toString(UploadedFiles.getNumOfFiles(OwnerID));
					writeOutputBuffer(outs, outData.getBytes());
				}
				break;
			case 2:
				{
					writeOutputBuffer(outs, UploadedFiles.getRecord(OwnerID));
					UploadedFiles.deleteRecord(OwnerID);
				}
				
				break;
			case 3:
				{
					int bufferSize = getSize(inst);
					byte[] imageBytes = new byte[bufferSize];
					imageBytes = readInputBuffer(ins, bufferSize);
					server.files.saveRecord(OwnerID, imageBytes);
					writeOutputBuffer(outs, "SUCCESS".getBytes());
				}
				break;
			case 4:
				//Read image and return back..
				{
					int size = Integer.parseInt(inst.split("<:>")[1]);
					byte[] buffer = new byte[size];
					buffer = readInputBuffer(ins, size);
					writeOutputBuffer(outs, buffer);
				}
			default:
				break;
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				sock.close();
				System.out.println("Closing connection");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private byte[] readInputBuffer(InputStream ins,int bufferSize) throws IOException{
		byte[] buffer = new byte[bufferSize];
		
		int bytesRead = 0,buffer_offset=0;
		do{
			bytesRead = ins.read(buffer,buffer_offset,buffer.length-buffer_offset);
			buffer_offset+=bytesRead;
			
			int pos=0;
			if((pos=checkEOL(buffer,buffer_offset))>-1){
				buffer=Arrays.copyOf(buffer, pos);
				break;
			}
				
		}while(bytesRead>=1);
		
		return buffer;
	}
	
	private void writeOutputBuffer(OutputStream outs,byte[] data) {
		try{
			byte[] byteEOL = EOL.getBytes();
			outs.write(ByteBuffer.allocate(data.length+byteEOL.length).put(data).put(byteEOL).array());
			outs.flush();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private int parseInstruction(String inst){
		int instType=0;
		
		String[] tokens=inst.split("<:>");
		if(tokens.length==2){
			if(tokens[0].equals("GET")){
				instType=1;
			}else if(tokens[0].equals("PING")){
				instType=4;
			}
		}else if(tokens.length==3||tokens.length==4){
			if(tokens[0].equals("GET")){
				instType=2;
			}else if(tokens[0].equals("SEND")){
				instType=3;
			}
		}
		
		return instType;
	}
	
	private String getOwnerID(String inst){
		String[] tokens=inst.split("<:>");

		switch (parseInstruction(inst)) {
		case 1:
			
		case 2:
			if(tokens[1]!=null){
				return tokens[1];
			}else{
				return null;
			}
		case 3:
			if(tokens[2]!=null){
				return tokens[2];
			}else{
				return null;
			}
	
			
			
		default:
			return null;
		}
	}
	
	private int getSize(String inst){
		String[] tokens=inst.split("<:>");
		int size=0;
		try{
			size=Integer.parseInt(tokens[3]);
		}catch(Exception e){
			
		}finally{
			
		}
		
		return size;
	}
	
	private int checkEOL(byte[] data,int dataLen){
		int positionEOL = -1;
		
		try{
			int len = dataLen;
			for(len=dataLen;len>5;len--){
				if(data[len-1]==62){
					if(data[len-2]==100){
						if(data[len-3]==110){
							if(data[len-4]==101){
								if(data[len-5]==64){
									positionEOL = len-EOL.length();
									break;
								}	
							}	
						}	
					}
				}
			}
		}catch(Exception e){
			
		}
		
		return positionEOL;
	}
}
