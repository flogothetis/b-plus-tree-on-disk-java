package bplusTreeNode;

import java.nio.ByteBuffer;

public abstract class Node  {
    private  final int  maxDegree ;
    public int mNumKeys = 0;
    public int[]  mKeys;
    public int  mIsLeafNode = -1;
    public int leftNode =-1;
    public int rightNode = -1;
    public int parent = -1;


    protected Node(int maxDegree) {
        this.maxDegree = maxDegree;
        this.mKeys = new int[maxDegree];
    }


    abstract public  byte [] serialize (int pageSize);
    abstract  public void deserialize(byte [] data);
}