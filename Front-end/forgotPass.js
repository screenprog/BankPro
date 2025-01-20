import config from './config.js';

document.addEventListener("DOMContentLoaded", function() {
    const form = document.querySelector("form");
    
    form.addEventListener("submit", function(event) {
        event.preventDefault(); // Prevent the default form submission

        const email = localStorage.getItem("email"); // Replace with the actual email you want to send
        const otp = document.getElementById("code").value;
        const newPass = document.getElementById("pass").value;
        const confPass = document.getElementById("confirm-pass").value;

        if(!validatePassword(newPass)){
            alert("Please enter a valid password that meets the following requirements:\n\n"
                +"- Password length must be at least 8 characters\n"
                +"- Password must contain at least 1 uppercase letter, 1 lowercase letter, 1 number, and 1 special character");                
            return;    
        }
        if(newPass != confPass){
            alert("Password not matched!");
            return;
        }

        // Send the OTP to the backend
        fetch(`${config.BACKEND_API_URL}/user/forgot-pass-change`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                email: email,
                otp: otp,
                newPass:newPass
            })
        })
        .then(response => {
            if (!response.ok) 
                throw new Error("Server is not connected");
            return response.text();
        })
        .then(() => {
            alert("Password changed successfully");
            window.location.href = "index.html"
            // You can redirect or perform other actions here
        })
        .catch(error => {
            alert("Error: " + error.message);
        });
    });
});

function validatePassword(password) {
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
    return passwordRegex.test(password);
}