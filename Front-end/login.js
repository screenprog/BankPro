import config from './config.js';

document.addEventListener("DOMContentLoaded", function() {
    const loginForm = document.querySelector('.login-form');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    const loadingSpinner = document.getElementById('loading-spinner');
    const container = document.getElementById('container');

    loginForm.addEventListener('submit', function(event) {
        event.preventDefault(); // Prevent the default form submission

        const username = emailInput.value;
        const password = passwordInput.value;

        if (validatePassword(password)) {
            sendLoginData(username, password);
        } else {
            alert('Please enter valid id/email and password.');
        }
    });

    function validatePassword(password) {
        return password.length >= 6;
    }


    function sendLoginData(username, password) {
        loadingSpinner.style.display = 'block';
        container.style.display = 'none';
        const apiUrl = `${config.BACKEND_API_URL}/admin/login`; 

        fetch(apiUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ username, password }),
        })
        .then(response => {
            if (!response.ok) {
                if(response.status === 403)
                    throw new Error('403');
                throw new Error('Network response was not ok ' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            localStorage.setItem("token", data["token"])
            localStorage.setItem("username", username);
            window.location.href = data["path"];
        })
        .catch((error) => {
            console.error('Error:', error);
            if(error == "TypeError: Failed to fetch")
                alert("Server is sleeping.");
            else if (error === '403')
                alert('Error: CORS header ‘Access-Control-Allow-Origin’ missing');
            else
                alert("Unauthorized!");
        })
        .finally(() => {
            container.style.display = 'flex';
            loadingSpinner.style.display = 'none';
            emailInput.value = "";
            passwordInput.value = "";
        });
    }
});


