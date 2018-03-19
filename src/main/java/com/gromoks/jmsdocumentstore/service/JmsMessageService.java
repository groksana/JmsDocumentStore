package com.gromoks.jmsdocumentstore.service;

import com.gromoks.jmsdocumentstore.entity.Document;

import javax.jms.Destination;
import java.util.List;

public interface JmsMessageService {
    void send(Destination destination, List<Document> documentList, String requestId);
}
