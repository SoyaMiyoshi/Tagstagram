package com.example.demo.domein;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity 
public class Uploaded {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    public String owner;
    public String tag;
    public String caption;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getTag() {
        return tag;
    }
    public void setTag(String owner) {
        this.tag = tag;
    }

    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }
    /*
    @Override
    public String toString() {
        return "uploaded [id=" + id + ", name=" + name + ", deadline=" + deadline + ", isDone=" + isdone + "]";
    }*/
}

