import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Klassen {

	static void writeKlassen(Connection c, String file) {
		try {
			File f = new File(file);
			FileWriter fw = new FileWriter(f);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Statement stmt = c.createStatement();
			String sql = "select klasse, anzSchueler, zdatum from klassen;";
			ResultSet rs = stmt.executeQuery(sql);
			int x = 1; 
			
			while (rs.next()) {
				String klasse = rs.getString("klasse");
				int anzS = rs.getInt("anzSchueler");
				Date d = rs.getDate("zdatum");
				String date = df.format(d);
				String s = x + ", " + klasse + ", " + anzS + ", " + date +  "\n";
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
