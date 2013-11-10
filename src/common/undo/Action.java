package common.undo;

public interface Action {
	void undo();
	void redo();
}