import config from './config.js';

document.addEventListener("DOMContentLoaded", function() {
    const form = document.querySelector("form");
    
    form.addEventListener("submit", function(event) {
        event.preventDefault();

        //TODO: two divs inside of the buttons one is for loading spinner another for the text
        // TODO: replace of visibility (css property) hidden/visible
        const emailInput = document.getElementById("email");
        const email = emailInput.value;
        localStorage.setItem("email", email);

        // Validate email format
        if (!validateEmail(email)) {
            alert("Please enter a valid email address.");
            return;
        }

        // Send the email to the backend
        fetch(`${config.BACKEND_API_URL}/user/forgot-pass-email`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({email: email})
        })
        .then(response => {
            if (!response.ok) 
                throw new Error("Network response was not ok");

            return response.text();
        })
        .then(data => {
            alert(data);
            window.location.href="forgotPass.html"
        })
        .catch(error => {
            console.error("There was a problem with the fetch operation:", error);
            alert("Email not found");
        });
    });

    function validateEmail(email) {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(String(email).toLowerCase());
    }
});
