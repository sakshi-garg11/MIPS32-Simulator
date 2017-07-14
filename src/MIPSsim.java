import java.io.IOException;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MIPSsim 
{
	public static void main(String[] args) 
	{
		List<Instruction> insts=new ArrayList<Instruction>();
		if (args.length>1 && args[2].equals("dis") && !args[1].equals(null) && !args[0].equals(null))
			try 
		{
				instructiondecode d=new instructiondecode(insts);
				PrintWriter write = new PrintWriter(args[1], "UTF-8");
				write.print(d.disassemble(args[0]));
				write.close();
				
		} 
		
		catch (IOException e) 
		   {
				
				e.printStackTrace();
			}
		
		else if(args.length>1 && args[2].equals("sim") && !args[1].equals(null) && !args[0].equals(null))	
		   try 
		   {
			   instructiondecode d=new instructiondecode(insts);
			   d.disassemble(args[0]);
			   PrintWriter write = new PrintWriter(args[1], "UTF-8");
			   simulator s=new simulator(insts,write);
			   s.simulate();
			   write.close();

			} 
			catch (IOException e) 
			   {
					
					e.printStackTrace();
				}
		else
			System.out.println("Invalid Input");
		
	}
}
