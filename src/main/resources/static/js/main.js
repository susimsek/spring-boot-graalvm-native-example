const responseElement = document.getElementById('apiResponse');
const fetchHelloButton = document.getElementById('fetchHello');
const resetButton = document.getElementById('resetResponse');
const faviconLink = document.querySelector('link[rel="icon"]');
const themeToggle = document.getElementById('themeToggle');

function toggleResetButton() {
  resetButton.disabled = responseElement.textContent.trim() === "";
}

function updateFaviconBasedOnTheme(theme) {
  faviconLink.href = theme === 'dark'
    ? "/favicons/favicon-dark.ico"
    : "/favicons/favicon-light.ico";
}

function applyTheme(theme) {
  const htmlElement = document.documentElement;

  htmlElement.setAttribute('data-theme', theme);

  if (theme === 'dark') {
    htmlElement.classList.add('dark-theme');
  } else {
    htmlElement.classList.remove('dark-theme');
  }

  updateFaviconBasedOnTheme(theme);

  localStorage.setItem('theme', theme);
}

themeToggle.addEventListener('change', () => {
  const theme = themeToggle.checked ? 'dark' : 'light';
  applyTheme(theme);
});

fetchHelloButton.addEventListener('click', () => {
  responseElement.textContent = "Loading...";
  responseElement.className = "alert alert-info d-block";
  toggleResetButton();

  fetch('/api/v1/hello')
    .then(response => response.text())
    .then(data => {
      responseElement.textContent = data;
      responseElement.className = "alert alert-success d-block";
      toggleResetButton();
    })
    .catch(error => {
      responseElement.textContent = `Error: ${error.message}`;
      responseElement.className = "alert alert-danger d-block";
      toggleResetButton();
    });
});

resetButton.addEventListener('click', () => {
  responseElement.textContent = "";
  responseElement.className = "alert d-none";
  toggleResetButton();
});

const savedTheme = localStorage.getItem('theme') || 'light';
if (savedTheme === 'dark') {
  themeToggle.checked = true;
}
applyTheme(savedTheme);

toggleResetButton();
