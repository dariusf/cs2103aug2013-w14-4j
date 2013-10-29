package common.undo;

import java.util.ListIterator;
import java.util.NoSuchElementException;

public class DoublyLinkedList<E> implements ListIterator<E> {
	
	private class Node {
		E item;
		Node prevNode;
		Node nextNode;
		
		public Node(E item, Node prevNode, Node nextNode) {
			this.item = item;
			this.prevNode = prevNode;
			this.nextNode = nextNode;
			if (prevNode != null) {
				prevNode.nextNode = this;
			}
			if (nextNode != null) {
				nextNode.prevNode = this;
			}
		}
	}
	
	Node previousNode;
	Node headNode;
	Node tailNode;
	int index;
	
	public DoublyLinkedList() {
		headNode = new Node(null, null, null);
		tailNode = new Node(null, headNode, null);
		previousNode = headNode;
		index = -1; //sentinel value representing empty list
	}
	
	public void add(E item) { // adds item to head of list
		if(index == -1) {
			Node newNode = new Node(item, headNode, tailNode);
			previousNode = newNode;
			index = 0;
		} else {
			Node newNode = new Node(item, previousNode, previousNode.nextNode);
			previousNode = newNode;
			index++;
		}
	}
	
	@Override
	public boolean hasNext() {
		return (previousNode.nextNode != tailNode);
	}
	
	@Override
	public E next() {
		if(!hasNext()){ throw new NoSuchElementException(); }
		
		previousNode = previousNode.nextNode;
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

		E result = previousNode.item;
		previousNode = previousNode.prevNode;
		index--;
		return result;
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
		if (previousNode == headNode) { return; }
		Node nextNode = previousNode.nextNode;
		nextNode.prevNode = headNode;
		headNode.nextNode = nextNode;
		previousNode = headNode;
		if (previousNode.nextNode == tailNode) {
			index = -1;
		} else {
			index = 0;
		}
	}
	
	public void push(E item) {
		add(item);
		previousNode = headNode;
		index = 0;
	}
	
	public void pushHere(E item) {
		removeAllPrevious();
		push(item);
	}

}