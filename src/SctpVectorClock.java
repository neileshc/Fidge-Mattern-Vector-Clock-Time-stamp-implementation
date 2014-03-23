
public class SctpVectorClock {
public static int[] currentvectorClock=new int[Configfilereader.totalnodes];

public SctpVectorClock()
{
	System.out.println("Initializing Vector Clock content to 0");
	for(int i=0;i<Configfilereader.totalnodes;i++)
	{
				currentvectorClock[i]=0;
	}
}

public void incrementVectorClock(int node)
{
	currentvectorClock[(node-1)]=currentvectorClock[(node-1)] + 1;
}

public void updateVectorClock(int node, int[] receiveVector)
{
	for(int i=0;i<Configfilereader.totalnodes;i++)
	{
		//currentvectorClock[i] = Math.max(currentvectorClock[i], receiveVector[i]);
	    if(currentvectorClock[i] >= receiveVector[i])
	    	currentvectorClock[i] = currentvectorClock[i];
	    else
	    	currentvectorClock[i] =receiveVector[i];
	}
	incrementVectorClock(node);

}


/*
public void updateVectorClock(int[] receiveVector)
{
	for(int i=0;i<Configfilereader.totalnodes;i++)
	{
		//currentvectorClock[i] = Math.max(currentvectorClock[i], receiveVector[i]);
	    if(currentvectorClock[i] >= receiveVector[i])
	    	currentvectorClock[i] = currentvectorClock[i];
	    else
	    	currentvectorClock[i] =receiveVector[i];
	}
	
}*/


public int[] getCurrentvectorClock() {
	return currentvectorClock;
}

public void setCurrentvectorClock(int[] receiveVector) {
	currentvectorClock = receiveVector;
}
}
