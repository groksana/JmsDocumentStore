package com.gromoks.jmsdocumentstore.service.jms;

import com.gromoks.jmsdocumentstore.entity.Document;
import com.gromoks.jmsdocumentstore.service.DocumentService;
import com.gromoks.jmsdocumentstore.service.MessageService;
import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import javax.jms.*;

import java.util.List;

import static com.gromoks.jmsdocumentstore.util.JsonJacksonConverter.parseValue;

@Service
public class JmsTopicSubscriber implements MessageListener {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Queue requestSearchQueue;

    private DocumentService documentService;

    private MessageService messageService;

    @Autowired
    public JmsTopicSubscriber(DocumentService documentService,
                              MessageService messageService,
                              @Value("${jms.document.search.response.queue}") String requestSearchQueueName) {
        this.documentService = documentService;
        this.messageService = messageService;
        this.requestSearchQueue = new ActiveMQQueue(requestSearchQueueName);
    }

    @Override
    @JmsListener(destination = "${jms.document.search.request.topic}", containerFactory = "topicJmsListenerContainerFactory")
    public void onMessage(Message message) {
        List<String> keyWordList;
        String requestId;

        try {
            log.debug("Start to process message with id = {}", message.getJMSMessageID());

            requestId = message.getStringProperty("requestId");

            TextMessage textMessage = (TextMessage) message;
            String list = textMessage.getText();
            keyWordList = parseValue(list, List.class);
        } catch (JMSException e) {
            log.error("Can't get JMS message with error: {}", e);
            throw new RuntimeException(e);
        }

        List<Document> documentList = documentService.getByKeyWords(keyWordList);

        messageService.send(requestSearchQueue, documentList, requestId);

        log.debug("Finish to process message");
    }
}
