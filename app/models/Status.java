package models;

import java.util.Map;

public class Status
{
	public String status;
	public Map<String, String> errors;

	public Status(String status)
	{
		this.status = status;
	}

	public Status(String status, Map<String, String> errors)
	{
		this.status = status;
		this.errors = errors;
	}
}
