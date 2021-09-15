package sun.java2d.pipe;

public interface SpanIterator {
   void getPathBox(int[] var1);

   void intersectClipBox(int var1, int var2, int var3, int var4);

   boolean nextSpan(int[] var1);

   void skipDownTo(int var1);

   long getNativeIterator();
}
