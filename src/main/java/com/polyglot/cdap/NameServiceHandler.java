package com.polyglot.cdap;

import co.cask.cdap.api.annotation.UseDataSet;
import co.cask.cdap.api.common.Bytes;
import co.cask.cdap.api.dataset.lib.KeyValueTable;
import co.cask.cdap.api.metrics.Metrics;
import co.cask.cdap.api.service.http.AbstractHttpServiceHandler;
import co.cask.cdap.api.service.http.HttpServiceRequest;
import co.cask.cdap.api.service.http.HttpServiceResponder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.nio.charset.Charset;

/**
 * Created by Rajiv Singla on 10/21/2016.
 */
public class NameServiceHandler extends AbstractHttpServiceHandler {

    @UseDataSet("nameDataset")
    private KeyValueTable nameTable;

    private Metrics metrics;

    @Path("name")
    @GET
    public void greet(HttpServiceRequest request, HttpServiceResponder responder) {

        byte[] nameArray = nameTable.read(Bytes.toBytes("NAME"));

        String name = nameArray != null ? Bytes.toString(nameArray) : "null (No name found)";
        responder.sendString(200, name, Charset.forName("UTF-8"));

    }

}
