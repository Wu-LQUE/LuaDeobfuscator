import android.content.Context;
import android.util.Log;

import com.lque.luahelper.Utils;

import java.io.File;
import java.io.PrintStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LuaASMParser {
    public static final String OP_TFORLOOP = "tforloop";
    public static final String OP_TESTSET = "testset";
    public static final String OP_TEST = "test";
    public static final String OP_LT = "lt";
    public static final String OP_LE = "le";
    public static final String OP_EQ = "eq";
    public static final String OP_LOADBOOL = "loadbool";
    public static final String OP_FORLOOP = "forloop";
    public static final String OP_FORPREP = "forprep";
    public static final String OP_JMP = "jmp";
    public static final String OP_RETURN = "return";
    public static final String OP_TAILCALL = "tailcall";

    //非结束指令
    public static final String OP_MOVE = "move";
    public static final String OP_LOADK = "loadk";
    public static final String OP_LOADKX = "loadkx";
    public static final String OP_LOADNIL = "loadnil";
    public static final String OP_GETUPVAL = "getupval";
    public static final String OP_GETTABUP = "gettabup";
    public static final String OP_GETTABLE = "gettable";
    public static final String OP_SETTABUP = "settabup";
    public static final String OP_SETUPVAL = "setupval";
    public static final String OP_SETTABLE = "settable";
    public static final String OP_NEWTABLE = "newtable";
    public static final String OP_SELF = "self";
    public static final String OP_ADD = "add";
    public static final String OP_SUB = "sub";
    public static final String OP_MUL = "mul";
    public static final String OP_DIV = "div";
    public static final String OP_MOD = "mod";
    public static final String OP_POW = "pow";
    public static final String OP_UNM = "unm";
    public static final String OP_NOT = "not";
    public static final String OP_LEN = "len";
    public static final String OP_CONCAT = "concat";
    public static final String OP_CALL = "call";
    public static final String OP_TFORCALL = "tforcall";
    public static final String OP_SETLIST = "setlist";
    public static final String OP_CLOSURE = "closure";
    public static final String OP_VARARG = "vararg";
    public static final String OP_EXTRAARG = "extraarg";
    public static final String OP_BAND = "band";
    public static final String OP_BOR = "bor";
    public static final String OP_BXOR = "bxor";
    public static final String OP_SHL = "shl";
    public static final String OP_SHR = "shr";
    public static final String OP_BNOT = "bnot";

    public static String version;
    public static String format;
    public static String endianness;
    public static String int_size;
    public static String size_t_size;
    public static String instruction_size;
    public static String number_format;

    public static BasicBlock isBlockParsed(LuaFunction luaFunction, String blockLabel) {
        ArrayList<BasicBlock> basicBlocks = luaFunction.basicBlocks;
        for (BasicBlock basicblock : basicBlocks) {
            String label = basicblock.label;
            if (label.equals(blockLabel)) {
                return basicblock;
            }
        }
        return null;
    }

    public static BasicBlock getBlockFromPC(LuaFunction luaFunction, int targetPC) {
        String blockLabel = ".label l" + (targetPC + 1);
        ArrayList<BasicBlock> basicBlocks = luaFunction.basicBlocks;
        for (BasicBlock basicblock : basicBlocks) {
            String label = basicblock.label;
            if (label.equals(blockLabel)) {
                return basicblock;
            }
        }
        return null;
    }
    public static Pattern labelPattern = Pattern.compile("l(\\d+)");
    private static Matcher labelPatternMatcher = labelPattern.matcher("");
    public static int getLabel(String label) {
        labelPatternMatcher.reset(label);
        if (labelPatternMatcher.find()) {
            String numberString = labelPatternMatcher.group(1);
            int number = Integer.parseInt(numberString);
            return number;
        } else {
            System.out.println("No match label");
            return -1;
        }
    }

    public static ArrayList<LuaFunction> luaFunctions = new ArrayList<>();

    public static ArrayList<LuaFunction> getSubFunctions(LuaFunction parentFucntion) {
        ArrayList<LuaFunction> subFunctions = new ArrayList<>();
        for (LuaFunction luaFunction : luaFunctions) {
            if (isDirectChildFunction(luaFunction.name, parentFucntion.name)) {
                subFunctions.add(luaFunction);
            }
        }
        return subFunctions;
    }
    private static final Pattern SLASH_PATTERN = Pattern.compile("/");

    public static boolean isDirectChildFunction(String functionName, String parentFunctionName) {
        // 使用正则表达式分割函数名
        String[] functionParts = SLASH_PATTERN.split(functionName);
        String[] parentParts = SLASH_PATTERN.split(parentFunctionName);

        // 如果子函数名不包含父函数名，或者两者长度不满足直接子函数的条件，则返回false
        if (!functionName.startsWith(parentFunctionName + "/") || functionParts.length != parentParts.length + 1) {
            return false;
        }

        // 检查每个部分是否匹配
        for (int i = 0; i < parentParts.length; i++) {
            if (!functionParts[i].equals(parentParts[i])) {
                return false;
            }
        }

        // 最后一个部分是直接子函数
        return true;
    }

    public static int getProcessed_luaFunction_num() {
        return processed_luaFunction_num;
    }

    public static void setProcessed_luaFunction_num(int processed_luaFunction_num) {
        LuaASMParser.processed_luaFunction_num = processed_luaFunction_num;
    }

    public static int getTotal_luaFunction_num() {
        return total_luaFunction_num;
    }

    public static void setTotal_luaFunction_num(int total_luaFunction_num) {
        LuaASMParser.total_luaFunction_num = total_luaFunction_num;
    }

    private static int processed_luaFunction_num;
    private static int total_luaFunction_num;
    public static void parseLuaASM(String input, String destinationPath, Context context) {
        if (true) {
            if (true) {
                ArrayList<String> lasm = removeEmptyLines(input);//去除空行
                int pc = 0;
                pc = readHeadInfo(lasm, pc);
                while (pc < lasm.size()) {
                    pc = readLuaFunction(lasm, pc);
                }
                //设置好处理数量变量
                setTotal_luaFunction_num(luaFunctions.size());
                setProcessed_luaFunction_num(0);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            PrintStream printStream = new PrintStream(destinationPath);
                            printStream.println(version);
                            printStream.println(format);
                            printStream.println(endianness);
                            printStream.println(int_size);
                            printStream.println(size_t_size);
                            printStream.println(instruction_size);
                            printStream.println(number_format);

                            for (LuaFunction luaFunction : luaFunctions) {
                                System.out.println("enter: " + luaFunction.name);
                                ArrayList<String> instructions = luaFunction.instructions;
                                //删除label
                                removeLabels(instructions, luaFunction);
                                //预处理jmp混淆
                                //instructions = preProcessJmpObfuscate(instructions);
                                //预处理无效跳转
                                instructions = preProcessNoUseInstructionObfuscate(instructions);
                                //根基本块
                                BasicBlock rootBlock = new BasicBlock();

                                refinedParseBasicBlocks(luaFunction, instructions);//解析所有基本块
                                Collections.sort(luaFunction.basicBlocks, Comparator.comparingInt(BasicBlock::getLabel));
                                refineBlockByDiffusion(luaFunction);//优化跳转后继块
                                deobfuscateTforloop(luaFunction);//去除tforloop混淆
                                deobfuscateforprep(luaFunction);//去除forprep混淆
                                refineControlFlow(luaFunction);
                                fixBlockEndJmp(luaFunction);//重编写末位跳转语句
                                //removeGarbageBlock(luaFunction);//去除所有垃圾块
                                Collections.sort(luaFunction.basicBlocks, Comparator.comparingInt(BasicBlock::getLabel));
                                //System.out.println(luaFunction.basicBlockStartPC);
                                printStream.println(luaFunction.name);
                                printStream.println(luaFunction.lineDefined);
                                printStream.println(luaFunction.lastLineDefined);
                                printStream.println(luaFunction.numParams);
                                printStream.println(luaFunction.isVarArg);
                                printStream.println(luaFunction.maxStackSize);
                                printStream.println(luaFunction.source);
                                for (Upvalue upvalue : luaFunction.upvalues) {
                                    printStream.println(upvalue.toString());
                                }
//                        for (String constant : luaFunction.constants) {
//                            printStream.println(constant);
//                        }
                                printStream.println(luaFunction.constantString);

                                for (BasicBlock block : luaFunction.basicBlocks) {
                                    printStream.println(block.label);
                                    for (String ins : block.instructions) {
                                        printStream.println(ins);
                                    }
                                }
                                printStream.flush();
                                System.out.println("leave: " + luaFunction.name);
                                processed_luaFunction_num=processed_luaFunction_num+1;
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }}).start();
            }}
    }

    public static ArrayList<Instruction> getInstructionsFromMap(HashMap<String, HashSet<Integer>> referredByBlock, LuaFunction luaFunction) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        for (Map.Entry<String, HashSet<Integer>> entry : referredByBlock.entrySet()) {
            String blockID = entry.getKey();
            BasicBlock block = luaFunction.getBlockByLabel(blockID);
            HashSet<Integer> referredPC = entry.getValue();
            for (Integer i : referredPC) {
                Instruction instruction = new Instruction(block.instructions.get(i));
                instructions.add(instruction);
            }
        }
        return instructions;
    }

    public static ArrayList<Instruction> getInstructionsExceptOpcode(HashMap<String, HashSet<Integer>> referredByBlock, LuaFunction luaFunction, String exceptOpcode) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        for (Map.Entry<String, HashSet<Integer>> entry : referredByBlock.entrySet()) {
            String blockID = entry.getKey();
            BasicBlock block = luaFunction.getBlockByLabel(blockID);
            HashSet<Integer> referredPC = entry.getValue();
            for (Integer i : referredPC) {
                Instruction instruction = new Instruction(block.instructions.get(i));
                if (instruction.getOpcode().equals(exceptOpcode) != true) {
                    instructions.add(instruction);
                }
            }
        }
        return instructions;
    }

    public static boolean isOnlyDefinedOnceInParentFunction(String register, LuaFunction luaFunction) {
        HashMap<String, HashSet<Integer>> registerReferences = DataFlowAnalysis.analyzeRegisterReferences(luaFunction, register);
        ArrayList<Instruction> definedInstructions = getInstructionsExceptOpcode(registerReferences, luaFunction, OP_EQ);//获取除了跳转外的其他指令引用
        if (definedInstructions.size() == 1) {
            return true;
        } else {
            //System.out.println("有条件变量其他用法:" + definedInstructions);
        }
        return false;
    }

    public static ArrayList<String> deobfuscateTforloop(LuaFunction luaFunction) {
        HashMap<String, HashSet<Integer>> tforloopUses = DataFlowAnalysis.analyzeOpcodeUses(luaFunction, OP_TFORLOOP);//获取所有tforloop指令
        //printReferredByBlock(tforloopUses);

        HashMap<String, Integer> RegisterTforloop = new HashMap<>();
        ArrayList<Instruction> instructions = getInstructionsFromMap(tforloopUses, luaFunction);//提取所有指令
        for (int i = 0; i < instructions.size(); i++) {
            Instruction instruction = instructions.get(i);
            String[] operands = instruction.getOperands();
            RegisterTforloop.put(operands[0], RegisterTforloop.getOrDefault(operands[0], 0) + 1);
        }//记录寄存器和使用次数的映射表
        //System.out.println(instructions);
        //大于等于150的寄存器，可能为jmp混淆,进行分析
        ArrayList<String> RegisterTforloop200 = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : RegisterTforloop.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("r") && Integer.parseInt(key.substring(1)) >= 150) {
                RegisterTforloop200.add(key);
            }
        }
        //System.out.println(RegisterTforloop200);
        //获取大于200的可疑变量的所有引用
        for (String register200 : RegisterTforloop200) {
            int R200Int = Integer.parseInt(register200.substring(1));
            HashMap<String, HashSet<Integer>> R200references = DataFlowAnalysis.analyzeRegisterReferences(luaFunction, register200);
            ArrayList<Instruction> R200Instructions = getInstructionsExceptOpcode(R200references, luaFunction, OP_TFORLOOP);//获取除了tforloop本身跳转外的其他指令引用
            ArrayList<LuaFunction> subFunctions = getSubFunctions(luaFunction);
            boolean isUsedByUpvalue = false;
            for (LuaFunction subfunction : subFunctions) {
                if (subfunction.isUpvalueUsed(R200Int, true)) {
                    isUsedByUpvalue = true;
                }//检查子函数有没有直接在栈里引用该变量
            }
            boolean isConditionVarUsedOnlyDefinedOnce = false;
            isConditionVarUsedOnlyDefinedOnce = isOnlyDefinedOnceInParentFunction("r" + (R200Int + 1), luaFunction);
//            System.out.println(isUsedByUpvalue+","+R200Instructions+","+isConditionVarUsedOnlyDefinedOnce);
            if (isUsedByUpvalue == false && R200Instructions.size() == 0 && isConditionVarUsedOnlyDefinedOnce) {
                //没有在除了跳转语句(其他基本块的结束语句)的其他地方使用，也没有在子函数里upvalue堆栈引用,条件变量只定义一次
                //修正跳转块,tforloop改为直接跳转
                for (BasicBlock block : luaFunction.basicBlocks) {
                    ArrayList<String> allinstructions = block.instructions;
                    Instruction instruction = new Instruction(allinstructions.get(allinstructions.size() - 1));
                    String opcode = instruction.getOpcode();
                    if (opcode.equals(OP_TFORLOOP) && instruction.getOperands()[0].equals(register200)) {
                        block.instructions.remove(allinstructions.size() - 1);
                        //block.instructions.add(String.format("%s r%d r%d",OP_MOVE,R200Int,R200Int+1));//rA=rA+1 一般不加入语义也不会改变
                        block.instructions.add("jmp 0 l" + (block.nextPC2 + 1));
                        block.setDirectToPC(block.nextPC2);
                    }
                }
                //System.out.println("tforloop混淆处理完成，变量:" + register200);
            }
        }
        if (RegisterTforloop200.isEmpty()) {
            //System.out.println("无tforloop混淆处理");
        }

