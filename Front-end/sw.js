self.addEventListener('install', event => {
    event.waitUntil(
        self.skipWaiting()
    );
});

self.addEventListener('activate', event => {
    event.waitUntil(
        self.clients.claim()
    );
});

self.addEventListener('push', function(event) {
    const getData = async (event) => {

        if(!(self.Notification && self.Notification.permission === 'granted')) {
            return;
        }

        try {
            if (event.data) {
                let data = {};
                try {
                    data = event.data.json(); 
                } catch (error) {
                    data = { title: 'Push Notification', body: event.data.text(), icon: 'images/icon.png', primaryKey: '1' };
                }

                const options = {
                    body: data.body || 'Você tem uma nova notificação.',
                    icon: data.icon || 'images/icon.png',
                    vibrate: [100, 50, 100],
                    data: {
                        dateOfArrival: Date.now(),
                        primaryKey: data.primaryKey || '1'
                    }
                };

                event.waitUntil(
                    self.registration.showNotification(data.title || 'Push Notification', options)
                );
            } else {
                const fallbackOptions = {
                    body: 'Você tem uma nova notificação.',
                    icon: 'images/icon.png',
                    vibrate: [100, 50, 100],
                    data: {
                        dateOfArrival: Date.now(),
                        primaryKey: '2'
                    }
                };
                event.waitUntil(
                    self.registration.showNotification('Push Notification', fallbackOptions)
                );
            }
        } catch (error) {
            console.error('Error handling push event:', error);
        }
    };

    getData(event);
});



self.addEventListener('notificationclick', event => {
    event.notification.close();
    event.waitUntil(
        clients.openWindow('https://example.com')
    );
});