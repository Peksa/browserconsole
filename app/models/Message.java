package models;

import java.io.Serializable;

public class Message implements Serializable, Comparable<Message>
{
	public Long id;
	public Long forId;
	public String response;
	public String browser;
	public String command;
	
	// Response
	public Message(Long id, Long forId, String response, String browser)
	{
		this.id = id;
		this.forId = forId;
		this.response = response;
		this.browser = browser;
	}
	
	// Request
	public Message(long id, String command)
	{
		this.id = id;
		this.command = command;
	}
	
	@Override
	public int compareTo(Message o)
	{
		if (this.id == o.id)
			return 0;
		if (this.id < o.id)
			return -1;
		return 1;
	}
}
