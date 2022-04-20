package cs321.search;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GeneBankSearchDatabase
{
    public static void main(String[] args) throws Exception//pass in a btree probs as arg
    {
        Connection connection = null;
        //maybe check to see if btree is legit before continuing at this point
        try
        {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:btree.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            statement.executeUpdate("drop table if exists btree");
            statement.executeUpdate("create table btree (name dnaseq, int freq)");
            /* For all numNodes in btree
             * for (int i = 1; i <= numNodes; i++) {// or better way to inorder traverse
             *    BNode current = btree.getNodeAtIndex(i);//from parameter
             *    String dnaseq = current.getStringKey();
             *    int freq = current.getFrequency();
             *    statement.executeUpdate("insert into btree values('dnaseq', freq)");
             * }
             */
//          statement.executeUpdate("insert into btree values(1, 'CS321')");
//          statement.executeUpdate("insert into btree values(2, 'CS-HU310')");
            ResultSet rs = statement.executeQuery("select * from btree");
            while(rs.next())
            {
                // read the result set, if needed, or it's already stored to a db file
                /*
                 *  System.out.println(rs.getString("dnasep") + ": " + rs.getInt("freq"));
                 */
//            System.out.println("name = " + rs.getString("name"));
//            System.out.println("id = " + rs.getInt("id"));
            }
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
    }
}

