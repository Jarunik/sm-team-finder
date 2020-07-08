package org.slos.flow;

import org.slos.permission.configuration.ConfigurationService;
import org.slos.permission.configuration.ConfigurationUpdateRequest;
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
public class UpdateConfigurationFlow {
    @Autowired
    ConfigurationService configurationService;

    @Bean
    public IntegrationFlow startSetActivePlayFlow() {
        return IntegrationFlows.from(setActivePlayRequestChannel())
                // Request Permission
                .transform(configurationService::updateService)
                // Grant or deny permission
                .channel(setActivePlayResponseChannel())
                // Sent back to requestor
                .get();
    }

    @Bean
    public HttpRequestHandlingMessagingGateway setActivePlayGateway() {
        HttpRequestHandlingMessagingGateway gateway = new HttpRequestHandlingMessagingGateway(true);
        RequestMapping mapping = new RequestMapping();
        mapping.setMethods(HttpMethod.POST);
        mapping.setPathPatterns("/updateConfiguration");
        gateway.setRequestMapping(mapping);
        gateway.setRequestChannel(setActivePlayRequestChannel());
        gateway.setReplyChannel(setActivePlayResponseChannel());
        gateway.setRequestPayloadTypeClass(ConfigurationUpdateRequest.class);

        return gateway;
    }

    @Bean
    public MessageChannel setActivePlayRequestChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel setActivePlayResponseChannel() {
        return new DirectChannel();
    }
}
