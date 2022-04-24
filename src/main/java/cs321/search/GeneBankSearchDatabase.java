package cs321.search;

import com.sun.tools.javac.util.StringUtils;
import cs321.btree.BTree;//delete
import cs321.btree.TreeObject;
import cs321.common.ParseArgumentException;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.Scanner;

public class GeneBankSearchDatabase
{

    //sqlite-jdbc-3.36.0.3.jar is needed in directory, attached it here

    public static void main(String[] args) throws Exception//pass in a btree probs as arg
    {
        Connection connection = null;
        try
        {

            /*****************************************************************************/
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
                System.out.print(qCurr + ": ");
                ResultSet rs = statement.executeQuery("select * from btree where dnaseq = '" + qCurr + "';");
                int currFreq;
                if (!rs.isClosed()) {
                    currFreq = rs.getInt("freq");
                } else {
                    currFreq = 0;
                }
                System.out.println(currFreq);
                rs.close();
            }
            qScan.close();
            /*****************************************************************************/

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

/***/
//Below is some of the implementation needed to create a database in createbtree
/***/
//IMPORTANT: BOTH CREATEBTREE AND SEARCHDATABASE NEED THE FOLLOWING COMMAND IN THE DIR IN TERMINAL BEFORE RUNNING PROGRAM:
// jar xf sqlite-jdbc-3.36.0.3.jar
/***/
//below is a method that could go atop the createbtree class **/
//	private void inOrderT(BTree b) { pseudo code
// for (int i = 1; i < b.getNumNodes; i++)
//    TestBNodeNoE c = b.getNodeAtIndex(i);
//    String dnaseq = c.getKeys()[1].getStringKey();
//    int freq = c.getKeys()[1].getFrequency();
//    statement.executeUpdate("insert into btree (dnaseq, freq) values (\'" + dnaseq + "\', " + freq + ");");
// end for loop
/***/
//public static void main(String[] args) throws Exception//pass in a btree probs as arg
//{
//    Connection connection = null;
//    try
//    {
//
//
//        /*Test for a driver*/
//        /*********/
//        // create a database connection
//        connection = DriverManager.getConnection("jdbc:sqlite:btree.db");
//        Statement statement = connection.createStatement();
//        statement.setQueryTimeout(30);  // set timeout to 30 sec.
//        statement.executeUpdate("drop table if exists btree;");
//        statement.executeUpdate("create table btree (dnaseq varchar(255), freq int);");
//
//        DON'T NEED THIS PART, AS WE'LL ALREADY HAVE A BTREE CREATED HERE
//        BTreeAaron b = new BTreeAaron(2, 1, null, "test.txt");
//        TestBNodeNoE root = new TestBNodeNoE(1);// important to start at address 1
//        b.setRoot(root);
//        b.insert(new TreeObjectNoE("a", 1));
//        END OF BTREE CREATED FOR TESTING

//in order traversal here
// see method struct in method imp atop this class
// use inOrderT(TestBNodeNoE(b); instead of
//
//
//        //below is a simple insert, don't need this exactly, but will following syntax for each insert
//        TestBNodeNoE c = b.getNodeAtIndex(1);
//        String dnaseq = c.getKeys()[1].getStringKey();
//        int freq = c.getKeys()[1].getFrequency();
//        statement.executeUpdate("insert into btree (dnaseq, freq) values (\'" + dnaseq + "\', " + freq + ");");
//
//    }
//    catch(SQLException e)
//    {
//        // if the error message is "out of memory",
//        // it probably means no database file is found
//        System.err.println(e.getMessage());
//    }
//    finally
//    {
//        try
//        {
//            if(connection != null)
//                connection.close();
//        }
//        catch(SQLException e)
//        {
//            // connection close failed.
//            System.err.println(e.getMessage());
//        }
//    }
//}// end of main
//}
