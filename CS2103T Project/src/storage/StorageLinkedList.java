package storage;

import java.util.ListIterator;
import java.util.NoSuchElementException;

public class StorageLinkedList<E> implements ListIterator<E> {
	
	private class Node {
		E item;
		Node prevNode;
		Node nextNode;
		
		public Node(E item, Node prevNode, Node nextNode) {
			this.item = item;
			this.prevNode = prevNode;
			this.nextNode = nextNode;
		}
	}
	
	Node previousNode;
	Node followingNode;
	Node headNode;
	Node tailNode;
	int index;
	
	public StorageLinkedList() {
		headNode = new Node(null, null, null);
		tailNode = new Node(null, headNode, null);
		headNode.nextNode = tailNode;
		previousNode = headNode;
		followingNode = tailNode;
		index = -1; //sentinel value representing empty list
	}
	
	public void add(E item) { // adds item to head of list
		if(index == -1) {
			Node newNode = new Node(item, headNode, tailNode);
			headNode.nextNode = newNode;
			tailNode.prevNode = newNode;
			followingNode = newNode;
			index = 0;
		} else {
			Node newNode = new Node(item, previousNode, followingNode);
			previousNode.nextNode = newNode;
			followingNode.prevNode = newNode;
			previousNode = newNode;
			followingNode = newNode.nextNode;
			index++;
		}
	}
	
	@Override
	public boolean hasNext() {
		return (followingNode != tailNode);
	}
	
	@Override
	public E next() {
		if(!hasNext()){ throw new NoSuchElementException(); }
		
		previousNode = followingNode;
		followingNode = followingNode.nextNode;
		index++;
		return previousNode.item;
	}

	@Override
	public boolean hasPrevious() {
		return (previousNode != headNode);
	}

	@Override
	public E previous() {
		if(!hasPrevious()){ throw new NoSuchElementException(); }

		followingNode = previousNode;
		previousNode = previousNode.prevNode;
		index--;
		return followingNode.item;
	}

	@Override
	public int nextIndex() {
		return index;
	}

	@Override
	public int previousIndex() {
		return index - 1;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void set(E e) {
		throw new UnsupportedOperationException();
		
	}
	
	public void removeAllPrevious() {
		previousNode = headNode;
		previousNode.nextNode = followingNode;
		followingNode.prevNode = headNode;
		if (followingNode == tailNode) {
			index = -1;
		} else {
			index = 0;
		}
	}
	
	public void push(E item) {
		add(item);
	}
	
	public void pushHere(E item) {
		removeAllPrevious();
		push(item);
	}

}