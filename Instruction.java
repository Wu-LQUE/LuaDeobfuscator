import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Instruction {
    private String opcode;
    private String[] operands;
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");
    public Instruction(String instructionString) {
        String[] parts = WHITESPACE_PATTERN.split(instructionString.trim());

        if (parts.length >= 1) {
            opcode = parts[0];
        } else {
            throw new IllegalArgumentException("Invalid instruction format: " + instructionString);
        }

        operands = new String[parts.length - 1];
        System.arraycopy(parts, 1, operands, 0, operands.length);
    }

    public String getOpcode() {
        return opcode;
    }

    public String[] getOperands() {
        return operands;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(opcode);
        for (String operand : operands) {
            sb.append(" ").append(operand);
        }
        return sb.toString();
    }

    public static Pattern getTargetPattern = Pattern.compile("l(\\d+)");
    private static final Matcher targetPatternMatcher = getTargetPattern.matcher("");
    public static int getTarget(String operand){

        targetPatternMatcher.reset(operand);

        if (targetPatternMatcher.find()) {
            String numberString = targetPatternMatcher.group(1);
            int number = Integer.parseInt(numberString);
            return number;
        } else {
            //System.out.println("No targetLabel sbx:"+operand);//无效跳转
            return -1;
        }
    }
    public static Pattern isUnknownOpPattern = Pattern.compile("op\\d+");
    private final Matcher isUnknownOpPatternMatcher = isUnknownOpPattern.matcher("");
    public boolean isGarbageInstruction(){
        //判断是否为未知opcode
        isUnknownOpPatternMatcher.reset(getOpcode());
        if (isUnknownOpPatternMatcher.matches()) {
            //System.out.println("garbage:"+toString());
            return true;
        }
        return false;
    }
    public Instruction modifyArgument(int index,String value){
        this.operands[index] = value;
        return this;

    }
}
