/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uff.ic.swlab.util.recall.coffman.datasets;

/**
 *
 * @author angelo
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnectionPostgres {
   
    private final String user = "CoffmanDatasets";
    private final String password = "coffman";
    private final String nameDataset = "MondialCoffman";
    

    
    public Connection connect() throws ClassNotFoundException {
        String url = String.format("jdbc:postgresql://localhost:5432/%1$s",nameDataset);
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");		
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
            
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
        
    }


}
