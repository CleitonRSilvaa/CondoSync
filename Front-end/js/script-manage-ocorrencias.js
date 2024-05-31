import zod from "https://cdn.jsdelivr.net/npm/zod@3.23.8/+esm";

import * as token from "/js/auth.js";

const baseUrl = "https://condosyn.eastus.cloudapp.azure.com:4433";

document.addEventListener("DOMContentLoaded", () => {
  token.validateSecurity();
  getOcorrencias();
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

async function getOcorrencias() {
  token.validateSecurity();
  showLoading();

  try {
    const response = await fetch(`${baseUrl}/api/v1/ocorrencia/listAll`, {
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
        p.textContent = "Nenhuma ocorrência encontrada.";
        p.classList.add("text-center", "text-uppercase", "fw-bold");
        container.innerHTML = "";
        container.appendChild(p);
        return;
      }

      buildTable(data);
      return;
    }
    if (response.status === 403 || response.status === 401) {
      showToast(
        "Atenção",
        "Você não tem permissão para acessar essa página!",
        "bg-warning",
        7000
      );
      return;
    }
    if (response.status === 400 || response.status === 404) {
      showToast("Atenção", data.message, "bg-warning", 7000);
      return;
    }
    showToast("Atenção", "Erro ao buscar as ocorrências!", "bg-warning", 7000);
  } catch (error) {
    showToast("Erro", "Erro ao buscar as ocorrências!", "bg-danger", 5000);
  } finally {
    hideLoading();
  }
}
const StatusOcorrencia = Object.freeze({
  ABERTO: "Em aberto",
  EM_ANDAMENTO: "Em andamento",
  RESOLVIDA: "Resolvida",
  CANCELADA: "Cancelada",
});

function inverterObjeto(obj) {
  const objetoInvertido = {};
  for (const [chave, valor] of Object.entries(obj)) {
    objetoInvertido[valor] = chave;
  }
  return Object.freeze(objetoInvertido);
}

function buildTable(data) {
  const container = document.getElementById("ocorrencias-container");
  const table = document.createElement("table");
  table.className = "table table-striped table-hover";
  table.innerHTML = `
    <thead class="table-dark">
      <tr>
        <th scope="col">ID</th>
        <th scope="col">Titulo</th>
        <th scope="col">Morador</th>
        <th scope="col">Data</th>
        <th scope="col">Status</th>
        <th scope="col">Ações</th>
      </tr>
    </thead>
    <tbody>
    </tbody>
  `;

  const tbody = table.querySelector("tbody");

  let count = 1;
  data.forEach((ocorrencia) => {
    const row = tbody.insertRow();

    row.insertCell().textContent = ocorrencia.id;
    row.insertCell().textContent = ocorrencia.title;
    row.insertCell().textContent = ocorrencia.morador.nomeCompleto;
    row.insertCell().textContent = ocorrencia.creation;
    row.insertCell().textContent = ocorrencia.status;

    const actionCell = row.insertCell();
    actionCell.className = "text-center";

    const isResolvida =
      ocorrencia.status === StatusOcorrencia.RESOLVIDA.toString();

    if (
      ocorrencia.status === StatusOcorrencia.CANCELADA.toString() ||
      ocorrencia.status === StatusOcorrencia.RESOLVIDA.toString()
    ) {
      const button = document.createElement("button");
      button.classList.add(
        "btn",
        isResolvida ? "btn-success" : "btn-secondary",
        "btn-sm",
        "me-2"
      );
      button.textContent = "Visualizar";
      button.dataset.bsToggle = "modal";
      button.dataset.bsTarget = "#exampleModal";
      button.onclick = () => changeModalEdit(ocorrencia);
      actionCell.appendChild(button);
    } else {
      const button = document.createElement("button");
      button.classList.add("btn", "btn-warning", "btn-sm", "me-2");
      button.textContent = "Resolver";
      button.dataset.bsToggle = "modal";
      button.dataset.bsTarget = "#exampleModal";
      button.onclick = () => changeModalEdit(ocorrencia);
      actionCell.appendChild(button);
    }
  });

  const div = document.createElement("div");
  div.className = "table-responsive";
  div.appendChild(table);
  container.innerHTML = "";
  container.appendChild(div);
}

function changeModalEdit(ocorrencia) {
  const modal = document.getElementById("exampleModal");
  const modalTitle = modal.querySelector(".modal-title");
  const modalFooter = modal.querySelector(".modal-footer");
  modalTitle.textContent = `Ocorrência ${
    ocorrencia.id + " - " + ocorrencia.creation
  }`;

  const moradorInfo = document.getElementById("moradorInfo");
  moradorInfo.value = `${ocorrencia.morador.nomeCompleto} - AP: ${
    ocorrencia.morador.apartamento
  } - Celular: ${
    ocorrencia.morador.celular ? ocorrencia.morador.celular : "Não informado"
  }`;

  const tituloOcorrencia = document.getElementById("tituloOcorrencia");
  tituloOcorrencia.value = ocorrencia.title;
  const descricaoOcorrencia = document.getElementById("descricaoOcorrencia");
  descricaoOcorrencia.value = ocorrencia.description;
  const statusOcorrencia = document.getElementById("statusOcorrencia");
  // statusOcorrencia.value = ocorrencia.status;
  const respostaOcorrencia = document.getElementById("respostaOcorrencia");
  respostaOcorrencia.value = ocorrencia.resolution ? ocorrencia.resolution : "";

  const StatusOcorrenciaInvertido = inverterObjeto(StatusOcorrencia);
  statusOcorrencia.innerHTML = "";
  for (const [chave, valor] of Object.entries(StatusOcorrenciaInvertido)) {
    if (ocorrencia.status !== "Em aberto" && valor === "ABERTO") {
      continue;
    }

    const option = document.createElement("option");
    option.value = valor;
    option.textContent = chave;

    if (ocorrencia.status === chave) {
      option.selected = true;
    }

    statusOcorrencia.appendChild(option);
  }

  modalFooter.innerHTML = "";
  const buttonFechar = document.createElement("button");
  buttonFechar.classList.add("btn", "btn-secondary");
  buttonFechar.textContent = "Fechar";
  buttonFechar.setAttribute("data-bs-dismiss", "modal");
  modalFooter.appendChild(buttonFechar);

  if (
    ocorrencia.status === "Em aberto" ||
    ocorrencia.status === "Em andamento"
  ) {
    const button = document.createElement("button");
    button.classList.add("btn", "btn-success");
    button.textContent = "Resolver";
    button.onclick = () => resolverOcorrencia(ocorrencia);
    modalFooter.appendChild(button);
  }
  statusOcorrencia.disabled = false;
  respostaOcorrencia.disabled = false;
  if (ocorrencia.status === "Resolvida" || ocorrencia.status === "Cancelada") {
    const bntMudaStatus = document.createElement("button");
    bntMudaStatus.classList.add("btn", "btn-warning");
    bntMudaStatus.textContent = "Mudar Status";
    bntMudaStatus.onclick = () => resolverOcorrencia(ocorrencia);
    modalFooter.appendChild(bntMudaStatus);
    respostaOcorrencia.disabled = true;
  }
}

async function resolverOcorrencia(ocorrencia) {
  const statusOcorrencia = document.getElementById("statusOcorrencia").value;
  const respostaOcorrencia =
    document.getElementById("respostaOcorrencia").value;

  const resolverOcorrenciaSchema = zod.object({
    id: zod.number(),
    status: zod.string(),
    resolution: zod.string(),
  });

  const resolverOcorrenciaData = resolverOcorrenciaSchema.parse({
    id: ocorrencia.id,
    resolution: respostaOcorrencia,
    status: statusOcorrencia,
  });

  showLoading();

  try {
    const response = await fetch(
      `${baseUrl}/api/v1/ocorrencia/update/` + ocorrencia.id,
      {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: "Bearer " + token.getToken(),
        },
        body: JSON.stringify(resolverOcorrenciaData),
      }
    );

    const data = await response.json();

    if (response.ok) {
      closeModal();
      showToast(
        "Sucesso",
        "Ocorrência atualizado com sucesso!",
        "bg-success",
        5000
      );
      await getOcorrencias();
      return;
    }
    if (response.status === 403 || response.status === 401) {
      showToast(
        "Atenção",
        "Você não tem permissão para acessar essa página!",
        "bg-warning",
        7000
      );
      return;
    }
    if (response.status === 400 || response.status === 404) {
      showToast("Atenção", data.message, "bg-warning", 7000);
      return;
    }
    showToast("Atenção", "Erro ao resolver a ocorrência!", "bg-warning", 7000);
  } catch (error) {
    showToast("Erro", "Erro ao resolver a ocorrência!", "bg-danger", 5000);
  } finally {
    hideLoading();
  }
}

function closeModal() {
  const modal = document.getElementById("exampleModal");
  const modalInstance =
    bootstrap.Modal.getInstance(modal) || new bootstrap.Modal(modal);
  modalInstance.hide();
}

document.getElementById("btn-logout").addEventListener("click", token.logout);
