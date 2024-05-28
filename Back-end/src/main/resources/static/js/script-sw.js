import * as token from "/js/auth.js";

const baseUrl = "https://200.155.171.178:16580/api/v1/notifications";
document.addEventListener("DOMContentLoaded", () => {
  token.validateSecurity();

  registerServiceWorker();
});

const registerServiceWorker = async () => {
  if ("serviceWorker" in navigator) {
    try {
      const permission = await Notification.requestPermission();
      if (permission !== "granted") {
        return;
      }

      const registration = await navigator.serviceWorker.register("/js/sw.js", {
        scope: "/js/",
      });
      const publicVapidKey = await getPublicKey();
      let subscription = await registration.pushManager.getSubscription();
      if (!subscription) {
        subscription = await registration.pushManager.subscribe({
          userVisibleOnly: true,
          applicationServerKey: urlBase64ToUint8Array(publicVapidKey),
        });
        await saveSubscribe(subscription);
      }
    } catch (error) {
      console.error(`Service Worker registration failed with ${error}`);
    }
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
    return null;
  } catch (error) {
    console.log("Error getting key from server:", error);
    return null;
  }
}

async function saveSubscribe(subscription) {
  try {
    // subscription.user_id = token.getUserId();
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
    return null;
  } catch (error) {
    console.log("Error saving subscription:", error);
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
