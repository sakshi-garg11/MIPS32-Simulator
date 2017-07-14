import java.util.*;

public class RegisterStat {
	public RegisterStat()
	{
		rse = new RegStatElement[size];
		for(int i = 0; i < size; i++)
			rse[i] = new RegStatElement(i);
	}
	
	public RegStatElement getRegStat(int index){
		return rse[index];
	}
	
	public boolean updateRegStat(int index){
		//String st = "" + rse[index].getField() + rse[index].getReorder() + rse[index].getBusy();
		//System.out.println(st);
		return true;
	}
	
	public int size()
	{
		return size;
	}
	
	private static final int size = 32;
	
	//private Arrays elements;
	
	public class RegStatElement{
		public RegStatElement(int i)
		{
			Field = i;
			Reorder = null;
			Busy = false;
		}
		public int getField(){return Field;}
		public void setField(int f){Field = f;}
		public ReorderBuffer.ReorderBufferElement getReorder(){return Reorder;}
		public void setReorder(ReorderBuffer.ReorderBufferElement r){Reorder = r;}
		public boolean getBusy(){return Busy;}
		public void setBusy(boolean b){Busy = b;}
		private int Field;
		private ReorderBuffer.ReorderBufferElement Reorder;
		private boolean Busy;
	};
	private RegStatElement[] rse; 
}
