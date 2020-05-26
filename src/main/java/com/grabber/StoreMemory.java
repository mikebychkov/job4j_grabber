package com.grabber;

import java.util.ArrayList;
import java.util.List;

public class StoreMemory implements Store {

    private List<Post> list = new ArrayList<>();

    @Override
    public void save(Post post) {
        this.list.add(post);
    }

    @Override
    public List<Post> getAll() {
        return this.list;
    }
}
