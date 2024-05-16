import * as jose from "https://cdnjs.cloudflare.com/ajax/libs/jose/5.3.0/index.bundle.min.js";

export function saveLogin(jwt) {
  const token = jose.decodeJwt(jwt);
  console.log(token);
  localStorage.setItem("token", jwt);
}

export function revogeToken() {
  localStorage.removeItem("token");
  Window.location.href = "http://localhost:8011/Login/Login.html";
}

export function logout() {
  revogeToken();
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
    Window.location.href = "http://localhost:8011/Login/Login.html";
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
  return Date.now() >= payload.exp * 1000;
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
