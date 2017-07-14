import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class instructiondecode {
	String result;
	List<Instruction> insts;
	Instruction temp;
	public instructiondecode(List<Instruction> i){
		result="";
		this.insts=i;
		//insts=new List<Instruction>();
		
	}
	public List<Instruction> getInstructions()
	{
		return insts;
	}
	public String disassemble(String inputfilename) throws IOException {
		
		FileInputStream file = null;
		System.out.println(inputfilename);
		file = new FileInputStream(inputfilename);
        byte[] b= new byte[4];//for storing 32 bits
        // read bytes to the buffer
        int i=4;
        boolean flag=false;
        String[] instructPart;
        int address=600;
        while(!flag){
        	i=file.read(b);
        //checking for 4 bytes
        	if(i<4){
        		System.out.println("Input is not appropriate ");
        		file.close();
        		return "error";
        	}
        	 instructPart=formatWord(b);
    	
        	for(int x=0;x<6;x++)
            result+=instructPart[x]+" ";
        	result+=address+" ";
        	address+=4;
      
        	flag=disassembleInstruction(instructPart,address);
        	result+="\n";
        	
		
       }
        while(true){
        	i=file.read(b);
        	if(i!=4)
        		break;
        	String word=getWord(b);
        	result+=word+" "+ address+" "+Integer.parseInt(word, 2);
        	address+=4;
        	result+="\n";
        	
        }
        file.close();
       return (result);
	}
	
	
	public  String[] formatWord(byte [] b){
		String bytes=getWord(b);
		
		//breaking input into separate instruction parts
		
		String[] instructionParts=new String[6];//special
		instructionParts[0]=bytes.substring(0,6);//rs
		instructionParts[1]=bytes.substring(6,11);//rt
		instructionParts[2]=bytes.substring(11,16);//rd
		instructionParts[3]=bytes.substring(16,21);
		instructionParts[4]=bytes.substring(21,26);
		instructionParts[5]=bytes.substring(26);//immediate value

	return instructionParts;
	
}

//converting byte array into binary string 
public  String getWord(byte [] bytes)
{
	String binary="";
	
	for(int n=0;  n<4; n++)
	{
		
		int unsigned=unsignedToBytes(bytes[n]);
		String b = Integer.toBinaryString(unsigned);//integer values to binary
	
	
		while(b.length()<8)
			b= new StringBuilder(b).insert(0, "0").toString();//add missing 0
	
		binary+=b;
	}
	
	return binary;
	
}

public int unsignedToBytes(byte bs) {
    return bs & 0xFF;
  }
	
	
	public boolean disassembleInstruction(String[] instructPart,int addr){
		boolean sign=false;
		int rt,rd,rs=0;
		String s;
		
		int I1=Integer.parseInt(instructPart[0],2);
		int I2=Integer.parseInt(instructPart[1],2);
		int I3=Integer.parseInt(instructPart[2],2);
		int I4=Integer.parseInt(instructPart[3],2);
		int I5=Integer.parseInt(instructPart[4],2);
		int I6=Integer.parseInt(instructPart[5],2);
	
		int code=Integer.parseInt(instructPart[0], 2);
		
		temp=new Instruction(I1,I2,I3,I4,I5,I6,addr);
				//Integer.parseInt(instructPart[1],2),
				//Integer.parseInt(instructPart[2],2),
				//Integer.parseInt(instructPart[3],2),
				//Integer.parseInt(instructPart[4],2),
				//Integer.parseInt(instructPart[5],2),addr);
		//System.out.println(temp.parts[0]);
		//if(temp==null)
		//	System.out.println("hello");
		insts.add(temp);
		//insts.add(new Instruction());
		switch (code) {
        
   case 0: 
        		
        		if(instructPart[5].equals("001101"))
        		{
        		result+="BREAK";
        		sign=true;
        		}
        		
        		if(instructPart[5].equals("101010"))
        		{
        			rs=Integer.parseInt(instructPart[1], 2);
            		rt=Integer.parseInt(instructPart[2], 2);
            		rd=Integer.parseInt(instructPart[3],2);
            		result+="SLT R"+rd+", R"+rs+", R"+rt;
        		}
        		
        		if(instructPart[5].equals("101011"))
        		{
        			rs=Integer.parseInt(instructPart[1], 2);
            		rt=Integer.parseInt(instructPart[2], 2);
            		rd=Integer.parseInt(instructPart[3],2);
            		result+="SLTU R"+rd+", R"+rs+", R"+rt;
        		}
        		
        		if(instructPart[5].equals("000000"))
        		{
        			if(instructPart[2].equals("00000"))
        		   {
        			rd=Integer.parseInt(instructPart[3],2);
        			if(rd==0)
        			result+="NOP";
        		   }
        			else
        			{
        		    rs=Integer.parseInt(instructPart[4], 2);
            		rt=Integer.parseInt(instructPart[2], 2);
            		rd=Integer.parseInt(instructPart[3], 2);
            		if(rt!=0)
                       result+="SLL R"+rd+", R"+rt+", #"+rs;
        			}
                }
        		
        		if(instructPart[5].equals("000010"))
        		{
        			rs=Integer.parseInt(instructPart[4], 2);
            		rt=Integer.parseInt(instructPart[2], 2);
            		rd=Integer.parseInt(instructPart[3], 2);
            		result+="SRL R"+rd+", R"+rt+", #"+rs;
        		}
        		
        		if(instructPart[5].equals("000011"))
        		{
        			rs=Integer.parseInt(instructPart[4], 2);
            		rt=Integer.parseInt(instructPart[2], 2);
            		rd=Integer.parseInt(instructPart[3], 2);
            		result+="SRA R"+rd+", R"+rt+", #"+rs;
        		}
        		
        		if(instructPart[5].equals("100010"))
        		{
        			rs=Integer.parseInt(instructPart[1], 2);
            		rt=Integer.parseInt(instructPart[2], 2);
            		rd=Integer.parseInt(instructPart[3], 2);
            		result+="SUB R"+rd+", R"+rs+", R"+rt;
        		}
        		
        		if(instructPart[5].equals("100011"))
        		{
        			rs=Integer.parseInt(instructPart[1], 2);
            		rt=Integer.parseInt(instructPart[2], 2);
            		rd=Integer.parseInt(instructPart[3], 2);
            		result+="SUBU R"+rd+", R"+rs+", R"+rt;
        		}
        		
        		if(instructPart[5].equals("100000"))
        		{
        		
        			rs=Integer.parseInt(instructPart[1], 2);
            		rt=Integer.parseInt(instructPart[2], 2);
            		rd=Integer.parseInt(instructPart[3], 2);
            		result+="ADD R"+rd+", R"+rs+", R"+rt;
        		}

        		
        		if(instructPart[5].equals("100001"))
        		{
        			rs=Integer.parseInt(instructPart[1], 2);
            		rt=Integer.parseInt(instructPart[2], 2);
            		rd=Integer.parseInt(instructPart[3], 2);
            		result+="ADDU R"+rd+", R"+rs+", R"+rt;
        		}

        		if(instructPart[5].equals("100100"))
        		{
        			rs=Integer.parseInt(instructPart[1], 2);
            		rt=Integer.parseInt(instructPart[2], 2);
            		rd=Integer.parseInt(instructPart[3],2);
            		result+="AND R"+rd+", R"+rs+", R"+rt;
        		}
        		
        		if(instructPart[5].equals("100101"))
        		{
        			rs=Integer.parseInt(instructPart[1], 2);
            		rt=Integer.parseInt(instructPart[2], 2);
            		rd=Integer.parseInt(instructPart[3],2);
            		result+="OR R"+rd+", R"+rs+", R"+rt;
        		}
        		
        		if(instructPart[5].equals("100110"))
        		{
        			rs=Integer.parseInt(instructPart[1], 2);
            		rt=Integer.parseInt(instructPart[2], 2);
            		rd=Integer.parseInt(instructPart[3],2);
            		result+="XOR R"+rd+", R"+rs+", R"+rt;
        		}
        		
        		if(instructPart[5].equals("100111"))
        		{
        			rs=Integer.parseInt(instructPart[1], 2);
            		rt=Integer.parseInt(instructPart[2], 2);
            		rd=Integer.parseInt(instructPart[3],2);
            		result+="NOR R"+rd+", R"+rs+", R"+rt;
        		}
        		
        		/*if(instructPart[5].equals("000000") && instructPart[2].equals("00000"))
        		{
        			rd=Integer.parseInt(instructPart[3],2);
        			if(rd==0)
        			result+="NOP";
        		}*/
                
                break;
        		
                
   case 1: 
    		
				rs=Integer.parseInt(instructPart[1], 2);
				rt=Integer.parseInt(instructPart[2], 2);
				s=instructPart[3]+instructPart[4]+instructPart[5];
				if(rt==1)
				{
					s+="00";
					result+="BGEZ R"+rs+", #"+Integer.parseInt(s, 2);
				}
				if(rt==0)
				{
					s+="00";
					result+="BLTZ R"+rs+", #"+Integer.parseInt(s, 2);
				}
				break;
				
				
   case 2:
				rs=Integer.parseInt(instructPart[1], 2);
				rt=Integer.parseInt(instructPart[2], 2);
				s=instructPart[1]+instructPart[2]+instructPart[3]+instructPart[4]+instructPart[5];
				s+="00";
				result+= "J #"+Integer.parseInt(s, 2);
				break;
				
   case 4: 
		         rs=Integer.parseInt(instructPart[1], 2);
		         rt=Integer.parseInt(instructPart[2], 2);
		         s=instructPart[3]+instructPart[4]+instructPart[5];
		         s+="00";
		         result+="BEQ R"+rs+", R"+rt+", #"+Integer.parseInt(s, 2);
		         break;
		         
   case 5: 
		         rs=Integer.parseInt(instructPart[1], 2);
		         rt=Integer.parseInt(instructPart[2], 2);
		         s=instructPart[3]+instructPart[4]+instructPart[5];
		         s+="00";
		         result+="BNE R"+rs+", R"+rt+", #"+Integer.parseInt(s, 2);
		         break;		

   case 6: 
	
	             rs=Integer.parseInt(instructPart[1], 2);
	             rt=Integer.parseInt(instructPart[2], 2);
	             s=instructPart[3]+instructPart[4]+instructPart[5];
	
	             s+="00";
	             if(s.startsWith("0"))
		         result+="BLEZ R"+rs+", #"+Integer.parseInt(s, 2);
	              else
		         result+="BLEZ R"+rs+", #"+(Integer.parseInt(s, 2) - 262144);
	
	             break;

    case 7: 
	
	             rs=Integer.parseInt(instructPart[1], 2);
	             rt=Integer.parseInt(instructPart[2], 2);
	             s=instructPart[3]+instructPart[4]+instructPart[5];
	
	             s+="00";
	             result+="BGTZ R"+rs+", #"+Integer.parseInt(s, 2);
	
	             break;

                
    case 8:
        		 rs=Integer.parseInt(instructPart[1], 2);
        		 rt=Integer.parseInt(instructPart[2], 2);
        		 s=instructPart[3]+instructPart[4]+instructPart[5];
        		 rd=Integer.parseInt(s, 2);
        		 if(s.startsWith("1"))
					 rd=rd-65536;
        		 result+="ADDI R"+rt+", R"+rs+", #"+rd;
            	 break; 
            	 
    case 9: 
				 rs=Integer.parseInt(instructPart[1], 2);
				 rt=Integer.parseInt(instructPart[2], 2);
				 s=instructPart[3]+instructPart[4]+instructPart[5];
				 rd=Integer.parseInt(s, 2);
				 if(s.startsWith("1"))
					 rd=rd-65536;
				 result+="ADDIU R"+rt+", R"+rs+", #"+rd;
				break; 
     
            	
            	
    case 10:
        		 rs=Integer.parseInt(instructPart[1], 2);
        		 rt=Integer.parseInt(instructPart[2], 2);
        		 s=instructPart[3]+instructPart[4]+instructPart[5];
        		 rd=Integer.parseInt(s, 2);
        		 if(s.startsWith("1"))
        			rd=rd-65536;
        		result+="SLTI R"+rt+", R"+rs+", #"+rd;
        		break;  
       
    case 35: 
		          rs=Integer.parseInt(instructPart[1], 2);
		          rt=Integer.parseInt(instructPart[2], 2);
		          s=instructPart[3]+instructPart[4]+instructPart[5];
		 
		          result+="LW R"+rt+", "+ Integer.parseInt(s, 2)+"(R"+rs+")";
		           break;
                
       

    case 43: 
        		rs=Integer.parseInt(instructPart[1], 2);
        		rt=Integer.parseInt(instructPart[2], 2);
        		s=instructPart[3]+instructPart[4]+instructPart[5];
        		result+="SW R"+rt+", "+ Integer.parseInt(s, 2)+"(R"+rs+")";
        		 break;
        		 
       default: 
        	
        		break;
    }
		
		if(instructPart[0].equals("000000")&&instructPart[5].equals("001101"))
		{
			sign=true;
			
		}
		return sign;
		
	}
}
