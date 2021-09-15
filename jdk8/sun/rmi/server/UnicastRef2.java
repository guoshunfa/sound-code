package sun.rmi.server;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import sun.rmi.transport.LiveRef;

public class UnicastRef2 extends UnicastRef {
   private static final long serialVersionUID = 1829537514995881838L;

   public UnicastRef2() {
   }

   public UnicastRef2(LiveRef var1) {
      super(var1);
   }

   public String getRefClass(ObjectOutput var1) {
      return "UnicastRef2";
   }

   public void writeExternal(ObjectOutput var1) throws IOException {
      this.ref.write(var1, true);
   }

   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      this.ref = LiveRef.read(var1, true);
   }
}
