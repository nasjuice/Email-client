
package com.naasirjusab.jagclient.interfaces;

import com.naasirjusab.jagclient.beans.JagEmail;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Naasir
 */
public interface JagDAO {
    
    public int create(JagEmail email) throws SQLException;
    
    public ArrayList<JagEmail> readJagEmail(String folder) 
            throws SQLException;
    
    public int updateFolderNames(int id, String folder) throws SQLException;

    public int delete(int id) throws SQLException;
    
    public int create(String folder) throws SQLException;
    
    public ArrayList<String> readFolders() throws SQLException;
    
    public int delete(String folder) throws SQLException;
}
