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
const token = localStorage.getItem("token");

spinner.style.display = "block";
transactions.style.display = "none";
// Fetch pending applications
fetch(`${config.BACKEND_API_URL}/staff/get-pending-application`, {
  method: "GET",
  headers: {
    "Content-Type": "application/json",
    Authorization: `Bearer ${token}`,
  },
})
  .then((response) => response.json())
  .then((data) => {
    const tableBody = document.querySelector("tbody");
    tableBody.innerHTML = data
      .map(
        (customer, index) => `
            <tr data-index="${index}">
                <td>${customer.firstName + " " + customer.lastName}</td>
                <td>${customer.dob}</td>
                <td>${customer.email}</td>
                <td>${customer.gender}</td>
                <td>${customer.mobileNumber}</td>
                <td><img id="image" src=${`data:image/png;base64,${customer.image}`} alt="Image"></td>
                <td><img id="card" src=${`data:image/png;base64,${customer.verificationId}`} alt="Card"></td>
                <td><img id="card" src=${`data:image/png;base64,${customer.signatureImage}`} alt="Signature"></td>
                <td class="status">${customer.status}</td>
                <td class="permission-cell">
                    <button class="remove-btn">Reject</button>
                    <button class="allow-btn">Verify</button>
                </td>
            </tr>
        `
      )
      .join("");

    // Add event listeners to buttons
    const rows = document.querySelectorAll("tbody tr");
    rows.forEach((row, index) => {
      row.querySelector(".remove-btn").addEventListener("click", () => {
        data[index].status = "NON_VERIFIED"; // Update status
        row.querySelector(".status").textContent = "NON_VERIFIED"; // Update UI
      });

      row.querySelector(".allow-btn").addEventListener("click", () => {
        data[index].status = "VERIFIED"; // Update status
        row.querySelector(".status").textContent = "VERIFIED"; // Update UI
      });
    });
  })
  .finally(() => {
    spinner.style.display = "none";
    transactions.style.display = "";
  });

const submitButton = document.getElementById("submitChanges");

// Submit updated applications
submitButton.addEventListener("click", () => {
  fetch(`${config.BACKEND_API_URL}/staff/update-applications`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(data),
  })
    .then((response) => {
      window.location.reload();
      if (response.ok) {
        alert("Applications updated successfully!");
      } else {
        alert("Failed to update applications.");
      }
    })
    .catch((error) => {
      console.error("Error updating applications:", error);
    });
});
