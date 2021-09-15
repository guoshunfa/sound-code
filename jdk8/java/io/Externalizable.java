package java.io;

public interface Externalizable extends Serializable {
   void writeExternal(ObjectOutput var1) throws IOException;

   void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException;
}
