package com.opdetiicos.localException;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class LocalException extends RuntimeException 
{
	private String error;
	private Integer status;
	
	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	

	Map<String,Integer> status_exception = new HashMap<String, Integer>()
			{
		
		{
			put("REGISTRATION IS NOT", 400);
			put("Cannot create Table", 403);
		}
		
			};
	
			Map<String,String> error_exception = new HashMap<String, String>()
					{
				{
				put("REGISTRATION IS NOT","Registration is not done");	
				put("CONNECTION_FAILURE","Failed to make connection with PCS Database");
				put("INVALID_CREDENTIALS","ACCESS_DENIED : Database Username or Password is Incorrect");
				put("INVALID_CLASS_NAME","Driver Class name is Incorrect");
				put("INVALID_URL","Hostname or Port is Incorrect");
				put("INVALID_ARGUMENT_SIZE","Provided Argument size is Invalid");
				put("ARGUMENTS_SIZE_MISMATCH","Provided Arguments Size is not Same");
				put("EXISTING_TABLE","Provided Table name already Exists!");
				put("FALSE_TABLE","Provided Table Doesn't Exists!");
				put("FALSE_DATABASE","Provided Database Doesn't Exists!");
				put("FALSE_COLUMN","Provided Column Doesn't Exists!");
				put("COLUMN_DUPLICATION","Column Adding not Supported. Provided Column Alredy Exists!");
				put("DATA_TRUNCATION","Provided Data is Too long for the Given Column");
				put("EXISTING_DATA_TRUNCATION","Provided Data Size is not Sufficient, Existing data is bigger than the New One");
				put("PINGING_FAILURE","Error while pinging google please check the Internet Connectivity");
				}
					};

					public LocalException(String error) {
						super();
						this.status = status_exception.get(error) ;
					}
					
			public LocalException(String message,String error) {
				super();
				this.status = status_exception.get(message) ;
				this.error = error_exception.get(error);
			}

			
			
}