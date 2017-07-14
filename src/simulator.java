import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


//import MIPSsimA.BranchProcess;

public class simulator {
	private class BranchProcess{
    	BranchProcess(ReservationStation.RSElem r, int p){
    		rse = r;
    		predict = p;
    	}
    	public ReservationStation.RSElem rse;
    	public int predict;
    };
	private ArrayList<BranchProcess> branch = new ArrayList<BranchProcess>();
    
    private int cycle = 0;
    private int oldpc;
    private int newpc;
    private int updatecycle = 0;
 
	//List<Instruction> allInst;
	boolean flag=false;
	
	private List<Instruction> pre_queue;
    private InstructionQueue iq;
    private BranchTargetBuffer buffer_bt;
    private RegisterFile rf;
    private RegisterStat r_stat;
    private ReorderBuffer rob;
    private ReservationStation rs;
    private DataSegment ds;
    private boolean stop = false;

    private String inputFilename = "", outputFilename = "", operation = "";
    private FileInputStream inputFile;
    private DataInputStream inputStream;
    private PrintWriter outputWriter;
    private int startCycle = 0, endCycle = 0;
    private int currentLine;
    private int endLine = 99999999;
    
    private int pc_get(int cycle)
    {
    	if(cycle > updatecycle)
    		return newpc;
    	else
    		return oldpc;
    }
    private int getnewpc()
    {
    	return newpc;
    }
    private void pc_set(int pc, int cycle)
    {
    	oldpc = newpc;
    	newpc = pc;
    	updatecycle = cycle;
    }
	public simulator(List<Instruction> inst,PrintWriter a)
	{
		
		this.pre_queue=inst;
		iq = new InstructionQueue();
    	buffer_bt = new BranchTargetBuffer();
    	rf = new RegisterFile();
    	r_stat = new RegisterStat();
    	rob = new ReorderBuffer();
    	rs = new ReservationStation();
    	ds = new DataSegment();
    	outputWriter=a;
    	currentLine = Defines.startCodeLine;
    	oldpc = newpc = Defines.startCodeLine;
	}
	
	public void simulate() {
		// TODO Auto-generated method stub
		//call open file
		do{
    		cycle++;
    		if(cycle==17)
    			stop=true;
    		if(flag == false && startCycle <= cycle)
    			flag = true;
    		if(flag == true && endCycle > 0 && endCycle < cycle)
    			flag = false;
    		if(flag)outputWriter.println("Cycle " + "<" + cycle + ">:");
    		cleaning();
    		
    		InstructionCommit();
    		InstructionWriteback();
 
    		InstructionExecute();
    		InstructionIssue();
    		InstructionFetch();
	
    		if(flag)outputWriter.print(iq.toString());
    		if(flag)outputWriter.print(rs.toString());
    		if(flag)outputWriter.print(rob.toString());
    		if(flag)outputWriter.print(buffer_bt.toString(cycle));
    		if(flag)outputWriter.print(rf.toString(cycle));
    		if(flag)outputWriter.print(ds.toString(cycle)); 
			
    		InstructionExecute2();
    		if((rs.size() == 0 && rob.getSize() == 0 && cycle > 1))
    			stop = true;
    	}
		while(!stop);
	}
	
	private Instruction InstructionFetch()
    {
    	if(pc_get(cycle) > endLine)
    		return null;
    	Instruction inst = pre_queue.get(((pc_get(cycle) - Defines.startCodeLine)/4));
    	if(inst == null)
    	{
    		System.out.println("instruction fetch: all done");
    		return inst;
    	}
    	
    	iq.enQueue(inst);
    	
    	if(inst.getInstType() == Defines.InstType_BRANCH)
    	{
    		if(buffer_bt.predict(pc_get(cycle), cycle, false) == Defines.Predict_TRUE)
    		{
    			pc_set(buffer_bt.target(pc_get(cycle)), cycle);
    			inst.setPredicted(1);
    		}
    		else
    		{
    			pc_set(pc_get(cycle) +4, cycle);
    			int bb = buffer_bt.predict(pc_get(cycle), cycle, false);
    			//if(inst.getOpName().equals("J"))
    			//	p = Defines.PredictType.Predict_TRUE;
    			buffer_bt.insert(inst.getAddr(), inst.getTargetAddr(getnewpc()), bb, cycle); /* temporary value for target address and prediction, to be update
    			 										  * after Instruction Execution stage.
    			 										  */
    			inst.setPredicted(0);

    		}
    	}
    	else
    		pc_set(pc_get(cycle) + 4, cycle);
    	return inst;
    }
	
