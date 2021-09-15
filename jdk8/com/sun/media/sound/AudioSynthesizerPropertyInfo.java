package com.sun.media.sound;

public final class AudioSynthesizerPropertyInfo {
   public String name;
   public String description = null;
   public Object value = null;
   public Class valueClass = null;
   public Object[] choices = null;

   public AudioSynthesizerPropertyInfo(String var1, Object var2) {
      this.name = var1;
      if (var2 instanceof Class) {
         this.valueClass = (Class)var2;
      } else {
         this.value = var2;
         if (var2 != null) {
            this.valueClass = var2.getClass();
         }
      }

   }
}
