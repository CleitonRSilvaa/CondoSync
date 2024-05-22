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

export function validateSecurity() {
  if (!isLogged()) {
    window.location.href = "../Login/login.html";
  }
  if (isExpiredToken()) {
    alert("Sua sessão expirou!");
    logout();
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
