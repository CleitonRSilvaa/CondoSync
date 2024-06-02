import * as token from "/js/auth.js";

const baseUrl = "https://condosyn.eastus.cloudapp.azure.com:4433";

document.addEventListener("DOMContentLoaded", () => {
  token.validateSecurity();
  getDadosMural();
  buildProfile();
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

document.getElementById("btn-logout").addEventListener("click", token.logout);

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
        buildMural(dataUnicoAviso, true);
        return;
      }

      buildMural(data);
    } else {
      buildMural(dataUnicoAviso, true);
    }
  } catch (error) {
    buildMural(dataUnicoAviso, true);
  } finally {
    hideLoading();
  }
}

const dataUnicoAviso = [
  {
    title: "Sem avisos no momento!",
    description: "Nenhum aviso foi cadastrado no momento.",
    image: {
      base64: "/imagens/imagensMural/mural-de-avisos_thumb2.png",
    },
  },
];

function buildMural(data, unicoAviso = false) {
  const muralCaroussel = document.getElementById("carouselMural");
  const muralIndicators = document.getElementById("carouselMural-indicators");
  const muralItems = document.getElementById("carouselMural-items");

  data.forEach((item, index) => {
    const indicator = document.createElement("button");
    indicator.type = "button";
    indicator.dataset.bsTarget = "#carouselMural";
    indicator.dataset.bsSlideTo = index;
    if (index === 0) {
      indicator.classList.add("active");
      indicator.setAttribute("aria-current", "true");
    }
    indicator.setAttribute("aria-label", `Slide ${index + 1}`);
    muralIndicators.appendChild(indicator);

    const itemDiv = document.createElement("div");
    itemDiv.classList.add("carousel-item");
    if (index === 0) {
      itemDiv.classList.add("active");
    }
    const img = document.createElement("img");
    img.src = item.image.base64;
    img.classList.add("d-block", "w-auto", "img-fluid", "rounded", "mx-auto");
    itemDiv.appendChild(img);

    if (!unicoAviso) {
      const carouselCaption = document.createElement("div");
      carouselCaption.classList.add(
        "carousel-caption",
        "d-none",
        "d-md-block",
        "bg-dark",
        "text-white",
        "rounded",
        "mx-auto",
        "m-3"
      );
      carouselCaption.style.opacity = 0.9;

      const h5 = document.createElement("h4");
      h5.textContent = item.title;
      carouselCaption.appendChild(h5);

      if (item.description) {
        const p = document.createElement("p");
        p.textContent = item.description;
        carouselCaption.appendChild(p);
      }
      itemDiv.appendChild(carouselCaption);
    }

    muralItems.appendChild(itemDiv);
  });
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
