package exportDB;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class MainDbExport {
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
			coll = db.getCollection("paperInfo");
			
		}catch(Exception e){
			System.out.println(e);
		}
		
	}
	static public Map getPaper(String queryStr){
		Map<String, String> map = new HashMap();
		try{
			st.execute(queryStr);
			ResultSet rs = st.getResultSet();
			while(rs.next()){
				System.out.println(rs);
				map.put("IndexId", rs.getString("IndexId"));
				map.put("contributor", rs.getString("contributor"));
				map.put("creator", rs.getString("creator"));
				map.put("date", rs.getString("date"));
				map.put("description", rs.getString("description"));
				map.put("publisher", rs.getString("publisher"));
				map.put("source", rs.getString("source"));
				map.put("title", rs.getString("title"));
				map.put("identifier", rs.getString("identifier"));
			}
		}catch(Exception e){
			System.out.println(e);
		}
		System.out.println(map);
		return map;
	}
	static public void saveToMongod(Map mapUse){
		BasicDBObject bo = new BasicDBObject(mapUse);
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
		init();

		for (int i = 0; i<jsonArray.size();i++){
			Map map = getPaper(String.format("SELECT * FROM `Papers` WHERE `IndexId` = '%d'", i));
			saveToMongod(map);
		}
	}

}
