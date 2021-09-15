package com.sun.xml.internal.ws.client;

import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.api.server.SDDocument;
import com.sun.xml.internal.ws.util.MetadataUtil;
import com.sun.xml.internal.ws.util.pipe.AbstractSchemaValidationTube;
import java.util.Map;
import java.util.logging.Logger;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import javax.xml.ws.WebServiceException;
import org.xml.sax.SAXException;

public class ClientSchemaValidationTube extends AbstractSchemaValidationTube {
   private static final Logger LOGGER = Logger.getLogger(ClientSchemaValidationTube.class.getName());
   private final Schema schema;
   private final Validator validator;
   private final boolean noValidation;
   private final WSDLPort port;

   public ClientSchemaValidationTube(WSBinding binding, WSDLPort port, Tube next) {
      super(binding, next);
      this.port = port;
      if (port != null) {
         String primaryWsdl = port.getOwner().getParent().getLocation().getSystemId();
         AbstractSchemaValidationTube.MetadataResolverImpl mdresolver = new AbstractSchemaValidationTube.MetadataResolverImpl();
         Map<String, SDDocument> docs = MetadataUtil.getMetadataClosure(primaryWsdl, mdresolver, true);
         mdresolver = new AbstractSchemaValidationTube.MetadataResolverImpl(docs.values());
         Source[] sources = this.getSchemaSources(docs.values(), mdresolver);
         Source[] var8 = sources;
         int var9 = sources.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            Source source = var8[var10];
            LOGGER.fine("Constructing client validation schema from = " + source.getSystemId());
         }

         if (sources.length != 0) {
            this.noValidation = false;
            this.sf.setResourceResolver(mdresolver);

            try {
               this.schema = this.sf.newSchema(sources);
            } catch (SAXException var12) {
               throw new WebServiceException(var12);
            }

            this.validator = this.schema.newValidator();
            return;
         }
      }

      this.noValidation = true;
      this.schema = null;
      this.validator = null;
   }

   protected Validator getValidator() {
      return this.validator;
   }

   protected boolean isNoValidation() {
      return this.noValidation;
   }

   protected ClientSchemaValidationTube(ClientSchemaValidationTube that, TubeCloner cloner) {
      super((AbstractSchemaValidationTube)that, (TubeCloner)cloner);
      this.port = that.port;
      this.schema = that.schema;
      this.validator = this.schema.newValidator();
      this.noValidation = that.noValidation;
   }

   public AbstractTubeImpl copy(TubeCloner cloner) {
      return new ClientSchemaValidationTube(this, cloner);
   }

   public NextAction processRequest(Packet request) {
      if (!this.isNoValidation() && this.feature.isOutbound() && request.getMessage().hasPayload() && !request.getMessage().isFault()) {
         try {
            this.doProcess(request);
         } catch (SAXException var3) {
            throw new WebServiceException(var3);
         }

         return super.processRequest(request);
      } else {
         return super.processRequest(request);
      }
   }

   public NextAction processResponse(Packet response) {
      if (!this.isNoValidation() && this.feature.isInbound() && response.getMessage() != null && response.getMessage().hasPayload() && !response.getMessage().isFault()) {
         try {
            this.doProcess(response);
         } catch (SAXException var3) {
            throw new WebServiceException(var3);
         }

         return super.processResponse(response);
      } else {
         return super.processResponse(response);
      }
   }
}
