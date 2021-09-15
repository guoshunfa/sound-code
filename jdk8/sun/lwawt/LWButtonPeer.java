package sun.lwawt;

import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.peer.ButtonPeer;
import javax.swing.JButton;

final class LWButtonPeer extends LWComponentPeer<Button, JButton> implements ButtonPeer, ActionListener {
   LWButtonPeer(Button var1, PlatformComponent var2) {
      super(var1, var2);
   }

   JButton createDelegate() {
      return new LWButtonPeer.JButtonDelegate();
   }

   void initializeImpl() {
      super.initializeImpl();
      this.setLabel(((Button)this.getTarget()).getLabel());
      synchronized(this.getDelegateLock()) {
         ((JButton)this.getDelegate()).addActionListener(this);
      }
   }

   public void actionPerformed(ActionEvent var1) {
      this.postEvent(new ActionEvent(this.getTarget(), 1001, ((Button)this.getTarget()).getActionCommand(), var1.getWhen(), var1.getModifiers()));
   }

   public void setLabel(String var1) {
      synchronized(this.getDelegateLock()) {
         ((JButton)this.getDelegate()).setText(var1);
      }
   }

   public boolean isFocusable() {
      return true;
   }

   private final class JButtonDelegate extends JButton {
      JButtonDelegate() {
      }

      public boolean hasFocus() {
         return ((Button)LWButtonPeer.this.getTarget()).hasFocus();
      }
   }
}
