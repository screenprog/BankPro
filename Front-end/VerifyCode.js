document.addEventListener("DOMContentLoaded", function() {
    const form = document.querySelector("form");
    
    form.addEventListener("submit", function(event) {
        event.preventDefault(); // Prevent the default form submission

        const email = localStorage.getItem("email"); // Replace with the actual email you want to send
        const otp = document.getElementById("code").value;

        // Send the OTP to the backend
        fetch("http://localhost:8080/user/verify-email", {
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
            if (response.ok) {
                return response.text();
            } else {
                throw new Error("Invalid OTP");
            }
        })
        .then(data => {
            alert("OTP verified successfully: " + data);
            window.location.href = "Registrationform.html"
            // You can redirect or perform other actions here
        })
        .catch(error => {
            alert("Error: " + error.message);
        });
    });
});
