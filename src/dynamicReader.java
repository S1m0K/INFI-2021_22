import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

public class dynamicReader {

	public static void createTable(String csvInputFilePath, String tableName, Connection c) {
		try {
			File f = new File(csvInputFilePath);
			Scanner s = new Scanner(f);
			String[][] fieldNamesAndDatatypes = readFirstTenLinesAndHeader(s, tableName, c);

			inserIntoTable(tableName, s, fieldNamesAndDatatypes, c);
			selectAllFromTable(tableName, c, fieldNamesAndDatatypes);
			s.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static String[][] readFirstTenLinesAndHeader(Scanner s, String tableName, Connection c) {

		String[][] fNaDt = new String[2][];
		if (s.hasNext()) {
			String firstLine = s.nextLine();
			firstLine.trim();
			fNaDt[0] = firstLine.split(";");
			// filters only first line and stores all fieldnames(Header readed)
		}

		ArrayList<String[]> firstLines = new ArrayList<>();
		int amount = 0;
		while (s.hasNextLine() && amount < 10) {
			String l = s.nextLine();
			l = l.trim();
			String[] splittedL = l.split(";");
			firstLines.add(splittedL);
			//			System.out.println(firstLines.toString());
			// reads first ten lines and stores them
			amount++;
		}
		String[][] firstXDataLines = new String[firstLines.size()][];

		for (int i = 0; i < firstXDataLines.length; i++) {
			firstXDataLines[i] = firstLines.get(i);
		}

		String[][] sortedDataByFields = new String[firstXDataLines[0].length][firstXDataLines.length];
		for (int i = 0; i < firstXDataLines[0].length; i++) {
			for (int j = 0; j < firstXDataLines.length; j++) {
				sortedDataByFields[i][j] = firstXDataLines[j][i];
				// System.out.println(i +"/"+ j);
				// System.out.println(firstXDataLines[j][i]);
				// stores the data sorted by the associated fields in new variable
			}
		}

		String[] datatypes = new String[sortedDataByFields.length];
		for (int i = 0; i < sortedDataByFields.length; i++) {
			datatypes[i] = discoverDatatypeOfField(sortedDataByFields[i]);
			// finds out which datatype fittes
		}
		fNaDt[1] = datatypes;

		for (int i = 0; i < fNaDt[1].length; i++) {
			//System.out.println(fNaDt[1][i]);
		}
		try {
			String sql = createSQL(fNaDt, tableName);
			Statement stmt = c.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Wasn't able to create Table!!");
			System.exit(1);
		}
		String sql2 = createPreparedStatement(tableName, fNaDt);
		insertFirstLines(firstXDataLines, sql2, c, fNaDt);

		return fNaDt;
	}

	public static String discoverDatatypeOfField(String[] field) {
		boolean foreverFalseInt = false;
		boolean foreverFalseDouble = false;
		boolean foreverFalseChar = false;
		boolean foreverFalseDate = false;
		boolean mustBeDoubleIfDigit = false;

		boolean datatypeInt = true;
		boolean datatypeDouble = false;
		boolean datatypeChar = false;
		boolean datatypeDate = false;
		boolean datatypeBool = false;

		int varcharLength = 0;
		for (int i = 0; i < field.length; i++) {
			// finds out if datatype should be int or [double(soon)]
			for (int j = 0; j < field[i].length(); j++) {
				char c = field[i].charAt(j);
				if ((Character.isDigit(c) || c == ',') && (!foreverFalseInt || !foreverFalseDouble)) {
					if (!field[i].endsWith(",00")) {

						foreverFalseInt = true;
						datatypeInt = false;
						datatypeDouble = true;
						mustBeDoubleIfDigit = true;
					} else {
						if (!mustBeDoubleIfDigit) {
							datatypeDouble = false;
							// System.out.println("thats an double: " +c);
						}
						if (!foreverFalseInt) {
							datatypeInt = true;
							//						System.out.println("thats an int: " +c);
						}

					}

				} else {
					datatypeDouble = false;
					datatypeInt = false;
					foreverFalseInt = true;
					foreverFalseDouble = true;
					// System.out.println("not an int or double or ','!: " + c);
				}

			}
			// finds out if datatype should date
			if (!datatypeInt && !datatypeDouble) {
				for (int j = 0; j < field[i].length(); j++) {
					char c = field[i].charAt(j);
					if ((Character.isDigit(c) || c == '-') && !foreverFalseDate) {
						datatypeDate = true;
						// System.out.println("thats an date: " +c);
					} else {
						datatypeDate = false;
						foreverFalseDate = true;
						// System.out.println("not a date: " + c);
					}

				}
			}
			// test if char or varchar or boolean
			if (!datatypeInt && !datatypeDouble && !datatypeDate) {

				if (field[i].length() == 1 && !foreverFalseChar) {
					datatypeChar = true;
				} else {
					if ((field[i].contains("true") || field[i].contains("false"))) {
						datatypeBool = true;

					} else {
						datatypeBool = false;
					}
					// System.out.println("we found a varchar: " +field[i]);
					datatypeChar = false;
					foreverFalseChar = false;
					if (varcharLength <= field[i].length()) {
						varcharLength = field[i].length();
						// define the size of x for datatype varchar(x)
					}
				}
			}
		}
		if (datatypeInt) {
			return "int";
		} else if (datatypeDouble) {
			return "double";
		} else if (datatypeDate) {
			return "date";
		} else if (datatypeChar) {
			return "char";
		} else if (datatypeBool) {
			return "boolean";
		} else {
			return "varchar(" + (varcharLength + 10) + ")";
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
		String sql = createPreparedStatement(tableName, fNaDt);

		while (s.hasNext()) {
			String[] nL = s.nextLine().split(";");
			try {
				PreparedStatement stmt = c.prepareStatement(sql);
				for (int i = 0; i < fNaDt[1].length; i++) {
					if (fNaDt[1][i].contains("char")) {
						stmt.setString(i + 1, nL[i]);
					} else if (fNaDt[1][i].contains("varchar")) {
						stmt.setString(i + 1, nL[i]);
					} else if (fNaDt[1][i].contains("double")) {
						String nL2 = nL[i].replace(",", ".");
						stmt.setDouble(i + 1, Double.parseDouble(nL2));
					} else if (fNaDt[1][i].contains("int")) {
						String[] nL2 = nL[i].split(",");
						stmt.setInt(i + 1, Integer.parseInt(nL2[0]));
					} else if (fNaDt[1][i].contains("date")) {
						Date da = Date.valueOf(nL[i]);
						stmt.setDate(i + 1, da);
					} else if (fNaDt[1][i].contains("boolean")) {
						if (nL[i].contains("true")) {
							stmt.setInt(i + 1, 1);
						} else {
							stmt.setInt(i + 1, 0);
						}
					} else {
						System.out.println("Bad");
					}
				}
				stmt.executeUpdate();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static String createPreparedStatement(String tableName, String[][] fNaDt) {
		String sql = "insert into " + tableName + "(";
		for (int i = 0; i < fNaDt[0].length; i++) {
			if (i != fNaDt[0].length - 1)
				sql = sql + fNaDt[0][i] + ",";
			else
				sql = sql + fNaDt[0][i] + ") values(";
		}
		for (int i = 0; i < fNaDt[0].length; i++) {
			if (i != fNaDt[0].length - 1)
				sql = sql + "?,";
			else
				sql = sql + "?);";
		}
		return sql;
	}

	public static void insertFirstLines(String[][] firstXDataLines, String sql, Connection c, String[][] fNaDt) {
		for (int j = 0; j < firstXDataLines.length; j++) {
			String[] nL = firstXDataLines[j];
			try {
				PreparedStatement stmt = c.prepareStatement(sql);
				for (int i = 0; i < fNaDt[1].length; i++) {
					if (fNaDt[1][i].contains("char")) {
						stmt.setString(i + 1, nL[i]);
					} else if (fNaDt[1][i].contains("varchar")) {
						stmt.setString(i + 1, nL[i]);
					} else if (fNaDt[1][i].contains("double")) {
						String nL2 = nL[i].replace(",", ".");
						stmt.setDouble(i + 1, Double.parseDouble(nL2));
					} else if (fNaDt[1][i].contains("int")) {
						String[] nL2 = nL[i].split(",");
						stmt.setInt(i + 1, Integer.parseInt(nL2[0]));
					} else if (fNaDt[1][i].contains("date")) {
						Date da = Date.valueOf(nL[i]);
						stmt.setDate(i + 1, da);
					} else if (fNaDt[1][i].contains("boolean")) {
						if (nL[i].contains("true")) {
							stmt.setInt(i + 1, 1);
						} else {
							stmt.setInt(i + 1, 0);
						}
					} else {
						System.out.println("Bad2");
					}
				}
				stmt.executeUpdate();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void selectAllFromTable(String tableName, Connection c, String[][] fNaDt) {
		try {
			Statement stmt = c.createStatement();
			String sql = "select tableID";
			for (int i = 0; i < fNaDt[0].length; i++) {
				sql = sql + ", " + fNaDt[0][i];
			}
			sql = sql + " from " + tableName + ";";
			//System.out.println(sql);
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				for (int i = 0; i < fNaDt[1].length; i++) {
					if (fNaDt[1][i].contains("char")) {
						String ch = rs.getString(fNaDt[0][i]);
						System.out.print(ch + "\t");
					} else if (fNaDt[1][i].contains("varchar")) {
						String s = rs.getString(fNaDt[0][i]);
						System.out.print(s + "\t\t");
					} else if (fNaDt[1][i].contains("double")) {
						Double d = rs.getDouble(fNaDt[0][i]);
						System.out.print(d + "\t");
					} else if (fNaDt[1][i].contains("int")) {
						int z = rs.getInt(fNaDt[0][i]);
						System.out.print(z + "\t");
					} else if (fNaDt[1][i].contains("date")) {
						Date da = rs.getDate(fNaDt[0][i]);
						System.out.print(da + "\t");
					} else if (fNaDt[1][i].contains("boolean")) {
						int b = rs.getInt(fNaDt[0][i]);
						if (b == 1) {
							System.out.print("true" + "\t");
						} else {
							System.out.print("false" + "\t");
						}

					}
				}
				System.out.println();
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
