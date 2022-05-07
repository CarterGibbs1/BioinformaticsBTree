

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteConfig.Pragma;

/**
 * Searches through db file and query file, displays 0 for frequencies not in the database,
 * or number that represents the frequency of the found dna sequence.
 */
public class GeneBankSearchDatabase
{

    //sqlite-jdbc-3.36.0.3.jar is needed in directory, attached it here
    //IMPORTANT: BOTH CREATEBTREE AND SEARCHDATABASE NEED THE FOLLOWING COMMAND IN THE DIR IN TERMINAL BEFORE RUNNING PROGRAM:
    // jar xf sqlite-jdbc-3.36.0.3.jar

    /**
     * Opens a database file and query file, and prints the quantities of matching DNA sequences from the query file.
     *
     * @param args args that get parsed with parseArgs
     * @throws Exception for any rare, untested circumstance that causes the program to not complete
     */
    public static void main(String[] args) throws Exception//pass in a btree probs as arg
    {
        Connection connection = null;
        try
        {
            GeneBankSearchDatabaseArguments a = parseArgs(args);

            // create a database connection
			Class.forName("org.sqlite.JDBC");
			SQLiteConfig config = new SQLiteConfig();

			// Optimize searching, only dangerous if Onyx goes up in flames
			config.setPragma(Pragma.SYNCHRONOUS, "0");
			config.setPragma(Pragma.JOURNAL_MODE, "OFF");
			config.setPragma(Pragma.LOCKING_MODE, "EXCLUSIVE");
				          
			connection = config.createConnection("jdbc:sqlite:" + a.getPathToDatabase());
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

            
            //if debug == 1, create new file to put query results in
	        boolean debug = a.debugLevel() == 1;
	        PrintStream result = null;
	        if(debug) {
	        	
	        	//if the desired location doesn't exist, create query result in main directory
	        	String QLocation = "./data/files_gbk_actual_results/";
	        	if(!Files.exists(Path.of(QLocation))) {
	        		QLocation = "./";
	        	}
	        	
		        File resultFile = new File(QLocation + a.getQueryPathway().substring(a.getQueryPathway().lastIndexOf('/') + 1, a.getQueryPathway().length()) 
		        		+ "_ON_" + a.getPathToDatabase().substring(a.getPathToDatabase().lastIndexOf('/') + 1, a.getPathToDatabase().length()) + ".out");
		        if(resultFile.exists()) {
		        	if(!resultFile.delete()) {
		        		throw new IOException("Already existing result file '" + resultFile.getName() + "' could not be deleted, might be open elsewhere.");
		        	}
		        }
		        resultFile.createNewFile();
		        result = new PrintStream(resultFile);
	        }
            
            // search logic, similar to searchbtree, start at query file, use database to search for dna seq
            File q = new File(a.getQueryPathway());
            Scanner qScan = new Scanner(q);
            String stringResult;
            while (qScan.hasNextLine()) {
                String qCurr = qScan.nextLine().toLowerCase();
                ResultSet rs = statement.executeQuery("select * from btree where dnaseq = '" + qCurr + "';");
                int currFreq;
                if (!rs.isClosed()) {
                    currFreq = rs.getInt("freq");
                } else {
                    currFreq = 0;
                }
                if (currFreq > 0) {
                	stringResult = qCurr + ": " + currFreq;
                	
                    System.out.println(stringResult);
                    if(debug) {
                    	result.println(stringResult);
                    }
                }
                rs.close();
            }
            qScan.close();

        }
        catch(SQLException e)
        {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        }
        finally
        {
            try
            {
                if(connection != null)
                    connection.close();
            }
            catch(SQLException e)
            {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
    }// end of main

    /**
     * Determines if command line arguments are legitimate.
     *
     * @param args the args that are passed in when using this class
     * @return all arguments that were passed in, if legitimate
     * @throws ParseArgumentException if any argument doesn't match the needed format
     */
    public static GeneBankSearchDatabaseArguments parseArgs(String args[]) throws ParseArgumentException {
        if (args.length < 2 || args.length > 3) {
            System.out.println("Usage: java GeneBankSearchDatabase <path_to_SQLite_database> <query_file> [<debug_level>]");
            System.out.println
                    ("<path_to_SQLite_database>: path to the SQLite database");
            System.out.println
                    ("<query_file>: file name of the query.");
            System.out.println
                    ("[<debug level>]: type of output wanted for program. 1 for a dump file output with console. 0 for just console.");
            
            System.err.println("Incorrect number of arguments");
            System.exit(1);
        }
        String dbName = args[0];
        if (!dbName.contains(".db")) {
            throw new ParseArgumentException("Database does not exist");
        }
        File fTwo = new File(args[1]);// should be existing query file in directory
        if (!fTwo.exists() || !fTwo.isFile() || !args[1].contains("query")) {//check extension
            throw new ParseArgumentException("Query file does not exist or is not in pwd.");
        }
        int d = args.length == 3 && Integer.parseInt(args[2]) == 1 ? 1 : 0;
        return new GeneBankSearchDatabaseArguments(args[0], args[1], d);
    }
}