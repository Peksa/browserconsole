package models;

import java.io.Serializable;

public class Command implements Comparable<Command>, Serializable
{
	public Long id;
	public String msg;
	
	public Command(long id, String msg)
	{
		this.id = id;
		this.msg = msg;
	}
	
	@Override
	public int compareTo(Command o)
	{
		if (this.id == o.id)
			return 0;
		if (this.id < o.id)
			return -1;
		return 1;
	}
}
