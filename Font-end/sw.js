console.log('Service Worker Loaded...');

self.addEventListener('push', e => {
    const data = e.data?.text() ?? '';
    console.log('Push Recieved...');
    self.registration.showNotification("CondSync", {
        body: data,
        icon: 'http://image.ibb.co/frYOFd/tmlogo.png'
    });
});