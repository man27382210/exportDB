package exportDB;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class dbExport {
	static HashMap hashUse = new HashMap();
	static Connection conn = null;
	static Statement st = null;
	static DBCollection coll = null;
	static JSONParser parser = new JSONParser();
	static ArrayList jsonArray = new ArrayList();
	static public void init(){
		readFromJson("");
		try{
			String usr = "cite";
			String pwd = "rb303";
			String dbUse = "140.118.175.200:3306/citeseerx";
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://"+dbUse, usr, pwd);
			st = conn.createStatement();
			
			
			MongoClient mongoClient = new MongoClient("114.34.79.27",27017);
			DB db = mongoClient.getDB( "paperMiningTest" );
			coll = db.getCollection("paperRefArray");
			
		}catch(Exception e){
			System.out.println(e);
		}
		
	}
	static public ArrayList getPaper(String queryStr){
		ArrayList returnArray = new ArrayList();
		try{
//			st.execute(String.format("SELECT `ReferenceId` FROM `References` WHERE `CitationId` = '%d'", 1));
			st.execute(queryStr);
			ResultSet rs = st.getResultSet();
			while(rs.next()){
				returnArray.add(rs.getString("ReferenceId"));
			}
		}catch(Exception e){
			System.out.println(e);
		}
		return returnArray;
	}
	
	static public ArrayList get(String id){
		ArrayList idCatitionArray = new ArrayList();
		if(hashUse.containsKey(id)){
			idCatitionArray = (ArrayList) hashUse.get(id);
		}else{
			String str = String.format("SELECT `ReferenceId` FROM `References` WHERE `CitationId` = '%s'", id);
			idCatitionArray = getPaper(str);
			hashUse.put(id, idCatitionArray);
		}
		System.out.println(idCatitionArray);
		return idCatitionArray;
	}
	static public void saveToMongod(String firstRef, String secRef, ArrayList intersectionArray){
		BasicDBObject bo = new BasicDBObject();
		bo.put("firstRef", firstRef);
		bo.put("secRef", secRef);
		bo.put("intersectionArray", intersectionArray);
		coll.save(bo);
		System.out.println(bo);
	}
	static public void saveToMongod2(String id, ArrayList refArray){
		BasicDBObject bo = new BasicDBObject();
		bo.put("IndexId", id);
		bo.put("ReferenceId", refArray);
		coll.save(bo);
		System.out.println(bo);
	}
	static public void readFromJson(String path){
		try {
			JSONObject a = (JSONObject) parser.parse(new FileReader("/Users/man27382210/Documents/workspace/exportDB/paperId.json"));
			jsonArray = (ArrayList) a.get("CitationId");
		} catch (Exception e){
			System.out.println(e);
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		select `CitationId` from `References` GROUP BY `CitationId` 
		init();
		
		ArrayList paperArray = new ArrayList();
//		refPaperArray = getPaper("SELECT `ReferenceId` FROM `References` WHERE `CitationId` = '%s'");
		paperArray = jsonArray;
		for (int i = 0; i<paperArray.size();i++){
			ArrayList saveRef = get(String.valueOf(paperArray.get(i)));
			saveToMongod2(String.valueOf(paperArray.get(i)), saveRef);
		}
//		for (int i = 0; i<paperArray.size();i++){
//			ArrayList firstRef = get(String.valueOf(paperArray.get(i)));
//			for (int j = i+1; j< paperArray.size();j++){
//				ArrayList secRef = get(String.valueOf(paperArray.get(j)));
//				ArrayList resultArray = firstRef;
//				resultArray.retainAll(secRef);
//				System.out.println(String.valueOf(paperArray.get(i)));
//				System.out.println(String.valueOf(paperArray.get(j)));
//				System.out.println(resultArray);
//				System.out.println("");
//				saveToMongod(String.valueOf(paperArray.get(i)), String.valueOf(paperArray.get(j)), resultArray);
//			}
//		}
	}

}
