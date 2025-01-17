document.addEventListener("DOMContentLoaded", function() {
    const loginForm = document.querySelector('.login-form');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    
    const fotgotPass = document.getElementById("forgot-password");
    if(fotgotPass.onclick)
        console.log("Forgot")

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
        const apiUrl = 'http://localhost:8080/admin/login'; 

        fetch(apiUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ username, password }),
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok ' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            console.log('Success');
            localStorage.setItem("token", data["token"])
            localStorage.setItem("username", username);
            console.log(localStorage.getItem("token"));
            window.location.href = data["path"];
        })
        .catch((error) => {
            console.error('Error:', error);
            if(error)
                alert("Unauthorized!");
            if(error == "TypeError: Failed to fetch")
                alert("Server not responding")
        })
        .finally(() => {
            // Will do something here in future
        });
    }
});


