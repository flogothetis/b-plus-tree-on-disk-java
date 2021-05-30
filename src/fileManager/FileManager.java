package fileManager;



import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Logger;

public class FileManager implements DAOManager {

    private  long curr_pos =0 ;
    private String fileName = null;
    RandomAccessFile file = null ;
    private Logger log = Logger.getLogger(this.getClass().getName());
    private int pageSize;
    private Integer  availPage = null;

    public FileManager(int pageSize) {
        this.pageSize = pageSize;
        this.file = null;
    }

    public int  getNextAvailPage(){
        if (availPage == null){
            availPage = (int)getTotalPages();
            return availPage;
        }
        return ++availPage ;

    }


    @Override
    public int fileHandle() {
        log.info("Total number of pages  are : " + getTotalPages());
        log.info("Page Size : "+ this.pageSize);
        return 0;
    }

    @Override

    public int createFile(String fileName) {
        try {
            file = new RandomAccessFile(fileName, "rw");
            file.setLength(0);
            this.curr_pos = 0 ;
            return 1;
        } catch (FileNotFoundException e) {
            log.warning("Error opening file");
        } catch (IOException e) {
           log.warning("There is no file ");
        }

        return 0 ;
    }

    @Override
    public int openFile(String fileName) {
        try {
            file = new RandomAccessFile(fileName, "rw");
            this.curr_pos = 0 ;
            file.seek(0);
            return 1;
        } catch (FileNotFoundException e) {
            log.warning("Error opening file");

        } catch (IOException e) {
            log.warning("There is no file ");

        }
        return  0;
    }

    @Override
    public byte[] readBlock(int pos) {
        if (file == null ){
            return  null ;
        }
        byte [] buffer = new byte[this.pageSize];

        try {
            file.seek(pos * this.pageSize);
            file.read(buffer, 0, this.pageSize);
            return  buffer;
        } catch (IOException e) {
            log.warning("There is no file ");
            return null;
        }

    }

    public void deleteWholeFile(){
        try {
            this.file.setLength(0);
            this.curr_pos = 0 ;
            this.file.seek(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void  reset(){
        try {
            file.seek(0);
            this.curr_pos = 0;

        } catch (IOException e) {
            log.warning("There is no file ");
        }
    }

    @Override
    public byte[] readNextBlock() {
        if (file == null ){
            return  null ;
        }
        if (this.curr_pos >= getTotalPages() * this.pageSize){
            return null;
        }
        byte [] buffer = new byte[this.pageSize];

        try {
            file.seek(this.curr_pos);
            file.read(buffer, 0, this.pageSize);
            this.curr_pos += this.pageSize;
            return  buffer;
        } catch (IOException e) {
            log.warning("There is no file ");
            return null;
        }
    }


    @Override
    public int writeBlock(byte[] block, int page ) {
        if (file == null ){
            return  0 ;
        }
        try {
            file.seek(page * this.pageSize);
            file.write(block);
            return  1;
        } catch (IOException e) {
            log.warning("There is no file");
            return 0 ;
        }

    }

    @Override
    public int writeNextBlock(byte[] block) {
        if (file == null ){
            return  0 ;
        }
        try {
            file.write(block);
            this.curr_pos += this.pageSize;
            return  1;
        } catch (IOException e) {
            log.warning("There is no file");
            return 0 ;
        }
    }

    @Override
    public int appendBlock(byte[] block) {
        if (file == null || block == null){
            return  0 ;
        }

        try {
            file.seek(getTotalPages() * this.pageSize);
            file.write(block);
            return  1;

        } catch (IOException e) {
            log.warning("There is no file ");
            return 0 ;
        }
    }

    @Override
    public long getTotalPages() {
        try {
            return  file.length() / this.pageSize ;
        } catch (IOException e) {
            return 0;
        }
    }


    @Override
    public int closeFile() {
        try {
            file.close();
            log.info("File closed successfully");
            return  1;
        } catch (IOException e) {
           log.warning("Error closing file");
            return  0;
        }

    }
}
