package javax.swing.event;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import javax.swing.SwingUtilities;

public final class SwingPropertyChangeSupport extends PropertyChangeSupport {
   static final long serialVersionUID = 7162625831330845068L;
   private final boolean notifyOnEDT;

   public SwingPropertyChangeSupport(Object var1) {
      this(var1, false);
   }

   public SwingPropertyChangeSupport(Object var1, boolean var2) {
      super(var1);
      this.notifyOnEDT = var2;
   }

   public void firePropertyChange(final PropertyChangeEvent var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         if (this.isNotifyOnEDT() && !SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                  SwingPropertyChangeSupport.this.firePropertyChange(var1);
               }
            });
         } else {
            super.firePropertyChange(var1);
         }

      }
   }

   public final boolean isNotifyOnEDT() {
      return this.notifyOnEDT;
   }
}
