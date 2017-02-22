/**
 * Created by vincent on 16/1/29.
 */
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
public class Memory extends JPanel{
    Map<Integer,Integer> block;
    Cache cache;//<address,value>
    String log;

    Memory(Cache ca){
        block = new HashMap<Integer, Integer>();//Each block contains a value at some address in memory.
        cache = ca;
    }

    Integer load(int address){

       int result=cache.ifHit(address);
       if (result==-1)
       {
           Util.writeLog("Read Miss!");
    	   int[] value=new int[4];
    	   for (int i=0;i<4;i++)
    	   {
    		   if (block.containsKey(address/4*4+i))
    		   {
    			   value[i]=block.get(address/4*4+i);
    		   }
    		   else
    		   {
    			   value[i]=0;
    		   }
    	   }
    	       	   
    	   Line temp=cache.store(address, value);

           //Write back policy
    	   if (temp.getTag()!=-1)
    	   {
    		   for (int i=0;i<4;i++)
    		   {
    			   storeBack(temp.getTag()*4 + i, temp.getWord(i));
    		   }
    	   }

           return block.get(address);
      
       }
       else
       {
           Util.writeLog("Read Hit!");
    	   return cache.load(result, address);
       }
    }

    boolean store(int address, int value){
    	int result=cache.ifHit(address);
    	if (result!=-1){
            Util.writeLog("Write Hit!");
    	    cache.write(result,address,value);
    	}else{
    	    block.put(address,value);
            Util.writeLog("Memory("+address+") --> "+value);
        }
        return true;
    }

    boolean storeBack(int address, int value){
        block.put(address,value);
        return true;
    }

    void reset(){
        block.clear();
        cache.clear();
    }
    
    String printMem()
    {
    	String result="";
    	for (Integer i:block.keySet())
    	{
    		result=result+"Memory("+i+") --> ";
    		result=result+block.get(i)+"\n";
    	}
    	return result;
    }
    

    void createUI(){
    };
}
