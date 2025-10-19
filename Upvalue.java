public class Upvalue {
    private String name;
    private int index;
    private boolean isInstack;

    public Upvalue(String name, int index, boolean isInstack) {
        this.name = name;
        this.index = index;
        this.isInstack = isInstack;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public boolean isInstack() {
        return isInstack;
    }
    public String toString(){
        return String.format(".upvalue %s %d %b",getName(),getIndex(),isInstack());
    }}
