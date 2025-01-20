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

spinner.style.display = 'block';
transactions.style.display = 'none';
fetch(`${config.BACKEND_API_URL}/staff/all-transactions`,{
    method: 'GET',
    headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    },
  })
  .then(response => response.json())
  .then(data => {
    const tableBody = document.querySelector('tbody');
    data.reverse();
    tableBody.innerHTML = data.map(transaction => `
      <tr>
        <td>${transaction.description}</td>
        <td>Rs. ${transaction.amount}</td>
        <td>${transaction.transactionDate}</td>
        <td>Rs. ${transaction.balanceLeft}</td>
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
