package com.guice.set;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;

public class SetModule implements Module {
    @Override
    public void configure(Binder binder) {
        binder.bind(SetApi.class).to(SetApiImpl.class).in(Scopes.SINGLETON);
    }
}
