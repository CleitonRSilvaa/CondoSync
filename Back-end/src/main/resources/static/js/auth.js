import * as jose from "https://cdnjs.cloudflare.com/ajax/libs/jose/5.3.0/index.bundle.min.js";

export function saveLogin(jwt) {
  const token = jose.decodeJwt(jwt);
  sessionStorage.setItem("token", jwt);
  if (token) {
    if (token.scope.includes("ADMIN")) {
      if (token.passwordExpiration === true) {
        console.log("entrou");
        window.location.href = "/admin/alterar-senha.html";
        return;
      }
      window.location.href = "/admin/index.html";
    }
    if (token.scope.includes("MORADOR")) {
      if (token.passwordExpiration === true) {
        console.log("entrou");
        window.location.href = "/morador/alterar-senha.html";
        return;
      }
      window.location.href = "/morador/index.html";
    }
  }
}

function passwordExpiration() {
  const payload = getPayload();
  if (!payload) {
    logout();
    return;
  }
  if (payload.passwordExpiration) {
    if (payload.scope.includes("ADMIN")) {
      window.location.href = "/admin/alterar-senha.html";
      return;
    }
    if (payload.scope.includes("MORADOR")) {
      window.location.href = "/morador/alterar-senha.html";
      return;
    }
  }
}

export function revogeToken() {
  sessionStorage.removeItem("token");
}

export function logout() {
  revogeToken();
  window.location.href = "/Login/login.html";
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
  "/admin/gerenciar-mural.html",
  "/admin/gerenciar-usuarios.html",
  "/admin/alterar-senha.html",
  "/admin/mural.html",
];

const pagesMorador = [
  "/morador/index.html",
  "/morador/perfil/index.html",
  "/morador/mural/index.html",
  "/morador/espaco-agendamento/index.html",
  "/morador/financeiro/index.html",
  "/morador/ocorrencia/index.html",
  "/morador/alterar-senha.html",
];

export function validateSecurity() {
  getReferrer();

  if (!window.location.pathname.includes("alterar-senha.html")) {
    passwordExpiration();
  }

  if (isExpiredToken()) {
    window.location.href =
      "/Login/login.html" +
      "?error=401&" +
      "mgs=Sessão expirada, faça login novamente";
    return;
  }

  if (!isLogged()) {
    window.location.href = "../Login/login.html";
  }

  if (!isLoggedAdmin()) {
    const page = window.location.pathname;
    if (pagesAdmin.includes(page)) {
      window.location.href = "/morador/index.html?error=403";
      return;
    }
  }
  if (isLoggedAdmin()) {
    const page = window.location.pathname;
    if (pagesMorador.includes(page)) {
      window.location.href = "/admin/index.html?error=403";
      return;
    }
  }
}

// Função para obter a página de referência
export function getReferrer() {
  const referrer = document.referrer;
  return referrer;
}

export function isLoggedAdmin() {
  return isLogged() && getScope().includes("ADMIN");
}

export function getUserId() {
  const payload = getPayload();
  if (!payload) return null;
  return payload.id;
}

export function getUser() {
  const payload = getPayload();
  if (!payload) return null;
  const [firstName, ...rest] = payload.name.split(" ");
  const lastName = rest.length > 0 ? rest[rest.length - 1] : "";
  return {
    id: payload.id,
    nome: firstName + " " + lastName,
    email: payload.sub,
    image: payload.image ?? null,
  };
}

export function getNome() {
  const payload = getPayload();
  if (!payload) return null;
  return payload.name;
}
