import java.util.ArrayList;
import java.util.List;

public class UploadedFiles {

	static List<Record> files=new ArrayList<Record>(); 
	
	public static byte[] getRecord(String OwnerID){
		if(files.isEmpty())
			return null;
		for(Record r:files){
			if(r.OwnerID.equals(OwnerID)){
				return r.FileData;
			}
		}
		return null;
	}
	public static int getNumOfFiles(String OwnerID){
		int num=0;
		
		for(Record r:files){
			if(r.OwnerID.equals(OwnerID)){
				num++;
			}
		}
		
		return num;
	}
	public static void deleteRecord(String OwnerID){
		
		for(Record r:files){
			if(r.OwnerID.equals(OwnerID)){
				files.remove(r);
			}
		}
		
	}
	
	public static void saveRecord(String OwnerID,byte[] data){
		Record r = new Record(OwnerID,data);
		files.add(r);
	}
	
	static class Record{
		public String OwnerID;
		public byte[] FileData;
		public Record(){
			//OwnerID="";
			//FileData=new byte[4000000];
		}
		public Record(String OID,byte[] d){
			this.OwnerID=OID;
			this.FileData=d;
		}
	}
}
