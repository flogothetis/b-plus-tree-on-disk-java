package tests;

import bplusTreeNode.InnerNode;
import bplusTreeNode.LeafNode;
import bplusTreeNode.Node;
import bplustree.BPlusTree;
import dataPageFactory.IntDataPageFactoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class BiggerBPlusTreeTest {


    private BPlusTree bPlusTree;
    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        bPlusTree = new BPlusTree( 256, 32, new IntDataPageFactoryImpl(32));
        for (int i=0 ; i< 44; i++){
            bPlusTree.insert(i,i);
        }
    }

    @Test
    void insert() {

        Node node = bPlusTree.bplusNodeFactory.getNodeInstanceFromFile(bPlusTree.fileManagerIndex.readBlock(0));
        assertEquals(node.mIsLeafNode, 0);
        assertEquals(node.mNumKeys, 2);
        /* Check parent Node */
        assertEquals( 1 , ((InnerNode)node).mChildNodes[0]);
        assertEquals( 2 , ((InnerNode)node).mChildNodes[1]);
        assertEquals( 14 , ((InnerNode)node).mKeys[0]);
        /* Check first piece of split node */
        Node split1 = bPlusTree.bplusNodeFactory.getNodeInstanceFromFile(bPlusTree.fileManagerIndex.readBlock(1));
        for (int i =0 ; i< 14; i++){
            assertEquals( i , ((LeafNode)split1).mKeys[i]);
        }
        assertEquals(2, ((LeafNode)split1).rightNode);
        /* Check second piece of split node */
        Node split2 = bPlusTree.bplusNodeFactory.getNodeInstanceFromFile(bPlusTree.fileManagerIndex.readBlock(2));
        for (int i =0 ; i< 14; i++){
            assertEquals( i +14 , ((LeafNode)split2).mKeys[i]);
        }
        /*Check third piece of split node */
        Node split3 = bPlusTree.bplusNodeFactory.getNodeInstanceFromFile(bPlusTree.fileManagerIndex.readBlock(3));
        for (int i =0 ; i< 14; i++){
            assertEquals( i +28 , ((LeafNode)split3).mKeys[i]);
        }

        /* Check linked list of leafs */
        assertEquals(3, split2.rightNode);
    }

    @Test
    void search() {
        for (int i=0 ; i< 44; i++){
            assertEquals(bPlusTree.search(i), i);
        }
    }

    @Test
    void searchRange1 (){
        assertEquals(  Arrays.asList(5, 6, 7, 8, 9, 10),   bPlusTree.searchRange(5, 10));
    }


    @Test
    void searchRange2 (){
        assertEquals(  Arrays.asList(13,14,15,16),   bPlusTree.searchRange(13, 16));
    }


    @Test
    void searchRange3 (){
        assertEquals(  Arrays.asList(0, 1, 2, 3, 4, 5),   bPlusTree.searchRange(-1, 5));
    }


    @Test
    void searchRange4 (){
        assertEquals(  Arrays.asList(40, 41, 42, 43),   bPlusTree.searchRange(40, 49));
    }
}