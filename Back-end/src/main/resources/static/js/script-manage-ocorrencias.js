import zod from "https://cdn.jsdelivr.net/npm/zod@3.23.8/+esm";

import * as token from "/js/auth.js";

const baseUrl = "http://localhost:8010";

async function getMoradores() {
  token.validateSecurity();
  showLoading();

  try {
    const response = await fetch(`${baseUrl}/api/v1/morador/list`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + token.getToken(),
      },
    });

    const data = await response.json();

    if (response.ok) {
      if (data.length === 0) {
        const container = document.getElementById("moradores-container");
        const p = document.createElement("p");
        p.textContent = "Nenhum morador encontrado.";
        p.classList.add("text-center", "text-uppercase", "fw-bold");
        container.innerHTML = "";
        container.appendChild(p);
        return;
      }

      buildTable(data);
    } else {
      showToast("Atenção", "Erro ao buscar os moradores!", "bg-warning", 7000);
    }
  } catch (error) {
    showToast("Erro", "Erro ao buscar os moradores!", "bg-danger", 5000);
  } finally {
    hideLoading();
  }
}

function showLoading() {
  document.getElementById("loading").style.display = "block";
}

function hideLoading() {
  document.getElementById("loading").style.display = "none";
}

function showToast(titulo, message, clss = "bg-primary", time = 5000) {
  const toastContainer = document.getElementById("toastContainer");

  const toastEl = document.createElement("div");
  toastEl.className = `toast align-items-center text-white border-0 ${clss}`;
  toastEl.setAttribute("role", "alert");
  toastEl.setAttribute("aria-live", "assertive");
  toastEl.setAttribute("aria-atomic", "true");
  toastEl.dataset.bsDelay = time;

  toastEl.innerHTML = `
          <div class="toast-header">
            <span class="text-primary"><i class="fa-solid fa-circle-info fa-lg"></i></span>
            <strong class="me-auto">${titulo}</strong>
            <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
          </div>
          <div class="toast-body">
            <span><i class="fa-solid fa-circle-check fa-lg"></i></span>
            <div class="d-flex flex-grow-1 align-items-center">
                    ${message}
                  </div>
          </div>
        `;

  toastContainer.appendChild(toastEl);

  toastEl.scrollIntoView({ behavior: "smooth", block: "end" });

  const toast = new bootstrap.Toast(toastEl);
  toast.show();
}
