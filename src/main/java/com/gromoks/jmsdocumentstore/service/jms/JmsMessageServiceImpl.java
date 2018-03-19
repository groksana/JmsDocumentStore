package com.gromoks.jmsdocumentstore.service.jms;

import com.gromoks.jmsdocumentstore.entity.Document;
import com.gromoks.jmsdocumentstore.service.JmsMessageService;
import com.gromoks.jmsdocumentstore.util.JsonJacksonConverter;
import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import javax.jms.*;
import java.util.List;

import static com.gromoks.jmsdocumentstore.util.JsonJacksonConverter.toJson;

@Service
public class JmsMessageServiceImpl implements JmsMessageService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("jmsTemplate")
    private JmsTemplate jmsTemplate;

    @Override
    public void send(Destination destination, List<Document> documentList, String requestId) {
        log.debug("Start to process document list for requestId = {}", requestId);
        long startTime = System.currentTimeMillis();

        jmsTemplate.send(destination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                Message message = session.createTextMessage(toJson(documentList));
                message.setStringProperty("requestId", requestId);
                return message;
            }
        });

        log.debug("Finish to send list of documents. It took {} ms", System.currentTimeMillis() - startTime);
    }
}
