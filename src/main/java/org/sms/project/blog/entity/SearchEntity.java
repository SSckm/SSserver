package org.sms.project.blog.entity;

import org.sms.project.page.Page;

public class SearchEntity extends Page {
    
    public SearchEntity(int pageNumber, int pageSize) {
        super(pageNumber, pageSize);
    }
    
    private String keyWord;

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }
}
