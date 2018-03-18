package com.gromoks.jmsdocumentstore.service;

import com.gromoks.jmsdocumentstore.entity.Document;

public interface DocumentService {
    void add(Document document);

    Document getById(String documentId);
}
