package com.lrabbitmx.personalblog.task;

import com.lrabbitmx.personalblog.util.BufferMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FlushBufferTask {

    @Autowired
    private BufferMap bufferMap;
    private boolean isFlushed = false;

    @Scheduled(cron="0 0 3 * * *")
    private void flushBuffer() {
        bufferMap.flushBufferMap();
    }

}
