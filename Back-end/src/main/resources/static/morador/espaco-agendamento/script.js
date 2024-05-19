const baseUrl = "http://localhost:8010";

import * as tokem from "/js/auth.js";

function validateSecurity() {
  if (!tokem.isLogged()) {
    window.location.href = "../Login/login.html";
  }
  if (tokem.isExpiredToken()) {
    alert("Sua sessão expirou!");
    tokem.logout();
  }
}

document.addEventListener("DOMContentLoaded", () => {
  validateSecurity();

  // console.log(tokem.getPayload());

  loadDate();
  getReservas();
  changeDesabilitarHorarios();
  changeDesabilitarEspacos();
  changeDesabilitaBntReservar();
  const inputData = document.getElementById("data-reserva");

  inputData.addEventListener("input", function () {
    if (inputData.value === "") {
      changeDesabilitarHorarios();
      changeDesabilitaBntReservar();
      changeDesabilitarEspacos();
    }
  });
});
function loadDate() {
  const startDate = document.getElementById("data-reserva");
  const date_default = new Date();
  date_default.setDate(date_default.getDate() + 1);

  const date_max = new Date();
  date_max.setDate(date_max.getDate() + 60);

  const date_min = new Date();
  date_min.setDate(date_min.getDate() + 1);

  // startDate.value = date_default.toISOString().split('T')[0];
  startDate.min = date_default.toISOString().split("T")[0];
  startDate.max = date_max.toISOString().split("T")[0];
}

async function getEspacos() {
  validateSecurity();
  showLoading();
  try {
    const response = await fetch(baseUrl + "/api/v1/area/list", {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + tokem.getToken(),
      },
    });

    const data = await response.json();

    if (response.ok) {
      const select = document.getElementById("espacos");
      select.innerHTML = "";
      const option = document.createElement("option");
      option.value = "";
      option.text = "Selecione um espaço";
      select.appendChild(option);

      if (data.length === 0) {
        showToast("Opss!", "Nenhum espaço disponível !", "bg-warning", 7000);
        changeDesabilitarEspacos();
        changeDesabilitarHorarios();
        changeDesabilitaBntReservar();
        return;
      }

      data.forEach((espaco) => {
        const option = document.createElement("option");
        option.value = espaco.id;
        option.text = espaco.name;
        select.appendChild(option);
      });
    } else {
      showToast("Erro", "Erro ao buscar os espaços!", "bg-danger", 5000);
    }
  } catch (error) {
    showToast("Erro", "Erro ao buscar os espaços!", "bg-danger", 5000);
    console.error("Error:", error);
  } finally {
    hideLoading();
  }
}

async function getReservas() {
  validateSecurity();
  showLoading();

  try {
    const params = new URLSearchParams({ userName: tokem.getUserName() });

    const response = await fetch(
      `${baseUrl}/api/v1/reserva/list?${params.toString()}`,
      {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: "Bearer " + tokem.getToken(),
        },
      }
    );

    const data = await response.json();

    if (response.ok) {
      if (data.length === 0) {
        const container = document.getElementById("reservas-container");
        const p = document.createElement("p");
        p.textContent = "Nenhuma reserva encontrada!";
        p.classList.add("text-center", "text-uppercase", "fw-bold");
        container.innerHTML = "";
        container.appendChild(p);
        return;
      }

      buildTable(data);
    } else {
      showToast("Erro", "Erro ao buscar as reservas!", "bg-danger", 5000);
    }
  } catch (error) {
    showToast("Erro", "Erro ao buscar as reservas!", "bg-danger", 5000);
    console.error("Error:", error);
  } finally {
    hideLoading();
  }
}

function buildTable(data) {
  const container = document.getElementById("reservas-container");
  const table = document.createElement("table");
  table.className = "table table-striped table-hover";
  table.innerHTML = `
    <thead class="table-dark">
      <tr>
        <th scope="col">#</th>
        <th scope="col">Área</th>
        <th scope="col">Morador</th>
        <th scope="col">Data</th>
        <th scope="col">Horário</th>
        <th scope="col">Status</th>
        <th scope="col">Ações</th>
      </tr>
    </thead>
    <tbody>
    </tbody>
  `;

  const tbody = table.querySelector("tbody");

  let count = 1;
  data.forEach((reserva) => {
    const row = tbody.insertRow();
    row.insertCell().textContent = count++;
    row.insertCell().textContent = reserva.area;
    row.insertCell().textContent = reserva.morador;
    row.insertCell().textContent = reserva.data;
    row.insertCell().textContent = reserva.horario;
    row.insertCell().textContent = reserva.status;

    const actionCell = row.insertCell();
    if (reserva.status.toLowerCase() === "pendente") {
      const button = document.createElement("button");
      button.className = "btn btn-danger";
      button.textContent = "Cancelar";
      button.onclick = () => cancelarReserva(reserva.id);
      actionCell.appendChild(button);
    }
  });

  const div = document.createElement("div");
  div.className = "table-responsive";
  div.appendChild(table);
  container.innerHTML = "";
  container.appendChild(div);
}

document.getElementById("data-reserva").addEventListener("change", (event) => {
  const data = event.target.value;
  if (data) {
    getEspacos();
    changeDesabilitarEspacos(false);
    changeDesabilitarHorarios();
    changeDesabilitaBntReservar();
  }
});

document.getElementById("espacos").addEventListener("change", (event) => {
  showLoading();
  const areaId = event.target.value;
  if (!areaId) {
    changeDesabilitarHorarios();
    hideLoading();
    changeDesabilitaBntReservar();
    return;
  }
  getHorarios(areaId);
});

