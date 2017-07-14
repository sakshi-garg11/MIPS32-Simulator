import java.util.*;

public class BranchTargetBuffer {
	public BranchTargetBuffer()
	{
		b_tb = new BtbSet[sets];
		for(int i = 0; i < sets; i++)
			b_tb[i] = new BtbSet(way);
	}
	
	public int predict(int addres, int cycle, boolean forcenew)
	{
		//Defines.PredictType pre = Defines.PredictType.Predict_NOTSET;
		int offset = (addres/sets)%sets;
		
		return b_tb[offset].predict(addres, cycle, forcenew);
	}
	
	public boolean insert(int addr, int t_addr, int pre, int cycle)
	{
		int offset = (addr/sets)%sets;
		return b_tb[offset].insert(addr, t_addr, pre, cycle);
	}
	
	public boolean new_val(int addres, int taddr, int pre, int cycle)
	{
		int offset = (addres/sets)%sets; 
		return b_tb[offset].updateValue(addres, taddr, pre, cycle);
	}
	
	public int target(int addres)
	{
		int offset = (addres/sets)%sets;
		return b_tb[offset].getTargetAddr(addres);
	}

	public String toString(int cycle)
	{
		String st = "BTB:\n";
		//int j = 0;
		for(int i = 0; i < b_tb.length; i++){
			for(int k = 0; k < b_tb[i].getElem().size(); k++){
				int addr = b_tb[i].getElem().get(k).getInstAddr();
				int taddr = b_tb[i].getElem().get(k).getTargetAddr();
				int pre = b_tb[i].getElem().get(k).getPredict(cycle);
				st += "[Set " + (i) + "]:<" + addr + "," + taddr + ",";
				if(pre == Defines.Predict_NOTSET)
					st += "NotSet>\n";
				else if (pre == Defines.Predict_FALSE)
					st += "0>\n";
				else
					st += "1>\n";
			}
		}
		return st;
	}


	private final int size = 16;
	private final int way = 4;
	private final int sets = size/way;
	
	private BtbSet[] b_tb;
	
	public class BtbSet
	{
		public BtbSet(int size)
		{
			elems = new ArrayList<BtbElem>();
			setSize = size;  
		}
		
		public int predict(int addr, int cycle, boolean forcenew)
		{
			int pre = Defines.Predict_NOTSET;
			BtbElem be;
			for(int i = 0; i < elems.size(); i++){
				if(addr == elems.get(i).getInstAddr()){
					if(forcenew)
						pre = elems.get(i).getNewPredict();
					else
						pre = elems.get(i).getPredict(cycle);
					/*the purpose of the following two codes is to move the visited branch to the end, so as to LRU*/
					if(i < elems.size() - 1){
						be = elems.remove(i);
						elems.add(be);
					}
				}
			}
			return pre;
		}
		
		public boolean insert(int addr, int taddr, int pre, int cycle)
		{
			for(int i = 0; i < elems.size(); i++){
				if(addr == elems.get(i).getInstAddr()){
					elems.get(i).setTargetAddr(taddr);
					elems.get(i).setPredict(pre, cycle);
					return true;
				}
			}
			BtbElem be = new BtbElem(addr, taddr, pre);
			if(elems.size() == setSize) //already full
				elems.remove(0); //remove the first element, due to LRU
			return elems.add(be);
		}
		
		public boolean updateValue(int addr, int taddr, int pre, int cycle)
		{
			BtbElem be;
			for(int i = 0; i < elems.size(); i++){
				if(addr == elems.get(i).getInstAddr()){	//found
					/*update the value of the visited branch and move it to the end, so as to LRU*/
					elems.get(i).setTargetAddr(taddr);
					elems.get(i).setPredict(pre, cycle);
					if(i < elems.size() - 1){
						be = elems.remove(i);
						return elems.add(be);
					}else
						return true;
				}
			}
			return false;
		}
		
		public int getTargetAddr(int addr)
		{
			int tad = -1;
			BtbElem be;
			for(int i = 0; i < elems.size(); i++){
				if(addr == elems.get(i).getInstAddr()){	//found
					tad = elems.get(i).getTargetAddr();
					if(i < elems.size() - 1){
						be = elems.remove(i);
						elems.add(be);
					}
				}
			}
			return tad;
		}
		
		public ArrayList<BtbElem> getElem()
		{
			return elems;
		}
		
		
		private int setSize; 
		private ArrayList<BtbElem> elems;
	}
	
	public class BtbElem implements Cloneable{
		public BtbElem(int addr, int taddr, int pred)
		{
			addr_inst = addr;
			addr_target = taddr;
			oldpredict = newpredict = pred;
			updateCycle = 0;
		}
		public BtbElem()
		{
			this(0, 0, Defines.Predict_NOTSET);
		}
		public BtbElem clone ()
		{
			try {
				return (BtbElem)super.clone();
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		public int getInstAddr()
		{
			return addr_inst;
		}
		public void setInstAddr(int addr)
		{
			addr_inst = addr;
		}
		public void setTargetAddr(int addr)
		{
			addr_target = addr;
		}
		public int getTargetAddr()
		{
			return addr_target;
		}
		public int getPredict(int cycle)
		{
			if(cycle > updateCycle)
				return newpredict;
			else
				return oldpredict;
		}
		public int getNewPredict()
		{
			return newpredict;
		}
		public void setPredict(int pred, int cycle)
		{
			oldpredict = newpredict;
			newpredict = pred;
			updateCycle = cycle;
		}
		int addr_inst = 0;
		int addr_target = 0;
		int oldpredict = Defines.Predict_NOTSET;	
		int newpredict = Defines.Predict_NOTSET;	
		int updateCycle = -1;
		Instruction instruction;
	};

}
