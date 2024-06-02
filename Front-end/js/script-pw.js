import zod from "https://cdn.jsdelivr.net/npm/zod@3.23.8/+esm";
import * as token from "/js/auth.js";

const baseUrl = "https://condosyn.eastus.cloudapp.azure.com:4433";

document.addEventListener("DOMContentLoaded", function () {
  token.validateSecurity();
  buildProfile();
});

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

function clearErrors() {
  document
    .querySelectorAll(".invalid-feedback")
    .forEach((el) => (el.textContent = ""));
  document
    .querySelectorAll(".form-control")
    .forEach((el) => el.classList.remove("is-invalid"));
}

function showError(id, message) {
  const errorElement = document.getElementById(id);
  const inputElement = document.querySelector(`#${id.replace("-error", "")}`);

  if (errorElement && inputElement) {
    errorElement.textContent = message;
    inputElement.classList.add("is-invalid");
  }
}

const usuarioSchema = zod
  .object({
    nome: zod.string().min(3, "Nome é obrigatório"),
    currentPassword: zod.string().min(6, "Senha atual é obrigatória"),
    newPassword: zod
      .string()
      .regex(
        /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{6,21}$/,
        {
          message:
            "A senha deve conter no mínimo 6 caracteres e no máximo 20 caracteres, incluindo pelo menos um número, uma letra maiúscula, uma letra minúscula e um caractere especial.",
        }
      ),
    confirmNewPassword: zod
      .string()
      .min(6, "Confirmação de senha é obrigatória"),
  })
  .refine((data) => data.newPassword === data.confirmNewPassword, {
    message: "As senhas não conferem",
    path: ["confirmNewPassword"],
  })
  .refine(
    (data) => {
      const nomePartes = data.nome.toLowerCase().split(/\s+/);
      const senhaLower = data.newPassword.toLowerCase();
      return !nomePartes.some(
        (parte) => senhaLower.includes(parte) && parte.length > 1
      );
    },
    {
      message: "A senha não deve conter partes do seu nome.",
      path: ["newPassword"],
    }
  );

document
  .getElementById("updatePasswordForm")
  .addEventListener("submit", async (event) => {
    event.preventDefault();

    clearErrors();

    const data = {
      nome: token.getNome(),
      currentPassword: document.getElementById("currentPassword").value,
      newPassword: document.getElementById("newPassword").value,
      confirmNewPassword: document.getElementById("confirmNewPassword").value,
    };

    const validation = usuarioSchema.safeParse(data);
    if (!validation.success) {
      validation.error.issues.forEach((err) => {
        const fieldName = err.path[0];
        const errorElementId = `${fieldName}-error`;
        showError(errorElementId, err.message);
      });
      return;
    }

    await updatePassword(data);
  });

async function updatePassword(data) {
  showLoading();

  const updateData = {
    senhaAtual: data.currentPassword,
    novaSenha: data.newPassword,
    confirmacaoNovaSenha: data.confirmNewPassword,
  };

  try {
    const response = await fetch(
      `${baseUrl}/api/v1/users/update-password/` + token.getUserId(),
      {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: "Bearer " + token.getToken(),
        },
        body: JSON.stringify(updateData),
      }
    );

    if (response.ok) {
      document.getElementById("updatePasswordForm").reset();
      showToast("Sucesso", "Senha alterada com sucesso!", "bg-success");
      setTimeout(() => {
        token.logout();
      }, 1500);
      return;
    }
    const data = await response.json();

    if (
      response.status === 404 ||
      response.status === 400 ||
      response.status === 409
    ) {
      function juntarTextos(dataObject) {
        let mensagens = "";
        for (const key in dataObject) {
          if (dataObject.hasOwnProperty(key)) {
            mensagens += dataObject[key] + "<br>";
          }
        }
        return mensagens.trim();
      }

      const length = juntarTextos(data.data).length > 0;

      showToast(
        data.error,
        length ? juntarTextos(data.data) : data.message,
        "bg-warning",
        5000
      );
      return;
    }

    showToast("Erro", "Erro ao alterar senha!", "bg-danger");
  } catch (error) {
    console.error("Erro ao alterar senha", error);
    showToast("Erro", "Erro ao alterar senha!", "bg-danger");
  } finally {
    hideLoading();
  }
}

function buildProfile() {
  const user = token.getUser();
  const namePerson = document.getElementById("name-person");
  namePerson.innerHTML = `${user.nome}`;
  const ul = document.getElementById("user-name");

  const li = document.createElement("li");
  li.className = "dropdown-item";
  li.innerHTML = `${user.email}`;
  ul.appendChild(li);

  const li2 = document.createElement("li");
  li2.className = "dropdown-item";
  if (token.isLoggedAdmin()) {
    li2.innerHTML = `<a href="/admin/alterar-senha.html">Alterar senha</a>`;
  } else {
    li2.innerHTML = `<a href="/morador/alterar-senha.html">Alterar senha</a>`;
  }

  ul.appendChild(li2);

  const imageProfile = document.getElementById("imagem-profile");
  if (user.image) {
    imageProfile.src = user.image;
    imageProfile.alt = "Imagem de perfil";
    imageProfile.className = "img-fluid rounded-circle";
    const imageDefault = document.getElementById("imagem-default");
    imageDefault.style.display = "none";
    imageProfile.style.display = "block";
  }
}
document.getElementById("btn-logout").addEventListener("click", token.logout);
