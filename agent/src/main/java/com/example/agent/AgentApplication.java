package com.example.agent;

import org.springaicommunity.agent.tools.SkillsTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@SpringBootApplication
public class AgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgentApplication.class, args);
    }

    @Bean
    QuestionAnswerAdvisor answerAdvisor(VectorStore vectorStore) {
        return QuestionAnswerAdvisor.builder(vectorStore).build();
    }
}

interface DogRepository extends ListCrudRepository<Dog, Integer> {
}

record Dog(@Id int id, String name, String description) {
}

@Controller
@ResponseBody
class AssistantController {

    private final ChatClient chatClient;

    AssistantController(QuestionAnswerAdvisor questionAnswerAdvisor,
                        DogRepository r, VectorStore v,
                        ToolCallbackProvider tcp,
                        ChatClient.Builder chatClient) {


        if (false)
            r.findAll().forEach(dog -> {
                var dogument = new Document("id: %s, name: %s, description: %s".formatted(
                        dog.id(), dog.name(), dog.description()));
                v.add(List.of(dogument));
            });
        var skills = SkillsTool.builder()
                .addSkillsResource(new ClassPathResource("/META-INF/skills"))
                .build();
        this.chatClient = chatClient
                .defaultTools(skills, tcp)
                .defaultAdvisors(questionAnswerAdvisor)
                .defaultSystem("""
                        you are an AI powered assistant to help people adopt a dog from the adoptions agency named 
                        Pooch Palace with locations in Taipei, Utrecht, Seoul, Tokyo, Singapore, Paris, Mumbai, New Delhi, Barcelona, San Francisco, and London. Information about the dogs availables will be presented below. If there is no information, then return a polite response suggesting wes don't have any dogs available. If somebody asks you about animals, and there's no information in the context, then feel free to source the answer from other places. If somebody asks for a time to pick up the dog, don't ask other questions: simply provide a time by consulting the tools you have available.\s
                        
                        """)
                .build();
    }

    @GetMapping("/ask")
    String ask(@RequestParam String question) {
        return this.chatClient
                .prompt(question)
                .call()
                .content();
    }
}