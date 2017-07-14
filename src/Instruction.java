import java.math.BigInteger;

public class Instruction {
	public int [] parts;
	private int op,imm,src1,src2,des;
	private int inst;
	private int predicted = -1;	//for branch instruction, whether it is predicted right or wrong,
							//1 for right, 0 for wrong
	//private int taddr=-1;
	private int shamt;

	private int address,offset;
	public Instruction(int p1,int p2,int p3,int p4,int p5,int p6,int addr)
	{
		parts=new int[6];
		this.parts[0]=p1;
		this.parts[1]=p2;
		this.parts[2]=p3;
		this.parts[3]=p4;
		this.parts[4]=p5;
		this.parts[5]=p6;
		this.src1=p2;
		this.src2=p3;
		this.des=p4;
		op=p1;
		imm=p4<<(6+5)|p5<<6|p6;
		if(p4>16)
			imm=imm-65536;
		inst=p1<<(6+5+5+5+5)|p2<<(6+5+5+5)|p3<<(6+5+5)|p4<<(6+5)|p5<<6|p6;
		this.address=addr;
		
		//imm=p4<<(6+5)|p5<<6|p6;
		//inst=p1<<(6+5+5+5+5)|p2<<(6+5+5+5)|p3<<(6+5+5)|p4<<(6+5)|p5<<6|p6;
		//this.address=addr;
	
    	offset = (inst >> Defines.IMMEDIATE_OFFSET) & Defines.IMMEDIATE_MASK;

	}
	
	
	public String toString()
	{
    	int opcode, rs, rt, rd, shamt, funct;
    	String retStr="";
    	opcode = (inst >> Defines.OPCODE_OFFSET) & Defines.OPCODE_MASK;
    	rt = (inst >> Defines.RT_OFFSET) & Defines.RT_MASK;
    	rs = (inst >> Defines.RS_OFFSET) & Defines.RS_MASK;
    	rd = (inst >> Defines.RD_OFFSET) & Defines.RD_MASK;
    	shamt = (inst >> Defines.SHAMT_OFFSET) & Defines.SHAMT_MASK;
    	funct = (inst >> Defines.FUNCT_OFFSET) & Defines.FUNCT_MASK;
    	retStr = String.format("%06d ", new BigInteger(Integer.toBinaryString(opcode)));
    	retStr += String.format("%05d ", new BigInteger(Integer.toBinaryString(rs)));
    	retStr += String.format("%05d ", new BigInteger(Integer.toBinaryString(rt)));
    	retStr += String.format("%05d ", new BigInteger(Integer.toBinaryString(rd)));
    	retStr += String.format("%05d ", new BigInteger(Integer.toBinaryString(shamt)));
    	retStr += String.format("%06d ", new BigInteger(Integer.toBinaryString(funct)));
    	return retStr;
	}
	
	
	public int getAddr() {
		// TODO Auto-generated method stub
		return address;
	}

	public int getTargetAddr(int pc)
	{
		return pc + offset<<2;
	}

	public int getSource1() {
		// TODO Auto-generated method stub
		return src1;
	}

	public int source_get() {
		// TODO Auto-generated method stub
		return src2;
	}

