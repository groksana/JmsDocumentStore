package com.gromoks.jmsdocumentstore.entity;

import com.gromoks.jmsdocumentstore.util.StringListConverter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "DOCUMENT")
public class Document {

    @Id
    @Column(name = "ID")
    private String documentId;

    @Column(name = "CONTEXT")
    @Convert(converter = StringListConverter.class)
    private List<String> context;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public List<String> getContext() {
        return context;
    }

    public void setContext(List<String> context) {
        this.context = context;
    }

    @Override
    public String toString() {
        return "Document{" +
                "documentId=" + documentId +
                ", context=" + context +
                '}';
    }
}


