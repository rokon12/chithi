// Load saved settings from storage
async function loadSettings() {
  const settings = await chrome.storage.local.get(['tone', 'purpose']);
  
  const toneSelect = document.getElementById('tone-select');
  const purposeSelect = document.getElementById('purpose-select');
  
  if (settings.tone) {
    toneSelect.value = settings.tone;
  }
  if (settings.purpose) {
    purposeSelect.value = settings.purpose;
  }
}

// Save settings to storage
async function saveSettings() {
  const tone = document.getElementById('tone-select').value;
  const purpose = document.getElementById('purpose-select').value;
  
  await chrome.storage.local.set({ tone, purpose });
}

// Check backend status
async function checkBackendStatus() {
  const statusDot = document.getElementById('status-dot');
  const statusText = document.getElementById('status-text');
  
  try {
    const response = await fetch('http://localhost:8080/api/email/health', {
      method: 'GET',
      mode: 'cors'
    });
    
    if (response.ok) {
      statusDot.classList.add('active');
      statusText.textContent = 'Backend connected';
    } else {
      statusDot.classList.remove('active');
      statusText.textContent = 'Backend error';
    }
  } catch (error) {
    statusDot.classList.remove('active');
    statusText.textContent = 'Backend disconnected';
  }
}

// Initialize popup
document.addEventListener('DOMContentLoaded', async () => {
  // Load settings
  await loadSettings();
  
  // Add event listeners for settings changes
  document.getElementById('tone-select').addEventListener('change', saveSettings);
  document.getElementById('purpose-select').addEventListener('change', saveSettings);
  
  // Check backend status
  checkBackendStatus();
  setInterval(checkBackendStatus, 5000);
  
  // Add click handlers for feature cards
  const features = document.querySelectorAll('.feature');
  features.forEach((feature, index) => {
    feature.style.cursor = 'pointer';
    feature.addEventListener('click', () => {
      // Send message to content script
      chrome.tabs.query({active: true, currentWindow: true}, (tabs) => {
        if (tabs[0] && tabs[0].url && tabs[0].url.includes('mail.google.com')) {
          const actions = ['enhance', 'generate', 'analyze'];
          chrome.tabs.sendMessage(tabs[0].id, {
            action: actions[index],
            tone: document.getElementById('tone-select').value,
            purpose: document.getElementById('purpose-select').value
          });
          window.close();
        } else {
          alert('Please open Gmail to use Chithi');
        }
      });
    });
  });
});