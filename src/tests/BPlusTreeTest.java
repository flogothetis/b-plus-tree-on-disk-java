package tests;

import bplusTreeNode.InnerNode;
import bplusTreeNode.LeafNode;
import bplusTreeNode.Node;
import bplustree.BPlusTree;
import dataPageFactory.IntDataPageFactoryImpl;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class BPlusTreeTest {
    private BPlusTree bPlusTree;
    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        bPlusTree = new BPlusTree( 256, 32, new IntDataPageFactoryImpl(32));
        for (int i=0 ; i< 30 ; i++){
            bPlusTree.insert(i,i);
        }
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void insertWithSplit()  {
        Node node = bPlusTree.bplusNodeFactory.getNodeInstanceFromFile(bPlusTree.fileManagerIndex.readBlock(0));
        assertEquals(node.mIsLeafNode, 0);
        assertEquals(node.mNumKeys, 1);
        /* Check parent Node */
        assertEquals( 1 , ((InnerNode)node).mChildNodes[0]);
        assertEquals( 2 , ((InnerNode)node).mChildNodes[1]);
        assertEquals( 14 , ((InnerNode)node).mKeys[0]);
        /* Check first piece of split node */
        Node split1 = bPlusTree.bplusNodeFactory.getNodeInstanceFromFile(bPlusTree.fileManagerIndex.readBlock(1));
        for (int i =0 ; i< 14; i++){
            assertEquals( i , ((LeafNode)split1).mKeys[i]);
           // assertEquals( -1, ((LeafNode)split1).mChildDataPages[i]);
        }
        assertEquals(2, ((LeafNode)split1).rightNode);
        /* Check second piece of split node */
        Node split2 = bPlusTree.bplusNodeFactory.getNodeInstanceFromFile(bPlusTree.fileManagerIndex.readBlock(2));
        for (int i =0 ; i< 16; i++){
            assertEquals( i +14 , ((LeafNode)split2).mKeys[i]);
           // assertEquals( -1, ((LeafNode)split2).mChildDataPages[i]);
        }

    }

    @org.junit.jupiter.api.Test
    void searchWithOneSplit()  {

    }


    @org.junit.jupiter.api.Test
    void searchWithSplit()  {
        for (int i = 0 ; i < 30; i++) {
            assertEquals(bPlusTree.search(i), i);
        }
    }
}