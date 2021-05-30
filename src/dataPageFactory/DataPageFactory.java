package dataPageFactory;

public interface DataPageFactory<T> {
     public byte [] serialize (T data) ;
     public T deserialize (byte [] data);

}
