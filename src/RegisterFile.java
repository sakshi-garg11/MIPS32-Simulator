
import java.util.*;

public class RegisterFile {
	public RegisterFile()
	{
		rfe = new ArrayList<RegFileElement>();
		for(int i = 0; i < size; i++)
			rfe.add(new RegFileElement(i, 0, 0));
	}
	
	public String toString(int cycle)
	{
		String st;
		st = "Registers:\n";
		
		for(int i = 0; i < size; i += 8){
			st += String.format("R%02d:", i);
			for(int j = i; j < i + 8; j++){
				st += String.format("\t%d",rfe.get(j).getValue(cycle));
			}
			st += "\n";
		}
		
		return st;
	}
	
	public int getReg(int i, int cycle)
	{
		return rfe.get(i).getValue(cycle);
	}
	
	public void setReg(int i, int v, int cycle)
	{
		rfe.get(i).setValue(v, cycle);
	}
	
	private static final int size = 32;

	private ArrayList<RegFileElement> rfe;
	
	public class RegFileElement{
		public RegFileElement(int a, int v, int cycle)
		{
			addr = a;
			oldValue = newValue = v;
			updateCycle = 0;
		}

		public int getAddr(){return addr;}
		public void setAddr(int a){addr = a;}
		public int getValue(int cycle){
			if(cycle > updateCycle)
				return newValue;
			else
				return oldValue;
		}
		public void setValue(int v, int cycle){
			oldValue = newValue;
			newValue = v;
			updateCycle = cycle;
		}
		public int getUpdateCycle(){return updateCycle;}
		public void setUpdateCycle(int u){updateCycle = u;}
		int addr;
		int newValue;
		int oldValue;
		int updateCycle = -1;
	}
}

