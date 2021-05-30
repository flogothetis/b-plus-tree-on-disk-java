package bplustree;



import bplusTreeNode.InnerNode;
import bplusTreeNode.LeafNode;
import bplusTreeNode.Node;
import dataPageFactory.DataPageFactory;
import fileManager.FileManager;
import nodeFactory.BplusNodeFactory;

import java.io.IOException;
import java.lang.Object;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

/* 
 * Unlike a binary search tree, each node of a B+-tree may have a variable number of keys and children.
 * The keys are stored in non-decreasing order. Each node either is a leaf node or
 * it has some associated children that are the root nodes of subtrees.
 * The left child node of a node's element contains all nodes (elements) with keys less than or equal to the node element's key
 * but greater than the preceding node element's key (except for duplicate internal node elements).
 * If a node becomes full, a split operation is performed during the insert operation.
 * The split operation transforms a full node with 2*T-1 elements into two nodes with T-1 and T elements
 * and moves the median key of the two nodes into its parent node.
 * The elements left of the median (middle) element of the splitted node remain in the original node.
 * The new node becomes the child node immediately to the right of the median element that was moved to the parent node.
 * 
 * Example (T = 4):
 * 1.  R = | 1 | 2 | 3 | 4 | 5 | 6 | 7 |
 * 
 * 2.  Add key 8
 *   
 * 3.  R =         | 4 |
 *                 /   \
 *     | 1 | 2 | 3 | -> | 4 | 5 | 6 | 7 | 8 |
 *
 */

public class BPlusTree {

	private Node mRootNode;

	private int maxDegree ;
    private int pageByteSize  ;
    private int valueByteSize ;
    public FileManager fileManagerIndex, fileManagerData;
    public BplusNodeFactory bplusNodeFactory;

    private static  final String  indexFileName = "indexBplusTree.bin";
    private static  final String  DataFileName = "dataFile.bin";
    public    int  rootPageNumber = 0 ;
    private  DataPageFactory pageFactory;
    public  int diskAccess = 0;

        public BPlusTree(int pageSize, int valueSize, DataPageFactory dataPageFactory) {

                this.pageByteSize = pageSize;
                this.valueByteSize = valueSize;
                this.maxDegree = (int)Math.floor ((pageSize - 24)/ 8.0);
                this.pageFactory = dataPageFactory;

                fileManagerIndex = new FileManager(pageSize);
                mRootNode = new LeafNode(maxDegree);
                fileManagerIndex.createFile(indexFileName);
                fileManagerIndex.appendBlock(mRootNode.serialize(pageByteSize));
                diskAccess ++;

                fileManagerData = new FileManager(valueByteSize);
                fileManagerData.createFile(DataFileName);

                bplusNodeFactory = new BplusNodeFactory(maxDegree, pageByteSize);
                this.rootPageNumber = 0;
        }

        

