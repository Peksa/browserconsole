package models;

import play.data.validation.Required;

public class Response implements Message
{
	private Long id;
	@Required
	public Long forId;
	@Required
	public String response;
	public String browser;
	
	public Response(Long id, Long forId, String response, String browser)
	{
		this.id = id;
		this.forId = forId;
		this.response = response;
		this.browser = browser;
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