
public class Line {
	
	int tag;
	int[] word;
	boolean dirty;
	
	public Line(){
        word=new int[4];
		tag=-1;
		for (int i=0;i<4;i++){
			word[i]=0;
		}
		dirty=false;
	}
	
	public Line(int tag,int[] word){
        this.word = new int[4];
		this.tag=tag;
		for (int i=0;i<4;i++){
			this.word[i]=word[i];
		}
		dirty=false;
	}
	
	void setTag(int value)
	{
		tag=value;
	}
	
	void setWord(int[] value){
		for (int i=0;i<4;i++){
			word[i]=value[i];
		}
	}
	
	void setWord(int i,int value){
		word[i]=value;
	}
	
	void setDirty(boolean value)
	{
		dirty=value;
	}
	int getTag()
	{
		return tag;
	}
	
	int getWord(int i){
		return word[i];
	}
	int[] getWord()
	{
		return word;
	}
	boolean ifDirty(){
		return dirty;
	}

    void clear(){
        setTag(-1);
        for (int i=0;i<4;i++){
            this.word[i]=word[i];
        }
        dirty=false;
    }
}
