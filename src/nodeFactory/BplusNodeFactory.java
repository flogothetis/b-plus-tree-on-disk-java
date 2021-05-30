package nodeFactory;



import bplusTreeNode.InnerNode;
import bplusTreeNode.LeafNode;
import bplusTreeNode.Node;

import java.nio.ByteBuffer;

public  class BplusNodeFactory {
    private int maxDegree, pageSize;

    public BplusNodeFactory(int maxDegree, int pageSize)
    {
        this.maxDegree = maxDegree;
        this.pageSize = pageSize;
    }

    public  Node getNodeInstanceFromFile (byte [] data ) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        int mNumKeys = buffer.getInt();
        int isleaf= buffer.getInt();
        Node node =  isleaf == 1 ? new LeafNode(maxDegree) : new InnerNode(maxDegree);
        node.deserialize(data);
        return node;
    }


    public byte [] nodeSerializer (Node node) {
        if (node.mIsLeafNode == 0){
            return  ((InnerNode)node).serialize(pageSize);
        }
        else{
            return ((LeafNode)node).serialize(pageSize);
        }


    }
}
