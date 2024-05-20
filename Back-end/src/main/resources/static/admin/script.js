import zod from "https://cdn.jsdelivr.net/npm/zod@3.23.8/+esm";

// Certifique-se de que o código seja executado após o carregamento do DOM
document.addEventListener("DOMContentLoaded", function () {
  // Aplicar máscaras aos campos de CPF e RG
  IMask(document.getElementById("cpf"), {
    mask: "000.000.000-00",
  });

  IMask(document.getElementById("dataNascimento"), {
    mask: "00/00/0000",
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

  // Definindo o esquema de validação
  const moradorSchema = zod
    .object({
      nome: zod.string().min(1, "Nome é obrigatório"),
      sobrenome: zod.string().min(1, "Sobrenome é obrigatório"),
      dataNascimento: zod
        .string()
        .refine((value) => {
          const date = new Date(value);
          if (
            date.toString() === "Invalid Date" ||
            isNaN(date.getTime()) ||
            value.length !== 10
          ) {
            return false;
          }
          if (date > new Date()) {
            return false;
          }
          if (date.getFullYear() < 1900) {
            return false;
          }
          return true;
        }, "Data de Nascimento inválida")
        .refine(
          (value) => {
            const date = new Date(value);
            const today = new Date();
            let age = today.getFullYear() - date.getFullYear(); // Usar `let` em vez de `const`
            const monthDifference = today.getMonth() - date.getMonth();
            if (
              monthDifference < 0 ||
              (monthDifference === 0 && today.getDate() < date.getDate())
            ) {
              age--;
            }
            return age >= 18;
          },
          {
            message: "Você deve ter pelo menos 18 anos",
            path: ["dataNascimento"],
          }
        ),
      cpf: zod.string().regex(/^\d{3}\.\d{3}\.\d{3}-\d{2}$/, "CPF inválido"),
      rg: zod.string().regex(/^\d{2}\.\d{3}\.\d{3}-[\dXx]$/, "RG inválido"),
      email: zod.string().email("E-mail inválido"),
      apartamento: zod.string().min(1, "Apartamento é obrigatório"),
      bloco: zod.string().min(1, "Bloco é obrigatório"),
      torre: zod.string().min(1, "Torre é obrigatória"),
      senha: zod.string().min(6, "Senha deve ter pelo menos 6 caracteres"),
      confirmarSenha: zod.string().min(6, "Confirmação de senha é obrigatória"),
    })
    .refine((data) => data.senha === data.confirmarSenha, {
      message: "As senhas não conferem",
      path: ["confirmarSenha"],
    });

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
        nome: document.getElementById("nome").value,
        sobrenome: document.getElementById("sobrenome").value,
        dataNascimento: document.getElementById("dataNascimento").value,
        cpf: document.getElementById("cpf").value,
        rg: document.getElementById("rg").value,
        email: document.getElementById("email").value,
        apartamento: document.getElementById("apartamento").value,
        bloco: document.getElementById("bloco").value,
        torre: document.getElementById("torre").value,
        senha: document.getElementById("senha").value,
        confirmarSenha: document.getElementById("confirmarSenha").value,
      };

      const validation = moradorSchema.safeParse(formData);

      if (!validation.success) {
        validation.error.errors.forEach((err) => {
          const fieldName = err.path[0];
          const errorElementId = `${fieldName}-error`;
          showError(errorElementId, err.message);
        });
      } else {
        alert("Formulário válido!");
        // Lógica para enviar o formulário
        // this.submit(); // descomente se deseja submeter o formulário após a validação
      }
    });
});
