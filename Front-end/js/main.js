import * as token from "/js/auth.js";

document.addEventListener("DOMContentLoaded", () => {
  if (token.isLogged()) {
    if (token.getScope().includes("ADMIN")) {
      window.location.href = "/admin/index.html";
    } else if (token.getScope().includes("MORADOR")) {
      window.location.href = "/morador/index.html";
    }
  }

  let paramUrl = new URLSearchParams(window.location.search);

  if (paramUrl.has("error") && paramUrl.get("error") === "401") {
    const divPaiForms = document.getElementById("divPaiForms");
    divPaiForms.innerHTML = criarMensagemDeErro(
      "Sessão expirada",
      "Faça login novamente para continuar!",
      "alert-warning"
    );

    paramUrl.delete("error");
    paramUrl.delete("mgs");
    const newUrl = `${window.location.pathname}?${paramUrl.toString()} `;
    window.history.replaceState({}, document.title, newUrl);
  }
});

const baseUrl = "https://condosyn.eastus.cloudapp.azure.com:4433";

document.addEventListener("DOMContentLoaded", () => {
  setFullHeight();

  document.querySelectorAll(".toggle-password").forEach((button) => {
    button.addEventListener("click", function () {
      this.classList.toggle("fa-eye");
      this.classList.toggle("fa-eye-slash");
      const input = document.querySelector(this.getAttribute("toggle"));
      if (input.getAttribute("type") === "password") {
        input.setAttribute("type", "text");
      } else {
        input.setAttribute("type", "password");
      }
    });
  });
});

function setFullHeight() {
  document.querySelectorAll(".js-fullheight").forEach((element) => {
    element.style.height = `${window.innerHeight}px`;
  });

  window.addEventListener("resize", () => {
    document.querySelectorAll(".js-fullheight").forEach((element) => {
      element.style.height = `${window.innerHeight}px`;
    });
  });
}

document
  .getElementById("forms-login")
  .addEventListener("submit", async function (event) {
    event.preventDefault();

    showLoading();
    clearError();

    const email = document.getElementById("email");
    const senha = document.getElementById("password-field");

    if (email.value.trim() === "" || senha.value.trim() === "") {
      if (email.value.trim() === "") {
        getOrCreateErrorMsg(email, "O email é obrigatório.");
      }
      if (senha.value.trim() === "") {
        getOrCreateErrorMsg(senha, "A senha é obrigatória.");
      }
    } else {
      await postLogin(email.value, senha.value);
    }
  });

function showLoading() {
  document.getElementById("loading").style.display = "block";
}

function hideLoading() {
  document.getElementById("loading").style.display = "none";
}

async function postLogin(email, senha) {
  const body = {
    email: email,
    password: senha,
  };
  try {
    const response = await fetch(`${baseUrl}/api/v1/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
      body: JSON.stringify(body),
    });

    if (response.ok) {
      const data = await response.json();
      token.saveLogin(data.token);
      return;
    }

    const data = await response.json();
    const divPaiForms = document.getElementById("divPaiForms");
    divPaiForms.innerHTML = criarMensagemDeErro(data.message, data.error);
  } catch (error) {
    const divPaiForms = document.getElementById("divPaiForms");
    divPaiForms.innerHTML = criarMensagemDeErro(
      "Erro ao fazer login.",
      "Tente novamente mais tarde!"
    );
    console.error(error);
  } finally {
    hideLoading();
  }
}

function criarMensagemDeErro(titulo = "", texto, clss = "alert-danger") {
  return `
    <div class="alert ${clss} alert-dismissible fade show" role="alert">
      <strong>${titulo}</strong> <p>${texto}</p>
      <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
  `;
}

function clearError() {
  const formulario = document.getElementById("forms-login");
  if (formulario) {
    Array.from(formulario.elements).forEach((campo) => {
      if (campo.tagName === "INPUT") {
        campo.classList.remove("border-danger");
        campo.classList.add("border-dark");

        const msgErro = campo.nextElementSibling;
        if (msgErro && msgErro.classList.contains("mensagem-erro")) {
          msgErro.remove();
        }
      }
    });
  }
}

function getOrCreateErrorMsg(input, message) {
  input.classList.add("border-danger");
  input.classList.remove("border-dark");

  let msgErro = input.nextElementSibling;
  if (!msgErro || !msgErro.classList.contains("mensagem-erro")) {
    msgErro = document.createElement("p");
    msgErro.classList.add(
      "mt-2",
      "text-sm",
      "text-red-600",
      "dark:text-red-500",
      "mensagem-erro"
    );
    msgErro.innerHTML = `<span class="font-medium"></span> ${message}`;
    input.parentNode.appendChild(msgErro);
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
