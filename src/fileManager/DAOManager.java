package fileManager;

public interface DAOManager {
    int fileHandle();
    int createFile(String fileName);
    int openFile(String fileName);
    byte[]  readBlock(int pos);
    byte[]  readNextBlock();
    int writeBlock(byte [] block, int pos );
    int writeNextBlock (byte [] block);
    int appendBlock (byte [] block);
    long getTotalPages () ;
    int closeFile ();
}
