package tests;

import bplusTreeNode.InnerNode;
import bplusTreeNode.Node;
import fileManager.FileManager;
import nodeFactory.BplusNodeFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BplusNodeFactoryTest {

    InnerNode innerNode ;
    FileManager fileManager;
    BplusNodeFactory bplusNodeFactory;
    @BeforeEach
    void setUp() {
     innerNode = new InnerNode(3);
     innerNode.mChildNodes [0] = 2 ;
     innerNode.mChildNodes[1] = 3 ;
     innerNode.mKeys[0] = 2;
     innerNode.mKeys[1] = 3;
     innerNode.mNumKeys = 2;
     fileManager = new FileManager(256);
     fileManager.createFile("unitest.bin");
     bplusNodeFactory = new BplusNodeFactory(3, 256);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getNodeInstanceFromFile() {
        fileManager.writeBlock(bplusNodeFactory.nodeSerializer(innerNode), 0);
        Node node= bplusNodeFactory.getNodeInstanceFromFile(fileManager.readBlock(0));
        InnerNode inner = (InnerNode) node;
        assertEquals( 2, inner.mChildNodes[0]);
        assertEquals( 3, inner.mChildNodes[1]);
    }

    @Test
    void nodeSerializer() {
    }
}