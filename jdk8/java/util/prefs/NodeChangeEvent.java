package java.util.prefs;

import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EventObject;

public class NodeChangeEvent extends EventObject {
   private Preferences child;
   private static final long serialVersionUID = 8068949086596572957L;

   public NodeChangeEvent(Preferences var1, Preferences var2) {
      super(var1);
      this.child = var2;
   }

   public Preferences getParent() {
      return (Preferences)this.getSource();
   }

   public Preferences getChild() {
      return this.child;
   }

   private void writeObject(ObjectOutputStream var1) throws NotSerializableException {
      throw new NotSerializableException("Not serializable.");
   }

   private void readObject(ObjectInputStream var1) throws NotSerializableException {
      throw new NotSerializableException("Not serializable.");
   }
}
