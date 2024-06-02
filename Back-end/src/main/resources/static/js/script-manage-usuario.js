import zod from "https://cdn.jsdelivr.net/npm/zod@3.23.8/+esm";

import * as token from "/js/auth.js";

const baseUrl = "http://localhost:8010";
// const baseUrl = "https://condosyn.eastus.cloudapp.azure.com:4433";

document.addEventListener("DOMContentLoaded", function () {
  token.validateSecurity();
  getUsuario();
  buildProfile();
});

document
  .getElementById("exampleModal")
  .addEventListener("hidden.bs.modal", () => {
    clearModalEditar();
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

document
  .getElementById("create-usuario")
  .addEventListener("submit", function (event) {
    token.validateSecurity();
    event.preventDefault();

    clearErrors();

    const formData = {
      nome: document.getElementById("nome").value.toUpperCase(),
      sobrenome: document.getElementById("sobrenome").value.toUpperCase(),
      email: document.getElementById("email").value.toLowerCase(),
      senha: document.getElementById("senha").value,
      confirmacaoSenha: document.getElementById("confirmacaoSenha").value,
      rolesIds: [parseInt(document.getElementById("tipo").value)],
    };

    console.log(formData);

    const usuarioSchema = zod
      .object({
        nome: zod.string().min(1, "Nome é obrigatório"),
        sobrenome: zod.string().min(1, "Sobrenome é obrigatório"),
        email: zod.string().email("E-mail inválido"),
        rolesIds: zod
          .array(zod.number())
          .nonempty("Tipo de conta é obrigatório"),
        senha: zod
          .string()
          .regex(
            /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{6,21}$/,
            {
              message:
                "A senha deve conter no mínimo 6 caracteres e no máximo 20 caracteres, incluindo pelo menos um número, uma letra maiúscula, uma letra minúscula e um caractere especial.",
            }
          ),
        confirmacaoSenha: zod
          .string()
          .min(6, "Confirmação de senha é obrigatória"),
      })
      .refine((data) => data.senha === data.confirmacaoSenha, {
        message: "As senhas não conferem",
        path: ["confirmacaoSenha"],
      })
      .refine(
        (data) => {
          const nomePartes = data.nome.toLowerCase().split(/\s+/);
          const senhaLower = data.senha.toLowerCase();
          return !nomePartes.some(
            (parte) => senhaLower.includes(parte) && parte.length > 1
          );
        },
        {
          message: "A senha não deve conter partes do seu nome.",
          path: ["senha"],
        }
      );

    const validation = usuarioSchema.safeParse(formData);

    if (!validation.success) {
      console.log(validation.error.errors);
      validation.error.errors.forEach((err) => {
        const fieldName = err.path[0];
        const errorElementId = `${fieldName}-error`;
        showError(errorElementId, err.message);
      });
    } else {
      showLoading();
      const forms = {
        ...formData,
        nomeCompleto: `${formData.nome} ${formData.sobrenome}`,
        email: formData.email.toLowerCase(),
      };
      delete forms.sobrenome;
      delete forms.nome;
      submitCriarUsuario(forms);
    }
  });

document
  .getElementById("editar-usuario")
  .addEventListener("submit", function (event) {
    token.validateSecurity();
    event.preventDefault();
    clearErrors();

    const formData = {
      id: document.getElementById("id-usuario-editar").value,
      nome: document.getElementById("nome-editar").value.toUpperCase(),
      sobrenome: document
        .getElementById("sobrenome-editar")
        .value.toUpperCase(),

      email: document.getElementById("email-editar").value.toLowerCase(),
      senha: document.getElementById("senha-editar").value,
      confirmacaoSenha: document.getElementById("confirmacaoSenha-editar")
        .value,
      rolesIds: [parseInt(document.getElementById("tipo-editar").value)],
    };

    const usuarioSchema = zod
      .object({
        nome: zod.string().min(1, "Nome é obrigatório"),
        sobrenome: zod.string().min(1, "Sobrenome é obrigatório"),
        email: zod.string().email("E-mail inválido"),
        rolesIds: zod
          .array(zod.number())
          .nonempty("Tipo de conta é obrigatório"),
        senha: zod.union([
          zod.literal(""),
          zod
            .string()
            .regex(
              /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{6,21}$/,
              {
                message:
                  "A senha deve conter no mínimo 6 caracteres e no máximo 20 caracteres, incluindo pelo menos um número, uma letra maiúscula, uma letra minúscula e um caractere especial.",
              }
            ),
        ]),
        confirmacaoSenha: zod.union([
          zod.literal(""),
          zod.string().min(6, "Confirmação de senha é obrigatória"),
        ]),
      })
      .refine((data) => data.senha === data.confirmacaoSenha, {
        message: "As senhas não conferem",
        path: ["confirmacaoSenha"],
      })
      .refine(
        (data) => {
          const nomePartes = data.nome.toLowerCase().split(/\s+/);
          const senhaLower = data.senha.toLowerCase();
          return !nomePartes.some(
            (parte) => senhaLower.includes(parte) && parte.length > 1
          );
        },
        {
          message: "A senha não deve conter partes do seu nome.",
          path: ["senha"],
        }
      );

    const validation = usuarioSchema.safeParse(formData);

    if (false) {
      validation.error.errors.forEach((err) => {
        const fieldName = err.path[0];
        const errorElementId = `${fieldName}-editar-error`;
        showError(errorElementId, err.message);
      });
    } else {
      const form = {
        ...formData,
        nomeCompleto: `${formData.nome.toUpperCase()} ${formData.sobrenome.toUpperCase()}`,
        email: formData.email.toLowerCase(),
      };
      delete form.nome;
      delete form.sobrenome;
      submitEditarusuario(form);
    }
  });

async function submitEditarusuario(form) {
  try {
    showLoading();
    const params = new URLSearchParams(form.id);
    const response = await fetch(
      `${baseUrl}/api/v1/users/update/${params.toString().replace("=", "")}`,
      {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: "Bearer " + token.getToken(),
        },
        body: JSON.stringify(form),
      }
    );

    if (response.ok) {
      const modalElement = document.getElementById("exampleModal");
      const modalInstance =
        bootstrap.Modal.getInstance(modalElement) ||
        new bootstrap.Modal(modalElement);
      modalInstance.hide();
      showToast("Sucesso", "usuário editado com sucesso!", "bg-success", 5000);
      getUsuario();
      return;
    }

    if (
      response.status === 404 ||
      response.status === 400 ||
      response.status === 409
    ) {
      const data = await response.json();

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

    showToast(
      "Atenção",
      "Erro ao atualizar registro do usuário!",
      "bg-warning",
      7000
    );
  } catch (error) {
    showToast(
      "Erro",
      "Erro ao atualizar registr do usuário!",
      "bg-danger",
      5000
    );
  } finally {
    hideLoading();
  }
}

async function submitCriarUsuario(form) {
  try {
    const response = await fetch(`${baseUrl}/api/v1/users/register`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + token.getToken(),
      },
      body: JSON.stringify(form),
    });

    if (response.ok) {
      showToast(
        "Sucesso",
        "usuário registrado com sucesso!",
        "bg-success",
        5000
      );
      clearModal();
      await getUsuario();
      return;
    }

    if (
      response.status === 404 ||
      response.status === 400 ||
      response.status === 409
    ) {
      const data = await response.json();

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

    showToast("Atenção", "Erro ao registrar o usuário!", "bg-warning", 7000);
  } catch (error) {
    showToast("Erro", "Erro ao registrar o morador!", "bg-danger", 5000);
  } finally {
    hideLoading();
  }
}

async function getUsuario() {
  token.validateSecurity();
  showLoading();

  try {
    const response = await fetch(`${baseUrl}/api/v1/users/list`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + token.getToken(),
      },
    });

    const data = await response.json();

    if (response.ok) {
      if (data.length === 0) {
        const container = document.getElementById("usuarios-container");
        const p = document.createElement("p");
        p.textContent = "Nenhum usuário encontrado.";
        p.classList.add("text-center", "text-uppercase", "fw-bold");
        container.innerHTML = "";
        container.appendChild(p);
        return;
      }

      buildTable(data);
    } else {
      showToast("Atenção", "Erro ao buscar os usuários!", "bg-warning", 7000);
    }
  } catch (error) {
    showToast("Erro", "Erro ao buscar os usuários!", "bg-danger", 5000);
  } finally {
    hideLoading();
  }
}

