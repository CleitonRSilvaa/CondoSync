import * as token from "/js/auth.js";

const baseUrl =
  "https://condosyn.eastus.cloudapp.azure.com:4433/api/v1/notifications";
document.addEventListener("DOMContentLoaded", () => {
  token.validateSecurity();
  registerServiceWorker();
});

const registerServiceWorker = async () => {
  if ("serviceWorker" in navigator && "PushManager" in window) {
    try {
      const permission = await Notification.requestPermission();
      if (permission !== "granted") {
        // console.warn("Permissão para notificações não foi concedida.");
        return;
      }

      const registration = await navigator.serviceWorker.register("/js/sw.js", {
        scope: "/js/",
      });
      // console.log("Service Worker registrado com sucesso:", registration);

      const publicVapidKey = await getPublicKey();
      if (!publicVapidKey) {
        // console.error("Chave pública não pôde ser obtida.");
        return;
      }

      let subscription = await registration.pushManager.getSubscription();
      if (!subscription) {
        subscription = await registration.pushManager.subscribe({
          userVisibleOnly: true,
          applicationServerKey: urlBase64ToUint8Array(publicVapidKey),
        });
        await saveSubscribe(subscription);
      }
    } catch (error) {
      // console.error(`Falha ao registrar o Service Worker: ${error}`);
    }
  } else {
    // console.error(
    //   "Service Worker ou Push Manager não são suportados no navegador."
    // );
  }
};

async function getPublicKey() {
  try {
    let url = baseUrl + "/public-key";
    const response = await fetch(url, {
      method: "GET",
      headers: {
        Accept: "application/text",
        "Content-Type": "application/text",
        Authorization: "Bearer " + token.getToken(),
      },
    });
    if (response.ok) {
      return await response.text();
    }
    // console.error("Falha ao obter a chave pública: Resposta não OK");
    return null;
  } catch (error) {
    // console.error("Erro ao obter a chave pública do servidor:", error);
    return null;
  }
}

async function saveSubscribe(subscription) {
  try {
    let url = baseUrl + "/subscribe";
    const response = await fetch(url, {
      method: "POST",
      headers: {
        Accept: "application/json",
        "Content-Type": "application/json",
        Authorization: "Bearer " + token.getToken(),
      },
      body: JSON.stringify(subscription),
    });
    if (response.ok) {
      return;
    }
    // console.error("Falha ao salvar a assinatura: Resposta não OK");
    return null;
  } catch (error) {
    // console.error("Erro ao salvar a assinatura:", error);
    return null;
  }
}

function urlBase64ToUint8Array(base64String) {
  const padding = "=".repeat((4 - (base64String.length % 4)) % 4);
  const base64 = (base64String + padding)
    .replace(/\-/g, "+")
    .replace(/_/g, "/");
  const rawData = window.atob(base64);
  const outputArray = new Uint8Array(rawData.length);
  for (let i = 0; i < rawData.length; ++i) {
    outputArray[i] = rawData.charCodeAt(i);
  }
  return outputArray;
}
