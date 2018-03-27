package com.gromoks.jmsdocumentstore.service.impl;

import com.gromoks.jmsdocumentstore.dao.DocumentDao;
import com.gromoks.jmsdocumentstore.entity.Document;
import com.gromoks.jmsdocumentstore.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DocumentServiceImpl implements DocumentService {

    private DocumentDao documentDao;

    @Autowired
    public DocumentServiceImpl(DocumentDao documentDao) {
        this.documentDao = documentDao;
    }

    @Override
    @Transactional
    public void add(Document document) {
        documentDao.add(document);
    }

    @Override
    @Transactional(readOnly = true)
    public Document getById(String documentId) {
        return documentDao.getById(documentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Document> getByKeyWords(List<String> keyWordList) {
        return documentDao.getByKeyWords(keyWordList);
    }
}
