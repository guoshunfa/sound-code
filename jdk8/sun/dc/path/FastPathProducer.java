package sun.dc.path;

public interface FastPathProducer {
   void getBox(float[] var1) throws PathError;

   void sendTo(PathConsumer var1) throws PathError, PathException;
}
