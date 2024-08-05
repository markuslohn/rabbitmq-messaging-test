package com.esentri.messaging.rabbitmq.boundary.services.jms;

import com.esentri.messaging.rabbitmq.entity.Quote;
import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Random;
import lombok.extern.java.Log;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

/**
 * A bean consuming data from the "quote-requests" RabbitMQ queue and giving out a random quote. The
 * result is pushed to the "quotes" RabbitMQ exchange.
 */
@Log
@ApplicationScoped
public class QuoteProcessor {

  private Random random = new Random();

  @Incoming("requests")
  @Outgoing("quotes")
  @Blocking
  public Quote process(String quoteRequest) throws InterruptedException {
    log.info("Received quote-request " + quoteRequest);
    // simulate some hard-working task
    Thread.sleep(1000);
    return new Quote(quoteRequest, random.nextInt(100));
  }
}
