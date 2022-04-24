

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;


import java.sql.Statement;

public class dynamicTableCreate {
	public static void createTable(String inputFile, String tableName, Connection c) {
		
		try {
			File f = new File(inputFile);
			Scanner s = new Scanner(f);
			String[][] fNaDt = readFirstLine(s);
			String sql = createSQL(fNaDt, tableName);
			Statement stmt = c.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();

			inserIntoTable(tableName, s, fNaDt, c);

			s.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Wasn't able to create Table!!");
			System.exit(1);
		}

	}

	private static String[][] readFirstLine(Scanner s) {
		if (s.hasNext()) {
			String[][] fNaDt = new String[2][];
			String firstLine = s.nextLine(); // First line contains table fields
			firstLine.trim();
			String[] tableFields = firstLine.split(";");
			String[] fieldNames = new String[tableFields.length];
			String[] datatypes = new String[tableFields.length];

			for (int i = 0; i < tableFields.length; i++) {

				String[] tableFieldDatatype = tableFields[i].split(",");
				if (tableFieldDatatype[1].contains("text")) {
					fieldNames[i] = tableFieldDatatype[0];
					datatypes[i] = "varchar(30)";

				} else if (tableFieldDatatype[1].contains("digit")) {
					fieldNames[i] = tableFieldDatatype[0];
					datatypes[i] = "int";

				} else if (tableFieldDatatype[1].contains("date")) {
					fieldNames[i] = tableFieldDatatype[0];
					datatypes[i] = "date";

				}
			}
			fNaDt[0] = fieldNames;
			fNaDt[1] = datatypes;
			return fNaDt;

		} else {
			System.out.println("Faild to read first Line");
			return null;
		}
	}

	private static String createSQL(String[][] fNaDt, String tableName) {
		String[] fieldNames = fNaDt[0];
		String[] datatypes = fNaDt[1];

		String sql = "create table if not exists " + tableName + "(tableID int PRIMARY KEY AUTO_INCREMENT";
		for (int i = 0; i < fieldNames.length; i++) {
			sql = sql + "," + fieldNames[i] + " " + datatypes[i];
		}
		sql = sql + ");";

		return sql;
	}

	private static void inserIntoTable(String tableName, Scanner s, String[][] fNaDt, Connection c) {
		String[] fieldNames = fNaDt[0];
		String[] datatypes = fNaDt[1];
		String sql = "insert into " + tableName + "(";
		for (int i = 0; i < fieldNames.length; i++) {
			if (i != fieldNames.length - 1)
				sql = sql + fieldNames[i] + ",";
			else
				sql = sql + fieldNames[i] + ") values(";
		}
		for (int i = 0; i < fieldNames.length; i++) {
			if (i != fieldNames.length - 1)
				sql = sql + "?,";
			else
				sql = sql + "?);";
		}

		while (s.hasNext()) {
			String[] nL = s.nextLine().split(";");
			try {
				PreparedStatement stmt = c.prepareStatement(sql);
				for (int i = 0; i < datatypes.length; i++) {
					if (datatypes[i].contains("char")) {
						stmt.setString(i + 1, nL[i]);
					} else if (datatypes[i].contains("int")) {
						stmt.setInt(i + 1, Integer.parseInt(nL[i]));
					} else if (datatypes[i].contains("date")) {
						Date da = Date.valueOf(nL[i]);
						stmt.setDate(i + 1, da);
					}
				}
				stmt.executeUpdate();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