        public Node insert (int key, Object object) {
                Node rootNode = bplusNodeFactory.getNodeInstanceFromFile(this.fileManagerIndex.readBlock(rootPageNumber));
                diskAccess++;

                if (rootNode.mNumKeys == this.maxDegree) {
                        // Create new root
                        InnerNode newRootNode = new InnerNode(maxDegree);
                        newRootNode.mChildNodes[0] = fileManagerIndex.getNextAvailPage();
                        int pageofNewSplitNode = fileManagerIndex.getNextAvailPage();
                        Node splitNode = splitChildNode(newRootNode, 0, rootNode, pageofNewSplitNode);
                        fileManagerIndex.writeBlock(bplusNodeFactory.nodeSerializer(splitNode), pageofNewSplitNode);
                        fileManagerIndex.writeBlock(bplusNodeFactory.nodeSerializer(rootNode), newRootNode.mChildNodes[0]);
                        fileManagerIndex.writeBlock(bplusNodeFactory.nodeSerializer(newRootNode), rootPageNumber);

                        // Insert the key into the B-Tree with root newRootNode.
                        insertIntoNonFullNode(newRootNode, rootPageNumber, key, object);
                        fileManagerIndex.writeBlock(bplusNodeFactory.nodeSerializer(newRootNode), rootPageNumber);
                        diskAccess +=4;
                } 
                else {
                        insertIntoNonFullNode(rootNode, rootPageNumber, key, object); // Insert the key into the B-Tree with root rootNode.
                        fileManagerIndex.writeBlock(bplusNodeFactory.nodeSerializer(rootNode), rootPageNumber);
                        diskAccess ++;

                }
                return rootNode;

        }
        
        
            
        
        //split method
        // Split the node, node, of a B-Tree into two nodes that contain T-1 (and T) elements and move node's median key up to the parentNode.
        // This method will only be called if node is full; node is the i-th child of parentNode.
        // All internal keys (elements) will have duplicates within the leaf nodes.
        Node splitChildNode(Node parentNode, int i, Node node, int pageToWriteSplitNode) {
                Node newNode =  node.mIsLeafNode == 1 ?  new LeafNode(maxDegree) : new InnerNode(maxDegree);
                newNode.mIsLeafNode = node.mIsLeafNode == 1 ? 1 : 0;
                int T = (int)Math.ceil(maxDegree/2.0);
                newNode.mNumKeys = T;


                // Copy the last T elements of node into newNode. Keep the median key as duplicate in the first key of newNode.
                for (int j = 0; j < T; j++) {
                        newNode.mKeys[j] = node.mKeys[j + T - 1];
                }

                if (newNode.mIsLeafNode == 0) {
                        InnerNode newInnerNode =(InnerNode) newNode;
                        InnerNode oldInnerNode = (InnerNode)  node ;
                        // Copy the last T + 1 pointers of node into newNode.
                        for (int j = 0; j < T + 1; j++) {
                                newInnerNode.mChildNodes[j] = oldInnerNode.mChildNodes[j + T - 1];
                        }
                        for (int j = T; j <= node.mNumKeys; j++) {
                                oldInnerNode.mChildNodes[j] = -1;
                        }
                } else {
                        // Manage the linked list that is used e.g. for doing fast range queries.

                        for (int j = 0; j < T; j++) {
                                ((LeafNode)newNode).mChildDataPages[j] = ((LeafNode)node).mChildDataPages[j + T - 1];
                        }
                        for (int j = T; j < node.mNumKeys; j++) {
                                ((LeafNode)node).mChildDataPages[j] = -1;
                        }
                        newNode.rightNode = node.rightNode;
                        node.rightNode = pageToWriteSplitNode; //get the next available page

                }
                // re-init empty positions
                for (int j = T - 1; j < node.mNumKeys; j++) {
                        node.mKeys[j] = -1;
                }

                node.mNumKeys = maxDegree - T;


                InnerNode parentInnerNode = (InnerNode) parentNode;
                // Insert a (child) pointer to node newNode into the parentNode, moving other keys and pointers as necessary.
                for (int j = parentNode.mNumKeys; j >= i + 1; j--) {
                        parentInnerNode.mChildNodes[j + 1] = parentInnerNode.mChildNodes[j];
                }
                parentInnerNode.mChildNodes[i + 1] = pageToWriteSplitNode;


                for (int j = parentNode.mNumKeys - 1; j >= i; j--) {
                        parentNode.mKeys[j + 1] = parentNode.mKeys[j];
                }
                parentNode.mKeys[i] = newNode.mKeys[0];
                parentNode.mNumKeys++;

                return newNode;
        }
        
