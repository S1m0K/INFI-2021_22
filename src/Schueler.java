
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Schueler {

	static void writeSchueler(Connection c, String file) {
		try {
			File f = new File(file);
			FileWriter fw = new FileWriter(f);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Statement stmt = c.createStatement();
			String sql = "select vorname, nachname, ort, age, zdatum from schueler;";
			ResultSet rs = stmt.executeQuery(sql);
			int x = 1; 
			

			while (rs.next()) {
				String vn = rs.getString("vorname");
				String nn = rs.getString("nachname");
				String ort = rs.getString("ort");
				int age = rs.getInt("age");
				Date d = rs.getDate("zdatum");
				String date = df.format(d);
				String s = x + ", " + vn + ", " + nn + ", " + ort + ", " + age + ", " + date + "\n";
				fw.write(s);			
				x++;
			}
			
			rs.close();
			stmt.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}