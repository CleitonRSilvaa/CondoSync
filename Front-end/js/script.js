import * as token from "/js/auth.js";

document.addEventListener("DOMContentLoaded", () => {
  token.validateSecurity();

  let paramUrl = new URLSearchParams(window.location.search);
  if (paramUrl.has("error")) {
    showToast(
      "Acesso negado",
      "Você não tem permissão para acessar esta página!",
      "bg-danger",
      10000
    );

    // Remover o parâmetro "error" da URL
    paramUrl.delete("error");
    const newUrl = `${window.location.pathname}?${paramUrl.toString()}`;
    window.history.replaceState({}, document.title, newUrl);
  }
});

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
document.getElementById("btn-logout").addEventListener("click", token.logout);
