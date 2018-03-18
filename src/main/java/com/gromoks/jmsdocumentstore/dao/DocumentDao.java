package com.gromoks.jmsdocumentstore.dao;

import com.gromoks.jmsdocumentstore.entity.Document;

public interface DocumentDao {
    void add(Document document);

    Document getById(String documentId);
}
