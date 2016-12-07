/*
 * Zed Attack Proxy (ZAP) and its related class files.
 * 
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 * 
 * Copyright 2010 psiinon@gmail.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */

package org.parosproxy.paros.db.paros;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.parosproxy.paros.db.DatabaseException;
import org.parosproxy.paros.db.DbUtils;
import org.parosproxy.paros.db.RecordSessionUrl;
import org.parosproxy.paros.db.TableSessionUrl;

public class ParosTableSessionUrl extends ParosAbstractTable implements TableSessionUrl {
    
    private static final String TABLE_NAME = "SESSION_URL";
    
    private static final String URLID	= "URLID";
    private static final String TYPE	= "TYPE";
    private static final String URL	= "URL";
    
    private PreparedStatement psRead = null;
    private PreparedStatement psInsert = null;
    private CallableStatement psGetIdLastInsert = null;
    private PreparedStatement psGetAlluRLSForType = null;
    private PreparedStatement psDeleteUrls = null;
    private PreparedStatement psDeleteAllUrlsForType = null;

    public ParosTableSessionUrl() {
        
    }
        
    @Override
    protected void reconnect(Connection conn) throws DatabaseException {
        try {
			if (!DbUtils.hasTable(conn, TABLE_NAME)) {
			    // Need to create the table
			    DbUtils.executeAndClose(
			            conn.prepareStatement("CREATE cached TABLE SESSION_URL (urlid bigint generated by default as identity (start with 1), type int not null, url varchar(8192) default '')"));
			}

			psRead	= conn.prepareStatement("SELECT * FROM SESSION_URL WHERE " + URLID + " = ?");
			psInsert = conn.prepareStatement("INSERT INTO SESSION_URL (" + TYPE + ","+ URL + ") VALUES (?, ?)");
			psGetIdLastInsert = conn.prepareCall("CALL IDENTITY();");

			psDeleteUrls = conn.prepareStatement("DELETE FROM SESSION_URL WHERE " + TYPE + " = ? AND " + URL + " = ?");
			psDeleteAllUrlsForType = conn.prepareStatement("DELETE FROM SESSION_URL WHERE " + TYPE + " = ?");

			psGetAlluRLSForType = conn.prepareStatement("SELECT * FROM SESSION_URL WHERE " + TYPE + " = ?");
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
    }
  
	/* (non-Javadoc)
	 * @see org.parosproxy.paros.db.paros.TableSessionUrl#read(long)
	 */
	@Override
	public synchronized RecordSessionUrl read(long urlId) throws DatabaseException {
		try {
			psRead.setLong(1, urlId);
			
			try (ResultSet rs = psRead.executeQuery()) {
				return build(rs);
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}
	
    /* (non-Javadoc)
	 * @see org.parosproxy.paros.db.paros.TableSessionUrl#insert(int, java.lang.String)
	 */
    @Override
	public synchronized RecordSessionUrl insert(int type, String url) throws DatabaseException {
        try {
			psInsert.setInt(1, type);
			psInsert.setString(2, url);
			psInsert.executeUpdate();
			
			long id;
			try (ResultSet rs = psGetIdLastInsert.executeQuery()) {
				rs.next();
				id = rs.getLong(1);
			}
			return read(id);
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
    }
    
    /* (non-Javadoc)
	 * @see org.parosproxy.paros.db.paros.TableSessionUrl#delete(int, java.lang.String)
	 */
    @Override
	public synchronized void delete(int type, String url) throws DatabaseException {
    	try {
			psDeleteUrls.setInt(1, type);
			psDeleteUrls.setString(2, url);
			psDeleteUrls.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
    }
    
    /* (non-Javadoc)
	 * @see org.parosproxy.paros.db.paros.TableSessionUrl#deleteAllUrlsForType(int)
	 */
    @Override
	public synchronized void deleteAllUrlsForType(int type) throws DatabaseException {
    	try {
			psDeleteAllUrlsForType.setInt(1, type);
			psDeleteAllUrlsForType.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
    }
    

    /* (non-Javadoc)
	 * @see org.parosproxy.paros.db.paros.TableSessionUrl#getUrlsForType(int)
	 */
    @Override
	public synchronized List<RecordSessionUrl> getUrlsForType (int type) throws DatabaseException {
    	try {
			psGetAlluRLSForType.setInt(1, type);
			try (ResultSet rs = psGetAlluRLSForType.executeQuery()) {
				List<RecordSessionUrl> result = new ArrayList<>();
				while (rs.next()) {
					result.add(new RecordSessionUrl(rs.getLong(URLID), rs.getInt(TYPE), rs.getString(URL)));
				}
				return result;
			}
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
    }
                
    private RecordSessionUrl build(ResultSet rs) throws DatabaseException {
        try {
			RecordSessionUrl rt = null;
			if (rs.next()) {
			    rt = new RecordSessionUrl(rs.getLong(URLID), rs.getInt(TYPE), rs.getString(URL));            
			}
			return rt;
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
    }

	/* (non-Javadoc)
	 * @see org.parosproxy.paros.db.paros.TableSessionUrl#setUrls(int, java.util.List)
	 */
	@Override
	public void setUrls(int type, List<String> urls) throws DatabaseException {
		this.deleteAllUrlsForType(type);
		for (String url : urls) {
			this.insert(type, url);
		}
	}    
}
