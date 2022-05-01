

import java.sql.*;

public class runnerJsonWriter {

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
			String url = "jdbc:mysql://localhost:3306/Json";
			String user = "root";
			String pass = "";

			Connection c = getConnection(url, user, pass);

			jsonWriter.write(c, "C:\\Users\\Simon\\EWS\\INFI_2021_22\\jsonFormat.txt");

			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