//        //替换为jmp
//        ArrayList<String> newInstructions = new ArrayList<>();
//        for (int i = 0; i < instructions.size(); i++) {
//            Instruction instruction = new Instruction(instructions.get(i));
//            String opcode = instruction.getOpcode();
//            String[] operands = instruction.getOperands();
//            if (opcode.equals(OP_TFORLOOP) && RegisterTforloop200.contains(operands[0])) {
//                newInstructions.add("jmp 0 " + operands[1]);
//            } else if (opcode.equals(OP_FORPREP) && RegisterForprep200.contains(operands[0])) {
//                newInstructions.add("jmp 0 " + operands[1]);
//            } else {
//                newInstructions.add(instruction.toString());
//            }
//        }
        return null;
    }

    public static String getBlockIDInfo(BasicBlock block) {
        String ID = "Block" + getLabel(block.label);
        return ID;
    }

    public static void deobfuscateforprep(LuaFunction luaFunction) {
        HashMap<String, HashSet<Integer>> forprepUses = DataFlowAnalysis.analyzeOpcodeUses(luaFunction, OP_FORPREP);
        HashMap<String, Integer> Registerforprep = new HashMap<>();
        ArrayList<Instruction> instructions = getInstructionsFromMap(forprepUses, luaFunction);
        for (int i = 0; i < instructions.size(); i++) {
            Instruction instruction = instructions.get(i);
            String[] operands = instruction.getOperands();
            Registerforprep.put(operands[0], Registerforprep.getOrDefault(operands[0], 0) + 1);
        }

        //大于等于150的寄存器，可能为jmp混淆
        ArrayList<String> Registerforprep200 = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : Registerforprep.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("r") && Integer.parseInt(key.substring(1)) >= 150) {
                Registerforprep200.add(key);
            }
        }
        for (String register200 : Registerforprep200) {
            for (BasicBlock block : luaFunction.basicBlocks) {
                ArrayList<String> allinstructions = block.instructions;
                Instruction instruction = new Instruction(allinstructions.get(allinstructions.size() - 1));
                String opcode = instruction.getOpcode();
                int R200Int = Integer.parseInt(register200.substring(1));
                if (opcode.equals(OP_FORPREP) && instruction.getOperands()[0].equals(register200)) {
                    block.instructions.remove(allinstructions.size() - 1);
                    //block.instructions.add(String.format("%s r%d r%d r%d",OP_SUB,R200Int,R200Int,R200Int+2));//R(A)-=R(A+2)一般不加入语义也不会改变
                    block.instructions.add("jmp 0 l" + (block.directToPC + 1));
                }
            }
            //System.out.println("forprep混淆处理完成，变量:" + register200);
        }
        if (Registerforprep200.size() == 0) {
            //System.out.println("无forperp混淆处理");
        }
    }


    public static ArrayList<String> preProcessNoUseInstructionObfuscate(ArrayList<String> instructions) {
        ArrayList<String> newInstructions = new ArrayList<>();
        for (int i = 0; i < instructions.size(); i++) {
            Instruction instruction = new Instruction(instructions.get(i));
            String opcode = instruction.getOpcode();
            String[] operands = instruction.getOperands();
            switch (opcode) {
                case OP_TFORLOOP:
                    case OP_FORLOOP:
                    int jmpTarget = Instruction.getTarget(operands[1]) - 1;
                    if (jmpTarget < 0) {
                        //无效跳转，该指令不执行
                        newInstructions.add("jmp 0 l" + (i + 1 + 1));//跳过这个指令
                        //System.out.println("无效跳转:"+instruction.toString());//替换为jmp到下一条指令
                    } else {
                        newInstructions.add(instructions.get(i));
                    }
                    break;
                default:
                    newInstructions.add(instructions.get(i));
                    break;

            }
        }
        return newInstructions;
    }


    public static void refinedParseBasicBlocks(LuaFunction luaFunction, ArrayList<String> lines) {//优化过的解析基本块算法
        Stack<Integer> needToParse = new Stack<>();
        needToParse.add(0);//从第一个开始解析

        while (!needToParse.isEmpty()) {
            int pc = needToParse.pop();//取出顶部pc
            String label = ".label l" + (pc + 1);//label即为指令条数(从0开始)，从1开始计数
            BasicBlock isParsedBlock = isBlockParsed(luaFunction, label);
            if (isParsedBlock != null) {//已经解析过了,跳过
                continue;
            }
            BasicBlock currentBlock = new BasicBlock();
            luaFunction.basicBlocks.add(currentBlock);
            ListIterator<String> instructions = lines.listIterator(pc);
            currentBlock.label = label;//重新命名label
            boolean shouldContinue = true;
            while (instructions.hasNext() && shouldContinue == true) {
                Instruction instruction = new Instruction(instructions.next());
                String opcode = instruction.getOpcode();
                String[] operands = instruction.getOperands();
                if (instruction.isGarbageInstruction()) {
                    currentBlock.isGarbageBlock = true;
                    currentBlock.instructions.add(instruction.toString());//保留垃圾指令标志
                    break;
                }

                int jmpTarget;
                //解析普通指令及结束指令
                switch (opcode) {
                    //有两个不相连的后继块的条件跳转指令
                    case OP_TFORLOOP:
                        case OP_FORLOOP:
                        jmpTarget = Instruction.getTarget(operands[1]) - 1;
//                        if (jmpTarget < 0) {
//                            //无效跳转，该指令不执行
//                            //如果直接该指令的后一条指令为首指令，则该指令也判定为结束指令
//                            if (luaFunction.basicBlockStartPC.contains(instructions.nextIndex())) {
//                                currentBlock.instructions.add("jmp 0 l" + (instructions.nextIndex() + 1));//line = pc+1
//                                currentBlock.directToPC = instructions.nextIndex();
//                                needToParse.push(currentBlock.directToPC);
//                                shouldContinue=false;
//                                break;
//                            }
//                            continue;
//                        }

                        currentBlock.instructions.add(instruction.toString());
                        //加入后继块和目标块
                        currentBlock.nextPC1 = instructions.nextIndex();
                        currentBlock.nextPC2 = jmpTarget;
                        needToParse.push(currentBlock.nextPC1);
                        needToParse.push(currentBlock.nextPC2);
                        luaFunction.basicBlockStartPC.add(currentBlock.nextPC1);
                        luaFunction.basicBlockStartPC.add(currentBlock.nextPC2);
                        shouldContinue = false;
                        break;
                    //有两个相连的后继块的条件跳转指令
                    case OP_TESTSET:
                    case OP_TEST:
                    case OP_LT:
                    case OP_LE:
                    case OP_EQ: {
                        currentBlock.instructions.add(instruction.toString());
                        currentBlock.nextPC1 = instructions.nextIndex();
                        currentBlock.nextPC2 = instructions.nextIndex() + 1;
                        needToParse.push(currentBlock.nextPC1);
                        needToParse.push(currentBlock.nextPC2);
                        luaFunction.basicBlockStartPC.add(currentBlock.nextPC1);
                        luaFunction.basicBlockStartPC.add(currentBlock.nextPC2);
                        shouldContinue = false;
                        break;
                    }
                    //有一个直接跳转后继块的指令
                    case OP_FORPREP:
                    case OP_JMP:
                        jmpTarget = Instruction.getTarget(operands[1]) - 1;
                        if (jmpTarget < 0) {
                            //无效跳转，该指令不执行,即为垃圾块
                            currentBlock.isGarbageBlock = true;
                            currentBlock.instructions.add(instruction.toString());//保留垃圾证据
                            shouldContinue = false;
                            break;
                        }
                        currentBlock.instructions.add(instruction.toString());
                        currentBlock.directToPC = jmpTarget;
                        needToParse.push(jmpTarget);
                        luaFunction.basicBlockStartPC.add(currentBlock.directToPC);
                        shouldContinue = false;
                        break;
                    case OP_TAILCALL:
                        //无后继块，直接结束
                        currentBlock.instructions.add(instruction.toString());
                        currentBlock.instructions.add("return " + operands[0] + " 0");
                        shouldContinue = false;
                        break;
                    case OP_RETURN:
                        currentBlock.instructions.add(instruction.toString());
                        shouldContinue = false;
                        break;
                    case OP_LOADBOOL:
                        int jmpflag = Integer.parseInt(operands[2]);//获取argc的数值
                        if (jmpflag != 0) {//c不为0则跳过下一条指令
                            //System.out.println("loadbool skipnext:" + instruction.toString());
                            currentBlock.directToPC = instructions.nextIndex() + 1;
                            currentBlock.instructions.add(instruction.modifyArgument(2, "0").toString());//指令改c为0并加入跳转语句
                            currentBlock.instructions.add("jmp 0 l" + (currentBlock.directToPC + 1));
                            needToParse.push(currentBlock.directToPC);
                            luaFunction.basicBlockStartPC.add(currentBlock.directToPC);
                            shouldContinue = false;
                            break;
                        } else {
                            currentBlock.instructions.add(instruction.toString());
                            break;
                        }
                        //普通指令
                    default:
                        currentBlock.instructions.add(instruction.toString());
                        break;
                }
            }
        }
    }

    //修改末语句使其能跳转到目标中
    public static void fixBlockEndJmp(LuaFunction luaFunction) {
        for (BasicBlock currentBlock : luaFunction.basicBlocks) {
            int lastIndex = currentBlock.instructions.size() - 1;
            String ins = currentBlock.instructions.get(lastIndex);
            Instruction lastInstruction = new Instruction(ins);
            String opcode = lastInstruction.getOpcode();
            String[] operands = lastInstruction.getOperands();
            //分析末指令
            BasicBlock directToBlock;
            BasicBlock nextBlock1, nextBlock2;
            switch (opcode) {
                case OP_TFORLOOP:
                    case OP_FORLOOP:
                    currentBlock.instructions.add("jmp 0 l" + (currentBlock.nextPC1 + 1));
                    break;
                case OP_TEST:
                case OP_LT:
                case OP_LE:
                case OP_EQ:
                case OP_TESTSET:
                    currentBlock.instructions.add("jmp 0 l" + (currentBlock.nextPC1 + 1));
                    currentBlock.instructions.add("jmp 0 l" + (currentBlock.nextPC2 + 1));
                    break;
                case OP_FORPREP:
                case OP_JMP:
                default:
                    //直接跳转没必要修改
                    break;
            }
        }
    }

    //通过扩散法优化基本块
    public static void refineBlockByDiffusion(LuaFunction luaFunction) {
        ArrayList<BasicBlock> garbageBlocks = luaFunction.getGarbageBlocks();
        while (!garbageBlocks.isEmpty()) {
            for (BasicBlock currentBlock : garbageBlocks) {
                ArrayList<BasicBlock> parentBlocks = luaFunction.getParentBlocks(currentBlock);
                //修改父块的末语句
                for (BasicBlock parentBlock : parentBlocks) {
                    int lastIndex = parentBlock.instructions.size() - 1;
                    String ins = parentBlock.instructions.get(lastIndex);
                    Instruction lastInstruction = new Instruction(ins);
                    String opcode = lastInstruction.getOpcode();
                    String[] operands = lastInstruction.getOperands();
                    //看看末指令
                    BasicBlock nextBlock1, nextBlock2;
                    switch (opcode) {
                        case OP_TFORLOOP:
                            case OP_FORLOOP:
                            nextBlock1 = getBlockFromPC(luaFunction, parentBlock.nextPC1);//rightAfterThat
                            nextBlock2 = getBlockFromPC(luaFunction, parentBlock.nextPC2);//sbx
                            if (nextBlock1.isGarbageBlock && !nextBlock2.isGarbageBlock) {
                                parentBlock.instructions.remove(lastIndex);
                                parentBlock.instructions.add("jmp 0 l" + (parentBlock.nextPC2 + 1));
                                parentBlock.setDirectToPC(parentBlock.nextPC2);
                            } else if (!nextBlock1.isGarbageBlock && nextBlock2.isGarbageBlock) {
                                parentBlock.instructions.remove(lastIndex);
                                parentBlock.instructions.add("jmp 0 l" + (parentBlock.nextPC1 + 1));
                                parentBlock.setDirectToPC(parentBlock.nextPC1);
                            } else if (!nextBlock1.isGarbageBlock && !nextBlock2.isGarbageBlock) {
                                System.out.println("forloop识别错误:" + parentBlock.label);
                            } else {
                                parentBlock.isGarbageBlock = true;
                            }
                            break;
                        case OP_TESTSET:
                            nextBlock1 = getBlockFromPC(luaFunction, parentBlock.nextPC1);//rightAfterThat
                            nextBlock2 = getBlockFromPC(luaFunction, parentBlock.nextPC2);//sbx
                            if (nextBlock1.isGarbageBlock && !nextBlock2.isGarbageBlock) {
                                parentBlock.instructions.remove(lastIndex);
                                parentBlock.instructions.add("jmp 0 l" + (parentBlock.nextPC2 + 1));
                                parentBlock.setDirectToPC(parentBlock.nextPC2);
                            } else if (!nextBlock1.isGarbageBlock && nextBlock2.isGarbageBlock) {
                                parentBlock.instructions.remove(lastIndex);
                                parentBlock.instructions.add("move " + operands[0] + " " + operands[1]);
                                parentBlock.instructions.add("jmp 0 l" + (parentBlock.nextPC1 + 1));
                                parentBlock.setDirectToPC(currentBlock.nextPC1);
                            } else if (!nextBlock1.isGarbageBlock && !nextBlock2.isGarbageBlock) {
                                System.out.println("testset识别错误:" + parentBlock.label);
//                            currentBlock.instructions.add("jmp 0 l" + (currentBlock.nextPC1 + 1));
//                            currentBlock.instructions.add("jmp 0 l" + (currentBlock.nextPC2 + 1));
                            } else {
                                parentBlock.isGarbageBlock = true;
                            }
                            break;
                        case OP_TEST:
                        case OP_LT:
                        case OP_LE:
                        case OP_EQ:
                            nextBlock1 = getBlockFromPC(luaFunction, parentBlock.nextPC1);//后一条
                            nextBlock2 = getBlockFromPC(luaFunction, parentBlock.nextPC2);//后两条
                            if (nextBlock1.isGarbageBlock && !nextBlock2.isGarbageBlock) {
                                parentBlock.instructions.remove(lastIndex);
                                parentBlock.instructions.add("jmp 0 l" + (parentBlock.nextPC2 + 1));
                                parentBlock.setDirectToPC(parentBlock.nextPC2);
                            } else if (!nextBlock1.isGarbageBlock && nextBlock2.isGarbageBlock) {
                                parentBlock.instructions.remove(lastIndex);
                                parentBlock.instructions.add("jmp 0 l" + (parentBlock.nextPC1 + 1));
                                parentBlock.setDirectToPC(parentBlock.nextPC1);
                            } else if (!nextBlock1.isGarbageBlock && !nextBlock2.isGarbageBlock) {
                                System.out.println("条件跳转识别错误:" + parentBlock.label);
//                            currentBlock.instructions.add("jmp 0 l" + (currentBlock.nextPC1 + 1));
//                            currentBlock.instructions.add("jmp 0 l" + (currentBlock.nextPC2 + 1));
                            } else {
                                parentBlock.isGarbageBlock = true;
                            }
                            break;
                        case OP_FORPREP:
                        case OP_JMP:
                        default:
                            parentBlock.isGarbageBlock = true;
                            break;
                    }
                }
                //遍历完垃圾块的父块后删除垃圾块
                luaFunction.basicBlocks.remove(currentBlock);
            }
            //刷新垃圾块列表
            garbageBlocks = luaFunction.getGarbageBlocks();
        }
    }

    public static void refineControlFlow(LuaFunction luaFunction) {
        refineDirectJmpTo(luaFunction);//去除jmp直接/间接跳转
        refineNoParentBlock(luaFunction);//去除无用块
        refineConditionJmp(luaFunction);//优化条件跳转
        refineDirectJmpTo(luaFunction);//去除jmp直接/间接跳转
        System.out.println("控制流优化完成");
    }

    public static void refineNoParentBlock(LuaFunction luaFunction) {
        ArrayList<BasicBlock> needToRefinedBasicBlocks = luaFunction.getNeedToRefineBlocks();
        for (BasicBlock block : needToRefinedBasicBlocks) {
            ArrayList<BasicBlock> parentBlocks = luaFunction.getParentBlocks(block);//获取父块数量
            if (parentBlocks.size() == 0 && getLabel(block.label) != 1) {
                luaFunction.basicBlocks.remove(block);
                block.isRefined = true;
            }
        }
        luaFunction.resetControlFlowRefinedState();//重置控制流优化状态
    }

    public static void refineConditionJmp(LuaFunction luaFunction) {//只用于测试
        ArrayList<BasicBlock> needToRefinedBasicBlocks = luaFunction.getNeedToRefineBlocks();
        for (BasicBlock block : needToRefinedBasicBlocks) {
            if (!block.isGarbageBlock) {
                if (block.hasConditionJmp() && !block.isRefined) {
                    Instruction endInstruction = new Instruction( block.getLastInstruction());
                    String endOpcode = endInstruction.getOpcode();
                    if(!endOpcode.equals(OP_TEST)&&!endOpcode.equals(OP_EQ)&&!endOpcode.equals(OP_LT)&&!endOpcode.equals(OP_LE))continue;
                    //分别处理两个后继块
                    BasicBlock nextPC1_Block = getBlockFromPC(luaFunction, block.nextPC1);
                    ArrayList<BasicBlock> parentBlocks = luaFunction.getParentBlocks(nextPC1_Block);//获取后继块的父块数量
                    if (parentBlocks.size() == 1) {
                        int size = nextPC1_Block.getInstructionsSize();
                        Instruction firstInstruction = new Instruction(nextPC1_Block.instructions.get(0));
                        if (size == 1 && firstInstruction.getOpcode().equals(OP_JMP)) {//可能有只有一条jmp指令的中间块
                            block.nextPC1 = nextPC1_Block.directToPC;
                            nextPC1_Block.isRefined = true;
                            luaFunction.basicBlocks.remove(nextPC1_Block);
                        }
                    }
                    if (block.nextPC1 == block.nextPC2) {
                        block.removeInstruction(block.getLastInstructionIndex());
                        block.instructions.add("jmp 0 l" + (block.nextPC2 + 1));
                        block.setDirectToPC(block.nextPC2);

                        BasicBlock nextPC2_Block = getBlockFromPC(luaFunction, block.directToPC);
                        parentBlocks = luaFunction.getParentBlocks(nextPC2_Block);//获取后继块的父块数量
                        if (parentBlocks.size() == 1) {
                            int size = nextPC2_Block.getInstructionsSize();
                            Instruction firstInstruction = new Instruction(nextPC2_Block.instructions.get(0));
                            if (size == 1 && firstInstruction.getOpcode().equals(OP_JMP)) {//可能有只有一条jmp指令的中间块
                                block.directToPC = nextPC2_Block.directToPC;
                                block.removeInstruction(block.getLastInstructionIndex());
                                block.copyInstruction(nextPC2_Block,nextPC2_Block.getLastInstructionIndex());
                                nextPC2_Block.isRefined = true;
                                luaFunction.basicBlocks.remove(nextPC2_Block);
                            }
                        }
                    }else {
                        BasicBlock nextPC2_Block = getBlockFromPC(luaFunction, block.nextPC2);
                        parentBlocks = luaFunction.getParentBlocks(nextPC2_Block);//获取后继块的父块数量
                        if (parentBlocks.size() == 1) {
                            int size = nextPC2_Block.getInstructionsSize();
                            Instruction firstInstruction = new Instruction(nextPC2_Block.instructions.get(0));
                            if (size == 1 && firstInstruction.getOpcode().equals(OP_JMP)) {//可能有只有一条jmp指令的中间块
                                block.nextPC2 = nextPC2_Block.directToPC;
                                nextPC2_Block.isRefined = true;
                                luaFunction.basicBlocks.remove(nextPC2_Block);
                            }
                        }
                        if (block.nextPC2 == block.nextPC1) {
                            block.removeInstruction(block.getLastInstructionIndex());
                            block.instructions.add("jmp 0 l" + (block.nextPC2 + 1));
                            block.setDirectToPC(block.nextPC2);

                            BasicBlock nextBlock = getBlockFromPC(luaFunction, block.directToPC);
                            parentBlocks = luaFunction.getParentBlocks(nextBlock);//获取后继块的父块数量
                            if (parentBlocks.size() == 1) {
                                int size = nextBlock.getInstructionsSize();
                                Instruction firstInstruction = new Instruction(nextBlock.instructions.get(0));
                                if (size == 1 && firstInstruction.getOpcode().equals(OP_JMP)) {//可能有只有一条jmp指令的中间块
                                    block.directToPC = nextBlock.directToPC;
                                    block.removeInstruction(block.getLastInstructionIndex());
                                    block.copyInstruction(nextBlock,nextBlock.getLastInstructionIndex());
                                    nextBlock.isRefined = true;
                                    luaFunction.basicBlocks.remove(nextBlock);
                                }
                            }
                        }
                    }
                }
                block.isRefined = true;
            } else {
                //System.out.println("应该没有垃圾块:" + block.label);
            }
        }

        luaFunction.resetControlFlowRefinedState();//重置控制流优化状态
    }

    public static void refineDirectJmpTo(LuaFunction luaFunction) {
        ArrayList<BasicBlock> needToRefinedBasicBlocks = luaFunction.getNeedToRefineBlocks();
        while (!needToRefinedBasicBlocks.isEmpty()) {
            for (BasicBlock block : needToRefinedBasicBlocks) {
                if (!block.isGarbageBlock) {
                    if (block.hasDirectBlock() && !block.isRefined) {
                        Instruction lastInstruction = new Instruction(block.getLastInstruction());
                        String opcode = lastInstruction.getOpcode();
                        if (opcode.equals(OP_JMP)) {//forperp的结尾需要特殊处理
                            BasicBlock nextBlock = getBlockFromPC(luaFunction, block.directToPC);
                            ArrayList<BasicBlock> parentBlocks = luaFunction.getParentBlocks(nextBlock);//获取直接后继块的父块数量
                            if (parentBlocks.size() == 1) {
                                int size = nextBlock.getInstructionsSize();
                                Instruction firstInstruction = new Instruction(nextBlock.instructions.get(0));
                                if (size == 1 && firstInstruction.getOpcode().equals(OP_JMP)) {//可能有只有一条jmp指令的中间块
                                    block.directToPC = nextBlock.directToPC;
                                    block.removeInstruction(block.getLastInstructionIndex());
                                    block.copyInstruction(nextBlock, 0);
                                    nextBlock.isRefined = true;
                                    luaFunction.basicBlocks.remove(nextBlock);
                                    continue;
                                } else {
                                    BasicBlock mergeBlock = new BasicBlock();
                                    mergeBlock.copyInstructions(block, 0, block.getLastInstructionIndex());//加入从该块(不包括结束语句)到后继块的结束语句的所有指令
                                    mergeBlock.copyInstructions(nextBlock, 0, nextBlock.getInstructionsSize());
                                    mergeBlock.copySuccessorInfo(nextBlock);
                                    mergeBlock.label = block.label;
                                    nextBlock.isRefined = true;
                                    luaFunction.basicBlocks.add(mergeBlock);
                                    luaFunction.basicBlocks.remove(block);
                                    luaFunction.basicBlocks.remove(nextBlock);
                                }
                            }
                        }
                    }
                    block.isRefined = true;
                } else {
                    //System.out.println("应该没有垃圾块:" + block.label);
                }
            }
            //刷新
            needToRefinedBasicBlocks = luaFunction.getNeedToRefineBlocks();
        }
        luaFunction.resetControlFlowRefinedState();//重置控制流优化状态
    }

    private static Pattern removeLabelsPattern = Pattern.compile("^\\.label\\s+l(\\d+).*");
    private static Matcher removeLablesPatternMatcher = removeLabelsPattern.matcher("");
    private static void removeLabels(ArrayList<String> lines, LuaFunction luaFunction) {
        // 构造正则表达式，匹配以".label"开头的行
        Iterator<String> iterator = lines.iterator();

        // 遍历列表，删除匹配的行
        while (iterator.hasNext()) {
            String line = iterator.next();
            removeLablesPatternMatcher.reset(line);
            if (removeLablesPatternMatcher.matches()) {
                luaFunction.basicBlockStartPC.add(Integer.valueOf(removeLablesPatternMatcher.group(1)) - 1);
                iterator.remove(); // 删除匹配的行
            }
        }
    }
    public static int readHeadInfo(ArrayList<String> lasm, int pc) {
        ListIterator<String> code = lasm.listIterator(pc);
        version = code.next();
        format = code.next();
        endianness = code.next();
        int_size = code.next();
        size_t_size = code.next();
        instruction_size = code.next();
        number_format = code.next();
        return code.nextIndex();
    }

    public static int readLuaFunction(ArrayList<String> lasm, int pc) {
        ListIterator<String> code = lasm.listIterator(pc);
        LuaFunction luaFunction = new LuaFunction();
        luaFunctions.add(luaFunction);

        luaFunction.name = code.next();
        luaFunction.lineDefined = code.next();
        luaFunction.lastLineDefined = code.next();
        luaFunction.numParams = code.next();
        luaFunction.isVarArg = code.next();
        luaFunction.maxStackSize = code.next();
        luaFunction.source = code.next();

        
        StringBuilder nextline = new StringBuilder(code.next());

        while (nextline.toString().startsWith(".upvalue")) {
            luaFunction.upvalues.add(parseUpvalue(nextline.toString()));
            nextline.replace(0, nextline.length(), code.next());
        }

        StringBuilder constantBuilder = new StringBuilder();
        while (nextline.toString().startsWith(".constant")) {
            constantBuilder.append(nextline);
            constantBuilder.append('\n');
            //luaFunction.constants.add(nextline.toString());
            nextline.replace(0, nextline.length(), code.next());
        }
        luaFunction.constantString=constantBuilder.toString();

        while (!nextline.toString().startsWith(".function")) {
            luaFunction.instructions.add(nextline.toString());
            if (code.hasNext()) {
                nextline.replace(0, nextline.length(), code.next());
            } else {
                return code.nextIndex();
            }
        }

        code.previous();
        return code.nextIndex();
    }

    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");
    public static Upvalue parseUpvalue(String line) {
        String[] parts = WHITESPACE_PATTERN.split(line.trim());
        String[] info = new String[parts.length - 1];
        System.arraycopy(parts, 1, info, 0, info.length);

        String name = info[0];
        int index = Integer.parseInt(info[1]);
        boolean isInstack = Boolean.parseBoolean(info[2]);
        return new Upvalue(name, index, isInstack);
    }

    private static final Pattern NEWLINE_PATTERN = Pattern.compile("\\n");

    public static ArrayList removeEmptyLines(String input) {
        ArrayList<String> linesList = new ArrayList<>();
        // 去除空行
        String[] lines = NEWLINE_PATTERN.split(input);
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                linesList.add(line.trim());
            }
        }
        return linesList;
    }
}
