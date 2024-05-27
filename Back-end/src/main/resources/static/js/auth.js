import * as jose from "https://cdnjs.cloudflare.com/ajax/libs/jose/5.3.0/index.bundle.min.js";

export function saveLogin(jwt) {
  const token = jose.decodeJwt(jwt);
  sessionStorage.setItem("token", jwt);

  console.log("Token salvo com sucesso!");
  if (token) {
    console.log("Token decodificado: ", token);
    if (token.scope.includes("ADMIN")) {
      console.log("Usuário é administrador!");
      window.location.href = "/admin/index.html";
    }
    if (token.scope.includes("MORADOR")) {
      console.log("Usuário é morador!");
      window.location.href = "/morador/index.html";
    }
  }
}

export function revogeToken() {
  sessionStorage.removeItem("token");
}

export function logout() {
  revogeToken();
  window.location.href =
    "/Login/login.html" +
    "?error=401" +
    "mgs=Sessão expirada, faça login novamente";
}

export function getToken() {
  return sessionStorage.getItem("token");
}

export function isLogged() {
  return !!sessionStorage.getItem("token") && !isExpiredToken();
}

export function getPayload() {
  const token = getToken();
  if (!token) {
    window.location.href = "/Login/login.html";
  }
  return jose.decodeJwt(token);
}

export function getRoles() {
  const payload = getPayload();
  if (!payload) return null;
  return payload.roles;
}

export function isExpiredToken() {
  const payload = getPayload();
  if (!payload) return true;
  const dataExpiracao = new Date(payload.exp * 1000);
  console.log("seu tempo de expiração é: " + dataExpiracao);
  return Date.now() >= payload.exp * 1000;
}

export function getUserName() {
  const payload = getPayload();
  if (!payload) return null;
  return payload.sub;
}

export function getScope() {
  const payload = getPayload();
  if (!payload) return null;
  return payload.scope;
}

const pagesAdmin = [
  "/admin/index.html",
  "/admin/gerenciar-reservas.html",
  "/admin/gerenciar-ocorrencias.html",
  "/admin/gerenciar-reservas.html",
  "/admin/gerenciar-moradores.html",
];

const pagesMorador = [
  "/morador/index.html",
  "/morador/perfil/index.html",
  "/morador/mural/index.html",
  "/morador/espaco-agendamento/index.html",
  "/morador/financeiro/index.html",
  "/morador/ocorrencia/index.html",
];

export function validateSecurity() {
  getReferrer();
  if (!isLogged()) {
    window.location.href = "../Login/login.html";
  }
  if (isExpiredToken()) {
    logout();
  }

  if (!isLoggedAdmin()) {
    const page = window.location.pathname;
    console.log("Página atual: ", page);
    if (pagesAdmin.includes(page)) {
      window.location.href = "/morador/index.html?error=403";
      return;
    }
  }
  if (isLoggedAdmin()) {
    const page = window.location.pathname;
    console.log("Página atual: ", page);
    if (pagesMorador.includes(page)) {
      window.location.href = "/admin/index.html?error=403";
      return;
    }
  }
}

// export function reloadToken() {
//   const token = getToken();
//   if (!token) {
//     window.location.href = "http://localhost:8011/Login/Login.html";
//   }
//   if (isExpiredToken()) {
//     revogeToken();
//     return;
//   }
//   saveLogin(token);
// }

// Função para obter a página de referência
export function getReferrer() {
  const referrer = document.referrer;
  console.log("Página de referência: ", referrer);
  return referrer;
}

export function isLoggedAdmin() {
  return isLogged() && getScope().includes("ADMIN");
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
