package com.sun.xml.internal.ws.server;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import com.sun.xml.internal.ws.util.pipe.AbstractSchemaValidationTube;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import javax.xml.ws.WebServiceException;
import org.xml.sax.SAXException;

public class ServerSchemaValidationTube extends AbstractSchemaValidationTube {
   private static final Logger LOGGER = Logger.getLogger(ServerSchemaValidationTube.class.getName());
   private final Schema schema;
   private final Validator validator;
   private final boolean noValidation;
   private final SEIModel seiModel;
   private final WSDLPort wsdlPort;

   public ServerSchemaValidationTube(WSEndpoint endpoint, WSBinding binding, SEIModel seiModel, WSDLPort wsdlPort, Tube next) {
      super(binding, next);
      this.seiModel = seiModel;
      this.wsdlPort = wsdlPort;
      if (endpoint.getServiceDefinition() != null) {
         AbstractSchemaValidationTube.MetadataResolverImpl mdresolver = new AbstractSchemaValidationTube.MetadataResolverImpl(endpoint.getServiceDefinition());
         Source[] sources = this.getSchemaSources(endpoint.getServiceDefinition(), mdresolver);
         Source[] var8 = sources;
         int var9 = sources.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            Source source = var8[var10];
            LOGGER.fine("Constructing service validation schema from = " + source.getSystemId());
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

   public NextAction processRequest(Packet request) {
      if (!this.isNoValidation() && this.feature.isInbound() && request.getMessage().hasPayload() && !request.getMessage().isFault()) {
         try {
            this.doProcess(request);
         } catch (SAXException var5) {
            LOGGER.log(Level.WARNING, (String)"Client Request doesn't pass Service's Schema Validation", (Throwable)var5);
            SOAPVersion soapVersion = this.binding.getSOAPVersion();
            Message faultMsg = SOAPFaultBuilder.createSOAPFaultMessage(soapVersion, (CheckedExceptionImpl)null, (Throwable)var5, (QName)soapVersion.faultCodeClient);
            return this.doReturnWith(request.createServerResponse(faultMsg, this.wsdlPort, this.seiModel, this.binding));
         }

         return super.processRequest(request);
      } else {
         return super.processRequest(request);
      }
   }

   public NextAction processResponse(Packet response) {
      if (!this.isNoValidation() && this.feature.isOutbound() && response.getMessage() != null && response.getMessage().hasPayload() && !response.getMessage().isFault()) {
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

   protected ServerSchemaValidationTube(ServerSchemaValidationTube that, TubeCloner cloner) {
      super((AbstractSchemaValidationTube)that, (TubeCloner)cloner);
      this.schema = that.schema;
      this.validator = this.schema.newValidator();
      this.noValidation = that.noValidation;
      this.seiModel = that.seiModel;
      this.wsdlPort = that.wsdlPort;
   }

   public AbstractTubeImpl copy(TubeCloner cloner) {
      return new ServerSchemaValidationTube(this, cloner);
   }
}
