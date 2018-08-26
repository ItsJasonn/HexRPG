package me.ItsJasonn.HexRPG.Tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.OfflinePlayer;

import me.ItsJasonn.HexRPG.Main.Plugin;

public class SQLManager {
	private String database, ip, port, username, password;
	
	public static boolean using() {
		return Plugin.getCore().getConfig().getBoolean("mysql.enabled");
	}
	
	public SQLManager(String database, String ip, String port, String username, String password) {
		this.database = database;
		this.ip = ip;
		this.port = port;
		this.username = username;
		this.password = password;
	}
	
	public Connection getConnection() throws Exception {
		try {
			String url = "jdbc:mysql://" + this.ip + ":" + this.port + "/" + this.database;
			Class.forName("com.mysql.jdbc.Driver");
			
			Connection connection = DriverManager.getConnection(url, this.username, this.password);
			
			return connection;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void createTable(String tableName, String[] keys) throws Exception {
		try {
			String keysString = "";
			for(int i=0;i<keys.length;i++) {
				if(keysString.length() > 0) {
					keysString += ", ";
				}
				
				if(isInt(keys[i])) {
					keysString += keys[i] + " int";
				} else {
					keysString += keys[i] + " varchar(100)";
				}
			}
			
			Connection connection = getConnection();
			PreparedStatement statement = connection.prepareStatement(
					"CREATE TABLE IF NOT EXISTS " + tableName + "(" +
					"uuid CHAR(36) NOT NULL, " + keysString +
					", PRIMARY KEY(uuid)" +
					")"
					);
			statement.executeUpdate();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void createColumn(String tableName, OfflinePlayer player, String[] keys, Object[] values) {
		try {
			String keysString = "";
			String valuesString = "";
			for(int i=0;i<keys.length;i++) {
				if(keysString.length() > 0) {
					keysString += ", ";
				}
				keysString += keys[i];
			}
			for(int i=0;i<values.length;i++) {
				if(valuesString.length() > 0) {
					valuesString += ", ";
				}
				if(isInt(values[i].toString())) {
					valuesString += values[i];
				} else {
					valuesString += "'" + values[i] + "'";
				}
			}
			
			Connection connection = getConnection();
			PreparedStatement statement = connection.prepareStatement(
					"INSERT INTO " + tableName + "(uuid, " + keysString + ") VALUES (" + player.getUniqueId().toString() + ", " + valuesString + ")");
			statement.executeUpdate();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateCell(String tableName, OfflinePlayer player, String key, String value) throws Exception {
		try {
			Connection connection = getConnection();
			PreparedStatement statement = connection.prepareStatement(
					"UPDATE " + tableName + "\n"
					+ "SET " + key + " = '" + value + "'\n"
					+ "WHERE uuid" + "=" + "'"+ player.getUniqueId().toString() + "'");
			statement.executeUpdate();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateCell(String tableName, String primaryKeyField, String primaryKeyValue, String key, String value) throws Exception {
		try {
			Connection connection = getConnection();
			PreparedStatement statement = connection.prepareStatement(
					"UPDATE " + tableName + "\n"
					+ "SET " + key + " = '" + value + "'\n"
					+ "WHERE " + primaryKeyField + "=" + ""+ primaryKeyValue + "");
			statement.executeUpdate();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void removeRow(String tableName, OfflinePlayer player) throws SQLException {
		try {
			Connection connection = getConnection();
			PreparedStatement statement = connection.prepareStatement("DELETE FROM " + tableName + " WHERE uuid = '" + player.getUniqueId().toString() + "'");
			statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String[] getRow(String tableName, OfflinePlayer player) throws Exception {
		try {
			Connection connection = getConnection();
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE uuid = '" + player.getUniqueId().toString() + "'");
			ResultSet result = statement.executeQuery();
			ResultSetMetaData rsmd = result.getMetaData();
			
			String data = "";
			while(result.next()) {
				for(int i=1;i<=rsmd.getColumnCount();i++) {
					if(data.length() > 0) {
						data += ":";
					}
					data += result.getString(i);
				}
			}
			
			return data.split(":");
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ArrayList<String> getIds(String tableName) throws Exception {
		Connection connection = getConnection();
		PreparedStatement statement = connection.prepareStatement("SELECT uuid FROM " + tableName);
		ResultSet result = statement.executeQuery();
		
		ArrayList<String> ids = new ArrayList<String>();
		while(result.next()) {
			ids.add(result.getString("uuid"));
		}
		return ids;
	}
	
	public int getRowCount(String tableName) {
		try {
			Connection connection = getConnection();
			PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) AS total FROM " + tableName);
			ResultSet result = statement.executeQuery();
			
			result.next();
			return result.getInt("total");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public Object getDataByField(String tableName, String fieldKey, String fieldValue, String key) throws Exception {
		Connection connection = getConnection();
		PreparedStatement statement = connection.prepareStatement("SELECT " + key + " FROM " + tableName + " WHERE " + fieldKey + " = '" + fieldValue + "';");
		ResultSet result = statement.executeQuery();
		
		result.next();
		try {
			return result.getObject(key);
		} catch(SQLException e) {
			return 0;
		}
	}
	
	public boolean hasData(String tableName, String primaryKey) {
		try {
			Connection connection = getConnection();
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + tableName);
			ResultSet result = statement.executeQuery();
			
			ArrayList<String> values = new ArrayList<String>();
			while(result.next()) {
				values.add(result.getString("uuid"));
			}
			
			return values.contains(primaryKey);
		} catch (Exception e) {
			return false;
		}
	}
	
	public String getDatabase() {
		return database;
	}

	public String getIp() {
		return ip;
	}

	public String getPort() {
		return port;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
	
	private boolean isInt(String str) {
		try {
			Integer.parseInt(str);
		} catch(NumberFormatException e) {
			return false;
		}
		return true;
	}
}