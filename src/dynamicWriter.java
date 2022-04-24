import java.sql.*;

public class dynamicWriter {

	static Connection getConnection(String url, String user, String pass) {
		try {
			return DriverManager.getConnection(url, user, pass);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		try {
			String url = "jdbc:mysql://localhost:3306/csvwrite";
			String user = "root";
			String pass = ""; 

			Connection c = getConnection(url, user, pass);

			Schueler.writeSchueler(c, "C:\\Users\\Simon\\Desktop\\schueler.csv");
			Klassen.writeKlassen(c, "C:\\Users\\Simon\\Desktop\\klassen.csv");
			SchuelerZuKlassen.writeSK(c, "C:\\Users\\Simon\\Desktop\\SzK_writer.csv");

			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
