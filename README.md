# Chithi (চিঠি) - Gmail AI Assistant

A Chrome extension that integrates with Gmail to provide AI-powered email assistance using local Llama 3 model via Ollama.

## Features

- **Enhance Emails**: Improve existing email drafts with better tone, clarity, and professionalism
- **Generate Emails**: Create complete emails from your ideas with customizable tone and purpose
- **Analyze Writing**: Get instant feedback and suggestions to improve your email communication
- **Privacy-First**: All AI processing happens locally using Llama 3

## Tech Stack

- **Chrome Extension**: Manifest V3 with content scripts for Gmail integration
- **Backend**: Spring Boot with LangChain4j Spring Boot Starter for Llama 3 integration
- **AI Model**: Local Ollama server running Llama 3
- **UI**: Modern, Gmail-native design with gradient purple/blue theme

## Prerequisites

1. **Java 21** or higher
2. **Maven** 3.6 or higher
3. **Ollama** installed and running with Llama 3 model
4. **Google Chrome** browser

## Setup Instructions

### 1. Install and Setup Ollama

```bash
# Install Ollama (macOS/Linux)
curl -fsSL https://ollama.com/install.sh | sh

# Pull Llama 3 model
ollama pull llama3

# Start Ollama server (it runs on http://localhost:11434 by default)
ollama serve
```

### 2. Build and Run Spring Boot Backend

```bash
# Clone the repository
cd Chithi

# Build the project
./mvnw clean install

# Run the Spring Boot application
./mvnw spring-boot:run
```

The backend will start on `http://localhost:8080`

### 3. Install Chrome Extension

1. Open Chrome and navigate to `chrome://extensions/`
2. Enable "Developer mode" in the top right
3. Click "Load unpacked"
4. Select the `chrome-extension` folder from this project
5. The Chithi extension icon should appear in your toolbar

### 4. Generate Extension Icons

1. Open `chrome-extension/icons/icon-generator.html` in your browser
2. Right-click each canvas and save as PNG with the appropriate filename:
   - Save 16x16 canvas as `icon16.png`
   - Save 48x48 canvas as `icon48.png`
   - Save 128x128 canvas as `icon128.png`

## Usage

1. Open Gmail in Chrome
2. Compose a new email or reply to an existing one
3. Click the Chithi button in the compose toolbar
4. Select your preferred mode:
   - **Enhance**: Select this to improve your existing draft
   - **Generate**: Enter your email idea and let AI create the full email
   - **Analyze**: Get feedback on your writing
5. Choose from multiple tone options:
   - Professional, Casual, Business Casual, Friendly, Formal
   - Conversational, Empathetic, Direct, Enthusiastic
6. Select purpose (Auto-detect or specific):
   - Auto-detect: AI automatically determines email purpose
   - Specific purposes: Apology, Thank You, Follow-up, Request, Introduction
   - And many more: Invitation, Announcement, Feedback, etc.
7. Click the action button to process
7. Use "Insert" to add the result to your email or "Copy" to clipboard

## API Endpoints

- `GET /api/email/health` - Health check endpoint
- `POST /api/email/enhance` - Enhance existing email content
- `POST /api/email/generate` - Generate email from ideas
- `POST /api/email/analyze` - Analyze email and provide feedback

### Request Format

```json
{
  "content": "Your email content or idea",
  "tone": "professional|casual|business_casual|friendly|formal|conversational|empathetic|direct|enthusiastic",
  "purpose": "auto-detect|general|apology|thank_you|follow-up|request|introduction|..."
}
```

### Response Format

```json
{
  "result": "Processed email content",
  "success": true,
  "error": null
}
```

## Configuration

Edit `src/main/resources/application.properties` to customize:

```properties
# LangChain4j Ollama Configuration
langchain4j.ollama.base-url=http://localhost:11434
langchain4j.ollama.model-name=llama3
langchain4j.ollama.timeout=60s
langchain4j.ollama.temperature=0.7

# Server Configuration
server.port=8080
```

## Troubleshooting

1. **Backend connection failed**: Ensure Spring Boot application is running on port 8080
2. **Ollama not responding**: Check if Ollama is running with `ollama list`
3. **Extension not working**: Check Chrome console for errors (F12 in Gmail)
4. **CORS errors**: Ensure the Gmail domain is properly configured in application.properties

## Development

- Frontend code: `chrome-extension/` directory
- Backend code: `src/main/java/ca/bazlur/chithi/`
- AI behavior is defined in `EmailAIAssistant.java` using declarative AI Services
- The `@AiService` annotation automatically creates implementation with LangChain4j

## License

This project is open source and available under the MIT License.