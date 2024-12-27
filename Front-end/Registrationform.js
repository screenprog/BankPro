document.addEventListener("DOMContentLoaded", function() {
    const form = document.querySelector("form");
    const registerButton = document.querySelector('.register-button');
    
    const emailElement = document.getElementById("email");
    emailElement.value = localStorage.getItem("email");
    // Disable the button initially
    
    form.addEventListener("submit", function(event) {
        event.preventDefault(); // Prevent the default form submission

        // Gather form data
        const firstName = document.getElementById("first-name").value;
        const lastName = document.getElementById("last-name").value;
        const email = document.getElementById("email").value;
        const phone = document.getElementById("phone").value;
        const dob = document.getElementById("dob").value;
        const gender = document.getElementById("gender").value;
        const address = document.getElementById("address").value;
        const password = document.getElementById("password").value;
        const confirmPassword = document.getElementById("confirm-password").value;

        // Check if passwords match
        if (password !== confirmPassword) {
            alert("Passwords do not match.");
            return; // Stop the submission process
        }
        if(!validatePhoneNumber(phone)){
            alert("Phone number is not valid")
            return;
        }
        if(!validatePassword(password)){
            alert("Please enter a valid password that meets the following requirements:\n\n"
                +"- Password length must be at least 8 characters\n"
                +"- Password must contain at least 1 uppercase letter, 1 lowercase letter, 1 number, and 1 special character");                
            return;    
        }

        // Prepare data to send to the backend
        const formData = new FormData();
        formData.append("firstName", firstName);
        formData.append("lastName", lastName);
        formData.append("email", email);
        formData.append("phone", phone);
        formData.append("dob", dob);
        formData.append("gender", gender);
        formData.append("address", address);
        formData.append("password", password); // Only send the password if you want to store it

        // Get file inputs
        const signatureFile = document.getElementById("upload1").files[0];
        const passportFile = document.getElementById("upload2").files[0];
        const identityFile = document.getElementById("upload3").files[0];

        if(!signatureFile || !passportFile || !identityFile) {
            alert(`Upload your document images by clicking on the 'Upload ----'`)
            return;
        }
        // Append files to FormData
        if (signatureFile) {
            formData.append("signature", signatureFile);
        }
        if (passportFile) {
            formData.append("image", passportFile);
        }
        if (identityFile) {
            formData.append("card", identityFile);
        }
    

        registerButton.disabled = true;
        // Send data to the backend
        fetch("http://localhost:8080/user/register", {
            method: "POST",
            body: formData // Send FormData directly
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("Network response was not ok");
            }
            return response.json();
        })
        .then(data => {
            registerButton.disabled = false;
            console.log("Success:", data["message"]);
            window.location.href = "Completeregistration.html"
            // Handle success (e.g., show a success message, redirect, etc.)
        })
        .catch((error) => {
            registerButton.disabled = false;
            console.error("Error:", error);
            // Handle error (e.g., show an error message)
        });
    });
});

function validatePassword(password) {
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
    return passwordRegex.test(password);
}
function validatePhoneNumber(phoneNumber) {
    const phoneRegex = /^(\+\d{1,2}\s?)?(\d{3})?[\s.-]?\d{3}[\s.-]?\d{4}$/;
    return phoneRegex.test(phoneNumber);
  }
  