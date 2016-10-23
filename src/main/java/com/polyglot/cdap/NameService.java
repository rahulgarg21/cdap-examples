package com.polyglot.cdap;

import co.cask.cdap.api.service.AbstractService;

/**
 * Created by Rajiv Singla on 10/21/2016.
 */
public class NameService extends AbstractService {
    @Override
    protected void configure() {
        setName("nameService");
        setDescription("Service that provides names");
        addHandler(new NameServiceHandler());
    }
}
