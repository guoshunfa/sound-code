package com.sun.media.sound;

import java.util.ArrayList;
import java.util.List;
import javax.sound.midi.Soundbank;
import javax.sound.midi.SoundbankResource;

public final class SF2Layer extends SoundbankResource {
   String name = "";
   SF2GlobalRegion globalregion = null;
   List<SF2LayerRegion> regions = new ArrayList();

   public SF2Layer(SF2Soundbank var1) {
      super(var1, (String)null, (Class)null);
   }

   public SF2Layer() {
      super((Soundbank)null, (String)null, (Class)null);
   }

   public Object getData() {
      return null;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public List<SF2LayerRegion> getRegions() {
      return this.regions;
   }

   public SF2GlobalRegion getGlobalRegion() {
      return this.globalregion;
   }

   public void setGlobalZone(SF2GlobalRegion var1) {
      this.globalregion = var1;
   }

   public String toString() {
      return "Layer: " + this.name;
   }
}