	 private boolean InstructionIssue()
	    {
	    	Instruction inst;
	  	
	    	if(rob.available()){
	    		inst = iq.getFirst(); 
	    		if(inst != null && (inst.OpName()==Defines.FUNCT_NOP || inst.OpName()==Defines.FUNCT_BREAK)){
	    			inst = iq.deQueue();
	    			int robidx = rob.getAvailable();
	        		ReorderBuffer.ReorderBufferElement robb = rob.getROB(robidx);
	            	robb.inst_set(inst);
	            	robb.ready_set(true);
	        		return true;
	    		}
	    	}
	    	
	    	if(rob.available() && rs.available()){

	    		inst = iq.deQueue();
	        	if(inst == null)
	        		return false;
	    		int robidx = rob.getAvailable();
	    		ReorderBuffer.ReorderBufferElement robb = rob.getROB(robidx);
	    		ReservationStation.RSElem rser = rs.getAvailable();
	 
	        	if(inst.getSource1() != -1){      		
	        		RegisterStat.RegStatElement rses1 = r_stat.getRegStat(inst.getSource1());
	        		if(true == rses1.getBusy()){
	        			ReorderBuffer.ReorderBufferElement h = rses1.getReorder();
	        		
	        			if(h.getReady() == true){
	        				rser.vj_set(h.getValue(), cycle);
	        				rser.setQj(null, cycle);
	        			}else
	        				rser.setQj(h, cycle);
	        		}else{
	        			rser.vj_set(rf.getReg(inst.getSource1(), cycle), cycle);
	        			rser.setQj(null, cycle);
	        		}
	        	}
	        	if(inst.getSource2() != -1){       		
	        		RegisterStat.RegStatElement rses2 = r_stat.getRegStat(inst.getSource2());
	        		if(true == rses2.getBusy()){
	        			ReorderBuffer.ReorderBufferElement h = rses2.getReorder(); 			
	        			if(true == h.getReady()){
	        				rser.vk_set(h.getValue(), cycle);
	        				rser.setQk(null, cycle);
	        			}else{
	        				rser.setQk(h, cycle);
	        			}
	        		}else{	    			
	        			rser.vk_set(rf.getReg(inst.getSource2(), cycle), cycle);
	        			rser.setQk(null, cycle);
	        		}
	        	}
	        	if(inst.getDest() != -1){
	        		RegisterStat.RegStatElement rsed = r_stat.getRegStat(inst.getDest());
	        		rsed.setReorder(robb);
	        		rsed.setBusy(true);
	        		robb.setDest(inst.getDest());
	        	}
	        	if(inst.getImm() != -1){
	        		rser.setA(inst.getImm());
	        	}
	        	rser.inst_set(inst);
	        	rser.dest_set(robb, cycle);
	        	rser.busy_set(true);
	        	robb.inst_set(inst);
	        	robb.ready_set(false);
	    //if(inst.getInstType()==Defines.InstType.InstType_ST)
	    //System.out.println("issue: getVj="+rser.getVj(cycle, false)+" getVk="+rser.getVk(cycle, false)
	    //+" getImm="+rser.getA()+" qj="+rser.getQj(cycle, false)+" qk="+rser.getQk(cycle, false));
	        	return true;
	    	}
	    	return false;
	    }
	 
