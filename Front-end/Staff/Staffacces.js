import config from "../config.js";

const imageOutput = document.querySelector(".profile .avatars img");
const username = localStorage.getItem("username");
const spinner = document.getElementById("loading-spinner");
const transactions = document.getElementById("transactions");
if (username === "harsh.singh@example.com") {
  imageOutput.src = "harsh.png";
  document.querySelector(".profile p").textContent = `Harsh Singh`;
}
if (username === "deepak.joshi@example.com") {
  imageOutput.src = "deepak.png";
  document.querySelector(".profile p").textContent = `Deepak Joshi`;
}

document.addEventListener("DOMContentLoaded", () => {
  const tbody = document.querySelector("table tbody");
  // if(localStorage.getItem("accountDetails") == null)
  //     console.log("Nulldfhkjshfkahsdfhska");
  spinner.style.display = "block";
  transactions.style.display = "none";
  const token = localStorage.getItem("token");
  // Fetch data from the backend API
  fetch(`${config.BACKEND_API_URL}/staff/get-all-accounts`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return response.json(); // Parse JSON response
    })
    .then((data) => {
      // Clear existing rows
      tbody.innerHTML = "";

      // Populating the table with fetched data
      data.forEach((account) => {
        const row = document.createElement("tr");
        row.innerHTML = `
                    <td>${account.accountNumber}</td>
                    <td>Rs. ${account.balance}</td>
                    <td>${account.openDate}</td>
                    <td class="status-cell ${account.status.toLowerCase()}">${
          account.status
        }</td>
                `;
        tbody.appendChild(row);
      });
    })
    .catch((error) => {
      console.error("Error fetching account data:", error);
      tbody.innerHTML =
        '<tr><td colspan="5">Failed to load data. Please try again later.</td></tr>';
    })
    .finally(() => {
      spinner.style.display = "none";
      transactions.style.display = "flex";
    });
});
