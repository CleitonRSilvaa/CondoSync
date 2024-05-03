<<<<<<< HEAD
const baseUrl = "http://localhost:8020/api/v1/notifications";

const registerServiceWorker = async () => {
    if ("serviceWorker" in navigator) {
        try {

            const permission = await Notification.requestPermission();
            if (permission !== "granted") {
               return;
            }

            const registration = await navigator.serviceWorker.register("/sw.js", { scope: "/" });
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
            method: 'GET',
            headers: {
                'Accept': 'application/text',
                'Content-Type': 'application/text'                
            }
        });
        if (response.ok) {
            return await response.text();
        }
        return null;
    } catch (error) {
        console.log('Error getting key from server:', error);
        return null;
    }
}

async function saveSubscribe(subscription) {
    try {
        let url = baseUrl + "/subscribe";
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'                
            },
            body: JSON.stringify(subscription)
        });
        if (response.ok) {
            const data = await response.json();
            console.log('Subscription saved:', data);
            return data;
        }
        return null;
    } catch (error) {
        console.log('Error saving subscription:', error);
        return null;
    }
}

function urlBase64ToUint8Array(base64String) {
    const padding = '='.repeat((4 - base64String.length % 4) % 4);
    const base64 = (base64String + padding)
        .replace(/\-/g, '+')
        .replace(/_/g, '/');
    const rawData = window.atob(base64);
    const outputArray = new Uint8Array(rawData.length);
    for (let i = 0; i < rawData.length; ++i) {
        outputArray[i] = rawData.charCodeAt(i);
    }
    return outputArray;
}

registerServiceWorker(); 
