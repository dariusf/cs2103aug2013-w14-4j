// @author: A0097556M

package storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * An implementation of the abstract class StorageBase. Stores data in an ArrayList
 * @author Matt-Laptop
 *
 * @param <E>
 */

public class RealStorage<E extends Comparable<E>> implements StorageBase<E>{
	
	private ArrayList<E> items;

	public RealStorage () {
		this.items = new ArrayList<>();
	}
	
	public RealStorage (List<E> items) {
		this.items = new ArrayList<>(items);
	}
	
	public RealStorage (RealStorage<E> original) {
		this.items = new ArrayList<>(original.items);
	}

	@Override
	public void insert(int index, E item) {
		if(index == items.size()) {
			items.add(item);
		} else {
			items.add(index, item);
		}
	}

	@Override
	public void remove(int index) {
		items.remove(index);
	}

	@Override
	public void remove(E item) {
		items.remove(item);
	}

	@Override
	public void setState(List<E> newState) {
		items = new ArrayList<>(newState);
	}
	
	@Override
	public void sort () {
		Collections.sort(items);
	}

	@Override
	public Iterator<E> iterator() {
		return items.iterator();
	}

	@Override
	public int size() {
		return items.size();
	}

	@Override
	public E get(int index) {
		return items.get(index);
	}

	@Override
	public int getIndex(E item) {
		return items.indexOf(item);
	}

}