	 private void InstructionExecute()
	    {
	    	for(int i = 0; i < rs.size(); i++){
	    		ReservationStation.RSElem a = rs.getRSElem(i);
	    		if(!rs.dependency(i, cycle) && rs.getRSElem(i).getCompute() == -1){
	    			ReservationStation.RSElem rse = rs.getRSElem(i);
	    			Instruction inst = rse.getInst();
	    			
	    			if(inst.getInstType() == Defines.InstType_LD 
	    					|| inst.getInstType() == Defines.InstType_ST){
	    				//check "all load/store ahead of this load have their addresses ready in the ROB" 
	    			}
	    			
	    			int dst = rs.compute(i, cycle);
	    			
	    			if(inst.getInstType() == Defines.InstType_BRANCH){
						int p;
						if(dst == 1)
							p = Defines.Predict_TRUE;
						else 
							p = Defines.Predict_FALSE;
	    				BranchProcess bp = new BranchProcess(rse, p);
	    				branch.add(bp);
	    			
	    			}
	    			else if(inst.OpName()==Defines.OPCODE_SW)
	    			{
	    				
	    				ReorderBuffer.ReorderBufferElement robe =rse.getDest(cycle, false);
	    				rse.busy_set(false);
	    				
	    				robe.setDest(rse.getCompute());
	    				
	    				if(robe.getValue() == -1 && rse.getQk(cycle, false) == null)
	    					robe.setValue(rse.vk_get(cycle, false));
	    				if(robe.getValue() != -1)
	    					robe.ready_set(true);	
	    			}else if(inst.OpName()==Defines.FUNCT_NOP){
	    				System.out.println("shouldn't come here");
	    			}else if(inst.OpName()==Defines.FUNCT_NOP){
	    				System.out.println("shouldn't come here");
	    			}
	    		}
	    	}
	    	//in the end: if the executed instruction is a branch instruction, resolve btb
	    }
	 
	 private void InstructionExecute2()
	    {
			if(branch.size() == 0)
				return;
			
			while(!branch.isEmpty()){
				BranchProcess bp = branch.remove(0);
				ReservationStation.RSElem rse = bp.rse;
				int p = bp.predict;
				Instruction inst = rse.getInst();
					
				ReorderBuffer.ReorderBufferElement robe = rse.getDest(cycle, true);

				if(p == buffer_bt.predict(inst.getAddr(), cycle, true) 
					|| (p == Defines.Predict_FALSE 
						&& buffer_bt.predict(inst.getAddr(), cycle, true) == Defines.Predict_NOTSET)){ //output == predict
					robe.setState("Commit");
					robe.ready_set(true);
					buffer_bt.new_val(inst.getAddr(), buffer_bt.target(inst.getAddr()), p, cycle);
				}else{
					int j;
					
					j = rob.getIdx(rse.getDest(cycle, true));
					while(j < rob.getSize() - 1){			
						for(int k = rs.size() - 1; k >= 0; k--){
							ReservationStation.RSElem rse1 = rs.getRSElem(k);
							if(rob.getIdx(rse1.getDest(cycle, true)) == rob.getSize() - 1){
								rs.remove(k);
								break;
							}
						}
						rob.remove(rob.getSize() - 1); 
					}
					
					
					while(iq.size() > 0)
						iq.deQueue();
					
					robe.setState("Commit");
					robe.ready_set(true);
					if(p == Defines.Predict_TRUE && buffer_bt.predict(inst.getAddr(), cycle, true) != Defines.Predict_TRUE){ //I think this branch includes "J **" operation
						int taddr = buffer_bt.target(inst.getAddr());
						pc_set(taddr, cycle);
						buffer_bt.new_val(inst.getAddr(), taddr, Defines.Predict_TRUE, cycle);
					}else{
						int taddr = buffer_bt.target(inst.getAddr());
						pc_set(inst.getAddr() + 4, cycle);
						buffer_bt.new_val(inst.getAddr(), taddr, Defines.Predict_TRUE, cycle);
					}
				}
				rse.busy_set(false);

	    }
	    }
	 
	 
	 private void InstructionWriteback()
	    {
	    	for(int i = 0; i < rs.size(); i++){
	    		ReservationStation.RSElem reserve = rs.getRSElem(i);
	    		Instruction inst = reserve.getInst();
	    		if(reserve.getCompute() != -1){ 
	    			if(inst.getInstType() == Defines.InstType_LD){
	    				if(reserve.getLoadVal() == -1){
		    				
		    				boolean conflict = false;
		    				for(int j = 0; j< rob.getIdx(reserve.getDest(cycle, false)); j++){
		    					ReorderBuffer.ReorderBufferElement robe = rob.getROB(j);
		    					if(robe.getInst().getInstType() == Defines.InstType_ST 
		    							&& robe.getDest() == reserve.getCompute())
		    						
		    						conflict = true;
		    						break; 
		    				}
		    				if(conflict)
		    					continue;
	    				
		    				reserve.setLoadVal(ds.getData(reserve.getCompute(), cycle));
		    				reserve.setCompute(reserve.getLoadVal());
	    				}else{
	    				
	    					broadcastCDB(reserve);
	    				}
	    			}else if(inst.getInstType() == Defines.InstType_ST){
	    				
	    			}else{ 
	    				broadcastCDB(reserve);
	    			}
	    			
	    		}
	    	}
	    }
	    
