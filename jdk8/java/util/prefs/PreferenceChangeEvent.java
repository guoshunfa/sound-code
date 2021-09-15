package java.util.prefs;

import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EventObject;

public class PreferenceChangeEvent extends EventObject {
   private String key;
   private String newValue;
   private static final long serialVersionUID = 793724513368024975L;

   public PreferenceChangeEvent(Preferences var1, String var2, String var3) {
      super(var1);
      this.key = var2;
      this.newValue = var3;
   }

   public Preferences getNode() {
      return (Preferences)this.getSource();
   }

   public String getKey() {
      return this.key;
   }

   public String getNewValue() {
      return this.newValue;
   }

   private void writeObject(ObjectOutputStream var1) throws NotSerializableException {
      throw new NotSerializableException("Not serializable.");
   }

   private void readObject(ObjectInputStream var1) throws NotSerializableException {
      throw new NotSerializableException("Not serializable.");
   }
}
