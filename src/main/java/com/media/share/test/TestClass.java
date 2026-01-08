package com.media.share.test;

import java.util.List;

public class TestClass<T> extends Object{
    private String that;

    public String test(){
        return null;
    }
    public <T> void printList(List<T> list) {
        for (T item : list) {
            System.out.println(item);
        }
    }

}
