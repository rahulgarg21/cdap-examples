package com.polyglot.cdap;

import co.cask.cdap.api.flow.AbstractFlow;

/**
 * Created by Rajiv Singla on 10/21/2016.
 */
public class NameSaverFlow extends AbstractFlow {

    @Override
    protected void configure() {
        setName("nameSaverFlow");
        setDescription("Flow to save names to dataset");
        addFlowlet("nameSaverFlowlet", new NameSaverFlowlet());
        connectStream("nameStream", "nameSaverFlowlet");
    }
}
