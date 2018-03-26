package com.gromoks.jmsdocumentstore.service.jms;

import com.gromoks.jmsdocumentstore.entity.Document;
import com.gromoks.jmsdocumentstore.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.*;
import java.util.List;

import static com.gromoks.jmsdocumentstore.util.JsonJacksonConverter.toJson;

@Service
public class JmsMessageService implements MessageService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private JmsTemplate jmsTemplate;

    @Autowired
    public JmsMessageService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public void send(Destination destination, List<Document> documentList, String requestId) {
        log.debug("Start to process document list for requestId = {}", requestId);
        long startTime = System.currentTimeMillis();

        jmsTemplate.send(destination, session -> {
            Message message = session.createTextMessage(toJson(documentList));
            message.setStringProperty("requestId", requestId);
            return message;
        });

        log.debug("Finish to send list of documents. It took {} ms", System.currentTimeMillis() - startTime);
    }
}