async function getusuario(id) {
  token.validateSecurity();
  showLoading();

  try {
    const response = await fetch(`${baseUrl}/api/v1/users/find/${id}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + token.getToken(),
      },
    });

    const data = await response.json();

    if (response.ok) {
      return data;
    } else {
      showToast("Atenção", "Erro ao buscar usuário!", "bg-warning", 7000);
    }
  } catch (error) {
    showToast("Erro", "Erro ao buscar usuário!", "bg-danger", 5000);
  } finally {
    hideLoading();
  }
}

async function changeStatus(usuarioID) {
  token.validateSecurity();
  showLoading();
  try {
    const response = await fetch(
      `${baseUrl}/api/v1/users/change-status/${usuarioID}`,
      {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: "Bearer " + token.getToken(),
        },
      }
    );
    if (response.ok) {
      const modalElement = document.getElementById(
        "confirme-chamge-status-modal"
      );
      const modalInstance =
        bootstrap.Modal.getInstance(modalElement) ||
        new bootstrap.Modal(modalElement);
      modalInstance.hide();
      await getUsuario();
      showToast("Sucesso", "Status alterado com sucesso!", "bg-success", 5000);
      return;
    }
    if (response.status === 404 || response.status === 400) {
      const data = await response.json();
      showToast("Atenção", data.message, "bg-warning", 7000);
      return;
    }
    showToast("Atenção", "Erro ao alterar o status!", "bg-warning", 7000);
  } catch (error) {
    showToast("Erro", "Erro ao alterar o status!", "bg-danger", 5000);
  } finally {
    hideLoading();
  }
}

function buildTable(data) {
  const container = document.getElementById("usuarios-container");
  const table = document.createElement("table");
  table.className = "table table-striped table-hover";
  table.innerHTML = `
    <thead class="table-dark">
      <tr>
        <th scope="col">ID</th>
        <th scope="col">Nome</th>
        <th scope="col">Email</th>
        <th scope="col">Status</th>
        <th scope="col" colspan="1" class="text-center">Ações</th>
      </tr>
    </thead>
    <tbody>
    </tbody>
  `;

  const tbody = table.querySelector("tbody");

  let count = 1;
  data.forEach((morad) => {
    const row = tbody.insertRow();
    row.insertCell().textContent = morad.id;
    row.insertCell().textContent = morad.nomeCompleto;
    row.insertCell().textContent = morad.email;
    row.insertCell().textContent = morad.status ? "Ativo" : "Inativo";

    const actionCell = row.insertCell();
    actionCell.className = "text-center";
    if (morad.status) {
      const button = document.createElement("button");
      button.classList.add("btn", "btn-danger", "btn-sm", "me-2");
      button.textContent = "Inativar";
      button.dataset.bsToggle = "modal";
      button.dataset.bsTarget = "#confirme-chamge-status-modal";
      button.onclick = () => {
        document.getElementById("id-status-change").value = morad.id;
      };
      actionCell.appendChild(button);
    } else {
      const button = document.createElement("button");
      button.classList.add("btn", "btn-success", "btn-sm", "me-2");
      button.textContent = "Ativar";
      button.dataset.bsToggle = "modal";
      button.dataset.bsTarget = "#confirme-chamge-status-modal";
      button.onclick = () => {
        document.getElementById("id-status-change").value = morad.id;
      };
      actionCell.appendChild(button);
    }
    const button = document.createElement("button");
    button.classList.add("btn", "btn-warning", "btn-sm", "me-2");
    button.textContent = "Editar";
    button.dataset.bsToggle = "modal";
    button.dataset.bsTarget = "#exampleModal";
    button.onclick = () => changeModalEdit(morad.id);
    actionCell.appendChild(button);
  });

  const div = document.createElement("div");
  div.className = "table-responsive";
  div.appendChild(table);
  container.innerHTML = "";
  container.appendChild(div);
}

async function changeModalEdit(usuarioID) {
  clearModalEditar();
  clearErrors();
  let usuario = await getusuario(usuarioID);

  console.log(usuario);
  const nome = usuario.nomeCompleto.split(" ");
  const sobrenome = nome.pop();

  usuario = {
    ...usuario,
    nome: nome.join(" "),
    sobrenome,
  };
  document.getElementById("id-usuario-editar").value = usuario.id;
  document.getElementById("nome-editar").value = usuario.nome;
  document.getElementById("sobrenome-editar").value = usuario.sobrenome;
  document.getElementById("email-editar").value = usuario.email;
}

function clearModal() {
  document.getElementById("create-usuario").reset();
}

function clearModalEditar() {
  document.getElementById("editar-usuario").reset();
}

document
  .getElementById("bnt-change-status")
  .addEventListener("click", function (event) {
    const usuarioID = document.getElementById("id-status-change").value;
    changeStatus(usuarioID);
  });

document.getElementById("btn-logout").addEventListener("click", token.logout);

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

function setdaDadosModalConfirmacao(titulo, mensagem) {
  const modal = document.getElementById("confirme-modal");
  const modalTitulo = modal.querySelector(".modal-title");
  modalTitulo.innerHTML = titulo;
  const modalBody = modal.querySelector(".modal-body");

  modalBody.innerHTML = `
      <h5 class="text-center" >${mensagem}</h5>
    `;
}
