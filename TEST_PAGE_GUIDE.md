# Chithi Email Assistant Test Page Guide

The test page is now available at http://localhost:8080/

## Features

### 1. **Enhance Email**
- Takes your draft email and improves it based on the selected tone and purpose
- Example: Transform "I need the quarterly report by end of week" into a professional request

### 2. **Generate Email**
- Creates a complete email from just an idea or brief description
- Example: Enter "schedule team meeting next Tuesday" and get a full email

### 3. **Analyze Email**
- Provides feedback on tone, clarity, and effectiveness of your email
- Helps identify areas for improvement

## How to Use

1. Open http://localhost:8080/ in your browser
2. Enter your email content in the text area
3. Select the desired tone:
   - Professional
   - Casual
   - Friendly
   - Formal
   - Persuasive
   - Empathetic
   - Assertive
   - Diplomatic

4. Choose the purpose (or let it auto-detect):
   - Request
   - Response
   - Follow-up
   - Thank You
   - Apology
   - Invitation
   - Announcement
   - Inquiry
   - Complaint
   - Feedback
   - Introduction
   - Update
   - General

5. Click one of the action buttons:
   - **Enhance Email** - Improve existing content
   - **Generate Email** - Create new email from idea
   - **Analyze Email** - Get feedback on content

## Keyboard Shortcuts

- `Ctrl/Cmd + E` - Enhance Email
- `Ctrl/Cmd + G` - Generate Email  
- `Ctrl/Cmd + A` - Analyze Email

## Note

Make sure you have Ollama running with a compatible model for the AI features to work properly. If you see errors, check that:
1. Ollama is running (`ollama serve`)
2. You have a model installed (e.g., `ollama pull llama2`)

## Testing from Command Line

You can also test the API directly:

```bash
# Enhance email
curl -X POST http://localhost:8080/api/email/enhance \
  -H "Content-Type: application/json" \
  -d '{
    "content": "I need the report",
    "tone": "professional",
    "purpose": "request"
  }'

# Generate email
curl -X POST http://localhost:8080/api/email/generate \
  -H "Content-Type: application/json" \
  -d '{
    "content": "invite team to lunch meeting",
    "tone": "friendly",
    "purpose": "invitation"
  }'

# Analyze email
curl -X POST http://localhost:8080/api/email/analyze \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Hey! Can u send me that thing we talked about yesterday? Thanks!",
    "tone": "casual",
    "purpose": "request"
  }'
```