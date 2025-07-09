# Chithi (চিঠি) Chrome Extension

AI-powered Gmail assistant that enhances, generates, and analyzes emails with customizable tone and purpose.

## Features

- **Enhance Emails**: Improve existing drafts with better tone, clarity, and professionalism
- **Generate Emails**: Create complete emails from your ideas
- **Analyze Writing**: Get instant feedback and suggestions to improve your communication
- **Customizable Settings**: Choose from various tones and purposes
- **Gmail Integration**: Works seamlessly within Gmail's compose window

## Installation

### Development Installation

1. Open Chrome and navigate to `chrome://extensions/`
2. Enable "Developer mode" in the top right
3. Click "Load unpacked"
4. Select the `chrome-extension` directory from this project
5. The Chithi icon will appear in your Chrome toolbar

### Prerequisites

- The Chithi backend server must be running on `http://localhost:8080`
- Gmail must be open in a Chrome tab

## Usage

### Method 1: Inline Toolbar in Gmail Compose

When you open a new compose window in Gmail, Chithi automatically adds an inline toolbar above the email body with:
- **Tone and Purpose dropdowns**: Select your preferred tone and purpose
- **Enhance button**: Click to improve your existing email draft
- **Analyze button**: Click to get feedback and suggestions
- **Results display**: Shows enhanced text or analysis directly below the toolbar
- **Apply button**: One-click to replace your email with the enhanced version

### Method 2: Via Extension Popup

1. Click the Chithi icon in your Chrome toolbar
2. Select your preferred tone and purpose
3. Click on one of the three features:
   - **Enhance Emails**: For improving existing drafts
   - **Generate Emails**: For creating new emails from ideas
   - **Analyze Writing**: For getting feedback on your email

## Available Tones

- Professional
- Casual
- Friendly
- Formal
- Persuasive
- Empathetic
- Assertive
- Diplomatic

## Available Purposes

- Auto-detect
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

## Icon Generation

To generate PNG icons from the SVG source:

1. Open `chrome-extension/icons/generate-icons.html` in a browser
2. The page will display all required icon sizes
3. Click "Download All" to save the icons
4. Place the downloaded icons in the `icons` directory

## Development

### File Structure

```
chrome-extension/
├── manifest.json           # Extension configuration
├── popup.html             # Extension popup UI
├── scripts/
│   ├── popup.js          # Popup functionality
│   └── gmail-integration.js  # Gmail content script
├── styles/
│   └── chithi.css        # Extension styles
├── icons/
│   ├── icon.svg          # Source SVG icon
│   └── icon*.png         # Generated PNG icons
└── README.md             # This file
```

### Making Changes

1. Edit the relevant files
2. Go to `chrome://extensions/`
3. Click the refresh button on the Chithi extension
4. Reload Gmail to see the changes

## Troubleshooting

- **Extension not working**: Ensure the backend server is running on port 8080
- **Button not appearing**: Refresh Gmail and wait a few seconds
- **Panel position issues**: The panel positions itself relative to the compose window
- **Settings not saving**: Check Chrome storage permissions

## Security

- The extension only runs on `https://mail.google.com/*`
- Requires minimal permissions (activeTab, storage)
- All email processing happens on your local backend server