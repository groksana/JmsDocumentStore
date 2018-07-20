package com.gromoks.jmsdocumentstore.service.jms;

import com.gromoks.jmsdocumentstore.entity.Document;
import com.gromoks.jmsdocumentstore.service.DocumentService;
import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.*;

import static com.gromoks.jmsdocumentstore.util.JsonJacksonConverter.*;

@Service
public class JmsMessageListener implements MessageListener {

    private static final String ADD_DOCUMENT_REQUEST = "add.AnyDatabase";

    private final Logger log = LoggerFactory.getLogger(getClass());

    private JmsTemplate jmsTemplate;

    private DocumentService documentService;

    private String databaseName;

    private final Queue responseQueue;

    @Autowired
    public JmsMessageListener(JmsTemplate jmsTemplate,
                              DocumentService documentService,
                              @Value("${database.name}") String databaseName,
                              @Value("${jms.document.response.queue}") String responseQueueName) {
        this.jmsTemplate = jmsTemplate;
        this.documentService = documentService;
        this.databaseName = databaseName;

        responseQueue = new ActiveMQQueue(responseQueueName);
    }

    @Override
    @JmsListener(destination = "${jms.document.request.queue}", selector = "(operation in ('add.AnyDatabase','get.${database.name}'))")
    public void onMessage(Message message) {
        long startTime = System.currentTimeMillis();

        String filter;
        try {
            filter = message.getStringProperty("operation");
        } catch (JMSException e) {
            log.error("Can't get JMS message with error: {}", e);
            throw new RuntimeException("Can't get JMS message with error: ", e);
        }

        if (ADD_DOCUMENT_REQUEST.equals(filter)) {
            add(message);
        } else if (filter.equals("get." + databaseName)) {
            getById(message);
        }

        log.debug("Finish to process message. It took {} ms", System.currentTimeMillis() - startTime);
    }

    private void add(Message message) {
        Document loadedDocument;

        try {
            log.debug("Start to process message with id = {}", message.getJMSMessageID());

            String correlationId = message.getJMSCorrelationID();

            TextMessage textMessage = (TextMessage) message;
            String document = textMessage.getText();
            loadedDocument = parseValue(document, Document.class);

            documentService.add(loadedDocument);

            jmsTemplate.send(responseQueue, session -> {
                Message sendMessage = session.createMessage();
                sendMessage.setJMSCorrelationID(correlationId);
                sendMessage.setStringProperty("database", databaseName);
                return sendMessage;
            });
        } catch (JMSException e) {
            log.error("Can't get JMS message with error: {}", e);
            throw new RuntimeException("Can't get JMS message with error: ", e);
        }
    }

    private void getById(Message message) {
        String documentId;
        try {
            log.debug("Start to process message with id = {}", message.getJMSMessageID());

            String correlationId = message.getJMSCorrelationID();

            TextMessage textMessage = (TextMessage) message;
            documentId = textMessage.getText();

            Document receivedDocument = documentService.getById(documentId);

            jmsTemplate.send(responseQueue, session -> {
                Message sendMessage = session.createTextMessage(toJson(receivedDocument));
                sendMessage.setJMSCorrelationID(correlationId);
                return sendMessage;
            });
        } catch (JMSException e) {
            log.error("Can't get JMS message with error: {}", e);
            throw new RuntimeException("Can't get JMS message with error: ", e);
        }
    }
}
