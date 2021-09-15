package sun.lwawt;

import java.awt.Label;
import java.awt.peer.LabelPeer;
import javax.swing.JLabel;

final class LWLabelPeer extends LWComponentPeer<Label, JLabel> implements LabelPeer {
   LWLabelPeer(Label var1, PlatformComponent var2) {
      super(var1, var2);
   }

   JLabel createDelegate() {
      return new JLabel();
   }

   void initializeImpl() {
      super.initializeImpl();
      this.setText(((Label)this.getTarget()).getText());
      this.setAlignment(((Label)this.getTarget()).getAlignment());
   }

   public void setText(String var1) {
      synchronized(this.getDelegateLock()) {
         ((JLabel)this.getDelegate()).setText(var1);
      }
   }

   public void setAlignment(int var1) {
      synchronized(this.getDelegateLock()) {
         ((JLabel)this.getDelegate()).setHorizontalAlignment(convertAlignment(var1));
      }
   }

   private static int convertAlignment(int var0) {
      switch(var0) {
      case 1:
         return 0;
      case 2:
         return 4;
      default:
         return 2;
      }
   }
}
