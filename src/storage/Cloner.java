package storage;

public interface Cloner<T> {
	T clone(T originalItem);
}
