package ca.bazlur.chithi.service;


import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import org.springframework.context.annotation.Profile;

@AiService(wiringMode = AiServiceWiringMode.EXPLICIT, chatModel = "ollama")
@Profile("!test")
public interface EmailAIAssistant {

  @SystemMessage("""
      You are a professional email assistant. Your role is to help users write better emails.
      Always maintain the user's intended message while improving clarity and professionalism.
      IMPORTANT: Return ONLY the enhanced email body text. Do not include:
      - Subject lines
      - Explanations of changes
      - Meta-commentary
      - Headers like "Enhanced Email:" or "Subject:"
      If the email contains a signature, preserve it unchanged at the end.
      """)
  @UserMessage("""
      Please enhance the following email to be more {{tone}} in tone.
      {% if purpose == 'auto-detect' %}
      Analyze the content and automatically determine the purpose of the email, then enhance it accordingly.
      {% else %}
      The email is intended as a {{purpose}}.
      {% endif %}
      Keep the main message intact but improve clarity, professionalism, and impact.
      Return ONLY the enhanced email body text without any additional commentary.
      
      Original email:
      {{content}}
      """)
  String enhanceEmail(@V("content") String content, @V("tone") String tone, @V("purpose") String purpose);

  @SystemMessage("""
      You are a professional email writer. Your role is to create well-structured emails based on user ideas.
      Always write clear, concise, and professional emails.
      IMPORTANT: Return ONLY the email body text. Do not include:
      - Subject lines
      - Headers like "Subject:" or "Email:"
      - Meta-commentary or explanations
      Start directly with the salutation (e.g., "Dear...", "Hi...", etc.)
      """)
  @UserMessage("""
      Generate a professional email based on the following idea.
      The email should have a {{tone}} tone.
      {% if purpose == 'auto-detect' %}
      Analyze the idea and automatically determine the appropriate purpose for the email.
      {% else %}
      The email should be suitable for {{purpose}}.
      {% endif %}
      Return ONLY the email body starting with the salutation.
      
      Idea: {{idea}}
      """)
  String generateEmail(@V("idea") String idea, @V("tone") String tone, @V("purpose") String purpose);

  @SystemMessage("""
      You are an email writing coach. Your role is to analyze emails and provide constructive feedback.
      Focus on clarity, tone, professionalism, and effectiveness.
      """)
  @UserMessage("""
      Analyze the following email and provide specific feedback on:
      1. Clarity and structure
      2. Tone and professionalism
      3. Grammar and spelling
      4. Suggestions for improvement
      
      Email to analyze:
      {{content}}
      """)
  String analyzeEmail(@V("content") String content);
}