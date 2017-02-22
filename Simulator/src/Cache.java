
public class Cache {
	
	Line[] cacheline=new Line[16];
	int nextToReplace;

	public Cache()
	{
		
		for (int i=0;i<16;i++)
		{
			cacheline[i]=new Line();
			
		}
		nextToReplace=0;
	}
	
	Integer load(int i,int address)
	{
		int j=getWord(address);
		return cacheline[i].getWord(j);
	
	}
	
	Line store(int address,int[] value)
	{
		int tag=getTag(address);
		int word=getWord(address);
		Line result;

        //If cache is set dirty, it needs to write back before it is replaced.
		if (cacheline[nextToReplace].ifDirty())
		{
			result = new Line(cacheline[nextToReplace].getTag(),cacheline[nextToReplace].getWord());
		}else{
            result = new Line();
        }
		cacheline[nextToReplace].setTag(tag);
		cacheline[nextToReplace].setWord(value);
        Util.writeLog(printCache(nextToReplace));
		nextToReplace=(nextToReplace+1)%16;
		return result;
	}
	
	boolean write(int i,int address, int value)
	{
		int j=getWord(address);
		cacheline[i].setWord(j,value);
		cacheline[i].setDirty(true);
        Util.writeLog(printCache(i));
		return true;
	}
	
	int getTag(int address)
	{
		return (address/4);
	}
	
	int ifHit(int address)
	{
		int tag=getTag(address);
		int word=getWord(address);
		for (int i=0;i<16;i++)
		{
			if (cacheline[i].getTag()==-1)
				break;
			if (cacheline[i].getTag()==tag)
				return i;
		}
		return -1;
	}
	
	int getWord(int address)
	{
		return (address%4);
	}
	
	String printCache(int i)
	{
		String result="Cache("+i+") ---> "+(cacheline[i].getTag()*4)+"-"+(cacheline[i].getTag()*4+1)+"-"+(cacheline[i].getTag()*4+2)+"-"+(cacheline[i].getTag()*4+3);
	    return result;
	}

    public void clear(){
        for (int i=0;i<16;i++){
            cacheline[i].clear();
        }
    }
	
}
