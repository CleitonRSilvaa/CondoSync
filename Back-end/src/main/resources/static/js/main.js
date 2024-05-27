import * as token from "/js/auth.js";

document.addEventListener("DOMContentLoaded", () => {
  let paramUrl = new URLSearchParams(window.location.search);
  if (paramUrl.has("error") && paramUrl.get("error") === "401") {
    showToast(
      "Sessão expirada",
      "Faça login novamente para continuar!",
      "bg-warning",
      10000
    );
    paramUrl.delete("error");
    const newUrl = `${window.location.pathname}?${paramUrl.toString()}`;
    window.history.replaceState({}, document.title, newUrl);
  }
});

if (token.isLogged()) {
  if (token.getScope().includes("ADMIN")) {
    window.location.href = "/admin/index.html";
  }
  if (token.getScope().includes("MORADOR")) {
    window.location.href = "/morador/index.html";
  }
}

const baseUrl = "http://192.168.0.115:8010";

(function ($) {
  "use strict";
  var fullHeight = function () {
    $(".js-fullheight").css("height", $(window).height());
    $(window).resize(function () {
      $(".js-fullheight").css("height", $(window).height());
    });
  };
  fullHeight();
  $(".toggle-password").click(function () {
    $(this).toggleClass("fa-eye fa-eye-slash");
    var input = $($(this).attr("toggle"));
    if (input.attr("type") == "password") {
      input.attr("type", "text");
    } else {
      input.attr("type", "password");
    }
  });
})(jQuery);

document
  .getElementById("forms-login")
  .addEventListener("submit", async function (event) {
    event.preventDefault();

    const btn = document.getElementById("btn-login");
    const loading = document.getElementById("loading");
    btn.disabled = true;
    loading.style.display = "block";

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
    }

    if (response.status === 401) {
      const data = await response.json();
      $("#divPaiForms").empty();
      const divPaiForms = document.getElementById("divPaiForms");
      divPaiForms.insertAdjacentHTML(
        "afterbegin",
        criarMensagemDeErro(data.message, data.error)
      );
      return;
    }
    if (response.status >= 400 && response.status < 500) {
      const data = await response.json();
      $("#divPaiForms").empty();
      const divPaiForms = document.getElementById("divPaiForms");
      divPaiForms.insertAdjacentHTML(
        "afterbegin",
        criarMensagemDeErro(data.message, data.error)
      );
      return;
    }
    if (response.status === 500) {
      $("#divPaiForms").empty();
      const divPaiForms = document.getElementById("divPaiForms");
      divPaiForms.insertAdjacentHTML(
        "afterbegin",
        criarMensagemDeErro("Erro ao fazer login, tente novamente mais tarde!")
      );
      return;
    }
  } catch (error) {
    $("#divPaiForms").empty();
    const divPaiForms = document.getElementById("divPaiForms");
    divPaiForms.insertAdjacentHTML(
      "afterbegin",
      criarMensagemDeErro("Erro ao fazer login.", "Tente novamente mais tarde!")
    );
    console.error(error);
    return;
  } finally {
    const bnt = document.getElementById("btn-login");
    const loading = document.getElementById("loading");
    loading.style.display = "none";
    bnt.disabled = false;
  }
}

function criarMensagemDeErro(titulo = "", texto) {
  return `
	<div class="alert alert-danger alert-dismissible fade show" role="alert">
  <strong>${titulo}</strong> <p>${texto}</p>
  <button type="button" class="close" data-dismiss="alert" aria-label="Close">
    <span aria-hidden="true">&times;</span>
  </button>
</div>
	`;
}

function clearError() {
  let formulario = document.getElementById("forms-login");
  if (formulario) {
    for (let i = 0; i < formulario.elements.length; i++) {
      let campo = formulario.elements[i];
      if (campo.tagName === "INPUT") {
        if (campo.classList.contains("border-danger")) {
          campo.classList.remove("border-danger");
        }
        if (!campo.classList.contains("border-dark")) {
          campo.classList.add("border-dark");
        }

        let msgErro = campo.nextElementSibling;
        if (msgErro && msgErro.classList.contains("mensagem-erro")) {
          campo.parentNode.removeChild(msgErro);
        }
      }
    }
  }
}

function getOrCreateErrorMsg(input, message) {
  input.classList.add("border-danger");
  input.classList.remove(" border-dark");
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
