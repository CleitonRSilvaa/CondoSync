import zod from "https://cdn.jsdelivr.net/npm/zod@3.23.8/+esm";

import * as tokem from "/js/auth.js";

const baseUrl = "http://localhost:8010";

document.addEventListener("DOMContentLoaded", function () {
  IMask(document.getElementById("cpf"), {
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
});

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
    bloco: zod.string().min(1, "Bloco é obrigatório"),
    torre: zod.string().min(1, "Torre é obrigatória"),
    senha: zod
      .string()
      .regex(
        /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{6,21}$/,
        {
          message:
            "A senha deve conter no mínimo 6 caracteres e no máximo 20 caracteres, incluindo pelo menos um número, uma letra maiúscula, uma letra minúscula e um caractere especial.",
        }
      ),
    confirmacaoSenha: zod.string().min(6, "Confirmação de senha é obrigatória"),
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
      submit(forms);
    }
  });

async function submit(form) {
  try {
    const response = await fetch(`${baseUrl}/api/v1/morador/register`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + tokem.getToken(),
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
