import java.util.ArrayList;

public class BasicBlock {
    ArrayList<String> instructions = new ArrayList<>();
    String label;
    Boolean isRefined=false;
    Boolean isGarbageBlock = false;

    public int getLabel() {
        return LuaASMParser.getLabel(label);
    }
    public int getStartPC() {
        return LuaASMParser.getLabel(label)-1;
    }

    int directToPC = -1;
    int nextPC1 = -1;
    int nextPC2 = -1;

    public void setDirectToPC(int targetPC) {
        this.nextPC1 = -1;
        this.nextPC2 = -1;
        this.directToPC = targetPC;
    }
    public boolean hasDirectBlock(){
        return this.directToPC!=-1;
    }
    public boolean hasConditionJmp(){
        return this.nextPC1!=-1&&this.nextPC2!=-1;
    }
    public void copyInstructions(BasicBlock targetBlock,int start,int end){
        ArrayList<String> instructions = targetBlock.instructions;
        for (int i = start;i<end;i++) {
            String instruction=instructions.get(i);
            this.instructions.add(instruction);
        }
    }
    public void copyInstruction(BasicBlock targetBlock,int index){
        ArrayList<String> instructions = targetBlock.instructions;
        String instruction=instructions.get(index);
        this.instructions.add(instruction);
    }
    public int getInstructionsSize(){
        return this.instructions.size();
    }
    public int getLastInstructionIndex(){
        return this.instructions.size()-1;
    }
    public void copySuccessorInfo(BasicBlock targetBlock){
        this.directToPC=targetBlock.directToPC;
        this.nextPC1=targetBlock.nextPC1;
        this.nextPC2=targetBlock.nextPC2;
    }
    public String getLastInstruction(){
        return instructions.get(getLastInstructionIndex());
    }
    public void removeInstruction(int index){
        instructions.remove(index);
    }
}
