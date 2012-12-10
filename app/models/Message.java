package models;

import java.io.Serializable;

public interface Message extends Serializable, Comparable<Message>
{
	public Long getId();
}
