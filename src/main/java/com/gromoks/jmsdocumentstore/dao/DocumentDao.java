package com.gromoks.jmsdocumentstore.dao;

import com.gromoks.jmsdocumentstore.entity.Document;

import java.util.List;

public interface DocumentDao {
    void add(Document document);

    Document getById(String documentId);

    List<Document> getByKeyWords(List<String> keyWordList);
}
