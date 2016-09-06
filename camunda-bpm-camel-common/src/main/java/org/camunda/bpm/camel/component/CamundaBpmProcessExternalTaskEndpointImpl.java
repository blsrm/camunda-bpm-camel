package org.camunda.bpm.camel.component;

import static org.camunda.bpm.camel.component.CamundaBpmConstants.COMPLETETASK_DEFAULT;
import static org.camunda.bpm.camel.component.CamundaBpmConstants.COMPLETETASK_PARAMETER;
import static org.camunda.bpm.camel.component.CamundaBpmConstants.RETRIES_PARAMETER;
import static org.camunda.bpm.camel.component.CamundaBpmConstants.RETRYTIMEOUTS_PARAMETER;
import static org.camunda.bpm.camel.component.CamundaBpmConstants.RETRYTIMEOUT_DEFAULT;
import static org.camunda.bpm.camel.component.CamundaBpmConstants.RETRYTIMEOUT_PARAMETER;
import static org.camunda.bpm.camel.component.CamundaBpmConstants.TOPIC_PARAMETER;
import static org.camunda.bpm.camel.component.CamundaBpmConstants.WORKERID_PARAMETER;

import java.util.Map;

import org.apache.camel.Processor;
import org.apache.camel.converter.TimePatternConverter;
import org.apache.camel.impl.ProcessorEndpoint;
import org.camunda.bpm.camel.component.externaltasks.TaskProcessor;
import org.camunda.bpm.engine.ProcessEngine;

public class CamundaBpmProcessExternalTaskEndpointImpl extends ProcessorEndpoint implements CamundaBpmEndpoint {

    public static final String EXCHANGE_HEADER_TASK = "CamundaBpmExternalTask";

    private CamundaBpmComponent component;

    // parameters
    private final String topic;
    private final boolean completeTask;
    private final int retries;
    private final long retryTimeout;
    private final long[] retryTimeouts;
    private final String workerId;

    public CamundaBpmProcessExternalTaskEndpointImpl(final String endpointUri, final CamundaBpmComponent component,
            final Map<String, Object> parameters) {

        super(endpointUri, component);

        this.component = component;

        if (parameters.containsKey(TOPIC_PARAMETER)) {
            this.topic = (String) parameters.remove(TOPIC_PARAMETER);
        } else {
        	this.topic = null;
        }
        
        if (parameters.containsKey(RETRIES_PARAMETER)) {
            this.retries = Integer.parseInt((String) parameters.remove(RETRIES_PARAMETER));
        } else {
            this.retries = 0;
        }

        if (parameters.containsKey(RETRYTIMEOUT_PARAMETER)) {
            this.retryTimeout = TimePatternConverter.toMilliSeconds((String) parameters.remove(RETRYTIMEOUT_PARAMETER));
        } else {
            this.retryTimeout = RETRYTIMEOUT_DEFAULT;
        }

        if (parameters.containsKey(RETRYTIMEOUTS_PARAMETER)) {
            final String retryTimeoutsString = (String) parameters.remove(RETRYTIMEOUTS_PARAMETER);
            final String[] retryTimeoutsStrings = retryTimeoutsString.split(",");
            retryTimeouts = new long[retryTimeoutsStrings.length];
            for (int i = 0; i < retryTimeoutsStrings.length; ++i) {
                retryTimeouts[i] = TimePatternConverter.toMilliSeconds(retryTimeoutsStrings[i]);
            }
        } else {
            retryTimeouts = null;
        }

        if (parameters.containsKey(COMPLETETASK_PARAMETER)) {
            this.completeTask = Boolean.parseBoolean((String) parameters.remove(COMPLETETASK_PARAMETER));
        } else {
            this.completeTask = COMPLETETASK_DEFAULT;
        }

        if (parameters.containsKey(WORKERID_PARAMETER)) {
            this.workerId = (String) parameters.remove(WORKERID_PARAMETER);
        } else {
        	this.workerId = null;
        }

    }

    @Override
    public boolean isSingleton() {

        return true;

    }

    @Override
    public ProcessEngine getProcessEngine() {

        return component.getProcessEngine();

    }
    
    @Override
    protected Processor createProcessor() throws Exception {
    	
    	return new TaskProcessor(this, topic, retries, retryTimeout,
    			retryTimeouts, completeTask, workerId);
    	
    }

}
