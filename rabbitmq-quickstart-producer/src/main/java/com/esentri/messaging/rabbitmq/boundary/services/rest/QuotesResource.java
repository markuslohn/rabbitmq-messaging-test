package com.esentri.messaging.rabbitmq.boundary.services.rest;

import com.esentri.messaging.rabbitmq.entity.Quote;
import io.smallrye.mutiny.Multi;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.UUID;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@Path("/quotes")
public class QuotesResource {

  @Channel("quote-requests")
  Emitter<String> quoteRequestEmitter;

  @Channel("quotes")
  Multi<Quote> quotes;

  /**
   * Endpoint to generate a new quote request id and send it to "quote-requests" channel (which maps
   * to the "quote-requests" RabbitMQ exchange) using the emitter.
   */
  @POST
  @Path("/request")
  @Produces(MediaType.TEXT_PLAIN)
  public String createRequest() {
    UUID uuid = UUID.randomUUID();
    quoteRequestEmitter.send(uuid.toString());
    return uuid.toString();
  }

  /** Endpoint retrieving the "quotes" queue and sending the items to a server sent event. */
  @GET
  @Produces(MediaType.SERVER_SENT_EVENTS)
  public Multi<Quote> stream() {
    return quotes;
  }
}
