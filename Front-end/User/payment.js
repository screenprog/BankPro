import config from '../config.js';

document.getElementById('sender-id').value = localStorage.getItem("accountId");
document.querySelector('.pay').addEventListener('click', () => {  
  const accountIdOfSender = document.getElementById('sender-id').value;
  const accountIdOfReceiver = document.getElementById('receiver-id').value;
  const balance = document.getElementById('payment-account').value;
  const pin = document.getElementById('pin-code').value;

  const paymentData = {
    accountIdOfSender,
    accountIdOfReceiver,
    balance,
    pin,
  };

  const token = localStorage.getItem("token");
  fetch(`${config.BACKEND_API_URL}/user/transfer`, {
    method: 'PUT',
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
      clearForm();
    })
    .catch((error) => {
      console.error('Error:', error);
      alert(error);
    });
});

function clearForm(){
  document.getElementById('receiver-id').value = "";
  document.getElementById('payment-account').value = "";
  document.getElementById('pin-code').value = "";
}