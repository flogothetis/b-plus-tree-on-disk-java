package bplusTreeNode;

import java.nio.ByteBuffer;

public class InnerNode extends Node {
    public int [] mChildNodes ;

    public InnerNode(int maxDegree) {
        super(maxDegree);
        this.mChildNodes = new int [maxDegree + 1];
        this.mIsLeafNode = 0 ;
    }

    @Override
    public byte[] serialize(int pageSize) {
        ByteBuffer buffer = ByteBuffer.allocate(pageSize);
        buffer.putInt(this.mNumKeys);
        buffer.putInt(this.mIsLeafNode);
        buffer.putInt(this.leftNode);
        buffer.putInt(this.rightNode);
        buffer.putInt(this.parent);

        for (int i = 0; i < this.mKeys.length; i ++){
            if (i < this.mNumKeys) {
                buffer.putInt(mKeys[i]);
            }
            else {
                buffer.putInt(-1);
            }
        }

        for (int i = 0; i < this.mChildNodes.length; i ++){
            if (i < this.mNumKeys + 1) {
                buffer.putInt(mChildNodes[i]);
            }
            else {
                buffer.putInt(-1);
            }
        }

        return buffer.array();
    }

    @Override
    public void deserialize(byte [] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);

        this.mNumKeys = buffer.getInt();
        this.mIsLeafNode= buffer.getInt();
        this.leftNode= buffer.getInt();
        this.rightNode= buffer.getInt();
        this.parent= buffer.getInt();

        for (int i = 0; i < this.mKeys.length; i ++){
            this.mKeys[i] = buffer.getInt();
        }

        for (int i = 0; i < this.mChildNodes.length; i ++){
            this.mChildNodes[i] = buffer.getInt();
        }
    }
}
