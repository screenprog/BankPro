import config from '../config.js';

document.addEventListener("DOMContentLoaded", function() {
  const form = document.querySelector("form");
  const customerIdInput = document.getElementById("customer-id");
  const accountTypeSelect = document.getElementById("account-type");
  const initialAmountInput = document.getElementById("initial-amount");
  const pinInput = document.getElementById("pin");
  const confirmPinInput = document.getElementById("confirm-pin");

  customerIdInput.value = localStorage.getItem("username");
  initialAmountInput.value = 0; 

  form.addEventListener("submit", function(event) {
      event.preventDefault(); 

      const accountType = accountTypeSelect.value;
      const pin = pinInput.value;
      const confirmPin = confirmPinInput.value;

      if (accountType === "Select Account Type") {
          alert("Please select an account type.");
          return;
      }

      if (pin.length < 4) {
          alert("PIN must be at least 4 characters long.");
          return;
      }

      if (pin !== confirmPin) {
          alert("PINs do not match. Please try again.");
          return;
      }

      // If validation passes, create an object to hold the form data
      const formData = {
          customerId: customerIdInput.value,
          type: accountType,
          balance: initialAmountInput.value,
          status: "ACTIVE",
          pin: pin
      };
      
      const token = localStorage.getItem("token");
      // Send the data to the backend
      fetch(`${config.BACKEND_API_URL}/user/open-account`, { 
          method: "POST",
          headers: {
              "Content-Type": "application/json",
              "Authorization": `Bearer ${token}` 
          },
          body: JSON.stringify(formData)
      })
      .then(response => {
          if (!response.ok) {
              throw new Error("Network response was not ok", response);
          }
          return response.json(); 
      })
      .then(() => {
          alert("Account added successfully!");
          form.reset(); 
          window.location.href = "Accountdetail.html"
      })
      .catch(error => {
          console.error("Error:", error);
          alert("There was a problem adding the account. Please try again.");
      });
  });
});
