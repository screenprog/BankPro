import config from './config.js';

document.addEventListener("DOMContentLoaded", function() {
    const form = document.querySelector("form");
    
    form.addEventListener("submit", function(event) {
        event.preventDefault(); // Prevent the default form submission

        const email = localStorage.getItem("email"); 
        const otp = document.getElementById("code").value;

        // Send the OTP to the backend
        fetch(`${config.BACKEND_API_URL}/user/verify-email`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                email: email,
                otp: otp
            })
        })
        .then(response => {
            if (!response.ok) 
                throw new Error("Invalid OTP");

            return response.text();
        })
        .then(() => {
            window.location.href = "Registrationform.html"
        })
        .catch(error => {
            alert("Error: " + error.message);
        });
    });
});
