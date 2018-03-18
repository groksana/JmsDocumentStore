package com.gromoks.jmsdocumentstore.dao.impl;

import com.gromoks.jmsdocumentstore.dao.DocumentDao;
import com.gromoks.jmsdocumentstore.entity.Document;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class DocumentDaoImpl implements DocumentDao {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private SessionFactory sessionFactory;

    @Autowired
    public DocumentDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional
    public void add(Document document) {
        log.debug("Start to add document to database with id = {}", document.getDocumentId());

        sessionFactory.getCurrentSession().merge(document);

        log.debug("Finish to add document to database");
    }

    @Override
    @Transactional
    public Document getById(String documentId) {
        log.debug("Start to get document by id = {}", documentId);

        Document document = sessionFactory.getCurrentSession().get(Document.class, documentId);

        log.debug("Finish to get document from database");
        return document;
    }
}
