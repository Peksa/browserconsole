package util;

import play.libs.F.EventStream;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;

public class Listener<T> implements EntryListener<Long, T>
{
	public EventStream<T> stream;

	public Listener()
	{
		stream = new EventStream<>();
	}

	@Override
	public void entryAdded(EntryEvent<Long, T> entry)
	{
		stream.publish(entry.getValue());
	}

	@Override
	public void entryEvicted(EntryEvent<Long, T> entry)
	{
	}

	@Override
	public void entryRemoved(EntryEvent<Long, T> entry)
	{
	}

	@Override
	public void entryUpdated(EntryEvent<Long, T> entry)
	{
	}
}
