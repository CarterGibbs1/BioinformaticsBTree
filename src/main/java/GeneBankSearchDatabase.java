

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

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
            connection = DriverManager.getConnection(a.getPathToDatabase());
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            // search logic, similar to searchbtree, start at query file, use database to search for dna seq
            File q = new File(a.getQueryPathway());
            Scanner qScan = new Scanner(q);
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
                    System.out.print(qCurr + ": ");
                    System.out.println(currFreq);
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
            throw new ParseArgumentException("Incorrect number of arguments");
        }
        String dbName = args[0];
        if (!dbName.contains(".db")) {
            throw new ParseArgumentException("Database does not exist");
        }
        File fTwo = new File(args[1]);// should be existing query file in directory
        if (!fTwo.exists() || !fTwo.isFile() || !args[1].contains("query")) {//check extension
            throw new ParseArgumentException("Query file does not exist or is not in pwd.");
        }
        int d = 0;
        if (args.length == 3) {
            Integer.parseInt(args[2]);
            if (d != 0) {// only having one option for right now: results on output stream
                //the results will mirror GeneBankSearchBTree, where the results will be printed on console
                throw new ParseArgumentException("Debug level is not valid.");
            }
        }
        return new GeneBankSearchDatabaseArguments(args[0], args[1], d);
    }
}