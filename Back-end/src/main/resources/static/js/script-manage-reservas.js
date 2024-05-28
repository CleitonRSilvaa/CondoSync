import zod from "https://cdn.jsdelivr.net/npm/zod@3.23.8/+esm";

import * as token from "/js/auth.js";

const baseUrl = "https://200.155.171.178:16580";

document.addEventListener("DOMContentLoaded", () => {
  token.validateSecurity();

  if (!token.isLoggedAdmin()) {
    window.location.href = "../home/index.html";
  }
  geReservas();
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

async function geReservas() {
  token.validateSecurity();
  showLoading();

  try {
    const response = await fetch(`${baseUrl}/api/v1/reserva/list`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + token.getToken(),
      },
    });
    const data = await response.json();

    if (response.ok) {
      if (data.length === 0) {
        const container = document.getElementById("reservas-container");
        const p = document.createElement("p");
        p.textContent = "Nenhuma reserva encontrada.";
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
    showToast("Atenção", "Erro ao buscar as reservas!", "bg-warning", 7000);
  } catch (error) {
    showToast("Erro", "Erro ao buscar as reservas!", "bg-danger", 5000);
  } finally {
    hideLoading();
  }
}

const StatusReserva = Object.freeze({
  PENDENTE: "Pendente",
  APROVADA: "Aprovada",
  FINALIZADA: "Finalizada",
  REJEITADA: "Rejeitada",
  CANCELADA: "Cancelada",
});

function inverterObjeto(obj) {
  const objetoInvertido = {};
  for (const [chave, valor] of Object.entries(obj)) {
    objetoInvertido[valor] = chave;
  }
  return Object.freeze(objetoInvertido);
}
const collor = {
  PENDENTE: "text-warning",
  APROVADA: "text-success",
  FINALIZADA: "text-primary",
  REJEITADA: "text-danger",
  CANCELADA: "text-secondary",
};

function buildTable(data) {
  const container = document.getElementById("reservas-container");
  const table = document.createElement("table");
  table.className = "table table-striped table-hover";
  table.innerHTML = `
    <thead class="table-dark">
      <tr>
        <th scope="col">ID</th>
        <th scope="col">Espaço</th>
        <th scope="col">Data</th>
        <th scope="col">Horário</th>
        <th scope="col">Morador</th>
        <th scope="col">Status</th>
        <th scope="col" colspan="1" class="text-center">Ações</th>
      </tr>
    </thead>
    <tbody>
    </tbody>
  `;

  const tbody = table.querySelector("tbody");

  let count = 1;
  data.forEach((reserva) => {
    const row = tbody.insertRow();
    row.insertCell().textContent = reserva.id;
    row.insertCell().textContent = reserva.area;
    row.insertCell().textContent = reserva.data;
    row.insertCell().textContent = reserva.horario;
    row.insertCell().textContent = reserva.morador;
    const cell = row.insertCell();
    cell.textContent = reserva.status;

    cell.classList.add(collor[inverterObjeto(StatusReserva)[reserva.status]]);

    const actionCell = row.insertCell();
    actionCell.className = "text-center";
    if (
      reserva.status === StatusReserva.PENDENTE.toString() ||
      reserva.status === StatusReserva.APROVADA.toString()
    ) {
      const button = document.createElement("button");
      button.classList.add("btn", "btn-warning", "btn-sm", "me-2");
      button.textContent = "Validar";
      button.dataset.bsToggle = "modal";
      button.dataset.bsTarget = "#exampleModal";
      button.onclick = () => changeModalEdit(reserva);
      actionCell.appendChild(button);
    }
  });

  const div = document.createElement("div");
  div.className = "table-responsive";
  div.appendChild(table);
  container.innerHTML = "";
  container.appendChild(div);
}

function changeModalEdit(reserva) {
  console.log(reserva);
  const modal = document.getElementById("exampleModal");
  const modalTitle = modal.querySelector(".modal-title");
  const modalFooter = modal.querySelector(".modal-footer");
  modalTitle.textContent = `Reserva ${reserva.id + " - " + reserva.data}`;

  const statusReserva = document.getElementById("statusReserva");
  // statusreserva.value = reserva.status;
  const respostareserva = document.getElementById("respostaReserva");
  respostareserva.value = reserva.resolution ? reserva.resolution : "";

  const StatusReservaInvertido = inverterObjeto(StatusReserva);
  statusReserva.innerHTML = "";
  for (const [chave, valor] of Object.entries(StatusReservaInvertido)) {
    if (
      (reserva.status !== "Pendente" &&
        valor === "PENDENTE" &&
        reserva.status !== "Aprovada") ||
      valor === "FINALIZADA"
    ) {
      continue;
    }

    const option = document.createElement("option");
    option.value = valor;
    option.textContent = chave;

    if (reserva.status === chave) {
      option.selected = true;
    }

    statusReserva.appendChild(option);
  }

  modalFooter.innerHTML = "";
  const buttonFechar = document.createElement("button");
  buttonFechar.classList.add("btn", "btn-secondary");
  buttonFechar.textContent = "Fechar";
  buttonFechar.setAttribute("data-bs-dismiss", "modal");
  modalFooter.appendChild(buttonFechar);

  const buttonSalvar = document.createElement("button");
  buttonSalvar.classList.add("btn", "btn-success");
  buttonSalvar.textContent = "Salvar";
  buttonSalvar.onclick = () => resolverReserva(reserva);
  modalFooter.appendChild(buttonSalvar);

  // if (
  //   ocorrencia.status === "Em aberto" ||
  //   ocorrencia.status === "Em andamento"
  // ) {
  //   const button = document.createElement("button");
  //   button.classList.add("btn", "btn-success");
  //   button.textContent = "Resolver";
  //   button.onclick = () => resolverOcorrencia(ocorrencia);
  //   modalFooter.appendChild(button);
  // }
  // statusOcorrencia.disabled = false;
  // respostaOcorrencia.disabled = false;
  // if (ocorrencia.status === "Resolvida" || ocorrencia.status === "Cancelada") {
  //   const bntMudaStatus = document.createElement("button");
  //   bntMudaStatus.classList.add("btn", "btn-warning");
  //   bntMudaStatus.textContent = "Mudar Status";
  //   bntMudaStatus.onclick = () => resolverOcorrencia(ocorrencia);
  //   modalFooter.appendChild(bntMudaStatus);
  //   respostaOcorrencia.disabled = true;
  // }
}

function closeModal() {
  const modal = document.getElementById("exampleModal");
  const modalInstance =
    bootstrap.Modal.getInstance(modal) || new bootstrap.Modal(modal);
  modalInstance.hide();
}

async function resolverReserva(reserva) {
  const statusReserva = document.getElementById("statusReserva").value;
  const respostaReserva = document.getElementById("respostaReserva").value;

  const resolverReservaData = {
    id: reserva.id,
    status: statusReserva.toString(),
  };

  console.log(resolverReservaData);

  showLoading();

  console.log("json", JSON.stringify(resolverReservaData));

  try {
    const response = await fetch(
      `${baseUrl}/api/v1/reserva/update/` + reserva.id,
      {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: "Bearer " + token.getToken(),
        },
        body: JSON.stringify(resolverReservaData),
      }
    );

    if (response.ok) {
      closeModal();
      showToast(
        "Sucesso",
        "Reserva atualizada com sucesso!",
        "bg-success",
        5000
      );
      await geReservas();
      return;
    }
    const data = await response.json();

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
    showToast("Atenção", "Erro ao resolver a reserva!", "bg-warning", 7000);
  } catch (error) {
    showToast("Erro", "Erro ao resolver a reserva!", "bg-danger", 5000);
  } finally {
    hideLoading();
  }
}

document.getElementById("statusReserva").addEventListener("change", (event) => {
  console;
  const respostareserva = document.getElementById("respostaReserva");
  const respostareservadiv = document.getElementById("respostaReserva-div");

  if (
    event.target.value === "REJEITADA" ||
    event.target.value === "CANCELADA"
  ) {
    respostareserva.disabled = false;
    //respostareservadiv.style.display = "block";
    respostareservadiv.removeAttribute("hidden");
  } else {
    respostareserva.value = "";
    respostareserva.disabled = true;
    respostareservadiv.setAttribute("hidden", true);
    //respostareservadiv.style.display = "none";
  }
});

document.getElementById("btn-logout").addEventListener("click", token.logout);
