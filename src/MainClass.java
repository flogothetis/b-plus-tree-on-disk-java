import bplustree.BPlusTree;
import dataPageFactory.IntDataPageFactoryImpl;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class MainClass {

	public static void main(String[] args) {
		BPlusTree bPlusTree = new BPlusTree(256, 32, new IntDataPageFactoryImpl(256));
		Random random = new Random();

		/** Insert 10^5 key-value pairs **/
		for (int i = 0; i< (int)Math.pow(10, 5); i++){
			bPlusTree.insert(i, i);
		}

		/*** Insert 20 more keys **/
		bPlusTree.diskAccess =0;
		for (int i = (int)Math.pow(10, 5); i< (int)Math.pow(10, 5) +20; i++){
			bPlusTree.insert(i, i);
		}
		System.out.println("Average I/Os per insertion: " + bPlusTree.diskAccess /20.0);

		/****** 20 point queries **/
		bPlusTree.diskAccess = 0;
		for (int i = 0; i < 20 ; i++){
			bPlusTree.search(random.nextInt((int)Math.pow(10, 5)));
		}
		System.out.println("Average I/Os per point query: " + bPlusTree.diskAccess /20.0);

		/***** 20 range queries [high - low = 10] */
		bPlusTree.diskAccess = 0;
		for (int i = 0; i < 20; i++){
			int low = random.nextInt(random.nextInt((int)Math.pow(10, 5)));
			bPlusTree.searchRange(low, low + 10);
		}
		System.out.println("Average I/Os per range query ( [high - low] = 10): " + bPlusTree.diskAccess /20.0);


		/***** 20 range queries [high - low = 1000] */
		bPlusTree.diskAccess = 0;
		for (int i = 0; i < 20; i++){
			int low = random.nextInt(random.nextInt((int)Math.pow(10, 5)));
			bPlusTree.searchRange(low, low + 1000);
		}
		System.out.println("Average I/Os per range query ( [high - low] = 1000): " + bPlusTree.diskAccess /20.0);


	}
}


