package exportDB;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONObject;
public class db {
	static HashMap paperSetDic = new HashMap();
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Connection conn = null;
//		ArrayList<HashMap<String, ArrayList<String>>> paperSet = new ArrayList<HashMap<String, ArrayList<String>>>();
		
		
//		ArrayList paperSetTwo = new ArrayList();
//		HashMap<String, ArrayList<String>> paper = new HashMap<String, ArrayList<String>>();
		try
		{
			String usr = "cite";
			String pwd = "rb303";
			String dbUse = "140.118.175.200:3306/citeseerx";
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://"+dbUse, usr, pwd);
			Statement st = conn.createStatement();
			st.execute("SELECT * FROM Papers WHERE identifier = '10.1.1.135.4448'");
			ResultSet rs = st.getResultSet();
			HashMap paper = new HashMap();
			while(rs.next()){
				paper.put("IndexId", rs.getString("IndexId").toString());
				paper.put("title", rs.getString("title").toString());
				paper.put("date", rs.getString("date").toString());
				paper.put("description", rs.getString("description").toString());
				
				System.out.println(paper);
//				paperSetTwo.add(paper);
			}
			
//			st.execute(String.format("SELECT * FROM References WHERE identifier = '%s'", ((HashMap)paperSetTwo.get(0)).get("IndexId")));
			st.execute(String.format("SELECT * FROM `References` WHERE CitationId = %d", Integer.valueOf((String) paper.get("IndexId"))));
//			st.execute("SELECT * FROM `References` WHERE CitationId = 999");
			rs = st.getResultSet();
			ArrayList arrayRef = new ArrayList();
			while(rs.next()){
//				System.out.println(rs.getInt("ReferenceId"));
				arrayRef.add(rs.getString("ReferenceId"));
			}
			paper.put("Ref", arrayRef);
			System.out.println(paper);
			ArrayList arrayPaper = new ArrayList();
			arrayPaper.add(paper);
			paperSetDic.put("l0", arrayPaper);
			getRequest("l1", conn, ((ArrayList)((HashMap)arrayPaper.get(0)).get("Ref")));
			for (int i =0; i< ((ArrayList)paperSetDic.get("l1")).size(); i++){
				HashMap dicPaper2 = (HashMap) ((ArrayList)paperSetDic.get("l1")).get(i);
				getRequest("l2", conn, ((ArrayList)dicPaper2.get("Ref")));
			}
			for (int i =0; i< ((ArrayList)paperSetDic.get("l2")).size(); i++){
				HashMap dicPaper2 = (HashMap) ((ArrayList)paperSetDic.get("l2")).get(i);
				getRequest("l3", conn, ((ArrayList)dicPaper2.get("Ref")));
			}
			JSONObject jsonObj = new JSONObject(paperSetDic);
			FileWriter file = new FileWriter("/Users/man27382210/Documents/mapreduceL3YearAbs.json");
	        try {
	            file.write(jsonObj.toJSONString());
	            System.out.println("Successfully Copied JSON Object to File...");
//	            System.out.println("\nJSON Object: " + jsonObj);
	 
	        } catch (IOException e) {
	            e.printStackTrace();
	 
	        } finally {
	            file.flush();
	            file.close();
	        }
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
		finally
		{
				try{
					if (conn != null){
						conn.close();
					}
				}
				catch (Exception e){
					
				}
		}
	}

	public static void getRequest(String level, Connection conn, ArrayList paperRefArray){
		for (int i = 0 ; i < paperRefArray.size();i++){
			Statement st;
			try {
				st = conn.createStatement();
				st.execute(String.format("SELECT * FROM Papers WHERE IndexId = %d", Integer.valueOf((String) paperRefArray.get(i))));
				ResultSet rs = st.getResultSet();
				HashMap paper = new HashMap();
				while(rs.next()){
					paper.put("IndexId", rs.getString("IndexId").toString());
					paper.put("title", rs.getString("title").toString());
					paper.put("date", rs.getString("date").toString());
					paper.put("description", rs.getString("description").toString());
					System.out.println(paper);
				}
				st.execute(String.format("SELECT * FROM `References` WHERE CitationId = %d", Integer.valueOf((String) paper.get("IndexId"))));
				rs = st.getResultSet();
				ArrayList arrayRef = new ArrayList();
				while(rs.next()){
//					System.out.println(rs.getInt("ReferenceId"));
					arrayRef.add(rs.getString("ReferenceId"));
				}
				paper.put("Ref", arrayRef);
				if (paperSetDic.containsKey(level)){
					((ArrayList)paperSetDic.get(level)).add(paper);
				}else{
					ArrayList paperList = new ArrayList();
					paperList.add(paper);
					paperSetDic.put(level, paperList);
				}
				System.out.println("paperSetDic : "+paperSetDic);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		System.out.println("paperSetDic loop end: "+paperSetDic);
	}
}
