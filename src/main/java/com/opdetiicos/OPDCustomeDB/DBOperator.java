package com.opdetiicos.OPDCustomeDB;

import java.sql.*;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Component;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import com.opdetiicos.localException.LocalException;

import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
public class DBOperator {

	
	
	private static final String PCS_DB_DRIVER_CLASSNAME = "com.mysql.cj.jdbc.Driver";
	private String pcsDBUsername = "";
	private String pcsDBPassword = "";
	private String pcsDBHostName = "";
	private String pcsDBPort = "";
	public static final String ALTER_MODE_DROP = "DROP_COLUMN";
	public static final String ALTER_MODE_ADD = "ADD_COLUMN";
	public static final String ALTER_MODE_MODIFY = "MODIFY_COLUMN";
	public static final String DB_INFORMATION_SCHEMA = "information_schema";
	public static final String DB_PERFORMANCE_SCHEMA = "performance_schema";
	public static final String INNO_DB = "innodb";
	public static final String DB_MYSQL = "mysql";

	/**
	 * Creates a Database Connection for PCS MariaDB.
	 * By calling initPCSDatabase method with UserName as pcsDBUsername and Password as pcsDBPassword given to the Constructor.
	 * </br>
	 * <code>This Constructor have to called only once when Booting up the Application</code>
	 * </br>
	 * <code>Frequent Connections are not needed also not advisable.It may Crash the Application.</code>
	 * @param pcsDBUsername
	 * @param pcsDBPassword
	 */
	public DBOperator(String hostName, String portNumber, String pcsDBUsername,String pcsDBPassword) throws LocalException
	{
		this.pcsDBHostName = hostName;
		this.pcsDBPort = portNumber;
		this.pcsDBUsername = pcsDBUsername;
		this.pcsDBPassword = pcsDBPassword;
	}
	
