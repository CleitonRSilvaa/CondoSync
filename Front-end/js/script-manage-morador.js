import zod from "https://cdn.jsdelivr.net/npm/zod@3.23.8/+esm";

import * as token from "/js/auth.js";

const baseUrl = "https://condosyn.eastus.cloudapp.azure.com:4433";

document.addEventListener("DOMContentLoaded", function () {
  token.validateSecurity();

  // if (!token.isLoggedAdmin()) {
  //   window.location.href = "../home/index.html";
  // }

  IMask(document.getElementById("cpf"), {
    mask: "000.000.000-00",
  });

  IMask(document.getElementById("cpf-editar"), {
    mask: "000.000.000-00",
  });

  IMask(document.getElementById("bloco"), {
    mask: "aa",
    definitions: {
      a: /[A-Za-z0-9]/,
    },
  });

  IMask(document.getElementById("apartamento"), {
    mask: "aaaa",
    definitions: {
      a: /[A-Za-z0-9]/,
    },
  });

  IMask(document.getElementById("rg"), {
    mask: "00.000.000-0",
    prepare: function (str) {
      return str.toUpperCase();
    },
    blocks: {
      0: {
        mask: IMask.MaskedRange,
        from: 0,
        to: 9,
        maxLength: 1,
      },
      X: {
        mask: IMask.MaskedEnum,
        enum: ["X"],
      },
    },
    pattern: "00.000.000-[0-9X]",
  });

  IMask(document.getElementById("rg-editar"), {
    mask: "00.000.000-0",
    prepare: function (str) {
      return str.toUpperCase();
    },
    blocks: {
      0: {
        mask: IMask.MaskedRange,
        from: 0,
        to: 9,
        maxLength: 1,
      },
      X: {
        mask: IMask.MaskedEnum,
        enum: ["X"],
      },
    },
    pattern: "00.000.000-[0-9X]",
  });

  getMoradores();
  buildProfile();

  document
    .getElementById("exampleModal")
    .addEventListener("hidden.bs.modal", () => {
      clearModalEdotar();
    });
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
  .getElementById("create-morador")
  .addEventListener("submit", function (event) {
    token.validateSecurity();
    event.preventDefault();

    clearErrors();

    const formData = {
      nome: document.getElementById("nome").value.toUpperCase(),
      sobrenome: document.getElementById("sobrenome").value.toUpperCase(),
      dataNascimento: document.getElementById("dataNascimento").value,
      cpf: document.getElementById("cpf").value,
      rg: document.getElementById("rg").value,
      email: document.getElementById("email").value.toLowerCase(),
      apartamento: document.getElementById("apartamento").value,
      bloco: document.getElementById("bloco").value.toUpperCase(),
      torre: document.getElementById("torre").value,
      senha: document.getElementById("senha").value,
      confirmacaoSenha: document.getElementById("confirmacaoSenha").value,
      rolesIds: [4],
    };

    function calculateAge(dob) {
      const today = new Date();
      const birthDate = new Date(dob);
      let age = today.getFullYear() - birthDate.getFullYear();
      const m = today.getMonth() - birthDate.getMonth();
      if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
        age--;
      }
      return age;
    }

    const moradorSchema = zod
      .object({
        nome: zod.string().min(1, "Nome é obrigatório"),
        sobrenome: zod.string().min(1, "Sobrenome é obrigatório"),
        dataNascimento: zod
          .string()
          .refine(
            (value) => {
              // Verifica se o formato da data é válido e se a data é uma data completa
              const date = new Date(value);
              if (
                date.toString() === "Invalid Date" ||
                isNaN(date.getTime()) ||
                value.length !== 10
              ) {
                return false; // Retorna falso se a data é inválida ou incompleta
              }
              // Verifica se a data é futura ou muito antiga
              if (date > new Date() || date.getFullYear() < 1900) {
                return false;
              }
              return true;
            },
            {
              message:
                "Data de Nascimento inválida. Deve ser no formato AAAA-MM-DD e real.",
            }
          )
          .refine(
            (value) => {
              // Verifica se a idade é de pelo menos 18 anos
              const date = new Date(value);
              return calculateAge(date) >= 18;
            },
            {
              message: "Você deve ter pelo menos 18 anos.",
              path: ["dataNascimento"],
            }
          ),
        cpf: zod.string().regex(/^\d{3}\.\d{3}\.\d{3}-\d{2}$/, "CPF inválido"),
        rg: zod.string().regex(/^\d{2}\.\d{3}\.\d{3}-[\dXx]$/, "RG inválido"),
        email: zod.string().email("E-mail inválido"),
        apartamento: zod.string().min(1, "Apartamento é obrigatório"),
        // bloco: zod.string().max(10, "Bloco é obrigatório"),
        // torre: zod.string().min(1, "Torre é obrigatória"),
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

    const validation = moradorSchema.safeParse(formData);

    if (!validation.success) {
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
        cpf: formData.cpf.replace(/\D/g, ""),
        rg: formData.rg.replace(/\D/g, ""),
      };
      delete forms.sobrenome;
      delete forms.nome;
      submitCriarMorador(forms);
    }
  });

document
  .getElementById("editar-morador")
  .addEventListener("submit", function (event) {
    token.validateSecurity();
    event.preventDefault();
    clearErrors();

    const formData = {
      id: document.getElementById("id-morador-editar").value,
      nome: document.getElementById("nome-editar").value.toUpperCase(),
      sobrenome: document
        .getElementById("sobrenome-editar")
        .value.toUpperCase(),
      dataNascimento: document.getElementById("dataNascimento-editar").value,
      cpf: document.getElementById("cpf-editar").value,
      rg: document.getElementById("rg-editar").value,
      email: document.getElementById("email-editar").value.toLowerCase(),
      apartamento: document.getElementById("apartamento-editar").value,
      // bloco: document.getElementById("bloco-editar").value.toUpperCase(),
      // torre: document.getElementById("torre-editar").value,
      senha: document.getElementById("senha-editar").value,
      confirmacaoSenha: document.getElementById("confirmacaoSenha-editar")
        .value,
      rolesIds: [4],
    };

    function calculateAge(dob) {
      const today = new Date();
      const birthDate = new Date(dob);
      let age = today.getFullYear() - birthDate.getFullYear();
      const m = today.getMonth() - birthDate.getMonth();
      if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
        age--;
      }
      return age;
    }

    const moradorSchema = zod
      .object({
        nome: zod.string().min(1, "Nome é obrigatório"),
        sobrenome: zod.string().min(1, "Sobrenome é obrigatório"),
        dataNascimento: zod
          .string()
          .refine(
            (value) => {
              // Verifica se o formato da data é válido e se a data é uma data completa
              const date = new Date(value);
              if (
                date.toString() === "Invalid Date" ||
                isNaN(date.getTime()) ||
                value.length !== 10
              ) {
                return false; // Retorna falso se a data é inválida ou incompleta
              }
              // Verifica se a data é futura ou muito antiga
              if (date > new Date() || date.getFullYear() < 1900) {
                return false;
              }
              return true;
            },
            {
              message:
                "Data de Nascimento inválida. Deve ser no formato AAAA-MM-DD e real.",
            }
          )
          .refine(
            (value) => {
              // Verifica se a idade é de pelo menos 18 anos
              const date = new Date(value);
              return calculateAge(date) >= 18;
            },
            {
              message: "Você deve ter pelo menos 18 anos.",
              path: ["dataNascimento"],
            }
          ),
        cpf: zod.string().regex(/^\d{3}\.\d{3}\.\d{3}-\d{2}$/, "CPF inválido"),
        rg: zod.string().regex(/^\d{2}\.\d{3}\.\d{3}-[\dXx]$/, "RG inválido"),
        email: zod.string().email("E-mail inválido"),
        apartamento: zod.string().min(1, "Apartamento é obrigatório"),
        // bloco: zod.string().min(1, "Bloco é obrigatório"),
        // torre: zod.union([
        //   zod.literal(""),
        //   zod.string().min(1, "Torre é obrigatória"),
        // ]),
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

    const validation = moradorSchema.safeParse(formData);

    if (!validation.success) {
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
        cpf: formData.cpf.replace(/\D/g, ""),
        rg: formData.rg.replace(/\D/g, ""),
      };
      delete form.nome;
      delete form.sobrenome;
      submitEditarMorador(form);
    }
  });

async function submitEditarMorador(form) {
  try {
    showLoading();
    const params = new URLSearchParams(form.id);
    const response = await fetch(
      `${baseUrl}/api/v1/morador/update/${params.toString().replace("=", "")}`,
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
      showToast("Sucesso", "Morador editado com sucesso!", "bg-success", 5000);
      getMoradores();
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
      "Erro ao atualizar registro do morador!",
      "bg-warning",
      7000
    );
  } catch (error) {
    showToast(
      "Erro",
      "Erro ao atualizar registr do morador!",
      "bg-danger",
      5000
    );
  } finally {
    hideLoading();
  }
}

async function submitCriarMorador(form) {
  try {
    const response = await fetch(`${baseUrl}/api/v1/morador/register`, {
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
        "Morador registrado com sucesso!",
        "bg-success",
        5000
      );
      clearModal();
      await getMoradores();
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

    showToast("Atenção", "Erro ao registrar o morador!", "bg-warning", 7000);
  } catch (error) {
    showToast("Erro", "Erro ao registrar o morador!", "bg-danger", 5000);
  } finally {
    hideLoading();
  }
}

async function getMoradores() {
  token.validateSecurity();
  showLoading();

  try {
    const response = await fetch(`${baseUrl}/api/v1/morador/list`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + token.getToken(),
      },
    });

    const data = await response.json();

    if (response.ok) {
      if (data.length === 0) {
        const container = document.getElementById("moradores-container");
        const p = document.createElement("p");
        p.textContent = "Nenhum morador encontrado.";
        p.classList.add("text-center", "text-uppercase", "fw-bold");
        container.innerHTML = "";
        container.appendChild(p);
        return;
      }

      buildTable(data);
    } else {
      showToast("Atenção", "Erro ao buscar os moradores!", "bg-warning", 7000);
    }
  } catch (error) {
    showToast("Erro", "Erro ao buscar os moradores!", "bg-danger", 5000);
  } finally {
    hideLoading();
  }
}

async function getMorador(id) {
  token.validateSecurity();
  showLoading();

  try {
    const response = await fetch(`${baseUrl}/api/v1/morador/find/${id}`, {
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
      showToast("Atenção", "Erro ao buscar morador!", "bg-warning", 7000);
    }
  } catch (error) {
    showToast("Erro", "Erro ao buscar morador!", "bg-danger", 5000);
  } finally {
    hideLoading();
  }
}

async function changeStatus(moradorID) {
  token.validateSecurity();
  showLoading();
  try {
    const response = await fetch(
      `${baseUrl}/api/v1/morador/change-status/${moradorID}`,
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
      await getMoradores();
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
  const container = document.getElementById("moradores-container");
  const table = document.createElement("table");
  table.className = "table table-striped table-hover";
  table.innerHTML = `
    <thead class="table-dark">
      <tr>
        <th scope="col">ID</th>
        <th scope="col">Nome</th>
        <th scope="col">Email</th>
        <th scope="col">Apartamento</th>
        <th scope="col">Bloco</th>
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
    row.insertCell().textContent = morad.apartamento;
    row.insertCell().textContent = morad.bloco;
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

async function changeModalEdit(moradorID) {
  clearModalEdotar();
  clearErrors();
  let morador = await getMorador(moradorID);
  const nome = morador.nomeCompleto.split(" ");
  const sobrenome = nome.pop();
  const cpf = morador.cpf.replace(
    /(\d{3})(\d{3})(\d{3})(\d{2})/,
    "$1.$2.$3-$4"
  );
  const rg = morador.rg.replace(/(\d{2})(\d{3})(\d{3})(\d{1})/, "$1.$2.$3-$4");

  morador = {
    ...morador,
    nome: nome.join(" "),
    sobrenome,
    cpf,
    rg,
  };
  document.getElementById("id-morador-editar").value = morador.id;
  document.getElementById("nome-editar").value = morador.nome;
  document.getElementById("sobrenome-editar").value = morador.sobrenome;
  document.getElementById("dataNascimento-editar").value =
    morador.dataNascimento;
  document.getElementById("cpf-editar").value = morador.cpf;
  document.getElementById("rg-editar").value = morador.rg;
  document.getElementById("email-editar").value = morador.email;
  document.getElementById("apartamento-editar").value = morador.apartamento;
  document.getElementById("bloco-editar").value = morador.bloco;
  document.getElementById("torre-editar").value = morador.torre;
}

function clearModal() {
  document.getElementById("nome").value = "";
  document.getElementById("sobrenome").value = "";
  document.getElementById("dataNascimento").value = "";
  document.getElementById("cpf").value = "";
  document.getElementById("rg").value = "";
  document.getElementById("email").value = "";
  document.getElementById("apartamento").value = "";
  document.getElementById("bloco").value = "";
  document.getElementById("torre").value = "";
  document.getElementById("senha").value = "";
  document.getElementById("confirmacaoSenha").value = "";
}

function clearModalEdotar() {
  document.getElementById("id-morador-editar").value = "";
  document.getElementById("nome-editar").value = "";
  document.getElementById("sobrenome-editar").value = "";
  document.getElementById("dataNascimento-editar").value = "";
  document.getElementById("cpf-editar").value = "";
  document.getElementById("rg-editar").value = "";
  document.getElementById("email-editar").value = "";
  document.getElementById("apartamento-editar").value = "";
  document.getElementById("bloco-editar").value = "";
  document.getElementById("torre-editar").value = "";
  document.getElementById("senha-editar").value = "";
  document.getElementById("confirmacaoSenha-editar").value = "";
}

document
  .getElementById("bnt-change-status")
  .addEventListener("click", function (event) {
    const moradorID = document.getElementById("id-status-change").value;
    changeStatus(moradorID);
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
