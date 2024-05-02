const registerServiceWorker = async () => {
    if ("serviceWorker" in navigator) {
      try {
        const registration = await navigator.serviceWorker.register("/sw.js", {
          scope: "/",
        }).then((registration) => {

            console.log("Service worker registered");

            registration.pushManager.getSubscription().then((subscription) => {
                if (subscription) {
                  console.log("Already subscribed", subscription.endpoint);
                  return;

                }
            });
                // registration.pushManager
                //   .subscribe({
                //     userVisibleOnly: true,
                //     applicationServerKey: urlBase64ToUint8Array(
                //       "BBlb7FJzvLQXe0KJpK5zX4t7Q5jv6H6DQZ6MvZ8W")


            
        });
        if (registration.installing) {
          console.log("Service worker installing");
        } else if (registration.waiting) {
          console.log("Service worker installed");
        } else if (registration.active) {
          console.log("Service worker active");
        }
      } catch (error) {
        console.error(`Registration failed with ${error}`);
      }
    }
};
  


function notifyMe(teste) {
  if (!("Notification" in window)) {
    // Check if the browser supports notifications
    alert("This browser does not support desktop notification");
  } else if (Notification.permission === "granted") {
    // Check whether notification permissions have already been granted;
    // if so, create a notification
    const notification = new Notification("Hi there!");
    // …
  } else if (Notification.permission !== "denied") {
    // We need to ask the user for permission
    Notification.requestPermission().then((permission) => {
      // If the user accepts, let's create a notification
      if (permission === "granted") {
        const notification = new Notification("Hi there!");
        // …
      }
    });
  }

  // At last, if the user has denied notifications, and you
  // want to be respectful there is no need to bother them anymore.
}


  
registerServiceWorker();

  

