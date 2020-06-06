package com.example.dbs.entitys;

import com.orm.SugarRecord;
import com.orm.dsl.Column;
import com.orm.dsl.Unique;

public class Articles extends SugarRecord {
    @Unique
    @Column(name = "ID")
    private long ID;
    @Column(name = "articleID")
    private Integer articleID;
    @Column(name = "title")
    private String title;
    @Column(name = "last_update")
    private Integer last_update;
    @Column(name = "short_description")
    private String short_description;
    @Column(name = "avatar")
    private String avatar;

    public Articles() {
    }

    public Articles(Integer articleID, String title, Integer last_update, String short_description, String avatar) {
        this.articleID = articleID;
        this.title = title;
        this.last_update = last_update;
        this.short_description = short_description;
        this.avatar = avatar;
    }

    public long getID() { return ID;  }

    public Integer getArticleID() { return articleID;  }

    public void setArticleID(Integer articleID) { this.articleID = articleID;  }

    public String getTitle() { return title;  }

    public void setTitle(String title) { this.title = title;  }

    public Integer getLast_update() { return last_update;  }

    public void setLast_update(Integer last_update) { this.last_update = last_update;  }

    public String getShort_description() { return short_description;  }

    public void setShort_description(String short_description) { this.short_description = short_description;  }

    public String getAvatar() { return avatar;  }

    public void setAvatar(String avatar) { this.avatar = avatar;  }
}