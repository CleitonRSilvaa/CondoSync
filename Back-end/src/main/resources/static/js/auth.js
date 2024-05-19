import * as jose from "https://cdnjs.cloudflare.com/ajax/libs/jose/5.3.0/index.bundle.min.js";

export function saveLogin(jwt) {
  const token = jose.decodeJwt(jwt);
  console.log(token);
  localStorage.setItem("token", jwt);
}

export function revogeToken() {
  localStorage.removeItem("token");
}

export function logout() {
  revogeToken();
  window.location.href = "/Login/login.html";
}

export function getToken() {
  return localStorage.getItem("token");
}

export function isLogged() {
  return !!localStorage.getItem("token");
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