	public int getDest() {
		// TODO Auto-generated method stub
		//int opcode;
		int opcode;
		opcode = (inst >> Defines.OPCODE_OFFSET) & Defines.OPCODE_MASK;
    	switch(opcode){
    	case Defines.OPCODE_ADDI:
    		return getSource2();
    		
    		
    	case Defines.OPCODE_ADDIU:
    		return getSource2();
    		
    		
    	case Defines.OPCODE_SW:
    		return -1;
    		
    		
    	case Defines.OPCODE_LW:
    		return getSource2();
    		
    		
    	case Defines.OPCODE_SPECIAL:
    		//neeche wala switch case
    		short funct = (short) ((inst >> Defines.FUNCT_OFFSET) & Defines.FUNCT_MASK);
        	switch(funct){
        	case Defines.FUNCT_BREAK:
        		//stop the simulator
        		//endLine = addr;
        		
        		break;
        	case Defines.FUNCT_SLT:
        		return des;
        		
        	case Defines.FUNCT_SLTU:
        		return des;
        		
        	case Defines.FUNCT_NOP:
        		return -1;
            		
        		
        	case Defines.FUNCT_SRL:		
        		return des;
        		
        	case Defines.FUNCT_SRA:
        		return des;
        		
        	case Defines.FUNCT_SUB:
        		return des;
        		
        	case Defines.FUNCT_SUBU:
        		return des;
        		
        	case Defines.FUNCT_ADD:
        		return des;      		
        		
        	case Defines.FUNCT_ADDU:
        		return des;
        		
        	case Defines.FUNCT_AND:
        		return des; 
        		
        	case Defines.FUNCT_OR:
        		return des;        		
        	case Defines.FUNCT_XOR:
        		return des;
        		
        	case Defines.FUNCT_NOR:
        		return des;
        	default:
        		break;
        	}

    		break;
    	case Defines.OPCODE_J:
    		
        	return -1;

    	case Defines.OPCODE_BEQ:
    	{
    		
    		return -1;

    	}
    		
    	case Defines.OPCODE_BNE:
    	{
    		return -1;

    	}
    		
    	case Defines.OPCODE_REGIMM:
    		
    		break;
    	case Defines.OPCODE_BGTZ:
    	{
        	return -1;
    	}
    		
    	case Defines.OPCODE_BLEZ:
    	{
        	return -1;
    	}
    		
    	case Defines.OPCODE_SLTI:
    	{
    		return getSource2();

    	}
    		
    	default:
    		break;
    	}
		return -1;
		//return des;
	}
	public void setPredicted(int p)
	{
		predicted=p;
	}
	public int getPredicted()
	{
		return predicted;
	}
	
	
	
	public int getShamt()
	{
		return shamt;
	}
	
	
	
	
	public int getInstType()
	{
		int opcode;
    	opcode = (inst >> Defines.OPCODE_OFFSET) & Defines.OPCODE_MASK;
    	switch(opcode){
    	case Defines.OPCODE_ADDI:
    	case Defines.OPCODE_ADDIU:
        		return Defines.InstType_ALU;    		
    		
    	case Defines.OPCODE_SW:
    		return Defines.InstType_ST;
    		
    	case Defines.OPCODE_LW:
    		return Defines.InstType_LD;
    		
    	case Defines.OPCODE_SPECIAL:
    		//neeche wala switch case
    		/*short funct = (short) ((inst >> Defines.FUNCT_OFFSET) & Defines.FUNCT_MASK);
        	switch(funct){
        	case Defines.FUNCT_BREAK:
        	case Defines.FUNCT_SLT:
        	case Defines.FUNCT_SLTU:
        	case Defines.FUNCT_NOP:
        	case Defines.FUNCT_SRL:		
        	case Defines.FUNCT_SRA:
        	case Defines.FUNCT_SUB:
        	case Defines.FUNCT_SUBU:
        	case Defines.FUNCT_ADD:
        	case Defines.FUNCT_ADDU:
        	case Defines.FUNCT_AND:
        	case Defines.FUNCT_OR:
        	case Defines.FUNCT_XOR:
        	case Defines.FUNCT_NOR:*/
            	return Defines.InstType_ALU;    		
        		
       	case Defines.OPCODE_J:
       	case Defines.OPCODE_BEQ:
       	case Defines.OPCODE_BNE:
       	case Defines.OPCODE_BGTZ:
       	case Defines.OPCODE_BLEZ:
        	return Defines.InstType_BRANCH;
    	case Defines.OPCODE_SLTI:
        	return Defines.InstType_ALU;
    	default:
    		break;
    	}
    
    	return -1;
	}
	/*public String toString()
	{
		//switch case ITYPE
		//print appropriately
		return "";
	}*/
	public int OpName()
	{
		return op;
	}
	
