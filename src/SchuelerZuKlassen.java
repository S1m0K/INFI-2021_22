import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class SchuelerZuKlassen {

	static void writeSK(Connection c, String file) {
		try {
			File f = new File(file);
			FileWriter fw = new FileWriter(f);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Statement stmt = c.createStatement();
			String sql = "select SID, KID, zdatum from SzK;";
			ResultSet rs = stmt.executeQuery(sql);
			int x = 1; 
			
			while (rs.next()) {
				int SID = rs.getInt("SID");
				int KID = rs.getInt("KID");
				Date d = rs.getDate("zdatum");
				String date = df.format(d);
				String s = x + ", " + SID + ", " + KID + ", " + date +  "\n";
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
