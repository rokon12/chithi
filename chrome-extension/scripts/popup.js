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

// Check status on load and every 5 seconds
checkBackendStatus();
setInterval(checkBackendStatus, 5000);