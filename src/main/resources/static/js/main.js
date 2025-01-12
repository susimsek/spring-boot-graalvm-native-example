const responseElement = document.getElementById('apiResponse');
const fetchHelloButton = document.getElementById('fetchHello');
const resetButton = document.getElementById('resetResponse');
const faviconLink = document.querySelector('link[rel="icon"]');

function toggleResetButton() {
  resetButton.disabled = responseElement.textContent.trim() === "";
}

function updateFaviconBasedOnTheme(event) {
  if (event.matches) {
    faviconLink.href = "/favicons/favicon-dark.ico";
  } else {
    faviconLink.href = "/favicons/favicon-light.ico";
  }
}

function setInitialFavicon() {
  const isDarkMode = window.matchMedia("(prefers-color-scheme: dark)").matches;
  if (isDarkMode) {
    faviconLink.href = "/favicons/favicon-dark.ico";
  } else {
    faviconLink.href = "/favicons/favicon-light.ico";
  }
}

const darkModeMediaQuery = window.matchMedia("(prefers-color-scheme: dark)");
darkModeMediaQuery.addEventListener('change', updateFaviconBasedOnTheme);



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

setInitialFavicon();
toggleResetButton();
