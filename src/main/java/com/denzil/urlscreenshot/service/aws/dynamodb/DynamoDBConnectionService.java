package com.denzil.urlscreenshot.service.aws.dynamodb;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.denzil.urlscreenshot.service.aws.utils.AWSConnectionService;
import org.springframework.beans.factory.annotation.Autowired;

public class DynamoDBConnectionService {
    @Autowired
    private AWSConnectionService awsConnectionService;

    private String dynamoDBEndpoint;
    private String awsRegion;

    public DynamoDBConnectionService(String dynamoDBEndpoint, String awsRegion){
        this.dynamoDBEndpoint = dynamoDBEndpoint;
        this.awsRegion = awsRegion;
    }

    public AmazonDynamoDB connectToDynamoDB() {
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = awsConnectionService.getAwsStaticCredentialsProvider();
        AmazonDynamoDBClientBuilder amazonDynamoDBClientBuilder = AmazonDynamoDBClientBuilder
                .standard()
                .withCredentials(awsStaticCredentialsProvider);
        if(!dynamoDBEndpoint.isEmpty()){
            AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(dynamoDBEndpoint, awsRegion);
            amazonDynamoDBClientBuilder.withEndpointConfiguration(endpointConfiguration);
        }

        if(!awsRegion.isEmpty()){
            amazonDynamoDBClientBuilder.withRegion(awsRegion);
        }
        return amazonDynamoDBClientBuilder.build();
    }
}
