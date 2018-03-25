package com.gromoks.jmsdocumentstore.entity;

import com.gromoks.jmsdocumentstore.util.StringListConverter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "DOCUMENT")
public class Document {

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "CONTENT")
    @Convert(converter = StringListConverter.class)
    private List<String> content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Document{" +
                "id=" + id +
                ", content=" + content +
                '}';
    }
}


