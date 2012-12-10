package models;

import play.data.validation.Required;

public class Command implements Message
{
	private Long id;
	@Required
	public String command;
	
	public Command(long id, String command)
	{
		this.id = id;
		this.command = command;
	}

	@Override
	public int compareTo(Message o)
	{
		if (this.id == o.getId())
			return 0;
		if (this.id < o.getId())
			return -1;
		return 1;
	}

	@Override
	public Long getId()
	{
		return id;
	}
}
