package sun.nio.ch.sctp;

import com.sun.nio.sctp.Association;

public class AssociationImpl extends Association {
   public AssociationImpl(int var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer(super.toString());
      return var1.append("[associationID:").append(this.associationID()).append(", maxIn:").append(this.maxInboundStreams()).append(", maxOut:").append(this.maxOutboundStreams()).append("]").toString();
   }
}
