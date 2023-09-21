package partTCP;

public class Jumper {
    private String name;
    private int id;
    private double jumpLength;

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public double getJumpLength() {
        return jumpLength;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setJumpLength(double jumpLength) {
        this.jumpLength = jumpLength;
    }
    public Jumper(String name, int id, double jumpLength){
        this.name = name;
        this.id = id;
        this.jumpLength = jumpLength;
    }

    public Jumper(String dat){
        String[] line = dat.split("\t");
        name = line[0];
        id = Integer.getInteger(line[1]);
        jumpLength = Integer.getInteger(line[2]);
    }

    public String toString(){
        return name + '\t' + id + '\t' + jumpLength;
    }
}
