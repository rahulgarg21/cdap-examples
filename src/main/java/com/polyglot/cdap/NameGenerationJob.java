package com.polyglot.cdap;

import co.cask.cdap.api.worker.WorkerContext;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Rajiv Singla on 10/22/2016.
 */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class NameGenerationJob implements Job {

    private static final Logger LOG = LoggerFactory.getLogger(NameGenerationJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        String streamName = jobDataMap.getString("streamName");
        WorkerContext workerContext = (WorkerContext) jobDataMap.get("workerContext");
        String uuid = UUID.randomUUID().toString();
        if(context.getNextFireTime() == null) {
            LOG.info("Last execution..............");
        }
        LOG.info("Writing to stream random UUID: {}", uuid);
        try {
            workerContext.write(streamName, uuid);
        } catch (IOException e) {
            LOG.error("Cannot write to stream Name: {}, exception: {}", streamName , e.toString());
        }
    }
}
