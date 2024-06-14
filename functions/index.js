const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.notifyOnObjectUpdate = functions.database.ref('/objects/{objectId}')
    .onUpdate((change, context) => {
        const objectId = context.params.objectId;
        const beforeData = change.before.val();
        const afterData = change.after.val();

        const payload = {
            notification: {
                title: 'Objeto Actualizado',
                body: `El objeto con ID ${objectId} ha sido actualizado.`,
                sound: 'default'
            }
        };

        return admin.messaging().sendToTopic(`topic_${objectId}`, payload)
            .then(response => {
                console.log('Mensaje enviado exitosamente:', response);
            })
            .catch(error => {
                console.log('Error al enviar el mensaje:', error);
            });
    });
