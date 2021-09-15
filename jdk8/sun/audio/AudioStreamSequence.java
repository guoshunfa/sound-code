package sun.audio;

import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Enumeration;

public final class AudioStreamSequence extends SequenceInputStream {
   Enumeration e;
   InputStream in;

   public AudioStreamSequence(Enumeration var1) {
      super(var1);
   }
}
