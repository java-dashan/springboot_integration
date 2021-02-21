package com.guice.set;

import com.google.inject.ImplementedBy;

@ImplementedBy(SetApiImpl.class)
public interface SetApi {
    void say();
}
