package life.genny.kafka.demo;

import io.smallrye.mutiny.Uni;
import life.genny.kafka.model.Transaction;
import life.genny.kafka.model.TransactionResult;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TransactionProcessor {

    private static final Logger LOGGER = Logger.getLogger("TransactionProcessor");

    @Inject @RestClient TransactionService service;

    @Incoming("in")
    @Outgoing("out")
    public Uni<TransactionResult> sendToTransactionService(Transaction transaction) {
        LOGGER.infof("Sending %s transaction service", transaction);
        return service.postAsync(transaction);
    }

}
