package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Result
{
	public List<Message> messages = new ArrayList<>();
	public Long lastId;
}
