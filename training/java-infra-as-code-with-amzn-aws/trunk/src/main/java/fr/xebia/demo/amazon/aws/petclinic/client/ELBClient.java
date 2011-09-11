package fr.xebia.demo.amazon.aws.petclinic.client;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancing;
import com.amazonaws.services.elasticloadbalancing.model.ConfigureHealthCheckRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLBCookieStickinessPolicyRequest;
import com.amazonaws.services.elasticloadbalancing.model.DeleteLoadBalancerListenersRequest;
import com.amazonaws.services.elasticloadbalancing.model.DeleteLoadBalancerPolicyRequest;
import com.amazonaws.services.elasticloadbalancing.model.DeleteLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersRequest;
import com.amazonaws.services.elasticloadbalancing.model.EnableAvailabilityZonesForLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.HealthCheck;
import com.amazonaws.services.elasticloadbalancing.model.LBCookieStickinessPolicy;
import com.amazonaws.services.elasticloadbalancing.model.Listener;
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerDescription;
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerNotFoundException;
import com.amazonaws.services.elasticloadbalancing.model.SetLoadBalancerPoliciesOfListenerRequest;

/**
 * Wrapper around {@link AmazonElasticLoadBalancing}.
 */
public class ELBClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(ELBClient.class);
    
    private final AmazonElasticLoadBalancing elb;

    public ELBClient(AmazonElasticLoadBalancing elb) {
        this.elb = elb;
    }
    
    public void enableAvailabilityZonesForLoadBalancer(String loadBalancerName,
            List<String> expectedAvailabilityZones) {
        LOGGER.debug("Request setup of availability zones for load balancer {}",loadBalancerName);
        EnableAvailabilityZonesForLoadBalancerRequest enableAvailabilityZonesForLoadBalancerRequest = new EnableAvailabilityZonesForLoadBalancerRequest( //
                loadBalancerName, expectedAvailabilityZones);
        elb.enableAvailabilityZonesForLoadBalancer(enableAvailabilityZonesForLoadBalancerRequest);
    }

    
    public void createElasticLoadBalancerHealthCheck(String loadBalancerName, String healthCheckUri) {
        LOGGER.debug("Request setup of health check for load balancer {}", loadBalancerName);
        HealthCheck expectedHealthCheck = new HealthCheck() //
                .withTarget("HTTP:8080" + healthCheckUri) //
                .withHealthyThreshold(2) //
                .withUnhealthyThreshold(2) //
                .withInterval(30) //
                .withTimeout(2);
        elb.configureHealthCheck(new ConfigureHealthCheckRequest(loadBalancerName, expectedHealthCheck));
    }

    public LBCookieStickinessPolicy createElasticLoadBalancerCookieStickiness(String loadBalancerName) {
        LOGGER.debug("Request creation of cookie stickiness policy for load balancer {}", loadBalancerName);
        final LBCookieStickinessPolicy expectedLbCookieStickinessPolicy = new LBCookieStickinessPolicy( //
                "petclinic-stickiness-policy", null);
        CreateLBCookieStickinessPolicyRequest createLbCookieStickinessPolicy = new CreateLBCookieStickinessPolicyRequest() //
                .withLoadBalancerName(loadBalancerName) //
                .withPolicyName(expectedLbCookieStickinessPolicy.getPolicyName()) //
                .withCookieExpirationPeriod(expectedLbCookieStickinessPolicy.getCookieExpirationPeriod());
        elb.createLBCookieStickinessPolicy(createLbCookieStickinessPolicy);
        return expectedLbCookieStickinessPolicy;
    }

    public void setupElasticLoadBalancerPolicy(String loadBalancerName, Listener expectedListener, LBCookieStickinessPolicy expectedLbCookieStickinessPolicy) {
        LOGGER.debug("Request setup of policy for load balancer {}", loadBalancerName);
        SetLoadBalancerPoliciesOfListenerRequest setLoadBalancerPoliciesOfListenerRequest = new SetLoadBalancerPoliciesOfListenerRequest() //
                .withLoadBalancerName(loadBalancerName) //
                .withLoadBalancerPort(expectedListener.getLoadBalancerPort()) //
                .withPolicyNames(expectedLbCookieStickinessPolicy.getPolicyName());
        elb.setLoadBalancerPoliciesOfListener(setLoadBalancerPoliciesOfListenerRequest);
    }

    public void deleteExistingElasticLoadBalancer(String loadBalancerName) {
        try {
            LOGGER.debug("Request deletion of load balancer {}", loadBalancerName);
            elb.deleteLoadBalancer(new DeleteLoadBalancerRequest() //
                    .withLoadBalancerName(loadBalancerName));
            
            LOGGER.debug("Request deletion of listeners for load balancer {}", loadBalancerName);
            elb.deleteLoadBalancerListeners(new DeleteLoadBalancerListenersRequest() //
                    .withLoadBalancerName(loadBalancerName) //
                    .withLoadBalancerPorts(80, 8080));
            
            LOGGER.debug("Request deletion of policy for load balancer {}", loadBalancerName);
            elb.deleteLoadBalancerPolicy(new DeleteLoadBalancerPolicyRequest().withLoadBalancerName(loadBalancerName));
        } catch (LoadBalancerNotFoundException e) {
            // Nothing to delete
        }
    }
    
    public LoadBalancerDescription describeLoadBalancer(String loadBalancerName) {
        LOGGER.debug("Request description of load balancer {}", loadBalancerName);
        return elb.describeLoadBalancers(new DescribeLoadBalancersRequest(Arrays.asList(loadBalancerName))).getLoadBalancerDescriptions().get(0);
    }

    
}
