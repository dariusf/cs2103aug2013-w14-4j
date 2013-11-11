// @author: A0097556M

package storage;

import java.util.Iterator;
import java.util.List;

public interface StorageBase<E extends Comparable<E>>{
	
	public void insert (int index, E item) throws ArrayIndexOutOfBoundsException;
	public void remove (int index) throws ArrayIndexOutOfBoundsException;
	public void remove (E item);
	public void setState (List<E> items);
	public void sort ();
	public E get (int index) throws ArrayIndexOutOfBoundsException;
	public int getIndex (E item);
	public int size ();
	public Iterator<E> iterator ();
	
}
