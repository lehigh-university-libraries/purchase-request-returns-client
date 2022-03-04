package edu.lehigh.libraries.purchase_request.returns_client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix="returns-client")
@EnableAsync
@Getter @Setter
public class PropertiesConfig {
    
    private Database db;
    private WorkflowServer workflowServer;

    /**
     * Disables all security.  DO NOT USE IN PRODUCTION.
     */
    private boolean disableSecurity = false;

    @Getter @Setter
    /**
     * Connection properties for database used for workflow proxy metadata.
     * 
     * This database is not used for the actual workflow items backed by a WorkflowService.
     */
    public static class Database {

        /**
         * Database hostname
         */
        private String host;

        /**
         * Database name
         */
        private String name;

        /**
         * Database username
         */
        private String username;

        /**
         * Database password
         */
        private String password;

    }

    @Getter @Setter
    public static class WorkflowServer {

        /**
         * Workflow Proxy Server url to post a new purchase request
         */
        private String postPurchaseRequestsUrl;

        /**
         * Workflow Proxy Server username
         */
        private String username;

        /**
         * Workflow Proxy Server password
         */
        private String password;

    }

}


