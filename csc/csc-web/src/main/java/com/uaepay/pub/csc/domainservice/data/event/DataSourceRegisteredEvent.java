package com.uaepay.pub.csc.domainservice.data.event;

import org.springframework.context.ApplicationEvent;

import com.uaepay.pub.csc.domainservice.data.DataSourceConfigFactory;

/**
 * @author caoyongxing
 */
public class DataSourceRegisteredEvent extends ApplicationEvent {
    public DataSourceRegisteredEvent(Object source) {
        super(source);
    }

    public final DataSourceConfigFactory getDataSourceConfigFactory() {
        return (DataSourceConfigFactory)this.getSource();
    }
}
