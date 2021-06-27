package ka.el.doit.Model;

public class ToDoTaskModel {
    private int statusTodo;
    private String textTodo, id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStatusTodo() {
        return statusTodo;
    }

    public void setStatusTodo(int statusTodo) {
        this.statusTodo = statusTodo;
    }

    public String getTextTodo() {
        return textTodo;
    }

    public void setTextTodo(String textTodo) {
        this.textTodo = textTodo;
    }
}
