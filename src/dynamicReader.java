import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.util.Scanner;

public class dynamicReader {

	public static void createTable(String csvInputFilePath, String tableName, Connection c,
			int size/* should change the way size works */) {
		try {
			File f = new File(csvInputFilePath);
			Scanner s = new Scanner(f);
			String[][] fieldNamesAndDatatypes = readFirstTenLinesAndHeader(s, size);
			// do inserts
			s.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static String[][] readFirstTenLinesAndHeader(Scanner s, int size) {
		String[][] fNaDt = new String[2][];
		String[][] firstTenDataLines = new String[size][];

		if (s.hasNext()) {
			String firstLine = s.nextLine();
			firstLine.trim();
			fNaDt[0] = firstLine.split(";");
			// filters only first line and stores all fieldnames(Header readed)
		}

		for (int i = 0; i < size; i++) {
			if (s.hasNextLine()) {
				String l = s.nextLine();
				l = l.trim();
				firstTenDataLines[i] = l.split(";");
				// reads first ten lines and stores them
			}
		}

		String[][] sortedDataByFields = new String[firstTenDataLines[0].length][firstTenDataLines.length];
		for (int i = 0; i < firstTenDataLines[0].length; i++) {
			for (int j = 0; j < firstTenDataLines.length; j++) {
				sortedDataByFields[i][j] = firstTenDataLines[j][i];
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
			System.out.println(fNaDt[1][i]);
		}

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

		int varcharLength = 0;
//field[i].charAt(field[i].length()-1)==0 && field[i].charAt(field[i].length()-2)==0
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
				// for (int k = 0; k < decimalPlaces[0].length(); k++) {
				// char c2 = field[i].charAt(k);
				// if (!foreverFalseDouble && c2 != '0') {
				// datatypeDouble = true;
				// System.out.println("thats an double: " +c);
				// }
				// }

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
			// test if char or varchar
			if (!datatypeInt && !datatypeDouble && !datatypeDate) {

				if (field[i].length() == 1 && !foreverFalseChar) {
					datatypeChar = true;
				} else {
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
		} else {
			return "varchar(" + (varcharLength + 10) + ")";
		}
	}
}
