const token = localStorage.getItem("token");
const imageOutput = document.querySelector('.profile .avatars img');
            const username = localStorage.getItem("username");
            if(username === "harsh.singh@example.com"){
                imageOutput.src = "harsh.png";
                document.querySelector('.profile p').textContent = `Harsh Singh`;
            }
            if(username === "deepak.joshi@example.com"){
                imageOutput.src = "deepak.png";
                document.querySelector('.profile p').textContent = `Deepak Joshi`;
            }

fetch('http://localhost:8080/staff/all-transactions',{
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
  });
