package com.example.SocialNetwork.utils;

import com.example.SocialNetwork.Domain.Entity;

public interface Observable<E extends Entity> {
    void addObserver(Observer<E> e);
    void removeObserver(Observer<E> e);
    void notifyObservers(E t);
}

