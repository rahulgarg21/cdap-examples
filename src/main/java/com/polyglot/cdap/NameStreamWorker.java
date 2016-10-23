package com.polyglot.cdap;

import co.cask.cdap.api.worker.AbstractWorker;
import co.cask.cdap.api.worker.WorkerContext;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Rajiv Singla on 10/22/2016.
 */
public class NameStreamWorker extends AbstractWorker {

    private static final Logger LOG = LoggerFactory.getLogger(NameStreamWorker.class);

    private Scheduler scheduler;

    @Override
    protected void configure() {
        setName("NameStreamWorker");
        setDescription("Generate random names and saves them to nameStream");
    }

    @Override
    public void initialize(WorkerContext context) throws Exception {
        super.initialize(context);
        initializeScheduler();
    }

    @Override
    public void run() {
        try {
            scheduler.start();
            LOG.info("Scheduler start successfully");
        } catch (SchedulerException e) {
            LOG.info("Error while starting scheduler: {}", e);
            throw new RuntimeException("Error while start Scheduler", e);
        }
        LOG.info("Finished triggering worker");


        // indefinite loop which sleeps for about 10 milli seconds confirms scheduler is not running
        // and finishes worker execution after scheduler is shutdown
        boolean isSchedulerShutdown = true;
        while(isSchedulerShutdown) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                if (scheduler!= null && scheduler.isInStandbyMode() && scheduler.isShutdown()) {
                    LOG.info("Scheduler is not running! So shutting down the worker.");
                    isSchedulerShutdown = false;
                }
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }
        LOG.info("Finished execution of Name Stream worker thread");
    }

    @Override
    public void stop() {
        try {
            LOG.info("Shutting down scheduler");
            scheduler.shutdown(true);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
    }

    private void initializeScheduler() throws SchedulerException {

        scheduler = StdSchedulerFactory.getDefaultScheduler();

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("streamName", "nameStream");
        jobDataMap.put("workerContext", getContext());

        JobDetail jobDetail = JobBuilder.newJob(NameGenerationJob.class)
                .withIdentity("NameGenerationJob", "Group1")
                .usingJobData(jobDataMap).build();
        SimpleScheduleBuilder simpleScheduleBuilder =
                SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(10).withRepeatCount(30);
        SimpleTrigger simpleTrigger = TriggerBuilder.newTrigger().withIdentity("NameGenerationTrigger", "Group1")
                .startNow().withSchedule(simpleScheduleBuilder).build();
        scheduler.scheduleJob(jobDetail, simpleTrigger);

        LOG.info("Initialized Name Stream Scheduler");

    }
}
