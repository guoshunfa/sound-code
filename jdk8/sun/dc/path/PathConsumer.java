package sun.dc.path;

public interface PathConsumer {
   void beginPath() throws PathError;

   void beginSubpath(float var1, float var2) throws PathError;

   void appendLine(float var1, float var2) throws PathError;

   void appendQuadratic(float var1, float var2, float var3, float var4) throws PathError;

   void appendCubic(float var1, float var2, float var3, float var4, float var5, float var6) throws PathError;

   void closedSubpath() throws PathError;

   void endPath() throws PathError, PathException;

   void useProxy(FastPathProducer var1) throws PathError, PathException;

   long getCPathConsumer();

   void dispose();

   PathConsumer getConsumer();
}
