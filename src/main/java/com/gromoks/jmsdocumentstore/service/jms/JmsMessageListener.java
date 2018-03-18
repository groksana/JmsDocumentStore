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
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import javax.jms.*;

import static com.gromoks.jmsdocumentstore.util.JsonJacksonConverter.*;

@Service
public class JmsMessageListener implements MessageListener {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private JmsTemplate jmsTemplate;

    private DocumentService documentService;

    @Value("${database.name}")
    private String databaseName;

    @Value("${document.queue.response}")
    private String responseQueueName;

    private ThreadLocal<String> correlationId;

    @Autowired
    public JmsMessageListener(JmsTemplate jmsTemplate, DocumentService documentService) {
        this.jmsTemplate = jmsTemplate;
        this.documentService = documentService;
    }

    @Override
    @JmsListener(destination = "${document.queue}", selector = "add = 'anyDatabase'")
    public void onMessage(Message message) {
        long startTime = System.currentTimeMillis();
        Document loadedDocument = null;

        try {
            log.debug("Start to process message with id = {}", message.getJMSMessageID());

            correlationId = new ThreadLocal<>();
            correlationId.set(message.getJMSCorrelationID());

            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String document = textMessage.getText();
                loadedDocument = parseValue(document, Document.class);
            }
        } catch (JMSException e) {
            log.error("Can't get JMS message with error: {}", e);
            throw new RuntimeException(e);
        }

        documentService.add(loadedDocument);

        Queue responseQueue = new ActiveMQQueue(responseQueueName);
        Document savedDocument = new Document();
        savedDocument.setDocumentId(loadedDocument.getDocumentId());
        jmsTemplate.send(responseQueue, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                Message message = session.createTextMessage(toJson(savedDocument));
                message.setJMSCorrelationID(correlationId.get());
                message.setStringProperty("database", databaseName);
                message.setJMSReplyTo(responseQueue);
                return message;
            }
        });
        log.debug("Finish to process message. It took {} ms", System.currentTimeMillis() - startTime);
    }
}
