// Chithi Gmail Integration - Inline Version
(function() {
  'use strict';

  let currentComposeView = null;
  let isProcessing = false;
  let currentSettings = { tone: 'professional', purpose: 'auto-detect' };

  // API Configuration
  const API_BASE_URL = 'http://localhost:8080/api/email';

  // Initialize when Gmail loads
  function init() {
    console.log('Chithi: Initializing inline integration...');
    
    // Load settings from storage
    loadSettings();
    
    // Listen for messages from popup
    chrome.runtime.onMessage.addListener((request, sender, sendResponse) => {
      if (request.action === 'enhance' || request.action === 'generate' || request.action === 'analyze') {
        currentSettings.tone = request.tone || 'professional';
        currentSettings.purpose = request.purpose || 'auto-detect';
        
        // Process the current compose window
        const composeView = findActiveCompose();
        if (composeView) {
          currentComposeView = composeView;
          processEmailAction(request.action);
        }
      }
    });
    
    // Wait for Gmail to fully load
    waitForGmail();
  }

  // Wait for Gmail to load
  function waitForGmail() {
    const checkInterval = setInterval(() => {
      // Check if Gmail is loaded by looking for the compose button
      const composeButton = document.querySelector('[gh="cm"]') || 
                           document.querySelector('.z0 > .L3') ||
                           document.querySelector('.T-I.T-I-KE.L3');
      
      if (composeButton) {
        clearInterval(checkInterval);
        console.log('Chithi: Gmail loaded, setting up observers...');
        setupGmailObserver();
        injectStyles();
        
        // Check for existing compose windows
        checkExistingComposeWindows();
      }
    }, 1000);
  }

  // Check for existing compose windows
  function checkExistingComposeWindows() {
    const composeWindows = document.querySelectorAll('[role="dialog"]');
    composeWindows.forEach(window => {
      if (!window.querySelector('.chithi-toolbar')) {
        injectChithiToolbar(window);
      }
    });
  }

  // Watch for new compose windows
  function setupGmailObserver() {
    const observer = new MutationObserver((mutations) => {
      mutations.forEach((mutation) => {
        mutation.addedNodes.forEach((node) => {
          if (node.nodeType === 1) {
            // Check for compose window
            if (node.matches && node.matches('[role="dialog"]')) {
              setTimeout(() => injectChithiToolbar(node), 500);
            } else if (node.querySelector && node.querySelector('[role="dialog"]')) {
              const dialog = node.querySelector('[role="dialog"]');
              setTimeout(() => injectChithiToolbar(dialog), 500);
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

  // Inject Chithi toolbar into compose window
  function injectChithiToolbar(composeContainer) {
    // Check if already injected
    if (composeContainer.querySelector('.chithi-toolbar') || 
        composeContainer.getAttribute('data-chithi-enabled') === 'true') return;
    
    console.log('Chithi: Injecting toolbar into compose window');
    
    // Find the compose body
    const messageBody = findMessageBody(composeContainer);
    if (!messageBody) {
      console.error('Chithi: Could not find message body');
      return;
    }
    
    // Create container to isolate from Gmail
    const container = document.createElement('div');
    container.style.position = 'relative';
    container.style.zIndex = '10000';
    
    // Create the inline toolbar
    const toolbar = document.createElement('div');
    toolbar.className = 'chithi-toolbar';
    toolbar.innerHTML = `
      <div class="chithi-inline-header">
        <div class="chithi-logo">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M3 8L10.89 13.26C11.2187 13.4793 11.6049 13.5963 12 13.5963C12.3951 13.5963 12.7813 13.4793 13.11 13.26L21 8M5 19H19C19.5304 19 20.0391 18.7893 20.4142 18.4142C20.7893 18.0391 21 17.5304 21 17V7C21 6.46957 20.7893 5.96086 20.4142 5.58579C20.0391 5.21071 19.5304 5 19 5H5C4.46957 5 3.96086 5.21071 3.58579 5.58579C3.21071 5.96086 3 6.46957 3 7V17C3 17.5304 3.21071 18.0391 3.58579 18.4142C3.96086 18.7893 4.46957 19 5 19Z" stroke="#667eea" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <span>Chithi</span>
        </div>
        <div class="chithi-actions">
          <select class="chithi-select chithi-tone" title="Select tone" tabindex="0">
            <option value="professional">Professional</option>
            <option value="casual">Casual</option>
            <option value="friendly">Friendly</option>
            <option value="formal">Formal</option>
            <option value="empathetic">Empathetic</option>
            <option value="direct">Direct</option>
          </select>
          <select class="chithi-select chithi-purpose" title="Select purpose" tabindex="0">
            <option value="auto-detect">Auto-detect</option>
            <option value="general">General</option>
            <option value="request">Request</option>
            <option value="follow-up">Follow-up</option>
            <option value="thank-you">Thank You</option>
            <option value="apology">Apology</option>
          </select>
          <button class="chithi-btn chithi-enhance" data-action="enhance" title="Enhance your email">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            Enhance
          </button>
          <button class="chithi-btn chithi-analyze" data-action="analyze" title="Analyze your email">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M9 11L12 14L22 4M21 12V19C21 19.5304 20.7893 20.0391 20.4142 20.4142C20.0391 20.7893 19.5304 21 19 21H5C4.46957 21 3.96086 20.7893 3.58579 20.4142C3.21071 20.0391 3 19.5304 3 19V5C3 4.46957 3.21071 3.96086 3.58579 3.58579C3.96086 3.21071 4.46957 3 5 3H16" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            Analyze
          </button>
          <button class="chithi-btn chithi-close" title="Close Chithi">×</button>
        </div>
      </div>
      <div class="chithi-result" style="display: none;">
        <div class="chithi-result-content"></div>
        <div class="chithi-result-actions">
          <button class="chithi-btn-small chithi-apply">Apply</button>
          <button class="chithi-btn-small chithi-copy">Copy</button>
          <button class="chithi-btn-small chithi-dismiss">Dismiss</button>
        </div>
      </div>
      <div class="chithi-loading" style="display: none;">
        <div class="chithi-spinner"></div>
        <span>Processing...</span>
      </div>
    `;
    
    // Add toolbar to container
    container.appendChild(toolbar);
    
    // Insert container above the message body
    messageBody.parentElement.insertBefore(container, messageBody);
    
    // Store reference to compose container
    currentComposeView = composeContainer;
    
    // Mark the compose container as having Chithi
    composeContainer.setAttribute('data-chithi-enabled', 'true');
    
    // Setup event listeners
    setupToolbarListeners(toolbar, composeContainer);
    
    // Load saved settings
    const toneSelect = toolbar.querySelector('.chithi-tone');
    const purposeSelect = toolbar.querySelector('.chithi-purpose');
    if (toneSelect) toneSelect.value = currentSettings.tone;
    if (purposeSelect) purposeSelect.value = currentSettings.purpose;
    
    // Ensure dropdowns work by preventing Gmail interference
    [toneSelect, purposeSelect].forEach(select => {
      if (select) {
        // Prevent all event bubbling
        ['mousedown', 'mouseup', 'click', 'focus', 'blur', 'change'].forEach(eventType => {
          select.addEventListener(eventType, (e) => {
            e.stopPropagation();
            e.stopImmediatePropagation();
          });
        });
        
        // Special handling for mousedown to prevent Gmail's blur behavior
        select.addEventListener('mousedown', (e) => {
          e.preventDefault();
          select.focus();
          
          // Delay the dropdown opening slightly
          setTimeout(() => {
            const event = new MouseEvent('mousedown', {
              view: window,
              bubbles: false,
              cancelable: true
            });
            select.dispatchEvent(event);
          }, 10);
        }, true);
      }
    });
  }

  // Setup toolbar event listeners
  function setupToolbarListeners(toolbar, composeContainer) {
    // Prevent toolbar from closing on clicks
    toolbar.addEventListener('click', (e) => {
      e.stopPropagation();
    });
    
    // Prevent dropdowns from losing focus
    const selects = toolbar.querySelectorAll('select');
    selects.forEach(select => {
      // Use capture phase to intercept events before Gmail
      select.addEventListener('mousedown', (e) => {
        e.stopPropagation();
        e.stopImmediatePropagation();
      }, true);
      
      select.addEventListener('click', (e) => {
        e.stopPropagation();
        e.stopImmediatePropagation();
      }, true);
      
      // Prevent blur when clicking on options
      select.addEventListener('blur', (e) => {
        if (select.matches(':active')) {
          e.preventDefault();
          e.stopPropagation();
          select.focus();
        }
      }, true);
    });
    
    // Enhance button
    toolbar.querySelector('.chithi-enhance').addEventListener('click', (e) => {
      e.stopPropagation();
      currentComposeView = composeContainer;
      processEmailAction('enhance');
    });
    
    // Analyze button
    toolbar.querySelector('.chithi-analyze').addEventListener('click', (e) => {
      e.stopPropagation();
      currentComposeView = composeContainer;
      processEmailAction('analyze');
    });
    
    // Close button
    toolbar.querySelector('.chithi-close').addEventListener('click', (e) => {
      e.stopPropagation();
      toolbar.remove();
    });
    
    // Apply button
    toolbar.querySelector('.chithi-apply').addEventListener('click', (e) => {
      e.stopPropagation();
      applyResult(toolbar);
    });
    
    // Copy button
    toolbar.querySelector('.chithi-copy').addEventListener('click', (e) => {
      e.stopPropagation();
      copyResult(toolbar);
    });
    
    // Dismiss button
    toolbar.querySelector('.chithi-dismiss').addEventListener('click', (e) => {
      e.stopPropagation();
      hideResult(toolbar);
    });
    
    // Update settings when changed
    toolbar.querySelector('.chithi-tone').addEventListener('change', (e) => {
      e.stopPropagation();
      currentSettings.tone = e.target.value;
      saveSettings();
    });
    
    toolbar.querySelector('.chithi-purpose').addEventListener('change', (e) => {
      e.stopPropagation();
      currentSettings.purpose = e.target.value;
      saveSettings();
    });
  }

  // Process email action
  async function processEmailAction(action) {
    if (isProcessing) return;
    
    const toolbar = currentComposeView.querySelector('.chithi-toolbar');
    if (!toolbar) return;
    
    const tone = toolbar.querySelector('.chithi-tone').value;
    const purpose = toolbar.querySelector('.chithi-purpose').value;
    
    const content = getEmailContent();
    if (!content.trim() && action !== 'generate') {
      showError(toolbar, 'Please write some content first');
      return;
    }
    
    isProcessing = true;
    showLoading(toolbar, true);
    hideResult(toolbar);
    
    try {
      const endpoint = action === 'enhance' ? '/enhance' : '/analyze';
      const requestBody = action === 'analyze' ? 
        { content: content } : 
        { content: content, tone: tone, purpose: purpose };
      
      const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(requestBody)
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      
      if (action === 'analyze') {
        showAnalysisResult(toolbar, data);
      } else {
        showEnhancedResult(toolbar, data.result || data.content);
      }
    } catch (error) {
      console.error('Chithi Error:', error);
      showError(toolbar, 'Failed to process email. Make sure the backend is running.');
    } finally {
      isProcessing = false;
      showLoading(toolbar, false);
    }
  }

  // Get email content
  function getEmailContent() {
    if (!currentComposeView) return '';
    
    const messageBody = findMessageBody(currentComposeView);
    if (!messageBody) return '';
    
    return messageBody.innerText || messageBody.textContent || '';
  }

  // Find message body in compose window
  function findMessageBody(container) {
    // Try multiple selectors
    return container.querySelector('div[contenteditable="true"][role="textbox"]') ||
           container.querySelector('div[contenteditable="true"][g_editable="true"]') ||
           container.querySelector('div[contenteditable="true"]') ||
           container.querySelector('.editable[contenteditable="true"]');
  }

  // Find active compose window
  function findActiveCompose() {
    // Look for the focused compose window
    const allDialogs = document.querySelectorAll('[role="dialog"]');
    for (const dialog of allDialogs) {
      if (dialog.querySelector('div[contenteditable="true"]')) {
        return dialog;
      }
    }
    return null;
  }

  // Show enhanced result
  function showEnhancedResult(toolbar, content) {
    const resultDiv = toolbar.querySelector('.chithi-result');
    const resultContent = toolbar.querySelector('.chithi-result-content');
    
    resultContent.textContent = content;
    resultDiv.style.display = 'block';
  }

  // Show analysis result
  function showAnalysisResult(toolbar, analysis) {
    const resultDiv = toolbar.querySelector('.chithi-result');
    const resultContent = toolbar.querySelector('.chithi-result-content');
    
    let html = '<div class="chithi-analysis">';
    
    if (analysis.score !== undefined) {
      html += `<div class="chithi-score">Score: <strong>${analysis.score}/10</strong></div>`;
    }
    
    if (analysis.feedback) {
      html += `<div class="chithi-feedback">${analysis.feedback}</div>`;
    }
    
    if (analysis.suggestions && analysis.suggestions.length > 0) {
      html += '<div class="chithi-suggestions"><strong>Suggestions:</strong><ul>';
      analysis.suggestions.forEach(suggestion => {
        html += `<li>${suggestion}</li>`;
      });
      html += '</ul></div>';
    }
    
    html += '</div>';
    
    resultContent.innerHTML = html;
    resultDiv.style.display = 'block';
    
    // Hide apply button for analysis
    toolbar.querySelector('.chithi-apply').style.display = 'none';
  }

  // Apply result to email
  function applyResult(toolbar) {
    const resultContent = toolbar.querySelector('.chithi-result-content').textContent;
    const messageBody = findMessageBody(currentComposeView);
    
    if (messageBody && resultContent) {
      // Clear and set new content
      messageBody.focus();
      document.execCommand('selectAll', false, null);
      document.execCommand('delete', false, null);
      
      // Insert new content line by line
      const lines = resultContent.split('\n');
      lines.forEach((line, index) => {
        document.execCommand('insertText', false, line);
        if (index < lines.length - 1) {
          document.execCommand('insertLineBreak', false, null);
        }
      });
      
      // Trigger Gmail's change events
      messageBody.dispatchEvent(new Event('input', { bubbles: true }));
      messageBody.dispatchEvent(new Event('change', { bubbles: true }));
      
      hideResult(toolbar);
    }
  }

  // Copy result to clipboard
  function copyResult(toolbar) {
    const resultContent = toolbar.querySelector('.chithi-result-content').textContent;
    navigator.clipboard.writeText(resultContent).then(() => {
      const copyBtn = toolbar.querySelector('.chithi-copy');
      const originalText = copyBtn.textContent;
      copyBtn.textContent = 'Copied!';
      setTimeout(() => {
        copyBtn.textContent = originalText;
      }, 2000);
    });
  }

  // Hide result
  function hideResult(toolbar) {
    const resultDiv = toolbar.querySelector('.chithi-result');
    resultDiv.style.display = 'none';
    
    // Show apply button again
    toolbar.querySelector('.chithi-apply').style.display = 'inline-block';
  }

  // Show loading
  function showLoading(toolbar, show) {
    const loadingDiv = toolbar.querySelector('.chithi-loading');
    loadingDiv.style.display = show ? 'flex' : 'none';
  }

  // Show error
  function showError(toolbar, message) {
    const resultDiv = toolbar.querySelector('.chithi-result');
    const resultContent = toolbar.querySelector('.chithi-result-content');
    
    resultContent.innerHTML = `<div class="chithi-error">${message}</div>`;
    resultDiv.style.display = 'block';
    
    // Hide apply button for errors
    toolbar.querySelector('.chithi-apply').style.display = 'none';
  }

  // Load settings from Chrome storage
  async function loadSettings() {
    try {
      const settings = await chrome.storage.local.get(['tone', 'purpose']);
      if (settings.tone) currentSettings.tone = settings.tone;
      if (settings.purpose) currentSettings.purpose = settings.purpose;
    } catch (error) {
      console.error('Error loading settings:', error);
    }
  }

  // Save settings to Chrome storage
  async function saveSettings() {
    try {
      await chrome.storage.local.set({
        tone: currentSettings.tone,
        purpose: currentSettings.purpose
      });
    } catch (error) {
      console.error('Error saving settings:', error);
    }
  }

  // Inject custom styles
  function injectStyles() {
    if (document.querySelector('#chithi-inline-styles')) return;
    
    const style = document.createElement('style');
    style.id = 'chithi-inline-styles';
    style.textContent = `
      /* Chithi Inline Toolbar Styles */
      .chithi-toolbar {
        background: #f8f9fa;
        border: 1px solid #e0e0e0;
        border-radius: 8px;
        margin-bottom: 8px;
        padding: 8px;
        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
        position: relative;
        z-index: 10000;
      }
      
      .chithi-inline-header {
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 12px;
      }
      
      .chithi-logo {
        display: flex;
        align-items: center;
        gap: 6px;
        color: #667eea;
        font-size: 14px;
        font-weight: 600;
      }
      
      .chithi-actions {
        display: flex;
        align-items: center;
        gap: 8px;
      }
      
      .chithi-select {
        padding: 4px 8px;
        border: 1px solid #d0d0d0;
        border-radius: 4px;
        font-size: 12px;
        background: white;
        cursor: pointer;
        max-width: 120px;
        position: relative;
        z-index: 10001;
        /* Ensure dropdown is clickable */
        user-select: none;
        -webkit-user-select: none;
        -moz-user-select: none;
      }
      
      /* Fix for dropdown options */
      .chithi-select option {
        background: white;
        color: #333;
        padding: 4px;
      }
      
      .chithi-select:hover {
        border-color: #667eea;
      }
      
      .chithi-select:focus {
        outline: none;
        border-color: #667eea;
        box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
      }
      
      .chithi-btn {
        padding: 4px 12px;
        border: 1px solid #d0d0d0;
        background: white;
        border-radius: 4px;
        font-size: 12px;
        cursor: pointer;
        display: flex;
        align-items: center;
        gap: 4px;
        transition: all 0.2s;
      }
      
      .chithi-btn:hover {
        background: #667eea;
        color: white;
        border-color: #667eea;
      }
      
      .chithi-btn.chithi-close {
        padding: 4px 8px;
        font-size: 16px;
        line-height: 1;
      }
      
      .chithi-result {
        margin-top: 8px;
        padding: 12px;
        background: white;
        border: 1px solid #e0e0e0;
        border-radius: 6px;
      }
      
      .chithi-result-content {
        font-size: 13px;
        line-height: 1.6;
        color: #333;
        max-height: 200px;
        overflow-y: auto;
        white-space: pre-wrap;
      }
      
      .chithi-result-actions {
        margin-top: 8px;
        display: flex;
        gap: 8px;
      }
      
      .chithi-btn-small {
        padding: 4px 12px;
        border: 1px solid #d0d0d0;
        background: white;
        border-radius: 4px;
        font-size: 12px;
        cursor: pointer;
        transition: all 0.2s;
      }
      
      .chithi-btn-small:hover {
        background: #f0f0f0;
      }
      
      .chithi-btn-small.chithi-apply {
        background: #667eea;
        color: white;
        border-color: #667eea;
      }
      
      .chithi-btn-small.chithi-apply:hover {
        background: #5a6fd8;
      }
      
      .chithi-loading {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 12px;
        color: #666;
        font-size: 13px;
      }
      
      .chithi-spinner {
        width: 16px;
        height: 16px;
        border: 2px solid rgba(102, 126, 234, 0.3);
        border-top-color: #667eea;
        border-radius: 50%;
        animation: chithi-spin 0.8s linear infinite;
      }
      
      @keyframes chithi-spin {
        to { transform: rotate(360deg); }
      }
      
      .chithi-error {
        color: #d93025;
        font-size: 13px;
      }
      
      .chithi-analysis {
        font-size: 13px;
      }
      
      .chithi-score {
        margin-bottom: 8px;
        color: #667eea;
      }
      
      .chithi-feedback {
        margin-bottom: 8px;
        line-height: 1.5;
      }
      
      .chithi-suggestions ul {
        margin: 4px 0 0 20px;
        padding: 0;
      }
      
      .chithi-suggestions li {
        margin-bottom: 4px;
      }
    `;
    document.head.appendChild(style);
  }

  // Initialize
  init();
})();