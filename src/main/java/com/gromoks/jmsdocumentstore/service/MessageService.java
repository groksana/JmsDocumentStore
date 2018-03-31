package com.gromoks.jmsdocumentstore.service;

import com.gromoks.jmsdocumentstore.entity.Document;

import javax.jms.Destination;
import java.util.List;

public interface MessageService {
    void send(Destination destination, List<Document> documentList, String requestId);

    void sendAcknowledgment(Destination destination, String requestId);
}
