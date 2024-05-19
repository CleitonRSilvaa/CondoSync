const baseUrl = "http://localhost:8010";

import * as tokem from "/js/auth.js";

function validateSecurity() {
  if (!tokem.isLogged()) {
    window.location.href = "../Login/login.html";
  }
  if (tokem.isExpiredToken()) {
    alert("Sua sessão expirou!");
    tokem.logout();
  }
}

document.addEventListener("DOMContentLoaded", () => {
  validateSecurity();
  getProfile();
});

function showLoading() {
  document.getElementById("loading").style.display = "block";
}

function hideLoading() {
  document.getElementById("loading").style.display = "none";
}

async function getProfile() {
  showLoading();
  try {
    const token = tokem.getToken();
    const response = await fetch(`${baseUrl}/api/v1/morador/perfil`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    });

    if (response.ok) {
      const data = await response.json();
      document.getElementById("nome").value = data.nomeCompleto;
      document.getElementById("cpf").value = data.cpf;
      document.getElementById("email").value = data.email;
      document.getElementById("celular").value = parseInt(data.celular);
      document.getElementById("apartamento").value = data.apartamento;
      document.getElementById("bloco").value = data.bloco
        ? data.bloco
        : "Não informado";
      document.getElementById("andar").value = data.andar
        ? data.andar
        : "Não informado";
      document.getElementById("torre").value = data.torre
        ? data.torre
        : "Não informado";
      return;
    }

    document.getElementById("nome").value = "";
    document.getElementById("cpf").value = "";
    document.getElementById("email").value = "";
    document.getElementById("telefone").value = "";
    document.getElementById("apartamento").value = "";
    document.getElementById("bloco").value = "";

    showToast("Erro", "Erro ao buscar perfil", "bg-danger");
  } catch (error) {
    console.error("Erro ao buscar perfil", error);
    showToast("Erro", "Erro ao buscar perfil", "bg-danger");
  } finally {
    hideLoading();
  }
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