	/**
	 * Creates a Table if it doesn't exists in (dataBase) with the name (tablename).
	 * </br>
	 * The Table Structure can be given in (constructorArguments) N number of arguments can be passed in the following order.
	 * </br>
	 * Give the name of column followed by the DataType for example <code>SampleTable(ID int,SampleColumn varchar(20)) will be like "ID","int","SampleColumn","varchar(20)"</code>
	 * @CreateCondition OnlyEvenSizeArguments
	 * @param dataBase
	 * @param tablename
	 * @param constructorArguments
	 * @return <code>true</code> (If the table is created Newly)
	 * @throws IllegalArgumentLengthException (If the Given constructorArguments size is not even)
	 * @throws PCSDBException (If the tablename already Exists)
	 */
	@Transactional
	public boolean createOPDTable(String dataBase, String tablename, String... constructorArguments) throws IllegalArgumentException, LocalException
	{
		PreparedStatement dbStatement = null;
		PreparedStatement crtTableStatement = null;
		Connection OPDDBConnections = null;
		Statement OPDDBStatement = null;
		
		if((constructorArguments.length%2) != 0)
		{
			throw new IllegalArgumentException("INVALID_ARGUMENT_SIZE");
		}
		else
		{
			String databaseQuery = "USE "+dataBase+";";
			String crtTableQuery = "CREATE TABLE "+tablename+"(";

			int argument = 0;
			for( ; argument<(constructorArguments.length-2); argument=argument+2)
			{
				crtTableQuery = crtTableQuery.concat(constructorArguments[argument]).concat(" ").concat(constructorArguments[argument+1]).concat(",");
			}
			crtTableQuery = crtTableQuery.concat(constructorArguments[argument]).concat(" ").concat(constructorArguments[argument+1]).concat(");");
		
			try
			{
				Class.forName(PCS_DB_DRIVER_CLASSNAME);
				String OPDDBURL = "jdbc:mysql://".concat(this.pcsDBHostName).concat(":").concat(this.pcsDBPort).concat("/?enabledTLSProtocols=TLSv1.2");
				OPDDBConnections = DriverManager.getConnection(OPDDBURL, pcsDBUsername, pcsDBPassword);
				OPDDBStatement = OPDDBConnections.createStatement();
				
				 dbStatement = OPDDBConnections.prepareStatement(databaseQuery);
				 crtTableStatement = OPDDBConnections.prepareStatement(crtTableQuery);
				 dbStatement.execute();
				 crtTableStatement.executeUpdate();
			}
			catch(SQLSyntaxErrorException syntaxException)
			{
				if(syntaxException.getMessage().contains("already exists"))
				{
					throw new LocalException("EXISTING_TABLE");
				}
			}
			catch(ClassNotFoundException classException)
			{
				throw new LocalException("INVALID_CLASS_NAME");
			}
			catch(CommunicationsException communicationException)
			{
				communicationException.printStackTrace();
				throw new LocalException("INVALID_URL");
			}
		   catch(SQLException sqlException)
			{
				if(sqlException.getMessage().contains("Access denied"))
				{
					throw new LocalException("INVALID_CREDENTIALS");
				}
				sqlException.printStackTrace();
			}
			finally 
			{
			    if (dbStatement != null) {
			        try {
			        	dbStatement.close();
			        } catch (SQLException e) { /* Ignored */}
			    }
			    if (crtTableStatement != null) {
			        try {
			        	crtTableStatement.close();
			        } catch (SQLException e) { /* Ignored */}
			    }
			    
				if (OPDDBConnections != null) 
				{
			        try 
			        {
			        	OPDDBConnections.close();
			        } catch (SQLException e) { /* Ignored */}
			    }
			    if (OPDDBStatement != null) 
			    {
			        try 
			        {
			        	OPDDBStatement.close();
			        } catch (SQLException e) { /* Ignored */}
			    }
			    
			}
         
		}
		return true;
	}
	/**
	 * Alters the Given table with the Given ALTER_MODE.
	 * @ALTER_MODE_DROP Drops the given Column (datatype is not necessary for this Mode)
	 * @ALTER_MODE_ADD Adds the given Column (datatype is necessary for this Mode)
	 * @ALTER_MODE_MODIFY Modifies the given Column (datatype is necessary for this Mode)
	 * @param dataBase
	 * @param tablename
	 * @param alterMode
	 * @param columnName
	 * @param dataType
	 * @return <code>true</code> (If the table Modification is Success)
	 * @throws PCSDBException with the following Error Messages
	 * </br>
	 * EXISTING_DATA_TRUNCATION : If the Provided DataType is not Sufficient for the Existing Column.
	 * </br>
	 * FALSE_DATABASE : When the provided Database is Incorrect
	 * </br>
	 * FALSE_TABLE : When the provided Table is Incorrect
	 * </br>
	 * FALSE_COLUMN : When the provided Column is Incorrect
	 */
	@Transactional
	public boolean alterOPDTable(String dataBase, String tablename, String alterMode, String columnName, String dataType) throws LocalException
	{
		
		PreparedStatement dbStatement = null;
		PreparedStatement crtTableStatement = null;
		Connection OPDDBConnections = null;
		Statement OPDDBStatement = null;
		
		String databaseQuery = "USE "+dataBase+";";
		String alterTableQuery = "ALTER TABLE "+tablename;

		if(alterMode.equals(this.ALTER_MODE_DROP))
		{
			alterTableQuery = alterTableQuery.concat(" ").concat("DROP COLUMN ").concat(columnName).concat(";");
		}
		else if(alterMode.equals(this.ALTER_MODE_ADD))
		{
			alterTableQuery = alterTableQuery.concat(" ").concat("ADD COLUMN ").concat(columnName).concat(" ").concat(dataType).concat(";");
		}
		else if(alterMode.equals(this.ALTER_MODE_MODIFY))
		{
			alterTableQuery = alterTableQuery.concat(" ").concat("MODIFY COLUMN ").concat(columnName).concat(" ").concat(dataType).concat(";");
		}

		try
		{
			Class.forName(PCS_DB_DRIVER_CLASSNAME);
			String OPDDBURL = "jdbc:mysql://".concat(this.pcsDBHostName).concat(":").concat(this.pcsDBPort).concat("/?enabledTLSProtocols=TLSv1.2");
			OPDDBConnections = DriverManager.getConnection(OPDDBURL, pcsDBUsername, pcsDBPassword);
			OPDDBStatement = OPDDBConnections.createStatement();
			dbStatement = OPDDBConnections.prepareStatement(databaseQuery);
			crtTableStatement = OPDDBConnections.prepareStatement(alterTableQuery);
			dbStatement.execute();
			crtTableStatement.executeUpdate();
		}
		catch(ClassNotFoundException classException)
		{
			throw new LocalException("INVALID_CLASS_NAME");
		}
		catch(CommunicationsException communicationException)
		{
			
			throw new LocalException("INVALID_URL");
		}
		catch(SQLException sqlException)
		{
			if(sqlException.getMessage().contains("Unknown database"))
			{
				throw new LocalException("FALSE_DATABASE");
			}
			else if(sqlException.getMessage().contains("doesn't exist"))
			{
				throw new LocalException("FALSE_TABLE");
			}
			else if(sqlException.getMessage().contains("Unknown column"))
			{
				throw new LocalException("FALSE_COLUMN");
			}
			else if(sqlException.getMessage().contains("Duplicate column"))
			{
				throw new LocalException("COLUMN_DUPLICATION");
			}
			else if(sqlException.getMessage().contains("Data truncated"))
			{
				throw new LocalException("EXISTING_DATA_TRUNCATION");
			}
			else if(sqlException.getMessage().contains("Access denied"))
			{
				throw new LocalException("INVALID_CREDENTIALS");
			}
		}
		finally 
		{
		    if (dbStatement != null) {
		        try {
		        	dbStatement.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    if (crtTableStatement != null) {
		        try {
		        	crtTableStatement.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    
			if (OPDDBConnections != null) 
			{
		        try 
		        {
		        	OPDDBConnections.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    if (OPDDBStatement != null) 
		    {
		        try 
		        {
		        	OPDDBStatement.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    
		}
		return true;
	}

	/**
	 * Inserts the values in the Given Columns Associated with the given table.
	 * </br>
	 * Column names should be sent as List<String> form and values can be sent as either String or integer format.
	 * @param dataBase
	 * @param tablename
	 * @param columnNames as Arras.asList("Column1","Column2",...);
	 * @param values as 1,"2","3",4...
	 * @return <code>true</code> if Insert Query Successfully Executed.
	 * @throws IllegalArgumentLengthException If the provided column names length does'nt match with values length.
	 * @throws PCSDBException  with the following Error Messages
	 * </br>
	 * DATA_TRUNCATION : If the Provided Data is longer than the Pre-Specified Value
	 * </br>
	 * FALSE_DATABASE : When the provided Database is Incorrect
	 * </br>
	 * FALSE_TABLE : When the provided Table is Incorrect
	 * </br>
	 * FALSE_COLUMN : When the provided Column is Incorrect
	 */
	@Transactional
	public boolean insertTableValues(String dataBase, String tablename, List<String> columnNames, Object... values) throws IllegalArgumentException, LocalException
	{
		
		
		PreparedStatement dbStatement = null;
		PreparedStatement crtTableStatement = null;
		Connection OPDDBConnections = null;
		Statement OPDDBStatement = null;
		
		String databaseQuery = "USE "+dataBase+";";
		String insrtTableQuery = "INSERT INTO "+tablename+"(";

		Boolean insertedStatus=false;
		
		if(columnNames.size()!=values.length)
		{
			throw new IllegalArgumentException("ARGUMENTS_SIZE_MISMATCH");
		}
		else
		{
			if(columnNames.size()%2==0)
			{
				int columnIterator = 0;
				for( ; columnIterator<columnNames.size()-1; columnIterator++)
				{
					insrtTableQuery = insrtTableQuery.concat(columnNames.get(columnIterator)).concat(",");
				}
				insrtTableQuery = insrtTableQuery.concat(columnNames.get(columnIterator)).concat(") VALUES(");

				int valueIterator = 0;
				for( ; valueIterator<values.length-1; valueIterator++)
				{
					if(values[valueIterator] instanceof String)
					{
						insrtTableQuery = insrtTableQuery.concat("'").concat(values[valueIterator].toString()).concat("'").concat(",");
					}
					else
					{
						insrtTableQuery = insrtTableQuery.concat(values[valueIterator].toString()).concat(",");
					}

				}
				if(values[valueIterator] instanceof String)
				{
					insrtTableQuery = insrtTableQuery.concat("'").concat(values[valueIterator].toString()).concat("'").concat(");");
				}
				else
				{
					insrtTableQuery = insrtTableQuery.concat(values[valueIterator].toString()).concat(");");
				}
			}
			else
			{
				int columnIterator = 0;
				for( ; columnIterator<=columnNames.size()-2; columnIterator++)
				{
					insrtTableQuery = insrtTableQuery.concat(columnNames.get(columnIterator)).concat(",");
				}
				insrtTableQuery = insrtTableQuery.concat(columnNames.get(columnIterator)).concat(") VALUES(");

				int valueIterator = 0;
				for( ; valueIterator<=values.length-2; valueIterator++)
				{
					if(values[valueIterator] instanceof String)
					{
						insrtTableQuery = insrtTableQuery.concat("'").concat(values[valueIterator].toString()).concat("'").concat(",");
					}
					else
					{
						insrtTableQuery = insrtTableQuery.concat(values[valueIterator].toString()).concat(",");
					}

				}
				if(values[valueIterator] instanceof String)
				{
					insrtTableQuery = insrtTableQuery.concat("'").concat(values[valueIterator].toString()).concat("'").concat(");");
				}
				else
				{
					insrtTableQuery = insrtTableQuery.concat(values[valueIterator].toString()).concat(");");
				}
			}
			try
			{
				
				
				Class.forName(PCS_DB_DRIVER_CLASSNAME);
				String OPDDBURL = "jdbc:mysql://".concat(this.pcsDBHostName).concat(":").concat(this.pcsDBPort).concat("/?enabledTLSProtocols=TLSv1.2");
				OPDDBConnections = DriverManager.getConnection(OPDDBURL, pcsDBUsername, pcsDBPassword);
				OPDDBStatement = OPDDBConnections.createStatement();
				
				dbStatement = OPDDBConnections.prepareStatement(databaseQuery);
				crtTableStatement = OPDDBConnections.prepareStatement(insrtTableQuery);
				dbStatement.execute();
				crtTableStatement.executeUpdate();
				insertedStatus=true;
			}
			catch(MysqlDataTruncation dataException)
			{
				throw new LocalException("DATA_TRUNCATION");
				
			}
			catch(CommunicationsException communicationException)
			{
				communicationException.printStackTrace();
				throw new LocalException("INVALID_URL");
			}
			
			catch(ClassNotFoundException classException)
			{
				throw new LocalException("INVALID_CLASS_NAME");
			}
			
			catch(SQLException sqlException)
			{
				insertedStatus=false;
				
				if(sqlException.getMessage().contains("Unknown database"))
				{
					throw new LocalException("FALSE_DATABASE");
				}
				else if(sqlException.getMessage().contains("doesn't exist"))
				{
					throw new LocalException("FALSE_TABLE");
				}
				else if(sqlException.getMessage().contains("Access denied"))
				{
					throw new LocalException("INVALID_CREDENTIALS");
				}
				else if(sqlException.getMessage().contains("Unknown column"))
				{
					throw new LocalException("FALSE_COLUMN");
				}
				return insertedStatus;
			}
			finally {
			    if (dbStatement != null) {
			        try {
			        	dbStatement.close();
			        } catch (SQLException e) { /* Ignored */}
			    }
			    if (crtTableStatement != null) {
			        try {
			        	crtTableStatement.close();
			        } catch (SQLException e) { /* Ignored */}
			    }
			    
				if (OPDDBConnections != null) 
				{
			        try 
			        {
			        	OPDDBConnections.close();
			        } catch (SQLException e) { /* Ignored */}
			    }
			    if (OPDDBStatement != null) 
			    {
			        try 
			        {
			        	OPDDBStatement.close();
			        } catch (SQLException e) { /* Ignored */}
			    }
			    
			}
		}
		return insertedStatus;
		
	}

	/**
	 * Updates the values in the Given Columns Associated with the given table.
	 * Give the name of Column followed by the Value for example <code>ID=2,Status='ON' will be like "ID",2,"Status","ON"</code>
	 * @param dataBase
	 * @param tablename
	 * @param whereColumnIdentifier
	 * @param whereColumnValue (Can be either String or Integer)
	 * @param updateValues (Values Can be either String or Integer)
	 * @return <code>true</code> if Update Query Successfully Executed.
	 * @throws IllegalArgumentLengthException
	 * @throws PCSDBException with the following Error Messages
	 * </br>
	 * DATA_TRUNCATION : If the Provided Data is longer than the Pre-Specified Value
	 * </br>
	 * FALSE_DATABASE : When the provided Database is Incorrect
	 * </br>
	 * FALSE_TABLE : When the provided Table is Incorrect
	 * </br>
	 * FALSE_COLUMN : When the provided Column is Incorrect
	 */
	@Transactional
	public boolean updateTableValues(String dataBase, String tablename, String whereColumnIdentifier,Object whereColumnValue, Object... updateValues) throws IllegalArgumentException, LocalException
	{
		PreparedStatement dbStatement = null;
		PreparedStatement updateTableStatement = null;
		Connection OPDDBConnections = null;
		Statement OPDDBStatement = null;
		
		
		if((updateValues.length%2) != 0)
		{
			throw new IllegalArgumentException("INVALID_ARGUMENT_SIZE");
		}
		else
		{
			String databaseQuery = "USE "+dataBase+";";
			String updateTableQuery = "UPDATE "+tablename+" SET ";
			String updateTableQuerySuffix = "";

			if(whereColumnValue instanceof String)
			{
				updateTableQuerySuffix = " WHERE "+whereColumnIdentifier+"='"+whereColumnValue.toString()+"';";
			}
			else
			{
				updateTableQuerySuffix = " WHERE "+whereColumnIdentifier+"="+whereColumnValue.toString()+";";
			}

			int updateIterator = 0;
			for( ; updateIterator < (updateValues.length-2) ; updateIterator = updateIterator+2)
			{
				if(updateValues[updateIterator+1] instanceof String)
				{
					updateTableQuery = updateTableQuery.concat(updateValues[updateIterator].toString()).concat("='").concat(updateValues[updateIterator+1].toString()).concat("',");
				}
				else
				{
					updateTableQuery = updateTableQuery.concat(updateValues[updateIterator].toString()).concat("=").concat(updateValues[updateIterator+1].toString()).concat(",");
				}

			}

			if(updateValues[updateIterator+1] instanceof String)
			{
				updateTableQuery = updateTableQuery.concat(updateValues[updateIterator].toString()).concat("='").concat(updateValues[updateIterator+1].toString()).concat("'").concat(updateTableQuerySuffix);
			}
			else
			{
				updateTableQuery = updateTableQuery.concat(updateValues[updateIterator].toString()).concat("=").concat(updateValues[updateIterator+1].toString()).concat(updateTableQuerySuffix);
			}
        	try
			{
        		Class.forName(PCS_DB_DRIVER_CLASSNAME);
				String OPDDBURL = "jdbc:mysql://".concat(this.pcsDBHostName).concat(":").concat(this.pcsDBPort).concat("/?enabledTLSProtocols=TLSv1.2");
				OPDDBConnections = DriverManager.getConnection(OPDDBURL, pcsDBUsername, pcsDBPassword);
				OPDDBStatement = OPDDBConnections.createStatement();
        		
				dbStatement = OPDDBConnections.prepareStatement(databaseQuery);
				updateTableStatement = OPDDBConnections.prepareStatement(updateTableQuery);
				dbStatement.execute();
				updateTableStatement.executeUpdate();
			}
			catch(MysqlDataTruncation dataException)
			{
				throw new LocalException("DATA_TRUNCATION");
			}
        	catch(ClassNotFoundException classException)
			{
				throw new LocalException("INVALID_CLASS_NAME");
			}
			catch(CommunicationsException communicationException)
			{
				communicationException.printStackTrace();
				throw new LocalException("INVALID_URL");
			}
			catch(SQLException sqlException)
			{
				if(sqlException.getMessage().contains("Unknown database"))
				{
					throw new LocalException("FALSE_DATABASE");
				}
				else if(sqlException.getMessage().contains("doesn't exist"))
				{
					throw new LocalException("FALSE_TABLE");
				}
				else if(sqlException.getMessage().contains("Unknown column"))
				{
					throw new LocalException("FALSE_COLUMN");
				}
				else if(sqlException.getMessage().contains("Access denied"))
				{
					throw new LocalException("INVALID_CREDENTIALS");
				}
			}
        	finally 
			{
			    if (dbStatement != null) {
			        try {
			        	dbStatement.close();
			        } catch (SQLException e) { /* Ignored */}
			    }
			    if (updateTableStatement != null) {
			        try {
			        	updateTableStatement.close();
			        } catch (SQLException e) { /* Ignored */}
			    }
			    
				if (OPDDBConnections != null) 
				{
			        try 
			        {
			        	OPDDBConnections.close();
			        } catch (SQLException e) { /* Ignored */}
			    }
			    if (OPDDBStatement != null) 
			    {
			        try 
			        {
			        	OPDDBStatement.close();
			        } catch (SQLException e) { /* Ignored */}
			    }
			    
			}
		}
		return true;
	}
	/**
	 * Deletes all the values in the given table.
	 * @param dataBase
	 * @param tablename
	 * @return <code>true</code> if Delete Query Successfully Executed.
	 * @throws PCSDBException with the following Error Messages
	 * </br>
	 * FALSE_DATABASE : When the provided Database is Incorrect
	 * </br>
	 * FALSE_TABLE : When the provided Table is Incorrect
	 */
	@Transactional
	public boolean deleteTableValues(String dataBase, String tablename) throws LocalException
	{
		PreparedStatement dbStatement = null;
		PreparedStatement deleteTableStatement = null;
		Connection OPDDBConnections = null;
		Statement OPDDBStatement = null;
		
		String databaseQuery = "USE "+dataBase+";";
		String deleteTableQuery = "DELETE FROM "+tablename+";";

		try
		{
			Class.forName(PCS_DB_DRIVER_CLASSNAME);
			String OPDDBURL = "jdbc:mysql://".concat(this.pcsDBHostName).concat(":").concat(this.pcsDBPort).concat("/?enabledTLSProtocols=TLSv1.2");
			OPDDBConnections = DriverManager.getConnection(OPDDBURL, pcsDBUsername, pcsDBPassword);
			OPDDBStatement = OPDDBConnections.createStatement();
			
			dbStatement = OPDDBConnections.prepareStatement(databaseQuery);
			deleteTableStatement = OPDDBConnections.prepareStatement(deleteTableQuery);
			dbStatement.execute();
			deleteTableStatement.executeUpdate();
		}
		catch(CommunicationsException communicationException)
		{
			throw new LocalException("INVALID_URL");
		}
		catch(ClassNotFoundException classException)
		{
			throw new LocalException("INVALID_CLASS_NAME");
		}
		catch(SQLException sqlException)
		{
			if(sqlException.getMessage().contains("Unknown database"))
			{
				throw new LocalException("FALSE_DATABASE");
			}
			else if(sqlException.getMessage().contains("doesn't exist"))
			{
				throw new LocalException("FALSE_TABLE");
			}
			else if(sqlException.getMessage().contains("Access denied"))
				{
					throw new LocalException("INVALID_CREDENTIALS");
				}
		}
		finally 
		{
		    if (dbStatement != null) {
		        try {
		        	dbStatement.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    if (deleteTableStatement != null) {
		        try {
		        	deleteTableStatement.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    
			if (OPDDBConnections != null) 
			{
		        try 
		        {
		        	OPDDBConnections.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    if (OPDDBStatement != null) 
		    {
		        try 
		        {
		        	OPDDBStatement.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    
		}

		return true;
	}
	/**
	 * Deletes the single value in the given table with the given whereColumnValue.
	 * @param dataBase
	 * @param tablename
	 * @param whereColumnIdentifier
	 * @param whereColumnValue (can be either String or Integer)
	 * @return <code>true</code> if Delete Query Successfully Executed.
	 * @throws PCSDBException with the following Error Messages
	 * </br>
	 * FALSE_DATABASE : When the provided Database is Incorrect
	 * </br>
	 * FALSE_TABLE : When the provided Table is Incorrect
	 * </br>
	 * FALSE_COLUMN : When the provided Column is Incorrect
	 */
	@Transactional
	public boolean deleteTableValues(String dataBase, String tablename, String whereColumnIdentifier,Object whereColumnValue) throws LocalException
	{
		
		PreparedStatement dbStatement = null;
		PreparedStatement deleteTableStatement = null;
		Connection OPDDBConnections = null;
		Statement OPDDBStatement = null;
		
		String databaseQuery = "USE "+dataBase+";";
		String deleteTableQuery = "";

		if(whereColumnValue instanceof String)
		{
			deleteTableQuery = "DELETE FROM "+tablename+" WHERE "+whereColumnIdentifier+"='"+whereColumnValue.toString()+"';";
		}
		else
		{
			deleteTableQuery = "DELETE FROM "+tablename+" WHERE "+whereColumnIdentifier+"="+whereColumnValue.toString();
		}

		try
		{
			Class.forName(PCS_DB_DRIVER_CLASSNAME);
			String OPDDBURL = "jdbc:mysql://".concat(this.pcsDBHostName).concat(":").concat(this.pcsDBPort).concat("/?enabledTLSProtocols=TLSv1.2");
			OPDDBConnections = DriverManager.getConnection(OPDDBURL, pcsDBUsername, pcsDBPassword);
			OPDDBStatement = OPDDBConnections.createStatement();
			
			 dbStatement = OPDDBConnections.prepareStatement(databaseQuery);
			 deleteTableStatement = OPDDBConnections.prepareStatement(deleteTableQuery);
			 dbStatement.execute();
			 deleteTableStatement.executeUpdate();
		}
		catch(SQLSyntaxErrorException syntaxException)
		{
			if(syntaxException.getMessage().contains("already exists"))
			{
				throw new LocalException("EXISTING_TABLE");
			}
		}
		catch(ClassNotFoundException classException)
		{
			throw new LocalException("INVALID_CLASS_NAME");
		}
		catch(CommunicationsException communicationException)
		{
			communicationException.printStackTrace();
			throw new LocalException("INVALID_URL");
		}
		catch(SQLException sqlException)
		{
			if(sqlException.getMessage().contains("Unknown database"))
			{
				throw new LocalException("FALSE_DATABASE");
			}
			else if(sqlException.getMessage().contains("doesn't exist"))
			{
				throw new LocalException("FALSE_TABLE");
			}
			else if(sqlException.getMessage().contains("Unknown column"))
			{
				throw new LocalException("FALSE_COLUMN");
			}
			else if(sqlException.getMessage().contains("Access denied"))
			{
				throw new LocalException("INVALID_CREDENTIALS");
			}
			sqlException.printStackTrace();

		}
		finally 
		{
		    if (dbStatement != null) {
		        try {
		        	dbStatement.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    if (deleteTableStatement != null) {
		        try {
		        	deleteTableStatement.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    
			if (OPDDBConnections != null) 
			{
		        try 
		        {
		        	OPDDBConnections.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    if (OPDDBStatement != null) 
		    {
		        try 
		        {
		        	OPDDBStatement.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    
		}
		

		return true;
	}
	/**
	 * Deletes the Rows of given deleteValues in the given Table.
	 * @param dataBase
	 * @param tablename
	 * @param whereColumnIdentifier
	 * @param deleteValues
	 * @return <code>true</code> if Delete Query Successfully Executed.
	 * @throws PCSDBException with the following Error Messages
	 * </br>
	 * FALSE_DATABASE : When the provided Database is Incorrect
	 * </br>
	 * FALSE_TABLE : When the provided Table is Incorrect
	 * </br>
	 * FALSE_COLUMN : When the provided Column is Incorrect
	 */
	@Transactional
	public boolean deleteTableValues(String dataBase, String tablename, String whereColumnIdentifier, Object... deleteValues) throws LocalException
	{
		PreparedStatement dbStatement = null;
		PreparedStatement deleteTableStatement = null;
		Connection OPDDBConnections = null;
		Statement OPDDBStatement = null;
		
		String databaseQuery = "USE "+dataBase+";";
		String deleteTableQuery = "DELETE FROM "+tablename+" WHERE "+whereColumnIdentifier+" IN (";

		int deleteIterator = 0;
		for( ; deleteIterator<deleteValues.length-1; deleteIterator++)
		{
			if(deleteValues[deleteIterator] instanceof String)
			{
				deleteTableQuery = deleteTableQuery.concat("'").concat(deleteValues[deleteIterator].toString()).concat("'").concat(",");
			}
			else
			{
				deleteTableQuery = deleteTableQuery.concat(deleteValues[deleteIterator].toString()).concat(",");
			}
		}
		if(deleteValues[deleteIterator] instanceof String)
		{
			deleteTableQuery = deleteTableQuery.concat("'").concat(deleteValues[deleteIterator].toString()).concat("'").concat(");");
		}
		else
		{
			deleteTableQuery = deleteTableQuery.concat(deleteValues[deleteIterator].toString()).concat(");");
		}

		try
		{
			Class.forName(PCS_DB_DRIVER_CLASSNAME);
			String OPDDBURL = "jdbc:mysql://".concat(this.pcsDBHostName).concat(":").concat(this.pcsDBPort).concat("/?enabledTLSProtocols=TLSv1.2");
			OPDDBConnections = DriverManager.getConnection(OPDDBURL, pcsDBUsername, pcsDBPassword);
			OPDDBStatement = OPDDBConnections.createStatement();
			
			 dbStatement = OPDDBConnections.prepareStatement(databaseQuery);
			 deleteTableStatement = OPDDBConnections.prepareStatement(deleteTableQuery);
			dbStatement.executeQuery();
			deleteTableStatement.executeUpdate();
		}
		catch(SQLSyntaxErrorException syntaxException)
		{
			if(syntaxException.getMessage().contains("already exists"))
			{
				throw new LocalException("EXISTING_TABLE");
			}
		}
		catch(ClassNotFoundException classException)
		{
			throw new LocalException("INVALID_CLASS_NAME");
		}
		catch(CommunicationsException communicationException)
		{
			communicationException.printStackTrace();
			throw new LocalException("INVALID_URL");
		}
		catch(SQLException sqlException)
		{
			if(sqlException.getMessage().contains("Unknown database"))
			{
				throw new LocalException("FALSE_DATABASE");
			}
			else if(sqlException.getMessage().contains("doesn't exist"))
			{
				throw new LocalException("FALSE_TABLE");
			}
			else if(sqlException.getMessage().contains("Unknown column"))
			{
				throw new LocalException("FALSE_COLUMN");
			}
			else if(sqlException.getMessage().contains("Access denied"))
			{
				throw new LocalException("INVALID_CREDENTIALS");
			}
			sqlException.printStackTrace();
		}
		finally 
		{
		    if (dbStatement != null) {
		        try {
		        	dbStatement.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    if (deleteTableStatement != null) {
		        try {
		        	deleteTableStatement.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    
			if (OPDDBConnections != null) 
			{
		        try 
		        {
		        	OPDDBConnections.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    if (OPDDBStatement != null) 
		    {
		        try 
		        {
		        	OPDDBStatement.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    
		}

		return true;
	}
	/**
	 * Used to Retreive data from given table and database.
	 * @param dataBase
	 * @param tablename
	 * @param whereColumnIdentifier (Can be empty)
	 * @param whereColumnValue (Can be empty)
	 * @param columnNames will be {@code *} for getting all values and {@code list[columnNames]} for getting values in specified columns
	 * @return <code>ResultSet</code> contained with the specified values
	 * @throws PCSDBException with the following Error Messages
	 * </br>
	 * FALSE_DATABASE : When the provided Database is Incorrect
	 * </br>
	 * FALSE_TABLE : When the provided Table is Incorrect
	 * </br>
	 * FALSE_COLUMN : When the provided Column is Incorrect
	 */
	@Transactional
	public ResultSet getTableVaues(Connection connection,Statement OPDDBStatement,PreparedStatement dbStatement,PreparedStatement selectTableStatement,String dataBase, String tablename, String whereColumnIdentifier, Object whereColumnValue, String... columnNames) throws LocalException
	{
		
		
		String databaseQuery = "USE "+dataBase+";";
		String selectTableQuery = "SELECT ";
		ResultSet resultSet = null;

		int selectIterator = 0;
		for( ; selectIterator<columnNames.length-1; selectIterator++)
		{
			selectTableQuery = selectTableQuery.concat(columnNames[selectIterator]).concat(",");
		}
		selectTableQuery = selectTableQuery.concat(columnNames[selectIterator]).concat(" ");

		selectTableQuery = selectTableQuery.concat("FROM ").concat(tablename).concat(" ");

		if(whereColumnIdentifier!=null && !whereColumnIdentifier.isEmpty() && whereColumnValue!=null)
		{
			if(whereColumnValue instanceof String)
			{
				selectTableQuery = selectTableQuery.concat("WHERE ").concat(whereColumnIdentifier).concat("=").concat("'").concat(whereColumnValue.toString()).concat("';");
			}
			else
			{
				selectTableQuery = selectTableQuery.concat("WHERE ").concat(whereColumnIdentifier).concat("=").concat(whereColumnValue.toString()).concat(";");
			}
		}
		else
		{
			selectTableQuery = selectTableQuery.concat(";");
		}

		try
		{
			Class.forName(PCS_DB_DRIVER_CLASSNAME);
			
			 dbStatement = connection.prepareStatement(databaseQuery);
			 selectTableStatement = connection.prepareStatement(selectTableQuery);
			 dbStatement.execute();
			 resultSet = selectTableStatement.executeQuery();
		}
		catch(SQLSyntaxErrorException syntaxException)
		{
			if(syntaxException.getMessage().contains("already exists"))
			{
				throw new LocalException("EXISTING_TABLE");
			}
		}
		catch(ClassNotFoundException classException)
		{
			throw new LocalException("INVALID_CLASS_NAME");
		}
		catch(CommunicationsException communicationException)
		{
			communicationException.printStackTrace();
			throw new LocalException("INVALID_URL");
		}
		catch(SQLException sqlException)
		{
			if(sqlException.getMessage().contains("Unknown database"))
			{
				throw new LocalException("FALSE_DATABASE");
			}
			else if(sqlException.getMessage().contains("doesn't exist"))
			{
				throw new LocalException("FALSE_TABLE");
			}
			else if(sqlException.getMessage().contains("Unknown column"))
			{
				throw new LocalException("FALSE_COLUMN");
			}
			else if(sqlException.getMessage().contains("Access denied"))
			{
				throw new LocalException("INVALID_CREDENTIALS");
			}
			sqlException.printStackTrace();
		}
		return resultSet;
	}
	
	/**
	 * Checks if the given table Exist in the Given Database.
	 * @param dataBase  - corresponding database.
	 * @param tablename - corresponding table name to be checked.
	 * @return  <code>true</code> if Table Exists in the specified Database.
	 * @throws PCSDBException with the following Error Messages
	 * </br>
	 * FALSE_DATABASE : When the provided Database is Incorrect
	 * </br>
	 */
	@Transactional
	public boolean tableExists(String dataBase, String tablename) throws LocalException
	{
		PreparedStatement dbStatement = null;
		PreparedStatement selectTableStatement = null;
		Connection OPDDBConnections = null;
		Statement OPDDBStatement = null;
		
		String databaseQuery = "USE "+dataBase+";";
		String showTableQuery = "SHOW TABLES;";
		ResultSet resultSet = null;
		try
		{
			Class.forName(PCS_DB_DRIVER_CLASSNAME);
			String OPDDBURL = "jdbc:mysql://".concat(this.pcsDBHostName).concat(":").concat(this.pcsDBPort).concat("/?enabledTLSProtocols=TLSv1.2");
			OPDDBConnections = DriverManager.getConnection(OPDDBURL, pcsDBUsername, pcsDBPassword);
			OPDDBStatement = OPDDBConnections.createStatement();
			
			dbStatement = OPDDBConnections.prepareStatement(databaseQuery);
			selectTableStatement = OPDDBConnections.prepareStatement(showTableQuery);
			dbStatement.executeQuery();
			resultSet = selectTableStatement.executeQuery();

			while(resultSet.next())
			{
				if(resultSet.getString(1)!=null && resultSet.getString(1).equals(tablename))
				{
					return true;
				}
			}
		}
		catch(SQLSyntaxErrorException syntaxException)
		{
			if(syntaxException.getMessage().contains("already exists"))
			{
				throw new LocalException("EXISTING_TABLE");
			}
		}
		catch(ClassNotFoundException classException)
		{
			throw new LocalException("INVALID_CLASS_NAME");
		}
		catch(CommunicationsException communicationException)
		{
			communicationException.printStackTrace();
			throw new LocalException("INVALID_URL");
		}
		catch(SQLException sqlException)
		{
			if(sqlException.getMessage().contains("Access denied"))
			{
				throw new LocalException("INVALID_CREDENTIALS");
			}
			sqlException.printStackTrace();
			
			if(sqlException.getMessage().contains("Unknown database"))
			{
				throw new LocalException("FALSE_DATABASE");
			}
			else
			{
				return false;
			}
		}
		finally 
		{
		    if (dbStatement != null) {
		        try {
		        	dbStatement.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    if (selectTableStatement != null) {
		        try {
		        	selectTableStatement.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    
			if (OPDDBConnections != null) 
			{
		        try 
		        {
		        	OPDDBConnections.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    if (OPDDBStatement != null) 
		    {
		        try 
		        {
		        	OPDDBStatement.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    
		}
		return false;
	}

	/**
	 * Checks if the given Column Exist in the Table of given Database.
	 * @param dataBaseName - respective Database Name
	 * @param tablename - respective Table Name
	 * @param columnName - corresponding Column to be checked.
	 * @return <code>true</code> if Column Exists in the specified Table.
	 * @throws PCSDBException
	 */
	@Transactional
	public boolean columnExists(String dataBaseName, String tablename,String columnName) throws LocalException
	{
		PreparedStatement dbStatement = null;
		PreparedStatement selectTableStatement = null;
		Connection OPDDBConnections = null;
		Statement OPDDBStatement = null;
		
		String databaseQuery = "USE "+dataBaseName+";";
		String showTableQuery = "DESCRIBE "+tablename+";";
		ResultSet resultSet = null;

		try
		{
			Class.forName(PCS_DB_DRIVER_CLASSNAME);
			String OPDDBURL = "jdbc:mysql://".concat(this.pcsDBHostName).concat(":").concat(this.pcsDBPort).concat("/?enabledTLSProtocols=TLSv1.2");
			OPDDBConnections = DriverManager.getConnection(OPDDBURL, pcsDBUsername, pcsDBPassword);
			OPDDBStatement = OPDDBConnections.createStatement();
			
			 dbStatement = OPDDBConnections.prepareStatement(databaseQuery);
			 selectTableStatement = OPDDBConnections.prepareStatement(showTableQuery);
			 dbStatement.executeQuery();
			 resultSet = selectTableStatement.executeQuery();

			while(resultSet.next())
			{
				if(resultSet.getString(1)!=null && resultSet.getString(1).equals(columnName))
				{
					return true;
				}
			}
		}
		catch(SQLSyntaxErrorException syntaxException)
		{
			if(syntaxException.getMessage().contains("already exists"))
			{
				throw new LocalException("EXISTING_TABLE");
			}
		}
		catch(ClassNotFoundException classException)
		{
			throw new LocalException("INVALID_CLASS_NAME");
		}
		catch(CommunicationsException communicationException)
		{
			communicationException.printStackTrace();
			throw new LocalException("INVALID_URL");
		}
		catch(SQLException sqlException)
		{
			if(sqlException.getMessage().contains("Access denied"))
			{
				throw new LocalException("INVALID_CREDENTIALS");
			}
			sqlException.printStackTrace();
			
			if(sqlException.getMessage().contains("Unknown database"))
			{
				throw new LocalException("FALSE_DATABASE");
			}
		}
		finally 
		{
		    if (dbStatement != null) {
		        try {
		        	dbStatement.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    if (selectTableStatement != null) {
		        try {
		        	selectTableStatement.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    
			if (OPDDBConnections != null) 
			{
		        try 
		        {
		        	OPDDBConnections.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    if (OPDDBStatement != null) 
		    {
		        try 
		        {
		        	OPDDBStatement.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    
		}
		return false;
	}

	/**
	 *
	 * @param databaseName
	 * @param argument
	 * @return
	 * @throws PCSDBException
	 */
	@Transactional
	public Boolean excecuteOPDBooleanQuery(String databaseName, String argument) throws LocalException
	{
		PreparedStatement dbStatement = null;
		PreparedStatement updateTableStatement = null;
		Connection OPDDBConnections = null;
		Statement OPDDBStatement = null;
		
		String databaseCreateQuery = "USE "+databaseName+";";
		String databaseQuery = argument;
		try
		{
			Class.forName(PCS_DB_DRIVER_CLASSNAME);
			String OPDDBURL = "jdbc:mysql://".concat(this.pcsDBHostName).concat(":").concat(this.pcsDBPort).concat("/?enabledTLSProtocols=TLSv1.2");
			OPDDBConnections = DriverManager.getConnection(OPDDBURL, pcsDBUsername, pcsDBPassword);
			OPDDBStatement = OPDDBConnections.createStatement();
			
			dbStatement = OPDDBConnections.prepareStatement(databaseCreateQuery);
			updateTableStatement = OPDDBConnections.prepareStatement(databaseQuery);
			dbStatement.execute();
			updateTableStatement.executeUpdate();
		
		}
		catch(SQLSyntaxErrorException syntaxException)
		{
			if(syntaxException.getMessage().contains("already exists"))
			{
				throw new LocalException("EXISTING_TABLE");
			}
		}
		catch(ClassNotFoundException classException)
		{
			throw new LocalException("INVALID_CLASS_NAME");
		}
		catch(CommunicationsException communicationException)
		{
			communicationException.printStackTrace();
			throw new LocalException("INVALID_URL");
		}
		catch(SQLException sqlException)
		{
			if(sqlException.getMessage().contains("Unknown database"))
			{
				throw new LocalException("FALSE_DATABASE");
			}
			else if(sqlException.getMessage().contains("doesn't exist"))
			{
				throw new LocalException("FALSE_TABLE");
			}
			else if(sqlException.getMessage().contains("Unknown column"))
			{
				throw new LocalException("FALSE_COLUMN");
			}
			else if(sqlException.getMessage().contains("Access denied"))
			{
				throw new LocalException("INVALID_CREDENTIALS");
			}
			sqlException.printStackTrace();
			
		}
		finally 
		{
		    if (dbStatement != null) {
		        try {
		        	dbStatement.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    if (updateTableStatement != null) {
		        try {
		        	updateTableStatement.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    
			if (OPDDBConnections != null) 
			{
		        try 
		        {
		        	OPDDBConnections.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    if (OPDDBStatement != null) 
		    {
		        try 
		        {
		        	OPDDBStatement.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		
		}
		return true;
	}

	@Transactional
	public ResultSet excecuteOPDQuery(Connection OPDDBConnections,Statement OPDDBStatement,PreparedStatement dbStatement,PreparedStatement dbCreateStatement,String databaseName,String query) throws LocalException
	{
	
		ResultSet resultSet = null;
		String databaseCreateQuery = "USE "+databaseName+";";
		String databaseQuery = query;
		
		try
		{
		
			Class.forName(PCS_DB_DRIVER_CLASSNAME);

			dbCreateStatement = OPDDBConnections.prepareStatement(databaseCreateQuery);
			dbStatement = OPDDBConnections.prepareStatement(databaseQuery);
			dbCreateStatement.execute();
			resultSet = dbStatement.executeQuery();
		}
		catch(SQLSyntaxErrorException syntaxException)
		{
			if(syntaxException.getMessage().contains("already exists"))
			{
				throw new LocalException("EXISTING_TABLE");
			}
		}
		catch(ClassNotFoundException classException)
		{
			throw new LocalException("INVALID_CLASS_NAME");
		}
		catch(CommunicationsException communicationException)
		{
			communicationException.printStackTrace();
			throw new LocalException("INVALID_URL");
		}
		catch(SQLException sqlException)
		{
			if(sqlException.getMessage().contains("Unknown database"))
			{
				throw new LocalException("FALSE_DATABASE");
			}
			else if(sqlException.getMessage().contains("doesn't exist"))
			{
				throw new LocalException("FALSE_TABLE");
			}
			else if(sqlException.getMessage().contains("Unknown column"))
			{
				throw new LocalException("FALSE_COLUMN");
			}
			else if(sqlException.getMessage().contains("Access denied"))
			{
				throw new LocalException("INVALID_CREDENTIALS");
			}
			sqlException.printStackTrace();
		}
		return resultSet;
	}

	@Transactional
	public boolean isDatabaseExists(String databaseName)
	{
	
		PreparedStatement dbStatement = null;
		Connection OPDDBConnections = null;
		Statement OPDDBStatement = null;
	 
		ResultSet resultSet = null;
		String databaseQuery = "SHOW DATABASES;";

		try
		{
			Class.forName(PCS_DB_DRIVER_CLASSNAME);
			String OPDDBURL = "jdbc:mysql://".concat(this.pcsDBHostName).concat(":").concat(this.pcsDBPort).concat("/?enabledTLSProtocols=TLSv1.2");
			OPDDBConnections = DriverManager.getConnection(OPDDBURL, pcsDBUsername, pcsDBPassword);
			OPDDBStatement = OPDDBConnections.createStatement();
		
			dbStatement = OPDDBConnections.prepareStatement(databaseQuery);
			resultSet = dbStatement.executeQuery();
			while(resultSet.next())
			{
				if(resultSet.getString("Database").equals(databaseName))
				{
					return true;
				}
			}
		}
		catch(SQLSyntaxErrorException syntaxException)
		{
			if(syntaxException.getMessage().contains("already exists"))
			{
				throw new LocalException("EXISTING_TABLE");
			}
		}
		catch(ClassNotFoundException classException)
		{
			throw new LocalException("INVALID_CLASS_NAME");
		}
		catch(CommunicationsException communicationException)
		{
			communicationException.printStackTrace();
			throw new LocalException("INVALID_URL");
		}
	   catch(SQLException sqlException)
		{
			if(sqlException.getMessage().contains("Access denied"))
			{
				throw new LocalException("INVALID_CREDENTIALS");
			}
			sqlException.printStackTrace();
		}
		finally 
		{
		    if (dbStatement != null) {
		        try {
		        	dbStatement.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		     
			if (OPDDBConnections != null) 
			{
		        try 
		        {
		        	OPDDBConnections.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    if (OPDDBStatement != null) 
		    {
		        try 
		        {
		        	OPDDBStatement.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		}
		return false;
	}

	@Transactional
	public Boolean createDatabase(String databaseName)
	{
	
		Connection OPDDBConnections = null;
		Statement OPDDBStatement = null;
		
		String databaseQuery = "CREATE DATABASE "+databaseName+";";
		try
		{
		
			Class.forName(PCS_DB_DRIVER_CLASSNAME);
			String OPDDBURL = "jdbc:mysql://".concat(this.pcsDBHostName).concat(":").concat(this.pcsDBPort).concat("/?enabledTLSProtocols=TLSv1.2");
			OPDDBConnections = DriverManager.getConnection(OPDDBURL, pcsDBUsername, pcsDBPassword);
			OPDDBStatement = OPDDBConnections.createStatement();
		
			OPDDBConnections.prepareStatement(databaseQuery).executeUpdate(databaseQuery);
			return true;
		}
		catch(SQLSyntaxErrorException syntaxException)
		{
			
			if(syntaxException.getMessage().contains("already exists"))
			{
				throw new LocalException("EXISTING_TABLE");
			}
			return false;
		}
		catch(ClassNotFoundException classException)
		{
			throw new LocalException("INVALID_CLASS_NAME");
			
		}
		catch(CommunicationsException communicationException)
		{
			communicationException.printStackTrace();
			throw new LocalException("INVALID_URL");
			
		}
	   catch(SQLException sqlException)
		{
			if(sqlException.getMessage().contains("Access denied"))
			{
				throw new LocalException("INVALID_CREDENTIALS");
			}
			sqlException.printStackTrace();
			return false;
		}
		finally 
		{    
			if (OPDDBConnections != null) 
			{
		        try 
		        {
		        	OPDDBConnections.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    if (OPDDBStatement != null) 
		    {
		        try 
		        {
		        	OPDDBStatement.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		}
	}

	@Transactional
	public ResultSet getTableNames(String databaseName) throws LocalException
	{
		
		PreparedStatement dbStatement = null;
		PreparedStatement tableStatement = null;
		Connection OPDDBConnections = null;
		Statement OPDDBStatement = null;
		
		ResultSet resultSet = null;
		String databaseQuery = "USE "+databaseName+";";
		String tableQuery = "SHOW TABLES;";

		try
		{
		
			Class.forName(PCS_DB_DRIVER_CLASSNAME);
			String OPDDBURL = "jdbc:mysql://".concat(this.pcsDBHostName).concat(":").concat(this.pcsDBPort).concat("/?enabledTLSProtocols=TLSv1.2");
			OPDDBConnections = DriverManager.getConnection(OPDDBURL, pcsDBUsername, pcsDBPassword);
			OPDDBStatement = OPDDBConnections.createStatement();
		
			dbStatement = OPDDBConnections.prepareStatement(databaseQuery);
			tableStatement = OPDDBConnections.prepareStatement(tableQuery);
			dbStatement.execute();
			resultSet = tableStatement.executeQuery();
		}
		catch(SQLSyntaxErrorException syntaxException)
		{
			if(syntaxException.getMessage().contains("already exists"))
			{
				throw new LocalException("EXISTING_TABLE");
			}
		}
		catch(ClassNotFoundException classException)
		{
			throw new LocalException("INVALID_CLASS_NAME");
		}
		catch(CommunicationsException communicationException)
		{
			communicationException.printStackTrace();
			throw new LocalException("INVALID_URL");
		}
	   catch(SQLException sqlException)
		{
		   
		   if(sqlException.getMessage().contains("Unknown database"))
			{
				throw new LocalException("FALSE_DATABASE");
			}
		   else if(sqlException.getMessage().contains("Access denied"))
			{
				throw new LocalException("INVALID_CREDENTIALS");
			}
			sqlException.printStackTrace();
		}
		finally 
		{
		    if (dbStatement != null) {
		        try {
		        	dbStatement.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    if (tableStatement != null) {
		        try {
		        	tableStatement.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    
			if (OPDDBConnections != null) 
			{
		        try 
		        {
		        	OPDDBConnections.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    if (OPDDBStatement != null) 
		    {
		        try 
		        {
		        	OPDDBStatement.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		}
		return resultSet;
	}

	@Transactional
	public ResultSet describeTable(String databaseName, String tableName) throws LocalException
	{
		
		PreparedStatement dbStatement = null;
		PreparedStatement tableStatement = null;
		Connection OPDDBConnections = null;
		Statement OPDDBStatement = null;
		
		ResultSet resultSet = null;
		String databaseQuery = "USE "+databaseName+";";
		String tableQuery = "DESCRIBE "+tableName+";";

		try
		{
			
			Class.forName(PCS_DB_DRIVER_CLASSNAME);
			String OPDDBURL = "jdbc:mysql://".concat(this.pcsDBHostName).concat(":").concat(this.pcsDBPort).concat("/?enabledTLSProtocols=TLSv1.2");
			OPDDBConnections = DriverManager.getConnection(OPDDBURL, pcsDBUsername, pcsDBPassword);
			OPDDBStatement = OPDDBConnections.createStatement();
			
			dbStatement = OPDDBConnections.prepareStatement(databaseQuery);
			tableStatement = OPDDBConnections.prepareStatement(tableQuery);
			dbStatement.executeQuery();
			resultSet = tableStatement.executeQuery();
		}
		catch(SQLSyntaxErrorException syntaxException)
		{
			if(syntaxException.getMessage().contains("already exists"))
			{
				throw new LocalException("EXISTING_TABLE");
			}
		}
		catch(ClassNotFoundException classException)
		{
			throw new LocalException("INVALID_CLASS_NAME");
		}
		catch(CommunicationsException communicationException)
		{
			communicationException.printStackTrace();
			throw new LocalException("INVALID_URL");
		}
		catch(SQLException sqlException)
		{
			if(sqlException.getMessage().contains("Unknown database"))
			{
				throw new LocalException("FALSE_DATABASE");
			}
			else if(sqlException.getMessage().contains("doesn't exist"))
			{
				throw new LocalException("FALSE_TABLE");
			}
			else if(sqlException.getMessage().contains("Access denied"))
			{
				throw new LocalException("INVALID_CREDENTIALS");
			}
			sqlException.printStackTrace();
		}
		finally 
		{
		    if (dbStatement != null) {
		        try {
		        	dbStatement.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    if (tableStatement != null) {
		        try {
		        	tableStatement.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    
			if (OPDDBConnections != null) 
			{
		        try 
		        {
		        	OPDDBConnections.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    if (OPDDBStatement != null) 
		    {
		        try 
		        {
		        	OPDDBStatement.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		}
		return resultSet;
	}
	
	@Transactional
	public void dumpTableValues(String fromDatabase,String fromTable,String toDatabase,String toTable,List<String> columnNames) throws LocalException
	{
		
		Connection OPDDBConnections = null;
		Statement OPDDBStatement = null;
		
		String dumpQuery = "INSERT INTO "+toDatabase+"."+toTable+" SELECT * FROM "+fromDatabase+"."+fromTable+" ON DUPLICATE KEY UPDATE ";

		if(columnNames.size()%2==0)
		{
			int selectIterator=0;

			for( ; selectIterator<columnNames.size()-3; selectIterator=selectIterator+2)
			{
				dumpQuery = dumpQuery+toDatabase+"."+toTable+"."+columnNames.get(selectIterator)+"="+fromDatabase+"."+fromTable+"."+columnNames.get(selectIterator)+",";
			}
			dumpQuery = dumpQuery+toDatabase+"."+toTable+"."+columnNames.get(selectIterator)+"="+fromDatabase+"."+fromTable+"."+columnNames.get(selectIterator)+";";
		}
		else
		{
			int selectIterator=0;

			for( ; selectIterator<columnNames.size()-2; selectIterator=selectIterator+2)
			{
				dumpQuery = dumpQuery+toDatabase+"."+toTable+"."+columnNames.get(selectIterator)+"="+fromDatabase+"."+fromTable+"."+columnNames.get(selectIterator)+",";
			}
			dumpQuery = dumpQuery+toDatabase+"."+toTable+"."+columnNames.get(selectIterator)+"="+fromDatabase+"."+fromTable+"."+columnNames.get(selectIterator)+";";
		}

		try
		{
			
			Class.forName(PCS_DB_DRIVER_CLASSNAME);
			String OPDDBURL = "jdbc:mysql://".concat(this.pcsDBHostName).concat(":").concat(this.pcsDBPort).concat("/?enabledTLSProtocols=TLSv1.2");
			OPDDBConnections = DriverManager.getConnection(OPDDBURL, pcsDBUsername, pcsDBPassword);
			OPDDBStatement = OPDDBConnections.createStatement();
			
			PreparedStatement tableStatement = OPDDBConnections.prepareStatement(dumpQuery);
			tableStatement.executeUpdate();
		}
		catch(SQLSyntaxErrorException syntaxException)
		{
			if(syntaxException.getMessage().contains("already exists"))
			{
				throw new LocalException("EXISTING_TABLE");
			}
		}
		catch(ClassNotFoundException classException)
		{
			throw new LocalException("INVALID_CLASS_NAME");
		}
		catch(CommunicationsException communicationException)
		{
			communicationException.printStackTrace();
			throw new LocalException("INVALID_URL");
		}
		catch(SQLException sqlException)
		{
			if(sqlException.getMessage().contains("Unknown database"))
			{
				throw new LocalException("FALSE_DATABASE");
			}
			else if(sqlException.getMessage().contains("doesn't exist"))
			{
				throw new LocalException("FALSE_TABLE");
			}
			else if(sqlException.getMessage().contains("Access denied"))
			{
				throw new LocalException("INVALID_CREDENTIALS");
			}
			sqlException.printStackTrace();
		}
		finally 
		{
		    
			if (OPDDBConnections != null) 
			{
		        try 
		        {
		        	OPDDBConnections.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		    if (OPDDBStatement != null) 
		    {
		        try 
		        {
		        	OPDDBStatement.close();
		        } catch (SQLException e) { /* Ignored */}
		    }
		}
	}

	
}
