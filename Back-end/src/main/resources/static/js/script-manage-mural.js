import zod from "https://cdn.jsdelivr.net/npm/zod@3.23.8/+esm";

import * as token from "/js/auth.js";

const baseUrl = "https://condosyn.eastus.cloudapp.azure.com:4433";

document.addEventListener("DOMContentLoaded", async function () {
  token.validateSecurity();

  await getDadosMural();
  buildProfile();
  setdaDadosModalConfirmacao(
    "Confirmação",
    "Deseja realmente excluir o aviso?"
  );
});

document
  .getElementById("modal-form-aviso")
  .addEventListener("hidden.bs.modal", () => {
    clearForms();
  });

function clearForms() {
  document.getElementById("formMural").reset();
  document.getElementById("preview").innerHTML = "";
}

function showLoading() {
  document.getElementById("loading").style.display = "block";
}

function hideLoading() {
  document.getElementById("loading").style.display = "none";
}

function updateRadioInputs() {
  var preview = document.getElementById("preview");
  var radios = preview.querySelectorAll(".form-check-input");
  var labels = preview.querySelectorAll(".form-check-label");
  for (var i = 0; i < radios.length; i++) {
    radios[i].id = "exampleRadios" + i;
    radios[i].value = i;
    labels[i].htmlFor = "exampleRadios" + i;
  }
}

function removeImage(card, file) {
  var input = document.getElementById("imagem");
  var preview = document.getElementById("preview");

  preview.removeChild(card);

  // Cria um novo objeto de FormData
  var dataTransfer = new DataTransfer();
  for (var i = 0; i < input.files.length; i++) {
    if (input.files[i] !== file) {
      dataTransfer.items.add(input.files[i]);
    }
  }

  // Atualiza o input com os arquivos restantes
  input.files = dataTransfer.files;
}

function handleFile(file, i) {
  if (!file.type.match("image/jpeg") && !file.type.match("image/png")) {
    showToast(
      "Erro",
      "Só são permitidos arquivos de imagem (jpg, jpeg, png).",
      "bg-warning"
    );
    this.value = "";
    return;
  }

  var reader = new FileReader();

  reader.onload = function (e) {
    var img = document.createElement("img");
    img.src = e.target.result;
    img.alt = "Imagem-produto";
    img.className = "card-img-top";

    var cardBody = document.createElement("div");
    cardBody.className = "card-body d-flex flex-column";

    var formCheck = document.createElement("div");
    formCheck.className = "form-check mt-auto";

    cardBody.appendChild(formCheck);

    var closeButton = document.createElement("button");
    closeButton.type = "button";
    closeButton.className = "btn-close";
    closeButton.setAttribute("aria-label", "Close");

    var card = document.createElement("div");
    card.className = "card p-2 m-1";
    card.style.width = "18rem";
    card.appendChild(closeButton);
    card.appendChild(img);
    card.appendChild(cardBody);

    closeButton.addEventListener("click", function () {
      removeImage(card, file);
      updateRadioInputs();
    });

    preview.appendChild(card);
  };

  reader.readAsDataURL(file);
}

document.body.addEventListener("change", function (event) {
  if (event.target.id === "imagem") {
    var preview = document.getElementById("preview");
    preview.innerHTML = "";

    for (var i = 0; i < event.target.files.length; i++) {
      handleFile(event.target.files[i], i);
    }
  }
});

document.body.addEventListener("click", function (event) {
  if (event.target.classList.contains("btn-close")) {
    event.target.parentElement.remove();
    updateRadioInputs();
  }
});

