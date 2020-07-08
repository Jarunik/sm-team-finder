package org.slos.flow;

import org.slos.permission.PermissionRequest;
import org.slos.permission.PermissionToPlayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.http.inbound.HttpRequestHandlingMessagingGateway;
import org.springframework.integration.http.inbound.RequestMapping;
import org.springframework.messaging.MessageChannel;

@Configuration
@IntegrationComponentScan
@EnableIntegration
public class PlayPermissionFlow {
    @Autowired
    PermissionToPlayService permissionToPlayService;

    @Bean
    public IntegrationFlow startPermissionRequestFlow() {
        return IntegrationFlows.from(permissionRequestRequestChannel())
                // Request Permission
                .transform(permissionToPlayService::permissionToPlay)
                // Grant or deny permission
                .channel(permissionRequestResponseChannel())
                // Sent back to requestor
                .get();
    }

    @Bean
    public HttpRequestHandlingMessagingGateway permissionRequestGateway() {
        HttpRequestHandlingMessagingGateway gateway = new HttpRequestHandlingMessagingGateway(true);
        RequestMapping mapping = new RequestMapping();
        mapping.setMethods(HttpMethod.POST);
        mapping.setPathPatterns("/permissionToPlay");
        gateway.setRequestMapping(mapping);
        gateway.setRequestChannel(permissionRequestRequestChannel());
        gateway.setReplyChannel(permissionRequestResponseChannel());
        gateway.setRequestPayloadTypeClass(PermissionRequest.class);

        return gateway;
    }

    @Bean
    public MessageChannel permissionRequestResponseChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel permissionRequestRequestChannel() {
        return new DirectChannel();
    }
}
