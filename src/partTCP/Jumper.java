package partTCP;

import static java.lang.Integer.parseInt;

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

    public Jumper(String dat){
        String[] line = dat.split("\t");
        name = line[0];
        id = parseInt(line[1]);
        jumpLength = 0;
    }

    public String toString(){
        return name + '\t' + jumpLength;
    }
}
