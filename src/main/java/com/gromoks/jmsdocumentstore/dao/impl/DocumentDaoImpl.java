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
    public void add(Document document) {
        log.info("Start to add document to database with id = {}", document.getId());

        sessionFactory.getCurrentSession().merge(document);

        log.info("Finish to add document to database");
    }

    @Override
    public Document getById(String documentId) {
        log.info("Start to get document by id = {}", documentId);

        Document document = sessionFactory.getCurrentSession().get(Document.class, documentId);

        log.info("Finish to get document from database");
        return document;
    }

    @Override
    public List<Document> getByKeyWords(List<String> keyWordList) {
        log.info("Start to get documents by key words: {}", keyWordList);

        StringBuilder queryText = new StringBuilder("select id, content from Document where ");

        for (int i = 0; i < keyWordList.size(); i++) {
            if (i == 0) {
                queryText.append("content like ? ");
            } else {
                queryText.append(" and content like ?");
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
            document.setId(row[0].toString());
            document.setContent(new ArrayList<>(Arrays.asList(row[1].toString().split(","))));
            documentList.add(document);
        }

        log.info("Finish to get documents from database. Count = {}", documentList.size());
        return documentList;
    }
}
