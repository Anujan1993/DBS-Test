package com.example.dbs.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ListArticle {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("last_update")
    @Expose
    private Integer lastUpdate;
    @SerializedName("short_description")
    @Expose
    private String shortDescription;
    @SerializedName("avatar")
    @Expose
    private String avatar;

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id;  }

    public String getTitle() {  return title;  }

    public void setTitle(String title) {  this.title = title;  }

    public Integer getLastUpdate() {  return lastUpdate;  }

    public void setLastUpdate(Integer lastUpdate) {  this.lastUpdate = lastUpdate;  }

    public String getShortDescription() { return shortDescription;   }

    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription;   }

    public String getAvatar() {  return avatar;  }

    public void setAvatar(String avatar) {  this.avatar = avatar; }

}