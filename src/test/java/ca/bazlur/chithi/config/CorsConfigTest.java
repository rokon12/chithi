package ca.bazlur.chithi.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CorsConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCorsFromGmail() throws Exception {
        mockMvc.perform(options("/api/email/health")
                        .header("Origin", "https://mail.google.com")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://mail.google.com"))
                .andExpect(header().exists("Access-Control-Allow-Methods"));
    }

    @Test
    void testCorsFromChromeExtension() throws Exception {
        mockMvc.perform(options("/api/email/health")
                        .header("Origin", "chrome-extension://abcdefghijklmnop")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "chrome-extension://abcdefghijklmnop"));
    }

    @Test
    void testCorsFromLocalhost() throws Exception {
        mockMvc.perform(options("/api/email/health")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }

    @Test
    void testCorsAllowedMethods() throws Exception {
        mockMvc.perform(options("/api/email/enhance")
                        .header("Origin", "https://mail.google.com")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Methods"))
                .andExpect(header().exists("Access-Control-Allow-Headers"))
                .andExpect(header().string("Access-Control-Max-Age", "3600"));
    }

    @Test
    void testCorsCredentials() throws Exception {
        mockMvc.perform(options("/api/email/health")
                        .header("Origin", "https://mail.google.com")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }
}