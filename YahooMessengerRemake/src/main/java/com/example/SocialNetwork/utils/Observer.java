package com.example.SocialNetwork.utils;

import com.example.SocialNetwork.Domain.Entity;

public interface Observer<E extends Entity> {
    void update(E e);
}