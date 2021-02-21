package com.guice;

import com.google.inject.ImplementedBy;

@ImplementedBy(ApiImpl.class)
public interface Api {
    void say();
}
