// Chithi Gmail Integration
(function() {
  'use strict';

  let chithiPanel = null;
  let currentComposeView = null;
  let isProcessing = false;

  // API Configuration
  const API_BASE_URL = 'http://localhost:8080/api/email';

  // Initialize when Gmail loads
  function init() {
    // Wait for Gmail to fully load
    const checkInterval = setInterval(() => {
      const composeButton = document.querySelector('[gh="cm"]');
      if (composeButton) {
        clearInterval(checkInterval);
        setupGmailObserver();
        injectStyles();
      }
    }, 1000);
  }

  // Watch for compose windows
  function setupGmailObserver() {
    const observer = new MutationObserver((mutations) => {
      mutations.forEach((mutation) => {
        mutation.addedNodes.forEach((node) => {
          if (node.nodeType === 1) {
            // Check for compose window
            const composeView = node.querySelector('[role="dialog"] [aria-label*="Message Body"]') ||
                               (node.matches && node.matches('[role="dialog"]') && node.querySelector('[aria-label*="Message Body"]'));
            
            if (composeView) {
              setTimeout(() => injectChithiButton(node), 500);
            }
          }
        });
      });
    });

    observer.observe(document.body, {
      childList: true,
      subtree: true
    });
  }

  // Inject Chithi button into Gmail toolbar
  function injectChithiButton(composeContainer) {
    const toolbar = composeContainer.querySelector('[command="cmd"]')?.parentElement ||
                   composeContainer.querySelector('td.gU.Up > div > div');
    
    if (!toolbar || toolbar.querySelector('.chithi-button')) return;

    const chithiButton = createChithiButton();
    
    // Insert before send button or at the end
    const sendButton = toolbar.querySelector('[data-tooltip*="Send"]');
    if (sendButton) {
      toolbar.insertBefore(chithiButton, sendButton);
    } else {
      toolbar.appendChild(chithiButton);
    }

    // Store reference to compose view
    currentComposeView = composeContainer;
  }

  // Create the Chithi button
  function createChithiButton() {
    const button = document.createElement('div');
    button.className = 'chithi-button wG J-Z-I';
    button.setAttribute('data-tooltip', 'Chithi AI Assistant');
    button.innerHTML = `
      <div class="J-J5-Ji J-Z-I-Kv-H">
        <div class="chithi-icon">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M3 8L10.89 13.26C11.2187 13.4793 11.6049 13.5963 12 13.5963C12.3951 13.5963 12.7813 13.4793 13.11 13.26L21 8M5 19H19C19.5304 19 20.0391 18.7893 20.4142 18.4142C20.7893 18.0391 21 17.5304 21 17V7C21 6.46957 20.7893 5.96086 20.4142 5.58579C20.0391 5.21071 19.5304 5 19 5H5C4.46957 5 3.96086 5.21071 3.58579 5.58579C3.21071 5.96086 3 6.46957 3 7V17C3 17.5304 3.21071 18.0391 3.58579 18.4142C3.96086 18.7893 4.46957 19 5 19Z" stroke="url(#chithi-gradient)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <defs>
              <linearGradient id="chithi-gradient" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%" style="stop-color:#667eea;stop-opacity:1" />
                <stop offset="100%" style="stop-color:#764ba2;stop-opacity:1" />
              </linearGradient>
            </defs>
          </svg>
        </div>
      </div>
    `;

    button.addEventListener('click', toggleChithiPanel);
    return button;
  }

  // Toggle Chithi panel
  function toggleChithiPanel() {
    if (chithiPanel) {
      chithiPanel.remove();
      chithiPanel = null;
    } else {
      showChithiPanel();
    }
  }

  // Show Chithi panel
  function showChithiPanel() {
    chithiPanel = document.createElement('div');
    chithiPanel.className = 'chithi-panel';
    chithiPanel.innerHTML = `
      <div class="chithi-header">
        <h3>
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M3 8L10.89 13.26C11.2187 13.4793 11.6049 13.5963 12 13.5963C12.3951 13.5963 12.7813 13.4793 13.11 13.26L21 8M5 19H19C19.5304 19 20.0391 18.7893 20.4142 18.4142C20.7893 18.0391 21 17.5304 21 17V7C21 6.46957 20.7893 5.96086 20.4142 5.58579C20.0391 5.21071 19.5304 5 19 5H5C4.46957 5 3.96086 5.21071 3.58579 5.58579C3.21071 5.96086 3 6.46957 3 7V17C3 17.5304 3.21071 18.0391 3.58579 18.4142C3.96086 18.7893 4.46957 19 5 19Z" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          Chithi (চিঠি)
        </h3>
        <button class="chithi-close" onclick="this.closest('.chithi-panel').remove()">×</button>
      </div>
      
      <div class="chithi-mode-selector">
        <button class="mode-btn active" data-mode="enhance">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          Enhance
        </button>
        <button class="mode-btn" data-mode="generate">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M14 2L14 6C14 6.53043 14.2107 7.03914 14.5858 7.41421C14.9609 7.78929 15.4696 8 16 8L20 8M14 2L9 2C7.89543 2 7 2.89543 7 4L7 20C7 21.1046 7.89543 22 9 22L18 22C19.1046 22 20 21.1046 20 20L20 8M14 2L20 8M10 9L10 9.01M10 13L10 13.01M10 17L10 17.01" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          Generate
        </button>
        <button class="mode-btn" data-mode="analyze">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M9 11L12 14L22 4M21 12V19C21 19.5304 20.7893 20.0391 20.4142 20.4142C20.0391 20.7893 19.5304 21 19 21H5C4.46957 21 3.96086 20.7893 3.58579 20.4142C3.21071 20.0391 3 19.5304 3 19V5C3 4.46957 3.21071 3.96086 3.58579 3.58579C3.96086 3.21071 4.46957 3 5 3H16" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          Analyze
        </button>
      </div>

      <div class="chithi-options">
        <div class="option-group">
          <label>Tone:</label>
          <select id="chithi-tone">
            <option value="professional">Professional</option>
            <option value="casual">Casual</option>
            <option value="business_casual">Business Casual</option>
            <option value="friendly">Friendly</option>
            <option value="formal">Formal</option>
            <option value="conversational">Conversational</option>
            <option value="empathetic">Empathetic</option>
            <option value="direct">Direct</option>
            <option value="enthusiastic">Enthusiastic</option>
          </select>
        </div>
        <div class="option-group">
          <label>Purpose:</label>
          <select id="chithi-purpose">
            <option value="auto-detect">Auto-detect</option>
            <option value="general">General</option>
            <option value="apology">Apology</option>
            <option value="thank_you">Thank You</option>
            <option value="follow-up">Follow-up</option>
            <option value="request">Request</option>
            <option value="introduction">Introduction</option>
            <option value="invitation">Invitation</option>
            <option value="announcement">Announcement</option>
            <option value="feedback">Feedback</option>
            <option value="complaint">Complaint</option>
            <option value="inquiry">Inquiry</option>
            <option value="confirmation">Confirmation</option>
            <option value="rejection">Rejection</option>
            <option value="acceptance">Acceptance</option>
            <option value="resignation">Resignation</option>
            <option value="recommendation">Recommendation</option>
            <option value="congratulations">Congratulations</option>
            <option value="condolence">Condolence</option>
            <option value="reminder">Reminder</option>
            <option value="proposal">Proposal</option>
            <option value="report">Report</option>
            <option value="update">Update</option>
          </select>
        </div>
      </div>

      <div class="chithi-input-section" id="generate-input" style="display: none;">
        <textarea id="chithi-idea" placeholder="Describe what you want to write about..."></textarea>
      </div>

      <button class="chithi-action-btn" id="chithi-process">
        <span class="btn-text">Enhance Email</span>
        <div class="spinner" style="display: none;"></div>
      </button>

      <div class="chithi-result" id="chithi-result" style="display: none;">
        <div class="result-content" id="result-content"></div>
        <div class="result-actions">
          <button class="action-btn" id="insert-btn">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M11 4H4C3.46957 4 2.96086 4.21071 2.58579 4.58579C2.21071 4.96086 2 5.46957 2 6V20C2 20.5304 2.21071 21.0391 2.58579 21.4142C2.96086 21.7893 3.46957 22 4 22H18C18.5304 22 19.0391 21.7893 19.4142 21.4142C19.7893 21.0391 20 20.5304 20 20V13M18.5 2.5C18.8978 2.10217 19.4374 1.87868 20 1.87868C20.5626 1.87868 21.1022 2.10217 21.5 2.5C21.8978 2.89782 22.1213 3.43739 22.1213 4C22.1213 4.56261 21.8978 5.10217 21.5 5.5L12 15L8 16L9 12L18.5 2.5Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            Insert
          </button>
          <button class="action-btn" id="copy-btn">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M8 5H6C5.46957 5 4.96086 5.21071 4.58579 5.58579C4.21071 5.96086 4 6.46957 4 7V19C4 19.5304 4.21071 20.0391 4.58579 20.4142C4.96086 20.7893 5.46957 21 6 21H16C16.5304 21 17.0391 20.7893 17.4142 20.4142C17.7893 20.0391 18 19.5304 18 19V18M8 5C8 5.53043 8.21071 6.03914 8.58579 6.41421C8.96086 6.78929 9.46957 7 10 7H12C12.5304 7 13.0391 6.78929 13.4142 6.41421C13.7893 6.03914 14 5.53043 14 5M8 5C8 4.46957 8.21071 3.96086 8.58579 3.58579C8.96086 3.21071 9.46957 3 10 3H12C12.5304 3 13.0391 3.21071 13.4142 3.58579C13.7893 3.96086 14 4.46957 14 5M14 5H16C16.5304 5 17.0391 5.21071 17.4142 5.58579C17.7893 5.96086 18 6.46957 18 7V10M20 14H10M10 14L13 11M10 14L13 17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            Copy
          </button>
        </div>
      </div>
    `;

    // Position panel near compose window
    const rect = currentComposeView.getBoundingClientRect();
    chithiPanel.style.top = `${rect.top + window.scrollY}px`;
    chithiPanel.style.left = `${rect.right + 20}px`;
    
    document.body.appendChild(chithiPanel);
    
    // Setup event listeners
    setupPanelEventListeners();
  }

  // Setup panel event listeners
  function setupPanelEventListeners() {
    // Mode selector
    document.querySelectorAll('.mode-btn').forEach(btn => {
      btn.addEventListener('click', function() {
        document.querySelectorAll('.mode-btn').forEach(b => b.classList.remove('active'));
        this.classList.add('active');
        
        const mode = this.dataset.mode;
        const processBtn = document.getElementById('chithi-process');
        const generateInput = document.getElementById('generate-input');
        
        if (mode === 'generate') {
          generateInput.style.display = 'block';
          processBtn.querySelector('.btn-text').textContent = 'Generate Email';
        } else {
          generateInput.style.display = 'none';
          processBtn.querySelector('.btn-text').textContent = mode === 'enhance' ? 'Enhance Email' : 'Analyze Email';
        }
      });
    });

    // Process button
    document.getElementById('chithi-process').addEventListener('click', processEmail);

    // Result actions
    document.getElementById('insert-btn')?.addEventListener('click', insertResult);
    document.getElementById('copy-btn')?.addEventListener('click', copyResult);
  }

  // Process email based on selected mode
  async function processEmail() {
    if (isProcessing) return;
    
    const mode = document.querySelector('.mode-btn.active').dataset.mode;
    const tone = document.getElementById('chithi-tone').value;
    const purpose = document.getElementById('chithi-purpose').value;
    
    let content = '';
    let endpoint = '';
    
    if (mode === 'generate') {
      content = document.getElementById('chithi-idea').value;
      if (!content.trim()) {
        showError('Please enter your email idea');
        return;
      }
      endpoint = '/generate';
    } else {
      content = getEmailContent();
      if (!content.trim()) {
        showError('Please write some content first');
        return;
      }
      endpoint = mode === 'enhance' ? '/enhance' : '/analyze';
    }

    isProcessing = true;
    showLoading(true);

    try {
      const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          content: content,
          tone: tone,
          purpose: purpose
        })
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      showResult(data.result || data.content);
    } catch (error) {
      console.error('Chithi Error:', error);
      showError('Failed to process email. Make sure the backend is running.');
    } finally {
      isProcessing = false;
      showLoading(false);
    }
  }

  // Get email content from Gmail compose
  function getEmailContent() {
    const messageBody = currentComposeView.querySelector('[aria-label*="Message Body"]');
    if (messageBody) {
      // Handle both div and iframe editors
      if (messageBody.contentDocument) {
        return messageBody.contentDocument.body.innerText || '';
      }
      return messageBody.innerText || '';
    }
    return '';
  }

  // Insert result into Gmail compose
  function insertResult() {
    const resultContent = document.getElementById('result-content').innerText;
    const messageBody = currentComposeView.querySelector('[aria-label*="Message Body"]');
    
    if (messageBody) {
      if (messageBody.contentDocument) {
        messageBody.contentDocument.body.innerHTML = resultContent.replace(/\n/g, '<br>');
      } else {
        messageBody.innerHTML = resultContent.replace(/\n/g, '<br>');
      }
      
      // Trigger Gmail's change event
      messageBody.dispatchEvent(new Event('input', { bubbles: true }));
    }
    
    toggleChithiPanel();
  }

  // Copy result to clipboard
  function copyResult() {
    const resultContent = document.getElementById('result-content').innerText;
    navigator.clipboard.writeText(resultContent).then(() => {
      const copyBtn = document.getElementById('copy-btn');
      const originalText = copyBtn.innerHTML;
      copyBtn.innerHTML = '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M20 6L9 17L4 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg> Copied!';
      setTimeout(() => {
        copyBtn.innerHTML = originalText;
      }, 2000);
    });
  }

  // Show loading state
  function showLoading(show) {
    const processBtn = document.getElementById('chithi-process');
    const spinner = processBtn.querySelector('.spinner');
    const btnText = processBtn.querySelector('.btn-text');
    
    if (show) {
      spinner.style.display = 'block';
      btnText.style.display = 'none';
      processBtn.disabled = true;
    } else {
      spinner.style.display = 'none';
      btnText.style.display = 'block';
      processBtn.disabled = false;
    }
  }

  // Show result
  function showResult(content) {
    const resultDiv = document.getElementById('chithi-result');
    const resultContent = document.getElementById('result-content');
    
    resultContent.textContent = content;
    resultDiv.style.display = 'block';
  }

  // Show error
  function showError(message) {
    const resultDiv = document.getElementById('chithi-result');
    const resultContent = document.getElementById('result-content');
    
    resultContent.innerHTML = `<div class="error-message">${message}</div>`;
    resultDiv.style.display = 'block';
  }

  // Inject custom styles
  function injectStyles() {
    if (document.querySelector('#chithi-styles')) return;
    
    const style = document.createElement('style');
    style.id = 'chithi-styles';
    style.textContent = `
      @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap');
    `;
    document.head.appendChild(style);
  }

  // Initialize extension
  init();
})();