        // Insert an element into a B-Tree. (The element will ultimately be inserted into a leaf node). 
        void insertIntoNonFullNode(Node node, int nodePageNumber, int key, Object object) {
                int i = node.mNumKeys - 1;
                if (node.mIsLeafNode == 1) {
                        // Since node is not a full node insert the new element into its proper place within node.
                        while (i >= 0 && key < node.mKeys[i]) {
                                node.mKeys[i + 1] = node.mKeys[i];
                                i--;
                        }
                        i++;
                        node.mKeys[i] = key;
                        node.mNumKeys++;

                        //add value to the
                        int pageToWriteObject = fileManagerData.getNextAvailPage();
                        fileManagerData.writeBlock(pageFactory.serialize(object),pageToWriteObject);
                        ((LeafNode)node).mChildDataPages[i] = pageToWriteObject;
                        diskAccess ++;


                } else {
                        // Move back from the last key of node until we find the child pointer to the node
                        // that is the root node of the subtree where the new element should be placed.
                        while (i >= 0 && key < node.mKeys[i]) {
                                i--;
                        }
                        i++;
                        InnerNode innerNode = (InnerNode) node;
                        byte [] childPageInBytes = fileManagerIndex.readBlock(innerNode.mChildNodes[i]);
                        Node childNode = bplusNodeFactory.getNodeInstanceFromFile(childPageInBytes);
                        diskAccess ++;

                        if (childNode.mNumKeys == maxDegree) {
                                int availPageToWriteNewSplitNode = fileManagerIndex.getNextAvailPage();
                                Node newSplitNode = splitChildNode(node, i, childNode, availPageToWriteNewSplitNode);
                                fileManagerIndex.writeBlock(bplusNodeFactory.nodeSerializer(childNode), innerNode.mChildNodes[i]);
                                if (key > node.mKeys[i]) {
                                        i++;
                                }

                                fileManagerIndex.writeBlock(bplusNodeFactory.nodeSerializer(newSplitNode), availPageToWriteNewSplitNode);
                                fileManagerIndex.writeBlock(bplusNodeFactory.nodeSerializer(node), nodePageNumber);
                                diskAccess +=3;

                        }
                        childPageInBytes = fileManagerIndex.readBlock(innerNode.mChildNodes[i]);
                        childNode = bplusNodeFactory.getNodeInstanceFromFile(childPageInBytes);
                        insertIntoNonFullNode(childNode, innerNode.mChildNodes[i] ,  key, object);
                        fileManagerIndex.writeBlock(bplusNodeFactory.nodeSerializer(childNode), innerNode.mChildNodes[i]);
                        diskAccess +=2;

                }
        }       
        
        //  search methods.
        public Object search(Node node, int key) {
        	
                int i = 0;

                while (node.mIsLeafNode == 0 && i < node.mNumKeys && key >= node.mKeys[i]) {
                        i++;
                }

                while (node.mIsLeafNode == 1 && i < node.mNumKeys && key > node.mKeys[i]) {
                        i++;
                }

                if (i < node.mNumKeys && key == node.mKeys[i] && node.mIsLeafNode == 1) {
                        diskAccess ++;
                        return pageFactory.deserialize(fileManagerData.readBlock(((LeafNode)node).mChildDataPages[i]));

                }
                if (node.mIsLeafNode == 1) {
                        return null;
                } else {
                        Node childNode = bplusNodeFactory.
                                getNodeInstanceFromFile(this.fileManagerIndex.readBlock(((InnerNode)node).mChildNodes[i]));
                        diskAccess ++;

                        return search(childNode, key);
                }       
        }
        
        public Object search(int key) {
                Node rootNode = bplusNodeFactory.getNodeInstanceFromFile(this.fileManagerIndex.readBlock(rootPageNumber));
                diskAccess ++;
                return search(rootNode, key);
        }


        //  search methods.
        public Object searchRange(Node node, int low, int high, List<Object> ans) {

                int i = 0;

                while (node.mIsLeafNode == 0 && i < node.mNumKeys && low >= node.mKeys[i]) {
                        i++;
                }

                while (node.mIsLeafNode == 1 && i < node.mNumKeys && low > node.mKeys[i]) {
                        i++;
                }

                if ( node.mIsLeafNode == 1) {

                        while  (node != null){
                                for (int j = i ; j < node.mNumKeys; j++){

                                        if (node.mKeys[j] >= low && node.mKeys[j]<= high){
                                                Object val = pageFactory.deserialize(fileManagerData.readBlock(
                                                        ((LeafNode)node).mChildDataPages[j]));
                                                diskAccess ++;
                                                ans.add(val);

                                        }
                                        else {
                                                node = null;
                                                break;

                                        }
                                }
                                if (node != null && node.rightNode != -1){
                                        node = bplusNodeFactory.getNodeInstanceFromFile(fileManagerIndex.readBlock(node.rightNode));
                                        diskAccess ++;
                                        i=0;
                                }
                                else {
                                        node = null;
                                }
                        }
                        return  ans ;

                }
                if (node.mIsLeafNode == 1) {
                        return null;
                } else {
                        Node childNode = bplusNodeFactory.
                                getNodeInstanceFromFile(this.fileManagerIndex.readBlock(((InnerNode)node).mChildNodes[i]));
                        diskAccess ++;
                        return searchRange(childNode, low, high,ans);
                }
        }

        public List<Object> searchRange (int low , int high) {
                List<Object> ans = new ArrayList<Object>();
                Node rootNode = bplusNodeFactory.getNodeInstanceFromFile(this.fileManagerIndex.readBlock(rootPageNumber));
                diskAccess++;
                searchRange(rootNode, low, high, ans);
                return  ans;
        }

       
}

