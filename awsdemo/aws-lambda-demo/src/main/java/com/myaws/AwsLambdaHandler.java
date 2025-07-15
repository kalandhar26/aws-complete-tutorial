package com.myaws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.Map;

public class AwsLambdaHandler implements RequestHandler<Map<String, String>, String> {

    Logger logger = LogManager.getLogger(this.getClass().getName());

    public String handleRequest(Map<String, String> input, Context context) {
        System.out.println("System Output is: " + input);
        LambdaLogger lambdaLogger = context.getLogger();
        lambdaLogger.log("log data is: " + LocalDateTime.now());
        return "Current Date Time: " + LocalDateTime.now();
    }
}
