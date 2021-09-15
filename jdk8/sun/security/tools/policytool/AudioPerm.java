package sun.security.tools.policytool;

class AudioPerm extends Perm {
   public AudioPerm() {
      super("AudioPermission", "javax.sound.sampled.AudioPermission", new String[]{"play", "record"}, (String[])null);
   }
}
