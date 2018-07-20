package com.gromoks.jmsdocumentstore.service;

import com.gromoks.jmsdocumentstore.entity.Document;

import java.util.List;

public interface DocumentService {
    void add(Document document);

    Document getById(String documentId);

    List<Document> getByKeyWords(List<String> keyWordList);
}
