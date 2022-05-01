

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



import org.json.simple.JSONObject;

public class jsonWriter {
	@SuppressWarnings("unchecked")
	static void write(Connection c, String file) {
		
	try
	{
		FileWriter fw = new FileWriter(file);
		JSONObject jsonO = new JSONObject();
		StringBuilder sb = new StringBuilder();
		
		
		Statement stmt = c.createStatement();
		String sql = "select name, hours from Spiele;";
		ResultSet rs = stmt.executeQuery(sql);
		
		while(rs.next()) {
			String name = rs.getString("name");
			int h = rs.getInt("hours");
			
			
			jsonO.put("gameName", name);
			jsonO.put("hoursPlayed", h);
			sb.append(jsonO+"\n");
		}
		
		fw.write(sb.toString());
		fw.flush();
		fw.close();
		rs.close();
		stmt.close();
	}catch (IOException  e){
		e.printStackTrace();
	}catch (SQLException e) {
		e.printStackTrace();
	}
}
}
