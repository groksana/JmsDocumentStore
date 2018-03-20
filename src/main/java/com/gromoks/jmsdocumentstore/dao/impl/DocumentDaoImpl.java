package com.gromoks.jmsdocumentstore.dao.impl;

import com.gromoks.jmsdocumentstore.dao.DocumentDao;
import com.gromoks.jmsdocumentstore.entity.Document;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @Override
    @Transactional
    public List<Document> getByKeyWords(List<String> keyWordList) {
        log.debug("Start to get documents by key words: {}", keyWordList);

        StringBuilder queryText = new StringBuilder("select id, context from Document where ");

        for (int i = 0; i < keyWordList.size(); i++) {
            if (i == 0) {
                queryText.append("context like ? ");
            } else {
                queryText.append(" and context like ?");
            }
        }

        Query query = sessionFactory.getCurrentSession().createSQLQuery(queryText.toString());

        int i = 0;
        for (String element : keyWordList) {
            query.setString(i, "%" + element + "%");
            i++;
        }

        List<Object[]> documentData = query.list();
        List<Document> documentList = new ArrayList<>();
        for (Object[] row : documentData) {
            Document document = new Document();
            document.setDocumentId(row[0].toString());
            document.setContext(new ArrayList<>(Arrays.asList(row[1].toString().split(","))));
            documentList.add(document);
        }

        log.debug("Finish to get documents from database. Count = {}", documentList.size());
        return documentList;
    }
}
