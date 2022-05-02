

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class jsonReader {

	static void read(Connection c, String file) {
		try {
			File f = new File(file);
			Scanner scanner = new Scanner(f);
			StringBuilder sb = new StringBuilder();
			
			while (scanner.hasNextLine()) {
				sb.append(scanner.nextLine());
				Object ob = new JSONParser().parse(sb.toString());
				JSONObject jsO = (JSONObject) ob;
				
				String name = (String)jsO.get("name");
				long hours = (long)jsO.get("hours");
				
				
				String sql = "insert into Spiele (name, hours) values (?, ?;";
				
				PreparedStatement stmt = c.prepareStatement(sql);
				stmt.setString(1, name);
				stmt.setLong(2, hours);
				stmt.executeUpdate();
				stmt.close();
				System.out.println();
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}