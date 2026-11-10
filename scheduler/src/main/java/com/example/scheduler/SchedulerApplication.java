package com.example.scheduler;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@SpringBootApplication
public class SchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchedulerApplication.class, args);
    }

}

@Component
class Scheduler {

    @McpTool(description = "schedule an appointment to pick up or adopt a dog from a Pooch Palace locatio")
    Instant schedule(@McpToolParam int dogId) {
        IO.println("scheduling " + dogId + "!");
        return Instant.now().plus(3, ChronoUnit.DAYS);
    }
}