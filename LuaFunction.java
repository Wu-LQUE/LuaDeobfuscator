
import java.util.ArrayList;

public class LuaFunction {
    String name;
    String lineDefined;
    String lastLineDefined;
    String numParams;
    String isVarArg;
    String maxStackSize;
    String source;
    ArrayList<Upvalue> upvalues = new ArrayList<>();

    String constantString;
    //ArrayList<String> constants = new ArrayList<>();
    ArrayList<String> instructions = new ArrayList<>();
    ArrayList<BasicBlock> basicBlocks = new ArrayList<>();
    ArrayList<Integer> basicBlockStartPC = new ArrayList<>();//所有基本块的起始pc

    public ArrayList<BasicBlock> getGarbageBlocks() {
        ArrayList<BasicBlock> garbageBlocks = new ArrayList<>();
        for (BasicBlock currentBlock : this.basicBlocks) {
            if (currentBlock.isGarbageBlock) {
                garbageBlocks.add(currentBlock);
            }
        }
        return garbageBlocks;
    }
    public ArrayList<BasicBlock> getNeedToRefineBlocks() {
        ArrayList<BasicBlock> needToRefineBlocks = new ArrayList<>();
        for (BasicBlock currentBlock : this.basicBlocks) {
            if (!currentBlock.isRefined) {
                needToRefineBlocks.add(currentBlock);
            }
        }
        return needToRefineBlocks;
    }

    public boolean isUpvalueUsed(int index,boolean isInstack){
        for (Upvalue upval:upvalues) {
            if(upval.isInstack()==isInstack&&upval.getIndex()==index){
                return true;
            }
        }
        return false;
    }
    public void resetControlFlowRefinedState(){
        for (BasicBlock block : this.basicBlocks) {
            block.isRefined = false;
        }
    }
    

    public ArrayList<BasicBlock> getParentBlocks(BasicBlock block) {
        ArrayList<BasicBlock> parentBlocks = new ArrayList<>();
        for (BasicBlock currentBlock : this.basicBlocks) {
            int directToPC = currentBlock.directToPC;
            int nextPC1 = currentBlock.nextPC1;
            int nextPC2 = currentBlock.nextPC2;
            int startPC = block.getStartPC();
            if (currentBlock.isGarbageBlock != true) {
                if (directToPC == startPC || nextPC1 == startPC || nextPC2 == startPC) {
                    parentBlocks.add(currentBlock);
                }
            }
        }
        return parentBlocks;
    }

    public BasicBlock getBlockByLabel(String targetLabel) {
        for (BasicBlock basicblock : basicBlocks) {
            if (basicblock.label.equals(targetLabel)) {
                return basicblock;
            }
        }
        return null;
    }
}