document.addEventListener("submit", async function (event) {
  event.preventDefault();

  const title = document.getElementById("title").value;
  const description = document.getElementById("descricao").value;

  if (document.getElementById("imagem").files.length === 0) {
    alert("Selecione uma imagem.");
    return;
  }

  const image = document.getElementById("imagem").files[0];
  let imageBase64 = null;
  const getImageBase64 = (file) => {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onloadend = function (e) {
        resolve(e.target.result);
      };
      reader.onerror = function (e) {
        reject(e);
      };
      reader.readAsDataURL(file);
    });
  };

  const handleSubmit = async () => {
    if (image) {
      try {
        return await getImageBase64(image);
      } catch (error) {
        return null;
      }
    }
  };

  const formData = {
    title: title,
    description: description,
    image: {
      base64: await handleSubmit(),
      name: image.name,
      isMain: true,
    },
  };

  try {
    const response = await fetch(baseUrl + "/api/v1/mural/save", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      Authorization: "Bearer " + token.getToken(),
      body: JSON.stringify(formData),
    });

    if (response.ok) {
      showToast("Sucesso", "Aviso cadastrado com sucesso.", "bg-success");
      document.getElementById("formMural").reset();
      const modalElement = document.getElementById("modal-form-aviso");
      const modalInstance =
        bootstrap.Modal.getInstance(modalElement) ||
        new bootstrap.Modal(modalElement);
      modalInstance.hide();
      await getDadosMural();
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
    } else {
      showToast("Erro", "Erro ao cadastrar o aviso.", "bg-danger");
    }
  } catch (error) {
    showToast("Erro", "Erro ao cadastrar o aviso.", "bg-danger");
  }
});

async function getDadosMural() {
  token.validateSecurity();
  showLoading();

  try {
    const response = await fetch(`${baseUrl}/api/v1/mural/list`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + token.getToken(),
      },
    });

    const data = await response.json();

    if (response.ok) {
      if (data.length === 0) {
        buildTable([]);
        return;
      }
      buildTable(data);
    } else {
      showToast("Erro", "Erro ao buscar os dados do mural.", "bg-danger");
    }
  } catch (error) {
    showToast("Erro", "Erro ao buscar os dados do mural.", "bg-danger");
  } finally {
    hideLoading();
  }
}

function buildTable(data) {
  const container = document.getElementById("avisos-container");
  container.innerHTML = "";

  if (data.length === 0) {
    container.innerHTML = `
      <div class="alert alert-warning text-center role="alert">
        Nenhum aviso cadastrado.
      </div>
    `;
    return;
  }

  const table = document.createElement("table");
  table.className = "table table-striped table-hover";
  table.innerHTML = `
    <thead class="table-dark">
      <tr>
        <th scope="col">ID</th>
        <th scope="col">Título</th>
        <th scope="col">Descrição</th>
        <th scope="col">Imagem nome</th>
        <th scope="col">Ações</th>
      </tr>
    </thead>
    <tbody>
    </tbody>
  `;
  container.appendChild(table);

  const tbody = table.querySelector("tbody");
  data.forEach((item) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${item.id}</td>
      <td>${item.title}</td>
      <td>${item.description}</td>
      <td>${item.image.name}</td>
    `;
    const tdActions = document.createElement("td");
    const button = document.createElement("button");
    button.className = "btn btn-danger";
    button.textContent = "Excluir";
    button.dataset.bsToggle = "modal";
    button.dataset.bsTarget = "#confirme-modal";

    button.addEventListener("click", function () {
      document.getElementById("id-value").value = item.id;
      document
        .getElementById("bnt-confirme-sim")
        .addEventListener("click", function (event) {
          const modalElement = document.getElementById("confirme-modal");
          const modalInstance =
            bootstrap.Modal.getInstance(modalElement) ||
            new bootstrap.Modal(modalElement);
          modalInstance.hide();
          deleteAviso(item.id);
        });
    });
    tdActions.appendChild(button);
    tr.appendChild(tdActions);
    tbody.appendChild(tr);
  });

  container.appendChild(table);
}

async function deleteAviso(id) {
  token.validateSecurity();
  showLoading();

  try {
    const response = await fetch(`${baseUrl}/api/v1/mural/deleteById/${id}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + token.getToken(),
      },
    });

    if (response.ok) {
      showToast(
        "Aviso excluído",
        "O aviso foi excluído com sucesso.",
        "bg-success"
      );
      await getDadosMural();
      return;
    } else {
      showToast("Erro", "Erro ao excluir o aviso.", "bg-danger");
    }
  } catch (error) {
    showToast("Erro", "Erro ao excluir o aviso.", "bg-danger");
  } finally {
    hideLoading();
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

function validateForm() {
  const schema = zod.object({
    title: zod.string().min(3).max(50).message("Título inválido"),
    description: zod.string().max(255).message("Descrição inválida "),
  });

  const title = document.getElementById("title").value;
  const description = document.getElementById("descricao").value;

  try {
    schema.parse({ title, description });
    return true;
  } catch (error) {
    showToast("Erro", error.errors[0], "bg-warning");
    return false;
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
