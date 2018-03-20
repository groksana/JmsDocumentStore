package com.gromoks.jmsdocumentstore.service.jms;

import com.gromoks.jmsdocumentstore.entity.Document;
import com.gromoks.jmsdocumentstore.service.DocumentService;
import com.gromoks.jmsdocumentstore.service.JmsMessageService;
import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.*;

import java.util.List;

import static com.gromoks.jmsdocumentstore.util.JsonJacksonConverter.parseValue;

@Service
public class JmsTopicSubscriber implements MessageListener {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Value("${document.subscriber.queue}")
    private String subscriberQueueName;

    private JmsTemplate jmsTopicTemplate;

    private DocumentService documentService;

    private JmsMessageService jmsMessageService;

    @Autowired
    public JmsTopicSubscriber(@Qualifier("jmsTopicTemplate") JmsTemplate jmsTopicTemplate, DocumentService documentService, JmsMessageService jmsMessageService) {
        this.jmsTopicTemplate = jmsTopicTemplate;
        this.documentService = documentService;
        this.jmsMessageService = jmsMessageService;
    }

    @Override
    @JmsListener(destination = "${document.topic}", containerFactory = "topicJmsListenerContainerFactory")
    public void onMessage(Message message) {
        List<String> keyWordList = null;
        String requestId = null;

        try {
            log.debug("Start to process message with id = {}", message.getJMSMessageID());

            requestId = message.getStringProperty("requestId");

            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String list = textMessage.getText();
                keyWordList = parseValue(list, List.class);
            }
        } catch (JMSException e) {
            log.error("Can't get JMS message with error: {}", e);
            throw new RuntimeException(e);
        }

        List<Document> documentList = documentService.getByKeyWords(keyWordList);

        Queue requestQueue = new ActiveMQQueue(subscriberQueueName);
        jmsMessageService.send(requestQueue, documentList, requestId);

        log.debug("Finish to process message");
    }
}
