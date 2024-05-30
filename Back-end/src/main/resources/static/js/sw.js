self.addEventListener("install", (event) => {
  event.waitUntil(self.skipWaiting());
});

self.addEventListener("activate", (event) => {
  event.waitUntil(self.clients.claim());
});

self.addEventListener("push", function (event) {
  const getData = async (event) => {
    if (!(self.Notification && self.Notification.permission === "granted")) {
      return;
    }

    try {
      let data = {
        title: "Push Notification",
        body: "Você tem uma nova notificação.",
        icon: "images/icon.png",
        data: {
          dateOfArrival: Date.now(),
          primaryKey: "1",
        },
      };

      if (event.data) {
        try {
          data = event.data.json();
        } catch (error) {
          data.body = event.data.text();
        }
      }

      console.log("data", data);

      const options = {
        body: data.body,
        icon: data.icon,
        vibrate: [100, 50, 100],
        data: {
          dateOfArrival: Date.now(),
          primaryKey: data.primaryKey || "1",
        },
        actions: data.actions || [],
      };

      event.waitUntil(self.registration.showNotification(data.title, options));
    } catch (error) {
      console.error("Error handling push event:", error);
    }
  };

  getData(event);
});

self.addEventListener("notificationclick", (event) => {
  event.notification.close();
  console.log("notificationclick", event.notification.data);

  let url = event.notification.data.url || "/";
  event.waitUntil(clients.openWindow(url));
});
