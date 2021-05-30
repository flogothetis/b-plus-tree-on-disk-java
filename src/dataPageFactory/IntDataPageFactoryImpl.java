package dataPageFactory;

import java.nio.ByteBuffer;

public class IntDataPageFactoryImpl implements  DataPageFactory<Integer>{
    private  int pageSize ;

    public IntDataPageFactoryImpl(int pageSize) {
        this.pageSize = pageSize;
    }


    @Override
    public byte[] serialize(Integer data) {
        ByteBuffer buffer = ByteBuffer.allocate(pageSize);
        buffer.putInt(data);
        return buffer.array();

    }

    @Override
    public Integer deserialize(byte[] data) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        return byteBuffer.getInt();
    }
}
