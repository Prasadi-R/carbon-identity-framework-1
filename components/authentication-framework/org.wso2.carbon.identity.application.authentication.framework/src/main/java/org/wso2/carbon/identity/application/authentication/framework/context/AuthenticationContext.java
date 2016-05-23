package org.wso2.carbon.identity.application.authentication.framework.context;


import org.wso2.carbon.identity.application.authentication.framework.cache.SessionContextCache;
import org.wso2.carbon.identity.application.authentication.framework.model.User;
import org.wso2.carbon.identity.application.authentication.framework.model.UserAttribute;
import org.wso2.carbon.identity.application.authentication.framework.processor.handler.authentication
        .AuthenticationHandlerException;
import org.wso2.carbon.identity.application.authentication.framework.processor.handler.authentication.impl.model
        .AbstractSequence;
import org.wso2.carbon.identity.application.authentication.framework.processor.handler.authentication.impl.util.Utility;
import org.wso2.carbon.identity.application.authentication.framework.processor.request.AuthenticationRequest;
import org.wso2.carbon.identity.application.authentication.framework.processor.request.ClientAuthenticationRequest;
import org.wso2.carbon.identity.application.common.model.AuthenticationStep;
import org.wso2.carbon.identity.application.common.model.ServiceProvider;

import java.io.Serializable;
import java.util.Map;

public class AuthenticationContext<T1 extends Serializable, T2 extends Serializable>  extends IdentityMessageContext {

    protected AuthenticationRequest initialAuthenticationRequest;

    private AbstractSequence sequence = null;
    private SequenceContext sequenceContext = new SequenceContext();

    public AuthenticationContext(
            AuthenticationRequest authenticationRequest,
            Map<T1, T2> parameters) {
        super(authenticationRequest, parameters);
        this.initialAuthenticationRequest = authenticationRequest;
    }

    public AuthenticationContext(
            AuthenticationRequest authenticationRequest) {
        super(authenticationRequest);
        this.initialAuthenticationRequest = authenticationRequest;
    }

    public AuthenticationRequest getInitialAuthenticationRequest() {
        return initialAuthenticationRequest;
    }

    public SessionContext getSessionContext() {
        AuthenticationRequest authenticationRequest = (AuthenticationRequest) getIdentityRequest();
        String browserCookieValue = authenticationRequest.getBrowserCookieValue();
        return SessionContextCache.getInstance().getValueFromCache(browserCookieValue);
    }


    public SequenceContext getSequenceContext() {
        return sequenceContext;
    }

    public void setSequenceContext(
            SequenceContext sequenceContext) {
        this.sequenceContext = sequenceContext;
    }

    public AbstractSequence getSequence() {
        return sequence;
    }

    public void setSequence(
            AbstractSequence sequence) {
        this.sequence = sequence;
    }

    public ServiceProvider getServiceProvider() throws AuthenticationHandlerException {
        ClientAuthenticationRequest clientAuthenticationRequest =
                (ClientAuthenticationRequest) getInitialAuthenticationRequest();
        ServiceProvider serviceProvider =
                Utility.getServiceProvider(clientAuthenticationRequest.getRequestType(), clientAuthenticationRequest
                        .getClientId(), clientAuthenticationRequest.getTenantDomain());
        return serviceProvider;
    }

    public User getSubjectUser(){
        SequenceContext sequenceContext = getSequenceContext();
        User subjectStepUser = null ;
        AbstractSequence sequence = getSequence();
        AuthenticationStep[] stepAuthenticatorConfig = sequence.getStepAuthenticatorConfig();
        for(AuthenticationStep authenticationStep : stepAuthenticatorConfig){
            boolean subjectUser = authenticationStep.isSubjectStep();
            if(subjectUser) {
                SequenceContext.StepContext stepContext =
                        sequenceContext.getStepContext(authenticationStep.getStepOrder());
                subjectStepUser = stepContext.getUser();
            }
        }
        return subjectStepUser ;
    }

    public UserAttribute getAttribute(){
        SequenceContext sequenceContext = getSequenceContext();
        User attributeStepUser = null ;
        AbstractSequence sequence = getSequence();
        AuthenticationStep[] stepAuthenticatorConfig = sequence.getStepAuthenticatorConfig();
        for(AuthenticationStep authenticationStep : stepAuthenticatorConfig){
            boolean attributeStep = authenticationStep.isAttributeStep();
            if(attributeStep) {
                SequenceContext.StepContext stepContext =
                        sequenceContext.getStepContext(authenticationStep.getStepOrder());
                attributeStepUser = stepContext.getUser();
            }
        }
        return attributeStepUser.getAttribute() ;
    }
}
