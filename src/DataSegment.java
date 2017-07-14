import java.util.ArrayList;


public class DataSegment {

	public DataSegment()
	{
		d_s = new ArrayList<DataSegmentElement>();
		for(int i = 0; i < size; i+=4)
			d_s.add(new DataSegmentElement(address_base + i, 0, 0));
	}
	
	public String toString(int cycle)
	{
		String st = "Data Segment:\n";
		st += address_base + ":";
		for(DataSegmentElement d:d_s){
			int val = d.getValue(cycle);
			/*
			st += ((val & 0xff000000)>>24) + "\t";
			st += ((val&0xff0000)>>16) + "\t";
			st += ((val&0xff00)>>8) + "\t";
			st += (val&0xff) + "\t";
			*/
			st += "\t" + val;
		}
		st += "\n";
		return st;
	}
	
	public int getData(int index, int cycle)
	{
		//System.out.println("getData: index-"+ index);
		return d_s.get((index - address_base)/4).getValue(cycle);
	}
	
	public void setData(int index, int value, int cycle)
	{
		//System.out.println("index="+index+",value="+value);
		d_s.get((index - address_base)/4).setValue(value,cycle);
	}
	
	private static final int size = 40;
	private static final int address_base = Defines.startDataLine;
	
	private ArrayList<DataSegmentElement> d_s;
	
	public class DataSegmentElement{
		public DataSegmentElement(int a, int v, int cycle)
		{
			addr = a;
			oldValue = newValue = v;
			cycle_update = 0;
		}

		public int getAddr()
		{
			return addr;
		}
		public void setAddr(int a)
		{
			addr = a;
			
		}
		public int getValue(int cycle)
		{
			if(cycle > (cycle_update + 1))
				return newValue;
			else
				return oldValue;
		}
		
		public void setValue(int v, int cycle)
		{
			oldValue = newValue;
			newValue = v;
			cycle_update = cycle;
		}
		
		public int getUpdateCycle()
		{
			return cycle_update;
		}
		
		public void setUpdateCycle(int u)
		{
			cycle_update = u;
		}
		
		int addr;
		int newValue;
		int oldValue;
		int cycle_update = -1;
	}
}
