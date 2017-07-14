
public class Defines {
	
    public static final int startCodeLine = 600;
    public static final int startDataLine = 716;
    
    public static final int Predict_NOTSET=0;
    public static final int Predict_FALSE=1;
    public static final int Predict_TRUE=2;
    
    public static final int OFFSET_OFFSET = 0;
    public static final int OFFSET_MASK = 0x3ffffff;
    
    public static final int IMMEDIATE_OFFSET = 0;
    public static final int IMMEDIATE_MASK = 0xffff;
    
    public static final int FUNCT_OFFSET = IMMEDIATE_OFFSET;
    public static final int FUNCT_MASK = 0x3f;
    
    public static final int SHAMT_OFFSET = 6;
    public static final int SHAMT_MASK = 0x1f;
    
    public static final int RD_OFFSET = 11;
    public static final int RD_MASK = 0x1f;
    
    public static final int RT_OFFSET = 16;
    public static final int RT_MASK = 0x1f;
  
    public static final int RS_OFFSET = 21;
    public static final int RS_MASK = 0x1f;

    public static final int OPCODE_OFFSET = 26;
    public static final int OPCODE_MASK = 0x3f;
    public static final int OPCODE_SPECIAL = 0x00;
    
    public static final int OPCODE_SW = 0x2b;
    public static final int OPCODE_LW = 0x23;
    public static final int OPCODE_J = 0x02;
    public static final int OPCODE_BEQ = 0x04;
    public static final int OPCODE_BNE = 0x05; 
    public static final int OPCODE_REGIMM = 0x01;
    public static final int SUBOPCODE_BGEZ = 0x01;
    public static final int SUBOPCODE_BLTZ = 0x00;
    
    public static final int OPCODE_BGTZ = 0x07;;
    public static final int OPCODE_BLEZ = 0x06;
   
    public static final int OPCODE_ADDI = 0x08;
    public static final int OPCODE_ADDIU = 0x09;
    public static final int OPCODE_SLTI = 0x0a;
    
    public static final int FUNCT_BREAK = 0x0d;
    public static final int FUNCT_SLT = 0x2a;
    public static final int FUNCT_SLTU = 0x2b;
    public static final int FUNCT_SLL = 0x00;
    public static final int FUNCT_SRL = 0x02;
    public static final int FUNCT_SRA = 0x03;
    public static final int FUNCT_SUB = 0x22;
    public static final int FUNCT_SUBU = 0x23;
    public static final int FUNCT_ADD = 0x20;
    public static final int FUNCT_ADDU = 0x21;
    public static final int FUNCT_AND = 0x24;
    public static final int FUNCT_OR = 0x25;
    public static final int FUNCT_XOR = 0x26;
    public static final int FUNCT_NOR = 0x27;
    public static final int FUNCT_NOP = 0X00;
    
    public static final int	InstType_ALU=0;
	public static final int InstType_LD=1;
	public static final int InstType_ST=2;
	public static final int InstType_BRANCH=3;

}