function changeDesabilitarHorarios(status = true) {
  const horarios = document.getElementById("horarios");
  if (status) {
    horarios.innerHTML = "";
    const option = document.createElement("option");
    option.value = "";
    option.text = "Selecione um espaço";
    horarios.appendChild(option);
  }
  horarios.disabled = status;
}

function changeDesabilitarEspacos(status = true) {
  const espacos = document.getElementById("espacos");

  if (status) {
    espacos.innerHTML = "";
    const option = document.createElement("option");
    option.value = "";
    option.text = "Selecione uma data";
    espacos.appendChild(option);
  }

  espacos.disabled = status;
}

function changeDesabilitaBntReservar(status = true) {
  const data = document.getElementById("btn-reservar");
  data.disabled = status;
}

function changeClearData() {
  const data = document.getElementById("data-reserva");
  data.value = "";
  changeDesabilitarHorarios();
  changeDesabilitarEspacos();
  changeDesabilitaBntReservar();
}

function showLoading() {
  document.getElementById("loading").style.display = "block";
}

function hideLoading() {
  document.getElementById("loading").style.display = "none";
}

async function getHorarios(areaId) {
  validateSecurity();
  showLoading();
  try {
    const date = document.getElementById("data-reserva").value;

    const params = new URLSearchParams({ data: date });

    const respose = await fetch(
      `${baseUrl}/api/v1/area/${areaId}/schedules?${params.toString()}`,
      {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: "Bearer " + tokem.getToken(),
        },
      }
    );

    const data = await respose.json();
    const select = document.getElementById("horarios");
    select.innerHTML = "";
    const option = document.createElement("option");
    if (respose.ok) {
      if (data.length === 0) {
        select.appendChild(option);
        changeDesabilitarHorarios();
        showToast(
          "Opss!",
          "Nenhum horário disponível para este espaço e data!",
          "bg-warning",
          7000
        );
        changeDesabilitaBntReservar();
        return;
      }
      data.forEach((horario) => {
        const option = document.createElement("option");
        option.value = horario.id;
        option.text = horario.horaInicio + " - " + horario.horaFim;
        select.appendChild(option);
      });
      changeDesabilitarHorarios(false);
      changeDesabilitaBntReservar(false);
      return;
    }

    if (respose.status === 404) {
      select.appendChild(option);
      changeDesabilitarHorarios();
      showToast("Opss!", data.error, "bg-warning", 7000);
      changeDesabilitaBntReservar();
      return;
    } else {
      showToast("Erro", "Erro ao buscar os horários!", "bg-danger", 7000);
    }
  } catch (error) {
    showToast("Erro", "Erro ao buscar os horários!", "bg-danger", 7000);
    console.error("Error:", error);
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

  const toast = new bootstrap.Toast(toastEl);
  toast.show();
}

async function saveReserva() {
  validateSecurity();
  const date = document.getElementById("data-reserva").value;
  const horario = document.getElementById("horarios").value;
  const espaco = document.getElementById("espacos").value;

  const reserva = {
    userName: tokem.getUserName(),
    data: date,
    horarioId: parseInt(horario),
    areaId: espaco,
  };

  try {
    const response = await fetch(baseUrl + "/api/v1/area/reservation", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + tokem.getToken(),
      },
      body: JSON.stringify(reserva),
    });
    const data = await response.json();
    if (response.ok) {
      showToast(
        "Sucesso",
        "Reserva realizada com sucesso!",
        "bg-success",
        7000
      );
      changeClearData();
      getReservas();
      return;
    }
    if (response.status === 401) {
    }

    if (response.status === 400 || response.status === 404) {
      showToast("Atenção", data.message, "bg-warning", 7000);
      return;
    }
    if (response.status >= 500) {
      showToast("Erro", "Erro ao realizar a reserva!", "bg-danger", 7000);
      return;
    }
  } catch (error) {
    console.error("Error:", error);
    showToast("Erro", "Erro ao realizar a reserva!", "bg-danger", 5000);
  } finally {
    hideLoading();
  }
}

function updateToastClass(element, newClass) {
  element.classList.remove(
    "bg-primary",
    "bg-success",
    "bg-danger",
    "bg-warning"
  );
  element.classList.add(newClass);
}

document.getElementById("btn-reservar").addEventListener("click", function () {
  showLoading();
  saveReserva();
});

async function cancelarReserva(reservaId) {
  validateSecurity();
  showLoading();
  try {
    const response = await fetch(
      `${baseUrl}/api/v1/reserva/cancel/${reservaId}`,
      {
        method: "POST", // Ou "DELETE", dependendo de como sua API está configurada
        headers: {
          "Content-Type": "application/json",
          Authorization: "Bearer " + tokem.getToken(),
        },
      }
    );

    if (response.ok) {
      showToast(
        "Sucesso",
        "Reserva cancelada com sucesso!",
        "bg-success",
        7000
      );
      getReservas();
      return;
    }
    if (response.status === 404) {
      const data = await response.json();
      showToast("Atenção", data.message, "bg-warning", 7000);
    } else {
      showToast("Erro", "Erro ao cancelar a reserva!", "bg-danger", 5000);
    }
  } catch (error) {
    console.error("Error:", error);
    showToast("Erro", "Erro ao cancelar a reserva!", "bg-danger", 5000);
  } finally {
    hideLoading();
  }
}
