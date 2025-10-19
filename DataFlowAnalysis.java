
import java.util.*;

public class DataFlowAnalysis extends LuaASMParser{

    public static HashMap<String, HashSet<Integer>> analyzeRegisterReferences(LuaFunction luaFunction,String register) {

        HashMap<String, HashSet<Integer>> referredByBlock = new HashMap<>();

        for(BasicBlock block:luaFunction.basicBlocks){
            ArrayList<String> instructions=block.instructions;
            for (int i = 0;i<instructions.size();i++) {
                String stringInstruction = instructions.get(i);
                Instruction instruction = new Instruction(stringInstruction);
                String[] operands = instruction.getOperands();
                String blockID = block.label; // 获取基本块标识
                for (String arg: operands) {
                    if(arg.equals(register)){
                        referredByBlock.computeIfAbsent(blockID,k->new HashSet<>()).add(i);
                    }
                }
            }
        }
        return referredByBlock;
    }
    public static HashMap<String, HashSet<Integer>> analyzeOpcodeUses(LuaFunction luaFunction,String opcode) {

        HashMap<String, HashSet<Integer>> referredByBlock = new HashMap<>();

        for(BasicBlock block:luaFunction.basicBlocks){
            ArrayList<String> instructions=block.instructions;
            for (int i = 0;i<instructions.size();i++) {
                String stringInstruction = instructions.get(i);
                Instruction instruction = new Instruction(stringInstruction);
                String insOpcode = instruction.getOpcode();
                String blockID = block.label; // 获取基本块标识
                if(insOpcode.equals(opcode)){
                    referredByBlock.computeIfAbsent(blockID,k->new HashSet<>()).add(i);
                }
            }
        }
        return referredByBlock;
    }

}
