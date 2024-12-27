function fetchAccountDetails() {
    // Send a GET request to the backend API to retrieve the account data
    const token = localStorage.getItem("token");
    const username = localStorage.getItem("username");
    fetch(`http://localhost:8080/user/dashboard?id=${username}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    })
    .then(response => response.json())
    .then(data => {
        const imageOutput = document.querySelector('.content-header .avatars .image');
        localStorage.setItem("image", data.image);
        imageOutput.src = `data:image/png;base64,${data.image}`;

        localStorage.setItem("accountId", data.account[0].accountNumber);
        localStorage.setItem("name", data.firstName + " " + data.lastName)

        console.log(data);
        document.querySelector('.profile p').textContent = `${data.firstName + " " + data.lastName}`;
        document.querySelector('.account-info table').innerHTML = `
            <tr><th>Customer ID:</th><td>${data.customerID}</td></tr>
            <tr><th>Account Number:</th><td>${localStorage.getItem("accountId")}</td></tr>
            <tr><th>Balance:</th><td>Rs. ${data.account[0].balance}</td></tr>
            <tr><th>Account Type:</th><td>${data.account[0].type}</td></tr>
            <tr><th>Status:</th><td>${data.account[0].status}</td></tr>
            <tr><th>Owner:</th><td>${data.firstName + " " + data.lastName}</td></tr>
            <tr><th>Date Opened:</th><td>${data.account[0].openDate}</td></tr>
            <tr><th>Email:</th><td>${data.email}</td></tr>
            <tr><th>Phone Number:</th><td>${data.phoneNumber}</td></tr>
            <tr><th>Address:</th><td>${data.address}</td></tr>
        `;
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Failed to load account details');
    });
}

window.onload = fetchAccountDetails;
