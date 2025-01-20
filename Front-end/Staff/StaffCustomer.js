import config from '../config.js';

const token = localStorage.getItem("token");
const imageOutput = document.querySelector('.profile .avatars img');
const spinner = document.getElementById('loading-spinner');
const transactions = document.getElementById('transactions');
const username = localStorage.getItem("username");
if(username === "harsh.singh@example.com"){
  imageOutput.src = "harsh.png";
  document.querySelector('.profile p').textContent = `Harsh Singh`;
}
if(username === "deepak.joshi@example.com"){
  imageOutput.src = "deepak.png";
  document.querySelector('.profile p').textContent = `Deepak Joshi`;
}

spinner.style.display = 'block'
transactions.style.display = 'none'
fetch(`${config.BACKEND_API_URL}/staff/get-all-customers`,{
  method: 'GET',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  },
})
.then(response => {
  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }
  return response.json(); // Parse JSON response
})
.then(data => {
    const tableBody = document.querySelector('tbody');
    tableBody.innerHTML = data.map(customer => `
      <tr>
        <td>${customer.customerID}</td>
        <td>${customer.firstName + " " + customer.lastName}</td>
        <td>${customer.dob}</td>
        <td>${customer.email}</td>
      </tr>
    `).join('');
    // <td class="permission-cell">
    // <button class="remove-btn">Delete</button>
    // </td>
})
.finally(() => {
  spinner.style.display = 'none';
  transactions.style.display = 'flex';
});