	public int getImm()
	{
		return imm;
	}
	
	public String toString2() 
	{
		// TODO Auto-generated method stub
		int opcode;
    	opcode = (inst >> Defines.OPCODE_OFFSET) & Defines.OPCODE_MASK;
    	switch(opcode){
    	case Defines.OPCODE_ADDI:
    		return OpName() + " " + "R" + source_get() + ", R" + getSource1() + ", #" + getImm();
    		
    		
    	case Defines.OPCODE_ADDIU:
    		return OpName() + " " + "R" + source_get() + ", R" + getSource1() + ", #" + getImm();
    		
    		
    	case Defines.OPCODE_SW:
    		return OpName() + " " + "R" + source_get() + ", " + getImm() + "(R" + getSource1() + ")";
    		
    		
    	case Defines.OPCODE_LW:
    		return OpName() + " " + "R" + source_get() + ", " + getImm() + "(R" + getSource1() + ")";
    		
    		
    	case Defines.OPCODE_SPECIAL:
    		//neeche wala switch case
    		short funct = (short) ((inst >> Defines.FUNCT_OFFSET) & Defines.FUNCT_MASK);
        	switch(funct){
        	case Defines.FUNCT_BREAK:
        		//stop the simulator
        		//endLine = addr;
        		
        		break;
        	case Defines.FUNCT_SLT:
        		return OpName() + " " + "R" + getDest() + ", R" + getSource1() + ", R" + source_get();
        		
        	case Defines.FUNCT_SLTU:
        		return OpName() + " " + "R" + getDest() + ", R" + getSource1() + ", R" + source_get();
        		
        	case Defines.FUNCT_NOP:
        		return OpName()+"";
            		
        		
        	case Defines.FUNCT_SRL:		
        		return OpName() + " " + "R" + getDest() + ", R" + source_get() + ", #" + getShamt();
        		
        	case Defines.FUNCT_SRA:
        		return OpName() + " " + "R" + getDest() + ", R" + source_get() + ", #" + getShamt();
        		
        	case Defines.FUNCT_SUB:
        		return OpName() + " " + "R" + getDest() + ", R" + getSource1() + ", R" + source_get();
        		
        	case Defines.FUNCT_SUBU:
        		return OpName() + " " + "R" + getDest() + ", R" + getSource1() + ", R" + source_get();
        		
        	case Defines.FUNCT_ADD:
        		return OpName() + " " + "R" + getDest() + ", R" + getSource1() + ", R" + source_get();        		
        		
        	case Defines.FUNCT_ADDU:
        		return OpName() + " " + "R" + getDest() + ", R" + getSource1() + ", R" + source_get();
        		
        	case Defines.FUNCT_AND:
        		return OpName() + " " + "R" + getDest() + ", R" + getSource1() + ", R" + source_get();        		
        	case Defines.FUNCT_OR:
        		return OpName() + " " + "R" + getDest() + ", R" + getSource1() + ", R" + source_get();        		
        	case Defines.FUNCT_XOR:
        		return OpName() + " " + "R" + getDest() + ", R" + getSource1() + ", R" + source_get();        		
        	case Defines.FUNCT_NOR:
        		return OpName() + " " + "R" + getDest() + ", R" + getSource1() + ", R" + source_get();
        	default:
        		break;
        	}

    		break;
    	case Defines.OPCODE_J:
    		
        	return OpName() + " #" + (offset<<2);

    	case Defines.OPCODE_BEQ:
    	{
    		
    		return OpName() + " " + "R" + getSource1() + ", " + "R" + source_get() + ", " + "#" + (getImm()<<2);

    	}
    		
    	case Defines.OPCODE_BNE:
    	{
    		return OpName() + " " + "R" + getSource1() + ", " + "R" + source_get() + ", " + "#" + (getImm()<<2);

    	}
    		
    	case Defines.OPCODE_REGIMM:
    		
    		break;
    	case Defines.OPCODE_BGTZ:
    	{
        	return OpName() + " " + "R" + getSource1() + ", " + "#" + (getImm()<<2);
    	}
    		
    	case Defines.OPCODE_BLEZ:
    	{
        	return OpName() + " " + "R" + getSource1() + ", " + "#" + (getImm()<<2);
    	}
    		
    	case Defines.OPCODE_SLTI:
    	{
    		return OpName() + " " + "R" + source_get() + ", R" + getSource1() + ", #" + getImm();

    	}
    		
    	default:
    		break;
    	}
		return "";
	}
	
