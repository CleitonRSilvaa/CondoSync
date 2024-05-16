const baseUrl = "http://localhost:8010";

document.addEventListener("DOMContentLoaded", () => {
  loadDate();
  loadCalendar();
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

function loadCalendar() {
  const calendar = new VanillaCalendar({
    selector: "#myCalendar",
    onSelect: (data, elem) => {
      console.log(data, elem);
    },
  });
}

function feachMy(url, method, body) {
  return fetch(url, {
    method: method,
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(body),
  })
    .then((response) => response.json())
    .then((data) => {
      console.log("Success:", data);
      return data;
    })
    .catch((error) => {
      console.error("Error:", error);
    });
}

function getReservas() {
  feachMy("http://localhost:8080/reservas", "GET", {}).then((data) => {
    console.log(data);
  });
}

function createReserva() {
  const reserva = {
    data: "2022-05-04",
    horario: "08:30 - 09:00",
    espaco: "Churrasqueira",
  };
  feachMy("http://localhost:8080/reservas", "POST", reserva).then((data) => {
    console.log(data);
  });
}

function deleteReserva() {
  feachMy("http://localhost:8080/reservas/1", "DELETE", {}).then((data) => {
    console.log(data);
  });
}

function updateReserva() {
  const reserva = {
    data: "2022-05-04",
    horario: "08:30 - 09:00",
    espaco: "Churrasqueira",
  };
  feachMy("http://localhost:8080/reservas/1", "PUT", reserva).then((data) => {
    console.log(data);
  });
}

function cancelReserva() {
  feachMy("http://localhost:8080/reservas/1/cancelar", "PUT", {}).then(
    (data) => {
      console.log(data);
    }
  );
}

function getEspacos() {
  showLoading();
  fetch(baseUrl + "/api/v1/area/list", {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    },
  })
    .then((response) => response.json())
    .then((data) => {
      const select = document.getElementById("espacos");
      select.innerHTML = "";
      const option = document.createElement("option");
      option.value = "";
      option.text = "Selecione um espaço";
      select.appendChild(option);

      data.forEach((espaco) => {
        const option = document.createElement("option");
        option.value = espaco.id;
        option.text = espaco.name;
        select.appendChild(option);
      });
    })
    .catch((error) => {
      console.error("Error:", error);
    })
    .finally(() => {
      hideLoading();
    });
}

document.getElementById("data-reserva").addEventListener("change", (event) => {
  const data = event.target.value;
  if (data) {
    getEspacos();
    changeDesabilitarEspacos(false);
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

function showLoading() {
  document.getElementById("loading-reservar").style.display = "block";
}

function hideLoading() {
  document.getElementById("loading-reservar").style.display = "none";
}

function getHorarios(areaId) {
  fetch(baseUrl + "/api/v1/area/" + areaId + "/schedule", {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    },
  })
    .then((response) => {
      const status = response.status;
      return response.json().then((data) => ({ data, status }));
    })
    .then((data) => {
      const select = document.getElementById("horarios");
      select.innerHTML = "";
      const option = document.createElement("option");
      if (data.status === 404) {
        select.appendChild(option);
        changeDesabilitarHorarios();
        showToast(
          "Opss!",
          "Nenhum horário disponível para este espaço e data!",
          "bg-warning",
          7000
        );
        //alert("Nenhum horário disponível para este espaço");
        changeDesabilitaBntReservar();
        return;
      }
      data = data.data;
      option.value = data.id;
      option.text = data.horaInicio + " - " + data.horaFim;
      select.appendChild(option);
      changeDesabilitarHorarios(false);
      changeDesabilitaBntReservar(false);
    })
    .catch((error) => {
      console.error("Error:", error);
    })
    .finally(() => {
      hideLoading();
    });
}

function showToast(titulo, message, clss = "bg-primary", time = 5000) {
  const toastContainer = document.getElementById("toastContainer");

  // Cria um novo elemento toast
  const toastEl = document.createElement("div");
  toastEl.className = `toast align-items-center text-white border-0 ${clss}`;
  toastEl.setAttribute("role", "alert");
  toastEl.setAttribute("aria-live", "assertive");
  toastEl.setAttribute("aria-atomic", "true");
  toastEl.dataset.bsDelay = time;

  // HTML interno do toast
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

  // Adiciona o toast ao contêiner
  toastContainer.appendChild(toastEl);

  // Inicializa e mostra o toast
  const toast = new bootstrap.Toast(toastEl);
  toast.show();
}

function saveReserva() {
  const data = document.getElementById("data-reserva").value;
  const horario = document.getElementById("horarios").value;
  const espaco = document.getElementById("espacos").value;

  const reserva = {
    moradorId: "4c74ca58-c3d9-487c-a041-cb92d3fe4d53",
    data: data,
    horarioId: parseInt(horario),
    areaId: espaco,
  };

  fetch(baseUrl + "/api/v1/area/reservation", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(reserva),
  })
    .then((response) => {
      const status = response.status;
      return response.json().then((data) => ({ data, status }));
    })
    .then((data) => {
      if (data.status >= 200 && data.status < 300) {
        showToast(
          "Sucesso",
          "Reserva realizada com sucesso!",
          "bg-success",
          5000
        );
      } else {
        showToast("Erro", "Erro ao realizar a reserva!", "bg-danger", 5000);
      }
    })
    .catch((error) => {
      console.error("Error:", error);
    })
    .finally(() => {
      hideLoading();
    });
}

function updateToastClass(element, newClass) {
  element.classList.remove("bg-primary", "bg-success", "bg-danger");
  element.classList.add(newClass);
}

document.getElementById("btn-reservar").addEventListener("click", function () {
  showLoading();
  saveReserva();
  // setTimeout(function () {
  //   hideLoading();
  // }, 3000);
});
