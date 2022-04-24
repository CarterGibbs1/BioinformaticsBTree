package cs321.search;

import com.sun.tools.javac.util.StringUtils;
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
            GeneBankSearchDatabaseArguments a = parseArgs(args);
            /* Below are things we'll have to import into the CreateBTree class */
            /*****************************************************************************/
            // create a database connection
//            connection = DriverManager.getConnection("jdbc:sqlite:btree.db");
//            Statement statement = connection.createStatement();
//            statement.setQueryTimeout(30);  // set timeout to 30 sec.
//
//            statement.executeUpdate("drop table if exists btree");
//            statement.executeUpdate("create table btree (name dnaseq, int freq)");
            /* For all numNodes in btree
             * for (int i = 1; i <= numNodes; i++) {// instead of this, do an in order traversal
             *    BNode current = btree.getNodeAtIndex(i);//from parameter
             *    String dnaseq = current.getStringKey();
             *    int freq = current.getFrequency();
             *    statement.executeUpdate("insert into btree values('dnaseq', freq)");
             * }
             */
            /*****************************************************************************/


            /*****************************************************************************/
            // search logic, similar to searchbtree, start at query file, use database to search for dna seq
            File q = new File(a.getQueryPathway());
            Scanner qScan = new Scanner(q);
            File d = new File(a.getPathToDatabase());
            Scanner dScan = new Scanner(d);
            while (qScan.hasNextLine()) {
                String qCurr = qScan.nextLine().toLowerCase();
                System.out.println(qCurr + ": ");
                int count = 0;
                while (dScan.hasNextLine()) {
                    String dCurr = dScan.nextLine();
                    if (dCurr.contains(qCurr)) {
                        Scanner getFreq = new Scanner(dCurr);
                        int freq = getFreq.nextInt();// make sure freq is the only int on the line
                        // and that there's a space between dna seq and the int
                        System.out.println(freq);
                        getFreq.close();
                        break;// go back to qScan
                    }
                }
                qScan.close();
                dScan.close();
            }
            /*****************************************************************************/
//            ResultSet rs = statement.executeQuery("select * from btree");
//            while(rs.next())
            {
                // read the result set in the db file
                /*
                 *  System.out.println(rs.getString("dnasep") + ": " + rs.getInt("freq"));
                 */
//            System.out.println("name = " + rs.getString("name"));
//            System.out.println("id = " + rs.getInt("id"));
            }
        }
//        catch(SQLException e)
//        {
//            // if the error message is "out of memory",
//            // it probably means no database file is found
//            System.err.println(e.getMessage());
//        }
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
        File f = new File(args[0]);// should be String of actual db file in the directory as this point
        if (!f.exists() || !f.isFile()) {
            throw new ParseArgumentException("Database does not exist");
        }
        File fTwo = new File(args[1]);// should be existing query file in directory
        if (!fTwo.exists() || !fTwo.isFile()) {
            throw new ParseArgumentException("Query file does not exist or is not in pwd.");
        }
        int d = Integer.parseInt(args[2]);
        if (d != 0) {// only having one option for right now: results on output stream
            //the results will mirror GeneBankSearchBTree, where the results will be printed on console
            throw new ParseArgumentException("Debug level is not valid.");
        }
        return new GeneBankSearchDatabaseArguments(args[0], args[1], d);
    }
}