	    private void InstructionCommit()
	    {
	    	if(rob.getSize() > 0){
		    	ReorderBuffer.ReorderBufferElement robe = rob.getROB(0);
		    	if(robe.getReady()){
		    		Instruction inst = robe.getInst();
		    		if(inst.getInstType() == Defines.InstType_BRANCH){
		    			
		    		}else if(inst.OpName() == Defines.FUNCT_NOP){
		    			
		    		}else if(inst.OpName()==Defines.FUNCT_BREAK){
		    		}else if(inst.getInstType() == Defines.InstType_ST){
		    			//System.out.println("**************** st: "+robe.getDest()+" "+robe.getValue());
		    			ds.setData(robe.getDest(), robe.getValue(), cycle);
		    		}else{
		    			//regular instructions, move store to InstructionCommit2
		    			rf.setReg(robe.getDest(), robe.getValue(), cycle);
		    		}
		    		rob.getROB(0).setState("Committed");
		    	}else{
		    	}
	    	}

	    	return;
	    }
	    
	    private void cleaning()
	    {
	    	for(int i = rs.size() - 1; i >= 0; i--){
	    		ReservationStation.RSElem rse = rs.getRSElem(i);
	    		if(!rse.getBusy())
	    			rs.remove(i);
	    	}
	    	
	    	if(rob.getSize() > 0){
	    		if(rob.getROB(0).getState().equals("Committed")){
	    			if(rob.getROB(0).getInst().getInstType() != Defines.InstType_ST
	    					&& rob.getROB(0).getInst().getInstType() != Defines.InstType_BRANCH
	    					&& rob.getROB(0).getInst().OpName()!=Defines.FUNCT_NOP
	    					&& rob.getROB(0).getInst().OpName()!=Defines.FUNCT_BREAK){
	    				r_stat.getRegStat(rob.getROB(0).getDest()).setReorder(null);
	    				r_stat.getRegStat(rob.getROB(0).getDest()).setBusy(false);
		    		}
	    			rob.remove(0);
	    			
	    		}
	    	}
	    	
	    }
	    
	    private void broadcastCDB(ReservationStation.RSElem rse)
	    {
			ReorderBuffer.ReorderBufferElement buffer_reorder = rse.getDest(cycle, false);
			rse.busy_set(false);
			for(int j = 0; j < rs.size(); j++)
			  {
				ReservationStation.RSElem rsej = rs.getRSElem(j);
				
				if(rsej.getQj(cycle, false) == rse.getDest(cycle, false))
				{
				
					rsej.vj_set(rse.getCompute(), cycle);
					rsej.setQj(null, cycle);
				}
				if(rsej.getQk(cycle, false) == rse.getDest(cycle, false))
				{
					rsej.vk_set(rse.getCompute(), cycle);
					rsej.setQk(null, cycle);
				}
			  }
			buffer_reorder.setValue(rse.getCompute());
			buffer_reorder.ready_set(true);
			//cannot inform store instructions by update values in ReservationStation, 
			//query ROB for instructions who need it.
			for(int k = 0; k <rob.getSize(); k++){
				Instruction in = rob.getROB(k).getInst();
				if(in.getInstType() == Defines.InstType_ST && in.getSource2() == rse.getInst().getDest())
				{
					rob.getROB(k).setValue(rse.getCompute());
					if(rob.getROB(k).getDest() != -1)
						rob.getROB(k).ready_set(true);
				}
			}
	    }
	    
	    
	    
	    }
	
	
