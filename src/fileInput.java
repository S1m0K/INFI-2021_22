import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class fileInput {
	public static void main(String[] args) {
		String[] props = getPropertiesFromConfigFile();
		Connection c = getConnection(props);
		setAutoCommit(c, true);
		dynamicReader.createTable(props[3], props[4], c, Integer.parseInt(props[5]));
	}

	public static Connection getConnection(String[] props) {

		String url = props[0];
		String user = props[1];
		String pass = props[2];

		try {
			Connection con = DriverManager.getConnection(url, user, pass);
			System.out.println("Verbindung erfolgreich hergestellt");
			return con;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

	public static void setAutoCommit(Connection c, boolean ToF) {
		try {
			c.setAutoCommit(ToF);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Set Auto Commit didnt work!");
		}

	}

	public static String[] getPropertiesFromConfigFile() {
		String[] props = new String[6];
		Properties prop = new Properties();
		String fileName = "config.cfg";
		try (FileInputStream fis = new FileInputStream(fileName)) {
			prop.load(fis);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		props[0] = prop.getProperty("url");
		props[1] = prop.getProperty("user");
		props[2] = prop.getProperty("pass");
		props[3] = prop.getProperty("absoluteFilePath");
		props[4] = prop.getProperty("databaseName");
		props[5] = prop.getProperty("tenYoN");

		return props;
	}
}
