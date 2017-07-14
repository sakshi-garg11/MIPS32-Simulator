import java.util.*;

public class ReorderBuffer {
	public ReorderBuffer()
	{
		buffer = new ArrayList<ReorderBufferElement>();
	}
	
	/*
	public boolean insert(Instructions inst)
	{
		ReorderBufferElement rbe = new ReorderBufferElement();
		//Here!!! to be implemented~~~  content of rbe
		if(!available())
			return false;
		return buffer.add(rbe);
	}
	*/
	
	public void flush()
	{
		
	}
	
	public ReorderBufferElement getROB(int index)
	{
		return buffer.get(index);
	}
	
	public int getAvailable()
	{
		if(available()){
			ReorderBufferElement rbe = new ReorderBufferElement();
			buffer.add(rbe);
			return buffer.size() - 1;	//the index of the newly added element should always be size - 1
		}else
			return -1;
			
	}
	
	
	public void remove(int index)
	{
		buffer.remove(index);
	}
	
	public String toString()
	{
		String st = "ROB:\n";
		for(ReorderBufferElement rbe: buffer)
			st += "[" + rbe.getInst().toString2() + "] \n";
		return st;
	}
	
	public boolean available()
	{
		return !(buffer.size() >= maxsize);
	}
	
	public int getSize()
	{
		return buffer.size();
	}
	
	public int getIdx(ReorderBufferElement rbe)
	{
		for(int i = 0; i < buffer.size(); i++){
			if(buffer.get(i) == rbe)
				return i;
		}
		return -1;
	}
	
	private static final int maxsize = 6;
	private ArrayList<ReorderBufferElement> buffer;
	
	public class ReorderBufferElement{
		public ReorderBufferElement()
		{
			//busy = false;
			ready = false;
			inst = null;
			state = "";
			destination = -1;
			value = -1;
			updateCycle = 0;
		}
		public Instruction getInst(){return inst;}
		public void inst_set(Instruction s){inst = s;}
		public boolean getReady(){return ready;}
		public void ready_set(boolean r){ready = r;}
		public String getState(){return state;}
		public void setState(String s){state = s;}
		public int getDest(){return destination;}
		public void setDest(int d){destination = d;}
		public int getValue(){return value;}
		public void setValue(int v){value = v;}
		public int getUpdateCycle(){return updateCycle;}
		public void setUpdateCycle(int u){updateCycle = u;}
	
		private boolean busy;
		private boolean ready;
		private Instruction inst;
		private String state;
		private int destination;
		private int value;
		private int updateCycle;
	}
	
}
