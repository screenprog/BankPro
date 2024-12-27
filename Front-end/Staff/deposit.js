document.querySelector('.pay').addEventListener('click', () => {  
  // const accountIdOfSender = document.getElementById('sender-id').value;
  const accountIdOfReceiver = document.getElementById('receiver-id').value;
  const balance = document.getElementById('payment-account').value;
  // const pin = document.getElementById('pin-code').value;

  const paymentData = {
    // accountIdOfSender,
    accountIdOfReceiver,
    balance,
    // pin,
  };

  const token = localStorage.getItem("token");
  fetch('http://localhost:8080/staff/deposit', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify(paymentData),
  })
    .then((response) => {
      if (response.ok) {
        return response.text();
      } else {
        return response.text().then((errorMessage) => {
            throw new Error(errorMessage);
          });
      }
    })
    .then((data) => {
      alert(data);
      console.log(data);
      document.getElementById('receiver-id').value = "";
      document.getElementById('payment-account').value = "";
    })
    .catch((error) => {
      console.error('Error:', error);
      alert(error);
    });
});

