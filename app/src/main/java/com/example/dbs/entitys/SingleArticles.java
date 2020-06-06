package com.example.dbs.entitys;

import com.orm.SugarRecord;
import com.orm.dsl.Column;
import com.orm.dsl.Unique;

public class SingleArticles extends SugarRecord {
    @Unique
    @Column(name = "ID")
    private long ID;
    @Column(name = "articleID")
    private Integer articleID;
    @Column(name = "test")
    private String text;

    public SingleArticles() {
    }
    public SingleArticles(Integer articleID, String text) {
        this.articleID = articleID;
        this.text = text;
    }
    public long getID() { return ID;  }
    public Integer getArticleID() { return articleID;  }
    public void setArticleID(Integer articleID) { this.articleID = articleID;}
    public String getText() { return text; }
    public void setText(String text) {this.text = text; }

}
