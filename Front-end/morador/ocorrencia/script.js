const baseUrl = "https://condosyn.eastus.cloudapp.azure.com:4433";

import * as token from "/js/auth.js";

window.onload = () => {
  token.validateSecurity();
  getOcorrencias();
  setdaDadosModalConfirmacao(
    "Confirmação de exclusão",
    "Deseja realmente cancelar a ocorrência?"
  );
  buildProfile();
};

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

async function saveOcorrencia() {
  token.validateSecurity();

  try {
    const titulo = document.getElementById("title");
    const descriccao = document.getElementById("descricao");

    const occoreencia = {
      title: titulo.value,
      description: descriccao.value,
    };
    const response = await fetch(baseUrl + "/api/v1/ocorrencia/register", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + token.getToken(),
      },
      body: JSON.stringify(occoreencia),
    });
    const data = await response.json();
    if (response.ok) {
      showToast(
        "Sucesso",
        "Ocorrência registrada com sucesso!",
        "bg-success",
        5000
      );
      titulo.value = "";
      descriccao.value = "";
      getOcorrencias();
      return;
    }

    if (response.status === 400 || response.status === 404) {
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
    if (response.status >= 500) {
      showToast("Erro", "Erro ao registrar a ocorrência!", "bg-danger", 5000);
      return;
    }
  } catch (error) {
    showToast("Erro", "Erro ao registrar a ocorrência!", "bg-danger", 5000);
  } finally {
    hideLoading();
  }
}

async function getOcorrencias() {
  token.validateSecurity();
  showLoading();

  try {
    const response = await fetch(`${baseUrl}/api/v1/ocorrencia/list`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + token.getToken(),
      },
    });

    const data = await response.json();

    if (response.ok) {
      if (data.length === 0) {
        const container = document.getElementById("ocorrencias-container");
        const p = document.createElement("p");
        p.textContent = "Nenhuma ocorrencia encontrada!";
        p.classList.add("text-center", "text-uppercase", "fw-bold");
        container.innerHTML = "";
        container.appendChild(p);
        return;
      }
      buildTable(data);
    } else {
      showToast("Erro", "Erro ao buscar as ocorrencias!", "bg-danger", 5000);
    }
  } catch (error) {
    showToast("Erro", "Erro ao buscar as ocorrencias!", "bg-danger", 5000);
  } finally {
    hideLoading();
  }
}

function buildTable(data) {
  const container = document.getElementById("ocorrencias-container");
  const table = document.createElement("table");
  table.className = "table table-striped table-hover";
  table.innerHTML = `
    <thead class="table-dark">
      <tr>
        <th scope="col">#</th>
        <th scope="col">Titulo</th>
        <th scope="col">Status</th>
        <th scope="col">Data</th>
        <th scope="col" colspan="1" class="text-center">Ações</th>
      </tr>
    </thead>
    <tbody>
    </tbody>
  `;

  const tbody = table.querySelector("tbody");

  data.forEach((ocorrencia) => {
    const row = tbody.insertRow();
    row.insertCell().textContent = ocorrencia.id;
    row.insertCell().textContent = ocorrencia.title;
    //  row.insertCell().textContent = ocorrencia.description;
    row.insertCell().textContent = ocorrencia.status;
    row.insertCell().textContent = ocorrencia.creation;

    const actionCell = row.insertCell();
    actionCell.className = "text-center";
    if (ocorrencia.status.toLowerCase() === "em aberto") {
      const button = document.createElement("button");
      button.classList.add("btn", "btn-danger", "btn-sm", "me-2");
      button.textContent = "Excluir";
      button.dataset.bsToggle = "modal";
      button.dataset.bsTarget = "#confirme-modal";

      button.addEventListener("click", function () {
        document.getElementById("id-value").value = ocorrencia.id;
      });
      actionCell.appendChild(button);
    }
    const button = document.createElement("button");
    button.classList.add("btn", "btn-warning", "btn-sm", "me-2");
    button.textContent = "Exibir-Detalhes";
    button.dataset.bsToggle = "modal";
    button.dataset.bsTarget = "#exampleModal";
    button.onclick = () => exibirDetalhes(ocorrencia);
    actionCell.appendChild(button);
  });

  const div = document.createElement("div");
  div.className = "table-responsive";
  div.appendChild(table);
  container.innerHTML = "";
  container.appendChild(div);
}

async function cancelarOcorencia() {
  token.validateSecurity();
  showLoading();

  const reservaId = document.getElementById("id-value").value;

  try {
    const response = await fetch(
      `${baseUrl}/api/v1/ocorrencia/cancel/${reservaId}`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: "Bearer " + token.getToken(),
        },
      }
    );

    if (response.ok) {
      showToast(
        "Sucesso",
        "Ocorrência cancelada com sucesso!",
        "bg-success",
        5000
      );
      getOcorrencias();
      return;
    }
    if (response.status === 404 || response.status === 400) {
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
      showToast("Erro", "Erro ao cancelar a ocorrência!", "bg-danger", 5000);
    }
  } catch (error) {
    showToast("Erro", "Erro ao cancelar a ocorrência!", "bg-danger", 5000);
  } finally {
    hideLoading();
  }
}

function exibirDetalhes(ocorrencia) {
  const modalAreaText = document.getElementById("texte-ocorencia-descricao");
  const modalTitle = document.getElementById("texte-ocorencia-title");
  const modalAreaTextResolution = document.getElementById(
    "texte-ocorencia-resolution"
  );

  modalTitle.value = ocorrencia.title;
  modalAreaText.value = ocorrencia.description;

  if (ocorrencia.resolution) {
    modalAreaTextResolution.value = ocorrencia.resolution;
  } else {
    modalAreaTextResolution.value = "Nenhuma resolução fornecida.";
  }
}

document
  .getElementById("save-ocorrencia")
  .addEventListener("submit", function (e) {
    e.preventDefault();
    showLoading();
    saveOcorrencia();
  });

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

document
  .getElementById("bnt-confirme-sim")
  .addEventListener("click", function (event) {
    const modalElement = document.getElementById("confirme-modal");
    const modalInstance =
      bootstrap.Modal.getInstance(modalElement) ||
      new bootstrap.Modal(modalElement);
    modalInstance.hide();
    cancelarOcorencia();
  });