	public int compute(int src1,int src2)
	{
		int opcode;
    	opcode = (inst >> Defines.OPCODE_OFFSET) & Defines.OPCODE_MASK;
    	switch(opcode){
    	case Defines.OPCODE_ADDI:
    		return src1 + getImm();
    		
    		
    	case Defines.OPCODE_ADDIU:
    		return src1 + getImm();
    		
    		
    	case Defines.OPCODE_SW:
    		return src1 + getImm();
    		
    	case Defines.OPCODE_LW:
    		return src1 + getImm();
    		
    	case Defines.OPCODE_SPECIAL:
    		//neeche wala switch case
    		short funct = (short) ((inst >> Defines.FUNCT_OFFSET) & Defines.FUNCT_MASK);
        	switch(funct){
        	case Defines.FUNCT_BREAK:
        		//stop the simulator
        		//endLine = addr;
        		
        		break;
        	case Defines.FUNCT_SLT:
        		if(src1 < src2)
        			return 1;
        		else
        			return 0;
        		
        	case Defines.FUNCT_SLTU:
        		if(src1 < src2)
        			return 1;
        		else
        			return 0;
        		
        	case Defines.FUNCT_NOP:
        		if(inst == 0)
        		{//NOP
            	
        			break;
        		}
        		else
        		{ // SLL
            		
        			break;
        		}
        		
        	case Defines.FUNCT_SRL:		
        		return src2 >> getShamt();
        		
        	case Defines.FUNCT_SRA:
        		return src2 >> getShamt();
        		
        	case Defines.FUNCT_SUB:
        		return src1 - src2;
        		
        	case Defines.FUNCT_SUBU:
        		return src1 - src2;
        		
        	case Defines.FUNCT_ADD:
        		return src1 + src2;
        		
        		
        	case Defines.FUNCT_ADDU:
        		return src1 + src2;
        		
        	case Defines.FUNCT_AND:
        		return src1 & src2;
        		
        	case Defines.FUNCT_OR:
        		return src1 | src2;
        		
        	case Defines.FUNCT_XOR:
        		return src1 ^ src2;
        		
        	case Defines.FUNCT_NOR:
        		if((src1 == 0) || (src2 == 0))
        			return 1;
        		else
        			return 0;
        		
        	default:
        		break;
        	}

    		break;
    	case Defines.OPCODE_J:
    		return 1;
    	case Defines.OPCODE_BEQ:
    	{
    		
    		if(src1 == src2)
    			return 1;
    		else
    			return 0;
    	}
    		
    	case Defines.OPCODE_BNE:
    	{
    		if(src1 != src2)
    			return 1;
    		else
    			return 0;
    	}
    		
    	case Defines.OPCODE_REGIMM:
    		
    		break;
    	case Defines.OPCODE_BGTZ:
    	{
    		if(src1 > 0)
    			return 1;
    		else
    			return 0;
    	}
    		
    	case Defines.OPCODE_BLEZ:
    	{
    		if(src1 <= 0)
    			return 1;
    		else
    			return 0;
    	}
    		
    	case Defines.OPCODE_SLTI:
    	{
    		if(src1 < getImm())
    			return 1;
    		else
    			return 0;
    	}
    		
    	default:
    		break;
    	}
    
    	return -1;
	}



	public int getSource2() {
		// TODO Auto-generated method stub
		return 0;
	}
}
