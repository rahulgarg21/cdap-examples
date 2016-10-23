package com.polyglot.cdap;

import co.cask.cdap.api.Config;
import co.cask.cdap.api.app.AbstractApplication;
import co.cask.cdap.api.data.stream.Stream;
import co.cask.cdap.api.dataset.lib.KeyValueTable;

/**
 * Created by Rajiv Singla on 10/21/2016.
 */
public class AppMain extends AbstractApplication<AppMain.AppConfig> {

    public static class AppConfig extends Config {

        protected String appName;
        protected String appDescription;
        public AppConfig() {
            appName = "DefaultAPPName";
            appDescription = "DefaultAppDescription";
        }

        public String getAppName() {
            return appName;
        }

        public String getAppDescription() {
            return appDescription;
        }

    }


    @Override
    public void configure() {

        AppConfig appConfig = getConfig();
        setName(appConfig.getAppName());
        setDescription(appConfig.getAppDescription());

        addStream(new Stream("nameStream"));
        addWorker(new NameStreamWorker());
        createDataset("nameDataset", KeyValueTable.class);
        addFlow(new NameSaverFlow());
        addService(new NameService());

    }

}
