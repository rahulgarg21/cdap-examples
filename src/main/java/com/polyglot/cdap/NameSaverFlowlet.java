package com.polyglot.cdap;

import co.cask.cdap.api.annotation.ProcessInput;
import co.cask.cdap.api.annotation.UseDataSet;
import co.cask.cdap.api.common.Bytes;
import co.cask.cdap.api.dataset.lib.KeyValueTable;
import co.cask.cdap.api.flow.flowlet.AbstractFlowlet;
import co.cask.cdap.api.flow.flowlet.StreamEvent;
import co.cask.cdap.api.metrics.Metrics;
import com.google.common.base.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Rajiv Singla on 10/21/2016.
 */
public class NameSaverFlowlet extends AbstractFlowlet {

    private static final Logger LOG = LoggerFactory.getLogger(NameSaverFlowlet.class);

    @UseDataSet("nameDataset")
    private KeyValueTable nameTable;

    private Metrics metrics;

    @ProcessInput
    public void process(StreamEvent streamEvent) {
        String name = Charsets.UTF_8.decode(streamEvent.getBody()).toString();
        LOG.info("Received message: {}", name);

        if(name.length() > 0) {
            nameTable.write("NAME",name);
        }

        if (name.length() > 10) {
            metrics.count("names.longnames", 1);
        }

        metrics.count("names.bytes", name.length());

    }


}
